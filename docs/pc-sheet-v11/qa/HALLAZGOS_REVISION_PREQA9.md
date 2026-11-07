# Estado de los hallazgos de la revisión de `0.5.0-preqa.9`

**Quién escribe:** el implementador. El acta original, sin cambios, es `ACTA_REVISION_INDEPENDIENTE_PREQA9.md`, y manda sobre este archivo.

**Resultado:** el candidato no pasa. 48 secciones: CUMPLE 35, NO_CUMPLE 4, NO_DEMOSTRADO 3, SUSTITUIDA 6. Golden: DIFERENCIAS_JUSTIFICADAS 1, NO_COINCIDE 7. De las reglas QA01 a QA08 y GLB12: 3 demostradas con salvedades, 6 sin demostrar.

**Comprobado por el implementador en los PDF:** N1, N2 (104 páginas), N3, N4, N8 y N9 son reales.

| Id | Hallazgo | Estado | Qué falta |
|---|---|---|---|
| N1 | Ataques en C2 como prosa y fuera de orden | Abierto | Decidido, «Sí, en su lugar». Falta corregir |
| N2 | Título `PERSONAJE, HABILIDADES Y ACCIONES / CONTINUACIÓN` | Abierto | Corregir: atributos y habilidades bajo su título, tabla bajo ACCIONES |
| N3 | Hojas casi vacías, un módulo por hoja | Abierto | Sin pregunta: el golden comparte hoja y prevalece sobre D-0076. Falta corregir |
| N4 | Municiones de C2 como prosa | Abierto | Corregir la fila. Más de 20: se queda `____ / X`, porque el contrato escrito prevalece sobre el golden |
| N5, G-e | Rótulos de Equipo Especial | Abierto | Sin pregunta: el golden prevalece sobre §3.42. Falta corregir |
| N6 | Fila larga de P4 | Abierto | Corregir |
| N7 | Ficha del Libro con nombre largo | Abierto | Corregir |
| N8 | Línea en ATAQUES vacía | Abierto | Corregir como el golden |
| N9 | Leyenda añadida en la variante B | Abierto | Corregir como el golden |
| N10 | Rótulo con una sola fuente | Abierto | Sin pregunta: el golden imprime solo el valor, y la aclaración escrita habla de varias fuentes. Falta corregir |
| N11 | Nombre largo recortado | Abierto | Decidido: «cortado hasta donde sea que quepa», sin aviso. Falta corregir |
| N12 | Android y Desktop no son idénticos | Registrado | Diferencia invisible; el acta del candidato ya indica su umbral |
| N13 | Rótulos de continuación y detalles | Abierto | Revisar uno por uno |
| Notas de objeto especial | Se imprimen | Decidido | «No, se imprimen» (2026-11-06) |
| F03 | Fuente de clase sin clase enlazada | Sin cambio, a propósito | Con «Clase» dos fuentes sin enlazar se leerían igual. Conserva su nombre solo en ese caso |
| F08 | Corte entre clases | Abierto | Corregir |
| §3.3 | Retrato real en Android | Sin demostrar | Probarlo en el emulador |
| §3.4 | Subraza | Sin demostrar | Crear el caso |
| §3.12 | Prueba impresa | Sin demostrar | La hace el propietario al final |
| QA05, GLB12 | Hojas antiguas | Redefinidas | El propietario decidió que V11 las reemplaza (2026-11-06). No se comparan ni se reparan. Falta demostrar que V11 no depende de ellas |

**Identidad del próximo candidato:** no usar `preqa.9` ni `preqa.10`, que ya se usaron en ramas anteriores para otros binarios. El siguiente será `0.5.0-preqa.11`, build 51100.
