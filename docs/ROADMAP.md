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

Integrated payload families and migrations:

- Creature — PR #48 / `19.sqm`;
- NPC — PR #51 / `20.sqm`;
- lightweight Homebrew/Rule — PR #53 / `21.sqm`;
- Place — PR #55 / `22.sqm`;
- Zone — PR #57 / `23.sqm`;
- Encounter — PR #59 / `24.sqm`.

The integrated spine preserves stable identity, Personal/Campaign scope, independent Personal -> Campaign copies with retained provenance, optimistic revisions/stale-write rejection, tombstones/non-resurrection and SQLDelight migration/reopen behavior.

### Wave 7 — Desktop authoring Managers

**ACTIVE.**

#### Desktop Creature/Monster Manager — local authoring core

**COMPLETE / INTEGRATED — PR #61.**

Personal/Campaign browse/search/create/edit, Creature payload authoring, scope/provenance/revision visibility, explicit independent copy and atomic display-name + payload update are integrated.

Validation: implementation `38d68dc832188f29c76ec40990297f53a85e9bed`; push `35267065066`; PR `35267241770`; merge `12a62288457ebe5892f90f637fe41c142b094591`; post-merge `35267674641` — SUCCESS.

#### Desktop NPC Manager — local authoring core

**COMPLETE / INTEGRATED — PR #63.**

Quick -> Developed NPC authoring, optional Creature mechanics, Personal/Campaign scope, provenance/revision visibility, explicit independent copy and atomic name + payload update are integrated.

Validation: implementation `58b680e71ec59c871854eb9c083ff2bc6906fe88`; push `35269013875`; PR `35269163375`; merge `58a565c3a33a433ce47e7fd4ac1185b5f980644f`; post-merge `35270643883` — SUCCESS.

#### Desktop Homebrew & Rules Manager — lightweight rules local core

**COMPLETE / INTEGRATED — PR #65.**

Lightweight rule browse/search/create/edit, Draft / Active / Retired lifecycle, provenance/revision visibility, explicit independent copy and atomic name + payload update are integrated.

Validation: implementation `17f4cd686114f734ab0bc50f453de9d579a52c79`; push `35272269481`; PR `35272560206`; merge `6febe3f936593999834189b92aeda9d209385fa7`; post-merge `35277359425` — SUCCESS.

Structured races/classes/subclasses/backgrounds/feats/spells/items, import/export, official/SRD customization, media/object storage, hosted reusable-content sync and homebrew-aware AI remain deferred concrete work.

#### Desktop Place/Shop Manager — local authoring core

**COMPLETE / INTEGRATED — PR #67.**

Personal/Campaign Place authoring, `PLACE` / `SHOP` specialization, full current payload editing, provenance/revision visibility, explicit independent copy and atomic name + payload update are integrated.

Validation: implementation `f4bb75f4b2872ebc6a1dc4302cd4890367e4618c`; push `35278740631`; PR `35279052405`; merge `8be8ec82702a782c65b2d6aedf9bbe4b5b58f240`; post-merge `35279329344` — SUCCESS.

#### Desktop Stage Manager — Place retrieval/organization core

**COMPLETE / INTEGRATED — PR #69.**

Stage-oriented retrieval over Places/Shops is integrated without duplicate Stage persistence: kind/scope/area/function/tag filtering, full-text retrieval and Name / Area / Recent ordering.

Validation: implementation `a01eb18a8385807d973c9f2059ff32779c0f6897`; push `35280359257`; PR `35280486923`; merge `a99f03bf53637494695cc39b39d077ea1ef61ada`; post-merge `35280636549` — SUCCESS.

#### Adventure/Scene Spine — lightweight local core

**COMPLETE / INTEGRATED — PR #71.**

The lightweight D-0072 orientation spine is now persisted and authorable on Desktop.

Integrated behavior:

- `SCENE` reusable-content family;
- Scene title/display name, purpose, possible next Scene cues, preparation references and DM notes;
- Personal + active-Campaign browse/search/create/open/edit;
- explicit independent Personal -> Campaign copy with provenance;
- atomic title + payload update under optimistic revision semantics;
- stale-write rejection and tombstone/non-resurrection;
- SQLDelight `scene_payload` plus migration `25.sqm` and migration/reopen coverage;
- Adventure/Scenes surface alongside the existing Stage Place/Shop surface.

`possibleNextScenes` and references remain human-readable strings. This package intentionally does not implement executable quest state, recursive dependency remapping, generalized graph infrastructure, clocks/readiness or live-state orchestration.

Validation:

- final implementation head `face568e7985125975731fef5e275e333ef79b9d`;
- push Scaffold `35281913166` — SUCCESS;
- PR Scaffold `35282188319` — SUCCESS;
- merged as `a84a8857102806f8a9ac588545167d697ea8a311`;
- post-merge Scaffold `35282483851` — SUCCESS.

#### Desktop Dungeon/Zone Manager — local Zone Brief authoring core

**NEXT BOUNDED PACKAGE.**

D-0072 places Dungeon/Zone preparation after Stage/Scene. Existing Wave 6 Zone persistence already provides the fields needed for a useful first Manager, so this package should remain schema-free unless concrete implementation evidence requires otherwise.

Current `ZonePayload` includes summary, area, presentation, space, exploration, interactives, clues, checks, consequences, encounter brief, DM guidance, player-safe text, paper references and tags.

Initial scope:

- Personal + active-Campaign Zone browse/search/create/open/edit;
- editor framing around the approved **PRESENTAR / INTERACTUAR / ENCUENTRO** Zone Brief grouping while retaining the richer existing fields;
- area/tag/search retrieval;
- explicit Personal -> Campaign independent copy with provenance;
- add atomic display-name + Zone payload update under one optimistic revision;
- preserve stale-write rejection and tombstone/non-resurrection;
- focused repository/controller tests;
- integrate into the current Desktop Managers surface with active Campaign context visible.

The existing `space` / `exploration` fields may hold topology/flow preparation in this first slice. Do not create tactical geometry or VTT maps, generalized graph infrastructure, clocks/readiness or automatic fictional consequences merely to complete the bounded Manager core.

#### Later Wave 7 packages

After Dungeon/Zone, continue with the remaining approved concrete surfaces in dependency order:

- Encounter Manager / Encounter Creator;
- PC Manager / Audit;
- Media / Handouts;
- deferred richer Homebrew families and other explicit gaps where still required.

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

Exercise representative Player + Server + DM flows end-to-end, including campaign/invite/PC sync, authored Creature/NPC/Place/Zone/Encounter/Scene content, live tablet play, Desktop authority resume, public combat projection, PC audit/correction, backup/export and official-SRD clarification.

## Security across waves

Maintain fail-closed authorization, object-level authorization, replay/idempotency safety, stale-write/tombstone guarantees, error/log hygiene and least privilege. Known owner-local 3 high-severity npm findings require deliberate package/reachability review; never run `npm audit fix --force` blindly.

## Git/provider rule

Use short-lived outcome branches from current `main`; integrate shared foundations early when later work depends on them. Routine safe green boundaries do not require owner confirmation. When a required provider action is inaccessible, finish safe repo work and stop at one bounded owner handoff instead of retrying access paths.
