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
- **CI:** código `db2a8f71`, Scaffold [#40727033232](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40727033232) y QA [#40727032738](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40727032738) SUCCESS. **B2: PASS LOCAL FÍSICO (no G4).**

## B3 / C237 — Tabla ATAQUES de P1 con la tipografía del golden

- **Defecto medido:** en el golden (`v11_extremo_variante_A.pdf` p1), los rótulos de columna de ATAQUES son pequeños (Barlow ~7 pt) y el bono y el daño están alineados a la derecha de su columna. En la app los rótulos eran de Fira 9 pt y los valores estaban alineados a la izquierda.
- **Cambio (Desktop + Android):** rótulos con `drawP1Label` (Barlow 7 pt, el mismo de los demás rótulos de P1 desde C232), BONO y DAÑO / EFECTO alineados a la derecha. `drawAttackValues` coloca el bono a 4 pt del borde de su columna y el daño a 4 pt del borde de la fila, en P1 y en las filas C2 de B1. No cambian la geometría, las filas ni los textos.
- **Pruebas:** la prueba PDF de B1 verifica ahora las filas 1–6 de P1 y 7–8 de C2: fin del bono en x+153 y fin del daño en x+254, con 0,6 pt de tolerancia. El control de paridad cubre `drawAttackValues` y los nuevos rótulos.
- **Local:** pasan todas las pruebas, Android compila y pasan los 11 auditores. Contra la CI de C234: las mismas 828 páginas y ninguna palabra perdida.
- **CI:** código `f42302d3`, Scaffold [#40728393331](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40728393331) y QA [#40728393313](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40728393313) SUCCESS. **B3: PASS LOCAL FÍSICO (no G4).**

## B4 / C238 — C-N3 Notas digitales con la gramática del golden

- **Comparación medida:** en el golden (`v11_extremo_variante_A.pdf` p9) hay cartela «NOTAS · solo notas digitales», cada nota va en su propia tarjeta (título, regla fina, texto con margen interior) con un espacio entre tarjetas, y hay cartela «NOTAS MANUSCRITAS» sobre los renglones a mano. En la app era una lista continua con bandas en los títulos, sin cartelas ni tarjetas.
- **Cambio (Desktop + Android):**
  - Cartela NOTAS con «SOLO NOTAS DIGITALES» en la banda libre 703–716 pt bajo la cabecera (la misma de las cartelas C2).
  - Cada nota en una tarjeta de 528 pt, con el texto 6 pt hacia dentro y la banda del título a todo el ancho.
  - Una línea en blanco entre tarjetas (`leadingSeparationLines = 1`; es papel, no datos).
  - Cartela «NOTAS MANUSCRITAS» dentro del hueco de 24 pt ya reservado sobre los renglones a mano.
  - El tamaño de letra no cambia (ver Q2).
- **Pruebas:**
  - Nueva prueba PDF `g3DigitalNotesUseGoldenNotasCartelaAndInsetCardsAB`: cartela en cada página C-N3 y texto en x=48.
  - Nuevo control físico independiente `cn3_cards` en el auditor de envoltura: cartela presente y todo texto Fira de nota dentro de una tarjeta. Control negativo: sobre los PDF de la CI de C234 detecta 208 errores (106 páginas sin cartela ni tarjetas).
  - Control de paridad `test_v11_cn3_cards_contract.py`, registrado en el workflow.
- **Local:** pasan todas las pruebas, Android compila y pasan los 11 auditores (98 PDF / 840 páginas). Contra la CI de C234, los 96 PDF comparables mantienen sus 828 páginas y no pierden ninguna palabra.
- **Pregunta guardada:** Q2 (letra del golden en P4 / resto del cuerpo de texto) en `DECISIONES_PENDIENTES_PROPIETARIO.md`.
- **CI:** código `5727265b`, Scaffold [#40730632923](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40730632923) y QA [#40730632968](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40730632968) SUCCESS. Auditoría de envoltura de la CI: 98 PDF / 840 páginas, 0 errores (incluido `cn3_cards`). **B4: PASS LOCAL FÍSICO (no G4).**

## B5 / C239 — Evidencia de la matriz para reglas ya demostradas

Solo reglas con demostración real en la CI exacta de `5727265b` (Scaffold #40730632923, artefactos PDF 12834864175 y auditoría 12835067012). Etapa G3, alcance acotado:

| Regla | Qué se demuestra |
|---|---|
| P111 | ATAQUES de P1 en tres columnas (121 / 36 / 101 pt), bono estrecho, rótulos del golden; posiciones medidas en las filas 1–6 de P1 y 7–8 de C2 (B1/B3). |
| FIN01 | La última página física es siempre la hoja NOTAS manuscrita, sin datos digitales (auditor de envoltura en los 98 PDF + prueba Ilyra). |
| FIN02 | Un único título NOTAS con cabecera de logo/nombre (envoltura + visibilidad del logo). |
| FIN03 | Dos columnas de renglones y cuadrícula completa, con vectores idénticos al golden (`audit_v11_final_notes_golden`: 98/98 PASS). |
| FIN04 | Nada sigue a la hoja NOTAS final. |

Matriz: de 28 a **33 de 242** comprobaciones por etapa en PASS. Reglas globales en PASS: 0 de 106 (G4 sigue bloqueado). `check_v11_pre_qa_gate.py` pasa. No se marcó CN302 ni ninguna otra regla cuya prueba no demuestre exactamente su obligación.
