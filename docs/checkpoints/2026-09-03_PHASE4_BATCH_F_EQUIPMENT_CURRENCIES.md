# Phase 4 — Batch F Equipment + Currencies closure

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** GREEN / COMPLETE  
**Canonical `main`:** untouched

## Scope closed

Batch F closes the approved Equipment + Currencies usability, metadata and tablet behavior while preserving the established manual-order and paper-first character model.

Delivered across F1–F3:

- materially denser ordinary inventory presentation;
- compact currency grid/editors;
- independent Manual / A–Z presentation for ordinary and special equipment;
- A–Z as presentation only; stored manual order survives and returns unchanged;
- visible drag feedback only when Manual mode is active and search/filters do not make reorder ambiguous;
- search across inventory text and location/container metadata;
- carried/stored, equipped, special, consumable, ammunition and located filters;
- explicit carried/stored metadata instead of unreliable location-name heuristics;
- total carried weight and attunement summary;
- location/container editing for both ordinary and special equipment;
- consumable/ammunition quick-use quantities;
- duplicate and collapsible sections;
- preservation of special-item location when converting to ordinary equipment;
- unified unsaved Equipment draft containing items, currencies and inventory-usage metadata, including newly-created objects before global Save;
- phone IME-safe modal editor retained;
- tablet/wide multi-column list plus persistent master-detail editor pane;
- selected item highlight and list-context preservation while editing on tablet.

## F1 — inventory foundation

Primary implementation:

- `b440eadb569d2ded4e169b37efebcab18dec380b`.

Migration fixture repair only:

- `ce3fc4996cf2090134f783dfd0d6bd9da55cba52`.

Final F1 controlling workflow:

- `33813882408` — PASS across backend, shared/Kotlin tests, SQLDelight migration regression, Android debug, Desktop and APK upload.

## F2 — Equipment/Currencies UI

Primary UI integration:

- `2141c6e93139f04519f6c129d950ec2eb751ebef`.

Controlling workflow:

- `33814616186` — PASS across backend, shared/Kotlin tests, Android debug, Desktop and APK upload.

Evidence artifact:

- `dnd-custom-aid-debug-apk`;
- ID `9916138915`;
- digest `sha256:d47fa825c134591133a6cb246ddab8cf1dc2598dfa58e00e31afdc76528716d8`.

## F3 — tablet master-detail

F3 was required because the approved Batch-F map explicitly requires tablet `multi-column/master-detail`, not merely a wider phone grid.

Implementation:

- `599926c94ebf4b2f7b0f09171255e4fa90152c2f`.

Controlling checkpoint head:

- `a0f41acf0ad440f0af43694ee5e46a4c0d7f8c17`.

Controlling workflow:

- `33816879652` — PASS across backend, shared/Kotlin tests, Android debug, Desktop and APK upload.

Evidence artifact:

- `dnd-custom-aid-debug-apk`;
- ID `9916904744`;
- digest `sha256:4ed09ae593f514967c16f256a7542e73467d5aea0bc7673fa25d95207eb01822`.

These artifacts are integration evidence only. They are not the final frozen owner-QA candidate from batch L.

## Gate F result

Automated Batch-F gate is GREEN:

- sort/manual-order behavior: covered by pure inventory operations and full regression suite;
- quantity/weight/attunement: covered by operations/tests and assembled UI;
- persistence: schema-8 additive migration + closure repository round-trip green;
- IME: phone editor uses the reusable IME-safe pattern; tablet pane uses independent scrolling inside the IME-padded surface;
- responsive behavior: phone retains modal flow; wide layouts use multiple columns plus master-detail.

Real-device portrait/landscape and larger-text acceptance remains part of the final owner QA matrix and is not replaced by CI.

## Exact continuation

Proceed to **Batch G1 — Rasgos closure**:

- source/type group/filter/search;
- remaining/max usage meter;
- Favorite / Quick Access integration;
- duplicate;
- real manual drag feedback preserved;
- responsive tablet grouping.

Do not begin DM implementation while the Phase 4 closure plan remains open.
