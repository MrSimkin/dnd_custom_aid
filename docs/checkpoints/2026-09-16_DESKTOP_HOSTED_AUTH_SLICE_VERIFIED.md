# Wave 5 — Desktop hosted auth/session slice verified

**Date:** 2026-09-16 (Chile local time)  
**Branch:** `wave5/desktop-hosted-campaign-administration`  
**PR:** #44 — draft  
**Verified implementation head:** `9c6994cff2782e2c3c8f37e08d4f0e483ae13322`  
**Scaffold:** `35146769605` — **SUCCESS**  
**Package state:** ACTIVE / PARTIAL IMPLEMENTATION VERIFIED

## Verified slice

The first provider/session slice is implemented and CI-verified:

- Desktop-only Descope email OTP sign-up-or-in request;
- OTP verification producing real Descope session and refresh JWTs;
- provider-neutral Shared integration through `HostedAccessTokenProvider`;
- session and refresh JWTs kept in memory only;
- no JWT/OTP persistence in `DesktopPreferencesStore`;
- refresh-before-use when the current session JWT is near expiry;
- refresh failure fails closed by clearing hosted session state;
- sign-out clears hosted session state without touching campaign/character data;
- focused JWT-expiry tests;
- only the required Desktop compile dependencies were added.

Current external-provider behavior follows the documented Descope public REST contract. No credentials, tokens, OTP values or provider secrets were committed or added to diagnostics.

## Verification

Scaffold run `35146769605` completed **SUCCESS** on exact head `9c6994cff2782e2c3c8f37e08d4f0e483ae13322`.

That run covered backend checks, hosted PostgreSQL contracts, Kotlin/Shared/Android/Desktop build+tests and Android artifact generation.

This does **not** claim real Desktop authentication against DEV yet. Provider/network behavior remains part of the later real owner/DEV integration gate.

## Current package continuation

Continue on PR #44 / `wave5/desktop-hosted-campaign-administration` with:

1. wire the verified Desktop auth controller into the Desktop workbench UX;
2. use `HostedCampaignBootstrapService` to converge hosted campaign identity/membership into the local database;
3. add real hosted member roster and server-authoritative moderation using `HostedCampaignAdministrationClient`;
4. implement the required font catalogue + font/theme preview settings follow-up;
5. extend non-secret QA diagnostics and focused tests;
6. run Scaffold on the new exact implementation head;
7. perform explicit DEV Worker deployment/integration and owner Windows Desktop QA before merge.

## Invariants

All opening package invariants remain controlling. In particular: Shared stays provider-neutral; tokens remain secret; local-only work remains usable without hosted auth; hosted campaign UUIDs remain canonical; moderation never mutates local truth before server confirmation; `LIFT_BAN` remains `BANNED -> KICKED`; no invitations, co-DM moderation, PC Manager/full PC sync, Live Combat or generalized RBAC are pulled into this package.
