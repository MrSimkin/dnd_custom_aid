# V11 G2/G3 — Municiones de nombre multilineal y tabulado en P1/C2

**Fecha:** 2026-10-18. Rama exclusiva `docs/pc-sheet-v11-approved-implementation-handoff`, PR #173 DRAFT sin merge. Base verificada `ed4cdafc01f63b0e3d30376ab2a9f20fbc2bc77f`, [Scaffold de base #39160921290](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39160921290) SUCCESS. Último paquete P1 ataques multilineales [#39160188797](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39160188797) ya probado; **no repetido** como desarrollo.

## Brecha nueva demostrable en código
La selección P1 de municiones usa `p1Font.width.measurePt(it.name) <= 68f` y no excluía **CR/LF/TAB** en el nombre persistido. Como P1 reserva dos renglones físicos rígidos para munición/marcas, esa medición no es prueba de impresión segura de texto multilineal. El contrato V11 P117/P118 y D-0076 §3.39 exige casillas útiles sin perder el dato auténtico, y C2 ya conserva una descripción completa de munición con UUID, nombre, cantidad y estado de marcas reales. Esta brecha es diferente al filtro de **ATAQUES** arreglado en el paquete anterior.

## Reparación acotada
En los dos renderizadores nativos V11 (Android/Desktop) el selector de P1 descarta únicamente nombres con CR/LF/TAB; conserva el filtrado existente por tipo real `AMMUNITION`, cantidad, ancho y orden. **No elimina** esos registros del origen ni de la ruta C2; una entrada de nombre corto sigue en P1. No se modifica ninguna familia legacy, modelo persistido, golden, `main`, cuadro de notas o paginador.

## Evidencia requerida
Nuevo test integrado en `DesktopPcSheetV11RuntimePdfProofTest.kt`: parte del backup real Aldren y genera **variantes A/B** con una copia en memoria de tres municiones reales clasificadas (`LF`, `TAB`, nombre corto); usa UUID separados y cantidades concretas 7, 9, 4. Comprueba que los textos problemáticos y las cantidades quedan legibles en C2, ninguno de esos nombres aparece en P1, la entrada corta conserva fila P1, la fuente sigue intacta y NOTAS final queda manuscrita. Conserva PDFs de aplicación real bajo `V11-INTERNAL-PROOF-P1-ammo-source-break-{A,B}.pdf`. CI también ejecuta auditorías físicas ya automatizadas, sin reiniciarlas manualmente.

**Estado antes del CI:** implementación publicada para prueba, **NO PASS anticipado**. G0 parcial, G4 BLOCKED, 105 obligaciones globales NOT_VERIFIED, G5/G6 pendientes, `active_candidate=null`. Esta pieza exige cotejo del PDF real y reporte de CI por SHA exacto. No cambiar V11-QA-001/002, 32 M50800 ni matriz sin compuerta completa; no RCR-1 salvo condición formal.

**Siguiente trabajo:** continuar sobre otras brechas reales de integridad P1/P2/C2 (en particular flujos y referencias por UID) que no cuenten ya con demostración; preservar los 8 golden, G0–G6 y la PR sin merge.
