# Para Hoja de PJ — owner-authored symbol font

Author/owner: **Gustavo Muñoz**.

On 2026-09-18 the owner explicitly authorized this project to publish the original owner-authored font in the public development repository and to modify, redesign and version derivatives for DnD Custom Aid. This authorization applies to this owner-authored font; it does not relax restrictions on third-party proprietary fonts.

## Version policy

- never overwrite an earlier version;
- keep one subfolder per version;
- every version has a human-readable mapping/use guide;
- v1 is immutable archival provenance;
- later versions may deliberately redraw geometry, but their guides must document compatibility and mapping choices;
- font symbols are rendering-layer conventions, never canonical game/domain state.

## Versions

| Version | Repository status | Purpose | Binary/source status | Approval status |
| --- | --- | --- | --- | --- |
| `v1` | Archival original | Exact 2007 owner-authored font | Exact original TTF committed; SHA-256 `d0c0d50ad8ed33064e3af89b0976933b9eba8ead2d26234011c1a3233c246658` | Historical source; immutable |
| `v2` | Historical renderer candidate | Preserve legacy mappings and add initial renderer PUA aliases | Reproducible legacy-expansion generator + mapping guide retained | Historical candidate; not current visual target |
| `v3` | Historical renderer candidate | Add the app-derived single/double-check training grammar while preserving prior mappings | Reproducible generator + guide retained | Historical candidate; superseded by v4 redesign direction |
| `v4` | Preserved rejected redesign candidate | Earlier modern coherent redesign attempt | TTF and deterministic builder preserved unchanged | **Technically valid; owner visual direction rejected; historical record only** |
| `v5` | Current final visual candidate | Exact-v1 + v1-derived and v3-derived complete symbol families, canonical checks, marked containers and ordinary characters | Deterministic builder + TTF + guide + diagnostic/reference PDF | **Technical QA passed; owner final visual approval pending** |

## v1 archival identity

- file: `v1/Para Hj De Pj.ttf`;
- size: 12,744 bytes;
- SHA-256: `d0c0d50ad8ed33064e3af89b0976933b9eba8ead2d26234011c1a3233c246658`;
- Git blob: `67cf8eb51dbcee7b4c68c8ef7e9e238ce010271c`.

The v1 file is committed byte-for-byte from the owner upload that passed the authoritative SHA-256 check. Do not rebuild, normalize, rename internally, repair metadata, change embedding flags or otherwise rewrite this binary.

## v4 current candidate

- file: `v4/Para Hoja de PJ Symbols v4.ttf`;
- SHA-256: `2c49b21c0616bab315f2e6f6b8c7be55e835fb5fa1db4e17e248861d4794b3e2`;
- Git blob: `cfc04ea4fb0ce53e8ee41c72df7e6281ef329c4a`;
- deterministic source/builder: `scripts/fonts/para-hoja-de-pj/v4/build_para_hoja_de_pj_v4.py`;
- complete mapping/use guide: `v4/GUIDE.md`.

v4 is a redesign, not merely v3 plus glyphs. It preserves the historical **meanings** of `A-E` / `a-e` on those keys while allowing their outlines to be modernized. It also provides legacy-refined and modern-clean families, paired outline/filled states for new shapes, renderer PUA aliases and the app training grammar: **Competent = one check; Expertise/Pericia = double check**.

The v4 binary is technically valid and reproducible, but its visual design remains a distinct owner-approval gate.


## v5 current final candidate

- file: `v5/Para Hoja de PJ Symbols v5.ttf`;
- SHA-256: `5e99861a9e9774c05da253b1bf0963eacd95ae50cef19ac5ffd2ba4a8362b588`;
- deterministic builder: `scripts/fonts/para-hoja-de-pj/v5/build_para_hoja_de_pj_v5.py`;
- complete mapping/use guide: `v5/GUIDE.md`;
- owner visual-QA artifact: `v5/DIAGNOSTIC_REFERENCE.pdf`;
- 291 glyphs / 502 cmap mappings.

v5 restores the agreed two-language design contract: historical A-E/a-e use the exact v1 geometry; new symbols have a v1-derived family; the historical concepts and new symbols also have a v3-derived geometric family. The single check is a conventional checkmark and Expertise/Pericia is two normal checks stacked vertically. Fillable/container shapes include precomposed /, \\, X, *, +, -, check and dot states. Ordinary digits and common punctuation/operators are also included.

v5 is technically validated and deterministic. It remains an owner visual-approval gate until the final diagnostic/reference PDF is explicitly accepted. v4 is preserved unchanged as historical provenance and is not the current visual target.
