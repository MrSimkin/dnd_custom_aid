# Para Hoja de PJ v6 — final candidate checkpoint

Date: 2026-09-18

Status: **V6 FEATURE COMPLETE / TECHNICAL QA PASSED / OWNER FINAL VISUAL QA PENDING**

## Owner feedback addressed from v5

1. The prior checkmark still had awkward geometry. v6 rebuilds it as a simple conventional stroke-based check; Expertise / Pericia is two complete copies stacked vertically.
2. The prior marked-container variants read as small punctuation inside the shape. v6 makes / and backslash diagonal strokes across the usable interior, X both diagonals, * a full interior asterisk, + and - full central-axis marks, plus full-size check and dot variants.
3. The prior numerals were rejected. v6 replaces the seven-segment/display-like construction with a new ordinary rounded/sans 0-9 set.

## Preserved design contract

- exact v1 A-E/a-e geometry remains copied from the authoritative archival font;
- v1-derived and v3-derived symbol families remain;
- C/D remain the historical non-uniform oval;
- h/H remains the additional uniform v1-style oval;
- p/P remains the v3-derived oval;
- extended reusable PC-sheet icon vocabulary and existing keyboard/PUA semantics remain;
- v5 is preserved unchanged and is not rewritten.

## Frozen identities for this candidate

- v1 source SHA-256: `d0c0d50ad8ed33064e3af89b0976933b9eba8ead2d26234011c1a3233c246658`;
- v6 TTF SHA-256: `b23cc8ce806ba5e6e6039fc1b24f8457bf91c726d6ecfc43451868d90b2ef02f`;
- builder SHA-256: `75db59fa6f2ca2cce9dd8c81d64e1095afa375e82652c6fe298cd540d9033547`;
- diagnostic source SHA-256: `3e6c00eef5e696ab0da7d6c81cdcb62e774056f0e4e0283aaf9ffc00780e623f`;
- guide SHA-256: `fce0941fbedc9c86c7e5838e0bae28fa7632d95ed6ae4882f19bc5e1dac989ab`;
- diagnostic/reference PDF SHA-256: `9996a3bee74acc9ddb1c40b9b595213f1ee40c9f3e2b00e91448258cb12b9374`;
- glyph count: 291;
- cmap mappings: 502;
- units per em: 2048;
- embedding: fsType=0.

The deterministic builder was rebuilt locally and produced byte-identical v6 TTF output.

## Visual/PDF QA

The 30-page diagnostic/reference PDF was rendered and inspected at high resolution. Dedicated pages cover the corrected check family, redesigned 0-9 numerals, full-span container mark semantics, all marked-container PUA blocks, and 32/24/16 pt compact stress tests.

Poppler and PDFium were also compared by matching page number. The automatic parity helper's lexicographic filename ordering was not relied upon; matching-page pixel comparison showed normal renderer/antialiasing differences only, with no glyph-layout failure observed.

## Approval boundary

- v1 remains immutable archival provenance;
- v4 and v5 remain preserved historical candidates;
- v6 must not be silently rewritten after publication; future visual changes require the next derivative;
- production PC-sheet rendering must not switch to v6 until the owner explicitly accepts this diagnostic/reference review;
- PR #85 remains DRAFT / DO NOT MERGE until the owner clears the visual gate.
