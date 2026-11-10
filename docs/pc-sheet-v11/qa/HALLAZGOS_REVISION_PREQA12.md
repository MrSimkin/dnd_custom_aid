# Estado de los hallazgos de la revisión de `preqa.12`

**Fuente:** `ACTA_REVISION_INDEPENDIENTE_PREQA12.md` (2026-11-07). Este documento solo sigue su estado; no es prueba.

**Resultado de la revisión:** no pasa. 48 secciones: CUMPLE 38, NO_CUMPLE 3 (§3.29, §3.32, §3.48), NO_DEMOSTRADO 1 (§3.12), SUSTITUIDA 6. Golden: DIFERENCIAS_JUSTIFICADAS 5, NO_COINCIDE 3. Sin preguntas nuevas al propietario.

**Comprobado por el implementador en los PDF de Android:** H1, H2 y H3 son reales. H1 es una regresión de C464.

| Id | Hallazgo | Estado | Qué falta |
|---|---|---|---|
| H1 | Columna en blanco sin NOTAS en hojas de C2 (regresión) | Corregido en C468 | Falta que el revisor lo confirme |
| H2 | Equipo Especial en su propia hoja aunque cabría en la anterior | Corregido en C468 | Mara A y la ficha de estrés usan una hoja menos. Falta que el revisor lo confirme |
| H3 | Ficha larga del Libro pierde el nombre al continuar | Corregido en C468 | Forma de C341, nombre cortado donde deja de caber. Falta que el revisor lo confirme |
| H4 | «HABILIDADES CONTINÚA» cuando solo sigue un atributo | Corregido en C468 | Falta que el revisor lo confirme |
| H5 | Enlace de P1 a una hoja que solo tiene ACCIONES y RECURSOS | Corregido en C468 | Falta que el revisor lo confirme |
| H6 | Equipo empieza con una sola fila al pie | Corregido en C468 | Al menos tres filas juntas. Falta que el revisor lo confirme |
| H7 | Fila vacía de Equipo Especial repite una ubicación ocupada | Corregido en C468 | Falta que el revisor lo confirme |
| H8 | «Clase —» en las tarjetas mágicas | Sin cambio, a propósito | El guion dice que no hay valor guardado (regla C221) |
| H9 | Nombre largo cortado al mismo ancho en todas las hojas | Informativo | Ver con la cabecera |
| M2, M5, M9 | Cerrados en parte | Abierto | Siguen en H1, H2, H5 y H8 |
| M12 | Alcance que sigue en C2 como renglón | Abierto | Sin golden para el caso |
| §3.12 | Prueba impresa | Sin demostrar | La hace el propietario al final |
