# Phase 4 — Batch I1 adaptive shell

**Date:** 2026-09-03 / 2026-09-04 UTC  
**Status:** GREEN  
**Branch:** `implementation/phase4-character-closure`  
**Controlling tested commit:** `ebceb1c747ff5649d8b0038ddf38b94b9caafcc6`  
**Workflow:** `33832927017` — SUCCESS  
**Artifact:** `9922360358` (`dnd-custom-aid-debug-apk`)  
**Artifact ZIP digest:** `sha256:2dfd189833807ff154647a6f0b28dd25d4d7d886fd899dc53c063ec12cb7f953`

## Delivered

Batch I1 closes the holistic adaptive character-shell work without rewriting the responsive/master-detail behavior already delivered incrementally in F–H.

- added available-width navigation presentation with ordinary top tabs on smaller/wide layouts and a Material 3 navigation rail only when there is enough width;
- rail threshold is `900dp`, deliberately leaving the existing `700dp+` master-detail content width usable without sacrificing too much horizontal space to navigation;
- compact character identity/save header remains outside tab scrolling content;
- existing list-heavy master-detail implementations remain authoritative and were not duplicated;
- added local per-character last-tab persistence through `CharacterNavigationPreferenceStore`;
- rotation/recreation continues to use `rememberSaveable`, while full app reopen can restore the last tab from local preferences;
- restored tab names are passed through the existing visibility resolver, so a hidden Conjuros/conditional-module tab safely falls back rather than reopening an unavailable destination;
- the resolved safe tab is written back to preferences, preventing a permanently stale remembered tab;
- navigation state remains local UI state and was not added to character/schema mechanics.

## Diff boundary

Compared with the pre-I1 durable closure head `9a072b05fd948c23b4c3d494a6274c5db3d8d7d0`, the final tested product tree changes only:

- `CharacterAdaptiveShellV4.kt` — new;
- `CharacterNavigationStateV4.kt` — new;
- `CharacterEditorV4.kt` — narrow shell + last-tab wiring;
- `MainActivity.kt` — explicit navigation-store ownership/injection.

No existing F–H responsive feature surface was rewritten.

## Gate history

An initial exact-tree gate, workflow `33832706244`, correctly failed Android compilation because `CharacterAdaptiveShellV4.kt` explicitly imported `androidx.compose.foundation.layout.weight`; with the Compose version used by this repository that import resolved to an internal parent-data symbol. The failure did not reach or modify the durable branch.

The repair removed exactly that one import. `Modifier.weight(...)` remains resolved from the normal `RowScope`/`ColumnScope` receivers.

Final controlling gate on `ebceb1c747ff5649d8b0038ddf38b94b9caafcc6`:

- backend dependency install + Worker type-check — PASS;
- full shared/Kotlin tests — PASS;
- Android debug assembly — PASS;
- Desktop build — PASS;
- Android debug APK upload — PASS.

The artifact recorded above is integration evidence only, not the future frozen owner-QA candidate.

## Real-device boundary

Automated compilation cannot substitute for final device acceptance. Batch M still must exercise phone/tablet portrait/landscape and representative larger text, including rail/top-tab transitions, D16 context, Back behavior and full application reopen.

## Exact continuation

Proceed to **Batch I2 — Supercompact + Table mode**.

Recommended internal split:

1. **I2a Supercompact completion** — authoritative persisted sheet + closure state, ordered Quick Access/Favorites projection, responsive density and intentional live HP/resource/slot controls without duplicate state;
2. **I2b Table mode completion** — suppress structural editing across the character experience while retaining explicitly permitted operational controls. Do not implement Table mode as a blanket pointer-blocking overlay.

No schema migration is expected for I2 unless implementation uncovers a genuinely missing durable requirement.
