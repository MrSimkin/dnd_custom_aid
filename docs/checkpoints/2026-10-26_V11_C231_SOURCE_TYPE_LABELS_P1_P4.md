# C231 — Fuentes de conjuro rotuladas por tipo de origen en P1 y P4

Fecha 2026-10-26 (Chile). PR #173 DRAFT/NO MERGE, rama `docs/pc-sheet-v11-approved-implementation-handoff`. **Estado: PASS LOCAL FÍSICO acotado (NO G4, NO aprobación del propietario).**

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

## Evidencia de CI

[Scaffold #40401076028](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40401076028) y [QA #40401076024](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40401076024) SUCCESS en `9e6cfda5`. Comparación sobre los artefactos de CI (96 PDF / 877 páginas contra el lote anterior): misma paginación, 877/877 imágenes iguales, 816 páginas con las mismas palabras y 61 distintas (celdas de lanzamiento de P1, filas y referencia de P4); 25 páginas cambian en vectores porque las filas de P4 ya no se parten y caben más filas manuscritas. Auditoría física de CI: 96 PDF, 0 errores; medidores P4 sin PDF fallido. [Acta](../pc-sheet-v11/qa/evidence/2026-10-26_V11_C231_SOURCE_TYPE_LABELS_CI.json). La matriz registra G1 y G3 acotados de `P411` y G3 acotados de `P406` y `P410`.
## Efecto sobre registros anteriores

Los G3 PASS acotados de `P406` y `P410` (C230) dependían de nombres impresos; se devolvieron a NOT_VERIFIED y se volvieron a registrar arriba con la evidencia de esta ronda. `P115` y `P113` no cambian.

## Hallazgo para la siguiente ronda: P1 frente al golden (medido, sin cambios de código todavía)

Se renderizó la P1 de Aldren A e Ilyra B de la App y las P1 A/B del golden `v11_ordinario_6mas2`. La estructura y las geometrías clave ya coinciden (cartelas achaflanadas de identidad, tarjetas de combate 3+3+2, cuadrícula 3x3 de espacios, paneles de Rasgos y Municiones); el código ya fija estas coordenadas con pruebas. Diferencias medidas que quedan:

1. **Jerarquía tipográfica invertida** en identidad y combate: etiqueta 12 pt Barlow en negrita y valor 9 pt, mientras el golden usa etiqueta pequeña y valor grande (CA, PV, Iniciativa...).
2. **Filas sin bandas de tono** en Ataques, Rasgos y Habilidades (el golden alterna tonos azul niebla; D-0076 §3.12 punto 3).
3. **Texto bajo el logo** `P1 - Personaje / combate / habilidades / 1` y etiqueta `PRUEBA INTERNA V11 - NO APROBADA` (el golden no las lleva; las de prueba son provisionales por diseño del QA previo).
4. **Aprovechamiento vertical:** en variante B los seis bloques de atributo ocupan más alto que en el golden y dejan filas vacías en el panel de Habilidades; en variante A aparece la etiqueta suelta `HABILIDADES BAJO ATRIBUTO` al pie.
5. **Panel de Rasgos:** texto de 7 pt partido en tres líneas por entrada; el golden alterna filas de una línea.
6. Sin adornos de rombo en cartelas: el contrato pospone la ornamentación (PIN visual), no es defecto.

Cada punto cambia flujos medidos (desbordes a C2) y pruebas de coordenadas, por lo que se tratará por separado y con comparación de PDF reales antes/después.

**Gates sin promoción:** G0 parcial, G1 PASS semántico anterior, G2/G3 parciales, G4 BLOCKED 106 reglas NOT_VERIFIED con `active_candidate=null`, G5/G6 pendientes, C211 BLOCKED. PR #173 DRAFT NO MERGE.

## C232 — Corrección de P1 frente al golden, paso 1: jerarquía etiqueta/valor

En el golden las **etiquetas** de P1 son pequeñas (≈6-7 pt) y los **valores** llevan el peso visual; la App tenía etiquetas de 12 pt en identidad y de 9 pt en combate con valores de 9 pt. Cambio (Android y Desktop): etiquetas de identidad (`CLASE / NIVEL`, `JUGADOR`, `RAZA`, `ALINEAMIENTO`) y de las ocho tarjetas de combate a 7 pt; valores de combate a 11 pt centrados en la tipografía de etiquetas. No cambian cartelas, marcos, paginación ni flujos a C2.

Comprobación local antes/después sobre los 96 PDF reales de escritorio: **96/96 paginaciones iguales, 877/877 páginas con vectores e imágenes idénticos; solo cambia la página 1 de cada PDF (palabras)**. 93 pruebas de PDF reales pasan. Revisión raster de Ilyra B. **CI del paso 1:** [Scaffold #40402442458](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40402442458) y [QA #40402442414](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40402442414) SUCCESS en `b3f41a2f`; la comparación sobre los artefactos de CI contra C231 repite el resultado local (96 PDF / 877 páginas; solo cambia la página 1 de cada PDF; vectores e imágenes idénticos); auditoría física de CI 96 PDF, 0 errores.

### Paso 2: bandas de tono alternado en las filas de P1

El golden alterna dos tonos azul niebla (`#E7F2F7` / `#F0F7FA`) con una línea fina al pie de cada fila (D-0076 §3.12 punto 3). Cambio (Android y Desktop): bandas en las siete filas de la tabla de Ataques (antes solo las pares y casi invisibles), en cada fila de Habilidades bajo atributo (variante A), en cada línea de la lista de Habilidades (variante B) y en las seis filas por columna de Rasgos; los bloques de atributo alternan tono. No cambia ningún texto, posición, paginación ni flujo a C2.

Comprobación local con los 96 PDF reales de escritorio contra el paso 1: **96/96 paginaciones iguales, 877/877 páginas con las mismas palabras y posiciones, 877/877 imágenes iguales; cambian solo los vectores de la página 1 de cada PDF** (las bandas). 93 pruebas de PDF reales pasan. Pendiente: CI exacto. Siguientes pasos: aprovechamiento vertical (filas vacías en Habilidades B y etiqueta suelta `HABILIDADES BAJO ATRIBUTO` en A) y panel de Rasgos en una línea por fila.

### Paso 3: etiqueta suelta y pie de la columna izquierda (variante A)

La etiqueta `HABILIDADES BAJO ATRIBUTO` no existe en el golden (las habilidades ya están físicamente bajo su atributo) y quedaba sola al pie de la hoja. Se elimina, y el pie sin uso de la columna izquierda se rellena, como en el golden, con filas manuscritas de 13 pt con las mismas bandas de tono alternado. No cambia ningún dato impreso. En la variante B se decidió **no** compactar los bloques de atributo: el espacio liberado solo añadiría más filas vacías, y el contrato pide evitar huecos arbitrarios, no minimizar altura.

Comprobación local con los 96 PDF reales contra el paso 2: 96/96 paginaciones iguales; 877/877 imágenes iguales; solo cambia la página 1 de los 48 PDF de variante A; las únicas palabras que desaparecen son las tres de la etiqueta; ninguna palabra nueva. Las pruebas de PDF reales pasan (la prueba de Aldren ahora afirma la ausencia de la etiqueta) y la verificación de texto de ataques (`test_v11_attacks_render_contract.py`) se actualizó a las bandas del paso 2; fallaba en la CI de `a5b9a88c` y se corrigió en `ae4cf199`. Pendiente: CI exacto.