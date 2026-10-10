# G0 — Único paso manual de custodia del golden V11 (cuando Git directo no esté disponible)

**Estado posterior (2026-10-09): R-01 VERIFICADO; pasos de este archivo ya ejecutados y quedan solo como fallback histórico.** Actions run #38009729554 comprobó SHA-256 después de checkout (1.520.026 bytes, 20 entradas, 8 PDFs). No solicitar subir otra vez. **Contexto previo al cierre:** R-01 OPEN / BLOQUEO DE CAPACIDAD, NO fallo de diseño. El Worker tiene el ZIP local íntegro y SHA-256 verificado; las herramientas GitHub actuales solo permiten publicar texto y blobs con contenido explícito, pero no transferir directamente el archivo binario local de 1,520,026 bytes. El intento de `git ls-remote` por contenedor falló por DNS. **No declarar ZIP publicado sin confirmarlo en Git.**

## Acción mínima del propietario — COMPLETADA (instrucciones históricas, NO repetir)

1. Descarga el archivo **`dnd_custom_aid_V11_Carta_revision.zip`** desde el mensaje V11/kit de ChatGPT; **no** extraer ni modificar el ZIP.
2. En GitHub, abre **la carpeta de esta rama**:
   `https://github.com/MrSimkin/dnd_custom_aid/tree/docs/pc-sheet-v11-approved-implementation-handoff/docs/pc-sheet-v11`
3. Usa **Add file → Upload files** y selecciona el ZIP original. En `Commit changes`, asegúrate de que se guarde directamente en **`docs/pc-sheet-v11/\` de la rama `docs/pc-sheet-v11-approved-implementation-handoff`**, NUNCA en `main`.
4. Cuando aparezca en la carpeta, la custodia sigue provisionalmente `OPEN` hasta que Worker descargue desde Git la copia nueva y compare SHA-256.

**Nombre obligatorio:** `docs/pc-sheet-v11/dnd_custom_aid_V11_Carta_revision.zip`.

**Hash esperado SHA-256:** `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a` y tamaño **1,520,026 bytes**.

## Verificación Worker obligatoria después

- Confirmar que el archivo aparece en la **rama exacta**.
- Descargar desde GitHub y medir hash sobre **los bytes recuperados**, no contra texto de conversación.
- Comparar inventario de 20 entradas/8 PDFs, fuente Python, pruebas, previsualizaciones.
- Solo entonces cambiar R-01 `OPEN` → `VERIFICADO` en `ARTEFACTOS_Y_PROCEDENCIA.md`, G0 mapa y checkpoint, citando SHA del commit y archivo; continuar con otros bloqueos G0 sin reabrir estética.
- Si hash diverge: **BLOCKED**, no reconstruir por intuición.

## Fallback técnico para propietario con terminal (solo si falla upload web)

Con un clon limpio de la rama específica y Git autenticado, copiar el ZIP a `docs/pc-sheet-v11/`, comparar SHA local y ejecutar `git add docs/pc-sheet-v11/dnd_custom_aid_V11_Carta_revision.zip`, `git commit -m "assets(pdf): preserve approved V11 golden"`, `git push origin docs/pc-sheet-v11-approved-implementation-handoff`. Nunca usar `git push --force`, reset destructivo, ni publicar en `main`.

**Este es un boundary de capacidad; no consume intentos ni habilita recuperación RCR-1.** El Worker puede seguir analizando datos y QA sin código destructivo mientras se resuelve.
