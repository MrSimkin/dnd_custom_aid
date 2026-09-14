# Phase 4A — Repair Round 3: compact checkbox + responsive grouping

**Date:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Round source baseline:** `473dab45850dae20b8b5360bf5d6ea226171d292`  
**Committed migrated product source:** `582a809bafbc4d7d38836283ed0eb5fe33d94624`  
**Round product/test HEAD:** `8455d8015e0bc6f7b4a6f813e56b03c5f9a2915c`  
**Physical baseline remains:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a`  
**Status:** ROUND 3 COMPLETE / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING

## 1. Scope

Round 3 repairs the systemic Player checkbox/control-density and responsive-grouping family from phone findings 17.1–17.3 plus the tablet reproduction.

The round is intentionally UI/control/layout-only. Existing domain callbacks, persistence, source associations and other character-state contracts remain unchanged. Material `Switch` controls were audited as a related toggle family but were not mechanically replaced because the source/physical evidence did not establish them as defective.

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

The same component language is applied to the remaining audited Player checkbox sites, including both Gestión rest-preview paths.

## 5. Durable guard / steady-state CI

`scripts/check_player_checkbox_consistency.py` is the permanent regression boundary. It fails if any raw Material `Checkbox` import/call exists outside the shared primitive in active Android Player Kotlin sources.

The guard also requires:

- all shared primitive variants and touch-size markers;
- responsive grouping in Equipment and Conjuros;
- semantic source/prepared pairing;
- expected V/S/M/Concentración/Ritual and Equipment labels;
- shared icon-only rest selectors in both current and legacy Gestión paths.

The temporary migration writer used to land the seven-file source conversion was retired after the migration was committed. The normal Scaffold no longer has source-write permission, no longer applies a migration during CI, and no longer self-commits. The one-off migration helper was also removed. Only the read-only durable guard remains.

## 6. Automation evidence

### Migration/workspace proof

Scaffold `34792939962` / run `1532` at setup HEAD `6f179ac6b6486606338038bef5b33fee23a84b16` completed **SUCCESS** after applying the bounded migration in its workspace and committing verified migrated source as `582a809bafbc4d7d38836283ed0eb5fe33d94624`.

### Exact committed-source proof

Scaffold `34793141844` / run `1533` at `0a4d06b225fffed053565c43ad2fc385a6e7d898` completed **SUCCESS** against already-committed migrated source.

Its logs explicitly prove:

- migration helper reported `Round 3 checkbox migration already applied; no changes required.`;
- compact Player geometry guard: PASS;
- reorder stability guard: PASS;
- checkbox consistency guard: PASS with `rawMaterialCheckboxes=0`, `sharedItemReferences=18`, responsive spell/equipment packing and shared Gestión selectors;
- Kotlin/shared tests + Android/Desktop build: `BUILD SUCCESSFUL`;
- Android debug APK upload: SUCCESS;
- migration commit step found `No source migration diff to commit.`

### Authoritative steady-state proof

After retiring the temporary writer/helper, normal Scaffold `34793215805` / run `1536` at **Round product/test HEAD `8455d8015e0bc6f7b4a6f813e56b03c5f9a2915c`** completed **SUCCESS**.

- backend/type-check: SUCCESS;
- compact Player geometry guard: SUCCESS;
- reorder stability guard: SUCCESS;
- permanent Player checkbox consistency guard: SUCCESS;
- Kotlin/shared tests + Android/Desktop build: SUCCESS;
- Android debug APK upload: SUCCESS;
- artifact ID `10327879251` / `dnd-custom-aid-debug-apk`;
- artifact size `13,629,448` bytes;
- GitHub Actions artifact digest `sha256:2af0eed7f7cb1de625d670beb6ddc2b4681dbf57d63b3e1a541f13d2a94c7b34`.

The artifact digest above is the GitHub Actions artifact digest; it is not relabeled as an independently computed APK-file SHA-256.

## 7. QA status after Round 3

Automation supports the intended systemic repair, but no new consolidated physical candidate has been frozen yet. Therefore:

- phone 17.1–17.3 are **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**;
- the tablet reproduction of the checkbox family is **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**;
- these findings are not physically PASS yet;
- accepted unrelated phone/tablet PASS evidence remains preserved and must not be replayed.

Targeted physical revalidation on the future consolidated candidate should include representative Equipment checkbox styling/spacing and Conjuros responsive grouping in portrait and landscape/wide layout, including source/prepared pairs and V/S/M + Concentración/Ritual.

## 8. Project gate / next round

Phase 4A remains **OPEN**. The exact frozen physical candidate remains `preqa.12` until the consolidated repair receives a new monotonic QA identity. DM implementation remains blocked pending explicit owner Phase 4A closure. No P18 exists.

**Next repair family: T5 spell-source/bootstrap/source-context compatibility.**

Canonical character origins should drive source availability while preserving compatible spellcasting source/profile overlays, existing IDs/associations, persistence/import/export and manual/homebrew source behavior. After that bounded round, update the dedicated checkpoint + `PROJECT_STATE.md` + `LATEST.md` + `TESTING.md` before proceeding.
