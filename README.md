# dnd_custom_aid

Personal/small-scale tabletop RPG assistant beginning with D&D, with Android Player/DM live use and a native Desktop DM application for both rich preparation/administration and operational fallback.

## Start here

This repository is designed so a new human collaborator, ChatGPT conversation, coding agent or other AI can resume from Git alone.

Read in this order:

1. `AGENTS.md` — mandatory operating rules;
2. `MANIFEST.md` — map of authoritative/project-memory files and implemented areas;
3. `docs/PROJECT_STATE.md` — authoritative current global state/navigation;
4. `docs/checkpoints/LATEST.md` — exact practical resume pointer;
5. `docs/BRANCH_STATUS.md` — current branch lifecycle map;
6. `docs/DECISIONS.md` **plus relevant later detailed records under `docs/decisions/`**;
7. `docs/CONVENTIONS.md`;
8. `docs/PRODUCT.md`;
9. `docs/ROADMAP.md`;
10. `docs/WORKFLOW.md`;
11. `docs/ARCHITECTURE.md`;
12. `docs/TESTING.md`;
13. relevant current checkpoints/feature files.

The most important current detailed decision is:

`docs/decisions/D-0071_MVP_INTEGRATION_PLAYER_SERVER_DM_DESKTOP_ARCHITECTURE.md`

Current pause/resume checkpoint:

`docs/checkpoints/2026-09-14_MVP_INTEGRATION_ARCHITECTURE_CONSOLIDATION.md`

## Current repository authority

There are currently **two active authoritative lines**:

- `main` — canonical global navigation plus current DM/integrated-MVP product/design/architecture truth;
- `implementation/phase4a-successor-cycle` — authoritative current Player runtime/repair/QA line.

These branches intentionally contain different valid work. `main` is **not** the latest Player runtime, and the Player successor branch must not overwrite later DM/MVP design records on `main`.

Use `docs/BRANCH_STATUS.md`, `docs/PROJECT_STATE.md` and the active branch's `docs/checkpoints/LATEST.md` rather than reconstructing authority from branch names or commit chronology.

## Working relationship

AI/coding agents perform the heavy technical execution. The owner remains the decision owner for consequential product/UX/game-semantic/data/privacy/service/compatibility choices.

Meaningful work must be explained and persisted in Git. C-0009 remains controlling: use the simplest safe implementation that satisfies real approved requirements and do not import enterprise machinery without a concrete reason.

## Approved architecture snapshot

- Android: **Kotlin + Jetpack Compose**, minimum Android 11 / API 30.
- Android phone/tablet are first-class live surfaces.
- DM Desktop App: **Kotlin + Compose Multiplatform Desktop**.
- Local persistence: **SQLite + SQLDelight** where local/offline behavior provides real value.
- Desktop synchronization: **Save locally + explicit Sync**.
- Hosted relational database: **Neon PostgreSQL**.
- Backend/API: **Cloudflare Worker**, TypeScript.
- Authentication: **Descope**; application/domain authorization remains project-owned.
- Native clients never connect directly to Neon or hold DB credentials.
- Object storage is required for this MVP, but the provider is **not yet selected**.
- Ordinary HTTP/request-response and refresh/polling remain preferred before generalized realtime infrastructure.
- Full server backup/export is part of the MVP hosted foundation.
- SRD clarification remains PostgreSQL full-text retrieval + grounded replaceable LLM, implemented late in this MVP cycle after foundations are in place.

See `docs/ARCHITECTURE.md` and D-0071 for the full record.

## Current Player reality

Current Player runtime authority:

`implementation/phase4a-successor-cycle`

According to that branch's 2026-09-14 `LATEST.md`, the current frozen physical candidate is:

- version `0.4.0-preqa.13`;
- versionCode/build `41300`;
- commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — **SUCCESS**;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted cross-device physical revalidation pending;
- Phase 4A still formally open on that branch.

Automation is not physical owner acceptance.

Do not use the older `preqa.9` global summaries as the current Player source. Switch to the successor branch and read its own `docs/checkpoints/LATEST.md` for exact evidence.

The 2026-09-14 architecture discussion also preserved four bounded planning labels (A10, B1, J1, J2). Before implementation, reconcile those discussion labels against the then-current authoritative successor evidence rather than silently replacing exact branch facts with chat memory.

## Current integrated-MVP / DM direction

D-0071 changes the next-cycle philosophy:

> The next implementation cycle is intended to reach the real integrated MVP across Player + Server/shared services + DM, rather than stop at an isolated DM prototype or tiny server milestone.

This **does not mean coding is authorized yet**. The owner deliberately paused after architecture consolidation so the DM Desktop product and exact outer MVP boundary can be finished first.

Current live DM Desk family remains:

1. DM Screen;
2. Stage Desk;
3. Dungeon Desk;
4. Combat Desk.

The DM Desktop App now has two MVP roles:

1. rich Creator/Manager/Campaign/System-Administration surface;
2. complete operational DM fallback/client if the tablet is unavailable.

Therefore all approved DM Desks must be functionally available on Desktop, with Desktop-specific keyboard/mouse/large-screen UX rather than copied tablet layouts.

Desktop authoring/management includes at minimum:

- Monster Creator/Manager;
- NPC Creator/Manager;
- Homebrew Rules Input/Manager;
- Zone Creator/Manager;
- Encounter Creator/Manager;
- PC Manager/Audit;
- campaign/member/permission administration;
- permission-gated System Administration including full server backup/export.

Because Desktop can replace the tablet operationally, explicit combat-authority resume/handoff from the latest synchronized state is now part of the MVP direction. Only one DM device remains authoritative at a time; simultaneous authoritative editing is not required.

## Current implementation-cycle direction

The future integrated build is organized through coordinated workstreams for:

- shared MVP/domain/API spine;
- Player stabilization/integration;
- hosted foundation;
- shared Kotlin sync/data;
- DM Desktop product;
- cross-client live-play exchange;
- SRD retrieval/AI.

Internal slices/waves/tests are engineering controls, not separate product acceptance milestones.

The next major owner-facing QA is intended to test the complete integrated Player + Server + DM MVP while preserving all valid historical Player evidence.

## Current authorization boundary and exact resume

The 2026-09-14 consolidation is **documentation/design authorization only**. It does not itself authorize implementation code.

Resume on `main` from:

1. **7D — detailed DM Desktop App product definition:** overall navigation/structure and area-by-area live Desk + Creator/Manager/Campaign/Admin behavior;
2. **7E — exact outside-MVP boundary**;
3. final Git/development topology and implementation gates;
4. explicit coding authorization.

Do not restart generic DM Desk taxonomy; D-0068/D-0069 already define the current family.

## Build and verification commands

Kotlin / Android / Desktop / SQLDelight:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

Backend:

```bash
cd backend
npm install --no-package-lock
npm run check
```

See `docs/TESTING.md` for current verification strategy and integrated-MVP QA direction.

## Development signing note

Development CI uses a stable **debug-only** Android signing identity so successive QA APKs can update one another in place and exercise real migrations. It is not a production/release identity and must never be reused for a real release.
