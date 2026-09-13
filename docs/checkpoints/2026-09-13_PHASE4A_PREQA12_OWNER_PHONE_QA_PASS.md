# Phase 4A — preqa.12 owner phone QA PASS

> **SUPERSEDED STATUS NOTE — 2026-09-13:** This checkpoint remains historical evidence of the earlier bounded `5/5` geometry recheck and `7/7` broad regression report, but its conclusion that the complete phone gate was `CLOSED / PASS` is superseded by the owner's later, more detailed 23-check pass on the same candidate. See `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_PHONE_FINDINGS_P17_ROUTE.md`. Individual PASS evidence below remains valid where not contradicted; do not discard or rerun it mechanically.

**Date:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Candidate:** `0.4.0-preqa.12 / 41200`  
**Exact candidate commit:** `abfc7e4a1519a27117f194721a425d75cb5df68a`  
**Historical status at time recorded:** OWNER PHONE QA PASS / P17 TABLET QA PENDING  
**Current interpretation:** SUPERSEDED BY LATER EXPANDED PHONE FINDINGS

## Result originally recorded

The owner physically tested the exact `preqa.12 / 41200` candidate and reported both then-requested post-repair phone boundaries as PASS.

### Affected transversal geometry recheck — 5/5 PASS

The owner reported **all 5 PASS** for the bounded repair recheck:

1. representative ordinary labelled/editable Player fields in portrait, including the prior attack/action editor example;
2. representative equivalent fields in landscape;
3. structured-dice `+ / −` selector compactness/alignment and tapability;
4. representative text/numeric editability, with multiline sanity where applicable;
5. short Save/Cancel sanity in an affected editor.

This physically closed the specific `preqa.11` check-8 geometry boundary repaired by `preqa.12`.

### Remaining phone-wide regression gate — 7/7 PASS

After the affected geometry boundary passed, the owner performed the then-requested broad phone regression gate on the same exact candidate and reported **all 7 PASS**:

1. character lifecycle: ordinary change/save/leave/reopen persistence;
2. main Player navigation reachability/scrollability sanity;
3. representative non-Combat editor edit/save behavior;
4. keyboard/editor behavior near the lower screen area;
5. harmless preference/settings behavior and persistence sanity;
6. cancel/discard or equivalent non-destructive lifecycle safety;
7. broad representative portrait/landscape visual sweep for new clipping, overlap, unreachable controls, broken scrolling, or grossly disproportionate UI.

This gate deliberately did not repeat already-valid focused checks whose behavior had not been invalidated by the transversal repair.

## Cumulative physical evidence preserved

- `preqa.10` R1–R3: PASS;
- `preqa.11` checks 1–7 and 9: PASS;
- `preqa.11` check 8: FAIL, physically closed for its geometry boundary by the `preqa.12` 5/5 affected-boundary PASS;
- `preqa.12` affected transversal geometry recheck: **5/5 PASS**;
- `preqa.12` broad phone-wide regression gate as then phrased: **7/7 PASS**.

These PASS results remain evidence. However, the later 23-check pass found additional structured-damage and checkbox/responsive-layout defects, so this checkpoint no longer controls the current gate status.

## Candidate automation evidence remains unchanged

- candidate commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- Scaffold run `34776627282` — SUCCESS;
- artifact ID `10323602038` / `dnd-custom-aid-debug-apk`;
- ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

No product code, version, artifact, or candidate identity is changed by this documentation correction.

## Current route

Use `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_PHONE_FINDINGS_P17_ROUTE.md` for current status. P17 tablet QA may proceed on the same candidate because the known phone findings do not constitute a hard shared/systemic failure that makes tablet evidence meaningless. Phase 4A remains OPEN; DM implementation remains blocked until explicit Phase 4A closure.
