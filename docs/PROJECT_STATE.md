# Project State

**Last verified:** 2026-08-30  
**Canonical branch:** `main`  
**Current working branch:** `architecture/phase2-topology`  
**Open review:** none yet  
**Phase:** Phase 2 — Technical Options and Foundation / Architecture & Technology Evaluation  
**Status:** Phase 1 is complete. Architecture evaluation is active on `architecture/phase2-topology`. Architecture decisions A-0001 through A-0008 are approved on the working branch. A-0009 (Supabase as the initial hosted application platform) is a documented proposal only and was **not approved** before the session paused. New governance rules explicitly make approvals revisable when later evidence exposes misunderstood implications or contradictions. No application code has been scaffolded and the broader architecture/technology foundation remains incomplete.

## 1. Current product baseline

`dnd_custom_aid` is a personal/small-scale tabletop RPG assistant beginning with D&D.

Approved product shape:

- Android phone/tablet is the primary at-the-table/live-use surface.
- Desktop/laptop is an intentionally narrower DM preparation/administration companion using the same shared campaign/domain data.
- A true native desktop application is an expected later product feature in addition to a web-capable desktop surface.
- The MVP is **multicampaign**.
- Paper is normally the authoritative live character surface; the latest intentionally reconciled digital character is the durable backup/reference baseline and exposes freshness/last-update information.
- Campaigns may mix D&D 5e/SRD 5.1, D&D 5.5e/SRD 5.2.1 and homebrew; the application is not a rules enforcer.
- MVP rules clarification is official-SRD-only, may use both supported SRDs, answers in Spanish, and preserves source/version provenance.
- Monster records are complete for human use while mechanics are selectively structured and future additive enrichment must remain possible.
- Live combat is local-first and DM-authoritative: DM actions commit locally first, hosted sync is secondary/opportunistic, and older remote state must not overwrite newer authoritative DM state.
- Player offline combat-view edits are provisional and yield to authoritative DM state on reconnection.
- Campaign moderation and global application administration are separate authority layers.
- Campaign invitations are campaign-scoped, reusable until revoked/regenerated, and permit direct join without a second DM approval step.
- Future evolution must permit deliberate cross-campaign NPC/creature reuse, manual Update from Library, selected automatic live links, and both PC copy and PC move/transfer between campaigns without making those capabilities accidental or forcing them into MVP now.

Detailed authoritative current product behavior lives in `docs/PRODUCT.md` and approved product decisions through D-0033 in `docs/DECISIONS.md`. Approved future-evolution constraints live in `docs/PRODUCT_EVOLUTION_REQUIREMENTS.md`.

## 2. Governance / decision-review rule

`docs/GOVERNANCE.md` is approved and mandatory.

Owner approval remains authoritative for current work, but is not treated as infallible or permanently irreversible. If a later use case, clarification, implementation discovery, or owner concern shows that an earlier approval may have been misunderstood or conflicts with another approved requirement, agents must:

- surface the contradiction rather than silently choosing;
- explain it in practical/layman terms;
- explain what the earlier decision was protecting;
- explain reversibility and likely migration/refactoring cost;
- recommend keep/amend/supersede;
- obtain owner resolution before a consequential contradictory implementation;
- preserve historical decision trace and update current truth/migration obligations.

The owner may approve a decision while acknowledging uncertainty; that approval is still valid, but future agents have an explicit duty to challenge it if later evidence exposes a problem.

## 3. Phase 1 closure

Phase 1 — Product Discovery and Design is **complete**.

PR #2 was owner-approved and merged into `main` at merge commit `b5a059b8e7fb9312232ad684356af05e27331b65`.

The final Phase 1 audit found no remaining product-level contradiction or behavioral ambiguity from the identified set at that time. D-0009 remains intentionally pending until the consequential Phase 2 architecture/technology set is sufficiently resolved.

## 4. Approved Phase 2 architecture decisions

### A-0001 — Multi-client target shape

Approved 2026-08-30.

Separate clients centered on the same durable shared domain/backend:

- dedicated Android client;
- web-capable desktop administration client;
- expected future true native desktop client.

The clients are intentionally separate product surfaces rather than one shared UI stretched across all platforms. Protocol/API details remain undecided.

### A-0002 — MVP desktop administration is local-web/localhost

Approved 2026-08-30.

The MVP desktop DM administration web client runs locally on the user's PC through a localhost application/server. It may require Internet connectivity for shared durable data. Desktop offline synchronization is not an MVP requirement. The web client should remain deployable as a hosted web application later without fundamental redesign.

### A-0003 — Native Android with Kotlin + Jetpack Compose

Approved 2026-08-30.

The Android client will be built natively in Kotlin using Jetpack Compose. Flutter, React Native and shared-UI Compose Multiplatform are not the starting approach. Kotlin Multiplatform may be reconsidered later for selective non-UI sharing only if concrete reuse justifies it.

### A-0004 — Room/SQLite for Android structured local persistence

Approved 2026-08-30.

Room, backed by SQLite, is the Android client's primary structured on-device persistence technology. It persists authoritative local live-combat state and other durable structured local/cached data. The Room schema does not need to mirror the hosted database schema.

### A-0005 — Current combat state + durable synchronization outbox

Approved 2026-08-30.

Every authoritative local combat mutation that needs remote synchronization commits the current-state change and a durable outbox operation in the same Room transaction. Outbox operations have stable identity and encounter-local ordering; the outbox is sync infrastructure, not permanent event sourcing/combat history.

### A-0006 — Single authoritative DM writer and reconnect repair

Approved 2026-08-30.

Each active encounter has one explicit authoritative DM device/session. Hosted combat accepts authoritative mutations only from that authority. Retries are ordered/idempotent, stale hosted state never automatically overwrites newer authoritative local state, and the current authority may perform an authoritative snapshot repair when operation replay cannot restore coherence. Authority does not expire merely because Internet connectivity is lost; another device cannot silently take over in MVP.

### A-0007 — Evolvable ownership/context boundaries and deliberate cross-campaign reuse

Approved 2026-08-30, explicitly subject to the normal decision-review governance if later implications prove undesirable.

Architecture keeps account/global state, campaign participation/context, reusable personal-library/source content, official/versioned source content and live-session state conceptually distinct, but these boundaries are **not permanent walls or a frozen database schema**.

Expected future feasibility includes:

- the same conceptual NPC appearing in multiple simultaneous campaigns;
- independent copies;
- linked records with explicit **Update from Library**;
- selected records that intentionally follow library changes automatically;
- campaign-specific context even for shared/canonical NPC identity;
- copying a PC into another campaign;
- moving/transferring the same PC between campaigns while retaining durable identity/continuity;
- migrations/model refinements when future requirements justify them.

Automatic cross-campaign propagation must always be intentional. Live encounters remain independent working snapshots and must not be silently rewritten by source/library/campaign definition changes.

Full rationale and consequences are in `docs/ARCHITECTURE.md`; future product expectations are in `docs/PRODUCT_EVOLUTION_REQUIREMENTS.md`.

### A-0008 — Hosted durable database engine: PostgreSQL

Approved 2026-08-30.

The shared hosted durable relational database engine is **PostgreSQL**. This selects the database engine only; it does not select Supabase, Neon, another host, authentication, API style, backend runtime, realtime transport or client access strategy.

PostgreSQL fits the project's relational/integrity needs and broad tooling/hosting ecosystem. The owner's strong SQL/PostgreSQL experience is an additional practical advantage because it reduces inspection, debugging, analysis and migration-review friction, but it is not the sole rationale.

Provider portability should remain reasonable where practical through repository-owned migrations and avoidance of unnecessary provider-specific coupling. Moving between PostgreSQL hosts should be easier than abandoning PostgreSQL entirely; the latter remains possible but may be expensive.

Detailed rationale is in `docs/architecture/A-0008_POSTGRESQL_HOSTED_DATABASE.md`.

## 5. Current technical state

No application code exists yet.

Approved foundation so far:

- target multi-client topology;
- local-web/localhost MVP desktop delivery;
- native Kotlin + Jetpack Compose Android client;
- Room/SQLite Android structured local persistence;
- hybrid combat current-state + outbox local model;
- single-authority DM combat/reconnect protocol principles;
- evolvable multicampaign/reuse domain boundaries;
- PostgreSQL as the hosted durable database engine.

Still unresolved includes:

- hosted backend/application platform/provider around PostgreSQL;
- authentication/authorization implementation;
- API/service protocol/contracts and Android networking approach;
- player public-projection transport and sync scheduling details;
- minimum Android version;
- local web implementation framework/launcher runtime details;
- future native desktop framework;
- PDF generation/rendering technology;
- SRD storage/retrieval/clarification implementation;
- build/module structure, test stack and CI.

**Supabase is currently only a proposal (A-0009), not approved.** Neon remains a serious alternative. PostgreSQL itself is approved under A-0008.

## 6. Architecture evaluation order

Current sequence:

1. **Overall multi-client topology** — approved A-0001.
2. **Android client approach** — approved A-0003.
3. **MVP desktop delivery** — approved A-0002.
4. **Android structured local persistence technology** — approved A-0004.
5. **Local combat state + synchronization outbox** — approved A-0005.
6. **Combat authority/reconnect semantics** — approved A-0006.
7. **Multicampaign/reuse domain boundaries** — approved A-0007.
8. **Hosted database engine** — PostgreSQL approved A-0008.
9. **Hosted application platform/provider + authentication/authorization/moderation + API/service approach** — active; A-0009 Supabase is proposed only.
10. Player public-projection transport and synchronization scheduling details.
11. Minimum Android version and remaining Android infrastructure choices where needed.
12. Local web implementation framework/launcher details.
13. PDF generation/rendering.
14. SRD corpus storage/retrieval/clarification and provenance.
15. Testing/build/CI and durable project/module conventions.

Some adjacent items may be discussed out of numerical order where one decision materially constrains another, but do not silently select unresolved consequential technologies.

## 7. Immediate next decision / exact resume point

The session stopped immediately after **Supabase was recommended but before the owner approved or rejected it**.

The next owner-facing architecture decision is therefore the **hosted application platform/provider around PostgreSQL**.

Current comparison state:

- **Supabase — proposed recommendation, NOT approved.** Strength: integrated PostgreSQL + Auth + RLS integration + server-side functions + Realtime can reduce MVP infrastructure/operational burden. Important limitation identified: current Free-plan projects may pause after sufficient inactivity and can require the owner to manually resume the project from the dashboard before online shared functionality returns. Provider-specific auth/RLS/realtime/functions would also increase migration cost if tightly coupled.
- **Neon — serious alternative.** Strength: PostgreSQL-first model and scale-to-zero/idle behavior are attractive for an infrequently used personal application. Trade-off: the project would likely need to assemble/host more surrounding API/auth/realtime infrastructure itself, increasing MVP complexity and maintenance burden.

Full unapproved proposal/rationale is recorded in `docs/architecture/A-0009_SUPABASE_HOSTED_PLATFORM_PROPOSAL.md`.

When resuming, current vendor documentation/free-tier behavior should be rechecked if enough time has passed for provider capabilities or pricing to change. Do **not** infer Supabase from the fact that it was recommended.

## 8. Handoff

A fresh agent should read, in order, `README.md`, `AGENTS.md`, `MANIFEST.md`, this file, `docs/GOVERNANCE.md`, `docs/DECISIONS.md`, `docs/PRODUCT.md`, `docs/PRODUCT_EVOLUTION_REQUIREMENTS.md`, `docs/ROADMAP.md`, `docs/WORKFLOW.md`, `docs/ARCHITECTURE.md`, `docs/architecture/A-0008_POSTGRESQL_HOSTED_DATABASE.md`, `docs/architecture/A-0009_SUPABASE_HOSTED_PLATFORM_PROPOSAL.md`, `docs/TESTING.md`, and relevant architecture/discovery notes only when rationale is needed.

Treat Phase 1 product decisions as closed unless a genuinely new requirement, contradiction, misunderstood implication, or implementation discovery triggers `docs/GOVERNANCE.md`. Treat A-0001 through A-0008 as approved Phase 2 architecture decisions on the active working branch. Treat A-0009 as **Proposed / NOT approved**. The broader D-0009 architecture decision remains incomplete; implementation/scaffolding must not begin yet.
