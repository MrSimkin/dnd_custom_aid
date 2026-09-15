# Phase 4A — Repair Round 3: compact checkbox + responsive grouping

**Date:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Round source baseline:** `473dab45850dae20b8b5360bf5d6ea226171d292`  
**Committed migrated product source:** `582a809bafbc4d7d38836283ed0eb5fe33d94624`  
**Round product/test HEAD:** `1d1c476ddaeb045c8a1b186267f452010cad7681`  
**Physical baseline remains:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a`  
**Status:** ROUND 3 COMPLETE / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING

## 1. Scope

Round 3 repairs the systemic Player checkbox/control-density and responsive-grouping family from phone findings 17.1–17.3 plus the tablet reproduction.

The round is intentionally UI/control/layout-only. Existing domain callbacks, persistence, source associations and other character-state contracts remain unchanged.

The agreed related-toggle audit also covered Material `Switch` and `TriStateCheckbox` sites. Those controls were to be classified rather than blindly restyled.

## 2. Source-complete audit correction

The earlier post-P17 audit identified **at least 18** active raw Material `Checkbox` calls across six Player files. During implementation, the new package-wide guard found one additional active raw checkbox in `CharacterManagementTabV4.kt`, inside the legacy Gestión rest-preview selection flow.

Source inspection established that this seventh site has the same checkbox-selection semantics as the already-audited successor Gestión rest-preview selector; it is not a legitimate Switch/settings exception.

Therefore the source-complete Round 3 baseline is:

- **19 raw Material `Checkbox` calls**;
- across **7 Player Kotlin files**.

The seven migrated files are:

1. `CharacterEquipmentClosureV4.kt`;
2. `CharacterSpellListClosureV4.kt`;
3. `CharacterCompanionsModuleV4.kt`;
4. `CharacterClassOptionModulesV4.kt`;
5. `CharacterArtificeModuleV4.kt`;
6. `CharacterManagementSuccessorV4.kt`;
7. `CharacterManagementTabV4.kt`.

All 19 were migrated. Final package-wide automation reports **0 raw Material `Checkbox` calls outside the shared primitive**.

This is additional coverage of the same owner-approved systemic family, not expanded product scope and not a new product decision.

## 3. Shared repair contract

Commit `f392f92b1591d8f917be42ccc82f565c2b8797cb` introduced `CharacterCheckboxPrimitivesV4.kt` with:

- `CharacterCompactCheckboxItemV4` — compact labelled checkbox with whole-row toggle semantics;
- `CharacterCompactCheckboxV4` — compact icon-only form for rows whose adjacent content already supplies the label;
- `CharacterCompactCheckboxPairV4` — keeps semantically coupled checkbox pairs together;
- `CharacterResponsiveCheckboxGroupV4` — `FlowRow`-based responsive packing.

The visual Material checkbox is 24 dp while interactive wrappers preserve at least a 48 dp touch envelope. Labels use the shared compact typography/spacing language. Enabled/read-only behavior remains explicit.

Compact appearance therefore does **not** reduce the safe interaction target.

## 4. Responsive repair

Core migration commit:

`582a809bafbc4d7d38836283ed0eb5fe33d94624` — `fix: migrate Player checkbox controls`

### Equipment

Both active Equipment editor presentations now use the shared control language. `Equipado` + `Especial` / `Equipo especial` use the responsive group and `Sintonizado` uses the same compact labelled primitive.

### Conjuros

Conjuros now:

- keeps each source + `Preparado` pair semantically together;
- allows multiple source/prepared pairs to occupy one row when width permits;
- wraps only when required by available width;
- places V / S / M / Concentración / Ritual in one responsive group rather than rigidly forcing two rows;
- uses the shared compact primitive for the list-row `Prep.` control.

This directly implements phone 17.2/17.3: use one row when controls fit, exploit wider layouts, and wrap only when necessary rather than preserving hard-coded two-row/full-width grouping.

### Other Player checkbox sites

The same component language is applied to Companions, class options, artifice, and both Gestión rest-preview paths.

## 5. Related toggle audit — deliberate exceptions

The final package-wide audit reports:

- raw Material `Checkbox` outside the shared primitive: **0**;
- Material `Switch`: **11 sites**;
- `TriStateCheckbox`: **0 sites**.

The 11 remaining `Switch` sites were source-inspected. They are legitimate binary on/off state or settings controls, including:

- current binary resource state;
- Inspiration operational state;
- temporary-effect active state and its editor;
- spellcaster enablement;
- PC/settings visibility toggles;
- haptic-profile enablement;
- shared settings-switch rows.

They express binary state rather than checkbox membership/selection, so they are **intentional exceptions and remain `Switch` controls**. Converting them to checkboxes would weaken the semantic distinction rather than repair it.

There are no active `TriStateCheckbox` sites to migrate or exempt.

## 6. Durable guard / steady-state CI

`scripts/check_player_checkbox_consistency.py` is the permanent regression boundary. It fails if any raw Material `Checkbox` import/call exists outside the shared primitive in active Android Player Kotlin sources.

The guard also requires:

- all shared primitive variants and touch-size markers;
- responsive grouping in Equipment and Conjuros;
- semantic source/prepared pairing;
- expected V/S/M/Concentración/Ritual and Equipment labels;
- shared icon-only rest selectors in both current and legacy Gestión paths.

It also reports `Switch` / `TriStateCheckbox` locations informationally so the related-toggle audit remains visible without incorrectly treating legitimate switches as defects.

The temporary migration writer used to land the seven-file source conversion was retired after migration. The normal Scaffold no longer has source-write permission, no longer applies a migration during CI, and no longer self-commits. The one-off migration helper was removed. Only the read-only durable guard remains.

## 7. Automation evidence

### Useful superseded guard discovery

Scaffold `34792818885` / run `1529` stopped before Kotlin compilation because the first package-wide guard detected the previously unaudited `CharacterManagementTabV4.kt` raw checkbox. That run is retained as useful evidence of the source-audit expansion and is superseded by the corrected source-complete runs.

### Migration/workspace proof

Scaffold `34792939962` / run `1532` successfully applied the bounded **19 → 0** migration in its workspace, passed the guards and Kotlin/Android/Desktop build/tests, uploaded an APK, and only then committed the seven migrated source files as `582a809bafbc4d7d38836283ed0eb5fe33d94624`.

### Steady-state proof after migration machinery removal

Scaffold `34793215805` / run `1536` at `8455d8015e0bc6f7b4a6f813e56b03c5f9a2915c` completed **SUCCESS** after the temporary source writer/helper had been removed and the normal read-only workflow restored.

### Authoritative final toggle-audited proof

Final authoritative Round 3 run:

- Workflow: `Scaffold checks`;
- Run ID: `34793253151`;
- Run number: `1537`;
- Head SHA: `1d1c476ddaeb045c8a1b186267f452010cad7681`;
- Conclusion: **SUCCESS**;
- Backend/type-check: **SUCCESS**;
- Compact Player geometry guard: **SUCCESS**;
- Player reorder stability guard: **SUCCESS**;
- Player checkbox consistency guard: **SUCCESS**;
- checkbox guard result: `rawMaterialCheckboxes=0`, responsive spell/equipment packing present, shared Gestión selectors present;
- related-toggle audit: `Switch=11`, `TriStateCheckbox=0`;
- Kotlin/shared tests + Android/Desktop build: **SUCCESS**;
- Gradle result: `BUILD SUCCESSFUL`;
- Android debug APK upload: **SUCCESS**;
- artifact ID `10328739208` / `dnd-custom-aid-debug-apk`;
- artifact size `13,629,448` bytes;
- GitHub Actions artifact digest `sha256:03f529acfde7ad18e2aa08752a28822d3d7f9c45916e02ddf65932714d52b1bf`.

The artifact digest above is the GitHub Actions artifact digest; it is not relabelled as an independently computed APK-file SHA-256.

## 8. QA status after Round 3

Automation supports the intended systemic repair, but no new consolidated physical candidate has been frozen yet. Therefore:

- phone 17.1–17.3 are **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**;
- the tablet reproduction of the checkbox family is **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**;
- these findings are not physically PASS yet;
- accepted unrelated phone/tablet PASS evidence remains preserved and must not be replayed.

Targeted physical revalidation on the future consolidated candidate should include:

### Phone

- representative Equipment `Equipado` / `Equipo especial` / `Sintonizado` scale, label spacing and touch behavior;
- a second representative migrated checkbox surface, preferably Gestión rest selection or another migrated editor;
- Conjuros V/S/M + Concentración/Ritual packing in portrait and landscape;
- Conjuros source/prepared packing when T5 makes the required source context available.

### Tablet

- one representative migrated checkbox surface for 17.1 parity;
- Conjuros responsive packing in portrait and landscape, including source/prepared groups after T5 is repaired.

Do **not** replay the complete 23-phone / 18-tablet discovery suites.

## 9. Compatibility / non-goals

Round 3 changes UI control composition only. It does not intentionally alter:

- checkbox-backed domain values;
- storage/import/export/migration contracts;
- canonical state authority;
- existing source IDs/associations;
- legitimate Switch semantics;
- Table Mode policy;
- T5 spell-source/bootstrap behavior.

The repair does not claim every toggle should look identical. `Switch` remains the appropriate binary state/settings control where its semantics fit.

## 10. Project gate / next round

Phase 4A remains **OPEN**. The exact frozen physical candidate remains `preqa.12` until the consolidated repair receives a new monotonic QA identity. DM implementation remains blocked pending explicit owner Phase 4A closure. No P18 exists.

**Next repair family: T5 spell-source/bootstrap/source-context compatibility.**

Canonical character origins should drive source availability while preserving compatible spellcasting source/profile overlays, existing IDs/associations, persistence/import/export and manual/homebrew source behavior. After that bounded round, update the dedicated checkpoint + `PROJECT_STATE.md` + `LATEST.md` + `TESTING.md` before proceeding.
