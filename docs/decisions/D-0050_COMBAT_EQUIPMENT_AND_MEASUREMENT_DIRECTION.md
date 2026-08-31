# D-0050 — Combat, equipment and measurement direction

**Status:** Approved except where explicitly marked pending  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Context

During the pre-implementation design pass for the V4 follow-up character-sheet build, the owner approved adding two real tabs: `Combate` and `Equipo`. This decision records their agreed product direction before data-shape or production-code implementation.

The owner's custom PDF sheets under `assets/character-sheets/templates/` remain design/grouping references, not literal Android layouts.

## 1. `Combate` — reusable attacks and actions

Approved as a flexible persistent character-sheet list rather than a weapon-only subsystem.

Each entry should support, at minimum:

- `Nombre`;
- `Tipo`: Ataque / Acción / Acción adicional / Reacción / Otro;
- optional manual `Modificador de ataque`;
- free-text `Daño / efecto`;
- optional free-text `Alcance`;
- optional `Notas`.

Important semantic clarification from the owner: an entry is not necessarily a weapon attack. It may also be a **condensed/resumed version of a spell or another frequently used effect** whose purpose is to help the player during play.

For this slice, attack modifiers/damage remain manual. Do not introduce weapon legality, finesse/FUE/DES selection, proficiency inference, magic-weapon arithmetic or spell-builder logic merely because the list exists.

## 2. `Combate` — read-only quick-reference strip

Approved.

At the top of `Combate`, show read-only references to the same authoritative character values already used elsewhere, including:

- CA;
- Iniciativa;
- Velocidad;
- PG actuales / máximos / temporales.

These are **views of the same underlying values**, not duplicated stored combat-tab copies.

Persistent character-sheet combat references remain separate from future live encounter state such as turn order, current target or enemy conditions.

## 3. `Equipo` — general inventory

Approved baseline: one flexible inventory list rather than separate mandatory weapon/armor/ammunition systems.

Each ordinary inventory entry should support at minimum:

- `Nombre`;
- `Cantidad`;
- optional `Peso`;
- optional `Equipado` state;
- `Notas`.

Ammunition may be represented as an ordinary item with quantity unless a future concrete need justifies a richer ammunition model.

## 4. `Equipo especial` / magic items

Approved as a richer equipment structure for special and magic items that need meaningful descriptive/mechanical text.

The owner supplied a rendered full-page image of the relevant custom sheet. Its lower `EQUIPO ESPECIAL` block establishes the intended semantic grouping:

- `Ubicación`;
- a compact `Sintonizado` state/checkbox;
- `Nombre`;
- a broad `Descripción` field.

The paper reference provides common locations:

- Cabeza;
- Rostro;
- Cuello;
- Mano Izquierda;
- Mano Derecha;
- Brazo Izquierdo;
- Brazo Derecho;
- Pecho;
- Piernas;
- Pies;

and additional blank rows for custom/nonstandard locations.

Digital implementation does **not** need to reproduce the paper grid literally. The important data semantics are location + attunement state + item identity + rich description.

## 5. `Sintonización` — official rules terminology

The earlier wording `sincronizado` was an owner typo and is superseded.

The correct D&D/SRD term is:

- noun: **Sintonización**;
- item/creature state: **Sintonizado / sintonizada**;
- action: **sintonizarse**.

This terminology is explicitly used in both official Spanish SRD 5.1 and SRD 5.2.1.

Therefore the special-equipment checkbox/state should be understood as a **manual attunement/sintonización marker**, not as data synchronization or ownership synchronization.

For this character-sheet slice:

- store the item's `sintonizado` state explicitly;
- do not infer it automatically from item type or description;
- do not confuse it with app/device synchronization;
- do not yet hard-code an attunement-limit enforcement rule until the UX/data-shape decision is made, because the app's established philosophy is calculation assistance rather than rigid rules enforcement and campaign/class exceptions must remain representable.

## 6. Equipment location semantics

Approved direction from the paper reference: special items may have an explicit equipment/body location.

However, location is currently an **organizational character-sheet property**, not a hard equipment-legality engine.

Therefore, unless later approved:

- do not reject an item because another item already has the same location;
- do not infer AC, attacks or magical effects from location;
- allow the common predefined locations from the paper sheet;
- permit a custom location for unusual/homebrew anatomy or item placement.

This preserves the useful visual/body-slot organization of the owner's paper sheet without turning the app into an equipment rules validator.

## 7. Weight and measurement convention

Approved.

### Optional stored weight

- item weight is optional;
- automatically calculate carried-weight total from item weights that have actually been entered;
- missing weight contributes nothing and never blocks saving.

### Imperial + approximate metric presentation

The owner is migrating play toward the 5.5e-style approximate metric conversions while retaining D&D's imperial source values.

User-facing measurement presentation should show **imperial first, approximate metric in parentheses**, for example:

- `1 ft (0,3 m)`;
- `1 lb (0,5 kg)`.

Use the owner's intended game-friendly approximation convention rather than scientific/exact conversion precision. Spanish UI should use normal Spanish decimal presentation where applicable.

This convention applies where distance/weight values are presented by the application; free-text fields remain permissive and must not become a rules-enforcement parser.

## 8. Currency

Approved default quick currencies:

- cobre;
- plata;
- electro;
- oro;
- platino.

Values remain manual and no automatic currency conversion is required.

The currency model must also permit **adding custom/new currency types when a campaign needs them**, without displaying those currencies by default. Example supplied by the owner: a Spelljammer-like campaign may need an `Astral Diamond` or other campaign-specific currency.

The five ordinary D&D currencies are therefore defaults, not a closed enum that rejects other money systems.

## 9. `Equipado`

Approved as a simple equipped / not-equipped property for ordinary inventory items.

Do not infer AC, attacks, encumbrance legality or other mechanics from this state in this slice. It is useful persistent character state that future views may consume without committing those future calculations now.

For `Equipo especial`, explicit `Ubicación` is the richer representation; exact relationship between a generic `Equipado` flag and a special-item location will be resolved during the data-shape pass so redundant state is avoided.

## 10. Quick Magic slot marks

Approved interaction details:

- spell-slot totals remain manually entered;
- display tappable slot marks/pips for spent/unspent state;
- spent/unspent state persists through Save and application restart;
- provide a manual `Restaurar espacios` action to clear spent marks;
- do not automatically infer short/long rests or restore slots based on rest rules.

## 11. Still pending before coding these new domains

- exact richer-detail interaction for `Equipo especial` on phone (for example compact row + expandable/editor detail);
- whether the UI should show an informational count/reference for currently sintonized items, without hard rules enforcement;
- exact final fields/interaction for `Combate` entries beyond the approved minimum where useful;
- exact tab ordering/navigation behavior once all four implemented tabs are considered together;
- persistence schema/migration details and targeted QA criteria.

The semantics of the former `located + synchronized equipment` question are now resolved as **special equipment with explicit location and manual Sintonización state**.

No production implementation of `Combate` or `Equipo` is authorized until the remaining consequential questions are resolved and checkpointed.