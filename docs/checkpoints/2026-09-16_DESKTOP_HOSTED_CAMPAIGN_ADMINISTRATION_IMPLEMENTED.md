# Wave 5 — Desktop hosted Campaign Administration implementation verified

**Date:** 2026-09-16 (Chile local time)  
**Branch:** `wave5/desktop-hosted-campaign-administration`  
**PR:** #44 — draft  
**Verified implementation head:** `58dc05933e35d47bc8f65f0249a2a0b74d6206c4`  
**Scaffold:** `35150382923` — **SUCCESS**  
**Package state:** IMPLEMENTED / CI VERIFIED / EXTERNAL INTEGRATION + OWNER QA PENDING

## Verified implementation

The bounded repository implementation for this package is now present and CI-verified.

### Desktop hosted authentication and bootstrap

- Desktop has a provider-specific Descope email-OTP adapter outside Shared.
- Shared remains provider-neutral through `HostedAccessTokenProvider`.
- Session and refresh JWTs remain memory-only.
- Tokens and OTP values are not written to ordinary Desktop preferences or diagnostics.
- Session refresh is attempted before use when the JWT is near expiry; refresh failure clears hosted session state.
- Sign-out clears only hosted session state and does not delete local campaign/character data.
- The Desktop workbench now exposes the real hosted login/bootstrap flow through Campaign Administration.
- Hosted bootstrap uses the existing `HostedCampaignBootstrapService` and canonical hosted campaign UUID semantics rather than a parallel link table or pasted-ID workflow.

### Campaign Administration

The former Desktop placeholder has been replaced with a real hosted administration surface using the existing Shared clients.

The implementation includes:

- authoritative hosted member-roster reads;
- moderation only for Player memberships, never DM rows;
- `ACTIVE -> KICK` and `ACTIVE -> BAN` actions;
- `KICKED -> BAN`;
- `BANNED -> LIFT_BAN`;
- confirmation text before mutations;
- authoritative roster refresh after successful moderation;
- hosted failures surfaced without pre-emptively mutating local truth;
- local-only campaign work remains usable without hosted authentication.

`LIFT_BAN` continues to mean `BANNED -> KICKED`; it does not silently reactivate membership.

### Desktop settings follow-up

The owner-approved Desktop settings follow-up is implemented in the same genuine Desktop feature build:

- the existing text-scale, spacing-scale and workspace-density behavior remains present;
- font and theme selection now use preview-oriented cards rather than plain abstract selectors;
- each offered font preview renders using the font it represents;
- Geist and Mona Sans Condensed are bundled for Desktop by reusing the existing repository font assets;
- their redistribution provenance/licenses are recorded beside the Desktop resources;
- the remaining current Android catalogue names are offered only when the exact family is actually installed on the Desktop machine, avoiding mislabeled generic fallbacks;
- unavailable or removed persisted named-font choices fall back safely to the stable system sans choice;
- theme previews show representative background/surface/accent presentation before selection;
- preference persistence and fallback behavior have focused tests.

## Verification evidence

Scaffold run `35150382923` completed **SUCCESS** on exact head `58dc05933e35d47bc8f65f0249a2a0b74d6206c4`.

That run passed:

- backend type-check;
- hosted PostgreSQL migrations/contracts;
- all permanent Player guards;
- Shared/Desktop tests;
- Android build;
- Desktop build;
- Android debug artifact upload.

Earlier implementation slices were also validated independently during development, including hosted administration wiring and the settings migration, before the final cleanup head above.

## What is NOT yet verified

This checkpoint does **not** claim the Campaign Administration routes are deployed/live on the real DEV Worker.

The repository still has no automatic Cloudflare Worker deployment workflow. Real provider/network behavior must therefore remain unclaimed until explicit DEV deployment/integration evidence exists.

This checkpoint also does **not** claim owner Windows Desktop QA. The owner still needs to validate the real Desktop artifact and flows on Windows, including:

- email-OTP authentication against DEV;
- hosted campaign bootstrap/convergence without local-data loss;
- real roster retrieval;
- Kick/Ban/Lift-ban behavior and post-mutation refresh;
- hosted sign-out/relaunch behavior;
- local-only operation without hosted auth;
- font/theme previews and selection;
- preference persistence across normal close/relaunch;
- survival of existing local campaign data throughout testing.

## Remaining package gate

Repository implementation is complete enough to advance to the external integration gate.

Next sequence:

1. inspect and execute only the safe/authorized DEV Worker deployment/integration path;
2. obtain explicit evidence that the Campaign Administration routes are live on DEV;
3. produce the owner-testable Windows Desktop build/artifact as applicable;
4. run owner Windows Desktop QA;
5. fix any defects found in narrowly scoped commits with CI verification;
6. merge PR #44 only after the required owner QA passes.

If DEV deployment requires credentials or provider-local access unavailable safely to the Work environment, stop at that narrow gate after documenting it. Never request or expose secrets in chat or Git.

## Invariants preserved

All opening-package invariants remain controlling, including provider-neutral Shared architecture, canonical hosted campaign identity, no insecure token persistence, no destructive local-state reset, server-authoritative moderation, no co-DM moderation/generalized RBAC, no PC Manager/full PC sync and no Live Combat expansion in this package.
