# D-0042 — Minimum supported Android version is Android 11 / API 30

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner

The Android application uses **minSdk 30**, corresponding to **Android 11**.

## Rationale

The actual expected device set does not include devices below Android 11. The project owner is also the DM and the oldest Android phone/tablet user relevant to the project, so supporting older Android versions would add compatibility and testing work for hypothetical users rather than real requirements.

The owner also explicitly prioritizes a modern, polished Android UX and does not want unnecessary legacy-SDK constraints to limit that goal.

## Consequences

- Android 10 and earlier are intentionally unsupported.
- The implementation may use APIs and modern platform behavior available from API 30 onward without carrying compatibility code for older Android generations.
- This is a personal-project compatibility decision, not a market-coverage decision.
- `targetSdk` and `compileSdk` remain maintenance/tooling values and may advance independently as required by Android/Google Play tooling; they do not change the minimum supported device version unless `minSdk` is separately reconsidered.
- If the real device set changes later, `minSdk` may be revisited based on concrete need rather than speculative compatibility.

## Proportionality

This decision follows C-0009: support the devices that actually matter to the project and avoid legacy compatibility burden that provides no practical benefit.

This resolves the minimum-Android-version portion of D-0009. D-0009 is now resolved/Approved by the complete D-0034 through D-0043 architecture set.

> Detail-record note: this file preserves the fuller rationale for D-0042. The chronological authoritative entry is consolidated in `docs/DECISIONS.md`.
