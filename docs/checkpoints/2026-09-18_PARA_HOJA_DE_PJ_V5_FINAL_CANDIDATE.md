# Para Hoja de PJ v5 — final candidate checkpoint

Date: 2026-09-18

Status: **V5 FEATURE COMPLETE / TECHNICAL QA PASSED / OWNER FINAL VISUAL QA PENDING**

## Scope completed

- exact v1 A-E/a-e geometry preserved from the authoritative archival TTF;
- complete v1-derived extension family;
- complete v3-derived geometric family;
- conventional single checkmark and vertically stacked double-check for Expertise / Pericia;
- precomposed marked-container variants for slash, backslash, X, asterisk, plus, minus, check and dot;
- ordinary digits 0-9 and common punctuation/operators;
- extended reusable PC-sheet icon vocabulary;
- keyboard, Unicode, generic PUA, style-specific PUA and semantic aliases documented;
- deterministic TTF builder;
- deterministic 28-page diagnostic/reference PDF.

## Frozen identities for this candidate

- v1 source SHA-256: `d0c0d50ad8ed33064e3af89b0976933b9eba8ead2d26234011c1a3233c246658`;
- v5 TTF SHA-256: `5e99861a9e9774c05da253b1bf0963eacd95ae50cef19ac5ffd2ba4a8362b588`;
- builder SHA-256: `cf0d20d6421710652b16d8a90fb04eb725884a5f5649f6e0998fbb432d97d757`;
- diagnostic source SHA-256: `209db9244bc029136930487b7239ce35679992fcd5e461823639c57e9101656e`;
- guide SHA-256: `ec999b7bf119a4dc3ee326ee6b0c4a4c62a05395816512aa3426cea54cc0b376`;
- diagnostic/reference PDF SHA-256: `891f3a4a1b7dff62e1e3387437ea440542b431b2effb08ce23886042ddc331f8`;
- glyph count: 291;
- cmap mappings: 502;
- units per em: 2048;
- embedding: fsType=0.

The deterministic builder was rebuilt locally and produced byte-identical TTF output.

## Visual QA performed before publication

The diagnostic PDF was inspected structurally, rendered at high resolution, and manually reviewed. Critical pages specifically checked conventional single checks, vertically stacked doubles, boxed variants, ordinary numbers/punctuation, marked-container families, historical vs uniform oval behavior, and compact-size rendering. The check page was also compared manually in PDFium and Poppler.

The owner has not yet explicitly accepted this final candidate after these last corrections. Therefore this checkpoint does **not** authorize switching production sheet rendering to v5 or merging PR #85.

## Version/provenance rule

- v1 remains immutable archival provenance.
- v4 remains preserved unchanged as a technically valid but visually rejected historical candidate.
- v5 must not be silently rewritten after publication; later visual changes require a new derivative version.
- PR #85 remains DRAFT / DO NOT MERGE until the owner clears the visual gate.
