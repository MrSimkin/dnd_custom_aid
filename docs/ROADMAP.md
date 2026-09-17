# Roadmap

This roadmap defines the current dependency-driven implementation sequence. Detailed behavior remains controlled by approved decisions and current checkpoints.

## Foundation and integrated baseline

Phases 0–3, Phase 4A Player foundation, and Waves 1–3 of the integrated MVP are complete for their recorded scope.

Approved technical foundation remains Kotlin/Compose Android, Kotlin + Compose Multiplatform Desktop, SQLite/SQLDelight local persistence, Ktor Client, TypeScript Cloudflare Worker/API, Neon PostgreSQL and Descope identity proof. Object-storage provider selection remains deferred until Media/Handouts/assets require it.

## Phase 4B — Integrated MVP Build

**Status:** IN PROGRESS — owner implementation authorization granted.

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Internal waves are engineering controls, not separate products.

### Wave 1 — integrated baseline convergence

**COMPLETE / INTEGRATED.**

### Wave 2 — Shared Integrated-MVP Spine

**COMPLETE / INTEGRATED.**

Includes identity/account, Campaign, Membership/role, PC owner/controller distinction, stable IDs, revision/stale-write semantics, tombstones/non-resurrection, scope/provenance and sync metadata primitives.

### Wave 3 — hosted foundation

**COMPLETE / INTEGRATED / REAL DEV VERIFIED.**

Includes Worker/API, PostgreSQL contracts, Ktor transport, token seam, durable outbox, campaign lifecycle, PC snapshots, application authorization, idempotency/revisions/conflicts/tombstones and real DEV provider activation.

### Wave 4 — Player <-> Server end-to-end

**COMPLETE / INTEGRATED for recorded scope.**

Remembered Android auth, hosted campaign bootstrap/delivery, PC snapshot push/pull, multi-client convergence/conflicts and membership authorization gates are integrated. Do not restart historical repair cycles without new evidence.

### Wave 5 — Desktop shell + Campaign Administration

**COMPLETE / OWNER-QA ACCEPTED / INTEGRATED.**

- Desktop workbench/local campaign — PR #42 merged/owner-QA accepted;
- hosted Campaign membership administration core — PR #43 merged;
- Desktop hosted authentication + Campaign Administration — PR #44 merged as `306377df1a453f531af4b670d2b231c88a3c9419`;
- real DEV deployment and owner Windows QA passed;
- post-merge Scaffold `35168920031` passed.

Do not redeploy the Worker merely because documentation advances.

### Wave 6 — reusable/persistent content architecture

**ACTIVE — FOUNDATION + CREATURE PAYLOAD INTEGRATED; NPC PAYLOAD CORE NEXT.**

PR #46 integrated the bounded reusable-content persistence foundation on top of the existing Shared `ContentScope`, provenance, revision and tombstone primitives. It includes:

- reusable-content family/catalog metadata for Creature, NPC, Homebrew/Rule, Place, Zone and Encounter;
- Personal and Campaign creation/listing;
- explicit Personal -> Campaign independent copy with retained provenance;
- optimistic revision/tombstone/non-resurrection behavior;
- local SQLDelight persistence and migration `18.sqm`;
- safe Desktop migration from the recognized unversioned Wave-5 schema and fail-closed refusal of unknown unversioned databases;
- invariant, migration and Desktop reopen/migration tests.

PR #46 merged as `013abbb9e57af0ba04fe1e8b678e8ed29522bedd`; post-merge Scaffold `35220099721` passed.

PR #48 then integrated the first domain-specific payload package:

- human-complete, selectively structured Creature payload;
- local SQLDelight Creature payload persistence linked to reusable-content identity;
- Personal/Campaign Creature creation/read;
- explicit Personal -> Campaign independent copy/provenance;
- payload mutation through existing revision/stale-write/tombstone semantics;
- migration `19.sqm` with safe metadata-only Creature backfill;
- focused persistence/invariant tests;
- bounded repair of the synthetic legacy Desktop migration fixture, with production migration logic unchanged.

PR #48 merged as `5762058645ba8af8fa470dcf185bd2b418af65e9`; post-merge Scaffold `35253188344` passed.

The next bounded Wave 6 package is **NPC payload + local persistence core**. It should preserve Quick/Developed NPC validity without mandatory combat mechanics and reuse the Creature/stat-block machinery when full combat mechanics are present.

Still deliberately outside these integrated packages:

- large Manager UI;
- hosted reusable-content sync;
- object storage/provider activation;
- import/export and AI helper workflows;
- a universal executable content payload model.

Domain-specific payloads should continue to build on the foundation rather than being forced into one giant universal abstraction.

### Wave 7 — Desktop authoring Managers

Implement Monster/Creature Creator, NPC, Homebrew & Rules, Stage/Place, Dungeon/Zone, Encounter, PC Manager/Audit and Media/Handouts workflows on the relevant Wave 6 foundations.

Select/activate object storage only when Media/Handouts/assets actually require it, after a fresh `$0` review.

### Wave 8 — DM Live Workspace

Implement DM Screen, Stage Desk, Dungeon Desk and Combat Desk on Android/tablet and Desktop with shared semantics and platform-appropriate UX.

### Wave 9 — live combat exchange

Implement local-first single-device combat authority, hosted exchange/public projection, stale-update rejection and explicit tablet/Desktop authority resume/handoff.

### Wave 10 — SRD retrieval + grounded clarification

Complete official SRD 5.1/5.2.1 retrieval and grounded Player/DM clarification. Workers AI remains conditional on safe `$0` operation. Homebrew-aware AI remains post-MVP.

### Wave 11 — backup/recovery/operator completion

Complete verifiable server backup/export and meaningful recovery/admin tooling.

### Wave 12 — integrated owner-facing QA

Exercise representative Player + Server + DM flows end-to-end.

## Security across waves

Maintain fail-closed authorization, object-level authorization, replay/idempotency safety, stale-write/tombstone guarantees, error/log hygiene and least privilege. Known owner-local 3 high-severity npm findings require deliberate package/reachability review; never run `npm audit fix --force` blindly.

## Git/provider rule

Use short-lived outcome branches from current `main`; integrate shared foundations early. When a required provider action is inaccessible, finish safe repo work and stop at one bounded owner handoff instead of retrying access paths.
