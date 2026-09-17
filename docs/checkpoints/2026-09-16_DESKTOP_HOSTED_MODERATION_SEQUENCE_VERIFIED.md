# Desktop hosted Campaign Administration — real moderation sequence verified

**Date:** 2026-09-16 (Chile local time)  
**Branch:** `wave5/desktop-hosted-campaign-administration`  
**PR:** #44 — draft  
**Gate result:** **REAL DEV MODERATION PASS**

## Result

The owner completed the bounded real-DEV Desktop Campaign Administration moderation sequence against the temporary QA Player membership.

Starting roster state:

- campaign: `Hosted Batch Test`;
- Gmail DEV identity: `DM / ACTIVE`;
- Outlook DEV identity: temporary QA fixture `PLAYER / ACTIVE`;
- roster/campaign revision: `0`;
- DM exposed no Player moderation controls;
- Player exposed `Expulsar` and `Bloquear`.

The owner then exercised all approved Player moderation transitions through the Desktop UI, with server confirmation and authoritative roster refresh after every action.

## Verified transitions

### 1. ACTIVE -> KICKED

Desktop action: `Expulsar`.

Observed after refresh:

- revision `0 -> 1`;
- DM remained `DM / ACTIVE`;
- Player became `PLAYER / KICKED` (`Expulsado`);
- Player row remained present;
- `Expulsar` disappeared;
- only `Bloquear` remained.

### 2. KICKED -> BANNED

Desktop action: `Bloquear`.

Observed after refresh:

- revision `1 -> 2`;
- DM remained `DM / ACTIVE`;
- Player became `PLAYER / BANNED` (`Bloqueado`);
- Player row remained present;
- only `Levantar bloqueo` remained.

### 3. BANNED -> KICKED

Desktop action: `Levantar bloqueo`.

Observed after refresh:

- revision `2 -> 3`;
- DM remained `DM / ACTIVE`;
- Player became `PLAYER / KICKED` (`Expulsado`);
- Player row remained present;
- only `Bloquear` remained.

This confirms the intended Lift Ban semantics: `BANNED -> KICKED`, not `BANNED -> ACTIVE`.

## Coverage conclusion

Real Desktop -> Worker -> Neon moderation behavior is physically verified for:

```text
ACTIVE -> KICKED -> BANNED -> KICKED
```

The campaign revision advanced exactly once for each real lifecycle change: `0 -> 1 -> 2 -> 3`.

The DM membership remained protected and unchanged throughout. No direct Neon lifecycle edits were used during the moderation sequence.

## Current cleanup gate

The Outlook Player membership was created only as a bounded QA fixture and should now be removed deliberately.

Cleanup must:

- target only the exact Outlook QA membership in `Hosted Batch Test`;
- require its current state to be `PLAYER / KICKED` before deletion;
- require the Gmail DM membership to remain `DM / ACTIVE`;
- make no campaign, app_user, PC, Descope-user or provider-resource deletion;
- abort if unexpected Player-owned/controlled PCs exist for the fixture identity;
- verify after cleanup that only the original DM membership remains.

After cleanup, refresh Desktop roster and confirm the DM row remains. Then complete final settings persistence and sign-out/relaunch QA before PR #44 readiness review.

## Safety

- Do not delete either `app_user` row.
- Do not delete the hosted campaign.
- Do not modify the Gmail DM membership.
- Do not reset campaign revision merely because the QA fixture is removed; revision `3` is legitimate history from the real moderation transitions.
- Do not remove unrelated mutation receipts or historical hosted evidence.
