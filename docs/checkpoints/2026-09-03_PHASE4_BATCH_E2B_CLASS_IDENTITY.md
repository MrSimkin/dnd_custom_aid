# Phase 4 — Batch E2b class/subclass/source identity checkpoint

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** PENDING INTEGRATION GATE  
**Canonical `main`:** untouched

## Scope

E2b replaces the dense inline class-field presentation in General with a compact identity summary plus a dedicated IME-safe detail editor.

Each class row now presents:

- class + optional subclass + level;
- rules family and source when known;
- remaining Hit Dice + die type;
- row tap as the primary edit affordance;
- explicit remove action.

The detail editor supports two independent identity paths:

1. **Official convenience metadata** from the audited `CharacterClassCatalog`;
2. **Manual / homebrew** identity with arbitrary class/subclass names, rules family and optional source.

Subclasses may be absent, selected from the official catalog for the selected official class, or entered manually/homebrew.

## Product boundary

The official catalog remains convenience metadata only:

- it does not validate character legality;
- it does not prohibit mixed 5e / 5.5e / homebrew characters;
- it does not enforce class/subclass level requirements;
- it does not enforce multiclass rules;
- arbitrary manual class/subclass values remain supported.

Hit Die behavior is also non-enforcing. When the class name has a known usual Hit Die, the editor shows a suggestion and an explicit `Aplicar dX` action. It never silently overwrites the stored value.

## Persistence boundary

No schema or repository migration was added. Existing `ClassLevelDraftV4` / `CharacterClassLevel` fields already preserve:

- `catalogKey`;
- `rulesFamily`;
- `source`;
- `subclassName`;
- `subclassCatalogKey`;
- `subclassRulesFamily`;
- `subclassSource`.

E2b only exposes and edits those existing fields. The draft type visibility was widened from file-private to package-internal so the isolated class-identity UI can reuse the existing authoritative draft rather than create a parallel model.

## Relevant integration

- bot integration commit `80c38218f72d2f85935ac100ade42db306824236` — `feat: integrate Batch E2b class identity editor`;
- `CharacterClassIdentityV4.kt` contains the new compact rows and detail editor;
- General now calls `CharacterClassIdentityCardV4` instead of the legacy dense `ClassesCardV4` surface;
- legacy private class UI helpers are temporarily retained but unused, avoiding unrelated cleanup inside the functional gate.

## Gate required

E2b is not GREEN until the controlling workflow on this checkpoint head passes:

- backend type-check;
- shared/Kotlin regression tests;
- Android debug compile/assembly;
- Desktop build;
- APK artifact upload.

If the new isolated editor exposes a Kotlin/Compose compatibility error, repair it before advancing to E3.
