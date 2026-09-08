# Phase 4A — contextual help presentation rule

**Date:** 2026-09-08
**Status:** OWNER-APPROVED CROSS-CUTTING UX RULE
**Branch:** `implementation/phase4a-successor-cycle`
**Product code changed:** no

## Controlling rule

The current application already contains many useful explanatory texts inside sections, cards and editors. The successor compactness redesign must **not** delete that explanatory value merely to save space.

Application Settings controls how the same canonical help content is presented:

1. **Siempre visible** — render the explanation inline in the relevant section/card/editor.
2. **ⓘ / tooltip** — hide the permanent paragraph but expose the same explanation from a circled-info affordance attached to the relevant section, field, formula or control.
3. **Oculto** — suppress ordinary explanatory UI for experienced users seeking maximum density.

## One explanation, three presentation modes

A feature owns **one canonical help/explanation payload**. The three modes change only presentation. Do not maintain separately authored inline and tooltip copies that can drift semantically.

This applies app-wide, including formula help, collection explanations, conditions, concentration, recovery/rest rules, Consumible/Munición semantics, Habilidades references, Dados decomposition help, PC Settings and Application Settings themselves.

## Compactness implication

The density goal is to remove wasted layout/chrome, not to make the application less understandable. A compact successor surface may replace a permanently visible explanation with `ⓘ`, but the explanation remains available unless the user explicitly chooses `Oculto`.

## Implementation dependency

Increment B6 of the successor plan must provide a shared help primitive backed by canonical help content and the global presentation preference. Surface redesigns must consume that primitive rather than deleting explanatory text ad hoc.
