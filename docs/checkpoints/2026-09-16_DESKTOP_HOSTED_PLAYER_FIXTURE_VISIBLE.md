# Desktop hosted Campaign Administration — PLAYER fixture visible

**Date:** 2026-09-16 (Chile local time)  
**Branch:** `wave5/desktop-hosted-campaign-administration`  
**PR:** #44 — draft  
**Gate result:** REAL ROSTER + PLAYER MODERATION PREFLIGHT PASS

## Observed owner QA evidence

The owner added the bounded reversible QA membership using the already-existing Outlook DEV application user as an ACTIVE PLAYER in the existing `Hosted Batch Test` campaign.

The existing Gmail DEV DM membership remained ACTIVE and unchanged.

After authoritative roster refresh in Desktop Campaign Administration, the real hosted roster showed:

- roster revision `0`;
- Gmail DEV membership: role `DM`, status `ACTIVE`, no moderation controls;
- Outlook DEV membership: role `PLAYER`, status `ACTIVE`;
- Player row exposed both `Expulsar` and `Bloquear` actions.

This verifies that the real deployed Worker, Neon data and Desktop UI agree on the initial moderation state and that DM rows remain protected from Player moderation actions.

## Next bounded moderation sequence

Run real moderation only through the Desktop UI, one transition at a time, with authoritative roster refresh after each server-confirmed action:

1. `ACTIVE -> KICKED` using `Expulsar`;
2. verify Player status becomes KICKED and campaign/roster revision advances exactly once;
3. `KICKED -> BANNED` using `Bloquear`;
4. verify Player status becomes BANNED and revision advances exactly once;
5. `BANNED -> KICKED` using `LIFT_BAN` / unblock action;
6. verify Player status becomes KICKED and revision advances exactly once.

Do not mutate the Gmail DM row.

Do not manually edit Neon between those moderation transitions unless a defect requires diagnosis.

The fixture may be removed after the moderation gate and after any necessary evidence capture.
