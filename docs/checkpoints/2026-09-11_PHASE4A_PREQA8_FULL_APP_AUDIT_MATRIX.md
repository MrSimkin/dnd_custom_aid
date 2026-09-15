# Phase 4A — preqa.8 — full-app audit matrix

**Date:** 2026-09-11  
**Branch:** `implementation/phase4a-successor-cycle`  
**Parent decision log:** `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md`  
**Status:** ACTIVE AUDIT-SCOPE CONTROL  
**Implementation authorization:** NOT YET

## Purpose

During point-by-point owner reconciliation, several findings that began as local QA defects exposed shared primitives, shared state rules, or interaction patterns used elsewhere in the Player application. Those points must not be implemented as one-screen patches.

This matrix is the durable marker requested by the owner. `FULL APP AUDIT REQUIRED` means that, before the repair is considered complete, implementation must identify all analogous usages/surfaces in the Player application, reconcile them with the approved rule, and add regression coverage sufficient to prove that the shared defect was not merely hidden on the originally observed screen.

This does **not** authorize redesigning unrelated product behavior. The audit follows the approved rule only.

## Standing rule for all remaining QA points

Whenever a later QA point reveals that the underlying problem is a shared primitive, shared data/state contract, shared presentation formatter, shared interaction grammar, or other reusable pattern, mark that point **`FULL APP AUDIT REQUIRED`** before implementation. Do not wait for the owner to rediscover the same defect on a second tab.

A point that is genuinely limited to one domain/surface remains a bounded/domain audit instead. The marker must be meaningful rather than applied indiscriminately.

## P1–P10 classification

### P1 — Canonical HP state

**Marker:** `FULL APP AUDIT REQUIRED`

**Why:** the accepted rule is explicitly `one datum / one canonical state` across General, Combate and every other HP projection/mutation path. Implementation must audit every read/edit/operation path for current HP, maximum HP and temporary HP, including future/alternate Player projections already present in the app.

### P2 — Daño / Curar high-frequency interaction

**Marker:** `BOUNDED / HP-OPERATION DOMAIN AUDIT`

**Why not full-app:** the direct `Daño / cantidad / Curar` UX is a combat operation, not a universal application interaction. Audit every HP operational path and exact-state correction path, but do not force unrelated domains into this interaction grammar.

### P3 — Custom Habilidades / custom Atributos as native participants

**Marker:** `FULL APP AUDIT REQUIRED`

**Why:** the finding expanded beyond one spacing defect into a shared rule: custom skills/attributes participate in the same visual, grouping, reference and interaction systems as standard ones. Audit all Player surfaces that render, select, group, reference, roll, summarize or configure skills/attributes so custom entities are not treated as second-class/special-case objects except for the approved identifying distinction.

### P4 — Structured attack damage editor

**Marker:** `BOUNDED / ATTACK-DAMAGE DOMAIN AUDIT`

**Why not full-app:** the accepted structured signed-component grammar applies to attack damage editing/rolling and any directly shared damage-component primitive, not arbitrary numeric or dice inputs elsewhere. Audit all attack/damage-component usages, but do not generalize the editor model to unrelated dice systems without a separate decision.

### P5 — Compact fixed/sticky regions + shared speed formatting

**Marker:** `FULL APP AUDIT REQUIRED`

**Why:** P5 explicitly established two transversal rules: persistent/fixed informational regions must use compact HUD/status-strip grammar appropriate to available vertical space, and speed must use one shared imperial-first + quick metric presentation formatter. Audit all fixed/sticky Player regions and every current Player speed projection, not only Combate.

### P6 — Direct drag-and-drop reorder

**Marker:** `FULL APP AUDIT REQUIRED`

**Why:** the accepted interaction is a shared manual-order collection grammar. Audit every Player collection/settings surface that exposes manual ordering or move-up/move-down controls and determine whether it should use the approved direct long-press/drag/drop behavior. Do not leave legacy special reorder modes or arrow-button ordering where the same direct-manipulation semantics apply.

**Known additional occurrence found during P10 audit:** `Orden de pestañas` currently uses literal `↑` / `↓` buttons. The owner has now explicitly approved direct drag-and-drop tab ordering; this occurrence is therefore covered by P6's shared reorder audit as well as P10's pseudo-icon audit.

### P7 — Canonical provenance / definition / character acquisition architecture

**Marker:** `FULL APP AUDIT REQUIRED`

**Why:** P7 deliberately expanded from Rasgos into a transversal canonical-reference architecture for classes/subclasses, species/subraces, backgrounds, feats, spells, items and analogous domains. Audit existing Player models/editors/projections for copied-name identity, redundant free-text structured provenance, catalog-implies-ownership mistakes, rename/delete behavior and SRD/custom schema divergence.

### P8 — Trasfondo photo UX

**Marker:** `BOUNDED / IMAGE-PERSISTENCE DOMAIN AUDIT`

**Why not full-app:** the visible tile/viewer design is specific to Trasfondo's two photo slots. Persistence/backup code must be audited anywhere those images travel, but this does not establish a universal photo-tile UX for unrelated future image features.

### P9 — Shared editor sizing / responsive editor layout

**Marker:** `FULL APP AUDIT REQUIRED`

**Why:** inspection confirmed the defect originates in the shared editor/dialog primitive. Audit every Player dialog/editor using that primitive (and equivalent legacy editors) for forced height, available-height response, IME safety, Save/Cancel reachability, bounded width and sensible wide-screen column opportunities.

### P10 — Application density + pseudo-icon consistency

**Marker:** `FULL APP AUDIT REQUIRED`

**Why:** the owner explicitly requires the density setting to govern app-controlled whitespace across the entire Player application, including dialogs/windows/editors, and the pseudo-icon decision requires a full Player sweep rather than fixing only currently known symbols.

Known pseudo-icon occurrences already confirmed during reconciliation include:

- shared collection add control using literal `+`;
- shared filter/check presentation using literal `✓` where it functions as iconography;
- `Orden de pestañas` using literal `↑` / `↓` move controls.

The implementation audit must search for analogous text/symbol-as-icon controls throughout Player UI, menus, dialogs, toolbars, settings and conditional modules. Legitimate textual actions such as `Guardar`, `Cancelar`, `Cerrar`, `Manual`, etc. remain text when text is semantically the intended control.

## Acceptance consequence

For every point marked `FULL APP AUDIT REQUIRED`, a repair is **not acceptance-complete** merely because the originally reported screenshot/tab now looks correct. The repair evidence must show that analogous Player usages were systematically inspected and either:

- updated to the approved shared rule; or
- explicitly classified as a legitimate exception with rationale.

This audit obligation must be carried into the final Phase 4A acceptance-repair implementation plan and regression checklist.