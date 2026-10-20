# V11 — P1 combate 3+3+2 preservando ocho fuentes (2026-10-20)

**Autoridad:** [PR #173](https://github.com/MrSimkin/dnd_custom_aid/pull/173), rama `docs/pc-sheet-v11-approved-implementation-handoff`, base `06d81c4fc484ef8efa2081b92cf53dde7b88b115`. Scaffold y QA del HEAD anterior **SUCCESS** (#39488428596, #39488428580); main `0c009f10d5e66880d68fa3732491c6fc3e172f22`.

## Diferencia primaria

Golden aprobado ZIP SHA256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a`, `v11_extremo_variante_B.pdf` p1: COMBATE tiene **tres tarjetas fila superior, tres fila media y dos PV anchas**. App real `V11-INTERNAL-PROOF-Mara-B.pdf` p1 del [artefacto #12294302755](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39487245834/artifacts/12294302755) todavía tiene ocho tarjetas 2×4. No confundir con el paquete anterior, que ya probó los contornos sin modificar la distribución.

## Cambio funcional y frontera

Renderers reales Android y Desktop pasan a disposición **3+3+2** en el rectángulo original `x312..570; y503..591` (28+28+32 puntos, 2.4pt de aire, Barlow Condensed encabezados/Fira Sans valores verdaderamente medidos):
- fila 1: CA, INICIATIVA, VELOCIDAD;
- fila 2: COMPETENCIA, **PV TEMP real**, DADOS;
- fila 3: PV ACTUALES y PV MÁXIMOS en dos tarjetas anchas.

La maqueta tiene INSPIRACIÓN, **no presente entre estas fuentes persistidas**: se conserva PV TEMP sin inventar inspiración ni perder datos. Se ajusta la derivación completa al C2 físico existente de valores demasiado anchos, 74pt banda estrecha y 117pt ancha; no reducción de fuente ni texto recortado. Los demás módulos y cuatro exportadores legacy NO se modifican.

Auditor P1 PyMuPDF existente **actualizado (no duplicado)** para verificar contornos y fuentes impresas dentro de su tarjeta, además de los cuatro marcos identidad en cada PDF App (mínimo 48). Se añade UNA prueba Kotlin Desktop A/B con dos personajes fuente reales para comprobar ocho campos y ausencia de inspiración artificial; conservamos pruebas previas y gates.

**Estado inicial:** código publicado, **CI del commit y comparación PDF exacta PENDIENTES**. Descargar 76 PDF reales al cerrar Scaffold; comparar física 3+3+2, rótulos, texto íntegro/páginas, derivaciones C2; registrar sin fabricar PASS. Rechazar errores observados, no repetir C211.

**Gates:** G0 parcial, G1 semántico PASS previo, G2/G3 parciales, **G4 BLOCKED 105/105 NOT_VERIFIED y active_candidate=null**, G5/G6 pendientes, propietario sin QA técnica/visual, Android instalado no comprobado. C211 normal 3/3 BLOCKED, RCR-1 estructural 0/2, PR DRAFT SIN MERGE.
