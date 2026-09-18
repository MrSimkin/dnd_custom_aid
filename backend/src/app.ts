import { AuthenticationError, type AuthVerifier } from "./auth.ts";
import {
  type CampaignAdministrationStore,
  type CampaignModerationAction,
} from "./campaignAdministrationStore.ts";
import type { ApiErrorBody, ApiErrorCode, Uuid } from "./contracts/spine.ts";
import {
  HostedAuthorizationError,
  HostedObjectGoneError,
  HostedObjectNotFoundError,
  MutationReuseError,
  StaleRevisionError,
  type CampaignStore,
} from "./store.ts";

const CHARACTER_BACKUP_FORMAT = "dnd-custom-aid.character-backup";

export interface ApiDependencies {
  auth: AuthVerifier;
  campaigns: CampaignStore;
  campaignAdministration?: CampaignAdministrationStore;
}

export type ApiHandler = (request: Request) => Promise<Response>;

export function createApiHandler(dependencies: ApiDependencies): ApiHandler {
  return async (request: Request): Promise<Response> => {
    try {
      const url = new URL(request.url);
      const path = normalizePath(url.pathname);

      if (path === "/health") {
        requireMethod(request, "GET");
        return jsonResponse({ status: "ok", service: "dnd-custom-aid-api" });
      }

      const campaignMembersMatch = /^\/v1\/campaigns\/([^/]+)\/members$/.exec(path);
      const campaignMemberModerationMatch =
        /^\/v1\/campaigns\/([^/]+)\/members\/([^/]+)\/moderation$/.exec(path);
      const campaignPcsMatch = /^\/v1\/campaigns\/([^/]+)\/pcs$/.exec(path);
      const pcAuthorityMatch = /^\/v1\/pcs\/([^/]+)\/authority$/.exec(path);
      const pcSnapshotMatch = /^\/v1\/pcs\/([^/]+)$/.exec(path);
      const knownFixedPath =
        path === "/v1/me" ||
        path === "/v1/campaigns" ||
        path === "/v1/campaign-memberships";

      if (
        !knownFixedPath &&
        campaignMembersMatch == null &&
        campaignMemberModerationMatch == null &&
        campaignPcsMatch == null &&
        pcAuthorityMatch == null &&
        pcSnapshotMatch == null
      ) {
        throw new ApiProblem(404, "NOT_FOUND", "Route not found.");
      }

      const identity = await dependencies.auth.verify(request);
      const user = await dependencies.campaigns.resolveUser(identity.subject, identity.displayName);

      if (path === "/v1/me") {
        requireMethod(request, "GET");
        return jsonResponse({
          account: {
            id: user.id,
            displayName: user.displayName,
          },
        });
      }

      if (path === "/v1/campaign-memberships") {
        requireMethod(request, "GET");
        const memberships = await dependencies.campaigns.listCampaignMemberships(user.id);
        return jsonResponse({ memberships });
      }

      if (campaignMembersMatch != null) {
        requireMethod(request, "GET");
        const campaignId = requireUuid(campaignMembersMatch[1], "campaignId");
        const administration = requireCampaignAdministration(dependencies);
        const roster = await administration.listMembers(user.id, campaignId);
        return jsonResponse(roster);
      }

      if (campaignMemberModerationMatch != null) {
        requireMethod(request, "POST");
        const campaignId = requireUuid(campaignMemberModerationMatch[1], "campaignId");
        const targetUserId = requireUuid(campaignMemberModerationMatch[2], "userId");
        const body = await readJsonObject(request);
        const action = requireCampaignModerationAction(body.action);
        const administration = requireCampaignAdministration(dependencies);
        const result = await administration.moderateMember({
          actorUserId: user.id,
          campaignId,
          targetUserId,
          action,
        });
        return jsonResponse({
          member: result.member,
          campaignRevision: result.campaignRevision,
          applied: result.applied,
        });
      }

      if (campaignPcsMatch != null) {
        requireMethod(request, "GET");
        const campaignId = requireUuid(campaignPcsMatch[1], "campaignId");
        const pcs = await dependencies.campaigns.listPcSnapshots(user.id, campaignId);
        return jsonResponse({ pcs });
      }

      if (pcAuthorityMatch != null) {
        requireMethod(request, "PUT");
        const pcId = requireUuid(pcAuthorityMatch[1], "pcId");
        const body = await readJsonObject(request);
        const campaignId = requireUuid(body.campaignId, "campaignId");
        const ownerUserId = requireNullableUuidField(body, "ownerUserId");
        const controllerUserId = requireNullableUuidField(body, "controllerUserId");
        const result = await dependencies.campaigns.setPcAuthority({
          actorUserId: user.id,
          pcId,
          campaignId,
          ownerUserId,
          controllerUserId,
        });
        return jsonResponse({
          authority: {
            pcId: result.authority.pcId,
            campaignId: result.authority.campaignId,
            ownerUserId: result.authority.ownerUserId,
            controllerUserId: result.authority.controllerUserId,
          },
          applied: result.applied,
        });
      }

      if (pcSnapshotMatch != null) {
        requireMethod(request, "PUT");
        const pcId = requireUuid(pcSnapshotMatch[1], "pcId");
        const body = await readJsonObject(request);
        const mutationId = requireUuid(body.mutationId, "mutationId");
        const campaignId = requireUuid(body.campaignId, "campaignId");
        const expectedRevision = requireNonNegativeInteger(body.expectedRevision, "expectedRevision");
        const snapshot = requireJsonObject(body.snapshot, "snapshot");
        const snapshotFormat = requireNonBlankString(snapshot.format, "snapshot.format");
        const snapshotVersion = requirePositiveInteger(snapshot.version, "snapshot.version");
        if (snapshotFormat !== CHARACTER_BACKUP_FORMAT) {
          throw new ApiProblem(400, "VALIDATION_FAILED", "snapshot.format is not a supported character snapshot format.", {
            field: "snapshot.format",
          });
        }

        const character = requireJsonObject(snapshot.character, "snapshot.character");
        const snapshotPcId = requireUuid(character.id, "snapshot.character.id");
        const snapshotCampaignId = requireUuid(character.campaignId, "snapshot.character.campaignId");
        const name = requireNonBlankString(character.name, "snapshot.character.name");
        if (snapshotPcId !== pcId || snapshotCampaignId !== campaignId) {
          throw new ApiProblem(
            400,
            "VALIDATION_FAILED",
            "Snapshot identity must match the requested PC and campaign.",
          );
        }

        const result = await dependencies.campaigns.putPcSnapshot({
          actorUserId: user.id,
          mutationId,
          pcId,
          campaignId,
          expectedRevision,
          name,
          snapshotFormat,
          snapshotVersion,
          snapshot,
        });
        return jsonResponse({ pc: result.pc, applied: result.applied });
      }

      if (request.method === "GET") {
        const campaigns = await dependencies.campaigns.listCampaigns(user.id);
        return jsonResponse({ campaigns });
      }

      if (request.method === "POST") {
        const body = await readJsonObject(request);
        const mutationId = requireUuid(body.mutationId, "mutationId");
        const campaignId = requireUuid(body.campaignId, "campaignId");
        const name = requireNonBlankString(body.name, "name");
        const result = await dependencies.campaigns.createCampaign({
          actorUserId: user.id,
          mutationId,
          campaignId,
          name,
        });
        return jsonResponse(
          {
            campaign: result.campaign,
            created: result.created,
          },
          result.created ? 201 : 200,
        );
      }

      throw methodNotAllowed(["GET", "POST"]);
    } catch (error) {
      return errorResponse(error);
    }
  };
}

class ApiProblem extends Error {
  readonly status: number;
  readonly code: ApiErrorCode;
  readonly details?: Record<string, unknown>;
  readonly headers?: HeadersInit;

  constructor(
    status: number,
    code: ApiErrorCode,
    message: string,
    details?: Record<string, unknown>,
    headers?: HeadersInit,
  ) {
    super(message);
    this.name = "ApiProblem";
    this.status = status;
    this.code = code;
    this.details = details;
    this.headers = headers;
  }
}

function normalizePath(pathname: string): string {
  if (pathname.length > 1 && pathname.endsWith("/")) {
    return pathname.slice(0, -1);
  }
  return pathname;
}

function requireCampaignAdministration(dependencies: ApiDependencies): CampaignAdministrationStore {
  const administration = dependencies.campaignAdministration;
  if (administration == null) {
    throw new Error("Campaign Administration API dependency is not configured.");
  }
  return administration;
}

function requireMethod(request: Request, method: string): void {
  if (request.method !== method) {
    throw methodNotAllowed([method]);
  }
}

function methodNotAllowed(allowed: string[]): ApiProblem {
  return new ApiProblem(
    405,
    "VALIDATION_FAILED",
    "Method not allowed.",
    undefined,
    { Allow: allowed.join(", ") },
  );
}

async function readJsonObject(request: Request): Promise<Record<string, unknown>> {
  let value: unknown;
  try {
    value = await request.json();
  } catch {
    throw new ApiProblem(400, "VALIDATION_FAILED", "Request body must be valid JSON.");
  }

  return requireJsonObject(value, "request");
}

function requireJsonObject(value: unknown, field: string): Record<string, unknown> {
  if (value == null || typeof value !== "object" || Array.isArray(value)) {
    throw new ApiProblem(400, "VALIDATION_FAILED", `${field} must be a JSON object.`, { field });
  }
  return value as Record<string, unknown>;
}

function requireNonBlankString(value: unknown, field: string): string {
  if (typeof value !== "string" || value.trim().length === 0) {
    throw new ApiProblem(400, "VALIDATION_FAILED", `${field} must be a non-blank string.`, { field });
  }
  return value.trim();
}

function requireCampaignModerationAction(value: unknown): CampaignModerationAction {
  if (value === "KICK" || value === "BAN" || value === "LIFT_BAN") {
    return value;
  }
  throw new ApiProblem(
    400,
    "VALIDATION_FAILED",
    "action must be one of KICK, BAN or LIFT_BAN.",
    { field: "action" },
  );
}

function requireUuid(value: unknown, field: string): Uuid {
  if (typeof value !== "string" || !UUID_PATTERN.test(value)) {
    throw new ApiProblem(400, "VALIDATION_FAILED", `${field} must be a UUID.`, { field });
  }
  return value.toLowerCase();
}

function requireNullableUuidField(body: Record<string, unknown>, field: string): Uuid | null {
  if (!Object.prototype.hasOwnProperty.call(body, field)) {
    throw new ApiProblem(400, "VALIDATION_FAILED", `${field} must be explicitly provided as a UUID or null.`, { field });
  }
  const value = body[field];
  if (value === null) {
    return null;
  }
  return requireUuid(value, field);
}

function requireNonNegativeInteger(value: unknown, field: string): number {
  if (typeof value !== "number" || !Number.isSafeInteger(value) || value < 0) {
    throw new ApiProblem(400, "VALIDATION_FAILED", `${field} must be a non-negative safe integer.`, { field });
  }
  return value;
}

function requirePositiveInteger(value: unknown, field: string): number {
  if (typeof value !== "number" || !Number.isSafeInteger(value) || value < 1) {
    throw new ApiProblem(400, "VALIDATION_FAILED", `${field} must be a positive safe integer.`, { field });
  }
  return value;
}

function errorResponse(error: unknown): Response {
  if (error instanceof ApiProblem) {
    return jsonError(error.status, error.code, error.message, error.details, error.headers);
  }
  if (error instanceof AuthenticationError) {
    return jsonError(401, "UNAUTHENTICATED", error.message, undefined, {
      "WWW-Authenticate": "Bearer",
    });
  }
  if (error instanceof HostedAuthorizationError) {
    return jsonError(403, "FORBIDDEN", error.message);
  }
  if (error instanceof MutationReuseError) {
    return jsonError(409, "CONFLICT_MUTATION_REUSE", error.message);
  }
  if (error instanceof StaleRevisionError) {
    return jsonError(409, "CONFLICT_STALE_REVISION", error.message, {
      currentRevision: error.currentRevision,
    });
  }
  if (error instanceof HostedObjectGoneError) {
    return jsonError(410, "GONE", error.message);
  }
  if (error instanceof HostedObjectNotFoundError) {
    return jsonError(404, "NOT_FOUND", error.message);
  }

  console.error("Unhandled API error", error);
  return jsonError(500, "INTERNAL_ERROR", "Unexpected server error.");
}

function jsonError(
  status: number,
  code: ApiErrorCode,
  message: string,
  details?: Record<string, unknown>,
  headers?: HeadersInit,
): Response {
  const body: ApiErrorBody = {
    code,
    message,
    ...(details == null ? {} : { details }),
  };
  return jsonResponse(body, status, headers);
}

function jsonResponse(value: unknown, status = 200, headers?: HeadersInit): Response {
  const responseHeaders = new Headers(headers);
  responseHeaders.set("content-type", "application/json; charset=utf-8");
  responseHeaders.set("cache-control", "no-store");
  return new Response(JSON.stringify(value), { status, headers: responseHeaders });
}

const UUID_PATTERN = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
