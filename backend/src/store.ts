import { neon } from "@neondatabase/serverless";
import type { CampaignRole, Uuid } from "./contracts/spine.ts";

export interface AppUser {
  id: Uuid;
  externalSubject: string;
  displayName: string | null;
}

export interface CampaignSummary {
  id: Uuid;
  name: string;
  role: CampaignRole;
  revision: number;
}

export interface CreateCampaignInput {
  actorUserId: Uuid;
  mutationId: Uuid;
  campaignId: Uuid;
  name: string;
}

export interface CreateCampaignResult {
  campaign: CampaignSummary;
  created: boolean;
}

export interface CampaignStore {
  resolveUser(externalSubject: string, displayName: string | null): Promise<AppUser>;
  listCampaigns(userId: Uuid): Promise<CampaignSummary[]>;
  createCampaign(input: CreateCampaignInput): Promise<CreateCampaignResult>;
}

export class MutationReuseError extends Error {
  constructor() {
    super("Mutation identity is already associated with a different operation or object.");
    this.name = "MutationReuseError";
  }
}

interface UserRow {
  id: string;
  external_subject: string;
  display_name: string | null;
}

interface CampaignRow {
  id: string;
  name: string;
  role: CampaignRole;
  revision: string;
  created?: boolean;
}

export class NeonCampaignStore implements CampaignStore {
  private readonly sql: ReturnType<typeof neon>;

  constructor(connectionString: string) {
    const normalized = connectionString.trim();
    if (normalized.length === 0) {
      throw new Error("DATABASE_URL must not be blank.");
    }
    this.sql = neon(normalized);
  }

  async resolveUser(externalSubject: string, displayName: string | null): Promise<AppUser> {
    const subject = externalSubject.trim();
    if (subject.length === 0) {
      throw new Error("Authenticated subject must not be blank.");
    }

    const proposedId = crypto.randomUUID();
    const rawRows = await this.sql`
      INSERT INTO app_user(id, descope_subject, display_name)
      VALUES (${proposedId}::uuid, ${subject}, ${displayName})
      ON CONFLICT(descope_subject) DO UPDATE SET
        display_name = COALESCE(EXCLUDED.display_name, app_user.display_name),
        updated_at = now()
      RETURNING id::text AS id, descope_subject AS external_subject, display_name
    `;
    const rows = rawRows as unknown as UserRow[];
    const row = requireSingle(rows, "Failed to resolve authenticated user.");
    return {
      id: row.id,
      externalSubject: row.external_subject,
      displayName: row.display_name,
    };
  }

  async listCampaigns(userId: Uuid): Promise<CampaignSummary[]> {
    const rawRows = await this.sql`
      SELECT
        c.id::text AS id,
        c.name,
        m.role,
        c.revision::text AS revision
      FROM campaign_membership m
      JOIN campaign c ON c.id = m.campaign_id
      WHERE m.user_id = ${userId}::uuid
        AND m.status = 'ACTIVE'
        AND c.deleted_at IS NULL
      ORDER BY c.name COLLATE "C", c.id
    `;
    const rows = rawRows as unknown as CampaignRow[];

    return rows.map(mapCampaignRow);
  }

  async createCampaign(input: CreateCampaignInput): Promise<CreateCampaignResult> {
    const name = input.name.trim();
    if (name.length === 0) {
      throw new Error("Campaign name must not be blank.");
    }

    const lockKey = `${input.actorUserId}:${input.mutationId}`;
    const [, resultRows] = await this.sql.transaction((txn) => [
      txn`SELECT pg_advisory_xact_lock(hashtextextended(${lockKey}, 0))`,
      txn`
        WITH existing_receipt AS (
          SELECT object_type, object_id, resulting_revision
          FROM mutation_receipt
          WHERE user_id = ${input.actorUserId}::uuid
            AND mutation_id = ${input.mutationId}::uuid
        ),
        created_campaign AS (
          INSERT INTO campaign(id, name, revision)
          SELECT ${input.campaignId}::uuid, ${name}, 0
          WHERE NOT EXISTS (SELECT 1 FROM existing_receipt)
          ON CONFLICT(id) DO NOTHING
          RETURNING id, name, revision
        ),
        new_receipt AS (
          INSERT INTO mutation_receipt(
            user_id, mutation_id, object_type, object_id, resulting_revision
          )
          SELECT
            ${input.actorUserId}::uuid,
            ${input.mutationId}::uuid,
            'CAMPAIGN',
            id,
            revision
          FROM created_campaign
          RETURNING object_type, object_id, resulting_revision
        ),
        target AS (
          SELECT object_type, object_id, resulting_revision FROM existing_receipt
          UNION ALL
          SELECT object_type, object_id, resulting_revision FROM new_receipt
        ),
        target_campaign AS (
          SELECT c.id, c.name, c.revision
          FROM target t
          JOIN campaign c ON c.id = t.object_id
          WHERE t.object_type = 'CAMPAIGN'
            AND t.object_id = ${input.campaignId}::uuid
            AND c.deleted_at IS NULL
        ),
        new_membership AS (
          INSERT INTO campaign_membership(campaign_id, user_id, role, status)
          SELECT id, ${input.actorUserId}::uuid, 'DM', 'ACTIVE'
          FROM created_campaign
          RETURNING campaign_id, role
        ),
        permitted_campaign AS (
          SELECT campaign_id, role FROM new_membership
          UNION ALL
          SELECT m.campaign_id, m.role
          FROM campaign_membership m
          WHERE m.user_id = ${input.actorUserId}::uuid
            AND m.status = 'ACTIVE'
            AND NOT EXISTS (SELECT 1 FROM created_campaign)
        )
        SELECT
          c.id::text AS id,
          c.name,
          p.role,
          c.revision::text AS revision,
          EXISTS (SELECT 1 FROM created_campaign) AS created
        FROM target_campaign c
        JOIN permitted_campaign p ON p.campaign_id = c.id
      `,
    ], { isolationLevel: "ReadCommitted" });

    const rows = resultRows as unknown as CampaignRow[];
    if (rows.length !== 1) {
      throw new MutationReuseError();
    }

    return {
      campaign: mapCampaignRow(rows[0]),
      created: rows[0].created === true,
    };
  }
}

function mapCampaignRow(row: CampaignRow): CampaignSummary {
  return {
    id: row.id,
    name: row.name,
    role: row.role,
    revision: parseRevision(row.revision),
  };
}

function parseRevision(value: string): number {
  const revision = Number(value);
  if (!Number.isSafeInteger(revision) || revision < 0) {
    throw new Error("Database returned an invalid revision.");
  }
  return revision;
}

function requireSingle<T>(rows: T[], message: string): T {
  if (rows.length !== 1) {
    throw new Error(message);
  }
  return rows[0];
}
