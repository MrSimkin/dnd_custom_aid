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

**ACTIVE — FOUNDATION + CREATURE + NPC + LIGHTWEIGHT HOMEBREW/RULE + PLACE PAYLOADS INTEGRATED; NEXT BOUNDED PACKAGE TO BE SELECTED AFTER PLACE DOC CLOSURE.**

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

PR #53 integrated lightweight Homebrew/Rule payload persistence:

- self-contained rule/ruling/custom-system payload;
- summary/body, optional category/rationale, examples, related references, tags and optional notes;
- lifecycle `DRAFT / ACTIVE / RETIRED`;
- Personal/Campaign create/read/copy/update/tombstone behavior;
- independent campaign copies with retained provenance;
- SQLDelight `homebrew_rule_payload` persistence and migration `21.sqm` with metadata-only backfill;
- explicit list serialization plus stale-write/non-resurrection, migration and database-reopen coverage;
- bounded synthetic legacy Desktop fixture update.

PR #53 merged as `fc870fe8303b0f9925c479bdc21c388ef5cc8450`; push Scaffold `35256210643`, PR Scaffold `35256230033`, and post-merge Scaffold `35256531651` passed.

PR #55 integrated Place payload persistence:

- canonical reusable Place data using `ReusableContentFamily.PLACE`;
- `PlaceKind.PLACE` plus `PlaceKind.SHOP`, preserving Shops as specialized Places rather than a separate top-level system;
- summary, area/geographic context, function/purpose, presentation text, services/interactives, hooks, player-safe text, DM-only notes, paper references and tags;
- Personal/Campaign create/read/copy/update/tombstone behavior;
- independent campaign copies with retained provenance;
- SQLDelight `place_payload` persistence and migration `22.sqm` with metadata-only Place backfill;
- explicit list serialization plus stale-write/non-resurrection, migration and database-reopen coverage;
- bounded synthetic legacy Desktop fixture update.

PR #55 merged as `d978a4191054227a03b32ecca3e7ceadc5d6e869`; push Scaffold `35258393882`, PR Scaffold `35258423034`, and post-merge Scaffold `35259027937` passed.

The next bounded Wave 6 implementation package is intentionally **not selected by this documentation closure**. After the Place closure is integrated, re-read current D-0072/D-0073 authority and select one dependency-safe package from the integrated state rather than inferring it from branch order.

Still deliberately outside the completed payload packages unless a later bounded scope explicitly requires them:

- large Manager UI;
- hosted reusable-content sync;
- object storage/provider activation;
- import/export and AI helper workflows;
- generalized relationship/dependency graphs before a concrete need;
- automatic reveal/publication behavior;
- clocks/media semantics not yet required by a selected domain;
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
