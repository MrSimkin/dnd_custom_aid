# Mara Phase 3 — post-Inventory green checkpoint

**Local date:** 2026-10-06 (Chile)
**Phase:** 3 — renderer repair implementation
**Branch:** `repair/mara-phase3-semantic-flow-compositor`
**Main base:** `c4e65955a5f58f88e0b03807b4616a3f8a9323a8`
**Current implementation code head:** `d9032bc7b207e42ca271ca2a31ee1c62a0bb511d`
**Current status:** ACTIVE / REPOSITORY-GREEN THROUGH CUSTOM-V2 INVENTORY
**Open implementation PR:** none at capture
**Owner visual handoff:** NOT AUTHORIZED

## Authority

Resume in this order:

1. `AGENTS.md`
2. `RESUME.md`
3. `docs/checkpoints/LATEST.md`
4. this checkpoint
5. `docs/checkpoints/2026-09-30_MARA_PHASE3_IMPLEMENTATION.md`
6. final burn-down in `docs/checkpoints/2026-09-30_MARA_PHASE2_EXISTING_REPAIR_AUDIT.md`
7. `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`
8. `docs/checkpoints/2026-09-26_PC_SHEET_MARA_PREQA8_PDF_VISUAL_QA_DEFECTS.md`
9. `docs/PC_SHEET_PDF_VISUAL_CONTRACT.md`
10. `docs/PC_SHEET_PDF_GEOMETRY_GATES.md`

The Phase-2 final burn-down and latest final owner decisions remain binding. Do not resurrect provisional questions already resolved there.

## Green recovery sequence after the 2026-09-30 interruption snapshot

- `0e258d2cb5a4d6d9896063b8ca8f5f77dcbb0f7c` — restore native v2 Inventory geometry constants; Scaffold #4495 compiled and exposed the semantic regression test.
- `1c959259c8d5cbffdd1683b6adcbd19387b81a4d` — scope Special Equipment association test to its native module; Scaffold #4496 SUCCESS.
- `7bfffa85d765aae52d0f079452e74bbb979fbb14` — release exhausted v2 Inventory submodules; Scaffold #4497 SUCCESS.
- `2c10cd37911271c1b27051985e4e43c5060f299f` — align Special Equipment fallback to source-measured native geometry; Scaffold #4498 SUCCESS.
- `d9032bc7b207e42ca271ca2a31ee1c62a0bb511d` — reuse native ordinary Equipment columns and semantic fit/wrap rules; Scaffold #4499 / `37516161639` SUCCESS.

Do not replay any of those commits.

## Current validated Custom-v2 Inventory state

- ordinary Equipment continuation exposes quantity + complete identity only;
- no ordinary weight, `Consumible`, `Equipado`, operational prose or description leakage;
- fit order is native preferred size -> bounded uniform reduction -> wrap at readable minimum;
- all physical lines of one wrapped identity use one uniform scale and continuation indentation;
- ordinary continuation reuses source/native column widths and row cadence instead of the old generic split-table design;
- Special Equipment detail remains associated with Equipo Especial;
- Special Equipment custom-location fallback uses source-measured location/name/detail/check geometry and no generic checkbox/location column;
- exhausted ordinary / treasure / Special Equipment submodules disappear rather than reserving empty scaffold space;
- Android/Desktop renderer sync guard is green.

Source coordinates are not sacred; source geometry/visual grammar is. Do not force absolute base-page coordinates onto a continuation layout without actual defect evidence.

## Shared architecture already green

- shared semantic-flow contracts: Scaffold #4391 PASS;
- native-module contracts: Scaffold #4394 PASS;
- constraint-aware Extended-page composer contract: Scaffold #4396 PASS;
- substantial family integration checkpoint: Scaffold #4485 PASS;
- latest Inventory package: Scaffold #4499 PASS.

## Exact next authorized action

The next active gap is **M50800-27: production integration of the global Extended-page compositor**.

1. Verify production family renderers still emit Extended pages role-by-role.
2. Wire the already-green `PcSheetExtendedPageComposer` into actual family rendering in bounded family packages.
3. Define only valid family-native layout templates supported by approved/source geometry.
4. Build active semantic demand from all remaining content, not one role at a time.
5. Exhausted modules disappear; compatible surviving modules reclaim only valid native slots.
6. Fixed modules repeat rather than resize.
7. Notes remains a full-native-page exclusive layout.
8. Preserve current family rendering primitives and record packing; the compositor chooses placement, not new visual grammar.
9. Add production regressions proving compositor use and reclaim behavior.
10. Preserve Android/Desktop parity for Custom renderers.
11. Rerun Scaffold after each coherent family package.

## Still pending before owner candidate

- finish compositor integration across affected families;
- reconcile Traits stored/player order and packing;
- finish Resources/class-choice global reclaim;
- complete semantic ellipsis / silent-drop audit;
- verify bidirectional continuity in actual output;
- verify source geometry / stale underlay;
- preserve Android/Desktop parity;
- generate real Mara in Fantasy, Custom v1, Custom v2 Atributo and Custom v2 Habilidad from one exact candidate;
- inspect those exact PDFs against M50800-01…32 and mark every item FIXED / OPEN / CHANGED-NEW;
- use a new unique versionName/versionCode; never reuse `0.5.0-preqa.8 / 50800`;
- record exact commit, workflow, artifact, APK SHA-256 and exact four PDF hashes.

Current Snapshot and Media/Handouts remain blocked. Phase 3 is not closed.

## Interruption safety

Before repeating any operation after timeout/poll/connection failure, verify branch HEAD, recent commits, workflows, PR state and whether the intended operation already occurred. Never reset automatically to an older green comparison point.

At Phase-3 closure, retain the owner's requested recovery/continuation prompt for entering the next phase without context loss.
