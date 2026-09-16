import { neon } from "@neondatabase/serverless";
import type { CampaignMembershipStatus, CampaignRole, Uuid } from "./contracts/spine.ts";
import { HostedAuthorizationError } from "./store.ts";

export type CampaignModerationAction = "KICK" | "BAN" | "LIFT_BAN";

export interface CampaignAdminMember {
  userId: Uuid;
  displayName: string | null;
  role: CampaignRole;
  status: CampaignMembershipStatus;
}

export interface CampaignMemberRoster {
  campaignId: Uuid;
  campaignRevision: number;
  members: CampaignAdminMember[];
}

export interface ModerateCampaignMemberInput {
  actorUserId: Uuid;
  campaignId: Uuid;
  targetUserId: Uuid;
  action: CampaignModerationAction;
}

export interface ModerateCampaignMemberResult {
  member: CampaignAdminMember;
  campaignRevision: number;
  applied: boolean;
}

export interface CampaignAdministrationStore {
  listMembers(actorUserId: Uuid, campaignId: Uuid): Promise<CampaignMemberRoster>;
  moderateMember(input: ModerateCampaignMemberInput): Promise<ModerateCampaignMemberResult>;
}

interface RosterRow {
  campaign_id: string;
  campaign_revision: string;
  user_id: string;
  display_name: string | null;
  role: CampaignRole;
  status: CampaignMembershipStatus;
}

interface ModerationRow {
  user_id: string;
  display_name: string | null;
  role: CampaignRole;
  status: CampaignMembershipStatus;
  campaign_revision: string;
  applied: boolean;
}

/**
 * Hosted Campaign Administration persistence boundary.
 *
 * The current package deliberately keeps membership lifecycle administration separate from the
 * ordinary CampaignStore. It reuses the same campaign_membership rows and DM role semantics while
 * avoiding a generalized RBAC layer that the product does not need.
 */
export class NeonCampaignAdministrationStore implements CampaignAdministrationStore {
  private readonly sql: ReturnType<typeof neon>;

  constructor(connectionString: string) {
    const normalized = connectionString.trim();
    if (normalized.length === 0) {
      throw new Error("DATABASE_URL must not be blank.");
    }
    this.sql = neon(normalized);
  }

  async listMembers(actorUserId: Uuid, campaignId: Uuid): Promise<CampaignMemberRoster> {
    const rawRows = await this.sql`
      WITH authorized_dm AS (
        SELECT 1
        FROM campaign_membership actor
        JOIN campaign c ON c.id = actor.campaign_id
        WHERE actor.campaign_id = ${campaignId}::uuid
          AND actor.user_id = ${actorUserId}::uuid
          AND actor.role = 'DM'
          AND actor.status = 'ACTIVE'
          AND c.deleted_at IS NULL
      )
      SELECT
        c.id::text AS campaign_id,
        c.revision::text AS campaign_revision,
        member.user_id::text AS user_id,
        account.display_name,
        member.role,
        member.status
      FROM campaign c
      JOIN campaign_membership member ON member.campaign_id = c.id
      JOIN app_user account ON account.id = member.user_id
      WHERE c.id = ${campaignId}::uuid
        AND c.deleted_at IS NULL
        AND EXISTS (SELECT 1 FROM authorized_dm)
      ORDER BY
        CASE member.role WHEN 'DM' THEN 0 ELSE 1 END,
        COALESCE(account.display_name, '') COLLATE "C",
        member.user_id
    `;
    const rows = rawRows as unknown as RosterRow[];

    // Every authorized campaign necessarily contains the active DM actor membership, so an empty
    // result is safely treated as authorization failure without disclosing campaign/member existence.
    if (rows.length === 0) {
      throw new HostedAuthorizationError();
    }

    const first = rows[0];
    return {
      campaignId: first.campaign_id,
      campaignRevision: parseRevision(first.campaign_revision),
      members: rows.map(mapRosterMember),
    };
  }

  async moderateMember(input: ModerateCampaignMemberInput): Promise<ModerateCampaignMemberResult> {
    const lockKey = `CAMPAIGN_MEMBERSHIP:${input.campaignId}:${input.targetUserId}`;
    const [, resultRows] = await this.sql.transaction((txn) => [
      txn`SELECT pg_advisory_xact_lock(hashtextextended(${lockKey}, 0))`,
      txn`
        WITH authorized_dm AS (
          SELECT 1
          FROM campaign_membership actor
          JOIN campaign c ON c.id = actor.campaign_id
          WHERE actor.campaign_id = ${input.campaignId}::uuid
            AND actor.user_id = ${input.actorUserId}::uuid
            AND actor.role = 'DM'
            AND actor.status = 'ACTIVE'
            AND c.deleted_at IS NULL
        ),
        target AS (
          SELECT
            member.campaign_id,
            member.user_id,
            member.role,
            member.status AS current_status
          FROM campaign_membership member
          WHERE member.campaign_id = ${input.campaignId}::uuid
            AND member.user_id = ${input.targetUserId}::uuid
            AND member.role = 'PLAYER'
            AND EXISTS (SELECT 1 FROM authorized_dm)
        ),
        desired AS (
          SELECT
            target.*,
            CASE
              WHEN ${input.action} = 'KICK' AND target.current_status = 'ACTIVE' THEN 'KICKED'
              WHEN ${input.action} = 'BAN' AND target.current_status IN ('ACTIVE', 'KICKED') THEN 'BANNED'
              WHEN ${input.action} = 'LIFT_BAN' AND target.current_status = 'BANNED' THEN 'KICKED'
              ELSE target.current_status
            END AS desired_status
          FROM target
        ),
        updated_membership AS (
          UPDATE campaign_membership member
          SET
            status = desired.desired_status,
            updated_at = now()
          FROM desired
          WHERE member.campaign_id = desired.campaign_id
            AND member.user_id = desired.user_id
            AND member.status <> desired.desired_status
          RETURNING member.user_id, member.role, member.status
        ),
        updated_campaign AS (
          UPDATE campaign c
          SET
            revision = c.revision + 1,
            updated_at = now()
          WHERE c.id = ${input.campaignId}::uuid
            AND EXISTS (SELECT 1 FROM updated_membership)
          RETURNING c.revision
        )
        SELECT
          changed.user_id::text AS user_id,
          account.display_name,
          changed.role,
          changed.status,
          campaign_revision.revision::text AS campaign_revision,
          true AS applied
        FROM updated_membership changed
        JOIN app_user account ON account.id = changed.user_id
        CROSS JOIN updated_campaign campaign_revision
        UNION ALL
        SELECT
          desired.user_id::text AS user_id,
          account.display_name,
          desired.role,
          desired.current_status AS status,
          c.revision::text AS campaign_revision,
          false AS applied
        FROM desired
        JOIN app_user account ON account.id = desired.user_id
        JOIN campaign c ON c.id = desired.campaign_id
        WHERE NOT EXISTS (SELECT 1 FROM updated_membership)
      `,
    ], { isolationLevel: "ReadCommitted" });

    const rows = resultRows as unknown as ModerationRow[];
    if (rows.length !== 1) {
      // Use one generic authorization failure for non-DMs, missing campaigns, missing members and
      // out-of-scope DM targets so this administration endpoint does not become an existence oracle.
      throw new HostedAuthorizationError();
    }

    const row = rows[0];
    return {
      member: {
        userId: row.user_id,
        displayName: row.display_name,
        role: row.role,
        status: row.status,
      },
      campaignRevision: parseRevision(row.campaign_revision),
      applied: row.applied === true,
    };
  }
}

function mapRosterMember(row: RosterRow): CampaignAdminMember {
  return {
    userId: row.user_id,
    displayName: row.display_name,
    role: row.role,
    status: row.status,
  };
}

function parseRevision(value: string): number {
  const revision = Number(value);
  if (!Number.isSafeInteger(revision) || revision < 0) {
    throw new Error("Database returned an invalid campaign revision.");
  }
  return revision;
}
