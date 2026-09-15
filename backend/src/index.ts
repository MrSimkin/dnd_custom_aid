import { createApiHandler, type ApiHandler } from "./app.ts";
import { DescopeJwtVerifier } from "./auth.ts";
import { NeonCampaignStore } from "./store.ts";

interface AppEnv {
  DATABASE_URL?: string;
  DESCOPE_PROJECT_ID?: string;
  DESCOPE_BASE_URL?: string;
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
