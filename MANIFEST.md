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

**Role:** durable registry of owner-approved project conventions: how coding, structure, naming, testing, documentation, workflow, terminology, or similar recurring practices should be handled consistently.  
**Update when:** a new convention is approved, an existing convention changes, or one is superseded.

### `docs/PRODUCT.md`

**Role:** approved product scope, MVP, design/discovery direction, user groups, constraints, and explicitly unresolved product questions.  
**Update when:** product scope, MVP, design direction, or requirements are approved or changed.

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

## `docs/discovery/`

**Role:** preserves raw or partially structured product-discovery material that is useful for continuity but is **not automatically approved product scope**.  
**Update when:** a discovery conversation introduces material ideas, questions, alternatives, observations, or unresolved concepts that a future chat/agent needs in order to continue the discussion accurately.

Rules:

- every discovery note must state clearly which material is provisional and which clarifications were explicitly confirmed;
- future agents must not implement an item merely because it appears only in a discovery note;
- confirmed conclusions should be reflected in `docs/PRODUCT.md`, `docs/DECISIONS.md`, and/or other authoritative files as appropriate;
- discovery notes remain useful as historical rationale after conclusions are approved.

### `docs/discovery/2026-08-28_INITIAL_PRODUCT_PICTURE.md`

**Role:** first structured capture of the owner's initial brainstorming: Spanish-only application idea, player character-sheet workflow, DM tablet workflow, accounts/campaign enrollment, online data, possible Windows companion, and SRD consultation.  
**Status:** historical/provisional discovery input.

### `docs/discovery/2026-08-28_CLARIFICATIONS_01.md`

**Role:** first clarification pass: physical-sheet/backup purpose, audit-with-correction model, campaign-scoped roles, future multi-system direction, DM quick views, combat tracker visibility, mixed SRD/house rules, rules-assistant intent, hosted-data scale, desktop administration need, language split, invitation direction and remaining questions.  
**Status:** mixed confirmed conclusions and explicitly open questions; confirmed conclusions have been promoted to authoritative files where appropriate.

### `docs/discovery/2026-08-29_CLARIFICATIONS_02.md`

**Role:** second clarification pass covering paper/digital reconciliation, full end-of-session character backup, grouped compensating audit history and bloat concern, ownership/control/co-DM extensibility, notes-style house rules and D&D 5e/5.5e presentation, Quick/Developed NPC examples, complete monster stat blocks, reusable creature library, combat working-state scope/offline behavior, and the remaining re-explanations needed before MVP design.  
**Status:** historical mixed confirmed conclusions/open follow-ups; its previously open high-value questions were resolved in Round 3 and promoted into authoritative files.

### `docs/discovery/2026-08-29_CLARIFICATIONS_03.md`

**Role:** third clarification pass closing the major Round 2 questions: PDF export with unsaved edits, initial audit-retention policy, unassigned PC records, stat-block internal granularity/extensibility, prepared vs on-the-fly encounters, account/invitation/recovery and moderation concepts, and the approved first usable release/MVP boundary.  
**Status:** confirmed owner decisions; conclusions are promoted into `docs/PRODUCT.md`, `docs/DECISIONS.md` and `docs/PROJECT_STATE.md`.

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
6. treat `docs/discovery/` as supporting rationale/provisional context except where conclusions were also promoted to authoritative files;
7. surface any material contradiction instead of guessing.