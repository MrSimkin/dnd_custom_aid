# AGENTS.md — Mandatory Project Operating Rules

This file applies to every human contributor, ChatGPT conversation, coding agent, autonomous agent, or other AI working in this repository.

## 1. Repository authority and operative memory

The repository is the project's durable source of truth **and its operative memory**.

Do not rely on chat memory, hidden context, previous conversations, personal memory, or assumptions to determine the current state of the project. Those may be used only as clues; if they conflict with the repository, the repository wins until the owner explicitly decides otherwise.

Any information needed for another chat, AI, agent, or human to continue the project must be written to the repository. Important decisions, conventions, rationale, current work, unresolved questions, verification results, known problems, and next actions must not exist only in chat history.

The canonical accepted branch is `main`.

## 2. Mandatory read order before work

Before proposing or making changes, read:

1. `README.md`
2. `AGENTS.md`
3. `MANIFEST.md`
4. `docs/PROJECT_STATE.md`
5. `docs/DECISIONS.md`
6. `docs/CONVENTIONS.md`
7. `docs/PRODUCT.md`
8. `docs/ROADMAP.md`
9. `docs/WORKFLOW.md`
10. `docs/ARCHITECTURE.md`
11. `docs/TESTING.md`
12. relevant detailed files under `docs/decisions/` when deeper architecture rationale is needed
13. any feature-specific documents relevant to the task

Do not start implementation from a stale branch or from remembered context.

## 3. Owner authority and working relationship

The project owner makes the final decisions about the product and consequential project conventions/architecture.

The owner understands programming concepts but is not a professional software developer and is delegating the heavy technical execution to AI/coding agents. Do not force the owner to perform implementation work that the agent can reasonably perform.

An agent may:

- explain options and consequences clearly;
- recommend a preferred option and explain why;
- implement approved behavior;
- make routine low-level coding choices required to carry out an approved design, subject to the communication and convention rules below;
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

## 4. Explain technical work; do not silently disappear into implementation

The agent doing the technical heavy lifting must keep the owner informed.

Before or while carrying out meaningful technical work, explain in proportionate detail:

- what is being changed;
- why it is being changed;
- what the important technical approach is;
- what alternatives matter, if any;
- what consequences or trade-offs the owner should know about.

Do not hide a meaningful technical choice merely because it is implementation-level. The distinction is:

- the agent may execute routine work without asking permission for every line of code;
- the owner should still understand the approach being taken and why;
- when a choice establishes a convention or has meaningful future consequences, discuss it with the owner before treating it as settled.

Explanations should respect the owner's programming knowledge: be clear and educational without assuming professional software-engineering experience and without unnecessarily oversimplifying.

C-0008 additionally requires representative SQL when it materially improves the owner's understanding of relational/data-model behavior.

## 5. Conventions are owner-reviewed and stored in Git

When a coding, naming, structure, formatting, testing, documentation, branching, or similar convention first becomes relevant and no approved convention already exists:

1. identify the convention choice;
2. explain the realistic alternatives and practical consequences;
3. give a recommendation;
4. ask the owner to approve or modify it;
5. record the approved convention in the repository before relying on it as durable project truth.

Once an approved convention is recorded, agents should follow it without repeatedly asking the same question. Re-open the discussion only when there is a concrete reason to change the convention.

Routine choices that do not establish a durable convention may be made by the agent, but they must still comply with approved conventions and the communication rule above.

### Personal-scale proportionality

C-0009 is controlling: this is a personal, deliberately limited project. Prefer the **simplest safe implementation that satisfies actual approved requirements**. Do not import enterprise/SaaS architecture, security layers, observability, test ceremony, deployment machinery, generalized abstractions, or speculative scale infrastructure merely because they are common industry patterns.

Concrete current examples:

- do not activate Cloudflare services merely because Cloudflare provides them;
- use HTTP/request-response and simple polling before realtime infrastructure;
- use Desktop local Save + explicit Sync rather than building a synchronization platform;
- keep one authoritative DM device + increasing combat sequence for MVP rather than pre-building cross-device authority generations;
- player offline combat convenience is ephemeral local UI state, not another synchronization domain;
- localize vendor-specific code rather than building provider abstraction frameworks;
- support offline operation only where it provides real value.

Add complexity only for a concrete requirement, observed problem, or real risk.

## 6. Design before technology; approved architecture is now the baseline

The project followed this sequence before implementation:

1. understand the product problem and intended users;
2. explore relevant product and interaction alternatives with the owner;
3. design the intended behavior and experience with the owner;
4. record approved product/design decisions and unresolved questions;
5. evaluate technical stack/architecture options against that approved design;
6. explain those technical options, trade-offs, and recommendations;
7. obtain owner approval before locking in consequential stack/architecture choices.

That architecture-selection sequence is complete for the initial scaffold under D-0034 through D-0043, including the 2026-08-30 proportionality clarifications.

Do **not** reopen approved architecture merely because historical discovery/decision prose describes an earlier uncertainty. Follow the current `docs/DECISIONS.md`, `docs/ARCHITECTURE.md`, `docs/PRODUCT.md`, `docs/CONVENTIONS.md`, and `docs/PROJECT_STATE.md` baseline.

Future genuinely consequential architecture changes still require owner approval. Routine reversible implementation details may be selected under D-0008 and approved conventions.

## 7. No silent invention

Do not silently invent requirements.

Clearly distinguish:

- **Approved** — explicitly decided by the owner and recorded in the repository.
- **Proposed** — recommended but not approved.
- **Pending** — needs an owner decision.
- **Implemented** — present in code and verified.

If implementation requires an unknown product behavior, prefer a reversible placeholder or stop at the decision boundary rather than embedding an assumption as fact.

## 8. Change workflow

For substantial work:

1. Verify the current state.
2. Identify applicable approved decisions and conventions.
3. Discuss meaningful alternatives or missing conventions with the owner when required.
4. Record any new pending decision before implementation if needed.
5. Work on a non-canonical branch unless the owner explicitly asks for a direct `main` change.
6. Implement the smallest coherent change.
7. Run the relevant checks and tests.
8. Update documentation and operative memory in the same change.
9. Summarize what changed, why, what was tested, what remains unresolved, and any owner decision needed.
10. Merge to `main` only after the owner explicitly approves that merge or has explicitly delegated that category of merge.

## 9. Definition of done

A change is not complete merely because code was written.

For a change to be considered complete:

- implementation is present;
- relevant automated checks pass, or failures are explicitly documented;
- behavior has been checked at the appropriate level;
- documentation reflects the new reality;
- `docs/PROJECT_STATE.md` is current;
- important new decisions are durably recorded;
- approved conventions are recorded in the appropriate repository documentation;
- unresolved items are visible and not hidden in chat history.

## 10. Continuity rule

At the end of every meaningful work session, leave the repository in a state where a new agent can answer these questions without reading the old chat:

- What is this project?
- What has been decided?
- Which conventions are approved?
- What exists now?
- What is currently being worked on?
- Why was the current approach chosen?
- What was last tested?
- What is broken or uncertain?
- What decision is needed next?
- What should be done next?

The primary current-state handoff file is `docs/PROJECT_STATE.md`; other authoritative details live in the files identified by `MANIFEST.md`.

## 11. Technical quality

Prefer maintainable, readable, testable code over clever code. Keep dependencies justified. Avoid secrets in the repository. Do not commit generated credentials, API keys, signing keys, local machine paths, or personal tokens.

Before adding a dependency or service that materially affects maintenance, privacy, cost, or lock-in, document the choice and obtain approval when required by Section 3.

Technical quality must remain proportionate under C-0009. "More architecture" is not automatically "better architecture."

## 12. Recovery from inconsistency

If repository documents disagree:

1. Do not guess silently.
2. Identify the contradiction.
3. Prefer the most specific later approved decision/clarification over older general prose.
4. Prefer newer explicitly dated current-state documentation when authority is otherwise equal.
5. Use detailed `docs/decisions/` records for rationale, but the chronological `docs/DECISIONS.md` plus later approved amendments/clarifications control the current decision state.
6. Ask the owner only for a genuinely material ambiguity not already resolved by an approved decision.
7. Record the resolution in the repository.

## 13. Current project stage

**Phase 1 — Product Discovery and Design is complete.** The approved product/MVP baseline is canonical on `main` after owner-approved PR #2.

**Phase 2 — foundational architecture selection/consolidation is complete and canonical on `main`.** Owner-approved PR #3 merged D-0034 through D-0043 and the 2026-08-30 proportionality clarifications. No application code has been scaffolded yet.

The current task is the **initial implementation scaffold**. Substantial scaffold work should begin on a focused non-`main` branch under Section 8 unless the owner explicitly requests direct `main` work.

Start with the smallest scaffold necessary to prove the foundation. Do not begin another broad architecture-discovery round or activate speculative infrastructure unless a genuinely new requirement, contradiction, or expensive-to-reverse choice appears.
