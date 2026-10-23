# C229 — Métricas por fuente existentes con referencia global ausente (P1/P4)

**Fecha Chile:** 2026-10-22. **Rama/PR autorizada:** `docs/pc-sheet-v11-approved-implementation-handoff` / #173 DRAFT **NO MERGE**. **Estado inicial: IMPLEMENTADO, CI/PDF C229 PENDIENTES, SIN PASS**.

## Caso real, contrato y delimitación

El contrato congelado de V11 y D-0076 §§3.24–3.26 requieren **tres** tarjetas mágicas P1/P4 basadas en fuentes reales. Las correcciones C221/C223/C224/C225/C228 evitan atribución numérica universal falsa, pero el código Shared `globalReferenceDisagreements` solo compara una fuente real **si existe un número global**. Cuando el número global es NULL, la tarjeta de CD o ATAQUE se deja vacía aun si el origen genuino puede calcular esa métrica.

La fuente de prueba es el backup original `qa/pc-sheet/fixtures/02_ilyra_quill_srd5_2_1_evoker_wizard.json`: INT configurado, CD global 15 y ataque global +7, perfil persistido con origen UUID real y valores calculables desde atributo/proficiencia. El caso se deriva cambiando **únicamente** dos referencias globales opcionales del backup a NULL, sin alterar conjuros, IDs, perfiles, números fuente ni el renderer. El código precedente dibuja blanco (`source.spellSaveDc?.toString() ?: ""` y equivalente de ataque), mientras `PcSheetPdfExportPlanner.spellbookPlan` obtiene CD/ataque específicos del perfil persistido. Es un caso de verdad de fuentes, no de rediseño.

## Bloque funcional acotado

- Shared `PcSheetV11SpellSourceLabels.missingOverallMetricsFromRealSource`: si una referencia global CD/ATAQUE falta, pero una fuente realmente asociada al conjuro posee ese número, marca exclusivamente el campo correspondiente `CD SALVACIÓN` o `ATAQUE MÁGICO`. Si no hay conjuros/asociaciones, si el origen no conoce ese número, o si el global es conocido, no inventa una discrepancia. Se suma a `contradictoryOverallFields`; C221–C228 no cambian.
- Android/Desktop V11 **ya utilizan la misma función Shared** para P1 y P4: la tarjeta utiliza su rótulo conservador, **«SEGÚN FUENTE»**, sin mostrar números globales inexistentes. No se modifica ningún renderer, geometría ni exportador legacy.
- Test Shared: ambos números ausentes con origen real; global conocido; sin conjuro; solo una métrica fuente presente; ninguna métrica fuente disponible; preserva aptitud INT correcta. Test Desktop del **servicio real de App A/B** con backup Ilyra y únicamente dos referencias globales nulas, manteniendo perfiles y UUID. Exige los dos rótulos fuente en P1 y P4, atributo INT aún visible y última NOTAS. Produce dos PDF reales.

## Verificación y límites

Antes de PASS LOCAL C229: [Scaffold](https://github.com/MrSimkin/dnd_custom_aid/actions) Kotlin Shared/Android/Desktop, [QA traceability](https://github.com/MrSimkin/dnd_custom_aid/actions) exacto, bytes PDF reales y auditor físico; comparar con **92 PDF/843 páginas de C228** ([lote #12462716343](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39861906118/artifacts/12462716343)) por SHA, paginación, palabras+posiciones/vectores/imágenes y dos PDF nuevos Ilyra A/B, inspección raster de P1/P4. Los previos deben permanecer sin cambios salvo diferencias de metadatos; sin esos bytes o con regresión, mantener `IN_PROGRESS`/bloqueo preciso, no PASS anticipado.

Autoridad intacta: `main` HEAD previo `0c009f10d5e66880d68fa3732491c6fc3e172f22`; rama antes C229 `69221e8377093efa28663f71bd8f4140dcad339b`. C228 PASS LOCAL demostrado, no G4. **G0 parcial / G1 PASS semántico anterior / G2 G3 parciales / G4 BLOCKED 105/105 NOT_VERIFIED active_candidate=null / G5 G6 pendientes**. C211 separado BLOCKED 3/3 normales y 0/2 estructural. Matriz, D-0076, golden, QA original, 32 M50800 y V11-QA-001/002 no se reescriben ni dan por aprobados. Android instalado y QA owner NO aprobados. #173 DRAFT, sin merge.

**Siguiente paso obligatorio:** veredicto CI y comparación física exactos C229; luego seleccionar otra brecha funcional independiente con evidencia o conciliar reglas G2/G3, sin repetir bloques PASS ni diseñar 7+ casillas sin autoridad.
