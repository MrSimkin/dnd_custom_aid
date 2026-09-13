# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Current physical-evidence candidate:** `0.4.0-preqa.10 / 41000` at `a0d7dbd8f0069c87690c8fa54da780da8ffd15e3` — R1–R3 PHYSICAL PASS; LATER PHONE QA REOPENED P16/P4  
**Current repair boundary:** P16 product repair `fcf62103e4d4f1a4167d31efc05d13964c873d6d` + P4 product repair `b40ed12862f73f8e179254b7e86a819962046cf1`; combined automation/new candidate pending  
**Release status:** development/debug; NOT owner-accepted and NOT release-ready

## Branch authority / authorization

This branch remains authoritative for current Player runtime and Phase 4A repairs. `main` remains intentionally divergent for global/Phase 5A/DM discovery and is not the latest Player runtime. Current work is inside the durable P1–P17 repair/validation authorization. No P18 exists.

## Preserved physical PASS

Owner `preqa.10` report `1–7 OK` remains direct physical PASS for the repaired R1–R3 boundary: canonical General↔Combate HP propagation, max clamp, targeted HP feedback, exact `Establecer PV`, and the earlier gross three-part Combat operation-row repair. Do not repeat these checks absent contradictory evidence.

## Physical defects that reopened repair

Controlling evidence: `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_OWNER_PHONE_QA_PROGRESS.md`.

- **P16:** phone landscape still stacked identity/save header + tabs + Combat HUD into too much persistent vertical footprint; portrait `Cantidad` retained excessive internal vertical whitespace.
- **P4:** structured damage editor was physically unusable/reliable only inconsistently: dice selector/edit state collapsed and numeric controls were clipped/oversized.

Portrait relocation of long-card actions remains only a consideration, not an approved change.

## Source repair progress

Detailed live repair checkpoint: `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_P16_P4_REPAIR_PROGRESS.md`.

### P16 — source repaired

`fcf62103e4d4f1a4167d31efc05d13964c873d6d` — `repair: compact phone landscape shell and Combat HUD`

- shallow phone landscape under reduced/constrained height uses one horizontal persistent row for identity/save header + scrollable top tabs;
- phone top-tab semantics remain intact;
- portrait/tablet shell behavior is unchanged;
- constrained Combat HUD nonessential vertical padding/spacing is reduced;
- `Cantidad` uses a compact explicitly padded numeric surface while `Daño`/`Curar` keep safe action height.

Net pre/post repair diff contains exactly `CharacterAdaptiveShellV4.kt` and `CharacterCombatOperationalV4.kt`.

### P4 — source repaired

`b40ed12862f73f8e179254b7e86a819962046cf1` — `repair: stabilize and compact structured damage editor`

- dice draft parsing preserves incomplete editing states such as `1d`, `d8`, `1d8-` and temporarily empty `Otro…` sides instead of blanking neighboring fields;
- save-time validation remains strict;
- clipped labelled numeric Material fields are replaced with compact inline fields using explicit internal padding and the shared safe minimum height;
- rigid exact-48dp maximums are removed from selector controls;
- damage-component internal padding/spacing is reduced;
- outer editor/dialog size is unchanged: repair follows the owner's padding-first priority rather than solving by enlarging the box.

Net pre/post P4 repair diff contains exactly `CharacterCombatSuccessorV4.kt`.

## Current validation gate

P16/P4 source repairs are **not yet declared automation-green or physically accepted**. A normal Scaffold gate on a descendant containing both product commits unchanged must pass backend typecheck, shared/Kotlin tests/builds, Android assemble and APK upload. If it fails, repair the failure before candidate versioning.

After green aggregate validation:

1. advance to a new monotonic QA identity (do not reuse `41000`);
2. validate the exact versioned candidate;
3. freeze commit/run/artifact/digest evidence;
4. resume focused owner phone QA only on the reopened P16/P4 boundaries;
5. preserve the existing R1–R3 physical PASS;
6. then continue remaining representative phone/tablet evidence as meaningful.

P17 tablet QA remains pending. Phase 4A remains open. DM implementation remains blocked until explicit owner acceptance/closure.
