# D-0052 — Character sheet data and migration rules

**Status:** Approved  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Context

This decision closes the persistence/data-shape questions raised before implementing the follow-up character-sheet build after V4 run #107 QA.

It supplements D-0046 through D-0051. Production coding remains blocked until the consolidated implementation specification, migration plan and QA checklist are reviewed by the owner.

## 1. One inventory model with optional special-item detail

Approved.

Ordinary and special/magic equipment are not separate disconnected ownership systems. A persistent inventory item has a common identity and ordinary fields, with optional richer special-item fields.

Common item fields include at least:

- name;
- quantity;
- optional weight;
- equipped state;
- notes;
- persistent user-defined order.

Special-item detail may additionally expose:

- long description;
- optional equipment/body location;
- manual `Sintonizado` state.

Turning an ordinary item into a special item exposes the richer fields without replacing the item identity.

If a future UI action converts a special item back to ordinary while special-only data would be lost, warn before discarding that data.

## 2. Custom currencies are character-level

Approved, explicitly rejecting the earlier campaign-level proposal.

Every PC sees and manages its **own** currency set.

Each character receives the ordinary default D&D currencies:

- cobre;
- plata;
- electro;
- oro;
- platino.

A character may add additional custom currencies for itself when needed. Example: `Diamante Astral` for a character in a campaign where that currency becomes relevant.

Do not automatically publish a PC's custom currency definition to every other PC in the campaign.

Currency values remain manual. No automatic exchange/conversion is required.

## 3. Structured measurements store imperial canonical values

Approved.

For structured distance/weight fields owned by the application:

- canonical stored unit is imperial (`ft`, `lb` as applicable);
- user-facing presentation shows imperial first and the game-friendly approximate metric value in parentheses;
- metric display is derived rather than stored as a second authoritative value.

Examples:

- `30 ft (9 m)`;
- `10 lb (5 kg)`.

Use the approved approximate D&D conversion convention, such as `1 ft (0,3 m)` and `1 lb (0,5 kg)`, not scientific-precision conversion.

Free-text descriptions remain free text and are not parsed as structured measurement rules.

## 4. Quick Magic slot-level presentation

Approved.

Quick Magic remains at the bottom of `General`.

Normal compact presentation shows only spell levels whose manually configured maximum-slot value is greater than zero.

A configuration/edit interaction exposes levels 1–9 so totals can be entered manually.

Each active spell level displays tappable spent/unspent slot marks/pips.

Slot state persists through character save and application restart, and can be manually reset through `Restaurar espacios` as approved in D-0047/D-0050.

## 5. `Aptitud mágica`

Approved manual selector values:

- FUE;
- DES;
- CON;
- INT;
- SAB;
- CAR;
- Otro;
- Ninguna.

Selecting an ability does **not** automatically calculate spell save DC or spell attack modifier in this build. Those remain manual quick-reference values.

This keeps Quick Magic a lightweight reference rather than a spellcasting character builder.

## 6. Proficiency-bonus migration

Approved.

`Bonificador por competencia` becomes a standard calculated value derived from total character level, with `Ajuste adicional` as the escape path for exceptions/homebrew.

Migration must preserve the old displayed V4 value exactly by converting any difference into the new adjustment.

Examples:

- level 4, old displayed PB `+2` -> calculated base `+2`, adjustment `0`, displayed total `+2`;
- level 4, old displayed PB `+4` -> calculated base `+2`, adjustment `+2`, displayed total `+4`.

Do not silently normalize unusual existing values to the standard table.

The derived-value interaction follows the same progressive-disclosure pattern approved in D-0046: compact final total, tap for calculation breakdown and `Ajuste adicional`.

## 7. Existing spell-save DC migration

Approved.

The existing stored `CD de salvación de conjuros` value is preserved exactly and its presentation moves out of `Referencia de combate` into Quick Magic.

Do not reset, recalculate or reinterpret the existing value merely because its UI location changes.

New `Combate` attacks/actions and `Equipo` collections start empty because no trustworthy pre-existing data exists from which to infer them.

## 8. Manual ordering for reusable lists

Approved.

Attacks/actions and inventory items preserve a user-defined order rather than being forcibly sorted alphabetically.

The UI should provide a simple reorder mechanism appropriate to phone use and persist the resulting order.

This allows tactical/use-frequency ordering such as a primary attack or condensed spell reference before less-used entries.

## 9. `Equipado` and `Ubicación` remain independent

Approved.

`Equipado` and special-item `Ubicación` represent different information and must not infer one another.

- `Equipado` answers whether the character is currently using/wearing the item.
- `Ubicación` records where the special item is placed/carried/associated when useful.

A location therefore does not automatically mean `Equipado = true`, and setting `Equipado` does not require or manufacture a location.

This preserves permissive cases such as carried, implanted, unusual-anatomy or homebrew items without turning the application into a body-slot legality engine.

## 10. Preservation principles

The follow-up migration must preserve all already-passing V4 character data and presentation preferences unless an explicit approved migration rule says otherwise.

In particular:

- do not duplicate authoritative values merely because they appear on more than one tab;
- new optional collections/default metadata must not make an existing character unsavable;
- migration must remain deterministic and testable;
- homebrew/exception values must remain representable through explicit adjustment/manual fields rather than being erased by standard-rule calculations.

## Next gate

The next step is a consolidated implementation specification, explicit migration plan and targeted owner QA checklist. That package must be reviewed and approved before production coding begins.
