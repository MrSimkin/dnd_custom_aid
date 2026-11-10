# C215 — Tarjeta de atributo custom en dos niveles realmente medidos (C2)

**Fecha:** 2026-10-21. **PR:** [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173) DRAFT/SIN MERGE. Rama autorizada `docs/pc-sheet-v11-approved-implementation-handoff`. HEAD inicial `aa07e38626c02d99feaa9f87524097958db5bc85`; `main` remoto `0c009f10d5e66880d68fa3732491c6fc3e172f22`. [Scaffold #39671000317](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39671000317) y [QA traceability #39671000191](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39671000191) **SUCCESS** previos.

## Defecto de producto C204 delimitado por evidencia genuina

[Checkpoint C214](2026-10-21_V11_C214_MEASURED_C2_SCORE_MODIFIER_SOURCE_INK.md) demostró **199** formas compactas reales, sin pérdida de texto, en 76 PDFs auténticos. Sin embargo, los valores permanecían en **la misma línea** que el nombre, cuando el contrato V11 requiere jerarquía física de tarjeta con nombre y puntaje/óvalo separados. El compositor `PcSheetV11TextFlow` soporta saltos de línea explícitos con offsets UTF-16 intactos, blancos realmente reservados y grupos `keepTogetherIfFits`. No necesita otra heurística o paginador paralelo.

## Intervención C215, bloque coherente Android+Desktop

- Cada fuente custom **aún no impresa en P1** se proyecta en su único `attribute:<index>` con `nombre + "\\n\\n" + score + " / mod " + modifier + " / salvación " + valor_real`. El primer renglón contiene el nombre original íntegro, el siguiente es una reserva **vacía medida** y el tercero conserva los valores originales. No se sintetizan registros digitales ni se reimprimen puntajes; el salto es geometría de papel del renderer.
- El compositor compartido decide wrapping, unidad con habilidades, columna y páginas desde el texto con offsets originales de la proyección. Al crecer los campos la paginación C2 **puede variar válidamente**; comparar integridad fuente→PDF, no exigir artificialmente idéntico número de páginas.
- Solo si la celda vacía **inmediatamente anterior** a los valores está en la misma columna y toca geométricamente la fila de valores, los trazos rectangulares/ovalados se extienden hacia esa celda **ya reservada** hasta 17pt sobre el baseline. Si la línea anterior no existe (continuación excepcional), se usa el contorno compacto demostrado por C214 sin descartar ningún valor.
- Preservar las bandas y rieles de agrupación C213, C212, relleno de cuadrículas C207, demás módulos y familias legacy. Los dos escritores nativos tienen las mismas decisiones semánticas y geometría. Guardia de paridad `scripts/test_v11_c2_source_navigation_contract.py` ampliada.
- No fingir que equivale a todas las cotas de la maqueta ni a las ocho golden: C204 y G4 **siguen abiertos** hasta comparación exhaustiva.

**Estado al publicarse:** `IMPLEMENTADO / CI Y PDFs DEL CANDIDATO PENDIENTES`. Se exige Scaffold/QA de SHA exacto, A/B PDFs **reales de Desktop**, revisión de nombre largo/140 habilidades y ausencia de pérdida, colisiones, fuga fuera C2 y regresiones P1/P2/P3/P4/CN3/Libro/NOTAS. Un fallo es STOP, no PASS; no repetir C211.

**Gates:** G0 histórico PARCIAL; G1 semántico PASS anterior; G2/G3 PARCIALES; G4 BLOCKED con 105/105 NOT_VERIFIED y active_candidate=null; G5/G6 PENDIENTES. C211 BLOCKED 3/3 normales, 0/2 RCR-1 estructurales, sin reiniciar. C212/C213/C214 PASS locales acotados previos, sin nueva aprobación técnica o visual del propietario.

**Siguiente trabajo:** verificar el nuevo binario exacto y PDF físico. Si pasa, registrar métricas y precisar qué resta de C204; luego continuar únicamente otras brechas verificables y reconciliación progresiva G4 sin falsas promociones.

## Primer PDF real C215: defecto físico medido y corrección acotada

Primer candidato `f4eb1db`, test guard fijo en `59bb4e4`. [Scaffold #39675089783](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39675089783) y [QA #39675089838](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39675089838) SUCCESS, **pero esto NO es PASS físico del acabado**. Se cotejaron 76 PDFs antes (711 páginas, ZIP SHA-256 `e8b41860f969fa28a24ba25f4cdbf9e4a6f87c28ccd4e9c91c7626b96d6d7321`) y después (713 páginas, ZIP SHA-256 `3b8bc54866beef1f0554e7686e3e91c9a2ec9d7fcf270a210c396cd0ad1a6445`). Solo dos fixtures densos C2 A/B agregan una página cada uno. **199/199 registros source score/mod/salvación idénticos**, **676/676 identificadores QA** preservados y **502 páginas no-C2** con mismos vectores. Sin embargo, el auditor encontró **115** rectángulos y óvalos grandes y **84** todavía compactos, en vez de todos grandes.

**Mecanismo exacto:** la reserva de salto `nombre + "\\n\\n" + valores` existe en cada atributo; el detector exigía igualdad bit a bit entre `prior.cellPt.bottomPt` y `cell.cellPt.topPt`, expresiones de Float cuyo redondeo puede diferir `6.103515625e-05` pt según índice de fila. Comparación de geometría con tolerancia mínima `abs(...) <= 0.02f` resuelve solo ese falso negativo, sin mover ningún valor/renglón/page ni relajar el `sourceEndExclusive == scoreStart`, identidad de columna ni celda en blanco. Test paridad Android/Desktop modificado.

**Estado de este parche:** corrección física implementada; **aún no PASS hasta PDF del commit exacto**, volver a comprobar los 76 y que los 199 pares de figuras tengan altura 19.7pt, conservando 713 páginas, identidad del resto de secciones y cero superposiciones. Si no sucede, STOP y no elevar C204/G4.

## Evidencia exacta y veredicto: C215 **PASS LOCAL FÍSICO ACOTADO**, NO C204/G4

**Código correctivo exacto:** [`e1d887368e`](https://github.com/MrSimkin/dnd_custom_aid/commit/e1d887368e19a7b4eb26e9d8fd8be677d728b23f), publicado en rama única. [Scaffold #39676934860](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39676934860) y [QA traceability #39676934764](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39676934764) terminaron **SUCCESS**. El primer [Scaffold #39675089783](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39675089783) ya construía la proyección de dos niveles y generaba PDFs, pero su *acabado* NO recibió PASS porque 84 figuras aún quedaban compactas; el ajuste posterior resolvió ese defecto medido.

**Material auténtico de comparación:** [76 PDF Desktop anteriores #12379791454](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39669461876/artifacts/12379791454), ZIP SHA-256 `e8b41860f969fa28a24ba25f4cdbf9e4a6f87c28ccd4e9c91c7626b96d6d7321`; [76 PDF exactos C215 #12382464137](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39676934860/artifacts/12382464137), ZIP SHA-256 `3b59fe3eddd09a5573915a80c3561955e491e80483bedfc0d3433aa1108fba1b`.

Auditoría independiente por nombre de archivo, PyMuPDF:
- **76/76 PDF**, **199/199 triples fuente puntaje/modificador/salvación** conservados y **676/676 identificadores QA** sin variación. Cada título custom tiene su renglón y los valores verdaderos están abajo.
- **711 → 713 páginas totales**: incremento exclusivo de una página C2 en `C2-grouped-custom-attributes-A` y otra en `C2-grouped-custom-attributes-B` con 72 atributos adicionales. Es espacio medido genuino, sin padding arbitrario de páginas vacías.
- **502 páginas ajenas a C2** comparadas con secuencia de roles y vectores idénticos. Controles de módulos y sobre de página del CI PASS.
- **199/199 rectángulos de puntaje** y **199/199 óvalos Bézier de modificador** con altura física **19,7pt**, leyendo los números reales; **0 superposiciones** con palabras no numéricas en esas formas, incluidas continuaciones y estrés. La **casilla SALVACIÓN sigue vacía**, sin estado inventado.
- Se renderizaron y revisaron páginas C2 reales de Mara A y estrés custom A/B. [Acta JSON del candidato exacto](../pc-sheet-v11/qa/evidence/2026-10-21_V11_C215_MEASURED_TWO_LEVEL_PDF.json) documenta artefactos, SHA, regresión previa, contador y límites.

**Veredicto restringido:** C215 PASS LOCAL para *composición en dos niveles, integridad y geometría física medida* del PDF Desktop, con código fuente paritario Android. NO C204 PASS (equivalencia visual íntegra con el golden incluyendo cota/gramática finales y casos de overflow), NO G4: **105/105 reglas todavía NOT_VERIFIED**, `active_candidate=null`. G0 PARCIAL, G1 PASS semántico histórico, G2/G3 PARCIALES, G5/G6 PENDIENTES. C211 sigue **BLOCKED 3/3** normales y **RCR-1 estructural 0/2**. No instalado Android, no aprobación técnica/visual del propietario, no merge.

**Siguiente trabajo real:** pasar a conciliación modular C204/C2 con las ocho fuentes golden y decisiones D-0076, priorizando discrepancias físicas verdaderas que requieran reparación funcional, sin repetir los defectos ya resueltos ni inventar PASS de la matriz.
