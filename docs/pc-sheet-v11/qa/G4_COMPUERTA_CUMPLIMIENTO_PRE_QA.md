# G4 — Compuerta obligatoria de cumplimiento V11 ANTES del QA del propietario

**Autorización:** 2026-10-16. **PR:** #173, única rama de implementación `docs/pc-sheet-v11-approved-implementation-handoff`; no fusionar. **Estado inicial:** BLOCKED / NOT_READY (las pruebas estructurales de CI NO equivalen a PASS de G4).

## Qué falta para pasar a G5

El propietario exige que **todas las reglas de exportación PDF aprobadas** estén implementadas, probadas y comprobadas sobre **PDF reales de la App** antes de hacer el QA manual del candidato instalado. Una suite Kotlin verde, una lista de pruebas propuestas o el golden visual aprobado **no son prueba de cumplimiento del renderer**.

La fuente principal es `../CONTRATO_VISUAL_APROBADO.md`; suplementan solamente decisiones compatibles de `docs/decisions/D-0076_PC_SHEET_PDF_REDESIGN_DECISION_LEDGER.md` §§3.1–3.48, `docs/decisions/D-0059_RASGOS_DOMAIN.md`, fuentes G1 y aclaraciones expresas posteriores (Rasgos 2026-10-16). **Si fuentes históricas y V11 se contradicen**, la decisión posterior específica V11 prevalece; registrar conflicto y justificación antes de declarar G4 PASS. Ejemplo obligatorio: **D-0076 autorizaba Rasgos mecánicos en P3; V11 aprobado establece P3 solo narrativa.** Prohibido resucitar Rasgos mecánicos en P3.

## Inventario y estados

La **matriz inicial de 104 reglas** está en `MATRIZ_CONTRATO_V11_PRE_QA.json` (IDs estables GLB, IN, P1, RSG, P2, P3, P4, C2, CN3, BK, FIN, QA).

**104 no es una promesa de exhaustividad:** antes de cualquier G4 PASS, realizar revisión documental INDEPENDIENTE de **todas las §§3.1–3.48** de D-0076 contra V11 y contra los ocho PDFs del ZIP golden. Cualquier decisión vigente no representada necesita un nuevo ID con fuente, caso y método; nunca borrar IDs para pasar. Especificar la precedencia de reglas modificadas; no añadir como obligación una propuesta vieja claramente descartada. Poner `source_reconciliation.legacy_48_sections`, `golden_eight_pdfs`, `conflicts` en `COMPLETE` **solo** con acta/evidencia real y coincidencia con el candidato.

Cada regla tiene:
- **ID, obligación y autoridad**, incluyendo correcciones posteriores de propietario;
- **casos** (vacío, poblado, A/B, largo, Android/Desktop, límites, cuando proceda);
- **método de comprobación** distinto de la implementación (texto fuente↔PDF, ID↔emisión, PDFBox externo/PyMuPDF o medición visual raster);
- **resultado:** `NOT_VERIFIED`, `FAIL`, `BLOCKED`, `PASS`;
- **evidencia para PASS:** hashes PDF y APK, SHA commit exacto, fixture, página, artefacto, referencia de revisión independiente, nombre/verificador y comparación/resultado observado sobre salida **real de Guardar/Compartir**.

**No existe N/A automático ni aprobación parcial:** en reglas condicionales se prueban ambos escenarios (p. ej., con y sin conjuros), en vez de omitirla cuando el fixture actual no la ejerce. Un PASS que utilice un PDF o APK viejo **caduca** si cambia materialmente el renderer/datos/built assets; revisar y regenerar pruebas. No poner `genuine_runtime=true` o `independent_inspection=true` sin evidencia comprobable. Una estructura JSON válida nunca reemplaza la inspección física.

## Verificación durante G1, G2 y G3 (sin esperar a G4)

A solicitud del propietario, las obligaciones **no se almacenan únicamente para el control final**. Cada regla en `MATRIZ_CONTRATO_V11_PRE_QA.json` contiene una lista `stage_checks` de los gates del plan donde hay que ejercerla. Cada check registra **etapa, objetivo, test real, estado y evidencia**. Se evita inventar estados PASS: por defecto `NOT_VERIFIED`; el checker exige una ruta de prueba existente y rechaza una marca PASS si no hay nombre de prueba, resultado, run y commit.

- **G1 / Datos:** comprobar fuente real, nombre/tipo/origen, ausencia de campos inventados, IDs y orden. Para Rasgos, `PcSheetV11TraitSummaryTest.kt` prueba la gramática de `Pericia - Rasgo de Clase: Pícaro`, otras categorías, ausencia de fuente e invariancia ante cambio de descripción/notas.
- **G2 / Composición:** comprobar que la identidad concisa fluye sin fragmentos omitidos, sin descripción, que P1 consume los primeros IDs medidos, P2 solo los siguientes y C2 solo excedentes. La prueba nativa Desktop de 48 entradas A/B revisa el resultado de composición real; el shared `PcSheetV11FlowSourceMapper` aplica el mismo formato en todos los consumidores.
- **G3 / PDF efectivo:** la misma prueba abre PDFBox real desde el servicio Desktop, busca cada identidad una sola vez y rechaza las descripciones y notas centinela; el script `scripts/test_v11_rasgos_render_contract.py` compara estructura y formato de Android y Desktop e impide desvíos de renderer. **Esta prueba NO sustituye la auditoría Android instalado ni el oráculo externo G4.**
- **G4 / Auditoría independiente:** contrastar todos los `stage_checks` previos con la salida del candidato exacto, además de los originales D-0076, golden, legado y reglas específicas de G4. Una compilación verde de G1–G3 **no equivale a conformidad G4**.

Los comandos de verificación de etapa son `python3 scripts/check_v11_pre_qa_gate.py --mode stage --stage G1`, `--stage G2`, `--stage G3`; **fallan deliberadamente** mientras haya reglas de esa etapa aún no verificadas. Durante desarrollo el CI ejecuta `--mode inventory` para impedir borrar pruebas/IDs, más los tests propios del código tocado y la suite de regresión. El comando `--mode release` requiere que **todos** los stage_checks también sean PASS del candidato vigente. Los tests pendientes siguen visibles: no ocultarlos ni convertir la matriz en mera lista de deseos.

## Defecto bloqueante de Rasgos, ya identificado

Fuentes: D-0076 §3.9 **prohíbe** toda descripción de Rasgos en P1/P2/C2; D-0059 define la ficha digital `Nombre`, `Fuente`, `Tipo`, `Descripción`, `Notas`; aclaración actual del propietario propone `Pericia - Rasgo de Clase: Pícaro` para identificar nombre + categoría + fuente reales.

**Código actual detectado antes de corregir:** Desktop `DesktopPcSheetV11PdfProofRenderer.kt` construye la línea P2 con `trait.name + " (" + trait.source + "): " + trait.description + notes` y el resumen P1 imprime solo `trait.name`. Requiere **reparación productiva en los DOS renderers Kotlin V11**, mapper si necesario, prueba negativa con descripción+notas centinela, comprobación real de PDFs y recuento por ID. Nunca truncar palabras para ocultar la descripción: simplemente NO exportar esos campos en el bloque de Rasgos. En P2 **Equipo Especial sí conserva su propia descripción**, P3 **Rasgos de personalidad son narrativos** y no pertenecen a esta prohibición.

Se registran `RSG01`–`RSG03` en `FAIL` (diagnóstico de código, NO prueba física de candidato), demás reglas `NOT_VERIFIED`; ausencia de prueba no es PASS. Estas observaciones de cumplimiento previo **no sustituyen** ni cambian los IDs originales del registro QA owner; seguir su protocolo si se reportan nuevos defectos en un QA posterior.

## Cómo se ejecuta la compuerta

1. Durante el desarrollo y en CI: `python3 scripts/check_v11_pre_qa_gate.py --repo-root . --mode inventory`. Solo detecta estructura, IDs ausentes/duplicados y PASS fingidos sin campos de evidencia. Un éxito aquí significa **inventario válido**, no G4 aprobado.
2. Para auditar el candidato G4 antes de ofrecer un APK al propietario: generar desde los flujos de la App PDFs de **ambas variantes A/B** y casos vacío, Mara, Ilyra, Aldren, stress; auditar fuente→código→prueba→PDF/página, incluidos los cuatro exportadores legacy y golden aprobado. Registrar observaciones independientes y SHA exactos, no inferir hechos a partir de CI.
3. Actualizar cada ID a `PASS` únicamente con evidencia verificable y actualizar `active_candidate` en matriz y QA registry para **el mismo candidato**. Conciliar por completo fuentes antiguas, V11 y golden. Solo entonces ejecutar `python3 scripts/check_v11_pre_qa_gate.py --repo-root . --mode release`.
4. El validador tradicional `python3 scripts/check_v11_qa_registry.py --repo-root . --gate owner_qa` llama además al gate de contrato **con `require_pass=true`**. No puede publicar `READY_FOR_OWNER_QA` o `READY_FOR_MERGE` si queda un solo ID pendiente/FAIL/BLOCKED, falta inspección independiente, fuentes sin reconciliar o SHA de otro candidato.
5. Solo después del PASS del gate y la regresión completa G4 se prepara la **entrega G5 de QA manual al propietario**. Hasta entonces **STOP técnico**, no solicitarle que pruebe un APK para encontrar los fallos.

## Límites para evitar un falso cierre

No reescribir históricos D-0076, golden, matriz M50800 ni observaciones originales. No modificar `main`, PR ni familia legada para hacer verde el gate. No inventar revisiones independientes/hashes, no marcar reglas PASS solo porque se implementó código, no promover sin binario Android con trazabilidad exacta. Mantener RCR-1 intacto. **G4 y G5 siguen sin PASS al publicar este documento.**
