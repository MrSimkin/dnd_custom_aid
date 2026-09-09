# Phase 4A — Increment C2 PC Settings Information Architecture

Date: 2026-09-08

## Status

**C2 implementation checkpoint: COMPLETE / AUTOMATED GATE GREEN.**

This checkpoint reorganizes `Ajustes de personaje` around character identity/lifecycle, safe character-level actions and persistent configuration rather than mixing operational character state into Settings.

It does **not** imply owner visual acceptance of the successor Settings UX.

## Branch and verified product head

Implementation branch:

`implementation/phase4a-successor-cycle`

Verified product head:

`99696ea4ce5c6e212fd0beaa351ec180727c0deb`

Workflow:

`34296859639` — SUCCESS

Verified together:

- backend check: PASS;
- shared/Kotlin tests: PASS;
- Android debug compilation/assembly: PASS;
- desktop compilation/build: PASS;
- Android debug APK upload: PASS.

CI artifact:

- artifact ID `10083511887`;
- name `dnd-custom-aid-debug-apk`;
- ZIP digest `sha256:47ae1a07e1995fd83efef8bd7875fd002d0492019c78c79c92f78ee5aaea5ce1`.

This artifact is automated implementation evidence, not a replacement owner-auditioned build.

`main` remains untouched by successor implementation.

## PC Settings information architecture

The Settings surface now presents:

1. lifecycle status and local backup near the top;
2. `Configuración de la aplicación` near the upper portion as a separate destination;
3. feature visibility (`Lanzamiento de Conjuros`, Inspiration);
4. character-sheet tab order;
5. custom attributes;
6. custom skills;
7. Custom Markers;
8. conditional-module visibility overrides;
9. haptic behavior and Modo Mesa;
10. the experimental supercompact view lower in the surface.

The old `Progreso` editor was removed from PC Settings. Progress is character state, not a Settings preference, and remains owned by the appropriate character surfaces for later operational redesign.

## Lifecycle safety

`Activo` / `Inactivo` remain direct lifecycle choices.

Transitions to:

- `Retirado`;
- `Muerto`;

now require explicit confirmation before persistence. These actions preserve character data and remain reversible lifecycle-state changes rather than deletion.

Backup remains disabled when unsaved structural changes make the persisted export ambiguous.

## Application Settings placement

`Configuración de la aplicación` is now intentionally easy to find near the top of PC Settings, but remains a separate application-level destination.

The later full-screen Application Settings redesign from the reconciled plan remains Increment H work; C2 does not falsely claim that broader redesign complete.

## Real configurable tab order

The stable `CharacterSheetTabKey` order stored in `CharacterSuccessorPreferences` now drives both:

- the phone/top-tab presentation;
- the wide/tablet navigation rail.

Conditional tabs are filtered **after** applying the saved order, so a temporarily hidden module retains its stored position and does not lose data or ordering preference.

The default remains the complete canonical tab list exactly once, and repository validation prevents duplicates or omissions.

## Custom attributes

PC Settings can now add/edit/delete custom ability-like attributes with:

- name;
- abbreviation;
- score;
- optional saving throw;
- optional saving-throw proficiency;
- saving-throw adjustment;
- optional notes.

Deletion is blocked while an attribute is still referenced by a custom skill or spellcasting profile. The user must reassign the dependency rather than silently corrupting a durable reference.

## Custom skills

PC Settings now provides the successor custom-skill manager.

A custom skill can select either:

- a built-in characteristic; or
- one of the character's custom attributes.

The new editor intentionally does **not** expose the legacy generic `Fuente` field. Existing legacy source text is preserved when editing an old skill but is not presented as a parallel provenance field.

The generalized ability relationship is persisted through `CharacterCustomSkillAbilityConfiguration`; the legacy built-in ability field remains compatibility state while successor consumers resolve the generalized reference.

### Transitional C5 note

The old `Habilidades personalizadas` card/editor still exists inside the current Habilidades tab until C5 rebuilds that tab. C2 establishes the new Settings authority, but **Increment C is not complete yet**: C5 must remove that duplicate special-card editing path and render custom skills inline with ordinary skills as already planned.

## Inspiration visibility

The canonical Inspiration value remains on `CharacterSheet`.

Only its presentation preference is configurable here.

`inspirationVisible` is stored in `CharacterSuccessorPreferences`, beside tab order, rather than in a second settings repository. This preserves the one-datum/one-owner rule and means own-format backup/import carries the preference automatically with successor state.

Focused tests verify:

- default visible behavior;
- independent per-character persistence;
- schema-12 → schema-13 migration defaulting existing characters to visible;
- own-format export/import preserving a hidden preference.

## Custom Markers

PC Settings configures marker **structure**, not live operational value:

- name;
- binary / counter / current-max type;
- maximum where applicable;
- recovery cadence;
- recovery amount mode;
- optional notes.

Existing current values are preserved during structural edits and clamped only when a type/maximum change makes the previous value invalid.

Actual live marker interaction belongs to General/Gestión in later increments rather than turning Settings into an operational state panel.

## Haptic configuration

The existing per-character `Respuesta háptica` on/off state remains authoritative in `CharacterClosureState`.

C2 adds understandable bounded device-wide feel controls:

- intensity: `Suave`, `Media`, `Fuerte`;
- duration: `Corta`, `Media`, `Larga`.

The controls are stored as device UI preferences rather than character-domain data.

The shared haptic hook now consumes those preferences for existing drag/resource/destructive events. On Android devices with amplitude control it uses bounded amplitude/duration approximations; when hardware cannot honor amplitude precision it falls back to Android's default amplitude, and when direct vibration is unavailable it falls back to the existing system haptic constants.

The UI explicitly avoids implying exact hardware-equivalent strength across devices.

## Responsive behavior

PC Settings no longer decides tablet composition from `maxWidth >= 720.dp`.

It uses the shared form-factor-aware layout context introduced in Increment B. Therefore a physical phone in landscape remains in the phone Settings composition rather than becoming tablet UI merely because its dp width crosses a threshold.

This does not close the broader tablet/wide redesign requirement.

## Schema and compatibility

C2 adds migration `12.sqm`, moving schema 12 → 13 by adding:

`character_successor_preferences.inspiration_visible INTEGER NOT NULL DEFAULT 1`

The temporary redundant `character_pc_configuration` table/repository explored during implementation was removed before the checkpoint. It is not part of the verified C2 architecture.

A historical migration-11 regression test was pinned to schema 12 so future migrations cannot accidentally broaden its fixture. C2 has its own focused migration-12 test.

## Acceptance boundary

C2 is technically green and suitable as the configuration foundation for C3/C4 General and C5 Habilidades.

It does **not** mean:

- owner has visually accepted PC Settings density/order on device;
- successor phone presentation is accepted;
- tablet/wide presentation is accepted;
- the Increment-B drag-feel finding is closed;
- the old custom-skill card in Habilidades is accepted as final;
- Application Settings has completed its later full-screen redesign;
- a replacement formal M6 candidate exists.

## Exact next action

Proceed to **C3/C4 — General compact identity and canonical reference/state projections**.

Use the now-established successor configuration to surface the same canonical state rather than building parallel General-only copies, including:

- compact class identity;
- Spanish class/subclass presentation;
- `Raza` and `Idiomas`;
- Defensas/current armor references;
- Inspiration only when enabled;
- enabled Custom Markers;
- Resources configured for General placement;
- custom attributes;
- per-source compact `Lanzamiento de Conjuros` rows.

Then complete **C5 — Habilidades**, including inline custom skills and removal of the transitional duplicate custom-skill editor/card.
