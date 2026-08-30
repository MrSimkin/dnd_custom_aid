# Roadmap

This roadmap defines development stages, not a fixed feature list. Product content within each stage remains subject to owner approval.

## Phase 0 — Project Foundation

**Goal:** make the repository safe for long-running, multi-chat, multi-agent development.

**Current status:** Complete. Owner-approved foundation merged to `main` via PR #1 on 2026-08-28.

---

## Phase 1 — Product Discovery and Design

**Goal:** understand and design what the first useful version should actually do before selecting the technology stack.

**Current status:** **Complete.** The approved product/MVP baseline was merged to canonical `main` via PR #2 on 2026-08-29.

The result includes multicampaign scope, paper/digital authority, Android/desktop surface responsibilities, local-first DM combat authority, moderation/invitation semantics, NPC/monster/encounter direction, and sufficient product clarity to evaluate architecture.

Do not reopen resolved Phase 1 questions merely because historical discovery notes describe earlier uncertainty; reopen only for a genuinely new requirement or contradiction.

---

## Phase 2 — Technical Options and Foundation

**Goal:** evaluate technical alternatives against the approved product/design baseline, obtain owner approval for consequential choices, then scaffold the chosen architecture.

**Current status:** **Architecture selection, consolidation and pre-main proportionality review complete; explicit owner merge authorization is the remaining gate before scaffolding.**

### Architecture selection — complete

Owner-approved decisions D-0034 through D-0043 establish:

- Neon PostgreSQL + Cloudflare + Descope provider topology;
- native Android Kotlin + Jetpack Compose;
- native Kotlin + Compose Multiplatform Desktop DM administration;
- Android 11 / API 30 minimum;
- explicit relational multicampaign domain boundaries;
- SQLDelight/SQLite local persistence on Android/desktop;
- deliberately small project-owned synchronization;
- Cloudflare Worker/API as the hosted gateway, with proportional rather than enterprise-grade authorization/security machinery;
- local/offline PDF export on Android and desktop;
- PostgreSQL full-text retrieval over official Spanish SRD 5.1 / SRD 5.2.1 with an initially Cloudflare Workers AI answer layer;
- a small shared/Android/desktop/backend/database implementation shape;
- TypeScript for the Cloudflare backend;
- focused tests and one simple GitHub Actions CI workflow.

The pre-main proportionality audit clarified implementation without changing the selected stack:

- Desktop uses local **Save + explicit Sync**;
- DM combat uses one authoritative device + increasing sequence/version; cross-device authority generations are deferred;
- player offline combat convenience is only ephemeral local Next-turn/visible-condition state and is discarded on reconnect;
- ordinary HTTP/polling comes before realtime infrastructure;
- provider-specific code is localized without generalized provider abstraction frameworks;
- offline capability is selective rather than universal.

C-0009 governs implementation: this is a personal/small-scale project, so choose the simplest safe solution and add complexity only for a concrete need.

### Owner merge gate — current

The architecture branch has completed its consolidation/contradiction review and is represented by PR #3.

Before scaffolding:

1. verify the final remote branch/PR head remains unchanged from the reviewed state;
2. obtain explicit owner authorization to merge PR #3 into `main` under D-0007;
3. merge only after that authorization.

This is a governance gate, not another architecture-discovery round.

### Scaffolding — next

After the architecture branch is accepted into `main`, create the approved minimal foundation:

- reproducible Kotlin/Gradle project;
- shared Kotlin logic/data module or equivalent standard structure;
- Android application shell;
- Desktop application shell;
- SQLDelight local database foundation;
- TypeScript Cloudflare Worker/backend shell;
- PostgreSQL migration/data-loading area;
- baseline focused automated tests;
- one simple CI workflow;
- developer/agent setup/build instructions.

Do **not** scaffold R2, Durable Objects, WebSockets, queues, generalized provider abstractions, a background desktop synchronization platform, or other deferred infrastructure merely because it may be useful someday.

### Phase 2 exit criterion

A fresh agent can clone the repository, follow documented commands, build the approved application foundation and run baseline tests, with the technical foundation traceable to approved product/design needs.

---

## Phase 3 — First Vertical Slice

**Goal:** implement one small end-to-end feature that proves the architecture and product workflow.

The feature itself must come from approved scope and have explicit observable acceptance criteria before it is considered complete.

The combat tracker remains the most important live-table MVP validation surface, but the exact first vertical slice should be selected after scaffolding so it proves the most useful cross-cutting risk rather than being chosen arbitrarily.

### Exit criterion

A real approved user task works end-to-end on representative configurations, with tests and documented behavior.

---

## Phase 4 — MVP Buildout

**Goal:** implement the remaining approved MVP scope incrementally.

Prefer complete, testable slices over many half-finished screens. Each feature should have explicit acceptance criteria and leave project-state documentation current.

---

## Phase 5 — MVP Hardening

**Goal:** make the first release dependable enough for real use.

Potential areas include, only as applicable:

- regression testing;
- real phone/tablet usability checks;
- desktop administration checks;
- multicampaign navigation/isolation;
- cross-surface shared-data behavior;
- local-first combat/offline/reconnection behavior;
- verification that ephemeral player offline turn/condition changes are discarded on reconnect and never affect DM authority;
- accessibility review;
- data migration/recovery;
- performance where observed to matter;
- backup/export behavior;
- crash handling;
- proportionate privacy/security review;
- packaging/release process.

---

## Phase 6 — Post-MVP Evolution

**Goal:** add features based on owner priorities and actual usage while preserving continuity and compatibility.

Possible later directions already distinguished from MVP include broader Android/desktop feature parity, player desktop access, desktop combat tracking, co-DMs, explicit DM-device combat handoff, house-rule-aware clarification, realtime transport if actually needed, and other approved future expansions.

Every significant expansion should go through the same sequence: alternatives/discussion → decision/design → specification → implementation → testing → Git continuity update → owner review.
