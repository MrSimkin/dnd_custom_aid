# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Current exact QA candidate:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a` — AUTOMATION GREEN; OWNER PHONE QA PASS  
**Current phase:** P17 tablet QA  
**Release status:** development/debug; phone gate closed, Phase 4A still OPEN pending P17 and explicit owner closure

## Authority / authorization

This branch remains authoritative for current Player runtime and Phase 4A repairs. `main` remains intentionally divergent for global/Phase 5A/DM discovery and is not the latest Player runtime. Current work remains inside the durable P1–P17 repair/validation authorization. No P18 exists.

## Preserved physical evidence

Earlier `preqa.10` R1–R3 PASS remains valid.

On `preqa.11`, the owner physically reported **checks 1–7 and 9 PASS, check 8 FAIL**. The preserved PASS scope covers portrait Combat `Cantidad`, phone-landscape shell footprint, rotation sanity, standard/custom dice editing, incomplete structured-damage edit stability, numeric clipping/visibility, and Save/Cancel + valid persistence.

`preqa.11` check 8 reopened the broader full-app equivalent-control vertical padding/spacing boundary. That defect was repaired in `preqa.12` and is now physically closed.

On exact `preqa.12 / 41200`, the owner reported:

- affected transversal geometry recheck: **5/5 PASS**;
- remaining phone-wide regression gate: **7/7 PASS**.

The Phase 4A **phone QA gate is CLOSED / PASS**. Do not restart phone QA from zero absent contradictory evidence or a later change directly affecting already-tested behavior.

Durable owner-result checkpoint: `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_OWNER_PHONE_QA_PASS.md`.

## Transversal geometry repair completed

Full-app source inventory found **160 raw Material `OutlinedTextField` sites across 29 Player Kotlin files**. These bypassed a shared compact internal-padding policy, explaining recurrence outside previously repaired compact controls.

The accepted engineering repair:

- routes all 160 sites through shared `CharacterCompactOutlinedTextFieldV4`;
- gives the actual editable single-line control a safe 48dp+ interaction envelope while reducing unnecessary internal label/value whitespace;
- preserves current multiline growth, numeric keyboard, enabled/read-only/error/supporting/placeholder/prefix semantics;
- changes the structured-dice `+ / −` selector to the shared compact glyph grammar with a safe interaction envelope;
- leaves already-accepted custom compact controls intact;
- does not globally resize cards or enlarge dialogs.

Repair/guard chain: `087f6b9c…` → `cc187f46…` → `ac6794ea…` → `869e2052…` → cleanup `05c638f6…`.

## Automated proof

Focused repair run `34775917100`: **SUCCESS**; geometry guard PASS (`compactFieldCount=160; rawMaterialFields=0; diceSign=shared-compact-glyph`) and Android assemble PASS.

Cleaned pre-version aggregate:

- commit `05c638f67dfbb8504575b175feac9c520763e744`;
- Scaffold `34776384008` — **SUCCESS**.

Exact versioned candidate:

- versionName `0.4.0-preqa.12`;
- versionCode `41200`;
- candidate commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- Scaffold `34776627282` — **SUCCESS**;
- backend typecheck, persistent geometry guard, shared/Kotlin tests, Android assemble, desktop build and APK upload all PASS.

Artifact evidence:

- artifact `10323602038` / `dnd-custom-aid-debug-apk`;
- ZIP size `13,627,800` bytes;
- GitHub digest = independent ZIP SHA-256 = `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK size `38,914,160` bytes;
- APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

Controlling candidate checkpoint: `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_QA_CANDIDATE.md`.

## Exact next action

Proceed to **P17 tablet QA** using the controlling Phase 4A tablet/physical-validation contract. Preserve all valid phone evidence and do not rerun the phone suite wholesale.

After P17 is resolved, Phase 4A still requires explicit owner acceptance/closure before DM implementation may begin. Portrait relocation of long-card action buttons remains only a prior consideration, not an approved automatic change. No P18 exists.
