# DEV canonical Outlook identity migration complete

**Date:** 2026-09-16 (Chile local time)  
**Branch:** `wave5/desktop-hosted-campaign-administration`  
**PR:** #44 — draft  
**Gate result:** **CANONICAL DEV IDENTITY MIGRATION PASS**

## Decision

The owner standardized future normal hosted DEV testing on the Outlook-backed application identity.

- Outlook application identity is the canonical DEV owner/DM account.
- Gmail application identity remains historical/inactive by default.
- Gmail may be used later only when a deliberate secondary account is useful for a multi-user test.
- Existing Gmail `app_user` and historical `mutation_receipt` rows remain valid historical evidence and must not be rewritten merely to make old activity appear to belong to Outlook.

## Verified pre-migration state

Read-only diagnostics established:

- hosted campaign `Hosted Batch Test` existed and was undeleted;
- campaign revision was `3`;
- Gmail application identity was the sole `DM / ACTIVE` membership;
- Outlook application identity had no campaign membership;
- no PC in the hosted campaign referenced Gmail or Outlook as owner/controller;
- no PC in another campaign referenced Gmail as owner/controller;
- Gmail retained historical mutation receipts.

The earlier attempted migration correctly rolled back because it assumed the Outlook QA Player fixture still existed. Diagnostics then established the actual current state before retrying.

## Migration result

The owner executed the revised controlled Neon transaction based on the actual state.

Post-transaction verification returned exactly one membership row for `Hosted Batch Test`:

- Outlook app_user `f34bc5f0-4d35-4d09-b771-505b3851440c`;
- role `DM`;
- status `ACTIVE`;
- campaign revision `4`.

This confirms the active hosted campaign authority has moved from Gmail to Outlook.

## Historical evidence policy

The migration does not retroactively rewrite earlier activity:

- Gmail `app_user` remains present;
- historical Gmail mutation receipts remain associated with Gmail;
- campaign moderation history remains reflected by revisions `0 -> 1 -> 2 -> 3`;
- the administrative canonical-identity migration advanced the campaign once more to revision `4`.

## Current owner/manual gate

Desktop must now verify the new canonical identity through the real application path:

1. sign out of any current Gmail hosted session;
2. authenticate Desktop using the Outlook DEV identity through normal email OTP;
3. let hosted campaign bootstrap complete;
4. verify `Hosted Batch Test` appears;
5. select/open it if necessary;
6. open Campaign Administration;
7. refresh the hosted roster;
8. verify Outlook resolves as `DM / ACTIVE` and displayed roster/campaign revision is `4`;
9. verify no Gmail membership row appears.

After that, complete the remaining settings-persistence and sign-out/relaunch QA before PR #44 readiness review.

## Safety

- Do not delete either Descope user merely because Outlook is canonical.
- Do not rewrite historical mutation receipts.
- Do not reset campaign revision `4`.
- Do not create another canonical DM identity unless the owner deliberately changes this decision.
