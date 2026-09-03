# Phase 4 — Batch F3 Equipment tablet master-detail checkpoint

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** GREEN / COMPLETE  
**Canonical `main`:** untouched

## Why F3 exists

Batch F's approved execution plan requires `tablet multi-column/master-detail`, not merely wider columns. F2 completed the dense inventory/currency feature set and passed its full gate, but its wide layout still opened item editing as a modal surface.

F3 closes only that remaining Batch-F tablet requirement. It does not add schema, inventory semantics, rules enforcement, or unrelated scope.

## Proven F2 baseline

F2 controlling workflow `33814616186` completed successfully:

- shared/Kotlin tests: success;
- Android debug assembly: success;
- Desktop build: success;
- backend type-check: success;
- APK upload: success.

F2 artifact:

- name: `dnd-custom-aid-debug-apk`;
- artifact ID: `9916138915`;
- workflow artifact digest: `sha256:d47fa825c134591133a6cb246ddab8cf1dc2598dfa58e00e31afdc76528716d8`.

This artifact is integration evidence, not the final owner-QA candidate.

## F3 implementation

Integration commit:

- `599926c94ebf4b2f7b0f09171255e4fa90152c2f` — `feat: add Batch F3 equipment tablet master-detail`.

Wide Equipment behavior now:

- the inventory/currency list remains on the left;
- a stable detail/editor pane remains on the right instead of opening the phone modal;
- the selected inventory object is visibly highlighted in the list;
- the editor pane has its own vertical scroll;
- the list preserves its existing search, filters, section collapse state and manual/A–Z projection while editing;
- closing the detail pane does not reset list context;
- deleting the selected object closes only that detail selection;
- an empty detail pane provides a direct `+ Añadir objeto` action;
- phone/narrow layout continues using the already-green IME-safe modal editor from F2.

All inventory persistence, consumable/ammunition, carried/stored, location/container, sort-order and currency behavior continues to use the F1/F2 implementation without schema changes.

## Gate result

Controlling checkpoint head:

- `a0f41acf0ad440f0af43694ee5e46a4c0d7f8c17`.

Controlling workflow:

- `33816879652` — PASS.

Verified green:

- all shared desktop tests;
- Android debug assembly;
- Desktop build;
- backend type-check;
- APK artifact upload.

Evidence artifact:

- name: `dnd-custom-aid-debug-apk`;
- artifact ID: `9916904744`;
- workflow artifact digest: `sha256:4ed09ae593f514967c16f256a7542e73467d5aea0bc7673fa25d95207eb01822`.

This artifact is integration evidence, not the final owner-QA candidate.

## Result

F3 is GREEN and Batch F is closed by `docs/checkpoints/2026-09-03_PHASE4_BATCH_F_EQUIPMENT_CURRENCIES.md`.

Proceed to Batch G1 — Rasgos closure. No DM implementation is permitted while the Phase 4 closure plan remains open.
