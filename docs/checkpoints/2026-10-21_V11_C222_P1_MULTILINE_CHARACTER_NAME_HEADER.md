# C222 — Cabecera P1 segura para nombres reales con saltos de línea

Fecha: 2026-10-21 Chile. PR #173, rama `docs/pc-sheet-v11-approved-implementation-handoff` DRAFT / NO MERGE. **Estado final: C222 PASS LOCAL FÍSICO (no G4, no QA propietario).**

## Autoridad / alcance

Ruta main `AGENTS.md → RESUME.md → LATEST.md → checkpoint 2026-10-09`, continuación de rama desde C221 `29ff601d086b1744befe924ca19d26e35856dc20` (80 PDF reales/Scaffold #39707416839 SUCCESS). El alcance C222 es **robustez independiente P1/C2 para nombre de PJ persistido con CR/LF/TAB**, NO reparar M50800, repetir C211, rediseñar P4 ni reabrir geometrías aprobadas. No se modificarán familias legacy.

### Evidencia y fallo preventivo observado en código

`PcSheetV11HeaderNameRouter.route` remitía el `sourceName` sin normalizar CR/LF al medidor embebido Barlow, aunque la cabecera aprobada es física **de una línea** y PDFBox no admite saltos como caracteres dibujables. Era un riesgo reproducible por inspección del código, **no se afirma un fallo APK del propietario ni que el previo PDF hubiese sido capturado**. La rama V11 ya disponía de destino C2 source-backed para nombres largos, lo que permite preservar el texto real sin inventar ni ocultar identidad.

### Implementación acotada

- [Commit productivo + tests C222](https://github.com/MrSimkin/dnd_custom_aid/commit/7dc9d06974cd686bc1db8f5b915e515118908b55): Shared trata CR/LF/CRLF como espacios **solo en el avance de cabecera**; TAB conserva expansión manuscrita existente. Todo nombre con salto duro se envía a C2 conservando `sourceName` textual original, aunque la vista de una línea cupiera. Nombres corrientes no cambian, las fuentes, layout y renderers legacy no se editan.
- Test Shared `persistedCrLfAndTabsNeverReachOneLineFontButSurviveInC2`: medidor estricto rechaza CR/LF/TAB en una línea; comprueba texto original literal en C2 y navegación con ancho normal/reducido.
- Test real del servicio Desktop `g3RealCharacterNameHardBreakIsNavigableAndLosslessInBothV11Variants`: fixture auténtico Aldren, solo nombre alterado a tokens QA con CRLF/TAB, PDFs A/B, recuento una vez en cuerpo C2, cabecera `VER C2` y referencia física P1, última página NOTAS.
- QA ledger [run #39709479111](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39709479111): **SUCCESS** en SHA de código exacto. [Scaffold run #39709479218](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39709479218): **PENDIENTE al crear este checkpoint**; no declarar C222 PASS local hasta que sea SUCCESS y los PDFs reales se auditen. No inferir PASS desde solo compilación.

### Gate, QA y política de recuperación

**Sin cambios:** G0 histórico PARCIAL (PDF runtime Mara propietario original no recuperado); G1 semántico PASS previo; G2/G3 PARCIALES; **G4 BLOCKED con 105/105 NOT_VERIFIED, active_candidate=null**; G5/G6 pendientes. C211 sigue **BLOCKED**: 3/3 intentos normales consumidos, recuperación estructural 0/2 no activada; no reintentar sin PDF exacto/causa distinta. C221 previo PASS LOCAL no se invalida sin regresión demostrada. Conservar 32 M50800, V11-QA-001/002 y sus estados OPEN, sin cambiar JSON de aceptación del propietario. Android instalado no validado, propietario no ha dado aceptación de V11. No merge ni despliegue.

### Próximo paso

1. Verificar CI exacto de C222, corregir solo fallo causal si existe, dentro de RCR-1.
2. Recuperar 80 PDF genuinos previos y el nuevo lote genuino Desktop A/B; contrastar paginación, texto+coordenadas, vectores e imágenes de los 80 documentos comunes. Abrir/inspeccionar ambas nuevas páginas con textos CRLF y C2.
3. Dejar acta física con hash/SHA, PASS LOCAL solo para C222 cuando la prueba lo permita; **no promover G4**. Después continuar reconciliación contrato-golden D-0076 por áreas no resueltas, sin inventar el diseño per-source de P4 ni reiniciar C211.

## Evidencia cerrada de C222 (PASS LOCAL solo para el alcance)

- **CI del código exacto** `7dc9d06974cd686bc1db8f5b915e515118908b55`: [Scaffold #39709479218](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39709479218) **SUCCESS**, Kotlin Shared/Android/Desktop + generación de PDFs y auditores; [QA registry #39709479111](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39709479111) **SUCCESS**.
- **82 PDF Desktop reales (761 páginas)** en [artefacto #12396234354](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39709479218/artifacts/12396234354), ZIP SHA-256 `91305d815c5e74eb1540d718791de216c3fe09ea0de04c638dcbaa017d2bdf61`. Los **80 PDF existentes** [C221 #12396081016](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39707416839/artifacts/12396081016), SHA-256 `ec3736c33aad7f96e0964f8c5a6631bbfb63f68892a540ab59d997009db0f9c1`, conservan **80/80 paginaciones, 747/747 páginas de palabras/posiciones, 747/747 conjuntos vectoriales y 747/747 referencias de imágenes idénticas**. El PDF no tiene bytes idénticos por metadatos/serialización, afirmación no realizada.
- **Dos PDF nuevos A/B**, 7 páginas cada uno, contienen el nombre de fuente con CRLF/TAB en C2, marcador `VER C2` en cabeceras y enlace P1 → C2 página 4, sin repetición en el cuerpo ni superposición visible en inspección raster de las páginas P1/C2. SHA-256 A `62dd238115fc4fa63acee479c0c5d0129e9c43937998c48926b1e1f24d8ab8a2`, B `2580fed99d174fee93ca47a483abe195c777a039df9fd5d7831803fa5df5cc24`.
- Auditores físicos de CI [#12396209765](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39709479218/artifacts/12396209765): **82/82 PDFs, 761 páginas**, 0 errores de perímetro, 0 huecos obligatorios C207, 0 errores de doble marco/logo, 82/82 hojas finales NOTAS, guardas P1/P4 verdes; estos criterios parciales no equivalen al golden total.
- [Acta independiente JSON C222](../pc-sheet-v11/qa/evidence/2026-10-21_V11_C222_P1_MULTILINE_NAME_REAL_APP_PDFS.json). Evidencia de comparación externa generada con PyMuPDF contra los ZIP auténticos de las dos ejecuciones; la inspección visual se limitó a P1/C2 A/B nuevos.

**Veredicto: C222 PASS LOCAL del nombre multilínea y no-regresión en 80 PDFs**, no G4 ni aceptación global. **G0 PARCIAL · G1 PASS semántico · G2/G3 PARCIALES · G4 BLOCKED 105/105 NOT_VERIFIED active_candidate=null · G5/G6 pendientes**. C211 sigue BLOCKED 3/3, RCR-1 estructural 0/2; 32+2 observaciones owner sin falsa promoción; Android instalado/QA propietario sin verificar; PR #173 DRAFT NO MERGE.

**Siguiente paso autorizado:** reconciliar reglas G4 por módulo contra ocho PDFs golden y D-0076/contrato V11, o resolver otro defecto seguro de G2/G3 comprobable por PDF real sin rehacer C222 ni los bloques C212–C221. C211 solo se reabre con PDF exacto y triage RCR-1 válido.
