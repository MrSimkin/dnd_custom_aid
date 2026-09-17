# Project State — global repository navigation

**Last reconstructed:** 2026-09-17 (Chile local time)  
**Owner integrated-MVP implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified runtime/integration merge:** `7000535b78df2b2a7149b019796ff3d5903fdb3d` (PR #75)  
**Post-merge Scaffold:** `35287713130` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Wave 7:** ACTIVE — Desktop authoring Managers  
**Integrated Wave 7 packages:** Desktop Creature/Monster Manager + Desktop NPC Manager + Desktop Homebrew & Rules lightweight local core + Desktop Place/Shop Manager local core + Desktop Stage Manager retrieval/organization core + lightweight Adventure/Scene Spine + Desktop Dungeon/Zone Manager local core + Desktop Encounter Manager local core  
**Next bounded package:** PC Manager / Audit — Desktop inspection/audit core

## 1. Current topology

`main` is the sole normal integrated-MVP trunk. New work uses short-lived outcome-oriented branches from current `main`.

Do not repeat completed Wave 5, Wave 6, Creature Manager, NPC Manager, Homebrew/Rules Manager, Place/Shop Manager, Stage retrieval, Scene Spine, Dungeon/Zone Manager or Encounter Manager work without new defect evidence.

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

Wave 6 and the integrated Wave 7 Creature/NPC/Homebrew/Place/Stage/Scene/Zone/Encounter packages did not require Worker changes or redeployment. Deploy again only when Worker code materially changes or newer evidence requires it.

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

## 6. Next Wave 7 package — PC Manager / Audit

Next bounded package: **PC Manager / Audit — Desktop inspection/audit core**.

D-0072 defines this as a DM-side inspection, audit and administration surface over the same canonical PC records used by the Player App. Desktop must not become a second Player character-builder and DM actions must not silently impersonate the Player.

Before changing persistence, inspect and reuse the existing canonical PC, authority, synchronization and history structures.

Initial bounded direction:

- campaign PC overview and retrieval;
- complete DM inspection of canonical PC data;
- expose available data-freshness and sync-freshness evidence distinctly;
- review meaningful grouped audit/history already represented by the project;
- explicit DM correction/edit entry points that preserve history and authority semantics;
- keep campaign membership, PC ownership and PC control distinct;
- focused controller/domain coverage around inspection, authority and correction boundaries.

Freeze/unfreeze, lifecycle administration, ownership/controller administration, duplication and PDF export remain approved D-0072/D-0074 responsibilities, but the exact first implementation package should follow evidence from the existing PC architecture rather than inventing parallel structures.

Media/Handouts and deferred richer Homebrew families remain later Wave 7 packages.

## 7. Security/provider boundaries

Repository is intentionally public. Never request, paste or commit secrets, credentials, OTPs, tokens, DB connection strings, JWTs or private keys.

Green CI does not prove provider/deployment behavior. Do not repeat completed provider verification merely because docs/local persistence changed.

Object-storage provider selection remains deferred until Media/Handouts/assets concretely require it.

Known residual: owner-local backend install reported 3 high-severity npm vulnerabilities. Do not run `npm audit fix --force` blindly; inspect package reachability and available fixed versions when a relevant hardening package is scheduled.

## 8. Resume rule

Read `AGENTS.md`, `docs/PROJECT_STATE.md`, `docs/BRANCH_STATUS.md`, `docs/checkpoints/LATEST.md`, the checkpoint referenced there, D-0071/D-0072/D-0073/D-0075 and `docs/ROADMAP.md`.

Resume Wave 7 from current `main` with PC Manager / Audit — Desktop inspection/audit core. Routine safe green boundaries do not require separate owner confirmation.
