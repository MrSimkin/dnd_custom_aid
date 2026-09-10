# Phase 4A — Player pre-QA.8 stabilization boundary

**Date:** 2026-09-09 (owner local date)  
**Branch:** `implementation/phase4a-successor-cycle`  
**Status:** STABILIZATION COMPLETE / RESIDUAL AUDIT GREEN / FULL NORMAL GATE PENDING  
**Owner acceptance:** NOT YET PERFORMED for this consolidated build  
**Release status:** debug / development; not release-ready  
**DM implementation:** blocked until Phase 4A is accepted and explicitly closed

## Purpose

This checkpoint closes the post-A–I stabilization pass that followed owner phone audition findings. It deliberately replaces repeated micro-retests with one consolidated Player QA build after the known blocking/structural findings were repaired and audited together.

This is not owner acceptance and is not yet a formal frozen M6 candidate. The next boundary is one normal full repository gate followed by consolidated owner QA.

## Exact product identity entering the full gate

- version name: `0.4.0-preqa.8`;
- version code: `40800`;
- product commit: `c78b06776f5ae7a253b5b12b791c71fa2a7da096`;
- product tree: `c612c07345ecdfc91d972118314ee649fe2048c4`;
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

## Acceptance boundary

Automated green is not owner/device acceptance. This checkpoint does not claim that tablet, phone landscape, drag feel, theme appearance, keyboard behavior or migration persistence look/feel correct on physical hardware.

For the consolidated owner QA build, the first installation must be **over the existing prior QA installation/data**. Do not clear app data first. The initial check must exercise the real upgrade/persistence path.

## Full normal gate now required

The checkpoint commit that adds this file must run the repository's normal `Scaffold checks` gate. Required evidence before exposing the consolidated APK:

- backend dependency install/check: PASS;
- shared/Kotlin desktop tests: PASS;
- Android debug assembly: PASS;
- desktop build: PASS;
- Android debug APK upload: PASS;
- exact workflow/artifact identity recorded.

After that, expose one consolidated QA APK to the owner. Do not resume repeated micro-retests and do not begin DM implementation.
