# V11 — P1 espacios de conjuro con contadores extensos: continuación C2 sin pérdida

**Fecha:** 2026-10-17. **Rama única:** `docs/pc-sheet-v11-approved-implementation-handoff`; **PR #173** abierta/DRAFT, **no fusionar**. `main` permanece en `0c009f10d5e66880d68fa3732491c6fc3e172f22`.

## Hallazgo concreto

Las nueve celdas P1 de espacios (niveles 1–9) reservan ~22 pt para «total» y ~29 pt para «gastados». Antes, el renderer dibujaba sin medir los contadores `Int` del origen en esas posiciones fijas; cantidades digitalmente válidas de más dígitos podían invadir la columna vecina y resultar ilegibles. Esta brecha es una **prueba técnica extrema nueva**, no una observación histórica atribuida al propietario.

## Implementación acotada

- Desktop renderer: commit `d8ee9bac115b348ca0eb017cc3db11d6a7a1b533`.
- Android renderer: commit `c11051ce0cbaa986cd1d202fbd02b0b4de5876ed`.
- Prueba PDF real Desktop A/B: commit `5bc9efcd02af70f8267eea914d6732bcd0b8ab92`, `g3OversizedPersistedSpellSlotCountersUseP1C2WithoutClippingOrInventingValues`.
- Se miden con la fuente real los valores total/gastados. Si cualquiera excede su ancho fijo, P1 conserva el nivel y muestra `VER C2` en vez de superponer números; C2 conserva **ambas cifras completas** en un único registro `spellcasting:slots:<nivel>` del flujo medido. Los niveles con cifras ordinarias permanecen en P1 sin cambio. Los niveles épicos ya documentados y los cuatro formatos anteriores no se modifican.
- Fixture discriminante: copia del backup auténtico Aldren con **solo** `spellSlots` cambiado artificialmente a nivel 1 total `2147483647`, gastados `2147483646` y nivel 2 total `3`, gastados `1`. No corresponde a cifras históricas reales del personaje. El test exige que los dos números extremos aparezcan una vez en C2, no aparezcan en P1, el segundo nivel permanezca normal y NOTAS siga siendo la última hoja.

## Evidencia y cautelas

- QA de trazabilidad para commit exacto `5bc9efc`: [Actions #39072946529](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39072946529) **SUCCESS**.
- Scaffold en commit `5bc9efc`: [Actions #39072946515](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39072946515), **verificar conclusión exacta**. Los jobs backend y hosted-database figuran SUCCESS; Kotlin estaba ejecutándose al redactar esta primera evidencia.
- Aunque el CI confirme la prueba, **no** se ha hecho todavía inspección independiente de páginas PNG/PDF del caso nuevo ni comparación exhaustiva con el ZIP golden y familias previas: no declarar G2/G3 PASS para este slice antes de esas verificaciones.
- Matriz pre-QA: **105 reglas globales NOT_VERIFIED / 12 checks históricos PASS** sin promoción; G4 BLOCKED, G5/G6 pendientes. QA V11-QA-001/002 y 32 M50800 no se reescriben ni se cierran; RCR-1 sin gastar recuperación. `active_candidate=null`, sin propietario/Android instalado/merge.
- No se cambiaron esquemas, datos guardados, servicios, recursos tipográficos, visual V11 aprobado, `main`, golden ni renderers de las cuatro familias legacy.

## Próxima acción segura

1. Comprobar [Scaffold #39072946515](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39072946515) sobre `5bc9efc`. Si falla, leer el job Kotlin, reparar causa reproducible y repetir sin falsear PASS.
2. Obtener PDFs nativos de A/B del artefacto del mismo run; verificar lectura, nombres/números, páginas, encuadre, última NOTAS y comparación física de casos previos sin afirmar identidad si no se prueba.
3. Registrar resultado y continuar una brecha funcional distinta con pruebas específicas; luego G4 integral y su auditoría independiente antes de cualquier candidato del propietario. No fusionar PR #173.
