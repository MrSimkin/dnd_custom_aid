# Special equipment data-shape discussion checkpoint

Date: 2026-08-31  
Branch: `implementation/character-data-foundation`  
Status: pre-coding design discussion; no production implementation authorized.

## Durable state entering this step

- D-0050 now resolves the former `located + synchronized equipment` ambiguity as **special equipment with explicit location and manual Sintonización state**.
- Correct official Spanish rules terminology is `Sintonización` / `Sintonizado` / `sintonizarse`.
- The rendered owner sheet reference shows `UBICACIÓN` + checkbox + `NOMBRE` + wide `DESCRIPCIÓN`, with common body/equipment locations and extra custom rows.
- Location is organizational in this app slice; do not hard-enforce equipment legality or one-item-per-location.
- No production code is to be changed until the remaining data/UX questions are answered and checkpointed.

## Current recommended model to discuss

Prefer **one underlying equipment item model**, not duplicated ordinary/special copies. A special item would expose richer optional fields while ordinary items remain compact.

Potential shared fields:

- Nombre
- Cantidad
- Peso
- Equipado
- Notas

Potential special-item fields:

- Es especial / richer-detail mode
- Ubicación (optional predefined or custom)
- Sintonizado (manual boolean)
- Descripción extensa

A special item with entered weight should contribute to the same carried-weight total as any other item.

## Owner questions for this step

1. Should `Equipo especial` be a richer view/state of the **same inventory item** (recommended), rather than a second disconnected inventory list?
2. On phone, should special items show as a compact card/row (`Nombre`, optional `Ubicación`, Sintonización indicator) that opens/expands to edit the full description (recommended)?
3. For Sintonización, should the UI show only an informational count such as `Sintonizados: 2` (recommended), or should it also expose a capacity such as `2 / 3`? Hard blocking at three is not recommended because exceptions/homebrew must remain representable.
4. Should `Ubicación` be optional, with predefined choices from the paper sheet plus `Otro`/custom (recommended)? This permits special items that are carried, floating, implanted, worn unusually, etc.
5. Should special items keep the ordinary `Cantidad`, `Peso`, `Equipado` and `Notas` fields as applicable, so they remain fully part of inventory/weight rather than becoming description-only records (recommended)?

## Next step after owner answers

Checkpoint answers first. Then discuss tab ordering/navigation and only afterward persistence/migration/QA shape.
