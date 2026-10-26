# C231 — Fuentes de conjuro rotuladas por tipo de origen en P1 y P4

Fecha 2026-10-26 (Chile). PR #173 DRAFT/NO MERGE, rama `docs/pc-sheet-v11-approved-implementation-handoff`. **Estado: IN_PROGRESS. NO PASS hasta CI exacto y comparación de PDF reales.**

## Decisión del propietario (documentada)

[Contrato visual, sección «Aclaración del propietario — fuentes de conjuro en P1 y P4 (2026-10-26)»](../pc-sheet-v11/CONTRATO_VISUAL_APROBADO.md): la fuente se rotula por **tipo de origen sin nombres específicos**, salvo la **Clase** (nombre de clase sin subclase); «SEGÚN FUENTE» significa `Mago 15 / Hechicero 17` (aptitud, CD y ataque, en P1 y P4); dos fuentes del mismo tipo pueden verse iguales; casillas de espacios por nivel dependen de la hoja del PJ, mínimo 0 y máximo 4. Regla nueva `P411` en la matriz. La lista de tipos y el rótulo `Otro` para una fuente inexistente son **supuestos del Worker** a confirmar (declarados en el contrato).

Antes de esta ronda, P4 imprimía nombres completos de fuente (`Sage — Magic Initiate (Wizard)`), lo que provocaba filas partidas en dos líneas. Era un incumplimiento de lo ya decidido (D-0058 «Tipo de origen», editor de fuentes de la App, mockup aprobado) y no una decisión de diseño pendiente.

## Producto

- Shared `PcSheetV11SpellSourceLabels.sourceLabel`: Clase → nombre de la clase; Rasgo, Raza, Trasfondo, Dote, Objeto, Objeto mágico, Don, Otro; sin tipo → `Otro`. `forSpells` solo añade prefijo de UUID a rótulos basados en nombre (Clase) que colisionan.
- P4: filas y referencia mágica usan esos rótulos (una línea o hasta tres líneas de 7 pt; más de tres fuentes conservan `SEGÚN FUENTE`).
- P1: la celda de 81 pt imprime los valores por fuente a 9, 8 o 7 pt si caben; si no, conserva `SEGÚN FUENTE`.
- Android y Desktop con el mismo código.

## Pruebas y comprobación local

Pruebas de escritorio ajustadas a la regla: Mara P1 ya no dice `SEGÚN FUENTE` (imprime `Cronomancia umbral ETE`); P4 de Ilyra imprime `Mago`, `Trasfondo`, `Raza` y no los nombres largos; el fixture de cuatro fuentes imprime `[ ] Otro`; el número de filas manuscritas de Ilyra pasa de 87 a 91 porque las filas ya no se parten en dos líneas. 93 pruebas de PDF real pasan localmente. Revisión raster: P4 de Ilyra queda alineada como el diseño aprobado (rótulo corto a la derecha, sin líneas partidas); P1 de Mara cabe a 8 pt; P1 de Ilyra, con tres fuentes, conserva `SEGÚN FUENTE`.

## Efecto sobre registros anteriores

Los G3 PASS acotados de `P406` y `P410` (C230) dependían de nombres impresos y **se devuelven a NOT_VERIFIED**. `P115` y `P113` no cambian.

## Hallazgo para la siguiente ronda (sin decisión)

Comparación visual de P1 contra el golden: la P1 de la App sigue visiblemente por debajo del diseño aprobado (bandas de filas, cartelas y ornamentos, tipografías, etiqueta suelta `HABILIDADES BAJO ATRIBUTO`). Es la mayor brecha medible y será el siguiente bloque.

**Gates sin promoción:** G0 parcial, G1 PASS semántico anterior, G2/G3 parciales, G4 BLOCKED 106 reglas NOT_VERIFIED con `active_candidate=null`, G5/G6 pendientes, C211 BLOCKED. PR #173 DRAFT NO MERGE.