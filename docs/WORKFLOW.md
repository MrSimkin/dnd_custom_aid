# Development and Review Workflow

This file defines the approved operating workflow for AI-led implementation with owner-controlled decisions and Git-based operative memory.

## 1. Guiding principle

The repository must always make a clear distinction between:

- accepted project state;
- work in progress;
- proposed decisions;
- unresolved questions;
- approved conventions;
- verification actually performed.

The goal is to prevent a future agent from confusing an experiment, chat suggestion, unrecorded convention, or remembered discussion with approved project truth.

## 2. Branch model

- `main` = canonical accepted/published project state.
- Substantial changes = developed on a focused branch.
- Experiments must not be treated as canonical until merged.
- Merge into `main` only after owner approval or explicit delegation for that category of change.

This model is approved by D-0007.

## 3. Communication model

The agent is responsible for the technical heavy lifting, but meaningful work must remain understandable to the owner.

During development, explain:

- what is being done;
- why it is needed;
- the important technical approach;
- meaningful alternatives when they exist;
- consequences or trade-offs the owner should know about.

The owner understands programming concepts but is not expected to operate as a professional software developer. Explanations should therefore be technically useful without assuming specialist experience and without oversimplifying unnecessarily.

## 4. Product/design before stack

Do not begin by choosing a framework, language, UI toolkit, database, sync model, or similar foundational technology.

The project sequence is:

1. understand the product purpose and users;
2. explore realistic feature/workflow/interaction alternatives with the owner;
3. design intended behavior and experience collaboratively;
4. record approved design decisions, rejected alternatives where useful, and unresolved questions in Git;
5. only then evaluate technical stack and architecture options against those requirements;
6. explain technical alternatives, trade-offs, and recommendation;
7. obtain owner approval before consequential technical choices become project truth.

See D-0011.

## 5. Work item lifecycle

### Step A — Understand

Before implementation:

1. read the mandatory continuity files;
2. identify what is already approved;
3. identify applicable conventions;
4. identify unknown behavior or decisions;
5. explain meaningful unresolved choices to the owner.

### Step B — Explore alternatives

When a product, interaction, technical, or convention choice matters:

1. identify realistic alternatives;
2. explain practical differences and consequences;
3. make a recommendation when justified;
4. ask the owner when approval is required;
5. record the result in Git.

For conventions, once a choice is approved and recorded in `docs/CONVENTIONS.md`, do not repeatedly ask the same question unless there is a reason to change it.

### Step C — Specify/design

For a user-visible feature, record as appropriate:

- purpose;
- user role(s);
- expected behavior;
- interaction/flow;
- phone/tablet considerations;
- acceptance criteria;
- important edge cases;
- out-of-scope items;
- pending decisions.

A lightweight feature-spec template lives under `docs/templates/`.

### Step D — Implement

Only after the required behavior/design decisions are sufficiently approved for the work at hand, the coding agent may:

- write the code;
- create/update tests;
- refactor as needed within approved behavior and conventions;
- update build/configuration files;
- execute available checks;
- diagnose and repair failures.

Do not bundle unrelated behavior into the same change without a clear reason.

### Step E — Verify

Run the checks appropriate to the change. Record:

- commands/checks executed;
- what passed;
- what failed;
- what was not tested and why.

Do not describe unexecuted tests as passed.

### Step F — Update operative memory

Before presenting meaningful work as complete, update all applicable repository truth:

- `docs/PROJECT_STATE.md`;
- `docs/DECISIONS.md` if a decision changed;
- `docs/CONVENTIONS.md` if a convention was approved/changed;
- `docs/PRODUCT.md` if approved scope/design changed;
- architecture/testing docs when applicable;
- feature-specific documentation when applicable;
- known issues, rationale, unresolved questions, and next action.

A meaningful fact needed for continuation must not be left only in chat.

### Step G — Owner review

Explain the result clearly, including:

- what changed;
- why it was done that way;
- what the owner can now do or what changed in project understanding;
- what was tested;
- known limitations;
- any pending decision;
- whether the change is still on a branch or has been merged.

### Step H — Merge/publish

Merge into `main` only after owner approval or explicit delegation for that category of change.

After merge, ensure `docs/PROJECT_STATE.md` reflects the canonical merged state rather than the now-completed branch state.

## 6. Significant decision workflow

When a significant decision is required:

1. add or update a `Pending` decision entry;
2. present the owner with realistic options;
3. explain practical trade-offs;
4. give a recommendation when justified;
5. let the owner choose;
6. record the chosen option as `Approved` before relying on it as project truth.

Do not use implementation momentum as a reason to bypass this process.

## 7. Routine implementation choices

Agents may make reversible, low-impact implementation details that do not alter approved behavior and do not establish a new durable convention.

Examples may include a local helper extraction, an obvious private implementation detail, or an equivalent line-level choice.

However:

- meaningful technical approaches must still be explained;
- new durable conventions must be owner-reviewed;
- choices creating meaningful future cost, lock-in, maintenance burden, privacy implications, compatibility limits, or migration risk are not routine and must be surfaced.

See D-0008 and `docs/CONVENTIONS.md`.

## 8. Commit and PR quality

Commit messages should describe the outcome, not the chat history.

A pull request or review summary should contain:

- purpose;
- important changes;
- decisions/conventions involved;
- verification performed;
- documentation updated;
- known issues;
- owner action required.

## 9. Failed or partial work

Partial work is acceptable if clearly recorded.

If work cannot be completed in a session, `docs/PROJECT_STATE.md` must say:

- what was completed;
- what remains;
- exact failure/blocker if known;
- relevant branch/commit;
- next recommended action.

Never hide an unfinished migration, failing test, or uncertain behavior behind a generic “in progress” note.

## 10. Secrets and credentials

Never commit passwords, tokens, API keys, signing keys, private certificates, or other credentials.

If future development needs secrets, document the setup using placeholders and secure local/CI secret storage rather than putting real values in tracked files.
