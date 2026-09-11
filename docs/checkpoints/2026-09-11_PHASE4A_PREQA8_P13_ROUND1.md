# Phase 4A — preqa.8 — P13 Round 1 consolidation

**Date:** 2026-09-11  
**Branch:** `implementation/phase4a-successor-cycle`  
**Parent decision log:** `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md`  
**Status:** P13 OPEN — ROUND 1 CONSOLIDATED  
**Implementation authorization:** NOT YET

## P13 — PC Settings visual / information-architecture redesign

This file is the durable round-level consolidation required before any further P13 questions. Functional behavior remains accepted; this point redesigns the page-level presentation and information architecture without changing working character-state semantics.

### Owner-approved Round 1 decisions

1. **Replace the wall-of-cards layout with grouped sections.** The main PC Settings page should be organized into meaningful sections rather than one large card per setting.

2. **Use section containers, not nested card soup.** A section may use one subtle shared surface containing multiple setting rows separated by spacing/dividers. Individual rows do not each need their own card shell.

3. **Primary section grouping is accepted:**
   - `Ficha y navegación` — what appears on the sheet and how it is organized;
   - `Contenido personalizado` — custom attributes, skills, markers and special modules;
   - `Uso en mesa` — Table Mode, Supercompact and character haptic behavior;
   - `Personaje y datos` — lifecycle status and backup;
   - `Configuración de la aplicación` remains visibly separate because it is global rather than character-specific.

4. **Substantial management workflows move to focused subpages/editors.** `Orden de pestañas`, `Características personalizadas`, `Habilidades personalizadas`, `Marcadores personalizados` and similarly substantial configuration flows should not dump their full management UI into the main PC Settings page. The main page shows a concise navigational row/summary and opens a focused management surface.

5. **Simple settings remain directly operable on the main page.** Examples include `Lanzamiento de conjuros`, `Inspiración` and `Modo mesa`; they should remain one-step switches/actions rather than forcing navigation to another page for a trivial state change.

6. **Tablet/wide-screen layout should use extra width intelligently.** Phone primarily stacks sections vertically. On sufficiently wide tablet layouts, independent section groups may use two columns when this improves scanning and density rather than stretching every setting row across the full screen.

7. **Automatic saving remains the interaction model.** Simple switches/selectors apply immediately. There is no global `Guardar ajustes` button. Focused editors/subpages may retain their own Save/Cancel where their editing workflow genuinely requires it.

### Scope classification

P13 is currently a **page-level PC Settings redesign**, not automatically a full-app redesign. However, implementation must reuse or establish shared setting-row/section primitives where sensible and must apply already-approved transversal rules from other points, including P9 adaptive editor behavior, P10 density/pseudo-icon rules and P12 contextual-help presentation.

If the remaining P13 discussion exposes a generic settings-page primitive that is also used outside PC Settings and requires equivalent correction elsewhere, P13 must then be upgraded to `FULL APP AUDIT REQUIRED` under the standing audit-trigger rule.

### Still open before P13 closure

- exact row presentation/summary behavior for navigational subpages;
- final treatment of `Módulos especiales` (inline versus focused subpage);
- placement and presentation of lifecycle/destructive-sensitive actions;
- final header/navigation treatment and section rhythm;
- regression boundary for phone portrait/landscape and tablet responsive layouts.

No implementation is authorized by this consolidation.