# Roadmap

This roadmap defines the current dependency-driven implementation sequence. Detailed behavior remains controlled by approved decisions and current checkpoints.

## Foundation and integrated baseline

Phases 0–3, Phase 4A Player foundation, and Waves 1–5 of the integrated MVP are complete for their recorded scope.

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

**COMPLETE FOR CORE REUSABLE-CONTENT ARCHITECTURE / INTEGRATED.**

Integrated packages:

- reusable-content foundation — PR #46;
- Creature payload — PR #48;
- NPC payload — PR #51;
- lightweight Homebrew/Rule payload — PR #53;
- Place payload — PR #55;
- Zone payload — PR #57;
- Encounter payload — PR #59.

The integrated spine preserves stable identity, Personal/Campaign scope, independent Personal -> Campaign copies with retained provenance, optimistic revisions/stale-write rejection, tombstones/non-resurrection and SQLDelight migration/reopen behavior.

PR #59 completes the six reserved reusable families with saved Encounter preparation, Creature/NPC participant references, freeform participants, `EXPECTED / RESERVE / CONDITIONAL` readiness, encounter-local overrides and domain-specific dependency copy/remap/deduplication.

PR #59 merged as `81303bf875457bd1fa0a9ce70d7a4e71eaad9edd`; push Scaffold `35264310753`, PR Scaffold `35264620723`, and post-merge Scaffold `35265162945` passed.

Wave 6 completion does not pre-model all future persistent concepts. Add Scene Spine, richer structured Homebrew families, clocks/readiness, Media/Handouts and live-state models only when their concrete approved package requires them.

### Wave 7 — Desktop authoring Managers

**ACTIVE NEXT.**

First bounded package:

#### Desktop Creature/Monster Manager — local authoring core

Use the existing Desktop `MANAGERS` destination and integrated Creature repository/payload.

Initial scope:

- browse/search Personal Creatures;
- browse/search active-Campaign Creatures when a campaign is active;
- create Personal or active-Campaign Creatures;
- open/edit the existing human-complete Creature payload;
- show scope and provenance;
- explicitly copy Personal Creature -> active Campaign;
- preserve revision/stale-write/tombstone semantics.

Deliberately defer from this first slice:

- Official/SRD catalog browsing;
- import/export;
- Creature Creator Assistant/advisory design helpers;
- media/object storage;
- hosted reusable-content sync;
- universal all-domain Manager abstractions.

Later Wave 7 packages implement the remaining approved authoring surfaces from D-0072: NPC, Homebrew & Rules, Stage/Place/Scene, Dungeon/Zone, Encounter, PC Manager/Audit and Media/Handouts. Family-specific supporting persistence may be added with the concrete Manager that needs it.

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

Exercise representative Player + Server + DM flows end-to-end, including campaign/invite/PC sync, authored Creature/NPC/Place/Zone/Encounter content, live tablet play, Desktop authority resume, public combat projection, PC audit/correction, backup/export and official-SRD clarification.

## Security across waves

Maintain fail-closed authorization, object-level authorization, replay/idempotency safety, stale-write/tombstone guarantees, error/log hygiene and least privilege. Known owner-local 3 high-severity npm findings require deliberate package/reachability review; never run `npm audit fix --force` blindly.

## Git/provider rule

Use short-lived outcome branches from current `main`; integrate shared foundations early when later work depends on them. Routine safe green boundaries do not require owner confirmation. When a required provider action is inaccessible, finish safe repo work and stop at one bounded owner handoff instead of retrying access paths.
