# Testing and Verification

**Last synchronized:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`

## Current status

Phases 0–3 are complete. Phase 4A Player physical discovery on the frozen preqa.12 candidate is complete, and the consolidated post-P17 **material repair cycle is now COMPLETE / AUTOMATION GREEN**.

Current position:

- frozen physical-QA baseline: `0.4.0-preqa.12 / 41200`;
- baseline commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- baseline Scaffold `34776627282`: **SUCCESS**;
- baseline artifact `10323602038` / `dnd-custom-aid-debug-apk`;
- phone + tablet discovery: **COMPLETE WITH OPEN FINDINGS**;
- Round 1 structured dice: **COMPLETE / AUTOMATION GREEN**;
- Round 2 T1 reorder stability: **COMPLETE / AUTOMATION GREEN**;
- Round 3 compact checkbox + responsive grouping: **COMPLETE / AUTOMATION GREEN**;
- T5 spell-source/bootstrap/source-context: **COMPLETE / AUTOMATION GREEN**;
- T6 class-editor numeric/hit-die controls: **COMPLETE / AUTOMATION GREEN**;
- Application Settings T3/T4/T9: **COMPLETE / AUTOMATION GREEN**;
- T7 wide Combat adaptive composition: **COMPLETE / AUTOMATION GREEN**;
- T8 Table Mode structural-affordance enforcement: **COMPLETE / AUTOMATION GREEN**;
- T2 dice-result / Custom Throw / Dice display-mode integration: **COMPLETE / AUTOMATION GREEN**;
- aggregate steady-state Scaffold: `34801201294` / #1625 — **SUCCESS**;
- optional phone-16 compact-density refinement: **NOT TAKEN**; check 16 was already PASS and no material benefit was demonstrated;
- next gate: **freeze one new monotonic physical-QA candidate**;
- no repaired physical-QA candidate has been frozen yet;
- Phase 4A owner closure: **NOT COMPLETE**;
- DM implementation: **BLOCKED UNTIL EXPLICIT PHASE 4A OWNER CLOSURE**.

Live status authorities, in order: `docs/PROJECT_STATE.md`, `docs/checkpoints/LATEST.md`, latest completed repair checkpoint, then this file for testing policy/route.

Green CI is technical evidence, not physical evidence or owner acceptance.

## 1. Core testing rules

Never claim a test passed unless it was actually executed successfully against the relevant revision. Every meaningful implementation/QA batch records what was tested, result, material omissions and device/environment where relevant.

Automated verification and physical acceptance are separate gates. A defect seen on one device can justify a shared repair when source audit proves a shared cause, but inference is not physical evidence on another device.

Preserve accepted physical evidence. Do not replay complete phone/tablet suites merely because later bounded repairs advance.

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
- 7–8 structured-damage findings → Round 1 implemented/green; targeted revalidation pending;
- 9 functional PASS plus direct-sign refinement → Round 1 implemented/green; targeted revalidation pending;
- 10–16 PASS; check 16 had only an optional density refinement, now deliberately not taken before the next candidate;
- 17.1–17.3 checkbox/responsive grouping → Round 3 implemented/green; targeted revalidation pending;
- 18–20 PASS;
- 21 UNASSESSED;
- 22 PARTIAL/AMBIGUOUS;
- 23 PASS.

### Tablet P17

- 1–6 PASS;
- 7 FAIL / T7 → implemented/green; targeted revalidation pending;
- 8 PASS;
- 9–10 FAIL / T5 → implemented/green; targeted revalidation pending;
- 11 PASS plus checkbox-family reproduction → Round 3 implemented/green; targeted revalidation pending;
- 12–14 PASS;
- 15 FAIL / T8 → implemented/green; targeted revalidation pending;
- 16–17 PASS;
- 18 was BLOCKED BY T5; T5 is repaired, so check 18 belongs to future targeted physical revalidation.

No further broad discovery pass is required on preqa.12.

## 5. Repair automation evidence

### Round 1 — structured dice / signed modifier

HEAD `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0`; Scaffold `34787688776` / #1508 — **SUCCESS**. Shared structured `NdS±M` parsing/rolling, independent quantity/sides/modifier state, explicit signed serialization and direct sign handling are automation green. Physical revalidation waits for the consolidated candidate.

### Round 2 — T1 reorder stability

HEAD `5b06056e9e8ed5cf05a767dd1da3d6f4f48363eb`; Scaffold `34788409987` / #1519 — **SUCCESS**. One-dimensional and spatial reorder use stable drag-start geometry, canonical preview, hysteresis, scroll translation and lazy-target capture.

T1 = **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**.

Future T1 physical coverage: one-column, multi-column/spatial, no preview-target chasing, applicable auto-scroll, final persisted order after leave/reopen. Reuse the existing owner failure video; do not request it again.

### Round 3 — compact checkbox + responsive grouping

HEAD `1d1c476ddaeb045c8a1b186267f452010cad7681`; Scaffold `34793253151` / #1537 — **SUCCESS**. Nineteen raw Material Checkbox sites across seven Player files migrated to shared compact/touch-safe controls; responsive Equipment/Conjuros packing and source/prepared pairing are guarded. Eleven legitimate Material `Switch` sites remain; zero `TriStateCheckbox` sites.

Future physical coverage: representative Equipment + another migrated checkbox surface; Conjuros V/S/M + Concentración/Ritual; source/prepared packing; phone/tablet portrait/landscape where applicable.

### T5 — spell-source bootstrap / source context

Core integration `2e7fda2852425594971f7df47b433d642eb2119a`; steady-state HEAD `3774c53f5189ebd535cc1b73ec18493e268e5d9f`; Scaffold `34794589758` / #1560 — **SUCCESS**.

Canonical base caster sources reconcile by exact linked class identity while preserving existing IDs/associations/configured profiles and manual/homebrew sources. Missing canonical profiles receive only bounded default casting ability. No spell-legality/subclass/multiclass rules were added.

Future physical coverage: Mago/Mage source availability, default INT unless existing configuration overrides it, add/associate/save/leave/reopen, stable source identity, manual-source coexistence, tablet portrait/landscape and former P17 check 18 sticky/source-context behavior.

### T6 — class-editor numeric / hit-die controls

Product commit `c98121e50f347f64898c9e31d077e53cad31685f`; steady-state HEAD `6ba73b22b07a4c5a92d69425a7372d2695fbc30a`; Scaffold `34795355116` / #1571 — **SUCCESS**.

Numeric keypad behavior for `Nivel`, `DG restantes` and custom hit-die sides plus standard `d4/d6/d8/d10/d12/d20 + Otro…` selector are implemented with catalog/custom preservation. Permanent `check_player_class_editor_controls.py` remains in Scaffold.

Artifact `10329671667`, size `13,645,969` bytes, digest `sha256:6391210567ff4964b7077e1cc6e9c3c38bd862b62aba19a01e929ef6bfe4db4d`.

Future physical coverage: numeric keyboard behavior, catalog preselection, representative standard die selection, `Otro…` custom sides and save/leave/reopen persistence.

### Application Settings — T3 / T4 / T9

Product `1dcd320417e7e3b45ec02ec6aa7cbb616c84e473`; steady-state HEAD `5db7bc3a48f1e640fc80770dd07d68a7ffaa02f7`; Scaffold `34796452617` / #1583 — **SUCCESS**.

Adaptive Vertical/Horizontal card-density preferences, symmetric `50..150` text scale, compatibility migration and explicit haptics `Ninguna` are implemented and guarded by `check_player_application_settings_semantics.py`.

Artifact `10329772684`, size `13,644,492` bytes, digest `sha256:74249a197c21572743165927c9330f38ceafd2db7f78267872f36e2edfd2f1af`.

Future physical coverage: persistence/understandability of Vertical/Horizontal density, safe column reduction under text pressure, symmetric text-size behavior and representative migration, `Ninguna` versus non-none haptics, and preservation of an existing non-none installation after upgrade.

### T7 — wide Combat adaptive composition

Product `5f00bc006a26600a5d03582fbfbdf2e266259673`; steady-state HEAD `e567750a529238a2b45722b6f5ed726dd9123d88`; Scaffold `34797403737` / #1592 — **SUCCESS**.

Narrow-phone one-column Combat remains intact. Wide/tablet Combat uses a bounded operational HUD, T3-driven adaptive attack/action grid, stabilized spatial reorder/auto-scroll and one canonical persisted-order path. Card child actions are suppressed during active spatial drag. Permanent `check_player_wide_combat.py` remains in Scaffold.

Artifact `10330505672`, size `13,651,587` bytes, digest `sha256:582d455aea9dad0f0898fa43368d6bd69e6ed8994914522008ada85abe46c4c9`.

Future physical coverage: tablet-landscape width use, bounded HUD, adaptive packing under representative density/text settings, multi-column reorder/auto-scroll, persisted order, normal child actions outside drag, no accidental child action during drag, plus one narrow-phone Combat sanity check.

### T8 — Table Mode structural-affordance enforcement

Product `2734d08a72e183ca213cd9213ff47a4db588cbf6`; steady-state HEAD `d6c13819a49e1cb7c71dffcad98b53c250d4d9d4`; Scaffold `34799667822` / #1611 — **SUCCESS**.

Table Mode now visibly gates structural name/class/ability/save/skill/general-reference editing while genuine operational HP, inspiration and current resource state remain usable. `check_player_table_mode_affordances.py` guards both structural gating and operational-state preservation.

Artifact `10330822191`, size `13,662,998` bytes, digest `sha256:bf90762e36177735bd948524c37552b143ec1bbf1ddd7bdb292511b7a7255715`.

Future physical coverage: tablet structural controls non-editable/non-opening; operational HP/inspiration/resources functional and persistent; one narrow-phone Table Mode sanity check.

### T2 — dice-result / Custom Throw / Dice display-mode integration

Product commit `8131dd13f4373148503b854b1448f9d081556847`; permanent-guard wiring `8a081dd17c75cf800a9c792d7c008ec4a95b9161`; final migration-free steady-state HEAD `4d09e9eca648e5ca82af896dab8d16b986faae5b`.

Implemented:

- standard die-specific result silhouettes for d4/d6/d8/d10/d12/d20;
- neutral circular fallback for custom/unsupported die identity and flat numeric results;
- Custom Throw standard die selection;
- `Otro…` custom sides bounded to `2..1000`;
- direct signed modifier UX;
- shared arbitrary-die resolver reused by damage rolling;
- Dice-tab ownership of the existing persisted `dice_result_mode` preference;
- removal of the active result-mode selector from Application Settings;
- permanent `check_player_dice_t2.py` guard;
- focused shared-domain test for arbitrary sides and signed modifier.

No preference key, storage/schema/import/export model changed.

Integrated pre-cleanup read-only Scaffold `34800980682` / #1622 — **SUCCESS**.

Authoritative clean steady-state / aggregate Scaffold `34801201294` / #1625 — **SUCCESS**. It ran after all temporary T2 migration machinery was removed and normal Scaffold remained `contents: read`. Every permanent Player guard, backend, shared tests, Android/Desktop builds and APK upload passed.

Final artifact `10331846822`, size `13,693,976` bytes, digest `sha256:4ceeba64f31ab3ee4436f2ee1cc71ce324154d84cb99a551cbb1ef3ba28cb7f0`.

T2 = **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**.

Future physical coverage: ordinary visible d20 sanity; representative non-d20 Custom Throw; `Otro…` valid/invalid side bounds; positive/negative modifier behavior; die-identity visuals including damage and flat fallback; Dice-tab display-mode control; preference persistence/reopen; representative phone + tablet/wide sanity. Round 1 phone 7–9 remain part of the same targeted dice-family revalidation.

## 6. Consolidated aggregate proof

All material post-P17 repair families are implemented and automation green. Scaffold `34801201294` / #1625 is the authoritative aggregate proof because it ran normal read-only CI on the final migration-free repair tree and passed:

- backend install/type-check;
- compact Player geometry guard;
- reorder stability guard;
- checkbox consistency guard;
- spellcasting-bootstrap guard;
- class-editor-controls guard;
- application-settings semantics guard;
- wide-Combat composition guard;
- Table Mode affordance guard;
- T2 dice presentation / Custom Throw guard;
- shared tests;
- Android build;
- Desktop build;
- APK upload.

The optional phone-16 compact-density refinement is not taken. It was never a blocking defect: phone check 16 was PASS, and no new evidence demonstrates a safe material benefit sufficient to justify pre-candidate churn.

## 7. Next gate — freeze a new candidate

The next action is to create one new **monotonic physical-QA candidate** from the consolidated repaired Player line.

Candidate freeze must:

1. use a new versionName/versionCode; do not reuse `0.4.0-preqa.12 / 41200`;
2. identify the exact candidate commit;
3. run normal read-only Scaffold on that candidate revision;
4. record artifact ID, size and available digest(s);
5. checkpoint the candidate before physical revalidation begins.

Until that happens, preqa.12 remains the physical baseline and no repair is physically PASS.

## 8. Targeted physical revalidation after freeze

Do **not** replay the full historical phone/tablet discovery suites. Revalidate only failed/touched/affected families plus unresolved evidence.

Required coverage includes:

- Round 1/T2 dice family: phone 7–9 plus Custom Throw/result presentation/display-mode persistence;
- T1 reorder: one-column + spatial/multi-column + auto-scroll where applicable + leave/reopen persisted order;
- Round 3: representative Equipment/other checkbox surface plus Conjuros responsive groups/source-prepared packing;
- T5: canonical caster source/bootstrap, associations, save/reopen, manual-source coexistence and former tablet check 18;
- T6: numeric keyboards, standard/custom hit die and persistence;
- T3/T4/T9: Vertical/Horizontal density semantics, text-size behavior/migration and haptics `Ninguna`/non-none;
- T7: tablet-landscape Combat width/packing/reorder/persistence plus one narrow-phone sanity;
- T8: structural controls genuinely non-editable/non-opening while operational HP/inspiration/current resources remain usable/persistent;
- phone 21, which remains unassessed;
- affected slice of phone 22, which remains partial/ambiguous;
- tablet 18, previously blocked by T5.

Preserve all unrelated accepted PASS evidence.

## 9. Failure handling

If targeted physical QA finds a defect: classify it; audit source before asserting shared cause; reopen only the relevant repair boundary; preserve storage/import/export, IDs/associations and accepted evidence unless the approved repair requires change; rerun focused + aggregate automation; create a new monotonic candidate identity if the frozen candidate materially changes; checkpoint exact evidence before another owner pass.

Do not invent a new numbered repair merely because the project is waiting at a manual gate.

## 10. Status synchronization rule

At the end of every bounded implementation/test family or candidate-freeze gate, synchronize:

1. dedicated checkpoint under `docs/checkpoints/`;
2. `docs/PROJECT_STATE.md`;
3. `docs/checkpoints/LATEST.md`;
4. this `docs/TESTING.md`.

They must agree on completed family/gate, automation evidence, next action and physical status.

## 11. Phase 4A closure

Phase 4A closes only after the consolidated repaired candidate has sufficient targeted phone/tablet evidence for relevant repaired/touched families, blocking/previously unresolved evidence is addressed as required, and the owner explicitly accepts/closes Phase 4A. CI cannot substitute for owner closure.

No P18 exists. DM implementation remains blocked until explicit Phase 4A owner closure.