# V11 G2/G3 — Contexto de nivel al continuar entre columnas físicas P4

**Fecha:** 2026-10-17 (Chile). **Autoridad:** [PR #173](https://github.com/MrSimkin/dnd_custom_aid/pull/173), rama `docs/pc-sheet-v11-approved-implementation-handoff`, OPEN/DRAFT/NO MERGE. Commit funcional [`dbf8a1967cff556dee4e9e07e9944f12db2662fe`](https://github.com/MrSimkin/dnd_custom_aid/commit/dbf8a1967cff556dee4e9e07e9944f12db2662fe); `main` continúa en `0c009f10d5e66880d68fa3732491c6fc3e172f22`.

## Incumplimiento reproducido sin rediseñar

La corrección anterior cubría el salto de un **nivel dentro de P4 entre páginas físicas**, pero la P4 de tres columnas podía comenzar una **columna 2 o 3** con conjuros de un nivel cuyo encabezado fuente estaba en una columna precedente. Prueba primaria: PDF genuino del artefacto #12119331435, `V11-INTERNAL-PROOF-P4-dense-single-level-A/B.pdf` (175 conjuros de nivel 1): P4 p4 col2 empieza en `QAP4UNO040`, col3 en `QAP4UNO082`, ambas sin contexto de nivel; p5 col2 comenzaba en `QAP4UNO166` sin etiqueta. No se atribuye este caso al propietario.

## Código y alcance

- En `DesktopPcSheetV11PdfProofRenderer.kt` y `AndroidPcSheetV11PdfProofRenderer.kt`, la rutina `addSection` ahora examina la primera celda física **de cada columna P4** en cada página, mediante `columnIndex` y `rowIndex` del flujo medido. Si esa celda **no es** un encabezado `spell-level`, resuelve el último nivel real anterior por `sourceKey` del flujo ordenado y muestra `NIVEL N / CONTINUACIÓN` (o `TRUCOS / NIVEL 0 / CONTINUACIÓN`) solo en esa columna.
- La etiqueta usa Fira Sans ya integrada a 7pt con comprobación de ancho contra la **columna concreta**, en la franja existente PDF y=610, entre tarjetas superiores y tabla de texto; x=`columna.leftPt + 2pt`. No altera filas, reservas, ancho del texto fuente, page breaks, niveles ni slots. No duplica encabezados genuinos ni dibuja en columnas vacías.
- No se tocaron Shared, esquemas, datos guardados, contrato visual, matriz, pruebas de otras familias, D-0076, golden ni `main`.
- Se **amplió** el test de PDF real A/B `g3DenseRealLevelOneSpellsKeepLevelContextOnP4ContinuationPagesAB` para verificar mediante `PDFTextStripperByArea` la franja reservada de las columnas 2 y 3 del primer P4, además de la continuación entre páginas ya demostrada. No se inventó otra suite ni otro fixture.

## Evidencias comprobadas por commit exacto

- [Scaffold #39082179553](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39082179553): **SUCCESS** para `dbf8a1967cff556dee4e9e07e9944f12db2662fe`; Kotlin Shared/Desktop, test fuente→PDF A/B, compilación Android, backend y hosted DB.
- [QA traceability #39082179525](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39082179525): **SUCCESS** del ledger, no aprobación humana.
- [Artefacto nativo PDF #12119138570](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39082179553/artifacts/12119138570): ZIP SHA-256 `b7db5e9d65054a185a834b86461948391dffafc5a5b47c2fe4c9bbd315526aeb`, CRC PASS, **36 PDF/338 páginas Carta**. Comparado contra [artefacto anterior #12119331435](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39081392551/artifacts/12119331435) ZIP SHA `ed79b5d0afa2e3ba2d46d0c40391a720cdafdec3bad3ffb1c2a6892a3a561223`.
- Análisis PDF físico palabra/coordenada (centésimas de pt), página y número de trazados: **19/36 sin ninguna diferencia textual**, **17/36 únicamente con 36 nuevas etiquetas en y≈175.5pt desde el borde superior**, todas `NIVEL X / CONTINUACIÓN`, x de inicio 224 o 404pt (más una nueva en col2 de p5 para cada caso denso); **ninguna palabra de origen borrada, desplazada o alterada**, sin cambios de cantidad vectorial ni tamaños/paginación. No se afirma igualdad byte a byte ni identidad exhaustiva de operadores vectoriales.
- Caso denso de origen auténtico Ilyra, artificialmente multiplicado solo para prueba: [A PDF SHA256 `46b97af8...`](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39082179553/artifacts/12119138570) y B SHA256 `268f3ea06c8a29086189052dadd0bc4ba73a14e108a68eb4a1dde89f2206f04a`, **10 páginas Carta** cada uno; p4 col2 y col3 y p5 col1 y col2 muestran `NIVEL 1 / CONTINUACIÓN`; p4 col1 comienza con `TRUCOS / NIVEL 0` genuino y p5 col3 vacía no recibe etiqueta. **175/175 conjuros** exactamente una vez por variante; página final NOTAS sin conjuros.
- P4 p4 y p5 de ambas variantes rasterizadas y examinadas visualmente en la franja del encabezado/filas; sin colisiones o recortes observados en esos ejemplos.

## Resultado de gates y próxima ruta

**PASS LOCAL G2/G3** solo para continuidad de nivel entre columnas P4 (con la continuidad de página probada antes). **No** aprobación técnica/visual del propietario ni QA en Android instalado. **105 reglas globales NOT_VERIFIED, 12 stage checks PASS históricos**, matriz y `active_candidate=null` sin modificar: **G4 BLOCKED**, G5/G6 pendientes. Observaciones V11-QA-001/002 + 32 M50800 abiertas; RCR-1 sin activación. PR #173 no fusionada.

**Siguiente bloque funcional:** examinar una brecha distinta, verificable y relevante del contrato en continuidad/legibilidad de campos impresos restantes (sin volver sobre nombres, slots ni encabezados P4 ya cubiertos). Si no se demuestra defecto, avanzar a consolidación G4 pre-QA de las 105 reglas; no repetir auditorías ni promover PASS sin prueba sobre candidato idéntico. `main`, golden y familias legacy permanecen intactos.
