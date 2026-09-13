# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Current exact QA candidate:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a` — AUTOMATION GREEN; PHONE QA HAS OPEN FINDINGS  
**Current gate:** P17 physical tablet QA IN PROGRESS on the same candidate before consolidated repair  
**Release status:** debug/development; Phase 4A OPEN; DM implementation blocked pending explicit owner closure

## Resume here

1. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_P17_TABLET_QA_PROGRESS.md` — **controlling current P17 evidence: tablet checks 1–5 PASS; findings T1–T4 captured; continue substantive tablet QA**.
2. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_PHONE_FINDINGS_P17_ROUTE.md` — controlling phone findings and decision to run P17 before repair.
3. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_QA_CANDIDATE.md` — exact candidate/run/artifact/digest evidence.
4. `docs/PROJECT_STATE.md` — live Player authority/current gate.
5. `docs/TESTING.md` — controlling physical-QA policy; explicitly permits P17 when phone findings do not make tablet evidence meaningless.
6. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_OWNER_PHONE_QA_PASS.md` — historical bounded 5/5 + 7/7 PASS evidence; **superseded for current gate status** by the later 23-check findings.

## Current tablet evidence

P17 is being executed physically on exact `preqa.12 / 41200`.

Tablet Batch 1:

- 1 Install/update + launch: PASS;
- 2 Campaign baseline: PASS;
- 3 portrait main navigation/adaptive shell: PASS;
- 4 landscape main navigation/adaptive shell: PASS;
- 5 portrait↔landscape rotation/state sanity: PASS.

No tablet baseline finding currently blocks further P17.

Non-blocking findings captured during P17:

- **T1:** card reorder/movement interaction behaves abnormally across devices/orientations/column counts, including one column; treat as open P6/reorder interaction defect. Owner supplied video, but it was not inspectable through the available file layer in the recording turn, so do not invent a more specific diagnosis yet.
- **T2:** owner clarified dice-mode UX/product contract: die result visuals should resemble selected die shape; `Otro`/total use circle; same applies to damage; move Dice Mode control to relevant tab; Custom Throw must allow die + modifier choices comparable to attack/damage.
- **T3:** App Settings column control no longer clearly represents current adaptive/column behavior; audit/remake control + semantics rather than cosmetic patch.
- **T4:** App Settings density/scale must center 100% and use symmetric decrement/increment options; 50%/60% may be added if needed for symmetry.

All T1–T4 are non-blocking for continued P17 and belong in the post-P17 consolidated repair batch.

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

The earlier complete-phone `CLOSED / PASS` interpretation remains superseded. Do not discard the individual PASS evidence and do not restart unaffected tests from zero.

## Candidate evidence unchanged

- candidate commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- Scaffold `34776627282` — SUCCESS;
- artifact ID `10323602038` / `dnd-custom-aid-debug-apk`;
- ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

No product code, APK, version or artifact changed during QA recording.

## Exact route

1. Continue P17 tablet QA on `preqa.12`, covering Combat/P5/P16, Conjuros, representative editor/IME behavior, reorder, settings responsiveness, P15 Supercompact, P14 Table Mode, larger text/density, persistence/reopen and canonical shared-state sanity such as HP.
2. Combine tablet findings with phone checks 7–8, the app-wide 17.1–17.3 checkbox/responsive audit, accepted refinements 9/16, and T1–T4.
3. Implement one coherent repair batch and create a new monotonic QA candidate if product code changes materially.
4. Targeted revalidation only for failed/touched/affected phone/tablet families plus unresolved phone checks 21/22; preserve unrelated PASS.
5. Explicit owner Phase 4A closure only after the repaired state has sufficient cross-device evidence.

No P18 exists. DM implementation remains blocked until explicit Phase 4A closure.
