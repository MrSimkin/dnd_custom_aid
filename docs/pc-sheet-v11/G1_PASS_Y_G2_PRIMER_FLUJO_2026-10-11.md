# V11 — G1 PASS semántico y primer paso G2 (2026-10-11)

**Ruta:** [PR #173](https://github.com/MrSimkin/dnd_custom_aid/pull/173), rama `docs/pc-sheet-v11-approved-implementation-handoff` (DRAFT, **NO fusionar**). V11 A/B es opt-in: las cuatro familias previas permanecen sin cambios de renderer ni UI.

## Gate G1 — PASS limitado y comprobado

**PASS del contrato semántico aislado**, con fuente Kotlin y test compartidos de los commits `97b4e8f8ecec9465bea55fabc15fcffc5a658195` y `38fbb0066e9054982ce87fa9dcb799a7693abfee`. En HEAD `4acc1a82e8251e40ee7f8b721429946dfe827d5c`:

- GitHub Actions **Scaffold checks** run [38170199248](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38170199248) = `completed/success`. Incluye `gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build`, pruebas del contrato y verificación de los respaldos originales de Aldren, Ilyra y Mara.
- **PC Sheet V11 owner QA traceability** run [38170199195](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38170199195) = `completed/success`.
- El mapper conserva IDs, los seis atributos + personalizados, habilidades vinculadas, conjuros/preparación por fuente, slots total/gastado, monedas 0, notas, equipo sin inferir ACTIVO, retrato y fallback de snapshot; véase `G1_CONTRATO_SEMANTICO_Y_FUENTES_2026-10-11.md`.

Este **PASS G1 semántico no cierra G0 QA histórico**: los archivos PDF exactos del propietario para Mara 50800 siguen sin inspección directa. La excepción autorizada separa aquella evidencia, necesaria más adelante para regresión/aceptación G4–G5, del mapeo V11 nuevo. **No se declara un PASS de G0 ni de PDFs V11.** Las 32 observaciones M50800 y V11-QA-001/002 permanecen OPEN; RCR-1 continúa 0/2.

## Gate G2 — IN_PROGRESS: primeras primitivas tipadas

- Commit `a5eb0e45a03ab7f968c0f004dcaa34491687e2d2`: `PcSheetV11TextFlow.kt`, paginador de **texto** determinista y lossless en Kotlin shared, nunca ReportLab. Recibe una interfaz `PcSheetV11TextWidth` y presupuesto de columnas `widthPt/heightPt/lineHeightPt` desde el llamador. No escoge valores de fuente falsos; **exige** medición externa real al integrarse en renderer.
- Salida: fragmentos con `sourceKey`, offsets UTF-16, rangos por línea, marcas de continuación, índice local de página/columna. Conserva texto exacto, espacios y CR/LF; no corta pares sustitutos UTF-16. Si un glifo no cabe, falla explícitamente en lugar de reducir el tamaño mínimo o borrar texto. Modo `keepTogetherIfFits` mueve una entrada corta íntegra a la columna siguiente; textos más grandes que una columna se parten con continuación. El diseño de página física/estilo definitivo aún no existe.
- Pruebas en `PcSheetV11TextFlowTest.kt`: salto entre columnas, renglones vacíos, saltos CRLF, surrogates/emoji, 386 entradas, palabra larga, nulos/medidas imposibles, fuente duplicada, estabilidad determinista. **Solo estos tests usan medida monoespaciada sintética como control de algoritmo**, nunca se atribuye verificación geométrica de PDF real.
- Commit `014f0e9ee8e9806dc4bf4e007c342b7bde29e917`: `PcSheetV11FlowSourceMapper.kt` asigna datos reales a cinco roles de flujo: P3 narrativa, notas digitales C-N3, descripciones de rasgos (P2/C2), detalle de equipo especial (ancho completo) y texto del Libro opcional. Una nueva prueba `v11FlowSourceSectionsRespectMaraOriginalContent` verifica fuente Mara auténtica + paginación lossless por sección.
- **Importante**: `ORDINARY_EQUIPMENT` y `QUICK_SPELLS` son tablas, no párrafos genéricos. Se mantienen fuera del mapeador de texto hasta implementar filas y módulo de columnas reales. Asimismo están pendientes encabezados, logo, marcos C2/C2c, fonts auténticas, grid manuscrita, referencias de páginas físicas, composición 2/3 columnas, corte por palabras/forma Unicode real y notas finales. No inferir G2 PASS a partir de una prueba de texto.

### Gate y criterio próximo

**G2 NO PASS** hasta verificar nuevo `Scaffold checks` sobre el HEAD que contiene ambos commits, integrar posicionamiento/medición con métricas reales, completar módulos P1/P2/P3/P4/C2/C-N3/Libro/última página, referencias físicas y pruebas de integridad física/fragmentos. Los ensayos actuales son solo primer lote G2 y no consumen intentos RCR-1 por sí mismos.

**Próxima acción segura, sin intervención del propietario:** revisar resultados exactos de GitHub CI para G2; corregir únicamente fallos reproducibles. Luego integrar plan lógico de página, módulos de dos/tres columnas y las reglas fijas de continuación/última página. No cambiar los cuatro renderers legacy ni mezclar el nuevo flujo como un renderer Classic. Mantener la PR abierta.
