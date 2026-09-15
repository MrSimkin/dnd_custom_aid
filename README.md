# dnd_custom_aid

Personal/small-scale tabletop RPG assistant beginning with D&D, with Android Player/DM live use and a native Desktop DM application for rich preparation/administration plus complete operational DM fallback.

## Start here

This repository is designed so a new human collaborator, ChatGPT conversation, coding agent or other AI can resume from Git alone.

Read in this order:

1. `AGENTS.md` — mandatory operating rules;
2. `MANIFEST.md` — map of authoritative/project-memory files and implemented areas;
3. `docs/PROJECT_STATE.md` — authoritative current global state/navigation;
4. `docs/checkpoints/LATEST.md` — exact practical resume pointer;
5. `docs/BRANCH_STATUS.md` — branch lifecycle map;
6. `docs/DECISIONS.md` + `docs/DECISIONS_RECENT.md` + relevant detailed records under `docs/decisions/`;
7. `docs/CONVENTIONS.md`;
8. `docs/PRODUCT.md`;
9. `docs/ROADMAP.md`;
10. `docs/WORKFLOW.md`;
11. `docs/ARCHITECTURE.md`;
12. `docs/TESTING.md`;
13. relevant checkpoints/feature files.

Current implementation checkpoint:

`docs/checkpoints/2026-09-15_PLAYER_SERVER_PROVIDER_BOUNDARY.md`

Provider activation handoff:

`docs/technical/HOSTED_PROVIDER_ACTIVATION_GATE.md`

## Current repository authority

The owner has authorized integrated-MVP implementation. **`main` is the normal integrated-MVP development trunk.** The historical Player successor and convergence branches remain frozen evidence rather than normal resume points.

Current verified implementation checkpoint:

`8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`

Post-merge GitHub Actions run `34985799585` completed **SUCCESS**, including all Player guard scripts, shared/Kotlin tests, Android debug build, Desktop build, backend checks, hosted PostgreSQL contracts and APK artifact upload.

Use short-lived outcome-oriented branches from current remote `main`; do not recreate permanent Player/Server/Desktop silos.

## Working relationship

AI/coding agents perform heavy technical execution.

The owner decides actual product/workflow/UX/game-semantic/privacy/scope decisions, external account/resource activation and meaningful cost/security/convenience/lock-in tradeoffs. Routine low-level engineering is delegated and must not be pushed back to the owner for ceremonial approval.

Escalate a technical choice only when it materially changes product behavior, cost, privacy/security, irreversible lock-in, destructive behavior or approved scope.

Use the simplest safe implementation that satisfies real approved requirements; do not import enterprise machinery without a concrete reason.

## Approved architecture snapshot

- Android: **Kotlin + Jetpack Compose**, minimum Android 11 / API 30.
- Android phone/tablet are first-class live surfaces.
- DM Desktop: **Kotlin + Compose Multiplatform Desktop**.
- Local persistence: **SQLite + SQLDelight** where local/offline behavior matters.
- Desktop: local Save + explicit Sync.
- Hosted relational database: **Neon PostgreSQL**.
- Backend/API: **Cloudflare Worker**, TypeScript.
- Authentication proof: **Descope**; application/domain authorization remains project-owned.
- Native clients do not connect directly to Neon or hold DB credentials.
- Shared native HTTP: **Ktor Client**.
- Project-specific sync: stable IDs, revisions, idempotent mutations, durable outbox, tombstones, scoped pull/push and explicit conflict handling.
- Hosted PC current state: versioned application-owned JSONB snapshot plus relational authorization/revision/lifecycle metadata.
- Object storage is MVP; **Cloudflare R2 Standard is the current technical recommendation**, but R2 is deliberately deferred until asset work.
- Ordinary HTTP/request-response and polling are preferred before generalized realtime infrastructure.
- Full verifiable server backup/export is MVP.
- Official SRD clarification uses PostgreSQL FTS + grounded replaceable LLM and remains official-SRD-only for MVP.
- PC Sheet PDF export is local/offline and cross-surface with one canonical semantic export path.

See `docs/ARCHITECTURE.md`, `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md` and the current checkpoint for detail.

## Current implementation state

Wave 2 — Shared Integrated-MVP Spine — is complete.

Provider-neutral hosted work is integrated through PR #25. The repository now includes:

- hosted `/v1` API/auth/domain behavior;
- explicit hosted PostgreSQL migrations/contracts;
- shared Android/Desktop hosted transport;
- provider-neutral token boundary;
- durable hosted outbox;
- local-first campaign creation + idempotent hosted delivery;
- hosted account/campaign bootstrap;
- explicit campaign membership lifecycle/deletion reconciliation;
- hosted PC current-state snapshot persistence and authorization;
- PC optimistic revision/idempotency/conflict/tombstone semantics;
- durable PC snapshot delivery;
- safe same-identity PC reconciliation distinct from backup restore-as-copy.

## Exact current continuation

The next meaningful package is **real authenticated Player↔Server development integration**.

The provider-neutral foundation is sufficiently complete. Owner-facing Android is still intentionally local-only at composition level; remembered Descope auth/session and real hosted campaign/PC round trips are not yet wired into Player UI.

That means the first external-provider activation gate has now been reached.

Before continuing that package, the owner must authorize/create development resources for:

1. **Cloudflare** — Worker/API runtime;
2. **Neon** — PostgreSQL;
3. **Descope** — authentication/identity.

Then implementation continues with remembered Android auth/session feeding the existing shared token seam, hosted campaign bootstrap/create/select, hosted PC push/pull and real two-device/offline/reconnect/revoke validation.

Do not create additional parallel auth/networking/sync abstractions merely to postpone this gate.

## External-service safety

Provider activation is not implied by general implementation authorization. Accounts/resources remain owner-controlled.

Immediately before activation, verify current provider plans, region/data-location options, pricing/quotas and relevant security/privacy/lock-in implications. Use development/test resources first.

Never commit database credentials, provider API tokens, bearer/session tokens, private keys or deployment credentials. Use provider/runtime secret stores or ignored local configuration.

R2 is **not** part of the first activation gate.

At the current checkpoint, GitHub repository metadata reports `private: false` even though the project has previously been described conversationally as private. The owner should verify intended visibility before provider integration. Do not change repository visibility autonomously.

## Historical Player evidence

The mature Player runtime from `implementation/phase4a-successor-cycle` is integrated into the normal baseline.

Historical frozen candidate remains evidence only:

- version `0.4.0-preqa.13` / build `41300`;
- candidate commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — SUCCESS;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted physical cross-device revalidation was pending at that historical boundary.

New integrated CI success does not retroactively claim physical acceptance of that old candidate.

## Integrated MVP scope

The target remains:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Desktop is both a rich authoring/admin workbench and complete operational DM fallback. Protected MVP scope includes Player hosted integration, DM live surfaces, combat authority resume, Managers, structured homebrew/import-export, object storage/media, PC audit/correction, PC Sheet PDF export, Campaign/System Administration, audit/recovery/full backup and official-SRD clarification.

The project remains paper-first and is intentionally not a generalized VTT, automatic legality engine, CRDT platform, marketplace/social product or enterprise infrastructure exercise.

## Build and verification

Kotlin / Android / Desktop / SQLDelight:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

Permanent Player guard scripts are part of the Scaffold workflow.

Backend:

```bash
cd backend
npm install
npm run check
```

Hosted PostgreSQL contracts are also exercised by Scaffold CI against PostgreSQL. See `docs/TESTING.md` for verification strategy.

## Development signing note

Development CI uses a stable **debug-only** Android signing identity so successive QA APKs can update one another in place and exercise migrations. It is not a production/release identity.
