# Membership revoke + Player/DM authorization — ready for physical QA

**Date:** 2026-09-16 (Chile local time)  
**Branch:** `wave4/membership-revoke-authorization`  
**PR:** #41  
**Verified automated head:** `a188417f8173573271346246e5cc129dabdf45cc`  
**Scaffold:** `35118236579` — **SUCCESS**  
**Integrated base main:** `587a000dae7ff9b9f997dd138b0ebbaeca201256`

## Result so far

The membership-revoke and Player/DM authorization boundary is **AUTOMATED VERIFIED / PHYSICAL QA PENDING**.

Repository inspection showed that the production implementation already contains the core authorization behavior. This package therefore does not invent a new permission system or prematurely build Campaign Manager moderation UI. It adds the missing transition-focused evidence and prepares one bounded real-DEV physical gate.

## 1. Existing implementation confirmed

### Hosted backend

Hosted PC reads require:

- an `ACTIVE` membership in the campaign; and
- either DM campaign role or Player owner/controller authority for that PC.

Hosted PC writes independently re-check the same active-membership and object-authority boundary. A non-authorized request fails as `FORBIDDEN` rather than exposing hosted PC state.

### Shared/client lifecycle

The shared membership model already defines:

- `ACTIVE`;
- `KICKED`;
- `BANNED`.

Only `ACTIVE` can use the hosted campaign.

Membership bootstrap deliberately preserves the local campaign when hosted membership becomes inactive. Explicit membership lifecycle state remains authoritative even when campaign object state is conflicted. Absence from the membership response is deliberately not interpreted as removal.

Android hosted synchronization intersects explicit membership/bootstrap state with the server's active-campaign projection. An inactive campaign is therefore not eligible for PC pull/queue work after the lifecycle refresh.

A pre-existing READY mutation may be attempted before that refresh. If the server now rejects it with non-transient `FORBIDDEN`, durable outbox handling blocks/preserves it rather than deleting local work.

## 2. New automated evidence

### Dynamic revoke database contract

Added:

`database/tests/0005_membership_revoke_authorization_contract.sql`

It proves a real transition rather than merely starting with an already-inactive account:

1. an ACTIVE Player owner/controller can initially read and write the existing PC;
2. changing that membership to `KICKED` removes active-campaign projection and hosted PC read authority;
3. the same owner/controller cannot write the existing PC even with the exact current revision;
4. membership revoke does not delete the hosted PC, change its owner/controller, or advance its revision as a side effect;
5. an independently ACTIVE DM retains authority after the Player is revoked;
6. the DM can still correct the PC without becoming its owner/controller;
7. changing the Player to `BANNED` remains inactive and does not restore access;
8. revoking the DM also removes DM hosted PC read/write authority;
9. the PC remains intact after both membership revocations.

Scaffold now executes this contract after the existing hosted database contracts.

### Backend HTTP regression

Added:

`backend/test/membership-revoke-authorization.test.mjs`

It proves a revoked/unauthorized PC PUT is surfaced as:

`HTTP 403 / FORBIDDEN`

with the stable generic authorization message and no hosted-state details.

### Existing client regression reused

The existing Shared test:

`explicitKickDisablesHostedMembershipWithoutDeletingLocalCampaign`

already proves that an explicit `KICKED` state is stored locally, disables hosted campaign use, and leaves the local campaign intact.

## 3. Automated gate

Exact verified head:

`a188417f8173573271346246e5cc129dabdf45cc`

Scaffold run:

`35118236579` — **SUCCESS**

This passed:

- new dynamic revoke PostgreSQL contract;
- earlier hosted database contracts;
- backend Node tests and TypeScript checks;
- Shared/Kotlin tests;
- permanent Player guard scripts;
- Android debug build;
- Desktop build;
- debug APK artifact upload.

No production authorization code was changed by this package because the inspected implementation already satisfied the approved core predicates.

## 4. Exact physical gate

The remaining gate is one bounded real-DEV membership revoke/reinstate exercise.

Do **not** modify real DEV membership until the preflight state is recorded.

### Owner step 1 — preflight only

On the currently configured Android test device:

1. open `DnD Aid - QA DEV`;
2. tap `Ejecutar sincronización QA`;
3. tap `Copiar log QA`;
4. return the complete log to the technical-assistant chat.

The preflight should establish a clean starting point, especially:

- remembered authentication works;
- the target campaign is currently active/eligible;
- no unexplained conflict exists;
- no READY/retryable/blocked hosted mutation is unexpectedly pending.

Do not uninstall, clear app data, edit the PC for this test, or change Neon membership yet.

### After preflight review

The technical assistant will provide one read-only Neon SQL query to identify the exact DEV membership row. Only after reviewing that result will the assistant provide the bounded `ACTIVE -> KICKED` update and its exact restore statement.

The intended physical proof is:

1. current membership starts `ACTIVE`;
2. DEV membership is deliberately changed to `KICKED`;
3. Android refresh receives the explicit inactive lifecycle state;
4. the campaign becomes ineligible for hosted PC sync;
5. local campaign/PC data remains present and usable locally;
6. no destructive cleanup/reset occurs;
7. membership is restored to `ACTIVE` using the exact same identified row;
8. hosted eligibility/synchronization resumes cleanly.

The full SQL change and restore must be performed as owner/manual DEV operations. Credentials or connection secrets must never be pasted into chat or Git.

## 5. Physical-test safety boundary

Do not:

- test against production resources;
- delete the membership row;
- delete the campaign or PC;
- clear local app data;
- clear the hosted outbox;
- uninstall/reinstall as a workaround;
- modify ownership/controller identity;
- use `BANNED` for the first physical gate;
- make unrelated local PC edits during the bounded revoke interval;
- expose database credentials or Descope tokens.

Use `KICKED` because it is reversible and sufficient to prove inactive-membership enforcement. Automated coverage already verifies `BANNED` has the same inactive authorization effect.

## 6. Package boundary

This PR is validation/infrastructure evidence for the approved Wave 4 authorization boundary.

It does **not** implement:

- final DM Kick/Ban product UI;
- invitation/rejoin UX;
- Campaign Manager administration surfaces;
- generalized RBAC/ACL;
- unrelated Wave 5 work.

Those belong to their later product packages.

PR #41 must remain open until the physical gate is reviewed and recorded.
