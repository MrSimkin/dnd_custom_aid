# Phase 4A — Player pre-QA.8 stabilization boundary

**Date:** 2026-09-09 (owner local date)  
**Branch:** `implementation/phase4a-successor-cycle`  
**Status:** STABILIZATION COMPLETE / RESIDUAL AUDIT GREEN / FULL NORMAL GATE GREEN / CONSOLIDATED OWNER QA PENDING  
**Owner acceptance:** NOT YET PERFORMED for this consolidated build  
**Release status:** debug / development; not release-ready  
**DM implementation:** blocked until Phase 4A is accepted and explicitly closed

## Purpose

This checkpoint closes the post-A–I stabilization pass that followed owner phone audition findings. It deliberately replaces repeated micro-retests with one consolidated Player QA build after the known blocking/structural findings were repaired and audited together.

This is not owner acceptance and is not yet a formal frozen M6 candidate. The next boundary is consolidated owner QA against the exact automated-green APK recorded below.

## Exact product identity

- version name: `0.4.0-preqa.8`;
- version code: `40800`;
- product commit: `c78b06776f5ae7a253b5b12b791c71fa2a7da096`;
- product tree: `c612c07345ecdfc91d972118314ee649fe2048c4`;
- validation/checkpoint head: `2a9b682f6aca2e95facecf1f6256039fd96cfefd`;
- previous product behavior tree before the version-only commit: `18c47f131a6a0442175e79a32850becf9da77133`.

The version-only commit exists so this repaired QA APK is distinguishable from historical `0.4.0-preqa.7 / 40700` and can be installed as a monotonic in-place update for persistence/migration testing.

## Stabilization repairs included

### Rasgos provenance

The Rasgos editor no longer requires the owner to select a source type and then redundantly type an already-known class/race/background value. The shared provenance row is fed from canonical character identity data:

- class origins use current `CharacterClassLevel` values and stable class IDs;
- race uses the character's current `background.race` value;
- background uses the character's current background name;
- custom origin remains an explicit fallback for feat/gift/other/custom cases;
- the persisted trait still has one `source` value rather than parallel source authorities.

### Multi-column reorder

Equipo, Rasgos and Notas retain their configured multi-column browsing layouts. When `Reordenar` is active they temporarily render one column and reuse the proven vertical whole-card drag primitive; `Listo` returns to normal configured columns. This avoids forcing the unstable 2-D drag feel into the acceptance path.

### Settings, fonts and themes

- Inter, Figtree, Public Sans, Cabin Condensed and Encode Sans Condensed are hidden from normal font selection;
- an already-saved hidden font resolves safely to Manrope;
- misleading miniature theme swatches were removed;
- the realistic free-text theme/font preview remains;
- Ámbar, Pizarra and Terracota were retuned for clearer visual identity.

### App-wide free-text density

A repository audit first found 58 multiline/fixed editor-field surfaces. The repair then:

- removed the large permanent multiline height slabs in Trasfondo and Notas;
- removed the same hard-height pattern from Artífice, class-option modules, Compañeros and Formas;
- introduced one shared spacing-aware multiline minimum-line policy;
- applied that policy broadly while preserving one-line fields and required interactive touch-target heights;
- kept bounded long-text scrolling instead of reserving a large empty vertical area permanently.

Focused density workflow `34429853012` completed successfully, including shared desktop tests and Android Kotlin compilation, and self-removed its temporary helper. Density product commit:

`4c90fcd4f3892b0d9eac799f4acffb537b0f9e47`

Density product tree:

`18c47f131a6a0442175e79a32850becf9da77133`

## Residual Player acceptance audit

Self-removing residual audit workflow:

`34430378823` — **SUCCESS**

It verified the current repaired product state for the owner-audition risk families:

- Equipo/Rasgos/Notas one-column reorder mode while preserving grid browsing;
- Rasgos canonical provenance choices and single source authority;
- Conjuros compact source context, collapsible search and shared spell-level normalization;
- Conjuros casting rows derived from the current projected per-source spellcasting profiles;
- phone landscape continues to use the phone model because `wide` is based on `layoutContext.isTablet`;
- one canonical contextual-help mode authority;
- hidden-font selection/fallback policy and realistic Settings preview;
- removal of old large fixed text-area geometry;
- broad shared multiline-density and IME-safe-editor coverage;
- temporary helper hygiene.

Measured static coverage from that audit:

- spacing-aware multiline helper references: `50`;
- shared `CharacterImeSafeEditorDialog` references: `42`.

The residual audit self-removed. Cleanup head `5bd48ed501a04deaff213af3e88023f5550e8606` had the same product tree as the density product commit.

## Full normal gate evidence

Normal `Scaffold checks` workflow:

`34430548061` — **SUCCESS**

Validation head:

`2a9b682f6aca2e95facecf1f6256039fd96cfefd`

### Backend

- dependency install: PASS;
- `npm run check`: PASS.

The successful backend job emitted non-blocking dependency/tooling warnings: npm reported three high-severity dependency findings, Wrangler suggested `@types/node` for Node compatibility, and the setup-node action reported its runtime migration/deprecation notice. These did not fail the backend check and are tracked as tooling/dependency technical debt rather than Player acceptance failures.

### Kotlin / Android / Desktop

Executed together:

`gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace`

Result:

- shared/Kotlin desktop tests: PASS;
- Android debug compilation/assembly: PASS;
- desktop build: PASS;
- stable CI debug signing step: PASS;
- APK artifact upload: PASS;
- Gradle result: `BUILD SUCCESSFUL in 2m 7s`.

### Artifact

GitHub Actions artifact:

- artifact ID: `10134364621`;
- artifact name: `dnd-custom-aid-debug-apk`;
- ZIP size: `13,321,947` bytes;
- ZIP SHA-256: `b7ead12a7501bbef96fef861321b5bebfd64c631647423b8eab9faec9580699a`;
- generated from workflow head `2a9b682f6aca2e95facecf1f6256039fd96cfefd`;
- ZIP contains exactly one file: `androidApp-debug.apk`.

Downloaded artifact verification:

- downloaded ZIP SHA-256 matched the GitHub Actions digest exactly;
- extracted APK size: `37,996,660` bytes;
- extracted APK SHA-256: `bb02b413919f55551eb7d4e78dfab2c37145b852c8827126df80082bd7a40815`;
- extracted file was identified as an Android package (APK).

## Acceptance boundary

Automated green is not owner/device acceptance. This checkpoint does not claim that tablet, phone landscape, drag feel, theme appearance, keyboard behavior or migration persistence look/feel correct on physical hardware.

For the consolidated owner QA build, the first installation must be **over the existing prior QA installation/data**. Do not clear app data first. The initial check must exercise the real upgrade/persistence path.

At minimum, first verify that campaigns/characters and representative General, Combate, Equipo/Monedas, Conjuros and Notas data survive and reopen correctly before any destructive/fresh-install step.

## Exact next action

1. expose/install the exact `0.4.0-preqa.8 / 40800` APK recorded above over the current QA installation;
2. verify upgrade/data preservation first;
3. execute the consolidated Player owner QA rather than isolated micro-retests;
4. classify any observations as non-blocking visual/ergonomic, later maintenance, or Phase 4A blocking;
5. repair only acceptance-blocking defects if found, with a new exact automated boundary;
6. when the owner-audited baseline is acceptable, freeze the replacement formal M6 candidate and complete the required regression matrix;
7. explicitly close Phase 4A before beginning DM implementation.
