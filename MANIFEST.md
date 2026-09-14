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
Project entry point, mandatory read sequence, dual-line authority model and authorization boundary. Volatile Player candidate/run details intentionally live in the synchronized state files rather than being duplicated here.

### `AGENTS.md`
Mandatory operating rules for humans and AI/coding agents. It distinguishes the two active authoritative lines, owner authority and the Phase 4A closure boundary.

### `MANIFEST.md`
This inventory.

## Core `docs/` truth

### `docs/PROJECT_STATE.md`
Authoritative current implementation/QA state, frozen physical baseline and exact continuation rules for the branch on which it is read.

### `docs/checkpoints/LATEST.md`
Stable practical resume pointer on each active authoritative line.

### latest bounded repair checkpoint under `docs/checkpoints/`
Exact implementation, automation and remaining physical-revalidation evidence for the most recently completed repair round. Follow `LATEST.md` rather than hard-coding a dated filename here.

### `docs/BRANCH_STATUS.md`
Canonical branch-lifecycle map: active lines, historical milestones, audit evidence, frozen QA refs and exact resume rules.

### `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md`
Canonical cross-branch reconciliation explaining why `main` and the Player successor intentionally diverge and how both valid lines must be preserved.

### `docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md`
Durable owner authorization for the accepted Player P1–P17 repair/validation cycle.

### `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P17_TABLET_QA_GATE_CLOSED.md`
Historical durable P17 physical tablet-QA gate policy. Later physical discovery/repair checkpoints control the current state.

### `docs/DECISIONS.md`
Master decision log. Later detailed approved decisions under `docs/decisions/` remain authoritative even if the master log has not yet been reconciled through the newest decision number.

### `docs/CONVENTIONS.md`
Approved recurring project conventions.

### `docs/PRODUCT.md`
Approved product scope and boundaries.

### `docs/ROADMAP.md`
Phase/exit-gate view. Use `PROJECT_STATE.md` and `LATEST.md` for the exact live repair-round position when roadmap prose is more general.

### `docs/WORKFLOW.md`
Approved design/implementation/verification/checkpoint/review workflow.

### `docs/ARCHITECTURE.md`
Current approved architecture and implementation consequences.

### `docs/TESTING.md`
Synchronized current automated/physical-QA evidence, testing policy and targeted revalidation route.

### `docs/TEST_DEVICES.md`
Owner-confirmed physical test devices.

## Current Player / Phase 4A boundary

Phases 0–3 are complete. Phase 4A remains open. Physical phone/tablet discovery has already produced a consolidated repair cycle, and the Player successor branch is the active implementation/automation line for that work.

This manifest deliberately does **not** duplicate an exact current candidate SHA, Scaffold run or repair-round number because those values change during bounded repairs. For the current Player state, always use:

1. `docs/PROJECT_STATE.md`;
2. `docs/checkpoints/LATEST.md`;
3. the latest repair checkpoint they reference;
4. `docs/TESTING.md`.

Automation-qualified repair work is **not owner acceptance**. The frozen physical baseline remains immutable evidence until a new monotonic candidate is explicitly created, and Phase 4A closes only after sufficient real-device revalidation and explicit owner closure.

## Current DM / Phase 5A boundary

`main` contains later accepted Phase 5A/DM discovery/design decisions, including the current Desk-family and shared Player/DM rules-question direction.

DM discovery/design may continue when explicitly requested.

DM **implementation** remains blocked until Phase 4A receives physical owner/device acceptance and explicit owner closure.

## Implemented application areas

### `shared/`
Kotlin Multiplatform shared domain/persistence module using SQLDelight. Current character work includes campaign-scoped characters, multiclass data, derived values/adjustments, Combat, Equipment/currencies, Background, Traits, Spells/sources/prepared state/shared slots, Notes, proficiencies, class/subclass provenance, Inspiration/death saves, Weapon Mastery, Resources, Forms, Companions, conditional modules and backup/import.

### `androidApp/`
Native Kotlin + Jetpack Compose Android app, `minSdk 30`, targeting phone/tablet portrait and landscape. The current Player implementation includes General, Habilidades, Combate, Gestión, Equipo/Monedas, Trasfondo, Rasgos, conditional Conjuros, Notas, PC Settings, Application Settings, Supercompact, Table Mode and approved conditional module families.

Current acceptance level must be read from `PROJECT_STATE.md` / `TESTING.md`; implementation or green CI alone does not imply physical acceptance.

### `desktopApp/`
Compose Multiplatform Desktop DM preparation/administration shell. Full Android parity is not required.

### `backend/`
TypeScript Cloudflare Worker/API area with current automated checks.

### `database/`
Hosted PostgreSQL schema/migration/data-loading area. Local Android character persistence lives under `shared/` SQLDelight/SQLite.

### `.github/workflows/scaffold-check.yml`
Normal automated gate: backend install/type-check, stable CI debug-keystore preparation, durable Player source guards, Kotlin/shared/Android/Desktop build-and-test surface and Android debug APK artifact upload.

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
