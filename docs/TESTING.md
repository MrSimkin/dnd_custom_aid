# Testing and Verification

**Last synchronized:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`

## Current status

Phases 0–3 are complete. Phase 4A Player physical discovery on the current frozen candidate is complete, and the project is inside the **consolidated post-P17 repair cycle**.

Current position:

- frozen physical-QA candidate: `0.4.0-preqa.12 / 41200`;
- candidate commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- candidate Scaffold `34776627282`: **SUCCESS**;
- artifact `10323602038` / `dnd-custom-aid-debug-apk`;
- phone + tablet discovery: **COMPLETE WITH OPEN FINDINGS**;
- Round 1 structured dice: **COMPLETE / AUTOMATION GREEN**;
- Round 2 T1 reorder stability: **COMPLETE / AUTOMATION GREEN**;
- Round 3 compact checkbox + responsive grouping: **COMPLETE / AUTOMATION GREEN**;
- T5 spell-source/bootstrap/source-context: **COMPLETE / AUTOMATION GREEN**;
- T6 class-editor numeric/hit-die controls: **COMPLETE / AUTOMATION GREEN**;
- Application Settings T3/T4/T9: **COMPLETE / AUTOMATION GREEN**;
- next implementation family: **T7 wide Combat adaptive composition**;
- no new repaired physical-QA candidate has been frozen yet;
- Phase 4A owner closure: **NOT COMPLETE**;
- DM implementation: **BLOCKED UNTIL EXPLICIT PHASE 4A OWNER CLOSURE**.

Live status authorities, in order: `docs/PROJECT_STATE.md`, `docs/checkpoints/LATEST.md`, latest completed repair checkpoint, then this file for testing policy/route.

Green CI is technical evidence, not physical evidence or owner acceptance.

## 1. Core testing rules

Never claim a test passed unless it was actually executed successfully against the relevant revision. Every meaningful implementation/QA batch records what was tested, result, material omissions and device/environment where relevant.

Automated verification and physical acceptance are separate gates. A defect seen on one device can justify a shared repair when source audit proves a shared cause, but inference is not physical evidence on another device.

Preserve accepted physical evidence. Do not replay complete phone/tablet suites merely because a later bounded family changes.

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

Every bounded repair needs focused guard/tests plus the normal read-only Scaffold before being marked automation green.

## 3. Frozen physical baseline

Exact immutable discovery baseline:

- versionName `0.4.0-preqa.12`;
- versionCode `41200`;
- commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- Scaffold `34776627282` — **SUCCESS**;
- artifact `10323602038`;
- ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

Later repair commits are not yet a frozen physical candidate.

## 4. Preserved physical discovery

### Phone

- 1–6 PASS;
- 7–8 structured-damage findings → Round 1 implemented/green, targeted revalidation pending;
- 9 functional PASS plus direct-sign refinement → Round 1 implemented/green, targeted revalidation pending;
- 10–16 PASS; 16 only optional compact-density refinement;
- 17.1–17.3 checkbox/responsive grouping → Round 3 implemented/green, targeted revalidation pending;
- 18–20 PASS;
- 21 UNASSESSED;
- 22 PARTIAL/AMBIGUOUS;
- 23 PASS.

### Tablet P17

- 1–6 PASS;
- 7 FAIL / T7;
- 8 PASS;
- 9–10 FAIL / T5 → T5 implemented/green, targeted revalidation pending;
- 11 PASS plus checkbox-family reproduction → Round 3 implemented/green, targeted revalidation pending;
- 12–14 PASS;
- 15 FAIL / T8;
- 16–17 PASS;
- 18 was BLOCKED BY T5; T5 is now repaired, so this is future targeted physical revalidation.

No further broad discovery pass is required on preqa.12.

## 5. Repair automation evidence

### Round 1 — structured dice / signed modifier

HEAD `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0`; Scaffold `34787688776` / #1508 — **SUCCESS**. Physical revalidation waits for the consolidated candidate. Remaining T2 integration is tracked separately.

### Round 2 — T1 reorder stability

HEAD `5b06056e9e8ed5cf05a767dd1da3d6f4f48363eb`; Scaffold `34788409987` / #1519 — **SUCCESS**. Both one-dimensional and spatial reorder are covered by stable drag geometry/hysteresis/scroll translation. T1 = **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**.

Future T1 physical coverage: one-column, multi-column/spatial, no preview-target chasing, applicable auto-scroll, final persisted order after leave/reopen. Reuse the existing owner failure video; do not request it again.

### Round 3 — compact checkbox + responsive grouping

HEAD `1d1c476ddaeb045c8a1b186267f452010cad7681`; Scaffold `34793253151` / #1537 — **SUCCESS**. Nineteen raw Material Checkbox sites across seven Player files migrated to shared compact/touch-safe controls; responsive Equipment/Conjuros packing and source/prepared pairing are guarded. Eleven legitimate Material `Switch` sites remain; zero `TriStateCheckbox` sites.

Future physical coverage: representative Equipment + another migrated checkbox surface; Conjuros V/S/M + Concentración/Ritual; source/prepared packing; phone/tablet portrait/landscape where applicable.

### T5 — spell-source bootstrap / source context

Core integration `2e7fda2852425594971f7df47b433d642eb2119a`; steady-state HEAD `3774c53f5189ebd535cc1b73ec18493e268e5d9f`; Scaffold `34794589758` / #1560 — **SUCCESS**.

Canonical base caster sources are reconciled by exact linked class identity; existing IDs/associations/configured profiles and manual/homebrew sources are preserved. Missing canonical profiles receive only bounded default casting ability. T5 deliberately does not implement spell legality/subclass/multiclass rules.

Future physical coverage: canonical Mago/Mage source availability, default INT unless existing configuration overrides it, add/associate/save/leave/reopen, stable source identity, manual source coexistence, tablet portrait/landscape and former P17 check 18 sticky/source-context behavior.

### T6 — class-editor numeric / hit-die controls

Product commit `c98121e50f347f64898c9e31d077e53cad31685f`; steady-state HEAD `6ba73b22b07a4c5a92d69425a7372d2695fbc30a`; authoritative Scaffold `34795355116` / #1571 — **SUCCESS**.

T6 implements numeric keyboard behavior for `Nivel`, `DG restantes` and custom hit-die sides; the standard `d4/d6/d8/d10/d12/d20 + Otro…` selector; catalog preselection and existing custom values; and permanent `check_player_class_editor_controls.py`. No schema/storage/class-rules redesign was introduced.

Artifact `10329671667`, size `13,645,969` bytes, digest `sha256:6391210567ff4964b7077e1cc6e9c3c38bd862b62aba19a01e929ef6bfe4db4d`.

T6 = **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**.

Future physical coverage: numeric keyboard behavior for Nivel/DG, catalog die preselection, representative standard die selection, `Otro…` custom sides, and save/leave/reopen persistence.

### Application Settings — T3 / T4 / T9

Product commit `1dcd320417e7e3b45ec02ec6aa7cbb616c84e473`; steady-state HEAD `5db7bc3a48f1e640fc80770dd07d68a7ffaa02f7`; authoritative Scaffold `34796452617` / #1583 — **SUCCESS**.

This family implements:

- T3 adaptive `Vertical` / `Horizontal` card-density preferences with `Cómodo / Equilibrado / Compacto / Denso`, runtime width/text/spacing safety and compatibility migration from the four legacy exact-count keys;
- T4 symmetric normal text-size scale `50..150` by 10 around 100, with nearest-value migration;
- T9 explicit haptics `Ninguna`, enforced at the shared dispatch point before both vibrator and fallback platform haptic paths;
- permanent `check_player_application_settings_semantics.py` guard;
- removal of the temporary migration and diagnostic helpers before the authoritative run.

Artifact `10329772684`, size `13,644,492` bytes, digest `sha256:74249a197c21572743165927c9330f38ceafd2db7f78267872f36e2edfd2f1af`.

T3/T4/T9 = **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**.

Future physical coverage: persistence/understandability of Vertical/Horizontal density choices; safe column reduction under text pressure; symmetric text-size behavior and representative migrated value; `Ninguna` versus non-none haptic behavior; and preservation of an existing non-none installation after upgrade.

## 6. Current implementation route — T7 wide Combat

The active audited path is `CharacterCombatSuccessorV4.kt`. The current one-dimensional `LazyColumn` composition stretches each attack/action card to full width in tablet landscape.

T7 contract:

- preserve the existing narrow-phone composition and operational semantics;
- keep the operational Combat HUD deliberately bounded/compact on wide layouts;
- use an adaptive multi-column/card composition for wide/tablet layouts, driven by the repaired T3 responsive preference/runtime calculation rather than a hard-coded promised count;
- reuse the stabilized Round 2 spatial reorder foundation for multi-column reorder;
- preserve structural edit, favorite, delete, persisted ordering and operational controls;
- add focused T7 guard/tests and pass the normal read-only Scaffold before marking T7 automation green.

T7 is not physically PASS until the future consolidated candidate receives targeted tablet-landscape revalidation.

## 7. Remaining repair order

After T7:

1. T8 Table Mode structural-affordance enforcement;
2. remaining T2 die-result silhouettes + Custom Throw die/custom sides/signed modifier + Dice-tab display-mode ownership;
3. optional phone-16 compact-density refinement only if safe and materially beneficial.

After material consolidated repair completion:

1. run aggregate Scaffold;
2. freeze a new monotonic physical-QA candidate;
3. perform targeted phone/tablet revalidation only for failed/touched/affected families plus phone 21/affected phone 22 and tablet 18;
4. preserve unrelated accepted PASS evidence;
5. require explicit owner acceptance before Phase 4A closure.

## 8. Failure handling

If physical QA finds a defect: classify it; audit source before asserting shared cause; reopen only the relevant repair boundary; preserve storage/import/export, IDs/associations and accepted evidence unless the approved repair requires change; rerun focused + aggregate automation; create a new monotonic candidate identity if the frozen candidate materially changes; checkpoint exact evidence before another owner pass.

Do not invent a new numbered repair merely because the project is waiting at a manual gate.

## 9. Status synchronization rule

At the end of every bounded implementation/test family, update all four surfaces before proceeding:

1. dedicated checkpoint under `docs/checkpoints/`;
2. `docs/PROJECT_STATE.md`;
3. `docs/checkpoints/LATEST.md`;
4. this `docs/TESTING.md`.

They must agree on completed family, automation evidence, next action and physical status.

## 10. Phase 4A closure

Phase 4A closes only after the consolidated repaired candidate has sufficient targeted phone/tablet evidence for relevant repaired/touched families, blocking/previously unresolved evidence is addressed as required, and the owner explicitly accepts/closes Phase 4A. CI cannot substitute for owner closure.

No P18 exists. DM implementation remains blocked until explicit Phase 4A owner closure.
