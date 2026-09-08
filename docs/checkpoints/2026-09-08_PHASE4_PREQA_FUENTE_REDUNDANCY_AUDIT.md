# Phase 4 pre-QA — `Fuente` redundancy audit

**Date:** 2026-09-08  
**Status:** OWNER FINDING RECORDED; NO PRODUCT REPAIR STARTED  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Review identity:** `0.4.0-preqa.7` / build `40700` / `debug`

## Scope

Narrow follow-up to Stage F4. The owner asked whether editor windows broadly contain an excessive/redundant `Fuente` field. Existing global findings for padding, margins, cards, IME and landscape are intentionally not duplicated here.

## Audit result

Read-only implementation audit confirms that generic source/provenance fields are exposed across a broad family of editors, including Rasgos, Técnicas/Metamagia/Pactos, Artífice, Formas, Compañeros, Idiomas/competencias, Condiciones, Recursos, Efectos temporales, Defensas, and manual class/subclass identity.

The issue is therefore cross-cutting information architecture, not a single Rasgos defect.

## Owner-preferred repair direction

Do **not** simply keep a vague full-width `Fuente` textbox everywhere, and do **not** blindly delete all provenance either.

Prefer a compact **two-part origin model on one row** where provenance is useful:

1. **Tipo de origen / fuente** — for example `Clase`, `Dote`, `Pacto`, `Objeto`, `Raza`, `Trasfondo`, `Otro`, etc.;
2. **Origen / fuente específica** — select the actual class/dote/pact/item/etc., or enter/select a custom value when `Otro` applies.

Both controls should share one row at ordinary text scale when width allows, following the app-wide rule that information fitting clearly in one row should not be spread across several rows. High zoom/accessibility may wrap as needed.

This model should be reused only where the provenance has a concrete user-facing purpose. A backing-model `source` property by itself is not sufficient reason to expose a normal UI field.

## Why this strengthens the redundancy finding

A selectable origin category with an `Otro`/custom path can already represent origins that are not a class. Therefore a second generic free-text `Fuente` field often overlaps the same user intent instead of adding meaningful information.

The repair should consolidate those concepts into the structured two-part origin model instead of presenting parallel ambiguous fields.

## Important exception — Conjuros

The Conjuros source system is functional and must remain.

A spellcasting source is a named domain object used to organize and associate spells. It may be linked to a class or be completely custom / have no linked class. Spell-to-source associations drive actual filtering/prepared-state/list behavior.

Therefore:

- preserve spellcasting source objects and spell-source associations;
- allow custom/non-class spell sources;
- avoid adding a second generic provenance field when the same origin is already represented by the selected/custom spell source.

## Rasgos implication

Existing Stage F4 finding F-F19 (`Fuente` + `Tipo` perceived as duplicate) is strengthened: the repair should not merely relabel two ambiguous controls. It should determine whether Rasgos needs the same structured `Tipo de origen` + `Origen específico` model, or whether its existing `Tipo` is actually a different semantic axis. If both axes remain, their distinction must be concrete and obvious to the user.

## Data-model note

Existing `source` fields may remain in persistence for compatibility/future migration. This finding is about normal UI exposure and semantic structure, not an instruction to destructively remove stored data.
