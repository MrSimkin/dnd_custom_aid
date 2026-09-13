# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Last physically tested candidate:** `0.4.0-preqa.11 / 41100` — checks 1–7 + 9 PASS / check 8 FAIL  
**Current exact QA candidate:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a` — AUTOMATION GREEN; focused physical geometry recheck pending  
**Acceptance boundary:** repaired full-app equivalent-control geometry first; then remaining phone QA and P17 tablet QA  
**Release status:** debug/development; NOT owner-accepted and NOT release-ready

## Resume here

1. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_QA_CANDIDATE.md` — **exact current candidate/run/artifact/digest evidence and bounded owner recheck**.
2. `docs/checkpoints/2026-09-12_PHASE4A_PREQA11_OWNER_PHONE_QA_PROGRESS.md` — preserved physical PASS/FAIL plus repair handoff.
3. `docs/PROJECT_STATE.md` — live Player authority/current gate.
4. `scripts/check_player_control_geometry.py` — persistent guard against raw Material field-geometry regression and old dice-sign button path.
5. `docs/checkpoints/2026-09-12_PHASE4A_PREQA11_QA_CANDIDATE.md` — prior candidate evidence; not current build.
6. P16/P4 and repair-principle checkpoints from 2026-09-11/12 remain controlling where not superseded.

## What changed after preqa.11 check 8 failed

The owner required a transversal fix for repeated excessive vertical internal whitespace/padding in equivalent Player controls in portrait and landscape, not a screenshot-only repair. The `+ / −` structured-dice selector was included in that audit.

Source inventory found **160 raw Material `OutlinedTextField` usages across 29 current Player files**. All are now routed through one shared compact field primitive. The editable single-line control still owns a safe 48dp+ interaction envelope; unnecessary internal visual whitespace is controlled by the shared decoration. The dice sign selector now uses a compact glyph surface inside a safe interaction envelope. Existing accepted custom compact controls remain untouched; cards/dialogs were not globally enlarged.

## Proof

Focused repair run `34775917100`: **SUCCESS**.

Cleaned pre-version aggregate `05c638f67dfbb8504575b175feac9c520763e744`, Scaffold `34776384008`: **SUCCESS**.

Exact `preqa.12` candidate:

- commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- Scaffold `34776627282` — **SUCCESS**;
- persistent geometry guard — PASS;
- backend + shared/Kotlin tests + Android assemble + desktop build + APK upload — PASS;
- artifact ID `10323602038`, `dnd-custom-aid-debug-apk`;
- ZIP `13,627,800` bytes;
- GitHub digest / independent ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK `38,914,160` bytes, SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

## Physical evidence preserved

- earlier `preqa.10` R1–R3: PASS;
- `preqa.11` checks 1–7 and 9: PASS;
- `preqa.11` check 8: FAIL and is the boundary repaired by `preqa.12`;
- `preqa.12`: no physical result yet.

## Exact next action

Install/test exact `preqa.12`. Do **not** restart the old nine-point list or broad QA from zero.

First recheck only:

1. representative ordinary labelled/editable Player fields in portrait, including `Editar ataque o acción`;
2. equivalent fields in landscape;
3. the structured-dice `+ / −` selector for compactness/alignment **and easy tapability**;
4. quick text/numeric editability and optionally one multiline field;
5. short Save/Cancel sanity in an affected editor.

If this affected boundary passes, preserve all earlier PASS and resume remaining broad phone QA, then P17 tablet QA. If it fails, capture the specific remaining geometry and keep repair scope evidence-driven.

No P18 exists. Phase 4A remains open. DM implementation remains blocked until explicit owner closure.
