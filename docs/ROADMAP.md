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

Wave 6 completion does not pre-model all future persistent concepts. Add Scene Spine, richer structured Homebrew families, clocks/readiness, Media/Handouts and live-state models only when their concrete approved package requires them.

### Wave 7 — Desktop authoring Managers

**ACTIVE.**

#### Desktop Creature/Monster Manager — local authoring core

**COMPLETE / INTEGRATED — PR #61.**

Integrated behavior:

- existing Desktop `MANAGERS` destination opens a usable Creature/Monster Manager;
- browse/search Personal and active-Campaign Creatures;
- create Personal or active-Campaign Creatures;
- open/edit the human-complete Creature payload;
- show scope, provenance and revision;
- explicitly copy Personal Creature -> active Campaign as an independent object;
- preserve stale-write/tombstone semantics with atomic name + payload update;
- keep active campaign context visible while authoring.

Validation:

- final implementation head `38d68dc832188f29c76ec40990297f53a85e9bed`;
- push Scaffold `35267065066` — SUCCESS;
- PR Scaffold `35267241770` — SUCCESS;
- merged as `12a62288457ebe5892f90f637fe41c142b094591`;
- post-merge Scaffold `35267674641` — SUCCESS.

Still deferred from Creature Manager: Official/SRD catalog browsing, import/export, Creature Creator Assistant/advisory guidance, media/object storage, hosted reusable-content sync, generalized all-domain Manager abstractions and live combat behavior.

#### Desktop NPC Manager — local authoring core

**COMPLETE / INTEGRATED — PR #63.**

Integrated behavior:

- concrete NPC selector/editor alongside Creature/Monster inside Desktop `MANAGERS`;
- browse/search Personal and active-Campaign NPCs;
- create Personal or active-Campaign NPCs;
- open/edit existing Quick and Developed NPC data without requiring a completion score;
- keep incomplete NPCs valid;
- optionally add/remove/edit full combat mechanics through the existing `CreaturePayload` model;
- show scope/provenance/revision;
- explicitly copy Personal NPC -> active Campaign as an independent object;
- preserve optimistic revision/stale-write/tombstone semantics with atomic name + payload update.

Validation:

- final implementation head `58b680e71ec59c871854eb9c083ff2bc6906fe88`;
- push Scaffold `35269013875` — SUCCESS;
- PR Scaffold `35269163375` — SUCCESS;
- merged as `58a565c3a33a433ce47e7fd4ac1185b5f980644f`;
- post-merge Scaffold `35270643883` — SUCCESS.

Still deferred from NPC Manager: NPC assistant/AI ideation, import/export, preserved-live-improvisation promotion workflow, media/object storage, hosted reusable-content sync and broad Manager framework generalization.

#### Desktop Homebrew & Rules Manager — lightweight rules local authoring core

**NEXT BOUNDED PACKAGE.**

Reuse the integrated lightweight Homebrew/Rule persistence from Wave 6 and implement the smallest concrete D-0072 authoring slice first.

Initial scope:

- browse/search Personal Homebrew/Rule records;
- browse/search active-Campaign Homebrew/Rule records when a campaign is active;
- create/open/edit title plus summary/body/category/rationale/examples/related references/tags/notes;
- expose the existing Draft / Active / Retired lifecycle;
- show scope/provenance/revision;
- explicitly copy Personal -> active Campaign as an independent object;
- preserve optimistic revision/stale-write/tombstone semantics with atomic name + payload update;
- reuse the conservative uniquely-resolvable local DM identity rule already proven by Creature/NPC Managers.

Deliberately defer structured races/sub-races/classes/subclasses/backgrounds/feats/spells/items, official/SRD customization, import/export, homebrew-aware AI, media/object storage, hosted reusable-content sync and broad Manager framework generalization. These remain approved D-0072 product direction but require later concrete packages rather than speculative pre-modeling.

Later Wave 7 packages implement the remaining approved authoring surfaces from D-0072: Stage/Place/Scene, Dungeon/Zone, Encounter, PC Manager/Audit and Media/Handouts. Family-specific supporting persistence may be added with the concrete Manager that needs it.

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
