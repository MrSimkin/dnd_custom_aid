# Project State — global repository navigation

**Last reconstructed:** 2026-09-17 (Chile local time)  
**Owner integrated-MVP implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified runtime/integration merge:** `12a62288457ebe5892f90f637fe41c142b094591` (PR #61)  
**Post-merge Scaffold:** `35267674641` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Wave 7:** ACTIVE — Desktop authoring Managers  
**Integrated Wave 7 package:** Desktop Creature/Monster Manager local authoring core  
**Next bounded package:** Desktop NPC Manager — local authoring core

## 1. Current topology

`main` is the sole normal integrated-MVP trunk. New work uses short-lived outcome-oriented branches from current `main`.

Do not repeat completed Wave 5, Wave 6 or integrated Wave 7 Creature Manager work without new defect evidence.

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

No Wave 6 or first Wave 7 Manager package required Worker changes or redeployment. Deploy again only when Worker code materially changes or newer evidence requires it.

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

## 5. Wave 7 integrated package — Creature/Monster Manager

PR #61 integrates the first visible Desktop authoring Manager.

The existing Desktop `MANAGERS` destination now supports:

- browse/search Personal and active-Campaign Creatures;
- create Personal or active-Campaign Creatures;
- open/edit the existing human-complete Creature/stat-block payload;
- scope, provenance and revision visibility;
- explicit Personal -> active Campaign independent copy;
- active campaign context while authoring.

Display name + Creature payload save atomically under one optimistic revision. Stale/deleted writes cannot silently overwrite or resurrect content.

Personal authoring uses only a uniquely resolvable locally persisted active DM account; ambiguity disables Personal creation rather than guessing. Campaign-local authoring remains available.

Validation:

- implementation head `38d68dc832188f29c76ec40990297f53a85e9bed`;
- push Scaffold `35267065066` — SUCCESS;
- PR Scaffold `35267241770` — SUCCESS;
- PR #61 merged as `12a62288457ebe5892f90f637fe41c142b094591`;
- post-merge Scaffold `35267674641` — SUCCESS.

Official/SRD browsing, import/export, Creature Creator Assistant/advisory balancing, media/object storage, hosted reusable-content sync, generalized all-Manager abstractions and live combat remain deferred.

## 6. Next Wave 7 package — Desktop NPC Manager

Next bounded package: **Desktop NPC Manager — local authoring core**.

Reuse the integrated `NpcPayload` / `NpcContentRepository` and D-0072 semantics:

- Quick NPC -> Developed NPC -> optional combat mechanics;
- incomplete NPCs remain valid;
- combat mechanics are optional and reuse `CreaturePayload`;
- Personal/Campaign independence and provenance remain unchanged.

Initial scope:

- browse/search Personal + active-Campaign NPCs;
- create/open/edit Quick and Developed NPC fields;
- optionally add/remove/edit combat mechanics;
- show scope/provenance/revision;
- explicit Personal -> active Campaign copy;
- preserve stale-write/tombstone behavior.

Defer NPC assistant/AI ideation, import/export, live-improvisation promotion workflow, media/object storage, hosted reusable-content sync and broad Manager generalization.

## 7. Security/provider boundaries

Repository is intentionally public. Never request, paste or commit secrets, credentials, OTPs, tokens, DB connection strings, JWTs or private keys.

Green CI does not prove provider/deployment behavior. Do not repeat completed provider verification merely because docs/local persistence changed.

Object-storage provider selection remains deferred until Media/Handouts/assets concretely require it.

Known residual: owner-local backend install reported 3 high-severity npm vulnerabilities. Do not run `npm audit fix --force` blindly; inspect package reachability and available fixed versions when a relevant hardening package is scheduled.

## 8. Resume rule

Read `docs/checkpoints/LATEST.md`, the referenced Creature Manager checkpoint, `docs/BRANCH_STATUS.md`, D-0071/D-0072/D-0073/D-0075 and `docs/ROADMAP.md`.

Resume Wave 7 from current `main` with the Desktop NPC Manager local authoring core. Routine safe green boundaries do not require separate owner confirmation.
