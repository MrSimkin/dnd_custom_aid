# Checkpoint canónico de trabajo — V11 P1 A: seis atributos + habilidades muy densas

**Fecha:** 2026-10-17. **Rama única:** `docs/pc-sheet-v11-approved-implementation-handoff`. **PR:** [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173) OPEN/DRAFT/unmerged. **Código demostrado:** [`e1681c14c722ac256243c8af50cf883af062e350`](https://github.com/MrSimkin/dnd_custom_aid/commit/e1681c14c722ac256243c8af50cf883af062e350). La autoridad de integración sigue siendo `main`, no alterada: `0c009f10d5e66880d68fa3732491c6fc3e172f22`.

## Defecto material reproducible por inspección de código

V11 variante A expandía la altura de **cada** atributo en función de hasta seis habilidades. Con seis atributos muy poblados se requerían hasta **6 × 92 = 552 pt** desde la cota PDF 577 hacia abajo y el sexto atributo terminaba en y=25; el renderer exigía explícitamente `p1AttributeTop(6) > 110f` y abortaba la exportación. La gramática aprobada obliga a que los seis atributos permanezcan en P1 y toda habilidad real sobreviviente continúe en C2. No era un problema que se resolviera encogiendo texto, suprimiendo habilidades ni moviendo atributos a otras páginas.

## Reparación Kotlin real

Nuevo **`PcSheetV11AttributeSkillBudget`** en Shared: mantiene los altos originales de las filas normales y, únicamente si los seis atributos superarían el piso físico P1 y110, reduce de forma determinista la cantidad de habilidades *impresas debajo* de los atributos más cargados. No reduce el tamaño de fuente ni la altura de las filas aceptadas. Los índices no impresos continúan con su source key y nombre en el flujo medido de C2. Renderizadores PDFBox Desktop y Android aplican la misma asignación. La variante B conserva su panel independiente sin esta reasignación. No cambia CharacterSheet, almacenamiento, servicios, backend ni las cuatro familias legacy.

Pruebas: `PcSheetV11AttributeSkillBudgetTest` (geometría previa sin cambios cuando cabe, seis filas base y reparto determinista en saturación, fallo explícito si hasta los mínimos fueran imposibles) y `g3RealSixMandatoryAttributesSurviveDenseCustomSkillsWithLosslessC2Continuation` en Desktop PDFBox, variantes A/B, desde fuente auténtica Mara con **54 habilidades añadidas exclusivamente como fixture técnico**, con UUIDs únicos y seis habilidades base conservadas. Esas habilidades de prueba **NO** se atribuyen al historial real del propietario.

## Evidencia del commit exacto

- [Scaffold Actions #39069330008](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39069330008): **SUCCESS**, Kotlin (Shared tests, Desktop PDF real y compilación Android), backend y DB. [QA traceability #39069330059](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39069330059): **SUCCESS**.
- [Artifact #12115230306](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39069330008/artifacts/12115230306): **26 PDFs nativos de la App**, ZIP SHA-256 `94aa6fb09ab1874f38e2de6e6888469e842288fb526e4b1c9c741d302544775f`, integridad CRC PASS.
- Contra los 24 PDFs de base [artifact #12114620678](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39067945274/artifacts/12114620678): **24/24 idénticos** en páginas Carta, lista de palabras+coordenadas, dibujos vectoriales y familias tipográficas. No se volvió a alterar ni rehechar un slice anterior.
- **A:** `V11-INTERNAL-PROOF-dense-six-attributes-A.pdf`, SHA-256 `7ba4ad2d267c5414ba92d8a8778d1c165bf1953379ea726e6821dc343835f2dd`, 10 páginas Carta, seis atributos fuente visibles P1, 11 habilidades nuevas en P1, el resto en C2, páginas 5 y 6.
- **B:** `V11-INTERNAL-PROOF-dense-six-attributes-B.pdf`, SHA-256 `7df4cb80285f0a567751c792777e4d3c61807174687c3f713479933c406ec501`, 10 páginas Carta, seis atributos básicos y tres personalizados en P1, habilidades nuevas en C2 páginas 5 y 6.
- **Ambas:** 54/54 nombres `QASKILL001…054` completos **una sola vez**; ningún registro añadido desapareció ni se duplicó. Cero palabras fuera del margen de seguridad comprobado (x23..590, y20..773); fuentes Barlow Condensed y Fira Sans incrustadas; última página manuscrita NOTAS sin registros de habilidades. P1 y C2 A/B examinadas visualmente a partir de los propios PDFs; sin colisiones observadas. No hay evidencia de Android instalado/paridad gráfica real por dispositivo.

## Límites inalterados y continuación

Este bloque dispone de **PASS local acotado G2/G3**. No equivale a validación técnica/visual del propietario, ni garantiza casos extremos de otros módulos, ni al 100% de la etapa. Matriz V11: **105 reglas globales NOT_VERIFIED**, 12 stage checks PASS previos, `active_candidate=null`, **G4 BLOCKED**. Mantener V11-QA-001/002, 32 M50800 históricos, 48 §§ de D-0076 y ocho golden sin cierre ficticio. **G5/G6 NO iniciados**, sin QA instalado Android ni merge. Las cuatro familias de exportación legacy no se tocaron, pero su regresión obligatoria del candidato global sigue sin auditarse. Ninguna aceptación del propietario se infiere de su orden de continuar.

**Próximo trabajo previsto:** abrir el siguiente bloque funcional no resuelto, con hallazgo concreto desde fuente real/matriz y contrato; reparar en Android/Desktop y verificar con prueba física proporcional. Tras cerrar módulos, ejecutar G4 integral con toda la matriz, D-0076 §§3.1–3.48, 8 golden y 4 familias previas en el candidato **exacto** antes de proponer G5 o QA propietario.
