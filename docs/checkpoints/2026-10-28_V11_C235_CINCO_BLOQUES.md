# C235–C239 — Cinco bloques seguidos sin preguntas (2026-10-28)

PR #173 DRAFT/NO MERGE. Anterior: [C234](2026-10-26_V11_C234_P3_STORY_GOLDEN_RHYTHM.md).

**Instrucción del propietario:** «hazlo con 5 bloques, como conversado». El Worker hace solo trabajo ya decidido por el contrato, el golden o el propietario. Cada decisión que corresponde al propietario se anota en [`DECISIONES_PENDIENTES_PROPIETARIO.md`](../pc-sheet-v11/DECISIONES_PENDIENTES_PROPIETARIO.md) y el desarrollo queda pospuesto. Cada bloque se cierra con pruebas, PDF reales y CI. G4 sigue en 106 NOT_VERIFIED.

## B1 / C235 — Ataques diferidos en la tabla ATAQUES del golden (C2)

- **Defecto medido:** un ataque que no cabe en las seis filas de P1, aunque mida lo que una fila, iba a C2 como registro de texto `ATAQUE … | BONO … | DAÑO …`. El golden C2 continúa ATAQUES en la misma tabla de tres columnas que P1.
- **Cambio (Desktop + Android):**
  - `attackFitsTableRow` es la misma regla de cabida de P1.
  - Los ataques diferidos que caben se imprimen en C2 como filas con bandas: nombre/rango, bono y daño en las columnas de P1 (121 / 36 / 101 pt).
  - Sus líneas ALCANCE y NOTAS van debajo.
  - Los que no caben conservan el registro de texto completo.
- **Pruebas:**
  - Nueva prueba PDF `g3DeferredFittingAttacksUseGoldenC2AttackTableRowsAB` con 8 ataques: el 7 y el 8 se imprimen en una sola línea, con el bono y el daño en las columnas del golden, cada ataque una sola vez y las notas reales en su línea.
  - Control de paridad Android/Desktop añadido a `test_v11_attacks_render_contract.py`.
- **Local:**
  - Pasan todas las pruebas Shared/Desktop/Python y Android compila.
  - Los 11 auditores pasan sobre los PDF.
  - Los 96 PDF anteriores no cambian: ningún personaje de prueba tenía ataques diferidos que cupieran. El cambio solo se ve en los 2 PDF nuevos.
- **CI:** código `432f3712`, Scaffold [#40725893896](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40725893896) y QA [#40725893911](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40725893911) SUCCESS. **B1: PASS LOCAL FÍSICO (no G4).**

## B2 / C236 — Margen interior de 4 pt del golden en la Historia de P3

- **Defecto medido:** en el golden (`v11_extremo_variante_A.pdf` p3), el texto de Historia empieza 4,0 pt dentro de la banda azul (banda x=39,7, texto x=43,7). En la app empezaba en el mismo borde (banda x=47, texto x=47), y lo mismo ocurría en las páginas de continuación (x=42).
- **Cambio (Desktop + Android):** solo el área de texto se reduce 4 pt por lado dentro de cada columna (`p3TextInsetPt`, con canal de 12+8 pt). Las bandas, renglones y páginas no se mueven. Se aplica a la Historia de P3 y a las dos continuaciones (campos e Historia). `addSection` acepta `gutterPt`.
- **Pruebas:** la prueba PDF de Historia exige ahora que cada línea empiece en x=51 (banda 47 + 4). El control de paridad de P3 exige el margen en ambos renderizadores.
- **Local:** pasan todas las pruebas Desktop y Python, Android compila y pasan los 11 auditores. Contra la CI de C234: las mismas 828 páginas, ninguna palabra perdida; solo cambian posiciones de texto.
