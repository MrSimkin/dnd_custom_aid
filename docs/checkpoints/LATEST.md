# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Current exact QA candidate:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a` — AUTOMATION GREEN; PHONE QA HAS OPEN FINDINGS  
**Current gate:** P17 physical tablet QA on the same candidate before consolidated repair  
**Release status:** debug/development; Phase 4A OPEN; DM implementation blocked pending explicit owner closure

## Resume here

1. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_PHONE_FINDINGS_P17_ROUTE.md` — **controlling current physical-QA status and route**.
2. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_QA_CANDIDATE.md` — exact candidate/run/artifact/digest evidence, updated with current phone findings and P17 route.
3. `docs/PROJECT_STATE.md` — live Player authority/current gate.
4. `docs/TESTING.md` — controlling physical-QA policy; explicitly permits P17 when phone findings do not make tablet evidence meaningless.
5. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_OWNER_PHONE_QA_PASS.md` — historical bounded 5/5 + 7/7 PASS evidence; **superseded for current gate status** by the later 23-check findings.
6. `scripts/check_player_control_geometry.py` — persistent field-geometry guard.

## Current phone evidence

Preserve all earlier PASS evidence where not contradicted. Latest detailed 23-check pass on exact `preqa.12` gives:

- 1–6 PASS;
- 7 OPEN DEFECT — modifier entry can change selected die to `Otro`;
- 8 OPEN DEFECT — `Otro` modifier input is misrouted/behaves as `caras del daño`;
- 9 PASS + UX request for direct `+`/`−` tap-toggle;
- 10–15 PASS;
- 16 PASS + optional tighter window vertical padding where safe;
- 17 OPEN APP-WIDE FAMILY — checkbox sizing/font/spacing/padding and responsive grouping audit/repair required, including Equipo and Conjuros examples;
- 18–20 PASS;
- 21 NOT ASSESSED because the test was not understood;
- 22 INCOMPLETE/AMBIGUOUS;
- 23 PASS.

The earlier complete-phone `CLOSED / PASS` interpretation is therefore superseded. Do not discard the individual PASS evidence and do not restart unaffected tests from zero.

## P17 decision

Proceed to **P17 tablet portrait/landscape QA now on the same exact `preqa.12 / 41200` APK**.

This is permitted by the controlling QA contract because the current phone defects do not constitute a hard shared/systemic failure that makes tablet evidence meaningless. Tablet testing before repair is also useful for characterizing the open responsive checkbox/layout family across form factors.

P17 execution does **not** waive the phone defects and does **not** close Phase 4A.

## Candidate evidence unchanged

- candidate commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- Scaffold `34776627282` — SUCCESS;
- artifact ID `10323602038` / `dnd-custom-aid-debug-apk`;
- ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

No product code, APK, version or artifact changed during the documentation correction.

## Exact route

1. Execute P17 tablet QA on `preqa.12` and record actual tablet evidence.
2. Combine tablet findings with phone checks 7–8, the app-wide 17.1–17.3 checkbox/responsive audit, and accepted non-blocking refinements 9/16.
3. Implement one coherent repair batch and create a new monotonic QA candidate if product code changes materially.
4. Targeted revalidation only for failed/touched/affected phone/tablet families plus unresolved phone checks 21/22; preserve unrelated PASS.
5. Explicit owner Phase 4A closure only after the repaired state has sufficient cross-device evidence.

No P18 exists. DM implementation remains blocked until explicit Phase 4A closure.
