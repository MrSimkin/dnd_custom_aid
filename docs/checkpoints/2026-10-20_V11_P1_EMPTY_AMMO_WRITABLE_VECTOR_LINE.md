# V11 — P1 municiones sin registrar: línea de nombre realmente manuscrita

**2026-10-20.** PR [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173) DRAFT, base `cabc497763a57b55658a6a059bae9b04b332d163`; `main` `0c009f10d5e66880d68fa3732491c6fc3e172f22`. [Scaffold #39505448540](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39505448540) y [QA #39505448589](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39505448589) del HEAD anterior **SUCCESS**. Prohibido fusionar o inferir aprobación técnica/visual owner.

## Diferencia fuente y papel medida

En la P1 de los PDF Desktop App genuinos (76 archivos [artifact #12302848879](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39504035835/artifacts/12302848879), P1 no modificada en trabajos P4 siguientes), **50/76 imprimen** la palabra literal `____________` entre x316..360 (PDF top-origin y663.455..671.855), incluso en el caso fuente **sin una sola munición persistida**. La rama exacta Kotlin `if (p1AmmoItems.isEmpty())` dibuja esa falsa tinta mediante `p1DrawSmall(stream,"____________",316f,122f)`, acompañada de 20 cuadrados físicos en dos filas. El contrato frozen `CONTRATO_VISUAL_APROBADO.md` exige papel de escritura manual realmente reglado, **sin fabricar ID/tipo/cantidad de munición**; D-0076 §3.39 explícitamente exige una única fila lógica en ausencia de fuente, con **20 casillas vacías agrupadas 5+5**. El golden P1 no contiene nombre falso ni 12 guiones impresos.

## Implementación limitada a estado vacío

Los dos PDF renderers Kotlin Android/Desktop reemplazan **únicamente** los 12 guiones falsos en rama `p1AmmoItems.isEmpty()` por **una única línea vectorial real** de (316,120) a (377,120), tinta azul 130/165/186, ancho 0.55pt, enteramente dentro del panel físico original. Las 20 marcas 6pt, la caja original, pie, niveles, texto fuente, cantidad cero real y el contrato para cantidades **mayores que 20** (`MARCAS: ____ / X`) permanecen intocados. No se trasladan datos ni se cambia paginación.

Se amplían **solo controles existentes**: Kotlin `g3RealP1WithoutAmmunitionHasOnePaperEntryWithTwentyEmptyBoxes` comprueba texto sin guiones y operadores vectoriales exactos, manteniendo el conteo 20; auditor PyMuPDF `scripts/audit_v11_p1_clipped_cards.py` (ya ejecutado por Scaffold) detecta automáticamente la firma física de 20 casillas en dos subfilas y exige trazo real sin token falso. No se añaden nuevos auditores ni se reabren los anteriores.

**Antes de PASS LOCAL:** Scaffold + QA del código exacto, auditor de 76 PDFs, y comparación fuente/PDF antes-después con páginas, palabras de fuente y posiciones conservadas excepto borrado previsto del token **falso** en 50 P1; todos los dibujos preexistentes (P1 y páginas ajenas) conservados. Verificar 50 líneas vectoriales nuevas, 20 marcas previas por caso, cero modificaciones a `MARCAS: ____ / X` y legacy. Estado: IMPLEMENTADO, **CI/PDF exactos PENDIENTES**. No promocionar las 105 reglas G4.

**Gates:** G0 histórico PARCIAL, G1 PASS solo semántico previo, G2/G3 PARCIALES, G4 **BLOCKED 105/105 NOT_VERIFIED, active_candidate=null**, G5/G6 pendientes, C211 BLOCKED normal 3/3 estructural RCR-1 0/2; owner Android instalado/visual/technical approval pendientes. PR #173 DRAFT OPEN NO MERGE.

**Siguiente:** cotejar PDF exacto, documentar evidencia física o fallo; continuar exclusivamente otra diferencia material sin especulación.

## Primera QA de trazabilidad: expectativa obsoleta corregida sin modificar producción

[Owner QA traceability #39507309995](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39507309995) **FAIL** en `scripts/test_v11_ammunition_manual_contract.py`: el test histórico `test_same_genuine_source_grammar` exigía literalmente `p1DrawSmall(stream, "____________", 316f, 122f)` para **ambas** plataformas, precisamente el comportamiento falsamente digital que el cambio elimina. Se modifica SOLO este test para prohibir esa cadena, exigir las operaciones `moveTo(316f,120f)` y `lineTo(377f,120f)` en ambas fuentes y mantener las demás obligaciones (20 casillas, grupos 5+5, altura, igualdad de fuente de renderer). No se modifica Kotlin de producción ni se rebaja un control físico. El Scaffold del commit anterior y el nuevo QA CI deben cerrarse antes de declarar PASS LOCAL.
