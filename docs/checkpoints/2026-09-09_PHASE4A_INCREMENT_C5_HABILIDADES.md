# Phase 4A successor cycle — Increment C5 Habilidades

**Date:** 2026-09-09  
**Branch:** `implementation/phase4a-successor-cycle`  
**Status:** COMPLETE / AUTOMATED GATE GREEN

## Scope

This checkpoint covers successor-plan step **C5 — Habilidades**.

Owner/device visual acceptance is **not** claimed by this checkpoint.

## Implemented

### One integrated skill presentation

A shared successor presentation projection now merges:

- all 18 built-in skills;
- character custom skills;
- successor generalized ability references, including custom attributes.

`Por habilidades` uses Spanish display labels for alphabetical ordering without rewriting stored manual/custom order.

### Owner terminology

- `Arcana` / `Arcanos` is presented as **`Conocimiento Arcano`**.
- `Trato con animales` uses normal Spanish sentence casing.

### Custom skills are no longer a duplicate Habilidades editor

The transitional separate `Habilidades personalizadas` card/editor is removed from the Habilidades composition.

Custom skills now appear inline with ordinary skills:

- in `Por habilidades`, interleaved alphabetically with built-in skills;
- in `Por característica`, inside the built-in or custom attribute to which the successor mapping points;
- with italic label styling so custom/homebrew entries remain visually distinguishable without a separate box.

Their structural configuration remains owned by **Ajustes del PJ**, where name, training, adjustment and generalized built-in/custom-attribute assignment are already managed. Habilidades projects the resulting live total and training state without exposing a second editor or the old generic `Fuente` field.

The legacy `CharacterCustomSkillsCardV4` implementation file remains present but is no longer called from Habilidades; it can be removed during later cleanup without changing current behavior.

### Custom-attribute grouping

When a custom skill references a custom attribute, `Por característica` creates a compact group for that attribute showing:

- name and abbreviation;
- score and modifier;
- optional saving-throw total when that custom attribute has saving throws enabled;
- the related custom skills and their successor-aware totals.

No parallel custom-attribute state is created in Habilidades.

### Passive reference strip

The fixed passive-reference card is compacted into one horizontal row and explicitly labels all three values as passive:

- `Per. pasiva`;
- `Persp. pasiva`;
- `Inv. pasiva`.

This preserves the owner-approved sticky reference while reducing permanent vertical footprint.

## Shared projection and focused tests

New shared file:

- `CharacterSkillsPresentation.kt`

It provides:

- canonical Spanish built-in skill labels;
- one merged built-in/custom presentation row model;
- alphabetical Spanish projection using the existing normalized presentation ordering semantics;
- generalized ability abbreviation resolution for built-in and custom attributes.

Focused tests cover:

- exact `Conocimiento Arcano` terminology;
- alphabetical interleaving of a custom skill among built-in Spanish labels;
- successor custom-attribute mapping and abbreviation resolution.

## Source evidence

- shared projection commit: `286bfb20521134fff9f658e7e0df1a5a6665b9ae`;
- focused-test commit: `a8640882361fb9fb4ffb84b80b04f933351e5f7a`;
- compact passive strip commit: `b243ab48b2b81e806568fe413dcb0988971c9805`;
- guarded editor integration source commit: `3c55a0cb3e37a4847087bbfa347737e900a6264c`;
- editor integration workflow `34301658611`: SUCCESS;
- final italic-plan correction source commit: `acda3c0f67ec063d8fbf495054a951bc55b9c516`;
- italic correction workflow `34302186976`: SUCCESS;
- exact guards and `git diff --check`: PASS;
- all temporary patch files/workflows removed by their source commits.

## Final automated gate

Validation descendant:

`cf372a26c260e65a3b01ef3634e310c74fd99fe3`

Workflow:

`34302249483` — SUCCESS

Verified together:

- backend check: PASS;
- shared/Kotlin tests: PASS;
- Android debug compilation/assembly: PASS;
- desktop compilation/build: PASS;
- Android debug APK artifact upload: PASS.

CI artifact:

- artifact ID `10085382195`;
- artifact name `dnd-custom-aid-debug-apk`;
- ZIP digest `sha256:a8b3d94a2f93418d81d38179e03b6ce22889bd2966676abacfd99966bf975573`.

## Outcome

C5 is technically complete and Increment C's navigation/settings/General/Habilidades family is automated-gate green.

The successor cycle now advances to **Increment D — Combate + Dados**, using the already-persisted structured attack-damage profiles and one generalized character-aware roll target engine.
