# Para Hoja de PJ v7 — final candidate checkpoint

Date: 2026-09-18

Status: **V7 FEATURE COMPLETE / TECHNICAL QA PASSED / OWNER FINAL VISUAL QA PENDING**

## Owner feedback addressed from v6

The owner requested correction of ordinary numerals 3, 5, 6, 8 and 9, plus cleanup of the marked-container variants where some strokes showed white gaps, extended too far, or did not read as full lines.

v7 addresses those items without changing the accepted broader design contract.

## Numeral corrections

- 3: redrawn as two smooth open bowls with a quiet waist and no v6 kink.
- 5: redrawn with a conventional flat top and one rounded lower bowl.
- 6: redrawn with a cleaner upper terminal and larger counter so its visual weight matches the ordinary numeral family.
- 8: rebuilt as one balanced outer silhouette with two clean counters.
- 9: rebuilt as the coherent rotational partner of the corrected 6.
- 0, 1, 2, 4 and 7 remain the accepted v6 forms.

## Marked-container correction

Marked variants no longer position a generic mark by approximate per-shape scaling.

The deterministic builder now:
1. flattens the actual outline glyph;
2. finds the real fillable inner contour;
3. calculates each line intersection with that contour;
4. draws slash/backslash/X/asterisk/plus/minus to that usable span with a tiny anti-collision inset;
5. fits check/dot to the actual fillable region.

This includes the original asymmetric historical v1 oval. The result removes outline overlap/white wedges while also removing visibly short or arbitrarily overlong lines.

## Preserved design contract

- exact v1 A-E/a-e geometry remains copied from the authoritative archival font;
- v1-derived and v3-derived symbol families remain;
- C/D remain the historical non-uniform oval;
- h/H remains the additional uniform v1-style oval;
- p/P remains the v3-derived oval;
- v6 check and stacked-double geometry remains;
- keyboard, Unicode, generic PUA, style-specific PUA and semantic aliases remain stable;
- v6 remains preserved unchanged and is not rewritten.

## Frozen identities for this candidate

- v1 source SHA-256: `d0c0d50ad8ed33064e3af89b0976933b9eba8ead2d26234011c1a3233c246658`;
- v7 TTF SHA-256: `242d4d6507a9fc93d549bfa57cc42dd7ece571b5a6b5a6d83a9124ac7927a14c`;
- builder SHA-256: `16b81b11081967ed99c7fb2660ea5a85ab601f7dbfc8e0d5ad6615deda3ab0fd`;
- diagnostic source SHA-256: `fe4cf8790bc41e45bdb5f7e82800bee1af7dec4e35a4d8872e27e85c40351cbc`;
- guide SHA-256: `94af9f1105e1b09d0163619bc1678a3549dd344d0e9d8a8f0b7410b9c9058fa0`;
- diagnostic/reference PDF SHA-256: `78517da63cab0ffc764ff0419809387ca41c28bb77cf20f719960a38700458e3`;
- glyph count: 291;
- cmap mappings: 502;
- units per em: 2048;
- embedding: fsType=0.

The deterministic builder was rebuilt locally and produced byte-identical v7 TTF output.

## Visual/PDF QA

The 30-page diagnostic/reference PDF was rendered at high resolution and manually inspected. Dedicated QA includes the corrected 0-9 numeral page, large full-span mark interpretation page, all v1-derived marked containers, all v3-derived marked containers, generic marked aliases, and compact stress pages.

Matching page numbers were also inspected across Poppler and PDFium. The renderer-parity helper has a known lexicographic filename-ordering issue for 10+ pages, so its automatic page pairing was not treated as authoritative; matching-page renders were used for the critical checks.

## Approval boundary

- v1 remains immutable archival provenance;
- v4, v5 and v6 remain preserved historical candidates;
- v7 must not be silently rewritten after publication; later visual changes require v8;
- production PC-sheet rendering must not switch to v7 until the owner explicitly accepts this diagnostic/reference review;
- PR #85 remains DRAFT / DO NOT MERGE until the owner clears the visual gate.
