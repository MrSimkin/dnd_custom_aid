# Testing and Verification

**Last synchronized:** 2026-09-14  
**Branch:** `implementation/phase4a-successor-cycle`

## Current status

Phases 0–3 are complete. Phase 4A broad physical discovery was completed on `preqa.12`; the consolidated post-P17 material repair cycle is now **COMPLETE / AUTOMATION GREEN** and the repaired candidate is frozen.

Current position:

- frozen physical-QA candidate: `0.4.0-preqa.13 / 41300`;
- candidate commit: `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- candidate Scaffold: `34801612526` / #1630 — **SUCCESS**;
- candidate artifact: `10331503478` / `dnd-custom-aid-debug-apk`;
- archive SHA-256: `13b72f8a7e797e40009f548bd45d1c538a41cfe52ecf9796110e75498e359893`;
- APK SHA-256: `81fbc20525221d791e6a0f786dec4b05709505e052e0e5ffba4ebfa5eb02391e`;
- Round 1 structured dice: **COMPLETE / AUTOMATION GREEN**;
- Round 2 T1 reorder stability: **COMPLETE / AUTOMATION GREEN**;
- Round 3 compact checkbox + responsive grouping: **COMPLETE / AUTOMATION GREEN**;
- T5 spell-source/bootstrap/source-context: **COMPLETE / AUTOMATION GREEN**;
- T6 class-editor numeric/hit-die controls: **COMPLETE / AUTOMATION GREEN**;
- Application Settings T3/T4/T9: **COMPLETE / AUTOMATION GREEN**;
- T7 wide Combat adaptive composition: **COMPLETE / AUTOMATION GREEN**;
- T8 Table Mode structural-affordance enforcement: **COMPLETE / AUTOMATION GREEN**;
- T2 dice-result / Custom Throw / Dice display-mode integration: **COMPLETE / AUTOMATION GREEN**;
- aggregate migration-free repaired-line Scaffold: `34801201294` / #1625 — **SUCCESS**;
- optional phone-16 compact-density refinement: **NOT TAKEN**; check 16 was already PASS and no material benefit was demonstrated;
- current gate: **TARGETED CROSS-DEVICE PHYSICAL REVALIDATION ON PREQA.13**;
- Phase 4A owner closure: **NOT COMPLETE**;
- DM implementation: **BLOCKED UNTIL EXPLICIT PHASE 4A OWNER CLOSURE**.

Live status authorities, in order: `docs/PROJECT_STATE.md`, `docs/checkpoints/LATEST.md`, `docs/checkpoints/2026-09-14_PHASE4A_PREQA13_CONSOLIDATED_REPAIR_CANDIDATE.md`, then this file for testing policy/route.

Green CI is technical evidence, not physical evidence or owner acceptance.

## 1. Core testing rules

Never claim a test passed unless it was actually executed successfully against the relevant revision. Every meaningful implementation/QA batch records what was tested, result, material omissions and device/environment where relevant.

Automated verification and physical acceptance are separate gates. A defect seen on one device can justify a shared repair when source audit proves a shared cause, but inference is not physical evidence on another device.

Preserve accepted physical evidence. Do not replay complete phone/tablet suites merely because later bounded repairs or candidate identities advance.

All physical revalidation in the current gate must use the exact frozen `preqa.13 / 41300` candidate. Documentation-only commits after the candidate commit do not change the APK under test.

## 2. Standard automated verification

Kotlin / Android / Desktop / SQLDelight:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

Backend:

```bash
cd backend
npm install --no-package-lock
npm run check
```

Current CI uses JDK 17, Gradle 9.5 and Android SDK platform 36. Normal Scaffold covers backend type-check, stable debug-keystore preparation, all permanent Player source guards, shared tests, Android/Desktop builds and Android debug APK upload.

Every bounded repair requires focused guard/tests plus normal read-only Scaffold before being marked automation green.

## 3. Frozen physical candidate — preqa.13

Exact immutable candidate for the current physical gate:

- versionName `0.4.0-preqa.13`;
- versionCode `41300`;
- commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — **SUCCESS**;
- artifact `10331503478`;
- artifact name `dnd-custom-aid-debug-apk`;
- archive size `13,693,984` bytes;
- GitHub Actions artifact digest `sha256:13b72f8a7e797e40009f548bd45d1c538a41cfe52ecf9796110e75498e359893`;
- independently downloaded ZIP SHA-256 `13b72f8a7e797e40009f548bd45d1c538a41cfe52ecf9796110e75498e359893` — exact match;
- APK filename `androidApp-debug.apk`;
- APK size `39,094,384` bytes;
- independent APK SHA-256 `81fbc20525221d791e6a0f786dec4b05709505e052e0e5ffba4ebfa5eb02391e`.

The exact candidate run passed backend, every permanent Player repair guard, shared tests, Android build, Desktop build and APK upload. Normal Scaffold remains read-only.

`preqa.13` is **AUTOMATION GREEN / PHYSICAL REVALIDATION PENDING**. It is not physically PASS.

## 4. Preserved preqa.12 physical discovery

`preqa.12 / 41200` remains the historical discovery baseline. Its accepted PASS evidence is preserved and does not need replay unless directly affected by a repaired family.

### Phone

- 1–6 PASS;
- 7–8 structured-damage findings → repaired by Round 1/T2; targeted revalidation required;
- 9 functional PASS plus direct-sign refinement → repaired/extended by Round 1; targeted revalidation required;
- 10–16 PASS; check 16 had only an optional density refinement, deliberately not taken;
- 17.1–17.3 checkbox/responsive grouping → repaired by Round 3; targeted revalidation required;
- 18–20 PASS;
- 21 UNASSESSED;
- 22 PARTIAL/AMBIGUOUS;
- 23 PASS.

### Tablet P17

- 1–6 PASS;
- 7 FAIL / T7 → repaired; targeted revalidation required;
- 8 PASS;
- 9–10 FAIL / T5 → repaired; targeted revalidation required;
- 11 PASS plus checkbox-family reproduction → Round 3 targeted revalidation required;
- 12–14 PASS;
- 15 FAIL / T8 → repaired; targeted revalidation required;
- 16–17 PASS;
- 18 was BLOCKED BY T5; T5 is repaired, so tablet 18 must now be executed.

No broad discovery replay is required.

## 5. Repair automation evidence summary

### Round 1 — structured dice / signed modifier

HEAD `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0`; Scaffold `34787688776` / #1508 — **SUCCESS**. Shared `NdS±M` parsing/rolling, independent quantity/sides/modifier state, explicit signed serialization and direct sign handling are automation green.

### Round 2 — T1 reorder stability

HEAD `5b06056e9e8ed5cf05a767dd1da3d6f4f48363eb`; Scaffold `34788409987` / #1519 — **SUCCESS**. Stable drag-start geometry, canonical preview, hysteresis, scroll translation and lazy-target capture cover one-dimensional and spatial reorder.

### Round 3 — compact checkbox + responsive grouping

HEAD `1d1c476ddaeb045c8a1b186267f452010cad7681`; Scaffold `34793253151` / #1537 — **SUCCESS**. Nineteen raw Material Checkbox sites across seven Player files migrated to shared compact/touch-safe controls; Equipment/Conjuros packing and source/prepared pairing are guarded.

### T5 — spell-source bootstrap / source context

Core `2e7fda2852425594971f7df47b433d642eb2119a`; steady-state `3774c53f5189ebd535cc1b73ec18493e268e5d9f`; Scaffold `34794589758` / #1560 — **SUCCESS**. Canonical caster sources reconcile while preserving compatible IDs/associations/configured profiles and manual/homebrew sources.

### T6 — class-editor numeric / hit-die controls

Product `c98121e50f347f64898c9e31d077e53cad31685f`; steady-state `6ba73b22b07a4c5a92d69425a7372d2695fbc30a`; Scaffold `34795355116` / #1571 — **SUCCESS**. Numeric keyboards and standard/custom hit-die selection are implemented and guarded.

### T3/T4/T9 — Application Settings semantics

Product `1dcd320417e7e3b45ec02ec6aa7cbb616c84e473`; steady-state `5db7bc3a48f1e640fc80770dd07d68a7ffaa02f7`; Scaffold `34796452617` / #1583 — **SUCCESS**. Adaptive Vertical/Horizontal density, symmetric 50–150 text scale, compatibility migration and haptics `Ninguna` are implemented and guarded.

### T7 — wide Combat adaptive composition

Product `5f00bc006a26600a5d03582fbfbdf2e266259673`; steady-state `e567750a529238a2b45722b6f5ed726dd9123d88`; Scaffold `34797403737` / #1592 — **SUCCESS**. Narrow one-column Combat is preserved; wide/tablet Combat uses a bounded HUD, adaptive grid and stabilized spatial reorder with canonical persistence.

### T8 — Table Mode structural-affordance enforcement

Product `2734d08a72e183ca213cd9213ff47a4db588cbf6`; steady-state `d6c13819a49e1cb7c71dffcad98b53c250d4d9d4`; Scaffold `34799667822` / #1611 — **SUCCESS**. Structural controls are visibly gated while genuine operational HP/inspiration/current-resource state remains live.

### T2 — dice-result / Custom Throw / Dice display-mode integration

Product `8131dd13f4373148503b854b1448f9d081556847`; final migration-free steady-state `4d09e9eca648e5ca82af896dab8d16b986faae5b`; authoritative Scaffold `34801201294` / #1625 — **SUCCESS**.

Implemented and guarded:

- standard die-specific result silhouettes for d4/d6/d8/d10/d12/d20;
- neutral fallback for custom/unsupported die identity and flat numeric results;
- Custom Throw standard die selection;
- `Otro…` custom sides bounded to `2..1000`;
- direct signed modifier UX;
- shared arbitrary-die resolver reused by damage rolling;
- Dice-tab ownership of the existing persisted `dice_result_mode` preference;
- removal of the active result-mode selector from Application Settings;
- focused shared-domain test for arbitrary sides and signed modifiers.

## 6. Consolidated aggregate proof

All material post-P17 repair families are implemented and automation green. Scaffold `34801201294` / #1625 is the authoritative migration-free aggregate proof. It passed every permanent Player guard, backend, shared tests, Android/Desktop builds and APK upload.

The optional phone-16 compact-density refinement remains **NOT TAKEN**. It was never a blocking defect and no later evidence demonstrated a material benefit sufficient to justify additional churn.

The exact versioned `preqa.13` candidate was then validated separately by Scaffold `34801612526` / #1630 — **SUCCESS**.

## 7. Current targeted physical-revalidation matrix

Do **not** replay the full historical phone/tablet suites. Revalidate only failed/touched/affected families plus unresolved evidence.

### A. Round 1 + T2 — dice family

On phone, cover the prior 7–9 findings and the T2 additions:

- structured damage die + positive modifier;
- structured damage `Otro…` + modifier without field misrouting;
- direct `+` / `−` sign behavior;
- ordinary visible d20 sanity;
- representative non-d20 Custom Throw;
- `Otro…` custom sides: valid lower/upper representative values plus rejection/disable behavior for invalid bounds;
- positive and negative Custom Throw modifiers;
- die-identity visuals for standard dice;
- representative damage result visual;
- flat numeric fallback visualization;
- Dice-tab result/display-mode selector;
- display-mode persistence after leave/reopen.

Include one tablet/wide Dice sanity check because the same active surface is shared.

### B. T1 — reorder stability

- one-column reorder;
- spatial/multi-column reorder on wide/tablet;
- no target chasing caused by preview reflow;
- auto-scroll while dragging where applicable;
- final order persists after leave/reopen.

Reuse the existing owner failure video as historical baseline. Do not request reproduction of the old failure before testing the repair.

### C. Round 3 — checkbox + responsive grouping

- representative Equipment migrated checkbox (`Equipado` / `Equipo especial` family);
- one additional migrated checkbox surface;
- Conjuros V/S/M grouping;
- Concentración/Ritual grouping;
- source/prepared grouping;
- portrait wrapping only when required;
- landscape/tablet use of available width rather than preserving unnecessary rigid rows.

### D. T5 — spell-source bootstrap / source context

- canonical caster source appears for a representative caster such as Mago;
- bounded default casting ability is correct unless existing configuration overrides it;
- add/associate/save;
- leave/reopen preserves source identity and associations;
- manual/homebrew source coexists;
- tablet portrait/landscape source context;
- execute former tablet P17 check 18, including sticky/source-context behavior.

### E. T6 — class-editor controls

- numeric keyboard behavior for Nivel and DG remaining;
- catalog hit-die preselection;
- representative standard die selection;
- `Otro…` custom sides;
- save/leave/reopen persistence.

### F. T3/T4/T9 — Application Settings

- Vertical density preference meaning/persistence;
- Horizontal density preference meaning/persistence;
- safe effective-column reduction under representative text pressure;
- symmetric text-size choices around 100 and representative migration behavior;
- haptics `Ninguna` produces no haptic feedback;
- representative non-none haptic choice still produces feedback;
- where an existing installation with non-none haptics is available, upgrade preserves that prior choice.

### G. T7 — wide Combat

Tablet landscape:

- meaningful width use without stretched full-width cards;
- bounded operational HUD;
- adaptive card packing under representative density/text settings;
- multi-column reorder stability;
- applicable auto-scroll;
- final persisted order after leave/reopen;
- favorite/Edit/Delete behave normally outside drag;
- no accidental child action fires during drag.

Also perform one narrow-phone Combat sanity check to confirm the preserved one-column path remains coherent.

### H. T8 — Table Mode

Tablet:

- structural name/class/ability/general-reference controls are genuinely read-only/non-opening;
- class Add/Edit/Delete cannot be opened;
- portrait/token, defense, sense and movement structural actions are absent/disabled as intended;
- Skills keeps its presentation selector usable while structural score/save/training/adjustment controls are gated;
- live HP remains usable and persists;
- inspiration remains usable and persists;
- current resource values remain usable and persist;
- leave/reopen confirms structural data unchanged and operational data retained.

Also perform one narrow-phone Table Mode sanity check.

### I. Previously unresolved evidence

- phone check 21 — execute and record an unambiguous result;
- affected slice of phone check 22 — execute only the portion touched/relevant to repaired behavior and resolve the prior ambiguity;
- tablet check 18 — execute now that T5 no longer blocks it.

Preserve every unrelated accepted PASS from `preqa.12`.

## 8. Evidence recording

For each targeted check, record:

- exact device/form factor and orientation;
- exact candidate identity `0.4.0-preqa.13 / 41300`;
- PASS / FAIL / BLOCKED;
- brief observable behavior;
- screenshot/video only where it materially helps diagnose a failure or demonstrates a layout/reorder result;
- whether failure appears local or potentially shared/systemic, without asserting shared cause until source audit.

Do not combine automation-green status with physical PASS. They are separate evidence classes.

## 9. Failure handling

If targeted physical QA finds a material defect:

1. classify it;
2. audit source before asserting shared cause;
3. reopen only the relevant bounded repair family;
4. preserve storage/import/export, IDs/associations and accepted evidence unless the approved repair requires otherwise;
5. rerun focused + aggregate automation;
6. advance to a new monotonic candidate identity if the frozen candidate changes materially;
7. checkpoint exact evidence before another owner pass.

Do not invent a new numbered repair merely because the project is waiting at a manual gate.

## 10. Status synchronization rule

At the end of a targeted physical batch or any reopened bounded repair, synchronize:

1. dedicated checkpoint under `docs/checkpoints/`;
2. `docs/PROJECT_STATE.md`;
3. `docs/checkpoints/LATEST.md`;
4. this `docs/TESTING.md`.

They must agree on exact candidate, completed evidence, remaining blockers, next action and physical status.

## 11. Phase 4A closure

Phase 4A closes only after the exact repaired candidate has sufficient targeted phone/tablet evidence for repaired/touched families, the previously unresolved evidence is addressed as required, and the owner explicitly accepts/closes Phase 4A.

CI cannot substitute for physical evidence or owner closure. No P18 exists. DM implementation remains blocked until explicit Phase 4A owner closure.