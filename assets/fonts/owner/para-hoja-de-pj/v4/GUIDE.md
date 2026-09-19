# Para Hoja de PJ Symbols v4 — mapping and use guide

Status: **modern redesign candidate; technical build validated; owner visual approval pending**.

Author/owner: **Gustavo Muñoz**.  
Modernization assistance: OpenAI for the DnD Custom Aid project.

v4 is a contemporary redesign informed by the archival v1 font. It is **not** v3 plus more glyphs and it does not promise geometric compatibility with v1. The historical meanings assigned to `A-E` / `a-e` remain on those same keys, but their outlines have been redrawn and normalized so they participate in a coherent modern symbol family.

## Provenance and build identity

- archival source v1 SHA-256: `d0c0d50ad8ed33064e3af89b0976933b9eba8ead2d26234011c1a3233c246658`;
- archival source v1 Git blob: `67cf8eb51dbcee7b4c68c8ef7e9e238ce010271c`;
- v4 TTF SHA-256: `2c49b21c0616bab315f2e6f6b8c7be55e835fb5fa1db4e17e248861d4794b3e2`;
- v4 TTF Git blob: `cfc04ea4fb0ce53e8ee41c72df7e6281ef329c4a`;
- canonical TTX SHA-256 embedded by the deterministic builder: `951c98add01ef66747be23535a946d25fe44a820087f8bd8c525e2df5219849c`;
- family: `Para Hoja de PJ Symbols v4`;
- style: `Regular`;
- PostScript name: `ParaHojadePJSymbolsV4-Regular`;
- version: `4.00`, dated 2026-09-18.

The owner explicitly authorizes public-repository publication and project modification/versioning of this owner-authored font. v1 remains the unchanged archival original.

## Style families

**Legacy-refined** modernizes the visual language descended from the 2007 font. It contains the historical A-E concepts plus newer renderer symbols interpreted in that refined legacy language.

**Modern-clean** reinterprets both old concepts and newer renderer symbols in a cleaner contemporary language. It is intended to give the renderer a second coherent family without changing domain/game state.

For newly paired shape symbols, the keyboard convention is generally **lowercase = outline / empty** and **uppercase = filled / active**. The historical `A-E` / `a-e` meanings are exceptions and must not be remapped merely to satisfy that convention.

## Historical keyboard continuity

These mappings preserve the v1 keyboard meanings while using redesigned v4 geometry.

| Keyboard key | Unicode | Glyph name | Style family | State | Intended semantic / use | Relationship to v1 |
| --- | --- | --- | --- | --- | --- | --- |
| `A` / `a` | `U+0041` / `U+0061` | `legacy_circle_outline` | Legacy-refined | Outline | Round empty marker | Same historical round-outline meaning; redesigned geometry |
| `B` / `b` | `U+0042` / `U+0062` | `legacy_square_outline` | Legacy-refined | Outline | Square empty marker / checkbox base | Same historical square-outline meaning; redesigned geometry |
| `C` / `c` | `U+0043` / `U+0063` | `legacy_oval_outline` | Legacy-refined | Outline | Narrow oval empty marker | Same historical narrow-oval-outline meaning; redesigned geometry |
| `D` / `d` | `U+0044` / `U+0064` | `legacy_oval_filled` | Legacy-refined | Filled | Narrow oval active marker | Same historical narrow-oval-filled meaning; redesigned geometry |
| `E` / `e` | `U+0045` / `U+0065` | `legacy_square_filled` | Legacy-refined | Filled | Square active marker | Same historical square-filled meaning; redesigned geometry |

## Legacy-refined expanded keyboard family

| Keyboard key | Unicode | Glyph name | Style family | State | Intended semantic / use | Legacy relationship |
| --- | --- | --- | --- | --- | --- | --- |
| `f` | `U+0066` | `legacy_circle_outline` | Legacy-refined | Outline | Empty round resource / slot / counter | Reuses refined historical round concept |
| `F` | `U+0046` | `legacy_circle_filled` | Legacy-refined | Filled | Active/spent round resource state | New paired filled form |
| `g` | `U+0067` | `legacy_square_outline` | Legacy-refined | Outline | Empty square state | Reuses refined historical square concept |
| `G` | `U+0047` | `legacy_square_filled` | Legacy-refined | Filled | Active square state | Reuses refined historical filled-square concept |
| `h` | `U+0068` | `legacy_oval_outline` | Legacy-refined | Outline | Empty narrow-oval state | Reuses refined historical oval concept |
| `H` | `U+0048` | `legacy_oval_filled` | Legacy-refined | Filled | Active narrow-oval state | Reuses refined historical filled-oval concept |
| `i` | `U+0069` | `legacy_diamond_outline` | Legacy-refined | Outline | Empty diamond state | New renderer symbol in legacy-refined language |
| `I` | `U+0049` | `legacy_diamond_filled` | Legacy-refined | Filled | Active diamond state | New renderer symbol in legacy-refined language |
| `j` | `U+006A` | `legacy_triangle_outline` | Legacy-refined | Outline | Empty triangle/status state | New renderer symbol in legacy-refined language |
| `J` | `U+004A` | `legacy_triangle_filled` | Legacy-refined | Filled | Active triangle/status state | New renderer symbol in legacy-refined language |
| `k` | `U+006B` | `legacy_hexagon_outline` | Legacy-refined | Outline | Empty hex resource/status state | New renderer symbol in legacy-refined language |
| `K` | `U+004B` | `legacy_hexagon_filled` | Legacy-refined | Filled | Active hex resource/status state | New renderer symbol in legacy-refined language |
| `l` | `U+006C` | `legacy_star_outline` | Legacy-refined | Outline | Empty special/status marker | New renderer symbol in legacy-refined language |
| `L` | `U+004C` | `legacy_star_filled` | Legacy-refined | Filled | Active special/status marker | New renderer symbol in legacy-refined language |
| `m` | `U+006D` | `legacy_pip_outline` | Legacy-refined | Outline | Empty pip/resource marker | New renderer symbol in legacy-refined language |
| `M` | `U+004D` | `legacy_pip_filled` | Legacy-refined | Filled | Active pip/resource marker | New renderer symbol in legacy-refined language |
| `v` | `U+0076` | `legacy_check` | Legacy-refined | Single check | Competent / proficiency check grammar | New renderer grammar in legacy-refined language |
| `V` | `U+0056` | `legacy_double_check` | Legacy-refined | Double check | Expertise / Pericia grammar | New renderer grammar in legacy-refined language |
| `x` | `U+0078` | `legacy_boxed_check` | Legacy-refined | Box + single check | Checked box / competent boxed marker | New composed renderer symbol |
| `X` | `U+0058` | `legacy_boxed_double_check` | Legacy-refined | Box + double check | Expertise boxed marker | New composed renderer symbol |
| `z` | `U+007A` | `legacy_cross` | Legacy-refined | Cross | Unavailable / failed / cancelled state where appropriate | New renderer symbol in legacy-refined language |

## Modern-clean keyboard family

| Keyboard key | Unicode | Glyph name | Style family | State | Intended semantic / use | Relationship to legacy concepts |
| --- | --- | --- | --- | --- | --- | --- |
| `n` | `U+006E` | `modern_circle_outline` | Modern-clean | Outline | Empty round resource / slot / counter | Modern reinterpretation of historical round concept |
| `N` | `U+004E` | `modern_circle_filled` | Modern-clean | Filled | Active/spent round resource state | Modern reinterpretation + paired filled form |
| `o` | `U+006F` | `modern_square_outline` | Modern-clean | Outline | Empty square / checkbox base | Modern reinterpretation of historical square concept |
| `O` | `U+004F` | `modern_square_filled` | Modern-clean | Filled | Active square state | Modern reinterpretation of historical filled-square concept |
| `p` | `U+0070` | `modern_oval_outline` | Modern-clean | Outline | Empty oval state | Modern reinterpretation of historical oval concept |
| `P` | `U+0050` | `modern_oval_filled` | Modern-clean | Filled | Active oval state | Modern reinterpretation of historical filled-oval concept |
| `q` | `U+0071` | `modern_diamond_outline` | Modern-clean | Outline | Empty diamond state | New clean renderer symbol |
| `Q` | `U+0051` | `modern_diamond_filled` | Modern-clean | Filled | Active diamond state | New clean renderer symbol |
| `r` | `U+0072` | `modern_triangle_outline` | Modern-clean | Outline | Empty triangle/status state | New clean renderer symbol |
| `R` | `U+0052` | `modern_triangle_filled` | Modern-clean | Filled | Active triangle/status state | New clean renderer symbol |
| `s` | `U+0073` | `modern_hexagon_outline` | Modern-clean | Outline | Empty hex resource/status state | New clean renderer symbol |
| `S` | `U+0053` | `modern_hexagon_filled` | Modern-clean | Filled | Active hex resource/status state | New clean renderer symbol |
| `t` | `U+0074` | `modern_star_outline` | Modern-clean | Outline | Empty special/status marker | New clean renderer symbol |
| `T` | `U+0054` | `modern_star_filled` | Modern-clean | Filled | Active special/status marker | New clean renderer symbol |
| `u` | `U+0075` | `modern_pip_outline` | Modern-clean | Outline | Empty pip/resource marker | New clean renderer symbol |
| `U` | `U+0055` | `modern_pip_filled` | Modern-clean | Filled | Active pip/resource marker | New clean renderer symbol |
| `w` | `U+0077` | `modern_check` | Modern-clean | Single check | **Competent = one check** | Clean version of app training grammar |
| `W` | `U+0057` | `modern_double_check` | Modern-clean | Double check | **Expertise / Pericia = double check** | Clean version of app training grammar |
| `y` | `U+0079` | `modern_boxed_check` | Modern-clean | Box + single check | Checked box / competent boxed marker | New composed clean symbol |
| `Y` | `U+0059` | `modern_boxed_double_check` | Modern-clean | Box + double check | Expertise boxed marker | New composed clean symbol |
| `Z` | `U+005A` | `modern_cross` | Modern-clean | Cross | Unavailable / failed / cancelled state where appropriate | Clean cross symbol |

## Renderer PUA aliases

PUA aliases are the stable renderer-facing namespace. Keyboard aliases exist for convenient human use and specimen entry; application/domain data must not store the PUA characters as game state.

| PUA | Glyph name | Style family | State | Renderer semantic / intended use | Legacy relationship |
| --- | --- | --- | --- | --- | --- |
| `U+E000` | `modern_circle_outline` | Modern-clean | Outline | Generic round outline | Modern round reinterpretation |
| `U+E001` | `modern_circle_filled` | Modern-clean | Filled | Generic round filled | Modern paired round state |
| `U+E002` | `modern_double_circle` | Modern-clean | Double outline | Generic / older expertise round grammar | New clean renderer symbol |
| `U+E003` | `modern_square_outline` | Modern-clean | Outline | Generic square / checkbox base | Modern square reinterpretation |
| `U+E004` | `modern_square_filled` | Modern-clean | Filled | Generic square active state | Modern filled-square reinterpretation |
| `U+E005` | `modern_check` | Modern-clean | Single check | Generic check | Current check primitive |
| `U+E006` | `modern_cross` | Modern-clean | Cross | Generic cross | New clean renderer symbol |
| `U+E007` | `modern_check` | Modern-clean | Single check | Checked-state mark alias; compose with box when needed | Alias of clean check |
| `U+E008` | `modern_diamond_outline` | Modern-clean | Outline | Generic diamond outline | New clean renderer symbol |
| `U+E009` | `modern_diamond_filled` | Modern-clean | Filled | Generic diamond filled | New clean renderer symbol |
| `U+E00A` | `modern_oval_outline` | Modern-clean | Outline | Generic narrow oval outline | Modern oval reinterpretation |
| `U+E00B` | `modern_oval_filled` | Modern-clean | Filled | Generic narrow oval filled | Modern filled-oval reinterpretation |
| `U+E00C` | `modern_double_check` | Modern-clean | Double check | Generic double check | Current double-check primitive |
| `U+E100` | `modern_circle_filled` | Modern-clean | Filled | `PROFICIENT` — older round-marker semantic alias | Preserved renderer compatibility alias |
| `U+E101` | `modern_double_circle` | Modern-clean | Double outline | `EXPERTISE` — older round-marker semantic alias | Preserved renderer compatibility alias |
| `U+E102` | `modern_square_outline` | Modern-clean | Outline | `CHECKBOX_EMPTY` | Checkbox base |
| `U+E103` | `modern_check` | Modern-clean | Single check | `CHECKBOX_CHECKED` mark; compose with box | Checkbox mark |
| `U+E104` | `modern_circle_outline` | Modern-clean | Outline | `SLOT_AVAILABLE` | Resource/slot empty state |
| `U+E105` | `modern_circle_filled` | Modern-clean | Filled | `SLOT_SPENT` | Resource/slot active-spent state |
| `U+E106` | `modern_circle_outline` | Modern-clean | Outline | `COUNTER_EMPTY` | Counter empty state |
| `U+E107` | `modern_circle_filled` | Modern-clean | Filled | `COUNTER_FILLED` | Counter active state |
| `U+E108` | `modern_check` | Modern-clean | Single check | `PROFICIENT_CHECK_VARIANT` — **Competent = one check** | App training grammar |
| `U+E109` | `modern_double_check` | Modern-clean | Double check | `EXPERTISE_DOUBLE_CHECK_VARIANT` — **Expertise / Pericia = double check** | App training grammar |

## Technical font characteristics

- contemporary TrueType/OpenType-compatible `.ttf` structure generated through FontTools;
- units-per-em: `1000`;
- 50 glyphs in the compiled v4 candidate;
- common advance width: `1904`, common left side bearing: `0`;
- hhea ascender `1900`, descender `-148`, line gap `0`;
- OS/2 version `4`, weight class `400`, width class `5`;
- OS/2 typo ascender `1900`, typo descender `-148`, typo line gap `0`;
- Windows ascent `1900`, descent `148`;
- embedding flags: `fsType = 0` (no embedding restriction set by v4);
- deterministic fixed font timestamps corresponding to 2026-09-18;
- table checksums and whole-font checksum are regenerated by FontTools;
- all mappings are Unicode cmap mappings; renderer-specific mappings use the Private Use Area.

The legacy-refined outline square had an incorrect contour winding in an earlier local v4 draft and rendered filled. That winding defect was corrected before the committed v4 candidate was frozen.

## Deterministic reproduction

Canonical source/builder:

`scripts/fonts/para-hoja-de-pj/v4/build_para_hoja_de_pj_v4.py`

The builder embeds a gzip-compressed **TTX source representation**, not the compiled TTF. It verifies the TTX digest, imports it with timestamp recalculation disabled, saves with deterministic table ordering, and then verifies both the compiled SHA-256 and Git blob ID. A mismatch deletes the produced output and fails the build.

Example:

```bash
python scripts/fonts/para-hoja-de-pj/v4/build_para_hoja_de_pj_v4.py \
  /tmp/Para_Hoja_de_PJ_Symbols_v4.ttf \
  --emit-ttx /tmp/Para_Hoja_de_PJ_Symbols_v4.ttx
```

Expected output identity:

```text
SHA-256: 2c49b21c0616bab315f2e6f6b8c7be55e835fb5fa1db4e17e248861d4794b3e2
Git blob: cfc04ea4fb0ce53e8ee41c72df7e6281ef329c4a
```

Requires Python and `fonttools`.

## Approval boundary

The compiled v4 candidate is technically validated and reproducible, but **its visual design is not owner-approved merely because the build is valid**. The v4 specimen is a separate owner visual gate. Likewise, no complete PC Sheet PDF visual family is approved by this guide.
