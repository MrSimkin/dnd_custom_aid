# Desktop hosted Campaign Administration — DEV identity mapping resolved

**Date:** 2026-09-16 (Chile local time)  
**Branch:** `wave5/desktop-hosted-campaign-administration`  
**PR:** #44 — draft  
**Result:** IDENTITY DEFECT NOT REPRODUCED / TWO DISTINCT DEV LOGIN IDENTITIES CONFIRMED

## Result

The apparent Android-vs-Desktop identity split is resolved as DEV test-account separation, not evidence that Desktop created a duplicate Descope identity.

Read-only Neon and Descope evidence together established:

- the historical Wave 4 hosted DM application user remains mapped to the older DEV Gmail login identity;
- that application user still has ACTIVE DM membership in `Hosted Batch Test`;
- the current Desktop QA application user is mapped to the separate DEV Outlook login identity;
- that application user currently has no campaign memberships;
- Descope shows two enabled users with distinct login IDs and distinct immutable user IDs;
- the login timestamps align with the historical Android hosted work using the older Gmail identity and the current Desktop QA using the Outlook identity.

No Descope duplicate-user condition for one login ID was found. No auth code change, identity merge, deletion, recreation or Neon repair is justified by the evidence.

## Preserved hosted state

Historical hosted test campaign remains:

- campaign UUID: `31762fa9-b01a-4f3d-80e5-877a07c63e62`;
- name: `Hosted Batch Test`;
- revision: `0`;
- deleted: no;
- historical DEV DM membership: ACTIVE.

The current Outlook-backed Desktop application user remains membership-free. This is valid data state, not a failure of Desktop bootstrap.

## Next bounded QA action

No database mutation is required for the next step.

Use the Desktop application itself:

1. sign out of the current hosted session;
2. sign in through the same real email-OTP flow using the historical DEV Gmail DM identity;
3. allow hosted bootstrap to run;
4. confirm `Hosted Batch Test` is returned as a hosted campaign and becomes available in the Desktop campaign context;
5. retrieve the hosted member roster;
6. report the visible rows/roles/statuses and moderation controls;
7. do not perform Kick/Ban/Lift Ban yet.

If the roster contains only the existing DM row, stop there. A later explicitly bounded QA fixture may add the already-existing Outlook-backed application user as a reversible PLAYER membership rather than inventing another account or modifying the historical DM identity.

## Safety

Do not store real login email addresses, OTPs, Descope user IDs, JWTs, provider credentials or database credentials in Git.

Do not alter Descope users or Neon membership rows merely to preserve a mistaken same-identity assumption.

Do not perform moderation mutations until a suitable Player membership is deliberately established and reviewed.
