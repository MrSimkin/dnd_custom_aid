# V11 — Comando «sigue» y preparación de G1

**Estado verificado:** 2026-10-09, PR #173 **DRAFT y sin fusionar**, `main` base `27bd199633f1068ca481c387973d38f35cbb0c48`. V11 **visualmente aprobada**, implementación Kotlin **NO INICIADA**. **G0 PARCIAL, G1–G6 NO INICIADOS**. No inferir nuevos estados del texto; comprobar GitHub y CI al ejecutar.

## 1. «Sigue» en cualquier sesión nueva

Al recibir «sigue», «continúa» o el prompt final, el Worker debe:
1. Verificar `main` remoto, leer **main** `AGENTS.md → RESUME.md → docs/checkpoints/LATEST.md → checkpoint canónico`.
2. Si `main` señala PR #173 y la rama `docs/pc-sheet-v11-approved-implementation-handoff`, abrir **su HEAD REMOTO vigente**, no el SHA histórico de este documento. Leer su `docs/checkpoints/LATEST.md`, `CONTRATO_VISUAL_APROBADO.md`, `PREFLIGHT_TECNICO_Y_RIESGOS.md`, `G0_MAPA_SEMANTICO_PRELIMINAR.md`, `PROCEDIMIENTO_QA_SIN_PERDIDA.md`, `qa/REGISTRO_QA_V11.json`, `PROTOCOLO_ROMPER_CICLO_Y_REPLANIFICAR.md` y plan G0–G6. Evitar recorrer la historia completa sin motivo.
3. Comprobar **el estado del gate y su evidencia**, contadores RCR-1 y las observaciones OPEN, sin suponer que un plan equivale a implementación.
4. Reanudar el **primer paso técnico pendiente y seguro** del gate; no pedir confirmación de aprobaciones anteriores ni limitarse a proponer otro plan.
5. Dejar **commit(s) y checkpoint actualizado** del trabajo terminado en PR #173, o un `BLOCKED` concreto con comandos/pruebas y la condición mínima de desbloqueo. **Nunca fusionar la PR de implementación automáticamente**.

## 2. G0 — controles contrastados y pendientes

| Control de G0 | Evidencia actual | Estado |
|---|---|---|
| Rama y punto de partida | main `27bd1996...`, PR #173 abierta, cambios solo en docs/scripts/workflows/ZIP, sin renderer Kotlin V11 | **VERIFICADO** (revalidar SHA al retomar) |
| Contrato V11 y golden | ZIP almacenado en rama, Actions run **38009729554** validó SHA `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a`, 1.520.026 bytes, 20 miembros y 8 PDFs | **VERIFICADO, R-01 cerrado** |
| Entorno de CI existente | último HEAD comprobado antes de este documento: `89af1b63411c...`; Scaffold checks run **38009865247** SUCCESS (jobs Kotlin, hosted-db, backend); QA ledger run **38009865273** SUCCESS; golden integrity run **38009865259** SUCCESS | **VERIFICADO como línea base, NO prueba V11 runtime** |
| QA histórica primaria textual | Se releyeron las observaciones originales documentadas en checkpoints `2026-09-26`, `2026-09-28` y matriz `M50800-01..32`. El propietario observó 45p/FAIL/28p/27p; el artefacto Worker Actions #10942589698 probó 29/18/16/15, de otro origen. | **Checkpoint original revisado**; **PDF runtime exacto aún no inspeccionado ni disponible** |
| Modelo de datos | `CharacterSheet.kt`, `PcSheetPdfExportFoundation.kt`, export services Android/Desktop revisados; primeras semánticas capturadas en `G0_MAPA_SEMANTICO_PRELIMINAR.md`. | **PARCIAL**: falta revisar definiciones de closure/successor y flujo UI real completo |
| Fuentes y marca gráfica | Barlow, Fira, Symbols-v8 y hojas originales identificadas en Git | **PARCIAL**: extracción/embed y compatibilidad real pendientes de G3 |
| QA y prevención de loops | `PROCEDIMIENTO_QA_SIN_PERDIDA.md`, JSON 32 M50800 + V11-QA-001/002, CI comprobado; RCR-1 aprobado | **Verificado como procedimiento**, no como prueba de exportación |

### Condición objetiva para G0 PASS y autorización automática de G1

**TODAS**: (a) matriz fuente→campo real/estado→destino V11→test completa; (b) originales de QA disponibles/inspeccionados hasta nivel necesario de regresión, sin sustituir por prueba Worker; (c) validar rutas UI reales y exportación Android/Desktop; (d) el estado de CI sigue verde en HEAD de trabajo y zip golden mantiene hash; (e) ninguna contradicción semántica material. Solo entonces registrar `G0 PASS` con SHA y evidencias, pasar a G1 sin pedir nuevamente permiso.

Cuando **falte un recurso externo no accesible** (p.ej. PDF runtime del propietario no conservado), registrar `BLOCKED` respecto de ESA evidencia, **sin gastar intentos**. Hacer otras comprobaciones read-only accesibles. No cambiar renderer mientras las reglas de AGENTS §6.3 exijan esa evidencia. Si el material original está documentado pero solo faltan bytes físicos, resolver conscientemente si alcanza para empezar **solo código aislado de mapeo G1 sin modificar las familias heredadas**; registrar la excepción explícita y reservar la aceptación final a un runtime real. Nunca fingir que se recuperaron PDFs.

## 3. Comienzo correcto de G1 (NO ejecutado aquí)

Secuencia en **la misma PR #173**, después de G0 PASS:
1. Test unitario rojo de cuatro familias inalteradas y opt-in V11 A/B (request + snapshot); trazar `PERMANENT`/`CURRENT_SNAPSHOT` y notices.
2. Añadir contrato semántico tipado V11 en **shared Kotlin**, preserve IDs/relaciones/estados; **sin** tocar el renderer legacy ni usar la maqueta Python en runtime.
3. Testear: 6 atributos + custom, skills relacionadas, `spells.isNotEmpty()` vs capacidad mágica, prepared **por fuente**, `spellSlots.totalSlots` vs `spentSlots`, moneda 0, `ACTIVO` papel vacío sin inferir `equipped/attuned`, notas generales/tituladas, Libro opcional y retrato ausente/Crop/Fit.
4. Ejecutar tests shared/CI y validador QA; registrar mapeo completo. Al **PASS G1** continuar G2: compositor determinista y fragmentación de textos extensos.

**No** crear otro prototipo V12, no reabrir estética, no dar `FIXED` sin diff de código productivo más nuevo PDF de candidato real. RCR-1 regula fallos repetidos.

## 4. Prompt de retoma mínimo para usuario

> Sigue el proyecto `MrSimkin/dnd_custom_aid` en GitHub. Recupera la ruta canónica desde `main` (`AGENTS.md` → `RESUME.md` → `docs/checkpoints/LATEST.md`) y continúa automáticamente la implementación de la Hoja de PJ V11 aprobada, usando el estado vigente de la PR #173 y sus procedimientos G0–G6, QA sin pérdida y recuperación RCR-1. Ejecuta el siguiente paso seguro, actualiza GitHub y entrega evidencia o BLOCKED preciso. No rediseñes, no repitas preguntas respondidas y no fusiones la PR de implementación sin mi aprobación.

Si la frase recibida es únicamente «sigue» **dentro de una sesión ya identificada con este repo**, interpretarla como este prompt. En un chat nuevo sin contexto, el usuario debe incluir al menos `MrSimkin/dnd_custom_aid` para desambiguar el proyecto.

## 5. Handoff

- **Rama única de implementación**: `docs/pc-sheet-v11-approved-implementation-handoff` / PR #173.
- **Sin merge** de PR #173, sin cambios runtime ni backend.
- **Trabajo inmediato seguro:** cerrar inventario de modelos closure/successor, entrada UI y recuperación QA primario; documentar G0 PASS cuando corresponda y comenzar G1.
- **No confundir** una PR independiente **solo de routing de reanudación** a `main` con la PR #173 de implementación: la primera no autoriza fusionar la segunda.
