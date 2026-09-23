# Para Hoja de PJ Symbols v7 - guide and renderer reference

Status: **v7 final candidate; deterministic build validated; owner final visual approval pending**.

Author/owner: **Gustavo Muñoz**.  
Technical reconstruction/versioning assistance: OpenAI for DnD Custom Aid.

v7 supersedes v6 after the owner's final visual corrections while preserving v6 unchanged. v1 remains the immutable archival original; v4, v5 and v6 remain preserved historical candidates.

The owner accepted most of v6 but requested two final visual refinements: numerals `3`, `5`, `6`, `8` and `9` needed new shapes; and the precomposed marked containers still showed inconsistent line lengths, occasional outline overlap/white wedges, or strokes that did not fully occupy the usable interior. v7 addresses those points while preserving the accepted check geometry, mappings and broader symbol inventory.

## Design contract implemented in v7

v7 retains the four design groups requested by the owner:

1. **All historical v1 concepts A-E/a-e in the v1 design model.** In practice the v7 glyphs for A-E copy the authoritative v1 contours byte-for-byte and preserve the historical metrics.
2. **New symbols in a v1-derived design model.** These use the historical 2048-UPM / 0..1600 visual scale, heavy outline language, simple shapes and intentionally substantial strokes.
3. **The historical A-E concepts reinterpreted in a v3-derived geometric model.** The actual v3 generator retained A-E, so v7 derives this second model from v3's added circle/check/double-check/cross/diamond geometry and applies it consistently to the historical concepts.
4. **New symbols in that v3-derived geometric model.** These are slightly more compact and lighter than the v1-derived family.
5. **A small standard-use set and common Unicode symbol aliases** are included so the font is useful outside one exact sheet.

The training grammar is fixed as **Competent / Proficient = one conventional check (`✓`)** and **Expertise / Pericia = two copies of that same conventional check stacked vertically**, following the Android app's stacked expertise grammar using a clean stroke-based construction that removes the spikes and odd joins seen in earlier candidates.

Fillable/container shapes also have **precomposed shape-aware marked states** for `/`, `\`, `X`, `*`, `+`, `-`, `✓`, and dot. In v7, line endpoints are derived from each container's actual inner contour rather than from generic scaling. Slash/backslash therefore span the real fillable diagonal, X combines both real diagonals, + and - span the real central axes, and the historical asymmetric oval is handled from its own contour. The strokes stop at the usable interior instead of entering the outline, eliminating the white wedges/overruns seen in v7 while also avoiding arbitrary short lines. These are real glyphs rather than PDF overlay tricks.

## Provenance and deterministic identity

- authoritative v1 source: `v1/Para Hj De Pj.ttf`;
- v1 SHA-256: `d0c0d50ad8ed33064e3af89b0976933b9eba8ead2d26234011c1a3233c246658`;
- v7 file: `v7/Para Hoja de PJ Symbols v7.ttf`;
- v7 SHA-256: `242d4d6507a9fc93d549bfa57cc42dd7ece571b5a6b5a6d83a9124ac7927a14c`;
- deterministic builder SHA-256 at publication: `16b81b11081967ed99c7fb2660ea5a85ab601f7dbfc8e0d5ad6615deda3ab0fd`;
- diagnostic/reference PDF SHA-256 at publication: `78517da63cab0ffc764ff0419809387ca41c28bb77cf20f719960a38700458e3`;
- diagnostic/reference source SHA-256 at publication: `fe4cf8790bc41e45bdb5f7e82800bee1af7dec4e35a4d8872e27e85c40351cbc`;
- units per em: `2048`;
- glyph count: `291`;
- cmap mappings: `502`;
- embedding flags: `fsType = 0`;
- family: `Para Hoja de PJ Symbols v7`;
- PostScript name: `ParaHojadePJSymbolsV7-Regular`;
- version: `7.00`, dated `2026-09-18`.

## Historical A-E compatibility

| Historical key | v1/v7 historical meaning | v7 historical implementation | v3-derived equivalent key |
| --- | --- | --- | --- |
| `A` / `a` | round outline | exact v1 contour | `n` |
| `B` / `b` | square outline | exact v1 contour | `o` |
| `C` / `c` | historical narrow oval outline | exact v1 **non-uniform historical oval** | `p` |
| `D` / `d` | historical narrow oval filled | exact v1 historical silhouette | `P` |
| `E` / `e` | square filled | exact v1 contour | `O` |

Important: `C/D` are not replaced by a mathematically uniform ellipse. The additional uniform v1-style oval is deliberately kept as `h/H`, while `p/P` is the v3-derived oval reinterpretation.

## Human keyboard aliases

Keyboard aliases exist for convenient manual entry and QA. Renderer/domain state must not depend on these letters.

| Key | Family | Meaning | Typical sheet use |
| --- | --- | --- | --- |
| `A/a` | v1 exact | historical round outline | legacy compatibility / empty round marker |
| `B/b` | v1 exact | historical square outline | legacy checkbox base |
| `C/c` | v1 exact | historical non-uniform oval outline | legacy oval marker |
| `D/d` | v1 exact | historical non-uniform oval filled | legacy active oval |
| `E/e` | v1 exact | historical square filled | legacy active square |
| `f/F` | v1-derived | circle outline / filled | slots, counters, resources |
| `g/G` | v1-derived | square outline / filled | state / checkbox family |
| `h/H` | v1-derived | **uniform oval** outline / filled | additional oval state |
| `i/I` | v1-derived | diamond outline / filled | attunement / special state |
| `j/J` | v1-derived | triangle outline / filled | warning / state |
| `k/K` | v1-derived | hexagon outline / filled | resource / status |
| `l/L` | v1-derived | star outline / filled | inspiration / special |
| `m/M` | v1-derived | pip outline / filled | compact counters |
| `v/V` | v1-derived | check / **stacked double-check** | proficiency / expertise |
| `x/X` | v1-derived | boxed check / boxed stacked double | boxed proficiency / expertise |
| `z` | v1-derived | cross | failure / unavailable |
| `n/N` | v3-derived | circle outline / filled | slots, counters, resources |
| `o/O` | v3-derived | square outline / filled | state / checkbox family |
| `p/P` | v3-derived | oval outline / filled | C/D concept in v3 language |
| `q/Q` | v3-derived | diamond outline / filled | attunement / special state |
| `r/R` | v3-derived | triangle outline / filled | warning / state |
| `s/S` | v3-derived | hexagon outline / filled | resource / status |
| `t/T` | v3-derived | star outline / filled | inspiration / special |
| `u/U` | v3-derived | pip outline / filled | compact counters |
| `w/W` | v3-derived | check / **stacked double-check** | proficiency / expertise |
| `y/Y` | v3-derived | boxed check / boxed stacked double | boxed proficiency / expertise |
| `Z` | v3-derived | cross | failure / unavailable |

## Ordinary keyboard characters and Unicode aliases

The font now includes ordinary keyboard glyphs needed for labels, counters and compact notation:

- numerals: `0 1 2 3 4 5 6 7 8 9`;
- punctuation/operators: `- _ + = / \ | . , : ; * ! ? # % ( ) [ ] { } < > @ ^ ~`;
- A-Z/a-z remain intentionally reserved for the documented symbol aliases.

The numerals remain an owner-authored ordinary rounded/sans set rather than a seven-segment construction. In v7, `3`, `5`, `6`, `8` and `9` were redrawn again: `3` uses two clean open bowls without the v6 kink, `5` has a conventional flat top and rounded lower bowl, `6/9` use lighter coherent bowl/counter geometry, and `8` uses a balanced continuous outer silhouette with two clear counters. The accepted `0`, `1`, `2`, `4` and `7` forms are retained. Punctuation/operators remain simple geometric forms. No third-party typeface is imported into the owner-authored font.

| Standard Unicode | v7 glyph |
| --- | --- |
| `○` U+25CB | v3 circle outline |
| `●` U+25CF | v3 circle filled |
| `□` U+25A1 | v3 square outline |
| `■` U+25A0 | v3 square filled |
| `◇` U+25C7 | v3 diamond outline |
| `◆` U+25C6 | v3 diamond filled |
| `△` U+25B3 | v3 triangle outline |
| `▲` U+25B2 | v3 triangle filled |
| `☆` U+2606 | v3 star outline |
| `★` U+2605 | v3 star filled |
| `✓` U+2713 | v3 check |
| `✕` U+2715 | v3 cross |
| `♡` U+2661 | v3 heart outline |
| `♥` U+2665 | v3 heart filled |
| `◎` U+25CE | v3 double circle |
| `⊙` U+2299 | v3 bullseye |
| `⚡` U+26A1 | v3 bolt filled |

## Precomposed marked-container variants

Marks are encoded in this fixed order inside each container block. In v6 they are **full-span interior marks**, not tiny punctuation centered inside the container:

| mark index | mark | intended reading |
| ---: | --- | --- |
| `0` | `/` | lower-left to upper-right diagonal across the interior |
| `1` | `\` | upper-left to lower-right diagonal across the interior |
| `2` | `X` | both diagonals across the interior / crossed state |
| `3` | `*` | full interior asterisk / special state |
| `4` | `+` | full horizontal + vertical interior cross / positive state |
| `5` | `-` | full central horizontal interior stroke / negative state |
| `6` | `✓` | conventional check spanning the usable interior / completed |
| `7` | dot | selected / occupied / point state |

Three PUA ranges are provided:

- `U+E400+`: v1-derived marked containers;
- `U+E500+`: v3-derived marked containers;
- `U+E600+`: generic renderer aliases to the v3-derived marked containers.

Code point formula: `range_base + (shape_index * 8) + mark_index`.

### v1-derived marked-container shape order (`U+E400+`)

| shape index | shape | first code (`/`) | last code (`dot`) |
| ---: | --- | --- | --- |
| `0` | circle | `U+E400` | `U+E407` |
| `1` | square | `U+E408` | `U+E40F` |
| `2` | historical non-uniform oval | `U+E410` | `U+E417` |
| `3` | additional uniform oval | `U+E418` | `U+E41F` |
| `4` | diamond | `U+E420` | `U+E427` |
| `5` | triangle | `U+E428` | `U+E42F` |
| `6` | hexagon | `U+E430` | `U+E437` |
| `7` | star | `U+E438` | `U+E43F` |
| `8` | heart | `U+E440` | `U+E447` |
| `9` | shield | `U+E448` | `U+E44F` |
| `10` | drop | `U+E450` | `U+E457` |

### v3-derived/generic marked-container shape order (`U+E500+` / `U+E600+`)

| shape index | shape | v3 first-last | generic first-last |
| ---: | --- | --- | --- |
| `0` | circle | `U+E500-E507` | `U+E600-E607` |
| `1` | square | `U+E508-E50F` | `U+E608-E60F` |
| `2` | oval | `U+E510-E517` | `U+E610-E617` |
| `3` | diamond | `U+E518-E51F` | `U+E618-E61F` |
| `4` | triangle | `U+E520-E527` | `U+E620-E627` |
| `5` | hexagon | `U+E528-E52F` | `U+E628-E62F` |
| `6` | star | `U+E530-E537` | `U+E630-E637` |
| `7` | heart | `U+E538-E53F` | `U+E638-E63F` |
| `8` | shield | `U+E540-E547` | `U+E640-E647` |
| `9` | drop | `U+E548-E54F` | `U+E648-E64F` |

These precomposed states are intentionally limited to container shapes where the internal mark stays visually reliable. More complex semantic icons (flame, bolt, eye, hourglass, book, target) retain their outline/filled forms rather than receiving unreadable internal-mark permutations.

## Generic renderer PUA namespace

The primary renderer namespace uses the v3-derived geometric family because it is the cleaner default for new renderer work. Sheet families that deliberately need v1 styling should use the E200 range instead of changing semantics.

| PUA | Concept | Proposed / current use |
| --- | --- | --- |
| `U+E000` | circle outline | resource/slot/counter |
| `U+E001` | circle filled | active/spent resource |
| `U+E002` | double circle | legacy expertise/target |
| `U+E003` | square outline | checkbox base |
| `U+E004` | square filled | active square |
| `U+E005` | check | generic check |
| `U+E006` | cross | failure/unavailable |
| `U+E007` | boxed check | composite checked box |
| `U+E008` | diamond outline | attunement/state |
| `U+E009` | diamond filled | active attunement/state |
| `U+E00A` | oval outline | oval state |
| `U+E00B` | oval filled | active oval |
| `U+E00C` | stacked double-check | Expertise / Pericia |
| `U+E00D` | boxed stacked double-check | boxed Expertise |
| `U+E00E` | triangle outline | warning/state |
| `U+E00F` | triangle filled | active warning |
| `U+E010` | hexagon outline | resource/state |
| `U+E011` | hexagon filled | active hex state |
| `U+E012` | star outline | special/inspiration |
| `U+E013` | star filled | active inspiration |
| `U+E014` | pip outline | small counter |
| `U+E015` | pip filled | active small counter |
| `U+E016` | heart outline | HP/life |
| `U+E017` | heart filled | active HP/life |
| `U+E018` | shield outline | AC/defense |
| `U+E019` | shield filled | active defense |
| `U+E01A` | flame outline | fire/effect |
| `U+E01B` | flame filled | active fire/effect |
| `U+E01C` | bolt outline | initiative/energy |
| `U+E01D` | bolt filled | active initiative/energy |
| `U+E01E` | drop outline | resource/blood/water |
| `U+E01F` | drop filled | active drop |
| `U+E020` | eye outline | perception/concentration |
| `U+E021` | eye filled | active eye |
| `U+E022` | hourglass outline | duration/turn |
| `U+E023` | hourglass filled | active duration |
| `U+E024` | book outline | spellbook/notes |
| `U+E025` | book filled | active book |
| `U+E026` | target | attack/aim |
| `U+E027` | bullseye | marked/critical |

## Style-specific PUA ranges

The conceptual ordering is identical in both ranges. This is the preferred way to request a visual family without changing domain meaning.

| Concept | v1-derived PUA | v3-derived PUA |
| --- | --- | --- |
| circle outline | `U+E200` | `U+E300` |
| circle filled | `U+E201` | `U+E301` |
| double circle | `U+E202` | `U+E302` |
| square outline | `U+E203` | `U+E303` |
| square filled | `U+E204` | `U+E304` |
| oval outline | `U+E205` | `U+E305` |
| oval filled | `U+E206` | `U+E306` |
| diamond outline | `U+E207` | `U+E307` |
| diamond filled | `U+E208` | `U+E308` |
| triangle outline | `U+E209` | `U+E309` |
| triangle filled | `U+E20A` | `U+E30A` |
| hexagon outline | `U+E20B` | `U+E30B` |
| hexagon filled | `U+E20C` | `U+E30C` |
| star outline | `U+E20D` | `U+E30D` |
| star filled | `U+E20E` | `U+E30E` |
| pip outline | `U+E20F` | `U+E30F` |
| pip filled | `U+E210` | `U+E310` |
| check | `U+E211` | `U+E311` |
| stacked double-check | `U+E212` | `U+E312` |
| boxed check | `U+E213` | `U+E313` |
| boxed stacked double-check | `U+E214` | `U+E314` |
| cross | `U+E215` | `U+E315` |
| heart outline | `U+E216` | `U+E316` |
| heart filled | `U+E217` | `U+E317` |
| shield outline | `U+E218` | `U+E318` |
| shield filled | `U+E219` | `U+E319` |
| flame outline | `U+E21A` | `U+E31A` |
| flame filled | `U+E21B` | `U+E31B` |
| bolt outline | `U+E21C` | `U+E31C` |
| bolt filled | `U+E21D` | `U+E31D` |
| drop outline | `U+E21E` | `U+E31E` |
| drop filled | `U+E21F` | `U+E31F` |
| eye outline | `U+E220` | `U+E320` |
| eye filled | `U+E221` | `U+E321` |
| hourglass outline | `U+E222` | `U+E322` |
| hourglass filled | `U+E223` | `U+E323` |
| book outline | `U+E224` | `U+E324` |
| book filled | `U+E225` | `U+E325` |
| target | `U+E226` | `U+E326` |
| bullseye | `U+E227` | `U+E327` |

## Compatibility and semantic PUA aliases

| PUA | Stable semantic / compatibility meaning | Rendered glyph | Expected sheet reference |
| --- | --- | --- | --- |
| `U+E100` | legacy proficient marker | filled circle | compatibility only |
| `U+E101` | legacy expertise marker | double circle | compatibility only |
| `U+E102` | `CHECKBOX_EMPTY` | square outline | checkboxes |
| `U+E103` | `CHECKBOX_CHECKED_MARK` | one check | compose when needed |
| `U+E104` | `SLOT_AVAILABLE` | circle outline | spell slots / resources |
| `U+E105` | `SLOT_SPENT` | circle filled | spell slots / resources |
| `U+E106` | `COUNTER_EMPTY` | circle outline | generic counters |
| `U+E107` | `COUNTER_FILLED` | circle filled | generic counters |
| `U+E108` | `PROFICIENT_CHECK` | **one check** | skills / saving throws |
| `U+E109` | `EXPERTISE_DOUBLE_CHECK` | **two checks stacked vertically** | skills / custom skills |
| `U+E10A` | `BOXED_CHECK` | boxed check | boxed state |
| `U+E10B` | `BOXED_EXPERTISE` | boxed stacked double-check | boxed expertise |
| `U+E10C` | `DEATH_SAVE_SUCCESS` | check | death saves |
| `U+E10D` | `DEATH_SAVE_FAILURE` | cross | death saves |
| `U+E10E` | `INSPIRATION_ACTIVE` | filled star | inspiration |
| `U+E10F` | `ATTUNED_ACTIVE` | filled diamond | equipment / magic items |
| `U+E110` | `CONCENTRATION` | eye outline | spell/current effect state |
| `U+E111` | `DEFENSE_AC` | shield outline | armor / defense |
| `U+E112` | `HP_LIFE` | filled heart | HP / companion / forms |
| `U+E113` | `FIRE_EFFECT` | filled flame | fire/effect/damage |
| `U+E114` | `INITIATIVE_ENERGY` | filled bolt | initiative / energy |
| `U+E115` | `DURATION_TURN` | hourglass outline | effects / conditions |
| `U+E116` | `SPELLBOOK_NOTES` | book outline | spells / notes |
| `U+E117` | `ATTACK_TARGET` | target | combat |
| `U+E118` | `MARKED_CRITICAL` | bullseye | combat / status |

## Extended icons and probable future use

- **heart**: HP, life, vitality, companion health, form health;
- **shield**: AC, defense, ward, resistance summary;
- **flame**: elemental/fire effect, ongoing effect, charged state;
- **bolt**: initiative, energy, charge, reaction/quick state;
- **drop**: consumable pool, blood, water, potion-like resource;
- **eye**: perception, senses, visibility, concentration or watched state;
- **hourglass**: duration, remaining turns, cooldown/recovery;
- **book**: spellbook, notes, prepared reference, lore;
- **target**: attack, aim, selected target;
- **bullseye**: marked target, critical focus, priority state.

The list is intentionally useful across PC sheets, NPC/companion sheets, forms, spell/effect pages and future custom sheets rather than being narrowly tailored to one current PDF.

## Diagnostic/reference sheet

`DIAGNOSTIC_REFERENCE.pdf` is the owner visual-QA artifact. It contains: exact-v1 vs v3-derived comparisons, the historical/non-uniform oval vs the additional uniform oval, both core families, corrected conventional single checks and stacked doubles, v7-redrawn numerals/punctuation, all shape-aware precomposed marked-container variants in v1/v3/generic ranges, extended icons, standard Unicode aliases, keyboard mappings, generic/style-specific/semantic PUA references, proposed sheet uses, and large/compact stress tests.

No glyph should be considered visually approved solely because it appears in this guide or passes technical validation.

## Deterministic reproduction

Builder: `scripts/fonts/para-hoja-de-pj/v7/build_para_hoja_de_pj_v7.py`

```bash
python scripts/fonts/para-hoja-de-pj/v7/build_para_hoja_de_pj_v7.py \
  assets/fonts/owner/para-hoja-de-pj/v1/Para\ Hj\ De\ Pj.ttf \
  /tmp/Para_Hoja_de_PJ_Symbols_v7.ttf
```

The builder fails closed if the v1 source SHA is not the authoritative archival SHA or if the resulting v7 SHA differs from the frozen deterministic identity.

## Approval boundary

- v1 remains immutable archival provenance;
- v4 remains preserved as the previously published, visually rejected candidate;
- v5 remains preserved unchanged as an earlier candidate;
- v6 remains preserved unchanged as the prior almost-final candidate, superseded by v7 after owner visual feedback;
- v7 is technically valid, deterministic and feature-complete for this iteration but remains **owner final visual approval pending**;
- the renderer must not switch the production sheet family to v7 until the owner accepts the diagnostic/reference review;
- future changes after a published v7 must create the next derivative rather than silently rewriting v7.
