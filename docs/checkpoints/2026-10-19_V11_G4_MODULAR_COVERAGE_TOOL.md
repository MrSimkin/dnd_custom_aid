# V11 G4 — inventario modular ejecutable, sin promoción de reglas
**Fecha:** 2026-10-19. **PR exclusiva:** #173, DRAFT; no merge. **Base funcional:** C-N3 PASS LOCAL, checkpoint `2026-10-19_V11_G2_G3_CN3_NOTE_BOUNDARY_CONTINUATION.md`.

## Resultado de esta sesión
Se incorporó `scripts/check_v11_g4_module_coverage.py` como herramienta de lectura **sin mutación** de `qa/MATRIZ_CONTRATO_V11_PRE_QA.json`, y `tests/test_v11_g4_module_coverage.py` (cinco casos: pendientes no promocionados, selección de área/etapas, área inválida, PASS falso sin evidencia y duplicados). Presenta por área IDs sin PASS, estados y stage_checks G1–G4; no infiere cumplimiento de CI verde ni modifica JSON. `--strict` falla deliberadamente si falta la revisión G4 para un candidato.

**Modo de operación desde raíz del repo:**
```sh
python3 scripts/check_v11_g4_module_coverage.py --area P3
python3 scripts/check_v11_g4_module_coverage.py --area C-N3
python3 scripts/check_v11_g4_module_coverage.py --area C2
python3 -m unittest discover -s tests -p 'test_v11_g4_module_coverage.py'
python3 scripts/check_v11_pre_qa_gate.py --repo-root . --mode inventory
```

## Alcance probado vs pendiente
El inventario remoto leído antes del cambio tenía **105/105 reglas NOT_VERIFIED** (Global 12, Entrada 8, P1 20, Rasgos 6, P2 12, P3 6, P4 10, C2 9, C-N3 4, Libro 6, Final 4, PreQA 8). Este cambio **no** cambia el estado de ninguna regla, candidato o QA. Los 8 golden y las 48 secciones históricas requieren todavía conciliación física independiente. Los 64 PDF reales Desktop/589p del bloque previo siguen siendo **evidencia parcial**, no comparaciones exhaustivas por regla/candidato Android instalado. **No se ha obtenido un PASS nuevo de G4.** Tests/CI de este commit requieren verificación de ejecución en el HEAD exacto; no afirmar éxito anticipadamente.

## Gates y siguiente paso
G0 QA histórico PARCIAL; G1 semántico PASS; G2/G3 parciales; **G4 BLOCKED**, `active_candidate=null`, 105 NOT_VERIFIED; G5/G6 pendientes. RCR-1 0/2. Original M50800 y V11 QA intactos.

**Siguiente bloque funcional:** utilizar informe modular para cotejar P3 y C-N3 primero con los 8 golden y PDFs App reales, registrar para cada regla hallazgos por ID y candidato; luego C2. Si se detecta defecto probado, corrección Kotlin Android+Desktop con prueba roja y nueva evidencia de salida, sin fingir cierre de QA propietario. Validar workflow exacto de los commits actuales y cualquier error de la nueva herramienta antes de adjudicar resultado.

## Continuación 2026-10-19 — corrección funcional del filtro estricto G4

El comando `--strict --area P3` heredaba una dependencia de `ready` global: incluso con todas las reglas P3 conformes, retornaba fallo porque G4 global seguía BLOCKED. Se corrigió en `scripts/check_v11_g4_module_coverage.py` para que el modo estricto con `--area` compruebe **solo IDs de esa área**, mientras el modo estricto sin filtro sigue exigiendo todo el gate/candidato. El JSON ahora distingue `selected_area_clear` (contabilización local) de `ready` (autorización global, que continúa falsa). Se añadió una sexta prueba unitaria de regresión. Esto permite cierres técnicos modulares SIN promover el release ni marcar un ID PASS por sí solo.

**Alcance:** corrección real de herramienta operativa, **no cambio del renderer ni resultado visual V11**. La ejecución CI de los commits nuevos no estaba aún acreditada al publicar; seis casos de prueba son código escrito, no seis resultados verdes. 105/105 reglas permanecen `NOT_VERIFIED` y `active_candidate=null`, por lo que ninguna área está aprobada hoy. RCR-1 intacto, G0/G1/G2/G3/G4/G5/G6 sin transición, PR #173 sigue DRAFT/no merge.
