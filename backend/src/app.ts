import { AuthenticationError, type AuthVerifier } from "./auth.ts";
import type { ApiErrorBody, ApiErrorCode, Uuid } from "./contracts/spine.ts";
import { MutationReuseError, type CampaignStore } from "./store.ts";

export interface ApiDependencies {
  auth: AuthVerifier;
  campaigns: CampaignStore;
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

      if (
        path !== "/v1/me" &&
        path !== "/v1/campaigns" &&
        path !== "/v1/campaign-memberships"
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

  if (value == null || typeof value !== "object" || Array.isArray(value)) {
    throw new ApiProblem(400, "VALIDATION_FAILED", "Request body must be a JSON object.");
  }
  return value as Record<string, unknown>;
}

function requireNonBlankString(value: unknown, field: string): string {
  if (typeof value !== "string" || value.trim().length === 0) {
    throw new ApiProblem(400, "VALIDATION_FAILED", `${field} must be a non-blank string.`, { field });
  }
  return value.trim();
}

function requireUuid(value: unknown, field: string): Uuid {
  if (typeof value !== "string" || !UUID_PATTERN.test(value)) {
    throw new ApiProblem(400, "VALIDATION_FAILED", `${field} must be a UUID.`, { field });
  }
  return value.toLowerCase();
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
  if (error instanceof MutationReuseError) {
    return jsonError(409, "CONFLICT_MUTATION_REUSE", error.message);
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
