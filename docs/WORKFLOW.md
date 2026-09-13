# Development and Review Workflow

This file defines the approved operating workflow for AI-led implementation with owner-controlled decisions and Git-based operative memory.

## 1. Guiding principle

The repository must always make a clear distinction between:

- approved/accepted project state;
- implemented but not owner-accepted state;
- work in progress;
- proposed decisions;
- unresolved questions;
- approved conventions;
- verification actually performed;
- branch role/lifecycle.

The goal is to prevent a future agent from confusing an experiment, chat suggestion, historical checkpoint, branch name, green CI result or remembered discussion with current approved project truth.

## 2. Current branch model

Current branch lifecycle is controlled by `docs/BRANCH_STATUS.md`.

There are presently **two active authoritative lines**:

- `main` — canonical global navigation plus Phase 5A/DM product discovery/design;
- `implementation/phase4a-successor-cycle` — authoritative Player/Phase 4A runtime, QA and defect-repair line.

They intentionally contain different valid work and must not be mechanically collapsed.

Rules:

- do not assume `main` contains the latest Player runtime;
- do not assume the Player successor supersedes later `main`-only DM discovery;
- do not force-move either active ref over the other;
- all other surviving branches are historical/audit/frozen evidence unless `docs/BRANCH_STATUS.md` explicitly changes their lifecycle;
- frozen QA refs remain immutable;
- any future integration of the two active lines requires explicit owner authorization and must preserve both sets of valid work.

D-0007 remains the historical general branching decision. D-0066 remains part of the repository-ordering history. Later explicit continuity records control the current dual-line topology.

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

### Step A — Establish authority

Before implementation:

1. read the mandatory continuity files;
2. identify the correct active authoritative branch from `docs/BRANCH_STATUS.md`;
3. read that branch's `docs/PROJECT_STATE.md` and `docs/checkpoints/LATEST.md`;
4. identify the owner's actual authorization boundary;
5. identify what is already approved/implemented/accepted;
6. identify applicable conventions and material unknowns.

Do not begin from a historical branch or stale checkpoint merely because its old prose says “next.”

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

Only after the required behavior/design decisions are sufficiently approved and the active branch/authorization are clear, the coding agent may:

- write the code;
- create/update tests;
- refactor as needed within approved behavior and conventions;
- update build/configuration files;
- execute available checks;
- diagnose and repair failures.

Do not bundle unrelated behavior into the same change without a clear reason.

Do not invent work merely because an owner/manual gate temporarily blocks further implementation.

### Step E — Verify

Run the checks appropriate to the change. Record:

- commands/checks executed;
- exact revision/build tested;
- what passed;
- what failed;
- what was not tested and why;
- whether evidence is automated, emulator/simulator, or physical owner/device evidence.

Do not describe unexecuted tests as passed. Do not infer owner acceptance from CI.

### Step F — Update operative memory

Before presenting meaningful work as complete, update all applicable repository truth:

- `docs/PROJECT_STATE.md`;
- `docs/checkpoints/LATEST.md` when the practical resume point changes;
- `docs/BRANCH_STATUS.md` when branch lifecycle/authority changes;
- `docs/DECISIONS.md` or detailed decision records if a decision changed;
- `docs/CONVENTIONS.md` if a convention was approved/changed;
- `docs/PRODUCT.md` if approved scope/design changed;
- roadmap/architecture/testing docs when applicable;
- feature-specific documentation/checkpoints when applicable;
- known issues, rationale, unresolved questions, and next action.

A meaningful fact needed for continuation must not be left only in chat.

### Step G — Owner review

Explain the result clearly, including:

- what changed;
- why it was done that way;
- what the owner can now do or what changed in project understanding;
- what was tested;
- known limitations;
- any pending decision/manual gate;
- which active branch contains the work;
- whether cross-line integration is needed or intentionally deferred.

### Step H — Publish/integrate

Publish commits to the correct active authoritative line for the work.

- Player/Phase 4A runtime, QA packaging and QA-reopened defect repairs belong on `implementation/phase4a-successor-cycle` unless a later explicit decision changes that authority.
- Global navigation and Phase 5A/DM discovery/design belong on `main`.
- A continuity/governance correction that affects both active lines may be committed to both when explicitly authorized.
- Do **not** merge one active line into the other merely for cosmetic linearity.
- Cross-line integration requires explicit owner authorization and an evidence-based reconciliation that preserves both valid histories.

## 6. Significant decision workflow

When a significant decision is required:

1. add or update a `Pending` decision entry when appropriate;
2. present the owner with realistic options;
3. explain practical trade-offs;
4. give a recommendation when justified;
5. let the owner choose;
6. record the chosen option as `Approved` before relying on it as project truth.

Do not use implementation momentum as a reason to bypass this process.

## 7. Routine implementation choices

Agents may make reversible, low-impact implementation details that do not alter approved behavior and do not establish a new durable convention.

However:

- meaningful technical approaches must still be explained;
- new durable conventions must be owner-reviewed;
- choices creating meaningful future cost, lock-in, maintenance burden, privacy implications, compatibility limits, or migration risk are not routine and must be surfaced.

See D-0008 and `docs/CONVENTIONS.md`.

## 8. Commit quality

Commit messages should describe the outcome, not the chat history.

A review/checkpoint summary should contain:

- purpose;
- important changes;
- decisions/conventions involved;
- verification performed;
- documentation updated;
- known issues;
- owner action/manual evidence required;
- active branch and exact continuation point.

## 9. Failed or partial work

Partial work is acceptable if clearly recorded.

If work cannot be completed in a session, operative-memory docs must say:

- what was completed;
- what remains;
- exact failure/blocker if known;
- relevant branch/commit;
- next recommended action.

Never hide an unfinished migration, failing test, uncertain behavior, or unresolved manual gate behind a generic “in progress” note.

## 10. Current Phase 4A gate

The current Player candidate is `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7`, automation-green under Scaffold run `34726572588`.

P1–P16 are implemented/automation-qualified. P17 is the physical Player-tablet QA gate policy.

Until physical owner/device QA produces new evidence:

- do not restart P1–P16;
- do not invent unrelated Player features;
- a real QA defect may reopen only the relevant accepted repair boundary on the Player successor branch;
- DM discovery/design may continue on `main` when requested;
- DM feature implementation remains blocked until explicit Phase 4A owner closure.

## 11. Secrets and credentials

Never commit passwords, tokens, API keys, production/release signing keys, private certificates, or other credentials.

If future development needs secrets, document setup using placeholders and secure local/CI secret storage rather than putting real values in tracked files.
