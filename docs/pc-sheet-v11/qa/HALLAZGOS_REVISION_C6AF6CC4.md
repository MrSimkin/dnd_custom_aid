# Estado de los hallazgos de la revisión independiente de `c6af6cc4`

**Fecha:** 2026-11-06. **Quién lo escribe:** el implementador, no el revisor. El acta original está en [ACTA_REVISION_INDEPENDIENTE_C6AF6CC4.md](ACTA_REVISION_INDEPENDIENTE_C6AF6CC4.md) y no se modifica.

Este documento dice qué se hizo con cada hallazgo. **«Corregido» es la lectura del implementador.** Solo una nueva revisión independiente sobre el candidato puede cerrarlo, y hasta entonces `legacy_48_sections` y `golden_eight_pdfs` siguen pendientes.

## Corrección a mi conciliación anterior

`CONCILIACION_D0076_V11.md` decía «sin diferencia» en §3.8, §3.25, §3.26, §3.29, §3.30, §3.31 y §3.33. El revisor encontró diferencias en todas ellas, y las comprobé en los PDF. Esas filas estaban mal: yo había revisado el texto contra las reglas, no contra lo impreso.

## Hallazgos

| Id | Hallazgo | Estado | Qué cambió o qué falta |
|---|---|---|---|
| F01 | P4 con 3 casillas fijas | **Corregido**, salvo la pregunta P3 | Un nivel con espacios guardados dibuja una casilla por espacio real. Un nivel sin espacios guardados conserva las 3 del golden hasta que el propietario decida |
| F02 | Salvación competente sin marca | **Corregido** | La casilla muestra ✓ según el dato guardado, en P1 y C2 |
| F03 | Rótulo con el nombre propio de la fuente | **Corregido** | Una fuente de clase se rotula con el nombre de la clase. No se imprime ningún identificador interno |
| F04 | P4 ocultaba valores con `SEGÚN FUENTE` | **Corregido** | P4 imprime el valor de cada fuente, hasta tres renglones a 5,5 pt. `SEGÚN FUENTE` queda solo si tampoco así cabe; probado hasta seis fuentes |
| F05 | Habilidades de C2 como prosa | **Corregido en parte** | Filas con casilla, nombre y valor con signo. **Abierto:** una habilidad que desborda el panel B a C2 aún no lleva casilla |
| F06 | Alcance añadido y datos repetidos | **Corregido en parte** | El alcance ya no se repite en C2. **Pregunta P6:** si debe ir en ATAQUES. **Abierto:** municiones en P1, Equipo y C2 |
| F07 | C2 de P1 tras P3 | **Sin cambio** | El golden, aprobado después, la pone tras P3, y la jerarquía del contrato le da prioridad. Se le informa al propietario |
| F08 | Multiclase con comas | **Corregido en parte** | Usa `Guerrero 5 / Mago 3`. **Abierto:** con muchas clases, el corte puede separar un nombre de su nivel |
| F09 | `VER C2` sin página | **Abierto** | Afecta a nombres muy largos, muchas clases y espacios sin cabida |
| F10 | Referencias incompletas | **Corregido en parte** | El pie de P1 y P2 nombra todas sus páginas. **Abierto:** los tramos que siguen dentro de C2 solo avisan en el pie |
| F11 G-a | Orden de habilidades | **Corregido** | Orden del golden |
| F11 G-b | Atributos propios sin mayúsculas | **Corregido** | Mayúsculas, a 7,7 pt en C2 como el golden |
| F11 G-c | Títulos de C2 | **Abierto** | «ESPACIOS REALES» y otros títulos no son los del golden |
| F11 G-d | Barra lateral en C2 | **Corregido** | Eliminada |
| F11 G-e | Rótulos de Equipo Especial | **Sin cambio** | §3.42 es decisión escrita y prevalece sobre el golden |
| F11 | Índice del Libro | **Pregunta** | El golden no lo trae; el contrato C341 lo menciona como existente |
| F11 | Siempre una C2 | **Corregido** | Un personaje nuevo imprime las 4 páginas del golden. La C2 solo sale con datos que continuar |
| F12 | Letra pequeña de recursos | **Abierto** | Sigue a 7,2 pt |
| F13 | Cabecera y717 en el contrato | **Corregido en el contrato** | La línea está en y709,8, como el golden, desde C367. El contrato ya lo indica |
| F14 | La hoja antigua dentro de cada PDF | **Corregido** | Se incrusta solo el logo, de 344×134 píxeles |
| F15 | Fila épica | **Pregunta P10** | La App no tiene dato de reglas épicas |

**Encontrado después por las auditorías:** una tarjeta de P4 sin valor dibujaba su línea en negro, en un no lanzador con un conjuro de raza. Ningún PDF anterior cubría ese caso. Corregido en C441.

## Casos sin PDF en la revisión

Ahora tienen PDF real conservado y una prueba de lo que pide la decisión: retrato en sus dos modos (§3.3), dos clases (§3.4, §3.7), más de 20 municiones (§3.15), no lanzador con un conjuro de raza (§3.35), sin ataques (§3.38), personaje nuevo sin notas (§3.44) e imágenes de Trasfondo sin retrato (§3.45).

**Siguen sin PDF:** niveles épicos (§3.11) y la comparación entre composiciones candidatas (§3.32).

## Preguntas al propietario

Se hacen de una en una, por página. La lista está en la sección 5 del acta: P2, P3, P4, P5, P6, P7, P8, P9 y P10. P1 se resuelve por la jerarquía del contrato y se informa.
