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

## Evidencia final física C221: **PASS LOCAL** de veracidad de referencia, NO P410/G4

**Producto exacto** [`cf7fe80`](https://github.com/MrSimkin/dnd_custom_aid/commit/cf7fe80c41bc8458de573dd85a1672094500a549), con fixture de preparado explícito [`9b5dbc9`](https://github.com/MrSimkin/dnd_custom_aid/commit/9b5dbc9dff13cefe7a44d7945fecc8fb29085114) y corrección de un nombre de prueba que no podía suponerse contiguo tras wrapping [`cd7607a`](https://github.com/MrSimkin/dnd_custom_aid/commit/cd7607abfd51a42d21ac3b45cea5d5f5960db218). [Scaffold #39707416839](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39707416839) **SUCCESS** (Android/Kotlin, test Shared, PDF App Desktop y auditores anteriores), [QA #39707416974](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39707416974) **SUCCESS**. El primer [Scaffold #39706616217](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39706616217) fue FAILURE **en una sola aserción del fixture PDF** de texto de fuente de cuatro palabras que podía envolver, no compilación ni dibujo. El test final usa un token de origen único y verifica su presencia y el estado no preparado por separado, sin debilitar el detector de conflictos de Shared.

**PDF auténticos antes:** [C220, 78 PDF #12393483195](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39702383965/artifacts/12393483195), ZIP SHA-256 `7056283e34ee15d0023c7fe93f67cd080f20e4503d186796138a0b55a2adaf45`. **Después:** [C221, 80 PDF #12396081016](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39707416839/artifacts/12396081016), ZIP SHA-256 `ec3736c33aad7f96e0964f8c5a6631bbfb63f68892a540ab59d997009db0f9c1`.

Contraste físico independiente PyMuPDF:
- **Los 78 PDF previos siguen con 731/731 páginas de la misma estructura y 731/731 páginas con vectores geométricamente idénticos**; 74/78 documentos idénticos también en palabra/posición.
- En **solo cuatro páginas P4**, dos A/B de fuentes homónimas y dos A/B de nombre con saltos CR/LF/TAB, cambian exclusivamente los glifos de los campos en la referencia mágica superior al detectar discrepancias reales entre perfiles, incluso `null` frente a conocido. **727/731 páginas** mantienen palabras y coordenadas idénticas, **0 cambios en el cuerpo P4 por debajo de las tarjetas**, **0 cambios en otras páginas, 0 cambios vectoriales**.
- Se suman **dos PDF nuevos** del Desktop App de Ilyra con conjuro auténtico y un nuevo origen con UUID y **sin perfil**: A y B, ocho páginas cada uno, P4 página física 6. Ambos muestran los **tres** marcadores `SEGÚN FUENTE` en las tarjetas de aptitud, CD y ataque; ambos incluyen el token fuente `QAORIGENSINPERFIL` una sola vez y la asociación real con `[X] Mago / [ ] QAORIGENSINPERFIL` en su fila medida. La fuente conocida no pierde su preparación; la fuente desconocida no adquiere una marca inventada. Se inspeccionó raster ampliado de las tres tarjetas: nada tapa o desborda labels.
- [Acta JSON del SHA exacto](../pc-sheet-v11/qa/evidence/2026-10-21_V11_C221_P4_SOURCE_CONFLICT_REAL_APP_PDFS.json) conserva la comparación, artefactos y límites.

**Veredicto:** **C221 PASS LOCAL físico para eliminar afirmaciones universales falsas en referencias P4 con fuentes contradictorias**. No significa que P4 ya muestre el desglose numérico de cada fuente ni que una casilla de preparación multinivel esté diseñada; estos detalles siguen pendientes de acuerdo con D-0076. No promover regla P406, P410, G4 o G5/G6 sin equivalencia y verificación independiente.

**Gates históricos:** G0 PARCIAL, G1 PASS semántico anterior, G2/G3 PARCIALES, G4 BLOCKED **105/105 NOT_VERIFIED, active_candidate=null**, G5/G6 pendientes. C211 BLOCKED (3/3 normal, 0/2 RCR-1 estructural); C212–C220 PASS locales previos sin reabrir. Android instalado, aprobación técnica/visual del propietario y merge PR #173 no concedidos.

**Siguiente trabajo previsto:** elegir otro incumplimiento físico *ya aprobado* en módulos distintos, o producir una propuesta **para decisión del propietario** sobre tarjetas per-source y casillas no preparables, sin implantar arbitrariamente ese diseño. Solo reconciliar G2/G3/G4 por regla cuando el mismo candidato y el golden puedan demostrarla.
