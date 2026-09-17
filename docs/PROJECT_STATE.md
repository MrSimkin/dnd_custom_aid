# Project State — global repository navigation

**Last reconstructed:** 2026-09-17 (Chile local time)  
**Owner integrated-MVP implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified runtime/integration merge:** `013abbb9e57af0ba04fe1e8b678e8ed29522bedd` (PR #46)  
**Post-merge Scaffold:** `35220099721` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 reusable-content persistence foundation:** INTEGRATED  
**Current normal wave:** Wave 6 — reusable/persistent content architecture continues

## 1. Current topology

`main` is the sole normal integrated-MVP trunk. New implementation work uses short-lived outcome-oriented branches from current `main`.

PR #46 `feat: add Wave 6 reusable content persistence foundation` is merged. Its final branch head was `008b196ec1fc36cbd637cfbb8b2b4915109ddc8d`; exact-head Scaffold `35219548535` succeeded; the PR-triggered Scaffold `35219863619` also succeeded; merge commit is `013abbb9e57af0ba04fe1e8b678e8ed29522bedd`; post-merge Scaffold `35220099721` succeeded.

No Wave 5 application/provider work or Wave 6 foundation work should be repeated without new defect evidence.

## 2. Wave 5 acceptance baseline

Wave 5 verified real behavior includes:

- Desktop local campaign/workbench persistence;
- real Descope email-OTP authentication;
- hosted campaign bootstrap/convergence;
- authoritative member roster;
- DM rows protected from Player moderation;
- Player moderation `ACTIVE -> KICKED -> BANNED -> KICKED`;
- authoritative campaign revisions `0 -> 1 -> 2 -> 3`;
- `LIFT_BAN = BANNED -> KICKED`;
- canonical Outlook DEV owner/DM migration, final campaign revision `4`;
- Outlook roster `DM / ACTIVE`, Gmail no current campaign membership;
- Outlook Desktop bootstrap `1 hosted / 1 applied / 0 conflicts`;
- font/theme visual previews and persisted device-local settings;
- hosted session intentionally not persisted across application shutdown;
- Outlook reauthentication after relaunch;
- explicit sign-out clearing hosted state without deleting local campaigns/settings.

Historical Gmail mutation/audit evidence remains truthful and unchanged.

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

Existing DEV Worker: `dnd-custom-aid-api` at `https://dnd-custom-aid-api.mrsimkin-dev.workers.dev`.

Wave 5 deployment was already performed and verified. Documentation-only changes do not require redeployment. Deploy again only if Worker code materially changes or newer evidence specifically requires it.

## 4. Durable provider rules

Provider work requires an explicit capability check. If the worker lacks authenticated provider capability, establish that once, stop alternate connection probing, finish safe repo/code/test/CI work, hand the owner one exact bounded action packet, and resume from non-secret evidence.

Never request/paste/commit passwords, OTPs, provider tokens, DB credentials/connection strings, JWTs, private keys/signing credentials or secret environment values.

Green CI does not prove deployment/auth/provider behavior. Completed real-provider evidence should not be repeated merely because docs changed.

Normal hosted DEV owner/DM identity is Outlook-backed. Gmail is historical/inactive by default and may be deliberately reused as a secondary identity only when a future multi-user test needs it.

## 5. Wave 6 approved semantic baseline and integrated foundation

Existing Shared spine already provides:

- `ContentScope` with Personal/Campaign/System/Official variants;
- `CopyProvenance`;
- `ScopedObjectIdentity.independentCampaignCopy()` assigning a new object ID and retained provenance;
- `Revision` / stale-write checks;
- sync metadata/tombstone semantics;
- local integrated-spine persistence for accounts/memberships/PC authority/object sync state.

PR #46 integrated the first bounded Wave 6 reusable-content persistence foundation:

- family/catalog metadata for Creature, NPC, Homebrew/Rule, Place, Zone and Encounter;
- Personal and Campaign scoped creation/listing;
- explicit independent Personal -> Campaign copy retaining provenance;
- optimistic revisions, stale-write rejection, tombstones and non-resurrection behavior;
- local SQLDelight persistence and migration `18.sqm`;
- safe Desktop migration from the verified unversioned Wave-5 local schema;
- fail-closed refusal of unknown unversioned Desktop databases;
- invariant, migration and Desktop reopen/migration tests.

Approved product semantics continue to require reusable Personal DM material, explicit independent Campaign copies, provenance visibility and no automatic inheritance/update after copy.

## 6. Wave 6 continuation

Wave 6 remains active. The next bounded package must be selected from the current architecture, decisions and roadmap at resume time; this document does not predeclare a later implementation as existing.

The first foundation deliberately did **not** implement:

- large Manager UI;
- hosted reusable-content sync;
- object-storage/provider activation;
- a universal executable content payload model.

Those areas remain future work unless a later integrated checkpoint supersedes this state. Domain-specific payloads should build on the integrated foundation rather than forcing all reusable content into one giant abstraction.

## 7. Security/cost residuals

Hard external-service operating budget remains USD $0. Repository is intentionally public. Object-storage provider selection remains deferred.

Known residual: owner-local backend install reported 3 high-severity npm vulnerabilities. Do not run `npm audit fix --force` blindly; inspect packages/reachability/fixed versions when a relevant hardening package is scheduled.

## 8. Resume rule

Read `docs/checkpoints/LATEST.md`, its referenced checkpoint, `docs/BRANCH_STATUS.md`, then the relevant D-0071/D-0072/D-0073/D-0075 records and `docs/ROADMAP.md`. Resume Wave 6 from current `main` on a short-lived outcome branch after determining the next bounded package. Do not resume old Wave 5 branches, rebuild the integrated Wave 6 foundation, or redeploy Cloudflare for documentation changes.