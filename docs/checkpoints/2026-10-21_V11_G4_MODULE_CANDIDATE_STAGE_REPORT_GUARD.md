# V11 G4 — no despejar un módulo por etiquetas PASS sin pruebas del candidato

**Fecha:** 2026-10-21. **Rama única:** `docs/pc-sheet-v11-approved-implementation-handoff` / [PR #173](https://github.com/MrSimkin/dnd_custom_aid/pull/173) DRAFT, **sin merge**. `main` observado `0c009f10d5e66880d68fa3732491c6fc3e172f22`; rama base al iniciar `27106b554081f0772b28f866e52760f4bd6b9b93`.

## Hallazgo concreto (G4, no cambio estético)

El informe operativo `scripts/check_v11_g4_module_coverage.py` calculaba `selected_area_clear=true` cuando todas las reglas del área tenían **estado** PASS, incluso con sus `stage_checks` G1/G2/G3 sin verificar, sin misma identidad de candidato o sin comparar las 48 decisiones y ocho PDF golden. Además, `--strict --area P3` se fijaba solo en la cantidad de reglas no-PASS: podía devolver éxito a pesar de esas condiciones faltantes. El guardia normativo `check_v11_pre_qa_gate.py --mode release` YA impedía promoción real, pero el informe auxiliar era engañoso.

## Cambio funcional seguro en el control de cumplimiento

- El informe muestra por módulo `unverified_candidate_stage_ids` y separa `selected_area_rules_pass` (solo estado nominal) de `selected_area_clear` (pruebas locales del mismo candidato y conciliación completa).
- El resumen requiere el mismo `source_commit` y `apk_sha256` entre matriz y pruebas, checks por etapa con evidencia del commit exacto, conciliación completa y 48/8 entradas revisadas antes de mostrar claridad. El estado `ready` también falla cerrado ante estas faltas.
- `--strict --area` ahora retorna fallo cuando el módulo aún no está realmente verificado, incluso si sus etiquetas de reglas dicen PASS.
- Ocho tests unitarios positivos/negativos en `tests/test_v11_g4_module_coverage.py` y ejecución integrada en `pc-sheet-v11-qa-ledger.yml`. El informe **NO sustituye** el validador de liberación que exige evidencias más profundas, igualdad con QA owner y auditores físicos independientes.

## Estado y continuidad, sin falsa promoción

En el HEAD inicial la matriz mantiene **105/105 reglas NOT_VERIFIED**, 48 decisiones y ocho PDFs sin reconciliar, `active_candidate=null`, G4 BLOCKED. G0 histórico PARCIAL, G1 semántico PASS anterior, G2/G3 parciales, G5/G6 pendientes, C211 permanece BLOCKED (3/3 intentos normales; RCR-1 estructural 0/2). Ningún estado QA, contrato, golden o renderer se modifica.

**Evidencia pendiente:** comprobar CI del commit que incluya esta intervención (QA traceability con ocho tests y Scaffold checks). No afirmar PASS del cambio sin run exacto. Después: auditoría física modular de una diferencia *todavía abierta* frente a V11/D-0076 con PDF auténtico, y solo entonces corregir código de exportación; no reabrir C211 sin su p6 original. Conservar PR DRAFT y no fusionar.
