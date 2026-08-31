# Follow-up implementation checkpoint — Step B persistence foundation

**Status:** COMPLETE / CI PASS  
**Date:** 2026-08-31  
**Branch:** `implementation/character-data-foundation`

## Purpose

This checkpoint closes the first production implementation increment after the owner approved the follow-up character-sheet design package.

No intended Android presentation/UX changes were made in this increment. The goal was to establish the shared Kotlin domain and SQLDelight persistence/migration foundation before UI work.

## Implementation commits in this increment

- `7986dad04ea11e2b7b5efa815004fdc887bbd2d4` — Add follow-up character domain foundation
- `ce698b1bd7de6bf8272a4f98b813039242671661` — Extend character persistence schema
- `51a5d1f94e5d2035298a19a14d2e15da081b7bd8` — Add follow-up character database migration
- `c6b50d07136326cfe54fa9b1cb4a3a12d3e2c2fe` — Persist follow-up character domains transactionally
- `c8a221148f59109a9078c162d4ad00e209c2779c` — Test follow-up character persistence foundation

## Shared-domain foundation now present

The shared character model now includes durable concepts for:

- proficiency-bonus adjustment and standard/final proficiency arithmetic;
- approved level-0 proficiency default (+2 for application purposes);
- Quick Magic spell slots, spell attack modifier and spellcasting ability;
- reusable combat entries;
- inventory items with quantity, per-unit `weightLb`, equipped state, special-item detail, optional location and Sintonización state;
- per-character currencies;
- carried-weight total using `quantity × per-unit weight`;
- informational attuned-item count.

Class level `0` is now accepted by repository validation as approved incomplete/permissive character data.

## Transitional proficiency compatibility bridge

The current V4 Android editor still edits a displayed/manual proficiency-bonus value. To avoid forcing UI work into the persistence checkpoint, `CharacterSheet.proficiencyBonus` temporarily remains as a compatibility snapshot.

Persistence is already adjustment-authoritative:

1. standard PB is calculated from total class level;
2. the compatibility final value is converted to `proficiency_bonus_adjustment` when necessary;
3. hydration reconstructs the displayed final PB from standard + stored adjustment;
4. skills and saving throws use the derived final proficiency bonus.

The next Android/UI increment must remove the old always-manual proficiency interaction and use the approved progressive-disclosure adjustment editor. The compatibility bridge can then be simplified/removed when source compatibility is no longer needed.

## SQLDelight migration

New migration:

- `shared/src/commonMain/sqldelight/io/github/mrsimkin/dndcustomaid/shared/db/3.sqm`

It:

- adds `proficiency_bonus_adjustment`;
- preserves each V4 displayed proficiency bonus by converting the difference from the calculated standard value into the adjustment;
- adds Quick Magic fields/tables;
- adds combat-entry and inventory tables;
- adds per-character currency storage;
- seeds Cobre / Plata / Electro / Oro / Platino independently for every existing PC;
- leaves new combat/inventory/spell-slot collections empty for migrated PCs;
- preserves existing spell save DC.

## Verification

GitHub Actions workflow: `Scaffold checks`

- run number: **154**
- run ID: `33430536737`
- head commit: `c8a221148f59109a9078c162d4ad00e209c2779c`
- Kotlin job: **success**
- `Build and test Kotlin surfaces`: **success**
- Android debug APK assembly: **success**
- desktop build/tests: **success**
- backend job: **success**

Focused tests added in `CharacterFollowupFoundationTest.kt` cover:

- level-0 and normal proficiency progression;
- independent default currencies for each PC;
- level-0 class persistence;
- preservation of unusual proficiency totals through adjustment;
- Quick Magic persistence;
- combat-entry persistence/order;
- inventory/special-item persistence;
- per-unit weight arithmetic;
- Sintonización count/location;
- per-PC custom-currency isolation;
- direct V4 -> follow-up migration preserving proficiency bonus and spell save DC while seeding standard currencies.

## Recovery / next step

If work is interrupted, resume from this checkpoint and the approved package:

- `docs/handoffs/2026-08-31_FOLLOWUP_IMPLEMENTATION_APPROVAL.md`
- `docs/handoffs/2026-08-31_FOLLOWUP_IMPLEMENTATION_REVIEW_PACKAGE.md`

Next implementation increment: Android character-editor integration and visible sheet behavior. Start with the existing/follow-up `General` + derived-value/numeric-editing foundation before layering the new `Combate` and `Equipo` tabs.

Do not skip directly to owner phone QA until the visible follow-up target is internally coherent and has its own CI/checkpoint.
