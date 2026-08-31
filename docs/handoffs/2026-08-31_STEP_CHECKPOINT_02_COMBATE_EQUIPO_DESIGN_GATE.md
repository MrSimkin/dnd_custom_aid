# Step checkpoint 02 — `Combate` and `Equipo` design gate

**Date:** 2026-08-31  
**Working branch:** `implementation/character-data-foundation`  
**Status:** SAFE RECOVERY POINT — QUESTIONS PENDING OWNER ANSWER — NO PRODUCTION CODING STARTED

## Context

D-0049 approved adding two real character-sheet tabs in the V4 follow-up build:

- `Combate`;
- `Equipo`.

They must contain useful durable data and must not be empty placeholders.

Project boundary remains controlling: persistent character-sheet information is separate from the future live DM combat tracker. Do not turn the `Combate` character tab into encounter initiative/conditions/turn-state machinery.

The stored owner paper-sheet references support attacks, ammunition/equipment/treasure and quick combat reference as useful character-sheet groupings, but they are inspiration rather than literal Android layout requirements.

## Assistant recommended minimum

### `Combate`

Keep the existing authoritative combat numbers in `Resumen`, but show a compact **read-only/reference strip** in `Combate` using the same values rather than duplicating storage:

- CA;
- Iniciativa;
- Velocidad;
- PG actuales / máximos / temporales.

Primary new durable content: a flexible **Ataques y acciones** list.

Recommended entry fields:

- Nombre;
- Tipo: `Ataque`, `Acción`, `Acción adicional`, `Reacción`, `Otro`;
- Modificador de ataque — optional/manual;
- Daño / efecto — free text;
- Alcance — optional/free text;
- Notas — optional/free text.

Recommendation: keep attack modifier/damage manual in this slice. Automatically deriving weapon attacks introduces ability choice, finesse/ranged rules, proficiency, magic bonuses and other exceptions that exceed the current simple calculation-assistance boundary.

Do not add encounter conditions, current turn, enemy target, combat initiative order or other live-combat state here.

### `Equipo`

Recommended minimum:

1. a **Dinero** block;
2. a flexible **Equipo / inventario** list.

Recommended inventory entry fields:

- Nombre;
- Cantidad;
- Peso — optional, decision pending;
- Equipado — optional boolean, decision pending;
- Notas.

Recommendation: ammunition can be an ordinary inventory item with quantity in this slice rather than requiring a separate ammunition subsystem/link to attacks.

Recommended currency direction: standard five D&D currencies as explicit quick fields, with clear Spanish labels/abbreviations; exact presentation can be compact.

## Questions for owner

### Q7 — `Combate`: attacks/actions model

Approve the recommended `Ataques y acciones` list and fields above?

If changing it, specify which fields/categories to add/remove.

### Q8 — `Combate`: reference strip

Should `Combate` repeat the key combat values as a **read-only view of the same underlying `Resumen` data**?

Assistant recommendation: **yes**. This is useful in play and does not duplicate durable state.

### Q9 — `Equipo`: inventory model

Approve a single flexible inventory list with `Nombre`, `Cantidad`, optional `Peso`, optional `Equipado`, and `Notas`, rather than separate weapon/armor/ammunition/treasure subsystems?

Assistant recommendation: **yes for this slice**.

### Q10 — Weight

Include optional item weight and an automatically calculated total carried weight when entered?

Assistant recommendation: **yes, but optional**. Missing weight contributes nothing to the displayed known total and must not block saving.

If approved, unit choice still needs definition: fixed D&D pounds (`lb`) or user-selectable/unit text.

### Q11 — Currency

Use the five standard D&D currency fields (`cobre`, `plata`, `electro`, `oro`, `platino`) as the initial money block?

Assistant recommendation: **yes**, manual numeric amounts, no automatic exchange/conversion needed.

### Q12 — Equipped flag

Should inventory entries have a simple `Equipado` marker?

Assistant recommendation: **yes** because it is cheap, useful and does not enforce equipment legality. It can later help combat/reference presentation without requiring that linkage now.

### Q13 — Quick Magic spent-slot persistence

D-0049 approved manual total slots + tappable spent/unspent marks. Should the spent/unspent marks persist through character save/app restart until the player manually restores them?

Assistant recommendation: **yes**. Losing slot expenditure because the app closes would make the tracker unreliable. A compact manual `Restaurar espacios` action can clear spent marks; it should not automatically infer rests.

## Exact next step

Wait for owner answers to Q7–Q13. Record them in Git before discussing any further data-shape/implementation details. Production coding remains blocked.
