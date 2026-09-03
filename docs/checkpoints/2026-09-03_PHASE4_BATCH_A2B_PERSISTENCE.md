# Phase 4 Closure — Batch A2b persistence checkpoint

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Canonical `main`:** untouched  
**Status:** **GREEN / COMPLETE** — full A2 schema/persistence gate closed; B1 may proceed

## Prerequisite

A2a is GREEN under corrected schema shape:

- schema code head `6f35997be2d11ce96b866a68c426a39671cd20ba`;
- workflow `33786927646`;
- conclusion PASS.

## A2b persistence approach

The additive schema-7 domains use a dedicated `CharacterClosureRepository` beside the existing `CharacterRepository`.

Reason:

- the existing core repository is already extensively tested and rewrites several existing child collections transactionally;
- closure state can remain additive without destabilizing that mature save/load path;
- Android closure UI can coordinate the two repositories while shared persistence boundaries remain clear;
- this is a reversible/proportionate implementation choice and does not alter approved product semantics.

`CharacterClosureRepository` owns:

- safe default closure state when no settings row exists;
- transactional save/load of schema-7 closure domains;
- input validation for nonnegative/scoped values and identity uniqueness;
- soft-reference normalization for resource recovery, inventory usage and concentration spell links;
- module override persistence independent of module data;
- generic Quick Access persistence.

## Rewrite-safety behavior

Existing `CharacterRepository.saveCharacter()` deletes/reinserts resource, inventory and spell rows using stable UUIDs. Closure metadata therefore uses scoped soft UUID references rather than child-row cascading FKs.

On save/load:

- recovery metadata is exposed only while the referenced resource exists;
- consumable/ammunition metadata is exposed only while the referenced inventory item exists;
- a concentration spell UUID is retained only while that spell exists;
- concentration's human-readable name remains even if the spell record disappears;
- Quick Access remains a permissive soft-reference mechanism because future/custom targets are valid product behavior.

## Initial compile failure and diagnosis

Initial persistence commit:

- `3cff87901a12825bf071b4d89c935634594a6aa7`.

CI run:

- `33787386116`;
- backend PASS;
- Kotlin job FAILED during shared compilation.

Diagnosis:

SQLDelight query mapper generics require each mapped SQL row to produce a non-null result. The first implementation tried to return nullable domain objects directly from four query mappers when filtering invalid/stale soft references:

- Resource recovery;
- Inventory usage;
- Module overrides;
- Quick Access.

This produced Kotlin type-inference/return-type errors. It was not a schema/migration failure; A2a's SQLDelight schema had already passed its full gate.

## Compile repair

Repair commit:

- `7f63e6be2ccdbb584152057a75887a2f74a658e9`.

The repaired implementation:

1. maps each SQLDelight row to a small non-null raw record;
2. executes the SQL query;
3. performs `mapNotNull` normalization in ordinary Kotlin afterward.

This preserves the intended stale-reference filtering without weakening schema constraints or domain validation.

## Regression suite

Test commit:

- `ee0cdeae9341a3818fe0e704eb543832d7c214c4`.

`CharacterClosureRepositoryTest` covers:

- safe defaults for a newly created character;
- rich closure-state round trip;
- Conditions, Defenses, Movement, Senses, Concentration, progress, Table mode, haptics, portrait/token, custom skills, temporary effects, module overrides and Quick Access;
- Resource recovery and Inventory usage;
- regression proving Resource/Inventory/Concentration metadata survives an ordinary core `CharacterRepository.saveCharacter()` rewrite;
- dangling resource/item references disappear from hydrated closure state after genuine deletion;
- deleted concentration spell clears only its UUID link and keeps readable identity;
- FORCE_HIDE module state does not delete existing Forms/module data;
- permissive custom/OTHER Quick Access target;
- additive schema-7 migration creates closure tables while preserving the pre-existing character row.

## Final A2 verification

Controlling repaired run:

- workflow `33787986897`;
- tested head `7f63e6be2ccdbb584152057a75887a2f74a658e9`;
- this head includes regression-test commit `ee0cdeae9341a3818fe0e704eb543832d7c214c4`;
- backend type-check: PASS;
- shared Kotlin compilation/tests: PASS;
- SQLDelight generation/compilation: PASS;
- Android debug assembly: PASS;
- Desktop build: PASS;
- debug APK artifact upload: PASS.

**Full Batch A2 is GREEN.** Schema 7 and its persistence boundary are now the verified foundation for the Android closure work.

## Exact next action

Begin **Batch B1 — global IME/editor/action foundation**. Establish one reusable IME-aware editor/dialog pattern and consistent action/destructive/validation primitives, then migrate existing character editors incrementally. Do not begin B2 until B1 receives its own checkpoint/gate.
