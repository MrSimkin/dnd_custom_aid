# Para Hoja de PJ Symbols v3 — candidate use guide

v3 preserves all v1 legacy mappings and all v2 PUA mappings, then adds the double-check visual already used by the app for **Pericia / Expertise**.

## Legacy keys

| Key | Symbol |
| --- | --- |
| `A` / `a` | round outline |
| `B` / `b` | square outline |
| `C` / `c` | narrow oval outline |
| `D` / `d` | narrow oval filled |
| `E` / `e` | square filled |

## v3 additions

| Code point | Meaning |
| --- | --- |
| `U+E00C` | generic double check |
| `U+E108` | `PROFICIENT_CHECK_VARIANT` — single check |
| `U+E109` | `EXPERTISE_DOUBLE_CHECK_VARIANT` — double check |

The square remains compositional: legacy square + single/double check when a boxed marker is wanted.

## Reproduction

```bash
python scripts/fonts/para-hoja-de-pj/v3/expand_para_hoja_de_pj_v3.py \
  "Para Hj De Pj.ttf" \
  "Para Hoja de PJ Symbols v3.ttf"
```

The source original must first match the recorded v1 SHA-256.
