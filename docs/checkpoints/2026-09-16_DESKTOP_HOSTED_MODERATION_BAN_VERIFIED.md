# Desktop hosted moderation — KICKED -> BANNED verified

**Date:** 2026-09-16 (Chile local time)  
**Branch:** `wave5/desktop-hosted-campaign-administration`  
**PR:** #44 — draft  
**Gate result:** REAL DEV MODERATION PASS (`KICKED -> BANNED`)

## Observed owner QA evidence

Starting state:

- roster revision `1`;
- Gmail DEV identity remained `DM / ACTIVE` with no Player moderation controls;
- Outlook DEV fixture remained `PLAYER / KICKED` (`Expulsado`);
- only `Bloquear` was available for the Player.

The owner exercised the real Desktop `Bloquear` action on that Player and accepted the confirmation dialog.

After server confirmation and authoritative roster refresh, Desktop showed:

- roster revision advanced exactly once from `1` to `2`;
- DM remained `DM / ACTIVE` and unchanged;
- Player changed from `PLAYER / KICKED` to `PLAYER / BANNED` (`Bloqueado`);
- Player row remained present;
- the moderation action changed to `Levantar bloqueo`;
- no unrelated hosted/local state change was observed.

This verifies the real DEV `KICKED -> BANNED` moderation path through Desktop -> Worker -> Neon and the post-mutation authoritative roster refresh.

## Next bounded gate

Perform one final moderation transition only:

`BANNED -> KICKED` via `Levantar bloqueo`.

Expected result:

- roster revision `2 -> 3` exactly once;
- DM remains unchanged;
- Player remains present;
- Player status becomes `KICKED` / `Expulsado`;
- action returns to `Bloquear` only.

Stop after that transition for review before fixture cleanup/final session/settings QA.
