# Estado de los hallazgos de la revisión independiente de `c6af6cc4`

**Fecha:** 2026-11-06. **Quién lo escribe:** el implementador, no el revisor. El acta original está en [ACTA_REVISION_INDEPENDIENTE_C6AF6CC4.md](ACTA_REVISION_INDEPENDIENTE_C6AF6CC4.md) y no se modifica.

Este documento dice qué se hizo con cada hallazgo. **«Corregido» es la lectura del implementador.** Solo una nueva revisión independiente sobre el candidato puede cerrarlo, y hasta entonces `legacy_48_sections` y `golden_eight_pdfs` siguen pendientes.

## Corrección a mi conciliación anterior

`CONCILIACION_D0076_V11.md` decía «sin diferencia» en §3.8, §3.25, §3.26, §3.29, §3.30, §3.31 y §3.33. El revisor encontró diferencias en todas ellas, y las comprobé en los PDF. Esas filas estaban mal: yo había revisado el texto contra las reglas, no contra lo impreso.

## Hallazgos

| Id | Hallazgo | Estado | Qué cambió o qué falta |
|---|---|---|---|
| F01 | P4 con 3 casillas fijas | **Corregido** | Por decisión del propietario (2026-11-06), P4 dibuja las mismas casillas por nivel que P1: 4, 3, 3, 3, 4, 2, 2, 1, 1 |
| F02 | Salvación competente sin marca | **Corregido** | La casilla muestra ✓ según el dato guardado, en P1 y C2 |
| F03 | Rótulo con el nombre propio de la fuente | **Corregido** | Una fuente de clase se rotula con el nombre de la clase. No se imprime ningún identificador interno |
| F04 | P4 ocultaba valores con `SEGÚN FUENTE` | **Corregido** | P4 imprime el valor de cada fuente, hasta tres renglones a 5,5 pt. `SEGÚN FUENTE` queda solo si tampoco así cabe; probado hasta seis fuentes |
| F05 | Habilidades de C2 como prosa | **Corregido en C440 y C452** | Filas con casilla, nombre y valor con signo. La habilidad que desborda el panel B también (C452) |
| F06 | Alcance añadido y datos repetidos | **Corregido en C440, C451 y C452** | El alcance ya no se repite en C2. **P6 respondida (2026-11-06):** «Nombre y alcance», se queda en ATAQUES. Las municiones ya salen una sola vez (C451). Notas de ataque: «Sí, solo en App» (C452) |
| F07 | C2 de P1 tras P3 | **Sin cambio** | El golden, aprobado después, la pone tras P3, y la jerarquía del contrato le da prioridad. Se le informa al propietario |
| F08 | Multiclase con comas | **Sigue en parte, confirmado en `preqa.9`** | Usa `Guerrero 5 / Mago 3`. **Abierto:** con muchas clases, el corte puede separar un nombre de su nivel |
| F09 | «VER C2» sin número de página | **Corregido en C453** | Ahora dice «CONTINÚA EN PÁG. n», o «NOMBRE EN PÁG. n» en la cabecera, con la página real |
| F10 | Referencias incompletas | **Corregido en C441 y C453** | El pie de P1 y P2 nombra todas sus páginas, y un módulo que sigue en otra hoja de C2 lo avisa en la banda inferior |
| F11 G-a | Orden de habilidades | **Corregido** | Orden del golden |
| F11 G-b | Atributos propios sin mayúsculas | **Corregido** | Mayúsculas, a 7,7 pt en C2 como el golden |
| F11 G-c | Títulos de C2 | **Sigue abierto** | Decía «corregido» y no lo estaba: la revisión de `preqa.9` halló `PERSONAJE, HABILIDADES Y ACCIONES / CONTINUACIÓN` en 104 páginas (N2) |
| F11 G-d | Barra lateral en C2 | **Corregido** | Eliminada |
| F11 G-e | Rótulos de Equipo Especial | **Sigue abierto** | Mi razonamiento invertía la precedencia: el golden va antes que D-0076. Es la pregunta 2 de la revisión de `preqa.9` |
| F11 | Índice del Libro | **Corregido en C450** | Decisión del propietario (2026-11-06): «Sí, quitarla». El Libro empieza en sus fichas, como el golden |
| F11 | Siempre una C2 | **Corregido** | Un personaje nuevo imprime las 4 páginas del golden. La C2 solo sale con datos que continuar |
| F12 | Letra pequeña de recursos | **Corregido en C452** | Recuperación, fuente y notas van a 6,1 pt |
| F13 | Cabecera y717 en el contrato | **Corregido en el contrato** | La línea está en y709,8, como el golden, desde C367. El contrato ya lo indica |
| F14 | La hoja antigua dentro de cada PDF | **Corregido** | Se incrusta solo el logo, de 344×134 píxeles |
| F15 | Fila épica | **Corregido en C448** | Por decisión del propietario (2026-11-06): opción por clase en la App y fila N10 a N12 solo si está activada. En C449 el propietario decidió `____/____` sin casillas y que esos niveles son solo a mano: la App no los guarda |

**Encontrado después por las auditorías:** una tarjeta de P4 sin valor dibujaba su línea en negro, en un no lanzador con un conjuro de raza. Ningún PDF anterior cubría ese caso. Corregido en C441.

## Casos sin PDF en la revisión

Ahora tienen PDF real conservado y una prueba de lo que pide la decisión: retrato en sus dos modos (§3.3), dos clases (§3.4, §3.7), más de 20 municiones (§3.15), no lanzador con un conjuro de raza (§3.35), sin ataques (§3.38), personaje nuevo sin notas (§3.44) e imágenes de Trasfondo sin retrato (§3.45).

**Siguen sin PDF:** niveles épicos (§3.11) y la comparación entre composiciones candidatas (§3.32).

## Preguntas al propietario

Se hacen de una en una, por página. La lista está en la sección 5 del acta: P4; P2 sigue al golden. Ya están respondidas P10 (opción por clase, regla P129), P9 («Sí, compartida», regla P307), P3 («Sí, las mismas», regla P412), P8 (casillas al máximo de cada nivel, regla P128), P5 («Sí, línea a mano»), P6 («Nombre y alcance», regla P126) y P7 («Sí, a ATAQUES», regla P127), las tres el 2026-11-06. P1 se resuelve por la jerarquía del contrato y se informa.
