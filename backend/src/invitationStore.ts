import { neon } from "@neondatabase/serverless";
import { HostedAuthorizationError, HostedObjectNotFoundError } from "./store.ts";
import type { Uuid } from "./contracts/spine.ts";

/*
 * Owner, 2026-11-18 («A: invitar jugadores»): a campaign could be moderated but nobody could join
 * it, so the whole player side was unreachable. PRODUCT.md §6 already describes the enrollment:
 * the DM holds one live code per campaign, it expires, and it can be replaced at any time.
 */

export interface CampaignInvitation {
  code: string;
  campaignId: Uuid;
  campaignName: string;
  expiresAtEpochSeconds: number;
}

export interface JoinedCampaign {
  campaignId: Uuid;
  campaignName: string;
  role: "DM" | "PLAYER";
  alreadyMember: boolean;
}

export interface InvitationStore {
  /** The live invitation of the campaign, or null. Only its DM may look. */
  current(actorUserId: Uuid, campaignId: Uuid): Promise<CampaignInvitation | null>;
  /** Replaces whatever was live with a new code. Only the DM. */
  create(actorUserId: Uuid, campaignId: Uuid, daysValid: number): Promise<CampaignInvitation>;
  /** Stops the live code. Only the DM. */
  revoke(actorUserId: Uuid, campaignId: Uuid): Promise<boolean>;
  /** Any signed-in account joins with a live code; a banned one is refused. */
  redeem(actorUserId: Uuid, code: string): Promise<JoinedCampaign>;
}

export class InvitationExpiredError extends Error {
  constructor() {
    super("La invitación ya venció. Pídele al DM una nueva.");
    this.name = "InvitationExpiredError";
  }
}

export class InvitationBannedError extends Error {
  constructor() {
    super("Esta cuenta no puede volver a esta campaña.");
    this.name = "InvitationBannedError";
  }
}

const ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ"; // no I and no O: they are read as 1 and 0

export function invitationCode(random: () => number = Math.random): string {
  let code = "";
  for (let index = 0; index < 6; index += 1) {
    code += ALPHABET[Math.floor(random() * ALPHABET.length)];
  }
  return code;
}

export class NeonInvitationStore implements InvitationStore {
  private readonly sql: ReturnType<typeof neon>;
  private readonly random: () => number;

  constructor(connectionString: string, random: () => number = Math.random) {
    const normalized = connectionString.trim();
    if (normalized.length === 0) {
      throw new Error("DATABASE_URL must not be blank.");
    }
    this.sql = neon(normalized);
    this.random = random;
  }

  private async requireDm(actorUserId: Uuid, campaignId: Uuid): Promise<string> {
    const rows = (await this.sql`
      SELECT c.name
      FROM campaign c
      JOIN campaign_membership member ON member.campaign_id = c.id
      WHERE c.id = ${campaignId}::uuid
        AND c.deleted_at IS NULL
        AND member.user_id = ${actorUserId}::uuid
        AND member.role = 'DM'
        AND member.status = 'ACTIVE'
    `) as Array<{ name: string }>;
    if (rows.length === 0) {
      throw new HostedAuthorizationError();
    }
    return rows[0].name;
  }

  async current(actorUserId: Uuid, campaignId: Uuid): Promise<CampaignInvitation | null> {
    const campaignName = await this.requireDm(actorUserId, campaignId);
    const rows = (await this.sql`
      SELECT code, EXTRACT(EPOCH FROM expires_at)::bigint AS expires_at
      FROM campaign_invitation
      WHERE campaign_id = ${campaignId}::uuid
        AND revoked_at IS NULL
        AND expires_at > now()
    `) as Array<{ code: string; expires_at: string }>;
    if (rows.length === 0) {
      return null;
    }
    return {
      code: rows[0].code,
      campaignId,
      campaignName,
      expiresAtEpochSeconds: Number(rows[0].expires_at),
    };
  }

  async create(actorUserId: Uuid, campaignId: Uuid, daysValid: number): Promise<CampaignInvitation> {
    const campaignName = await this.requireDm(actorUserId, campaignId);
    const days = Math.min(Math.max(Math.trunc(daysValid), 1), 30);
    await this.sql`
      UPDATE campaign_invitation
      SET revoked_at = now()
      WHERE campaign_id = ${campaignId}::uuid AND revoked_at IS NULL
    `;
    for (let attempt = 0; attempt < 8; attempt += 1) {
      const code = invitationCode(this.random);
      const rows = (await this.sql`
        INSERT INTO campaign_invitation(code, campaign_id, created_by, expires_at)
        VALUES (${code}, ${campaignId}::uuid, ${actorUserId}::uuid, now() + make_interval(days => ${days}))
        ON CONFLICT (code) DO NOTHING
        RETURNING code, EXTRACT(EPOCH FROM expires_at)::bigint AS expires_at
      `) as Array<{ code: string; expires_at: string }>;
      if (rows.length === 1) {
        return {
          code: rows[0].code,
          campaignId,
          campaignName,
          expiresAtEpochSeconds: Number(rows[0].expires_at),
        };
      }
    }
    throw new Error("Could not allocate a free invitation code.");
  }

  async revoke(actorUserId: Uuid, campaignId: Uuid): Promise<boolean> {
    await this.requireDm(actorUserId, campaignId);
    const rows = (await this.sql`
      UPDATE campaign_invitation
      SET revoked_at = now()
      WHERE campaign_id = ${campaignId}::uuid AND revoked_at IS NULL
      RETURNING code
    `) as Array<{ code: string }>;
    return rows.length > 0;
  }

  async redeem(actorUserId: Uuid, code: string): Promise<JoinedCampaign> {
    const normalized = code.trim().toUpperCase();
    const rows = (await this.sql`
      SELECT
        invitation.campaign_id::text AS campaign_id,
        c.name AS campaign_name,
        invitation.expires_at <= now() AS expired
      FROM campaign_invitation invitation
      JOIN campaign c ON c.id = invitation.campaign_id AND c.deleted_at IS NULL
      WHERE invitation.code = ${normalized}
        AND invitation.revoked_at IS NULL
    `) as Array<{ campaign_id: string; campaign_name: string; expired: boolean }>;
    if (rows.length === 0) {
      throw new HostedObjectNotFoundError();
    }
    if (rows[0].expired) {
      throw new InvitationExpiredError();
    }
    const campaignId = rows[0].campaign_id as Uuid;

    const existing = (await this.sql`
      SELECT role, status
      FROM campaign_membership
      WHERE campaign_id = ${campaignId}::uuid AND user_id = ${actorUserId}::uuid
    `) as Array<{ role: "DM" | "PLAYER"; status: "ACTIVE" | "KICKED" | "BANNED" }>;
    if (existing.length > 0) {
      if (existing[0].status === "BANNED") {
        throw new InvitationBannedError();
      }
      if (existing[0].status === "ACTIVE") {
        return {
          campaignId,
          campaignName: rows[0].campaign_name,
          role: existing[0].role,
          alreadyMember: true,
        };
      }
    }

    // A kicked member may come back with a valid invitation (PRODUCT.md §6).
    await this.sql`
      INSERT INTO campaign_membership(campaign_id, user_id, role, status)
      VALUES (${campaignId}::uuid, ${actorUserId}::uuid, 'PLAYER', 'ACTIVE')
      ON CONFLICT (campaign_id, user_id)
      DO UPDATE SET status = 'ACTIVE', updated_at = now()
    `;
    return {
      campaignId,
      campaignName: rows[0].campaign_name,
      role: existing.length > 0 ? existing[0].role : "PLAYER",
      alreadyMember: false,
    };
  }
}
