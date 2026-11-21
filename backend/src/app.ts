import { AuthenticationError, type AuthVerifier } from "./auth.ts";
import {
  type CampaignAdministrationStore,
  type CampaignModerationAction,
} from "./campaignAdministrationStore.ts";
import {
  InvitationBannedError,
  InvitationExpiredError,
  type InvitationStore,
} from "./invitationStore.ts";
import {
  DmContentIdentityError,
  type DmContentScopeKind,
  type DmContentStore,
} from "./dmContentStore.ts";
import {
  MEDIA_MAX_BYTES,
  MEDIA_TYPES,
  type MediaAuthorization,
  type MediaBytesStore,
  type MediaMigrationReport,
  MEDIA_MIGRATION_MAX,
  sha256Hex,
} from "./mediaStore.ts";
import type { ApiErrorBody, ApiErrorCode, Uuid } from "./contracts/spine.ts";
import {
  RULES_PASSAGE_MAX_CHARS,
  RULES_PASSAGES_MAX,
  RULES_QUESTION_MAX_CHARS,
  type RulesAnswerer,
  type RulesPassage,
} from "./rulesAnswer.ts";
import {
  HostedAuthorizationError,
  HostedObjectGoneError,
  HostedObjectNotFoundError,
  MutationReuseError,
  StaleRevisionError,
  type CampaignStore,
} from "./store.ts";

const CHARACTER_BACKUP_FORMAT = "dnd-custom-aid.character-backup";
const DM_CONTENT_FORMAT = "dnd-custom-aid.dm-content";
const DM_CONTENT_MAX_SNAPSHOT_BYTES = 1_000_000;
const DM_CONTENT_FAMILY_PATTERN = /^[A-Z][A-Z0-9_]{0,63}$/;
/** How many changes one group request may carry (owner's server plan A). */
const DM_CONTENT_GROUP_MAX = 100;

export interface ApiDependencies {
  auth: AuthVerifier;
  campaigns: CampaignStore;
  campaignAdministration?: CampaignAdministrationStore;
  dmContent?: DmContentStore;
  /** Absent until the owner binds storage for the images; media routes then answer 503. */
  media?: MediaBytesStore;
  mediaAuthorization?: MediaAuthorization;
  /**
   * Owner's K (2026-11-20): one bounded pass of the move from Workers KV to the bucket. Absent
   * unless both are bound, because there is nothing to move otherwise.
   */
  mediaMigration?: (options: { limit?: number; cursor?: string | null }) => Promise<MediaMigrationReport>;
  /** Absent until the invitation table exists; the invitation routes then answer 503. */
  invitations?: InvitationStore;
  /** Absent until the owner binds Workers AI (package 8); the rules route then answers 503. */
  rules?: RulesAnswerer;
}

export type ApiHandler = (request: Request) => Promise<Response>;

export function createApiHandler(dependencies: ApiDependencies): ApiHandler {
  return async (request: Request): Promise<Response> => {
    try {
      const url = new URL(request.url);
      const path = normalizePath(url.pathname);

      const invitePageMatch = /^\/unirme\/([A-Za-z]{6})$/.exec(path);
      if (invitePageMatch != null) {
        // Owner's A (2026-11-20): the link and the QR used to point at nothing. This page needs no
        // account and asks the database nothing: it only hands the six letters to whoever opened it.
        requireMethod(request, "GET");
        return invitationPage(invitePageMatch[1].toUpperCase());
      }

      if (path === "/health") {
        requireMethod(request, "GET");
        return jsonResponse({ status: "ok", service: "dnd-custom-aid-api" });
      }

      const campaignMembersMatch = /^\/v1\/campaigns\/([^/]+)\/members$/.exec(path);
      const campaignInvitationMatch = /^\/v1\/campaigns\/([^/]+)\/invitation$/.exec(path);
      const redeemInvitationMatch = /^\/v1\/invitations\/([A-Za-z]{6})$/.exec(path);
      const campaignMemberModerationMatch =
        /^\/v1\/campaigns\/([^/]+)\/members\/([^/]+)\/moderation$/.exec(path);
      const campaignPcsMatch = /^\/v1\/campaigns\/([^/]+)\/pcs$/.exec(path);
      const pcAuthorityMatch = /^\/v1\/pcs\/([^/]+)\/authority$/.exec(path);
      const pcSnapshotMatch = /^\/v1\/pcs\/([^/]+)$/.exec(path);
      const campaignContentMatch = /^\/v1\/campaigns\/([^/]+)\/content$/.exec(path);
      const contentMatch = /^\/v1\/content\/([^/]+)$/.exec(path);
      const mediaMatch = /^\/v1\/media\/([0-9a-f]{64})$/.exec(path);
      const sharedHandoutsMatch = /^\/v1\/campaigns\/([^/]+)\/handouts$/.exec(path);
      const knownFixedPath =
        path === "/v1/me" ||
        path === "/v1/me/content" ||
        path === "/v1/content:batch" ||
        path === "/v1/media:migrate" ||
        path === "/v1/campaigns" ||
        path === "/v1/campaign-memberships" ||
        path === "/v1/rules/answer";

      if (
        !knownFixedPath &&
        campaignMembersMatch == null &&
        campaignInvitationMatch == null &&
        redeemInvitationMatch == null &&
        campaignMemberModerationMatch == null &&
        campaignPcsMatch == null &&
        pcAuthorityMatch == null &&
        pcSnapshotMatch == null &&
        campaignContentMatch == null &&
        contentMatch == null &&
        mediaMatch == null &&
        sharedHandoutsMatch == null
      ) {
        throw new ApiProblem(404, "NOT_FOUND", "Route not found.");
      }

      const identity = await dependencies.auth.verify(request);
      const user = await dependencies.campaigns.resolveUser(identity.subject, identity.displayName);

      if (path === "/v1/rules/answer") {
        requireMethod(request, "POST");
        const rules = dependencies.rules;
        if (rules == null) {
          throw new ApiProblem(503, "TRANSIENT_FAILURE", "Rules answers are not available yet.");
        }
        const body = await readJsonObject(request);
        const question = requireNonBlankString(body.question, "question");
        if (question.length > RULES_QUESTION_MAX_CHARS) {
          throw new ApiProblem(400, "VALIDATION_FAILED", "question is too long.", { field: "question" });
        }
        const passages = requireRulesPassages(body.passages);
        try {
          const answer = await rules.answer(question, passages);
          return jsonResponse({ answer, passageCount: passages.length });
        } catch {
          // Quota exhausted or model unavailable: the App keeps showing the SRD search.
          throw new ApiProblem(503, "TRANSIENT_FAILURE", "Rules answers are not available right now.");
        }
      }

      if (path === "/v1/me") {
        requireMethod(request, "GET");
        return jsonResponse({
          account: {
            id: user.id,
            displayName: user.displayName,
          },
        });
      }

      if (path === "/v1/me/content") {
        requireMethod(request, "GET");
        const content = await requireDmContent(dependencies).listPersonalContent(user.id);
        return jsonResponse({ content });
      }

      if (campaignContentMatch != null) {
        requireMethod(request, "GET");
        const campaignId = requireUuid(campaignContentMatch[1], "campaignId");
        const content = await requireDmContent(dependencies).listCampaignContent(user.id, campaignId);
        return jsonResponse({ content });
      }

      if (sharedHandoutsMatch != null) {
        requireMethod(request, "GET");
        const campaignId = requireUuid(sharedHandoutsMatch[1], "campaignId");
        const authorization = dependencies.mediaAuthorization;
        if (authorization == null) {
          throw new ApiProblem(503, "TRANSIENT_FAILURE", "Media storage is not configured yet.");
        }
        const handouts = await authorization.listSharedHandouts(user.id, campaignId);
        if (handouts == null) {
          throw new ApiProblem(404, "NOT_FOUND", "Campaign not found.");
        }
        return jsonResponse({ handouts });
      }

      if (path === "/v1/media:migrate") {
        // Owner's K: the DM asks for one page of the move and is told where to continue.
        requireMethod(request, "POST");
        const authorization = dependencies.mediaAuthorization;
        const migrate = dependencies.mediaMigration;
        if (authorization == null || migrate == null) {
          throw new ApiProblem(503, "TRANSIENT_FAILURE", "There is nothing to move: object storage or KV is not bound.");
        }
        if (!(await authorization.isActiveDm(user.id))) {
          throw new ApiProblem(403, "FORBIDDEN", "Only an active DM may move the images.");
        }
        const limitText = url.searchParams.get("limit");
        const limit = limitText == null ? undefined : Number(limitText);
        if (limit != null && (!Number.isInteger(limit) || limit < 1 || limit > MEDIA_MIGRATION_MAX)) {
          throw new ApiProblem(400, "VALIDATION_FAILED", `limit must be between 1 and ${MEDIA_MIGRATION_MAX}.`, { field: "limit" });
        }
        const report = await migrate({ limit, cursor: url.searchParams.get("cursor") });
        return jsonResponse(report);
      }

      if (mediaMatch != null) {
        const sha256 = mediaMatch[1];
        const media = dependencies.media;
        const authorization = dependencies.mediaAuthorization;
        if (media == null || authorization == null) {
          throw new ApiProblem(503, "TRANSIENT_FAILURE", "Media storage is not configured yet.");
        }

        if (request.method === "PUT") {
          if (!(await authorization.isActiveDm(user.id))) {
            throw new ApiProblem(403, "FORBIDDEN", "Only an active DM may upload images.");
          }
          const mediaType = (request.headers.get("content-type") ?? "").split(";")[0].trim().toLowerCase();
          if (!MEDIA_TYPES.has(mediaType)) {
            throw new ApiProblem(400, "VALIDATION_FAILED", "Images must be JPEG, PNG or WebP.", { field: "content-type" });
          }
          const declared = Number(request.headers.get("content-length") ?? "0");
          if (declared > MEDIA_MAX_BYTES) {
            throw new ApiProblem(400, "VALIDATION_FAILED", "Images must be at most 5 MiB.", { field: "body" });
          }
          const bytes = await request.arrayBuffer();
          if (bytes.byteLength === 0 || bytes.byteLength > MEDIA_MAX_BYTES) {
            throw new ApiProblem(400, "VALIDATION_FAILED", "Images must be between 1 byte and 5 MiB.", { field: "body" });
          }
          if ((await sha256Hex(bytes)) !== sha256) {
            throw new ApiProblem(400, "VALIDATION_FAILED", "Image content does not match its checksum.", { field: "body" });
          }
          const stored = await media.put(sha256, mediaType, bytes);
          return jsonResponse({ sha256, stored }, stored ? 201 : 200);
        }

        requireMethod(request, "GET");
        const contentId = requireUuid(url.searchParams.get("content"), "content");
        // Not readable and not stored look the same, so the API never reveals other campaigns' files.
        if (!(await authorization.mayReadMedia(user.id, contentId, sha256))) {
          throw new ApiProblem(404, "NOT_FOUND", "Image not found.");
        }
        const stored = await media.get(sha256);
        if (stored == null) {
          throw new ApiProblem(404, "NOT_FOUND", "Image not found.");
        }
        return new Response(stored.bytes, {
          status: 200,
          headers: {
            "content-type": stored.mediaType,
            "cache-control": "private, max-age=31536000, immutable",
          },
        });
      }

      if (path === "/v1/content:batch") {
        // One request carries a group of changes instead of one each (owner's server plan A,
        // 2026-11-18). Each change is applied on its own: one refusal never stops the rest, and
        // what the group could not apply comes back per item so the App can keep it queued.
        requireMethod(request, "POST");
        const body = await readJsonObject(request);
        const items = body.items;
        if (!Array.isArray(items) || items.length === 0) {
          throw new ApiProblem(400, "VALIDATION_FAILED", "items must be a non-empty array.", { field: "items" });
        }
        if (items.length > DM_CONTENT_GROUP_MAX) {
          throw new ApiProblem(400, "VALIDATION_FAILED", `items must not carry more than ${DM_CONTENT_GROUP_MAX} changes.`, {
            field: "items",
          });
        }
        const store = requireDmContent(dependencies);
        const results: Array<Record<string, unknown>> = [];
        for (const raw of items) {
          const item = requireJsonObject(raw, "items[]");
          const contentId = requireUuid(item.contentId, "items[].contentId");
          try {
            const result = await applyDmContentChange(store, user.id, {
              contentId,
              mutationId: item.mutationId,
              expectedRevision: item.expectedRevision,
              snapshot: item.delete === true ? undefined : item.snapshot,
              remove: item.delete === true,
            });
            results.push({ contentId, content: result.content, applied: result.applied });
          } catch (error) {
            const problem = describeProblem(error);
            // Not the object's fault (no session, overload, a bug here): the whole group waits.
            if (problem.status >= 500 || problem.status === 401 || problem.status === 429) {
              throw error;
            }
            results.push({ contentId, errorCode: problem.code, errorMessage: problem.message });
          }
        }
        return jsonResponse({ results });
      }

      if (contentMatch != null) {
        const contentId = requireUuid(contentMatch[1], "contentId");
        if (request.method !== "PUT" && request.method !== "DELETE") {
          throw methodNotAllowed(["PUT", "DELETE"]);
        }
        const body = await readJsonObject(request);
        const result = await applyDmContentChange(requireDmContent(dependencies), user.id, {
          contentId,
          mutationId: body.mutationId,
          expectedRevision: body.expectedRevision,
          snapshot: request.method === "PUT" ? body.snapshot : undefined,
          remove: request.method === "DELETE",
        });
        return jsonResponse({ content: result.content, applied: result.applied });
      }

      if (path === "/v1/campaign-memberships") {
        requireMethod(request, "GET");
        const memberships = await dependencies.campaigns.listCampaignMemberships(user.id);
        return jsonResponse({ memberships });
      }

      if (campaignInvitationMatch != null) {
        // Owner, 2026-11-18: the DM holds one live code per campaign, with its expiry.
        const campaignId = requireUuid(campaignInvitationMatch[1], "campaignId");
        const invitations = requireInvitations(dependencies);
        if (request.method === "GET") {
          const invitation = await invitations.current(user.id, campaignId);
          return jsonResponse({ invitation });
        }
        if (request.method === "POST") {
          const body = await readJsonObject(request);
          const days = body.daysValid === undefined ? 7 : requirePositiveInteger(body.daysValid, "daysValid");
          const invitation = await invitations.create(user.id, campaignId, days);
          return jsonResponse({ invitation }, 201);
        }
        if (request.method === "DELETE") {
          const revoked = await invitations.revoke(user.id, campaignId);
          return jsonResponse({ revoked });
        }
        throw methodNotAllowed(["GET", "POST", "DELETE"]);
      }

      if (redeemInvitationMatch != null) {
        requireMethod(request, "POST");
        const joined = await requireInvitations(dependencies).redeem(user.id, redeemInvitationMatch[1]);
        return jsonResponse({ joined });
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

/** What someone sees when they open an invitation link on their phone. */
function invitationPage(code: string): Response {
  const body = `<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
<title>Invitación a una campaña</title>
<style>
  :root { color-scheme: light dark; --bg: #faf7f2; --fg: #1d1b16; --soft: #6b6559; --card: #ffffff; }
  @media (prefers-color-scheme: dark) {
    :root { --bg: #16150f; --fg: #f1ece1; --soft: #a9a294; --card: #211f17; }
  }
  body { margin: 0; background: var(--bg); color: var(--fg); font: 16px/1.5 system-ui, sans-serif; }
  main { max-width: 26rem; margin: 0 auto; padding: 2rem max(16px, env(safe-area-inset-left)) 3rem; }
  h1 { font-size: 1.4rem; margin: 0 0 .25rem; }
  p { margin: .6rem 0; }
  .soft { color: var(--soft); font-size: .9rem; }
  .code { background: var(--card); border-radius: 14px; padding: 1rem 1.2rem; margin: 1.2rem 0;
          font-size: 2.1rem; letter-spacing: .35rem; text-align: center; font-weight: 700; }
  a.button { display: inline-block; background: var(--fg); color: var(--bg); text-decoration: none;
             padding: .7rem 1.1rem; border-radius: 999px; font-weight: 600; }
  ol { padding-left: 1.2rem; }
</style>
</head>
<body>
<main>
  <h1>Te invitaron a una campaña</h1>
  <p>Este es tu código para entrar en D&amp;D Custom Aid:</p>
  <div class="code">${code}</div>
  <p><a class="button" href="dndcustomaid://unirme/${code}">Abrir la App con este código</a></p>
  <p class="soft">Si el botón no hace nada, la App todavía no está instalada en este teléfono, o se abre así:</p>
  <ol class="soft">
    <li>Abre D&amp;D Custom Aid.</li>
    <li>Ve a «Tus campañas» y toca «＋ Unirme».</li>
    <li>Escribe las seis letras de arriba.</li>
  </ol>
  <p class="soft">El código vence; si ya no funciona, pídele otro a tu DM.</p>
</main>
</body>
</html>`;
  return new Response(body, {
    status: 200,
    headers: {
      "content-type": "text/html; charset=utf-8",
      "cache-control": "no-store",
      "referrer-policy": "no-referrer",
      "x-content-type-options": "nosniff",
    },
  });
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

/** One queued change as it arrives, alone on its own route or inside a group. */
interface DmContentChange {
  contentId: Uuid;
  mutationId: unknown;
  expectedRevision: unknown;
  snapshot?: unknown;
  remove: boolean;
}

/** Validates one change and applies it, so the single route and a group behave identically. */
async function applyDmContentChange(
  store: DmContentStore,
  actorUserId: Uuid,
  change: DmContentChange,
): Promise<{ content: unknown; applied: boolean }> {
  const mutationId = requireUuid(change.mutationId, "mutationId");
  const expectedRevision = requireNonNegativeInteger(change.expectedRevision, "expectedRevision");
  const contentId = change.contentId;

  if (change.remove) {
    return await store.deleteContent({ actorUserId, mutationId, contentId, expectedRevision });
  }

  const snapshot = requireJsonObject(change.snapshot, "snapshot");
  const snapshotFormat = requireNonBlankString(snapshot.format, "snapshot.format");
  const snapshotVersion = requirePositiveInteger(snapshot.version, "snapshot.version");
  if (snapshotFormat !== DM_CONTENT_FORMAT) {
    throw new ApiProblem(400, "VALIDATION_FAILED", "snapshot.format is not a supported DM content format.", {
      field: "snapshot.format",
    });
  }
  if (JSON.stringify(snapshot).length > DM_CONTENT_MAX_SNAPSHOT_BYTES) {
    throw new ApiProblem(400, "VALIDATION_FAILED", "snapshot is too large.", { field: "snapshot" });
  }
  const content = requireJsonObject(snapshot.content, "snapshot.content");
  if (requireUuid(content.id, "snapshot.content.id") !== contentId) {
    throw new ApiProblem(400, "VALIDATION_FAILED", "Snapshot identity must match the requested content.", {
      field: "snapshot.content.id",
    });
  }
  const family = requireNonBlankString(content.family, "snapshot.content.family");
  if (!DM_CONTENT_FAMILY_PATTERN.test(family)) {
    throw new ApiProblem(400, "VALIDATION_FAILED", "snapshot.content.family is invalid.", {
      field: "snapshot.content.family",
    });
  }
  const displayName = requireNonBlankString(content.displayName, "snapshot.content.displayName");
  const scope = requireJsonObject(content.scope, "snapshot.content.scope");
  const scopeKind = requireDmContentScopeKind(scope.kind);
  const scopeRef = requireUuid(scope.ref, "snapshot.content.scope.ref");

  return await store.putContent({
    actorUserId,
    mutationId,
    contentId,
    expectedRevision,
    scopeKind,
    scopeRef,
    family,
    displayName,
    snapshotFormat,
    snapshotVersion,
    snapshot,
  });
}

function requireInvitations(dependencies: ApiDependencies): InvitationStore {
  const invitations = dependencies.invitations;
  if (invitations == null) {
    throw new ApiProblem(503, "TRANSIENT_FAILURE", "Las invitaciones todavía no están disponibles en el servidor.");
  }
  return invitations;
}

function requireDmContent(dependencies: ApiDependencies): DmContentStore {
  const store = dependencies.dmContent;
  if (store == null) {
    throw new Error("DM content API dependency is not configured.");
  }
  return store;
}

function requireDmContentScopeKind(value: unknown): DmContentScopeKind {
  if (value === "PERSONAL" || value === "CAMPAIGN") {
    return value;
  }
  throw new ApiProblem(400, "VALIDATION_FAILED", "snapshot.content.scope.kind must be PERSONAL or CAMPAIGN.", {
    field: "snapshot.content.scope.kind",
  });
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

function requireRulesPassages(value: unknown): RulesPassage[] {
  if (!Array.isArray(value) || value.length === 0 || value.length > RULES_PASSAGES_MAX) {
    throw new ApiProblem(400, "VALIDATION_FAILED", `passages must hold 1 to ${RULES_PASSAGES_MAX} SRD passages.`, { field: "passages" });
  }
  return value.map((entry, index) => {
    const passage = requireJsonObject(entry, `passages[${index}]`);
    const version = requireNonBlankString(passage.version, `passages[${index}].version`);
    if (version !== "5.1" && version !== "5.2.1") {
      throw new ApiProblem(400, "VALIDATION_FAILED", "passages[].version must be 5.1 or 5.2.1.", { field: `passages[${index}].version` });
    }
    const page = passage.page;
    if (typeof page !== "number" || !Number.isInteger(page) || page < 1 || page > 2000) {
      throw new ApiProblem(400, "VALIDATION_FAILED", "passages[].page must be a page number.", { field: `passages[${index}].page` });
    }
    const text = requireNonBlankString(passage.text, `passages[${index}].text`);
    const heading = typeof passage.heading === "string" ? passage.heading.trim().slice(0, 200) : "";
    return { version, page, heading, text: text.slice(0, RULES_PASSAGE_MAX_CHARS) };
  });
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

interface ApiProblemShape {
  status: number;
  code: ApiErrorCode;
  message: string;
  details?: Record<string, unknown>;
  headers?: HeadersInit;
}

/** The status and code one failure deserves, used both for a response and per item of a group. */
function describeProblem(error: unknown): ApiProblemShape {
  if (error instanceof ApiProblem) {
    return { status: error.status, code: error.code, message: error.message, details: error.details, headers: error.headers };
  }
  if (error instanceof DmContentIdentityError) {
    return { status: 400, code: "VALIDATION_FAILED", message: error.message };
  }
  if (error instanceof AuthenticationError) {
    return {
      status: 401,
      code: "UNAUTHENTICATED",
      message: error.message,
      headers: { "WWW-Authenticate": "Bearer" },
    };
  }
  if (error instanceof HostedAuthorizationError) {
    return { status: 403, code: "FORBIDDEN", message: error.message };
  }
  if (error instanceof MutationReuseError) {
    return { status: 409, code: "CONFLICT_MUTATION_REUSE", message: error.message };
  }
  if (error instanceof StaleRevisionError) {
    return {
      status: 409,
      code: "CONFLICT_STALE_REVISION",
      message: error.message,
      details: { currentRevision: error.currentRevision },
    };
  }
  if (error instanceof InvitationExpiredError) {
    return { status: 410, code: "GONE", message: error.message };
  }
  if (error instanceof InvitationBannedError) {
    return { status: 403, code: "FORBIDDEN", message: error.message };
  }
  if (error instanceof HostedObjectGoneError) {
    return { status: 410, code: "GONE", message: error.message };
  }
  if (error instanceof HostedObjectNotFoundError) {
    return { status: 404, code: "NOT_FOUND", message: error.message };
  }

  console.error("Unhandled API error", error);
  return { status: 500, code: "INTERNAL_ERROR", message: "Unexpected server error." };
}

function errorResponse(error: unknown): Response {
  const problem = describeProblem(error);
  return jsonError(problem.status, problem.code, problem.message, problem.details, problem.headers);
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
