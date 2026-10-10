# V11 — Protocolo ejecutable de riesgos, recuperación y STOP

**2026-10-09.** Complementa `PREFLIGHT_TECNICO_Y_RIESGOS.md` y `PLAN_TECNICO_IMPLEMENTACION_V11.md`. **Estado de partida: G0 PARCIAL; G1–G6 NO INICIADOS.** No acredita ninguna prueba de App, Kotlin, Gradle, APK o PDF real.

**Estado de custodia actualizado 2026-10-09:** R-01 **VERIFICADO** en esta rama mediante Actions run #38009729554 (SHA-256 desde checkout, 20 miembros, 8 PDFs). La fila R-01 describe el procedimiento de recuperación ante futuros daños y no es ya un bloqueo presente.

## Principio operativo

Cada riesgo se atiende mediante **prevención → detector objetivo → respuesta acotada → prueba de recuperación → decisión PASS/STOP**. **Control previsto** no significa **riesgo resuelto**. Registrar evidencia (commit, comando, fixture, log, PDF real con número de página/hash) y evitar correcciones a ciegas. Una incidencia debe tener **una sola causa operativa activa**, responsable técnico, gate y criterio de cierre. La aprobación visual V11 no se reabre.

### Matriz de actuación (ID estable, severidad, gate, decisión)

| Riesgo | Detección/barrera preventiva | Respuesta si ocurre | PASS comprobable / STOP |
|---|---|---|---|
| **R-01 Custodia e integridad verificable del golden V11** — ALTO, G0 | Verificar que el ZIP integral con SHA-256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a` se puede descargar desde una referencia durable/versionada; probar hash **después** de recuperarlo, no solo antes de subirlo. | Crear un único archivo binario versionado o referencia durable verificable; conservar inventario de 8 PDF, fuente Python, QA e imágenes. No reconstruir por semejanza ni afirmar que está versionado por guardar solo el manifiesto. | **PASS**: recuperación independiente byte-a-byte y SHA igual. **STOP G0**: no existe fuente durable o integridad no coincide. |
| **R-02 Modelo real/semánticas** — CRÍTICO, G1 | Matriz `campo real → null/false/0 → ID/origen → bloque → evidencia`; versiones Permanente/Actual, habilidades por atributo, monedas cero, ACTIVO, slots vs preparación/gasto, retratos y notas. | Detener solo el campo material ambiguo; inspeccionar modelos/test/serialización existentes. Nunca inferir con fixture inventado. Elevar **una única consulta agrupada** si la decisión afecta gameplay o estado del usuario. | **PASS**: mapping tipado, tests de ausencias y manifiesto 1:1 de IDs. **STOP G1**: estado indispensable no disponible o interpretaciones funcionales no equivalentes. |
| **R-03 Desbordamiento/integridad** — CRÍTICO, G2 | Compositor `medir → segmentar → colocar → numerar → dibujar`; cada paso consume caracteres/filas/IDs o cambia de región finita. Establecer límites mínimos de celda/fuente y detectar fragmento no progresivo. | Ante fragmento que no cabe, fragmentarlo con marca de continuación y al menos una unidad de progreso; si no cabe ni la unidad mínima, fallo explícito con ID/campo/fuente y prueba reproducible, no ajustar 20 offsets. | **PASS**: historias, conjuros y Libro arbitrariamente largos dentro del dominio soportado, sin pérdidas/duplicación/cuelgue; textos completos recuperables. **STOP G2**: cualquier truncamiento o ciclo repetido. |
| **R-04 Dos motores PDF** — ALTO, G2–G3 | Definir `page-plan` compartido Kotlin con tamaños, saltos, orden/IDs y coordenadas físicas; separar adaptación de dibujo Android/Desktop. Medir fuente incrustada *real*, no fuente aproximada. | Si métricas difieren, normalizar fuentes/medición o introducir medidas de fuente en plan compartido. No crear un segundo paginador Android. Mantener aislados los renderers legacy. | **PASS**: mismo inventario, orden, etiquetas, páginas/continuaciones y tolerancias geométricas justificadas. **STOP**: pérdida de dato o semántica al cambiar plataforma. |
| **R-05 Regresión Mara/legacy** — CRÍTICO, G0/G4 | Reabrir los PDFs originales y la matriz 50800 **antes de editar** conforme a AGENTS §6.3. Registrar baseline/artefacto/commit. Cubrir Fantasy, Custom v1, Custom v2 atributo y habilidad. | En fallo, primero comprobar identidad de APK/commit/fixture; reproducir en una familia aislada; revertir cambio V11 que invade ruta congelada, no arreglar todos los sistemas a la vez. | **PASS**: 4 familias reales generan con Mara y no degradan frente a sus autoridades. **STOP G4**: falla una familia, o se desconoce identidad/runtime. |
| **R-06 Fuentes/símbolos/logo** — ALTO, G0/G3 | Comprobar TTF Barlow/Fira/Symbols-v8 de repo, ruta y licencias; fuente/clave PUA desde GUIDE; recurso logo de hoja, lectura offline; extracción PDF de fonts/Unicode y raster visible. | Corregir empaquetado/cmap/embebido, no sustituir silenciosamente por fonts genéricas. Mantener texto y ornamentación independientes. | **PASS**: Android+Desktop sin rutas del SO, todos los glifos/ñ/acento/0 visibles y fonts válidas incrustadas. **STOP**: recurso ausente o ilegible. |
| **R-07 Candidato instalado ≠ pruebas** — ALTO, G5 | Cada candidato visible: `versionName`, `versionCode`, commit, CI/artifact ID, SHA-256 de binario, PDF y fixture; ID único por cambio material. | Ante discrepancia, inmovilizar la rama y verificar procedencia antes de tocar coordenadas; pedir un solo paquete de evidencia local al propietario si el runtime no es accesible al Worker. | **PASS**: output de la App instalada corresponde exactamente al candidato auditado. **STOP**: APK o PDF sin cadena de identidad. |
| **R-08 Iteración infinita** — ALTO, transversal | Una rama, una PR, un gate activo, un contador de intentos; cada corrección tiene hipótesis causal, prueba roja antes/verde después, y resultado registrado. | Pasada inicial + **máximo dos correcciones normales**; tras el tercer fallo aplicar **RCR-1**: causa y alternativa distintas, retroceso de hasta dos gates y dos pasadas de recuperación, máximo dos globales. | **PASS**: evidencia exacta. **BLOCKED/STOP** si falta evidencia/alternativa, o falla la recuperación. Nunca relajar aceptación. |
| **R-09 Continuidad Git/reanudación** — ALTO, documentación | Seguir `AGENTS → RESUME → LATEST → checkpoint V11 → contrato → riesgos → evidencia`. Comprobar SHA remoto y branch/PR en cada chat. | El checkpoint V11 es autoridad **solo en esta rama** hasta merge de la PR. El futuro Worker debe partir del puntero de `main` y, si la PR continúa abierta, leer su HEAD explícitamente. No crear ramas paralelas. | **PASS durable en main** solo al fusionar conscientemente el traspaso documental con pointer coherente. **OPEN** mientras rama no fusionada. |
| **R-10 Alcance/coste/branding** — BAJO, controlado | Uso 100 % personal y logo original **ya decididos**; operación local y sin proveedores. Contrato fija nuevos estilo A/B opt-in, no altera otros. | Rechazar refactors de sincronización/backend, servicios pagados o rediseño inadvertido; anotarlos fuera de alcance. | **PASS**: diff limitado y sin costes. **STOP**: dependencia nueva paga/destructiva o cambio de producto no autorizado. |
| **R-11 Compositor pasa pruebas sintéticas pero falla en mundo real** — CRÍTICO, G4–G5 | Separar oracle de QA del motor, cotejar bounding boxes/textos y páginas raster; fixtures sintéticos + fixture **Mara real** + PDF salido de exportación Android. | Si divergencia, anclar al primer fragmento/registro diferente y al binario; registrar el desajuste en matriz FIXED/OPEN/CHANGED y corregir exclusivamente esa causa. | **PASS**: misma salida real revisada por verificación independiente. **STOP**: solo existe `1.104/1.104` de tests derivados. |
| **R-12 Espacios libres y ajuste final visual** — MEDIO, G2–G4 | Comprobar en C2 todo hueco físicamente apto y relleno `NOTAS` con cuadrícula completa; verificar cuadrícula de última hoja y `Salvación` más arriba/derecha. | Si hay hueco: reconstruir regla del compositor con condición general, no parchar una sola página. Nunca rellenar con notas digitales ni generar página nueva para llenar. | **PASS**: inspección de páginas reales/extremos. **STOP si material**: cuadrícula cortada, módulo perdido o página extra absurda; defectos leves no bloqueantes quedan documentados sin reabrir maquetas. |

### Normas para actuar sin loops

1. **Severidad:** CRÍTICO (pérdida/corrupción/bloqueo de exportación/semántica falsa); ALTO (paridad, procedencia, continuidad, regresión); MEDIO (defecto medible sin pérdida); BAJO (fuera de alcance o mitigado). El gate falla siempre ante CRÍTICO o ALTO sin prueba de contención.
2. **Precedencia anti-loop:** [RCR-1](PROTOCOLO_ROMPER_CICLO_Y_REPLANIFICAR.md) autoriza una única recuperación estructural acotada como excepción al STOP normal. Dos intentos de recuperación fallidos implican STOP final. **No inventar un PASS.** Estados permitidos: `OPEN`, `CONTROLADO-PENDIENTE-DE-PRUEBA`, `VERIFICADO`, `STOP`. Los riesgos de funcionalidad siguen OPEN mientras no haya candidato Kotlin y QA runtime.
3. **Orden de diagnóstico de cualquier fallo:** (a) verificar HEAD/commit/build/hash; (b) reproducir con fixture exacto; (c) aislar primera divergencia y registro ID; (d) corregir una causa; (e) prueba negativa/positiva; (f) actualizar matriz y contador; (g) continuar o STOP. No cambiar de método sin registrar por qué falló el anterior.
4. **No disimular bloqueos** rebajando la prueba, suprimiendo registros o declarando “no aplica” sin evidencia. Las mejoras opcionales no ingresan a la ruta crítica.
5. **Evitar bloqueo de usuario innecesario:** Worker realiza diagnóstico, código y QA reproducible. Pedir al propietario solamente acceso a runtime local o una decisión semántica genuina que no se pueda inferir de autoridad aprobada. Una sola solicitud compacta.
6. **Rollback:** V11 es opt-in, conserva los cuatro estilos; con defectos no se activa V11 por defecto ni se fusiona la PR. Reversión de rama sin migraciones destructivas; no modificar backend/DB.

### Libro de bitácora obligatorio por gate

```text
Gate: G0/G1/... | Estado: OPEN/PASS/RECOVERY_CANDIDATE/RECOVERY_ACTIVE/BLOCKED/STOP | Intento normal: 1/2/3 o recuperación R1/R2
SHA de código: ... | fixture y origen: ... | comando/CI: ...
Primera observación original / regla V11 afectada: ...
Resultado esperado: ... | resultado observado: ...
Riesgo R-xx: ... | causa comprobada: ...
Corrección concreta y archivo/línea: ...
Prueba roja→verde y PDF candidato (SHA/página): ...
Decisión: PASS / corregir (si quedan intentos) / triage RCR-1 / BLOCKED / STOP
```

La bitácora se conserva en un único documento de la PR, no en comentarios repartidos por chats ni en informes que reescriban el resultado previo.

### Handoff y STOP limpio

- **PASS gate:** emitir matriz por requisito con evidencia **del candidato exacto**. Pasar al gate siguiente; no solicitar confirmación estética.
- **RECUPERACIÓN RCR-1:** no reabrir un gate sin SHA de último PASS, causa verificable, alternativa distinta, invalidación de pruebas afectadas y presupuesto global documentado.
- **STOP gate:** anotar primera causa no corregida, impacto, prueba mínima, historial de intentos, alternativa técnica recomendada, condición objetiva de desbloqueo y **estado del ZIP golden y PR**. Dejar rama consistente y sin merge.
- **PR revisable, pero no integrada:** la evidencia de rama persiste en GitHub; `main` sigue con su ruta previa. Hasta que se fusione la ruta documental, el lector debe ir explícitamente a la PR/handoff.
- **Final:** ningún gate puede acreditarse por el plan escrito. Requiere pruebas ejecutadas y artefactos accesibles.
