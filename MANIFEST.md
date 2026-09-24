# Repository Manifest

This file maps current project-control authority so a fresh human or AI can resume without branch archaeology.

## Current authority

`main` is the sole normal integrated-MVP trunk. Current practical truth is controlled by:

- `RESUME.md` — stable fresh-context entry route;
- `docs/checkpoints/LATEST.md` — single mutable practical resume pointer;
- the **Canonical active checkpoint** named by `LATEST.md`;
- `docs/PROJECT_STATE.md` — broader current state;
- `docs/BRANCH_STATUS.md` — branch lifecycle;
- approved detailed decisions under `docs/decisions/`.

The exact active wave/package/defect is deliberately not duplicated here. Follow the canonical resume route rather than assuming a continuation from historical wave text.

## Start/resume files

- `README.md` — human-facing entry point;
- `AGENTS.md` — mandatory operating/provider/security rules;
- `RESUME.md` — stable fast-resume route;
- `docs/checkpoints/LATEST.md` — single mutable practical continuation pointer;
- canonical checkpoint named by `LATEST.md` — exact active task evidence/next action;
- `docs/PROJECT_STATE.md` — broader current implementation state;
- `docs/BRANCH_STATUS.md` — branch lifecycle;
- `docs/DECISIONS_RECENT.md` — recent decision navigation;
- `docs/ROADMAP.md` — dependency-driven waves;
- `docs/WORKFLOW.md` — development and provider-handoff workflow;
- `docs/ARCHITECTURE.md` — integrated architecture;
- `docs/TESTING.md` — verification/evidence model;
- `docs/recovery/PROJECT_RECOVERY_PROMPT.md` — fresh-chat recovery;
- `docs/recovery/EXTERNAL_PROVIDER_HANDOFF_PROMPT.md` — provider-boundary recovery.

## Controlling recent decisions

- D-0071 — integrated Player + hosted/shared + DM architecture;
- D-0072 — Desktop product and authoring/management semantics;
- D-0073 — integrated MVP boundary, waves and delegation;
- D-0074 — PC Sheet PDF export;
- D-0075 — hard `$0` provider policy, public repo and owner guidance.

D-0072/D-0073 approve the Wave 6 Personal -> Campaign independent-copy/provenance model; routine schema/class/test details remain delegated engineering.

## Implemented areas

- `shared/` — Kotlin Multiplatform domain/persistence/networking/sync foundations, including integrated spine scope/provenance/revision primitives and the Wave 6 local reusable-content catalog/persistence foundation;
- `androidApp/` — Player runtime with real hosted auth/campaign/PC synchronization for the recorded Wave 4 scope;
- `desktopApp/` — persistent local workbench plus real hosted authentication/Campaign Administration integrated in Wave 5, with safe local DB migration support for the Wave 6 foundation;
- `backend/` — TypeScript Worker/API with identity verification and application-owned authorization;
- `database/` — hosted PostgreSQL migrations/contracts;
- `scripts/` — permanent guard scripts;
- `assets/character-sheets/templates/` — PC Sheet PDF visual authorities.

## Current route

The manifest intentionally does not carry a duplicated live wave/branch pointer.

Use:

`RESUME.md -> docs/checkpoints/LATEST.md -> Canonical active checkpoint`.

Completed historical waves/packages remain discoverable through project state, branch status and checkpoints without being mistaken for the current continuation.

## Provider/cost/security memory

Cloudflare/Descope/Neon are authenticated capability boundaries. An agent without capability stops probing alternatives, finishes safe repo work and gives one exact owner-action packet. Repository CI and real provider evidence are different. Completed provider actions are not repeated merely because docs changed.

Normal hosted DEV owner/DM identity is Outlook-backed; Gmail is historical/inactive by default. Existing DEV Worker is `dnd-custom-aid-api`; do not recreate/redeploy it without material Worker changes or new evidence.

External-service budget is USD $0. The repository is intentionally public. Never commit/request secrets. Object-storage provider selection remains deferred until assets require it.