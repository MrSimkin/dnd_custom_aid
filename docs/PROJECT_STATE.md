# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Previous physical-evidence candidate:** `0.4.0-preqa.10 / 41000` — R1–R3 PHYSICAL PASS; later phone QA reopened P16/P4  
**Current repaired physical-QA candidate:** `0.4.0-preqa.11 / 41100` at `21dc2b0eed4afc261b89578da424cd28d9894500` — AUTOMATION GREEN / FOCUSED OWNER P16-P4 RECHECK NEXT  
**Release status:** development/debug; NOT owner-accepted and NOT release-ready

## Authority / authorization

This branch remains authoritative for current Player runtime and Phase 4A repairs. `main` remains intentionally divergent for global/Phase 5A/DM discovery and is not the latest Player runtime. Current work is inside the durable P1–P17 repair/validation authorization. No P18 exists.

## Preserved physical PASS

Owner `preqa.10` report `1–7 OK` remains direct physical PASS for R1–R3: canonical General↔Combate HP propagation, max clamp, targeted HP feedback, exact `Establecer PV`, and the prior gross three-part Combat operation-row repair. Do not repeat these checks absent contradictory evidence.

## Physical defects repaired in preqa.11

Controlling source evidence: `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_OWNER_PHONE_QA_PROGRESS.md`.

Detailed engineering repair record: `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_P16_P4_REPAIR_PROGRESS.md`.

### P16

Product commit `fcf62103e4d4f1a4167d31efc05d13964c873d6d`:

- shallow/reduced phone landscape places identity/save header + scrollable top tabs in one horizontal persistent row;
- phone top-tab semantics remain intact; no tablet side-rail switch;
- portrait/tablet shell behavior is unchanged;
- constrained Combat HUD nonessential vertical padding/spacing is reduced;
- `Cantidad` uses a compact explicitly padded numeric surface while `Daño`/`Curar` keep safe action height.

### P4

Product commit `b40ed12862f73f8e179254b7e86a819962046cf1`:

- partial structured dice-edit tokens survive recomposition instead of wiping neighboring controls;
- strict save validity remains in force;
- quantity/modifier/custom-sides/flat/custom-type fields use compact explicit padding rather than clipped exact-48dp labelled Material fields;
- selector controls keep safe minimum height without a rigid maximum;
- component padding/spacing is reduced;
- outer modal size was not increased.

Portrait relocation of long-card action buttons remains only a consideration, not an approved change.

## Automated proof

Combined repaired source state:

- qualifying descendant commit `5a6cb06cbcd3380622a01c27cac3985d65f51ab9`
- Scaffold `34732466227` — **SUCCESS**

Exact versioned candidate:

- `0.4.0-preqa.11 / 41100`
- exact candidate commit `21dc2b0eed4afc261b89578da424cd28d9894500`
- exact Scaffold run `34732621381` — **SUCCESS**
- artifact ID `10309348779`
- artifact ZIP size `13,616,149` bytes
- GitHub ZIP digest `sha256:752302327bc4af330f5a1ea6e11b6ad61e4324e8cc230675f6d8d2da48fff391`
- independently downloaded ZIP hash: exact match
- APK size `38,881,392` bytes
- independent APK SHA-256 `1eebaffbed5e2f0d4479f288575186a916dde0a124e26e72948144bda032ff3a`

Dedicated candidate checkpoint: `docs/checkpoints/2026-09-12_PHASE4A_PREQA11_QA_CANDIDATE.md`.

## Current gate

Install `preqa.11` over the current QA installation without clearing data and perform a **focused P16/P4 phone recheck only**:

- portrait `Cantidad` padding/height;
- phone-landscape combined header/tab/HUD footprint and useful attacks/actions area;
- rotate portrait↔landscape without overlap/reachability/state regression;
- standard and custom die selection/editing without state wipe;
- fully visible numeric controls and materially more compact editor geometry at 100%;
- Save/Cancel and persistence of a valid edited component.

R1–R3 remain PASS and should not be restarted. P17 tablet QA remains pending after meaningful phone evidence. Phase 4A remains open. DM implementation remains blocked until explicit owner acceptance/closure.
