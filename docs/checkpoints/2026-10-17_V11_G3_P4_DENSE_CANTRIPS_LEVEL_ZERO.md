# V11 G3 — Trucos de nivel 0 densos: continuidad física P4 A/B

**Fecha local:** 2026-10-17. **Autoridad:** PR [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173), rama `docs/pc-sheet-v11-approved-implementation-handoff`, DRAFT/OPEN/sin merge. **Main verificado:** `0c009f10d5e66880d68fa3732491c6fc3e172f22`.

## Brecha comprobada y trabajo realizado

El último checkpoint cubría 175 conjuros de **nivel 1**, pero no validaba un listado igual de denso de **Trucos / nivel 0** pasando entre columnas y páginas reales. El renderer ya contenía una regla general para el rótulo por nivel; aquí se verifica por primera vez el caso 0 en el PDF auténtico de la App, sin presumir que el código funcionara.

Commit de test funcional [`efdb1860`](https://github.com/MrSimkin/dnd_custom_aid/commit/efdb18607ee48b46aedb8b56ca1b33839bfe326c): `g3DenseRealCantripsKeepLevelZeroContextAcrossPhysicalColumnsAB` en `DesktopPcSheetV11RuntimePdfProofTest.kt`. Parte del respaldo auténtico Ilyra y amplifica a 175 entradas **solo en la prueba**, manteniendo asociaciones reales; comprueba variantes A/B, Carta, 175 IDs exactamente una vez, cabecera genuina TRUCOS, rótulos de continuación por columna/página, ausencia de rótulo falso nivel 1 y NOTAS manuscritas última. No modifica los datos guardados de Ilyra, código productivo, Android, cuatro exportadores legacy, golden ni `main`.

## Verificación del commit funcional exacto

- [Scaffold checks #39083161297](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39083161297): **SUCCESS** (Kotlin Shared/Desktop + compilación Android, backend y hosted DB).
- [PC Sheet V11 owner QA traceability #39083161213](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39083161213): **SUCCESS** de registro, no QA del propietario.
- [Artefacto ZIP de PDFs nativos #12119613456](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39083161297/artifacts/12119613456): SHA-256 `32084784140528d1b65c45ae6bc07411524a86a219110e19e6918f057ea824d3`, ZIP CRC íntegro, **38 PDFs**, contra [anterior #12119138570](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39082179553/artifacts/12119138570), SHA `b7db5e9d65054a185a834b86461948391dffafc5a5b47c2fe4c9bbd315526aeb`.
- Inspección independiente local PyMuPDF sobre ambos ZIP: los **36 PDF anteriores** conservan la misma cantidad de páginas, posiciones/palabras (0,01 pt), cantidad de trazados por página e imágenes; **no se afirma igualdad de bytes ni identidad exhaustiva de vectores**.
- PDFs NUEVOS `V11-INTERNAL-PROOF-P4-dense-cantrips-A.pdf` SHA `e6ec29516bfd94aabb5dc72e60a6abcca3b289d581b92b606a67698405863af0` y B SHA `4b227a931285a35d6fb8db2170ac18f47176cd8f13ebd3d1d995bde8460ec6e8`: **10 páginas Carta cada uno**, P4 físicas 4 y 5, **125 + 50 = 175** trucos fuente únicos, cero pérdida/repetición, cabeceras y rótulos `TRUCOS / NIVEL 0 / CONTINUACIÓN` en columnas 2/3 de la primera P4 y 1/2 de la segunda, nunca se etiquetan como nivel 1, NOTAS final sin conjuros. Se rasterizaron y examinaron visualmente las franjas de páginas 4–5 A/B; sin choques/cortes visibles en esa zona.
- No es prueba de Android instalado ni inspección completa de las 38 hojas frente al golden. Cualquier discrepancia posterior se vuelve a examinar sobre candidato exacto.

## Gate y siguiente paso

**PASS LOCAL G3 de este caso únicamente**. Los gate-checks antiguos contabilizados **12 PASS de etapa** y **105 reglas globales NOT_VERIFIED** permanecen sin modificación; G4 **BLOCKED**, G5/G6 pendientes. `active_candidate=null` y ledger V11/M50800 sin estados inventados. G0 QA original Mara runtime incompleta (PDF del propietario ausente), no usar este lote para declararla cerrada. RCR-1 no activado. PR #173 sigue DRAFT/unmerged; no firma ni nueva identidad de APK owner por ser solo un test.

**Continuación segura:** pasar a una brecha **distinta** del contrato aún no ejercida en G1/G2/G3 o comenzar reconciliación G4 (105 reglas, 48 §§ D-0076, 8 golden) con oráculo independiente y candidato exacto. NO repetir este caso, NO convertir PASS local en PASS global, NO enviar APK a QA del propietario, NO fusionar PR #173. Resolver primero las barreras documentadas de G4 y la falta de QA Mara primaria cuando aplique.
