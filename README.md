# dnd_custom_aid

Personal/small-scale tabletop RPG assistant beginning with D&D, with Android Player/DM live use and a native Desktop DM application for rich preparation/administration plus complete operational DM fallback.

## Start here

This repository is designed so a new human collaborator, ChatGPT conversation, coding agent or other AI can resume from Git alone.

Read in this order:

1. `AGENTS.md` — mandatory operating rules;
2. `MANIFEST.md` — map of authoritative/project-memory files and implemented areas;
3. `docs/PROJECT_STATE.md` — authoritative current global state/navigation;
4. `docs/checkpoints/LATEST.md` — exact practical resume pointer;
5. `docs/BRANCH_STATUS.md` — current branch lifecycle map;
6. `docs/DECISIONS.md` plus `docs/DECISIONS_RECENT.md` and relevant detailed records under `docs/decisions/`;
7. `docs/CONVENTIONS.md`;
8. `docs/PRODUCT.md`;
9. `docs/ROADMAP.md`;
10. `docs/WORKFLOW.md`;
11. `docs/ARCHITECTURE.md`;
12. `docs/TESTING.md`;
13. relevant current checkpoints/feature files.

Current controlling records:

- `docs/decisions/D-0071_MVP_INTEGRATION_PLAYER_SERVER_DM_DESKTOP_ARCHITECTURE.md`;
- `docs/decisions/D-0072_DM_DESKTOP_PRODUCT_AND_AUTHORING_MANAGERS.md`;
- `docs/decisions/D-0073_INTEGRATED_MVP_BOUNDARY_AND_IMPLEMENTATION_GOVERNANCE.md`;
- `docs/checkpoints/2026-09-14_INTEGRATED_MVP_TECHNICAL_READINESS_REVIEW.md`.

## Current repository authority

Until the planned convergence is executed there are **two active authoritative lines**:

- `main` — canonical global integrated-MVP product/design/architecture/governance truth;
- `implementation/phase4a-successor-cycle` — authoritative current Player runtime/repair/QA line.

These branches contain different valid work. `main` is not yet the latest Player runtime, and the Player successor must not overwrite later integrated-MVP decisions on `main`.

D-0073 defines the intended transition: after explicit implementation authorization, reconcile both lines on a dedicated convergence branch, validate, then merge the coherent baseline to `main`. After that successful convergence, `main` becomes the normal integrated-MVP trunk and the old Player successor becomes historical evidence.

Use `docs/BRANCH_STATUS.md`, `docs/PROJECT_STATE.md` and the relevant `docs/checkpoints/LATEST.md` rather than reconstructing authority from branch names or chronology.

## Working relationship

AI/coding agents perform the heavy technical execution.

The owner decides actual product/workflow/UX/game-semantic/privacy/scope decisions and meaningful cost/security/convenience tradeoffs. Routine low-level engineering is delegated under D-0073 and must **not** be pushed back to the owner for ceremonial rubber-stamping.

Escalate a technical choice only when it materially changes product behavior, cost, privacy/security, irreversible lock-in or approved scope.

C-0009 remains controlling: use the simplest safe implementation that satisfies real approved requirements and do not import enterprise machinery without a concrete reason.

## Approved architecture snapshot

- Android: **Kotlin + Jetpack Compose**, minimum Android 11 / API 30.
- Android phone/tablet are first-class live surfaces.
- DM Desktop App: **Kotlin + Compose Multiplatform Desktop**.
- Local persistence: **SQLite + SQLDelight** where local/offline behavior matters.
- Desktop: local Save + explicit Sync.
- Hosted relational database: **Neon PostgreSQL**.
- Backend/API: **Cloudflare Worker**, TypeScript.
- Authentication proof: **Descope**; application/domain authorization remains project-owned.
- Native clients never connect directly to Neon or hold database credentials.
- Object storage is required; **Cloudflare R2 Standard is the current technical recommendation**, but enabling its subscription/account relationship is still an owner/service action before use.
- Ordinary HTTP/request-response and polling remain preferred before generalized realtime infrastructure.
- Full verifiable server backup/export is part of the MVP.
- Official SRD clarification remains PostgreSQL full-text retrieval + grounded replaceable LLM, implemented late in the MVP cycle after foundations are real.

See `docs/ARCHITECTURE.md`, D-0071 and the technical-readiness checkpoint for full detail.

## Current Player reality

Authoritative Player runtime/evidence before convergence:

`implementation/phase4a-successor-cycle`

Observed branch HEAD during the 2026-09-14 technical-readiness review:

`b9dea8ad6b17dcf3feeabba263eff1ee498f1536`

Current frozen physical candidate:

- version `0.4.0-preqa.13`;
- versionCode/build `41300`;
- commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — **SUCCESS**;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted cross-device physical revalidation pending;
- Phase 4A formally open on the historical Player line.

The Scaffold run was independently rechecked during technical readiness and is completed/successful on the exact candidate SHA.

Automation is not physical owner acceptance. Preserve the evidence; do not restart historical Player repair work without new evidence.

## Integrated MVP scope

The next implementation cycle targets one coherent product:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Approved live DM Desk family:

1. DM Screen;
2. Stage Desk;
3. Dungeon Desk;
4. Combat Desk.

The Desktop App is both:

1. a full operational DM client/fallback with the same game/domain semantics as DM Android;
2. the richer authoring/management/administration workbench.

Desktop Prepare/Manage includes Monsters, NPCs, Homebrew & Rules, Stage/Places, Dungeon/Zones, Encounters, PCs/Audit and Media/Handouts. Administration includes Campaign Manager plus the sole-admin System Administration/operator console.

See D-0072 for the full product definition.

## Technical readiness

The 2026-09-14 technical review found:

- backend = health-only scaffold;
- hosted PostgreSQL migrations = scaffold only;
- Desktop = basic placeholder shell;
- the substantial existing technical asset is the mature Player/shared Kotlin + SQLDelight implementation;
- Player and `main` are divergent but technically reconcilable under D-0073's semantic precedence;
- mandatory governance/navigation files had stale pre-D-0073 wording and are being reconciled by the technical-readiness documentation pass;
- the existing versioned Player backup/serialization model is a strong basis for hosted PC snapshots rather than duplicating the entire local SQLDelight graph in PostgreSQL;
- Ktor Client is the preferred shared Android/Desktop HTTP layer;
- Neon serverless driver is the preferred initial Worker->PostgreSQL access path;
- R2 Standard is the preferred first object-storage provider, pending owner/service activation;
- no unresolved low-level engineering choice currently needs owner approval.

## Current authorization boundary and exact next action

Product design, MVP boundary and technical readiness are complete enough to start implementation **once explicitly authorized**.

No product-code implementation was authorized merely by the technical review.

The next meaningful owner decision is:

> authorize beginning the integrated-MVP implementation, starting with the protected `main` + Player-successor convergence.

After that authorization, technical agents should carry the engineering details and return to the owner only for material product/scope/security/cost choices or external account/service actions.

## Build and verification commands

Kotlin / Android / Desktop / SQLDelight:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

Before convergence, also preserve/run the permanent Player guard scripts from the successor workflow.

Backend:

```bash
cd backend
npm install
npm run check
```

See `docs/TESTING.md` for the verification strategy.

## Development signing note

Development CI uses a stable **debug-only** Android signing identity so successive QA APKs can update one another in place and exercise real migrations. It is not a production/release identity and must never be reused for a real release.