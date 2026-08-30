# Roadmap

This roadmap defines development stages, not a fixed feature list. Product content within each stage remains subject to owner approval.

## Phase 0 — Project Foundation

**Goal:** make the repository safe for long-running, multi-chat, multi-agent development.

**Status:** Complete. Merged to `main` through PR #1 on 2026-08-28.

---

## Phase 1 — Product Discovery and Design

**Goal:** understand/design the first useful version before selecting technology.

**Status:** Complete. Approved product/MVP baseline merged to `main` through PR #2 on 2026-08-29.

---

## Phase 2 — Technical Foundation

**Goal:** select proportionate architecture, scaffold it, verify it, and make the repository ready for incremental feature development.

**Status:** **Scaffold implementation complete and CI-verified on `implementation/initial-scaffold`; review/merge is the remaining Phase 2 gate.**

### Architecture selection — complete

D-0034 through D-0043 establish the approved provider/client/data/testing baseline. The 2026-08-30 C-0009 proportionality audit further requires:

- Desktop local Save + explicit Sync;
- one authoritative DM combat device + sequence/version;
- ephemeral player offline turn/condition convenience only;
- HTTP/polling before realtime;
- localized provider code rather than generalized abstractions;
- selective offline capability;
- no speculative Cloudflare services or synchronization platform.

Architecture checkpoint merged through PR #3 on 2026-08-30.

### Scaffolding — implemented and verified

The focused scaffold branch now provides:

- reproducible root Gradle configuration;
- one shared Kotlin Multiplatform module;
- Android Jetpack Compose application shell;
- Compose Multiplatform Desktop application shell;
- SQLDelight local database foundation and smoke test;
- TypeScript Cloudflare Worker shell;
- PostgreSQL migration/data-loading area;
- one simple GitHub Actions workflow;
- documented setup/build/test commands.

The final scaffold code head `335cf523785a7f503186acd1057ebce72c121b27` passed the Kotlin/Android/Desktop/SQLDelight and backend CI jobs.

### Phase 2 exit criterion

Phase 2 closes when the verified scaffold is accepted into canonical `main` and repository status documentation reflects that merge.

---

## Phase 3 — First Vertical Slice

**Goal:** implement one small real user workflow end-to-end to prove the foundation without prematurely activating the whole architecture.

The first slice should:

- be useful on its own;
- exercise meaningful shared domain logic and local persistence;
- produce a real UI interaction on at least one intended client surface;
- have explicit observable acceptance criteria;
- avoid hosted/auth/realtime complexity unless the workflow genuinely needs it;
- remain small enough to diagnose architecture mistakes cheaply.

Candidate slices should be compared after scaffold acceptance. A local-first character-record slice is a strong candidate because it can prove shared Kotlin + SQLDelight + real UI without requiring authentication, Neon, Descope, or realtime infrastructure immediately. The final slice selection remains a product/implementation decision, not an automatic consequence of this roadmap.

### Exit criterion

A real approved user task works end-to-end on representative configuration(s), with focused tests and documented behavior.

---

## Phase 4 — MVP Buildout

**Goal:** implement remaining approved MVP scope incrementally.

Prefer complete testable slices over many half-finished screens. Introduce hosted sync/auth/provider integrations only when the corresponding slice needs them.

---

## Phase 5 — MVP Hardening

**Goal:** make the first release dependable enough for real use.

Potential areas, only as observed/needed:

- regression testing;
- real phone/tablet usability;
- desktop administration workflows;
- multicampaign isolation;
- local-first/offline/reconnection behavior;
- PDF/export verification;
- accessibility;
- data migration/recovery;
- performance where measured to matter;
- crash handling;
- proportionate privacy/security review;
- packaging/release process.

---

## Phase 6 — Post-MVP Evolution

**Goal:** add features based on actual priorities/usage while preserving continuity.

Possible later directions include broader Android/desktop parity, player desktop, desktop combat, co-DMs, explicit DM-device combat handoff, house-rule-aware clarification, realtime transport if proven useful, and other owner-approved expansions.
