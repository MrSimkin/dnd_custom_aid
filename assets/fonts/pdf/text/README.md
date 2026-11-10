# PDF text-font QA candidates

- **Fira Sans Regular / SemiBold** — ordinary generated text and prominent values.
- **Barlow Condensed Bold** — compact/condensed roles where strong weight helps readability.
- **Kalam Bold** — handwritten/script candidate for deliberate handwritten-style content.

These files are deterministic QA resources, not final owner-approved mappings.

All are copied from the public Google Fonts repository and remain under their upstream SIL Open Font License 1.1 terms. License texts are stored in `licenses/`.

## V11 golden typefaces (owner approval 2026-10-29: «se agregan las fuentes»)

The approved V11 golden was generated with these exact files (advance widths match the golden PDF spans with 0.0 pt error):

| File | Golden role | Source | SHA-256 |
|---|---|---|---|
| `NotoSansDisplay-CondensedBold.ttf` | labels | Debian `fonts-noto-extra_20201225-6_all.deb` | `1f07421ff2325605e4b2e50fe0735bc74db03595a4bc3acafa2ee89e598510ab` |
| `NotoSans-Regular.ttf` | body text | Debian `fonts-noto-core_20201225-6_all.deb` | `89c3c497f618fdaa0b2d1e98fef93582f28c71debd2c4a8cdf41f190ced2909d` |
| `NotoSans-Bold.ttf` | bold body | Debian `fonts-noto-core_20201225-6_all.deb` | `e83493c945848ecd4a9ad0f6d19164541a0d3e23a9c952304a00a46e00272ac5` |
| `EBGaramond12-Bold.ttf` | names and titles (Georg Duffner) | Debian `fonts-ebgaramond-extra_0.016+git20230418-2_all.deb` | `41d27cd0a341f57220a432703962a3196c49d2b3d8bd6625c986425d59f03431` |
| `EBGaramond-Bold.ttf` | fallback glyphs only | `github.com/octaviopardo/EBGaramond12`, `fonts/ttf/EBGaramond-Bold.ttf` | `0cfed122e51e3fd44ccedaef7637efed6d5bdc4ad89a6117d70241510309a186` |

`EBGaramond12-Bold.ttf` has no accented letters or dashes (the golden itself lost the «á» of «mágico»). The renderer draws each character from it when it has the glyph and from the later EB Garamond Bold of the same project otherwise, so no letter is lost. All are SIL Open Font License 1.1; texts in `licenses/`.
