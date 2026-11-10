# V11 G2/G3 — combate P1: valores largos con continuación auténtica C2

**Fecha:** 2026-10-17 (Chile). **PR:** [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173), OPEN/DRAFT/unmerged. **Commit de código:** [`8e9b6bc8f76a65a676f94875a3e6e7b1f8e46690`](https://github.com/MrSimkin/dnd_custom_aid/commit/8e9b6bc8f76a65a676f94875a3e6e7b1f8e46690). Se conserva el contrato V11, gates G0→G6, `main`, cuatro exportadores legacy y ocho PDFs golden sin modificación.

## Defecto real de código y reparación

El dashboard fijo de combate P1 imprimía ocho estadísticas en columnas de 129pt y abortaba `require(body.width.measurePt(value) <= 121f)` cuando un valor legítimo era demasiado ancho. **DADOS** construye `hitDiceRemaining + "d" + hitDieSides` por clase; suficientes clases podían hacer imposible exportar V11, contrariando integridad y compatibilidad con personajes extremos.

Se añadió `PcSheetV11CompactFieldRouter` en Shared (no cambia dominio, almacenamiento ni exportadores anteriores). Mide con la fuente PDFBox real y conserva el valor íntegro en P1 si cabe. Cuando excede el ancho, muestra únicamente **VER C2** en su celda P1 y aporta una entrada estable `p1-combat:<etiqueta>` con **etiqueta + valor fuente sin modificar** al flujo mixto C2 medido. Las ocho estadísticas tienen la misma protección; el contenido C2 y sus páginas físicas se trazan por la infraestructura existente. Los dos renderizadores nativos Android/Desktop aplican el mismo plan. El marcador no es un dato de juego inventado.

No se acortan valores, no se repiten datos entre P1/C2, no se añaden páginas si el valor original cabe.

## Pruebas ejecutadas en el commit exacto

- Shared `PcSheetV11CompactFieldRouterTest`: valores normales/vacíos permanecen en P1; dieciséis valores en orden con texto fuente íntegro medido en C2; fail-closed si ni el marcador cabe.
- Desktop PDFBox `g3ExtremeRealMulticlassHitDiceContinueInC2WithoutP1CrashOrDataLoss`: backup original Aldren, con **16 filas de clases alteradas exclusivamente como fixture de estrés** (no son los datos originales de un personaje del propietario); salida del servicio real `generateV11Proof`, variante A y B.
- [Scaffold #39063006457](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39063006457) **SUCCESS**: Kotlin unit/integration, compilación Android, backend y DB. [QA traceability #39063006437](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39063006437) **SUCCESS**.
- [20 PDFs nativos de la App, artefacto #12112407223](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39063006457/artifacts/12112407223): ZIP SHA-256 `650de0d98d758ea10720db321bff8fd304797662bf6fcb367934b6fe9e8b564d`; ZIP CRC íntegro.

## Inspección independiente de PDF verdadero

Contra los 18 PDFs de referencia del commit anterior `b32c954...` (Scaffold #39058406060), **18/18 sin cambio** en número de páginas, todas las palabras/posiciones, vector paths y familias de fuentes. Ningún cambio en escenarios ordinarios, Libro, C2, P2, tabs ni notas.

**Nuevos A/B:** siete páginas Carta (612×792pt) cada uno; en P1 aparecen DADOS y VER C2 dentro de la celda y pie que remite a **C2 pág. 4**; C2 pág. 4 lleva los **16** valores fuente distintos **exactamente una vez** y el pie inverso **VIENE DE P1 PÁG. 1**. Ninguna palabra sale del marco imprimible; Barlow Condensed y Fira Sans siguen incrustadas. Inspeccionadas las páginas P1 y C2 renderizadas a imagen. La última página es NOTAS manuscrita sin datos de golpe.

Hashes SHA-256:
- Variante A `V11-INTERNAL-PROOF-P1-multiclass-dice-A.pdf`: `ea1f9087b5882c05b86ced2b4428e4d40f2d6f52ad76417517177674ccaf4bc4`.
- Variante B `V11-INTERNAL-PROOF-P1-multiclass-dice-B.pdf`: `879597f77462ecfbec4f076b5c01573c9ac23dd0289f877a7c8ee3d1ad345a10`.

## Alcance de cumplimiento y siguiente paso

**Validación local de esta corrección: PASS con evidencia G2/G3 acotada.** No se promueve íntegramente P110, GLB06, GLB09 ni ninguna otra regla global: faltan sus demás escenarios y las verificaciones independientes del candidato completo. Matriz existente: **105 reglas**, **12 checks PASS previos**, `active_candidate=null`, **G4 BLOCKED**. G5/G6, QA instalado Android, auditoría de ocho golden, 48 decisiones D-0076 y cuatro familias legacy siguen pendientes. No merge ni aceptación del propietario. Siguiente prioridad: seguir módulos de riesgo semántico/visual no cerrados y planificar validación completa sin repetir BK04/BK05/C206/tabs ni esta reparación.
