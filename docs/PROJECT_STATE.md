# Project State — global repository navigation

**Last reconstructed:** 2026-09-17 (Chile local time)  
**Owner integrated-MVP implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified runtime/integration merge:** `6febe3f936593999834189b92aeda9d209385fa7` (PR #65)  
**Post-merge Scaffold:** `35277359425` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Wave 7:** ACTIVE — Desktop authoring Managers  
**Integrated Wave 7 packages:** Desktop Creature/Monster Manager + Desktop NPC Manager + Desktop Homebrew & Rules lightweight local authoring cores  
**Next bounded package:** Desktop Place/Shop Manager — local authoring core

## 1. Current topology

`main` is the sole normal integrated-MVP trunk. New work uses short-lived outcome-oriented branches from current `main`.

Do not repeat completed Wave 5, Wave 6, Creature Manager, NPC Manager or Homebrew/Rules Manager work without new defect evidence.

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

Wave 6 and the integrated Wave 7 Creature/NPC/Homebrew Manager packages did not require Worker changes or redeployment. Deploy again only when Worker code materially changes or newer evidence requires it.

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

Encounter dependency copy/remap remains domain-specific; no generalized dependency graph was introduced.

## 5. Wave 7 integrated Managers

### Creature/Monster Manager

PR #61 integrated the first visible Desktop authoring Manager. It supports Personal + active-Campaign Creature browsing/search, create/open/edit, scope/provenance/revision visibility and explicit Personal -> Campaign independent copy. Display name + Creature payload save atomically under one optimistic revision.

Validation:

- implementation head `38d68dc832188f29c76ec40990297f53a85e9bed`;
- push Scaffold `35267065066` — SUCCESS;
- PR Scaffold `35267241770` — SUCCESS;
- merged as `12a62288457ebe5892f90f637fe41c142b094591`;
- post-merge Scaffold `35267674641` — SUCCESS.

### NPC Manager

PR #63 integrated the second concrete Desktop Manager. It supports Personal/Campaign browse, create and edit for Quick -> Developed NPC data, optional combat mechanics through `CreaturePayload`, scope/provenance/revision visibility, explicit Personal -> Campaign independent copy and atomic name + payload updates. Incomplete NPCs remain valid.

Validation:

- implementation head `58b680e71ec59c871854eb9c083ff2bc6906fe88`;
- push Scaffold `35269013875` — SUCCESS;
- PR Scaffold `35269163375` — SUCCESS;
- merged as `58a565c3a33a433ce47e7fd4ac1185b5f980644f`;
- post-merge Scaffold `35270643883` — SUCCESS.

### Homebrew & Rules Manager

PR #65 integrated the third concrete Desktop Manager using the existing lightweight Homebrew/Rule persistence.

It supports:

- browse/search Personal and active-Campaign rule records;
- create/open/edit title, summary/body, category, rationale, examples, related references, tags and notes;
- Draft / Active / Retired lifecycle;
- scope, provenance and revision visibility;
- explicit Personal -> Campaign independent copy;
- atomic display-name + payload update under one optimistic revision.

Personal authoring uses the same conservative uniquely-resolvable local DM identity rule as Creature/NPC. Stale/deleted writes cannot overwrite or resurrect content, and Campaign copies remain independent after copy.

Validation:

- implementation head `17f4cd686114f734ab0bc50f453de9d579a52c79`;
- push Scaffold `35272269481` — SUCCESS;
- PR Scaffold `35272560206` — SUCCESS;
- PR #65 merged as `6febe3f936593999834189b92aeda9d209385fa7`;
- post-merge Scaffold `35277359425` — SUCCESS.

The lightweight Homebrew Manager does not claim completion of structured races/classes/subclasses/backgrounds/feats/spells/items, official/SRD customization, import/export, homebrew-aware AI, media/object storage, hosted reusable-content sync or generalized Manager abstractions.

## 6. Next Wave 7 package — Desktop Place/Shop Manager

Next bounded package: **Desktop Place/Shop Manager — local authoring core**.

Reuse the integrated Wave 6 Place persistence family and D-0072 Stage/Place direction rather than expanding persistence prematurely. Shops are specialized Places; expose only behavior supported by the integrated Place payload unless concrete implementation evidence requires a bounded extension.

Initial scope:

- browse/search Personal + active-Campaign Places;
- create/open/edit the existing Place payload;
- show scope/provenance/revision;
- explicit Personal -> active Campaign independent copy;
- atomic display-name + payload update through the existing optimistic revision spine;
- preserve stale-write/tombstone behavior and the same conservative Personal-owner identity rule used by the preceding Managers;
- focused coverage for persistence, atomic edit, stale-write rejection, provenance and copy independence.

Do not pull Scene Spine into this first package. Scene persistence is not yet an integrated family and should be introduced only with a concrete bounded Stage/Scene requirement rather than turning Place authoring into a speculative schema package.

Dungeon/Zone, Encounter, PC Manager/Audit, Media/Handouts, richer Stage/Scene work and the deferred richer Homebrew families remain later Wave 7 packages.

## 7. Security/provider boundaries

Repository is intentionally public. Never request, paste or commit secrets, credentials, OTPs, tokens, DB connection strings, JWTs or private keys.

Green CI does not prove provider/deployment behavior. Do not repeat completed provider verification merely because docs/local persistence changed.

Object-storage provider selection remains deferred until Media/Handouts/assets concretely require it.

Known residual: owner-local backend install reported 3 high-severity npm vulnerabilities. Do not run `npm audit fix --force` blindly; inspect package reachability and available fixed versions when a relevant hardening package is scheduled.

## 8. Resume rule

Read `AGENTS.md`, `docs/PROJECT_STATE.md`, `docs/BRANCH_STATUS.md`, `docs/checkpoints/LATEST.md`, the checkpoint referenced there, D-0071/D-0072/D-0073/D-0075 and `docs/ROADMAP.md`.

Resume Wave 7 from current `main` with the Desktop Place/Shop Manager local authoring core. Routine safe green boundaries do not require separate owner confirmation.
