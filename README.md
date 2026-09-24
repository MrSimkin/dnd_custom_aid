# dnd_custom_aid

Personal/small-scale tabletop RPG assistant beginning with D&D, with Android Player/DM live use, a native Desktop DM application, and hosted/shared services.

## Start here

This repository is the project's durable source of truth and operative memory.

For a fresh human or AI session — especially when the instruction is simply **"see/resume repo dnd_custom_aid"** — use the fast route:

1. read `AGENTS.md` for mandatory rules;
2. read `RESUME.md`;
3. read `docs/checkpoints/LATEST.md`;
4. follow its single **Canonical active checkpoint**;
5. load only the additional authority files required by that active checkpoint/task.

Historical checkpoints remain evidence for their time, not automatic resume instructions. Do not reconstruct current work from old PRs, branch names, or stale prose before following the canonical resume pointer.

## Current project position

`main` is the sole normal integrated-MVP trunk.

The exact current stage, active defect/package, manual boundary and next action are intentionally **not duplicated in README** because duplicated current-state prose becomes stale. Use:

`RESUME.md -> docs/checkpoints/LATEST.md -> Canonical active checkpoint`.

Stable architecture/product information remains below.

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