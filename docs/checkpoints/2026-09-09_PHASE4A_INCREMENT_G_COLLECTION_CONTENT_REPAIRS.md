# Phase 4A — Increment G collection/content repairs

**Date:** 2026-09-09  
**Branch:** `implementation/phase4a-successor-cycle`  
**Canonical branch:** `main` (unchanged by Increment G)  
**Status:** COMPLETE / AUTOMATED INTEGRATED GATE GREEN / OWNER ACCEPTANCE PENDING

## 1. Boundary

Increment G repairs Equipo, Rasgos, conditional character modules, Notas and Trasfondo while reusing the successor domain/storage foundations and the shared interaction primitives. It does not create parallel state authorities.

The owner explicitly asked implementation to continue into G. The repaired Increment F automated boundary remains valid, but its targeted repaired-F owner phone retest has **not** been reclassified as passed. Automated validation and owner/device acceptance remain separate.

## 2. Product commits

- G1 Equipo: `f2f669175e0250d449b0b8ac227de3455cc38966` — `feat: wire Increment G1 equipment canonical state`;
- G2 Rasgos core: `d1ee49df02019071b9de48f522f6e8857b63e293` — `feat: repair Increment G2 traits projections`;
- G2 conditional-module compaction: `d8ff2c32e7fac65096d62382728d94da2c0b5a61` — `feat: compact Increment G2 conditional modules`;
- G3 Notas: `ec9997ed0ba8099570bb24fa661831e6bd7c9e0b` — `feat: add Increment G3 notes search and filtering`;
- G4 Trasfondo images: `3b6118e490edc8d57a0097c60d196d4de112722a` — `feat: add Increment G4 persistent background images`.

All successful one-off helpers self-removed. At the completed G product boundary, `.github/workflows` contains only the normal `scaffold-check.yml` workflow.

## 3. G1 — Equipo

Implemented:

- canonical Armor Class projection rather than a second AC authority;
- current equipped-item projection consistent with the character sheet / General-Defensas state;
- Resources with `CharacterResourcePlacement.EQUIPMENT` shown and edited from the same canonical Resource state;
- compact `Gemas / arte` free-text storage through successor preferences;
- clearer `Tipo de consumo`, `Descuento por uso`, and `No consume cantidad` semantics;
- canonical `Electrum` terminology, including compatibility display normalization for the old default `ep` row;
- existing shared card/reorder behavior preserved.

Focused workflow `34411179956` — SUCCESS.

An earlier G1 attempt exposed a stale regression expectation for `Electro`. The protected product term is `Electrum`, so the obsolete test was updated rather than weakening the implementation.

## 4. G2 — Rasgos and conditional modules

### Rasgos core

Implemented:

- Resources with `CharacterResourcePlacement.TRAITS` projected from the same Resource state;
- shared measured 2-D whole-card reorder for multi-column Rasgos;
- `Raza` terminology;
- previous generic free-text `Fuente` + `Tipo` ambiguity consolidated through the existing structured provenance primitive;
- search/filter/group/manual-order remain presentation logic rather than a second stored-order authority.

Focused workflow `34411590496` — SUCCESS.

### Conditional modules

Artífice, Formas, Técnicas, Metamagia, Pactos and Compañeros already used the correct module-owned domain data. Module visibility already implements AUTO / FORCE_SHOW / FORCE_HIDE without deleting data, so G2 did not rewrite that domain behavior.

Their collection UI now uses:

- one shared compact sticky `CharacterCollectionToolbarV4`;
- collapsible search;
- compact Manual/A–Z control;
- compact add action;
- existing filters;
- `CharacterHelpV4` for explanatory content outside the permanent sticky footprint;
- existing editors, favorites, duplicate/remove actions and whole-card reorder semantics.

Focused workflow `34412312723` — SUCCESS.

## 5. G3 — Notas

The approved Notes model remains deliberately minimal: freeform general notes plus optional title/content cards and manual order. No tags, categories, dates or synthetic metadata were added.

Implemented:

- shared `presentCharacterNotes(...)` projection;
- accent-insensitive search across title and content;
- presentation-only `Con contenido` / `Sin contenido` filters;
- Manual and A–Z views, with A–Z never rewriting stored manual order;
- reorder only in Manual with no active search/filter;
- shared compact sticky toolbar for titled notes;
- measured 2-D whole-card reorder for multi-column notes and measured 1-D reorder for one column;
- compact duplicate/remove actions and destructive delete haptic;
- regression coverage for search, filters and stored-order independence.

Focused workflow `34412667797` — SUCCESS.

## 6. G4 — Trasfondo persistent images

The successor domain already defined app-owned `CharacterBackgroundImage` values with PRIMARY/SECONDARY slots and encoded payload persistence. G4 wires Android to that authority instead of storing external URIs.

Implemented:

- working primary and secondary image cards replacing placeholders;
- Android `image/*` picker;
- sampled decode for large inputs;
- resize to a maximum 1600-pixel edge;
- PNG storage for alpha-bearing images, otherwise JPEG at bounded quality;
- Base64 payload stored inside the existing successor aggregate, so reopen does not depend on the original picker URI;
- persistent rendering with crop behavior;
- add/change/remove controls in structural-edit mode;
- original filename retained when available;
- local user-visible import failure message;
- ownership explanation through `CharacterHelpV4`;
- repository regression proving image payload save/reopen;
- backup regression proving encode/decode/import preserves slot/MIME/payload/name while assigning a fresh imported image identity.

First focused G4 attempt `34413043981` failed safely on one nullable Android content-description expression after shared tests had passed. No product commit occurred. Retry `34413311747` changed only that expression to null-safe access and was SUCCESS.

## 7. Integrated Increment G gate

Validation head:

`aa7e57647e4a1b467de028f9d0a0d13e4a1ef0bd`

Normal `Scaffold checks` workflow:

`34413644371` — **SUCCESS**

Verified together:

- backend dependency install: PASS;
- backend Worker type-check: PASS;
- `:shared:desktopTest`: PASS;
- Android debug assembly: PASS;
- desktop application build: PASS;
- Android debug APK upload: PASS.

Artifact:

- ID `10128271891`;
- name `dnd-custom-aid-debug-apk`;
- ZIP size `13,294,132` bytes;
- ZIP digest `sha256:1a3aab50c9c5122f94ddaa2b7de173cddd046b74b80f8be58b0ed4ec564bca2f`;
- generated from head `aa7e57647e4a1b467de028f9d0a0d13e4a1ef0bd`.

This is the authoritative automated Increment G boundary.

## 8. Acceptance boundary

The green gate is **not owner acceptance**.

Outstanding physical/owner items include:

- repaired Increment F targeted phone retest remains unresolved unless later explicit owner evidence closes it;
- G4 image picker/render/reopen behavior should be exercised on a physical phone during consolidated successor audition;
- image backup/import independence should be sampled practically when appropriate;
- card drag feel remains an owner-audition item;
- tablet/wide acceptance remains deferred to Increment I.

## 9. Next implementation position

Engineering sequence after this checkpoint:

- H — full-screen Application Settings, live previews, density/text controls and audition themes;
- I — independent tablet portrait/landscape redesign;
- then targeted repairs/acceptance and later formal Phase 4A closure.

Do not merge this branch to `main` merely because Increment G is automated-green. No DM implementation begins before explicit Phase 4A closure.
