# V11 G2/G3 — BK04 + BK05: fichas completas y continuaciones íntegramente medidas

**Fecha:** 2026-10-17 (Chile). **Alcance:** avanzar en [PR #173](https://github.com/MrSimkin/dnd_custom_aid/pull/173) con el mismo plan técnico original G0→G6 y contrato V11 + D-0076 §§3.1–3.48. La PR continúa OPEN/DRAFT/UNMERGED. No se modificaron `main`, cuatro familias legacy ni el golden ZIP de 8 PDF.

## Hallazgo / qué se desarrolló

D-0076 §3.48 exige simultáneamente (A) pasar **completa** una ficha mediana del Libro de Conjuros a la siguiente columna vacía si allí cabe (BK04), y (B) dividir solo una ficha más grande que una columna completa, conservando el texto y señalando la continuación (BK05).

El paginador compartido `PcSheetV11TextFlow.paginate` ya respetaba A por `keepTogetherIfFits && lines.size <= column.lineCapacity && lines.size > remaining`: mueve la ficha íntegra con `nextColumn()`. La corrección funcional BK05 de la jornada anterior ya marca las columnas de un conjuro muy largo. **No se introdujo un rediseño, ni se reescribió paginación válida:** se incorporaron pruebas G2/G3 específicas que faltaban y se conservaron PDF nativos con escenarios discriminantes.

**BK04/G2:** `bk04MeasuredBookKeepsMediumCardWholeAtStartOfNextColumn` comprueba geometría con área real Carta y una métrica de fuente declarada de prueba. Primera ficha 6 líneas de 8, segunda 4 líneas; la segunda se mueve COMPLETA a columna 2, fila 0, sin `continuedFromPrevious`, sin duplicados ni huecos; prueba aparte que una tercera ficha mayor que una columna sigue paginándose sin pérdida. **Kotlin real PASS**; fuente de prueba **`d26ced4dc0443127fb3607c8d8e14aa63dbef71f`**.

**BK04/G3:** `g3Bk04MediumRealSpellCardMovesWholeToNextPhysicalBookColumn`: respaldo real Ilyra; escoge las primeras dos identidades efectivas desde el mapper semántico, calibra la cantidad de líneas con **la tipografía real PDFBox 9pt Fira embebida**, modifica solamente las descripciones del fixture, genera PDFs nativos A/B y verifica que la primera ficha **Fire Bolt** queda a la izquierda (38 líneas `BK04_PREVIA_`), mientras **Light** comienza íntegra a la derecha (8 líneas `BK04_SEGUNDA_`) en la **página física 10**; no hay falsa marca CONTINUACIÓN. Índice de los dos conjuros apunta a páginas reales; NOTAS sigue última. Se cotejaron los 14 PDFs preexistentes, idénticos en página, palabras, posiciones y trazos vectoriales.

**BK05/G2:** `bk05MeasuredBookSplitsOnlyOversizedCardAcrossColumnsAndPagesLosslessly`, fuente final **`8c82b5004238095b7683dde16119871c2798c3e9`**. Ficha de 18 líneas fuente ocupa exactamente tres columnas de seis líneas: (página local1, columna1)→(local1, columna2)→(local2, columna1), tres fragmentos con `continuedFromPrevious` y `continuesOnNext` correctos, offsets UTF-16 contiguos y ningún carácter perdido, repetido o alterado; ficha siguiente arranca en página2 columna2. G2 es geométrico, y BK05/G3 sigue respaldado por los anteriores PDFs A/B de 14 páginas y etiquetas realmente inspeccionadas.

## CI y evidencia PDF exacta

- **[Scaffold #38997328224](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38997328224): SUCCESS**, SHA `d26ced4dc0443127fb3607c8d8e14aa63dbef71f`. QA trazabilidad [#38997328340](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38997328340): SUCCESS. Kotlin/G2/G3/Android DEBUG, backend y DB.
- **[Scaffold #38997803250](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38997803250): SUCCESS**, SHA `8c82b5004238095b7683dde16119871c2798c3e9`. QA trazabilidad [#38997803151](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38997803151): SUCCESS. BK05/G2 incluido.
- **[16 PDFs nativos App, artefacto #12087359034](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38997328224/artifacts/12087359034)**, ZIP SHA256 `3d7819085f8ec46de23ccd7d3f1dc0cf88c1382f5a3358719c76143a1abb2f5f`, CRC PASS. Los nuevos `V11-INTERNAL-PROOF-BK04-whole-next-column-A/B.pdf` tienen 11 hojas Carta, un solo Libro en p10, izquierda desde x42 y ficha completa siguiente derecha desde x312. Los otros 14 PDFs continúan idénticos a los baselines previos en palabras/coordenadas/vectores/páginas.
- **[16 PDFs nativos del commit final BK05/G2, artefacto #12087649359](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38997803250/artifacts/12087649359)**, ZIP SHA256 `131b70db48c72e682df31d81a0afc4378ca239c31ff6a3e16e6eef55f03192e8`, CRC PASS. Los 16 conservan idénticos números de páginas, palabras, posiciones y dibujos vectoriales; hay bytes PDF distintos por regeneración de documentos pero **no cambio visible**. BK04-A/B sigue mostrando sus ocho marcadores derechos una sola vez.
- No se sustituyó ningún PDF verdadero de la App por un PDF sintético ni se atribuyó al unit test G2 una validación visual.

## Estado normativo y límites

La matriz conserva las **105 reglas**. Se registran **BK04.G2=PASS, BK04.G3=PASS y BK05.G2=PASS**, todos de alcance **local/etapa y con referencia exacta a evidencia**. BK05.G3 mantiene el PASS limitado anterior, con pruebas reales de continuación. **BK04.status y BK05.status global permanecen `NOT_VERIFIED`**. El gate **G4=BLOCKED**, `active_candidate=null`; faltan la conciliación completa de todas las reglas, las ocho variantes golden, cuatro exportaciones anteriores, diferencias de contrato si las hubiera y paridad Android instalada. No se aprueba G5/G6, QA del propietario, publicación de APK ni merge. `main` sigue en `0c009f10d5e66880d68fa3732491c6fc3e172f22`.

Siguiente avance operativo conforme al plan: seguir por verificaciones materiales todavía abiertas (especialmente BK03, donde `CharacterSpell` carece de escuela y no se permite inventarla), o por defectos funcionales comprobables en P1/P2/P3/C2, priorizando cobertura G1/G2/G3 hacia G4; evitar cosmética y regresiones.
