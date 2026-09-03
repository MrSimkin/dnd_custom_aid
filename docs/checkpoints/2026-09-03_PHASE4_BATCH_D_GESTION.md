# Phase 4 — Batch D Gestión checkpoint

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** GREEN  
**Canonical `main`:** untouched

## Scope implemented

Batch D adds the approved `Gestión` live character-maintenance surface without creating shadow copies of character state.

Implemented responsibilities:

- Conditions + Exhaustion;
- current Concentration tracking;
- generic Resources with quick `−` / `+` operations and exact editing;
- optional structured recovery metadata per resource;
- Short/Long Rest assistant with preview and explicit selective apply;
- manual/custom recovery rows are informational and never auto-applied;
- temporary effects/bonuses with active state;
- Inspiration operational control;
- death saves surfaced contextually at 0 HP;
- reconciliation checkpoint creation and recent-history display;
- responsive stacking on narrow layouts and paired state cards on wider layouts;
- configured character haptics reused for operational resource/destructive actions.

## Ownership boundary

`CharacterRepository` / `CharacterSheet` remains authoritative for existing core live state:

- Resources;
- Inspiration;
- death-save successes/failures;
- HP and class hit-die records.

`CharacterClosureRepository` / schema-7 `CharacterClosureState` remains authoritative for additive closure state:

- Conditions/Exhaustion;
- Concentration;
- resource recovery metadata;
- temporary effects;
- reconciliation checkpoints.

`Gestión` persists live operational changes immediately against the currently stored character. It does not consume, reset or silently save unrelated structural drafts from General/Combat/Equipment/etc.

## Rest behavior

Pure shared recovery logic lives in `CharacterRestOperations.kt`.

Rules:

- rest preview never mutates state;
- `TO_MAX` and `FIXED` recovery can produce a numeric proposal;
- fixed recovery is capped at a known maximum;
- only explicitly selected numeric proposals are applied;
- manual/custom recovery remains visible but cannot mutate automatically;
- Hit Dice are shown for review but are **not automatically recovered** because this project permits mixed D&D 5e / 5.5e / custom rules and Batch D must not silently choose a ruleset.

## Verification history

### D1 — shared rest operations

Workflow `33797081412`: **PASS**.

Covered:

- Short vs Long Rest matching;
- `SHORT_OR_LONG_REST` behavior;
- fixed recovery capping;
- manual-review behavior;
- selective application only;
- already-full resource behavior.

The run included backend, shared desktop tests, Android debug assembly, Desktop build and APK artifact upload.

### D2 — Gestión UI integration

Integration bot commit:

- `d5389e707ba05bf4373122763ae2f69c0518654c` — `feat: integrate Gestión character maintenance`

First controlling workflow:

- `33808464225` — **FAILED Android compile**;
- backend PASS;
- shared desktop tests PASS;
- Desktop build PASS;
- Android failed on one cross-module Kotlin smart-cast of `CharacterResource.maxValue`.

Repair commit:

- `2a5d9b35669e62ac44b92bdca9fbcf649ba5fcd0` — `fix: repair Gestión resource validation`.

The repair also corrected an independently found UX validation issue: after changing a resource recovery cadence to Manual/None, a previously selected hidden FIXED amount can no longer incorrectly block saving.

Final controlling head before this documentation-close commit:

- `64033be2632012cb6cac19728ebecb1d44ec553b`.

Final controlling workflow:

- `33809045740` — **PASS**;
- backend PASS;
- shared desktop tests PASS;
- Android debug assembly PASS;
- Desktop build PASS;
- APK artifact upload PASS.

## Gate conclusion

**Batch D is closed GREEN.**

The next implementation batch is **Batch E — General + Habilidades + Combate**.

Real phone/tablet portrait/landscape QA remains part of the later frozen closure-candidate acceptance gate; CI does not substitute for that owner QA.
