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

`docs/checkpoints/2026-09-14_INTEGRATED_MVP_BASELINE_CONVERGENCE.md`

## Current repository authority

The owner explicitly authorized beginning the integrated-MVP implementation on 2026-09-14 (Chile local time).

The former split between `main` product/governance and `implementation/phase4a-successor-cycle` Player runtime has been semantically reconciled on `integration/mvp-baseline-convergence`.

Validated convergence commit:

`5bed85cbb3e86ae63eac79149fadc5e56e61b256`

GitHub Actions run `34917259324` / #1694 completed **SUCCESS**, including all Player guard scripts, shared tests, Android debug build, Desktop build, backend type-check and APK artifact upload.

After promotion of this validated branch, **`main` is the normal integrated-MVP development trunk**. The old Player successor remains historical/frozen QA evidence and is not the normal resume point.

Use short-lived outcome-oriented branches from current `main`; do not recreate permanent Player/Server/Desktop silos.

## Working relationship

AI/coding agents perform the heavy technical execution.

The owner decides actual product/workflow/UX/game-semantic/privacy/scope decisions and meaningful cost/security/convenience/lock-in tradeoffs. Routine low-level engineering is delegated and must not be pushed back to the owner for ceremonial approval.

Escalate a technical choice only when it materially changes product behavior, cost, privacy/security, irreversible lock-in, destructive behavior or approved scope.

C-0009 remains controlling: use the simplest safe implementation that satisfies real approved requirements and do not import enterprise machinery without a concrete reason.

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
- Project-specific sync: stable IDs, revisions, idempotent mutations, outbox, tombstones, scoped pull/push, explicit conflict handling.
- Object storage is MVP; **Cloudflare R2 Standard is the current technical recommendation**, but it has not been activated.
- Ordinary HTTP/request-response and polling are preferred before generalized realtime infrastructure.
- Full verifiable server backup/export is MVP.
- Official SRD clarification uses PostgreSQL FTS + grounded replaceable LLM and remains official-SRD-only for MVP.
- PC Sheet PDF export is local/offline and cross-surface with one canonical semantic export path.

See `docs/ARCHITECTURE.md`, `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md` and the current checkpoint for detail.

## Historical Player evidence

The mature Player runtime from `implementation/phase4a-successor-cycle` has been integrated into the new baseline.

Historical frozen candidate remains evidence only:

- version `0.4.0-preqa.13` / build `41300`;
- candidate commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — SUCCESS;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted physical cross-device revalidation was pending at that historical boundary.

New integrated CI success does not retroactively claim physical acceptance of that old candidate.

## Integrated MVP scope

The current build targets:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Approved live DM Desks:

1. DM Screen;
2. Stage Desk;
3. Dungeon Desk;
4. Combat Desk.

Desktop is both a rich authoring/admin workbench and complete operational DM fallback. Prepare/Manage includes Monsters, NPCs, Homebrew & Rules, Stage/Places, Dungeon/Zones, Encounters, PCs/Audit and Media/Handouts. Administration includes Campaign Manager and System Administration.

PC Sheet PDF export is protected in MVP across Player Android, authorized DM Android/tablet and DM Desktop, including Classic/custom visual families, custom-stat completeness, matching Extended pages, portrait handling, Permanent vs Current Snapshot and optional Spellbook.

## Current implementation step

Baseline convergence is validated and being promoted to `main`.

The next package is the **Shared Integrated-MVP Spine**:

- account/identity;
- campaign + membership + role;
- PC owner/controller;
- stable IDs;
- revisions/stale-write rejection;
- tombstones/non-resurrection;
- scope/provenance semantics;
- basic audit/sync metadata and invariant tests.

Then proceed to the hosted foundation and Player↔Server integration in dependency order.

No additional owner approval is needed for routine implementation details.

## Build and verification

Kotlin / Android / Desktop / SQLDelight:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

Permanent Player guard scripts are also part of the Scaffold workflow.

Backend:

```bash
cd backend
npm install
npm run check
```

See `docs/TESTING.md` for the verification strategy.

## External-service note

No provider activation occurred during convergence. R2 and provider secrets should be requested/configured only when implementation reaches those dependencies. Secrets must remain outside Git.

## Development signing note

Development CI uses a stable **debug-only** Android signing identity so successive QA APKs can update one another in place and exercise migrations. It is not a production/release identity.
