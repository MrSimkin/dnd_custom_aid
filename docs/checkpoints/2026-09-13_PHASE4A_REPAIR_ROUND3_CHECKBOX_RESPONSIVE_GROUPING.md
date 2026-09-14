# Phase 4A — Repair Round 3: shared compact checkbox + responsive grouping

**Date:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Round baseline HEAD:** `473dab45850dae20b8b5360bf5d6ea226171d292`  
**Core product migration commit:** `582a809bafbc4d7d38836283ed0eb5fe33d94624`  
**Round product/test HEAD:** `1d1c476ddaeb045c8a1b186267f452010cad7681`  
**Physical baseline remains:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a`  
**Status:** ROUND 3 COMPLETE / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION DEFERRED TO CONSOLIDATED CANDIDATE

## 1. Scope

This round repairs the systemic Player checkbox/control-density and responsive-grouping family established by phone findings 17.1–17.3 and reproduced during P17 tablet QA.

The round covers:

- one shared compact/touch-safe Player checkbox language;
- migration of active raw Material `Checkbox` sites across the Android Player package;
- responsive packing for Equipment and Conjuros checkbox groups;
- semantic grouping of Conjuros source/prepared pairs;
- package-wide source protection against raw Material `Checkbox` reintroduction;
- explicit audit/classification of related `Switch` / `TriStateCheckbox` sites rather than blindly restyling all toggles.

No new physical candidate is frozen by this round, so phone 17.1–17.3 and the tablet reproduction are **not** marked physically PASS.

## 2. Source-audit correction discovered during implementation

The pre-round post-P17 audit had correctly identified **at least 18** active raw Material `Checkbox` calls across six known Player files.

The new package-wide guard found one additional active raw `Checkbox` in `CharacterManagementTabV4.kt`, inside the legacy/rest-preview selection flow. Source inspection showed that this seventh site has the same checkbox-selection semantics as the already-audited successor Management rest-preview selector; it is not a legitimate Switch/settings exception.

Therefore the source-complete Round 3 baseline is:

- **19 raw Material `Checkbox` calls**;
- across **7 Player Kotlin files**.

The round migrated all 19 and the final package-wide guard reports **0 raw Material `Checkbox` calls outside the shared primitive**.

This expands the implementation scope without changing the owner-approved product requirement; no additional owner decision was required.

## 3. Shared checkbox primitive

Commit `f392f92b1591d8f917be42ccc82f565c2b8797cb` — `feat: add shared compact Player checkbox primitives`

Added `CharacterCheckboxPrimitivesV4.kt` with:

- `CharacterCompactCheckboxItemV4` — labelled whole-row checkbox;
- `CharacterCompactCheckboxV4` — icon-only selection variant for rows whose adjacent content supplies the label;
- `CharacterCompactCheckboxPairV4` — keeps semantically coupled controls together;
- `CharacterResponsiveCheckboxGroupV4` — responsive `FlowRow` packing.

Interaction/visual contract:

- compact visible checkbox: 24 dp;
- touch-safe interactive envelope: at least 48 dp;
- shared `bodySmall` label typography;
- shared compact spacing/padding grammar;
- whole-row toggling where appropriate;
- explicit enabled/read-only capability;
- visual compactness does not shrink the touch target.

## 4. Migration and responsive grouping

Core product migration commit:

`582a809bafbc4d7d38836283ed0eb5fe33d94624` — `fix: migrate Player checkbox controls`

The migration covers seven files:

1. `CharacterEquipmentClosureV4.kt`;
2. `CharacterSpellListClosureV4.kt`;
3. `CharacterCompanionsModuleV4.kt`;
4. `CharacterClassOptionModulesV4.kt`;
5. `CharacterArtificeModuleV4.kt`;
6. `CharacterManagementSuccessorV4.kt`;
7. `CharacterManagementTabV4.kt`.

### Equipment

Both active Equipment editor presentations now use the shared control language. `Equipado` and `Especial` / `Equipo especial` are packed through the responsive group, and `Sintonizado` uses the same compact labelled primitive.

### Conjuros

The rigid full-width checkbox rows were replaced by responsive packing:

- source controls are placed in one responsive outer group;
- each source + `Preparado` pair remains semantically coupled through `CharacterCompactCheckboxPairV4`, so responsive wrapping does not separate the pair;
- V / S / M / Concentración / Ritual now share one responsive group and wrap only when the available width requires it;
- the list-row `Prep.` checkbox also uses the shared primitive.

This directly implements the phone 17.2/17.3 rule: use one row when the controls fit, exploit wider layouts, and wrap only when necessary rather than preserving hard-coded two-row/full-width grouping.

### Other Player checkbox sites

Companions, class options, artifice, and both Management rest-preview selectors were migrated to the same shared checkbox family.

## 5. Related toggle audit — deliberate exceptions

The final package-wide audit reports:

- raw Material `Checkbox` outside the shared primitive: **0**;
- `TriStateCheckbox`: **0 sites**;
- Material `Switch`: **11 sites**.

The 11 `Switch` sites were source-inspected and are legitimate binary state/settings controls, including:

- current binary resource state;
- Inspiration operational state;
- temporary-effect active state and its editor;
- spellcaster enablement;
- visibility/settings rows;
- haptic profile enablement.

These controls express binary on/off state rather than checkbox membership/selection. They are therefore **intentional exceptions and remain `Switch` controls**. Round 3 does not mechanically convert them to checkboxes.

## 6. Durable guard

`scripts/check_player_checkbox_consistency.py` is now a permanent Scaffold guard.

It enforces:

- no raw Material `Checkbox` imports/calls outside the shared primitive;
- presence of the compact visual + >=48 dp interaction contracts;
- responsive Equipment grouping;
- responsive Conjuros grouping;
- semantic source/prepared pairing;
- shared Management rest selectors;
- informational reporting of `Switch` / `TriStateCheckbox` sites so related-toggle scope remains visible without treating valid switches as defects.

The temporary bounded migration helper and temporary CI self-commit machinery were removed after the verified product source was committed. The normal workflow is back to read-only repository permissions.

## 7. Verification history

### Useful superseded guard discovery

Scaffold run `34792818885` / run `1529` intentionally stopped before Kotlin compilation because the first package-wide checkbox guard detected the previously unaudited `CharacterManagementTabV4.kt` raw checkbox. That run is retained as useful evidence of the source-audit expansion and is superseded by the corrected source-complete runs.

### Transactional migration verification

Scaffold run `34792939962` / run `1532` successfully applied the source-complete **19 → 0** migration in the build workspace, passed all guards and Kotlin/Android/Desktop build/tests, uploaded an APK, and only then committed the seven migrated source files as `582a809bafbc4d7d38836283ed0eb5fe33d94624`.

### Authoritative clean committed-source verification

Final authoritative Round 3 run:

- Workflow: `Scaffold checks`;
- Run ID: `34793253151`;
- Run number: `1537`;
- Head SHA: `1d1c476ddaeb045c8a1b186267f452010cad7681`;
- Conclusion: **SUCCESS**;
- Backend job: **SUCCESS**;
- Compact Player control geometry guard: **SUCCESS**;
- Player reorder stability guard: **SUCCESS**;
- Player checkbox consistency guard: **SUCCESS**;
- Kotlin/shared build and tests: **SUCCESS**;
- Android build: **SUCCESS**;
- Desktop build: **SUCCESS**;
- Debug APK upload: **SUCCESS**;
- Artifact ID: `10328739208` / `dnd-custom-aid-debug-apk`;
- GitHub Actions artifact digest: `sha256:03f529acfde7ad18e2aa08752a28822d3d7f9c45916e02ddf65932714d52b1bf`.

The artifact digest above is the GitHub Actions artifact digest; it is not relabelled as an independently computed APK-file SHA-256.

## 8. QA status after Round 3

Automation proves the shared control migration and responsive source contracts, but no new consolidated physical candidate has been frozen yet.

Therefore:

- phone 17.1–17.3 are **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**;
- the tablet checkbox-family reproduction is **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**;
- accepted unrelated phone/tablet PASS evidence remains preserved and must not be replayed.

Future targeted physical revalidation should include, at minimum:

### Phone

- representative Equipment `Equipado` / `Equipo especial` / `Sintonizado` control scale, label spacing and touch behavior;
- a second representative migrated checkbox surface, preferably Management rest selection or another migrated editor;
- Conjuros V/S/M + Concentración/Ritual packing in portrait and landscape;
- Conjuros source/prepared packing when T5 makes the required source context available.

### Tablet

- one representative migrated checkbox surface for 17.1 parity;
- Conjuros responsive packing in portrait and landscape, including source/prepared groups after T5 is repaired.

Do not replay the complete 23-phone / 18-tablet discovery suites.

## 9. Compatibility / non-goals

Round 3 changes UI control composition only. It does not intentionally alter:

- checkbox-backed domain values;
- storage/import/export/migration contracts;
- canonical state authority;
- existing source IDs/associations;
- Switch semantics;
- Table Mode policy;
- T5 spell-source/bootstrap behavior.

The repair also does not claim every toggle should look identical: `Switch` remains the appropriate binary state/settings control where its semantics fit.

## 10. Project gate and next action

Phase 4A remains **OPEN**. The exact frozen physical candidate remains `preqa.12` until the consolidated repair receives a new monotonic QA identity. DM implementation remains blocked pending explicit owner Phase 4A closure. No P18 exists.

**Next repair family: T5 — spell source/bootstrap/source-context compatibility.**

Implement canonical-origin-driven source availability analogous to Rasgos while preserving existing spellcasting source/profile data as compatible overlays, IDs and associations. After that bounded round, run focused tests + normal Scaffold and update the dedicated checkpoint, `docs/PROJECT_STATE.md`, `docs/checkpoints/LATEST.md`, and `docs/TESTING.md` before proceeding.
