# V11 — Procedimiento de ruptura de ciclo y recuperación (RCR-1)

**Aprobación:** propietario, 2026-10-09, solicita y autoriza un proceso que, ante intentos fallidos, vuelva a un punto verificado, replanifique y siga **sin crear un loop nuevo**. **Alcance:** rama/PR V11, nunca `main`, backend o otras familias. **Estado inicial:** G0 parcial; G1–G6 pendientes. El procedimiento no simula pruebas ni concede PASS.

## 0. Regla de precedencia y salida

Este documento **sustituye expresamente el STOP automático tras tres intentos** en `PLAN_TECNICO_IMPLEMENTACION_V11.md`, `PROMPT_WORKER_IMPLEMENTAR_V11.md`, `PREFLIGHT_TECNICO_Y_RIESGOS.md` y `PROTOCOLO_RIESGOS_Y_RECUPERACION.md` **solo** cuando se cumplen las condiciones de recuperación siguientes. Los tres intentos normales siguen siendo el límite de correcciones locales. **No se recupera una causa externa sin nueva evidencia**: falta de golden, identidad de binario, credenciales, acceso a fixture real, dudas semánticas materiales o corrupción no verificable = `BLOCKED` con solicitud/condición precisa; no consumir intentos inútiles.

**Estados de gate:** `NOT_STARTED`, `IN_PROGRESS`, `PASS`, `RECOVERY_CANDIDATE`, `RECOVERY_ACTIVE`, `BLOCKED`, `STOP`. Solo `PASS` habilita el gate siguiente. El estado de riesgo `CONTROLADO` jamás equivale a `VERIFICADO`.

## 1. Flujo de control cerrado

```text
G0→G1→G2→G3→G4→G5→G6  (cada gate: 1 implementación + máx. 2 reparaciones)
                        │
                        └─ tercer fallo reproducible de la MISMA causa
                                   │
                         PAUSA + congelar evidencia
                                   │
                      ¿problema verificable y alternativa
                        realmente distinta y acotada?
                          │                  │
                          NO                 SÍ
                          │                  │
                    BLOCKED/STOP       RECOVERY_CANDIDATE
                                             │
                       localizar último checkpoint PASS verificable
                                             │
                       retroceder SOLO los 1–2 gates necesarios
                       y preservar cambios/evidencia no afectados
                                             │
                        REPLAN breve (nueva hipótesis + prueba)
                                             │
                        RECOVERY_ACTIVE: 1 implementación alternativa
                        + máx. 1 corrección (DOS pasadas)
                                             │
                     ¿PASS de gate y revalidación de gates invalidados?
                            │                              │
                            SÍ                             NO
                            │                              │
                      SEGUIR normal                    STOP definitivo
```

**Excepción importante:** `G0` no puede retroceder a un gate anterior. Para bloqueo de recursos/QA/autoridad se queda `BLOCKED` hasta recuperar evidencia, sin especular ni reescribir la maqueta.

## 2. Cuándo se permite romper el ciclo

Se exige simultáneamente: (a) reproducir el fallo del candidato identificado por commit/build/hash; (b) tener prueba o inspector independiente que detecte el defecto, no solo el propio renderer; (c) identificar una causa arquitectónica o una suposición errónea por la que otra corrección local sería repetitiva; (d) proponer **una alternativa técnica distinta con evidencia de viabilidad**, sin cambiar el contrato visual/datos; (e) existir un checkpoint previo `PASS` desde el cual se pueda recuperar sin perder integridad de datos. Si falta cualquiera, `BLOCKED/STOP`.

**No habilita recuperación**: gusto visual nuevo, optimización estética, ahorro de páginas a costa de datos, inventar una nueva familia o anexo, reemplazar fixtures para pasar las pruebas, renombrar la misma hipótesis, cambio de rama, repetir una acción fallida o alterar criterios de PASS.

## 3. Retroceso seguro (máximo dos gates)

1. Guardar `HEAD`, base `main`, diff, hashes de artefactos, logs y matriz `FIXED/OPEN/CHANGED` **antes** de tocar archivos. El historial y los fallos se conservan.
2. Elegir el **gate causante más temprano** y el último checkpoint `PASS` no contaminado. Solo puede reabrirse **uno o dos gates hacia atrás**, no todo el proyecto. Por ejemplo: error detectado en G4 causado por paginación G2: reabrir G2/G3 y repetir las validaciones afectadas de G4. Si requiere más de dos gates, **STOP estructural**, con propuesta acotada de nuevo alcance.
3. **No usar `git reset --hard`, force-push ni borrar commits como modo normal.** Revertir mediante nuevos commits los cambios responsables y conservar intactos los commits/documentos no relacionados; documentar los SHAs de reversión. Ante cambios mezclados, revertir parches selectivos revisados.
4. Toda prueba de gates posteriores dependiente de los cambios revertidos pasa a `INVALIDATED`, aunque antes estuviera verde. No conservar aprobaciones caducadas.
5. Redactar **una sola replanificación breve**, no una investigación abierta: causa demostrada; por qué fallaron los intentos 1–3; alternativa distinta; superficies afectadas; nueva prueba roja; criterio fijo de éxito; plan de reversión.
6. Ejecutar máximo **dos pasadas de recuperación** (implementación alternativa + una corrección). Si pasan y el resultado completo vuelve a cumplir el gate, se revalidan los gates posteriores afectados y continúa la ruta habitual.

**Cotas globales:** máximo **una recuperación por gate y por causa**, máximo **dos recuperaciones estructurales en toda la implementación V11**. Se registran de forma acumulada en la PR. Ninguna renumeración de gate, nueva sesión o nuevo chat reinicia el contador. Una alternativa fallida no recibe una tercera oportunidad oculta. Si aparece un defecto verdaderamente independiente, se clasifica y registra por separado; no se oculta como repetición de la misma causa.

## 4. Condiciones de detención irrenunciables

- Pérdida/corrupción de datos, inventar estados de juego o truncamiento: no se promueve candidato; se corrige dentro del presupuesto o se detiene.
- Fixture Mara o V11 golden no disponible/verificable: `BLOCKED`, **no sustituirlo por un fixture ficticio** para cerrar aceptación.
- APK instalado sin identidad reproducible: `BLOCKED` hasta verificar commit, build y hash; **no ajustar posiciones** sin confirmar que el código probado corresponde al instalado.
- Fallo del segundo intento de recuperación, ausencia de nueva hipótesis validable, más de dos gates a retroceder o segunda recuperación global agotada: **STOP definitivo** con un solo informe de salida.
- Nunca fusionar PR ni publicar en producción automáticamente. **Un STOP no pierde avances**: deja una PR consistente, SHA, deuda precisa, pruebas y opción de rollback documentada.

## 5. Formato obligatorio de cada incidente y recuperación

```text
INCIDENTE: <ID estable>    Gate detectado / gate causante: ...
CANDIDATO: branch + HEAD + build/CI/artifact + SHA del PDF (cuando exista)
EVIDENCIA ORIGINAL: fixture/QA/PDF, página, ID o línea afectada
INTENTOS NORMALES: 1/3, 2/3, 3/3; hipótesis, cambio y resultado de cada uno
CAUSA RAÍZ: confirmada / NO VERIFICADA (si no verificada: BLOCKED)
CHECKPOINT PASS AL QUE RETROCEDER: gate, SHA, tests y evidencias
PROFUNDIDAD: 0, 1 o 2 gates; compromisos revertidos y pruebas invalidadas
ALTERNATIVA: qué cambia, por qué es distinta, cómo podría fallar
RECUPERACIÓN: intento R1/2 y R2/2 con rojo→verde y PDF exacto
PRESUPUESTO GLOBAL: 0/2, 1/2 o 2/2 recuperaciones estructurales gastadas
DECISIÓN: PASS y revalidar / BLOCKED con condición concreta / STOP
```

## 6. Siguiente acción ya autorizada para el Worker

Sin pedir al propietario confirmar este procedimiento: seguir G0 de la PR #173. **La custodia del golden V11 ya se realizó y verificó** (Actions #38009729554, R-01 VERIFICADO); no pedir otra subida. Inspeccionar pruebas originales Mara, completar mapa semántico y preflight de compilación; completar lo seguro. Si falta capacidad material, registrar `BLOCKED` en el punto exacto y conservar toda evidencia útil. **No retroceder ficticiamente gates aún no ejecutados** ni usar el modo recuperación para saltarse G0.
