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

### Waves 1–5

Waves 1–4 are complete/integrated for their recorded scope. Wave 5 Desktop shell + Campaign Administration is complete, owner-QA accepted and integrated, including real DEV deployment/verification. Do not restart historical repair/deployment cycles without new evidence.

### Wave 6 — reusable/persistent content architecture

**ACTIVE — FOUNDATION + CREATURE + NPC PAYLOADS INTEGRATED; LIGHTWEIGHT HOMEBREW/RULE CORE NEXT.**

PR #46 integrated the reusable-content persistence foundation:

- family/catalog metadata for Creature, NPC, Homebrew/Rule, Place, Zone and Encounter;
- Personal/Campaign creation/listing;
- explicit Personal -> Campaign independent copy with retained provenance;
- optimistic revision/tombstone/non-resurrection behavior;
- SQLDelight migration `18.sqm`;
- recognized Wave-5 Desktop migration/fail-closed unknown-unversioned handling;
- focused invariant/migration/reopen tests.

PR #46 merged as `013abbb9e57af0ba04fe1e8b678e8ed29522bedd`; post-merge Scaffold `35220099721` passed.

PR #48 integrated Creature payload persistence:

- human-complete, selectively structured Creature payload;
- local SQLDelight persistence linked to reusable-content identity;
- Personal/Campaign create/read/copy/update/tombstone behavior;
- migration `19.sqm` with metadata-only Creature backfill;
- focused invariant/migration/reopen tests.

PR #48 merged as `5762058645ba8af8fa470dcf185bd2b418af65e9`; post-merge Scaffold `35253188344` passed.

PR #51 integrated NPC payload persistence:

- Quick and Developed NPCs both valid without mandatory combat mechanics;
- human-facing narrative/dossier payload with no completion score;
- optional full combat mechanics reusing the existing `CreaturePayload` model;
- Personal/Campaign create/read/copy/update/tombstone behavior;
- independent campaign copies with retained provenance;
- SQLDelight `npc_payload` persistence and migration `20.sqm` with metadata-only NPC backfill;
- stale-write/non-resurrection, migration and database-reopen coverage;
- bounded synthetic legacy Desktop fixture update.

PR #51 merged as `1aebc6d6b769d0da59b4dfd6e13a1ce52ccbb99a`; push Scaffold `35254216489`, PR Scaffold `35254260799`, and post-merge Scaffold `35254527744` passed.

The next bounded Wave 6 package is **lightweight Homebrew/Rule payload + local persistence core** using `ReusableContentFamily.HOMEBREW_RULE`.

The initial payload should cover the self-contained rule/ruling/custom-system form approved in D-0072: summary, rich body, optional category/rationale, examples, related references, tags and `DRAFT / ACTIVE / RETIRED` lifecycle. It should reuse existing Personal/Campaign identity, provenance, copy, revision and tombstone seams.

Do not use this package to flatten structured races/classes/subclasses/backgrounds/feats/spells/items into generic text. Those deserve appropriate domain structure in later bounded packages. Likewise, defer Place/Zone/Encounter reference/dependency graphs until those domains require them.

Still deliberately outside current Wave 6 payload packages:

- large Manager UI;
- hosted reusable-content sync;
- object storage/provider activation;
- import/export and AI helper workflows;
- a universal executable content payload model.

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
