# C217 — totales originales de habilidades custom alineados a la derecha en C2

Fecha: 2026-10-21. PR #173 DRAFT sin merge, rama `docs/pc-sheet-v11-approved-implementation-handoff`, HEAD inicial `2c6bf4aa07763a6f54f861696cd47eb4b577070c`. Main remoto `0c009f10d5e66880d68fa3732491c6fc3e172f22`. Scaffold #39690123261 y QA traceability #39690123044 del HEAD anterior **SUCCESS**.

## Brecha demostrada

PDF aprobado `V11_Carta/pdfs/v11_extremo_variante_A.pdf` p4 del ZIP golden SHA256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a`: totales numéricos de habilidades bajo Cordura/Honor a derecha, manual boxes a izquierda. App real [C216, artefacto #12387592619](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39689107598/artifacts/12387592619) Mara-A p4: `Habilidad Sincronía: 9 / EXPERTISE`, con 9 junto al nombre. C216 490 casillas verificadas, intactas.

## Bloque funcional C217

Ambos renderizadores PDF, Android/Desktop, desplazan **solo el glifo del total real** al extremo derecho del renglón `attribute-skill:<ID>` cuando toda la entrada cabe en una sola celda original medida y el texto a la izquierda (`Habilidad NOMBRE (TRAINING)`) cabe con 9pt de respiro. El total no se modifica, infiere ni repite; se dibuja al x `col.rightPt - font_real.measure(total) - 4pt`. `PROFICIENT/EXPERTISE/NONE` permanece escrito junto al nombre, sin convertir en marca falsa la casilla manual C216. La fuente `PcSheetV11FlowItem`, identidad ID, grupos y offsets se conservan; no hay nueva paginación, pérdida ni duplicación. Para nombres largos o saltos de línea queda la impresión original completa, sin esconder campo alguno. Se mantiene la paridad de ambos escritores con guard y se añade recuento/fallo por colisión al auditor **físico** PyMuPDF existente, no un auditor redundante.

**Estado en este commit:** IMPLEMENTADO; **NO PASS hasta CI exacto y comparación de los 76 PDF** con los previos. Examinar totales numéricos realmente alineados, trazos y casillas C216, no repeticiones del valor o del training, cero pérdida de fuente y cero alteraciones fuera C2. No declarar equivalencia completa del golden/C204/G4.

**Gates conservados:** G0 PARCIAL; G1 PASS semántico histórico; G2/G3 parciales; G4 BLOCKED 105/105 NOT_VERIFIED, active_candidate=null; G5/G6 pendientes; C211 BLOCKED 3/3 normales y RCR-1 estructural 0/2; C212–C216 PASS LOCAL previos sin reabrir. Falta PDF Android instalado y aceptación técnica/visual del propietario. PR #173 no se fusiona.

**Siguiente trabajo tras prueba:** cotejar qué registros largos requieren una composición futura de habilidades de dos niveles y avanzar otras brechas documentadas del golden por módulo; conciliar matriz G4 únicamente con evidencia independiente real del mismo candidato.
