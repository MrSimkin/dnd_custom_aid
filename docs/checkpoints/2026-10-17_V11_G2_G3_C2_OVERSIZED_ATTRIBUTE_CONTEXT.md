# V11 G2/G3 — Grupo custom mayor que una columna: contexto fuente en C2

**Fecha local:** 2026-10-17 (Chile). **Autoridad exclusiva:** [PR #173](https://github.com/MrSimkin/dnd_custom_aid/pull/173), rama `docs/pc-sheet-v11-approved-implementation-handoff` (DRAFT/OPEN/sin merge). `main` comprobado `0c009f10d5e66880d68fa3732491c6fc3e172f22`; implementación exacta [`b5c575da0542beabe1f865d2fdf9b32dcbacc0b6`](https://github.com/MrSimkin/dnd_custom_aid/commit/b5c575da0542beabe1f865d2fdf9b32dcbacc0b6).

## Obligación y defecto concreto

El [contrato V11 aprobado](../pc-sheet-v11/CONTRATO_VISUAL_APROBADO.md) requiere que atributos personalizables y habilidades mantengan su relación visual al pasar a C2, sin pérdida de IDs ni páginas fabricadas. El checkpoint anterior verificó **grupos completos que caben en una columna**; no cubría un solo grupo legítimo **más alto que una columna**, por lo que la primera columna imprimía el atributo real pero las siguientes podían listar solo habilidades, sin identificar su relación de origen.

**Hallazgo técnico independiente, NO nueva observación del propietario.** El paginador Shared ya preservaba todos los offsets; el problema era exclusivamente la ausencia de referencia visible en columnas físicas de continuación. No se reabrió el diseño V11 ni se alteró la gramática de habilidades separadas de un atributo ya impreso P1 (abreviatura `(XXX)`).

## Cambio de producción acotado

- Ambos renderizadores PDFBox **V11 Android y Desktop**, solamente dentro de `addSection("C2 - Habilidades, acciones y recursos")`, inspeccionan la primera celda **de cada columna física** y su `groupKey` Shared.
- Si esa columna continúa un atributo personalizado cuya entrada fuente comenzó en una columna/página diferente, imprime `CONTINÚA ATRIBUTO: <nombre real>` con 7 pt Fira/body en la banda reservada **PDF y702–717** (baseline y707), bajo la regla de cabecera y por encima de la primera fila de contenido. Usa la medición real del PDF: si el nombre no cabe, emplea abreviatura real; si no está disponible, el ID real. Si ninguna forma cabe, falla explícitamente; **no** corta el dato, no inventa abreviatura ni introduce texto dentro de las celdas.
- La etiqueta es **referencia de navegación**, no reimpresión de la fila de puntuación/bonificadores ni segundo ID de juego. No se mueven entradas, geometría de contenido, fuentes, otras secciones V11 ni los cuatro exportadores legacy; `main` y golden no se alteran.
- Test PDF real `g3OversizedRealCustomAttributeShowsItsIdentityOnEveryC2ContinuationColumnAB`: parte del backup genuino de Mara y añade **solo al snapshot temporal de prueba** un atributo con 140 habilidades realmente vinculadas por IDs UUID y relaciones sucesoras; variantes A/B, Carta, contexto por columna, score original una vez, 140 nombres únicos, ninguna habilidad en hoja manuscrita final.
- RCR-1 no se activó; primera solución y comprobación, sin retroceso de gates ni aprobación del propietario.

## Verificación del candidato exacto

- [Scaffold checks #39089195856](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39089195856) **SUCCESS**, incluido Shared/Desktop Kotlin, compilación Android y controles backend/DB. [QA traceability #39089195908](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39089195908) **SUCCESS** del mismo commit; esto no es aceptación owner ni prueba Android instalado.
- [Artefacto GitHub #12122340801 — ZIP de 44 PDF genuinos Desktop](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39089195856/artifacts/12122340801). SHA-256 ZIP `b595fa1ad8076afef1133a3525d06a45fdba3644bf739ad348c580efd7107ae4`; archivo ZIP CRC PASS. Contra [42 PDF anteriores #12121592973](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39087923925/artifacts/12121592973): **42/42 sin cambios detectables** en número de páginas, palabras con posición redondeada a 0,01 pt, número de trazados por página e imágenes. No equivale a identidad byte a byte ni comparación geométrica exhaustiva.
- Nuevo `V11-INTERNAL-PROOF-C2-oversized-custom-attribute-A.pdf`: SHA `bdcf026abfc18fe620bb709b96269755972ebabe586eca77b05a65ce4ae30645`; **11 páginas Carta**. El grupo se distribuye en C2 física p5 columna 1 (**43 habilidades, único atributo original**), p5 columna 2 (**50 habilidades, contexto real visible**) y p6 columna 1 (**47 habilidades, contexto real visible**): 140/140 únicas.
- Nuevo `V11-INTERNAL-PROOF-C2-oversized-custom-attribute-B.pdf`: SHA `2e5ae4b799bdec784f7d491d09a5a227db3c0e139786e6eb230cb9db36f83ec0`; **11 páginas Carta**. C2 p5 columna 1 (**44 habilidades, único atributo original**), p5 columna 2 (**50, contexto**), p6 columna 1 (**46, contexto**): 140/140 únicas.
- Inspector **independiente PyMuPDF** sobre los ZIP nativos de GitHub confirmó texto fuente exactamente una vez por habilidad, fila original de puntuación exactamente una vez, rótulos de continuación en las 2 columnas posteriores, última página NOTAS manuscritas, Carta todas las páginas, sin duplicados ni pérdida de identidades. Se rasterizaron/inspeccionaron visualmente franjas superiores de p5–p6 A/B; referencia dentro de banda libre y sin cortes ni colisiones aparentes en esas franjas.
- **PASS local G2/G3 solamente del caso de grupos sobredimensionados con nombre fuente que cabe en la banda**. No se ha demostrado aún cada rama de fallback de nombre largo/abreviatura/ID en PDF Android instalado. No extrapolar PASS global.

## Gates, deudas y continuación

- Matriz de contrato: **105 reglas globales `NOT_VERIFIED` y 12 comprobaciones históricas de etapa PASS**, conservadas sin cambio ni reinterpretación; `active_candidate=null`. G0 mantiene QA histórica Mara del propietario incompleta (PDF original de runtime instalado no recuperado). **G4 BLOCKED**, G5/G6 pendientes. QA V11 y M50800 originales, golden, libro y política RCR-1 intactos.
- No hay aprobación técnica/visual adicional del propietario; no se ofreció APK al propietario, no se ejecutó Android instalado, no se fusionó PR #173.

**Próximo bloque recomendado:** pasar a otra obligación V11 realmente pendiente, no repetir los grupos C2 ya verificados. En particular inspeccionar **salvación P1 (microajuste aprobado)** y **relleno manuscrito NOTAS en huecos C2** contra PDF genuino antes de editar, o consolidar pruebas independientes de G4/D-0076/golden cuando sean materialmente aplicables. Cualquier cambio vuelve a cotejar artefacto exacto; no elevar 105 reglas ni enviar G5 sin compuerta.
