# Phase 4A — preqa.9 owner/device QA progress

**Date:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Candidate:** `0.4.0-preqa.9 / 40900`  
**Candidate commit:** `cd0c203d337c062fa388010d300e875f2f54ced7`  
**Status:** PHYSICAL OWNER QA IN PROGRESS / NOT YET ACCEPTED

## Evidence recorded

### Step 1 — update-in-place and persistence sanity

Owner report: **PASS** for steps 1–6 of the initial QA handoff.

This confirms, on the owner's physical test device for the current QA pass:

- `preqa.9 / 40900` installed over the previous QA installation without requiring app-data clearing;
- application launched normally;
- existing campaign data remained present;
- the normal test character remained present;
- representative previously saved data remained present across General, Combate, Equipo/Monedas, Conjuros and Notas sanity inspection;
- full application close/reopen completed successfully with the checked data still present.

Interpretation: the initial upgrade/persistence boundary for `preqa.9 / 40900` is **PASS**. This is real owner/device evidence, not inferred from CI.

## Still open

This does not accept Phase 4A and does not establish tablet PASS/FAIL.

The next targeted physical QA boundary is canonical HP synchronization and the damage/healing workflow across General and Combate. Subsequent representative repaired boundaries remain pending under the P17 owner/device QA gate.

Any physical defect may reopen only the relevant accepted P1–P16 repair boundary. DM implementation remains blocked until explicit Phase 4A owner acceptance/closure.
