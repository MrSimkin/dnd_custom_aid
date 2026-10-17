# V11 G1/G2/G3 — Tabulaciones fuente e impresión PDF local

**Fecha:** 2026-10-17. **PR:** [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173), rama `docs/pc-sheet-v11-approved-implementation-handoff`. V11 y G0→G6 no cambian. Sin merge ni cambios en `main`, exportadores legacy o PDF golden.

## Defecto y reparación de código

Los campos reales multilínea (P3 Historia, C-N3 notas digitales y otros flujos V11) pueden contener `\\t` de entrada. El flujo anterior entregaba tabuladores literales a medición `PDType0Font.getStringWidth` y dibujo `PDPageContentStream.showText`; PDFBox no garantiza un glifo imprimible para tabulación. El problema afecta a datos válidos no contemplados por los fixtures anteriores.

**Commit productivo:** [`11a2723fcb9502849786349515cd872dfe80f283`](https://github.com/MrSimkin/dnd_custom_aid/commit/11a2723fcb9502849786349515cd872dfe80f283), descendiente directo de `239bcb2...`. Corrige únicamente ocho archivos V11: una proyección común `pcSheetV11PrintableText` que expande tabuladores a cuatro espacios **solo durante medición y representación física**, medidores tipográficos PDFBox Desktop/Android, renderizadores y pruebas. El origen, ID, asociación, índices UTF-16 y texto real quedan sin transformar.

**Prueba compartida G2:** `sourceTabsUsePrintableWidthWithoutChangingSourceOffsetsOrCharacters` comprueba `A\\tB`, cortes reales por anchura, salidas imprimibles, offsets y reconstrucción fuente íntegra.

**Prueba G3 de aplicación:** `g3RealTabbedNarrativeAndDigitalNotesRenderOnBothVariantsWithoutSourceMutation` usa backup genuino Aldren y fuentes reales, inyecta solo relato y notas con tabuladores, ejecuta `DesktopPcSheetExportService.generateV11Proof` para variantes A/B, comprueba P3, C-N3, NOTAS final y fidelidad de fuente. La prueba ahora conserva los dos PDF genuinos como `V11-INTERNAL-PROOF-source-tabs-A.pdf` y `-B.pdf` para examen físico independiente.

## Evidencia verificada y límites

- [Scaffold #39057693058](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39057693058) **SUCCESS** en commit `11a2723...`: Kotlin, backend y base de datos verde.
- [Control QA #39057693073](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39057693073) **SUCCESS**.
- Artefacto del primer commit: [16 PDF genuinos #12109837816](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39057693058/artifacts/12109837816), ZIP SHA-256 `7dc2d3d53037e67377811e2bc91c1ebf2541354aaa761f0339a222b8c2e2f96c`, CRC sin errores. Son baselines existentes; **no constituyen inspección visual del caso tabulado**.
- El nuevo artefacto A/B exacto deberá inspeccionarse físicamente por texto, páginas, posiciones, fonts y contenido antes de asignar PASS de etapa. No registrar `independent_inspection=true` sin hacerlo.
- Matriz: 105 reglas y 12 PASS locales de etapa anteriores. Nuevos checks de tabulaciones **no promovidos**: su evidencia completa exige CI del presente commit, PDFs tabulados y revisión física. G4 `BLOCKED`; `active_candidate=null`, G5/G6 pendientes. No se ejecutó Android instalado ni QA propietario.

**Siguiente prioridad:** verificar CI y analizar los nuevos PDFs A/B, comprobar no pérdida ni cambios fuera de campos intencionales, luego continuar por módulos pendientes y conciliación de D-0076/golden/legacy para G4. No reiniciar BK04/BK05/C206 ya probados localmente.
