# Plantilla operativa obligatoria — una ronda QA, un plan, una demostración del resultado

**Uso:** el Worker copia este archivo para cada QA real a `qa/rondas/<QA-ID>.md` y actualiza el JSON maestro. **El propietario NO debe completar documentos:** basta con que diga sus observaciones normalmente y pruebe el candidato. Ninguna sección puede declararse «PASS» por ser un plan escrito.

## 0. Identidad del QA (antes de modificar código)
- ID de ronda, fecha, propietario, canal/source_ref:
- Versión de App (versionName / versionCode), commit, run CI / artifact ID:
- APK SHA-256 y firma/identidad de compilación:
- PDFs reales originales: nombre, SHA-256, cantidad de páginas y páginas afectadas; screenshot si existe:
- Entorno de ejecución: Android físico/emulador/desktop, modo de exportación y fixture (ej. Mara):
- Disponibilidad: `PRESENTE` / `NO DISPONIBLE` (sin suplir silenciosamente).

## 1. Recepción completa e inmutable
Copiar literalmente todos los comentarios numerados del propietario, **incluido «no veo nuevos errores»**, con ID asignado `V11-QA-###`, página/caso y referencia original. Añadir cada uno al JSON; no sustituir entradas antiguas. Si dos frases se refieren a lo mismo, enlazar IDs sin eliminar ninguna.

| ID | Cita literal | Artefacto/Página | Defecto observado | Esperado | Estado |
|---|---|---|---|---|---|
| ... | ... | ... | ... | ... | OPEN |

**Control de completitud:** cantidad comentarios originales = cantidad entradas registradas (+ comentarios solo de aprobación sin defecto, también preservados). Diferencia implica `BLOCKED`.

## 2. Plan de corrección ANTES del primer cambio de código
| IDs vinculados | Causa reproducible / primera divergencia | Ruta REAL del renderer/UI | Fuente/ejemplo nativo | Prueba roja + comando | Resultado aceptable medible | Impacto/Regresión | Gate y RCR |
|---|---|---|---|---|---|---|---|
| ... | ... | ... | ... | ... | ... | ... | ... |

**Cierre diagnóstico:** no hay observaciones del lote omitidas; tests de legado y V11 definidos. Si algo es verdaderamente material y ambiguo, una sola consulta consolidada; no mover criterio de éxito durante el arreglo.

## 3. Ejecución efectiva
| ID | Commit que cambió código/recurso productivo | Archivos y diff | Intento 1/3–3/3 o RCR-1 | Resultado rojo | Resultado verde | Estado |
|---|---|---|---|---|---|---|
| ... | ... | ... | ... | ... | ... | IMPLEMENTED_UNVERIFIED |

**Regla de verdad:** sin diff productivo verificable no existe «IMPLEMENTED». Si se cambia solo el test, sigue `TRIAGED`.

## 4. Prueba sobre nuevo candidato REAL (no mockup)
- SHA commit V11 compilado / run / artifact:
- APK versión y SHA (distintos si cambió materialmente el binario):
- Versión/candidato Desktop y SHA:
- PDF del Android instalado, SHA, páginas concretas y origen (`ANDROID_INSTALLED_APP`):
- PDF generado por Desktop UI, SHA, páginas concretas (`DESKTOP_UI_EXPORT`):
- Oráculo independiente de geometría/textos/IDs:
- Matriz de 4 familias antiguas + V11 A/B y fixture Mara: PASS/OPEN por caso, evidencias:
- Manifiesto de 0 IDs omitidos/duplicados (o detalle de fallo):

| ID original | Antes (página y evidencia) | Después (PDF real, SHA, página y evidencia) | Diferencia específica comprobada | Inspección independiente | Estado |
|---|---|---|---|---|---|
| ... | ... | ... | ... | ... | CANDIDATE_VERIFIED / OPEN |

**Regla crítica:** si la página se ve igual, no afirmar «fixed». Primero verificar identidad APK/código/fixture. Marcar `BLOCKED` o `OPEN`. Cualquier PDF de un commit antiguo invalida `CANDIDATE_VERIFIED` para el nuevo candidato.

## 5. Entrega y resultado propietario
- Entregar **una sola versión** identificada, instrucciones normales de instalación/selección, qué cambió por ID y qué permanece abierto.
- Si necesita recuperar todos los PDF de `Download`, aplicar pull completo de `AGENTS.md §6.1`, no exigir archivos individuales ni que el usuario haga hashes.
- Cita literal de aprobación/rechazo del propietario **asociada al candidato exacto**:
- `OWNER_ACCEPTED` solo con aceptación explícita; si la observación persiste, nuevo evento `OPEN` con ID original, nuevo incidente enlazado y causa reproducible.
- Actualizar `REGISTRO_QA_V11.json`, checkpoint/LATEST cuando cambien estado/ruta, contador de intentos y RCR, y PR.
- **Resultado del lote:** `READY_FOR_OWNER_QA` / `OWNER_ACCEPTED` / `BLOCKED` / `STOP`. Registro completado, ninguna observación perdida.

La revisión visual de maqueta V11 permanece aprobada; este documento gobierna **solo la prueba del producto real**.
