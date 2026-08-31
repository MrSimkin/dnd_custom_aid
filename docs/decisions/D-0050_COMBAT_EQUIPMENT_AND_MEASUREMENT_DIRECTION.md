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

## 4. Special equipment / magic items

Approved product need: `Equipo` must have a suitable path for **special equipment, particularly magic items that require meaningful descriptive/mechanical text**, rather than forcing them into an impoverished one-line ordinary-item representation.

The exact next-build UI/data shape for this richer description remains to be specified before coding (for example, expandable item detail versus another simple presentation). Do not assume that special equipment is a completely separate inventory ownership model unless later approved.

## 5. Located + synchronized equipment

**PENDING PRECISE INTERPRETATION.**

The owner identified a distinct concept described as **located + synchronized equipment** and directed the assistant to the lower section of page 2 of the custom PDF sheet as the clearest reference.

The relevant PDF is present in Git at:

- `assets/character-sheets/templates/Hoja de PJ v2 - 5.0 - Simkin.pdf`

The GitHub connector confirms the file and its page-2 PDF object, but the current connector surface does not render the repository PDF page visually. Existing textual UX notes did not preserve the lower equipment block in enough detail to infer its semantics safely.

Therefore:

- do **not** invent the meaning of located/synchronized equipment;
- do **not** implement ownership/location/synchronization behavior from the phrase alone;
- inspect a rendered/cropped view of the lower page-2 block, then describe the interpretation back to the owner for confirmation before finalizing the model.

This is deliberately pending because it could affect equipment location, shared ownership and/or future synchronization boundaries.

## 6. Weight and measurement convention

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

## 7. Currency

Approved default quick currencies:

- cobre;
- plata;
- electro;
- oro;
- platino.

Values remain manual and no automatic currency conversion is required.

The currency model must also permit **adding custom/new currency types when a campaign needs them**, without displaying those currencies by default. Example supplied by the owner: a Spelljammer-like campaign may need an `Astral Diamond` or other campaign-specific currency.

The five ordinary D&D currencies are therefore defaults, not a closed enum that rejects other money systems.

## 8. `Equipado`

Approved as a simple equipped / not-equipped property for inventory items.

Do not infer AC, attacks, encumbrance legality or other mechanics from this state in this slice. It is useful persistent character state that future views may consume without committing those future calculations now.

## 9. Quick Magic slot marks

Approved interaction details:

- spell-slot totals remain manually entered;
- display tappable slot marks/pips for spent/unspent state;
- spent/unspent state persists through Save and application restart;
- provide a manual `Restaurar espacios` action to clear spent marks;
- do not automatically infer short/long rests or restore slots based on rest rules.

## 10. Still pending before coding these new domains

- exact semantics/data shape of **located + synchronized equipment**, after visual review of the lower page-2 PDF reference;
- exact richer-detail interaction/data fields for special/magic equipment;
- exact final fields/interaction for `Combate` entries beyond the approved minimum where useful;
- exact tab ordering/navigation behavior once all four implemented tabs are considered together;
- persistence schema/migration details and targeted QA criteria.

No production implementation of `Combate` or `Equipo` is authorized until the remaining consequential questions are resolved and checkpointed.