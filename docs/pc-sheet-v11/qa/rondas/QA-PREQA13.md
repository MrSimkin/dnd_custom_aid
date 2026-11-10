# Ronda QA-PREQA13 — QA del propietario sobre `0.5.0-preqa.13`

## 0. Identidad del QA

- **ID de ronda:** QA-PREQA13. **Fecha de entrega:** 2026-11-08. **Propietario:** MrSimkin, por el chat de esta sesión.
- **Versión:** `0.5.0-preqa.13`, build 51300. **Commit:** `bdfdaa292e319dc0c5a5a9bce62ad1f619c4b477`. **CI:** Scaffold 42493357978, APK artefacto 13622832102, PDF artefacto 13622727830.
- **APK SHA-256:** `6f586093debc9bd54d5ded260e70b2292b15813f748537ffb07be24f1f226ad7`, entregado en `D:\DnD_Aid\APKs\dnd-custom-aid-0.5.0-preqa.13-build-51300.apk`.
- **Fichas:** `PJ-QA-1-Aldren-Vale.json`, `PJ-QA-2-Ilyra-Quill.json` y `PJ-QA-3-Mara-de-los-Siete-Umbrales.json`, copias exactas de `qa/pc-sheet/fixtures/01`, `02` y `03`.
- **Guía:** `GUIA_QA_PROPIETARIO_PREQA13.md`.
- **Excepción del propietario:** entra a QA sin que el control final haya pasado. Sus palabras: «no, estamos dando mil vueltas en revisiones independientes, es hora de; preparar 3 PJs para probar, darme el APK, preparar el QA» (2026-11-08).
- **PDF del propietario:** `PENDIENTE` (llegarán a `D:\DnD_Aid\PDF Check`).

## 1. Recepción completa e inmutable

Pendiente: se copian aquí, literales y numerados, todos los comentarios del propietario, con su ID `V11-QA-###`.

| ID | Cita literal | Artefacto/Página | Defecto observado | Esperado | Estado |
|---|---|---|---|---|---|

## Carga de los PJ en la base DEV alojada (2026-11-10)

El propietario pidió los PJ por SQL. Script para el SQL Editor de Neon (proyecto `dnd-custom-aid-dev`, rama `production`, base `dnd-custom-aid-dev`), el mismo camino probado el 2026-09-24: `D:\DnD_Aid\APKs\QA preqa.13\qa_pj_preqa13_neon_sql_editor.sql`, SHA-256 `701ef26897860f0fdc9d84bdc2790e08723fe359ce264db1b11ff5a174061fe7`.

- **Qué hace:** crea o actualiza la campaña `QA - PC Sheet PDF Runtime` (`7a000000-…-0001`), deja a Outlook como DM activo y crea o actualiza Aldren (`…-0101`), Ilyra (`…-0102`) y Mara (`…-0103`) con las fichas exactas del repositorio, con el mismo `ON CONFLICT` que `database/qa/seed_pc_sheet_runtime_characters.sql`.
- **Qué no toca:** dueño ni controlador de cada PJ, ni la membresía de Gmail.
- **Cambio esperado:** Aldren e Ilyra coinciden con lo cargado el 2026-09-24 (mismos blobs `498a47e9` y `9b980847`), así que su revisión no debería subir. Mara se cargó entonces con el blob `d33dd0aa`; el repositorio tiene `ed919c6e` (commit `559cae9d`, «make Mara binary QA markers persistence-valid»), así que su revisión debería subir en 1.
- **No ejecutado por el Worker:** no hay acceso autenticado a Neon desde esta sesión ni PostgreSQL local; el script se revisó a mano y sigue el patrón ya ejecutado con éxito.
- **Evidencia a devolver:** la tabla final (id, nombre, revisión, dueño, controlador, versión, fecha y `outlook_dm_activo`).
