# V11 — Retrato ausente P1: silueta de dos óvalos del golden aprobado

**Fecha:** 2026-10-18. Implementación exclusiva PR #173 (`docs/pc-sheet-v11-approved-implementation-handoff`, OPEN/DRAFT, sin merge). `main` y las cuatro familias previas permanecen intactos.

## Origen / compuerta de comprensión
Se reabrieron `docs/checkpoints/2026-09-26_PC_SHEET_MARA_PREQA8_PDF_VISUAL_QA_DEFECTS.md`, `2026-09-28_PC_SHEET_MARA_OWNER_REQA_RUNTIME_FAIL.md` y la matriz primaria M50800, junto al contrato V11 y golden PDF real. M50800-25 trata la **cinta de nombre en otra familia** y NO se reclama corregida. El original V11 `v11_ordinario_6mas3_variante_B.pdf` P1 presenta marco de 8 aristas recortadas (x39.685..119.055; y93.543..170.079), óvalo de cabeza (68.032..90.709; 102.898..125.575) y hombros (58.110..100.630; 130.394..160.441), relleno RGB 231/242/247 y borde fino. El PDF auténtico anterior `V11-INTERNAL-PROOF-Ilyra-B.pdf` P1 (artefacto #12146562393) exhibía **marco rectangular**, círculo y arco contorneados sin relleno, más rótulo RETRATO demasiado alto. La observación es una **brecha visual independiente** frente al diseño aprobado, no una nueva petición estética ni verificación de V11-QA-001/002.

## Cambio funcional delimitado
En los DOS renderizadores PDFBox exclusivos V11, se reemplaza solo la rama de **retrato sin bytes locales** por óvalos llenos de azul pálido en proporciones normalizadas del golden; mismo marco chamfered, regla decorativa y rótulo inferior. Se conserva la ventana actual de 104×100pt, las vías foto real FIT/CROP y sus bytes, los datos y el algoritmo de páginas. El color del caption se aísla con save/restore del estado gráfico para no colorear otras palabras. No cambia plantilla golden, encabezados generales, otras páginas ni `main`.

El inspector **independiente PyMuPDF** `scripts/audit_v11_portrait_placeholder_golden.py` compara en cada PDF de CI real Desktop el marco, dos figuras rellenas, su color y proporciones relativas al golden, sin inventar retrato donde existe imagen. Se integra al Scaffold y produce JSON con SHA de cada PDF; los regresores anteriores de 48 PDF, NOTAS final, Kotlin y Android DEBUG continúan operando.

**Estado al publicar el cambio:** CI exacto y comprobación de PDF aún pendientes; no anticipar PASS. Quedan G0 QA histórico PARCIAL, 105 reglas de matriz global `NOT_VERIFIED`, G4 BLOCKED, G5/G6 pendientes. No se ha instalado APK en Android, ni dado aprobación visual/técnica owner, ni fusionado PR #173. RCR-1 no activado.

**Siguiente:** verificar nuevo CI/artefacto, comparar P1 A/B y constatar que otros folios no cambiaron; documentar PASS/BLOCKED acotado. Después continuar brechas reales de contenido/geometría de módulos V11 y G4 sin repetir trabajos ya probados.
