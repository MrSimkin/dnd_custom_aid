# V11 G3 — Corrección puntual de HABILIDADES (P1, variante B)

**Fecha:** 2026-10-16. **Ámbito:** PR #173, rama exclusiva `docs/pc-sheet-v11-approved-implementation-handoff`. **Autoridad:** `CONTRATO_VISUAL_APROBADO.md` (C2c y variante B), golden ZIP SHA256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a`; reglas existentes **GLB02 / P107** de `qa/MATRIZ_CONTRATO_V11_PRE_QA.json`. No abrir diseño estético, ni crear una norma nueva por un detalle que las anteriores ya abarcan.

## Diagnóstico físico y comprensión antes de editar

El golden real `v11_ordinario_6mas3_variante_B.pdf` P1 muestra **HABILIDADES** con cartela azul niebla C2c de esquinas recortadas. El PDF **real** previo de Desktop App `V11-INTERNAL-PROOF-Ilyra-B.pdf`, extraído del artefacto `12076089298` de Scaffold SUCCESS `38969460423`, tenía el encabezado como texto flotante sin cartela, entre el último atributo y el cuadro de habilidades.

Geometría medida del caso previo (PDF origen superior): palabras `HABILIDADES`, `P=COMPETENCIA`, `E=PERICIA` en y442..456.4; el cuadro de Habilidades comienza en y464 (PDF box x42..300) y el último atributo termina aprox. y443. No desplazar o comprimir datos de habilidades para obtener la cartela, ni falsear la anchura real del golden. No se requiere otra elección del propietario: está aprobado.

## Cambio técnico en código productivo

Dos renderizadores aislados, Desktop y Android, en `drawP1BIndependentSkills` (no aplica a variante A):

- Añadir **una** cartela de ocho lados, relleno RGB(231,242,247), trazo RGB(38,75,104), caja física PDF x42..300/y330..348 (top-origin y444..462), cortes de 4pt;
- Mantener el mismo rótulo y leyenda `HABILIDADES  P=COMPETENCIA  E=PERICIA`; mover **solo el encabezado** de x43/y338 a x51/y335 para garantizar márgenes legibles;
- Mantener sin cambio el cuadro fuente PDF x42..300/y105..328, filas, casillas, dos columnas, líneas manuscritas, IDs, orden, overflow C2, contenido de las cuatro familias antiguas, P3 y paginación.

Cambios productivos: `9bb09a9919865d6b140503d40308df13adf6cce7` (Desktop) y `862eea6201efd1d5ea9cdac211e40732b02a2951` (Android).

Prueba Desktop que exporta PDF **real de la App**: `g3RealP1VariantBSkillsHeaderHasApprovedBlueClippedCartelaWithoutMovingSkillRows` en `DesktopPcSheetV11RuntimePdfProofTest.kt` (`291da14e2b9f9e569d0ecd56f3f427a1672c6552`), sobre Ilyra B y Mara B. Comprueba operador físico de ocho vértices, cuadro original, leyenda y una habilidad fuente. Guardia adicional de paridad Android/Desktop `scripts/test_v11_p1_skills_cartela_contract.py`, activada en QA ledger por `6da0dcd3d801b91e3ff0c6888255b660acc457e7`.

## Verificación antes del próximo trabajo

- QA ledger para HEAD `6da0dcd3`: verificar run `38971731506`. Este control garantiza sintaxis/identidad de pruebas, no adecuación de PDF.
- Scaffold exacto `38971731677`: comprobar resultado Kotlin/Desktop + Android DEBUG, backend y hosted-db. **Si sigue ejecutándose: NO PASS**.
- Si PASS: recuperar `v11-internal-real-app-pdf-proofs` de ese run, comparar contra real anterior `12076089298` usando inspector independiente (fuentes, geometría vectorial, texto, posiciones en TODAS las páginas, contadores, última hoja). Solo deberían cambiar P1 B Ilyra/Mara: una cartela + reposición deliberada de las palabras del rótulo, ninguna otra palabra/página. A también debe permanecer idéntica.
- Una vez verificado, seguir siguiente sección **por el procedimiento G0–G6 vigente**, sin repetir slice, sin marcar P107 ni GLB02 global PASS (penden muchos escenarios/fuentes), sin promover G4. QA owner V11-QA-001/002 OPEN, 32 M50800 históricos intactos, RCR-1 0/2.

**Estado global:** G4 BLOCKED; G5/G6 no aceptados. PR #173 sigue DRAFT/unmerged, main y las cuatro familias previas intocadas.
