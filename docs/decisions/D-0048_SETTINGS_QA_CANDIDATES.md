# D-0048 — Settings QA candidates after V4 phone review

**Status:** Approved for next-build QA  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Context

V4 run #107 Settings QA clarified three presentation points:

- IBM Plex Sans Condensed is not liked by the owner, but the owner wants it **replaced by another condensed candidate rather than eliminating the second condensed-font slot**;
- text scaling at 115%/130% is broadly acceptable, but rows become misaligned when only one label wraps to two lines;
- the revised `Gris claro` theme still fails its intended identity and now reads somewhat green on the intended phone.

The owner requested a broader theme experiment rather than repeatedly tuning only `Gris claro`.

## Typography decision

For the next QA build:

- replace **IBM Plex Sans Condensed** with another condensed font candidate;
- use **Roboto Condensed** as the recommended replacement candidate for the next on-device audition;
- retain **Manrope**, **Sora**, and **Barlow Condensed** for continued evaluation.

The next QA set therefore remains two normal-width sans choices and two condensed choices:

1. Manrope;
2. Sora;
3. Barlow Condensed;
4. Roboto Condensed.

Roboto Condensed is a **QA candidate, not pre-approved final typography**. This is a presentation preference decision, not a technical limitation on IBM Plex.

## Text-scale decision

Keep the existing larger text-size options, including 115% and 130%.

The defect to fix is responsive geometry: when a label wraps, cells/controls participating in the same logical row must preserve coherent row height/alignment instead of allowing only the wrapped element to shift vertically.

Do not solve this by simply deleting the larger text options unless later device QA shows a separate unavoidable problem.

## Theme decision

### Retire `Gris claro`

Do not spend another iteration attempting to preserve the existing `Gris claro` identity. Replace it with **Gris**, designed as a clearly neutral monochrome gray theme without perceptible green/blue/brown tint.

### Add reversible QA candidates

The next build should additionally expose these themes for owner evaluation:

- **Cian oscuro** — dark teal/cyan family;
- **Azul noche** — deep navy/cool blue;
- **Verde bosque** — muted dark forest green;
- **Pergamino** — restrained warm light/paper theme;
- **Alto contraste** — accessibility-oriented high-contrast theme whose readability does not depend on hue alone;
- **Matriz** — near-black surfaces with vivid green text/accent language, inspired by the recognizable black-and-green *Matrix* aesthetic while remaining readable and avoiding decorative effects that interfere with use.

These are **QA candidates, not a commitment to retain every theme permanently**. The next phone pass should prune candidates that are redundant, unattractive, or visually unstable.

## Product principle

Theme variety is optional presentation, so it should remain reversible and must not destabilize layout or obscure semantic states. Distinct themes should differ enough to justify separate choices rather than becoming a long list of nearly identical palettes.
