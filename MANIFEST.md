# Repository Manifest

This file inventories the project-control files and implemented code areas so a fresh human or AI can orient quickly without guessing what is authoritative or what actually exists.

## Root control files

### `README.md`
Project entry point, mandatory read order, current build commands and active-stage summary.

### `AGENTS.md`
Mandatory operating rules for humans and AI/coding agents.

### `MANIFEST.md`
This inventory of durable project-control files and implemented areas.

## Core `docs/` truth

### `docs/PROJECT_STATE.md`
Authoritative snapshot of current branches, implementation reality, verification, blockers and exact next action.

### `docs/DECISIONS.md`
Chronological significant-decision log. Detailed later Phase 4 decisions under `docs/decisions/` remain authoritative where the consolidated master log has not yet been reconciled.

### `docs/decisions/`
Detailed approved decision records. Current Phase 4 character work is governed especially by D-0044 through D-0047. Historical `D-0065...` is explicitly superseded by D-0047 and must not be treated as a competing decision.

### `docs/CONVENTIONS.md`
Owner-approved recurring project conventions, including product-Spanish/technical-English, representative SQL when useful, personal-scale proportionality and intended-device QA.

### `docs/PRODUCT.md`
Approved product scope and product boundaries.

### `docs/ROADMAP.md`
Development phases and current Phase 4 closure boundary.

### `docs/WORKFLOW.md`
Approved design/implementation/verification/checkpoint/review/merge workflow.

### `docs/ARCHITECTURE.md`
Current approved architecture and implementation consequences.

### `docs/TESTING.md`
Verification policy, commands, migration expectations and current phone/tablet QA boundary.

### `docs/checkpoints/`
Durable implementation, QA and handoff checkpoints. The current execution entry point is `2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md`, supported by `2026-09-03_PHASE4_CLOSURE_IMPLEMENTATION_MAP.md`.

### `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md`
Current class/subclass audit and conditional-module design input for D-0047.

## Implemented application areas

### `shared/`
One Kotlin Multiplatform shared module containing campaign and character domain/persistence logic plus SQLDelight.

Current character work includes persistent campaign-scoped characters, multiclass data, derived values/adjustments, Combat, Equipment/currencies, Background, Traits, Spells/sources/prepared state/shared slots, Notes, and the schema-6 closure prototype domains for subclass/provenance, Inspiration/death saves, proficiencies, Weapon Mastery, Resources, Forms and Companions.

Do not split this into speculative architecture-layer Gradle modules without a concrete need.

### `androidApp/`
Native Kotlin + Jetpack Compose Android application, `minSdk 30`.

Current Phase 4 character editor includes General, Habilidades, Combate, Equipo, Trasfondo, Rasgos, conditional Conjuros, Notas and PC Settings. The D-0047 closure expands/fixes these surfaces and adds phone/tablet adaptive behavior, Gestión and conditional class/subclass modules.

### `desktopApp/`
Kotlin + Compose Multiplatform Desktop DM preparation/administration shell. It builds against shared APIs; full Android feature parity is not required.

### `backend/`
TypeScript Cloudflare Worker/API area with current minimal backend checks. Hosted feature integrations remain incremental and are not activated merely because providers support them.

### `database/`
Hosted PostgreSQL schema/migration/data-loading area. Local Android character persistence currently lives in SQLDelight/SQLite under `shared/`.

### `.github/workflows/scaffold-check.yml`
Current simple CI gate: JDK 17, Android SDK 36, Gradle 9.5, shared desktop tests, Android debug assembly, Desktop build, backend Node/TypeScript check and debug APK artifact upload.

The workflow also prepares a stable development-only Android debug signing identity for update-in-place QA/migration testing. This is not a release signing identity.

## Character-sheet assets

### `assets/character-sheets/templates/`
Owner-provided blank/custom PDF presentation templates. They are references/export templates, not the authoritative character data model.

### `assets/character-sheets/CHANGE_REQUESTS.md`
Owner-side InDesign/PDF changes exposed by implementation needs.

## Historical/discovery material

### `docs/discovery/`
Historical exploratory reasoning. It does not override approved decisions/current state.

### historical handoffs/checkpoints/branches
Preserve until the eventual post-merge unique-commit audit. Do not delete merely because newer checkpoints supersede their next-action instructions.

## Current major exclusions / later architecture

The current character closure does not itself activate or implement the future hosted account/sync stack, DM combat features, PDF completion, SRD retrieval/AI clarification or generalized realtime infrastructure unless a separately approved current batch requires it.

C-0009 remains controlling: add infrastructure only for concrete approved needs.

## Authority rule

If documents appear to conflict:

1. `AGENTS.md` controls governance/working rules;
2. later specific Approved decisions/clarifications control over older general prose;
3. `docs/CONVENTIONS.md` controls recurring approved practice;
4. `docs/PRODUCT.md` controls approved product direction;
5. `docs/PROJECT_STATE.md` controls current implementation/next action;
6. current checkpoints provide the exact active work sequence;
7. discovery/history is contextual only;
8. surface material contradictions instead of guessing.
