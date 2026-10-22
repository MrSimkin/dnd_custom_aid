# C221 — P4 referencia mágica sin valores universales falsos al discrepar las fuentes

Fecha 2026-10-21, PR #173 DRAFT/NO MERGE, rama `docs/pc-sheet-v11-approved-implementation-handoff`, HEAD anterior `331be83130b336bf2363f56e11b7954bf95a23e0`, main remoto `0c009f10d5e66880d68fa3732491c6fc3e172f22`. CI anterior del HEAD completo Scaffold #39703342469 y QA #39703342570 SUCCESS.

## Brecha demostrable y límite de autoridad

D-0076 §3.25 obliga a **distinguir fuentes con valores diferentes y evitar CD universal ficticia** en la referencia P4. El contrato §3.24 deja **pendiente de decisión de diseño** la representación física exacta de múltiples fuentes y casillas de preparación. La App ya preserva por fuente los IDs, nombres, preparación, aptitud, CD y ataque en `PcSheetSpellbookSourceProjection`; P4 imprime asociaciones por conjuro con `[X]/[ ]` correctamente (test real A/B anterior). No reabrir ese trabajo.

Pero `drawP4MagicReference` imprimía sin condición `source.spellSaveDc`/ataque/aptitud **globales** como si fueran referencia universal, incluso cuando las fuentes asociadas a conjuros tenían otros valores o perfiles desconocidos. Una diferencia por UUID y campos `null` frente a conocido importa: no se puede convertir a igualdad inventada. Esta contradicción es un defecto semántico visible independiente de C211–C220.

## Producto C221 y pruebas

Un helper puro de Shared `PcSheetV11SpellSourceLabels.conflictingReferenceFields(entries)` agrupa proyecciones **por UUID real**, no por nombre visible, y detecta contradicción por campo `castingAbility`, `saveDc` y `spellAttackModifier`, considerando `null` distinto de un valor conocido. Una única fuente o fuentes con los mismos valores siguen usando el contenido anterior.

Los renderers Android y Desktop utilizan el mismo resultado. En la tarjeta P4 de cada campo **realmente conflictivo**, sustituyen el valor universal por el texto explícito y no numérico `SEGÚN FUENTE`, que cabe en su rectángulo existente; no imprimen un número inventado ni afirman que el dato ausente equivalga a cero. Los campos no conflictivos permanecen iguales. Los detalles físicos de referencias por fuente siguen **PENDIENTES** del propietario, en vez de inventar una nueva tabla o iconografía. Se preservan `[X]/[ ]` por fuente en las filas medidas y todo el flujo anterior. No se alteran P1, valores persistidos, UUID, paginación ni decisiones originales.

Se agregan unit tests Shared sobre UUID con nombre duplicado, CD 13 frente a 15, ataque 5 frente a `null`, y referencias uniformes sin falso positivo. Fixture Desktop real parte de backup Ilyra, agrega solo una fuente con UUID nueva **sin perfil mágico**, y vincula un conjuro real con estado de preparación falso, sin perder la fuente original: A/B PDF de App deben mostrar `SEGÚN FUENTE` y preservar ambas asociaciones. No se reclaman números per-source impresos aún; es corrección de **veracidad de la referencia**, no el diseño completo P410.

**Estado al implementar:** no PASS sin CI exacto, prueba física fuente→PDF del fixture con conflicto y comparación regresiva de los PDF reales restantes. Los módulos C211–C220 no se reabren.

**Gates inalterados:** G0 parcial; G1 semántico PASS anterior; G2/G3 parciales; G4 BLOCKED 105/105 NOT_VERIFIED y active_candidate=null; G5/G6 pendientes; C211 BLOCKED 3/3 normal, RCR-1 estructural 0/2. C212–C220 PASS locales anteriores. Android instalado y aprobación técnica/visual del propietario NO otorgados. PR #173 DRAFT NO MERGE.

**Siguiente trabajo:** resolver bajo decisión autorizada la presentación completa de estadísticas per-source y casillas no preparables, sin inferir fuentes; luego continuar otra brecha verificable P4 o G2/G3 por reglas ya sustentadas en el mismo candidato. No promover P406/P410/G4 sin evidencia independiente.
