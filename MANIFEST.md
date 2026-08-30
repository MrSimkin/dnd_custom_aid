# Repository Manifest

This file inventories the project-control files and explains when each one must be updated. Its purpose is to help a fresh human or AI orient quickly without guessing which document is authoritative for a given question.

## Root

### `README.md`

**Role:** project entry point and mandatory read order.  
**Current state:** Phase 1 product discovery complete; Phase 2 foundational architecture selected; documentation consolidation/review is the final gate before implementation scaffolding.  
**Update when:** the project purpose, canonical read order, or top-level status changes.

### `AGENTS.md`

**Role:** mandatory operating rules for humans and AI/coding agents.  
**Update when:** project governance, authority boundaries, required workflow, communication rules, or continuity rules change.

### `MANIFEST.md`

**Role:** inventory of durable project-control files.  
**Update when:** important control documents are added, removed, renamed, or change purpose.

## `docs/`

### `docs/PROJECT_STATE.md`

**Role:** authoritative snapshot of what exists now, what is being worked on, known blockers, last verification, next owner decision, and next implementation action.  
**Current state:** foundational architecture D-0034 through D-0043 is approved; no application code exists yet; documentation consolidation/review precedes scaffolding.  
**Update when:** every meaningful work session changes project reality or next action.

### `docs/DECISIONS.md`

**Role:** chronological significant-decision log.  
**Current state:** chronological consolidation currently reaches D-0035; approved architecture decisions D-0036 through D-0043 are durably stored under `docs/decisions/` and must be consolidated here before the architecture branch is merged. D-0009's older Pending wording is therefore known stale documentation, not an unresolved architecture choice.  
**Update when:** a significant decision is proposed, approved, rejected, amended, superseded, or consolidated from a dedicated safety-checkpoint file.

### `docs/decisions/`

**Role:** dedicated safety-checkpoint files for consequential architecture decisions approved during the active Phase 2 branch before chronological-log consolidation.  
**Current state:** D-0036 through D-0043 are approved and authoritative checkpoints pending consolidation into `docs/DECISIONS.md`.  
**Update when:** a consequential decision needs immediate durable capture before the main chronological log is consolidated.

### `docs/CONVENTIONS.md`

**Role:** durable registry of owner-approved recurring project conventions.  
**Current state:** includes C-0008 (use representative SQL when useful for owner review) and C-0009 (personal-scale proportionality; avoid enterprise overengineering).  
**Update when:** a new convention is approved, an existing convention changes, or one is superseded.

### `docs/PRODUCT.md`

**Role:** approved product scope, MVP, design/discovery direction, user groups, constraints, and current product boundaries.  
**Current state:** final Phase 1 product baseline. Later architecture decisions clarify that the DM desktop is native Kotlin/Compose Multiplatform and that character-sheet PDF export is required on both Android and desktop; stale older wording must be consolidated before merge.  
**Update when:** product scope, MVP, design direction, or requirements are approved or changed.

### `docs/ROADMAP.md`

**Role:** staged development plan and current phase.  
**Current state:** Phase 1 complete; Phase 2 architecture selection complete in substance; implementation scaffolding follows documentation consolidation/review.  
**Update when:** project phases, exit criteria, or current phase materially change.

### `docs/WORKFLOW.md`

**Role:** approved discussion, design, implementation, verification, review, merge, and operative-memory workflow.  
**Update when:** working process changes.

### `docs/ARCHITECTURE.md`

**Role:** current approved architecture record and rationale.  
**Current state:** foundational architecture D-0034 through D-0043 is selected and sufficient for the initial scaffold.  
**Update when:** architecture is materially changed or a new consequential architecture choice is approved.

### `docs/TESTING.md`

**Role:** test/verification policy and future test-state expectations across approved application surfaces.  
**Current state:** must be aligned during the final consolidation with D-0043's deliberately small risk-focused testing/CI approach before scaffolding.  
**Update when:** test strategy, device/environment matrix, commands, or release verification rules change.

## `docs/discovery/`

**Role:** preserves historical product-discovery material and rationale. Discovery notes are **not automatically approved product scope** and do not override authoritative product/decision records.

Rules:

- every discovery note states which material was provisional and which clarifications were explicitly confirmed;
- future agents must not implement an item merely because it appears only in a discovery note;
- confirmed conclusions are reflected in `docs/PRODUCT.md`, `docs/DECISIONS.md`, and/or other authoritative files;
- discovery notes remain useful as historical rationale after conclusions are approved.

### `docs/discovery/2026-08-28_INITIAL_PRODUCT_PICTURE.md`

**Role:** first structured capture of the owner's initial brainstorming.  
**Status:** historical/provisional discovery input.

### `docs/discovery/2026-08-28_CLARIFICATIONS_01.md`

**Role:** first clarification pass.  
**Status:** historical mixed confirmed conclusions/open questions; later authoritative records resolve the applicable questions.

### `docs/discovery/2026-08-29_CLARIFICATIONS_02.md`

**Role:** second clarification pass.  
**Status:** historical rationale; its high-value open questions were resolved later and promoted.

### `docs/discovery/2026-08-29_CLARIFICATIONS_03.md`

**Role:** third clarification pass closing the major Round 2 questions and MVP boundary.  
**Status:** confirmed owner decisions; promotion into authoritative records is complete.

### `docs/discovery/2026-08-29_TENSION_RESOLUTIONS.md`

**Role:** final Phase 1 clarification pass resolving eight soft product tensions/underspecified boundaries.  
**Status:** confirmed owner decisions; conclusions were promoted into authoritative records. Historical rationale only after Phase 1 closure.

## `assets/character-sheets/`

**Role:** owner-provided character-sheet presentation assets and the owner-side layout change log.

### `assets/character-sheets/templates/`

**Role:** blank/custom PDF character-sheet templates supplied by the owner. These are presentation/output templates, not the authoritative character data model.

### `assets/character-sheets/CHANGE_REQUESTS.md`

**Role:** records Adobe InDesign/PDF changes that implementation/design requires from the owner.  
**Update when:** an app requirement exposes a necessary owner-side template layout change or when such a request is completed/cancelled.

## `docs/templates/`

### `FEATURE_SPEC_TEMPLATE.md`

Template for defining user-visible features before implementation.

### `DECISION_TEMPLATE.md`

Template for presenting consequential decisions to the owner, including alternatives/trade-offs, and recording the outcome.

### `HANDOFF_TEMPLATE.md`

Optional detailed handoff template for partial or complex sessions. `docs/PROJECT_STATE.md` remains the authoritative current-state summary.

## `.github/`

### `.github/pull_request_template.md`

Review checklist for substantial changes, including owner decisions, conventions, verification, and continuity updates.

## Future application code

No application-code directories exist yet. D-0043 approves a deliberately small initial scaffold with the equivalent of shared Kotlin logic, Android app, desktop app, TypeScript Cloudflare backend, and SQL/database areas. Add the actual generated paths to this manifest when scaffolding creates them.

## Authority rule

If documents appear to conflict:

1. follow `AGENTS.md` for governance/working rules;
2. follow explicitly approved significant decisions, including dedicated `docs/decisions/` checkpoints not yet consolidated into `docs/DECISIONS.md`;
3. follow `docs/CONVENTIONS.md` for approved recurring project practices;
4. follow `docs/PRODUCT.md` for approved product requirements/direction, subject to later approved decision amendments;
5. follow `docs/PROJECT_STATE.md` for current implementation/work status;
6. treat `docs/discovery/` as supporting historical rationale/provisional context except where conclusions were promoted to authoritative files;
7. surface any material contradiction instead of guessing.
