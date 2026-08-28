# Repository Manifest

This file inventories the project-control files and explains when each one must be updated. Its purpose is to help a fresh human or AI orient quickly without guessing which document is authoritative for a given question.

## Root

### `README.md`

**Role:** project entry point and mandatory read order.  
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
**Update when:** every meaningful work session changes project reality or next action.

### `docs/DECISIONS.md`

**Role:** durable significant-decision log.  
**Update when:** a significant decision is proposed, approved, rejected, or superseded.

### `docs/CONVENTIONS.md`

**Role:** durable registry of owner-approved project conventions: how coding, structure, naming, testing, documentation, workflow, or similar recurring practices should be handled consistently.  
**Update when:** a new convention is approved, an existing convention changes, or one is superseded.

### `docs/PRODUCT.md`

**Role:** approved product scope, design/discovery approach, user groups, constraints, and explicitly unresolved product questions.  
**Update when:** product scope, design direction, or requirements are approved or changed.

### `docs/ROADMAP.md`

**Role:** staged development plan and current phase.  
**Update when:** project phases, exit criteria, or current phase materially change.

### `docs/WORKFLOW.md`

**Role:** approved discussion, design, implementation, verification, review, merge, and operative-memory workflow.  
**Update when:** working process changes.

### `docs/ARCHITECTURE.md`

**Role:** technical architecture record and rationale.  
**Current state:** architecture not selected; it must follow sufficient product/design work.  
**Update when:** architecture alternatives are formally evaluated, selected, or materially changed.

### `docs/TESTING.md`

**Role:** test/verification policy and future test-state expectations.  
**Update when:** test strategy, device matrix, commands, or release verification rules change.

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

No application-code directories are authoritative yet because product/interaction design is not complete and the Android architecture has not been chosen. When the technical foundation is approved and scaffolded, add the major code/build directories to this manifest with a one-line purpose for each.

## Authority rule

If documents appear to conflict:

1. follow `AGENTS.md` for governance/working rules;
2. follow `docs/DECISIONS.md` for explicitly approved significant decisions;
3. follow `docs/CONVENTIONS.md` for approved recurring project practices;
4. follow `docs/PROJECT_STATE.md` for current implementation/work status;
5. surface any material contradiction instead of guessing.
