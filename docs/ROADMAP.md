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

**NEXT / ACTIVE DIRECTION.**

Establish durable reusable-content persistence before rich Managers. Reuse existing Shared `ContentScope`, provenance, revision and tombstone primitives.

First package should provide a bounded reusable-content envelope/catalog, local SQLDelight persistence/migration, explicit Personal -> Campaign independent copy, optimistic update/tombstone/non-resurrection behavior, listing by scope/family and invariant/migration tests.

Do not force Monster/NPC/Homebrew/Place/Encounter payloads into one giant universal model. Do not add large Manager UI, hosted sync or object storage to the first package unless implementation proves they are strictly required.

### Wave 7 — Desktop authoring Managers

Implement Monster/Creature Creator, NPC, Homebrew & Rules, Stage/Place, Dungeon/Zone, Encounter, PC Manager/Audit and Media/Handouts workflows on the Wave 6 foundation.

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