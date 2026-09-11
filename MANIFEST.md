# Repository Manifest

This file maps the authoritative project-control files and implemented areas so a fresh human or AI can orient without reconstructing history from branch names.

## Canonical rule

`main` is the single canonical repository baseline under D-0066.

The active continuation branch for the current Phase 4A successor/acceptance cycle is `implementation/phase4a-successor-cycle`. It contains the full A–I Player successor implementation, later automated-green stabilization, `preqa.8 / 40800` owner-QA evidence, the active repair-decision discussion log and design-only DM discovery not yet merged to `main`.

Presence on `main` or the continuation branch does not imply release acceptance.

The repository contains many historical branches; use `docs/BRANCH_STATUS.md` rather than treating branch names as competing sources of truth.

## Root control files

### `README.md`
Project entry point, mandatory read sequence, current build identity and broad stage summary.

### `AGENTS.md`
Mandatory operating rules for humans and AI/coding agents.

### `MANIFEST.md`
This inventory.

## Core `docs/` truth

### `docs/PROJECT_STATE.md`
Authoritative current implementation/QA state and exact next sequence.

### `docs/checkpoints/LATEST.md`
Stable practical resume pointer.

### `docs/BRANCH_STATUS.md`
Controlling interpretation of historical implementation/tmp/frozen branches after D-0066 consolidation.

### `docs/DECISIONS.md`
Master chronological decision log. Later detailed approved decisions/checkpoints remain authoritative even before the master log is next reconciled.

### `docs/decisions/D-0066_MAIN_CANONICAL_DEVELOPMENT_CONSOLIDATION.md`
Owner-approved repository consolidation rule separating canonical repository ordering from QA/release acceptance.

### `docs/decisions/D-0068_DM_COMBAT_DESK_PRODUCT_AND_UX.md`
Owner-approved design baseline for the future DM tablet-landscape live Combat Desk. **Design truth only; no DM implementation before Phase 4A closure.**

### `docs/CONVENTIONS.md`
Approved recurring project conventions.

### `docs/PRODUCT.md`
Approved product scope and boundaries, refined by later specific decisions where stated.

### `docs/ROADMAP.md`
Development phases and the current Phase 4A point-by-point reconciliation/closure boundary.

### `docs/WORKFLOW.md`
Approved design/implementation/verification/checkpoint/review/merge workflow.

### `docs/ARCHITECTURE.md`
Current approved architecture and implementation consequences.

### `docs/TESTING.md`
Verification policy, technically verified build identity and owner-QA requirements. Historical evidence may remain in this file; `docs/checkpoints/LATEST.md` and `docs/PROJECT_STATE.md` control the current resume state.

### `docs/PREQA_OWNER_VISUAL_AUDITION.md`
Historical staged owner-audition guide used for build `40700`. **Do not use as the current resume pointer.**

### `docs/TEST_DEVICES.md`
Owner-confirmed physical test devices. Primary recorded phone: Redmi Note 11 Pro 5G. Physical tablet acceptance remains pending.

## Current detailed checkpoints

### `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_OWNER_PHONE_QA_CONSOLIDATED.md`
Controlling real-device owner QA record for `0.4.0-preqa.8 / 40800`. Records PASS evidence, acceptance blockers, shared phone/tablet repair scope and tablet deferral.

### `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md`
Live point-by-point owner design-reconciliation log. Every QA point is discussed individually; each explicitly closed decision is consolidated here before the next point begins. No repair implementation is authorized merely by opening this log.

### `docs/checkpoints/2026-09-09_PHASE4A_PLAYER_PREQA8_STABILIZATION.md`
Exact technically verified `0.4.0-preqa.8 / 40800` product/audit/full-gate checkpoint.

### `docs/checkpoints/2026-09-08_PHASE4A_RECONCILED_SUCCESSOR_IMPLEMENTATION_PLAN.md`
Historical controlling A–I successor implementation plan. A–I engineering is complete; no planned Increment J exists.

### September 7–8 preqa.7 audition checkpoints
Historical `40700` Stage D/E/F and source/provenance audit evidence. They explain why successor work was undertaken but are not current QA targets.

### `docs/checkpoints/2026-09-08_PHASE4_M6_OWNER_QA_PROGRESS.md`
Historical/superseded preservation of the one unique M6-detour upgrade-test record. Not a current resume point.

## Implemented application areas

### `shared/`
Kotlin Multiplatform shared domain/persistence module using SQLDelight.

Current character work includes campaign-scoped characters, multiclass data, derived values/adjustments, Combat, Equipment/currencies, Background, Traits, Spells/sources/prepared state/shared slots, Notes, proficiencies, class/subclass provenance, Inspiration/death saves, Weapon Mastery, Resources, Forms, Companions, conditional modules and backup/import.

Do not split this into speculative architecture-layer Gradle modules without a concrete need.

### `androidApp/`
Native Kotlin + Jetpack Compose Android app, `minSdk 30`.

The Phase 4A character editor includes General, Habilidades, Combate, Gestión, Equipo/Monedas, Trasfondo, Rasgos, conditional Conjuros, Notas, PC Settings, Application Settings, Supercompact/Table mode and all approved conditional module families.

A–I successor engineering plus post-A–I Player stabilization is automated-green. Build `40800` has received consolidated physical owner phone QA and is **not accepted**; point-by-point repair design reconciliation is current.

The future DM Combat Desk has a documented product/UX baseline under D-0068 but **no DM implementation has begun**.

### `desktopApp/`
Compose Multiplatform Desktop DM preparation/administration shell. Full Android parity is not required.

### `backend/`
TypeScript Cloudflare Worker/API area with current minimal checks. Hosted integrations remain incremental.

### `database/`
Hosted PostgreSQL schema/migration/data-loading area. Local Android character persistence lives under `shared/` SQLDelight/SQLite.

### `.github/workflows/scaffold-check.yml`
Current CI gate: JDK 17, Android SDK 36, Gradle 9.5, shared desktop tests, Android debug assembly, Desktop build, backend Node/TypeScript check and debug APK artifact upload.

The workflow reconstructs a stable development-only Android debug signing identity for update-in-place QA. It is not a release signing identity.

## Latest technically verified Player product identity

- version `0.4.0-preqa.8` / build `40800` / `debug`;
- product source commit `c78b06776f5ae7a253b5b12b791c71fa2a7da096`;
- product tree `c612c07345ecdfc91d972118314ee649fe2048c4`;
- authoritative validation/checkpoint head `2a9b682f6aca2e95facecf1f6256039fd96cfefd`;
- workflow `34430548061` — SUCCESS;
- artifact `10134364621` / `dnd-custom-aid-debug-apk`;
- artifact ZIP SHA-256 `b7ead12a7501bbef96f861321b5bebfd64c631647423b8eab9faec9580699a`;
- APK SHA-256 `bb02b413919f55551eb7d4e78dfab2c37145b852c8827126df80082bd7a40815`.

The owner installed this build over the prior QA installation/data and completed the consolidated phone QA pass. Upgrade/data preservation passed. The overall build did **not** pass Phase 4A acceptance and requires the bounded repair pass now being designed point by point.

Later documentation-only commits do not change the validated product identity unless a newer checkpoint explicitly says otherwise.

## Historical/discovery material

### `docs/discovery/`
Historical exploratory reasoning. It does not override approved decisions/current state.

### `docs/checkpoints/`
Durable implementation, QA and handoff history. Historical next-action text may be superseded; `LATEST.md` controls the current resume point.

### historical branches
Branch refs remain evidence/history but are non-current after D-0066. Frozen candidates are immutable. See `docs/BRANCH_STATUS.md`.

## Authority rule

If documents appear to conflict:

1. `AGENTS.md` controls governance/working rules;
2. later specific approved decisions/clarifications control over older general prose;
3. `docs/CONVENTIONS.md` controls recurring approved practice;
4. `docs/PRODUCT.md` controls approved product direction, with later specific decisions refining it where stated;
5. `docs/PROJECT_STATE.md` controls current implementation/next action;
6. `docs/checkpoints/LATEST.md` is the practical resume pointer;
7. the September 11 `40800` QA checkpoint controls current owner findings;
8. the September 11 repair-decision log controls newly closed point-by-point repair decisions;
9. `docs/BRANCH_STATUS.md` controls interpretation of branch refs;
10. discovery/history is contextual only;
11. surface material contradictions instead of guessing.
