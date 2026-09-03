# Phase 4 Closure — Batch A2a schema/domain checkpoint

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Canonical `main`:** untouched  
**Status:** **GREEN / COMPLETE** — A2b repository integration may proceed

## Completed prerequisite

Batch A1 is GREEN:

- tested head `dd6f50afebe862222861ee8ccb39cfe99ee82df1`;
- workflow `33785858196`;
- conclusion: PASS.

## A2a implementation

Created `CharacterClosureDomain.kt` with durable domain types for:

- Conditions;
- Defenses (resistance/immunity/vulnerability);
- special movement;
- senses;
- Concentration;
- generic Resource recovery cadence/amount metadata;
- consumable/ammunition quick-use metadata;
- reconciliation checkpoints;
- custom skills linked to an ability/training state;
- temporary effects;
- module override state;
- generic Quick Access references;
- per-character closure settings for Exhaustion, portrait/token references, XP/Milestone mode, Table mode and haptic preference.

Created `CharacterClosure.sq` defining the current-schema tables and SQLDelight queries for those domains.

Created additive migration `7.sqm` with the same new tables. **No schema-6 migration or existing stable table definition was rewritten.**

## Relational shape

Character-wide scalar state uses one one-to-one table:

```sql
character_closure_settings(character_id PK/FK, ...)
```

Repeatable records use child tables with `character_id` and `ON DELETE CASCADE`, for example:

```sql
character_condition(id PK, character_id FK, name, source, notes, sort_order)
character_defense(id PK, character_id FK, defense_type, name, source, notes, sort_order)
character_custom_skill(id PK, character_id FK, name, ability_key, training, adjustment, ...)
```

The existing `CharacterRepository.saveCharacter()` rewrites inventory/resources by delete-and-reinsert. A2a therefore intentionally uses **soft UUID references** from closure metadata to those child rows, scoped by character, rather than `ON DELETE CASCADE` foreign keys to the rewritten rows:

```sql
character_resource_recovery(
    character_id FK,
    resource_id,
    recovery_cadence,
    amount_mode,
    fixed_amount,
    notes,
    PRIMARY KEY(character_id, resource_id)
)

character_inventory_usage(
    character_id FK,
    item_id,
    consumable_kind,
    quick_use_amount,
    PRIMARY KEY(character_id, item_id)
)
```

This preserves recovery/consumable metadata across an ordinary character save. A2b repository hydration must filter genuinely dangling resource/item references rather than allowing stale metadata into the domain model.

The existing `character_inventory_item.location` field remains the intentional location/container label. A duplicate container field was not introduced.

Quick Access uses a generic soft-reference table rather than adding a new favorite column to every future domain:

```sql
character_quick_access(character_id, target_kind, target_id, sort_order)
```

Existing schema-6 pinned fields remain untouched for compatibility. A2b/future UI integration must preserve them and may bridge them into the generic Quick Access projection without destructive migration.

## Important semantics

- The app remains permissive; none of these tables enforce D&D legality.
- Exhaustion is stored as a nonnegative integer rather than embedding an edition-specific maximum in the database.
- Concentration may soft-link to a spell ID but also stores a human-readable name; deleting/changing a spell must not corrupt the character.
- Rest recovery metadata describes what a user-configured resource proposes to recover; actual Rest actions remain preview + selective apply in Batch D.
- Portrait/token fields are generic local references, not a commitment to cloud/object-storage architecture.
- Module override modes are `AUTO`, `FORCE_SHOW`, `FORCE_HIDE`; hiding a module does not delete its underlying module data.
- Table mode and haptic preference are character settings because the approved entry/configuration lives in PC Settings.

## Commits

Initial A2a:

- `66e27be537657b0b2f93a3630887a12b96ea0739` — closure domain types;
- `2f8d6ae67352f0ea1d538b8659bdd5150129f994` — current SQLDelight closure schema/queries;
- `5327d293e3f5c471849b13b952dbb3b9a9b7d8a7` — additive schema-7 migration.

Rewrite-safety correction after repository audit:

- `b03de330340096aa55bebb167dfff8ad04634fd8` — closure SQL extension references changed to soft scoped UUID references;
- `6f35997be2d11ce96b866a68c426a39671cd20ba` — schema-7 migration updated to match.

## Verification

Corrected A2a run:

- workflow `33786927646`;
- tested code head `6f35997be2d11ce96b866a68c426a39671cd20ba`;
- final conclusion: **PASS**;
- backend PASS;
- shared Kotlin/tests PASS;
- SQLDelight generation/compilation PASS;
- Android debug assembly PASS;
- Desktop build PASS;
- debug APK artifact upload PASS.

An earlier pre-rewrite-safety A2a run `33786719634` also passed, but `33786927646` is the controlling A2a evidence because it includes the corrected schema shape.

## Exact next action

Begin **A2b repository integration** with explicit dangling-reference filtering and a regression test proving inventory/resource metadata survives an ordinary `CharacterRepository.saveCharacter()` rewrite. Then run the complete A2 gate before beginning B1.
