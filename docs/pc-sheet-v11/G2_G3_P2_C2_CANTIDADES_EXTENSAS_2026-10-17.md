# V11 G2/G3 — Equipo y Tesoro: cantidades extensas sin pérdida, P2 a C2

**Fecha:** 2026-10-17 (Chile). **PR:** [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173), OPEN/DRAFT/unmerged. **Código funcional:** [`cfdf9c9a7def0382e96adae6032c5ccdb4ebf702`](https://github.com/MrSimkin/dnd_custom_aid/commit/cfdf9c9a7def0382e96adae6032c5ccdb4ebf702). Se mantiene la implementación G0–G6, el contrato V11 aprobado y la matriz vigente; `main`, los cuatro exportadores legacy y ocho golden PDF no se tocaron.

## Defecto y cambio real

La composición previa de las tablas dobles P2/C2 asignaba exactamente **36pt** a cada cantidad; el compositor lanzaba una excepción si un entero persistido no cabía y abortaba la exportación. El mismo ancho fijo se reutilizaba en C2, por lo que simplemente enviar la entrada fuera de P2 no resolvía el fallo. Además, el separador físico de cantidad estaba fijo a 42pt del borde incluso para un eventual ancho ampliado.

Se incorporó **`PcSheetV11PairedAmountRouting` en Shared** para las tablas de Equipo y Tesoro. P2 mantiene **36pt** y sus dos pares originales para registros que realmente caben; los registros con cantidades extensas pasan *completos, por sourceKey*, a C2. C2 conserva dos pares objeto/cantidad o denominación/cantidad, pero mide el ancho necesario para la cantidad real y desplaza también el separador correspondiente; no abrevia, reduce letra, inventa una segunda cantidad ni descarta una entrada. Es adaptación basada en datos: cantidades normales continúan con geometría de 36pt. Ambos PDFBox nativos Android/Desktop utilizan el mismo algoritmo; el backend, la fuente del personaje y los PDF legacy no cambian.

## Regresión, pruebas y PDFs auténticos

- Shared: `widePersistedEquipmentAndTreasureAmountsMoveWholeToMeasuredC2`, `ordinaryP2AmountWidthsKeepTheApprovedOriginalColumns`: ambas familias, fuente, medición, cantidad única, espacio físico y preservación de 36pt para valores normales.
- Desktop PDFBox: `g3RealExtremeEquipmentAndTreasureAmountsMoveWholeToC2ForBothVariants` parte del backup **genuino Mara** y añade únicamente **dos registros sintéticos de estrés**. Es un fixture técnico, **NO** afirmación de que el personaje real posee esos inventarios.
- [Scaffold #39064659575](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39064659575): **SUCCESS** Kotlin (unit, Desktop PDFBox y compilación Android), backend, hosted DB para el SHA funcional exacto. [QA traceability #39064659551](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39064659551): **SUCCESS**.
- [22 PDFs nativos generados por la App, artifact #12112039499](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39064659575/artifacts/12112039499), ZIP SHA-256 `5cf1d0e57d7abd82b45e8e3bc77b2632b6a5e6411ccc9fdc7c768f82fa89093e`, CRC íntegro. Comparación contra [20 PDFs fuente anteriores #12112407223](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39063006457/artifacts/12112407223): **20/20 idénticos en número de páginas, palabras, coordenadas y todos los trazos vectoriales**. Ninguna regresión detectada en esos baselines.
- **Dos nuevos PDF A/B:** ambos 10 páginas Carta 612×792pt. `EQUIPO_QA_CANTIDAD_COMPLETA` y **2147483647** una sola vez en C2 **página 6**, `TESORO_QA_CANTIDAD_COMPLETA` y **1987654321** una sola vez en C2 **página 7**. P2 no imprime ninguno de esos dos importes; las páginas afectadas tienen fuente embebida Barlow Condensed/Fira Sans. Inspección física de los rasterizados A/B confirma separadores y números en columnas sin colisión; pies de C2 remiten a P2 pág.2; última página NOTAS manuscrita.
- SHA-256 PDF A `V11-INTERNAL-PROOF-P2-C2-large-amounts-A.pdf`: `2cb85725bf6925183d250364f526ab258a520107d8396524b401b92ff2b6a321`.
- SHA-256 PDF B `V11-INTERNAL-PROOF-P2-C2-large-amounts-B.pdf`: `d3df358050760601c45211a7c8efc9c5151ce4b9f3730a4620e8e539f58e5d38`.

## Alcance de PASS / trabajo restante

**PASS G2/G3 LOCAL Y ACOTADO a cantidades extendidas Equipo/Tesoro y no regresión de 20 baselines**, no aprobación de P202, P204, P205, GLB06 ni otras reglas globales. Matriz vigente permanece 105 obligaciones globales `NOT_VERIFIED` y 12 `stage_checks` PASS anteriormente probados; `active_candidate=null`, **G4 BLOCKED**. No se sustituyen los 48 apartados D-0076, ocho golden ni las cuatro exportaciones previas requeridas en auditoría independiente. Sin Android **instalado**, sin QA propietario, sin aceptación visual/técnica ni G5/G6. RCR-1 sin gasto, sin merge, `main` intacta.

**Siguiente trabajo previsto:** continuar con un incumplimiento funcional concreto de otra sección V11 aún abierta (semántica o paginación), agrupar casos relacionados y demostrarlo con pruebas de origen real/PDF sin repetir este módulo; después realizar la conciliación total y auditoría independiente G4 del candidato único.
