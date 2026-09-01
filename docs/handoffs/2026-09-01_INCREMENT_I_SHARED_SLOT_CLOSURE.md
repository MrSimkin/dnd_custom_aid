# Increment I — Quick Magic / Conjuros shared-slot integration closure

**Date:** 2026-09-01  
**Recovery branch:** `tmp/increment-i-shared-slot-integration`  
**Baseline:** `bf8d5d4af8f750edd061d3589af1b6339fd8e8ee` — Increment H closed  
**Gate I tested head:** `2c79bde3dcee35c2c67d109a25cbb08fee23665b`

## Result

Increment I is technically complete.

Quick Magic and `Conjuros` now operate on one authoritative spell-slot state. No duplicate persistence model or slot cache was introduced.

## Implemented behavior

- Quick Magic remains the sole place to configure slot totals and the primary manual spellcasting profile for:
  - spell save DC;
  - spell attack modifier;
  - spellcasting ability.
- `Conjuros` level headers for levels 1–9 show compact spent/unspent slot pips when that level has configured slots.
- cantrips do not show slot controls;
- tapping a Conjuros slot pip changes the spent count on the same `CharacterEditorDraftV4.spellSlots` record used by Quick Magic;
- returning to Quick Magic immediately reflects a Conjuros spent-count change because both surfaces render from the same editor draft;
- Quick Magic slot changes likewise feed the Conjuros projection on recomposition;
- Conjuros slot controls remain visible across `Todos` and individual source views because the slot pool is character-wide, not source-owned;
- large/manual slot totals use a compact horizontally scrollable pip strip rather than assuming official class progression limits;
- `Lanzador de conjuros = OFF` continues to hide Quick Magic and `Conjuros` without deleting slot data.

## Data ownership

No schema or migration change was required.

Persisted authority remains the existing `CharacterSpellSlot(level, totalSlots, spentSlots)` collection on `CharacterSheet`.

The new `CharacterSpellSlotUiV4` type is an Android UI-only projection. It is deliberately not persisted and is not added to `CharacterSpellcastingDraftV4`.

## Regression coverage

`CharacterSpellSlotIntegrationTest` verifies:

1. an initial Quick-Magic-style update persists slot totals/spent state;
2. a Conjuros-style update changes the spent count on those same records;
3. a later Quick-Magic-style read/update observes that changed state and can restore it;
4. disabling `spellcasterEnabled` preserves the slot collection unchanged;
5. re-enabling spellcasting restores visibility over the same preserved data.

The Android build additionally compiles the concrete shared-draft callback wiring between the two UI surfaces.

## Source-change safety

Because `CharacterEditorV4.kt` is large and has a historical truncation incident, Increment I used an asserted narrow patch. Every expected source seam had to match exactly once or the patch would abort.

Final source diff from the H baseline includes only:

- `CharacterEditorV4.kt`: 17 added wiring lines;
- `CharacterSpellsTabV4.kt`: 4 added callback/projection lines;
- `CharacterSpellListV4.kt`: focused slot/header wiring;
- new `CharacterSpellSlotsV4.kt` UI projection/control;
- new shared regression test;
- Increment I checkpoint/handoff documentation.

Temporary patch workflow/script files were removed in the same source-wiring commit and are not part of the candidate.

## Gate I evidence

GitHub Actions `Scaffold checks` run **`33465230273`** passed on tested head `2c79bde3dcee35c2c67d109a25cbb08fee23665b`:

- backend install/type check: **PASS**;
- shared desktop tests: **PASS**;
- Increment I shared-slot regression: **PASS** as part of the shared suite;
- Android debug build: **PASS**;
- desktop build: **PASS**;
- Android debug APK upload: **PASS**.

APK artifact:

- ID: `9784647947`
- name: `dnd-custom-aid-debug-apk`
- digest: `sha256:a4ee65b6ed075e0b8b0d5bea3805fe37679ec589492acad540c8134befd041eb`

## Manual acceptance boundary

Automated Gate I is green. Intended-device Android phone QA remains separate under C-0010. In particular, the owner has not yet manually verified the new Conjuros slot pip density/tap ergonomics or switching between Quick Magic and Conjuros on the produced APK.

Do not describe Increment I as manually accepted until that QA is performed or explicitly waived/accepted by the owner.

## Promotion boundary

This closure may be fast-forwarded to `implementation/character-data-foundation` because it is a descendant-only continuation of the existing Phase 4 branch.

It must not be merged to `main` without explicit owner approval.

## Next implementation boundary

**Increment J — Notas tab.**

Implement the already-approved hybrid notes model: one prominent unrestricted `Notas generales` area plus optional ordered titled note cards (title + content only), with add/edit/delete, drag ordering, persistence/recreation support, IME-safe editing, and proportional wide-layout presentation.