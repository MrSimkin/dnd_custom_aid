# AGENTS.md — Mandatory Project Operating Rules

This file applies to every human contributor, ChatGPT conversation, coding agent, autonomous agent, or other AI working in this repository.

## 1. Repository authority

The repository is the project's durable source of truth.

Do not rely on chat memory, hidden context, previous conversations, personal memory, or assumptions to determine the current state of the project. Those may be used only as clues; if they conflict with the repository, the repository wins until the owner explicitly decides otherwise.

The canonical published branch is `main`.

## 2. Mandatory read order before work

Before proposing or making changes, read:

1. `README.md`
2. `AGENTS.md`
3. `docs/PROJECT_STATE.md`
4. `docs/DECISIONS.md`
5. `docs/PRODUCT.md`
6. `docs/ROADMAP.md`
7. `docs/WORKFLOW.md`
8. Any architecture, testing, or feature-specific documents relevant to the task

Do not start implementation from a stale branch or from remembered context.

## 3. Owner authority and decision boundary

The project owner makes the final decisions about the product.

An agent may:

- explain options in plain language;
- recommend a preferred option and explain trade-offs;
- implement approved behavior;
- make routine low-level coding choices required to implement an approved design when they do not materially change product behavior;
- run tests, diagnose failures, refactor safely, and improve internal code quality;
- identify missing decisions, contradictions, risks, or ambiguities.

An agent must obtain owner approval before locking in a **significant** decision involving:

- product scope or features;
- player/DM behavior or permissions;
- navigation or important UX flows;
- data ownership, sync, backup, accounts, networking, or privacy;
- monetization;
- external services or recurring costs;
- compatibility targets that materially affect users;
- game-system rules or content semantics;
- significant architecture or technology choices that would be expensive to reverse;
- destructive migrations or deletion of user data.

When unsure whether a decision is significant, document it as pending and present it to the owner.

## 4. No silent invention

Do not silently invent requirements.

Clearly distinguish:

- **Approved** — explicitly decided by the owner and recorded in the repository.
- **Proposed** — recommended but not approved.
- **Pending** — needs an owner decision.
- **Implemented** — present in code and verified.

If implementation requires an unknown product behavior, prefer a reversible placeholder or stop at the decision boundary rather than embedding an assumption as fact.

## 5. Change workflow

For substantial work:

1. Verify the current state.
2. Identify applicable approved decisions.
3. Record any new pending decision before implementation if needed.
4. Work on a non-canonical branch unless the owner explicitly asks for a direct `main` change.
5. Implement the smallest coherent change.
6. Run the relevant checks and tests.
7. Update documentation and project state in the same change.
8. Summarize what changed, what was tested, what remains unresolved, and any owner decision needed.
9. Merge to `main` only after the owner approves the change or has explicitly delegated that category of change.

## 6. Definition of done

A change is not complete merely because code was written.

For a change to be considered complete:

- implementation is present;
- relevant automated checks pass, or failures are explicitly documented;
- behavior has been checked at the appropriate level;
- documentation reflects the new reality;
- `docs/PROJECT_STATE.md` is current;
- important new decisions are in `docs/DECISIONS.md`;
- unresolved items are visible and not hidden in chat history.

## 7. Continuity rule

At the end of every meaningful work session, leave the repository in a state where a new agent can answer these questions without reading the old chat:

- What is this project?
- What has been decided?
- What exists now?
- What is currently being worked on?
- What was last tested?
- What is broken or uncertain?
- What decision is needed next?
- What should be done next?

The primary file for this handoff is `docs/PROJECT_STATE.md`.

## 8. Technical quality

Prefer maintainable, readable, testable code over clever code. Keep dependencies justified. Avoid secrets in the repository. Do not commit generated credentials, API keys, signing keys, local machine paths, or personal tokens.

Before adding a dependency or service that materially affects maintenance, privacy, cost, or lock-in, document the choice and obtain approval when required by Section 3.

## 9. Recovery from inconsistency

If repository documents disagree:

1. Do not guess silently.
2. Identify the contradiction.
3. Prefer the most specific approved decision over general prose.
4. Prefer newer explicitly dated state over older state when both have equal authority.
5. Ask the owner to resolve material ambiguity.
6. Record the resolution in the repository.

## 10. Current project stage

The project is currently in foundation/discovery. Do not assume an Android framework, language, persistence layer, sync model, game ruleset, UI structure, or feature list until those choices are explicitly approved and recorded.
