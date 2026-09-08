# Repository Manifest

This file maps the authoritative project-control files and implemented areas so a fresh human or AI can orient without reconstructing history from branch names.

## Canonical rule

`main` is the single canonical current development baseline under D-0048.

The current baseline is still debug/pre-QA with known defects. Presence on `main` does not imply release acceptance.

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
Authoritative current implementation/QA state, known defect families and exact next sequence.

### `docs/checkpoints/LATEST.md`
Stable practical resume pointer.

### `docs/BRANCH_STATUS.md`
Controlling interpretation of historical implementation/tmp/frozen branches after D-0048 consolidation.

### `docs/DECISIONS.md`
Master chronological decision log, reconciled through D-0047. Later detailed approved decisions under `docs/decisions/` remain authoritative even before the master log is next reconciled.

### `docs/decisions/D-0048_MAIN_CANONICAL_DEVELOPMENT_CONSOLIDATION.md`
Owner-approved decision to consolidate the current in-progress Phase 4 development state into `main` without implying QA/release acceptance.

### `docs/CONVENTIONS.md`
Approved recurring project conventions, including product-Spanish/technical-English, representative SQL when useful, personal-scale proportionality and intended-device QA.

### `docs/PRODUCT.md`
Approved product scope and boundaries.

### `docs/ROADMAP.md`
Development phases and Phase 4A closure boundary.

### `docs/WORKFLOW.md`
Approved design/implementation/verification/checkpoint/review/merge workflow.

### `docs/ARCHITECTURE.md`
Current approved architecture and implementation consequences.

### `docs/TESTING.md`
Verification policy, latest technically verified build identity and future formal owner-QA requirements.

### `docs/PREQA_OWNER_VISUAL_AUDITION.md`
Historical/staged owner visual-audition guide used for build `40700`. Do not use it as the current resume pointer; current results are in the Stage A–F checkpoints and `LATEST.md`.

### `docs/TEST_DEVICES.md`
Owner-confirmed physical test devices. Primary recorded phone: Redmi Note 11 Pro 5G. No owner tablet is currently recorded.

## Current detailed checkpoints

### `docs/checkpoints/2026-09-07_PHASE4_PREQA_UX_REPAIR_PASS_07.md`
Last technically green pre-QA product-repair checkpoint for build `40700`.

### `docs/checkpoints/2026-09-07_PHASE4_PREQA_OWNER_AUDITION_PHONE_STAGE_D.md`
Phone responsive/rotation/card-density findings, including the wide/tablet-mode problem.

### `docs/checkpoints/2026-09-07_PHASE4_PREQA_OWNER_AUDITION_PHONE_STAGE_E.md`
Shared editor/IME/orientation findings.

### `docs/checkpoints/2026-09-07_PHASE4_PREQA_OWNER_AUDITION_PHONE_STAGE_F.md`
Fixed/sticky footprint findings plus the promoted app-wide density/card/landscape directions.

### `docs/checkpoints/2026-09-08_PHASE4_PREQA_FUENTE_REDUNDANCY_AUDIT.md`
Cross-cutting source/provenance information-architecture audit and owner-preferred compact origin model.

### `docs/checkpoints/2026-09-08_PHASE4_M6_OWNER_QA_PROGRESS.md`
Historical/superseded preservation of the one unique M6-detour upgrade-test record. Not a current resume point.

## Implemented application areas

### `shared/`
Kotlin Multiplatform shared domain/persistence module using SQLDelight.

Current character work includes campaign-scoped characters, multiclass data, derived values/adjustments, Combat, Equipment/currencies, Background, Traits, Spells/sources/prepared state/shared slots, Notes, proficiencies, class/subclass provenance, Inspiration/death saves, Weapon Mastery, Resources, Forms, Companions, conditional modules and backup/import.

Do not split this into speculative architecture-layer Gradle modules without a concrete need.

### `androidApp/`
Native Kotlin + Jetpack Compose Android app, `minSdk 30`.

The Phase 4 character editor includes General, Habilidades, Combate, Gestión, Equipo/Monedas, Trasfondo, Rasgos, conditional Conjuros, Notas, PC Settings, Application Settings, Supercompact/Table mode and all six approved conditional module families.

Current UX is **not accepted**. Owner findings require a substantial repair cycle across density, editors/IME, cards, landscape/wide behavior and information architecture.

### `desktopApp/`
Compose Multiplatform Desktop DM preparation/administration shell. Full Android parity is not required.

### `backend/`
TypeScript Cloudflare Worker/API area with current minimal checks. Hosted integrations remain incremental.

### `database/`
Hosted PostgreSQL schema/migration/data-loading area. Local Android character persistence lives under `shared/` SQLDelight/SQLite.

### `.github/workflows/scaffold-check.yml`
Current CI gate: JDK 17, Android SDK 36, Gradle 9.5, shared desktop tests, Android debug assembly, Desktop build, backend Node/TypeScript check and debug APK artifact upload.

The workflow reconstructs a stable development-only Android debug signing identity for update-in-place QA. It is not a release signing identity.

## Latest technically verified product identity

- version `0.4.0-preqa.7` / build `40700` / `debug`;
- tested product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- tested tree `3b2f2ab471097d3b108c9a787fc2342c5aad683a`;
- workflow `34171466714` — SUCCESS;
- artifact `10035895186`;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

Later consolidation commits are documentation/governance-only unless a newer checkpoint explicitly identifies newer product code.

## Historical/discovery material

### `docs/discovery/`
Historical exploratory reasoning. It does not override approved decisions/current state.

### `docs/checkpoints/`
Durable implementation, QA and handoff history. Historical next-action text may be superseded; `LATEST.md` controls the current resume point.

### historical branches
Branch refs remain evidence/history but are non-canonical after D-0048. Frozen candidates are immutable. See `docs/BRANCH_STATUS.md`.

## Authority rule

If documents appear to conflict:

1. `AGENTS.md` controls governance/working rules;
2. later specific Approved decisions/clarifications control over older general prose;
3. `docs/CONVENTIONS.md` controls recurring approved practice;
4. `docs/PRODUCT.md` controls approved product direction;
5. `docs/PROJECT_STATE.md` controls current implementation/next action;
6. `docs/checkpoints/LATEST.md` is the practical resume pointer;
7. `docs/BRANCH_STATUS.md` controls interpretation of branch refs;
8. detailed current checkpoints provide exact evidence;
9. discovery/history is contextual only;
10. surface material contradictions instead of guessing.
