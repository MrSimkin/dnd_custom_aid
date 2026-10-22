# C228 — Aptitud de fuente mágica personalizada real en P1 y P4

**Fecha Chile:** 2026-10-22. **PR #173 DRAFT / NO MERGE**. Estado al publicar: **IN_PROGRESS: CI y cotejo físico C228 PENDIENTES**.

## Hallazgo en salida auténtica, sin rediseño

La [matriz V11](../pc-sheet-v11/qa/MATRIZ_CONTRATO_V11_PRE_QA.json) y el contrato aprobado de P1/P4 exigen referencias mágicas veraces por fuente. El PDF **auténtico de la aplicación** del backup original `03_mara_siete_umbrales_custom_extended.json`, `V11-INTERNAL-PROOF-Mara-A.pdf` y variante B de [C227 #12447073887](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39828557324/artifacts/12447073887), exhibe la casilla **APTITUD vacía** en P1 y P4. Sin embargo el backup tiene `character.spellcastingAbility=OTHER`, `successorState.spellcastingProfiles[].ability.customAttributeId=7a300000-0000-4000-8000-000000000502` y un source real de conjuro `7a300000-0000-4000-8000-000000000100`. El atributo no es universal ni equivalente a un built-in; sin la fuente, no es legítimo fabricar un nombre.

C224 compara contradicciones entre global built-in y fuentes; C225 reutiliza el criterio P1/P4. Su condición C224 deliberadamente no compara global `NONE/OTHER`, pero tampoco comunica la **presencia demostrada** de un atributo válido fuente-only. Por eso al renderizar global NONE/OTHER la referencia queda en blanco aunque el origen tenga aptitud persistida, un fallo informativo específico, no motivo para reabrir el diseño de tarjetas.

## Cambio funcional acotado

- Shared `PcSheetV11SpellSourceLabels.unmappedOverallAbilityFromRealSource`: si el global es OTHER o NONE y **alguna fuente de conjuro realmente asociada por UUID tiene aptitud explícitamente configurada**, agrega solo la advertencia `APTITUD`; si no hay conjuros, si los orígenes no tienen aptitud, o si hay un atributo global built-in, devuelve vacío y conserva comportamientos C221/C223/C224/C225.
- `contradictoryOverallFields` concatena este criterio al conjunto de discrepancias anteriores. Los dos renderizadores **Android y Desktop ya lo consumen tanto en P1 como en P4**; no modificar layout, geometría, posiciones, CD, ataque, ni ningún exportador legacy.
- Shared unit: global OTHER y NONE con source custom genuino, sin asociaciones, fuentes sin configurar y protección built-in previa. Desktop integración: importa el **backup original Mara sin modificaciones**, exige la aptitud personalizada en un origen realmente asociado, PDF A/B del servicio App, ambas franjas P1/P4 con `SEGÚN FUENTE` aptitud, preserva `+8` no afectado y última NOTAS.
- Verificación proporcionada: CI exacto Scaffold (Kotlin/Android/Desktop) + QA. Recuperar **los PDF del commit C228** y compararlos contra [90 PDF/825p C227](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39828557324/artifacts/12447073887) con hash/paginación/texto-coordenadas/vectores/imágenes. Diferencias admitidas: solo aparición del rótulo antes ausente en los dos campos P1/P4 cuando el perfil de fuente realmente lo justifica, más dos nuevos PDF Mara A/B de prueba; no modificar otras páginas. Revisar crop raster físico de ambas franjas y errores del auditor. **NO PASS hasta CI y bytes verificados**.

## Retenciones y siguiente paso

Rama anterior `45c1b476508bab59edad18b4c4eb094ca0d6a1e0`, main `0c009f10d5e66880d68fa3732491c6fc3e172f22`. C227 PASS LOCAL físico, **no** global. G0 PARCIAL; G1 semántico PASS histórico, G2/G3 PARCIALES; G4 BLOCKED **105/105 NOT_VERIFIED** `active_candidate=null`; G5/G6 pendientes; C211 BLOCKED 3/3 normales y 0/2 estructural. Se preservan las 32 observaciones M50800 y V11-QA-001/002, golden, matriz y D-0076 sin modificar. No hay aprobación técnica/visual owner, ni QA Android instalado. No fusionar la PR.

**Siguiente acción:** confirmar prueba de PDF real y registrar C228 PASS LOCAL o incidencia precisa con presupuesto RCR-1. Posteriormente seleccionar otra brecha funcional fuente-contrastada independiente o conciliación de reglas pendientes de G2/G3, sin reabrir 7+ casillas mágicas por su diseño no aprobado.
