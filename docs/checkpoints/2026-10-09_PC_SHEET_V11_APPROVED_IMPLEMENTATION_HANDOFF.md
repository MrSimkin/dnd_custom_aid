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
4. `docs/pc-sheet-v11/ARTEFACTOS_Y_PROCEDENCIA.md`: golden ZIP **ya versionado y verificado en Git** mediante Actions #38009729554; preservar hash, PDF, procedencia.
5. `docs/pc-sheet-v11/PROTOCOLO_RIESGOS_Y_RECUPERACION.md`: matriz ejecutable R-01..R-12 (detector, contención, prueba y STOP). El riesgo registrado NO pasa a resuelto por publicarse este protocolo.
6. `docs/pc-sheet-v11/PROTOCOLO_ROMPER_CICLO_Y_REPLANIFICAR.md`: recuperación RCR-1 autorizada: rewind seguro hasta dos gates, una alternativa y dos pasadas de recuperación; conserva historia, máximo dos recuperaciones globales.
7. `docs/pc-sheet-v11/G0_MAPA_SEMANTICO_PRELIMINAR.md`: primer análisis de campos reales, diferencia de Conjuros V11/legacy y ausencia de estado ACTIVO.
8. `docs/pc-sheet-v11/PLAN_TECNICO_IMPLEMENTACION_V11.md` y `docs/pc-sheet-v11/PROMPT_WORKER_IMPLEMENTAR_V11.md`: contrato de ejecución anti-loop; **una rama y una PR, hasta 3 pasadas normales/gate y recuperación estrictamente acotada RCR-1 si existe alternativa probada; BLOCKED/STOP cuando corresponda**.
9. Por gate de QA `AGENTS.md §6.3`: reabrir **evidencia original** de `docs/checkpoints/2026-09-26_PC_SHEET_MARA_PREQA8_PDF_VISUAL_QA_DEFECTS.md`, `docs/checkpoints/2026-09-28_PC_SHEET_MARA_OWNER_REQA_RUNTIME_FAIL.md`, `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md` y los PDFs reales originales, no solo auditorías secundarias.
10. Inspeccionar fuente real `PcSheetPdfExportFoundation.kt`, `AndroidPcSheetExportService.kt`, `DesktopPcSheetExportService.kt`, renderers y pruebas antes de modificar código.

## QA anti-pérdida autorizado y publicado

Tras el reporte del propietario sobre QA cíclica sin corrección efectiva, se fijó el flujo `Q0–Q7` de `docs/pc-sheet-v11/PROCEDIMIENTO_QA_SIN_PERDIDA.md`. El ledger `docs/pc-sheet-v11/qa/REGISTRO_QA_V11.json` conserva literalidad de V11-QA-001/002 y referencias a los 32 M50800; `scripts/check_v11_qa_registry.py` rechaza omisiones, falsos cierres sin cambio productivo y pruebas de APK obsoletas; la workflow `.github/workflows/pc-sheet-v11-qa-ledger.yml` ejecuta los controles y `docs/pc-sheet-v11/qa/PLANTILLA_CIERRE_POR_RONDA.md` obliga a plan, cambio de código, antes/después, QA independiente y propietario (su resultado CI debe consultarse; su existencia no es PASS). Cada QA futuro debe producir plan Q2, diff de código, artefacto nuevo y cotejo de todas las observaciones antes de solicitar una nueva revisión. G0 sigue PARCIAL, no existe implementación V11.

## Hito de preservación cerrado — R-01

**R-01 VERIFIED / GOLDEN DURABLE (2026-10-09):** ZIP original archivado en `docs/pc-sheet-v11/dnd_custom_aid_V11_Carta_revision.zip` sobre esta rama; SHA-256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a`; 1.520.026 bytes; SHA de objeto Git `99ed18a2c59b71440cb8b96fafea5d8df1832bd6`. GitHub Actions `PC Sheet V11 golden archive integrity` run [38009729554](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38009729554), commit `b7ca78bac9d7d884f6ab37be809685cd59a0d5fc`: comprobación SHA-256 **sobre bytes recuperados mediante checkout**, ZIP testzip PASS, 20 entradas y 8 PDFs. Esto cierra SOLO preservación R-01; G0 sigue PARCIAL y renderer/QA APK no iniciado.

## Continuidad entre chats — orden «sigue»

**Desde main** ya existe la ruta documental de reanudación (PR #174, merge `0c009f10d5e66880d68fa3732491c6fc3e172f22`) que apunta a esta PR **sin fusionar implementación**. El checkpoint canónico main es `docs/checkpoints/2026-10-09_PC_SHEET_V11_IMPLEMENTATION_RESUME_ROUTE.md`. Al recibir «sigue» con repositorio identificado: seguir `AGENTS → RESUME → LATEST → checkpoint canónico de main → esta rama PR #173 HEAD actual → docs/pc-sheet-v11/COMANDO_SIGUE_Y_G1_HANDOFF.md`. La guía nueva contiene el primer paso seguro de G0, G1 y las observaciones sin pérdida; cualquier implementación debe registrar commit, evidencia y nuevo estado. No revivir el diseño 2026-10-08 de main ni iniciar rama de producto paralela.

## Gate presente

- **G0**: parcialmente revisado. Ya existe `G0_MAPA_SEMANTICO_PRELIMINAR.md` con evidencia de código y divergencia de Conjuros; RCR-1 fue autorizado y publicado, **sin gastar ninguna recuperación**. Fuente de verdad del contrato y paths de código registrados; ZIP golden **VERIFICADO / R-01 RESUELTO**; faltan matriz campo-por-campo completa en datos reales, inspección exhaustiva de QA original de Mara y ambiente de compilación/ejecución.
- **G1–G6**: sin ejecutar. **No avanzar a code renderer** fingiendo haber terminado G0.
- Prohibido: nueva maqueta V12/V13, excepciones silenciosas para overflow, degradar las cuatro familias existentes, eliminar texto/IDs para ahorrar páginas, aprobar por 1104 checks sintéticos, saltar el cotejo del APK instalado.

## Publicación / recuperación

Esta publicación en rama y PR conserva instrucciones en GitHub aunque se pierda el chat. Sin embargo, `main` **no redirigirá automáticamente** a este checkpoint hasta fusionar el PR; no declarar el traspaso canónico final cerrado sin esa integración documental. Mantener PR **sin merge** para revisión del propietario, conforme al plan.

**Siguiente acción Worker:** R-01 archivado, NO volver a pedir ZIP. Completar **G0 con evidencia primaria original de Mara, mapa semántico y preflight de compilación**, luego G1→G6 o BLOCKED con evidencia. No reabrir estética ni pedir decisiones dadas. No pedir al propietario decisiones ya dadas.


## Verificación de continuación — 2026-10-11 (sin G0 PASS)

Se verificó el HEAD remoto de `main` (`0c009f10d5e66880d68fa3732491c6fc3e172f22`), la autoridad de PR #173 y el código real de `CharacterSheet`, `CharacterClosureDomain`, `CharacterSuccessorDomain`, planner shared y selectores/servicios Android + Desktop. Hallazgos y pruebas a exigir: [`G0_VERIFICACION_MODELOS_UI_2026-10-11.md`](G0_VERIFICACION_MODELOS_UI_2026-10-11.md). Corrige el estado obsoleto de R-09: **`main` YA enruta V11** mediante PR documental #174, pero PR de implementación #173 sigue separada, DRAFT y sin merge.

CI en HEAD de lectura de rama `62ac252297583b22db639952dd120354e4aeb150`: Scaffold run `38010867507` SUCCESS; ledger QA run `38010867541` SUCCESS. Estos checks no significan APK V11, PDFs de propietario o aprobación G0. Ausentes los bytes originales de Fantasy/Custom-v2 A/B y captura Custom-v1 del runtime 50800; solo quedan sus hashes/reportes en los checkpoints. Git local falló por DNS, por lo que Gradle/Android/Desktop no fue ejecutado en este entorno. **G0 NO PASS (dependencias de QA primaria y compilación bloqueadas); G1–G6 NO INICIADOS**. QA 32 M50800 y V11-QA-001/002 sin cierre, RCR-1 0/2 sin intentos. La PR #173 no se fusionó.

## Avance autónomo G1 aislado — 2026-10-11

Por autorización expresa del propietario se deja de usar la **falta de PDFs históricos Mara** como prohibición global para escribir el **contrato semántico aislado** de V11. **No se concede G0 PASS**: la inspección directa de los PDFs/captura 50800 sigue pendiente y es precondición de la aceptación final contra runtime. No tocar renderer heredado ni atribuir FIXED a sus observaciones.

Commit funcional `97b4e8f8ecec9465bea55fabc15fcffc5a658195` (PR #173): nuevo `shared/.../PcSheetV11SemanticContract.kt` con request **V11 A/B opt-in independiente**, proyección tipada sobre `CharacterSheet+Closure+Successor`, selección snapshot/avisos/retrato reusando semántica shared sin aplicar plan visual Classic, 6 atributos y custom enlazados por ID, habilidades, ranuras separadas, conjuros preparados **por fuente**, monedas con cero, notas digitales separadas, equipo especial ACTIVO desconocido/desmarcado. `PcSheetPdfExportFoundation.kt` solo expone `spellbookPlan` como `internal` para reusar asociación de fuentes; **su comportamiento y los cuatro renderers legacy no cambiaron**. Prueba nueva `PcSheetV11SemanticContractTest.kt` cubre A/B, familias legacy intactas, estado permanente/actual/fallback, relaciones custom faltantes, per-source prepared, P4 solo conjuros reales, Libro opcional, moneda cero, notas y retrato ausente.

**Estado:** G0 PARCIAL (bloqueo QA original acotado); subtrabajo técnico G1 **EN PROGRESO por excepción**; G1 no puede recibir PASS ni G2 iniciarse sin verificar la suite CI y completar el inventario de semántica/restantes pruebas. CI de `97b4e8f` debe consultarse desde GitHub, **no inferir SUCCESS** por commit. QA V11-QA-001/002 OPEN, 32 M50800 preservados, RCR-1 0/2 y PR no fusionada. Próximo: verificar CI Kotlin y reparar únicamente fallos reales, completar cobertura G1 sin rediseño y conservar frontera QA Mara para G4–G5.

## G1: pruebas con respaldo real y ledger semántico (2026-10-11)

Se confirmó **SUCCESS** del Scaffold Kotlin/general del primer código V11 (commit `97b4e8f8ecec9465bea55fabc15fcffc5a658195`, run `38168641345`) y del HEAD documental posterior (`d909bee4ed4b8ee169ab2178e2cc7aaad3c9e24d`, run `38168898381`). Ambos solo validan Kotlin/tests existentes y **NO constituyen PASS de PDF V11**.

El siguiente commit funcional `38fbb0066e9054982ce87fa9dcb799a7693abfee` incorpora `PcSheetV11SemanticExtensions.kt` (`extensionData()` + `inputRecords()` UUID por tipo, SIN apariciones físicas inventadas) y un test nuevo sobre **los respaldos reales versionados Aldren, Ilyra y Mara**, cada uno exportado semánticamente en A/B. Comprueba IDs de clases/atributos/habilidades/custom/equipo/rasgos/conjuros/notas/recursos, asociación preparada por fuente y texto del conjuro; no genera PDF ni corrige legacy. Descripción de cobertura, semántica y condición de cierre: `docs/pc-sheet-v11/G1_CONTRATO_SEMANTICO_Y_FUENTES_2026-10-11.md`.

**Estado al registrar este checkpoint:** QA traceability run `38169942683` SUCCESS para el commit Kotlin citado; Scaffold Kotlin run `38169942666` aún sin conclusión verificada. **G1 IN_PROGRESS / NO PASS hasta confirmar el resultado de esa compilación y pruebas**; G0 histórico sigue PARCIAL; G2–G6 NO INICIADOS. Preservar 32 M50800, V11-QA-001/002 OPEN y RCR-1 0/2. No pedir los PDF antiguos como condición de G1 aislado; sí usarlos para aceptación G4–G5 de un candidato real.

## G1 semántica verificada y G2 iniciado — 2026-10-11

**G1 SEMANTIC PASS, delimitado al contrato shared:** Scaffold/Gradle completo `38170199248` **SUCCESS** y QA ledger `38170199195` **SUCCESS** en commit `4acc1a82e8251e40ee7f8b721429946dfe827d5c`. Los tests incluyen fuente verdadera Aldren/Ilyra/Mara A/B e IDs/todos los campos enumerados en `docs/pc-sheet-v11/G1_CONTRATO_SEMANTICO_Y_FUENTES_2026-10-11.md`. No demostrar PDF real ni G0 histórico mediante CI.

**G2 IN_PROGRESS (primer lote Kotlin, sin PASS):** commit `a5eb0e45a03ab7f968c0f004dcaa34491687e2d2` agrega partición lossless de líneas/texto con interfaz de medición, offsets fuente y continuaciones; commit `014f0e9ee8e9806dc4bf4e007c342b7bde29e917` conecta secciones con datos verdaderos y test sobre Mara. Pormenores, cobertura y límites en `docs/pc-sheet-v11/G1_PASS_Y_G2_PRIMER_FLUJO_2026-10-11.md`. **Verificar todavía CI sobre el HEAD posterior de G2**: no documentar PASS de G2, no inferir página física final/font embebida o compatibilidad de renderer. G0 QA histórico PARCIAL, 32 M50800 + V11-QA-001/002 OPEN, RCR-1 0/2; PR #173 DRAFT/sin merge. Siguiente: pruebas G2, luego compositor páginas físicas + tablas + geometría real.

## G2 secuencia de páginas — 2026-10-11, pendiente CI

Commit `ce3bf100104dda99e5ca29ce4493507ad1e80df6` añade un contrato Kotlin de orden **P1/P2/P3/P4 condicional/C2/C-N3/Libro/Notas final** con números de página contiguos y test de no-lanzador, extensiones y exclusividad digital/final. No deduce cantidades ficticias: un compositor medido debe aportar el número real de páginas adicionales. Este es un bloque lógico previo a la geometría/renderer, **no PDF generado** ni G2 PASS. Evidencia detallada en `docs/pc-sheet-v11/G1_PASS_Y_G2_PRIMER_FLUJO_2026-10-11.md`. Verificar Scaffold CI del commit `ce3bf10`. QA histórica de Mara sigue pendiente; no gastar RCR-1 ni fusionar PR.

## G2 siguiente lote — contrato geométrico de celdas (2026-10-11)

Commit `1109a18fda135f72338abcbacb69f7bbc7ec4f6b` añade `PcSheetV11MeasuredFlowPlacer.kt`, celdas Carta 612×792 pt, 1/2/3 columnas, líneas en coordenadas PDF y validación anticorte/colisión; añade `startLineIndexInColumn` al flujo existente para impedir que dos bloques consecutivos se dibujen sobre el mismo origen de columna; más 6 tests. Se verificó la ruta real PDFBox Desktop/Android generada **sin editar renderers legacy**. No existen aún fuentes reales medidas ni PDF V11. Evidencia detallada: `docs/pc-sheet-v11/G2_GEOMETRIA_CARTA_LINEAS_2026-10-11.md`.

**CI confirmado anterior:** commit `616f70b8f9ed0ec76815c9765a5b152bb9a0a5b7` Scaffold `38173199042` **SUCCESS**, QA `38173199107` **SUCCESS**. **CI del nuevo commit:** QA ledger `38174780469` SUCCESS; Kotlin Scaffold `38174780447` **IN_PROGRESS a la última revisión, revalidar antes de declarar aprobado**. Mantener G1 semántico PASS, G0 QA histórico PARCIAL, G2 IN_PROGRESS/NO PASS, 32 M50800 + V11-QA-001/002 OPEN, RCR-1 0/2, PR #173 OPEN DRAFT sin merge. Próximo: cerrar resultado Kotlin de esta rebanada y conectar medición de fuentes verdaderas/módulos, sin inventar medidas de diseño.
