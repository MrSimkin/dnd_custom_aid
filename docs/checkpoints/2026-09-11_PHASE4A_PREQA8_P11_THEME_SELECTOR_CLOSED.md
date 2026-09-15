# Phase 4A — preqa.8 — P11 Theme selector closure

**Date:** 2026-09-11  
**Branch:** `implementation/phase4a-successor-cycle`  
**Parent decision log:** `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md`  
**Status:** `CLOSED / READY FOR REPAIR SPEC`  
**Implementation authorization:** NOT YET

## P11 — Theme selector: name + three-color shorthand + one realistic/live preview

### QA problem

The 40800 theme selector retained theme names but lost the earlier useful visual shorthand, while selection still uses a literal text checkmark. The owner wants fast visual identification without returning to a rejected miniature-theme-design concept.

### Owner-approved target behavior

1. Every theme option displays the theme name plus a simple three-color palette shorthand.
2. The three swatches represent, conceptually, the theme's main background, ordinary surface/card, and primary/accent color. They are a quick identity shorthand, not a miniature fake UI preview.
3. Keep one larger realistic/live `Vista previa · ficha` for the currently selected theme. Selecting a theme updates that preview immediately.
4. Theme selection remains immediate; no separate `Aplicar` step is added.
5. `Sistema` resolves visually to the actual currently active system appearance: if Android currently resolves the app to dark appearance, its three-color shorthand and realistic preview show that resolved dark appearance; if light, they show the resolved light appearance. Do not use a half-light/half-dark abstract representation that differs from the actual current result.
6. The selected-theme indicator uses a proper graphical/shared indicator rather than a literal text `✓`.
7. Theme option cards use responsive columns: phone layouts use as many columns as remain comfortably readable (two columns is a reasonable portrait baseline), while wider/tablet layouts may use more columns instead of stretching a few giant cards across the display.
8. The larger realistic preview remains separate from the compact theme option grid.

### Scope / transversal audit classification

**Audit scope:** `BOUNDED / DOMAIN AUDIT` for the theme-selector behavior itself.

P11 does not introduce a new full-app primitive requirement beyond already-accepted shared rules. Its literal text `✓` is one concrete offender covered by **P10 — FULL APP AUDIT REQUIRED: pseudo-icons / text icons**. Implementation must therefore fix the local P11 instance as part of the P10 app-wide icon audit rather than treating it as an isolated exception.

Responsive column behavior should reuse the accepted P9 responsive-layout principles where applicable rather than inventing a separate device taxonomy.

### Explicit non-goals / rejected alternatives

- Do not use a complex miniature fake UI preview inside every theme card.
- Do not show only theme names with no visual shorthand.
- Do not require a separate Apply/Save action for theme selection.
- Do not make `Sistema` an abstract dual-mode palette disconnected from the system appearance currently being resolved.
- Do not retain literal `✓` text as the selection indicator.

### Regression boundary

UI/integration coverage must prove: every theme option presents name + three swatches; selection applies immediately; the larger realistic preview updates to the selected theme; `Sistema` reflects the currently resolved light/dark appearance; selected state uses a proper graphical indicator; and theme option columns adapt sensibly between phone and wide/tablet layouts without sacrificing readability.

**Status:** `CLOSED / READY FOR REPAIR SPEC`.
