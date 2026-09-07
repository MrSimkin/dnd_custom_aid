# Pre-QA owner visual audition

**Prepared:** 2026-09-07  
**Target review identity:** `0.4.0-preqa.7` / build `40700`  
**Purpose:** controlled visual/ergonomic audition before an exact replacement candidate is frozen for formal M6 QA.

This is intentionally **not** the full M6 functional checklist. It answers the visual questions that automated tests cannot settle: density, readability, fixed-control footprint, orientation behavior, keyboard ergonomics and whether owner-selected high column counts remain useful.

## 1. Preconditions

- install the exact identified APK from the Pass 07 checkpoint;
- open **Ajustes -> Acerca de** and verify version `0.4.0-preqa.7` and build `40700` before recording findings;
- use a representative character with enough data to populate Equipo, Rasgos, Conjuros, Notas, Gestión and at least one conditional module;
- perform the audition on one phone and one tablet when available, in both portrait and landscape;
- do not treat a preference combination as a defect merely because it is personally unattractive; record a defect when content overlaps, clips, becomes unusable, loses required controls or creates an unreasonable permanent footprint.

## 2. Stage A — typography shortlist

Keep application text at **100%**, spacing at **80%** and card columns at the defaults (phone 1/2, tablet 2/3). Compare these representative families first:

1. Manrope — current baseline;
2. Source Sans 3 — conventional high-legibility sans;
3. Roboto Condensed — compact reference;
4. IBM Plex Sans Condensed — alternate compact reference;
5. Mona Sans Condensed — bundled condensed candidate;
6. Geist — bundled modern candidate;
7. Inter — neutral UI reference;
8. Sora — geometric contrast candidate.

Shortlist no more than 2–3 before continuing. The remaining available fonts can be sampled only if none of these is satisfactory.

Check especially: long spell/trait names, numeric fields, badges, toolbar chips, accented Spanish text, disabled text and small metadata.

## 3. Stage B — application text scale

Using the top 1–2 font candidates, test **70%, 100%, 130%, 160% and 200%**. Intermediate values can be used later to refine a preferred result.

At each scale inspect:

- General and class identity summaries;
- Habilidades fixed selector/passive references plus scrolled detail;
- Gestión fixed operational state plus scrolled sections;
- Equipo and Rasgos sticky toolbars;
- Conjuros source selector + fixed toolbar + sticky level header together;
- Notas general text editor and titled cards;
- one conditional collection (Técnicas/Metamagia/Pactos, Artífice, Formas or Compañeros);
- PC Settings and the global Settings dialog itself;
- Supercompact.

Pass criterion: no overlap or inaccessible required action; scrolling/wrapping at large scales is acceptable.

## 4. Stage C — whitespace compactness

Return text scale to the preferred/near-100 value. Compare spacing **100%, 80% and 60%**.

Check that 60% reduces whitespace without shrinking intrinsic icon/control targets. Pay special attention to adjacent destructive/duplicate/favorite controls and to dense Gestión/Equipo rows.

## 5. Stage D — card-column opt-in and rotation

Test defaults first, then the user-selectable maxima:

| Form factor | Default | User maximum |
| --- | ---: | ---: |
| Phone portrait | 1 | 4 |
| Phone landscape | 2 | 5 |
| Tablet portrait | 2 | 5 |
| Tablet landscape | 3 | 6 |

Surface-specific readability caps are intentional. In particular:

- Combate: max 4;
- Rasgos: max 4;
- Notas: max 4;
- Equipo especial: max 3;
- Equipo ordinario: phone max 3, wide max 5;
- Monedas, ability grids and Supercompact use dedicated layout rules rather than the generic card-column preference.

The main new observation for Pass 07 is **ordinary Equipo at 5 columns on a wide device**. Verify that name, quantity/weight, state/meta and quick actions remain understandable and tappable.

Rotate the device with a scrolled list and an active search/filter. Confirm the layout reflows without losing the selected tab or creating overlap.

## 6. Stage E — keyboard and editor window behavior

On phone and tablet, open representative editors with the software keyboard visible:

- long Notas general content;
- titled note editor;
- Equipo editor;
- Rasgo editor;
- Conjuro editor;
- one conditional-module editor;
- a Gestión editor/preview.

Pass criterion: the focused field can be reached by scrolling and the cancel/save/apply action remains reachable. On wide/tablet persistent side panels, verify the panel itself scrolls above the IME rather than requiring the keyboard to be dismissed.

## 7. Stage F — fixed/sticky footprint

This is the most important perceptual check after Pass 03–07.

### Habilidades
The view selector and passive Perception/Investigation/Insight references remain visible while detail scrolls. Confirm the fixed band earns its height.

### Gestión
`Estado operativo` remains visible while conditions/resources/effects scroll. Check both normal HP and the 0-HP/death-save state, where the band grows.

### Conjuros
The source selector, compact fixed spell toolbar and current sticky level header can all coexist. This is the highest-risk permanent footprint. Test phone portrait at 100% and at a large text scale before accepting it.

### Long collections
Equipo, Rasgos and conditional-module toolbars remain sticky. Confirm they do not obscure the first visible row or consume unreasonable height when filters are active.

## 8. Finding format

Record each concrete finding with:

- device + approximate screen size;
- portrait/landscape;
- font + text scale + spacing scale + column preferences;
- tab/surface;
- exact visible problem;
- severity: **blocking**, **major**, **minor**, or **preference only**;
- screenshot when the problem is visual.

Do not combine unrelated observations into one finding. A preference-only item does not block formal M6 unless the owner explicitly promotes it.

## 9. Exit rule

The audition is complete when phone and tablet have each covered Stages A–F sufficiently to select a comfortable baseline and there are no unresolved blocking visual/IME/layout findings.

After that, the owner may explicitly request that the current build (or a repair successor) be frozen as the replacement formal M6 candidate. Until that instruction, this remains a pre-QA repair/audition line.
