# V11 G3 — C206: referencias físicas de C2 por origen efectivo, sin saltar el plan

**Fecha:** 2026-10-16 (Chile). **Plan rector:** `docs/pc-sheet-v11/PLAN_TECNICO_IMPLEMENTACION_V11.md` original 2026-10-09, con la ampliación obligatoria del 2026-10-16 de verificar 100 % del contrato ANTES del QA del propietario. No se cambió el plan G0→G6, ni el golden, ni la UX aprobada. Ruta: `main/AGENTS.md → RESUME.md → main/LATEST.md → main/docs/checkpoints/2026-10-09_PC_SHEET_V11_IMPLEMENTATION_RESUME_ROUTE.md → PR #173`. PR DRAFT/OPEN/UNMERGED.

## Defecto verificable de C206 / causa raíz

En los dos renderizadores PDFBox V11, la lógica de footer vinculaba cualquier sección rotulada `C2 - Habilidades, acciones y recursos` a la página base P1, **aunque los registros de esa página fueran únicamente Rasgos mecánicos diferidos desde P2**. Se obtenía un enlace falso P1→C2 y un backlink `VIENE DE P1` sobre un C2 cuya única autoridad era P2. El error provenía de usar el **título de sección** para deducir procedencia en vez de los `sourceKey` físicos de `PcSheetV11PlacedLineCell`. Se trató como defecto de navegación real del contrato aprobado, no como solicitud estética.

## Implementación real

Commit [`d25c2824a90d4d19fa9ac3794f2621ea6b4ab4b3`](https://github.com/MrSimkin/dnd_custom_aid/commit/d25c2824a90d4d19fa9ac3794f2621ea6b4ab4b3) en los renderizadores V11 aislados Android y Desktop. Durante la emisión de cada página C2 mixta se registra por separado qué páginas contienen origen P1 y cuáles contienen origen P2 (incluida la cartela real `c2-heading:traits`). Las referencias de ida apuntan a la **primera página real de cada origen**; los backlinks se imprimen solo en la página física en la que empieza efectivamente esa procedencia. Los enlaces `ANT.` y `SIG.` siguen según páginas reales; los C2 dedicados a Equipo/Especial siguen enlazados a P2. No se toca mapeo, datos, flujo, métricas, páginas, tipografías, legacy, backend ni golden.

Tests nuevos:
- `g3OnlyP2TraitsInMixedC2NeverInventAP1Backlink`: respaldo real de Aldren con **118 Rasgos auténticos sintéticos diferenciados por UUID**, mientras se vacían componentes no relacionados que generarían continuaciones P1. A/B deben generar C2 solo desde P2, jamás enlace ficticio P1; último ID visible; texto de descripción/notas digitales de rasgo no se imprime; última hoja NOTAS.
- `g3MixedC2ActualAldrenIlyraMaraNavigationKeepsTrueP1AndP2Origins`: Aldren A, Ilyra B, Mara A/B, garantía de que los backlinks previamente auténticos siguen intactos.
- Guard `scripts/test_v11_c2_source_navigation_contract.py` verifica paridad fuente entre PDFBox Android/Desktop y registro de ambos tests.
- Matriz `qa/MATRIZ_CONTRATO_V11_PRE_QA.json`, regla `C206.stage_checks.G3`: PASS **solo de esta etapa**, con evidencia del commit y CI exactos. `C206.stage_checks.G2` sigue `NOT_VERIFIED`; `C206.status` global continúa `NOT_VERIFIED` y `gate=BLOCKED`. No hay promoción masiva ficticia.

## CI y examen físico independiente

**Scaffold [#38985980989](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38985980989): SUCCESS**, jobs Kotlin/Android debug, backend, hosted database; QA traceability [#38985980924](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38985980924): SUCCESS. PDF generado por el servicio Desktop real, [artefacto #12083292410](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38985980989/artifacts/12083292410), ocho PDF Carta, ZIP CRC PASS y SHA-256 `a28abede505c59df652574fb2aec9885e1e742505ac4324eb9ee561d6a36225b`.

Inspección independiente en PyMuPDF comparó los seis PDFs habituales previos en [artefacto #12082570208](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38983178486/artifacts/12082570208) contra los nuevos **página por página, palabra por palabra con posición y vectores**:
- Aldren A: 7→7 páginas; Aldren sin Especial A: 7→7; Ilyra B: 13→13; Mara A: 9→9; Mara B: 14→14; P2 sin Equipo/Tesoro: 6→6. **CERO** diferencias de texto, coordenadas de palabras o geometrías PDF, incluidos footers normales.
- Nuevos PDFs `V11-INTERNAL-PROOF-P2-only-traits-C2-A/B.pdf`: 7 páginas cada uno; página 1 sin enlace falso a C2; P2 página 2 anuncia `CONTINÚA EN C2 PÁG. 4`; página 4 anuncia `VIENE DE P2 PÁG. 2 | SIG. 5`, sin `VIENE DE P1`. Los **118** nombres aparecen y el nº 118 se conserva; ningún sentinel de descripción/notas digital aparece en Rasgos; última página `Aldren Vale / NOTAS` sin datos digitales. PDFs exclusivamente Carta 612×792.
- SHA-256 de los PDF de estrés: A `8f057d131f859657441a205c8f12a08f06d577a0f50c9472d36d900a25749fbe`; B `751db148de5d1aaada0700c05bf6907e76da638ccb99dda7d08fb934eef5c235`.

## Continuidad sin pérdida ni retroceso

**Conservar plan original G0–G6 y prioridad funcional:** G0 histórico Mara parcialmente pendiente de evidencia runtime propietaria; G1 PASS solo semántico; G2 parcial; G3 PDF nativo real con pruebas parciales exitosas; G4 = BLOCKED y reconciliación pendiente de D-0076 §§3.1–3.48, ocho goldens, reglas individuales, cuatro legacy y Android instalado/candidato exacto; G5/G6 no autorizados. No confundir **G3 C206 local** con cierre de G3 global ni dueño QA. Matriz mantiene 105 obligaciones y QA 32 M50800 + V11-QA-001/002 preservados; RCR-1 0/2. No alterar `main`, no entregar APK, no publicar ni fusionar.

**Siguiente trabajo seguro:** seguir implementando brechas funcionales demostradas contra contrato; particularmente auditar integridad de contenido, vínculos de C2 dedicados y denominaciones/valores reales; añadir evidencias específicas durante cada slice. Mantener los gates abiertos visibles y preparar auditoría global G4 sin paralizar el código.
