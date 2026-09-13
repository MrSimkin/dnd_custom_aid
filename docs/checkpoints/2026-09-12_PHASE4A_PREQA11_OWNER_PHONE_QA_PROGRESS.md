# Phase 4A — owner phone QA progress / preqa.11 evidence → preqa.12 repair handoff

**Updated:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Last physically tested candidate:** `0.4.0-preqa.11 / 41100` at `21dc2b0eed4afc261b89578da424cd28d9894500`  
**Owner result on preqa.11:** checks **1–7 PASS, 8 FAIL, 9 PASS**  
**New exact repair candidate:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a` — AUTOMATION GREEN; focused physical geometry recheck pending

## Physical owner evidence preserved

The owner physically tested `preqa.11` and reported:

- **1–7: PASS**
- **8: FAIL**
- **9: PASS**

Earlier `preqa.10` R1–R3 PASS also remains preserved. Do not restart these checks absent contradictory evidence or a later change that directly affects the same behavior.

The preserved `preqa.11` PASS scope covers portrait Combat `Cantidad`, phone-landscape combined header/tab/HUD footprint, portrait↔landscape rotation, standard/custom dice editing, incomplete structured-damage draft stability, numeric visibility/clipping, and Save/Cancel plus valid component persistence.

## Check 8 — failure repaired in preqa.12

Check 8 showed recurring excessive vertical internal whitespace/padding in equivalent Player controls, illustrated by `Editar ataque o acción`, with the owner explicitly clarifying that portrait **and** landscape and equivalent controls across the app had to be covered. The structured-dice `+ / −` selector was also explicitly queued for geometry/consistency inspection.

The 2026-09-13 source audit established the systemic escape hatch: **160 raw Material `OutlinedTextField` sites across 29 current Player Kotlin files** bypassed a shared compact internal-padding policy.

## Implemented transversal repair

- shared compact field and glyph primitives: `087f6b9cd72b8bc376d9553d71be7ad48eb3a9ff`;
- 160-site full-app field migration + structured-dice sign migration: `cc187f46efe73515953f84ad31b0162b208d86ea`;
- persistent source guard: `ac6794ea64a3cefedb872e9047d5d1ece00266b7`;
- guard wired into normal Scaffold: `869e20526755b854f2711e319be1d4aed2ba38f6`;
- temporary repair workflow removed: `05c638f67dfbb8504575b175feac9c520763e744`.

The shared field keeps a safe 48dp+ single-line editable envelope while directly controlling the internal label/value whitespace. Multiline growth and current numeric/read-only/error/supporting/placeholder/prefix use cases are preserved. Already-accepted custom compact controls are left intact; cards/dialogs are not globally enlarged or indiscriminately resized.

## Automated proof

Focused repair run `34775917100`: **SUCCESS** with guard result `compactFieldCount=160; rawMaterialFields=0; diceSign=shared-compact-glyph` and focused Android assemble PASS.

Cleaned pre-version aggregate at `05c638f67dfbb8504575b175feac9c520763e744`: Scaffold `34776384008` — **SUCCESS**.

Exact `preqa.12` candidate:

- commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- Scaffold `34776627282` — **SUCCESS**;
- backend, persistent geometry guard, shared/Kotlin tests, Android assemble, desktop build and APK upload all PASS.

Artifact:

- ID `10323602038`, name `dnd-custom-aid-debug-apk`;
- ZIP size `13,627,800` bytes;
- GitHub digest and independent ZIP SHA-256: `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK `androidApp-debug.apk`, size `38,914,160` bytes;
- independent APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

Full frozen evidence: `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_QA_CANDIDATE.md`.

## Exact next owner action — affected boundary only

Use `preqa.12`; do not resume by rerunning checks 1–7 or 9 from scratch.

First verify only:

1. representative ordinary labelled/editable Player fields in **portrait**, including the prior attack/action editor example;
2. representative equivalent fields in **landscape**;
3. structured-dice `+ / −` selector compactness/alignment **and tapability**;
4. quick editability sanity for text/numeric fields, optionally one multiline field;
5. short Save/Cancel sanity in an affected editor.

Pass criterion: equivalent controls should no longer have disproportionate internal vertical empty space, should remain legible/focusable/tappable, and should not gain clipping or broken interaction in landscape.

If this focused transversal boundary passes, preserve all earlier PASS and resume remaining broader phone QA, then P17 tablet QA. `preqa.12` has no physical result yet. Phase 4A remains open; no P18 exists; DM implementation remains blocked until explicit owner closure.
