# Roadmap

This roadmap defines development stages and current integration direction. Product content remains controlled by approved decisions and owner authorization.

## Phase 0 — Project Foundation

**Status:** Complete.

---

## Phase 1 — Product Discovery and Design

**Status:** Foundational pass complete; targeted product design continues where the expanded MVP requires it.

---

## Phase 2 — Technical Foundation

**Status:** Foundational architecture selected; hosted implementation not yet fully activated.

Approved base remains Kotlin/Compose Android, Kotlin + Compose Multiplatform Desktop, genuinely shared Kotlin logic/data where useful, SQLDelight/SQLite local persistence, TypeScript Cloudflare Worker/API, Neon PostgreSQL and Descope authentication.

C-0009 remains controlling: add complexity only for concrete requirements.

---

## Phase 3 — First Vertical Slice

**Status:** Complete.

---

## Phase 4A — Player Character Foundation

**Status:** substantial implementation complete / automation-green candidate exists / physical revalidation and final integration reconciliation remain.

Authoritative Player branch:

`implementation/phase4a-successor-cycle`

Current frozen physical candidate according to that branch's 2026-09-14 checkpoint:

- `0.4.0-preqa.13 / 41300`;
- commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — SUCCESS;
- targeted cross-device physical revalidation pending.

Existing Player evidence remains valid for the exact tested boundaries. Do not restart completed repair history or discard useful QA merely because the next build becomes broader.

The 2026-09-14 architecture discussion also retained four bounded defect labels as planning input (A10, B1, J1, J2). Before implementation, reconcile those labels against the then-current authoritative successor checkpoint rather than treating chat labels as stronger evidence than the branch.

---

## Phase 4B — Integrated MVP Build

**Status:** architecture/product scope being finalized; implementation not yet authorized by D-0071 alone.

D-0071 changes the next-cycle philosophy. The project will not treat a tiny server slice or isolated local DM prototype as the next major product milestone.

The target is the first coherent integrated MVP:

```text
Player Android <-> hosted/shared services <-> DM tablet/Desktop
```

Implementation is organized through internal dependency waves and parallel workstreams, with continuous engineering integration. These are not separate owner-acceptance products.

### Workstream A — shared MVP spine/contracts

Define/stabilize common domain meaning, IDs, revisions, network/API semantics, permissions, asset references and local-vs-hosted state.

### Workstream B — Player stabilization and hosted integration

Preserve the mature Player foundation, close/reconcile remaining bounded defects, and add remembered authentication, campaign selection, hosted PC sync, assets, audit/history/conflicts and public combat projection without gratuitous Player rewrites.

### Workstream C — hosted foundation

Implement PostgreSQL migrations/schema, Cloudflare API, Descope identity mapping/verification, domain authorization, sync endpoints, object storage, audit/recovery and full backup/export. Begin SRD storage/provenance foundations early.

### Workstream D — shared Kotlin data/sync

Evolve SQLDelight/local persistence, outbox/pending mutations, revisions, tombstones, idempotency, conflict handling, asset references and reconciliation logic genuinely shared by Android/Desktop.

### Workstream E — DM tablet/Desktop product

Implement the approved DM Workspace/Desk family and the expanded DM Desktop App.

Current live Desk family remains:

1. DM Screen;
2. Stage Desk;
3. Dungeon Desk;
4. Combat Desk.

Desktop must provide functional equivalents of all four Desks so a laptop can replace the tablet operationally when needed.

Desktop also provides richer authoring/management, explicitly including Monster Creator/Manager, NPC Creator/Manager, Homebrew Rules Input/Manager, Zone Creator/Manager, Encounter Creator/Manager, PC Manager/Audit, campaign administration and a distinct system-administration area.

### Workstream F — live-play exchange and combat authority

Implement local-first authoritative DM combat, hosted opportunistic sync, Player public projection, stale-update rejection, reconnect behavior and explicit resume/handoff to another DM device from the latest synchronized state.

Exactly one DM device remains authoritative at a time. Simultaneous authoritative multi-device editing is not an MVP requirement.

### Workstream G — SRD retrieval + AI clarification

SRD schema/provenance/loading foundation begins earlier in hosted work. The actual PostgreSQL retrieval -> grounded LLM -> Spanish Player/DM answer feature is deliberately the **last substantial user-facing feature implemented in this cycle**.

MVP AI remains official SRD 5.1 / 5.2.1 only even though Homebrew Rules input/storage exists in the Desktop App.

### Cross-cutting MVP foundations

- remembered login/trusted-device behavior;
- campaign-scoped roles and multicampaign selection;
- project-specific sync/revisions/idempotency/tombstones;
- visible sync/freshness/conflict state;
- object storage — required, provider still Pending;
- meaningful audit/history and object-appropriate recovery;
- full server backup/export early enough to protect valuable hosted state;
- scoped offline/local-first behavior where required;
- ordinary HTTP/request-response and polling before generalized realtime.

### Internal implementation waves

- **Wave 0:** protect/reconcile current Player state; backend/Desktop scaffolding may start in parallel;
- **Wave 1:** establish shared semantic/data/API spine;
- **Wave 2:** heavy parallel Player/Server/Desktop/shared implementation with frequent real integration;
- **Wave 3:** combat/public-projection/device-authority integration;
- **Wave 4:** finish administration/recovery edges and SRD retrieval/AI, then create the full integrated QA candidate.

The workstreams/waves are development organization, not separate MVP scope gates.

---

## Integrated MVP QA gate

The next major owner-facing product acceptance QA is intended to exercise **Player + Server + DM together**.

Representative end-to-end coverage should include:

- remembered login, campaign membership/switching and role behavior;
- Player local Save and PC/assets/history synchronization;
- authorized DM PC retrieval/audit/correction without ownership confusion;
- DM preparation/content/campaign-copy workflows;
- saved encounter -> independent live encounter;
- local-first combat and public Player projection;
- offline/reconnect behavior;
- stale revision/conflict/tombstone behavior;
- explicit DM-device combat resume from the latest synchronized state;
- full server backup/export;
- official-SRD grounded rules clarification for Player and DM.

Internal automated/integration checks still run throughout implementation. This final integrated gate does not mean deferring testing until the end.

---

## Current product-design substage before coding

The project is intentionally paused after the 2026-09-14 consolidation.

Current resume point:

### 7D — DM Desktop App detailed product definition

Define the Desktop App's overall navigation/structure and then walk area-by-area through:

- all live DM Desks;
- Monster/NPC/Homebrew Rules/Zone/Encounter authoring/management;
- Stage/Dungeon data authoring counterparts as required;
- PC Manager/Audit;
- campaign/member/permission administration;
- Sync/recovery/backup/system-administration surfaces.

Do not reopen generic Desk taxonomy: D-0068/D-0069 already define the current family.

Then:

### 7E — exact outside-MVP boundary

Only after the expanded Desktop/MVP product is sufficiently defined should the owner/assistant draw the complete post-MVP boundary.

After 7E, derive the final Git/development topology, concrete implementation gates and explicit coding authorization.

---

## Explicitly deferred/generalized directions unless a concrete need changes them

Current MVP does not automatically require:

- always-online play or a formal technical Session object;
- generalized realtime/WebSockets infrastructure;
- Durable Objects/equivalent server-room coordination;
- queues without a real long-running/asynchronous job;
- arbitrary/general ACL framework;
- generalized synchronization platform;
- simultaneous authoritative multi-device DM combat editing;
- public/community homebrew publishing;
- house-rule-aware AI clarification;
- VTT/rules-engine behavior;
- Player desktop application/full Player UI parity;
- elaborate history-retention/cold-storage machinery.

This list is not the final 7E boundary.

---

## Post-MVP evolution

Future evolution remains evidence/priority driven. Candidate areas may include broader sharing/delegation, co-DMs, richer restore tooling, house-rule-aware clarification, realtime transport if polling proves inadequate, broader desktop/Player parity, Journey-specific behavior if Dungeon mode proves insufficient, and other owner-approved expansions.

## Branch/continuity rule

`docs/BRANCH_STATUS.md` controls branch authority/lifecycle. `docs/checkpoints/LATEST.md` controls the practical global resume point.

Current Player runtime remains on `implementation/phase4a-successor-cycle`; current integrated-MVP/DM design truth belongs on `main` after this documentation checkpoint is merged. Future integration must preserve both lines and requires explicit planning/authorization.