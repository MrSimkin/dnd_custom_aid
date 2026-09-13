# Phase 4A — preqa.11 owner phone QA progress

**Updated:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Physical-evidence candidate:** `0.4.0-preqa.11 / 41100`  
**Exact preqa.11 candidate commit:** `21dc2b0eed4afc261b89578da424cd28d9894500`  
**Owner result on preqa.11:** checks **1–7 PASS, 8 FAIL, 9 PASS**  
**Engineering status:** transversal control-geometry repair IMPLEMENTED and pre-version aggregate AUTOMATION GREEN; next candidate identity advanced to `0.4.0-preqa.12 / 41200`, physical recheck still pending.

## Physical owner evidence preserved

The owner physically tested the focused nine-point `preqa.11` recheck and reported:

- **1–7: PASS**
- **8: FAIL**
- **9: PASS**

These are real device results and remain authoritative for the tested scope. Earlier `preqa.10` R1–R3 PASS also remains preserved. Do not restart those checks unless later work directly changes the relevant behavior or contradictory evidence appears.

The preserved PASS scope includes portrait Combat `Cantidad`, phone-landscape combined header/tab/HUD footprint, portrait↔landscape rotation, standard/custom dice editing, incomplete structured-damage draft stability, numeric visibility/clipping, and Save/Cancel plus valid component persistence.

## Check 8 failure that triggered this repair

The owner supplied an annotated portrait `Editar ataque o acción` screenshot showing repeated excessive internal vertical whitespace/padding in equivalent controls and clarified that the same class matters in landscape. The owner explicitly required a **full-app equivalent-control audit**, not a one-dialog patch. The structured-dice `+ / −` selector was also explicitly included for geometry/consistency inspection.

Repair priority remained:

1. correct unnecessary internal vertical padding/margins first;
2. preserve legibility and safe touch targets;
3. adjust outer size only when still necessary;
4. do not solve by enlarging dialogs/containers;
5. prefer shared primitives/policies across portrait and landscape.

## 2026-09-13 transversal repair — implemented

Source inventory established that the recurrence was systemic rather than isolated:

- **160 raw Material `OutlinedTextField` call sites** existed across **29 current Player Kotlin files**;
- those usages bypassed a shared compact internal-padding policy even though P9 already supplied shared dialog sizing/min-height infrastructure;
- previously repaired compact controls therefore coexisted with ordinary raw Material fields, allowing the same vertical-whitespace defect to survive elsewhere;
- cards did not show a comparable systemic padding defect and were not globally resized.

Implemented repair chain:

- `087f6b9cd72b8bc376d9553d71be7ad48eb3a9ff` — added shared `CharacterCompactOutlinedTextFieldV4` and compact glyph-selector primitives; the editable field itself owns the safe 48dp+ single-line interaction envelope while its decoration controls compact label/value padding;
- `cc187f46efe73515953f84ad31b0162b208d86ea` — migrated all 160 raw Material Player fields across 29 files to the shared compact primitive and changed the structured-dice `+ / −` control to the shared compact glyph selector;
- `ac6794ea64a3cefedb872e9047d5d1ece00266b7` — added the durable source guard `scripts/check_player_control_geometry.py`;
- `869e20526755b854f2711e319be1d4aed2ba38f6` — wired that guard into normal Scaffold validation;
- `05c638f67dfbb8504575b175feac9c520763e744` — removed the completed one-shot repair workflow.

The repair deliberately leaves already-accepted custom compact controls alone and does not globally shrink cards/dialog containers.

## Automated proof before new candidate identity

Focused guarded migration proof:

- one-shot repair run `34775917100` — **SUCCESS**;
- exact migration guard result: `compactFieldCount=160; rawMaterialFields=0; diceSign=shared-compact-glyph`;
- focused `:androidApp:assembleDebug` — **SUCCESS** before the migration commit was pushed.

Cleaned authoritative branch aggregate proof:

- exact head `05c638f67dfbb8504575b175feac9c520763e744`;
- Scaffold run `34776384008` — **SUCCESS**;
- backend typecheck — PASS;
- persistent compact-control geometry guard — PASS;
- shared/Kotlin tests + Android assemble + desktop build — PASS;
- APK upload — PASS.

## Candidate progression

Because product UI code changed materially, build `41100` is not reused. The next monotonic candidate identity is now:

- versionName `0.4.0-preqa.12`
- versionCode `41200`

The exact `preqa.12` candidate commit/run/artifact/digests must be frozen only after the versioned Scaffold run succeeds. Until then, `preqa.11` remains the last physically tested candidate and its PASS/FAIL evidence is preserved exactly.

## Next owner gate — affected boundary only

Broad phone QA remains paused and P17 tablet QA remains pending until the versioned `preqa.12` candidate is automation-green and frozen.

When that happens, owner recheck should target only the affected transversal geometry boundary first:

- ordinary labelled/editable Player fields in representative portrait and landscape surfaces, including the prior `Editar ataque o acción` example;
- proportional vertical whitespace/padding across equivalent controls rather than one isolated dialog;
- structured-dice `+ / −` selector visual compactness/alignment and practical tapability;
- sanity that the repaired fields remain legible/editable and do not regress accepted Save/Cancel behavior.

Do **not** rerun preqa.11 checks 1–7 or 9 from scratch unless the new shared field primitive directly touches the specific control under test and a short sanity check is genuinely needed. Phase 4A remains open; no P18 exists; DM implementation remains blocked until explicit owner closure.
