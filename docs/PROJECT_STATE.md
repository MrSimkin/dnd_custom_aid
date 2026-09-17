# Project State — global repository navigation

**Last reconstructed:** 2026-09-17 (Chile local time)  
**Owner integrated-MVP implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified runtime/integration merge:** `8be8ec82702a782c65b2d6aedf9bbe4b5b58f240` (PR #67)  
**Post-merge Scaffold:** `35279329344` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Wave 7:** ACTIVE — Desktop authoring Managers  
**Integrated Wave 7 packages:** Desktop Creature/Monster Manager + Desktop NPC Manager + Desktop Homebrew & Rules lightweight local core + Desktop Place/Shop Manager local core  
**Next bounded package:** Desktop Stage Manager — Place retrieval/organization core

## 1. Current topology

`main` is the sole normal integrated-MVP trunk. New work uses short-lived outcome-oriented branches from current `main`.

Do not repeat completed Wave 5, Wave 6, Creature Manager, NPC Manager, Homebrew/Rules Manager or Place/Shop Manager work without new defect evidence.

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

Wave 6 and the integrated Wave 7 Creature/NPC/Homebrew/Place Manager packages did not require Worker changes or redeployment. Deploy again only when Worker code materially changes or newer evidence requires it.

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

PR #67 integrated the fourth concrete Desktop Manager using the existing Wave 6 Place persistence.

It supports:

- Personal and active-Campaign Place browse/search/create/open/edit;
- both existing `PLACE` and `SHOP` kinds, with Shop remaining a specialized Place;
- full current Place payload authoring: summary, area, function, presentation, services, interactives, hooks, player-safe text, DM notes, paper references and tags;
- scope, provenance and revision visibility;
- explicit Personal -> Campaign independent copy;
- atomic display-name + payload update under one optimistic revision.

Personal authoring uses the same conservative uniquely-resolvable local DM identity rule as the preceding Managers. Stale/deleted writes cannot overwrite or resurrect content, and Campaign copies remain independent after copy.

Validation:

- implementation head `f4bb75f4b2872ebc6a1dc4302cd4890367e4618c`;
- push Scaffold `35278740631` — SUCCESS;
- PR Scaffold `35279052405` — SUCCESS;
- PR #67 merged as `8be8ec82702a782c65b2d6aedf9bbe4b5b58f240`;
- post-merge Scaffold `35279329344` — SUCCESS.

No database migration, Worker change or provider action was required.

## 6. Next Wave 7 package — Desktop Stage Manager

Next bounded package: **Desktop Stage Manager — Place retrieval/organization core**.

Build on the integrated Place/Shop Manager and existing Place persistence. Do not create a separate Stage persistence family merely to organize Places. The current Place payload already contains kind, summary, area, function, presentation, services, interactives, hooks, player-safe text, DM notes, paper references and tags; reusable-content metadata provides scope/provenance/revision and timestamps.

Initial scope:

- present Places/Shops as the Stage preparation collection;
- richer retrieval/filter/grouping using existing Place data, especially kind, area, function, tags, scope and recent updates;
- preserve existing Place create/open/edit/copy semantics and atomic optimistic-revision saves;
- keep active Campaign context visible;
- make only the smallest refactor necessary to share Place behavior;
- focused coverage for retrieval/filtering and stable state/selection.

Do not pull Scene Spine into this package. D-0072's lightweight Adventure/Scene Spine is the following concrete package. Current source has no `SCENE` reusable-content family, no Scene payload repository and no Scene schema, so that later package must own any minimal Scene persistence extension explicitly.

Dungeon/Zone, Encounter, PC Manager/Audit, Media/Handouts and deferred richer Homebrew families remain later Wave 7 packages.

## 7. Security/provider boundaries

Repository is intentionally public. Never request, paste or commit secrets, credentials, OTPs, tokens, DB connection strings, JWTs or private keys.

Green CI does not prove provider/deployment behavior. Do not repeat completed provider verification merely because docs/local persistence changed.

Object-storage provider selection remains deferred until Media/Handouts/assets concretely require it.

Known residual: owner-local backend install reported 3 high-severity npm vulnerabilities. Do not run `npm audit fix --force` blindly; inspect package reachability and available fixed versions when a relevant hardening package is scheduled.

## 8. Resume rule

Read `AGENTS.md`, `docs/PROJECT_STATE.md`, `docs/BRANCH_STATUS.md`, `docs/checkpoints/LATEST.md`, the checkpoint referenced there, D-0071/D-0072/D-0073/D-0075 and `docs/ROADMAP.md`.

Resume Wave 7 from current `main` with the Desktop Stage Manager — Place retrieval/organization core. Routine safe green boundaries do not require separate owner confirmation.
