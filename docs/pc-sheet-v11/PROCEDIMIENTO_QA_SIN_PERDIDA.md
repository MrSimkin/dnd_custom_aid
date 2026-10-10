# V11 — Protocolo QA cerrado: observación → reparación → cambio real → verificación

**Autorización del propietario:** 2026-10-09. **Objetivo:** impedir que una observación se pierda, que un «arreglo» quede solo en documentos/tests, o que el siguiente APK enseñe la misma página sin un cambio comprobable. **Ámbito:** V11 y comprobaciones de no regresión de las 4 familias existentes; la regla general `AGENTS.md §6.3` continúa vigente. Este documento **no declara** implementado el renderer V11.

## Fuente de verdad: evidencia original + registro auditable

- **Nunca modificar ni resumir sustituyendo** el texto original del usuario, su fecha/contexto y PDF/captura/página. Conservarlo literal en `qa/REGISTRO_QA_V11.json` y, si corresponde, como archivo binario referenciado mediante SHA-256. Si no existe captura, registrar exactamente `EVIDENCIA_NO_DISPONIBLE` y no fingirla.
- **Cada observación tiene ID estable** (p. ej. `V11-QA-001`), categoría, severidad, alcance (V11 / legado), origen y estado. No borrar IDs, fusionarlos o renumerarlos: duplicados se **vinculan** con `related_ids`, manteniendo ambos textos originales.
- La matriz **M50800-01..N** no se copia como «cerrada»: sigue siendo autoridad histórica independiente en `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`. El validador exige cobertura exacta de sus IDs en el inventario del registro. `M50800-01` tiene **solo la resolución histórica de procedencia**; no da PASS al candidato V11.
- Un cambio de chat, sesión, modelo, Worker, rama de trabajo o nombre de archivo **no reinicia** el registro, los intentos, los hallazgos ni los controles RCR-1.

## Máquina de estados por observación

```text
OPEN  →  TRIAGED  →  IMPLEMENTED_UNVERIFIED  →  CANDIDATE_VERIFIED  →  OWNER_ACCEPTED
  ↑         ↑                  ↑                         │
  └─────────┴──── NEW REGRESSION / EVIDENCE MISMATCH ────┘
```

- `OPEN`: observación registrada textualmente, aunque no exista diagnóstico.
- `TRIAGED`: **antes de editar código**, se asocia causa o hipótesis reproducible, módulo/archivo, ejemplo nativo, prueba roja, prueba de aceptación y riesgo. En batch, todas las observaciones están identificadas; no aplicar cambios para «una parte» ignorando las restantes.
- `IMPLEMENTED_UNVERIFIED`: existe cambio real en **código productivo**, commit verificable, prueba roja→verde y salida prevista. Cambiar solo documentos, fixture, test o plan **no alcanza**.
- `CANDIDATE_VERIFIED`: se ha generado el PDF del **candidato exacto** (APK/desktop ejecutable identificable), se revisaron página y geometría y se demostró explícitamente que **el síntoma original ya no aparece**; conserva hash PDF, commit, build, QA suite, resultado y responsable. Puede entregarse al propietario **sin afirmar aceptación suya**.
- `OWNER_ACCEPTED`: aceptación explícita del propietario asociada al **mismo candidato verificado**, no una aprobación visual previa de maqueta.
- `BLOCKED`: no se permite simular un arreglo ni declarar PASS. Registrar condición precisa de desbloqueo; el caso vuelve a su estado operativo al solucionarla, con todo el historial.
- Si un commit/rebuild altera materialmente el renderer, una prueba previamente verificada **caduca** hasta revalidar el candidato nuevo. Aunque el test unitario pase, no se conserva un «FIXED» antiguo. El validador comprobará el SHA del candidato activo frente al SHA de las pruebas por observación.
- Nuevas regresiones: crear **nuevo ID**, enlazar al fallo de origen y al candidato; **no sobrescribir** la observación anterior.

## Proceso después de CADA QA del propietario

**Q0 — Recepción (sin código):** recoger en el chat los comentarios numerados tal como los entregue el propietario. No exigir un formulario; Worker transcribe y etiqueta. Identificar binario `versionName/versionCode`, commit, artifact ID, hash y PDFs/PNG por caso (si alguna evidencia no existe, indicar que falta). Conservar reporte RAW sin retoques y lista completa de IDs, incluyendo «sin otros defectos». Si el usuario no conoce el SHA, **lo determina el Worker**, no pide al usuario calcularlo.

**Q1 — Congelar el problema:** generar en un único commit **matriz integral de QA**, con `observación → página concreta → síntoma → resultado esperado → subsistema responsable → prueba roja → evidencia exigida`. Recuperar el PDF real y los originales de errores previos. Separar observación nueva, regresión, defecto preexistente, pregunta y sugerencia opcional; nunca descartar una observación sin registrar el motivo.

**Q2 — Diagnóstico antes del arreglo:** agrupar observaciones por causa y dependencia. Crear una **única propuesta de reparación de ese QA**, ordenada por riesgos (corrupción, pérdida, exportación fallida, desbordes, geometría, cosmético). Para cada grupo: primer registro que falla, causa reproducible, archivos de producción previstos, criterio de salida, alcance Android/Desktop y cuatro familias legado, test independiente. No pedir una ronda más de diseño por detalles ya aprobados. Aprobar internamente solo soluciones técnicas, sin adivinar semántica de negocio.

**Q3 — Reparación real:** aplicar cambios de Kotlin/render/UI (si corresponde), **committear** y enlazar el SHA con las observaciones afectadas. Ejecutar primero prueba negativa original y después positiva; **no reemplazar el test fallido por uno más fácil**. Todo arreglo debe tener `diff` del código ejecutado. Si CI falla, caso sigue abierto. RCR-1 controla replanificaciones; no loops infinitos.

**Q4 — Regeneración y prueba del candidato:** generar APK de QA con **identidad nueva**, y candidato Desktop equivalente. Exportar el mismo fixture/caso en Android y Desktop. Verificar en PDF *real* fuente, página, posición, texto, ID y composición; detectar signos de salida vieja comparando hash, build/versionCode y geometría. Revisar **cada observación original**, incluso si otras pruebas pasan. Ejecutar suite de cuatro familias previas + V11, incluyendo Mara. Si la exportación de la App diverge del laboratorio, congelar correcciones y comprobar identidad del APK primero.

**Q5 — Auditoría anti-falso-arreglo:** reabrir exactamente los archivos de QA original, el plan Q2, el diff productivo, la prueba y el PDF candidato. Regla de cierre doble: **(a) existe código productivo distinto que alcanza la App**; **(b) existe evidencia del cambio visible/semántico en el PDF del candidato exacto**. Para defectos cuya solución sea exclusivamente de recurso/configuración y no Kotlin, demostrar recurso empaquetado + nuevo build y PDF. Sin ambas evidencias: `IMPLEMENTED_UNVERIFIED` o `OPEN`, nunca «corregido». Repetición idéntica entre PDFs exige parar y verificar procedencia, no aumentar 20 offsets.

**Q6 — Devolución al propietario:** una sola entrega consolidada: nuevo APK identificado y método de instalación normal (no `adb install`), PDFs de comparación, **tabla de todas sus observaciones** con anterior/actual y los ítems aún abiertos, cambios en código/commit, qué debe mirar y cómo recopilar resultados. Nunca enviar un QA «nuevo» sin declarar qué observaciones viejas se corrigieron y cuáles permanecen. Para recuperar archivos Android desde `Download`, usar el comando de **pull total** fijado en `AGENTS.md §6.1`.

**Q7 — Cierre o nuevo ciclo:** registrar literalmente la respuesta del propietario y enlazarla a ID/candidato. Sin respuesta explícita, queda `CANDIDATE_VERIFIED`, no `OWNER_ACCEPTED`. Si rechaza o reaparece el defecto, reabrirlo **con ID original + nuevo incidente/QA**, no borrarlo. El siguiente ciclo empieza desde los ítems abiertos; **nunca reinicia el proyecto ni rehace aprobaciones anteriores**.

## Puertas de no regresión: aplicables por cada nuevo candidato

1. **Snapshot/equivalencia:** para *todas* las 5 familias (4 legadas + V11 A/B), fixture real Mara más vacío, normal, custom, mágico y texto extremo; preservar orden/IDs, estados, nulos, montos cero y relaciones, sin regresiones frente al baseline vigente. Si falta la evidencia primaria, `BLOCKED` explícito.
2. **Geometría/lectura:** Carta V11, fuente real embebida, cobertura A/B, 0 texto fuera de límites, saltos/marcas/casillas, encuadre y márgenes; cuadrículas sin cortes y sin espacios C2 injustificados, referencias físicas bidireccionales, página manuscrita estrictamente última.
3. **Flujo de datos:** capturar IDs de origen antes de renderizar y compararlos con manifest de emisión y texto/operaciones PDF; ninguna pérdida/duplicación, ninguna preparación inferida, ninguna casilla ACTIVO inventada. Verificar con herramienta independiente del algoritmo compositor.
4. **Integración:** mismo snapshot y layout contractual en Android/Desktop; exportar desde las acciones reales Save/Share/Export (no solo helpers unitarios). VersionName/Code y SHA binario único, SHA PDFs y commit del candidato publicados.
5. **Oráculo independiente:** al menos uno de inspección PDF (extracción + geometría/raster) diferente del compositor. Un test que reutiliza los mismos cálculos que el renderer **no es oráculo independiente**.
6. **Control de regresión en QA siguiente:** la matriz siempre incluye IDs **OPEN**, `IMPLEMENTED_UNVERIFIED` y `CANDIDATE_VERIFIED` del ciclo anterior hasta aceptación, además de los nuevos. No sacrificar comportamientos ya aceptados para resolver otros.

## Regla de promoción

- **Entrada a QA propietario:** ninguna observación bloqueante de V11 puede estar `OPEN`, `TRIAGED` o `IMPLEMENTED_UNVERIFIED`; deben existir evidencia `CANDIDATE_VERIFIED` del candidato exacto, tests de no regresión y un manifiesto de binario.
- **Promoción/merge:** todas las observaciones bloqueantes del candidato requieren `OWNER_ACCEPTED` o una excepción explícita del propietario conservada como decisión; PR aprobada separadamente. Una aprobación **visual de la maqueta V11** jamás se interpreta como aceptación del binario.
- Si hay una regresión no resuelta: `BLOCKED`, volver a Q1–Q5 con RCR-1. No entregar el mismo APK como «nuevo».
- Lo anterior **no obliga a resolver todos los defectos legados anteriores para cerrar V11**; sí exige comprobar que V11 no los empeora y registrar por separado el estado histórico abierto de M50800 sin afirmar su cierre.

**Plantilla por cada QA:** copiar `qa/PLANTILLA_CIERRE_POR_RONDA.md` a `qa/rondas/<QA-ID>.md`. Worker la completa y enlaza en la PR; el propietario solo comunica hallazgos y revisa el candidato. La plantilla registra plan previo, commit productivo, antes/después, PDF real y decisión final.

## Aseguramiento automatizado

`qa/REGISTRO_QA_V11.json` almacena IDs, citas originales, estado, plan y evidencia; **no es opcional**. `scripts/check_v11_qa_registry.py` valida coherencia, nombres, estados, presupuestos RCR-1, cobertura M50800 y **prohíbe estados verificados sin hashes/commit del candidato idénticos**. Ejecutar el validador en cada commit y en CI antes de cualquier anuncio de reparación/cierre. El validador solo comprueba la **calidad formal de la evidencia**, nunca sustituye la inspección humana de los PDFs ni demuestra que Kotlin genere un resultado correcto.

## Estado inicial

Las únicas dos correcciones menores pendientes del prototipo V11 se registran textualmente como `V11-QA-001` (posición «Salvación») y `V11-QA-002` (huecos C2), ambas `OPEN`. La aprobación visual completa está intacta; no hay PDF/App Kotlin V11 nuevo. La antigua matriz M50800 mantiene su autoridad y estados. G0 sigue PARCIAL.
