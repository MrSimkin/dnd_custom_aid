# V11 P3 — recuperación de la gramática azul-niebla del diseño aprobado

**2026-10-20 | Base:** PR [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173) DRAFT, rama `docs/pc-sheet-v11-approved-implementation-handoff` HEAD `d8b7db19b808b2955956c0fb05ff584683556926` (CI Scaffold #39491483007 y QA #39491483060 **SUCCESS**). `main` `0c009f10d5e66880d68fa3732491c6fc3e172f22`. **NO MERGE.**

## Evidencia de desviación

La **P3** de la maqueta aprobada `v11_extremo_variante_B.pdf` p3, ZIP SHA256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a`, presenta renglones narrativos con **bandas azul-niebla alternadas** y seis cabeceras de perfil también tintadas. La App auténtica, `V11-INTERNAL-PROOF-Mara-B.pdf` p3 del artefacto [#12295018331](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39490469236/artifacts/12295018331), tenía **seis rellenos uniformes 256×45pt, dos columnas de historia uniformes y seis cabeceras blancas**. Se comprobó en vectores/PyMuPDF, no en supuesto estético. La reserva geométrica y textos fuente ya eran correctos; no se modifica su semántica.

## Cambio funcional preciso

Ambos renderers de **App Desktop y Android** aplican franjas alternadas `RGB(231,242,247)` sobre rellenos base `RGB(238,247,251)` a cada segundo **renglón ya medido** de seis campos, dos columnas de historia y páginas P3 de continuación; 6 cabeceras `TRASFONDO`, `RELIGIÓN/FE`, `RASGOS DE PERSONALIDAD`, `IDEALES`, `VÍNCULOS`, `DEFECTOS` reciben banda de 16pt **dentro del espacio existente**, antes de dibujar el texto. El grosor y lugar de renglones, contenido, paginación, cuadros, el diseño C2, las 4 familias legacy y C211 no cambian.

No se crea auditor adicional. Se extiende **`scripts/audit_v11_p3_c2_manuscript.py`**, ya invocado en Scaffold, para exigir seis bandas y ocho filas tintadas medibles en P3 primera página, y presencia de filas tintadas en ambas columnas P3 continuación; preserva controles previos P305/C207/C208, colisión con texto y cuadrículas.

**Estado al implementar:** código publicado, **CI del commit exacto y comparación de PDF aún PENDIENTES**. Condiciones para PASS LOCAL restringido: Scaffold/QA correctos, 76 PDF genuinos sin cambios de páginas/posiciones de palabra, vectores inalterados fuera P3 y franjas físicas correctas. No inferir paridad integral con 8 golden ni aprobación visual/técnica del usuario.

**Gates:** G0 PARCIAL, G1 PASS semántico previo, G2–G3 PARCIALES, **G4 BLOCKED con 105/105 NOT_VERIFIED y active_candidate=null**, G5–G6 pendientes, Android instalado/QA owner pendientes. RCR-1 C211 normal agotado 3/3, recuperación estructural 0/2 sin iniciar. PR #173 DRAFT no fusionar.

**Siguiente:** revisar CI exacto y artefactos, registrar evidencia física o fallo. Después continuar otra diferencia concreta fuera de C211 sin reauditar zonas cerradas.

## Resultado del commit exacto y cotejo externo — PASS LOCAL P3 acotado

El commit funcional `917766bb64196f120e1b19659cef51c30a9fbf1c` pasó [Scaffold #39493923916](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39493923916) **SUCCESS** (Kotlin Desktop, APK Android, pruebas existentes y auditoría PDF auténtica, backend y DB), así como [QA traceability #39493923928](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39493923928) **SUCCESS**. [Artefacto 76 PDF App reales #12296978059](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39493923916/artifacts/12296978059) ZIP SHA256 `db6fde5d360c2ab9642c2f9c85ca67914af0e2e19c67907b24babd52645e7d9d`; [auditor original de este run #12296434436](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39493923916/artifacts/12296434436) ZIP SHA256 `1233e25f1db0232c67b993a5ec8408d4a432a1e9cc02f95ca36cbc0da41b5265`.

**Comparación física independiente** con los 76 PDF auténticos del HEAD anterior: **711/711 páginas con palabras exactas y mismas coordenadas**; **96/96 páginas P3 preservan TODOS los vectores originales**, solamente añaden color físico; **615/615 páginas NO P3 tienen vectores idénticos**; cero páginas añadidas o pérdidas de datos. Sobre 76 primeras P3: **456/456 cabeceras de campo tintadas**, **456 franjas iniciales de campo**, **152 franjas iniciales de historia**; en **20 páginas de continuación** hay **40 franjas** repartidas en ambas columnas. **0 errores**. PDF real Mara B p3 renderizado/inspeccionado visualmente con bandas azules detrás de texto sin desplazamientos. Informe diferencial local SHA256 `bb39d6903a1e51f18f66f768d3a70d6218f3fce1036d39c87305b95c5eae47ee`.

El auditor físico P305/C207/C208 **ya existente, extendido, PASS LOCAL** en los 76 PDF: 96 P3, 456 encabezados nuevos, 648 filas azul-niebla obligatorias, **19.325 palabras con regla alineada**, 209 folios C2, **230/230 cuadrículas C2 completas**, cero fallos. Su JSON tiene SHA256 `f114cea06473066e6f09a2308de3ea92d09f19190297f80874892ea13a57d263`. Ver [acta estructurada](../pc-sheet-v11/qa/evidence/2026-10-20_V11_P3_GOLDEN_ROW_BANDING_REAL_PDFS.json).

**Alcance exacto:** solo una característica de gramática P3, no equivalencia de todos los elementos con los ocho golden. No se cambia matriz, NO G4 PASS: **105/105 NOT_VERIFIED; active_candidate=null**. G0 histórico PARCIAL, G1 PASS semántico previo, G2/G3 PARCIALES, G5/G6 pendientes. Android compilado y APK generado, NO instalado ni revisado por propietario. C211 sigue BLOCKED normal 3/3, RCR-1 estructural 0/2. PR #173 DRAFT, NO MERGE.

**Siguiente avance racional:** otra diferencia de funcionamiento o disposición contractual no abordada ni bloqueada, demostrada con PDF real frente golden/D-0076; ejecutar exclusivamente verificaciones proporcionales y mantener las pruebas originales.
