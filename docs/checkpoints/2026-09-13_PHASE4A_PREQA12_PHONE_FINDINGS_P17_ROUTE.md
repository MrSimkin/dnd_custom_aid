# Phase 4A — preqa.12 expanded phone findings and P17 route

**Date:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Candidate:** `0.4.0-preqa.12 / 41200`  
**Exact candidate commit:** `abfc7e4a1519a27117f194721a425d75cb5df68a`  
**Status:** PHONE QA EXECUTED WITH OPEN FINDINGS / P17 TABLET QA MAY PROCEED ON SAME CANDIDATE / PHASE 4A OPEN

## Why this checkpoint exists

After the earlier bounded `5/5` geometry recheck and `7/7` broad phone regression pass, the owner performed a more detailed 23-check phone pass on the same exact `preqa.12` candidate. That later evidence exposed additional defects and observations, so the earlier interpretation that the complete phone gate was cleanly `CLOSED / PASS` is superseded.

The earlier individual PASS evidence remains valid for behavior actually exercised and not contradicted by this later pass. Do **not** restart unaffected phone testing from zero.

## Latest owner phone results

The owner reported:

- checks **1–6:** PASS;
- check **7:** near-pass but **functional defect found** — in structured damage entry, selecting `dice` and then entering a number in the `mod` field can change the die selection to `Otro`;
- check **8:** **functional defect found** — under `Otro`, numeric input intended for `mod` is routed/behaves as though it belongs to `caras del daño`, and the modifier cannot be entered normally;
- check **9:** PASS, with UX request to make the structured-damage sign control toggle directly between `+` and `−` when tapped rather than using a dropdown;
- checks **10–15:** PASS;
- check **16:** PASS, with non-blocking density request to reduce window vertical padding a little further if it can be done without making controls undersized;
- check **17:** broad equivalent-control application looked generally present, but the owner identified an **open app-wide checkbox/responsive-layout family** requiring audit:
  - **17.1** checkbox fields are not yet aligned with the app design grammar for size, font, spacing, margins and padding in portrait/landscape; explicit examples include Equipo `Equipado` and `Equipo especial`;
  - **17.2** Conjuros add/edit landscape does not consistently exploit available width for multi-column checkbox/source/prepared layouts (for example `mago` and `brujo` remain stacked when multiple items can fit in one row);
  - **17.3** Conjuros add/edit portrait and landscape does not consistently follow the established rule of avoiding two rows when one fits, including the V/S/M and Concentración/Ritual checkbox groups;
  - owner explicitly requested a **full Player app audit** for equivalent checkbox/group/layout sites before repair scope is finalized;
- checks **18–20:** PASS;
- check **21:** NOT ASSESSED — owner did not understand the test as phrased;
- check **22:** INCOMPLETE/AMBIGUOUS — owner response was `but on landscape`, so no PASS is inferred;
- check **23:** PASS.

## Classification

Current open phone findings are:

1. **Structured-damage interaction/state binding** — checks 7–8; functional, bounded until source audit proves otherwise.
2. **Checkbox/control density and responsive grouping** — check 17.1–17.3; explicitly requires app-wide Player source audit and cross-form-factor treatment.
3. **Structured-damage sign interaction** — check 9 UX refinement; non-blocking.
4. **Window vertical density** — check 16 refinement; non-blocking and conditional on preserving usable/safe control geometry.
5. **Unresolved evidence only** — checks 21 and 22 need later clarification/recheck; they are not silently promoted to PASS.

No current report describes an install/start failure, crash/ANR, persistence corruption, navigation-wide blocker or global UI failure that would make observations on tablet meaningless.

## P17 route decision

The controlling `docs/TESTING.md` states that P17 physical Player-tablet QA should proceed when the phone result does not expose a **hard shared/systemic failure that would make tablet evidence meaningless**, and explicitly states that a bounded/local phone defect does not automatically block tablet QA.

The current findings do **not** meet that blocking threshold. Therefore the approved route is:

1. preserve this `preqa.12` phone evidence as-is;
2. proceed now to **P17 physical tablet portrait/landscape QA on the same exact `preqa.12 / 41200` APK**;
3. use tablet QA to discover tablet-specific issues and to characterize responsive/shared families, especially checkbox/layout behavior;
4. after P17 discovery is complete, perform the requested app-wide checkbox/layout audit and consolidate all phone + tablet findings into one coherent repair batch;
5. produce a new monotonic QA candidate if product code changes materially;
6. revalidate only affected/failed/touched phone and tablet families plus unresolved checks 21/22, preserving unrelated PASS evidence;
7. only then consider explicit Phase 4A owner acceptance/closure.

Tablet PASS/FAIL still requires actual tablet evidence. Running P17 now does **not** accept the known phone defects and does **not** close Phase 4A.

## Candidate evidence unchanged

- Scaffold run `34776627282` — SUCCESS;
- artifact ID `10323602038` / `dnd-custom-aid-debug-apk`;
- ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

No product code, APK, version or candidate identity is changed by this checkpoint.

## Phase boundary

Phase 4A remains **OPEN**. No P18 exists. DM implementation remains blocked until blocking defects are repaired/revalidated as needed, P17 has real tablet evidence, and the owner explicitly closes Phase 4A.
