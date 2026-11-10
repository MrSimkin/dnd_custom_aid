# V11 G2/G3 — Equipo y Tesoro juntos en C2 cuando realmente caben

**Fecha:** 2026-10-18 · PR #173 DRAFT / NO MERGE · Base `26371528a38b3ed2ea65d390fd91874366e9020f`.

## Problema comprobado sobre PDF real

Los PDF genuinos del candidato anterior ([Scaffold #39181706739](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39181706739), caso `V11-INTERNAL-PROOF-P2-currency-source-break-A.pdf`) empleaban **dos páginas físicas C2 distintas**: una para cinco pares ordinarios `Mochila / Libro / Incienso / Vestiduras / Ropa`, otra para tres monedas de origen. Ambas muestran abundante espacio NOTAS y estaban generadas desde una misma P2. Contrato aprobado `C201–C205`: extensión compacta sin desperdiciar hojas ni sacrificar pares, IDs, fuentes o reservas manuales.

## Cambio productivo delimitado

- **Android y Desktop V11:** composición conjunta de **dos tablas de dos parejas internas** en UNA página C2 solo cuando ambas tienen fuente real, cantidades legibles en columnas estándar y el compositor medido demuestra que ambas caben en rectángulos no superpuestos. La altura del primero se deriva de las celdas efectivas, sin escalamiento/ellipsis; segundo usa el resto. Encabezados/cartelas independientes y única referencia bidireccional P2→C2 a esa página física. NO concatenar orígenes/ID de Equipo y Tesoro.
- Si una tabla no cabe, si hay cantidades amplias o módulos muy densos, se mantiene la **ruta completa existente de páginas C2 separadas**. No se tocan los registros P2, los dibujos/saltos de otras familias, el ZIP golden, los demás C2 ni los datos persistidos.
- Evitar escribir NOTAS de la tabla superior sobre la inferior; la tabla inferior conserva cuadrícula NOTAS en espacio útil. No crear hojas sólo de relleno.
- **Prueba funcional real A/B:** se amplía el test de monedas custom CRLF/TAB ya existente, sin duplicarlo. Exige ambos encabezados y sus datos en una sola página C2, 0/37/41/73 sin duplicados, referencia P2 correcta, NOTAS manuscrito y una página física menos (8→7). La regresión ya existente de Equipo/Tesoro densos sigue exigiendo páginas separadas y navegaciones correctas.

## Gate

**Al publicar código: CI y PDF A/B del commit PENDIENTES.** No inventar PASS. G0 QA histórico PARCIAL; G1 semántico PASS; G2/G3 parciales; **G4 BLOCKED 105 reglas NOT_VERIFIED, active_candidate=null**, G5/G6 pendientes, RCR-1=0/2. M50800 y V11-QA-001/002 intactos. PR #173 DRAFT sin merge. Cualquier fallo real se investiga acotadamente, nunca degradando el contrato ni cambiando el test para ocultarlo.

**Siguiente bloque:** comprobar PDF reales, geometría del área compacta y no regresión; después tratar otros grupos de C2 con evidencia primaria, no ciclos de auditorías repetidas.

## CI 1 y reparación acotada del test de no regresión

- [Scaffold #39184693848](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39184693848): **FAIL**: 158 pruebas Desktop, **una sola** falla: `sparseTreasureContinuationKeepsWritingGridBelowRealPairedRows`. Causa demostrada: el test antiguo asumía **página física dedicada** para Tesoro y buscaba literal `C2 - Equipo y tesoro / TESORO`, incluso en el caso donde el nuevo comportamiento aprobado comparte una página con Equipo. No es fallo de pérdida de registros ni de medida: la prueba no encontraba el módulo por su título anterior.
- Se conserva su obligación sustantiva: **Tesoro auténtico en tabla de pares C2**, títulos, denominación fuente, cuadrícula NOTAS y última página manuscrita. La selección de página ahora reconoce ambos caminos solo si también existe cartela genuina `TESORO / CANTIDAD`; no se salta una aserción ni se desactiva la prueba.
- Cambia únicamente el test que imponía una página dedicada; código de producción de `16a4201` intacto. **Nuevo CI PENDIENTE.** Primer intento ordinario de reparación local (no RCR-1); ninguna matriz G4 ni evidencia histórica cambia.

## Prueba final del bloque — PASS LOCAL (2026-10-18)

**Commit productivo:** [`16a4201ac4942ccb6adb0d4518be7f2d9f4f3c1d`](https://github.com/MrSimkin/dnd_custom_aid/commit/16a4201ac4942ccb6adb0d4518be7f2d9f4f3c1d); **candidato probado, con aserción corregida:** [`08b7772668f2f1c95e9c9117a62a5ff29b914a5b`](https://github.com/MrSimkin/dnd_custom_aid/commit/08b7772668f2f1c95e9c9117a62a5ff29b914a5b).

- [**Scaffold #39185287349**](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39185287349) **SUCCESS**, Kotlin Desktop/Android DEBUG, backend y hosted-db. [QA traceability #39185287244](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39185287244) **SUCCESS documental** (no QA humano). Primer CI [#39184693848](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39184693848) FALLÓ 1/158 tests por exigencia histórica de página exclusiva; constan ambas ejecuciones.
- [**ZIP de 58 PDF reales de Desktop #12157992511**](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39185287349/artifacts/12157992511), **550 folios**; SHA-256 ZIP `aceb0e6492a4c43db13fbe2ad241f11a147cd083cd2803c854e61080f0b8cfa5`, CRC PASS. Referencia para comparar: [58 PDF anteriores #12156707254](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39181706739/artifacts/12156707254), **552 folios**.
- **Caso real A:** `V11-INTERNAL-PROOF-P2-currency-source-break-A.pdf` **8→7 páginas**, SHA-256 `05163d57a3b9cb9661a5136f3498e91d68a753b82a1a14d06939a0a4bd88e3de`. **B:** mismo ahorro **8→7**, SHA-256 `75c19b1c93006f4760e9d18f6af313d7d3d32c2905008193b45a49188892f118`. En cada PDF, página física **5** contiene cartelas independientes `EQUIPO / CANTIDAD` y `TESORO / CANTIDAD`, cinco filas de Equipo, las tres monedas continuadas `37/41/73`, encabezados fuente CRLF/TAB, `VIENE DE P2 PÁG. 2` y cuadrícula manuscrita `NOTAS`. La moneda `0` sigue en P2, nunca duplicada. NOTAS final permanece última. El test real Kotlin exige los mismos datos y evita alterar el backup.
- **No regresión independiente por comparación PyMuPDF:** mismos **58 PDF** en ambos ZIP. **56/58** conservaron número de páginas, secuencia de palabras con coordenadas, recuento de trazos e imágenes. Los **2 cambios únicos** fueron los A/B de C2 antes descritos; total pasa de 552 a 550 páginas, sin modificar las otras salidas. No se afirma igualdad de bytes ni paridad visual integral.
- **Verificación física y raster:** se abrió/renderizó el nuevo folio 5 de A; cabeceras de tablas a y≈76pt y 166pt desde el borde superior, etiquetas de registros más abajo, NOTAS comienza ~215pt; las regiones separadas se observan sin solaparse y dentro de marcos. [Auditores independientes #12158067027](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39185287349/artifacts/12158067027) registraron `V11_PHYSICAL_AUDIT_ERRORS=0`, **550/550** marcos, **550/550** logo visible y **58/58** NOTAS finales.
- **Límites:** prueba funcional y física **Desktop real** más compilación Android equivalente, **no** comprobación de Android instalado/Guardar/Compartir ni aceptación visual owner. Se acredita **PASS LOCAL de compactación medida C2 Equipo+Tesoro**; esto no convierte ninguna de las 105 reglas globales en PASS.

**Conservación de gate:** G0 QA histórica PARCIAL; G1 semántico PASS, G2/G3 parcialmente demostrados. **G4 BLOCKED, 105 NOT_VERIFIED, `active_candidate=null`**; G5/G6 pendientes, QA owner y M50800/V11-QA-001/002 intactos, RCR-1 no activado (0/2). PR #173 DRAFT sin merge, `main` intacto.

**Siguiente trabajo:** atacar un incumplimiento sustantivo todavía demostrado en otros módulos C2/P3 o relación de notas digitales, con cotejo fuente→PDF→golden; no reabrir los dos pares ya verificados ni generar auditorías redundantes.
