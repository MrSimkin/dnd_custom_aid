# Project State — global repository navigation

**Last reconstructed:** 2026-09-17 (Chile local time)  
**Owner integrated-MVP implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified runtime/integration merge:** `81303bf875457bd1fa0a9ce70d7a4e71eaad9edd` (PR #59)  
**Post-merge Scaffold:** `35265162945` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Current normal wave after docs closure:** Wave 7 — Desktop authoring Managers  
**Next bounded package:** Desktop Creature/Monster Manager — local authoring core

## 1. Current topology

`main` is the sole normal integrated-MVP trunk. New work uses short-lived outcome-oriented branches from current `main`.

Do not repeat completed Wave 5 work or integrated Wave 6 packages without new defect evidence.

## 2. Integrated Wave 5 baseline

Wave 5 verified Desktop behavior includes:

- local campaign/workbench persistence;
- real Descope email-OTP authentication;
- hosted campaign bootstrap/convergence;
- authoritative campaign member administration;
- canonical Outlook DEV owner/DM identity;
- persisted device-local settings;
- explicit hosted-session lifecycle and sign-out semantics.

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

No Wave 6 package required Worker changes or redeployment. Deploy again only when Worker code materially changes or newer evidence requires it.

Hard external-service operating budget remains USD $0.

## 4. Wave 6 integrated architecture

The shared reusable-content spine now provides:

- `ContentScope` Personal/Campaign semantics;
- stable reusable-content identity;
- family/catalog metadata;
- provenance for independent Personal -> Campaign copies;
- optimistic revisions and stale-write rejection;
- tombstones/non-resurrection;
- sync metadata/invariants;
- local SQLDelight persistence and migrations;
- verified reopen/migration behavior.

Approved product semantics remain:

- Personal DM material is reusable;
- Personal -> Campaign use creates a **new independent Campaign object ID**;
- provenance may remain visible;
- later Personal-master edits do not automatically change Campaign copies.

## 5. Integrated reusable families

### Creature — PR #48

Human-complete selectively structured Creature payload, local persistence, Personal/Campaign create/read/copy/update/tombstone, migration `19.sqm`, stale-write/non-resurrection and reopen/migration coverage.

### NPC — PR #51

Quick and Developed NPCs are both valid. Combat mechanics are optional and may reuse `CreaturePayload` without hidden duplicate Creature objects. Includes independent copy/provenance and migration `20.sqm`.

### Homebrew/Rule — PR #53

Lightweight rule/ruling/custom-system record with summary/body/category/rationale/examples/references/tags/notes and `DRAFT / ACTIVE / RETIRED` lifecycle. Includes migration `21.sqm`.

Structured races/classes/backgrounds/feats/spells/items remain family-appropriate later work rather than one universal arbitrary payload.

### Place — PR #55

Canonical Place records with `PLACE` / `SHOP`, summary, area, function, presentation, services/interactives, hooks, player-safe text, DM notes, paper references and tags. Includes migration `22.sqm`.

Shops remain specialized Places rather than a separate top-level family.

### Zone — PR #57

Prepared Zone / Zone Brief records with summary, area/context, presentation, space/layout, exploration, interactives, clues, checks, consequences, encounter orientation, DM guidance, player-safe text, paper references and tags. Includes migration `23.sqm`.

Prepared Zone Briefs remain distinct from live Dungeon Turn movement-zone state.

### Encounter — PR #59

Saved/prepared Encounter records with summary, environment, context, DM guidance, participants, tags and notes. Participant entries support Creature/NPC references or freeform entries, quantity, `EXPECTED / RESERVE / CONDITIONAL` readiness, condition text, encounter-local overrides and notes.

Personal -> Campaign Encounter copy validates dependencies, copies each unique referenced Personal Creature/NPC once, remaps participant references to independent Campaign IDs and retains provenance without introducing a generalized dependency graph.

Persistence includes migration `24.sqm`, dependency-scope validation, copy/remap/deduplication, revision/tombstone and reopen/migration coverage.

PR #59 merged as `81303bf875457bd1fa0a9ce70d7a4e71eaad9edd`; push Scaffold `35264310753`, PR Scaffold `35264620723`, and post-merge Scaffold `35265162945` all succeeded.

## 6. Wave 6 completion boundary

The core Wave 6 reusable-content architecture is complete enough to begin Wave 7 authoring Managers.

This does **not** mean every later persistent concept has already been pre-modeled. Scene Spine, richer structured Homebrew families, clocks/readiness, media/handouts and live-state records should be introduced when their concrete approved Manager/live package requires them.

Do not extend Wave 6 indefinitely for speculative completeness.

## 7. Wave 7 entry package

First selected package:

**Desktop Creature/Monster Manager — local authoring core**

Use the existing Desktop `MANAGERS` destination and integrated `CreatureContentRepository`.

Initial bounded behavior:

- browse/search Personal Creature records;
- browse/search active-Campaign Creature records when a campaign is active;
- create Personal or active-Campaign Creatures;
- open and edit the existing human-complete Creature payload;
- display scope and provenance;
- explicitly copy Personal Creature -> active Campaign;
- preserve revision/stale-write/tombstone behavior.

Deferred from the first slice:

- Official/SRD catalog browsing;
- import/export;
- Creature Creator Assistant/advisory balancing helpers;
- media attachments/object storage;
- hosted reusable-content synchronization;
- generalized framework for every Manager.

Those are later Wave 7 packages unless a concrete dependency proves otherwise.

## 8. Security/provider boundaries

Repository is intentionally public. Never request, paste or commit secrets, credentials, OTPs, tokens, DB connection strings, JWTs or private keys.

Green CI does not prove provider/deployment behavior. Do not repeat completed provider verification merely because docs/local persistence changed.

Object-storage provider selection remains deferred until Media/Handouts/assets concretely require it.

Known residual: owner-local backend install reported 3 high-severity npm vulnerabilities. Do not run `npm audit fix --force` blindly; inspect package reachability and available fixed versions when a relevant hardening package is scheduled.

## 9. Resume rule

Read `docs/checkpoints/LATEST.md`, the referenced Encounter checkpoint, `docs/BRANCH_STATUS.md`, D-0071/D-0072/D-0073/D-0075 and `docs/ROADMAP.md`.

Finish the Encounter documentation closure, then resume Wave 7 from current `main` with the Desktop Creature/Monster Manager local authoring core. Routine safe green boundaries do not require separate owner confirmation.
