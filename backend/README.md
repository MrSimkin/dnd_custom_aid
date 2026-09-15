# Backend / hosted API

Cloudflare Worker API for the integrated `dnd_custom_aid` MVP.

Current implemented foundation:

- `GET /health` is public and does not require provider configuration;
- protected `/v1/*` routes validate Descope session JWTs against the project JWKS and required audience;
- authenticated provider subjects are resolved to stable application accounts;
- `GET /v1/me` returns the current application account;
- `GET /v1/campaigns` lists active campaign memberships;
- `POST /v1/campaigns` creates an independently identified campaign, makes the creator its DM, and uses a mutation UUID for retry-safe idempotency;
- Neon PostgreSQL is accessed through the edge-compatible `@neondatabase/serverless` HTTP driver;
- API and authentication boundaries have deterministic Node tests.

Runtime configuration is intentionally supplied through Worker environment/secrets, never committed:

- `DATABASE_URL` — Neon PostgreSQL connection string;
- `DESCOPE_PROJECT_ID` — Descope project/audience identifier;
- `DESCOPE_BASE_URL` — optional HTTPS Descope base URL; defaults to `https://api.descope.com`.

No provider account, database, secret, or deployment is created by the repository itself. R2, Durable Objects, WebSockets, queues, and generalized realtime remain outside this foundation.

Commands from this directory:

```bash
npm install
npm run check
npm run dev
```

`npm run check` generates Worker types, type-checks the TypeScript surface, and runs the backend tests with Node's built-in test runner.
