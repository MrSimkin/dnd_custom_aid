# V11 — Comando «sigue» y preparación de G1

**Main routing integrado:** PR documental [#174](https://github.com/MrSimkin/dnd_custom_aid/pull/174) **MERGED** mediante commit de main `0c009f10d5e66880d68fa3732491c6fc3e172f22`. Main ahora apunta al checkpoint `docs/checkpoints/2026-10-09_PC_SHEET_V11_IMPLEMENTATION_RESUME_ROUTE.md` y desde allí a esta rama. Este merge fue solo documental; **PR #173 permanece abierta y sin fusionar**. Antes de la integración futura de #173, conciliar los cambios de ruta de `main` sin revivir el viejo design-only checkpoint.

**Actualización 2026-10-11 (vigente):** PR #173 **DRAFT y sin fusionar**; main `0c009f10...`. **G0 QA histórico PARCIAL; G1 contrato semántico PASS** (CI Scaffold `38170199248`, QA registry `38170199195`, HEAD `4acc1a82`), bajo excepción owner expresa que no exige PDF antiguo para código aislado. **G2 IN_PROGRESS** en commits `a5eb0e4` y `014f0e9`: paginador text-flow en Kotlin shared + mapeo por sección real de Mara; **G2 sin PASS**, no PDF ni render/UI V11. Verificar CI de los commits G2 antes de avanzar. G3–G6 no iniciados; regresión contra PDF original queda para G4–G5.

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

## 3. G1: contrato semántico aislado iniciado; PASS pendiente

La primera rebanada de mapeo se ejecutó **en la misma PR #173** por excepción explícita, sin declarar G0 PASS ni tocar renderers. Continúa esta secuencia antes de cerrar G1:
1. **CÓDIGO INICIADO**: tests shared verifican cuatro familias intactas, opt-in V11 A/B (request + snapshot), `PERMANENT`/`CURRENT_SNAPSHOT` y notices. Consultar CI para confirmar ejecución; no declarar test PASS sin Actions.
2. **CÓDIGO INICIADO**: contrato semántico tipado V11 en **shared Kotlin**, preserva IDs/relaciones/estados y agregado origen; **sin** tocar el renderer legacy ni usar Python en runtime.
3. Testear: 6 atributos + custom, skills relacionadas, `spells.isNotEmpty()` vs capacidad mágica, prepared **por fuente**, `spellSlots.totalSlots` vs `spentSlots`, moneda 0, `ACTIVO` papel vacío sin inferir `equipped/attuned`, notas generales/tituladas, Libro opcional y retrato ausente/Crop/Fit.
4. Ejecutar tests shared/CI y validador QA; registrar mapeo completo. Al **PASS G1** continuar G2: compositor determinista y fragmentación de textos extensos.

**No** crear otro prototipo V12, no reabrir estética, no dar `FIXED` sin diff de código productivo más nuevo PDF de candidato real. RCR-1 regula fallos repetidos.

## Avance G1 — 2026-10-11

- `97b4e8f` añadió contrato semántico opt-in A/B y tests unitarios shared; **Scaffold SUCCESS** en run `38168641345`.
- `38fbb006` añadió extensión semántica y ledger UUID por tipo, con prueba de integración sobre respaldos reales **Aldren, Ilyra y Mara** en ambas variantes; **Scaffold run `38169942666` debe verificarse** antes de marcar G1 PASS; QA registry `38169942683` SUCCESS.
- Matriz real por campo→destino→prueba: [`G1_CONTRATO_SEMANTICO_Y_FUENTES_2026-10-11.md`](G1_CONTRATO_SEMANTICO_Y_FUENTES_2026-10-11.md). Los tests no inspeccionan ni sustituyen PDFs históricos owner.
- **G0 QA histórico PARCIAL / G1 IN_PROGRESS; G2–G6 sin comenzar.** Próximo paso: verificar y corregir fallos reproducibles del Kotlin CI del código G1; si PASS actualizar gate con SHA + suite exacta, avanzar únicamente al primer trabajo seguro G2 sin tocar renderer legacy.

## G1 cerrado / G2 activo — último alcance\n\n- Documento detallado: [`G1_PASS_Y_G2_PRIMER_FLUJO_2026-10-11.md`](G1_PASS_Y_G2_PRIMER_FLUJO_2026-10-11.md). G1 es **PASS solo semántico** contra backups originales y CI del commit exacto; no aceptación del PDF.\n- G2: compositor de texto inicial y flujos de narrativas/notas/rasgos/equipo especial/Libro, no renderer PDF. Primero confirmar **Scaffold** para nuevo G2 y corregir errores; luego desarrollar geometría de módulos, tres/dos columnas, páginas físicas, encabezados y referencias; sin tocar las cuatro familias legacy.\n- **G0 QA histórica sigue PARCIAL**, el golden V11 está custodiado y las QA 50800 no se consideran FIXED.\n\n## G2 coordenadas PDF y anticollisión — nuevo avance 2026-10-11

- Commit `1109a18`: `PcSheetV11TextFlow.startLineIndexInColumn` elimina el riesgo de solapar dos bloques contiguos, y `PcSheetV11MeasuredFlowPlacer.kt` reserva celdas de texto Carta PDF en 2 o 3 columnas sin salirse de los márgenes definidos por el llamador. Unit tests nuevos de coordenadas, repetición, letras largas, saltos y guardas.
- Referencia: [`G2_GEOMETRIA_CARTA_LINEAS_2026-10-11.md`](G2_GEOMETRIA_CARTA_LINEAS_2026-10-11.md). QA ledger `38174780469` SUCCESS; **Scaffold `38174780447` pendiente de comprobar/terminar**. No afirmar geometría visual final, fuente real integrada, V11 PDF generado ni G2 PASS.
- **Siguiente tarea:** confirmar estado de Scaffold y corregir cualquier fallo demostrado; luego conectar un medidor PDFBox/fonts real y los módulos de página, sin tocar renderers heredados. QA Mara histórica aún G0 parcial, RCR-1 0/2, PR #173 sin merge.

## G2 tipografía real — avance 2026-10-11

- Commit `4414b27`: adapters de medición con Fira Sans y Barlow Condensed de los assets originales para ambas plataformas. Métrica horizontal desde `getStringWidth` real PDFBox y vertical desde su descriptor, test JVM sobre Mara y campos largos. No se cambió ningún renderer legacy ni se generó PDF V11.
- Estado: G1 PASS, G2 IN_PROGRESS; la fuente real está preparada pero **Scaffold run `38175718653` aún requiere resultado**, QA ledger `38175718666` SUCCESS. [Detalle `G2_METRICAS_TIPOGRAFICAS_REALES_2026-10-11.md`](G2_METRICAS_TIPOGRAFICAS_REALES_2026-10-11.md).
- Siguiente trabajo técnico seguro: revisar CI exacto; corregir errores verificados, luego incorporar las mediciones a la composición física P1/P2/P3/P4/C2/C-N3/Libro/Notas sin sustituir el diseño V11 ni tocar el renderer antiguo. Pruebas con fuente real **no** equivalen a PDF V11 final inspeccionado.

## G2 Equipo/Tesoro y Especial — avance 2026-10-11

- Commits `524020f` + `ed72c46`: nuevo par de subcolumnas objeto/cantidad y Tesoro (moneda cero real, IDs, continuidad sin duplicar cantidad) + Equipo Especial a ancho completo con marca ACTIVO desconocida sin inferencias. Tests shared de 386 filas, originales largos y pruebas Desktop sobre **Mara** (29 normales, 5 monedas, 5 especiales) usando fuente Fira verdadera.
- Detalle: [`G2_MODULOS_P2_EQUIPO_TESORO_ESPECIAL_2026-10-11.md`](G2_MODULOS_P2_EQUIPO_TESORO_ESPECIAL_2026-10-11.md). G2 **NO PASS**: `Scaffold checks` de HEAD `38176858258` requiere verificación final; QA registry `38176858259` SUCCESS.
- Próximo trabajo: CI, fallo concreto si existe, luego compositor físico P2/C2 y V11 PDF autónomo. No fusionar PR #173; no declarar resueltas las observaciones legacy ni pedir original Mara para esta rama G2 aislada.

## 4. Prompt de retoma mínimo para usuario

> Sigue el proyecto `MrSimkin/dnd_custom_aid` en GitHub. Recupera la ruta canónica desde `main` (`AGENTS.md` → `RESUME.md` → `docs/checkpoints/LATEST.md`) y continúa automáticamente la implementación de la Hoja de PJ V11 aprobada, usando el estado vigente de la PR #173 y sus procedimientos G0–G6, QA sin pérdida y recuperación RCR-1. Ejecuta el siguiente paso seguro, actualiza GitHub y entrega evidencia o BLOCKED preciso. No rediseñes, no repitas preguntas respondidas y no fusiones la PR de implementación sin mi aprobación.

Si la frase recibida es únicamente «sigue» **dentro de una sesión ya identificada con este repo**, interpretarla como este prompt. En un chat nuevo sin contexto, el usuario debe incluir al menos `MrSimkin/dnd_custom_aid` para desambiguar el proyecto.

## 4a. Convergencia de ramas antes de fusionar PR #173

Luego de integrar la PR documental #174 en main, **main y la PR #173 difieren en rutas de documentación** (`AGENTS.md`, `docs/checkpoints/LATEST.md`, `docs/PROJECT_STATE.md`, `docs/BRANCH_STATUS.md`). **La PR #173 fue marcada `mergeable=false` al verificar**. Esto **NO bloquea investigación G0 ni código aislado G1**, pero **SÍ bloquea eventual merge** hasta resolver coherentemente los conflictos. 

Acción de Worker en una fase segura anterior a G6: traer `main` vigente a la **misma rama PR #173**, reconciliando los cuatro documentos sin sobrescribir el checkpoint canónico de main que apunta a PR #173; conservar el checkpoint histórico de rama y su registro QA. Validar `scripts/check_resume_route.py`, workflow Scaffold y QA ledger tras esa sincronización. Registrar commits e invalidaciones; **no usar force-push ni reset --hard**. No crear rama paralela de implementación ni fusionar PR #173 automáticamente. Si no existe una operación de merge segura disponible, dejar `BLOCKED` SOLO para la promoción final y continuar G0/G1 de forma segura.

## 5. Handoff

- **Rama única de implementación**: `docs/pc-sheet-v11-approved-implementation-handoff` / PR #173.
- **Sin merge** de PR #173, sin cambios runtime ni backend.
- **Trabajo inmediato seguro:** cerrar inventario de modelos closure/successor, entrada UI y recuperación QA primario; documentar G0 PASS cuando corresponda y comenzar G1.
- **No confundir** una PR independiente **solo de routing de reanudación** a `main` con la PR #173 de implementación: la primera no autoriza fusionar la segunda.
