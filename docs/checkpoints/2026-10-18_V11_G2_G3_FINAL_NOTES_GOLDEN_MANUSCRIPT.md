# V11 — Última hoja NOTAS: reparación basada en los siete golden completos

**Fecha:** 2026-10-18. **Rama única:** `docs/pc-sheet-v11-approved-implementation-handoff`, PR #173 DRAFT/sin fusionar. **G4 BLOCKED; G5/G6 pendientes; G0 QA histórico parcial.**

## Recuperación tras interrupción
El HEAD anterior `61f1c47ec1e866827b2d131d9eadddf4d88e012d` y Scaffold #39148491475 = SUCCESS (Kotlin, backend, database). La auditoría externa anterior de 48 PDF/466 páginas, 0 alertas de perímetro y artefacto #12144595619 estaba correctamente persistida; no se repitió ni reinició. ZIP original golden y ZIP de 48 PDF real Desktop del CI estaban en el entorno anterior, íntegros. Se recuperaron ocho PDFs aprobados, de los cuales **siete incluyen la última NOTAS**; `v11_comparador_C2_extremo.pdf` es solo comparador C2 y se excluye correctamente de este control.

## Defecto independiente contrastado contra el original
Fuente primaria visual revisada: última hoja de `v11_ordinario_6mas3_variante_B.pdf`, más los otros seis golden con final NOTAS, versus `V11-INTERNAL-PROOF-Ilyra-B.pdf` y demás PDF V11 Desktop de #12123599698. El contrato V11 exige fondo blanco/azul niebla, dos columnas rayadas y cuadrícula final de escritura manuscrita. **Golden:** 17 renglones por columna, 34 fondos alternados azul claro, 34 reglas azul gris 0.32pt, cuadrícula 34×20 (52 líneas internas 0.27pt, borde 0.45pt). **App anterior:** 15 renglones por columna y cuadrícula 33×22 a 1.0pt negro, sin fondos alternados. Es una desviación física verificable que el barrido anterior no detectaba, NO un nuevo diseño ni una observación QA de propietario.

## Reparación acotada
El bloque final `PDPageContentStream` en los DOS renderizadores Kotlin V11 se ajustó a la geometría/color de los vectores de los golden reales: 17 renglones por columna con bandas alternadas, cuadrícula completa 34×20, líneas y marcos azules de espesor medido. No cambia fuente de datos ni identidad, paginador, otras páginas, cabecera, golden, cuatro familias anteriores ni `main`. La corrección se limita a G2/G3 último folio.

`scripts/audit_v11_final_notes_golden.py` es un **oráculo externo PyMuPDF**: extrae el PDF V11 aprobado del ZIP ya versionado en Git; compara las 121 primitivas de manuscrito por tipo, coordenadas, color, espesor contra el último folio de cada PDF real de App y publica JSON de hashes. Corre en Scaffold *después* de generar PDFs Desktop, junto al control de 48 PDF existente; falla cerrado si falta un PDF, hay cambio de geometría o se modifica el golden. La prueba anterior no detectaba la gramática visual; esta prueba sí la captura. La última hoja conserva exclusivamente NOTAS, sin contenido digital.

**Compilación y prueba reales del nuevo HEAD:** todavía pendientes al emitir este checkpoint; verificar CI y su artefacto antes de declarar PASS de este cambio. No presentar G4 general como aprobado, no actualizar las 105 reglas `NOT_VERIFIED` sin evidencia del candidato exacto ni tratar este PDF Desktop como QA del Android instalado. QA V11-QA-001/002 y 32 M50800 no se cierran, RCR-1 global sin recuperación estructural. Si falla el check, revisar razón específica sin abrir diseño ni nuevo branch.

## Siguiente paso
Verificar Scaffold exacto, descargar el JSON de comparación y dos PDF A/B reales para inspección visual del último folio; ante PASS de este slice, continuar G4 con comparación de otros módulos respecto de ocho golden y D-0076, 105 reglas pendientes, QA Android/legado; sin G5 ni merge.
