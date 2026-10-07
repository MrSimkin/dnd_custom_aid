# Mara Phase 3 — continuation / interruption recovery prompt

**Date:** 2026-10-06 (Chile local time)  
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

- último HEAD funcional **validado** conocido: `c2367e11bef9e92990f0f9efc6e89b13cf07795f`;
- Custom-v1 Traits: **CLOSED / VALIDATED**;
- Scaffold #4518 / run `37553725881`: backend SUCCESS, hosted-database SUCCESS, kotlin SUCCESS;
- proof artifact: `11454232332`;
- proof digest: `sha256:37645e52aab6949580ca0c87cd5567b655aebeb73f12a810a1a2d65d552f4107`;
- Traits cerró en tres ciclos: #4516 dos criterios OPEN -> #4517 uno -> #4518 cero;
- Narrative sigue validado y separado semánticamente en el proof más reciente;
- residual visible `EXTENSIÓN: NOTAS` continúa OPEN bajo M50800-03 stale-underlay/source-geometry;
- siguiente frente autorizado: Combat/Actions M50800-12/13, comenzando read-only;
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

## 4. FRENTE FUNCIONAL ACTUAL

**Traits, Combat/Actions, Notes y M50800-26/29 semantic survival ya están CLOSED / VALIDATED. No los reabras sin nueva evidencia real.**

Última evidencia física:

- validated production HEAD: `d4e42818223100d96de32e0fe4a278dc0356153e`;
- Scaffold #4521 / `37557543660`: backend / hosted-database / kotlin SUCCESS;
- proof HEAD: `59b039bdccc8d33a79252536c1d5d7bd10470e0e`;
- Scaffold #4522 / `37558318363`: SUCCESS;
- proof artifact `11455609450`;
- digest `sha256:72cb4aba21442a76aa887605cdcc2a05a117c50475f159a8d5471f268529ec97`;
- Fantasy long character identity complete in two lines with no ellipsis;
- Fantasy base Combat long detail uses `[continúa en COMBATE / ACCIONES]`;
- referenced Extended Combat row retains the complete semantic record in native table grammar;
- current four key family PDFs contain no literal `...` / `…`;
- M50800-26 / 29 demonstrated;
- front count: #4520 RED -> #4521 GREEN -> #4522 GREEN focused proof;
- `LOOP_SUSPECTED = NO`.

El siguiente frente autorizado es:

**General bidirectional semantic continuity — read-only first**

Antes de cualquier write:

1. enumera los flows que realmente pueden partir un mismo registro/módulo entre secciones/páginas no contiguas;
2. distingue flows atómicos/movidos completos (sin marker necesario) de flows realmente partidos;
3. verifica uso actual de `PcSheetBidirectionalContinuation` y paridad Desktop/Android;
4. inspecciona proofs #4522 para Narrative, Notes y otros split flows;
5. evalúa específicamente si el nuevo cue Fantasy Combat de origen requiere `proviene de...` en destino bajo la regla general del owner;
6. NO agregues markers a registros que sólo se repiten o mueven enteros;
7. sólo si queda un gap material, implementa el paquete mínimo y cuenta el primer CI como **ciclo 1/4**;
8. cierra con CI verde + proof PDF visual real.

El residual de source/footer como `EXTENSIÓN: NOTAS` / `EXTENSIÓN: RASGOS` sigue reservado para el frente posterior M50800-03 stale-underlay/source-geometry.

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
