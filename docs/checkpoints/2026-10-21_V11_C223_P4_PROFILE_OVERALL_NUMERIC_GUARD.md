# C223 — P4: los valores globales no sustituyen estadísticas explícitas por origen

Fecha Chile 2026-10-21; PR #173 DRAFT/NO MERGE; rama `docs/pc-sheet-v11-approved-implementation-handoff`. **Estado: IN_PROGRESS. NO PASS hasta Scaffold y verificación PDF real.**

## Brecha semántica concreta y autoridad

Contrato visual V11 aprobado, D-0076 §§3.24–3.26: cuando existen fuentes mágicas con valores propios distintos, la franja P4 no puede declarar como universales valores engañosos. C221 solucionó diferencias **entre** varias fuentes (incluso conocidas/desconocidas) y dejó intacta la geometría aprobada. Pero si **todas las fuentes tienen el mismo CD y ataque y ambas cifras discrepan del dato global de personaje**, C221 no detecta contradicción: imprimía los valores globales como referencia inequívoca. La información source-specific se encuentra en perfiles persistidos y no debe ser sustituida por un valor genérico. Esto es una brecha de corrección distinta de C211 y C222; no se reconstruye estética ni se toca legacy.

## Cambio funcional real

[Commit de producto `440b74f`](https://github.com/MrSimkin/dnd_custom_aid/commit/440b74fb369503419392dbde75176ef566f68bcc) modifica la lógica compartida `PcSheetV11SpellSourceLabels.globalReferenceDisagreements`: para perfiles con CD/ataque numéricos **explícitos**, detecta diferencia con el dato general; los perfiles desconocidos (`null`) no se fabrican ni se clasifican como discrepancias demostradas. Combina esta comprobación con la regla C221 preexistente. Los renderizadores V11 Android y Desktop ponen `SEGÚN FUENTE` SOLO en la tarjeta numérica realmente contradictoria. Aptitud sin conflicto, tablas de conjuros/preparación/slots y página/ornamento son iguales. El desglose físico numérico por cada fuente sigue pendiente de decisión de producto, NO se introduce una tabla nueva.

Unit test Shared prueba dos UUID reales distintos con el mismo perfil explícito 18/+5 frente a general 15/+7, ausencia de false positives cuando coincide y ausencia de inferencia ante `null`. Prueba integrada Desktop parte de backup Ilyra, conserva todos los conjuros, asociaciones y nombre, y modifica únicamente los **ajustes persistidos de los tres perfiles existentes por igual** (+2 DC / -2 ataque), de modo que dejan valores comprobables 17/+5 frente al general 15/+7: verifica que NO es el caso C221, que se marcan dos tarjetas, que INT/preparación/final NOTAS continúan y exporta PDF auténtico A/B para inspección. Las cuatro familias legacy siguen inalteradas.

## CI, aceptación y continuidad

QA trazabilidad: [#39713904856](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39713904856) **SUCCESS** del SHA `440b74fb369503419392dbde75176ef566f68bcc`. Scaffold [#39713904751](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39713904751) **EN CURSO/PENDIENTE al registrar**. No afirmar PDF real ni PASS antes del resultado y auditoría externa sobre los PDF del **mismo SHA** comparados con 82 PDF previos de C222 (761 folios) y dos candidatos A/B nuevos. Si falla, corregir solo causa demostrada bajo RCR-1; conservar error y presupuesto.

**Gates sin promoción:** G0 histórico PARCIAL / G1 PASS semántico anterior / G2 y G3 PARCIALES / G4 BLOCKED **105/105 NOT_VERIFIED, active_candidate=null** / G5 y G6 pendientes. C211 BLOCKED 3/3 intentos normales agotados, 0/2 estructural. Las 32 observaciones M50800 y dos V11 QA siguen sin falsa validación. PDF runtime propietario original de Mara, APK Android instalado, QA visual/técnica propietaria y G4 completos **PENDIENTES**; no merge, no tocar main ni despliegues.

**Próximo paso:** verificar ejecución exacta del Scaffold y auditores físicos; descargar nuevo lote PDF de App y comparar palabra/posición/vectores/imágenes con los 82 anteriores, revisar dos P4 nuevos, crear acta por hash y registrar veredicto acotado. Luego continuar tareas G2/G3/G4 verificables independientes de C211 y de las decisiones pendientes sobre diseño per-source.
