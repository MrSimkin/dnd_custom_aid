# V11 — G2 geometría compartida y reservas de líneas (2026-10-11)

**Estado:** G2 IN_PROGRESS; primer posicionamiento geométrico implementado en la única PR #173, **sin PDF generado ni G2 PASS**. G1 semántico quedó PASS con CI del commit `4acc1a82`. G0 histórico sigue PARCIAL.

## Contraste de rutas reales

Se leyeron las primitivas de PDF de `desktopApp/.../DesktopPdfRenderingPrimitives.kt` y `androidApp/.../pdf/AndroidPdfRenderingPrimitives.kt`. Ambas usan PDFBox (Android: port Tom Roush) y tipografías cargadas mediante recursos; la ruta Android indica que es **generada** desde su autoridad Desktop. Los cuatro renderers heredados y esos archivos **no se modificaron**. Las fuentes reales Barlow/Fira/Symbols v8 están localizadas en los assets del proyecto, pero **no han sido medidas ni probadas como render V11**.

## Código efectivo nuevo

Commit `1109a18fda135f72338abcbacb69f7bbc7ec4f6b`:

1. `PcSheetV11TextFlow.kt`: cada fragmento agrega `startLineIndexInColumn`, calculado a partir de la capacidad consumida de la columna. Antes conocíamos solo columna/página; dos items consecutivos habrían podido ocupar visualmente la misma fila si se posicionaban con el origen de columna. La nueva reserva conserva el desplazamiento real.
2. `PcSheetV11MeasuredFlowPlacer.kt` (commonMain Kotlin): contrato geométrico de hoja **US Letter 612 × 792 puntos**, origen PDF inferior izquierdo, rectángulos finitos dentro de la página, partición de 1/2/3 columnas con gutter explícito y positivo de ancho resultante. Coloca cada línea en su propia celda (página *local*, columna, fila, offsets UTF-16, `sourceText` y `drawableText` sin CR/LF final). Rechaza celdas superpuestas, desbordes de Letter, rectángulos inválidos, columnas imposibles y glifos que no caben según el medidor suministrado.
3. `PcSheetV11MeasuredFlowPlacerTest.kt` (commonTest): verifica divisiones a 2/3 columnas, posiciones, textos adyacentes sin pisarse, narrativas de 110 renglones con cambio de página, conjuro sin espacios que cruza columnas, CRLF y línea vacía, guardas de fuera de límites y repetibilidad.

**Límite funcional expreso:** las medidas `widthPt`/`lineHeightPt` y la zona útil vienen del llamador. Los valores 36/60/12pt de tests son **fixtures sintéticos, NO geometría visual aprobada**. `PcSheetV11TextWidth` todavía no está conectado al font real Barlow/Fira/PDFBox. La celda es una **reserva**, no una baseline garantizada; se necesita metricas verdaderas y shaping para dibujar. Tampoco están resueltas las tablas Ataques/Equipo/Tesoro/Conjuros, módulos P1/P2/P3/C2, fuentes/logotipo, continuidad de referencias de ida/vuelta, huecos NOTAS y las páginas físicas mediante el `PcSheetV11PageSequenceBuilder`.

## Gate CI y plan inmediato

En el commit Kotlin `1109a18` ya pasó el **V11 owner QA traceability** `38174780469`; la suite `Scaffold checks` `38174780447` continuaba **IN_PROGRESS** al documentar. **Nunca inferir PASS sin revalidar esa ejecución**.

Próximo paso correcto: verificar la suite y resolver fallos reales antes de extender. Luego obtener medición tipográfica real con la misma fuente/recurso en Desktop y Android y transformar las reservas en planos de módulos que respeten el visual V11 congelado, conservando todos los offsets/IDs de G1. Solo después se implementan los renderers independientes del G3.

**Invariantes:** PR #173 DRAFT/OPEN, no merge de `main` ni force push. G0 QA original Mara pendiente de contraste de PDF del APK; 32 observaciones M50800 y V11-QA-001/002 OPEN, RCR-1 0/2. `main` solo apunta a la rama mediante PR documental #174. No pedir nuevas aprobaciones estéticas o PDFs viejos para el G2 aislado.
