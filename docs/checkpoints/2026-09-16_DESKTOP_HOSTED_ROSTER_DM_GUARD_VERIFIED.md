# Desktop hosted Campaign Administration — real roster + DM guard verified

**Date:** 2026-09-16 (Chile local time)  
**Branch:** `wave5/desktop-hosted-campaign-administration`  
**PR:** #44 — draft  
**Gate result:** REAL GMAIL DM LOGIN PASS / HOSTED BOOTSTRAP PASS / ROSTER PASS / DM MODERATION GUARD PASS

## Observed owner QA evidence

The owner signed out of the Outlook-backed DEV session and authenticated the Desktop app with the historical Gmail DEV identity through the normal email-OTP flow.

Observed:

- Gmail OTP login succeeded;
- hosted bootstrap reported `Campañas alojadas: 1 · aplicadas: 1 · conflictos: 0`;
- hosted campaign `Hosted Batch Test` appeared in the Desktop campaign surface;
- hosted role resolved as `DM`;
- hosted membership status resolved as active;
- Campaign Administration fetched the real hosted roster successfully;
- roster revision shown by the UI was `0`;
- roster contained exactly one visible membership row: the existing DM account;
- the DM row showed no Player moderation actions and explicitly stated that Player moderation actions do not apply to DM members.

This verifies real authenticated Desktop -> Worker -> Neon Campaign Administration roster behavior and the no-moderation-on-DM guard in the current DEV environment.

## Bootstrap meaning

For this QA package, Desktop hosted bootstrap means fetching the authenticated account's hosted campaign memberships and reconciling them into the local Desktop campaign context. The observed `1 / 1 / 0` result means one hosted campaign was returned, one was applied locally, and no reconciliation conflicts occurred.

## Current limitation

The real hosted roster currently contains only the DM membership. Therefore `KICK`, `BAN`, and `LIFT_BAN` cannot yet be exercised against a real Player row.

Do not alter the existing DM membership for this moderation test.

## Next bounded gate — reversible Player QA fixture

Use the already-existing Outlook DEV application user as the Player fixture for `Hosted Batch Test`.

The fixture must:

- add only one `campaign_membership` row for the existing Outlook application user;
- use role `PLAYER` and initial status `ACTIVE`;
- preserve the existing Gmail DM row unchanged;
- avoid creating another Descope user;
- avoid creating/deleting campaign or PC rows;
- be verified immediately after insertion;
- remain easy to remove after moderation QA if desired.

The owner/provider mutation must be supplied as an exact, bounded Neon SQL handoff with preflight checks and stop conditions. Do not execute any database mutation from the agent environment.

After the Player row is confirmed visible in Desktop, run real UI moderation QA in a controlled sequence and verify authoritative roster refresh/revision changes after each successful transition.
