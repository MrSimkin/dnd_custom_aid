# Retoma corregida: hacer que LA APLICACIÓN produzca PDFs reales (2026-10-11)

**Prioridad operativa explícita del propietario:** No seguir creando componentes geométricos aislados antes de demostrar exportación PDF en la App. El arte visual V11 ya está aprobado; la meta ahora es **exportación real desde código Kotlin local Android y Desktop**, después reproducibilidad, entrega QA y corrección visual. V11 es solo el nombre del diseño aprobado, **no una nueva ronda de diseño**.

## Auditoría de rumbo

- Antes del cambio había contratos de datos y paginación y tipografía Kotlin G1/G2. Estos compilan y pasan tests; eran necesarios, pero **no existía ningún renderer V11 que escribiera un PDF ni servicio V11 en la App**. Esta desconexión era una desviación de prioridad respecto al objetivo, reconocida al propietario.
- Las cuatro familias antiguas SÍ tienen exportador PDF operativo en el código existente; conservarlas intactas y su QA histórico abierto. El nuevo V11 debe usar otra ruta, nunca sustituir el renderer legacy ni hacer pasar un Classic como V11.
- Se **congelan nuevos submodelos sin demanda concreta de PDF real**. A partir de aquí cada cambio debe relacionarse con una generación PDF reproducible, el flujo Guardar/Compartir, una comparación de contenido/fuente PDF o un defecto identificado por esos tests.

## Primer trabajo correctivo publicado

**Commit `85acc2034a03f55a6b0282185bac83a8e8413692`:**

- `DesktopPcSheetV11PdfProofRenderer.kt` y `AndroidPcSheetV11PdfProofRenderer.kt`: dos adaptadores PDFBox separados de los renderers anteriores. **Escriben documentos PDF nativos Carta reales** en OutputStream con páginas iniciales de personaje/equipo/narrativa, P4 solo con hechizos reales, extensiones, notas digitales, Libro opcional y última hoja manuscrita. Usan la tipografía empaquetada Fira/Barlow y la medida/colocación shared. Las páginas se etiquetan inequívocamente `PRUEBA INTERNA V11 - NO APROBADA`.
- `DesktopPcSheetExportService.generateV11Proof` y `AndroidPcSheetExportService.generateV11Proof` usan fuentes reales de `PcSheetV11SemanticMapper` para retornar **bytes PDF emitidos por la propia aplicación**, no un mockup Python/ReportLab ni bytes de una hoja anterior. La ruta actual es QA-only, **no está aún conectada a los botones de UI Guardar/Compartir**.
- Kotlin Desktop test `DesktopPcSheetV11RuntimePdfProofTest` genera directamente desde los servicios de la App con los respaldos auténticos de Mara y Aldren, comprueba la cabecera `%PDF-`, apertura con PDFBox, Carta 612×792 pt, texto extraíble, Libro condicional, no inventar P4 de Aldren y notas manuales finales.

**Commit `b115110ccc06a26cd69f8bf7efe7bbc0c31715d4`:**

- Los tests **guardan dos archivos PDF reales** en `desktopApp/build/pc-sheet-proofs/V11-INTERNAL-PROOF-Mara-B.pdf` y `V11-INTERNAL-PROOF-Aldren-A.pdf`. El workflow existente `Scaffold checks` ya publica esa carpeta en el artefacto `pc-sheet-populated-template-proofs` **solo si el build/test pasa**.
- CI sobre commit `b115110`: `Scaffold checks` run [38178067583](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38178067583) y QA ledger run [38178067552](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38178067552). QA ledger = SUCCESS, Scaffold Kotlin = IN_PROGRESS a la última comprobación. **NO declarar que un PDF fue generado satisfactoriamente en CI hasta obtener conclusión SUCCESS y verificar artefacto.**

## Límites y pasos vinculantes

Este renderer es **prueba real pero NO layout V11 final**: no reproduce aún logo, retrato, marcos/atribu. C2, modulo manuscrito de P1/P2, casillas, etiquetas y continuaciones fieles. El flujo simplificado puede carecer de elementos del dominio; la aceptación de pérdida cero G2/G4 y la comparación al golden aún están pendientes. **Nunca entregar estos PDFs como candidato de aprobación del propietario.** La existencia de bytes PDF tampoco equivale a instalación en Android ni calidad visual.

**Siguiente orden innegociable:** (1) CI Kotlin y artefactos PDF realmente generados; (2) inspeccionar PDF extraíble y páginas fuente, reparar errores concretos; (3) completar el PDF visual y contenido integral basado en golden aprobado, incluida igualdad Android/Desktop; (4) conectar V11 A/B a Guardar/Compartir de la App y probar flujo real sin alterar 4 familias; (5) G4/G5 QA de regresión, APK y aprobación propietaria, y G6 auditoría final/merge solo autorizado.

**Autoridad:** PR #173 DRAFT/OPEN, no merge. G0 histórico PARCIAL, G1 PASS semántico, G2 IN_PROGRESS/NO PASS; G3 **primera prueba física de renderer de App implementada, aún no validada**; G4–G6 pendientes. 32 M50800 y V11-QA-001/002 OPEN. RCR-1 0/2. Conflictos documentales con main pendientes antes de integración final. 
