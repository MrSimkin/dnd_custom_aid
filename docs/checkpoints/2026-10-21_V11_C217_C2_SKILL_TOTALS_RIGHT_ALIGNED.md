# C217 — totales originales de habilidades custom alineados a la derecha en C2

Fecha: 2026-10-21. PR #173 DRAFT sin merge, rama `docs/pc-sheet-v11-approved-implementation-handoff`, HEAD inicial `2c6bf4aa07763a6f54f861696cd47eb4b577070c`. Main remoto `0c009f10d5e66880d68fa3732491c6fc3e172f22`. Scaffold #39690123261 y QA traceability #39690123044 del HEAD anterior **SUCCESS**.

## Brecha demostrada

PDF aprobado `V11_Carta/pdfs/v11_extremo_variante_A.pdf` p4 del ZIP golden SHA256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a`: totales numéricos de habilidades bajo Cordura/Honor a derecha, manual boxes a izquierda. App real [C216, artefacto #12387592619](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39689107598/artifacts/12387592619) Mara-A p4: `Habilidad Sincronía: 9 / EXPERTISE`, con 9 junto al nombre. C216 490 casillas verificadas, intactas.

## Bloque funcional C217

Ambos renderizadores PDF, Android/Desktop, desplazan **solo el glifo del total real** al extremo derecho del renglón `attribute-skill:<ID>` cuando toda la entrada cabe en una sola celda original medida y el texto a la izquierda (`Habilidad NOMBRE (TRAINING)`) cabe con 9pt de respiro. El total no se modifica, infiere ni repite; se dibuja al x `col.rightPt - font_real.measure(total) - 4pt`. `PROFICIENT/EXPERTISE/NONE` permanece escrito junto al nombre, sin convertir en marca falsa la casilla manual C216. La fuente `PcSheetV11FlowItem`, identidad ID, grupos y offsets se conservan; no hay nueva paginación, pérdida ni duplicación. Para nombres largos o saltos de línea queda la impresión original completa, sin esconder campo alguno. Se mantiene la paridad de ambos escritores con guard y se añade recuento/fallo por colisión al auditor **físico** PyMuPDF existente, no un auditor redundante.

**Estado en este commit:** IMPLEMENTADO; **NO PASS hasta CI exacto y comparación de los 76 PDF** con los previos. Examinar totales numéricos realmente alineados, trazos y casillas C216, no repeticiones del valor o del training, cero pérdida de fuente y cero alteraciones fuera C2. No declarar equivalencia completa del golden/C204/G4.

**Gates conservados:** G0 PARCIAL; G1 PASS semántico histórico; G2/G3 parciales; G4 BLOCKED 105/105 NOT_VERIFIED, active_candidate=null; G5/G6 pendientes; C211 BLOCKED 3/3 normales y RCR-1 estructural 0/2; C212–C216 PASS LOCAL previos sin reabrir. Falta PDF Android instalado y aceptación técnica/visual del propietario. PR #173 no se fusiona.

**Siguiente trabajo tras prueba:** cotejar qué registros largos requieren una composición futura de habilidades de dos niveles y avanzar otras brechas documentadas del golden por módulo; conciliar matriz G4 únicamente con evidencia independiente real del mismo candidato.

## Evidencia real exacta y resultado: **C217 PASS LOCAL FÍSICO, NO C204/G4**

**Commit de producto:** [`74e9860d9f`](https://github.com/MrSimkin/dnd_custom_aid/commit/74e9860d9f2eb1a121c6749ba750e95752333a83); CI [Scaffold #39695802858](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39695802858) **SUCCESS**, [QA traceability #39695802842](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39695802842) **SUCCESS**. Compila Android y Desktop con misma lógica en los dos escritores.

**PDFs auténticos de la App:** antes C216 [76 PDF #12387592619](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39689107598/artifacts/12387592619), SHA256 ZIP `9ff8c3cafc61e560e2ea7fa94c6c077c983d6d8d0b70f724d975b7839f933ebf`; después C217 [76 PDF #12390577095](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39695802858/artifacts/12390577095), ZIP SHA256 `d243ef1c41c3b0c58ce6a7d713245a488a968c8b6bc07d796678af0b006d59e1`.

**Comparación independiente PyMuPDF** por archivos y páginas: **76/76 PDF** y **713/713 páginas** sin altas ni bajas; los **713** dibujos vectoriales anteriores **geométricamente idénticos** (ignorando solo el campo `seqno` de orden de operaciones en el extractor, que cambia al separarse un `showText` en dos). **679 páginas** mantienen posiciones, orden y texto word-box íntegramente iguales, incluidas las **623 ajenas a C2**. En las otras **34 páginas C2** se reordenan solamente los glifos de las habilidades reales; todas conservan exactamente el mismo multiconjunto de palabras fuente, normalizado por signos de presentación (`:`, `/`, paréntesis), por página.

**490/490** primeras filas genuinas `Habilidad...` posicionan su **total numérico exactamente al x final medido (296pt izquierda / 566pt derecha)**, cero números ausentes, duplicados o desplazados fuera de columna; **0 errores** físicos. El auditor CI del mismo commit exige la muestra física y colisión; el comparador offline confirma exhaustivamente las 490 filas. Se rasterizó y revisó `V11-INTERNAL-PROOF-Mara-A.pdf` p4: `Sincronía (EXPERTISE)` con 9 a derecha y `Manipulación de éter (PROFICIENT)` con 8 a derecha, casilla vacía C216 al inicio, sin ocultar nombres.

[Acta JSON del SHA exacto](../pc-sheet-v11/qa/evidence/2026-10-21_V11_C217_C2_SKILL_NUMBERS_RIGHT_REAL_PDFS.json) archiva los IDs, SHA, prueba y limitaciones.

**Resultado SOLO C217:** **PASS LOCAL FÍSICO** para totales de habilidades custom con espacio medido y training explícito, sin atribuir estética global ni equivalencia exacta C204. G0 PARCIAL, G1 PASS semántico histórico, G2/G3 PARCIALES, **G4 BLOCKED 105/105 NOT_VERIFIED, active_candidate=null**, G5/G6 PENDIENTES. C211 BLOCKED 3/3 normal y RCR-1 estructural 0/2, C212–C216 PASS locales anteriores intactos. Sin Android instalado ni aprobación técnica/visual del propietario; PR #173 DRAFT NO MERGE.

**Siguiente bloque previsto:** reconciliar ahora otros módulos que aún presenten una diferencia objetiva frente a los ocho golden, sin reabrir C211 ni retrabajar C212–C217; crear evidencia de etapa G2/G3 por regla G4 cuando esté sustentada por el mismo candidato y exista revisión independiente.
