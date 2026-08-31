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

Approved as a richer presentation/data mode of the **same underlying inventory concept**, not a completely disconnected ownership system.

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

### Approved phone interaction

On phone, special/magic equipment should use a compact card/row that prioritizes:

- `Nombre`;
- `Ubicación` when present;
- a recognizable `Sintonizado` indicator.

Tapping/expanding the item opens its richer detail/editor, including the full long description and other editable inventory properties.

### Approved shared inventory fields

Special items retain ordinary inventory properties where relevant:

- `Cantidad`;
- optional `Peso`;
- equipment state where not redundant with explicit location;
- `Notas`;

plus the special fields:

- optional `Ubicación`;
- `Sintonizado`;
- long `Descripción`.

Entered special-item weight contributes to the same carried-weight total as ordinary inventory.

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
- do not hard-enforce the ordinary SRD attunement limit because the app's established philosophy is calculation assistance rather than rigid rules enforcement and class/homebrew exceptions must remain representable.

### Approved informational count

The UI may show an informational count such as:

- `Sintonizados: 2`

but should **not** show a restrictive `2 / 3` meter and must not block a fourth or later item from being marked sintonizado.

## 6. Equipment location semantics

Approved direction from the paper reference: special items may have an explicit equipment/body location.

Location is an **optional organizational character-sheet property**, not a hard equipment-legality engine.

Therefore:

- do not reject an item because another item already has the same location;
- do not infer AC, attacks or magical effects from location;
- allow the common predefined locations from the paper sheet;
- permit `Otro` / a custom location for unusual anatomy, carried artifacts, implanted items or homebrew placement;
- allow location to remain blank when a meaningful body/equipment location does not apply.

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

For `Equipo especial`, explicit `Ubicación` is the richer representation. Avoid storing redundant contradictory state where possible: if the final data-shape pass can derive display semantics cleanly from an explicit special-item location, prefer that over requiring both a body location and a second mandatory equipped flag.

## 10. Quick Magic slot marks

Approved interaction details:

- spell-slot totals remain manually entered;
- display tappable slot marks/pips for spent/unspent state;
- spent/unspent state persists through Save and application restart;
- provide a manual `Restaurar espacios` action to clear spent marks;
- do not automatically infer short/long rests or restore slots based on rest rules.

## 11. Still pending before coding these new domains

The special-equipment semantics and phone interaction are now resolved. Remaining pre-code work includes:

- exact final fields/interaction for `Combate` entries beyond the approved minimum where useful;
- persistence schema/migration details;
- targeted QA criteria;
- any implementation-level choices that materially affect migration or data ownership.

Tab ordering/navigation is recorded separately in D-0051.

The semantics of the former `located + synchronized equipment` question are resolved as **special equipment with optional explicit location and manual Sintonización state**.

No production implementation of `Combate` or `Equipo` is authorized until the remaining consequential questions are resolved and checkpointed.