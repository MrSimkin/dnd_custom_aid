# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Current physical-evidence candidate:** `0.4.0-preqa.10 / 41000` at `a0d7dbd8f0069c87690c8fa54da780da8ffd15e3` — R1–R3 PHYSICAL PASS; P16/P4 REOPENED BY LATER PHONE QA  
**Current source repair boundary:** P16 `fcf62103e4d4f1a4167d31efc05d13964c873d6d` + P4 `b40ed12862f73f8e179254b7e86a819962046cf1` — IMPLEMENTED / COMBINED AUTOMATION PENDING  
**Acceptance boundary:** green combined validation → next monotonic candidate → focused P16/P4 phone recheck → remaining phone/tablet evidence  
**Release status:** debug/development; NOT owner-accepted and NOT release-ready

## Resume here

1. `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_P16_P4_REPAIR_PROGRESS.md` — **current P16/P4 source repairs and exact commits**.
2. `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_OWNER_PHONE_QA_PROGRESS.md` — source physical evidence and owner repair priorities.
3. `docs/PROJECT_STATE.md` — live Player state and validation gate.
4. `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_QA_CANDIDATE.md` — historical exact preqa.10 artifact identity.
5. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P16_LANDSCAPE_VERTICAL_SPACE_CLOSED.md` — controlling P16 contract.
6. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md` — controlling P4/P5/P9 contracts.
7. `docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md` — durable authorization.

## Preserved PASS

Owner `preqa.10` report `1–7 OK` remains physical PASS for R1–R3. Do not repeat canonical HP/feedback/exact-correction checks unless later evidence directly contradicts them.

## P16 source repair

`fcf62103…`:

- in reduced/constrained phone landscape, identity/save header + scrollable top tabs now share one horizontal persistent row, using width to save height while retaining phone top-tab navigation;
- portrait/tablet shell behavior is unchanged;
- constrained Combat HUD nonessential vertical padding/spacing is reduced;
- `Cantidad` is now an explicitly compact numeric surface with controlled internal padding; `Daño`/`Curar` retain safe action height.

Net diff contains only `CharacterAdaptiveShellV4.kt` and `CharacterCombatOperationalV4.kt`.

## P4 source repair

`b40ed128…`:

- partial structured dice-edit tokens (`1d`, `d8`, `1d8-`, temporarily empty custom sides) now survive recomposition;
- strict save validity remains in force;
- quantity/modifier/custom-sides/flat/custom-type controls use compact explicitly padded fields instead of clipped exact-48dp labelled Material fields;
- selector controls keep safe minimum height without rigid maximum;
- damage-component internal padding/spacing is reduced;
- outer modal size was not increased.

Net diff contains only `CharacterCombatSuccessorV4.kt`.

## Exact next action

Observe the normal Scaffold gate on a descendant containing both source commits unchanged. Any compile/test/build failure must be repaired. After green aggregate validation, bump to the next monotonic QA identity (do not reuse `41000`), validate that exact candidate, freeze artifact/digest evidence, and hand it to the owner for a focused P16/P4 phone recheck.

P17 tablet QA remains pending. No P18 exists. Phase 4A remains open. DM implementation remains blocked until explicit owner closure.
