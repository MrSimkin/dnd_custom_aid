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

The lightweight D-0072 orientation spine is persisted and authorable on Desktop with Scene title, purpose, possible-next-scene cues, preparation references, DM notes, Personal/Campaign authoring, explicit independent copy and atomic title + payload updates. `possibleNextScenes` and references remain human-readable strings rather than executable quest state.

Validation: final implementation `face568e7985125975731fef5e275e333ef79b9d`; push `35281913166`; PR `35282188319`; merge `a84a8857102806f8a9ac588545167d697ea8a311`; post-merge `35282483851` — SUCCESS.

#### Desktop Dungeon/Zone Manager — local Zone Brief authoring core

**COMPLETE / INTEGRATED — PR #73.**

The first Desktop Zone Manager is integrated on top of the existing Wave 6 Zone persistence with no schema migration.

Integrated behavior:

- Personal + active-Campaign Zone browse/search/create/open/edit;
- area/tag/full-text filtering;
- editor framing around **PRESENTAR / INTERACTUAR / ENCUENTRO** plus DM support fields;
- complete existing Zone payload authoring;
- explicit independent Personal -> Campaign copy with provenance;
- atomic display-name + Zone payload update;
- stale-write rejection and tombstone/non-resurrection coverage;
- dedicated `Mazmorras / Zonas` route in the Desktop Managers hub.

Existing `space` / `exploration` fields hold current topology/flow preparation. Tactical geometry, VTT maps, generalized graph infrastructure, clocks/readiness and automatic fictional consequences were not introduced.

Validation: implementation `1a8b875a567ed73fbcceee73872ec59702b4b3f0`; push `35285060523`; PR `35285359440`; merge `5be90a994f453e5444ecf00762cd72407cfe790a`; post-merge `35285629973` — SUCCESS.

#### Encounter Manager / Encounter Creator — local authoring core

**COMPLETE / INTEGRATED — PR #75.**

The first Desktop Encounter Manager is integrated on top of the existing Wave 6 Encounter persistence with no schema migration.

Integrated behavior:

- Personal + active-Campaign Encounter browse/search/create/open/edit;
- full-text/tag filtering;
- participant authoring using same-scope Creature/NPC choices or freeform groups;
- quantity, Expected / Reserve / Conditional readiness, condition, encounter-specific overrides and notes;
- explicit independent Personal -> Campaign Encounter copy preserving current Creature/NPC dependency copy/remap and deduplication semantics;
- atomic display-name + payload update under optimistic revision semantics;
- stale-write, dependency, copy and tombstone/non-resurrection coverage;
- dedicated `Encuentros` route in the Desktop Managers hub.

Saved Encounter preparation remains separate from live initiative/combat state. No generalized dependency graph or encounter-balancing authority was introduced.

Validation: initial implementation `91d744a66e3ff18ee9190c41d4dbb3970ca412fe`; initial push `35286844850` exposed one corrected Kotlin visibility mismatch; final implementation `a68f62897d6178f1da1c19deb2721ea04abc837a`; corrected push `35287257508` SUCCESS; PR `35287507268` SUCCESS; merge `7000535b78df2b2a7149b019796ff3d5903fdb3d`; post-merge `35287713130` SUCCESS.

#### PC Manager / Audit — Desktop inspection/audit core

**INSPECTION/AUDIT CORE COMPLETE / INTEGRATED — PR #77.**

The first Desktop PC Manager slice now operates over the same canonical Player PC data rather than introducing a parallel Desktop character model.

Integrated behavior:

- campaign PC browse/search through the existing reserved `Personajes jugadores` destination;
- complete read inspection of the canonical Character sheet plus Closure and Successor aggregates;
- explicit owner/controller display independent from DM role;
- distinct local data timestamp, sync revision, baseline revision and outbox status;
- reconciliation checkpoint history;
- explicit audited DM core-field correction;
- correction reason + field-change summary stored as a `Corrección DM` reconciliation checkpoint;
- correction queues the canonical hosted PC snapshot at the current sync revision and refuses to stack another PC mutation while one still needs resolution;
- owner/controller authority is preserved during DM correction.

Validation: initial implementation `2d792abaa843734acd2a1226f91d2e86e7b2b929`; initial push `35288970002` exposed one corrected Kotlin visibility mismatch; final implementation `50132cdb295097ac4a7ab91c4d766b900eeb7771`; corrected push `35289198415` SUCCESS; PR `35289424011` SUCCESS; merge `e14784390971f2e27025dd2fff1f5000658eb2f0`; post-merge `35289703289` SUCCESS.

##### PC Manager ownership/controller administration core

**COMPLETE / INTEGRATED / DEV DEPLOYMENT VERIFIED — PR #79.**

Integrated repository behavior:

- dedicated DM-only hosted authority mutation;
- owner/controller assignment remains independent from campaign role and from each other;
- explicit nullable unassignment;
- omitted owner/controller fields rejected;
- non-null targets restricted to active same-campaign members;
- fail-closed non-DM/inactive/cross-campaign handling;
- typed missing/tombstoned PC behavior;
- idempotent same-state update;
- authority changes leave PC snapshot content/revision untouched;
- explicit-null hosted wire handling;
- shared hosted client and Desktop PC Manager controls;
- local authority convergence from authoritative hosted response;
- backend API, PostgreSQL contract, shared-client and Desktop eligibility coverage.

Validation: initial head `5de58771702dd2f1f548ca00f066318c630bb622`; initial push `35290905581` failed on corrected backend row-typing and Kotlin visibility compile issues while hosted DB passed; final head `418ac19d4d247cfbf19d6fb7f9b158df5c900bdc`; corrected push `35291183960` SUCCESS; PR `35291417403` SUCCESS; merge `2e12400ee18026c702d6727793a3aea5d23d07b4`; post-merge `35291685597` SUCCESS.

DEV deployment verification is closed. Owner-executed `npm run deploy` published Worker version `ccdeca47-7622-4eb7-8dfa-a197d62bf3cb`; `/health` returned 200, while both the existing Campaign-members route and new PC-authority route returned the expected 401 `UNAUTHENTICATED` without credentials. This verifies live route presence and fail-closed authentication. Authenticated mutation semantics remain covered by repository tests rather than this smoke check.

##### PC Sheet PDF Export — shared semantic/render-plan foundation

**COMPLETE / INTEGRATED — PR #83.**

Integrated behavior:

- canonical read-only export aggregate over Character Sheet + Closure + Successor state;
- Permanent vs Current Snapshot selection with explicit fallback notice when no separate current aggregate exists;
- the four D-0074 visual families;
- authoritative Custom v1/v2 source-template page mappings;
- custom Attribute/custom Skill projection using existing shared calculations;
- Extended Page / App Modified / combined custom-stat semantics;
- overflow routing contracts without hard-coding physical geometry;
- portrait Crop-to-fill / Fit-entire-image planning with missing-local-asset continuation;
- optional attached-spell Spellbook planning preserving all recorded casting-source relationships and source-specific derived values.

Validation: final head `dea22b0823d6c4ad54e24839943d5952e2020392`; push Scaffold `35294590552` SUCCESS; PR Scaffold `35294792657` SUCCESS; merge `f6350d34087aae55d5247f2ba23153814eeed04b`; post-merge Scaffold `35295050340` SUCCESS.

No renderer, persistence migration, backend/provider change or export UI was introduced.

##### PC Sheet PDF Export — local renderer + authoritative template mapping

**NEXT BOUNDED PACKAGE.**

Build the first physical static-PDF renderer on top of the integrated shared plan:

- local/offline generation;
- owner v1/v2 PDFs used as authoritative base pages;
- concrete field/template placement metadata and renderer primitives;
- generated/Extended-page support where D-0074 requires it;
- keep Classic independently designed;
- preserve the mandatory owner visual gate: populated dummy-data examples must be reviewed before any visual family is considered approved.

Freeze/unfreeze remains an approved requirement but is not implementation-ready: no current freeze field/contract exists and D-0072 does not define what PC operations freezing blocks. Do not invent this behavior.

#### Later Wave 7 packages

After the remaining bounded PC Manager responsibilities, continue with the remaining approved concrete surfaces in dependency order:

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
