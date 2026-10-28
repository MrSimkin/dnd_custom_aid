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
