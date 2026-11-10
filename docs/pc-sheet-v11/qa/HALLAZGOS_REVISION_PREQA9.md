# Estado de los hallazgos de la revisión de `0.5.0-preqa.9`

**Quién escribe:** el implementador. El acta original, sin cambios, es `ACTA_REVISION_INDEPENDIENTE_PREQA9.md`, y manda sobre este archivo.

**Resultado:** el candidato no pasa. 48 secciones: CUMPLE 35, NO_CUMPLE 4, NO_DEMOSTRADO 3, SUSTITUIDA 6. Golden: DIFERENCIAS_JUSTIFICADAS 1, NO_COINCIDE 7. De las reglas QA01 a QA08 y GLB12: 3 demostradas con salvedades, 6 sin demostrar.

**Comprobado por el implementador en los PDF:** N1, N2 (104 páginas), N3, N4, N8 y N9 son reales.

| Id | Hallazgo | Estado | Qué falta |
|---|---|---|---|
| N1 | Ataques en C2 como prosa y fuera de orden | Corregido en C456 | «Sí, en su lugar». Falta que el revisor lo confirme |
| N2 | Título `PERSONAJE, HABILIDADES Y ACCIONES / CONTINUACIÓN` | Corregido en C456 | Falta que el revisor lo confirme |
| N3 | Hojas casi vacías, un módulo por hoja | En parte, según la revisión de `preqa.11`: Especial sigue solo (M2) | Equipo y Tesoro van en el flujo compartido. Especial, al pie si cabe. Falta que el revisor lo confirme |
| N4 | Municiones de C2 como prosa | Corregido en C456 | Más de 20: `____ / X`, porque el contrato escrito prevalece sobre el golden. Falta que el revisor lo confirme |
| N5, G-e | Rótulos de Equipo Especial | Corregido en C456 | El golden prevalece sobre §3.42. Falta que el revisor lo confirme |
| N6 | Fila larga de P4 | Corregido en C458 | Falta que el revisor lo confirme |
| N7 | Ficha del Libro con nombre largo | Corregido en C458 | Falta que el revisor lo confirme |
| N8 | Línea en ATAQUES vacía | En parte, según la revisión de `preqa.11`: quedan las líneas verticales (M6) | Falta que el revisor lo confirme |
| N9 | Leyenda añadida en la variante B | Corregido en C456 | Falta que el revisor lo confirme |
| N10 | Rótulo con una sola fuente | Corregido en C456 | Falta que el revisor lo confirme |
| N11 | Nombre largo recortado | Corregido en C456 | «Cortado hasta donde sea que quepa», sin aviso. Falta que el revisor lo confirme |
| N12 | Android y Desktop no son idénticos | Registrado | Diferencia invisible; el acta del candidato ya indica su umbral |
| N13 | Rótulos de continuación y detalles | En parte | `TEMP` corregido en C456. La segunda línea de cabecera queda para el propietario, que se reservó ese control. El rótulo `/ CONTINUACIÓN` sigue el contrato |
| Notas de objeto especial | Se imprimen | Decidido | «No, se imprimen» (2026-11-06) |
| F03 | Fuente de clase sin clase enlazada | Sin cambio, a propósito | Con «Clase» dos fuentes sin enlazar se leerían igual. Conserva su nombre solo en ese caso |
| F08 | Corte entre clases | Corregido en C458 | Clase y nivel juntos; las que no caben siguen en C2. Falta que el revisor lo confirme |
| §3.3 | Retrato real en Android | Probado en `preqa.11` | Los dos modos, sin deformar. Ver `ACTA_CANDIDATO_PREQA11.md`. Falta que el revisor lo confirme |
| §3.4 | Subraza | Caso creado | `V11-INTERNAL-PROOF-subrace-A.pdf` y `-B`, en CI. Falta que el revisor lo confirme |
| §3.12 | Prueba impresa | Sin demostrar | La hace el propietario al final |
| QA05, GLB12 | Hojas antiguas | Redefinidas | El propietario decidió que V11 las reemplaza (2026-11-06). No se comparan ni se reparan. Falta demostrar que V11 no depende de ellas |

**Identidad del próximo candidato:** no usar `preqa.9` ni `preqa.10`, que ya se usaron en ramas anteriores para otros binarios. El siguiente será `0.5.0-preqa.11`, build 51100.
