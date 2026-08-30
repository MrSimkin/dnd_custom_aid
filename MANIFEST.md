# Repository Manifest

This file inventories the project-control files and explains when each one must be updated. Its purpose is to help a fresh human or AI orient quickly without guessing which document is authoritative for a given question.

## Root

### `README.md`

**Role:** project entry point and mandatory read order.  
**Current state:** Phase 1 complete; Phase 2 architecture/technology evaluation active.  
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
**Current state:** Phase 2 active; next decision is overall application topology/surface relationship.  
**Update when:** every meaningful work session changes project reality or next action.

### `docs/DECISIONS.md`

**Role:** durable significant-decision log.  
**Current state:** approved product decisions through D-0033; D-0009 remains intentionally pending for active Phase 2 architecture/technology evaluation.  
**Update when:** a significant decision is proposed, approved, rejected, amended, or superseded.

### `docs/CONVENTIONS.md`

**Role:** durable registry of owner-approved project conventions: how coding, structure, naming, testing, documentation, workflow, terminology, or similar recurring practices should be handled consistently.  
**Update when:** a new convention is approved, an existing convention changes, or one is superseded.

### `docs/PRODUCT.md`

**Role:** approved product scope, MVP, design/discovery direction, user groups, constraints, and current product boundaries.  
**Current state:** final Phase 1 product baseline, including multicampaign MVP and the eight final tension resolutions.  
**Update when:** product scope, MVP, design direction, or requirements are approved or changed.

### `docs/ROADMAP.md`

**Role:** staged development plan and current phase.  
**Current state:** Phase 1 complete; Phase 2 active.  
**Update when:** project phases, exit criteria, or current phase materially change.

### `docs/WORKFLOW.md`

**Role:** approved discussion, design, implementation, verification, review, merge, and operative-memory workflow.  
**Update when:** working process changes.

### `docs/ARCHITECTURE.md`

**Role:** technical architecture evaluation/record and rationale.  
**Current state:** architecture is not selected; Phase 2 evaluation is active and begins with overall application topology/surface relationship. Consequential choices remain owner-controlled and implementation has not been scaffolded.  
**Update when:** architecture alternatives are formally evaluated, selected, or materially changed.

### `docs/TESTING.md`

**Role:** test/verification policy and future test-state expectations across approved application surfaces.  
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
**Status:** confirmed owner decisions; conclusions are promoted into `docs/PRODUCT.md`, `docs/DECISIONS.md` (D-0033 and amended earlier decisions), `docs/PROJECT_STATE.md`, roadmap, architecture/testing and entry-point documentation. Historical rationale only after Phase 1 closure.

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

No application-code directories are authoritative yet because architecture/technology selection has not been approved. When the technical foundation is approved and scaffolded, add the major code/build directories to this manifest with a one-line purpose for each.

## Authority rule

If documents appear to conflict:

1. follow `AGENTS.md` for governance/working rules;
2. follow `docs/DECISIONS.md` for explicitly approved significant decisions;
3. follow `docs/CONVENTIONS.md` for approved recurring project practices;
4. follow `docs/PRODUCT.md` for approved product requirements/direction;
5. follow `docs/PROJECT_STATE.md` for current implementation/work status;
6. treat `docs/discovery/` as supporting historical rationale/provisional context except where conclusions were promoted to authoritative files;
7. surface any material contradiction instead of guessing.