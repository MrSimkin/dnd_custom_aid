# Para Hoja de PJ Symbols v2 — Candidate Mapping

Status: **experimental renderer candidate; not visually approved yet**.

This candidate was generated from Gustavo Muñoz's original `Para Hj De Pj.ttf` while preserving the legacy glyph behavior.

## Provenance

- Original SHA-256: `d0c0d50ad8ed33064e3af89b0976933b9eba8ead2d26234011c1a3233c246658`
- Candidate v2 SHA-256: `fb539e276861a164614cf1666482bd302a5e8efcbca20fff73051d42614e6295`
- Original family name: `Para hj de pj`
- Candidate family name: `Para Hoja de PJ Symbols v2`
- Candidate PostScript name: `ParaHojadePJSymbolsV2-Regular`
- Candidate version string: `Version 2.00 2026-09-18; expanded renderer symbol set`

The original file is never overwritten by the generator.

## Legacy mappings preserved

| Legacy key | Shape |
| --- | --- |
| `A` / `a` | round outline marker |
| `B` / `b` | square outline marker |
| `C` / `c` | narrow oval outline marker |
| `D` / `d` | narrow oval filled marker |
| `E` / `e` | square filled marker |

## New generic Private Use Area glyphs

| Code point | Glyph / purpose |
| --- | --- |
| `U+E000` | round outline |
| `U+E001` | round filled |
| `U+E002` | double round outline |
| `U+E003` | exact legacy square outline (`B`) |
| `U+E004` | exact legacy square filled (`E`) |
| `U+E005` | check mark |
| `U+E006` | cross mark |
| `U+E007` | checked-state mark; compose with square when needed |
| `U+E008` | diamond outline |
| `U+E009` | diamond filled |
| `U+E00A` | exact legacy narrow oval outline (`C`) |
| `U+E00B` | exact legacy narrow oval filled (`D`) |

## Semantic renderer aliases

| Code point | Renderer meaning | Drawn glyph |
| --- | --- | --- |
| `U+E100` | `PROFICIENT` | round filled |
| `U+E101` | `EXPERTISE` | double round outline |
| `U+E102` | `CHECKBOX_EMPTY` | legacy square outline |
| `U+E103` | `CHECKBOX_CHECKED` mark | check mark; renderer composes with box |
| `U+E104` | `SLOT_AVAILABLE` | round outline |
| `U+E105` | `SLOT_SPENT` | round filled |
| `U+E106` | `COUNTER_EMPTY` | round outline |
| `U+E107` | `COUNTER_FILLED` | round filled |

The semantic code points are a rendering-layer convention only. Character data must never store these PUA characters as game state.

## Design notes

- The new round markers use the same 0–1600 vertical visual field as the legacy A–E symbols.
- The v2 font preserves the original 2048 units-per-em.
- The square aliases deliberately point to the owner's original `B`/`E` glyphs rather than redrawing them.
- A checked checkbox is intentionally compositional: render the legacy square plus the check mark.
- Proficiency/expertise and new shapes remain owner-QA candidates rather than approved final artwork.
- The original and generated TTF binaries remain outside the public repository while repository visibility is public.

## Reproduction

```bash
python scripts/fonts/expand_para_hoja_de_pj_v2.py "Para Hj De Pj.ttf" "Para Hoja de PJ Symbols v2.ttf"
```

Requires Python plus `fonttools`.

The generator is intentionally deterministic for the supplied original font. If the symbol set evolves, update both the generator and this mapping together.


## Later owner authorization

On 2026-09-18 the owner explicitly authorized publishing the original owner-authored font and versioned derivatives in the public development repository.

Therefore the earlier v2 note saying the TTF binaries should remain outside the public repo is historical, not current policy. The v2 mapping itself remains unchanged and preserved as a versioned record. New work continues under `assets/fonts/owner/para-hoja-de-pj/`.
