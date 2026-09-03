# Phase 4 Correction A — applied, validation trigger

Date: 2026-09-03
Branch: `implementation/character-data-foundation`
Implementation commit: `b9fb1347bbf7c46662075e61a6571e8bc5e00cb4`

Correction A applied:
- N-01 Android system Back hierarchy for app navigation.
- Internal PC Settings Back handling.
- C-01 combat editor dialog made IME/inset aware so actions remain reachable.
- E-04 equipment editor dialog made IME/inset aware so actions remain reachable.
- C-02 outside dialog tap now clears focus/IME without discarding the dialog draft.

This connector-authored checkpoint intentionally triggers the normal repository CI because GitHub suppresses recursive workflow triggers for commits pushed by `GITHUB_TOKEN`.

Status at creation: implementation applied; normal CI validation pending. This is not owner acceptance and does not authorize merge to `main`.
