# Desktop canonical Outlook DM — verified

**Date:** 2026-09-16 (Chile local time)  
**Branch:** `wave5/desktop-hosted-campaign-administration`  
**PR:** #44 — draft  
**Gate result:** **PASS**

## Result

After the controlled DEV identity migration, the owner authenticated the Desktop app with the canonical Outlook-backed DEV identity and refreshed Campaign Administration against the real DEV Worker/Neon environment.

Observed real Desktop state:

- hosted session authenticated as Outlook app_user `f34bc5f0-4d35-4d09-b771-505b3851440c`;
- bootstrap: `Campañas alojadas: 1 · aplicadas: 1 · conflictos: 0`;
- hosted campaign: `Hosted Batch Test`;
- hosted role: `DM`;
- membership status: `ACTIVE` / `Activo`;
- roster/campaign revision: `4`;
- exactly one hosted member row is visible;
- that row is the Outlook DM membership;
- Gmail does not appear as a campaign member;
- the DM row exposes no Player moderation controls.

This verifies that the canonical DEV identity migration is not only present in Neon but is consumed correctly by Desktop through the real hosted stack.

## Canonical DEV identity rule

Future normal hosted DEV testing uses the Outlook-backed application identity as the default owner/DM identity.

The Gmail-backed application identity remains historical/inactive by default and may only be used deliberately when a second identity is needed for a specific multi-user test.

Historical Gmail mutation receipts remain unchanged because they truthfully record earlier operations.

## Remaining PR #44 owner QA

Only final Desktop persistence/session behavior remains before PR readiness review:

1. verify font and theme preview cards are visible in `Configuración`;
2. deliberately change a small set of device-local preferences;
3. close the Desktop app without signing out first;
4. relaunch and verify those settings and local campaign data persist;
5. verify hosted session does **not** persist across relaunch and begins signed out;
6. authenticate again with Outlook and verify `Hosted Batch Test` returns as `DM / ACTIVE`, revision `4`;
7. explicitly sign out and verify local campaign data/settings remain intact and hosted state becomes signed out.

## Safety

- Do not alter Neon during the remaining persistence/relaunch QA.
- Do not delete either app_user.
- Do not rewrite historical mutation receipts.
- Do not reset campaign revision `4`.
- Do not persist OTPs, session JWTs or refresh JWTs.
