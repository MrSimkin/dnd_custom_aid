# V11 — G2 módulos de Equipo/Tesoro y Equipo Especial (2026-10-11)

**Autoridad:** PR [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173), única rama `docs/pc-sheet-v11-approved-implementation-handoff` (DRAFT, sin fusión). **G1 PASS semántico, G2 IN_PROGRESS sin PASS, G0 QA histórico PARCIAL**. ZIP de referencia V11 ya preservado y aprobado visualmente. NO introducir variante V12.

## Código real publicado: dos módulos P2/C2

**Commit `524020f273a869e281411805b71e820ad300a7ce` — Equipo ordinario y Tesoro:**

- `shared/src/commonMain/.../PcSheetV11PairedTableComposer.kt`: admite dos parejas de subcolumnas **nombre/cantidad**, usando rectángulos Carta y la interfaz de medición tipográfica real incorporada en la iteración previa. Conserva fuente `sourceId` de cada objeto; las monedas se identifican por la clave string real, no por UUID inventado. Mantiene la cantidad exactamente, incluyendo cero.
- Las filas largas pueden fragmentarse en líneas, columnas y páginas locales; la **cantidad se reserva una sola vez**, en la primera línea del registro, incluso con continuación del nombre. Los textos de etiqueta conservan offsets/carácteres originales; controles detectan solapamientos y campos demasiado angostos, sin truncar ni reducir fuente.
- Solo entran ítems **ordinarios**: no se imprimen en esta tabla textos largos de descripción, peso ni etiquetas `Consumible`, que el contrato V11 no solicita allí.
- Pruebas `PcSheetV11PairedTableComposerTest.kt`: monedas cero, cantidad única en continuaciones largas, 386 registros distintos, CR/LF original, tabla vacía, columnas demasiado angostas y claves repetidas. La prueba Desktop en `DesktopPcSheetV11FontMeasurerTest.kt` emplea el backup auténtico de **Mara** (29 objetos ordinarios y 5 monedas) con Fira Sans empaquetada; compara cada etiqueta y cantidad con su fuente.

**Commit `ed72c46e70155837fdef2eeb3672cec88f5eae81` — Equipo Especial ancho completo:**

- `PcSheetV11SpecialEquipmentComposer.kt` reserva un único cuerpo de texto a todo el ancho del área disponible, con espacio exclusivo para la marca papel ACTIVO. Cada objeto mantiene su `sourceId` y campos individuales nombre, ubicación, descripción, notas y estado documentado `active: Boolean?`. Si es desconocido, el marcador sigue **null**, sin inferir ACTIVO desde equipado/sintonizado/efectos temporales.
- Los campos largos pueden continuar en otra página; el marcador ACTIVO aparece una sola vez junto a la primera línea del **nombre** (sin replicarlo por fragmento). No se mezcla con la tabla ordinaria.
- Tests shared de dos objetos, descripción más alta que columna/página, conservación de campos, cero objetos, detección de ID duplicado/mala geometría. Test Desktop de backup real **Mara con 5 objetos especiales** y medidas de Fira Sans: conservación íntegra y marcador ACTIVO vacío.

## Lo verificado y lo pendiente

Los dos módulos reutilizan `PcSheetV11MeasuredFlowPlacer`, `PcSheetV11TextFlow` y `PcSheetV11ResolvedFont`. Se inspeccionaron rutas PDFBox reales de Desktop/Android, **pero aún NO se ha producido ninguna hoja V11 PDF, ni se han modificado los cuatro renderers antiguos**. Los tamaños y rectángulos de tests son **parámetros de escenario**, nunca coordenadas declaradas equivalentes a las del golden aprobado. Los números de página de flujo son **locales a cada sección**; todavía deben unirse al compositor físico P2/C2 con referencias globales y otras secciones (rasgos).

**CI (estado al registrar la evidencia):** `524020f` Scaffold `38176697986` pendiente; `ed72c46` Scaffold `38176858258` en ejecución, QA owner traceability `38176858259` **SUCCESS**. No declarar éxito de tests hasta comprobar las conclusiones, ni G2 PASS por tests parciales. Antes de avanzar a nuevos cambios funcionales verificar fallos reales y subsanarlos dentro de los límites RCR-1.

**Siguiente trabajo seguro:** cerrar CI en HEAD, luego composición física P2/P4/C2 usando áreas del golden, reservar referencias de continuación y dibujar con el backend V11 independiente. G3 UI/PDF no iniciado; G4–G6 pendientes. QA Mara 50800 original no recuperado para contraste directo; 32 M50800 y V11-QA-001/002 continúan OPEN, sin consumir RCR-1 (0/2). PR #173 NO fusionada; sus conflictos de routing con main quedan para reconciliación posterior.
