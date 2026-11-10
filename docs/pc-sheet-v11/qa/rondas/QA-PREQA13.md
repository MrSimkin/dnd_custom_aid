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

El propietario pidió los PJ por SQL y después eligió que el Worker lo ejecutara con la herramienta `neon` de su PC, con una sesión que él mismo inició (`neon auth`). El Worker no vio ni pidió credenciales.

- **Destino:** Neon, proyecto `holy-meadow-19010740` (`dnd-custom-aid-dev`), rama `production`, base `dnd-custom-aid-dev`. Organización `MrSimkin_Org`.
- **Script:** `D:\DnD_Aid\APKs\QA preqa.13\qa_pj_preqa13_neon_sql_editor.sql`, SHA-256 `48fc7d518c399631c3e582e8be60702bc13f6688dff811a3d55ba71c279a5abe`. Solo caracteres ASCII: los acentos van como `\uXXXX` dentro del JSON, para que ningún cliente pueda volver a dañarlos. Mismo `ON CONFLICT` que `database/qa/seed_pc_sheet_runtime_characters.sql`; no toca dueño, controlador ni la membresía de Gmail.
- **Hallazgo antes de ejecutar (solo lectura):** los tres PJ de QA tenían los acentos dañados en la base desde la carga del 2026-09-24 («Acólito» guardado como «AcÃ³lito»): 3 de los 4 PJ de la base. Su última modificación era del 2026-09-24, así que no había ediciones del propietario que perder. Las revisiones eran Aldren 1, Ilyra 1 y Mara 2.
- **Ejecución:** `BEGIN`, comprobación de Outlook, campaña, membresía DM, tres `INSERT … ON CONFLICT`, comprobación final y `COMMIT`. Sin errores.
- **Resultado devuelto por Neon:**

| id | PJ | revisión | dueño y controlador | versión |
|---|---|---|---|---|
| `7a000000-0000-4000-8000-000000000101` | Aldren Vale | 2 | Gmail `4ba0f476-…` | 2 |
| `7a000000-0000-4000-8000-000000000102` | Ilyra Quill | 2 | Gmail `4ba0f476-…` | 2 |
| `7a000000-0000-4000-8000-000000000103` | Mara de los Siete Umbrales | 3 | Gmail `4ba0f476-…` | 2 |

- **Membresía de la campaña:** Outlook `f34bc5f0-…` DM ACTIVE; Gmail `4ba0f476-…` PLAYER ACTIVE.
- **Verificación posterior:** 0 PJ con acentos dañados en toda la base. Cada ficha guardada se leyó en base64 y es igual a su fixture del repositorio (blobs `498a47e9`, `9b980847`, `ed919c6e`).
