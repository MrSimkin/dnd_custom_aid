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

export interface PcSnapshotState {
  id: Uuid;
  campaignId: Uuid;
  ownerUserId: Uuid | null;
  controllerUserId: Uuid | null;
  name: string;
  revision: number;
  deletedAtEpochSeconds: number | null;
  snapshotFormat: string;
  snapshotVersion: number;
  snapshot: Record<string, unknown>;
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

export interface PutPcSnapshotInput {
  actorUserId: Uuid;
  mutationId: Uuid;
  pcId: Uuid;
  campaignId: Uuid;
  expectedRevision: number;
  name: string;
  snapshotFormat: string;
  snapshotVersion: number;
  snapshot: Record<string, unknown>;
}

export interface PutPcSnapshotResult {
  pc: PcSnapshotState;
  applied: boolean;
}

export interface PcAuthorityState {
  pcId: Uuid;
  campaignId: Uuid;
  ownerUserId: Uuid | null;
  controllerUserId: Uuid | null;
}

export interface SetPcAuthorityInput {
  actorUserId: Uuid;
  pcId: Uuid;
  campaignId: Uuid;
  ownerUserId: Uuid | null;
  controllerUserId: Uuid | null;
}

export interface SetPcAuthorityResult {
  authority: PcAuthorityState;
  applied: boolean;
}

export interface CampaignStore {
  resolveUser(externalSubject: string, displayName: string | null): Promise<AppUser>;
  listCampaigns(userId: Uuid): Promise<CampaignSummary[]>;
  listCampaignMemberships(userId: Uuid): Promise<CampaignMembershipState[]>;
  createCampaign(input: CreateCampaignInput): Promise<CreateCampaignResult>;
  listPcSnapshots(userId: Uuid, campaignId: Uuid): Promise<PcSnapshotState[]>;
  setPcAuthority(input: SetPcAuthorityInput): Promise<SetPcAuthorityResult>;
  putPcSnapshot(input: PutPcSnapshotInput): Promise<PutPcSnapshotResult>;
}

export class MutationReuseError extends Error {
  constructor() {
    super("Mutation identity is already associated with a different operation or object.");
    this.name = "MutationReuseError";
  }
}

export class HostedAuthorizationError extends Error {
  constructor() {
    super("The authenticated account is not allowed to perform this operation.");
    this.name = "HostedAuthorizationError";
  }
}

export class StaleRevisionError extends Error {
  readonly currentRevision: number | null;

  constructor(currentRevision: number | null) {
    super("The hosted object revision no longer matches the expected revision.");
    this.name = "StaleRevisionError";
    this.currentRevision = currentRevision;
  }
}

export class HostedObjectGoneError extends Error {
  constructor() {
    super("The hosted object has been deleted.");
    this.name = "HostedObjectGoneError";
  }
}

export class HostedObjectNotFoundError extends Error {
  constructor() {
    super("The hosted object was not found.");
    this.name = "HostedObjectNotFoundError";
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

interface PcSnapshotRow {
  id: string;
  campaign_id: string;
  owner_user_id: string | null;
  controller_user_id: string | null;
  name: string;
  revision: string;
  deleted_at_epoch_seconds: string | null;
  snapshot_format: string;
  snapshot_version: string;
  snapshot: unknown;
  applied?: boolean;
}

interface PcMutationDiagnosticRow {
  campaign_id: string;
  owner_user_id: string | null;
  controller_user_id: string | null;
  revision: string;
  deleted: boolean;
}

interface PcAuthorityRow {
  pc_id: string;
  campaign_id: string;
  owner_user_id: string | null;
  controller_user_id: string | null;
  applied: boolean;
}

interface PcAuthorityDiagnosticRow {
  campaign_id: string;
  deleted: boolean;
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

  async listPcSnapshots(userId: Uuid, campaignId: Uuid): Promise<PcSnapshotState[]> {
    const rawRows = await this.sql`
      SELECT
        p.id::text AS id,
        p.campaign_id::text AS campaign_id,
        p.owner_user_id::text AS owner_user_id,
        p.controller_user_id::text AS controller_user_id,
        p.name,
        p.revision::text AS revision,
        CASE
          WHEN p.deleted_at IS NULL THEN NULL
          ELSE floor(extract(epoch FROM p.deleted_at))::bigint::text
        END AS deleted_at_epoch_seconds,
        p.snapshot_format,
        p.snapshot_version::text AS snapshot_version,
        p.snapshot
      FROM pc p
      JOIN campaign c ON c.id = p.campaign_id
      JOIN campaign_membership m ON m.campaign_id = p.campaign_id
      WHERE p.campaign_id = ${campaignId}::uuid
        AND m.user_id = ${userId}::uuid
        AND m.status = 'ACTIVE'
        AND c.deleted_at IS NULL
        AND p.snapshot IS NOT NULL
        AND p.snapshot_format IS NOT NULL
        AND p.snapshot_version IS NOT NULL
        AND (
          m.role = 'DM'
          OR p.owner_user_id = ${userId}::uuid
          OR p.controller_user_id = ${userId}::uuid
        )
      ORDER BY p.id
    `;
    const rows = rawRows as unknown as PcSnapshotRow[];
    return rows.map(mapPcSnapshotRow);
  }

  async setPcAuthority(input: SetPcAuthorityInput): Promise<SetPcAuthorityResult> {
    const objectLockKey = `PC_AUTHORITY:${input.pcId}`;
    const [, resultRows] = await this.sql.transaction((txn) => [
      txn`SELECT pg_advisory_xact_lock(hashtextextended(${objectLockKey}, 0))`,
      txn`
        WITH actor AS (
          SELECT 1
          FROM campaign_membership m
          JOIN campaign c ON c.id = m.campaign_id
          WHERE m.campaign_id = ${input.campaignId}::uuid
            AND m.user_id = ${input.actorUserId}::uuid
            AND m.role = 'DM'
            AND m.status = 'ACTIVE'
            AND c.deleted_at IS NULL
        ),
        owner_allowed AS (
          SELECT 1
          WHERE ${input.ownerUserId}::uuid IS NULL
             OR EXISTS (
               SELECT 1
               FROM campaign_membership m
               WHERE m.campaign_id = ${input.campaignId}::uuid
                 AND m.user_id = ${input.ownerUserId}::uuid
                 AND m.status = 'ACTIVE'
             )
        ),
        controller_allowed AS (
          SELECT 1
          WHERE ${input.controllerUserId}::uuid IS NULL
             OR EXISTS (
               SELECT 1
               FROM campaign_membership m
               WHERE m.campaign_id = ${input.campaignId}::uuid
                 AND m.user_id = ${input.controllerUserId}::uuid
                 AND m.status = 'ACTIVE'
             )
        ),
        existing_pc AS (
          SELECT p.*
          FROM pc p
          JOIN campaign c ON c.id = p.campaign_id
          WHERE p.id = ${input.pcId}::uuid
            AND p.campaign_id = ${input.campaignId}::uuid
            AND p.deleted_at IS NULL
            AND c.deleted_at IS NULL
        ),
        updated_pc AS (
          UPDATE pc p
          SET owner_user_id = ${input.ownerUserId}::uuid,
              controller_user_id = ${input.controllerUserId}::uuid,
              updated_at = now()
          WHERE p.id = ${input.pcId}::uuid
            AND p.campaign_id = ${input.campaignId}::uuid
            AND p.deleted_at IS NULL
            AND EXISTS (SELECT 1 FROM actor)
            AND EXISTS (SELECT 1 FROM owner_allowed)
            AND EXISTS (SELECT 1 FROM controller_allowed)
            AND (
              p.owner_user_id IS DISTINCT FROM ${input.ownerUserId}::uuid
              OR p.controller_user_id IS DISTINCT FROM ${input.controllerUserId}::uuid
            )
          RETURNING p.*, true AS applied
        ),
        unchanged_pc AS (
          SELECT p.*, false AS applied
          FROM existing_pc p
          WHERE EXISTS (SELECT 1 FROM actor)
            AND EXISTS (SELECT 1 FROM owner_allowed)
            AND EXISTS (SELECT 1 FROM controller_allowed)
            AND NOT EXISTS (SELECT 1 FROM updated_pc)
        )
        SELECT
          result.id::text AS pc_id,
          result.campaign_id::text AS campaign_id,
          result.owner_user_id::text AS owner_user_id,
          result.controller_user_id::text AS controller_user_id,
          result.applied
        FROM (
          SELECT * FROM updated_pc
          UNION ALL
          SELECT * FROM unchanged_pc
        ) result
      `,
    ], { isolationLevel: "ReadCommitted" });

    const rows = resultRows as unknown as PcAuthorityRow[];
    if (rows.length !== 1) {
      await this.throwPcAuthorityFailure(input);
    }
    const row = requireSingle(rows, "PC authority mutation returned an unexpected result count.");
    return {
      authority: mapPcAuthorityRow(row),
      applied: row.applied === true,
    };
  }

  private async throwPcAuthorityFailure(input: SetPcAuthorityInput): Promise<never> {
    const actorRows = await this.sql`
      SELECT 1
      FROM campaign_membership m
      JOIN campaign c ON c.id = m.campaign_id
      WHERE m.campaign_id = ${input.campaignId}::uuid
        AND m.user_id = ${input.actorUserId}::uuid
        AND m.role = 'DM'
        AND m.status = 'ACTIVE'
        AND c.deleted_at IS NULL
    `;
    if (actorRows.length !== 1) {
      throw new HostedAuthorizationError();
    }

    const pcRows = await this.sql`
      SELECT
        p.campaign_id::text AS campaign_id,
        (p.deleted_at IS NOT NULL) AS deleted
      FROM pc p
      WHERE p.id = ${input.pcId}::uuid
    ` as unknown as PcAuthorityDiagnosticRow[];

    if (pcRows.length === 0) {
      throw new HostedObjectNotFoundError();
    }
    const pc = requireSingle(pcRows, "PC identity unexpectedly resolved to multiple rows.");
    if (pc.campaign_id !== input.campaignId) {
      throw new HostedAuthorizationError();
    }
    if (pc.deleted) {
      throw new HostedObjectGoneError();
    }

    for (const targetUserId of [input.ownerUserId, input.controllerUserId]) {
      if (targetUserId == null) continue;
      const targetRows = await this.sql`
        SELECT 1
        FROM campaign_membership m
        WHERE m.campaign_id = ${input.campaignId}::uuid
          AND m.user_id = ${targetUserId}::uuid
          AND m.status = 'ACTIVE'
      `;
      if (targetRows.length !== 1) {
        throw new HostedAuthorizationError();
      }
    }

    throw new Error("PC authority mutation could not be applied after passing authorization checks.");
  }

  async putPcSnapshot(input: PutPcSnapshotInput): Promise<PutPcSnapshotResult> {
    const name = input.name.trim();
    if (name.length === 0) {
      throw new Error("PC name must not be blank.");
    }
    if (!Number.isSafeInteger(input.expectedRevision) || input.expectedRevision < 0) {
      throw new Error("Expected PC revision must be a non-negative safe integer.");
    }
    if (input.snapshotFormat.trim().length === 0) {
      throw new Error("PC snapshot format must not be blank.");
    }
    if (!Number.isSafeInteger(input.snapshotVersion) || input.snapshotVersion < 1) {
      throw new Error("PC snapshot version must be a positive safe integer.");
    }

    const snapshotJson = JSON.stringify(input.snapshot);
    const mutationLockKey = `${input.actorUserId}:${input.mutationId}`;
    const objectLockKey = `PC:${input.pcId}`;
    const [, , resultRows] = await this.sql.transaction((txn) => [
      txn`SELECT pg_advisory_xact_lock(hashtextextended(${mutationLockKey}, 0))`,
      txn`SELECT pg_advisory_xact_lock(hashtextextended(${objectLockKey}, 0))`,
      txn`
        WITH existing_receipt AS (
          SELECT object_type, object_id, resulting_revision
          FROM mutation_receipt
          WHERE user_id = ${input.actorUserId}::uuid
            AND mutation_id = ${input.mutationId}::uuid
        ),
        membership AS (
          SELECT m.role
          FROM campaign_membership m
          JOIN campaign c ON c.id = m.campaign_id
          WHERE m.campaign_id = ${input.campaignId}::uuid
            AND m.user_id = ${input.actorUserId}::uuid
            AND m.status = 'ACTIVE'
            AND c.deleted_at IS NULL
        ),
        existing_pc AS (
          SELECT p.*
          FROM pc p
          WHERE p.id = ${input.pcId}::uuid
        ),
        permitted AS (
          SELECT 1
          FROM membership m
          WHERE NOT EXISTS (SELECT 1 FROM existing_pc)
             OR m.role = 'DM'
             OR EXISTS (
               SELECT 1
               FROM existing_pc p
               WHERE p.owner_user_id = ${input.actorUserId}::uuid
                  OR p.controller_user_id = ${input.actorUserId}::uuid
             )
        ),
        replayed AS (
          SELECT p.*, false AS applied
          FROM existing_receipt r
          JOIN pc p ON p.id = r.object_id
          WHERE r.object_type = 'PC'
            AND r.object_id = ${input.pcId}::uuid
            AND p.campaign_id = ${input.campaignId}::uuid
            AND EXISTS (SELECT 1 FROM permitted)
        ),
        created_pc AS (
          INSERT INTO pc(
            id,
            campaign_id,
            owner_user_id,
            controller_user_id,
            name,
            revision,
            snapshot_format,
            snapshot_version,
            snapshot,
            reconciled_at
          )
          SELECT
            ${input.pcId}::uuid,
            ${input.campaignId}::uuid,
            CASE WHEN (SELECT role FROM membership) = 'PLAYER' THEN ${input.actorUserId}::uuid ELSE NULL END,
            CASE WHEN (SELECT role FROM membership) = 'PLAYER' THEN ${input.actorUserId}::uuid ELSE NULL END,
            ${name},
            0,
            ${input.snapshotFormat},
            ${input.snapshotVersion},
            ${snapshotJson}::jsonb,
            now()
          WHERE NOT EXISTS (SELECT 1 FROM existing_receipt)
            AND NOT EXISTS (SELECT 1 FROM existing_pc)
            AND ${input.expectedRevision} = 0
            AND EXISTS (SELECT 1 FROM permitted)
          ON CONFLICT(id) DO NOTHING
          RETURNING pc.*, true AS applied
        ),
        updated_pc AS (
          UPDATE pc p
          SET name = ${name},
              revision = p.revision + 1,
              snapshot_format = ${input.snapshotFormat},
              snapshot_version = ${input.snapshotVersion},
              snapshot = ${snapshotJson}::jsonb,
              reconciled_at = now(),
              updated_at = now()
          WHERE p.id = ${input.pcId}::uuid
            AND p.campaign_id = ${input.campaignId}::uuid
            AND p.deleted_at IS NULL
            AND p.revision = ${input.expectedRevision}
            AND NOT EXISTS (SELECT 1 FROM existing_receipt)
            AND EXISTS (SELECT 1 FROM existing_pc)
            AND EXISTS (SELECT 1 FROM permitted)
          RETURNING p.*, true AS applied
        ),
        applied_pc AS (
          SELECT * FROM created_pc
          UNION ALL
          SELECT * FROM updated_pc
        ),
        new_receipt AS (
          INSERT INTO mutation_receipt(
            user_id, mutation_id, object_type, object_id, resulting_revision
          )
          SELECT
            ${input.actorUserId}::uuid,
            ${input.mutationId}::uuid,
            'PC',
            id,
            revision
          FROM applied_pc
          RETURNING object_type, object_id, resulting_revision
        )
        SELECT
          result.id::text AS id,
          result.campaign_id::text AS campaign_id,
          result.owner_user_id::text AS owner_user_id,
          result.controller_user_id::text AS controller_user_id,
          result.name,
          result.revision::text AS revision,
          CASE
            WHEN result.deleted_at IS NULL THEN NULL
            ELSE floor(extract(epoch FROM result.deleted_at))::bigint::text
          END AS deleted_at_epoch_seconds,
          result.snapshot_format,
          result.snapshot_version::text AS snapshot_version,
          result.snapshot,
          result.applied
        FROM (
          SELECT * FROM replayed
          UNION ALL
          SELECT * FROM applied_pc
        ) result
      `,
    ], { isolationLevel: "ReadCommitted" });

    const rows = resultRows as unknown as PcSnapshotRow[];
    if (rows.length !== 1) {
      await this.throwPcMutationFailure(input);
    }
    const row = requireSingle(rows, "PC snapshot mutation returned an unexpected result count.");
    return {
      pc: mapPcSnapshotRow(row),
      applied: row.applied === true,
    };
  }

  private async throwPcMutationFailure(input: PutPcSnapshotInput): Promise<never> {
    const receiptRows = await this.sql`
      SELECT object_type, object_id::text AS object_id
      FROM mutation_receipt
      WHERE user_id = ${input.actorUserId}::uuid
        AND mutation_id = ${input.mutationId}::uuid
    ` as unknown as Array<{ object_type: string; object_id: string }>;
    if (receiptRows.length > 0) {
      throw new MutationReuseError();
    }

    const membershipRows = await this.sql`
      SELECT m.role
      FROM campaign_membership m
      JOIN campaign c ON c.id = m.campaign_id
      WHERE m.campaign_id = ${input.campaignId}::uuid
        AND m.user_id = ${input.actorUserId}::uuid
        AND m.status = 'ACTIVE'
        AND c.deleted_at IS NULL
    ` as unknown as Array<{ role: CampaignRole }>;
    if (membershipRows.length !== 1) {
      throw new HostedAuthorizationError();
    }

    const pcRows = await this.sql`
      SELECT
        p.campaign_id::text AS campaign_id,
        p.owner_user_id::text AS owner_user_id,
        p.controller_user_id::text AS controller_user_id,
        p.revision::text AS revision,
        (p.deleted_at IS NOT NULL) AS deleted
      FROM pc p
      WHERE p.id = ${input.pcId}::uuid
    ` as unknown as PcMutationDiagnosticRow[];

    if (pcRows.length === 0) {
      throw new StaleRevisionError(null);
    }

    const pc = requireSingle(pcRows, "PC identity unexpectedly resolved to multiple rows.");
    if (pc.campaign_id !== input.campaignId) {
      throw new HostedAuthorizationError();
    }
    if (pc.deleted) {
      throw new HostedObjectGoneError();
    }

    const role = membershipRows[0].role;
    if (
      role !== "DM" &&
      pc.owner_user_id !== input.actorUserId &&
      pc.controller_user_id !== input.actorUserId
    ) {
      throw new HostedAuthorizationError();
    }

    const currentRevision = parseRevision(pc.revision);
    if (currentRevision !== input.expectedRevision) {
      throw new StaleRevisionError(currentRevision);
    }

    throw new Error("PC snapshot mutation could not be applied after passing authorization and revision checks.");
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

function mapPcAuthorityRow(row: PcAuthorityRow): PcAuthorityState {
  return {
    pcId: row.pc_id,
    campaignId: row.campaign_id,
    ownerUserId: row.owner_user_id,
    controllerUserId: row.controller_user_id,
  };
}

function mapPcSnapshotRow(row: PcSnapshotRow): PcSnapshotState {
  return {
    id: row.id,
    campaignId: row.campaign_id,
    ownerUserId: row.owner_user_id,
    controllerUserId: row.controller_user_id,
    name: row.name,
    revision: parseRevision(row.revision),
    deletedAtEpochSeconds: parseNullableEpochSeconds(row.deleted_at_epoch_seconds),
    snapshotFormat: row.snapshot_format,
    snapshotVersion: parsePositiveInteger(row.snapshot_version, "snapshot version"),
    snapshot: parseJsonObject(row.snapshot),
  };
}

function parseRevision(value: string): number {
  const revision = Number(value);
  if (!Number.isSafeInteger(revision) || revision < 0) {
    throw new Error("Database returned an invalid revision.");
  }
  return revision;
}

function parsePositiveInteger(value: string, label: string): number {
  const parsed = Number(value);
  if (!Number.isSafeInteger(parsed) || parsed < 1) {
    throw new Error(`Database returned an invalid ${label}.`);
  }
  return parsed;
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

function parseJsonObject(value: unknown): Record<string, unknown> {
  const parsed = typeof value === "string" ? JSON.parse(value) : value;
  if (parsed == null || typeof parsed !== "object" || Array.isArray(parsed)) {
    throw new Error("Database returned an invalid PC snapshot JSON object.");
  }
  return parsed as Record<string, unknown>;
}

function requireSingle<T>(rows: T[], message: string): T {
  if (rows.length !== 1) {
    throw new Error(message);
  }
  return rows[0];
}
