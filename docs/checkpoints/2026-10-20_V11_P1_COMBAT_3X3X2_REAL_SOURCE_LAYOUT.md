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

## Prueba exacta del nuevo panel (CI y PDF reales) — PASS LOCAL restringido

El commit **funcional** `1cad2971b38610f3de7c605c0d23061950b7b393` terminó [Scaffold #39490469236](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39490469236) **SUCCESS** (Kotlin Desktop tests/Android build/APK, PDF 76 casos, auditores físicos, backend y DB), junto a [QA traceability #39490469224](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39490469224) **SUCCESS**. PDF App auténticos [artifact #12295018331](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39490469236/artifacts/12295018331) ZIP SHA256 `5e49034fb44e3404215e357ada25e3e4fc12a0bb1fd4cf2eb22d30fe8782424b`; auditor físico [#12294869361](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39490469236/artifacts/12294869361) ZIP SHA `32836714c361324e6df0269cb13fa6159de5ce6a3427796f249f8e401ecd6d8c`.

Comparación externa PyMuPDF con [PDF de base P1 C2c anteriores](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39487245834/artifacts/12294302755): **76/76 mismos documentos y conteos de página, 711/711 folios conservados**, 76/76 P1 mismas palabras y coordenadas **fuera** del panel de combate; 76/76 paneles preservan todo su contenido fuente como multiconjunto de palabras aunque cambien las posiciones legítimamente; **635/635 páginas posteriores a P1 idénticas en texto/coordenadas y vectores**. El auditor físico actualizado sobre la salida exacta verificó 76/76 PDF, **304 marcos identidad y 608 celdas combate** en la NUEVA gramática 3+3+2, rótulos fuente en casilla correspondiente, 0 errores. Mara B p1 inspeccionada visualmente; ocho valores reales visibles y panel 3+3+2 sin invadir tabla de ataques. SHA256 del informe diferencial local `239e6765dcf493293c52cc2284b51ad6bb972ac922475198e3a0ed7cde465ff2`. [Acta de evidencia JSON completa](../pc-sheet-v11/qa/evidence/2026-10-20_V11_P1_COMBAT_3X3X2_PDF_COMPARISON.json).

**Límites:** este es **PASS LOCAL subalcance P1**; **NO es PASS formal de ninguna regla G4** ni de toda la paridad golden. El mockup usa INSPIRACIÓN (sin fuente real), mientras App preserva PV TEMP. El resto de P1 aún necesita conciliación visual integral. Android solo compilado, NO instalado ni aceptado por propietario. Matriz G4 sigue **105/105 NOT_VERIFIED** con `active_candidate=null`; G0 parcial, G1 semántico PASS, G2/G3 parciales, G5/G6 pendientes; C211 intacto BLOQUEADO 3/3 normales, RCR-1 estructural 0/2. PR #173 DRAFT NO MERGE.

**Siguiente:** otro bloque funcional verificado frente golden/D-0076 fuera de C211, o plan de reconciliación de los componentes no aprobados; nunca repetir solo auditorías ni reclamar aprobación del usuario.
