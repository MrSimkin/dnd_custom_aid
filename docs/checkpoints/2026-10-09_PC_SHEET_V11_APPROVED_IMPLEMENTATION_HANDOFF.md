# Checkpoint — V11 visual aprobado / traspaso técnico de implementación

**Fecha:** 2026-10-09 (Chile)
**Rama:** `docs/pc-sheet-v11-approved-implementation-handoff`
**Base auditada de `main`:** `27bd199633f1068ca481c387973d38f35cbb0c48`
**Estado:** **V11 VISUAL APROBADO POR PROPIETARIO; G0 PRE-FLIGHT DOCUMENTAL PARCIAL; RENDERER V11 NO IMPLEMENTADO; PR NO FUSIONADA.**
**Autoridad:** esta revisión owner-led V8→V11 es más reciente que los checkpoints de diseño de 2026-10-08. Las decisiones §§3.1–3.48 de D-0076 no se reescriben; los cambios owner-expresos posteriores en esta lista especial V11 tienen precedencia en sus temas.

## Verdad durable

El propietario aprobó visualmente el paquete de maqueta V11, ambas variantes A/B y las páginas P1–P4, extensiones C2, Libro opcional y la última hoja Notas, con dos ajustes: mover ligeramente “Salvación” hacia arriba/derecha y que las extensiones aprovechen todos los huecos restantes mediante mini-cuadrículas NOTAS escritas a mano, sin crear páginas de relleno. No se autoriza una nueva ronda de diseño; sí la **implementación técnica** ahora solicitada por el propietario.

**Uso personal 100 %** y logo D&D de hoja propia ratificados; no volver a pedir autorización de branding salvo que el alcance cambie a distribución.

**No confundir aprobación visual con PASS de aplicación.** No hay compilación Kotlin V11, código productivo V11, APK V11 ni validación de PDFs reales de la App. El prototipo Python no es renderer autorizado.

## Orden de arranque sin chats ni memoria

1. `AGENTS.md` → `RESUME.md` → `docs/checkpoints/LATEST.md` → este checkpoint.
2. `docs/pc-sheet-v11/CONTRATO_VISUAL_APROBADO.md`: autoridad específica del diseño V11.
3. `docs/pc-sheet-v11/PREFLIGHT_TECNICO_Y_RIESGOS.md`: mapa de código validado, R-01..R-10 y estado G0..G6.
4. `docs/pc-sheet-v11/ARTEFACTOS_Y_PROCEDENCIA.md`: nombres y hash del golden; **el ZIP no está versionado** y debe recuperarse/committearse antes de declarar G0 PASS.
5. `docs/pc-sheet-v11/PROTOCOLO_RIESGOS_Y_RECUPERACION.md`: matriz ejecutable R-01..R-12 (detector, contención, prueba y STOP). El riesgo registrado NO pasa a resuelto por publicarse este protocolo.
6. `docs/pc-sheet-v11/PROTOCOLO_ROMPER_CICLO_Y_REPLANIFICAR.md`: recuperación RCR-1 autorizada: rewind seguro hasta dos gates, una alternativa y dos pasadas de recuperación; conserva historia, máximo dos recuperaciones globales.
7. `docs/pc-sheet-v11/G0_MAPA_SEMANTICO_PRELIMINAR.md`: primer análisis de campos reales, diferencia de Conjuros V11/legacy y ausencia de estado ACTIVO.
8. `docs/pc-sheet-v11/PLAN_TECNICO_IMPLEMENTACION_V11.md` y `docs/pc-sheet-v11/PROMPT_WORKER_IMPLEMENTAR_V11.md`: contrato de ejecución anti-loop; **una rama y una PR, hasta 3 pasadas normales/gate y recuperación estrictamente acotada RCR-1 si existe alternativa probada; BLOCKED/STOP cuando corresponda**.
9. Por gate de QA `AGENTS.md §6.3`: reabrir **evidencia original** de `docs/checkpoints/2026-09-26_PC_SHEET_MARA_PREQA8_PDF_VISUAL_QA_DEFECTS.md`, `docs/checkpoints/2026-09-28_PC_SHEET_MARA_OWNER_REQA_RUNTIME_FAIL.md`, `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md` y los PDFs reales originales, no solo auditorías secundarias.
10. Inspeccionar fuente real `PcSheetPdfExportFoundation.kt`, `AndroidPcSheetExportService.kt`, `DesktopPcSheetExportService.kt`, renderers y pruebas antes de modificar código.

## Gate presente

- **G0**: parcialmente revisado. Ya existe `G0_MAPA_SEMANTICO_PRELIMINAR.md` con evidencia de código y divergencia de Conjuros; RCR-1 fue autorizado y publicado, **sin gastar ninguna recuperación**. Fuente de verdad del contrato y paths de código registrados; faltan ZIP golden versionado/restituible, matriz campo-por-campo en datos reales, inspección artefactos QA originales exhaustiva y ambiente de compilación.
- **G1–G6**: sin ejecutar. **No avanzar a code renderer** fingiendo haber terminado G0.
- Prohibido: nueva maqueta V12/V13, excepciones silenciosas para overflow, degradar las cuatro familias existentes, eliminar texto/IDs para ahorrar páginas, aprobar por 1104 checks sintéticos, saltar el cotejo del APK instalado.

## Publicación / recuperación

Esta publicación en rama y PR conserva instrucciones en GitHub aunque se pierda el chat. Sin embargo, `main` **no redirigirá automáticamente** a este checkpoint hasta fusionar el PR; no declarar el traspaso canónico final cerrado sin esa integración documental. Mantener PR **sin merge** para revisión del propietario, conforme al plan.

**Siguiente única acción de Worker:** cerrar **G0 con evidencia primaria y custodia del golden**, después proceder G1→G6 según contrato o emitir STOP. No pedir al propietario decisiones ya dadas.
