# V4 Settings QA — 2026-08-31

**Working branch:** `implementation/character-data-foundation`  
**QA target:** V4 run #107 APK

This file records owner feedback from the Settings / presentation QA pass. It supplements `2026-08-31_V4_QA_RESULTS.md`.

## Typography

- `IBM Plex Sans Condensed`: **REJECTED by owner preference as this condensed candidate**, but the owner does **not** want the condensed-font slot removed.
- Replace `IBM Plex Sans Condensed` in the next QA build with another condensed candidate.
- Assistant recommendation for the replacement QA candidate: **Roboto Condensed**. It should be evaluated on-device rather than treated as automatically accepted.
- `Manrope`, `Sora`, and `Barlow Condensed`: no rejection reported in this pass; remain viable candidates.

The intended next-build font audition therefore remains four choices:

1. Manrope;
2. Sora;
3. Barlow Condensed;
4. Roboto Condensed — replacing IBM Plex Sans Condensed.

## Text size / responsive layout

- `100%`: acceptable baseline.
- At `115%` and `130%`, the owner does **not** report a general scaling failure.
- Specific defect: when one label/control wraps to two lines, that element grows/moves but adjacent controls in the same logical row do not reflow to a common height/baseline, producing visible misalignment.
- Next build should treat this as a responsive row-layout issue: rows containing potentially wrapping labels must align against the tallest participating cell/control rather than letting one cell independently change geometry.

## Themes

### Current `Gris claro`

- **REJECTED again for current palette**.
- Owner reports the revised version now reads somewhat green rather than as neutral light gray.
- Current implementation uses a cool/slate-tinted palette; after two intended-device attempts, continuing to tune the same `Gris claro` identity is not preferred.

### Next-build theme audition

Owner requests broader theme experimentation. These are **QA candidates**, not all guaranteed permanent themes.

1. **Gris** — genuinely neutral monochrome gray; avoid blue/green/brown cast. Background/surface colors should have equal or near-equal RGB channels so the theme reads unmistakably gray.
2. **Cian oscuro** — deep charcoal/teal surfaces with cyan accents; conceptually analogous to `Morado oscuro` but in a dark cyan family.
3. **Azul noche** — deep navy / cool-blue dark theme, visually distinct from both default Dark and Cian oscuro.
4. **Verde bosque** — dark muted forest-green theme, avoiding fluorescent/game-console green.
5. **Pergamino** — warm light theme inspired by paper/parchment tones; should remain readable and restrained rather than yellowed/sepia-heavy.
6. **Alto contraste** — accessibility-oriented high-contrast theme with near-black/near-white surfaces/text and a highly visible accent; core legibility must not depend on hue alone.
7. **Matriz** — deliberately stylized near-black theme with vivid green text/accent language inspired by the black-and-green visual identity associated with *The Matrix*. It should remain usable as an app theme rather than reproducing decorative falling-code effects or sacrificing readability.

Recommendation for next build: **retire `Gris claro` and replace it with `Gris`**, then expose the additional themes above for device QA. Keep only the candidates the owner actually likes after testing.

## Next-build Settings target

- Replace `IBM Plex Sans Condensed` with **Roboto Condensed** as the second condensed-font QA candidate.
- Preserve `Manrope`, `Sora`, and `Barlow Condensed` for continued evaluation.
- Fix 115%/130% wrapping-row alignment without removing the larger scale options.
- Replace `Gris claro` with neutral `Gris`.
- Add `Cian oscuro`, `Azul noche`, `Verde bosque`, `Pergamino`, `Alto contraste`, and `Matriz` as reversible QA candidates.
