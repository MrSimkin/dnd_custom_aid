# Phase 4A — preqa.12 P17 tablet QA progress

**Date:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Candidate:** `0.4.0-preqa.12 / 41200`  
**Exact candidate commit:** `abfc7e4a1519a27117f194721a425d75cb5df68a`  
**Status:** P17 TABLET DISCOVERY COMPLETE / OPEN FINDINGS REQUIRE CONSOLIDATED REPAIR + TARGETED REVALIDATION

## Tablet QA evidence

The owner executed P17 on the same exact `preqa.12 / 41200` APK before consolidated phone+tablet repair, as authorized by the controlling physical-QA contract.

### Batch 1 — baseline + adaptive shell

1. Install/update + launch: **PASS**.
2. Campaign baseline: **PASS**.
3. Tablet portrait main navigation/adaptive shell: **PASS**.
4. Tablet landscape main navigation/adaptive shell: **PASS**.
5. Rotation/state sanity (portrait → landscape → portrait): **PASS**.

### Batch 2 — Combat + shared state + Conjuros

6. Combat tablet portrait: **PASS**. Owner reports no new observations beyond findings already recorded elsewhere.
7. Combat tablet landscape: **FAIL / OPEN**. The available width is not used adaptively; the HUD/cards/content stretch essentially across the full remaining screen width, producing large horizontal dead areas rather than a deliberate wide-screen composition. Physical image evidence supplied by the owner corroborates this. Tracked as **T7**.
8. Canonical HP synchronization: **PASS**.
9. Conjuros tablet portrait: **FAIL / OPEN**. On a newly created character configured as Mago/Mage level 10, expected spell-source context was not created/exposed; the spell source is absent, which prevents adding a spell through the tested flow. Tracked under **T5**.
10. Conjuros tablet landscape: **FAIL / OPEN for the same T5 functional reason**. Owner reports the visible controls/layout themselves look good in landscape; the blocking issue in this test is the absent spell-source context, not a newly observed landscape-control defect.

### Batch 3 — representative editor + settings + special presentation modes

11. Representative non-spell editor / IME: **PASS**, with the already-known app-wide checkbox inconsistency reproduced again and explicitly classified as **same phone finding 17.1**, not a new tablet defect.
12. PC / Character Settings responsiveness: **PASS**.
13. Application Settings responsiveness: **PASS**. Previously recorded T3/T4 product issues remain open; no additional tablet-specific defect was reported in this check.
14. P15 Supercompact portrait/landscape: **PASS**.
15. P14 Table Mode portrait/landscape: **FAIL / OPEN**. Overall table presentation looks correct, but interactive/edit controls remain visibly available and can be opened even though their attempted edits/actions do not actually take effect. This creates a misleading affordance that looks like a broken normal editing mode rather than an intentional table/read-only interaction state. Tracked as **T8**.

### Batch 4 — larger density + cold persistence + Conjuros sticky independence

16. Representative larger text/density behavior in portrait/landscape: **PASS**. No new clipping, overlap, unreachable-control or scrolling defect was reported at the tested larger density. T4 remains a control/semantics redesign issue, not a rendering failure from this check.
17. Cold persistence/reopen: **PASS**. Saved state/settings survived full close and relaunch and normal navigation remained available.
18. Independent Conjuros sticky-region behavior: **BLOCKED BY T5 / NOT A NEW FAILURE**. The owner could not perform an independent sticky/source-context check without first resolving the already-established missing spell-source/bootstrap defect. Do not count this as a separate fail or require synthetic test-data workarounds. After T5 repair, targeted Conjuros revalidation must include sticky/source-context behavior in portrait and landscape.

## P17 discovery conclusion

The physical Player-tablet discovery pass on exact `preqa.12 / 41200` is now **COMPLETE WITH OPEN FINDINGS**.

This means:

- actual tablet portrait and landscape evidence has been collected;
- baseline launch/navigation/rotation passed;
- Combat portrait and canonical HP passed;
- representative editor/IME behavior passed, while reproducing the known checkbox-family issue;
- PC Settings, Application Settings responsiveness and Supercompact passed;
- representative larger-density rendering passed;
- cold persistence/reopen passed;
- responsive Combat landscape, Conjuros source/bootstrap, and Table Mode interaction/affordance defects remain open;
- independent Conjuros sticky revalidation is deferred specifically because T5 prevents meaningful execution of that subcheck.

There is **no further broad tablet-discovery batch required on `preqa.12`**. Future device work should be targeted revalidation against the repaired monotonic candidate, preserving all unrelated PASS evidence above.

## Non-blocking findings captured during P17

### T1 — card reorder live-reflow/drag feedback is unstable across devices/orientations/column counts

Owner reports that moving cards behaves abnormally across device/form-factor perspectives, portrait/landscape, and with different column counts, including a single column.

The subsequently supplied physical video was directly inspected. Observed behavior is now more specific:

- while a card is still actively being dragged, the surrounding layout repeatedly reflows/snaps into candidate insertion positions;
- the dragged card/placeholder does not remain visually stable relative to the finger during those transitions;
- crossing insertion boundaries causes neighboring cards to jump/repack immediately;
- as a result, the apparent drop target itself moves while the user is trying to place the card, producing a "chasing the layout" interaction rather than controlled drag-and-drop feedback.

This evidence supports treating T1 as an open P6/reorder interaction/live-reflow defect rather than a tablet-specific breakpoint or column-count defect. The source-level root cause is **not yet asserted**; it still requires code audit before repair design.

### T2 — dice-mode visual/product contract + custom throw capability correction

Owner clarified desired behavior that had not been specified clearly enough earlier:

- when dice mode is used for dice throws, the visual die should resemble the actual die shape/border for the selected die;
- for `Otro` or total-only/result representations, use a circle;
- the same visual principle should apply to damage throws;
- move the Dice Mode setting/control to the relevant tab rather than leaving it only in the present settings location;
- Custom Throw must allow die choice and modifier entry comparable to an attack/damage throw, rather than lacking those choices.

Treat this as an owner-authorized product/UX correction to include in the consolidated repair batch.

### T3 — App Settings column control no longer represents current adaptive behavior clearly

Owner reports that the current column setting in Application Settings is not representative/intuitive relative to the app's evolved adaptive/column behavior. Audit both the setting control and the behavior it governs; likely redesign the control/semantics rather than applying a cosmetic label-only patch.

### T4 — density/scale control must center 100% and be symmetric

In Application Settings, 100% should be the visual/semantic center of the scale and decrement/increment options should be symmetric around it. The owner explicitly accepts adding 50% and 60% if needed to obtain symmetry. Audit current supported range/steps and redesign the control accordingly.

### T5 — spell-source/bootstrap behavior does not satisfy the owner-required source model

During Batch 2 the owner created a new character, selected Mago/Mage at level 10, and found that the expected spell/source context did not appear. In the tested state, the absent source prevents adding a spell. The same functional result was observed regardless of portrait/landscape; this is not classified as a tablet-only layout defect.

The owner recalls the intended contract as: **spell sources should behave analogously to Rasgos/feature sources for source availability/ownership rather than disappearing from the workflow when the relevant class exists.** Existing project context already establishes source-context ownership inside Conjuros, but the exact historical wording "spell sources act just as Rasgos sources" was not independently located in the material inspected during this QA recording pass. Therefore this checkpoint does not manufacture a historical citation; instead it records the owner's present clarification/reconfirmation as the controlling product requirement for the repair audit.

Repair audit must inspect at least:

- class/spellcasting bootstrap behavior for new and existing characters;
- creation/availability/persistence of spell sources;
- relationship between class sources and Conjuros source selection;
- source-dependent spellcasting ability / save DC / spell attack modifier ownership;
- add/edit-spell behavior when a source should exist;
- consistency across device size and orientation;
- post-repair sticky/source-context behavior in tablet portrait and landscape, because tablet check 18 was blocked by T5.

Classify T5 as a cross-device functional/contract defect requiring source-model audit.

### T6 — legacy class-editor controls for `Nivel`, `DG restante`, and `Dado`

Physical image evidence of `Editar clase` shows legacy-style fields for class level, remaining hit dice and hit die, and a full QWERTY keyboard is presented for input that is fundamentally numeric/die-oriented.

Owner requires these controls to adopt the newer shared control language already used in Combat where applicable:

- numeric values such as level and remaining hit dice should request a numeric keypad rather than the full text keyboard;
- hit-die selection should expose the standard SRD dice plus `Otro…` for custom values rather than rely on free-form legacy text entry;
- the appropriate SRD die should be preselected when the class/source data determines it;
- retain an editable custom path through `Otro…` rather than limiting the model to standard dice;
- reuse the compact/shared control language instead of maintaining a visually and behaviorally separate legacy editor family.

T6 applies across devices/orientations and is a control-consistency/editor-input defect/UX correction, not a tablet-only issue.

### T7 — Combat landscape over-stretches content instead of adapting to tablet width

Physical tablet-landscape image evidence shows the Combat HUD and attack/action cards stretching essentially across the full content width. Short content remains concentrated toward the left while edit/delete actions are pushed far to the right, leaving large horizontal dead zones inside each full-width card.

This is an open P16/responsive-layout defect: wide-screen space is technically occupied but not used meaningfully. The repair audit should evaluate deliberate max-widths, adaptive grouping/columns, card spans and/or other responsive composition rather than simply stretching the phone composition to the available width. The exact implementation should be decided from the source/layout audit rather than inferred from the screenshot alone.

T7 may share design principles with the existing phone responsive-layout family (especially 17.2/17.3), but it remains separately evidenced until code audit establishes a common cause.

### T8 — Table Mode exposes editable affordances whose actions do not take effect

In P14 Table Mode, the owner reports that the table itself looks correct, but controls still appear interactive: they can be opened and edits can apparently be initiated. The resulting actions/edits do not actually take effect.

This is confusing because the UI advertises an editable interaction model while functionally behaving as though editing is unavailable. The repair audit must first establish the intended P14 contract, then make the affordance and behavior agree:

- if Table Mode is intentionally read-only/restricted, controls that cannot act should not present as normal enabled editing controls; or
- if those controls are intended to remain available, their actions must work and persist correctly in Table Mode.

Do not choose between those product behaviors by assumption during QA recording; inspect the accepted P14 design/source contract before repair. Classify T8 as an interaction/affordance-state defect.

## Relationship to open phone findings

All earlier phone findings remain open where applicable, especially structured-damage checks 7–8 and the app-wide checkbox/responsive-layout family 17.1–17.3. Tablet T7 and phone 17.2/17.3 may ultimately share responsive-layout rules, but they are not collapsed into one root cause before source audit. T5 and T6 are cross-device product/control findings discovered during tablet QA rather than tablet-specific failures. Batch 3 reproduced phone 17.1 on tablet, strengthening the evidence that the checkbox family is truly cross-device/systemic.

## Exact next action

Move from physical discovery to **cross-device audit + consolidated repair design/implementation** on `implementation/phase4a-successor-cycle`.

The repair audit must cover, at minimum:

- phone structured-damage failures 7–8 and requested direct sign-toggle refinement from phone 9;
- phone compact-field density refinement from 16 only if safe without shrinking interaction targets;
- app-wide checkbox/responsive family 17.1–17.3;
- T1 reorder live-reflow behavior;
- T2 dice-mode visual/product contract and Custom Throw parity;
- T3 columns-setting semantics/control redesign;
- T4 symmetric density-scale control;
- T5 spell-source/bootstrap/source-context contract;
- T6 legacy class-editor numeric/die controls;
- T7 tablet-landscape Combat adaptive composition;
- T8 Table Mode affordance/behavior contract.

Audit actual source before asserting shared root causes. Consolidate compatible fixes into the next monotonic QA candidate. After material code changes, run focused automated tests plus the aggregate gate, freeze exact artifact evidence, then perform **targeted** phone/tablet revalidation only for failed/touched/affected boundaries plus unresolved phone checks 21–22 and Conjuros sticky behavior blocked here by T5.

Do not rerun unrelated accepted phone/tablet checks wholesale. Phase 4A remains OPEN; DM implementation remains blocked pending explicit owner closure after successful repair/revalidation.
