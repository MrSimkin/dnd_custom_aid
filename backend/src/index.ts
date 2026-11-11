import { createApiHandler, type ApiHandler } from "./app.ts";
import { DescopeJwtVerifier } from "./auth.ts";
import { NeonCampaignAdministrationStore } from "./campaignAdministrationStore.ts";
import { NeonDmContentStore } from "./dmContentStore.ts";
import { MediaStore, NeonMediaAuthorization, type MediaKv } from "./mediaStore.ts";
import { RulesAnswerer, type AiRunner } from "./rulesAnswer.ts";
import { NeonCampaignStore } from "./store.ts";

interface AppEnv {
  DATABASE_URL?: string;
  DESCOPE_PROJECT_ID?: string;
  DESCOPE_BASE_URL?: string;
  /** Workers KV namespace for handout images (P27); optional so the Worker deploys without it. */
  MEDIA?: MediaKv;
  /** Workers AI binding for rules answers (package 8); optional so the Worker deploys without it. */
  AI?: AiRunner;
  /** Replaceable model name (D-0070); the default is in rulesAnswer.ts. */
  AI_MODEL?: string;
}

let cachedHandler: ApiHandler | null = null;

export default {
  async fetch(request: Request, env: AppEnv): Promise<Response> {
    const url = new URL(request.url);
    if (normalizePath(url.pathname) === "/health") {
      return jsonResponse({ status: "ok", service: "dnd-custom-aid-api" });
    }

    const databaseUrl = env.DATABASE_URL?.trim();
    const projectId = env.DESCOPE_PROJECT_ID?.trim();
    if (!databaseUrl || !projectId) {
      return jsonResponse(
        {
          code: "TRANSIENT_FAILURE",
          message: "Hosted API is not configured.",
        },
        503,
      );
    }

    if (cachedHandler == null) {
      cachedHandler = createApiHandler({
        auth: new DescopeJwtVerifier({
          projectId,
          baseUrl: env.DESCOPE_BASE_URL,
        }),
        campaigns: new NeonCampaignStore(databaseUrl),
        campaignAdministration: new NeonCampaignAdministrationStore(databaseUrl),
        dmContent: new NeonDmContentStore(databaseUrl),
        media: env.MEDIA == null ? undefined : new MediaStore(env.MEDIA),
        mediaAuthorization: env.MEDIA == null ? undefined : new NeonMediaAuthorization(databaseUrl),
        rules: env.AI == null ? undefined : new RulesAnswerer(env.AI, env.AI_MODEL?.trim() || undefined),
      });
    }

    return cachedHandler(request);
  },
} satisfies ExportedHandler<AppEnv>;

function normalizePath(pathname: string): string {
  if (pathname.length > 1 && pathname.endsWith("/")) {
    return pathname.slice(0, -1);
  }
  return pathname;
}

function jsonResponse(value: unknown, status = 200): Response {
  return new Response(JSON.stringify(value), {
    status,
    headers: {
      "content-type": "application/json; charset=utf-8",
      "cache-control": "no-store",
    },
  });
}
