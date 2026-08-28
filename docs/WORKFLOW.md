# Development and Review Workflow

This file defines the proposed operating workflow for AI-led implementation with owner-controlled significant decisions.

## 1. Guiding principle

The repository should always make a clear distinction between:

- accepted project state;
- work in progress;
- proposed decisions;
- unresolved questions.

The goal is to prevent a future agent from confusing an experiment or chat suggestion with an approved product decision.

## 2. Proposed branch model

- `main` = canonical accepted/published project state.
- Substantial changes = developed on a focused branch.
- Small administrative corrections may be made directly only when the owner has explicitly allowed that category of change.
- Experiments must not be treated as canonical until merged.

This branch model is currently **proposed**, not yet owner-approved. See D-0007 in `docs/DECISIONS.md`.

## 3. Work item lifecycle

### Step A — Understand

Before coding:

1. read the mandatory continuity files;
2. identify what is already approved;
3. identify unknown behavior or decisions;
4. explain any significant unresolved choice to the owner in plain language.

### Step B — Specify

For a user-visible feature, record:

- purpose;
- user role(s);
- expected behavior;
- acceptance criteria;
- important edge cases;
- out-of-scope items;
- pending decisions.

A lightweight feature-spec template lives under `docs/templates/`.

### Step C — Implement

The coding agent may:

- write the code;
- create/update tests;
- refactor as needed within the approved behavior;
- update build/configuration files;
- execute available checks;
- diagnose and repair failures.

Do not bundle unrelated behavior into the same change without a clear reason.

### Step D — Verify

Run the checks appropriate to the change. Record:

- commands/checks executed;
- what passed;
- what failed;
- what was not tested and why.

Do not describe unexecuted tests as passed.

### Step E — Update continuity

Before presenting the change as complete:

- update `docs/PROJECT_STATE.md`;
- update `docs/DECISIONS.md` if a decision changed;
- update `docs/PRODUCT.md` if approved scope changed;
- update architecture/testing docs when applicable;
- ensure the next action is explicit.

### Step F — Owner review

Explain the result in nontechnical language first, then technical detail if useful.

The owner should be told:

- what changed;
- what they can now do;
- what was tested;
- known limitations;
- any decision needed;
- whether the change is still on a branch or has been merged.

### Step G — Merge/publish

Merge into `main` only after the owner approves the change or has explicitly delegated that category of change.

After merge, update state documentation if the branch name/current-work status changed.

## 4. Significant decision workflow

When a significant decision is required:

1. add or update a `Pending` decision entry;
2. present the owner with the practical options;
3. explain the trade-offs without requiring technical expertise;
4. give a recommendation when one is justified;
5. let the owner choose;
6. record the chosen option as `Approved` before relying on it as project truth.

## 5. Routine implementation choices

The proposed default is that agents may choose reversible, low-impact implementation details that do not alter approved behavior. Examples include variable names, small internal refactors, helper functions, formatting, or equivalent implementation mechanics.

If a technical choice creates meaningful future cost, lock-in, maintenance burden, privacy implications, compatibility limits, or migration risk, it is not routine and should be surfaced as a decision.

This boundary is currently **proposed**. See D-0008.

## 6. Commit and PR quality

Commit messages should describe the outcome, not the chat history.

A pull request or review summary should contain:

- purpose;
- important changes;
- decisions involved;
- verification performed;
- documentation updated;
- known issues;
- owner action required.

## 7. Failed or partial work

Partial work is acceptable if clearly recorded.

If work cannot be completed in a session, `docs/PROJECT_STATE.md` must say:

- what was completed;
- what remains;
- exact failure/blocker if known;
- relevant branch/commit;
- next recommended action.

Never hide an unfinished migration, failing test, or uncertain behavior behind a generic “in progress” note.

## 8. Secrets and credentials

Never commit passwords, tokens, API keys, signing keys, private certificates, or other credentials.

If future development needs secrets, document the setup using placeholders and secure local/CI secret storage rather than putting the real value in tracked files.
