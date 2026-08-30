# Project State

**Last verified:** 2026-08-30  
**Canonical branch:** `main`  
**Current working branch:** `architecture/approved-backend-and-android`  
**Open review:** none  
**Phase:** Phase 2 — Technical Options and Foundation / Architecture & Technology Evaluation  
**Status:** Architecture evaluation is active. Hosted backend/provider topology, Android client approach, native desktop administration delivery, multicampaign domain/data boundaries, and local persistence/offline synchronization architecture are owner-approved and committed remotely; no application code has been scaffolded yet. The next owner-facing architecture decision is hosted API/data-access and authorization-enforcement boundaries.

## 1. Current product baseline

`dnd_custom_aid` is a personal/small-scale tabletop RPG assistant beginning with D&D.

Approved product shape:

- Android phone/tablet is the primary at-the-table/live-use surface.
- Desktop/laptop is a native DM preparation/administration companion using the same shared campaign/domain data and intended to permit meaningful local/offline operation.
- The MVP is **multicampaign**.
- Paper is normally the authoritative live character surface; the latest intentionally reconciled digital character is the durable backup/reference baseline and exposes freshness/last-update information.
- Campaigns may mix D&D 5e/SRD 5.1, D&D 5.5e/SRD 5.2.1 and homebrew; the application is not a rules enforcer.
- MVP rules clarification is official-SRD-only, may use both supported SRDs, answers in Spanish, and preserves source/version provenance.
- Monster records are complete for human use while mechanics are selectively structured and future additive enrichment must remain possible.
- Live combat is local-first and DM-authoritative: DM actions commit locally first, hosted sync is secondary/opportunistic, and older remote state must not overwrite newer authoritative DM state.
- Player offline combat-view edits are provisional and yield to authoritative DM state on reconnection.
- Campaign moderation and global application administration are separate authority layers.
- Campaign invitations are campaign-scoped, reusable until revoked/regenerated, and permit direct join without a second DM approval step.

Detailed authoritative behavior lives in `docs/PRODUCT.md` and approved decisions in `docs/DECISIONS.md`; D-0036, D-0037 and D-0038 are also durably checkpointed in `docs/decisions/` pending chronological-log consolidation before merge.

## 2. Approved Phase 2 architecture choices

### Hosted backend/provider topology — D-0034

- **Neon** is selected for hosted PostgreSQL as the durable shared relational database.
- **Cloudflare** is selected as the preferred stable complementary application-infrastructure platform for API/backend execution, database connectivity/pooling, object storage and realtime transport/coordination where appropriate.
- **Descope** is selected for authentication only.
- Campaign roles, ownership/control, moderation and application authorization remain project-owned in PostgreSQL/application logic.
- The initial architecture must not depend on Neon beta/preview backend features merely because they are temporarily free.
- Provider boundaries must preserve practical migration options; no provider is assumed permanent.

### Android client — D-0035

- Native **Kotlin** is selected for Android.
- **Jetpack Compose** is selected for Android UI.
- Phone and tablet layouts must be adaptive.
- Flutter and React Native are not selected for the initial app.
- Unnecessary Android coupling in reusable domain/business logic should be avoided where straightforward.

### DM desktop client — D-0036

- Native **Kotlin + Compose Multiplatform Desktop** is selected for DM desktop/laptop preparation and administration.
- Installer/update/distribution overhead is explicitly accepted by the owner and is not considered a material disadvantage.
- The owner prefers avoiding unnecessary online dependency; the desktop design should permit meaningful local/offline operation.
- Android and desktop may selectively share Kotlin domain/business/networking/synchronization code, but UI parity is neither required nor desired.

### Multicampaign domain/data boundaries — D-0037

- One shared relational PostgreSQL model is selected rather than database/schema-per-campaign tenancy.
- Internal users are global; external auth identity remains separate.
- Campaign roles are membership relationships, not global user attributes.
- Characters belong to one campaign while existence, ownership and current control remain separate relationships.
- Mutable personal reusable content and campaign content are distinct; campaign use normally creates an independent copy retaining provenance.
- Immutable/versioned official SRD content may be referenced canonically and copied when customized.
- Saved encounters, live encounters, durable character state, grouped character history and live combat state remain distinct domains.
- Mutable entities have stable globally unique identities suitable for local/offline synchronization.

### Local persistence and synchronization — D-0038

- Android and desktop use **SQLite via SQLDelight** as the durable local relational persistence layer.
- Native clients are local-first where practical and synchronize through the project-owned Cloudflare backend/API to Neon PostgreSQL.
- Authorized useful data may be cached locally for offline workflows; authorization boundaries remain intact.
- Local changes and pending outbox entries are committed atomically where applicable.
- Ordinary durable data uses stable global IDs, idempotent mutations, revision-based optimistic concurrency and deletion tombstones rather than blind last-write-wins.
- Ambiguous normal conflicts are detected for explicit reconciliation.
- Live combat uses stricter authoritative DM-device lineage/generation plus ordered sequence semantics; stale hosted state cannot overwrite newer authoritative DM state.
- Player combat projections remain non-authoritative.
- Android and desktop may share SQLDelight/domain/sync Kotlin code selectively.

D-0009 remains intentionally Pending because several consequential architecture choices remain unresolved.

## 3. Current technical state

No application code exists yet.

Approved:

- hosted database/provider: Neon PostgreSQL;
- application-infrastructure provider: Cloudflare for suitable stable services;
- authentication provider: Descope, authentication only;
- Android language/UI: Kotlin + Jetpack Compose;
- DM desktop delivery: native Kotlin + Compose Multiplatform Desktop;
- multicampaign data/domain boundary model: one shared relational PostgreSQL model with explicit global/campaign scope;
- local persistence: SQLite via SQLDelight on Android and desktop;
- sync architecture: project-owned local-first outbox/revision/tombstone synchronization, with separate DM-authoritative combat reconciliation.

Still unresolved:

- hosted API/data-access and exact authorization-enforcement boundary;
- minimum Android version;
- PDF generation/rendering technology;
- SRD storage/retrieval/clarification implementation;
- build system details, project/module layout, test stack and CI.

Implementation/scaffolding remains blocked until the required architecture set is sufficiently complete and owner-approved.

## 4. Current architecture principles

- Shared durable domain data is hosted in PostgreSQL.
- Domain data uses explicit relational global/campaign ownership rather than provider-specific tenancy or giant campaign blobs.
- Native clients use durable local SQLite persistence and should not unnecessarily depend on continuous connectivity.
- Active DM combat remains locally authoritative under D-0025/D-0033 and uses stricter synchronization semantics than ordinary durable data.
- Ordinary synchronized data must detect stale revisions rather than silently use generic last-write-wins.
- Descope identity is separate from internal application identity and domain authorization.
- Irreplaceable domain truth must not live only in Cloudflare or Descope.
- Prefer stable/GA capabilities over avoidable beta/preview dependencies.
- Prefer no-cost personal-scale operation where practical while preserving low-cost paid and migration paths.
- Share Kotlin code selectively when useful; do not force Android and desktop UI/feature parity.
- Owner-facing relational/data-model explanations should include representative SQL when useful under C-0008.
- Avoid speculative future-system complexity until a real requirement justifies it.

## 5. Architecture evaluation order

1. overall Android + desktop/laptop topology and shared-domain relationship;
2. Android client approach;
3. desktop/laptop administration delivery approach;
4. multicampaign domain/data-model boundaries;
5. local-first combat persistence, authority, synchronization and reconciliation;
6. hosted backend/database/authentication/authorization and moderation boundaries;
7. PDF generation/rendering;
8. SRD corpus storage/retrieval/clarification and provenance;
9. testing/build/CI and durable project/module conventions.

Items 2, 3, 4, 5 and the provider portion of item 6 are now approved. The current next decision is the remaining hosted API/data-access and authorization-enforcement portion of item 6.

## 6. Immediate next decision

Evaluate **hosted API/data-access and authorization-enforcement boundaries**.

The evaluation must determine:

- whether native clients ever connect directly to Neon or all remote database interaction flows through Cloudflare;
- how Descope tokens are verified and mapped to internal application users;
- where campaign membership/role, PC ownership/control, moderation and global-account-freeze authorization are enforced;
- whether PostgreSQL Row Level Security is used, and if so as primary authorization or defense in depth;
- how the server accepts/rejects idempotent offline outbox mutations and stale revisions safely;
- how privileged application-administration operations are isolated from normal campaign-DM authority;
- what database credentials/roles are held by Cloudflare/backend components and what privileges they receive;
- how the design remains inspectable/testable and reasonably portable away from current providers.

No native client should receive privileged database credentials merely for convenience.

## 7. Durable checkpoint

The approved backend/provider, Android-client, native-desktop, multicampaign-domain and SQLDelight/local-first synchronization decisions are committed to the remote branch `architecture/approved-backend-and-android`. This branch remains the active safety checkpoint for the current Phase 2 architecture session until review/merge.

## 8. Handoff

A fresh agent should read, in order, `README.md`, `AGENTS.md`, `MANIFEST.md`, this file, `docs/DECISIONS.md`, `docs/PRODUCT.md`, `docs/ROADMAP.md`, `docs/WORKFLOW.md`, `docs/ARCHITECTURE.md`, `docs/TESTING.md`, `docs/CONVENTIONS.md`, and the dedicated files in `docs/decisions/` that have not yet been consolidated into the chronological decision log.

Treat Phase 1 product decisions as closed unless a genuinely new requirement or contradiction emerges. Continue Phase 2 from hosted API/data-access and authorization-enforcement boundaries. Consequential choices remain owner-controlled.
