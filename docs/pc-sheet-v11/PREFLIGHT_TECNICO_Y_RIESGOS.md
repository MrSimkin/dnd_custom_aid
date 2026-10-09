# V11 — Preflight técnico, mapa de código y riesgos

**Revisión independiente realizada:** 2026-10-09. **Main auditado:** `27bd199633f1068ca481c387973d38f35cbb0c48`.
**Estado de ejecución:** **G0 PARCIAL / G1–G6 NO EJECUTADOS.** Esta rama publica contrato y encargo, **no** contiene aún renderer V11 en producción.
**Autoridad de aceptación:** `CONTRATO_VISUAL_APROBADO.md`, QA primario y datos reales, nunca únicamente el reporte de pruebas sintéticas.

## Código real identificado

| Capacidad existente | Ruta comprobada | Efecto sobre V11 |
|---|---|---|
| Familias y request | `shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/PcSheetPdfExportFoundation.kt` | `PcSheetVisualFamily` tiene cuatro familias, **no V11**; `PcSheetPdfExportRequest` no lleva variante A/B. Cambio futuro explícito, con compatibilidad de los cuatro valores. |
| Selección Permanente / Actual | mismo `PcSheetPdfExportFoundation.kt` | `PcSheetExportSources`, `PcSheetPdfExportPlanner.plan()` y `PcSheetExportSnapshot` ya existen; reutilizar, no duplicar modelos. |
| Android entrada a PDF | `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetExportService.kt` | Opciones, staging/export y render vía `AndroidPcSheetRendererBridge.kt` + `AndroidPcSheetWholeDraftRenderer.kt`. No confundir con Desktop. |
| Desktop entrada a PDF | `desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetExportService.kt` | Reutiliza planner y `DesktopPcSheetWholeDraftRenderer.kt`. Save/Share deben conservar idénticos bytes de un mismo render. |
| Motor Android | `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/` | Tiene renderers de las familias existentes y primitivas Android; **no hay compositor V11 probado**. |
| Motor Desktop | `desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/` | Motor PDFBox/primitivas Desktop, diferente backend gráfico de Android. Compartir semántica y decisión de paginado en shared siempre que permita los tamaños medidos verificables. |
| Tipografías | `assets/fonts/pdf/text/BarlowCondensed-Bold.ttf`, `FiraSans-Regular.ttf`, `FiraSans-SemiBold.ttf`, licencias; `assets/fonts/owner/para-hoja-de-pj/v8/Para Hoja de PJ Symbols v8.ttf` | **Archivos verificados en árbol remoto**. Falta comprobar empaquetado/incrustación en el artefacto V11 de Android y Desktop. |
| Fuentes de hoja original/logo | `assets/character-sheets/templates/Hoja de PJ - 5.0 - Simkin.pdf` y `Hoja de PJ v2 - 5.0 - Simkin.pdf` | Originales presentes. Aún NO existe extracción/normalización/logo V11 comprobado. |
| QA heredado | `desktopApp/src/test/.../DesktopPcSheetRuntimeQaFixtureTest.kt` y tests por familia; `shared/src/commonTest/.../PcSheetPdfExportPlannerTest.kt` | Reaprovechar sin rehacer y añadir V11; nunca dar PASS por fixture/sintético solamente. |

## Riesgos activos y tratamiento obligatorio

| ID | Criticidad | Estado | Evidencia concreta | Resolución y criterio de cierre |
|---|---|---|---|---|
| R-01: golden V11 ausente del Git | **ALTA / preservación** | **OPEN** | ZIP local 1 520 026 bytes SHA-256 documentado; no objeto Git | Guardar ZIP íntegro en repo/almacenamiento durable con hash comprobado y referencia versionada. Cerrar solo tras lectura independiente. |
| R-02: datos reales/semántica | **CRÍTICA** | **OPEN** | `PcSheetPdfExportRequest` no tiene V11 ni variante; fixture Python es inventado | Trazar cada campo desde `CharacterSheet` / closure/successor/spells/notes, estado nulo vs cero, slots, ACTIVO, imágenes y permanencia/actual; pruebas de fuente→PDF para todos los IDs. |
| R-03: overflow no total | **CRÍTICA** | **OPEN** | V11 Python aborta cuando una ficha Libro excede columna y en otros casos de overflow; no portarlo | Fragmentación exacta de texto/límites, progreso monótono, corte tipográfico seguro, pruebas extremas sin pérdidas o hang. |
| R-04: doble backend | **ALTA** | **OPEN** | `AndroidPcSheetWholeDraftRenderer` y `DesktopPcSheetWholeDraftRenderer` diferentes | Un plan geométrico/paginación compartido; renderizadores delgados con mismas coordenadas y caracteres; paridad medible, no solo counts. |
| R-05: regresiones históricas | **CRÍTICA** | **OPEN** | Re-QA Mara Fantasy 45p; Custom v1 falla; v2 28/27 pese a cierre previo | Reabrir defectos originales y artefactos reales, generar Mara en 4 familias legadas y V11, verificar contra candidato exacto. Cero fallos y sin cambios inadvertidos a legados. |
| R-06: recursos tipográficos/logo | **MEDIA-ALTA** | **PARCIAL** | TTF Barlow, Fira y Symbols v8 y PDF originales presentes | Medición real + embed, caracteres Ñ/acentos/símbolos y recorte de logo del asset, verificar ambos empaquetados; no inferir que estar en Git = renderizar bien. |
| R-07: validación de binario instalado | **ALTA** | **OPEN** | No hay APK candidato V11 ni PDFs de runtime | Nueva versión/versionCode, SHA256, CI/commit/artifact exactos, export Android real + Desktop, inspección independiente páginas/geometría. |
| R-08: bucle interminable | **ALTA** | **CONTROL DOCUMENTAL** | Plan limita a 1 implementación + 2 reparaciones por gate | Mantener una rama y una PR, métricas/evidencia por intento, STOP al 3.er intento fallido. No reemplazar bloqueantes por ideas opcionales. |
| R-09: pérdida de ruta futura | **ALTA** | **PARCIAL** | `main` aún dice fase documentación | Crear checkpoint V11 y pointer `LATEST.md` coherentes **en la rama**; hasta fusionar el PR, main NO retomará V11 automáticamente. No afirmar lo contrario. |
| R-10: coste/permisos | **BAJA** | **CONTROLADA** | Export local, logo para uso privado 100 % personal ya decidido | No pedir de nuevo permiso o dirección estética; no nuevos proveedores, nube, costes ni despliegues. |

**Procedimiento operativo de prevención, detección, recuperación, evidencia y STOP para R-01..R-12:** [PROTOCOLO_RIESGOS_Y_RECUPERACION.md](PROTOCOLO_RIESGOS_Y_RECUPERACION.md). Este documento no acredita PASS de ningún riesgo abierto.

## Puertas y presupuestos de intentos

`G0=PARCIAL`; `G1=NO INICIADO`; `G2=NO INICIADO`; `G3=NO INICIADO`; `G4=NO INICIADO`; `G5=NO INICIADO`; `G6=NO INICIADO`.

1. G0: revisar **QA original** (no solo sus síntesis), PDF real y diseños V11 A/B, confirmar recursos y **recuperación del ZIP golden**; mapa `source→module→test` antes de tocar renderer.
2. G1: integrar tipos/mapper V11 y tests de casos reales. **STOP** si semántica requerida no existe.
3. G2: compositor con crecimiento y progreso demostrable, repacking/NOTAS, referencias físicas y texto íntegro.
4. G3: fuentes y logo en ambos backends, selector A/B opt-in y export local funcional.
5. G4: regresión Mara en cuatro familias + V11; inspectores de PDFs independientes.
6. G5: APK/artefacto versionados y correlación real runtime/CI.
7. G6: una PR revisable y paquete de evidencia; **NO MERGE** sin autorización.

Por gate: pasada inicial + **máximo dos reparaciones**. Cada reparación necesita diagnóstico causal, prueba que falle antes y pase después. Si persiste cualquier criterio bloqueante: **STOP** con reporte e instrucciones mínimas, sin V12/V13 ni más branches.

## Precisión del presente preflight

Esta revisión se basó en lectura GitHub de código/documentos y revisión del ZIP original en el entorno del asistente. **No** se clonó/buildió el repo localmente (la conexión de red del contenedor a GitHub no resolvió DNS), ni se ejecutó Gradle, CI o prueba runtime. Todo G1–G6 permanece PENDIENTE. No usar este registro como acreditación de implementación.
