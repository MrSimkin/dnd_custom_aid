# Desktop hosted Campaign Administration — owner QA complete

**Date:** 2026-09-16 (Chile local time)  
**Branch:** `wave5/desktop-hosted-campaign-administration`  
**PR:** #44  
**Gate result:** **OWNER WINDOWS QA PASS / PACKAGE READY FOR FINAL CI + MERGE PREPARATION**

## Scope verified

The owner completed the real Windows Desktop acceptance sequence for PR #44 against the live DEV Worker and Neon database.

Verified behavior:

- Desktop launches successfully on the known-good Windows JDK 17 / Gradle 9.5 QA toolchain;
- existing local campaign data survives hosted-auth work and relaunches;
- real Descope email OTP authentication succeeds;
- hosted bootstrap and canonical campaign convergence succeed;
- real hosted member roster loads;
- DM rows expose no Player moderation controls;
- real Player moderation passed `ACTIVE -> KICKED -> BANNED -> KICKED` with authoritative roster refresh and campaign revisions `0 -> 1 -> 2 -> 3`;
- Lift Ban correctly means `BANNED -> KICKED`, not `ACTIVE`;
- DEV identity mapping was resolved as two distinct provider identities;
- the owner standardized Outlook as the canonical DEV owner/DM identity;
- the controlled Gmail -> Outlook membership migration passed, preserving historical Gmail mutation receipts;
- Outlook now resolves as the sole `DM / ACTIVE` member of `Hosted Batch Test` at campaign revision `4`;
- Desktop Outlook bootstrap returns `1 hosted / 1 applied / 0 conflicts`;
- Gmail is absent from the current campaign roster;
- font and theme choices render as visual preview cards;
- device-local preferences persist across application close/relaunch;
- the hosted session does not persist across application shutdown, as intended for memory-only JWT handling;
- reauthentication with Outlook restores `Hosted Batch Test` as `DM / ACTIVE`, revision `4`;
- explicit sign-out clears hosted session state without deleting local campaigns or resetting settings.

## Canonical DEV identity baseline

Future normal hosted DEV owner/DM testing uses the Outlook-backed application identity.

Current hosted baseline:

- campaign: `Hosted Batch Test`;
- canonical Outlook app user: `f34bc5f0-4d35-4d09-b771-505b3851440c`;
- role/status: `DM / ACTIVE`;
- campaign revision: `4`;
- Gmail: no current campaign membership;
- historical Gmail `mutation_receipt` rows remain untouched as truthful audit/idempotency history.

Gmail may be used later only when a test explicitly needs a secondary identity.

## Settings/session persistence acceptance

The final manual gate passed with deliberately visible device-local preference changes. The owner verified that font/theme previews are present, the chosen preferences survive application relaunch, local campaign data remains intact, and the hosted session starts signed out after relaunch. The owner then reauthenticated with Outlook, reconfirmed the hosted DM context, explicitly signed out, and reconfirmed local data/settings preservation.

## CI / readiness state

The immediately preceding documentation head `281275638855a1200cecee9f1beef806638d11e6` passed Scaffold `35168371109` — **SUCCESS**.

`main` remained at package base `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded` during the readiness sweep. PR #44 had no unresolved review threads and no conversation comments.

This checkpoint commit is documentation-only and still requires its own exact-head Scaffold before the PR is marked ready/merged.

## Next actions

1. wait for exact-head Scaffold on the final readiness documentation commit;
2. confirm `main` has not moved;
3. mark PR #44 ready for review;
4. merge using expected-head protection only after the exact-head checks are green;
5. verify post-merge `main` and post-merge CI;
6. advance durable project state to the next Wave 5 package.

## Safety / residuals

- hard external-service budget remains USD $0;
- do not commit or expose secrets, OTPs, provider tokens or session JWTs;
- do not rewrite historical identity/audit records;
- known backend install output still reports 3 high-severity npm dependency findings; treat these as a later explicit hardening item and do not run `npm audit fix --force` blindly;
- PR #44 non-goals remain invitations/rejoin, co-DM/role editing, PC Manager/Audit, full new PC sync architecture, Managers, Media/Handouts/object storage, Live Combat, System Administration, backup/export/PDF hardening and generalized RBAC/ACL.
