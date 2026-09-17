# Project State — global repository navigation

**Last reconstructed:** 2026-09-17 (Chile local time)  
**Owner integrated-MVP implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified runtime/integration merge:** `a99f03bf53637494695cc39b39d077ea1ef61ada` (PR #69)  
**Post-merge Scaffold:** `35280636549` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Wave 7:** ACTIVE — Desktop authoring Managers  
**Integrated Wave 7 packages:** Desktop Creature/Monster Manager + Desktop NPC Manager + Desktop Homebrew & Rules lightweight local core + Desktop Place/Shop Manager local core + Desktop Stage Manager place retrieval/organization core  
**Next bounded package:** Adventure/Scene Spine — lightweight local core

## 1. Current topology

`main` is the sole normal integrated-MVP trunk. New work uses short-lived outcome-oriented branches from current `main`.

Do not repeat completed Wave 5, Wave 6, Creature Manager, NPC Manager, Homebrew/Rules Manager, Place/Shop Manager or Stage retrieval work without new defect evidence.

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

Wave 6 and the integrated Wave 7 Creature/NPC/Homebrew/Place/Stage packages did not require Worker changes or redeployment. Deploy again only when Worker code materially changes or newer evidence requires it.

Hard external-service operating budget remains USD $0.

## 4. Wave 6 integrated architecture

The shared reusable-content spine provides Personal/Campaign scope, stable identity, family metadata, provenance for independent Personal -> Campaign copies, optimistic revisions/stale-write rejection, tombstones/non-resurrection, sync metadata/invariants, SQLDelight persistence and verified migrations/reopen behavior.

Approved semantics remain: Personal DM material is reusable; explicit use in a Campaign creates a new independent Campaign object ID; provenance may remain visible; later Personal edits do not automatically update Campaign copies.

Integrated reusable families:

- Creature — PR #48, migration `19.sqm`;
- NPC — PR #51, migration `20.sqm`;
- Homebrew/Rule — PR #53, migration `21.sqm`;
- Place — PR #55, migration `22.sqm`;
- Zone — PR #57, migration `23.sqm`;
- Encounter — PR #59, migration `24.sqm`.

Encounter dependency copy/remap remains domain-specific; no generalized dependency graph was introduced. Scene is not currently a reusable-content family and must not be assumed to exist.

## 5. Wave 7 integrated Managers

### Creature/Monster Manager

PR #61 integrated the first visible Desktop authoring Manager. It supports Personal + active-Campaign Creature browsing/search, create/open/edit, scope/provenance/revision visibility and explicit Personal -> Campaign independent copy. Display name + Creature payload save atomically under one optimistic revision.

Validation: implementation `38d68dc832188f29c76ec40990297f53a85e9bed`; push `35267065066`; PR `35267241770`; merge `12a62288457ebe5892f90f637fe41c142b094591`; post-merge `35267674641` — SUCCESS.

### NPC Manager

PR #63 integrated the second concrete Desktop Manager. It supports Personal/Campaign browse, create and edit for Quick -> Developed NPC data, optional combat mechanics through `CreaturePayload`, scope/provenance/revision visibility, explicit Personal -> Campaign independent copy and atomic name + payload updates. Incomplete NPCs remain valid.

Validation: implementation `58b680e71ec59c871854eb9c083ff2bc6906fe88`; push `35269013875`; PR `35269163375`; merge `58a565c3a33a433ce47e7fd4ac1185b5f980644f`; post-merge `35270643883` — SUCCESS.

### Homebrew & Rules Manager

PR #65 integrated the third concrete Desktop Manager using the existing lightweight Homebrew/Rule persistence. It supports Personal/active-Campaign browse/search/create/edit, Draft / Active / Retired lifecycle, scope/provenance/revision visibility, explicit Personal -> Campaign independent copy and atomic name + payload updates.

Validation: implementation `17f4cd686114f734ab0bc50f453de9d579a52c79`; push `35272269481`; PR `35272560206`; merge `6febe3f936593999834189b92aeda9d209385fa7`; post-merge `35277359425` — SUCCESS.

The lightweight Homebrew Manager does not claim completion of structured races/classes/subclasses/backgrounds/feats/spells/items, official/SRD customization, import/export, homebrew-aware AI, media/object storage, hosted reusable-content sync or generalized Manager abstractions.

### Place/Shop Manager

PR #67 integrated the fourth concrete Desktop Manager using the existing Wave 6 Place persistence. It supports Personal/Campaign Place browse/search/create/open/edit, `PLACE` / `SHOP` specialization, full current Place payload authoring, scope/provenance/revision visibility, explicit Personal -> Campaign independent copy and atomic display-name + payload updates.

Validation: implementation `f4bb75f4b2872ebc6a1dc4302cd4890367e4618c`; push `35278740631`; PR `35279052405`; merge `8be8ec82702a782c65b2d6aedf9bbe4b5b58f240`; post-merge `35279329344` — SUCCESS.

### Stage Manager — Place retrieval/organization core

PR #69 integrated the fifth concrete Wave 7 package without adding a Stage persistence model.

The existing Place/Shop authoring surface now also provides Stage-oriented retrieval:

- kind filter: all / Place / Shop;
- scope filter: all / Personal / Campaign;
- area, function and tag filters;
- full-text retrieval across the existing Place payload;
- deterministic Name / Area / Recent ordering;
- Stage framing while preserving the existing Place editor and copy flows.

The package reuses existing Place persistence, so all Personal/Campaign copy, provenance, atomic update, optimistic revision, stale-write and tombstone/non-resurrection semantics remain unchanged.

Validation:

- implementation head `a01eb18a8385807d973c9f2059ff32779c0f6897`;
- push Scaffold `35280359257` — SUCCESS;
- PR Scaffold `35280486923` — SUCCESS;
- PR #69 merged as `a99f03bf53637494695cc39b39d077ea1ef61ada`;
- post-merge Scaffold `35280636549` — SUCCESS.

No database migration, Worker change or provider action was required.

## 6. Next Wave 7 package — Adventure/Scene Spine

Next bounded package: **Adventure/Scene Spine — lightweight local core**.

Source inspection confirms Scene is genuinely absent rather than merely hidden from the UI:

- `ReusableContentFamily` currently contains Creature, NPC, Homebrew/Rule, Place, Zone and Encounter only;
- there is no Scene payload repository or Scene SQL schema;
- the shared spine package provides generic identity/revision/scope primitives, not a Scene domain model;
- current SQLDelight payload migrations run through `24.sqm` for Encounter;
- the generic reusable-content table is already family-agnostic.

The Scene package should introduce only the minimum concrete persistence/model/UI surface required for D-0072's lightweight orientation spine.

Initial principles:

- title/display name and purpose/summary;
- possible next Scenes;
- only lightweight references actually needed by the concrete preparation/navigation flow;
- Personal and Campaign scope using the proven reusable-content semantics;
- explicit Personal -> Campaign independent copy;
- optimistic revisions, stale-write rejection and tombstone/non-resurrection;
- minimal Desktop browse/create/edit/copy authoring;
- focused migration/persistence/revision/copy tests.

Do not turn Scene into a quest engine. Do not pre-model clocks, media storage, generalized dependency graphs or live-state orchestration. Keep cross-domain reference semantics deliberately narrow and evidence-driven.

Dungeon/Zone, Encounter, PC Manager/Audit, Media/Handouts and deferred richer Homebrew families remain later Wave 7 packages.

## 7. Security/provider boundaries

Repository is intentionally public. Never request, paste or commit secrets, credentials, OTPs, tokens, DB connection strings, JWTs or private keys.

Green CI does not prove provider/deployment behavior. Do not repeat completed provider verification merely because docs/local persistence changed.

Object-storage provider selection remains deferred until Media/Handouts/assets concretely require it.

Known residual: owner-local backend install reported 3 high-severity npm vulnerabilities. Do not run `npm audit fix --force` blindly; inspect package reachability and available fixed versions when a relevant hardening package is scheduled.

## 8. Resume rule

Read `AGENTS.md`, `docs/PROJECT_STATE.md`, `docs/BRANCH_STATUS.md`, `docs/checkpoints/LATEST.md`, the checkpoint referenced there, D-0071/D-0072/D-0073/D-0075 and `docs/ROADMAP.md`.

Resume Wave 7 from current `main` with the Adventure/Scene Spine — lightweight local core. Routine safe green boundaries do not require separate owner confirmation.
