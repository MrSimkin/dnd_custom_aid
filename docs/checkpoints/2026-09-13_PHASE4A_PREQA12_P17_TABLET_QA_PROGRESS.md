# Phase 4A — preqa.12 P17 tablet QA progress

**Date:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Candidate:** `0.4.0-preqa.12 / 41200`  
**Exact candidate commit:** `abfc7e4a1519a27117f194721a425d75cb5df68a`  
**Status:** P17 TABLET QA IN PROGRESS / BASELINE 1–5 PASS / NON-BLOCKING FINDINGS T1–T4 OPEN

## Tablet QA evidence so far

The owner is executing P17 on the same exact `preqa.12 / 41200` APK before consolidated phone+tablet repair, as authorized by the controlling physical-QA contract.

### Batch 1 — baseline + adaptive shell

1. Install/update + launch: **PASS**.
2. Campaign baseline: **PASS**.
3. Tablet portrait main navigation/adaptive shell: **PASS**.
4. Tablet landscape main navigation/adaptive shell: **PASS**.
5. Rotation/state sanity (portrait → landscape → portrait): **PASS**.

No baseline finding currently blocks continued tablet QA.

## Non-blocking findings captured during P17

### T1 — card reorder/movement interaction behaves abnormally across devices/orientations/column counts

Owner reports that moving cards behaves "weird" and is difficult to describe in words. It occurs across device/form-factor perspectives, portrait/landscape, and with different column counts, including a single column. Treat as an open P6/reorder interaction defect/UX issue requiring source audit and physical-video review before repair design.

The owner supplied a video in chat, but the attachment was not exposed to the available file-inspection layer in the recording turn. Do not invent a more specific motion diagnosis until the video is actually inspectable or equivalent direct evidence is available. This does not block P17.

### T2 — dice-mode visual/product contract + custom throw capability correction

Owner clarified desired behavior that had not been specified clearly enough earlier:

- when dice mode is used for dice throws, the visual die should resemble the actual die shape/border for the selected die;
- for `Otro` or total-only/result representations, use a circle;
- the same visual principle should apply to damage throws;
- move the Dice Mode setting/control to the relevant tab rather than leaving it only in the present settings location;
- Custom Throw must allow die choice and modifier entry comparable to an attack/damage throw, rather than lacking those choices.

Treat this as an owner-authorized product/UX correction to include in the consolidated repair batch, not as a tablet blocker.

### T3 — App Settings column control no longer represents current adaptive behavior clearly

Owner reports that the current column setting in Application Settings is not representative/intuitive relative to the app's evolved adaptive/column behavior. Audit both the setting control and the behavior it governs; likely redesign the control/semantics rather than applying a cosmetic label-only patch. Include in consolidated repair scope. Non-blocking for P17.

### T4 — density/scale control must center 100% and be symmetric

In Application Settings, 100% should be the visual/semantic center of the scale and decrement/increment options should be symmetric around it. The owner explicitly accepts adding 50% and 60% if needed to obtain symmetry. Audit current supported range/steps and redesign the control accordingly. Non-blocking for P17.

## Relationship to open phone findings

All earlier phone findings remain open where applicable, especially structured-damage checks 7–8 and the app-wide checkbox/responsive-layout family 17.1–17.3. P17 remains intentionally in progress before repair so tablet evidence can inform one coherent cross-device repair batch.

## Exact next action

Continue P17 on `preqa.12` with substantive tablet coverage: Combat/P5/P16, Conjuros sticky/adaptive behavior, representative P9 editor/IME behavior, P6 reorder characterization, settings responsiveness, P15 Supercompact, P14 Table Mode, larger text/density, persistence/reopen and canonical shared-state sanity (HP).

Do not repair product code mid-P17 unless a newly discovered hard/systemic failure makes remaining tablet evidence meaningless. Phase 4A remains OPEN; DM implementation remains blocked pending explicit owner closure.
