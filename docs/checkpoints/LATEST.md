# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Current exact QA candidate:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a` — AUTOMATION GREEN; OWNER PHONE QA PASS  
**Acceptance boundary:** phone QA CLOSED / PASS; next gate is P17 tablet QA  
**Release status:** debug/development; Phase 4A still OPEN pending P17 and explicit owner closure

## Resume here

1. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_OWNER_PHONE_QA_PASS.md` — **authoritative owner phone-QA closure evidence; exact next gate is P17 tablet QA**.
2. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_QA_CANDIDATE.md` — exact candidate/run/artifact/digest evidence plus recorded phone PASS.
3. `docs/PROJECT_STATE.md` — live Player authority/current gate.
4. `scripts/check_player_control_geometry.py` — persistent guard against raw Material field-geometry regression and old dice-sign button path.
5. `docs/checkpoints/2026-09-12_PHASE4A_PREQA11_OWNER_PHONE_QA_PROGRESS.md` — historical preqa.11 PASS/FAIL and repair handoff; superseded for current gate by the preqa.12 phone-PASS checkpoint.
6. P16/P4 and repair-principle checkpoints from 2026-09-11/12 remain controlling where not superseded.

## Current physical state

Cumulative physical evidence:

- earlier `preqa.10` R1–R3: PASS;
- `preqa.11` checks 1–7 and 9: PASS;
- `preqa.11` check 8: FAIL, repaired by `preqa.12`;
- `preqa.12` affected transversal geometry recheck: **5/5 PASS**;
- `preqa.12` remaining phone-wide regression gate: **7/7 PASS**.

The Phase 4A **phone QA gate is therefore CLOSED / PASS** on exact candidate `preqa.12 / 41200`.

Do not rerun phone QA from zero absent contradictory evidence or a later change that directly affects already-tested behavior.

## Repair summary

The `preqa.11` check-8 failure was systemic rather than local. Source inventory found **160 raw Material `OutlinedTextField` usages across 29 Player files**. `preqa.12` routes those sites through a shared compact field primitive while preserving a safe 48dp+ editable envelope, and moves the structured-dice `+ / −` selector to the compact glyph grammar inside a safe interaction envelope. Cards/dialogs were not globally enlarged or indiscriminately resized.

The owner has now physically accepted this repaired boundary on phone in portrait/landscape and completed the remaining phone-wide regression pass.

## Automated proof

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

## Exact next action

Proceed to **P17 tablet QA** under the existing Phase 4A physical-validation contract. Preserve all phone PASS evidence.

Phase 4A remains open until P17 is resolved and the owner explicitly accepts/closes Phase 4A. No P18 exists. DM implementation remains blocked until explicit owner closure.
