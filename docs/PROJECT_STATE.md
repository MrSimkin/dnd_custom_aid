# Project State — global repository navigation

**Last reconstructed:** 2026-09-17 (Chile local time)  
**Owner integrated-MVP implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified runtime/integration merge:** `5762058645ba8af8fa470dcf185bd2b418af65e9` (PR #48)  
**Post-merge Scaffold:** `35253188344` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 reusable-content persistence foundation:** INTEGRATED  
**Wave 6 Creature payload persistence:** INTEGRATED  
**Current normal wave:** Wave 6 — reusable/persistent content architecture continues  
**Next bounded package:** NPC payload + local persistence core

## 1. Current topology

`main` is the sole normal integrated-MVP trunk. New implementation work uses short-lived outcome-oriented branches from current `main`.

PR #46 `feat: add Wave 6 reusable content persistence foundation` is merged as `013abbb9e57af0ba04fe1e8b678e8ed29522bedd`; post-merge Scaffold `35220099721` succeeded.

PR #48 `feat: add Wave 6 Creature payload persistence` is merged. Its final branch head was `8a9ec6425c697472a6c57982acbe8e4001b4fc5d`; replacement PR Scaffold `35252554882` succeeded; merge commit is `5762058645ba8af8fa470dcf185bd2b418af65e9`; post-merge Scaffold `35253188344` succeeded.

No Wave 5 application/provider work, Wave 6 reusable-content foundation work, or integrated Creature payload work should be repeated without new defect evidence.

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

Wave 5 deployment was already performed and verified. Documentation-only and local/shared persistence changes do not require redeployment. Deploy again only if Worker code materially changes or newer evidence specifically requires it.

## 4. Durable provider rules

Provider work requires an explicit capability check. If the worker lacks authenticated provider capability, establish that once, stop alternate connection probing, finish safe repo/code/test/CI work, hand the owner one exact bounded action packet, and resume from non-secret evidence.

Never request/paste/commit passwords, OTPs, provider tokens, DB credentials/connection strings, JWTs, private keys/signing credentials or secret environment values.

Green CI does not prove deployment/auth/provider behavior. Completed real-provider evidence should not be repeated merely because docs changed.

Normal hosted DEV owner/DM identity is Outlook-backed. Gmail is historical/inactive by default and may be deliberately reused as a secondary identity only when a future multi-user test needs it.

## 5. Wave 6 approved semantic baseline and integrated foundation

Existing Shared spine provides:

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

## 6. Integrated Creature payload package

PR #48 builds the first domain-specific payload on that foundation without introducing a universal executable content model.

Integrated scope includes:

- human-complete, selectively structured Creature payload/domain representation;
- local SQLDelight Creature payload persistence linked to reusable-content identity;
- Personal/Campaign Creature creation and read flows;
- explicit Personal -> Campaign copy retaining provenance and creating an independent campaign object;
- payload mutation through the existing optimistic revision/stale-write/tombstone semantics;
- migration `19.sqm` with safe metadata-only Creature backfill;
- focused persistence and invariant tests.

The original PR CI exposed only a synthetic Desktop legacy-fixture mismatch. Repair commit `8a9ec6425c697472a6c57982acbe8e4001b4fc5d` corrected the test fixture to remove the newly introduced `creature_payload` table when simulating a pre-Wave-6 database; production migration logic was not changed. Replacement and post-merge CI both passed.

## 7. Wave 6 continuation — NPC payload core

The next bounded package is **NPC payload + local persistence core**.

Required semantic shape from D-0072:

- Quick NPC and Developed NPC are both first-class valid states;
- no combat stat block is required for a valid NPC;
- human-facing identity, concept, appearance, personality/manner, wants/fears/needs, offers, limits/refusals, relationships/context and notes may exist without combat mechanics;
- richer Developed NPC material may add motivations, values, relationships, history, secrets, knowledge, goals, resources, affiliations, places, adventure/scene links and DM guidance;
- optional full combat mechanics should reuse the Creature/stat-block machinery rather than introduce a second incompatible combat representation;
- there is no completion score or requirement to fill every field;
- Personal -> Campaign copies remain independent after explicit copy/use, with retained provenance.

Keep this package bounded to domain payload + local persistence/revision/copy behavior. It does **not** include large NPC Manager UI, import/export, AI helper flows, hosted reusable-content sync, object storage or provider activation.

## 8. Security/cost residuals

Hard external-service operating budget remains USD $0. Repository is intentionally public. Object-storage provider selection remains deferred.

Known residual: owner-local backend install reported 3 high-severity npm vulnerabilities. Do not run `npm audit fix --force` blindly; inspect packages/reachability/fixed versions when a relevant hardening package is scheduled.

## 9. Resume rule

Read `docs/checkpoints/LATEST.md`, its referenced checkpoint, `docs/BRANCH_STATUS.md`, relevant D-0071/D-0072/D-0073/D-0075 records and `docs/ROADMAP.md`.

Resume from current `main` on a new short-lived branch for the NPC payload + local persistence core. Reuse the integrated reusable-content and Creature seams; do not rebuild prior Wave 6 packages, force all domain payloads into a universal abstraction, or redeploy Cloudflare for local/shared persistence work.
