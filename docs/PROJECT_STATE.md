# Project State — global repository navigation

**Last reconstructed:** 2026-09-17 (Chile local time)  
**Owner integrated-MVP implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified runtime/integration merge:** `306377df1a453f531af4b670d2b231c88a3c9419` (PR #44)  
**Post-merge Scaffold:** `35168920031` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Next normal wave:** Wave 6 — reusable/persistent content architecture

## 1. Current topology

`main` is the sole normal integrated-MVP trunk. New implementation work uses short-lived outcome-oriented branches from current `main`.

PR #44 `feat: add Desktop hosted campaign administration` is merged. Its final branch head was `20f62b110df80759b5e90d083253b3b87716ff31`; exact-head pre-merge Scaffold `35168771704` succeeded; merge commit is `306377df1a453f531af4b670d2b231c88a3c9419`; post-merge Scaffold `35168920031` succeeded with backend, hosted-database and Kotlin/build/test/APK jobs green.

No Wave 5 application/provider work should be repeated without new defect evidence.

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

## 5. Wave 6 approved semantic baseline

Existing Shared spine already provides:

- `ContentScope` with Personal/Campaign/System/Official variants;
- `CopyProvenance`;
- `ScopedObjectIdentity.independentCampaignCopy()` assigning a new object ID and retained provenance;
- `Revision` / stale-write checks;
- sync metadata/tombstone semantics;
- local integrated-spine persistence for accounts/memberships/PC authority/object sync state.

Approved product semantics further require reusable Personal DM material, explicit Personal -> Campaign independent copies, provenance visibility, no automatic inheritance/update after copy, recoverable/tombstoned durable content where applicable, and dependency-aware copies for reusable Encounters when needed.

## 6. Recommended first Wave 6 package

Build a bounded reusable-content persistence foundation on top of the existing spine:

- reusable-content identity/family/scope/provenance envelope;
- local SQLDelight persistence and migration;
- explicit independent Personal -> Campaign copy operation;
- optimistic revision/tombstone/non-resurrection behavior;
- scope/family browsing primitives needed by later Managers;
- invariant/migration tests.

Do not build large Manager UI, hosted sync, object storage, or a universal executable rules model in this first package. Domain-specific Monster/NPC/Homebrew/Place/Encounter payload schemas should build on the foundation rather than being forced into one giant universal abstraction.

No owner product decision or external-provider handoff is currently required for this package.

## 7. Security/cost residuals

Hard external-service operating budget remains USD $0. Repository is intentionally public. Object-storage provider selection remains deferred.

Known residual: owner-local backend install reported 3 high-severity npm vulnerabilities. Do not run `npm audit fix --force` blindly; inspect packages/reachability/fixed versions when a relevant hardening package is scheduled.

## 8. Resume rule

Read `docs/checkpoints/LATEST.md`, its referenced checkpoint, `docs/BRANCH_STATUS.md`, then the relevant D-0071/D-0072/D-0073/D-0075 records. Start Wave 6 from current `main` on a short-lived outcome branch. Do not resume old Wave 5 branches or redeploy Cloudflare for documentation changes.