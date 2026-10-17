# V11 G2/G3 — Ataques P1: llenar seis filas disponibles sin perder el orden ni datos C2

**Fecha:** 2026-10-17. **PR:** [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173), OPEN/DRAFT/unmerged. **Commit productivo:** [`f3c406af980473b0e9be7d6e30f8c0cf3adc54eb`](https://github.com/MrSimkin/dnd_custom_aid/commit/f3c406af980473b0e9be7d6e30f8c0cf3adc54eb). `main` `0c009f10d5e66880d68fa3732491c6fc3e172f22` inalterada. No hay aprobación técnica ni visual del propietario.

## Defecto real y corrección de producción

La selección anterior de Ataques P1 era `attackRows.take(6).filter { fits }`, es decir, filtraba por ancho **después** de limitar a las primeras seis entradas. Si las primeras cuatro tenían nombres o efectos demasiado anchos, la hoja solo imprimía dos ataques pese a tener otros seis ataques fuente posteriores perfectamente legibles. Esa selección desperdiciaba filas físicas y enviaba registros que cabían innecesariamente al flujo C2.

En los renderizadores **Desktop y Android PDFBox V11** se aplica `attackRows.filter { fits }.take(6)`: se recorren los registros con su orden fuente real y se seleccionan los seis primeros que cumplen simultáneamente los límites medidos de rótulo/rango, bonificador angosto y daño/efecto. Todos los UUID no seleccionados, por largo o por capacidad, conservan su registro fuente completo en C2 a través de `extraCombatData`. No cambian el dominio, ningún valor, el ancho de columnas, las siete filas físicas (seis ataques + encabezado), los gráficos, páginas de otros módulos ni las cuatro familias legacy. No se inventan ataques.

## Prueba auténtica discriminante

`g3SourceOrderP1AttackSelectionUsesAllSixFittingRowsWithoutLosingDeferredAttacks`, DesktopApp PDFBox, usa el backup auténtico Aldren como base de los datos del personaje y **sustituye exclusivamente sus filas de combate para crear el fixture técnico** de diez ataques con UUID únicos:
- `QALONG1…4`: primeros cuatro con nombres legítimos demasiado anchos para P1, diferidos íntegros;
- `QASHORT5…10`: seis posteriores que caben y deben ocupar P1, en orden fuente;
- notas, bonificaciones y efectos siguen vinculados a sus IDs; este fixture NO representa combates históricos del propietario.

Ejecuta A/B desde el servicio real `generateV11Proof`, guarda PDFs nativos y verifica cada nombre **exactamente una vez** (P1 o C2), sin `QASHORT` duplicado en C2, con última NOTAS manuscrita. Es un caso que habría fallado con el `take(6).filter` previo.

## Evidencia CI y examen físico del mismo código

- [Scaffold #39071100853](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39071100853) **SUCCESS**: Kotlin tests / Desktop PDF real, compilación Android, backend y hosted DB sobre el SHA productivo exacto.
- [QA traceability #39071100909](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39071100909) **SUCCESS**: control de consistencia, NO QA ni aprobación del propietario.
- [Artifact #12115157309](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39071100853/artifacts/12115157309): ZIP de **28 PDFs reales App**, SHA-256 `a356c7720edfbb61bf7a6bfef2a7cf196453978598952bdde0186571b860c8bb`, ZIP CRC PASS.
- Regresión contra los **26 PDFs anteriores** [artifact #12115230306](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39069330008/artifacts/12115230306): **26/26 físicamente equivalentes** en página/Letter, palabras y coordenadas, vectores y fuentes tipográficas. Nota: hashes binarios de PDFs no necesariamente iguales; la comparación es semántica y geométrica, **no** identidad byte a byte.
- Nuevo `V11-INTERNAL-PROOF-P1-attack-fit-order-A.pdf`, SHA-256 `ff80119a4d8253dd8be1b89b04eb798a4c6d018689b9f3861676659d2d89817c`, **7 páginas Carta 612×792pt**. P1 imprime QASHORT5–10 en seis filas consecutivas, C2 **página 4** imprime QALONG1–4 completos, ninguno duplicado; última NOTAS.
- Nuevo `V11-INTERNAL-PROOF-P1-attack-fit-order-B.pdf`, SHA-256 `3dbbb1bef29971612283f2c2b33f9d89a84308d7862ce50c7effe2afdc174f5f`, mismo resultado **7 páginas** y referencias C2 p4.
- Los PDF A/B y recortes de P1 Ataques y C2 se rasterizaron e inspeccionaron visualmente; los nombres/campos de ataques aparecen dentro de sus columnas, sin colisión observada. C2 conserva su cuadrícula manuscrita titulada NOTAS y pie `VIENE DE P1 PÁG. 1`. Última página exclusiva de anotaciones manuales.

## Interpretación y puertas restantes

**PASS local acotado G2/G3 para la selección de seis ataques y la preservación de los cuatro restantes**, con cobertura PDF verdadera Desktop A/B y compilación Android; **NO** es paridad Android instalado, aceptación estética/funcional por el propietario ni certificación del contrato global. Matriz conserva **105 reglas globales NOT_VERIFIED** y 12 checks etapa PASS históricos; `active_candidate=null`, **G4 BLOCKED**. G4 exige, antes de QA propietario, conciliación completa con 48 decisiones §§3.1–3.48 del D-0076, 8 PDF golden, cuatro familias legacy, 105 reglas/cada stage check y auditoría independiente del **mismo** candidato. **G5/G6 no iniciados** y sin merge ni alteración `main`. V11-QA-001/002 y registros históricos M50800 permanecen abiertos; RCR-1 no activado.

**Siguiente trabajo:** seguir la próxima brecha funcional de una regla aún pendiente con evidencia concreta de fuente/contrato y test discriminante (priorizar integridad de contenido, elementos de rango y rutas de continuidad); no reabrir Ataques P1 ni otros slices con PASS local demostrado. Solo cuando el candidato esté completo, ejecutar G4 integral antes de generar candidato propietario para G5.
