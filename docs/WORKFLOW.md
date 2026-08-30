# Development and Review Workflow

This file defines the approved operating workflow for AI-led implementation with owner-controlled decisions, revisable approvals, active contradiction detection, and Git-based operative memory.

## 1. Guiding principle

The repository must always make a clear distinction between:

- accepted project state;
- work in progress;
- proposed decisions;
- unresolved questions;
- approved conventions;
- verification actually performed.

The goal is to prevent a future agent from confusing an experiment, chat suggestion, unrecorded convention, or remembered discussion with approved project truth.

Approved project truth is authoritative for current work, but approvals may be deliberately reviewed/amended/superseded under `docs/GOVERNANCE.md` when later evidence shows a contradiction, misunderstood implication, or materially better understanding.

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

For consequential technical/architecture decisions, include the practical behavior, a concrete example when useful, what becomes easier/harder, whether the choice can later be changed, and the likely kind of migration/refactoring cost if it is changed.

## 4. Product/design before stack

Do not begin by choosing a framework, language, UI toolkit, database, sync model, or similar foundational technology.

The project sequence is:

1. understand the product purpose and users;
2. explore realistic feature/workflow/interaction alternatives with the owner;
3. design intended behavior and experience collaboratively;
4. record approved design decisions, rejected alternatives where useful, and unresolved questions in Git;
5. only then evaluate technical stack and architecture options against those requirements;
6. explain technical alternatives, trade-offs, reversibility/migration consequences, and recommendation;
7. obtain owner approval before consequential technical choices become project truth.

See D-0011 and `docs/GOVERNANCE.md`.

## 5. Work item lifecycle

### Step A — Understand

Before implementation:

1. read the mandatory continuity files;
2. identify what is already approved;
3. identify approved future evolution requirements relevant to the work;
4. identify applicable conventions;
5. check the new request/use case against existing approved decisions for contradictions or unintended restrictions;
6. identify unknown behavior or decisions;
7. explain meaningful unresolved choices to the owner.

### Step B — Explore alternatives

When a product, interaction, technical, or convention choice matters:

1. identify realistic alternatives;
2. explain practical differences and consequences;
3. explain reversibility and likely correction/migration cost when material;
4. make a recommendation when justified;
5. ask the owner when approval is required;
6. record the result in Git.

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

If implementation reveals that an approved decision has a previously unexplained consequence or conflicts with an approved requirement, stop at the consequential boundary and trigger the decision-review procedure rather than burying the conflict in code.

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
- `docs/GOVERNANCE.md` if governance rules changed;
- `docs/DECISIONS.md` if a recorded product/project decision changed;
- `docs/CONVENTIONS.md` if a convention was approved/changed;
- `docs/PRODUCT.md` and/or approved product-evolution records if scope/design direction changed;
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
- whether any earlier approval had to be amended/superseded;
- whether the change is still on a branch or has been merged.

### Step H — Merge/publish

Merge into `main` only after owner approval or explicit delegation for that category of change.

After merge, ensure `docs/PROJECT_STATE.md` reflects the canonical merged state rather than the now-completed branch state.

## 6. Significant decision workflow

When a significant decision is required:

1. identify/record the pending choice;
2. present the owner with realistic options;
3. explain practical trade-offs and important assumptions;
4. explain whether/how the choice can be changed later and the likely migration/refactoring cost when material;
5. give a recommendation when justified;
6. let the owner choose;
7. record the chosen option as `Approved` before relying on it as project truth.

An owner may approve while acknowledging uncertainty. The approval is still authoritative for current work; the uncertainty does not waive the future contradiction/review duties in `docs/GOVERNANCE.md`.

Do not use implementation momentum as a reason to bypass this process.

## 7. Decision review / contradiction lifecycle

Trigger this process when:

- the owner says an earlier decision was confusing or may have been misunderstood;
- a new concrete use case conflicts with an earlier approval;
- implementation reveals a hidden consequence that materially changes the decision;
- two approved rules cannot both be satisfied;
- later product direction makes an earlier structural restriction undesirable.

Then:

1. identify the affected approval(s);
2. explain the contradiction in ordinary practical terms;
3. explain what the earlier choice was protecting;
4. explain the effect and cost of changing it, including schema/data migrations, compatibility work, or major refactoring where applicable;
5. recommend keep, amend, or supersede;
6. obtain the owner's resolution before making a consequential contradictory change;
7. preserve the historical decision record while clearly marking the new controlling rule;
8. update current product/architecture/state documentation and record any migration obligation.

Never respond to a genuine contradiction with only “but this was already approved.”

## 8. Routine implementation choices

Agents may make reversible, low-impact implementation details that do not alter approved behavior and do not establish a new durable convention.

Examples may include a local helper extraction, an obvious private implementation detail, or an equivalent line-level choice.

However:

- meaningful technical approaches must still be explained;
- new durable conventions must be owner-reviewed;
- choices creating meaningful future cost, lock-in, maintenance burden, privacy implications, compatibility limits, or migration risk are not routine and must be surfaced.

See D-0008 and `docs/CONVENTIONS.md`.

## 9. Commit and PR quality

Commit messages should describe the outcome, not the chat history.

A pull request or review summary should contain:

- purpose;
- important changes;
- decisions/conventions involved;
- verification performed;
- documentation updated;
- known issues;
- migrations/refactors required by superseded decisions, if any;
- owner action required.

## 10. Failed or partial work

Partial work is acceptable if clearly recorded.

If work cannot be completed in a session, `docs/PROJECT_STATE.md` must say:

- what was completed;
- what remains;
- exact failure/blocker if known;
- relevant branch/commit;
- next recommended action.

Never hide an unfinished migration, failing test, or uncertain behavior behind a generic “in progress” note.

## 11. Secrets and credentials

Never commit passwords, tokens, API keys, signing keys, private certificates, or other credentials.

If future development needs secrets, document the setup using placeholders and secure local/CI secret storage rather than putting real values in tracked files.
