# Phase 4 M6 Owner Real-Device QA — Historical Detour Evidence

**Date:** 2026-09-08  
**Status:** HISTORICAL / SUPERSEDED — preserved during D-0048 main consolidation  
**Original QA documentation branch:** `tmp/phase4-m6-qa-pause-docs`  
**Frozen product candidate that was briefly tested:** `tmp/phase4-m5-frozen-qa-candidate`  
**Frozen candidate commit:** `adc286b3e1305ed706c2ed04d478a43652f6b365`  
**Frozen candidate APK SHA-256:** `e31ce44a84cd79260ea2c51c65cb6a63675b1f916998e44d583358d72893c8ee`

## Why this file exists

This record was originally the only unique commit on `tmp/phase4-m6-qa-pause-docs`. It is copied into canonical history so that repository consolidation does not lose real owner QA evidence.

Its original "M6 is in progress" next-action instruction is **superseded**. Repository reconstruction on 2026-09-08 established that the newer practical owner-audition path is build `0.4.0-preqa.7` / `40700` on the Phase 4 pre-QA line. Do not use this file as the current resume pointer; use `docs/checkpoints/LATEST.md`.

## M6 Test 1 — in-place upgrade / data preservation

Result: **PASS**.

Owner installed the exact frozen M6 APK over the previously installed QA version without uninstalling or clearing app data.

Owner observations/results:

- installation over previous version: OK;
- campaigns preserved: OK;
- characters preserved: OK;
- existing character data preserved: OK;
- after full app close/reopen: OK.

No migration/data-loss blocker was observed in this gate.

## Historical interpretation

This PASS remains valid evidence about that historical frozen candidate and upgrade path. It does **not** establish acceptance of the current build `40700`, does not reactivate historical M6, and does not override the later owner-audition findings.

The frozen candidate branch remains immutable historical evidence.
