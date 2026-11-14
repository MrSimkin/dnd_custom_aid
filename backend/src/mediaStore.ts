import { neon } from "@neondatabase/serverless";
import type { Uuid } from "./contracts/spine.ts";

/*
 * Base para DM, package 4: handout images (owner decisions P3, P5 and P27 «Workers KV»).
 *
 * Image bytes live in Workers KV under "media/<sha256>": content-addressed, immutable, stored once.
 * Their description (name, kind, visibility, checksum) is ordinary DM content (family HANDOUT)
 * in Neon, so authorization to read an image is authorization to read a handout that names it.
 */

export const MEDIA_TYPES = new Set(["image/jpeg", "image/png", "image/webp"]);
export const MEDIA_MAX_BYTES = 5 * 1024 * 1024;
export const SHA256_PATTERN = /^[0-9a-f]{64}$/;

/** The part of a Workers KV namespace this API uses. */
export interface MediaKv {
  get(key: string, type: "arrayBuffer"): Promise<ArrayBuffer | null>;
  getWithMetadata<M>(key: string, type: "arrayBuffer"): Promise<{ value: ArrayBuffer | null; metadata: M | null }>;
  put(key: string, value: ArrayBuffer, options?: { metadata?: unknown }): Promise<void>;
}

export interface MediaAuthorization {
  /** Uploading is limited to accounts that run at least one campaign as an ACTIVE DM. */
  isActiveDm(actorUserId: Uuid): Promise<boolean>;
  /** True when the actor may read the active HANDOUT content [contentId] and it names [sha256]. */
  mayReadMedia(actorUserId: Uuid, contentId: Uuid, sha256: string): Promise<boolean>;
  /**
   * Redesign A7: the campaign's handouts the DM sent to the players (PLAYER_SAFE and shared),
   * for any ACTIVE member of that campaign. Null when the actor is not an active member.
   */
  listSharedHandouts(actorUserId: Uuid, campaignId: Uuid): Promise<SharedHandout[] | null>;
}

/** What a player receives about a handout: never the DM notes. */
export interface SharedHandout {
  id: Uuid;
  name: string;
  caption: string;
  kind: string;
  sha256: string;
  mediaType: string;
  revision: number;
}

export interface StoredMedia {
  bytes: ArrayBuffer;
  mediaType: string;
}

export class MediaStore {
  private readonly kv: MediaKv;

  constructor(kv: MediaKv) {
    this.kv = kv;
  }

  /** Stores the bytes unless that checksum is already present; returns whether it wrote. */
  async put(sha256: string, mediaType: string, bytes: ArrayBuffer): Promise<boolean> {
    const key = mediaKey(sha256);
    const existing = await this.kv.get(key, "arrayBuffer");
    if (existing != null) {
      return false;
    }
    await this.kv.put(key, bytes, { metadata: { mediaType, size: bytes.byteLength } });
    return true;
  }

  async get(sha256: string): Promise<StoredMedia | null> {
    const result = await this.kv.getWithMetadata<{ mediaType?: string }>(mediaKey(sha256), "arrayBuffer");
    if (result.value == null) {
      return null;
    }
    const mediaType = result.metadata?.mediaType;
    return {
      bytes: result.value,
      mediaType: mediaType != null && MEDIA_TYPES.has(mediaType) ? mediaType : "application/octet-stream",
    };
  }
}

export async function sha256Hex(bytes: ArrayBuffer): Promise<string> {
  const digest = await crypto.subtle.digest("SHA-256", bytes);
  return Array.from(new Uint8Array(digest), (b) => b.toString(16).padStart(2, "0")).join("");
}

function mediaKey(sha256: string): string {
  return `media/${sha256}`;
}

/** Neon-backed media authorization using the dm_content rules of migration 0002. */
export class NeonMediaAuthorization implements MediaAuthorization {
  private readonly sql: ReturnType<typeof neon>;

  constructor(connectionString: string) {
    const normalized = connectionString.trim();
    if (normalized.length === 0) {
      throw new Error("DATABASE_URL must not be blank.");
    }
    this.sql = neon(normalized);
  }

  async isActiveDm(actorUserId: Uuid): Promise<boolean> {
    const rows = await this.sql`
      SELECT 1
      FROM campaign_membership m
      JOIN campaign c ON c.id = m.campaign_id
      WHERE m.user_id = ${actorUserId}::uuid
        AND m.role = 'DM'
        AND m.status = 'ACTIVE'
        AND c.deleted_at IS NULL
      LIMIT 1
    ` as unknown[];
    return rows.length > 0;
  }

  async mayReadMedia(actorUserId: Uuid, contentId: Uuid, sha256: string): Promise<boolean> {
    const rows = await this.sql`
      SELECT 1
      FROM dm_content d
      WHERE d.id = ${contentId}::uuid
        AND d.family = 'HANDOUT'
        AND d.deleted_at IS NULL
        AND d.snapshot -> 'payload' ->> 'sha256' = ${sha256}
        AND (
          dm_content_actor_may_write(${actorUserId}::uuid, d.scope_kind, d.owner_user_id, d.campaign_id)
          OR (
            d.scope_kind = 'CAMPAIGN'
            AND d.snapshot -> 'payload' ->> 'visibility' = 'PLAYER_SAFE'
            AND d.snapshot -> 'payload' ->> 'sharedWithPlayers' = 'true'
            AND EXISTS (
              SELECT 1 FROM campaign_membership m
              WHERE m.campaign_id = d.campaign_id
                AND m.user_id = ${actorUserId}::uuid
                AND m.status = 'ACTIVE'
            )
          )
        )
    ` as unknown[];
    return rows.length > 0;
  }

  async listSharedHandouts(actorUserId: Uuid, campaignId: Uuid): Promise<SharedHandout[] | null> {
    const member = await this.sql`
      SELECT 1
      FROM campaign_membership m
      JOIN campaign c ON c.id = m.campaign_id
      WHERE m.campaign_id = ${campaignId}::uuid
        AND m.user_id = ${actorUserId}::uuid
        AND m.status = 'ACTIVE'
        AND c.deleted_at IS NULL
      LIMIT 1
    ` as unknown[];
    if (member.length === 0) return null;
    const rows = await this.sql`
      SELECT d.id::text AS id,
             d.display_name AS name,
             COALESCE(d.snapshot -> 'payload' ->> 'caption', '') AS caption,
             COALESCE(d.snapshot -> 'payload' ->> 'kind', 'HANDOUT') AS kind,
             d.snapshot -> 'payload' ->> 'sha256' AS sha256,
             d.snapshot -> 'payload' ->> 'mediaType' AS media_type,
             d.revision AS revision
      FROM dm_content d
      WHERE d.campaign_id = ${campaignId}::uuid
        AND d.scope_kind = 'CAMPAIGN'
        AND d.family = 'HANDOUT'
        AND d.deleted_at IS NULL
        AND d.snapshot -> 'payload' ->> 'visibility' = 'PLAYER_SAFE'
        AND d.snapshot -> 'payload' ->> 'sharedWithPlayers' = 'true'
      ORDER BY d.display_name
    ` as Array<{ id: string; name: string; caption: string; kind: string; sha256: string; media_type: string; revision: string | number }>;
    return rows.map((row) => ({
      id: row.id,
      name: row.name,
      caption: row.caption,
      kind: row.kind,
      sha256: row.sha256,
      mediaType: row.media_type,
      revision: Number(row.revision),
    }));
  }
}
