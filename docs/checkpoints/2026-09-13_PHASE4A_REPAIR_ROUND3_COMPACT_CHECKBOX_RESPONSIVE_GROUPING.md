# Phase 4A — Repair Round 3: compact checkbox + responsive grouping

**Date:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Round source baseline:** `473dab45850dae20b8b5360bf5d6ea226171d292`  
**Committed migrated product source:** `582a809bafbc4d7d38836283ed0eb5fe33d94624`  
**Exact-source verification trigger:** `0a4d06b225fffed053565c43ad2fc385a6e7d898`  
**Physical baseline remains:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a`  
**Status:** IMPLEMENTATION COMMITTED / MIGRATED-WORKSPACE AUTOMATION GREEN / EXACT COMMITTED-SOURCE SCAFFOLD PENDING

## 1. Scope

Round 3 repairs the systemic Player checkbox/control-density and responsive-grouping family from phone findings 17.1–17.3 plus the tablet reproduction.

The round is intentionally UI/control/layout-only. Existing domain callbacks, persistence, source associations and other character state contracts remain unchanged. Material `Switch` controls are not mechanically replaced because the audit did not establish them as defective.

## 2. Source-complete audit

The earlier post-P17 audit identified 18 active raw Material `Checkbox` call sites across six Player files. The implementation-time source-complete scan found one additional legacy Gestión rest-preview selector, yielding the complete migration baseline of **19 raw Material Checkbox calls across seven Player files**:

- `CharacterEquipmentClosureV4.kt`;
- `CharacterSpellListClosureV4.kt`;
- `CharacterCompanionsModuleV4.kt`;
- `CharacterClassOptionModulesV4.kt`;
- `CharacterArtificeModuleV4.kt`;
- `CharacterManagementSuccessorV4.kt`;
- `CharacterManagementTabV4.kt`.

This is additional coverage of the same approved systemic family, not expanded product scope.

## 3. Shared repair contract

`CharacterCheckboxPrimitivesV4.kt` provides the shared Player control language:

- `CharacterCompactCheckboxItemV4` — compact labelled checkbox with whole-row toggle semantics;
- `CharacterCompactCheckboxV4` — compact icon-only form for rows whose adjacent content already supplies the label;
- `CharacterCompactCheckboxPairV4` — keeps semantically coupled checkbox pairs together;
- `CharacterResponsiveCheckboxGroupV4` — `FlowRow`-based responsive packing.

The visual Material checkbox is 24dp while interactive wrappers preserve at least a 48dp touch envelope. Labels use the shared compact typography/spacing language. Enabled/read-only behavior remains explicit.

## 4. Responsive repair

Equipment editor checkbox rows now use the shared responsive group for `Equipado` + `Especial` / `Equipo especial`, with `Sintonizado` using the same compact item primitive.

Conjuros now:

- keeps each source + `Preparado` pair semantically together;
- allows multiple source/prepared pairs to occupy one row when width permits;
- wraps only when required by available width;
- places V / S / M / Concentración / Ritual in one responsive group rather than rigidly forcing two rows.

The same component language is applied to the remaining audited Player checkbox sites. Existing `Switch` sites are left untouched absent evidence that they share the defect.

## 5. Durable guard

`scripts/check_player_checkbox_consistency.py` now fails if any raw Material `Checkbox` import/call exists outside the shared primitive in active Android Player Kotlin sources.

The guard also requires:

- all shared primitive variants and touch-size markers;
- responsive grouping in Equipment and Conjuros;
- semantic source/prepared pairing;
- expected V/S/M/Concentración/Ritual and Equipment labels;
- shared icon-only rest selectors in both current and legacy Gestión paths.

`scripts/apply_round3_checkbox_migration.py` is bounded to the exact audited 19-call baseline and becomes a no-op once migration is present.

## 6. Automation evidence so far

Scaffold run `34792939962` / run `1532` at setup HEAD `6f179ac6b6486606338038bef5b33fee23a84b16` completed **SUCCESS** after applying the bounded migration in its workspace:

- backend checks: SUCCESS;
- Round 3 migration: SUCCESS;
- compact Player geometry guard: SUCCESS;
- reorder stability guard: SUCCESS;
- Player checkbox consistency guard: SUCCESS;
- Kotlin/shared build and tests: SUCCESS;
- Android build: SUCCESS;
- debug APK upload: SUCCESS;
- verified migration commit step: SUCCESS, producing `582a809bafbc4d7d38836283ed0eb5fe33d94624`.

Because that run applied the migration inside the workflow before committing it, this checkpoint does **not yet** call Round 3 fully automation-green against the already-committed migrated source. A normal Scaffold run against a descendant whose checkout already contains the migration is required before final Round 3 closure.

## 7. QA status

Phone 17.1–17.3 and the tablet checkbox reproduction are **IMPLEMENTED / TARGETED PHYSICAL REVALIDATION PENDING**. They are not physically PASS yet.

No new physical candidate is frozen by this round. Accepted unrelated phone/tablet PASS evidence remains preserved and must not be replayed.

## 8. Next action

Run normal Scaffold against the committed migrated source with the migration step proving no-op. If green:

1. record the exact run/product HEAD here;
2. mark Round 3 COMPLETE / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING;
3. synchronize `docs/PROJECT_STATE.md`, `docs/checkpoints/LATEST.md`, and `docs/TESTING.md`;
4. proceed to the next dependency-aware repair family: T5 spell-source/bootstrap/source-context compatibility.

Phase 4A remains OPEN. No P18 exists. DM implementation remains blocked until explicit owner Phase 4A closure.
