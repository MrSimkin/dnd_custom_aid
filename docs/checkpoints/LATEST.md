# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Previous physical-evidence candidate:** `0.4.0-preqa.10 / 41000` — R1–R3 PHYSICAL PASS; later phone QA reopened P16/P4  
**Current repaired physical-QA candidate:** `0.4.0-preqa.11 / 41100` at `21dc2b0eed4afc261b89578da424cd28d9894500` — AUTOMATION GREEN  
**Exact candidate run:** `34732621381` — SUCCESS  
**Acceptance boundary:** focused owner P16/P4 phone recheck → remaining representative phone/tablet evidence  
**Release status:** debug/development; NOT owner-accepted and NOT release-ready

## Resume here

1. `docs/checkpoints/2026-09-12_PHASE4A_PREQA11_QA_CANDIDATE.md` — **exact current candidate/run/artifact/digest evidence and focused owner recheck**.
2. `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_P16_P4_REPAIR_PROGRESS.md` — exact P16/P4 product repair commits and aggregate green proof.
3. `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_OWNER_PHONE_QA_PROGRESS.md` — physical evidence that reopened P16/P4 and owner repair priorities.
4. `docs/PROJECT_STATE.md` — live Player state/gate.
5. `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_QA_CANDIDATE.md` — previous physical candidate evidence.
6. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P16_LANDSCAPE_VERTICAL_SPACE_CLOSED.md` — controlling P16 contract.
7. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md` — controlling P4/P5/P9 contracts.

## Preserved PASS — do not repeat

Owner `preqa.10` report `1–7 OK` remains physical PASS for R1–R3: canonical HP propagation/max clamp, targeted HP feedback, exact `Establecer PV`, and the earlier gross Combat operation-row proportion repair.

## Repaired boundaries in preqa.11

**P16 — `fcf62103…`**

- shallow phone landscape uses one horizontal persistent row for identity/save header + scrollable top tabs;
- phone top-tab navigation semantics are preserved;
- constrained Combat HUD padding/spacing is tighter;
- `Cantidad` uses explicit compact internal padding rather than the taller default Material text-field body.

**P4 — `b40ed128…`**

- partial dice-edit state (`1d`, `d8`, `1d8-`, temporarily empty custom sides) remains stable during editing;
- strict save validity remains;
- numeric/custom fields no longer use clipped exact-48dp labelled Material geometry;
- selector controls use safe minimum height without rigid maximum;
- component internal padding/spacing is reduced;
- outer modal is not enlarged.

Portrait relocation of long-card actions is still only a consideration, not part of the repair.

## Candidate evidence

- `0.4.0-preqa.11 / 41100`
- exact commit `21dc2b0eed4afc261b89578da424cd28d9894500`
- exact Scaffold `34732621381` — **SUCCESS**
- artifact ID `10309348779`
- ZIP size `13,616,149` bytes
- GitHub artifact/ZIP digest `sha256:752302327bc4af330f5a1ea6e11b6ad61e4324e8cc230675f6d8d2da48fff391`
- independent ZIP hash: exact match
- APK size `38,881,392` bytes
- independent APK SHA-256 `1eebaffbed5e2f0d4479f288575186a916dde0a124e26e72948144bda032ff3a`

## Exact next action

Install `preqa.11` over the existing QA installation without clearing data. Recheck only the reopened P16/P4 physical boundary:

1. portrait `Cantidad` padding/height;
2. phone landscape combined header/tab/HUD footprint and useful attacks/actions area;
3. rotate landscape↔portrait without overlap/reachability/state regression;
4. standard die selection works reliably;
5. `Otro…` custom die sides works without wiping neighboring fields;
6. clearing/retyping quantity/sides/signed modifier keeps the structured controls stable;
7. numeric values are fully visible, not clipped;
8. internal editor geometry is materially more compact at 100% without a larger modal;
9. Save/Cancel remains usable and a valid edited component persists.

R1–R3 remain PASS. P17 tablet QA remains pending. No P18 exists. Phase 4A remains open. DM implementation remains blocked until explicit owner closure.
