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

### `docs/checkpoints/LATEST.md`
Stable current resume pointer. It names the active checkpoint, review identity and exact next action without requiring historical reconstruction.

### `docs/checkpoints/2026-09-07_PHASE4_PREQA_UX_REPAIR_PASS_07.md`
Current technical repair checkpoint. Pass 07 marks the focused fixed/sticky, spacing-scale, IME/window and card-column audit line technically stable at build `40700`; formal M6 remains deferred.

### `docs/PREQA_OWNER_VISUAL_AUDITION.md`
Staged owner phone/tablet visual-audition guide for typography, text scale, spacing, columns/rotation, IME behavior and fixed/sticky footprint before any replacement formal M6 candidate is frozen.

### `docs/DECISIONS.md`
Chronological significant-decision log, reconciled through D-0047. Detailed records under `docs/decisions/` remain the authoritative source for full rationale and approved nuance.

### `docs/decisions/`
Detailed approved decision records. Current Phase 4 character work is governed especially by D-0044 through D-0047. Historical `D-0065...` is explicitly superseded by D-0047 and must not be treated as a competing decision.

### `docs/CONVENTIONS.md`
Owner-approved recurring project conventions, including product-Spanish/technical-English, representative SQL when useful, personal-scale proportionality and intended-device QA.

### `docs/PRODUCT.md`
Approved product scope and product boundaries.

### `docs/ROADMAP.md`
Development phases and current Phase 4 closure boundary. The focused pre-QA repair line is technically stable through Pass 07; staged owner visual audition precedes freezing a replacement formal M6 candidate.

### `docs/WORKFLOW.md`
Approved design/implementation/verification/checkpoint/review/merge workflow.

### `docs/ARCHITECTURE.md`
Current approved architecture and implementation consequences.

### `docs/TESTING.md`
Verification policy, commands, current pre-QA review identity, owner visual-audition boundary and the formal phone/tablet M6 matrix to use after a replacement candidate is explicitly frozen.

### `docs/checkpoints/`
Durable implementation, QA and handoff checkpoints. The current resume entry point is always `LATEST.md`; historical batch/M6-pause checkpoints remain evidence and must not override the current pointer.

### `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md`
Class/subclass audit and conditional-module design input that informed D-0047.

## Implemented application areas

### `shared/`
One Kotlin Multiplatform shared module containing campaign and character domain/persistence logic plus SQLDelight.

Current character work includes persistent campaign-scoped characters, multiclass data, derived values/adjustments, Combat, Equipment/currencies, Background, Traits, Spells/sources/prepared state/shared slots, Notes, structured proficiencies, class/subclass provenance, Inspiration/death saves, Weapon Mastery, Resources, Forms, Companions, conditional modules, backup/import and owner-lineage migration coverage.

Do not split this into speculative architecture-layer Gradle modules without a concrete need.

### `androidApp/`
Native Kotlin + Jetpack Compose Android application, `minSdk 30`.

The Phase 4 character editor includes General, Habilidades, Combate, Gestión, Equipo/Monedas, Trasfondo, Rasgos, conditional Conjuros, Notas, PC Settings, Application Settings, Supercompact/Table mode and all six approved conditional class/subclass module families, with phone/tablet adaptive behavior. The focused pre-QA repair line is technically stable through Pass 07; it is awaiting owner visual audition rather than frozen formal M6 QA.

### `desktopApp/`
Kotlin + Compose Multiplatform Desktop DM preparation/administration shell. It builds against shared APIs; full Android feature parity is not required.

### `backend/`
TypeScript Cloudflare Worker/API area with current minimal backend checks. Hosted feature integrations remain incremental and are not activated merely because providers support them.

### `database/`
Hosted PostgreSQL schema/migration/data-loading area. Local Android character persistence currently lives in SQLDelight/SQLite under `shared/`.

### `.github/workflows/scaffold-check.yml`
Current simple CI gate: JDK 17, Android SDK 36, Gradle 9.5, shared desktop tests, Android debug assembly, Desktop build, backend Node/TypeScript check and debug APK artifact upload.

The workflow also prepares a stable development-only Android debug signing identity for update-in-place QA/migration testing. This is not a release signing identity.

## Current pre-QA review identity

The current owner-audition build is:

- branch `implementation/phase4-preqa-ux-repair`;
- review version `0.4.0-preqa.7` / build `40700`;
- tested product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- tested tree `3b2f2ab471097d3b108c9a787fc2342c5aad683a`;
- checkpoint head `4c6da4577b57e472819e096ecff55bd6750e026d`;
- workflow `34171466714` — SUCCESS;
- artifact `10035895186` / `DND-Custom-Aid-0.4.0-preqa.7-build-40700-debug`;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

This is **not yet a frozen formal M6 candidate**. Use `docs/PREQA_OWNER_VISUAL_AUDITION.md` first. The historical M5 and Batch L frozen branches remain immutable evidence and are not the active audition target.

## Character-sheet assets

### `assets/character-sheets/templates/`
Owner-provided blank/custom PDF presentation templates. They are references/export templates, not the authoritative character data model.

### `assets/character-sheets/CHANGE_REQUESTS.md`
Owner-side InDesign/PDF changes exposed by implementation needs.

## Historical/discovery material

### `docs/discovery/`
Historical exploratory reasoning. It does not override approved decisions/current state.

### historical handoffs/checkpoints/branches
Preserve until the eventual post-QA/post-merge unique-commit audit. Do not delete merely because newer checkpoints supersede their next-action instructions.

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
6. `docs/checkpoints/LATEST.md` is the stable practical resume pointer;
7. other current checkpoints provide exact historical work evidence;
8. discovery/history is contextual only;
9. surface material contradictions instead of guessing.
