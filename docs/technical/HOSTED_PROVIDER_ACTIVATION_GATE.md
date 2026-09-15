# Hosted provider activation gate — development environment handoff

**Status:** PREPARED / OWNER ACTION REQUIRED BEFORE ACTIVATION  
**Prepared:** 2026-09-15  
**Implementation checkpoint:** `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`

This document defines the first external-service activation boundary for the integrated MVP. It is deliberately a handoff/checklist, not authorization to create accounts or resources.

## Purpose

Provider-neutral implementation and CI have reached the point where further meaningful Player↔Server work requires a real authenticated hosted development environment.

The first development environment uses:

- Cloudflare Worker/API runtime;
- Neon PostgreSQL;
- Descope authentication/identity.

Cloudflare R2 is later and is not needed for this gate.

## Already ready before activation

Repository code already provides:

- Worker request handling and `/v1` hosted domain routes;
- application-owned server authorization;
- provider-neutral token-verifier seam and local-only static auth mode;
- Descope/JWKS verifier path;
- explicit PostgreSQL migrations and PostgreSQL 17 contract tests;
- campaign/membership lifecycle contracts;
- hosted PC snapshot contracts and authorization;
- shared Ktor hosted client;
- `HostedAccessTokenProvider` seam;
- local-first durable outbox, idempotency and retry classification;
- campaign reconciliation and PC snapshot reconciliation/conflict rules.

Do not duplicate these systems during provider integration.

## Owner-controlled activation sequence

Immediately before activation, verify current provider documentation, available plans, region/data-location options and any pricing or quota implications. Provider offerings can change; do not rely on stale plan assumptions stored in the repository.

The owner should then create or authorize development/test resources in this order:

1. Neon development PostgreSQL project/database;
2. Descope development project/application;
3. Cloudflare development Worker environment/project.

This ordering is operational convenience, not architecture: the Worker remains the API boundary and native clients must not connect directly to Neon.

## Configuration mapping

The Worker currently expects the established configuration/binding family:

- `APP_ENV` — environment classification;
- `DATABASE_URL` — Neon PostgreSQL connection string; treat as secret;
- `AUTH_MODE` — hosted auth mode;
- `DESCOPE_PROJECT_ID` — non-secret project identifier unless provider guidance says otherwise;
- `DESCOPE_JWKS_URL` — verifier/JWKS endpoint configuration;
- local static-auth values only for the explicitly local development path.

At activation time, inspect the current code before applying values because later work may evolve names or deployment configuration.

## Secret-handling rules

Never commit any of the following:

- database passwords/connection credentials;
- access/refresh/session tokens;
- private keys;
- provider API tokens;
- deployment credentials;
- local static bearer tokens used for testing.

Use provider/runtime secret stores or ignored local configuration. Do not paste secrets into durable repository documentation, issues, PR descriptions or test fixtures.

Native Android/Desktop clients must never receive Neon/database credentials.

## First real validation after activation

The activation package is complete only after the development environment proves, in order:

1. Worker health/configuration against the real development environment;
2. SQL migrations applied and contracts compatible with the development Neon database;
3. valid Descope-authenticated request resolves to the application-owned user identity;
4. campaign list/bootstrap works through the deployed Worker;
5. local campaign creation survives offline-first persistence and later hosted delivery;
6. hosted PC snapshot upload/download works through the existing shared sync path;
7. stale revision and authorization failures remain explicit/non-destructive;
8. second-device observation and reconnect/convergence are demonstrated before calling Player↔Server end-to-end complete.

## Android auth/session package after activation

The first owner-facing Android hosted package should add remembered authentication/session acquisition at the platform edge and feed access tokens into the existing shared `HostedAccessTokenProvider` contract.

Keep provider-specific Android/session logic outside common domain logic. Do not rewrite the Player runtime or replace existing shared sync/transport solely because a provider SDK is introduced.

## Security/privacy check before activation

At the time this document was prepared, GitHub repository metadata was observed as `private: false`. The owner should verify repository visibility before provider integration. Do not alter visibility autonomously.

Repository visibility is not a substitute for secret hygiene: credentials stay outside Git even if the repository is private.

## Stop conditions

Return to the owner before proceeding if activation requires a material choice involving:

- paid plan or billing commitment;
- region/data residency with meaningful consequences;
- account ownership/recovery policy;
- security/privacy tradeoff;
- meaningful vendor lock-in;
- production rather than development resources.

Routine project naming, migration commands, code wiring and test mechanics remain delegated technical work once the owner has authorized the external resources.
