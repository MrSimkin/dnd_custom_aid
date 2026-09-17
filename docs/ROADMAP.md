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

Integrated behavior includes Personal and active-Campaign browse/search/create/edit, human-complete Creature payload authoring, scope/provenance/revision visibility, explicit independent Personal -> Campaign copy and atomic display-name + payload updates under optimistic revision semantics.

Validation: implementation `38d68dc832188f29c76ec40990297f53a85e9bed`; push `35267065066` SUCCESS; PR `35267241770` SUCCESS; merge `12a62288457ebe5892f90f637fe41c142b094591`; post-merge `35267674641` SUCCESS.

Still deferred: Official/SRD catalog browsing, import/export, Creature Creator Assistant/advisory guidance, media/object storage, hosted reusable-content sync, generalized all-domain Manager abstractions and live combat behavior.

#### Desktop NPC Manager — local authoring core

**COMPLETE / INTEGRATED — PR #63.**

Integrated behavior includes Personal/Campaign browse, create and edit for Quick -> Developed NPC data, optional combat mechanics through the existing `CreaturePayload`, scope/provenance/revision visibility, explicit Personal -> Campaign independent copy and atomic display-name + payload updates.

Validation: implementation `58b680e71ec59c871854eb9c083ff2bc6906fe88`; push `35269013875` SUCCESS; PR `35269163375` SUCCESS; merge `58a565c3a33a433ce47e7fd4ac1185b5f980644f`; post-merge `35270643883` SUCCESS.

Still deferred: NPC assistant/AI ideation, import/export, preserved-live-improvisation promotion workflow, media/object storage, hosted reusable-content sync and broad Manager framework generalization.

#### Desktop Homebrew & Rules Manager — lightweight rules local authoring core

**COMPLETE / INTEGRATED — PR #65.**

Integrated behavior:

- browse/search Personal and active-Campaign Homebrew/Rule records;
- create/open/edit title, summary/body, category, rationale, examples, related references, tags and notes;
- Draft / Active / Retired lifecycle;
- scope/provenance/revision visibility;
- explicit independent Personal -> Campaign copy;
- optimistic revision/stale-write/tombstone semantics with atomic name + payload update;
- same conservative uniquely-resolvable local DM identity rule used by Creature/NPC Managers.

Validation:

- final implementation head `17f4cd686114f734ab0bc50f453de9d579a52c79`;
- push Scaffold `35272269481` — SUCCESS;
- PR Scaffold `35272560206` — SUCCESS;
- merged as `6febe3f936593999834189b92aeda9d209385fa7`;
- post-merge Scaffold `35277359425` — SUCCESS.

This is the lightweight rule-record core, not completion of the full D-0072 Homebrew surface. Structured races/sub-races/classes/subclasses/backgrounds/feats/spells/items, official/SRD customization, import/export, homebrew-aware AI, media/object storage, hosted reusable-content sync and broad Manager generalization remain later concrete work.

#### Desktop Place/Shop Manager — local authoring core

**COMPLETE / INTEGRATED — PR #67.**

Integrated behavior:

- browse/search Personal and active-Campaign Places;
- create/open/edit the complete existing Place payload;
- explicit `PLACE` / `SHOP` specialization, with Shop remaining a specialized Place rather than a separate persistence family;
- author summary, area, function, presentation, services, interactives, hooks, player-safe text, DM notes, paper references and tags;
- scope/provenance/revision visibility;
- explicit independent Personal -> Campaign copy;
- optimistic revision/stale-write/tombstone semantics with atomic name + payload update;
- same conservative uniquely-resolvable local DM identity rule used by the preceding Managers.

Validation:

- final implementation head `f4bb75f4b2872ebc6a1dc4302cd4890367e4618c`;
- push Scaffold `35278740631` — SUCCESS;
- PR Scaffold `35279052405` — SUCCESS;
- merged as `8be8ec82702a782c65b2d6aedf9bbe4b5b58f240`;
- post-merge Scaffold `35279329344` — SUCCESS.

No database migration, Worker deployment or provider action was required. Still deferred: Stage-level retrieval/organization, Scene Spine, linked-NPC and media-rich extensions not present in the current Place payload, hosted reusable-content sync and broad Manager generalization.

#### Desktop Stage Manager — Place retrieval/organization core

**NEXT BOUNDED PACKAGE.**

Build the first Stage preparation surface on the already-integrated Place/Shop Manager rather than creating duplicate Stage records. The current Place model already carries the data needed for a useful first Stage collection: kind, summary, area, function, presentation, services/interactives, hooks, player-safe text, DM notes, paper references and tags; reusable-content metadata provides scope/provenance/revision and update timestamps.

Initial scope:

- present Places/Shops as the Stage preparation collection;
- provide richer retrieval/filter/grouping by existing fields such as kind, area, function, tags and scope, plus recent-update ordering;
- preserve existing Place browse/create/open/edit and explicit Personal -> Campaign independent-copy semantics;
- preserve atomic optimistic-revision saves, stale-write rejection and tombstone/non-resurrection behavior;
- keep active Campaign context visible;
- make only the smallest sharing/refactor needed to reuse existing Place authoring behavior;
- add focused coverage for retrieval/filtering and stable selection/state.

Do not add a separate Stage persistence family for this package. Do not pull Scene Spine into it.

#### Adventure/Scene Spine — later bounded package

After the Stage retrieval/organization core, add D-0072's lightweight Adventure/Scene Spine as its own concrete package. Current source does not contain a `SCENE` reusable-content family, Scene payload repository or Scene schema; any minimal persistence extension therefore belongs explicitly to that package.

Keep the Scene model lightweight: orientation rather than quest-engine behavior, supporting the minimum approved title/purpose/possible-next-scenes and references needed by the concrete implementation. Do not pre-model clocks, handouts, generalized dependency graphs or live-state behavior unless that package concretely requires them.

Later Wave 7 packages implement the remaining approved authoring surfaces from D-0072: Dungeon/Zone, Encounter, PC Manager/Audit and Media/Handouts. Deferred richer Homebrew families remain separate concrete packages rather than speculative pre-modeling.

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
