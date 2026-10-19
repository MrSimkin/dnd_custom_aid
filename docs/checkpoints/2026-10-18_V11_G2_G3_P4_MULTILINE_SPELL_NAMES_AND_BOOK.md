# V11 — P4 nombre real de conjuro multilineal y Libro opcional

**Fecha:** 2026-10-18; PR #173 DRAFT/SIN MERGE. Base `00487cfa14b9830a3998406a39ee1f3a4086867c`. Alcance **solo** V11 Kotlin Android/Desktop + test de salida real Desktop; sin modificar la maqueta, golden, familia anterior, persistencia ni la matriz de aceptación.

## Hallazgo concreto / corrección productiva

- Con el arreglo anterior ya se impidió medir *fuentes de conjuros* CR/LF/TAB dentro del renglón fijo derecho de P4. La MISMA selección todavía podía medir `fullName = "     " + entry.spell.name` con CR o LF antes de decidir si el **nombre persistido del conjuro** era apto para ese renglón. PDFBox rechaza los controles como glifos, por lo que un nombre real multilineal con fuente no vacía podía abortar la exportación.
- Corrección de ambos renderizadores exclusivamente V11: comprobación de una línea física para los **dos operandos**, nombre y fuente, ANTES de medir ancho. Si no son monolínea, se conservan enteros en el compositor medido y la página P4 de tres columnas. No se borran CRLF/TAB del origen, no se fabrican estados ni se cambia la selección de fuente/preparación por UUID.
- Test Kotlin del **servicio real Desktop** sobre backup Ilyra: un conjuro original con nombre `QAP4SPELLFIRST\r\nQAP4SPELLSECOND\tQAP4SPELLTHIRD`, variantes A/B, Libro opcional presente, preservación del UUID y relaciones de fuentes/preparación, tres fragmentos impresos exactamente una vez en P4, nombre íntegro en Libro, índice anterior a las fichas, última NOTAS y snapshot inmutado.
- El test anterior de fuentes con controles se conserva tal cual: esto cubre un **campo diferente** del mismo diseño sin volver a ejecutar una auditoría ad hoc redundante. La suite y auditores existentes son la verificación proporcional.

## Compromiso de evidencia y continuidad

**Al publicar: CI del cambio PENDIENTE.** No declarar PASS hasta verificar compilación, test Kotlin/PDF nuevo A/B y controles físicos del artefacto exacto. **No** cerrar un reporte histórico del propietario con esta prueba de laboratorio: los PDFs originales de Mara 50800 no son el candidato actual, siguen OPEN y requieren comparación por APK exacto. Este arreglo se funda en el uso comprobado de `getStringWidth` sobre un origen multilineal, no en un defecto previamente denunciado por el propietario.

**Estado global:** G0 QA histórica parcial, G1 semántico PASS, G2/G3 parciales; G4 BLOCKED con 105 reglas NOT_VERIFIED y `active_candidate=null`; G5/G6 pendientes; V11-QA-001/002 y M50800 intactos; RCR-1 0/2. No merge.

**Siguiente trabajo después de CI:** continuar con carencias funcionales de más impacto (contenido/continuaciones C2, Libro o integridad fuente→PDF), guiadas por evidencia real, en vez de crear más tests de un solo carácter. Reconciliar la matriz G4 y las ocho referencias antes del QA owner.
