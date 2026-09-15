import { neon } from "@neondatabase/serverless";
import type { CampaignMembershipStatus, CampaignRole, Uuid } from "./contracts/spine.ts";

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

export interface CampaignMembershipState {
  campaignId: Uuid;
  name: string;
  role: CampaignRole;
  status: CampaignMembershipStatus;
  revision: number;
  deletedAtEpochSeconds: number | null;
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
  listCampaignMemberships(userId: Uuid): Promise<CampaignMembershipState[]>;
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

interface CampaignMembershipRow {
  campaign_id: string;
  name: string;
  role: CampaignRole;
  status: CampaignMembershipStatus;
  revision: string;
  deleted_at_epoch_seconds: string | null;
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

  async listCampaignMemberships(userId: Uuid): Promise<CampaignMembershipState[]> {
    const rawRows = await this.sql`
      SELECT
        c.id::text AS campaign_id,
        c.name,
        m.role,
        m.status,
        c.revision::text AS revision,
        CASE
          WHEN c.deleted_at IS NULL THEN NULL
          ELSE floor(extract(epoch FROM c.deleted_at))::bigint::text
        END AS deleted_at_epoch_seconds
      FROM campaign_membership m
      JOIN campaign c ON c.id = m.campaign_id
      WHERE m.user_id = ${userId}::uuid
      ORDER BY c.id
    `;
    const rows = rawRows as unknown as CampaignMembershipRow[];

    return rows.map((row) => ({
      campaignId: row.campaign_id,
      name: row.name,
      role: row.role,
      status: row.status,
      revision: parseRevision(row.revision),
      deletedAtEpochSeconds: parseNullableEpochSeconds(row.deleted_at_epoch_seconds),
    }));
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
        existing_campaign AS (
          SELECT c.id, c.name, c.revision
          FROM existing_receipt r
          JOIN campaign c ON c.id = r.object_id
          WHERE r.object_type = 'CAMPAIGN'
            AND r.object_id = ${input.campaignId}::uuid
            AND c.deleted_at IS NULL
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
        new_membership AS (
          INSERT INTO campaign_membership(campaign_id, user_id, role, status)
          SELECT id, ${input.actorUserId}::uuid, 'DM', 'ACTIVE'
          FROM created_campaign
          RETURNING campaign_id, role
        ),
        existing_permitted AS (
          SELECT c.id, c.name, m.role, c.revision
          FROM existing_campaign c
          JOIN campaign_membership m ON m.campaign_id = c.id
          WHERE m.user_id = ${input.actorUserId}::uuid
            AND m.status = 'ACTIVE'
        )
        SELECT
          c.id::text AS id,
          c.name,
          m.role,
          c.revision::text AS revision,
          true AS created
        FROM created_campaign c
        JOIN new_membership m ON m.campaign_id = c.id
        UNION ALL
        SELECT
          c.id::text AS id,
          c.name,
          c.role,
          c.revision::text AS revision,
          false AS created
        FROM existing_permitted c
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

function parseNullableEpochSeconds(value: string | null): number | null {
  if (value == null) {
    return null;
  }
  const epochSeconds = Number(value);
  if (!Number.isSafeInteger(epochSeconds) || epochSeconds < 0) {
    throw new Error("Database returned an invalid deletion timestamp.");
  }
  return epochSeconds;
}

function requireSingle<T>(rows: T[], message: string): T {
  if (rows.length !== 1) {
    throw new Error(message);
  }
  return rows[0];
}
