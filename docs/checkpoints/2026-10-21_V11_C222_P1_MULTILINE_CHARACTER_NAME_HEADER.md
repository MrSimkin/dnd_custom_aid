# C222 — Cabecera P1 segura para nombres reales con saltos de línea

Fecha: 2026-10-21 Chile. PR #173, rama `docs/pc-sheet-v11-approved-implementation-handoff` DRAFT / NO MERGE. **Estado inicial: IN_PROGRESS / verificación exacta pendiente.**

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
