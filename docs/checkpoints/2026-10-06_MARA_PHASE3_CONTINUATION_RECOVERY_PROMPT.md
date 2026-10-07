# Mara Phase 3 — continuation / interruption recovery prompt

**Date:** 2026-10-07 (Chile local time)  
**Scope:** active Mara Phase-3 renderer repair and convergence to the next owner-facing QA candidate  
**Repository:** `MrSimkin/dnd_custom_aid`  
**Implementation branch:** `repair/mara-phase3-semantic-flow-compositor`

This file is a ready-to-copy recovery prompt for a fresh conversation or a continuation after timeout, connection interruption, server failure, lost polling response or other ambiguous execution interruption.

It is intentionally state-aware but not state-assumptive: every resume must verify current GitHub reality before acting.

---

# CONTINUACIÓN / RECUPERACIÓN CONTROLADA — MARA PHASE 3

Continúa el trabajo existente del repositorio:

`MrSimkin/dnd_custom_aid`

Branch de implementación:

`repair/mara-phase3-semantic-flow-compositor`

## OBJETIVO GENERAL

NO reinicies Phase 3.

NO reconstruyas un plan general desde cero.

Reanuda desde la autoridad canónica y la evidencia física actual de GitHub.

El objetivo operativo es **seguir cerrando de forma convergente los requisitos M50800 pendientes hasta llegar a una nueva versión candidata de QA para el owner**, con identidad nueva y trazabilidad completa.

No crear esa versión candidata antes de sus gates.

Pero tampoco detener el trabajo simplemente porque un paquete intermedio quedó verde: una vez cerrado y documentado un frente, continúa autónomamente con el siguiente frente Phase-3 autorizado, salvo que exista:

- un guardrail anti-loop activado;
- una decisión genuinamente nueva de owner;
- una frontera manual/provider/security/cost;
- o el gate de handoff visual al owner.

## 1. REANCLAJE OBLIGATORIO

Antes de cualquier write, relee en este orden:

1. `AGENTS.md`
2. `RESUME.md`
3. `docs/checkpoints/LATEST.md`
4. el checkpoint canónico activo apuntado por `LATEST.md`
5. `docs/checkpoints/2026-10-06_MARA_PHASE3_CONTINUATION_RECOVERY_PROMPT.md`
6. sólo las autoridades adicionales que el checkpoint activo declare necesarias

La autoridad final de Phase 2 y las decisiones owner ya cerradas prevalecen sobre preguntas, hipótesis o clasificaciones provisionales históricas.

No resurrectes decisiones ya resueltas.

## 2. VERIFICACIÓN FÍSICA ANTES DE CONTINUAR

Verifica directamente en GitHub, antes de modificar nada:

- branch real;
- HEAD exacto;
- commits recientes;
- workflows recientes;
- jobs `queued`, `in_progress` y `completed`;
- conclusiones de cada job;
- PR abierto o inexistente;
- artifacts disponibles;
- si el último write esperado realmente existe.

Últimos anchors conocidos al actualizar este prompt:

- último HEAD funcional del renderer: `516fa0bfac06e9efb70a8dbe4f4d4b3f4d62c1f7`;
- frente general de continuidad: **CI_GREEN / VISUAL_PENDING**, no CLOSED;
- full Scaffold #4523 / run `37560523288`: backend SUCCESS, hosted-database SUCCESS, kotlin SUCCESS;
- full proof artifact: `11456517371` (~186 MB), disponible pero no necesario para el loop interactivo;
- HEAD de infraestructura previo a esta consolidación documental: `ab48e71097ef7ef49628507b1f06423a6c8ed916`;
- `PC Sheet fast gate` final: run `37566302657` SUCCESS;
- focused artifact: `11457979404` = 1,886,765 bytes;
- focused artifact digest: `sha256:36dcc8222eabb275747344cf26909c3ea4dc8da65afcc1c704b0c97dd1cd7b5f`;
- en `ab48e71097ef7ef49628507b1f06423a6c8ed916` sólo corrió el fast gate; el full Scaffold no se disparó;
- no PR de implementación abierto al último chequeo.

Estos datos son **sólo anchors de recuperación**.

NO asumas que siguen siendo actuales.

Si HEAD avanzó, apareció otro workflow, cambió un PR o existe un artifact nuevo, reconstruye primero el estado desde esa evidencia.

## 3. REGLA CRÍTICA PARA TIMEOUT / CONNECTION INTERRUPTED / SERVER ERROR

Un timeout, error de polling, conexión interrumpida, respuesta perdida o problema de servidor **NO demuestra que una operación no ocurrió**.

Nunca repitas automáticamente:

- commits;
- pushes;
- cambios de archivo;
- reruns;
- creación/actualización de PR;
- generación de artifacts;
- cambios de versión;
- ni otro write.

Primero verifica físicamente GitHub.

Si el write ya ocurrió, continúa desde él.

Si ocurrió parcialmente, reconstruye qué parte quedó aplicada antes de decidir el siguiente paso.

Si el estado sigue ambiguo, permanece read-only hasta resolverlo.

NO hagas rollback automático a un commit verde anterior.

NO fuerces una rama sobre un HEAD que cambió.

NO interpretes ausencia de respuesta del agente como ausencia de efectos en GitHub.

### Ruta CI activa para este branch

En `repair/mara-phase3-semantic-flow-compositor`, los pushes ordinarios del renderer usan primero `PC Sheet fast gate`. No dispares ni repitas el Scaffold completo por cada microcorrección.

- Fast gate = feedback de implementación + proofs focalizados.
- Full Scaffold = cierre coherente, PR o `workflow_dispatch`.
- `CI_GREEN / VISUAL_PENDING` es un estado válido cuando falta inspección PDF real.
- Si falla polling, conexión, servidor o descarga de artifact, consulta primero el mismo run y su estado físico; no crees otro commit para "hacerlo correr de nuevo".

### Polling interactivo espaciado + trabajo útil mientras CI corre

Para evitar timeouts sin convertir al owner en un botón manual de “continúa”:

1. después de un commit, confirma una vez que existe el `PC Sheet fast gate` esperado y registra su `run_id`;
2. si el run está `queued` o `in_progress`, no termines automáticamente la ejecución si todavía existe trabajo read-only independiente y útil;
3. continúa sólo trabajo que no dependa del resultado pendiente: reconciliar burn-down, revisar evidencia ya validada, inspeccionar outputs previos, localizar el siguiente criterio o preparar diagnóstico sin hacer writes dependientes;
4. luego de un intervalo material de trabajo útil, puedes consultar **el mismo run exacto** otra vez; el polling debe ser espaciado y nunca un loop de espera;
5. no hagas un commit correctivo dependiente, rerun, run duplicado ni abras otro frente de write hasta que el run previo complete y, cuando corresponda, su proof real sea inspeccionado;
6. usa `CI_RUNNING / NO_ACTION` sólo cuando CI sea realmente la única dependencia restante y ya no haya trabajo independiente razonable en ese turno.

Esta regla controla el tiempo de sesión y reduce interrupciones al owner; **no rebaja ningún gate de evidencia** ni permite saltarse la inspección PDF exigida.

## 4. FRENTE FUNCIONAL ACTUAL

**EXACT MARA FOUR-FAMILY PROOF GATE — ACTUAL OUTPUT FIRST**

General bidirectional semantic continuity permanece **CLOSED / VALIDATED**.

M50800-03 source-geometry / stale-underlay está **CLOSED / VALIDATED** en:

- functional head: `1566bd3c04bd64ec136fbf9c5df510ac60641a66`;
- fast run: `37568418440` — SUCCESS;
- artifact: `11459124665`;
- digest: `sha256:1c9675a4afc5d504f131f15849d81783ba207bd9c304780c30b3fa6f14527d2c`;
- text-layer PASS: PERSONALIDAD/HISTORIA Custom-v1 ya no retienen labels fuente ajenos ni `EXTENSIÓN: NOTAS`;
- visual PASS: fragments fuente aislados conservan apariencia/geometría sin cubrir contenido generado;
- cue routing: resuelto por `PcSheetBasePageRole` presente, no por índice fijo;
- Android renderer-sync + PDF-delivery guards: PASS.

No reabras M50800-03 ni continuidad sin nueva evidencia material.

Siguiente acción exacta:

1. generar Mara exacta desde un mismo source head en Fantasy, Custom v1, Custom v2 · Atributo y Custom v2 · Habilidad;
2. conservar los cuatro PDFs reales como artifact;
3. inspeccionar text layer + render visual real;
4. verificar paridad Android/Desktop;
5. aplicar M50800-01…32 a esos outputs exactos como `FIXED` / `OPEN` / `CHANGED-NEW`;
6. si aparece un blocker, corregir sólo ese defecto observado y volver al fast gate;
7. sólo con hard gates cerrados crear nueva identidad candidata; nunca reutilizar `0.5.0-preqa.8 / 50800`.

## 5. GUARDRAIL ANTI-LOOP

Para cada frente registra:

`criterio -> fallo observado -> causa raíz -> cambio -> resultado`

Marca `LOOP_SUSPECTED` y detén los commits correctivos de ese frente si ocurre cualquiera:

A. el mismo criterio sigue fallando después de dos correcciones de la misma causa;

B. la misma función/área recibe tres o más commits correctivos consecutivos sin cerrar un criterio material nuevo;

C. se alcanzan cuatro ciclos CI sobre el mismo frente sin reducir los criterios abiertos;

D. arreglar A vuelve a romper B y arreglar B vuelve a romper A;

E. los tests se relajan repetidamente sólo para aceptar el output actual sin autoridad canónica;

F. los defectos materiales OPEN dejan de disminuir durante una secuencia suficientemente larga y aparecen nuevos defectos en las mismas áreas.

Una secuencia de fallos distintos NO es loop si cada ciclo cierra criterios permanentes y reduce OPEN.

Cuando un frente cierre materialmente, su contador no se hereda al frente siguiente.

## 6. CONTINUACIÓN AUTÓNOMA HACIA LA NUEVA VERSIÓN

Una vez cerrado el frente actual:

1. actualiza la memoria operativa/checkpoint con evidencia real;
2. consulta el burn-down M50800-01…32 y el checkpoint canónico;
3. selecciona el siguiente frente **material, localizado y autorizado** por dependencias;
4. trabaja ese frente hasta:
   - cierre demostrado;
   - guardrail;
   - o frontera real de owner/manual/provider;
5. repite el burn-down.

No abras varios frentes correctivos a la vez.

No detengas el trabajo sólo para pedir permiso entre paquetes técnicos ya autorizados.

Sí informa claramente los cierres y cambios de estado.

El orden exacto restante debe venir del repo actual, pero al último checkpoint todavía quedaban, como mínimo:

- cierre de Custom-v1 Traits;
- Combat/Actions restante, incluyendo Fantasy native table grammar y Custom-v2 logical-row behavior donde aún aplique;
- Notes native full-page + continuidad bidireccional;
- auditoría restante de ellipsis / silent drop;
- continuidad bidireccional general en output real;
- source-geometry / stale-underlay restante;
- paridad Android/Desktop;
- exacta generación Mara en las cuatro familias;
- matriz M50800-01…32 contra el candidato exacto.

No uses page count como objetivo de calidad.

## 7. GATE PARA CREAR LA NUEVA VERSIÓN CANDIDATA

La meta es llegar a una nueva versión candidata owner-facing.

Sólo cuando los blockers Phase-3 estén realmente cerrados:

1. genera Mara exacta desde **un mismo candidato** en:
   - Fantasy;
   - Custom v1;
   - Custom v2 · Atributo;
   - Custom v2 · Habilidad;
2. inspecciona los PDFs reales, no sólo extracción textual;
3. marca M50800-01…32 como:
   - `FIXED`;
   - `OPEN`;
   - `CHANGED-NEW`;
4. cualquier blocker `OPEN` o regresión blocking impide candidate;
5. si el gate pasa, crea identidad nueva:
   - nuevo `versionName`;
   - nuevo `versionCode`;
   - nunca reutilizar `0.5.0-preqa.8 / 50800`;
6. conserva la cadena exacta:
   - source commit;
   - workflow/run;
   - artifact ID;
   - APK SHA-256;
   - nombre owner-facing del APK;
   - hashes de los cuatro proofs Mara;
7. sólo entonces autoriza el handoff visual al owner.

No crear owner candidate, APK de entrega, Current Snapshot final, Media/Handouts ni build final anticipadamente.

## 8. DISCIPLINA DE CHECKPOINT

Después de cada cambio material de route o cierre de paquete:

- actualiza el checkpoint canónico;
- actualiza `LATEST.md` si cambia la ruta práctica;
- actualiza `PROJECT_STATE.md` / `BRANCH_STATUS.md` cuando su estado material cambie;
- registra HEAD, runs y artifacts relevantes;
- conserva este recovery prompt vigente.

Si la ruta Phase-3 cambia materialmente, actualiza también este prompt o crea su sucesor y enlázalo desde el checkpoint activo.

No dejes un checkpoint afirmando GREEN si el HEAD funcional actual está RED.

Distingue siempre:

- último HEAD funcional validado;
- HEAD funcional abierto/no validado;
- commits sólo documentales.

## 9. RESTRICCIONES

Mantener:

- reuse-first / native-first;
- geometrías owner-approved/frozen;
- no semantic ellipsis;
- no silent drop;
- ordinary Equipment = cantidad + identidad completa solamente;
- Notes = full native page;
- Equipo Especial = native fixed module;
- explicit bidirectional semantic continuity;
- Desktop/Android parity;
- exact actual-PDF inspection cuando corresponde.

No crear un PASS sólo por mensaje de commit.

No considerar CI verde suficiente cuando el criterio requiere proof visual real.

No sustituir prueba visual por extracción textual.

## 10. RESPUESTA / HANDOFF

Si no aparece una frontera de owner:

- continúa trabajando dentro del turno tanto como sea coherente;
- no pidas confirmación rutinaria;
- no detengas el burn-down después de un paquete verde sólo para esperar;
- mantén al owner informado de cierres materiales y guardrails.

Si aparece una frontera real:

informa exactamente:

1. último HEAD físico demostrado;
2. último CI/run relevante;
3. qué criterios cerraron;
4. qué sigue OPEN;
5. si existe `LOOP_SUSPECTED`;
6. por qué hace falta owner;
7. qué acción/evidencia concreta debe devolver.

Objetivo final de esta ruta:

**converger hasta una nueva versión candidata trazable y visualmente verificada, no acumular indefinidamente reparaciones intermedias.**
