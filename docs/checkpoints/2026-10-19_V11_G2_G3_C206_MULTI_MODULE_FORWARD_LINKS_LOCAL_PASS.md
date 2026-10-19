# V11 — C206: P2 enlaza físicamente todos sus módulos C2 independientes (PASS LOCAL)

**Fecha:** 2026-10-19. **PR de implementación:** [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173), rama `docs/pc-sheet-v11-approved-implementation-handoff`, **DRAFT, sin merge**. **HEAD funcional comprobado:** `36914c870305c96f61273ce48d2fb53e82ec9b75`. Autoridad `AGENTS.md → RESUME.md → LATEST.md → este checkpoint`, contrato V11 aprobado, regla **C206**. Diseño y aceptación del propietario NO han sido aprobados para este candidato.

## Defecto material comprobado en los PDF originales

En los 70 PDF genuinos Desktop del lote anterior [#12222611889](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39326088504/artifacts/12222611889), P2 imprimía solo `CONTINÚA EN C2 PÁG. <primera>`, mientras cada una de varias secciones autónomas C2 imprimía su propio `VIENE DE P2 PÁG. 2` sin que P2 indicara cómo encontrar **ese** módulo.

- `V11-INTERNAL-PROOF-C206-both-paired-overflow-A/B.pdf` (10 folios): P2 apunta a Equipo p5, pero **Tesoro empieza en p7**, sin referencia de P2 a p7.
- `V11-INTERNAL-PROOF-Mara-A/B.pdf`: P2 apunta al C2 mixto Rasgos p5; **Equipo empieza en p6** y **Especial en p7**, sin enlaces propios desde P2.
- Incumplimiento comprobado en código: `c2FromP2` escogía exclusivamente la página menor y la usaba como único texto de navegación; no se trataba de error de paginación ni de contenido fuente.

## Cambio funcional publicado y regresiones

- **Desktop** `cbcded7055133947c8300197a18bc3aa7166efed`, **Android** `3673b03c26d762bc75cd43e8241cff1159f91138`: ambos calculan desde `physicalSectionKeys` reales una lista de destinos por módulo (Rasgos, Equipo, Tesoro, Especial, o Equipo/Tesoro agrupados), deduplicada por módulo y ordenada por número físico FINAL. Mantienen íntegro el primer `CONTINÚA EN C2 PÁG. N`; agregan `OTROS C2: [MÓDULO] PÁG. [N] | ...` **solo cuando hay otro destino físico**, en la reserva inferior P2, PDFBox (42,63). No se modifica contenido, celdas, orden, cantidades, paginación, marcos, roles, último NOTAS, encabezados o sección de datos digitales. Falla explícita si los enlaces no caben en 528 pt disponibles.
- Test Desktop genuino [`36914c870305c96f61273ce48d2fb53e82ec9b75`](https://github.com/MrSimkin/dnd_custom_aid/commit/36914c870305c96f61273ce48d2fb53e82ec9b75): amplía sin duplicar `g3C2EquipoAndTesoroOverflowKeepIndependentPhysicalNavigationInBothVariants` para exigir TESORO p7 en la región física del pie de P2; amplía `g3MixedC2ActualAldrenIlyraMaraNavigationKeepsTrueP1AndP2Origins` para exigir referencias EQUIPO p6 y ESPECIAL p7 en Mara A/B, además de las referencias P1/P2 previas.
- Fuente Android/Desktop inspeccionada: misma implementación de referencias, con pruebas Kotlin reales en CI. No pretende validar ejecución de PDF Android en dispositivo.

## Evidencia real exacta y comparación independiente

- [Scaffold #39328633469](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39328633469) **SUCCESS** en HEAD funcional exacto (jobs Kotlin, backend y hosted-database), [QA traceability #39328633307](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39328633307) **SUCCESS**, sin interpretar el registro QA como aprobación del propietario.
- [Artefacto 70 PDF auténticos #12223282713](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39328633469/artifacts/12223282713), total **647 folios**, comparados contra [70 PDF anteriores #12222611889](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39326088504/artifacts/12222611889). PyMuPDF: **70/70 documentos**, **0 folios agregados/eliminados**, **0 palabras antiguas eliminadas o desplazadas** (todas las cajas texto originales persisten, coordenadas comparadas a 0,1 pt), **0 cambios de rutas vectoriales o imágenes**, **24 PDF con la nueva línea solo en P2 p2**, **46 PDF sin cambio de palabras**. Ningún otro folio cambia.
- La línea PDF real de C206 A/B es `OTROS C2: TESORO PÁG. 7`, bbox ≈ (42,720.6,145.1,731.4) desde el borde superior. Mara A/B muestra `OTROS C2: EQUIPO PÁG. 6 | ESPECIAL PÁG. 7`, bbox ≈ (42,720.6,217.3,731.4). La reserva queda por encima del watermark (top PDF≈737.6) y lejos del cuerpo fuente; se abrieron los raster auténticos nuevos de P2 de Aldren C206-A y Mara-A, **sin solape visual**. Los destinos escritos coinciden con los títulos de página C2 realmente impresos. No se han comprimido nombres ni creado enlaces falsos.
- **PASS LOCAL limitado al cumplimiento de C206 de referencias P2→múltiples módulos C2 en PDF Desktop A/B demostrado**. No autoriza estado `PASS` de `C206` en la matriz integral ni de toda V11.

## Gates inmutables y próxima ruta

**G0** QA histórico PARCIAL, **G1** PASS semántico previo, **G2/G3** PARCIALES con esta ruta C206 PASS LOCAL, **G4 BLOCKED** (**105/105 reglas `NOT_VERIFIED`**, `active_candidate=null`), **G5/G6** pendientes, RCR-1 **0/2**. Golden V11, D-0076, 32 controles M50800 y registro V11 QA, matriz y gates intactos. Android instalado/owner todavía **sin PASS** y PR **sin merge**.

**Siguiente trabajo previsto:** ahora que la conservación del contenido, C2 manuscrito, la navegación P2→C2 y los casos P3/C-N3 específicos tienen evidencias de PDF real, priorizar la **conciliación G4 por regla y por módulo contra los ocho golden**, empezando por P3/C2, y preparar candidato Android identificable con verificación en dispositivo cuando el protocolo lo habilite. Corregir únicamente un defecto de salida realmente probado; no crear mejoras estéticas especulativas ni certificar las 105 reglas por una sola prueba local.
