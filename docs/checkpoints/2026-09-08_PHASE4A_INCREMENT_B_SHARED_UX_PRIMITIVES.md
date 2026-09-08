# Phase 4A — Increment B Shared UX / Responsive Primitives

Date: 2026-09-08

## Status

**Increment B automated/shared-foundation checkpoint: COMPLETE / GREEN.**

This checkpoint closes the shared UX/responsive primitive boundary required by the reconciled Phase 4A successor plan before broad tab conversion.

It does **not** mean the successor UI has owner visual acceptance. In particular, the owner's prior observation that card movement felt stiff remains an explicit real-device audition item even though the shared drag implementation has been mechanically improved.

## Branch and verified product head

Implementation branch:

`implementation/phase4a-successor-cycle`

Verified product head:

`f0762d115c6bb577fb4785c210439613688e075c`

Full-gate workflow:

`34291047675` / Scaffold checks run `#981`

Result:

- backend check: PASS;
- shared/Kotlin tests: PASS;
- Android debug compilation/assembly: PASS;
- desktop compilation/build: PASS;
- Android debug APK artifact upload: PASS.

`main` remained untouched at:

`698d40b7da75bb7535d83f834db7044ef3e626a8`

## B1 — form-factor-aware shell

Added explicit layout-context semantics distinguishing:

- phone portrait;
- phone landscape;
- tablet portrait;
- tablet landscape.

Phone landscape no longer needs to inherit tablet composition merely because its width crosses a dp threshold. Responsive column selection now consumes the explicit form-factor context rather than treating `wide` as equivalent to tablet.

This is the shared foundation for later screen-by-screen phone-landscape repair and the separate tablet redesign. It does not claim the current tablet UI is accepted.

## B2 — spacing / density primitives

Successor spacing is centralized through the application preference layer and shared spacing helper so visual density can be applied consistently instead of screen-specific hard-coded spacing.

The owner-requested 40% spacing option remains available for later device audition. Visual density remains separate from minimum reliable touch targets.

## B3 — compact collection toolbar

The shared `CharacterCollectionToolbarV4` supports the common long-collection family:

- search;
- active filters;
- Manual/A–Z ordering where applicable;
- compact presentation;
- contextual use by real collection surfaces.

It is not dead scaffolding: Equipo and Conjuros already consume the shared toolbar family, giving a real high-risk collection consumer before broader successor conversion.

## B4 — shared card manipulation and drag-feel correction

Added `CharacterCardInteractionV4` as the shared card-reorder foundation with:

- whole-card long-press/drag support where safe;
- explicit drag pickup/movement/drop visual state;
- haptic pickup/step/drop hooks;
- shared reorder-enable rules for Manual/unfiltered/unsearched contexts;
- protected nested interactive controls.

### Owner stiffness observation

The owner's earlier real-device finding that card movement felt too stiff/mechanical was investigated rather than treated as cosmetic preference only.

A concrete implementation defect was found: an active pointer gesture could retain stale reorder callbacks/index state after the card moved during recomposition. Commit:

`975e10a87b7e0040ed01f81c3d866810d428ad26` — `fix: keep drag callbacks current during reorder`

The shared visual drag layer was also softened so pickup/movement/drop do not behave as an immediate rigid snap.

Notas is the representative proving consumer for whole-card drag: the bulky dedicated drag handle was removed, tap-to-edit remains, and long-press/drag begins from the non-interactive card body while delete/duplicate actions stay protected.

**Acceptance qualifier:** this is technically green, but card movement feel is **not owner-accepted yet**. Real-device audition remains controlling. If it still feels stiff, the next refinement should focus on card geometry / midpoint crossing and sibling displacement animation rather than blindly lowering the reorder threshold.

## B5 — editor / IME and numeric-input foundation

The shared `CharacterImeSafeEditorDialog` keeps save/cancel actions outside the scrollable editor body and applies IME/navigation-bar insets so required actions remain reachable with the keyboard visible.

Outside-tap focus dismissal clears the keyboard without dismissing the editor draft.

Added shared numeric normalization with focused tests. Single-digit numeric fields can replace an initial zero correctly instead of producing trapped leading-zero input (for example `05` normalizes to `5`). A real operational consumer is wired, so this is not test-only scaffolding.

## B6 — contextual help and provenance primitives

Added reusable contextual-help presentation with the approved global modes:

- `Siempre visible`;
- `ⓘ / tooltip-info`;
- `Oculto`.

The mode is now a persisted application preference. Existing installs/default contexts preserve explanatory text by default through `Siempre visible` rather than unexpectedly hiding help.

Added the compact structured provenance control:

`Tipo de origen | Origen específico`

with `Clase` as the default origin type where applicable and support for other origin categories/custom `Otro` paths.

This primitive does **not** justify mechanically stacking a new provenance field on top of Conjuros' functional spellcasting-source association system. Existing legacy `Fuente` fields must be migrated only when each surface's semantics are understood.

## Representative product evidence

Between the Increment A product head and this B head, the shared implementation changed the expected families:

- explicit layout context / adaptive shell;
- responsive preference selection;
- centralized spacing/density use;
- collection toolbar;
- card interaction/drag feedback;
- Notes representative whole-card drag consumer;
- shared numeric normalization + tests;
- help/provenance primitives;
- persisted global help mode.

## Acceptance boundary

This checkpoint means:

- shared form-factor, density, toolbar, drag, IME, numeric-input, help and provenance primitives exist;
- representative real consumers exercise the primitives;
- exact product head is automated-gate green;
- broad successor tab conversion can proceed without recreating these interaction families per screen.

It does **not** mean:

- card drag feel is owner-approved;
- prior build `40700` visual findings are closed;
- phone landscape is fully repaired on every surface;
- tablet/wide presentation is accepted;
- a new formal M6 candidate exists.

## Exact next action

Proceed to **Increment C — navigation, PC Settings and General/Habilidades**, in this order:

1. C1 character-first application entry;
2. C2 PC Settings information architecture;
3. C3 compact General identity/class presentation;
4. C4 General canonical state/reference projections;
5. C5 Habilidades inline custom-skill integration and compact passive row.

Keep `main` untouched. Preserve the drag-feel finding as a later owner phone audition criterion.