# Backend / hosted API

Cloudflare Worker API for the integrated `dnd_custom_aid` MVP.

Current implemented foundation:

- `GET /health` is public and does not require provider configuration;
- protected `/v1/*` routes validate Descope session JWTs against the project JWKS and required audience;
- authenticated provider subjects are resolved to stable application accounts;
- `GET /v1/me` returns the current application account;
- `GET /v1/campaigns` lists active campaign memberships;
- `POST /v1/campaigns` creates an independently identified campaign, makes the creator its DM, and uses a mutation UUID for retry-safe idempotency;
- `GET /v1/campaigns/{campaignId}/members` returns the active-DM-authorized hosted member roster;
- `POST /v1/campaigns/{campaignId}/members/{userId}/moderation` applies server-authoritative Player `KICK`, `BAN`, or `LIFT_BAN` actions;
- `PUT /v1/pcs/{pcId}/authority` lets an active campaign DM explicitly replace nullable PC owner/controller authority using active same-campaign members without mutating the PC snapshot revision/content;
- Neon PostgreSQL is accessed through the edge-compatible `@neondatabase/serverless` HTTP driver;
- API, authentication and Campaign Administration boundaries have deterministic tests.

Runtime configuration is intentionally supplied through Worker environment/secrets, never committed:

- `DATABASE_URL` — Neon PostgreSQL connection string;
- `DESCOPE_PROJECT_ID` — Descope project/audience identifier;
- `DESCOPE_BASE_URL` — optional HTTPS Descope base URL; defaults to `https://api.descope.com`.

`backend/wrangler.jsonc` declares `DATABASE_URL` and `DESCOPE_PROJECT_ID` as required secret names. Their values remain provider-side; normal deployment must not copy them into Git, chat, command history or a `--secrets-file` merely to redeploy code.

No provider account, database, secret, or deployment is created by the repository itself. R2, Durable Objects, WebSockets, queues, and generalized realtime remain outside this foundation.

Commands from this directory:

```bash
npm install --no-package-lock
npm run check
npm run dev
npm run deploy
```

`npm run check` generates Worker types, type-checks the TypeScript surface, and runs the backend tests with Node's built-in test runner.

`npm run deploy` is the explicit owner/provider deployment action for the existing Cloudflare Worker named `dnd-custom-aid-api`. The repository has no automatic Worker deployment workflow. Existing configured Worker secrets are expected to remain provider-side across a normal code deployment; the required-secret declaration causes deployment to fail if those required secret names are missing instead of silently publishing an unusable configuration.

Before deployment, confirm the local Wrangler session with:

```bash
npx wrangler whoami
```

After deployment, a secret-free public route-presence check can distinguish the new Campaign Administration route from the pre-deployment Worker without exposing a JWT:

```bash
node -e "fetch('https://dnd-custom-aid-api.mrsimkin-dev.workers.dev/health').then(async r => console.log(r.status, await r.text()))"
node -e "fetch('https://dnd-custom-aid-api.mrsimkin-dev.workers.dev/v1/campaigns/00000000-0000-0000-0000-000000000000/members').then(async r => console.log(r.status, await r.text()))"
node -e "fetch('https://dnd-custom-aid-api.mrsimkin-dev.workers.dev/v1/pcs/00000000-0000-0000-0000-000000000000/authority').then(async r => console.log(r.status, await r.text()))"
```

Expected after the current Worker code is live:

- `/health` -> HTTP `200` with the normal service health JSON;
- the unauthenticated Campaign Administration route -> HTTP `401` / `UNAUTHENTICATED`;
- the unauthenticated PC authority route -> HTTP `401` / `UNAUTHENTICATED`.

The protected-route checks are intentionally unauthenticated. A `401` means the route is recognized and reached the authentication boundary; a `404` on the PC authority route would indicate that the deployed Worker still predates PR #79.
