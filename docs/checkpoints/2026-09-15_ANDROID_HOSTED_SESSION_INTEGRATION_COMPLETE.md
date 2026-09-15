# Checkpoint — Android hosted session integration complete

**Date:** 2026-09-15 (Chile local time)  
**Status:** COMPLETE / VERIFIED  
**Integrated PR:** #30 — `android: wire remembered Descope session into hosted client`  
**Merged `main` commit:** `bf5f843066a7c2f8674a4577918156e8a8d2c139`  
**Post-merge Scaffold run:** `35020281492` / #1898 — **SUCCESS**

## Purpose

This checkpoint records the first real Android Player-side integration with the already activated hosted DEV environment.

It closes the first two dependency steps of Wave 4:

1. remembered Android Descope session/token acquisition at the platform edge;
2. feeding the active short-lived session JWT into the existing `HostedAccessTokenProvider` seam.

Provider activation itself was already complete before this package and was not repeated.

## What is integrated

Android now includes the Descope Android SDK and initializes it at application startup so the SDK can reload a remembered session.

The Android platform adapter now implements the existing provider-neutral hosted token boundary:

```text
Descope Android session manager
        |
        | refresh session if needed
        v
DescopeHostedAccessTokenProvider
        |
        | short-lived session JWT only
        v
shared HostedApiClient
        |
        v
Cloudflare Worker
        |
        v
Neon PostgreSQL
```

The normal shared/network/sync architecture was reused. No parallel authentication, HTTP or synchronization stack was introduced.

Android also has the required INTERNET permission for real hosted calls.

## DEV-only authentication harness

A separate debug-only launcher activity exists for development verification:

`DnD Aid - Hosted DEV Auth`

It provides the already-approved DEV email-OTP path without silently turning that temporary test screen into the final Player login UX.

The ordinary `dnd_custom_aid` launcher remains the normal Player application and is not yet gated by a product login screen.

The debug harness and its launcher entry must remain debug-only unless a later explicit product decision replaces it with the real owner-facing authentication UX.

## Physical Android proof — accepted

The owner physically verified the exact PR build on Android.

The following real path passed:

```text
email OTP
   -> Descope Android SDK
   -> managed/persisted session
   -> HostedAccessTokenProvider
   -> shared HostedApiClient
   -> deployed Cloudflare Worker `/v1/me`
   -> Neon-backed application account
```

Observed owner/manual results:

- first launch of the DEV auth harness had no remembered session;
- real email OTP authentication succeeded;
- authenticated Worker account lookup succeeded;
- the application was completely closed and reopened;
- the Descope session was remembered and reused successfully against the Worker without another login;
- DEV logout was executed;
- the app was completely closed and reopened again;
- the DEV auth harness no longer found a remembered session.

Therefore the Android session lifecycle gate is **PASS**:

```text
LOGIN -> persist -> restart -> reuse   PASS
LOGOUT -> clear/revoke -> restart      PASS
```

This physical proof is specific to the Android hosted-auth/session path. It does not retroactively change the bounded acceptance status of older historical Player QA candidates.

## Automated verification

Exact PR head before merge:

`b27bab9dbba6fe8e2b033cba8239484c82672a8c`

Latest exact-head PR Scaffold run:

`35017450602` / #1897 — **SUCCESS**

That run passed:

- permanent Player guard checks;
- Kotlin/shared/Android build and tests;
- Android debug APK generation;
- backend type-check;
- hosted PostgreSQL migration/contracts.

PR #30 was squash-merged to:

`bf5f843066a7c2f8674a4577918156e8a8d2c139`

Post-merge `main` Scaffold run:

`35020281492` / #1898 — **SUCCESS**

## Security / secret handling

No provider secret or database credential was added to the Android client.

The Descope Project ID and Worker DEV base URL are public application configuration, not secrets.

Descope owns Android persisted session/refresh-token storage. The shared hosted client receives only the short-lived session JWT needed for the authenticated request.

The DEV harness does not intentionally print or display JWTs, refresh tokens or database credentials.

Existing security residuals remain open and are not closed by this checkpoint, including dependency-vulnerability review, JWT/object-level authorization hardening and least-privilege Neon runtime-role evaluation.

## Budget/provider state

The hard external-service budget remains **USD $0**.

This package reused the existing Descope Free + Cloudflare Workers Free + Neon Free DEV resources. No new paid resource, payment method, billable add-on, R2, D1, Durable Object, Queue or Workers AI dependency was introduced.

## Exact continuation

The next primary implementation package is:

**owner-facing hosted account/campaign bootstrap**

Authentication/session acquisition is no longer the current blocker and should not be reimplemented.

Continue through the already-existing provider-neutral contracts in this order:

```text
remembered Android Descope session/token        COMPLETE
existing HostedAccessTokenProvider              COMPLETE
        |
        v
owner-facing hosted account/campaign bootstrap  NEXT
        |
        v
campaign create/select + durable hosted delivery
        |
        v
PC snapshot push/pull
        |
        v
second-device observation
        |
        v
offline/reconnect/convergence + revoke enforcement
```

Preserve local-first behavior, stable IDs, optimistic revisions, idempotent mutations, tombstones/non-resurrection, explicit conflicts, DM authority distinct from PC ownership and owner distinct from current controller.

Do not repeat provider activation, create a second auth/network/sync architecture or turn the DEV auth harness into the final product login UX by inertia.
