# V11 — G1: contrato semántico compartido y pruebas reales (2026-10-11)

**Rama única:** `docs/pc-sheet-v11-approved-implementation-handoff`, PR #173 (DRAFT, sin merge). **Estado:** G1 semantic-only desarrollado bajo excepción del propietario; **no considerar PASS hasta el cierre verde del Kotlin CI del commit `38fbb0066e9054982ce87fa9dcb799a7693abfee`**. G0 sigue PARCIAL **solo en contraste de PDFs históricos del propietario** y aceptación final; el golden ZIP V11 ya está preservado y verificado.

## Decisión de alcance

La ausencia de bytes de los PDF de Mara generados en el APK antiguo **no impide** construir y probar el mapper de datos nuevo e independiente V11. Tampoco prueba que los defectos antiguos estén resueltos. Esta excepción **no** permite cambiar ninguno de los cuatro renderers anteriores, presentar un candidato QA o marcar observaciones históricas FIXED sin PDF real.

**Producto implementado en G1:** `PcSheetV11SemanticContract.kt` y `PcSheetV11SemanticExtensions.kt` en `shared/src/commonMain`. Solicitud `PcSheetV11ExportRequest(variant A/B, stateSelection, portraitFitMode, includeSpellDescriptions)` separada de los cuatro `PcSheetVisualFamily` legacy. `PcSheetV11SemanticMapper.map(request,sources)` usa selección de agregado, saneamiento solo de proyección de texto, notices, retrato, atributos personalizados y fuente de conjuros del planner compartido, pero **descarta su plan visual Classic**. Esta dependencia semántica compartida no convierte V11 en el quinto renderer legado.

## Matriz de correspondencia campo real → V11 → prueba

| Origen y significado real | Proyección Kotlin G1 | Destino visual V11 futuro | Evidencia de test |
|---|---|---|---|
| `PcSheetExportSources.permanent/currentSnapshot?`; `sheet.id,campaignId` | `selectedState`, `source`, `characterId/campaignId`, notices `CURRENT_SNAPSHOT_UNAVAILABLE` | Toda página, exportación permanente/actual | `PcSheetV11SemanticContractTest.permanentCurrentAndMissingCurrent...`; identidad contra los tres backups |
| 6 `CharacterAbility`, salvación y habilidades estándar | `attributes`, `skills` por keys originales; score/mod/total calculado sin alterar origen | P1; orden y sufijos dependen de G2 | test A/B y 6 built-in |
| `successor.customAttributes[id]`, `closure.customSkills[id]`, `customSkillAbilities.customSkillId/customAttributeId` | `attributes.reference`, `skills.ability`, `skills.total?` con referencia desconocida = null | P1 y C2; no unir por nombre | test custom con target real, target faltante, IDs Mara |
| `sheet.classes`, `combatEntries`, `traits`; `successor` amplía perfiles de combate | `source`, `combatEntries`, `traits`, `extensionData().classes`, ledger UUID | P1/P2/C2; jamás Rasgos mecánicos en P3 | fixture audit 3 personajes; fuente íntegra |
| `inventoryItems[id,special,quantity,location,description,notes,equipped,attuned]` | `ordinaryEquipment` / `specialEquipment`, `active=null` real; metadatos conservados | Equipo y Equipo Especial ancho completo P2/C2, casilla papel sin inferencia | test especial equipada/sintonizada, fixture Mara y ledger |
| `currencies[key,amount]` incluso 0 | `currencies` sin filtrar valor cero | Tesoro P2/C2 | test `amount=0`, comparación por fixture |
| `background`, `generalNotes`, `noteCards[id,title,content]` | `background`, `digitalNotes` (general sin UUID, notas tituladas con ID) | P3 narrativa vs C-N3 notas digitales; NOTAS manuscritas C2/última hoja pertenecen a G2 | test contenido literal y notas tituladas; fixture IDs |
| `spellSlots[level,totalSlots,spentSlots]`, `spellcastingSources[id]`, `spells[id,sourceAssociations(sourceId,prepared)]` | `spellSlots` separados, `spellcastingSources`, `spells` como entrada única con estado **por fuente** | P1 siempre reserva Lanzamiento; P4 si y solo si spells no vacía; Libro opt-in | test fuentes opuestas y 3/2 slots; inventario exacto de fuentes/asociaciones Ilyra/Mara |
| `portraitRef`, refs disponibles y Fit/Crop | `portrait`, notice de ausencia sin bloquear | P1; silueta/área de retrato a cargo G3 | tests sin archivo local y FIT |
| `resources`, `classOptions`, `forms`, `companions`, `customMarkers`, proficiencies y weapon masteries | `extensionData()` tipado con IDs y orden estable | C2 / extensiones según contenido verdadero | test con backups reales y fuente-ID ledger |

## Ledger G1, no manifiesto físico

`PcSheetV11SemanticData.inputRecords()` enumera identidades UUID disponibles para el compositor sin inventar identificadores para monedas (clave texto), notas generales, habilidades/atributos built-in. **No es un recuento de apariciones impresas.** Un mismo conjuro tiene **una identidad semántica** incluso si el usuario solicita Libro, y el compositor G2 deberá contar apariciones físicas permitidas, con localizaciones y continuaciones, para detectar pérdida o duplicación. El ledger conserva registros repetidos si el origen los trae; **no elimina silenciosamente duplicados**.

## Cobertura sobre datos verdaderos

- Unit tests `shared/src/commonTest/.../PcSheetV11SemanticContractTest.kt`: A/B vs cuatro estilos unchanged, CURRENT válido/fallback, attributes+skills custom, conjuros prepared por fuente, P4 dependiente de conjuros reales, Libro opcional, gasto distinto del total, moneda cero, notas y retrato ausente, ACTIVO no inferido.
- Test JVM `shared/src/desktopTest/.../PcSheetQaCharacterFixturesTest.kt`: **Aldren, Ilyra y Mara** importados desde `qa/pc-sheet/fixtures/*.json` ya versionados. Por cada A/B compara IDs disponibles por tipo, hechizos/preparación por fuente, descripción sin pérdida, slots, divisas, background e identidad. No se fabricó un fixture nuevo para sustituir a Mara ni se inspeccionó un PDF del propietario.
- CI disponible: `Scaffold checks` construye y ejecuta `:shared:desktopTest :androidApp:assembleDebug :desktopApp:build`, más defensas de rutas y artefactos. El ledger de QA de V11 corre por workflow independiente.
- Los tests G1 **no demuestran** geometría, fuentes, páginas reales, render Android/Desktop V11 ni resolución de hallazgos M50800.

## Condiciones para cierre

Marcar **G1 SEMÁNTICA PASS** solo si todos sus tests de fixture y sintéticos/Scaffold están verdes en el HEAD exacto, el ledger QA sigue íntegro y no hay campo real material perdido. Marcar **G0 QA histórico PARCIAL** en paralelo; ningún PASS de G1 implica G0 QA, G2, G3, G4, G5 o aprobación del propietario.

**Siguiente gate cuando corresponda:** G2 compositor/layout determinista en shared Kotlin, sin alterar familias legacy; empezar por manifiesto de registros de entrada y partición que conserve integridad/IDs, luego medición tipográfica real, columnas y continuaciones en V11. El contraste visual con los PDF del antiguo APK de Mara se reserva para G4–G5 contra candidatos exactos. **No fusionar PR #173.**
