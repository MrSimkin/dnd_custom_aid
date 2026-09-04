# Phase 4 — Batch K closure candidate stabilization

**Date:** 2026-09-04  
**Status:** GREEN  
**Safety branch:** `tmp/phase4-k-stabilization`  
**Canonical `main`:** untouched

## Scope closed

Batch K was stabilization only. No unrelated feature work and no production migration repair were required.

The missing stabilization evidence was an explicit end-to-end migration regression from the database lineage represented by the previously identified owner-QA APK to the current closure schema. That regression is now durable.

## Owner APK lineage proof

The preserved pre-closure QA branch `tmp/increment-l-final-regression-qa-target` identifies the prior owner-phone-QA APK as:

- tested commit `089a991c6491627961f1e75f3815959a8a1c8b48`;
- workflow `33468310534`;
- artifact `9785676981`;
- artifact digest `sha256:4836f5b1fe1b9ae8cb11bdb6b61231782a2a474377afb4f9e27a347288d0f194`.

That preserved branch contains SQLDelight migration files `1.sqm` through `4.sqm`, so the installed owner-QA lineage is schema version **5**. The current closure line contains migrations `1.sqm` through `8.sqm`, so the current generated schema is version **9**.

Therefore the required owner upgrade path is schema **5 -> 9**, exercising migrations `5.sqm`, `6.sqm`, `7.sqm` and `8.sqm` in order.

## New migration regression

`CharacterOwnerLineageMigrationTest.kt` reproduces the historical schema-5 table shape from the preserved owner-QA branch rather than approximating it.

The fixture seeds representative protected data across:

- Campaign and active campaign state;
- character core/derived compatibility fields;
- class;
- saving throw;
- skill/training;
- spell slots;
- Combat entry;
- Equipment item and currency;
- Trasfondo;
- Rasgo;
- titled Nota;
- spellcasting source;
- conceptual spell;
- source association and Prepared state.

It then runs `AppDatabase.Schema.migrate(oldVersion = 5, newVersion = AppDatabase.Schema.version)` and reopens the result through the current `CampaignRepository`, `CharacterRepository` and `CharacterClosureRepository`.

Assertions prove:

- all seeded pre-closure data survives;
- Background receives safe empty `race` and `religionFaith` defaults;
- migrated class provenance/subclass fields receive non-guessing defaults;
- migrated Combat/Trait/Spell pinned state defaults to false;
- Inspiration and death-save counters default safely;
- schema-6 collections start empty;
- closure-era state is exactly the current safe default `CharacterClosureState()`;
- migration 8 participates in the chain without requiring any historical closure rows.

No existing migration file was modified.

## Focused migration gate

Guarded workflow:

`33886853307`

Result: **PASS**

The historical fixture generated successfully, the focused `CharacterOwnerLineageMigrationTest` passed, and only then the workflow committed the accepted regression and removed its staging helpers.

Accepted test commit:

`5030a0ed03df4ae92e6de312b1951b7f364c40d7`

Accepted exact tree:

`bcd22883c7a08d4c59394d799336f664137f1961`

## Controlling exact-clean Batch K gate

Validation branch:

`tmp/gate-k-exact`

Workflow:

`33887059005`

Exact tested commit:

`5030a0ed03df4ae92e6de312b1951b7f364c40d7`

Exact tested tree:

`bcd22883c7a08d4c59394d799336f664137f1961`

Result: **PASS**

Verified together on that exact clean tree:

- owner schema-5 -> current migration regression — PASS as part of full shared tests;
- all shared/Kotlin tests — PASS;
- SQLDelight generated/migration surfaces — PASS;
- Android debug assemble — PASS;
- Desktop build — PASS;
- backend dependency install — PASS;
- backend Worker type-check — PASS;
- Android debug APK upload — PASS.

No cross-tab or production integration defect was demonstrated by the stabilization gate, therefore Batch K correctly made no production-code repair.

## K technical artifact

- artifact ID: `9942413356`;
- name: `dnd-custom-aid-debug-apk`;
- size: `12498598` bytes;
- GitHub artifact ZIP digest: `sha256:dde2ebdfc5f82ce2d2623c8bbd6fc52a00bdc609d90abeed539c5f8c830212ce`.

This is Batch K technical evidence only. It is **not yet** the frozen owner-QA candidate required by Batch L.

## Batch boundary

Batch K is complete and GREEN.

The next approved work is **Batch L — frozen phone+tablet APK candidate**. Batch L must make no feature change. It must freeze one exact candidate identity, run the full standard gate on that exact candidate commit, capture the workflow/artifact identity plus ZIP and extracted APK hashes, and then prohibit silent code changes once owner QA begins.

`main` remains untouched. DM feature implementation remains blocked until Batch M owner phone+tablet QA is accepted and the owner explicitly approves Phase 4 closure/merge.
