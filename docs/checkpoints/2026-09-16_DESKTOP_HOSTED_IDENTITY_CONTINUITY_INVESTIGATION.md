# Desktop hosted identity continuity investigation

**Date:** 2026-09-16 (Chile local time)  
**Branch:** `wave5/desktop-hosted-campaign-administration`  
**PR:** #44 — draft  
**State:** REAL AUTH PASS / HOSTED IDENTITY CONTINUITY INVESTIGATION ACTIVE

## Trigger

Owner Windows Desktop QA successfully launched, preserved local campaign data, authenticated through the real Descope email-OTP flow, reached the real DEV Worker and completed hosted bootstrap.

Bootstrap returned zero hosted campaigns for the Desktop-authenticated application account.

A read-only Neon query then established:

- current Desktop-authenticated application user: `f34bc5f0-4d35-4d09-b771-505b3851440c`;
- that application user has no campaign membership rows;
- historical Wave 4 application user `4ba0f476-2eba-4eff-b4e0-78bb9372a8b4` still has ACTIVE DM membership in hosted campaign `31762fa9-b01a-4f3d-80e5-877a07c63e62` (`Hosted Batch Test`);
- that hosted campaign remains undeleted at revision 0.

The owner confirmed that the same email address was used for the earlier Android hosted/Descope QA and the current Desktop OTP login.

## Why this is material

Android and Desktop both use the same Descope project ID and the same Cloudflare Worker. Backend identity resolution maps the validated Descope JWT `sub` one-to-one to `app_user.descope_subject`, which is UNIQUE in PostgreSQL.

Therefore the two different application-user UUIDs imply two different Descope subjects were observed at different times.

Current Descope documentation states that:

- every user has an immutable Descope user ID;
- login IDs such as email are unique across users in a project;
- OTP Sign-Up-or-In should sign in an existing user when that login ID already exists;
- the successful verification response/JWT identifies the Descope user.

The Desktop implementation uses the documented REST endpoints `/v1/auth/otp/signup-in/email` and `/v1/auth/otp/verify/email`, so no code change is justified yet.

## Leading hypothesis — not yet conclusion

A plausible explanation is that the original Descope user was deleted/re-created at some point. Re-creating a user with the same email would yield a new immutable Descope user ID while the old Neon `app_user` row and its application memberships remain.

Other possibilities remain open, such as a historical login-ID variant or another identity-management event. Do not mask the discrepancy by inserting a membership for the new application user.

## Current owner/provider gate

Perform a READ-ONLY Descope Console user lookup in the existing DEV project.

Search the Users page for the exact email used in both Android and Desktop QA and determine:

1. how many matching user records are currently visible;
2. whether the exact email is listed as a Login ID for each match;
3. each matching user's current status;
4. each matching user's creation time/date if visible;
5. whether multiple records differ by login-ID spelling/case/alias.

Do not edit, merge, disable, delete, invite or recreate users. Do not create a management key merely for this investigation.

If only one current Descope user exists and it was created after the historical Android identity was first used, deletion/recreation becomes the leading confirmed explanation. If two matching users exist, inspect their Login IDs before any repair decision.

## Safety

- no Neon mutation;
- no Campaign Administration moderation mutation;
- no Descope user mutation;
- no credentials, OTPs, JWTs, management keys or database secrets in chat/Git;
- preserve the historical hosted campaign/membership until identity continuity is understood.
