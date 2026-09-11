# Phase 4A — preqa.8 — P13 PC Settings redesign closure

**Date:** 2026-09-11  
**Branch:** `implementation/phase4a-successor-cycle`  
**Parent decision log:** `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md`  
**Status:** P13 CLOSED / READY FOR REPAIR SPEC  
**Implementation authorization:** NOT YET

## P13 — PC Settings visual / information-architecture redesign

The owner accepts the current PC Settings functionality/state behavior but rejects the existing card-heavy presentation and information architecture. The redesign must preserve working behavior underneath while substantially improving scanability, grouping, hierarchy and responsive use of space.

### Round 1 — accepted page architecture

1. Replace the wall-of-cards presentation with grouped sections of related settings.
2. Use section containers/rows rather than one large card per setting.
3. Main conceptual groups:
   - `Ficha y navegación` — what appears on the character sheet and how it is organized;
   - `Contenido personalizado` — custom attributes, skills, markers and special modules;
   - `Uso en mesa` — Table Mode, Supercompact and character haptic behavior;
   - `Personaje y datos` — lifecycle status and backup;
   - global `Configuración de la aplicación` remains visibly separate from character-specific settings.
4. Substantial management workflows open focused subpages/editors instead of dumping all management controls into the main PC Settings page. This includes at minimum tab ordering, custom attributes, custom skills and custom markers.
5. Simple settings remain directly operable on the main page, including spellcasting visibility, Inspiration visibility and Table Mode where its own activation contract permits.
6. Wide/tablet layouts may use multiple section columns when sensible rather than stretching every row across the full display; phone normally stacks sections vertically.
7. Keep immediate/automatic application of simple settings; no page-level `Guardar ajustes` button. Editors may retain their own local Save/Cancel when appropriate.

### Round 2 — accepted navigation / communication details

8. Rows that open subpages should expose a concise useful state summary/count when available rather than carrying a long permanent explanation. Examples: custom-skill count, custom-marker count, visible-module count, tab count.
9. `Módulos especiales` becomes a focused subpage. The main page exposes one concise navigation row/summary; detailed `Automático / Mostrar / Ocultar` controls remain inside the subpage with their existing semantics unchanged.
10. `Estado del personaje` stays directly accessible on the main page as a compact selector/navigation row. Existing confirmation requirements for sensitive lifecycle transitions such as Retirado/Muerto remain.
11. `Respaldo local` remains a direct action rather than a subpage. If temporarily unavailable because of pending structural state, the row communicates that clearly.
12. `Configuración de la aplicación` becomes a clean, visually separated navigation row near the bottom. Do not repeat a large explanation of theme/font/density/etc. on the PC Settings page when the destination already explains those options.
13. Simplify the header to the useful identity/context only, conceptually `← Ajustes de personaje` + character name. Remove permanent explanatory text such as `Los cambios de esta pantalla se guardan al aplicarlos.` when the behavior is already normal/obvious.
14. Do not overload every row with a title + paragraph + control. Obvious settings can be concise. Use a short secondary line only when state/context truly matters, or use the P12 contextual-help system for explanatory material.

## Transversal UX-writing rule surfaced by P13

**FULL APP AUDIT REQUIRED.**

The owner explicitly stated that simplifying explanations that are unnecessarily complicated is welcome **all across the app**. Therefore the repair pass must audit explanatory/helper copy across Player surfaces, not only PC Settings.

App-wide copy rule:

- prefer the shortest wording that preserves the required meaning;
- remove repetitive explanations when the surrounding UI already makes the behavior obvious;
- do not repeat destination-page explanations on navigation rows;
- keep important warnings, state-dependent explanations and destructive-action clarification;
- use P12 `Siempre visible` help or anchored rich tooltip where explanation is genuinely useful instead of making every normal row permanently verbose;
- simplification must not remove rule-critical distinctions, destructive-action warnings, or state-integrity guidance.

This adds P13 to the set of points marked `FULL APP AUDIT REQUIRED` for implementation/audit purposes. The full-app portion is explanatory/help copy consistency; the PC Settings information-architecture redesign itself remains specific to P13.

## Responsive / regression boundary

Implementation and QA must prove:

- phone main PC Settings page is grouped, scannable and no longer a wall of independent cards;
- wide/tablet layout uses available width intelligently without arbitrary stretching;
- substantial management functions navigate to focused subpages;
- direct toggles/selectors remain direct;
- module semantics, lifecycle confirmations, backup behavior, haptic behavior and existing state persistence remain functionally unchanged except where separately governed by P14/P15;
- no global Save button is introduced;
- global Application Settings remains visibly distinct from character-specific settings;
- explanatory copy is simplified across representative Player surfaces while preserving essential meaning.

## Explicit non-goals

- P13 does not decide/fix Table Mode activation mechanics; that remains P14.
- P13 does not decide/redesign Supercompact content; that remains P15.
- P13 does not authorize changing canonical state semantics of the existing settings merely to fit the new presentation.

**Status:** `CLOSED / READY FOR REPAIR SPEC`.
