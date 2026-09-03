# Phase 4 Closure — Batch 0 + A1 checkpoint

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Canonical `main`:** untouched

## Batch 0 — housekeeping completed

Current-truth drift was corrected in:

- `README.md`;
- `AGENTS.md`;
- `MANIFEST.md`;
- `docs/ROADMAP.md`;
- `docs/ARCHITECTURE.md`;
- `docs/TESTING.md`.

Key corrections:

- Phase 4 Character Foundation Closure is now the active stage everywhere relevant;
- DM implementation is explicitly blocked until the D-0047 character closure is complete and owner-accepted;
- phone + tablet portrait/landscape are first-class closure QA targets;
- current character implementation is no longer described as an empty scaffold;
- the active execution plan is `2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md`;
- the previous correction APK is historical evidence, not the next QA target;
- the old blanket signing wording was reconciled with actual CI behavior: the stable tracked/reconstructable Android identity is development/debug-only for update-in-place migration QA, is not a secret trust boundary and must never be used for a real release; future production/release signing remains private.

### Known housekeeping item intentionally still open

The large consolidated `docs/DECISIONS.md` still ends at D-0043 while detailed approved D-0044 through D-0047 records exist under `docs/decisions/`. This is an index/consolidation lag, not an unresolved product decision. It remains visible and must be reconciled before the eventual Phase 4 merge proposal. Historical decision numbers must not be renumbered merely to make the file look contiguous.

## Execution plan created

`docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md` decomposes the large A–J implementation map into smaller recoverable batches:

- Batch 0 housekeeping;
- A1 catalog reconciliation;
- A2 schema 7;
- B1/B2 global interaction foundations;
- C PC Settings;
- D Gestión;
- E General/Habilidades/Combat;
- F Equipment;
- G1/G2/G3 Traits/Spells/Notes/Background;
- H1/H2/H3 conditional modules;
- I1/I2 adaptive shell/Supercompact/Table mode;
- J backup/reconciliation;
- K integration;
- L candidate freeze;
- M phone+tablet owner QA.

Each batch gets its own durable checkpoint/gate. DM work remains blocked until the Phase 4 exit gate.

## Batch A1 — catalog reconciliation implemented

Code commit:

- `1b2f6a79924b88d71567a277f6752dea6bbbb1c9` — Arcana Unleashed availability reconciliation.

The code diff was inspected after commit and changed only the intended catalog lines/helper semantics.

Changes:

- all eight audited Arcana Unleashed subclasses now use current released-source metadata rather than stale upcoming/early-access metadata;
- source remains `Arcana Unleashed`;
- rules family remains D&D 5.5e;
- module suggestions are preserved.

Affected keys:

- `cleric-arcana-2026`;
- `fighter-arcane-archer-2026`;
- `monk-mystic-arts-2026`;
- `warlock-vestige-2026`;
- `wizard-conjurer-2026`;
- `wizard-enchanter-2026`;
- `wizard-necromancer-2026`;
- `wizard-transmuter-2026`.

Test commit:

- `dd6f50afebe862222861ee8ccb39cfe99ee82df1` — focused current/legacy/provenance/module catalog tests.

Added assertions cover:

- all eight Arcana Unleashed keys exist;
- each has the correct current source and D&D 5.5e rules family;
- none retains a stale availability note;
- Arcane Archer and Mystic Arts trigger Techniques;
- Vestige triggers Pacts + Companions;
- Necromancer triggers Companions;
- current and legacy Artificer/Battle Smith variants retain distinct source/version identity;
- the `custom` escape key remains intentionally outside the built-in legality catalog.

## Verification status at checkpoint creation

A1 GitHub Actions run:

- run `33785858196` for head `dd6f50afebe862222861ee8ccb39cfe99ee82df1`;
- status at checkpoint creation: queued/running, final conclusion not yet recorded.

Do **not** call Gate A1 closed until that run completes successfully.

## Exact next action

1. Read the final conclusion for run `33785858196`.
2. If PASS: mark A1 green in `docs/PROJECT_STATE.md` and begin **Batch A2 — additive schema 7 durable domain model**.
3. If FAIL: diagnose/fix A1 and create a replacement A1 checkpoint before beginning A2.
