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

## Resultado real del commit: PASS LOCAL exclusivamente en P4 + Libro A/B

- **Código probado:** [`262c4396bed81140d79d7a005452d998023d3fe2`](https://github.com/MrSimkin/dnd_custom_aid/commit/262c4396bed81140d79d7a005452d998023d3fe2). [Scaffold #39181706739](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39181706739) **SUCCESS**: Kotlin `shared:desktopTest`, Desktop build, Android Debug `assembleDebug`, backend y hosted-db. [QA registry #39181706741](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39181706741) **SUCCESS documental**, no QA propietario.
- **[Artefacto genuino Desktop #12156707254](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39181706739/artifacts/12156707254)**: 58 PDF, 552 páginas tamaño Carta. ZIP inspeccionado localmente, SHA-256 `9f9f1545515d772e933f592b5df8589f151b1b89b5ddc50fc6bad304d68b4bf8`. Contenido no embebido en Git.
- **Nuevos casos reales:**
  - `V11-INTERNAL-PROOF-P4-spell-name-break-A.pdf` — 12 páginas, SHA-256 `cbcdfa6df12859639afe4ae2e02db2131537873217fe2a2054040abe846f3ff3`.
  - `V11-INTERNAL-PROOF-P4-spell-name-break-B.pdf` — 12 páginas, SHA-256 `ad8004b5b57a75aaebccf955375daeba70ded6e4bc638243698dc2d5984d1b14`.
  - En **cada** PDF, PyMuPDF cuenta **exactamente 1** aparición en P4 de cada `QAP4SPELLFIRST`, `QAP4SPELLSECOND`, `QAP4SPELLTHIRD`. El Libro (primera ficha página 10, índice página 9) reproduce cada fragmento, final NOTAS manuscrito, Carta en todos los folios. Se renderizaron/inspeccionaron las páginas 4, 9, 10 del PDF A para este caso. El test Kotlin verifica además identidad de conjuro, fuentes, preparación y ausencia de mutación del backup Ilyra.
  - **No regresión acotada:** comparación independiente con los 56 PDF del candidato anterior [#12152454938](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39171821142/artifacts/12152454938). **56/56**, 528/528 páginas comunes, conservaron exactamente recuento de páginas, palabras con sus coordenadas, número de dibujos y de imágenes; solo se agregaron los dos nuevos PDF (24 páginas). No se reivindica igualdad binaria entre generaciones.
- **[Auditorías #12156971081](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39181706739/artifacts/12156971081)**: `V11_PHYSICAL_AUDIT_ERRORS=0`, doble marco `552/552`, logotipo visible `552/552`, retrato ausente `58/58`. La prueba del nuevo caso no sustituye oráculo integral de golden/QA humano.
- **Límite estricto:** No se ejecutó versión anterior *con el nuevo nombre* para demostrar una prueba roja de runtime histórico; la causa está sustentada en que la ruta antigua llamaba a medición PDFBox con CR/LF en nombre persistido. PASS LOCAL de salida actual Desktop real y compilación Android equivalente, **NO** demostración Android instalado, ni G4/G5/G6, ni QA/aceptación visual del propietario. En la imagen renderizada del índice el nombre se presenta en varias líneas; su ajuste fino de legibilidad y presentación sigue sujeto a G4, no se atribuye conformidad visual por preservar las palabras.

**Continuidad:** G0 QA histórico de Mara sigue PARCIAL; G1 semántico PASS; G2/G3 parciales. **G4 BLOCKED, 105/105 reglas globales NOT_VERIFIED y `active_candidate=null`**; G5/G6 pendientes. M50800 y V11-QA-001/002 permanecen originales, sin cambiar evidencias ni estados; RCR-1=0/2. PR #173 DRAFT y sin merge, `main` intacto.

**Siguiente trabajo recomendado:** dejar de agregar escenarios de control aislados a P4 y concentrarse en implementación/cotejo sustantivo aún pendiente de C2 y su composición/referencias, priorizando deficiencias observadas del código o PDF verdadero bajo G2/G3 y usando la matriz de 105 criterios de G4 como trazabilidad. Sin presentar a propietario un APK antes de G4.
