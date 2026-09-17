# dnd_custom_aid

Personal/small-scale tabletop RPG assistant beginning with D&D, with Android Player/DM live use, a native Desktop DM application, and hosted/shared services.

## Start here

This repository is the project's durable source of truth and operative memory. A fresh human or AI worker should read, in order:

1. `AGENTS.md`;
2. `MANIFEST.md`;
3. `docs/PROJECT_STATE.md`;
4. `docs/checkpoints/LATEST.md` and the checkpoint it references;
5. `docs/BRANCH_STATUS.md`;
6. `docs/DECISIONS.md`, `docs/DECISIONS_RECENT.md` and relevant detailed decisions;
7. `docs/CONVENTIONS.md`, `docs/PRODUCT.md`, `docs/ROADMAP.md`, `docs/WORKFLOW.md`, `docs/ARCHITECTURE.md`, `docs/TESTING.md`.

Historical checkpoints remain evidence for their time, not automatic resume instructions.

## Current project position

`main` is the sole normal integrated-MVP trunk.

Wave 5 — Desktop shell + hosted Campaign Administration — is **COMPLETE / OWNER-QA ACCEPTED / INTEGRATED**. PR #44 (`feat: add Desktop hosted campaign administration`) merged as `306377df1a453f531af4b670d2b231c88a3c9419`; post-merge Scaffold `35168920031` passed backend, hosted-database and Kotlin/build/test/APK jobs.

Do not restart Wave 5 or redeploy its already verified Cloudflare Worker merely because documentation changed.

The next normal implementation direction is **Wave 6 — reusable/persistent content architecture**. Build the reusable-content semantics/persistence foundation before large Wave 7 Desktop Managers.

## Architecture snapshot

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Current stack/direction:

- Android: Kotlin + Jetpack Compose, minSdk 30;
- Desktop: Kotlin + Compose Multiplatform;
- local persistence: SQLite + SQLDelight where local-first/offline behavior matters;
- shared native HTTP: Ktor Client;
- backend/API: TypeScript Cloudflare Worker;
- hosted relational data: Neon PostgreSQL;
- identity proof: Descope; application-owned authorization;
- stable IDs, optimistic revisions, idempotent mutation IDs, durable outbox, tombstones and explicit conflicts;
- native clients never connect directly to Neon.

## External-provider boundary

Cloudflare, Descope, Neon and future providers are authenticated capability boundaries. If the current worker lacks authenticated capability for a required provider action, establish that once, finish all safe repo/CI work, provide one exact owner-action packet, and stop at that boundary. Do not probe alternate plugins/browser/token paths repeatedly.

Never ask for or commit passwords, OTPs, API/deployment tokens, DB credentials, connection strings, session/access/refresh JWTs, private keys or provider credential files.

Repository CI and real-provider evidence are different. Green CI does not prove deployment or real authentication; once provider behavior has been tested and durably recorded, do not repeat it just because docs changed.

## Cost/security

The external-service operating budget is **USD $0** unless the owner explicitly changes it. Do not enable paid plans, overage, paid add-ons or billing commitments without explicit owner approval. The repository is intentionally public; secrets never belong in Git.

See `AGENTS.md`, `docs/WORKFLOW.md`, and `docs/recovery/EXTERNAL_PROVIDER_HANDOFF_PROMPT.md` for the controlling workflow.