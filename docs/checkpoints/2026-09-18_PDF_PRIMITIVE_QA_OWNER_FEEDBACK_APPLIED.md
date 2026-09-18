# Checkpoint — owner typography feedback applied to Primitive QA

**Date:** 2026-09-18 (Chile local time)  
**Status:** IMPLEMENTED / OWNER PRIMITIVE QA PENDING  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE

## Implemented

- PDFBox 3.0.8 remains the renderer baseline;
- Fira Sans Regular/SemiBold bundled for ordinary/prominent generated text;
- Barlow Condensed Bold bundled for deliberately compact candidate roles;
- Kalam Bold bundled for a dedicated handwritten/script role;
- text sizing remains per-element and now explicitly distinguishes adaptive fitting from fixed-size ruled regions;
- vector marker inventory includes the app-derived double-check used for Expertise/Pericia;
- Para-hoja-de-pj has versioned `v1/`, `v2/`, `v3/` architecture with per-version guides;
- v3 generator preserves prior mappings and adds generic/semantic double-check PUA entries;
- owner-font public-repository inclusion/modification is explicitly authorized.

## Evidence

Scaffold `35359508972` at `4d4c2fae3d6b483c5e508886783fda37076f2d81` — SUCCESS across backend, hosted database and Kotlin/build/tests/artifact generation.

The generated Primitive QA was visually inspected after CI:

- Fira candidate is clear for ordinary text;
- Kalam Bold produces a distinct readable handwritten name treatment;
- the app-derived double check is clearly distinguishable;
- Barlow Condensed Bold is intentionally dense and must be judged by the owner at realistic print scale before wider use.

## Remaining blocker for owner symbol-font binary comparison

The exact original TTF is not available in the current chat/library.

Known original SHA-256:

`d0c0d50ad8ed33064e3af89b0976933b9eba8ead2d26234011c1a3233c246658`

Do not recreate v1 from memory or an embedded PDF subset. When the owner asset is available again, verify this hash, commit the exact original under the versioned owner-font tree, then generate later versions from it.

## Owner gate

No complete PDF visual family is approved.

The next review is Primitive QA of:

- handwritten/script treatment;
- per-element size hierarchy;
- fixed versus adaptive sizing;
- condensed readability;
- single/double check;
- other vector markers.

PR #85 remains draft until the renderer/family gates are satisfied.
