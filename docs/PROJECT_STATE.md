# Project State — global repository navigation

**Last reconstructed:** 2026-09-19 (Chile local time)  
**Owner integrated-MVP implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified integrated `main`:** `2dc74e2d9c7d853a068e9052ec4928bf5178eb9f` (PC Sheet PDF foundation docs closure PR #84)  
**Post-merge Scaffold:** `35295050340` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Wave 7:** ACTIVE — Desktop authoring Managers  
**Integrated Wave 7 repository packages:** Desktop Creature/Monster Manager + Desktop NPC Manager + Desktop Homebrew & Rules lightweight local core + Desktop Place/Shop Manager local core + Desktop Stage Manager retrieval/organization core + lightweight Adventure/Scene Spine + Desktop Dungeon/Zone Manager local core + Desktop Encounter Manager local core + Desktop PC Manager inspection/audit core + PC ownership/controller administration core + PC Sheet PDF Export shared semantic/render-plan foundation  
**PC authority DEV deployment:** VERIFIED — Worker version `ccdeca47-7622-4eb7-8dfa-a197d62bf3cb`  
**Current active package:** PC Sheet PDF Export — Custom v1 source-faithful Extended Run-2 owner review

### Superseding PDF visual state — 2026-09-19

Current active branch: `wave7/pc-sheet-pdf-renderer-template-proof` / PR #85 (DRAFT / DO NOT MERGE).

Approved/frozen base-sheet baselines:
- Custom v1 Run 7;
- Custom v2 per Attribute Run 4;
- Custom v2 per Ability corrected Run 2;
- Para Hoja de PJ Symbols v8.

Current Classic candidate:
- approved corrected Classic Run-2 commit `3dbcff8f5f9a2413f6deb8e400daeb6288b4f6b1`;
- Scaffold `35480871986` / #2793 — SUCCESS;
- complete nine-page Classic approval set: three normal pages plus all six D-0074 extension roles;
- OWNER APPROVED / FROZEN.

D-0074 + owner clarification require design-specific Extended pages for **all** families. The Custom base-sheet approvals do not close their Extended-page work.

## 1. Current topology

`main` is the sole normal integrated-MVP trunk. New work uses short-lived outcome-oriented branches from current `main`.

Do not repeat completed Wave 5, Wave 6, Creature Manager, NPC Manager, Homebrew/Rules Manager, Place/Shop Manager, Stage retrieval, Scene Spine, Dungeon/Zone Manager, Encounter Manager, PC Manager inspection/audit, PC authority repository work or the PC Sheet PDF semantic foundation without new defect evidence.

### Current active PDF branch / renderer state

Active branch: `wave7/pc-sheet-pdf-renderer-template-proof`. Draft PR: **#85**.

The first populated MAIN-page proof remains **owner-rejected historical evidence**. The approved continuation is whole-export-first architecture with structured incremental QA rather than coordinate nudging.

The shared primitive foundation has now passed owner Primitive PDF QA. Renderer work may proceed beyond the primitive gate using:

- PDFBox 3.0.8;
- metric-based text/layout, explicit adaptive vs fixed font sizing and measured wrapping/overflow;
- repeated-row/table and portrait Fit/Crop primitives;
- deterministic sans/condensed/handwritten typography candidates;
- native vector markers and the app single-check / double-check training grammar.

The exact owner-authored `Para-hoja-de-pj` v1 is now committed as immutable provenance after SHA-256 verification. v4 is published as a contemporary redesign with legacy-refined and modern-clean families, stable renderer PUA aliases, complete documentation and a deterministic TTX-backed builder.

Para Hoja de PJ Symbols v8 is OWNER APPROVED / FROZEN. Custom v1 base pages, Custom v2 per-Attribute base pages and Custom v2 per-Ability base pages are owner-approved/frozen. Classic Run 2 corrected complete family is OWNER APPROVED / FROZEN at `3dbcff8f5f9a2413f6deb8e400daeb6288b4f6b1`. Classic Run 1 is owner-rejected historical evidence only. Custom-v1 Extended Run 1 is owner-rejected historical evidence. Custom-v1 Extended Run 2 is now the technically/visually audited 11-page candidate at the mandatory owner-review gate (`b244159b467d162c8637db5532dcfe6f2f831953`, Scaffold #2817 SUCCESS); it rebuilds extensions from the actual Custom-v1 source-page composition while guarding frozen pages 1–5 pixel-identical. Custom-v2 family-matched Extended-page design/QA remains pending. Do not describe either Custom family as fully closed until its extension set receives explicit owner approval.

Resume from the latest checkpoint referenced by `docs/checkpoints/LATEST.md`.

## 2. Integrated Wave 5 baseline

Wave 5 verified Desktop behavior includes local campaign/workbench persistence, real Descope email-OTP authentication, hosted campaign bootstrap/convergence, authoritative campaign member administration, canonical Outlook DEV owner/DM identity, device-local settings and explicit hosted-session lifecycle/sign-out semantics.

Historical Gmail evidence remains truthful and unchanged.

## 3. Hosted DEV architecture

```text
Android / Desktop clients
        |
        v
Cloudflare Worker/API <---- Descope identity proof
        |
        v
Neon PostgreSQL
```

Existing DEV Worker: `dnd-custom-aid-api`.

Wave 6 and the earlier Wave 7 Creature/NPC/Homebrew/Place/Stage/Scene/Zone/Encounter/PC-audit packages did not require Worker changes. PR #79 materially changed Worker/API code and has now been deployed to DEV. Owner-executed checks verified Worker version `ccdeca47-7622-4eb7-8dfa-a197d62bf3cb`, `/health` = 200, and both the existing Campaign-members route and new PC-authority route = 401 when unauthenticated.

Hard external-service operating budget remains USD $0.

## 4. Reusable-content architecture

The shared reusable-content spine provides Personal/Campaign scope, stable identity, family metadata, provenance for independent Personal -> Campaign copies, optimistic revisions/stale-write rejection, tombstones/non-resurrection, sync metadata/invariants, SQLDelight persistence and verified migrations/reopen behavior.

Approved semantics remain: Personal DM material is reusable; explicit use in a Campaign creates a new independent Campaign object ID; provenance may remain visible; later Personal edits do not automatically update Campaign copies.

Integrated reusable families:

- Creature — PR #48, migration `19.sqm`;
- NPC — PR #51, migration `20.sqm`;
- Homebrew/Rule — PR #53, migration `21.sqm`;
- Place — PR #55, migration `22.sqm`;
- Zone — PR #57, migration `23.sqm`;
- Encounter — PR #59, migration `24.sqm`;
- Scene — PR #71, migration `25.sqm`.

Encounter dependency copy/remap remains domain-specific; no generalized dependency graph was introduced. Scene possible-next-scene cues and preparation references are intentionally human-readable strings in the current lightweight orientation model.

## 5. Wave 7 integrated Managers and preparation surfaces

### Creature/Monster Manager

PR #61 integrated Personal + active-Campaign Creature browse/search/create/edit, scope/provenance/revision visibility, explicit independent Personal -> Campaign copy and atomic display-name + payload updates.

Validation: implementation `38d68dc832188f29c76ec40990297f53a85e9bed`; push `35267065066`; PR `35267241770`; merge `12a62288457ebe5892f90f637fe41c142b094591`; post-merge `35267674641` — SUCCESS.

### NPC Manager

PR #63 integrated Personal/Campaign browse/create/edit for Quick -> Developed NPC data, optional combat mechanics through `CreaturePayload`, provenance/revision visibility, explicit independent copy and atomic name + payload updates.

Validation: implementation `58b680e71ec59c871854eb9c083ff2bc6906fe88`; push `35269013875`; PR `35269163375`; merge `58a565c3a33a433ce47e7fd4ac1185b5f980644f`; post-merge `35270643883` — SUCCESS.

### Homebrew & Rules Manager

PR #65 integrated the lightweight rule-record core: Personal/active-Campaign browse/search/create/edit, Draft / Active / Retired lifecycle, provenance/revision visibility, explicit independent copy and atomic name + payload updates.

Validation: implementation `17f4cd686114f734ab0bc50f453de9d579a52c79`; push `35272269481`; PR `35272560206`; merge `6febe3f936593999834189b92aeda9d209385fa7`; post-merge `35277359425` — SUCCESS.

Structured races/classes/subclasses/backgrounds/feats/spells/items, official/SRD customization, import/export, homebrew-aware AI, media/object storage and hosted reusable-content sync remain later work.

### Place/Shop Manager

PR #67 integrated Personal/Campaign Place browse/search/create/open/edit, `PLACE` / `SHOP` specialization, full existing Place payload authoring, provenance/revision visibility, explicit independent copy and atomic display-name + payload update.

Validation: implementation `f4bb75f4b2872ebc6a1dc4302cd4890367e4618c`; push `35278740631`; PR `35279052405`; merge `8be8ec82702a782c65b2d6aedf9bbe4b5b58f240`; post-merge `35279329344` — SUCCESS.

### Stage Manager — Place retrieval/organization core

PR #69 added Stage-oriented retrieval over the existing Place/Shop family without duplicate Stage persistence: kind/scope/area/function/tag filters, full-text retrieval and deterministic Name / Area / Recent ordering while preserving the Place editor/copy flows.

Validation: implementation `a01eb18a8385807d973c9f2059ff32779c0f6897`; push `35280359257`; PR `35280486923`; merge `a99f03bf53637494695cc39b39d077ea1ef61ada`; post-merge `35280636549` — SUCCESS.

### Adventure/Scene Spine — lightweight local core

PR #71 integrated the lightweight Scene orientation model and Desktop authoring surface: title, purpose, possible-next-scene cues, preparation references, DM notes, Personal/active-Campaign authoring, explicit independent copy and atomic title + payload update.

Persistence adds `ReusableContentFamily.SCENE`, `ScenePayload` / `SceneContent`, `SceneContentRepository`, `scene_payload` and migration `25.sqm`. Scene remains orientation, not a quest engine.

Validation: final implementation `face568e7985125975731fef5e275e333ef79b9d`; push `35281913166`; PR `35282188319`; merge `a84a8857102806f8a9ac588545167d697ea8a311`; post-merge `35282483851` — SUCCESS.

### Desktop Dungeon/Zone Manager — local Zone Brief authoring core

PR #73 integrated the first Desktop Zone Manager on top of the existing Wave 6 Zone persistence. No schema migration was required.

Integrated behavior:

- Personal + active-Campaign Zone browse/search/create/open/edit;
- area/tag/full-text filtering with deterministic Personal-before-Campaign browsing;
- editor grouping around **PRESENTAR / INTERACTUAR / ENCUENTRO**, plus DM support fields;
- full existing Zone payload authoring: summary, area, presentation, space, exploration, interactives, clues, checks, consequences, encounter brief, DM guidance, player-safe text, paper references and tags;
- explicit Personal -> Campaign independent copy with provenance;
- atomic display-name + Zone payload update under one optimistic revision;
- focused stale-write, copy-independence and tombstone/non-resurrection coverage;
- dedicated `Mazmorras / Zonas` route in the existing Desktop Managers hub.

The `space` and `exploration` fields carry the current topology/flow preparation. Tactical geometry, VTT maps, generalized graph infrastructure, clocks/readiness and automated fictional consequences remain outside this bounded Manager core.

Validation:

- implementation head `1a8b875a567ed73fbcceee73872ec59702b4b3f0`;
- push Scaffold `35285060523` — SUCCESS;
- PR Scaffold `35285359440` — SUCCESS;
- PR #73 merged as `5be90a994f453e5444ecf00762cd72407cfe790a`;
- post-merge Scaffold `35285629973` — SUCCESS.

### Desktop Encounter Manager / Encounter Creator — local authoring core

PR #75 integrated the first Desktop Encounter Manager on top of the existing Wave 6 Encounter persistence. No schema migration was required.

Integrated behavior:

- Personal + active-Campaign Encounter browse/search/create/open/edit;
- full-text and tag filtering;
- participant authoring through same-scope Creature/NPC selection or freeform label-only groups;
- quantity plus `EXPECTED` / `RESERVE` / `CONDITIONAL` readiness, condition, encounter-specific overrides and notes;
- explicit Personal -> Campaign independent Encounter copy preserving the existing domain-specific Creature/NPC dependency copy/remap and repeated-dependency deduplication behavior;
- atomic display-name + Encounter payload update under one optimistic revision;
- focused dependency, stale-write, copy and tombstone/non-resurrection coverage;
- dedicated `Encuentros` route in the Desktop Managers hub.

The initial branch push exposed one Kotlin visibility mismatch between a public controller method and an internal Desktop participant-source UI type. The follow-up commit only aligned visibility; the corrected push, PR and post-merge gates all passed.

Validation:

- initial implementation head `91d744a66e3ff18ee9190c41d4dbb3970ca412fe`;
- initial push Scaffold `35286844850` — FAILED on the visibility mismatch;
- final implementation head `a68f62897d6178f1da1c19deb2721ea04abc837a`;
- corrected push Scaffold `35287257508` — SUCCESS;
- PR Scaffold `35287507268` — SUCCESS;
- PR #75 merged as `7000535b78df2b2a7149b019796ff3d5903fdb3d`;
- post-merge Scaffold `35287713130` — SUCCESS.

Saved Encounters remain preparation, not live initiative/combat working state. No generalized dependency graph, encounter-balancing authority, hosted reusable-content synchronization or provider change was introduced.

### Desktop PC Manager / Audit — inspection and audited correction core

PR #77 integrated the first PC Manager/Audit slice over the same canonical PC data used by Player. No new local schema was required.

Integrated behavior:

- `Personajes jugadores` is now a real Desktop destination;
- active-Campaign PC browse/search;
- inspection of the canonical core Character sheet plus Closure and Successor aggregates;
- PC owner and controller shown independently from campaign DM role;
- local data timestamp, sync revision, durable baseline revision and pending outbox state shown as distinct evidence;
- existing reconciliation checkpoints surfaced as grouped history;
- explicit `Correct / Edit as DM` flow for bounded core fields;
- DM correction requires active DM campaign membership, preserves PC/campaign identity and owner/controller authority, appends a `Corrección DM` checkpoint with reason/change summary, exports the resulting canonical aggregate and queues the existing optimistic hosted PC snapshot mutation;
- correction refuses to proceed while another PC snapshot mutation still requires resolution;
- focused shared/domain and Desktop controller tests.

The initial branch push exposed one Kotlin visibility mismatch between a public controller method and an internal Desktop details model. The follow-up commit aligned visibility only.

Validation:

- initial implementation head `2d792abaa843734acd2a1226f91d2e86e7b2b929`;
- initial push Scaffold `35288970002` — FAILED on the visibility mismatch;
- final implementation head `50132cdb295097ac4a7ab91c4d766b900eeb7771`;
- corrected push Scaffold `35289198415` — SUCCESS;
- PR Scaffold `35289424011` — SUCCESS;
- PR #77 merged as `e14784390971f2e27025dd2fff1f5000658eb2f0`;
- post-merge Scaffold `35289703289` — SUCCESS.

This slice does not create a second Desktop character model, a generalized audit/event-sourcing framework, a new local schema, automatic Player impersonation or direct sync bypass.

### PC Manager ownership/controller administration — repository core

PR #79 merged the explicit ownership/controller administration core. No hosted database migration was required; the existing nullable `pc.owner_user_id` and `pc.controller_user_id` columns are reused.

Integrated repository behavior:

- dedicated `PUT /v1/pcs/{pcId}/authority` route;
- authenticated actor must be an active campaign DM;
- owner/controller are changed independently and may be explicitly unassigned;
- omitted authority fields are invalid, preventing accidental partial-clear semantics;
- every non-null target must be an active member of the same campaign;
- cross-campaign/inactive targets fail closed;
- missing PC -> typed 404; tombstoned PC -> typed 410;
- repeat of the same authority state is an idempotent no-op;
- authority mutation does not alter PC snapshot JSON or its optimistic synchronization revision;
- shared Kotlin hosted client preserves explicit JSON nulls even though the normal hosted serializer omits nullable null properties;
- Desktop Campaign Administration hydrates hosted roster/account/membership state before local convergence;
- Desktop PC Manager exposes explicit owner/controller controls using active campaign-member choices;
- local `PcAuthority` is updated from the authoritative hosted response.

Validation:

- initial implementation head `5de58771702dd2f1f548ca00f066318c630bb622`;
- initial push Scaffold `35290905581` — FAILED on narrow backend query-row typing and Kotlin visibility compile issues; hosted database contract passed;
- final head `418ac19d4d247cfbf19d6fb7f9b158df5c900bdc`;
- corrected push Scaffold `35291183960` — SUCCESS;
- PR Scaffold `35291417403` — SUCCESS;
- PR #79 merged as `2e12400ee18026c702d6727793a3aea5d23d07b4`;
- post-merge Scaffold `35291685597` — SUCCESS.

### DEV provider verification — CLOSED

The Worker/API change from PR #79 is deployed to the existing DEV Worker `dnd-custom-aid-api`.

Owner-executed deployment evidence:

- local project path: `D:\DnD_Aid\repo\dnd_custom_aid\backend`;
- `npm run deploy` succeeded;
- deployed Worker version: `ccdeca47-7622-4eb7-8dfa-a197d62bf3cb`;
- `GET /health` -> 200 / normal service-health JSON;
- unauthenticated `GET /v1/campaigns/00000000-0000-0000-0000-000000000000/members` -> 401 / `UNAUTHENTICATED`;
- unauthenticated `GET /v1/pcs/00000000-0000-0000-0000-000000000000/authority` -> 401 / `UNAUTHENTICATED`.

This closes the deployment/route-presence/auth-boundary gate. An authenticated live authority mutation was not part of this deployment smoke check; its semantics remain covered by repository tests.

## 6. Next bounded package — PC Sheet PDF Export shared foundation

D-0074 fully closes the PC Sheet PDF product definition. The canonical PC model is now coherent enough to start a shared export-semantics/render-plan foundation without waiting on Media/object storage.

Initial bounded direction after provider closure:

- canonical export snapshot abstraction over the existing PC data;
- Permanent vs Current Snapshot selection contract;
- visual-family selection model: Classic, Custom v1, Custom v2 per Attribute, Custom v2 per Ability;
- custom-stat presentation modes and overflow/Extended-page render-plan semantics;
- portrait behavior represented as optional local/cached input rather than network dependency;
- optional Spellbook inclusion contract;
- platform-neutral render plan first; actual platform PDF renderer/coordinates follow in separate bounded work where helpful;
- preserve local/offline generation and one canonical PC across Player/DM surfaces.

D-0072 freeze/unfreeze remains required but currently lacks a concrete product contract in the repository: there is no freeze field and no approved definition of what operations freezing blocks. Do not invent that behavior. Broader lifecycle administration and duplication should also remain separate from the PDF foundation.

Media/Handouts and deferred richer Homebrew families remain later Wave 7 packages.

## 7. Security/provider boundaries

Repository is intentionally public. Never request, paste or commit secrets, credentials, OTPs, tokens, DB connection strings, JWTs or private keys.

Green CI does not prove provider/deployment behavior. Do not repeat completed provider verification merely because docs/local persistence changed.

Object-storage provider selection remains deferred until Media/Handouts/assets concretely require it.

Known residual: owner-local backend install reported 3 high-severity npm vulnerabilities. Do not run `npm audit fix --force` blindly; inspect package reachability and available fixed versions when a relevant hardening package is scheduled.

## 8. Resume rule

Read `AGENTS.md`, `docs/PROJECT_STATE.md`, `docs/BRANCH_STATUS.md`, `docs/checkpoints/LATEST.md`, the checkpoint referenced there, D-0071/D-0072/D-0073/D-0075 and `docs/ROADMAP.md`.

Resume Wave 7 from current `main` with PC Sheet PDF Export — local renderer + authoritative template mapping. The semantic/render-plan foundation is integrated; the next code must consume it rather than rebuilding export semantics. Routine safe green boundaries do not require separate owner confirmation.

### PC Sheet PDF Export — shared semantic/render-plan foundation

PR #83 integrated the D-0074 platform-neutral export plan over canonical PC data. The foundation covers state selection, visual-family selection, authoritative v1/v2 template-page mapping, custom-stat modes, overflow routes, portrait behavior and the optional attached-spell Spellbook contract. It does not render physical PDFs yet.

Validation: final implementation head `dea22b0823d6c4ad54e24839943d5952e2020392`; push Scaffold `35294590552` SUCCESS; PR Scaffold `35294792657` SUCCESS; merge `f6350d34087aae55d5247f2ba23153814eeed04b`; post-merge Scaffold `35295050340` SUCCESS.

Custom v1/v2 source PDFs remain authoritative. Populated dummy-data examples are a mandatory owner approval gate when visual rendering reaches reviewable output.
