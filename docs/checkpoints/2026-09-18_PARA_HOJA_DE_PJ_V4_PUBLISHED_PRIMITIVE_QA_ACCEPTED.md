# Checkpoint — Para Hoja de PJ v4 published; Primitive QA accepted

**Date:** 2026-09-18 (Chile local time)  
**Status:** FONT PUBLICATION COMPLETE / OWNER PRIMITIVE QA ACCEPTED / V4 VISUAL QA PENDING  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / **DO NOT MERGE**

## Owner gate resolved

The owner has accepted the renderer **Primitive PDF QA**. Renderer work is authorized to proceed beyond the primitive gate. This acceptance covers the shared primitive foundation; it does **not** approve any complete Classic / Custom v1 / Custom v2 visual family.

The v4 symbol-font artwork has its own owner visual gate and remains pending at this checkpoint.

## Exact v1 archival publication

The authoritative owner upload `Para Hj De Pj.ttf` was read directly from the uploaded binary. No manual/base64 reconstruction was accepted.

Verified identity:

- size: `12,744` bytes;
- SHA-256: `d0c0d50ad8ed33064e3af89b0976933b9eba8ead2d26234011c1a3233c246658`;
- Git blob: `67cf8eb51dbcee7b4c68c8ef7e9e238ce010271c`;
- repository path: `assets/fonts/owner/para-hoja-de-pj/v1/Para Hj De Pj.ttf`;
- publication commit: `87525f4f3648343a76c0192bc50a74e08740b396`.

GitHub independently produced the same expected Git blob ID before the binary was attached to the branch. Two earlier transfer attempts that did not match the expected object identity were rejected and remain unreferenced loose objects; the integrity rule was not weakened.

v1 remains byte-for-byte archival and must not be technically modernized in place.

## v4 publication and deterministic source

Compiled v4:

- path: `assets/fonts/owner/para-hoja-de-pj/v4/Para Hoja de PJ Symbols v4.ttf`;
- SHA-256: `2c49b21c0616bab315f2e6f6b8c7be55e835fb5fa1db4e17e248861d4794b3e2`;
- Git blob: `cfc04ea4fb0ce53e8ee41c72df7e6281ef329c4a`;
- binary publication commit: `525b1a223b5ce79fe5aa0c10600b3ccd8152ae37`.

Deterministic source/build:

- path: `scripts/fonts/para-hoja-de-pj/v4/build_para_hoja_de_pj_v4.py`;
- source Git blob: `033155c35ea92bbeaaf9ebbffabdffed1fbb897d`;
- source publication commit: `c21fd13d5a1da0a623deea1f1b5852d04075e8d1`;
- embedded canonical TTX SHA-256: `951c98add01ef66747be23535a946d25fe44a820087f8bd8c525e2df5219849c`.

The builder imports the canonical TTX with timestamp recalculation disabled, saves with deterministic table ordering and fails closed unless the rebuilt TTF has both the expected SHA-256 and the expected Git blob ID. Local validation rebuilt the committed 9,104-byte v4 TTF byte-for-byte.

Complete mapping/use documentation was published in `assets/fonts/owner/para-hoja-de-pj/v4/GUIDE.md` by commit `a59fb0a2bfd52f9426ac88b3ef0e9c1ef5613f74`. Root/version provenance documentation was synchronized by `2b0b36f70a5b23e918b298e61ee0b9cb0ca91435`.

## v4 technical validation

The candidate is recognized as a scalable TrueType font. Relevant properties include:

- modernized family/version/PostScript metadata;
- Unicode keyboard mappings plus renderer PUA aliases;
- historical A-E/a-e meanings retained on the same keys with redesigned geometry;
- legacy-refined and modern-clean style families;
- current training grammar: Competent = one check; Expertise/Pericia = double check;
- units-per-em `1000`;
- OS/2 version `4`;
- embedding flags `fsType = 0`;
- deterministic timestamps/checksums/build identity;
- the earlier legacy-refined outline-square contour-winding defect is corrected in the frozen candidate.

Technical validation is not visual approval.

## Current visual gates

Still pending:

1. **Para Hoja de PJ v4 visual specimen approval** — assess recognizable A-E continuity, legacy-refined/modern-clean coherence, paired states, training checks, centering, proportions and weight;
2. complete populated sheet/family rendering for Classic, Custom v1, Custom v2 per Attribute and Custom v2 per Ability;
3. structured section -> page -> family -> end-to-end visual QA.

The renderer may continue beyond Primitive QA while the font candidate is reviewed, but do not treat v4 artwork as owner-approved until that separate specimen gate passes.

If published v4 artwork requires design changes after this checkpoint, preserve v4 as a versioned record and create the next derivative version rather than silently replacing the published v4 binary.

## Merge boundary

PR #85 remains **DRAFT / DO NOT MERGE**. Green automation is technical evidence only and does not satisfy the remaining font or full-family visual gates.

A final Scaffold run must be green after this publication/documentation state before the shared foundation can be considered clean and stable for any orchestration handoff.
