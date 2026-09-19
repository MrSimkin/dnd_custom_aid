# Checkpoint — PC Sheet PDF Hybrid Strategy 1 / Run 2

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Strategy:** 1 — Hybrid authoritative-template + native-geometry + separate vector Form/OCG overlay  
**Iteration:** Run 2 of minimum 3 before considering a strategy switch  
**Final Run-2 code head:** `e742635e9873eec6ad3568ba29c857176bc02405`  
**Scaffold:** `35453220465` / run #2647 — SUCCESS

## Run 2 purpose

Run 2 intentionally kept the representative geometry/content from Run 1 and changed the Form-layer font path only:

- Fira Sans Regular;
- Fira Sans SemiBold;
- Para Hoja de PJ Symbols v8;

are loaded with full embedding rather than subset embedding.

A saved-PDF preflight recursively inspects page/Form resources and fails if the hybrid fonts are not embedded.

## Technical result

**PASS.**

The final PDF reports:

- FiraSans-Regular — embedded=yes, subset=no;
- FiraSans-SemiBold — embedded=yes, subset=no;
- ParaHojadePJSymbolsV8-Regular — embedded=yes, subset=no.

The source-template fonts remain embedded as before.

The corrupted/gibberish Run-1 rendering is gone. This confirms the Run-1 failure belonged to the Form/font-resource path and that the Hybrid architecture can carry reliable embedded text and owner-symbol fonts.

## Independent visual audit

### Ruled narrative / table text

**PROVISIONAL PASS / KEEP FOR RUN 3.**

The 9.0–9.25 pt Fira Sans Regular treatment is materially more print-readable than the earlier ~6.5–7.5 pt Barlow Condensed Bold treatment.

Observed positives:

- text now uses the vertical space between rules rather than leaving excessive empty space;
- page-3 narrative text starts at the real right-column writing region rather than left of it;
- the ~2 pt intentional inset from the physical rule start reads as padding rather than displacement;
- baseline position leaves the text close to the rule without obvious collision;
- the regular-weight face is easier to read than tiny condensed bold.

This still needs owner/real-print confirmation before final approval, but there is no independent reason to shrink it again.

### `ESPACIOS` numeric value

**PROVISIONAL PASS / KEEP FOR RUN 3.**

The level-1 `ESPACIOS` value `4` is now 15 pt SemiBold and uses the printed header box confidently. It is materially clearer than the prior small value and appears optically centered.

### Check marker

**FAIL.**

The v8 check is visibly tiny and displaced below-left of the printed square.

Source square bbox from the authoritative PDF:

`x 28.205..37.874, top-y 327.187..339.474`

Run-2 v8 check bbox:

`x 26.769..33.902, top-y 332.493..345.054`

The target rectangle itself was correctly identified. The failure is in the glyph-fitting transform.

### Spell-slot filled oval

**FAIL.**

The source first spell-slot outline is the template's `C` glyph with bbox:

`x 80.646..94.710, top-y 442.688..460.560`

Run-2 v8 filled oval `D` bbox:

`x 78.132..88.621, top-y 450.507..462.358`

Again, the target rectangle is correct; the v8 glyph-fitting transform is wrong.

### Key learned cause

Run 2 proves that the remaining marker defects are **not coordinate-map errors** for these two examples.

The experiment currently scales markers using hard-coded design-space glyph bounds as though PDF text placement exposed those bounds directly. The saved PDF instead exposes text-span metrics governed by the embedded font's advance width and ascent/descent.

Therefore the next iteration should fit the rendered glyph span to the authoritative target rectangle using actual PDF font metrics:

- horizontal normalized width from `font.getStringWidth(...)/1000`;
- vertical normalized extent from font descriptor ascent/descent;
- baseline derived from target bottom minus scaled descent;
- small context-specific optical insets retained where appropriate.

This is a reusable renderer lesson, not a coordinate nudge.

## Run 3 proposed changes

Keep the successful Run-2 text and numeric geometry unchanged.

Change only the marker fitting primitive:

1. replace design-bounds-to-text-matrix placement with PDF-font-metric target-rectangle fitting;
2. preserve the authoritative source marker bboxes as target rectangles;
3. retain small optical insets:
   - check: modest inset inside square;
   - filled oval: very small inset so it nearly fills the source oval;
4. regenerate the same three representative pages;
5. preflight embedding again;
6. independently audit:
   - square check fit/optical centering;
   - filled oval fit/white-space ratio;
   - regression of text, `ESPACIOS`, Form layer and embedding.

## Current gate

- Hybrid Strategy 1 remains active.
- Run 1: FAIL / informative.
- Run 2: technical PASS, partial visual PASS, markers FAIL.
- Run 3 is required before any strategy-level decision.
- PR #85 remains **DRAFT / DO NOT MERGE**.
- No Custom visual family is approved.
