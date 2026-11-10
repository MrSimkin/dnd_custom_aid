# V11 — P3: continuidad narrativa identificable, PASS LOCAL en PDF Desktop A/B

**Fecha:** 2026-10-19. **PR exclusiva:** [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173), **DRAFT / NO MERGE**. **HEAD funcional exacto comprobado:** `70a130266b48b52133637e1589f87ab3f0fa2bef`. Autoridad `main/AGENTS.md → RESUME.md → LATEST.md → checkpoint`, contrato `docs/pc-sheet-v11/CONTRATO_VISUAL_APROBADO.md`, reglas P303–P305 y D-0076 compatible, NO modificar matriz/candidato sin evidencia de aceptación global.

## Hallazgo original y cambio funcional

Previo al lote, el renderer P3 imprimía el título genérico `P3 - Narrativa / continuación / N` en todas las hojas de continuación; solo la primera llevaba dentro del flujo un encabezado del registro narrativo original (`p3-story-heading`, `p3-field-heading:*`). En una historia que ocupa varias hojas, las posteriores quedan sin una referencia explícita al relato activo en su propia cabecera, aunque el origen era conocido en el `p3Overflow`. En las **68 PDF existentes** del lote anterior no había un caso que ejerciera una P3 de varias páginas; no inventar fallo de usuario observado.

Se modificaron exclusivamente:
- `desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt` — commit `2dbf760d`.
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt` — commit `1e41243a`.
- `desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt` — test real `g3P3ExtremeStoryContinuationKeepsRealSourceHeadingOnEveryLaterPageAB` en commit `70a13026`.

Desde la segunda hoja de continuación P3, si la primera celda impresa es un ítem `narrative:*`, la cabecera física vuelve a identificar el campo de origen con el texto del encabezado `p3-story-heading` o `p3-field-heading:*` ya incluido en `entries`. Se mide con Barlow incrustada a **12 pt** y el ancho real de **524 pt**. Si no cabe el título completo, prueba prefijo de navegación compacto sin eliminar el nombre fuente; no modifica source IDs, offset, texto, layout de dos columnas, pie de página ni cantidad de páginas. Ambos renderizadores fueron actualizados idénticamente; ninguna familia legacy tocada.

## Demostración exacta

- [Scaffold #39323890281](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39323890281) **SUCCESS** en commit `70a130266b48b52133637e1589f87ab3f0fa2bef`, incluidos Kotlin/Android/Desktop, backend, DB y auditorías físicas, y [QA traceability #39323890248](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39323890248) **SUCCESS**.
- [Artefacto genuino de la App/Desktop #12221423646](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39323890281/artifacts/12221423646): **70 PDF / 647 folios**. Nuevo test preserva `V11-INTERNAL-PROOF-P3-long-story-heading-A.pdf` y `-B.pdf` (11 páginas cada uno; P3 continuación en folios físicos **4, 5, 6, 7**).
- Comparación PyMuPDF frente a [lote anterior genuino de 68 PDF #12221230614](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39321593761/artifacts/12221230614): **68/68 sin pérdida de archivo, número de folios, palabras/coordenadas redondeadas a 0,1 pt ni diferencias de rutas vectoriales**; la única diferencia respecto al conjunto previo son los **dos nuevos PDF** A/B. No confundir estabilidad de contenido/geometría con identidad binaria del PDF.
- **A y B**: el encabezado `HISTORIA Y CRÓNICA / CONTINUACIÓN` aparece en las hojas P3 5–7; la primera de continuación p4 ya contenía su título de sección original. El contenido prueba `P3LONG-CONTINUATION-START` y `P3LONG-CONTINUATION-END` exactamente una vez y las **500** ocurrencias de `Viaje` y `registrado` (en cada variante); última NOTAS intacta.
- Se abrieron/rasterizaron p4, p5 y p7 de la nueva variante A; la cabecera se muestra en la reserva física sin invadir marcos ni renglones. La auditoría física del workflow es independiente y de alcance acotado; la revisión visual local NO equivale a QA de propietario ni paridad golden total.

## Gates, reservas y siguiente trabajo

**PASS LOCAL exclusivo de P3 narración extrema/Desktop A/B**; se conserva **PASS LOCAL C-N3** previo del checkpoint `2026-10-19_V11_CN3_RUNTIME_PDF_LOCAL_PASS.md`. **G0** owner/legado PARCIAL; **G1** semántico PASS anterior; **G2/G3** PARCIALES con estas rutas locales acreditadas; **G4 BLOCKED**, **105/105 `NOT_VERIFIED`**, `active_candidate=null`; **G5/G6** pendientes, RCR-1 0/2. Android generador code-sync/build verificado, **no** PDF Android instalado/revisado; no aprobación del propietario. Golden V11, M50800 QA, D-0076, matriz de reglas y registros pre-QA intactos. PR #173 permanece DRAFT, NO MERGE.

**Próximo trabajo funcional seguro:** revisar **C2** contra fuentes/contrato y PDFs reales del mismo candidato, seleccionar únicamente un incumplimiento material demostrable, corregir Android/Desktop en un bloque coherente y exigir PDF A/B + comparación no-regresión. No volver a ejecutar estrés P3/C-N3 ya demostrado sin cambios que lo justifiquen; luego construir candidato G4 único, conciliar reglas por ID y realizar la comprobación Android/owner autorizada cuando corresponda.
