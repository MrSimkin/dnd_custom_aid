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
  /** Owner's K (2026-11-20): only the move from KV to the bucket walks the keys. */
  list?(options?: { prefix?: string; limit?: number; cursor?: string }): Promise<MediaKvListing>;
}

/** The part of a KV listing the move needs. */
export interface MediaKvListing {
  keys: Array<{ name: string }>;
  list_complete: boolean;
  cursor?: string;
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

/**
 * Where the bytes of an image live. Owner's «N» (2026-11-18): the free Workers KV plan allows
 * 1000 writes a day, which a campaign of handouts and maps eats quickly; object storage holds the
 * same files with far more room. The API in front never changes, so the Apps do not care which one
 * is behind, and no client ever holds storage credentials (AGENTS §3).
 */
export interface MediaBytesStore {
  /** Stores the bytes unless that checksum is already present; returns whether it wrote. */
  put(sha256: string, mediaType: string, bytes: ArrayBuffer): Promise<boolean>;
  get(sha256: string): Promise<StoredMedia | null>;
}

/** The object storage binding this Worker uses (Cloudflare R2 shape). */
export interface MediaBucket {
  head(key: string): Promise<unknown | null>;
  put(key: string, value: ArrayBuffer, options?: { httpMetadata?: { contentType?: string } }): Promise<unknown>;
  get(key: string): Promise<
    | {
        arrayBuffer(): Promise<ArrayBuffer>;
        httpMetadata?: { contentType?: string };
      }
    | null
  >;
}

/** Keeps the images in object storage, by checksum, exactly as KV did. */
export class BucketMediaStore implements MediaBytesStore {
  private readonly bucket: MediaBucket;

  constructor(bucket: MediaBucket) {
    this.bucket = bucket;
  }

  async put(sha256: string, mediaType: string, bytes: ArrayBuffer): Promise<boolean> {
    const key = mediaKey(sha256);
    if ((await this.bucket.head(key)) != null) {
      return false;
    }
    await this.bucket.put(key, bytes, { httpMetadata: { contentType: mediaType } });
    return true;
  }

  async get(sha256: string): Promise<StoredMedia | null> {
    const stored = await this.bucket.get(mediaKey(sha256));
    if (stored == null) {
      return null;
    }
    const mediaType = stored.httpMetadata?.contentType;
    return {
      bytes: await stored.arrayBuffer(),
      mediaType: mediaType != null && MEDIA_TYPES.has(mediaType) ? mediaType : "application/octet-stream",
    };
  }
}

/**
 * Owner's K (2026-11-20): while the images move from Workers KV to object storage, both are open
 * at once. Everything new is written to [primary]; a read that [primary] does not have is served
 * from [fallback] and copied across on the way out, so an image moves the first time anyone looks
 * at it and nothing has to be moved before the bucket can be used.
 */
export class FallbackMediaStore implements MediaBytesStore {
  private readonly primary: MediaBytesStore;
  private readonly fallback: MediaBytesStore;

  constructor(primary: MediaBytesStore, fallback: MediaBytesStore) {
    this.primary = primary;
    this.fallback = fallback;
  }

  put(sha256: string, mediaType: string, bytes: ArrayBuffer): Promise<boolean> {
    return this.primary.put(sha256, mediaType, bytes);
  }

  async get(sha256: string): Promise<StoredMedia | null> {
    const first = await this.primary.get(sha256);
    if (first != null) {
      return first;
    }
    const old = await this.fallback.get(sha256);
    if (old == null) {
      return null;
    }
    // Copying must never cost the reader the image: if the write fails, serve it anyway.
    try {
      await this.primary.put(sha256, old.mediaType, old.bytes);
    } catch {
      // The next read tries again.
    }
    return old;
  }
}

export class MediaStore implements MediaBytesStore {
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

/** What one pass of the move from KV to the bucket did. */
export interface MediaMigrationReport {
  examined: number;
  moved: number;
  alreadyThere: number;
  missing: number;
  /** Where the next pass starts, or null when there is nothing left. */
  cursor: string | null;
  done: boolean;
}

export const MEDIA_MIGRATION_MAX = 100;

/**
 * Owner's K: one bounded pass of the move. It reads a page of KV keys, copies into the bucket the
 * ones that are not there yet, and hands back where to continue, so the whole move is a few calls
 * instead of one request that would time out. It never deletes from KV: the old copy stays until
 * the owner says the move is finished.
 */
export async function migrateMediaToBucket(
  kv: MediaKv,
  bucket: MediaBucket,
  options: { limit?: number; cursor?: string | null } = {},
): Promise<MediaMigrationReport> {
  if (kv.list == null) {
    throw new Error("This KV binding cannot list its keys.");
  }
  const limit = Math.min(Math.max(options.limit ?? 25, 1), MEDIA_MIGRATION_MAX);
  const listing = await kv.list({ prefix: "media/", limit, cursor: options.cursor ?? undefined });
  const from = new MediaStore(kv);
  const to = new BucketMediaStore(bucket);
  let moved = 0;
  let alreadyThere = 0;
  let missing = 0;

  for (const key of listing.keys) {
    const sha256 = key.name.slice("media/".length);
    if (!SHA256_PATTERN.test(sha256)) {
      missing += 1;
      continue;
    }
    const stored = await from.get(sha256);
    if (stored == null) {
      missing += 1;
      continue;
    }
    if (await to.put(sha256, stored.mediaType, stored.bytes)) {
      moved += 1;
    } else {
      alreadyThere += 1;
    }
  }

  return {
    examined: listing.keys.length,
    moved,
    alreadyThere,
    missing,
    cursor: listing.list_complete ? null : (listing.cursor ?? null),
    done: listing.list_complete,
  };
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
