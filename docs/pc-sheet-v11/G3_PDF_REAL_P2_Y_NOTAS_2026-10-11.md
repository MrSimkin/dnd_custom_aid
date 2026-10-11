# Hito G3 — PDFs reales de la App con P2/C2 y hoja final manuscrita (2026-10-11)

**Objeto autorizado:** que la aplicación **genere, guarde y comparta PDFs reales**, basados en el diseño A/B ya aprobado. **Prohibido confundir prueba interna con PDF final aprobado.** Única implementación: PR #173 DRAFT, rama `docs/pc-sheet-v11-approved-implementation-handoff`, sin merge ni tocar renderers antiguos.

## Lo que ya estaba demostrado

- El código de la propia aplicación produce PDFs locales reales: `DesktopPcSheetExportService.generateV11Proof` usando PDFBox y fuente Fira/Barlow del repositorio, respaldos auténticos Mara/Aldren, salida `%PDF-`, Letter 612×792, lectura de texto con PDFBox.
- **CI real certificado:** Scaffold run `38178067583` SUCCESS; artefacto `pc-sheet-populated-template-proofs` id `11716577069` contiene PDFs internos creados durante esa ejecución.
- Guardar/Compartir de prueba interna integrado para Android DEBUG (commit `b622a48`) y Desktop PC Manager (commit `933ed4d`), con opción A/B; ambos y el checkpoint final con Scaffold SUCCESS en `38178454511`, `38178555148`, `38178703810`. Los cuatro renderers anteriores permanecen intactos. Los controles UI internos no son equivalentes a un APK instalado y aprobado.

## Cambios ejecutados en esta continuación

**`d3ee992f38320bb95c2ad04fcdc05170057f41f8`: PDF P2/C2 ya dibuja módulos verdaderos, no listado corrido.**

- Adaptadores reales `DesktopPcSheetV11PdfProofRenderer` / `AndroidPcSheetV11PdfProofRenderer`: la página **P2** muestra dos parejas **EQUIPO / CANTIDAD**, **TESORO / CANTIDAD**, **RASGOS** en dos columnas, y **EQUIPO ESPECIAL** a ancho físico completo con casillas ACTIVO basadas en estado verificado.
- Reutiliza `PcSheetV11PairedTableComposer` y `PcSheetV11SpecialEquipmentComposer` ya testeados para conservar UUID/clave y contenido íntegro en las páginas locales, incluyendo montos reales cero, nombres largos y descripciones de objetos. Las páginas de continuación se materializan como **C2 luego de P3 y P4 si existe**, sin rasgos insertados en P3. Se dibujan reglas y separadores físicos PDFBox, no texto en bruto exclusivamente.
- Todos los rectángulos actuales **son geometría de PRUEBA INTERNA, no coordenadas medidas del golden visual**. **No** se declara completada la composición dinámica multisección C2 de relleno de huecos ni referencias de páginas físicas finales.
- Tests Desktop adicionales comprueban que PDFBox extrae módulos y monedas reales del PDF generado.

**`31442693075072d10c5303203e3652ae0d1673c5`: la última hoja se dibuja para escritura manual.**

- Dos columnas de renglones en la zona superior y una cuadrícula de celdas completas (33 columnas × 22 filas, 16pt, sin última celda cortada) en zona inferior. **Ningún contenido digital** en la última hoja.
- Test Desktop real adicional para **Ilyra**: P4 sólo con spells, Libro opcional solicitado, última hoja exclusivamente manuscrita. El test genera archivo `V11-INTERNAL-PROOF-Ilyra-B.pdf` y lo abre con PDFBox.
- Las páginas siguen marcadas `PRUEBA INTERNA V11 - NO APROBADA` en el flujo de diagnóstico, sin exponerlo como hoja final.

**`d6c172d6f62bcdd88e6d9c42dcf7ce20669dc031`: pruebas descargables y acotadas.**

- Workflow `.github/workflows/scaffold-check.yml` añade sólo a la rama de implementación el artefacto `v11-internal-real-app-pdf-proofs`: tres PDFs producidos realmente por el servicio Desktop (Aldren-A, Mara-B, Ilyra-B). `if-no-files-found:error` impide declarar success si faltan.
- Este artefacto pequeño evita descargar un paquete grande de renders previos. Continúa manteniendo artefactos preexistentes y APK DEBUG; **NO etiquetar dicho APK como candidato propietario**.

## Gate / próximo paso operativo

CI iniciado en:
- `d3ee992`: Scaffold `38180740199`, QA ledger `38180740241` (SUCCESS).
- `3144269`: Scaffold `38180883334`, QA ledger `38180883349`.
- `d6c172d`: Scaffold `38180947733` (in progress a la última consulta).

**Revalidar conclusiones exactas del HEAD antes de declarar estos últimos módulos PASS** y confirmar artefacto `v11-internal-real-app-pdf-proofs`. Ante fallo, leer logs Kotlin y reparar error demostrado, sin seguir ampliando módulos. Después inspeccionar las páginas PDF en imagen y compararlas con golden V11 aprobado; completar logo, bordes azules, retrato, gramática manuscrita de secciones, módulos P1/P4 y continuidad C2/C-N3, con fuente/símbolos. Finalmente binario Android de QA con versión/procedencia y validación manual en App (G4–G5).

**Estados:** G0 histórico Mara 50800 PARCIAL; G1 semántico PASS; G2 parcial; G3 PDF Desktop de prueba PASS previo, nueva salida P2/Notas pendiente CI, sin aprobación visual; G4–G6 pendientes. 32 M50800 y V11-QA-001/002 OPEN; RCR-1 0/2; PR #173 no fusionada y con conflictos documentales con main.
