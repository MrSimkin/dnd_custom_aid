# DEV identity standardization — Outlook canonical migration preflight

**Date:** 2026-09-16 (Chile local time)  
**Branch:** `wave5/desktop-hosted-campaign-administration`  
**PR:** #44 — draft  
**Gate result:** **CANONICAL IDENTITY DECISION RECORDED / DATA MIGRATION PENDING**

## Owner decision

For future hosted DEV testing, the Outlook-backed DEV identity is the canonical owner/DM test identity.

The Gmail-backed DEV identity becomes historical/inactive by default. It may later be reused deliberately as a secondary Player identity, but future normal owner/DM testing should use Outlook unless a specific test says otherwise.

Do not rewrite or delete historical audit/idempotency evidence merely to make history appear to have been performed by Outlook. Existing `app_user` rows and historical `mutation_receipt` rows remain truthful history.

## Current verified DEV database state after rollback + read-only diagnostics

The previous attempted migration aborted and rolled back.

Current state:

- campaign `Hosted Batch Test` remains undeleted at revision `3`;
- exactly one ACTIVE DM membership exists;
- Gmail application user `4ba0f476-2eba-4eff-b4e0-78bb9372a8b4` is that `DM / ACTIVE` member;
- Outlook application user `f34bc5f0-4d35-4d09-b771-505b3851440c` currently has **no campaign membership row**;
- no PC in `Hosted Batch Test` references either Gmail or Outlook as owner/controller;
- no PC in another campaign references Gmail as owner/controller;
- Gmail has 12 historical mutation receipts in the inspected DEV database; Outlook has none in the reported result;
- no provider identity or app-user deletion is authorized.

The reason the prior migration failed is therefore narrowed: it assumed Outlook still had the temporary `PLAYER / KICKED` membership, but the actual post-rollback state has no Outlook membership row.

## Intended migration semantics

Perform one controlled administrative migration:

1. verify the above current state again inside one transaction;
2. create Outlook as `DM / ACTIVE` for `Hosted Batch Test`;
3. remove Gmail's `DM / ACTIVE` membership from that campaign;
4. advance the campaign revision exactly once from `3` to `4` for this logical administrative state change;
5. preserve both `app_user` rows;
6. preserve historical `mutation_receipt` rows unchanged;
7. do not delete the campaign, PCs, Descope users or provider resources.

After migration, the expected active hosted state is exactly one membership:

- Outlook -> `DM / ACTIVE`;
- campaign revision -> `4`.

Gmail should have no campaign membership in `Hosted Batch Test` unless later deliberately reintroduced for a secondary-user test.

## Next owner/provider gate

Run the revised transaction built for the actual current state (Outlook membership absent). If any precondition fails, abort/rollback and inspect rather than weakening safeguards blindly.

After success:

1. sign out of Gmail in Desktop;
2. sign in with Outlook;
3. bootstrap hosted campaigns;
4. confirm `Hosted Batch Test` appears;
5. verify Outlook resolves as `DM / ACTIVE` and roster revision/campaign revision is `4`;
6. continue final settings persistence + sign-out/relaunch QA.

## Safety

- never expose provider secrets, OTPs, JWTs or `DATABASE_URL`;
- do not rewrite historical mutation receipts;
- do not delete Gmail or Outlook `app_user` rows;
- do not reset campaign revision history;
- do not use destructive cleanup merely to make QA pass.
