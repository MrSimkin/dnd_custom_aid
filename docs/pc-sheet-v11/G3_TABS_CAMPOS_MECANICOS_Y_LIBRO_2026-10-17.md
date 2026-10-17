# V11 G3 — tabulaciones fuente en Rasgos, Habilidades, P4 y continuación del Libro

**Fecha:** 2026-10-17. **PR:** [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173), OPEN/DRAFT/unmerged. **Último código y prueba exactos:** [`2ba705891d66a4f8c5c624be805572ce0f953765`](https://github.com/MrSimkin/dnd_custom_aid/commit/2ba705891d66a4f8c5c624be805572ce0f953765). Mantener plan G0–G6, contrato aprobado, 105 reglas, raw/evidencias y `main` inalterados; ni merge ni QA propietario.

## Hallazgo verificado en fuente productiva

El fix anterior de tabuladores había protegido el medidor/flujo general y `draw()`, pero cinco llamadas **directas** a `PDPageContentStream.showText` aún podían recibir datos fuente con `\\t`: rótulos personalizados de habilidades agrupadas P1 A, habilidades independientes B, resúmenes Rasgos P1, fuente real de conjuro P4 y otras celdas de habilidades. Además, el **encabezado del Libro `CONTINUACIÓN: <nombre real>`** enviaba el nombre sin normalizar tanto a `getStringWidth` como a `showText`. En PDFBox, la tabulación de fuente no garantiza un glifo medible/imprimible: exportación válida puede abortarse.

**Implementación:** [`b24576f`](https://github.com/MrSimkin/dnd_custom_aid/commit/b24576fd965d2a2224a0740e9304238388e71be2) aplica `pcSheetV11PrintableText` *solo en dibujo* a las rutas restantes; [`7d27831`](https://github.com/MrSimkin/dnd_custom_aid/commit/7d2783153329e055227f8de21586e0f6cfe0deb9) mide e imprime asimismo el título real de continuación del Libro de forma segura. Dos renderers V11 PDFBox Android y Desktop, sin cambios en el modelo, IDs, asociaciones, offsets fuente, paginación compartida ni familias legacy. No se altera el valor persistido.

## Prueba discriminante, error de fixture y reparación

`g3RealTabbedTraitsCustomSkillsAndSpellSourcesRenderWithoutMutatingSavedText` toma el backup auténtico **Mara** y modifica **únicamente cadenas fuente** para el estrés: primer nombre y origen de Rasgo, primera habilidad personalizada, una fuente de lanzamiento, nombre del primer conjuro y su descripción extensa para forzar páginas de Libro. Ejecuta las variantes A y B con el servicio real `generateV11Proof`, conserva ambos PDF, verifica contenido P1/P4/C2, comienzo/fin de la descripción y encabezados reales de continuación.

- [Scaffold #39067267112](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39067267112): SUCCESS del primer conjunto de cambios de código.
- [Scaffold #39067439661](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39067439661): **FAIL de prueba, no aprobado**. La prueba exigió Libro aunque `PcSheetV11ExportRequest.includeSpellDescriptions` estaba en el valor predeterminado `false`. Falló la aserción `BOOK_TAB_START`, no se atribuyó erróneamente a la salida de producción.
- [`2ba7058`](https://github.com/MrSimkin/dnd_custom_aid/commit/2ba705891d66a4f8c5c624be805572ce0f953765): corrige solo la prueba y solicita explícitamente `includeSpellDescriptions=true`; ningún nuevo cambio productivo.
- [Scaffold #39067945274](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39067945274): **SUCCESS** exacto; pruebas Kotlin Desktop, Android DEBUG, backend y DB. [QA traceability #39067945251](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39067945251): **SUCCESS**; esto no es aprobación del propietario.

## Evidencia física independiente del candidato exacto

[Artefacto #12114620678](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39067945274/artifacts/12114620678): **24 PDFs nativos de la aplicación**. ZIP SHA-256 `a6e8e768712b81c5661092c3a38c5622aa5eba98171bcab5eb6b6540693862f3`, CRC `PASS`. Comparado con los 22 PDFs del [artefacto anterior #12112039499](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39064659575/artifacts/12112039499): **22/22 sin divergencia** en número de páginas, Carta, palabras/coordenadas, trazos vectoriales o familias tipográficas según inspección PyMuPDF.

Dos nuevas pruebas PDF A/B, cada una **14 páginas Carta 612×792pt**:
- A: `V11-INTERNAL-PROOF-source-tabs-mechanical-A.pdf`, SHA-256 `02fd78befca80a46aa264752c19faa7342e1927e76660681bdd69085ba4d10ef`.
- B: `V11-INTERNAL-PROOF-source-tabs-mechanical-B.pdf`, SHA-256 `2b11bf8b013920f105361d29b952e3eafdce9aa99601eab325b186da17cb26d9`.
- P1 Rasgos: `TRAITTAB` y origen `ORIGENTAB` en página 1, variantes A/B.
- Habilidad personalizada `SKILLTAB`: A página 1, B página 5 (continuación según gramática de variante).
- Fuente del conjuro `SRC7`: P4 página 4, variantes A/B.
- Libro opcional: `BOOK_TAB_START` en página 10, `BOOK_TAB_FINISH` en 11; `CONTINUACIÓN: QA7` visible en 10 y 11. Nombre verdadero de conjuro con tabulación; ninguna pérdida detectada.
- Barlow Condensed y Fira Sans incrustadas, última NOTAS manuscrita, revisión de rasterizados P1/P4/Libro A y P1/P4/C2 B sin superposición observada. Fixture sintético de estrés derivado de backup auténtico, **no** historial de partida del propietario.

## Delimitación y siguiente bloque

**PASS LOCAL G3 del defecto concreto** — rutas restantes de tabulador fuente y encabezado Libro en PDF Desktop, más compilación Android y paridad de fuente implementada. NO equivale a Android instalado, paridad física certificada, validación visual integral o aprobación del propietario. No modificar falsamente `stage_checks` ajenos: matriz 105 global `NOT_VERIFIED`, 12 PASS locales de etapa previos, `active_candidate=null`, **G4 BLOCKED**; G5/G6 no iniciados, auditoría D-0076 48 secciones, 8 golden, 4 legados y pre-QA externo pendiente. Sin tocar ZIP golden, legacy, `main`, backend de producción ni secretos.

**Próximo trabajo:** seleccionar siguiente defecto material de semántica/paginación efectivamente no resuelto según matriz y contrato; agrupar aspectos relacionados, implementar pruebas específicas con fuente real. Una vez cerrados los módulos, conciliación total por regla y G4 sobre candidato exacto antes de cualquier QA propietario.
