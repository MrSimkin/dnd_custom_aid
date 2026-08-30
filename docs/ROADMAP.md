# Roadmap

This roadmap defines development stages, not a fixed feature list. Product content within each stage remains subject to owner approval.

## Phase 0 — Project Foundation

**Status:** Complete. Merged through PR #1 on 2026-08-28.

---

## Phase 1 — Product Discovery and Design

**Status:** Complete. Approved product/MVP baseline merged through PR #2 on 2026-08-29.

---

## Phase 2 — Technical Foundation

**Goal:** select proportionate architecture, scaffold it, verify it, and make the repository ready for incremental feature development.

**Status:** **Complete.** Architecture checkpoint merged through PR #3; audited minimal scaffold merged through PR #4 on 2026-08-30.

The canonical scaffold provides:

- one shared Kotlin Multiplatform module;
- Android Jetpack Compose application shell;
- Compose Multiplatform Desktop application shell;
- SQLDelight local database foundation and smoke test;
- TypeScript Cloudflare Worker shell;
- PostgreSQL migration/data-loading area;
- one simple GitHub Actions workflow;
- documented setup/build/test commands.

C-0009 remains controlling: no speculative Cloudflare services, provider abstractions, sync platform, realtime transport, or other infrastructure without a concrete feature need.

---

## Phase 3 — First Vertical Slice

**Goal:** implement one small real user workflow end-to-end to prove the foundation without prematurely activating the whole architecture.

**Status:** **Current.** First slice selected: local Android campaign creation and active-campaign selection.

### Slice boundary

The slice should prove:

- a real Android Material 3 workflow;
- shared Kotlin domain/data behavior;
- SQLDelight persistence;
- locally durable active-campaign selection.

Initial behavior:

1. show locally stored campaigns;
2. create a campaign using a nonblank name;
3. persist it locally;
4. select a campaign as active;
5. persist active selection across app restart;
6. allow duplicate display names because identity is by stable ID.

Explicitly excluded from this slice:

- rename/delete;
- invites, membership and roles;
- Descope/authentication;
- Neon/hosted sync;
- realtime infrastructure;
- desktop campaign UI;
- characters, NPCs, encounters or combat;
- PDF and SRD features.

### Why this slice first

Campaign selection is already approved MVP behavior and forms a useful parent boundary for later records. It proves UI + shared logic + local persistence without forcing hosted infrastructure or the much larger character/combat models into the first feature.

### Phase 3 exit criterion

A real approved campaign create/select task works end-to-end on Android with focused persistence tests and documented behavior.

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
