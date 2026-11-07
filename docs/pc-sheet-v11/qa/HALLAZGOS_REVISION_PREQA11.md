# Estado de los hallazgos de la revisión de `preqa.11`

**Fuente:** `ACTA_REVISION_INDEPENDIENTE_PREQA11.md` (2026-11-07). Este documento solo sigue su estado; no es prueba.

**Resultado de la revisión:** no pasa. 48 secciones: CUMPLE 39, NO_CUMPLE 2 (§3.29, §3.32), NO_DEMOSTRADO 1 (§3.12), SUSTITUIDA 6. Golden: DIFERENCIAS_JUSTIFICADAS 2, NO_COINCIDE 6.

**Comprobado por el implementador en los PDF de Android:** M1, M2 y M3 son reales.

| Id | Hallazgo | Estado | Qué falta |
|---|---|---|---|
| M1 | Equipo en pares cambia de orden al partirse | Corregido en C464 | Cada tramo se lee por columnas. Falta que el revisor lo confirme |
| M2 | Equipo Especial en una hoja casi vacía | Corregido en C464 | Como el golden B p5: a todo el ancho, con Rasgos e Historia debajo. Sin módulos que lo sigan, queda en su hoja. Falta que el revisor lo confirme |
| M3 | Habilidades sueltas sin subtítulo (A) | Corregido en C463 | Subtítulo del golden («HABILIDADES DE SABIDURÍA»). Falta que el revisor lo confirme |
| M4 | Rótulos de columna en la franja de cabecera | Corregido en C463 | Sin banda; en la línea del rótulo «MÓDULOS COMPARTIDOS». Falta que el revisor lo confirme |
| M5 | «VIENE DE PÁG. 1» en módulos que no están en P1 | Corregido en C463 | ACCIONES y RECURSOS sin origen, y P1 no los nombra. Falta que el revisor lo confirme |
| M6 | Filetes verticales en ATAQUES vacía | Corregido en C463 | Solo en las filas con ataque. Falta que el revisor lo confirme |
| M7 | Falta «dos subcolumnas» (B) | Corregido en C463 | Falta que el revisor lo confirme |
| M8 | Segunda línea de cabecera inexacta | Decidido | «nada»: las hojas de continuación no llevan nada a la derecha (propietario, 2026-11-07) |
| M9, F03 | Fuente de clase sin clase enlazada | Decidido | «Solo "Clase"» (propietario, 2026-11-07) |
| M10 | Nombre completo como prosa en C2 | Decidido | «solo quiero el nombre en la cabecera» (propietario, 2026-11-07) |
| M11 | Respuestas atribuidas al propietario | Corregido en el contrato | El origen de cada regla está escrito (2026-11-07) |
| M12 | Alcance que sigue en C2 como renglón de texto | Abierto | Sin golden para este caso; se deja como está y se informa |
| M13 | Tres fuentes «Otro» con un solo rótulo | Decidido | «Una vez» (propietario, 2026-11-07) |
| Notas de munición | Quedan en la App | Decidido | «No, solo en App» (propietario, 2026-11-07) |
| §3.12 | Prueba impresa | Sin demostrar | La hace el propietario al final |
