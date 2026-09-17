# dnd_custom_aid

Personal/small-scale tabletop RPG assistant beginning with D&D, with Android Player/DM live use, a native Desktop DM application, and hosted/shared services.

## Start or resume here

Do **not** reconstruct the project by reading the repository from top to bottom.

Use the fast path:

1. `START_HERE.md`;
2. `CURRENT_TASK.md`;
3. verify the remote Git state named there;
4. `AGENTS.md`;
5. only the deeper authority files directly required by the current task.

`START_HERE.md` defines the complete restart order and interruption procedure.

`CURRENT_TASK.md` is deliberately volatile: it records only the task currently in execution, including the exact branch/PR/CI resume point. Durable integrated truth remains in `docs/PROJECT_STATE.md` and checkpoints.

Project chat stays in **English** unless the owner explicitly requests another language for a specific interaction.

## Current integrated project position

`main` is the sole normal integrated-MVP trunk.

Wave 5 — Desktop shell + hosted Campaign Administration — is **COMPLETE / OWNER-QA ACCEPTED / INTEGRATED**. PR #44 merged as `306377df1a453f531af4b670d2b231c88a3c9419`; post-merge Scaffold `35168920031` passed.

Wave 6 reusable/persistent content architecture is active. Its first reusable-content persistence foundation is integrated through PR #46, merge `013abbb9e57af0ba04fe1e8b678e8ed29522bedd`; post-merge Scaffold `35220099721` passed.

The live unintegrated Wave 6 execution point belongs in `CURRENT_TASK.md`, not in this README.

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

Cloudflare, Descope, Neon and future providers are authenticated capability boundaries. If the current worker lacks required authenticated capability, establish that once, finish safe repo/CI work, provide one exact owner-action packet and stop at that boundary. Do not repeatedly probe alternate access paths.

Never ask for or commit passwords, OTPs, API/deployment tokens, DB credentials, connection strings, JWTs, private keys or provider credential files.

Repository CI and real-provider evidence are different. Green CI does not prove deployment/provider behavior.

## Cost/security

The external-service operating budget is **USD $0** unless the owner explicitly changes it. The repository is intentionally public; secrets never belong in Git.

See `START_HERE.md`, `AGENTS.md`, `MANIFEST.md`, and `docs/recovery/INTERRUPTION_RECOVERY.md` for the controlling operating workflow.
