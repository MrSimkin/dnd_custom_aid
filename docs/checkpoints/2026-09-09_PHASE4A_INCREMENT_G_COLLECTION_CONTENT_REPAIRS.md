# Phase 4A — Increment G collection/content repairs

**Date:** 2026-09-09  
**Branch:** `implementation/phase4a-successor-cycle`  
**Canonical branch:** `main` (unchanged by Increment G)  
**Status:** COMPLETE IMPLEMENTATION / G1–G4 FOCUSED GREEN / INTEGRATED GATE PENDING / OWNER ACCEPTANCE PENDING

## 1. Boundary

Increment G implements the planned repairs for Equipo, Rasgos, conditional character modules, Notas and Trasfondo. It reuses the successor domain/storage work from Increment A and the shared interaction primitives from Increment B rather than creating parallel state or one-off UI authorities.

The owner explicitly asked implementation to continue into G. The repaired Increment F automated boundary remains valid, but the targeted repaired-F owner phone retest recorded in `LATEST.md` has **not** been reclassified as passed by this implementation work. Automated validation and owner/device acceptance remain separate.

## 2. Product commits

Focused product commits in this Increment G sequence:

- G1 Equipo: `f2f669175e0250d449b0b8ac227de3455cc38966` — `feat: wire Increment G1 equipment canonical state`;
- G2 Rasgos core: `d1ee49df02019071b9de48f522f6e8857b63e293` — `feat: repair Increment G2 traits projections`;
- G2 conditional-module compaction: `d8ff2c32e7fac65096d62382728d94da2c0b5a61` — `feat: compact Increment G2 conditional modules`;
- G3 Notas: `ec9997ed0ba8099570bb24fa661831e6bd7c9e0b` — `feat: add Increment G3 notes search and filtering`;
- G4 Trasfondo images: `3b6118e490edc8d57a0097c60d196d4de112722a` — `feat: add Increment G4 persistent background images`.

Temporary one-off helper workflows/scripts self-removed after their successful product commits. At the G4 product boundary `.github/workflows` again contains only the normal `scaffold-check.yml` workflow.

## 3. G1 — Equipo

Implemented and focused-green:

- canonical Armor Class projection instead of a second AC authority;
- current equipped-item projection consistent with the character sheet / General-Defensas state;
- Resources configured with `CharacterResourcePlacement.EQUIPMENT` appear in Equipo and update the same canonical Resource values;
- compact `Gemas / arte` free-text storage through successor preferences;
- consumable semantics clarified to `Tipo de consumo`, `Descuento por uso`, and `No consume cantidad` where appropriate;
- canonical terminology changed to `Electrum`, including compatibility display normalization for the old default `ep` row;
- existing shared card/reorder behavior preserved.

Focused workflow: `34411179956` — SUCCESS.

One earlier G1 focused attempt exposed a stale regression expectation for `Electro`; the product terminology was already protected as `Electrum`, so the obsolete test expectation was updated rather than weakening the implementation.

## 4. G2 — Rasgos and conditional modules

### Rasgos core

Implemented and focused-green:

- Resources configured with `CharacterResourcePlacement.TRAITS` are projected into Rasgos from the same canonical Resource state;
- multi-column Rasgos use the shared measured 2-D whole-card reorder primitive;
- user terminology uses `Raza`;
- the former separate generic free-text `Fuente` plus `Tipo` editor ambiguity was consolidated through the existing structured provenance primitive instead of adding another source model;
- search/filter/group/manual-order behavior remains presentation logic rather than a second stored order authority.

Focused workflow: `34411590496` — SUCCESS.

### Conditional module collection grammar

Artífice, Formas, Técnicas, Metamagia, Pactos and Compañeros were audited before modification. Their domain behavior already used the correct class-option/form/companion state and the module-visibility layer already implements AUTO / FORCE_SHOW / FORCE_HIDE without deleting module-owned data.

The repair therefore stayed UI-focused:

- one shared compact sticky `CharacterCollectionToolbarV4` per collection;
- collapsible search;
- compact Manual/A–Z control;
- compact add action;
- existing filters preserved;
- module explanation moved to `CharacterHelpV4` outside the permanent sticky footprint;
- reorder guidance moved out of the permanent sticky area;
- existing editors, favorites, duplicate/remove actions and whole-card reorder semantics preserved.

Focused workflow: `34412312723` — SUCCESS.  
Product commit: `d8ff2c32e7fac65096d62382728d94da2c0b5a61`.

## 5. G3 — Notas

The approved Notes model remains deliberately minimal: freeform general notes plus optional cards with only title/content and manual order. Increment G does **not** add tags, categories, dates or synthetic metadata merely to support filtering.

Implemented and focused-green:

- shared `presentCharacterNotes(...)` presentation projection;
- accent-insensitive search over title and content;
- presentation-only `Con contenido` / `Sin contenido` filters;
- Manual and A–Z views, where A–Z does not rewrite stored manual order;
- manual drag available only when Manual view has no active search/filter;
- titled-note collection migrated to the shared compact sticky toolbar;
- multi-column note cards use measured 2-D whole-card reorder, with the existing measured 1-D primitive when one column is shown;
- duplicate/remove actions remain compact;
- destructive delete haptic is preserved;
- added shared regression coverage for search, filters and A–Z/manual-order independence.

Focused workflow: `34412667797` — SUCCESS.  
Product commit: `ec9997ed0ba8099570bb24fa661831e6bd7c9e0b`.

## 6. G4 — Trasfondo persistent images

The successor domain already defined app-owned `CharacterBackgroundImage` values with PRIMARY/SECONDARY slots and encoded payload persistence. G4 wires the Android surface to that existing authority instead of persisting external URIs or introducing a second media store.

Implemented and focused-green:

- the two Trasfondo placeholders are replaced by working primary/secondary image cards;
- Android picker accepts `image/*`;
- selected content is decoded into app memory, sampled for large inputs, reduced to a maximum 1600-pixel edge, then encoded into the existing app-owned payload;
- alpha-bearing images are stored as PNG; other images are stored as JPEG at bounded quality;
- the stored image therefore no longer depends on access to the original picker URI after import;
- actual persistent image is rendered with crop behavior;
- add/change/remove are available when structural editing is enabled;
- original filename is retained as metadata when available;
- import failures produce a local user-visible error instead of corrupting state;
- explanatory ownership text uses the global `CharacterHelpV4` system;
- successor repository regression verifies payload survives save/reopen;
- backup regression verifies encode/decode/import preserves slot/MIME/payload/name while assigning a fresh imported image identity.

First focused G4 attempt `34413043981` failed safely at Android compilation on one nullable content-description reference after shared tests had passed. No product commit occurred. The retry changed only that expression to null-safe access.

Focused G4 retry workflow: `34413311747` — SUCCESS.  
Product commit: `3b6118e490edc8d57a0097c60d196d4de112722a`.

## 7. Focused validation status

Across G1–G4, each successful product boundary ran at least:

- `:shared:desktopTest` — PASS;
- `:androidApp:compileDebugKotlin` — PASS;
- guarded diff checks for the intended product properties.

These focused checks are not the final Increment G integration gate.

## 8. Integrated gate — pending

This checkpoint commit intentionally triggers the repository's normal `Scaffold checks` workflow against the complete G1–G4 product tree.

The required authoritative gate is:

- shared/Kotlin desktop tests;
- Android debug assembly;
- desktop application build;
- backend dependency install/type-check;
- Android debug APK artifact upload.

Workflow ID, validation head and artifact evidence will be written here after the normal workflow finishes successfully. Until then, do not describe Increment G as integrated-green.

## 9. Acceptance boundary

Even after the automated integrated gate becomes green:

- Increment G is not owner/device accepted merely because CI passes;
- repaired Increment F targeted phone acceptance remains a separate outstanding owner boundary unless explicit owner evidence later closes it;
- G4 image picking/rendering/reopen should be physically auditioned in the later consolidated successor owner test, including closing/reopening the character and backup/import independence where practical;
- card drag feel remains an owner-audition item;
- tablet/wide acceptance remains deferred to Increment I.

## 10. Next implementation position

After a successful integrated G gate and documentation finalization:

- Increment H — full-screen Application Settings, live previews, density/text controls and audition themes — is the next implementation increment;
- Increment I — independent tablet portrait/landscape redesign — remains after H;
- no DM implementation begins before explicit Phase 4A closure.
