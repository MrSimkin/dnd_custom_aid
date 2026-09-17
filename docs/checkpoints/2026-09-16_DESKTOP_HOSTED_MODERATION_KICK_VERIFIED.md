# Desktop hosted Campaign Administration — real moderation KICK verified

**Date:** 2026-09-16 (Chile local time)  
**Branch:** `wave5/desktop-hosted-campaign-administration`  
**PR:** #44 — draft  
**Gate result:** REAL DEV `ACTIVE -> KICKED` MODERATION PASS

## Observed owner QA evidence

Using the real Windows Desktop Campaign Administration UI while authenticated as the historical Gmail DEV DM identity, the owner moderated the bounded Outlook DEV Player fixture through the `Expulsar` action.

Before the action:

- roster revision = `0`;
- Gmail DEV membership = `DM / ACTIVE`, with no Player moderation controls;
- Outlook DEV membership = `PLAYER / ACTIVE`, with `Expulsar` and `Bloquear` controls.

After confirmation and server-authoritative roster refresh:

- roster revision advanced exactly once from `0` to `1`;
- Gmail DEV membership remained `DM / ACTIVE` and unchanged;
- Outlook DEV membership changed from `PLAYER / ACTIVE` to `PLAYER / KICKED` (`Expulsado` in Spanish UI);
- the kicked Player row no longer exposed `Expulsar`;
- the kicked Player row exposed only `Bloquear`;
- the Player membership remained present in the roster rather than being destructively deleted.

This is real DEV evidence through Desktop -> Cloudflare Worker -> Neon, not only an automated contract result.

## Interpretation

The real moderation path correctly applied the approved lifecycle semantics:

`ACTIVE -> KICKED`

The campaign revision advanced exactly once for the actual lifecycle change. The DM row remained protected and unaffected. The UI updated from authoritative hosted state after the mutation.

## Next bounded gate

Perform exactly one further Desktop moderation action:

`KICKED -> BANNED`

Use the remaining `Bloquear` control on the Player row, confirm the action, wait for server-authoritative roster refresh, then verify:

- roster revision advances exactly once from `1` to `2`;
- Player status becomes `BANNED` / `Bloqueado`;
- DM remains `ACTIVE` and unchanged;
- Player row remains present;
- the UI presents only the action appropriate to a banned Player, expected to be Lift Ban / unban semantics.

Stop after that result. Do not perform Lift Ban until the BANNED state has been reviewed.

Do not manually edit Neon during this moderation sequence unless a defect requires diagnosis.
