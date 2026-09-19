# Para Hoja de PJ v8 — APPROVED / FROZEN checkpoint

Date: 2026-09-18

Status: **OWNER APPROVED / FROZEN / TECHNICAL QA PASSED / FONT GATE CLOSED**

## Approval basis

The owner explicitly stated that correcting numerals 6 and 9 from v7 would automatically approve the font.

v8 implements that exact scope. A glyph-by-glyph comparison against v7 confirms that exactly two glyphs changed:

- `std_digit_6`
- `std_digit_9`

All other 289 glyphs, mappings, historical geometry, checks, marked-container variants and extended symbols remain unchanged from v7.

## Final 6 / 9 correction

The v7 forms were visually heavier than the surrounding ordinary numeral family.

v8 replaces them with lighter ring-and-sweep forms:

- one clean closed bowl;
- one restrained sweep/terminal;
- clean join without the former heavy silhouette;
- apparent stroke weight aligned to 0/2/3/5/7;
- 9 remains the coherent rotational partner of 6;
- verified at large and sheet sizes.

## Frozen identities

- v1 source SHA-256: `d0c0d50ad8ed33064e3af89b0976933b9eba8ead2d26234011c1a3233c246658`;
- v8 TTF SHA-256: `f8f7eeed331be34ea08da23bb165f200c8313e1ba670667466ad8e1aebf77d8b`;
- builder SHA-256: `803e11840defbb1b70990c6fca2084091a187a68a1240adec5f269277a5e0eab`;
- diagnostic source SHA-256: `5c4d43893c3f27f852ae60ffcda1ba38ed30b03536a5d869b01408a7d880ffd3`;
- guide SHA-256: `f74bd02b8e102238eaa3ca957de2f855d46a6a0cb8857f95318edfda38bf9bf7`;
- diagnostic/reference PDF SHA-256: `0aea974e8b3a1bd1f674c0369dce68ac31f5cbba0603c5acb39121e1186a8de4`;
- glyph count: 291;
- cmap mappings: 502;
- units per em: 2048;
- embedding: fsType=0.

The deterministic builder was rebuilt locally and produced a byte-identical v8 TTF.

## PDF / visual QA

- 30-page diagnostic/reference PDF preflight: openable, 30 pages, not encrypted, not scanned, no XFA;
- page 1 inspected after shortening a previously clipped explanatory line;
- page 8 inspected at high resolution for the full 0-9 family and sheet-size 32/22 pt lines;
- page 30 compact 32/24/16 pt symbol stress inspected;
- critical page 8 verified in both PDFium and Poppler with consistent glyph geometry/layout.

## Version and integration rule

- v1 remains immutable archival provenance;
- v4, v5, v6 and v7 remain preserved historical candidates;
- v8 is the approved/frozen font and must not be silently rewritten;
- any future font change requires v9;
- the font gate is closed, so subsequent PC-sheet renderer work may use v8;
- actual renderer integration, PR readiness and merge remain separate repository decisions;
- PR #85 remains DRAFT / DO NOT MERGE until separately authorized.
