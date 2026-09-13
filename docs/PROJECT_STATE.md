# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Current exact QA candidate:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a` — AUTOMATION GREEN; PHONE QA EXECUTED WITH OPEN FINDINGS  
**Current phase:** P17 physical tablet QA on the same `preqa.12` candidate before consolidated repair  
**Release status:** development/debug; Phase 4A OPEN; DM implementation blocked pending explicit Phase 4A closure

## Authority / authorization

This branch remains authoritative for current Player runtime and Phase 4A repairs. `main` remains intentionally divergent for global/Phase 5A/DM discovery and is not the latest Player runtime. Current work remains inside the durable P1–P17 repair/validation authorization. No P18 exists.

## Current physical evidence

Earlier physical evidence remains preserved where not contradicted:

- `preqa.10` R1–R3: PASS;
- `preqa.11` checks 1–7 and 9: PASS;
- `preqa.11` check 8: FAIL; its transversal field-geometry boundary was repaired in `preqa.12`;
- `preqa.12` bounded geometry recheck: 5/5 PASS;
- `preqa.12` broad regression gate as then phrased: 7/7 PASS.

A later detailed 23-check phone pass on the same exact `preqa.12` candidate supersedes the earlier interpretation that the complete phone gate was cleanly CLOSED/PASS.

Latest detailed phone findings:

- checks 1–6 PASS;
- check 7 OPEN DEFECT — structured-damage modifier entry can change selected die to `Otro`;
- check 8 OPEN DEFECT — under `Otro`, modifier entry is misrouted/behaves as `caras del daño` and normal modifier entry is unavailable;
- check 9 PASS + UX request — direct tap-toggle between `+` and `−` instead of dropdown;
- checks 10–15 PASS;
- check 16 PASS + non-blocking request — slightly tighter window vertical padding where safe;
- check 17 OPEN FAMILY — app-wide Player checkbox/control-density and responsive grouping audit required (including Equipo checkbox styling and Conjuros source/prepared + V/S/M + Concentración/Ritual layout efficiency);
- checks 18–20 PASS;
- check 21 NOT ASSESSED because the test was not understood;
- check 22 INCOMPLETE/AMBIGUOUS;
- check 23 PASS.

Controlling current checkpoint: `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_PHONE_FINDINGS_P17_ROUTE.md`.

The older `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_OWNER_PHONE_QA_PASS.md` remains historical evidence but is explicitly superseded for current gate status.

## Why P17 proceeds before repair

`docs/TESTING.md` explicitly permits P17 Player-tablet QA when phone results do not expose a hard shared/systemic failure that would make tablet evidence meaningless, and states that a bounded/local phone defect does not automatically block tablet QA.

The current open findings do not describe install/start failure, crash/ANR, persistence corruption, navigation-wide failure or another condition that invalidates tablet observation. Tablet QA is also useful for characterizing the responsive checkbox/layout family before one consolidated repair.

Therefore `preqa.12` remains the exact P17 discovery/QA candidate even though it is not an accepted clean phone candidate.

## Automated proof / candidate identity

Exact candidate remains unchanged:

- versionName `0.4.0-preqa.12`;
- versionCode `41200`;
- candidate commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- Scaffold `34776627282` — SUCCESS;
- artifact `10323602038` / `dnd-custom-aid-debug-apk`;
- ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

No product code, APK, version or artifact has changed since that candidate was built.

## Exact route / next action

1. Run **P17 physical tablet QA now** on exact `preqa.12 / 41200`, portrait and landscape, preserving all valid phone evidence.
2. Record tablet PASS/FAIL/observations without treating known phone defects as automatic tablet failures.
3. After tablet discovery is complete, audit the full Player checkbox/responsive-layout family and consolidate all phone + tablet defects/refinements into one coherent repair batch.
4. Produce a new monotonic candidate after material product changes.
5. Revalidate only failed/touched/affected phone and tablet families plus unresolved phone checks 21/22; do not restart unaffected PASS coverage wholesale.
6. Phase 4A may close only after required defects are repaired/revalidated, P17 has actual tablet evidence, and the owner explicitly accepts/closes Phase 4A.

Portrait relocation of long-card action buttons remains only a prior consideration, not an approved automatic change. No P18 exists. DM implementation remains blocked until explicit Phase 4A owner closure.
