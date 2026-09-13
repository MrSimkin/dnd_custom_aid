# Repository Manifest

This file maps the authoritative project-control files and implemented areas so a fresh human or AI can orient without reconstructing history from branch names or old checkpoints.

## Current authority model

There are currently **two active authoritative lines**:

- `main` — canonical global navigation plus current Phase 5A/DM product-discovery decisions;
- `implementation/phase4a-successor-cycle` — authoritative current Player/Phase 4A runtime, repair and QA line.

These refs are intentionally divergent and contain different valid work. `main` is not the latest Player runtime. The Player successor branch must not overwrite valid later DM/Phase 5A discovery records on `main`.

`docs/BRANCH_STATUS.md` is the canonical lifecycle map for every surviving branch/ref. Branch existence alone does not make a branch active.

## Root control files

### `README.md`
Project entry point, mandatory read sequence, current dual-line authority model, QA candidate and authorization boundary.

### `AGENTS.md`
Mandatory operating rules for humans and AI/coding agents. It explicitly distinguishes the two active authoritative lines and the owner-QA gate.

### `MANIFEST.md`
This inventory.

## Core `docs/` truth

### `docs/PROJECT_STATE.md`
Authoritative current implementation/QA state and exact continuation rules for the branch on which it is read.

### `docs/checkpoints/LATEST.md`
Stable practical resume pointer on each active authoritative line.

### `docs/BRANCH_STATUS.md`
Canonical branch-lifecycle map: active lines, historical milestones, audit evidence, frozen QA refs and exact resume rules.

### `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md`
Canonical cross-branch reconciliation explaining why `main` and the Player successor intentionally diverge and how both valid lines must be preserved.

### `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_QA_CANDIDATE.md`
Current Player QA candidate identity, automated evidence, physical owner-QA boundary and exact continuation point.

### `docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md`
Durable owner authorization for the accepted Player P1–P17 repair/validation cycle.

### `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P17_TABLET_QA_GATE_CLOSED.md`
Durable P17 physical tablet-QA gate policy.

### `docs/DECISIONS.md`
Master decision log. Later detailed approved decisions under `docs/decisions/` remain authoritative even if the master log has not yet been reconciled through the newest decision number.

### `docs/CONVENTIONS.md`
Approved recurring project conventions.

### `docs/PRODUCT.md`
Approved product scope and boundaries.

### `docs/ROADMAP.md`
Current phase/exit-gate view. Phase 4A is at owner/device QA, not mid-successor implementation.

### `docs/WORKFLOW.md`
Approved design/implementation/verification/checkpoint/review workflow.

### `docs/ARCHITECTURE.md`
Current approved architecture and implementation consequences.

### `docs/TESTING.md`
Current automated/physical-QA evidence and verification policy.

### `docs/TEST_DEVICES.md`
Owner-confirmed physical test devices.

## Current Player / Phase 4A boundary

Phases 0–3 are complete. Phase 4A remains open pending physical owner/device acceptance and explicit closure.

Accepted repair implementation:

- P1–P16 implemented / automation-qualified;
- P17 design decision closed as the physical tablet-QA gate policy;
- physical owner/device acceptance still pending.

Current QA candidate:

- version `0.4.0-preqa.9`;
- versionCode/build `40900`;
- candidate commit `cd0c203d337c062fa388010d300e875f2f54ced7`;
- Scaffold run `34726572588` — **SUCCESS**;
- artifact ID `10307444450`;
- artifact name `dnd-custom-aid-debug-apk`;
- artifact digest `sha256:2e8c7e3b2a3b11096eaeed3179b707a61b0d24e241c3fb5c31e9a5d99251ba7e`.

This is automation-qualified development/debug evidence, **not owner acceptance**.

## Current DM / Phase 5A boundary

`main` contains later accepted Phase 5A/DM discovery/design decisions, including the current Desk-family and shared Player/DM rules-question direction.

DM discovery/design may continue when explicitly requested.

DM **implementation** remains blocked until Phase 4A receives physical owner/device acceptance and explicit owner closure.

## Implemented application areas

### `shared/`
Kotlin Multiplatform shared domain/persistence module using SQLDelight. Current character work includes campaign-scoped characters, multiclass data, derived values/adjustments, Combat, Equipment/currencies, Background, Traits, Spells/sources/prepared state/shared slots, Notes, proficiencies, class/subclass provenance, Inspiration/death saves, Weapon Mastery, Resources, Forms, Companions, conditional modules and backup/import.

### `androidApp/`
Native Kotlin + Jetpack Compose Android app, `minSdk 30`, targeting phone/tablet portrait and landscape. The current Player implementation includes General, Habilidades, Combate, Gestión, Equipo/Monedas, Trasfondo, Rasgos, conditional Conjuros, Notas, PC Settings, Application Settings, Supercompact, Table Mode and approved conditional module families.

The repaired implementation is automation-qualified but still awaits physical owner/device acceptance.

### `desktopApp/`
Compose Multiplatform Desktop DM preparation/administration shell. Full Android parity is not required.

### `backend/`
TypeScript Cloudflare Worker/API area with current automated checks.

### `database/`
Hosted PostgreSQL schema/migration/data-loading area. Local Android character persistence lives under `shared/` SQLDelight/SQLite.

### `.github/workflows/scaffold-check.yml`
Current normal automated gate: backend install/type-check, stable CI debug keystore preparation, Kotlin/shared/Android/Desktop build-and-test surface and Android debug APK artifact upload.

## Historical/discovery material

Historical checkpoints and milestone branches remain evidence; their old next-action text may be superseded.

- `docs/checkpoints/LATEST.md` controls the practical resume point on the active branch;
- `docs/BRANCH_STATUS.md` controls branch lifecycle/authority;
- frozen QA refs stay immutable;
- deliberately removed refs are documented under the archive rather than recreated.

Do not restart work from an old checkpoint merely because its prose says “next.”

## Authority rule

If documents appear to conflict:

1. `AGENTS.md` controls governance/working rules;
2. later specific Approved decisions/clarifications control over older general prose;
3. `docs/BRANCH_STATUS.md` controls current branch lifecycle/role;
4. `docs/PROJECT_STATE.md` controls current implementation state on the active branch;
5. `docs/checkpoints/LATEST.md` controls practical resume;
6. current detailed checkpoints provide exact evidence;
7. `docs/CONVENTIONS.md`, `docs/PRODUCT.md`, `docs/ROADMAP.md`, `docs/WORKFLOW.md`, `docs/ARCHITECTURE.md` and `docs/TESTING.md` control their respective domains;
8. historical discovery/checkpoints are contextual evidence only when superseded;
9. surface material contradictions instead of guessing.
