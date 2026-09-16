# Wave 5 — Desktop hosted Campaign Administration package opened

**Date:** 2026-09-16 (Chile local time)  
**Branch:** `wave5/desktop-hosted-campaign-administration`  
**Verified base:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Base meaning:** merged PR #43 (`feat: add hosted campaign membership administration core`)  
**Post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Opening unique commits:** 0  
**Package state:** ACTIVE / IMPLEMENTATION AUTHORIZED

## Purpose

Turn the existing Desktop `Administración de campaña` placeholder into the first real hosted DM administration surface while preserving the provider-neutral Shared contracts already integrated.

This package is also the next genuine Desktop feature build, so it owns the previously approved font-catalogue and font/theme preview selector follow-up.

## Verified opening reconstruction

At package opening:

- repository access was confirmed;
- `main` remained exactly `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`;
- `wave5/desktop-hosted-campaign-administration` remained exactly at the same commit;
- the branch therefore had zero unique commits;
- PR #43 was confirmed merged despite chronologically stale pre-merge wording in operative-memory files;
- no newer external change materially altered the handoff.

## Relevant integrated seams inspected

Desktop:

- `desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/Main.kt`;
- `desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPreferences.kt`;
- `desktopApp/build.gradle.kts`.

Android reference:

- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/AndroidHostedAuth.kt`;
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/UiPreferences.kt`.

Shared:

- `HostedAccessTokenProvider` / `HostedApiClient`;
- `HostedCampaignBootstrapService`;
- `HostedCampaignAdministrationClient`.

## Opening findings

### Desktop shell

The current Desktop Campaign Administration surface is an honest local-context placeholder. It currently states that hosted Kick/Ban/Unban administration is deferred. This package should replace that placeholder rather than create a parallel administration surface.

Desktop settings currently use plain selectors and generic JVM font families. The current Desktop theme catalogue is already broader and does not require Android-theme parity expansion for this package.

### Hosted campaign discovery/bootstrap

Shared already exposes hosted campaign discovery through `HostedApiClient.campaigns()` and canonical convergence through `HostedCampaignBootstrapService`.

Therefore Desktop does not need a new hosted/local campaign-link table or a manually pasted campaign UUID workflow. Hosted bootstrap should use the existing canonical campaign UUID and Shared persistence semantics.

### Desktop authentication provider boundary

The current Descope Kotlin SDK is an Android client SDK, not a general Desktop/JVM client SDK. Reusing it directly in Desktop would be an architecture/platform mismatch.

The existing Android adapter keeps Descope outside Shared and exposes only `HostedAccessTokenProvider`; Desktop must preserve the same separation.

The current public DEV configuration already committed in Android platform code is:

- Descope project ID `P3JNKAUazZAxRXF4uM7nKzaAiy7Y`;
- hosted API base URL `https://dnd-custom-aid-api.mrsimkin-dev.workers.dev`.

These are public identifiers/configuration, not credentials.

Descope's current Android implementation and public API contract establish the email-OTP wire flow used by the existing DEV proof:

- `POST /v1/auth/otp/signup-in/email` with the login ID;
- `POST /v1/auth/otp/verify/email` with login ID + code;
- `POST /v1/auth/refresh` authorized with the refresh JWT when refreshing;
- normal unauthenticated authentication requests identify the public project in the authorization/project headers.

Desktop can implement that provider-specific wire adapter in Desktop code using the existing Ktor/JVM stack rather than importing Android-only provider machinery.

### Session-storage decision for this bounded package

Do **not** persist session or refresh JWTs in ordinary Desktop preferences or diagnostics.

The bounded implementation default is an in-memory hosted session:

- real usable email-OTP login;
- refresh while the application remains open;
- closing/relaunching requires hosted re-authentication;
- local/offline data remains usable;
- sign-out clears hosted session state but never local campaign/character data.

This limitation is preferable to insecure persistence and may be revisited later with a platform-secure credential-store decision.

### Android selectable font reference

The actual current Android selectable catalogue excludes the historical/hidden entries and currently contains:

- Manrope;
- Sora;
- Source Sans 3;
- Roboto Condensed;
- Archivo Narrow;
- IBM Plex Sans Condensed;
- Mona Sans Condensed;
- Geist;
- Barlow Semi Condensed;
- Space Grotesk;
- Recursive;
- PT Sans Narrow;
- League Spartan.

Desktop should use this real selectable set as the parity target where technically and licensing-wise appropriate. Named choices must render honestly; do not expose fake choices that all resolve to a generic fallback.

## Package invariants

- Shared remains provider-neutral.
- No token-paste product login flow.
- Never log/expose/persist tokens or OTPs insecurely.
- Hosted campaign identity remains canonical UUID.
- No silent hosted/local duplicate campaign creation.
- No deletion of local state when hosted access/session changes.
- Roster/moderation is server-authoritative.
- Successful moderation refreshes authoritative roster state.
- Failed hosted actions do not mutate local truth.
- `LIFT_BAN` remains `BANNED -> KICKED`.
- No co-DM moderation or generalized RBAC.
- No PC Manager/full PC sync in this package.
- Font/theme preview changes must not regress existing preference persistence or scale/density behavior.

## Verification gates

Automated:

- focused Desktop preference/auth/state tests;
- Shared/backend contracts remain green;
- Desktop build/tests;
- full Scaffold on exact implementation head.

External/manual:

- explicit DEV Worker deployment/integration evidence before claiming PR #43 routes live;
- owner Windows Desktop QA for real auth, hosted campaign bootstrap/roster/moderation and visual font/theme previews;
- owner local campaign data must survive hosted sign-out/relaunch/testing.

Do not merge this owner-testable Desktop surface before required owner QA passes.

## Resume instruction

Continue implementation on `wave5/desktop-hosted-campaign-administration`. Do not restart the merged PR #43 package or the completed Desktop-shell package. If work is interrupted, update this package checkpoint or supersede it with a newer current-package checkpoint before stopping.
