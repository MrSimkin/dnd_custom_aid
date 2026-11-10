# V11 — C2 espacio vacío en tabla de pares asimétrica, PASS LOCAL con PDF real A/B

**Fecha:** 2026-10-19. **PR:** [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173), rama `docs/pc-sheet-v11-approved-implementation-handoff`, DRAFT, sin merge. **Candidato funcional validado:** commit `d568a492e6f559cf9b0e001b9aa259212384c88e`; [Scaffold #39326088504](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39326088504) **SUCCESS** y flujo de trazabilidad V11 del commit PASS. Autoridad: contrato `docs/pc-sheet-v11/CONTRATO_VISUAL_APROBADO.md`, regla **C207** (huecos utilizables llevan cartela NOTAS y cuadrícula completa) y **C208** (ningún nuevo folio de relleno). Otros módulos y golden V11 no modificados.

## Defecto demostrado por PDF previo, no inferido

[70 PDF Desktop previos #12221423646](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39323890281/artifacts/12221423646): `V11-INTERNAL-PROOF-C206-both-paired-overflow-A.pdf` y variante B, página física **6/10**, `C2 - Equipo y tesoro / EQUIPO / 2`. Las 82 entradas de Equipo C206 conservan orden/parejas/valor; el par IZQUIERDO continúa hasta el suelo de la tabla, pero el DERECHO termina en `EQUIPO-C206-082` hacia y≈337.6 (coordenada top-left PDF); el suelo visible está hacia y≈708. **Quedan ~370 pt vacíos a la derecha**, sin `NOTAS`, aunque el izquierdo sigue ocupado (140 palabras en área inferior izquierda; **0** en área inferior derecha). Se inspeccionó raster del PDF fuente, mismo defecto en A/B. No extrapolar el hallazgo a todas las C2.

## Cambio implementado

- Kotlin productivo Desktop [`9ebf5b3`](https://github.com/MrSimkin/dnd_custom_aid/commit/9ebf5b3fa84aa888105df9cf0a9ba6eb519fb5a8) y Android [`b4c6b7d`](https://github.com/MrSimkin/dnd_custom_aid/commit/b4c6b7d1d5c5cbaf629b4d9ef8b3ca9b78589a18): en `drawPaired` C2, para tabla de pares llena en altura y **solo si las fuentes demuestran asimetría física real ≥128 pt**, comparar las celdas de origen de ambas parejas; sobre la zona inferior sin datos de la pareja más corta enmascarar los renglones de tabla vacíos y dibujar una cartela `NOTAS` más cuadrícula **completa** de paso 16 pt. Se exige margen de seguridad de texto ≥10 pt por debajo de la banda; mínimo 4 filas, no se cambia el flujo medido ni cantidad de páginas, fuente, numeración, cantidades o la zona opuesta ocupada. La cuadrícula general bajo AMBAS parejas mantiene prioridad si cabe. Los bloques Kotlin Desktop/Android se cotejaron idénticos.
- Se extendió el **test real** `g3C2EquipoAndTesoroOverflowKeepIndependentPhysicalNavigationInBothVariants` [`d568a492`](https://github.com/MrSimkin/dnd_custom_aid/commit/d568a492e6f559cf9b0e001b9aa259212384c88e), sin crear fixture duplicada: genera PDF A/B originales C206, exige cartela NOTAS físicamente en la banda inferior derecha de la segunda página de Equipo, ausencia a la izquierda y **línea vectorial PDF real**, preservando sus aserciones previas de IDs, cantidades, orden y vínculos independientes con P2 y Tesoro.

## Pruebas y salida reales del mismo candidato

- [Scaffold #39326088504](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39326088504) **SUCCESS**, jobs Kotlin, backend y hosted-database; generó [70 PDF auténticos Desktop #12222611889](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39326088504/artifacts/12222611889), no simulaciones.
- Comparación PyMuPDF lote previo→nuevo, **70/70 documentos comunes**, **0 faltantes, 0 nuevos, 0 cambios de número de páginas y 0 errores**. **68/70** sin ningún cambio de palabras/cajas (redondeo 0.1 pt) ni número de trazos físicos en página alguna.
- Los **únicos dos** PDF distintos son C206 A/B, solamente **página 6**, con **+1 palabra impresa `NOTAS`** en x≈321, y≈359 (top-left PDF), **0 palabras antiguas eliminadas o alteradas**, y **+41 trayectorias vectoriales PDF** en cada página; conservan 10/10 folios. Ninguna otra página C2, P1/P2/P3/P4, C-N3, Libro o final alterada por este lote.
- Inspección raster directa del PDF nuevo A p6: cuadro `NOTAS` y cuadrícula física completa ocupan el hueco inferior derecho; los pares reales siguen a la izquierda, los renglones de Equipo a la derecha terminan antes de la cuadrícula, sin colisión visible. B p6 presenta las mismas medidas y texto.
- **Alcance demostrable:** **PASS LOCAL C207/C208 solamente para el escenario C206 de tabla de pares asimétrica Desktop A/B**. No promocionar IDs `C207`/`C208` en la matriz G4 hasta cotejo global mismo candidato, golden, 48 secciones históricas, prueba Android instalada y revisión independiente requerida.

## Gates sin falsas aprobaciones

**G0 PARCIAL** QA histórico; **G1 PASS** semántico previo; **G2/G3 PARCIALES** con C-N3, P3 historia y ahora C2 tabla asimétrica **PASS LOCAL** en pruebas Desktop A/B; **G4 BLOCKED**, **105/105 NOT_VERIFIED**, `active_candidate=null`; **G5/G6 pendientes**, RCR-1 0/2. PDF Android en dispositivo y aceptación técnica/visual del propietario NO obtenidos. Mantener M50800, D-0076, ZIP golden, matriz y registros QA intactos. PR #173 DRAFT, NO MERGE.

## Continuación exacta

- No repetir las rutas C2 ya demostradas (compactación Equipo/Tesoro, Equipo+Especial y cuadrícula asimétrica) sin defecto observado. Mantener el patrón: **PDF real anterior → defecto físico concreto → cambio acotado Android+Desktop → test A/B → Scaffold/artefactos → comparación y evidencia**.
- Próximo trabajo: revisar **C206** referencias de ida/vuelta y orden de módulos restantes en las C2 reales, y conciliar las reglas G4 P3/C2 con los ocho golden **sin** cambiar el contrato ni asumir QA propietaria. Si no hay defecto funcional nuevo, priorizar preparación de candidato Android y demostración G4, no una cadena de mejoras cosméticas.
