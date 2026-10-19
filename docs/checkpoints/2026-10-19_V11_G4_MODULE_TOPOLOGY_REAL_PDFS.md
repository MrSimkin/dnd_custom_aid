# V11 G4 — control independiente de topología física de módulos

**Fecha:** 2026-10-19 (Chile). **Rama exclusiva:** `docs/pc-sheet-v11-approved-implementation-handoff` / PR #173 DRAFT. **NO MERGE.**

## Motivo y evidencia anterior
El checkpoint precedente `2026-10-19_V11_G2_G3_CN3_NOTE_BOUNDARY_CONTINUATION.md` registra PASS LOCAL de bandas y continuidad de notas digitales: Scaffold #39193379904, QA #39193380450 y 64 PDF auténticos Desktop/589 hojas. Es evidencia ya lograda, no se repite como trabajo nuevo. El diseño fuente de `CONTRATO_VISUAL_APROBADO.md` fija P1→P2→P3, P4 opcional, C2, C-N3 digital exclusivo, Libro opcional y NOTAS manuscrita **única y final**. `PcSheetV11PageSequence.kt` mantiene ese mismo orden lógico; el inspector anterior comprobaba solo los primeros roles y el final, sin rechazar intercalaciones en hojas intermedias.

## Nuevo bloque ejecutado
- `scripts/audit_v11_module_topology.py`: oráculo externo (PyMuPDF) que extrae el **título impreso** de cada folio PDF real, clasifica ocho roles, exige P1/P2/P3 iniciales, progresión monotónica, C2 antes de C-N3, C-N3 antes de Libro y una única hoja NOTAS final. Rechaza títulos desconocidos para evitar un falso verde, permite continuaciones P3 contiguas y páginas C2/C-N3/Libro múltiples.
- `scripts/test_audit_v11_module_topology.py`: casos positivos y negativos de intercalación C2↔C-N3, Libro↔C-N3, NOTAS no final, reentrada P3 y rol desconocido.
- `.github/workflows/scaffold-check.yml`: tras `gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build`, ejecuta test y auditor contra `desktopApp/build/pc-sheet-proofs/V11-INTERNAL-PROOF-*.pdf` (mínimo 48); publica `v11-module-topology.json` con títulos por hoja, hashes SHA256, resultado y errores en el artefacto independiente ya existente.
- Commits: `e4d47f0`, `db279d1`, `ee09845`, `e38e98a`, `917179b`. Se preservan contrato, matriz de 105 reglas, registro de propietario, golden y los renderizadores Kotlin: este lote aumenta **verificación funcional**, sin alterar el PDF producido.

## Aceptación y estado de ejecución
Al publicar este checkpoint, **NO hay resultado CI confirmado del nuevo auditor**: no declarar PASS de este bloque hasta identificar run exacto del último HEAD, leer resultado y revisar el JSON contra PDF reales. Si el nuevo control descubre divergencia, verificar título real/página y origen antes de cambiar producción; no relajar el contrato por un test rojo ni gastar RCR-1 artificialmente.

**G0:** QA Mara histórico parcial. **G1:** PASS semántico. **G2/G3:** implementación y pruebas locales por módulos, no aprobación global. **G4:** BLOCKED, 105/105 reglas contractuales `NOT_VERIFIED`, `active_candidate=null`. **G5/G6:** pendientes, sin Android instalado ni aprobación owner. RCR-1 0/2, PR #173 no fusionada. La compuerta de 105 reglas NO se actualiza por este smoke; no afirmar conciliación de ocho golden o D-0076 completa.

## Siguiente paso seguro
1. Verificar CI del HEAD exacto de este bloque, inspeccionar `v11-module-topology.json` y el primer PDF que falle, si lo hay; reparar el auditor solo ante falso positivo demostrado por salida auténtica.
2. Conciliar **módulos completos** P3/C2/C-N3/Libro con PDF reales y los ocho golden por fuente→sección→folio, detectar brechas físicas sustantivas y cubrirlas por lote, con Android equivalente.
3. Solo cambiar estado de las reglas G4 mediante evidencia individual vinculada al mismo candidato y auditoría independiente; mantener STOP antes de G5/owner QA/merge.
