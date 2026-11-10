import { neon } from "@neondatabase/serverless";
import type { Uuid } from "./contracts/spine.ts";
import {
  HostedAuthorizationError,
  HostedObjectGoneError,
  HostedObjectNotFoundError,
  MutationReuseError,
  StaleRevisionError,
} from "./store.ts";

export type DmContentScopeKind = "PERSONAL" | "CAMPAIGN";

export interface DmContentState {
  id: Uuid;
  scopeKind: DmContentScopeKind;
  scopeRef: Uuid;
  family: string;
  displayName: string;
  revision: number;
  deletedAtEpochSeconds: number | null;
  snapshotFormat: string;
  snapshotVersion: number;
  snapshot: Record<string, unknown>;
}

export interface PutDmContentInput {
  actorUserId: Uuid;
  mutationId: Uuid;
  contentId: Uuid;
  expectedRevision: number;
  scopeKind: DmContentScopeKind;
  scopeRef: Uuid;
  family: string;
  displayName: string;
  snapshotFormat: string;
  snapshotVersion: number;
  snapshot: Record<string, unknown>;
}

export interface DeleteDmContentInput {
  actorUserId: Uuid;
  mutationId: Uuid;
  contentId: Uuid;
  expectedRevision: number;
}

export interface DmContentMutationResult {
  content: DmContentState;
  applied: boolean;
}

/** Thrown when a mutation tries to change the content scope or family. */
export class DmContentIdentityError extends Error {
  constructor(message: string) {
    super(message);
    this.name = "DmContentIdentityError";
  }
}

export interface DmContentStore {
  listCampaignContent(actorUserId: Uuid, campaignId: Uuid): Promise<DmContentState[]>;
  listPersonalContent(actorUserId: Uuid): Promise<DmContentState[]>;
  putContent(input: PutDmContentInput): Promise<DmContentMutationResult>;
  deleteContent(input: DeleteDmContentInput): Promise<DmContentMutationResult>;
}

interface DmContentRow {
  id: string;
  scope_kind: DmContentScopeKind;
  owner_user_id: string | null;
  campaign_id: string | null;
  family: string;
  display_name: string;
  revision: string;
  deleted_at_epoch_seconds: string | null;
  snapshot_format: string;
  snapshot_version: string;
  snapshot: unknown;
}

interface MutationRow {
  content_id: string;
  resulting_revision: string;
  applied: boolean;
}

/**
 * Hosted DM content persistence (Base para DM, package 1).
 *
 * Authorization, revisions, idempotency and tombstones live in the PostgreSQL functions of
 * migration 0002 so the SQL contract test proves the same code the Worker runs. This class only
 * maps rows and private SQLSTATE codes (class DC) to the shared hosted error types.
 */
export class NeonDmContentStore implements DmContentStore {
  private readonly sql: ReturnType<typeof neon>;

  constructor(connectionString: string) {
    const normalized = connectionString.trim();
    if (normalized.length === 0) {
      throw new Error("DATABASE_URL must not be blank.");
    }
    this.sql = neon(normalized);
  }

  async listCampaignContent(actorUserId: Uuid, campaignId: Uuid): Promise<DmContentState[]> {
    const rows = await this.run(() => this.sql`
      SELECT ${projection(this.sql)}
      FROM dm_content_list_campaign(${actorUserId}::uuid, ${campaignId}::uuid) d
    `);
    return (rows as DmContentRow[]).map(mapRow);
  }

  async listPersonalContent(actorUserId: Uuid): Promise<DmContentState[]> {
    const rows = await this.run(() => this.sql`
      SELECT ${projection(this.sql)}
      FROM dm_content_list_personal(${actorUserId}::uuid) d
    `);
    return (rows as DmContentRow[]).map(mapRow);
  }

  async putContent(input: PutDmContentInput): Promise<DmContentMutationResult> {
    const snapshotJson = JSON.stringify(input.snapshot);
    const rows = await this.run(() => this.sql`
      SELECT content_id::text AS content_id, resulting_revision::text AS resulting_revision, applied
      FROM dm_content_put(
        ${input.actorUserId}::uuid,
        ${input.mutationId}::uuid,
        ${input.contentId}::uuid,
        ${input.expectedRevision}::bigint,
        ${input.scopeKind},
        ${input.scopeRef}::uuid,
        ${input.family},
        ${input.displayName},
        ${input.snapshotFormat},
        ${input.snapshotVersion}::integer,
        ${snapshotJson}::jsonb
      )
    `);
    return this.resultFor(input.contentId, rows as MutationRow[]);
  }

  async deleteContent(input: DeleteDmContentInput): Promise<DmContentMutationResult> {
    const rows = await this.run(() => this.sql`
      SELECT content_id::text AS content_id, resulting_revision::text AS resulting_revision, applied
      FROM dm_content_delete(
        ${input.actorUserId}::uuid,
        ${input.mutationId}::uuid,
        ${input.contentId}::uuid,
        ${input.expectedRevision}::bigint
      )
    `);
    return this.resultFor(input.contentId, rows as MutationRow[]);
  }

  private async resultFor(contentId: Uuid, rows: MutationRow[]): Promise<DmContentMutationResult> {
    if (rows.length !== 1) {
      throw new Error("DM content mutation returned an unexpected result count.");
    }
    const current = await this.sql`
      SELECT ${projection(this.sql)}
      FROM dm_content d
      WHERE d.id = ${contentId}::uuid
    ` as DmContentRow[];
    if (current.length !== 1) {
      throw new Error("DM content mutation result could not be read back.");
    }
    return { content: mapRow(current[0]), applied: rows[0].applied === true };
  }

  private async run<T>(query: () => Promise<T>): Promise<T> {
    try {
      return await query();
    } catch (error) {
      throw mapDatabaseError(error);
    }
  }
}

function projection(sql: ReturnType<typeof neon>) {
  return sql.unsafe(`
    d.id::text AS id,
    d.scope_kind,
    d.owner_user_id::text AS owner_user_id,
    d.campaign_id::text AS campaign_id,
    d.family,
    d.display_name,
    d.revision::text AS revision,
    CASE
      WHEN d.deleted_at IS NULL THEN NULL
      ELSE floor(extract(epoch FROM d.deleted_at))::bigint::text
    END AS deleted_at_epoch_seconds,
    d.snapshot_format,
    d.snapshot_version::text AS snapshot_version,
    d.snapshot
  `);
}

export function mapDatabaseError(error: unknown): unknown {
  const code = (error as { code?: unknown } | null)?.code;
  switch (code) {
    case "DC403":
      return new HostedAuthorizationError();
    case "DC404":
      return new HostedObjectNotFoundError();
    case "DC409": {
      const detail = Number((error as { detail?: unknown }).detail);
      return new StaleRevisionError(Number.isSafeInteger(detail) ? detail : null);
    }
    case "DC410":
      return new HostedObjectGoneError();
    case "DC412":
      return new MutationReuseError();
    case "DC422":
      return new DmContentIdentityError(
        error instanceof Error ? error.message : "DM content identity is invalid.",
      );
    default:
      return error;
  }
}

function mapRow(row: DmContentRow): DmContentState {
  const scopeRef = row.scope_kind === "PERSONAL" ? row.owner_user_id : row.campaign_id;
  if (scopeRef == null) {
    throw new Error("DM content row has no scope reference.");
  }
  return {
    id: row.id,
    scopeKind: row.scope_kind,
    scopeRef,
    family: row.family,
    displayName: row.display_name,
    revision: Number(row.revision),
    deletedAtEpochSeconds: row.deleted_at_epoch_seconds == null ? null : Number(row.deleted_at_epoch_seconds),
    snapshotFormat: row.snapshot_format,
    snapshotVersion: Number(row.snapshot_version),
    snapshot: row.snapshot as Record<string, unknown>,
  };
}
