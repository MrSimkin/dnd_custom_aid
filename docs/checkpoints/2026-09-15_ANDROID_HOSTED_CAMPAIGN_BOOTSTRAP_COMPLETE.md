# Checkpoint — Android hosted campaign bootstrap complete

**Date:** 2026-09-15 (Chile local time)  
**Status:** COMPLETE / VERIFIED / OWNER-PHYSICAL PASS  
**Integrated PR:** #32 — `android: wire hosted campaign bootstrap into Player`  
**Exact tested PR head:** `59ca7ec57592c630d00cbb0029df69dcdee29a11`  
**Merged `main` commit:** `161edc2cf52104a906d03891a56ee73edda90dde`  
**Exact-head PR Scaffold run:** `35022173998` / #1915 — **SUCCESS**  
**Post-merge Scaffold run:** `35023358758` / #1916 — **SUCCESS**

## Purpose

This checkpoint records the first owner-facing hosted account/campaign bootstrap in the ordinary Android Player application.

The Android Descope session edge was already complete before this package. This package reused that remembered session and the existing shared hosted contracts instead of introducing another authentication, HTTP or synchronization stack.

It closes the next Wave 4 dependency step:

```text
remembered Android Descope session/token        COMPLETE
existing HostedAccessTokenProvider              COMPLETE
owner-facing hosted account/campaign bootstrap  COMPLETE
        |
        v
campaign create/select + durable hosted delivery NEXT
```

## What is integrated

The normal `dnd_custom_aid` Player now exposes a `Servidor` card on the `Campañas` screen.

`Actualizar desde servidor` uses the remembered Descope session through the existing Android `HostedAccessTokenProvider`, calls the shared `HostedCampaignBootstrapService`, and refreshes local campaign/account/membership state from the hosted DEV API.

The shared bootstrap remains authoritative for reconciliation semantics. In particular it:

- completes authenticated hosted reads before mutating local state;
- records/resolves the hosted application account locally;
- reconciles explicit hosted campaign membership state transactionally;
- preserves local-ahead revisions, local tombstones and same-revision state mismatches as explicit conflicts instead of silently overwriting them;
- does not interpret a campaign's absence from the hosted membership response as a deletion instruction.

The ordinary local campaign creation `+` flow was deliberately left unchanged in PR #32. Hosted campaign mutation/delivery is the next package rather than being hidden inside this bootstrap proof.

## Physical Android proof — accepted

The owner physically verified the exact PR build on Android and reported the requested gate as passing.

The exercised sequence covered:

1. ordinary Player `Campañas` with no remembered hosted session;
2. safe `Actualizar desde servidor` behavior while unauthenticated, without deleting/changing existing local campaigns;
3. real DEV email-OTP authentication through the existing debug-only hosted-auth harness;
4. return to the ordinary Player application with the remembered session;
5. successful hosted refresh from the ordinary `Campañas` screen;
6. preservation of existing local campaign state after the hosted refresh.

Therefore the owner-facing bootstrap gate is **PASS**.

This proof establishes hosted read/bootstrap integration in the normal Player. It does not yet establish hosted campaign creation, cross-device propagation or PC snapshot synchronization.

## Automated verification

Exact tested PR head:

`59ca7ec57592c630d00cbb0029df69dcdee29a11`

Exact-head PR Scaffold run:

`35022173998` / #1915 — **SUCCESS**

That run passed:

- permanent Player guard checks;
- Kotlin/shared/Android/Desktop build and tests;
- Android debug APK generation;
- backend type-check;
- hosted PostgreSQL migration/contracts.

PR #32 was squash-merged to:

`161edc2cf52104a906d03891a56ee73edda90dde`

Post-merge `main` Scaffold run:

`35023358758` / #1916 — **SUCCESS**

## Architecture boundary preserved

The active path is now:

```text
ordinary Android Player
        |
        | remembered Descope session
        v
Android HostedAccessTokenProvider
        |
        v
shared HostedApiClient
        |
        v
Cloudflare Worker
        |
        v
Neon PostgreSQL
        |
        v
shared conflict-preserving local reconciliation
```

No database credential is exposed to the client. Authentication proves identity; application authorization remains server-owned.

The separate `DnD Aid - Hosted DEV Auth` activity remains a debug-only verification harness and is not the final product login UX.

## Budget / security state

The hard external-service budget remains **USD $0**.

This package reused the already-active Descope Free + Cloudflare Workers Free + Neon Free DEV environment. No paid resource, R2, D1, Durable Object, Queue or Workers AI dependency was introduced.

Existing security residuals remain open and are not closed by this checkpoint, including dependency-vulnerability review, JWT/object-level authorization hardening, request/log/error review and least-privilege Neon runtime-role evaluation.

## Exact continuation

The next primary implementation package is:

**local campaign creation -> durable hosted delivery -> hosted read-back/reconciliation**

Use the already-existing provider-neutral mechanisms rather than inventing a new mutation path:

- `HostedCampaignCreationService` for atomic local campaign creation + durable outbox enqueue;
- `HostedOutboxRepository` for retry-preserving local mutation state;
- `HostedOutboxDeliveryService` for idempotent delivery through `HostedApiClient.createCampaign`;
- the existing server `POST /v1/campaigns` mutation-receipt contract;
- `HostedCampaignBootstrapService` for authoritative hosted read-back/reconciliation.

The required behavioral boundary is local-first:

```text
create locally + queue durably
        |
        +--> UI/local data remains usable even if offline/auth unavailable
        |
        v
attempt hosted delivery when a session/network is available
        |
        v
server idempotency receipt + DM membership
        |
        v
hosted read-back/reconciliation
```

Do not make local creation depend on immediate network success. Preserve stable campaign identity, mutation idempotency, explicit retry/block states and non-destructive local recovery.

PC snapshot push/pull remains the following package, not part of campaign-creation proof.
