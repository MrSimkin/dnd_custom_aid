# G0 — Primer mapa semántico verificado en código real (lectura GitHub)

**Fecha:** 2026-10-09. **Rama:** `docs/pc-sheet-v11-approved-implementation-handoff`. **Evidencia:** lectura directa de `CharacterSheet.kt`, `PcSheetPdfExportFoundation.kt`, `AndroidPcSheetExportService.kt` y `DesktopPcSheetExportService.kt` en GitHub. **Estado:** hallazgos de lectura, **NO** prueba Kotlin/Android/Desktop, NO es mapeo G1 íntegro. G0 PARCIAL.

## Hallazgos de ingeniería que deben convertirse en tests antes de G1 PASS

| Origen real (campos verificados) | Regla V11 / riesgo de conversión | Decisión técnica segura | Test obligatorio |
|---|---|---|---|
| `PcSheetPdfExportRequest.visualFamily` (`PcSheetVisualFamily`, 4 valores actuales) | V11 todavía no existe como familia/variante | Añadir V11 opt-in y variante A/B de forma compatible; **no modificar** significado de 4 enum anteriores. | Solicitud legacy idéntica, nueva V11 A/B, serialización/valores soportados |
| `PcSheetExportSources(permanent,currentSnapshot?)`, planner `selectedState` y notice `CURRENT_SNAPSHOT_UNAVAILABLE` | Si Current no existe, planner **vuelve a Permanent** y avisa; no debe fingir estado actual | Reusar mismo snapshot y su aviso sin cambiar persistencia | Current presente vs ausente, identidad de PC/campaña, ausencia de mutación |
| `CharacterSheet.spells`, `spellcasterEnabled`, `spellSlots`, `spellcastingSources` | **Divergencia confirmada:** el planner LEGACY muestra base `SPELL_LIST` si hay *cualquiera* de esas capacidades; V11 solo la muestra cuando `spells.isNotEmpty()` | Condicionar P4 con regla exclusiva para V11; preservar LEGACY. P1 mantiene Lanzamiento aun sin conjuros. | No lanzador, fuentes sin conjuros, ranuras sin conjuros, primer conjuro |
| `CharacterSpellSlot(level,totalSlots,spentSlots)` | Ranuras reales distinguen total/gastado; no inventar cantidades | Solo usar valores reales con semántica de nivel; campos ausentes no equivalen a cero inventado | Slot 0/1/9; gastado 0; lista de slots vacía; fuente/nivel especial |
| `CharacterSpell(id,level,description,sourceAssociations)`, `CharacterSpellSourceAssociation(sourceId,prepared)` | Preparación es **por fuente**; no es gasto de slot. Texto de Libro potencialmente sin límite de una columna | Mantener associations con `sourceId` y prepared por pareja; texto segmentable. No aplastar fuentes en booleana global | Dos fuentes opuestas, cantrip, Libro largo, relación 1:n y conservación IDs |
| `CharacterInventoryItem(id,name,quantity,special,description,location,equipped,attuned)` | `equipped` y `attuned` NO significan `ACTIVO` | Equipo especial con casilla `ACTIVO` **desmarcada si no hay estado real**; D-0076 §3.22 autoriza papel manual. No crear booleano inferido. Equipo normal solo identidad/cantidad; especiales conservan ubicación/desc. | `equipped=true` y `attuned=true` no premarcan ACTIVO; 2 grupos, 0 cantidad |
| `CharacterCurrency(key,name,amount,sortOrder,isDefault)` | `amount=0` es un registro real, NO ausencia | Imprimir nombre y cero; sin lista conservar celdas manuales sin monedas inventadas | Amount 0, personalizado, nombre largo y lista vacía |
| `CharacterBackground(name,summary,race,religionFaith,personalityTraits,ideals,bonds,flaws,story)` | P3 narrativa; párrafos consecutivos, sin Rasgos mecánicos | Mantener todos los campos verdaderos y fragmentar textos largos | Historia >1 página, acentos, Trasfondo vacío manuscrito |
| `CharacterTrait(id,name,source,type,description,sortOrder,...)` | P1/P2/C2 muestra origen+nombre sin descripciones; no P3 | Copiar ID/origen/nombre una vez, registros desbordados a C2 | Un solo rasgo, muchos, 0 duplicados, separación P3 |
| `CharacterNote(id,title,content,sortOrder)` y `CharacterSheet.generalNotes` | Notas digitales C-N3 exclusivas; vacías no crean C-N3. Relleno `NOTAS` C2 es papel, no dato | Mantener distinción explícita entre digital y cuadrícula manuscrita | Nota general, tituladas largas, cero notas, última página intacta |
| `CharacterCombatEntry(id,name,type,attackModifier,damageEffect,...)` | Ataques tres columnas, distinguir tipos y origen real | Filtrar/clasificar por categoría real; no generar ataques desde inventario | Entradas de tipos distintos, ataques sin datos, nombres extensos |
| `PcSheetCustomAttributeProjection`, `PcSheetCustomSkillProjection(ability,total)` | Relación atributo↔habilidad y sufijo solo si el atributo no es visible | Reusar `customStatisticsProjection()` y su vínculo `abilityReference()`; el layout decide el sufijo, no el dominio | Dos atributos custom + habilidades, una habilidad en C2 separada, A/B |
| `PcSheetPortraitPlan(portraitRef,fitMode,locallyAvailable)` | Retrato opcional Crop/Fit; el recurso local puede faltar | Reusar loader de ambos services; no depender de nube, mantener notice de ausencia | Crop vs Fit, ausente, datos mismos con foto no disponible |

## Evidencia original de QA que NO puede omitirse

Abrir `docs/checkpoints/2026-09-26_PC_SHEET_MARA_PREQA8_PDF_VISUAL_QA_DEFECTS.md`, `docs/checkpoints/2026-09-28_PC_SHEET_MARA_OWNER_REQA_RUNTIME_FAIL.md` y la matriz `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`. Se confirmó que existen y se leyeron secciones de la matriz, **pero no se tiene todavía el PDF exacto de runtime / APK instalado recuperado y cotejado**. Conforme a `AGENTS.md §6.3`, no atribuir PASS de esa gate por documentación secundaria.

## Problemas de disponibilidad constatados

- ZIP golden V11 original **sí está en entorno de trabajo**: 1,520,026 bytes y SHA-256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a`; inventario ZIP accesible. **Pero no existe versión almacenada en GitHub.** Falta R-01 PASS de recuperación byte-a-byte desde Git.
- Un intento controlado de acceso Git directo desde contenedor falló por DNS: `fatal: unable to access ... Could not resolve host: github.com`. El conector GitHub sí permite operaciones de archivos de texto. No probar rutas de credenciales, ni declarar que el ZIP quedó versionado.
- Sin clon local confiable no se corrieron `Gradle`, compilación, test Android/Desktop ni extracción de PDFs de la App. **G0 PARCIAL / G1–G6 NO INICIADOS.**
- RCR-1 se encuentra **aprobado y documentado**; aún no tiene sentido usar recuperación, pues no hay tres intentos reales ni checkpoint previo PASS fallido.

## Próximo procedimiento determinista

1. Custodiar/recuperar golden ZIP en fuente durable con verificación SHA post-descarga; preservar los ocho PDFs.
2. Reabrir PDFs originales y fixture Mara de QA, hacer la matriz de observaciones `original → renderer → test → candidato`.
3. Completar mapeo de campos reales desde `CharacterClosureState`, `CharacterSuccessorState` y la UI export real. No inventar semántica ACTIVO.
4. Solo al completar G0 pasar a G1 y empezar cambios productivos; conservar PR sin fusionar y contador de intentos en cero.

**Dictamen del presente trabajo:** avance documental/arquitectónico verificable; **ningún riesgo funcional certificado como resuelto**, no declaramos G0 PASS.

## Evidencia histórica Mara recuperada posteriormente (read-only)

Se descargó desde GitHub Actions el artefacto **ID 10942589698** (`mara-phase2-existing-repair-proof.zip`), SHA-256 `6592148eb9c5254c65b6ec0cb33ffe09bf6efb8f9b97af61f3fe09797c1d6bb7`. Contiene pruebas de Worker, no el conjunto que el propietario generó desde su APK instalado.

| PDF extraído del artefacto | Páginas verificadas con `pdfinfo` | SHA-256 |
|---|---:|---|
| `mara-fantasy-stress-baseline.pdf` | 29 | `9da2d727bfd7d7a1959be7cb79d71a474eb8e0bb9bb3108c10582af02ee9977a` |
| `mara-custom-v1-stress-baseline.pdf` | 18 | `057263a509413decd09cda08b3c118299cfc08276d265a3f0af506aac23a154f` |
| `mara-custom-v2-attribute-stress-baseline.pdf` | 16 | `6d64f1860fcc29233f1eb8fb84ba51804a63fc4cef23863f773098aa12cbfe19` |
| `mara-custom-v2-ability-stress-baseline.pdf` | 15 | `23756f9096b55e9d4de9fc0fded5f44926476590bdc38b2e04df1834175bfe5a` |

El `mara-cross-family-failures.txt` del artefacto está vacío. Se renderizó/revisó la página 6 de Custom v1 de ese artefacto; el PDF incluye `Lectura de presagios` en extracción de texto. Esto demuestra que **el PDF de laboratorio existe y contiene el texto**; no demuestra que la exportación del APK del propietario funcionara. **Contradicción histórica de QA sigue abierta:** el propietario obtuvo Fantasy 45 páginas, Custom v1 error y Custom v2 28/27. No mezclar pruebas de distinto candidato ni dar PASS por artefactos de Worker.

**Disponible para futuros rescates:** artefacto GitHub Actions #10942589698 mientras su retención lo permita y los hashes citados. No guardar archivos ZIP/PDF temporales sin verificar permisos/tamaño; no inferir custodia perpetua desde Actions. Falta recuperar el PDF de runtime exacto y el APK con identidad.
