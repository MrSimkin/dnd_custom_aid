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
5. `docs/GOVERNANCE.md`
6. `docs/DECISIONS.md`
7. `docs/PRODUCT.md`
8. `docs/PRODUCT_EVOLUTION_REQUIREMENTS.md`
9. `docs/ROADMAP.md`
10. `docs/WORKFLOW.md`
11. Any architecture, testing, convention, or feature-specific documents relevant to the task

Do not start implementation from a stale branch or from remembered context.

## 3. Owner authority and working relationship

The project owner makes the final decisions about the product and about project conventions.

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

Owner approval is authoritative but **revisable**. It must not be treated as proof that every hidden implication was understood or that the decision can never be reconsidered. Follow `docs/GOVERNANCE.md` whenever a later use case, clarification, implementation discovery, or owner concern suggests that an earlier approval may be wrong, misunderstood, or inconsistent with another approved requirement.

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

For significant technical/architecture decisions, explain practical meaning, a concrete example where useful, what the choice enables or restricts, whether it can be changed later, and the likely kind of migration/refactoring cost if reversed. Do not rely on unexplained specialist implications when asking the owner to approve a consequential choice.

## 5. Conventions are owner-reviewed and stored in Git

When a coding, naming, structure, formatting, testing, documentation, branching, or similar convention first becomes relevant and no approved convention already exists:

1. identify the convention choice;
2. explain the realistic alternatives and practical consequences;
3. give a recommendation;
4. ask the owner to approve or modify it;
5. record the approved convention in the repository before relying on it as durable project truth.

Once an approved convention is recorded, agents should follow it without repeatedly asking the same question. Re-open the discussion only when there is a concrete reason to change the convention.

Routine choices that do not establish a durable convention may be made by the agent, but they must still comply with approved conventions and the communication rule above.

## 6. Design before technology stack

Do not choose or scaffold the application technology stack merely because development could start.

The required order is:

1. understand the product problem and intended users;
2. explore relevant product and interaction alternatives with the owner;
3. design the intended behavior and experience with the owner;
4. record approved product/design decisions and unresolved questions;
5. only then evaluate technical stack/architecture options against that approved design;
6. explain those technical options, trade-offs, and recommendation to the owner;
7. obtain approval before locking in consequential stack/architecture choices.

No Android framework, language, UI toolkit, persistence layer, sync architecture, service, or comparable foundational technology may be treated as selected before this sequence reaches the appropriate decision point.

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
2. Identify applicable approved decisions, evolution requirements, governance rules, and conventions.
3. Check whether the new request/use case contradicts or materially strains an existing approved decision.
4. Discuss meaningful alternatives or missing conventions with the owner when required.
5. Record any new pending decision before implementation if needed.
6. Work on a non-canonical branch unless the owner explicitly asks for a direct `main` change.
7. Implement the smallest coherent change.
8. Run the relevant checks and tests.
9. Update documentation and operative memory in the same change.
10. Summarize what changed, why, what was tested, what remains unresolved, and any owner decision needed.
11. Merge to `main` only after the owner approves the change or has explicitly delegated that category of change.

## 9. Definition of done

A change is not complete merely because code was written.

For a change to be considered complete:

- implementation is present;
- relevant automated checks pass, or failures are explicitly documented;
- behavior has been checked at the appropriate level;
- documentation reflects the new reality;
- `docs/PROJECT_STATE.md` is current;
- important new decisions are in the appropriate decision/architecture records;
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

## 12. Recovery from inconsistency and misunderstood approvals

If repository documents disagree, a new requirement conflicts with an approved decision, or the owner indicates that an earlier approval may have been based on incomplete understanding:

1. Do not guess silently.
2. Identify the contradiction or affected earlier decision.
3. Explain the conflict in practical/layman terms and state what the earlier decision was intended to protect.
4. Prefer the most specific approved decision over general prose only when no genuine reconsideration is needed.
5. Do **not** automatically prefer an older approval when newer product evidence shows that it may be wrong or misunderstood.
6. Explain reversibility and likely migration/refactoring cost.
7. Recommend keep/amend/supersede as appropriate.
8. Ask the owner to resolve material ambiguity or approve the correction.
9. Record the resolution and any migration obligation in the repository.

See `docs/GOVERNANCE.md` for the mandatory review procedure.

## 13. Current project stage

**Phase 1 — Product Discovery and Design is complete.** The approved product/MVP baseline, including the final multicampaign scope and eight product-tension resolutions, is canonical on `main` after owner-approved PR #2.

**Phase 2 — Architecture & Technology Evaluation is active** on the focused architecture branch identified in `docs/PROJECT_STATE.md`. Architecture sub-decisions already approved on that branch are recorded in `docs/ARCHITECTURE.md`; the broader D-0009 foundation remains incomplete.

Architecture evaluation is authorized; application implementation/scaffolding is **not** yet authorized. Do not treat any unresolved hosted provider, database, authentication service, API style, PDF library, AI/retrieval approach, native-desktop framework, or other consequential technology as selected until the alternatives have been explained, the owner has approved the choice, and the decision is recorded in Git.

Do not reopen already approved Phase 1 product questions merely because older discovery notes describe them as unresolved. However, **do** reopen an approval when a genuinely new requirement, contradiction, misunderstood implication, or implementation consequence triggers the review rules in `docs/GOVERNANCE.md`.
