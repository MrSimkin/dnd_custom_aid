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

### `docs/checkpoints/2026-09-04_PHASE4_M6_QA_PAUSE_HANDOFF.md`
Current practical resume checkpoint while owner QA is paused. It records the exact frozen M6 candidate, APK identity, mandatory first migration test, QA matrix, branch invariants and exact resume sequence.

### `docs/DECISIONS.md`
Chronological significant-decision log. Detailed later Phase 4 decisions under `docs/decisions/` remain authoritative where the consolidated master log has not yet been reconciled. Full reconciliation through D-0044–D-0047 is intentionally deferred to post-QA governance housekeeping.

### `docs/decisions/`
Detailed approved decision records. Current Phase 4 character work is governed especially by D-0044 through D-0047. Historical `D-0065...` is explicitly superseded by D-0047 and must not be treated as a competing decision.

### `docs/CONVENTIONS.md`
Owner-approved recurring project conventions, including product-Spanish/technical-English, representative SQL when useful, personal-scale proportionality and intended-device QA.

### `docs/PRODUCT.md`
Approved product scope and product boundaries.

### `docs/ROADMAP.md`
Development phases and current Phase 4 closure boundary. Phase 4A implementation is complete; M6 owner real-device QA is the next gate.

### `docs/WORKFLOW.md`
Approved design/implementation/verification/checkpoint/review/merge workflow.

### `docs/ARCHITECTURE.md`
Current approved architecture and implementation consequences.

### `docs/TESTING.md`
Verification policy, commands, exact frozen M6 QA candidate and current phone/tablet owner-QA matrix.

### `docs/checkpoints/`
Durable implementation, QA and handoff checkpoints. The current resume entry point is `2026-09-04_PHASE4_M6_QA_PAUSE_HANDOFF.md`. Historical batch checkpoints remain evidence and must not override the current resume point.

### `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md`
Class/subclass audit and conditional-module design input that informed D-0047.

## Implemented application areas

### `shared/`
One Kotlin Multiplatform shared module containing campaign and character domain/persistence logic plus SQLDelight.

Current character work includes persistent campaign-scoped characters, multiclass data, derived values/adjustments, Combat, Equipment/currencies, Background, Traits, Spells/sources/prepared state/shared slots, Notes, structured proficiencies, class/subclass provenance, Inspiration/death saves, Weapon Mastery, Resources, Forms, Companions, conditional modules, backup/import and owner-lineage migration coverage.

Do not split this into speculative architecture-layer Gradle modules without a concrete need.

### `androidApp/`
Native Kotlin + Jetpack Compose Android application, `minSdk 30`.

The Phase 4 character editor includes General, Habilidades, Combate, Gestión, Equipo/Monedas, Trasfondo, Rasgos, conditional Conjuros, Notas, PC Settings, Application Settings, Supercompact/Table mode and all six approved conditional class/subclass module families, with phone/tablet adaptive behavior. Phase 4A implementation is now frozen for M6 owner QA.

### `desktopApp/`
Kotlin + Compose Multiplatform Desktop DM preparation/administration shell. It builds against shared APIs; full Android feature parity is not required.

### `backend/`
TypeScript Cloudflare Worker/API area with current minimal backend checks. Hosted feature integrations remain incremental and are not activated merely because providers support them.

### `database/`
Hosted PostgreSQL schema/migration/data-loading area. Local Android character persistence currently lives in SQLDelight/SQLite under `shared/`.

### `.github/workflows/scaffold-check.yml`
Current simple CI gate: JDK 17, Android SDK 36, Gradle 9.5, shared desktop tests, Android debug assembly, Desktop build, backend Node/TypeScript check and debug APK artifact upload.

The workflow also prepares a stable development-only Android debug signing identity for update-in-place QA/migration testing. This is not a release signing identity.

## Active frozen QA candidate

Owner QA must use only:

- branch `tmp/phase4-m5-frozen-qa-candidate`;
- commit `adc286b3e1305ed706c2ed04d478a43652f6b365`;
- tree `fd1f7feffde082b34cce41248e951a25eed7a004`;
- artifact `9951922423` / `phase4-m5-frozen-qa-apk`;
- APK SHA-256 `e31ce44a84cd79260ea2c51c65cb6a63675b1f916998e44d583358d72893c8ee`.

The historical Batch L frozen candidate remains immutable evidence only and is not the active QA target.

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
6. `docs/checkpoints/2026-09-04_PHASE4_M6_QA_PAUSE_HANDOFF.md` is the practical resume checkpoint while QA is paused;
7. other current checkpoints provide exact historical work evidence;
8. discovery/history is contextual only;
9. surface material contradictions instead of guessing.
