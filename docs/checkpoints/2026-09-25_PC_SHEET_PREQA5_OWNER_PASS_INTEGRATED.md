# Checkpoint — PC Sheet preqa.5 owner PASS integrated

**Date:** 2026-09-25 (Chile local time)  
**Normal trunk:** `main`  
**Integrated merge:** `8b1618d56d5483524559f9598bc8862acfe91c9a` (PR #104)  
**Post-merge Scaffold:** `36203501345` / #3826 — SUCCESS  
**Status:** ALDREN CROSS-FAMILY REPAIR INTEGRATED / OWNER PASS / STAGED ANDROID RUNTIME SMOKE ACTIVE

## Closed gate

The repaired Android candidate `0.5.0-preqa.5` / versionCode `50500` passed the bounded owner rerun for **Aldren Vale / Permanente** across:

1. Fantasy Sheet;
2. Custom v1;
3. Custom v2 — per Attribute;
4. Custom v2 — per Ability.

The repair implementation was repository-green before QA, was reconciled with current `main` without rewriting the tested runtime implementation, and PR #104 was merged after the reconciled head passed Scaffold.

Post-merge `main` also passed Scaffold #3826 / `36203501345`, including backend, hosted-database, route guard, Kotlin/Android build/tests, APK upload and PDF proof uploads.

## Accepted minor residual — non-blocking

Owner observation in Custom v1 and Custom v2 continuation pages:

- column/header text can overlap the first rendered row;
- vertical row spacing is excessively tall across the affected page.

The owner explicitly classified this as **minor and non-blocking** and authorized continuing while fixing it opportunistically.

This is a bounded follow-up. It does **not** reopen the passed Aldren semantic/routing gate and does not authorize redesign of the frozen base-sheet visual geometry.

## Historical authority

The preqa.4 owner failure list remains useful historical acceptance evidence:

`docs/checkpoints/2026-09-25_PC_SHEET_ALDREN_PREQA4_CROSS_FAMILY_REVIEW.md`

The completed repair-history checkpoint is:

`docs/checkpoints/2026-09-25_PC_SHEET_CROSS_FAMILY_RUNTIME_REPAIR_ACTIVE.md`

Those files are evidence, not the current continuation route.

## Current manual boundary

Resume the staged Android real-device/runtime smoke using the integrated preqa.5 behavior:

1. **Aldren:** exercise **Share** through a real compatible target and verify the shared PDF is readable. Save/open has already been exercised during the four-family rerun.
2. **Ilyra:** export **Custom v2 + Spellbook** after one harmless unsaved edit; choose **Exportar sin guardar**; verify the PDF reflects the edit while persisted character data remains unchanged.
3. **Mara:** exercise both **Custom v2 per Attribute** and **Custom v2 per Ability** with Extended content and inspect custom-stat/overflow continuation behavior.
4. **Current Snapshot:** request it once and verify the known user-visible fallback notice and Permanent fallback.

Record PASS or exact observed defects.

The accepted Custom v1/v2 continuation spacing/overlap residual does not block these checks.

## Current branch / PR state

- no non-main implementation branch is continuation authority;
- PR #104 is MERGED / INTEGRATED;
- normal continuation authority is current `main`;
- the old repair branch may remain as historical Git evidence but must not be used as the resume route.

## Blocked later work

Do not start **Media / Handouts** until the staged Android runtime smoke above is recorded.

No external provider action is required for the current manual PDF/runtime boundary.


## End-of-day consolidation — 2026-09-25

Owner requested to stop for the day after the repair/integration closure.

State at pause:

- `main` is integrated and green through documentation head `d6c930f38720c5a2e1c5826a291066ca7a816500`;
- the Aldren/Permanente four-family `preqa.5` gate remains PASS;
- the accepted Custom v1/v2 header-overlap / excessive-row-height residual remains minor and non-blocking;
- **Aldren Share has NOT yet been executed** in the resumed runtime-smoke sequence;
- Ilyra, Mara and Current Snapshot checks in that sequence also remain unexecuted;
- no repository repair or implementation work is required before resuming manual QA.

### Exact resume point

Resume with **Step 1 — Aldren Share** only:

1. Aldren Vale;
2. Permanente;
3. generate one already-validated family (Fantasy Sheet is sufficient);
4. choose Share;
5. send/open through one real compatible Android target;
6. verify the received PDF is readable and non-corrupt;
7. record PASS or exact failure.

After that result, continue one QA step at a time. Do **not** repeat the already-passed four-family Aldren Save/open/content inspection unless a new defect requires it.

**Session status:** PAUSED BY OWNER / SAFE TO STOP.
