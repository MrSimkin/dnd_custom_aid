# C229 — Métricas por fuente existentes con referencia global ausente (P1/P4)

**Fecha Chile:** 2026-10-22. **Rama/PR autorizada:** `docs/pc-sheet-v11-approved-implementation-handoff` / #173 DRAFT **NO MERGE**. **C229 PASS LOCAL FÍSICO VERIFICADO; NO G4 NI OWNER**.

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

## Cierre C229: PASS LOCAL físico, sin promoción de G4

**SHA funcional exacto:** `de5df41c9d5538282701084c16a800ed52d7344e`. [Scaffold #39883400413](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39883400413) y [QA #39883400285](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39883400285) **SUCCESS** (Kotlin Shared/Desktop/Android, backend/DB, exportador real Ilyra A/B y auditor independiente). No hubo reparación de C229 ni se consumió presupuesto anti-loop.

[Nuevo lote de PDF reales #12471702417](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39883400413/artifacts/12471702417): **94 PDF / 859 páginas**, ZIP SHA256 `51aecd7968af871e3bbe439ed04ffa1fa49fdcf9df0e5d7f8163b610f2b9011f`. Lote [C228 #12462716343](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39861906118/artifacts/12462716343): **92 PDF / 843 páginas**, ZIP SHA256 `3b10ff123010bf60e7cf1e19b791bd57fa42287ef04ea1a965576e4285506245`. Comparador independiente PyMuPDF: los 92/92 PDF anteriores retienen idéntica paginación, **843/843** páginas con iguales palabras y coordenadas, **843/843** dibujos vectoriales y **843/843** referencias de imágenes. Cero cambios inesperados. No se reclama igualdad binaria del PDF ni aprobación de golden completo.

Dos casos adicionales del exportador de aplicación Desktop, Ilyra de backup original salvo dos globals nulos: `V11-INTERNAL-PROOF-P1-P4-Ilyra-source-metrics-without-overall-A.pdf` SHA256 `c094906db9f215dc87cfe84e49d0eab302faab6662dc550f12da603adda625e0`; B SHA256 `ffa60651d090c01b674d88672c262d3b59e0c9f36847e07ce783a2d8c2339060`. Cada PDF ocho páginas Carta, **INT** permanece verdadero, P1 y P4 muestran dos «SEGÚN FUENTE» para CD/ataque, sin referenciar cifras globales ausentes, y la última hoja es NOTAS. Recortes físicos raster P1/P4 del A verificados legibles, sin corte visible.

[Auditor físico #12472370115](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39883400413/artifacts/12472370115), ZIP `8ae36051960b188c02847331954cb17d779619225b2f344aa1bbd1a10ad4dbea`: 94 PDF / 859p, cero errores de perímetro y topología, 94/94 última NOTAS, 0 omisiones de C207 entre 458 regiones C2 / 251p, sin fallos del medidor P4. [Acta JSON verificable](../pc-sheet-v11/qa/evidence/2026-10-22_V11_C229_MISSING_GLOBAL_SOURCE_METRICS_REAL_PDFS.json).

**Límites:** PASS **solo** C229 local; no G4, no equivalencia completa 8 golden/D-0076, ni QA APK instalado, ni owner acceptance. G0 PARCIAL / G1 PASS semántico histórico / G2-G3 PARCIALES / G4 BLOCKED **105/105 NOT_VERIFIED active_candidate=null** / G5-G6 pendientes, C211 BLOCKED 3/3 normal y 0/2 estructural. Matriz, golden, QA histórica y M50800-01..32 + V11-QA-001/002 intactos. PR #173 DRAFT NO MERGE.

**Siguiente trabajo:** buscar una brecha funcional física demostrable independiente. Si no existe evidencia de un defecto material, no abrir fixes especulativos: consolidar evidencia objetiva contra reglas individuales de G2/G3 y paquete de reconciliación G4, sin falsos PASS.
