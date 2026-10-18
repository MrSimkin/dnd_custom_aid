**2026-10-18 — PR #173 V11 P4 origen multilineal:** código Android/Desktop `8b57bae` + CI real [Scaffold 39171821142](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39171821142) SUCCESS, dos PDF nuevos A/B y 54 regresiones comparadas, informe en `docs/checkpoints/2026-10-18_V11_G2_G3_P4_SOURCE_CONTROL_BREAKS.md`. PASS LOCAL acotado; G4 BLOCKED 105 reglas NOT_VERIFIED, G5/G6 sin PASS, owner QA pendiente, sin merge.

**2026-10-18 — V11/PR #173 P4 fuente real multilínea:** cambios Kotlin solo de selección de carril de origen en ambos exportadores + prueba Desktop real A/B. CI de cambio todavía pendiente; G4 BLOCKED, 105 reglas NOT_VERIFIED, RCR-1 no consumido, QA owner y G5/G6 pendientes. Ruta actual: `docs/checkpoints/2026-10-18_V11_G2_G3_P4_SOURCE_CONTROL_BREAKS.md`.

**2026-10-18 — PR #173 V11:** control de visibilidad física del logotipo **PASS LOCAL** en `0af6b2c`, [Scaffold 39169494332](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39169494332) SUCCESS (54 PDF/510 folios; prueba negativa sintética PASS). Referencia de continuidad: `docs/checkpoints/2026-10-18_V11_G4_HEADER_LOGO_VISIBILITY_PREFLIGHT.md`. Sin paridad visual completa, G4 BLOCKED/105 NOT_VERIFIED, G5/G6 pendientes, PR DRAFT/sin merge.

**2026-10-18 — V11 / PR #173 (preflight cabecera):** Auditoría de visibilidad real del logotipo agregada al CI, resultado del nuevo commit pendiente. Ruta activa `docs/checkpoints/2026-10-18_V11_G4_HEADER_LOGO_VISIBILITY_PREFLIGHT.md` vía LATEST; G4 BLOCKED y matriz de 105 reglas sin PASS global. PR abierta sin merge.

**2026-10-18 — PASS LOCAL marco doble físico V11 en TODAS las páginas:** [Código `6cb177a`](https://github.com/MrSimkin/dnd_custom_aid/commit/6cb177a6011cc4d38eec1c72f9a9142736d5497c), [Scaffold #39168510609](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39168510609) + QA documental #39168510698 SUCCESS. [PDF genuinos #12151881264](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39168510609/artifacts/12151881264): 54/54 PDF y 510/510 páginas con 2 marcos exteriores cotejados contra los **8 golden/59 páginas**, posiciones/color/grosor PASS. Inspector negativo previo rechazó 510/510; no regresión 54 PDF/510 páginas de texto/páginas/trazos fuera del marco; [auditores #12151578370](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39168510609/artifacts/12151578370): perímetro 0 errores, última NOTAS y retrato 54/54. [Checkpoint canónico](checkpoints/2026-10-18_V11_G2_G3_ALL_PAGES_DOUBLE_FRAME_GOLDEN.md). **G4 BLOCKED (105 NOT_VERIFIED), G5/G6 pendientes, sin APK instalado/QA owner ni merge #173.**

**2026-10-18 — V11 G2/G3 marco doble exterior, nueva paridad física con 8 golden:** corrección acotada en PDFBox Android/Desktop de 2 trazos de marco (geometría/grosor/RGB), sin tocar datos/paginación. Oráculo externo nuevo de las ocho referencias golden contra CADA folio PDF App, JSON SHA en CI; el candidato anterior dio 0/510 conformes (reproducción negativa). **Nuevo Scaffold PENDIENTE**, no anticipar PASS. G4 BLOCKED (105 reglas), G5/G6 pendientes, owner QA no otorgado, PR #173 sin merge. [Checkpoint canónico](checkpoints/2026-10-18_V11_G2_G3_ALL_PAGES_DOUBLE_FRAME_GOLDEN.md).

**2026-10-18 — PASS LOCAL P2/C2 monedas custom multilineales, ceros y orden fuente:** [Prueba App real `f632d811`](https://github.com/MrSimkin/dnd_custom_aid/commit/f632d81192ab90efed8c8860375d8a331a8361b4); [Scaffold #39165353061](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39165353061) y QA documental #39165353049 SUCCESS. [54 PDF #12150547330](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39165353061/artifacts/12150547330): A/B nuevos 8p, moneda `0` P2, otras etiquetas CRLF/TAB y 37/41/73 C2 p6, sin duplicación; 52/52 anteriores sin cambio de páginas/palabras/cajas/cantidades de dibujos/imágenes. [Auditoría #12149839134](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39165353061/artifacts/12149839134): 54 PDF/510p 0 alertas, 54/54 NOTAS y retrato. **Solo nuevo test, no cambio productivo; G4 BLOCKED (105 reglas NOT_VERIFIED), G5/G6 pendientes; sin QA owner ni merge.** [Checkpoint canónico](checkpoints/2026-10-18_V11_G2_G3_P2_CUSTOM_CURRENCY_MULTILINE_GUARD.md).

**2026-10-18 — V11 P2/C2 Tesoro custom: nueva prueba A/B de monedas reales con CRLF/TAB y saldo cero:** protección funcional del orden y monto por origen, usando el compositor ya implementado (sin modificación especulativa al renderer). PDF Desktop App genuinos de Aldren, cuatro monedas con IDs fuente distintos, cantidades 0/37/41/73, P2 y continuación C2, preservación exacta y último NOTAS. **CI exacto PENDIENTE**; no PASS hasta verificar. G4 BLOCKED, 105 reglas globales NOT_VERIFIED, G5/G6 pendientes, #173 sin merge. [Checkpoint canónico](checkpoints/2026-10-18_V11_G2_G3_P2_CUSTOM_CURRENCY_MULTILINE_GUARD.md).

**2026-10-18 — PASS LOCAL P1/P2/C2: municiones con CR/LF/TAB de origen.** [Candidato `3255b2a`](https://github.com/MrSimkin/dnd_custom_aid/commit/3255b2af88ffccaa7d27aa82120e3dbf4015c5d5), [Scaffold #39163833908](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39163833908) + [QA documental #39163833885](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39163833885) SUCCESS. Causa reproducida en primer CI: Fira `No glyph U+000A` en `PairedAmountRouting.p2Eligible` por medir antes de excluir salto; corregida la selección P1 y el enrutamiento temprano P2→C2 compartido. [52 PDF #12150042698](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39163833908/artifacts/12150042698): dos nuevos A/B 7p con nombres LF/TAB y cantidades 7/9 C2 p4, entrada corta sigue P1; 50/50 PDF anteriores sin cambios de páginas/palabras/cajas/cantidad de dibujos. [Auditoría #12150345958](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39163833908/artifacts/12150345958): 52 PDF/494p cero alertas, 52/52 NOTAS y retrato. [Checkpoint canónico](checkpoints/2026-10-18_V11_G2_G3_P1_AMMO_SOURCE_BREAKS_C2.md). **G4 BLOCKED (105 reglas globales NOT_VERIFIED), G5/G6 pendientes; sin APK instalado, sin QA propietario ni merge.**

**2026-10-18 — V11 P1 municiones con LF/TAB:** P1 no acepta nombres físicos de varias líneas aunque su ancho parezca caber; el registro completo de munición permanece en C2, nombres cortos siguen P1. Ambos PDFBox V11 corregidos; prueba A/B de aplicación real con backup Aldren, tres UUID/cantidades fuente (7/9/4), verifica P1/C2, datos sin mutar, última NOTAS. **Nuevo CI pendiente**, no PASS por anticipación; G4 BLOCKED y 105 reglas globales NOT_VERIFIED, G5/G6 pendientes, PR #173 sin merge. [Checkpoint canónico](checkpoints/2026-10-18_V11_G2_G3_P1_AMMO_SOURCE_BREAKS_C2.md).

**2026-10-18 — PASS LOCAL V11 ataques P1 multilineales/C2:** [código+prueba `fdcefcb5`](https://github.com/MrSimkin/dnd_custom_aid/commit/fdcefcb5ff3c1c467fb848ffce9f1c3bc08ca720), [Scaffold #39160188797](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39160188797) y [QA #39160188801](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39160188801) SUCCESS. [50 PDF App #12148523038](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39160188797/artifacts/12148523038), A/B nuevos 7p con fuente íntegra en C2 p4 y ataques normales P1; 48/48 previos sin regresiones de palabras/cajas/páginas/trazados contados. [Auditorías #12148458506](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39160188797/artifacts/12148458506): 50 PDF/480p 0 alertas, 50/50 NOTAS y 50/50 retrato. [Checkpoint canónico](checkpoints/2026-10-18_V11_G2_G3_P1_MULTILINE_ATTACKS_C2.md). **No G4 PASS: 105 globales NOT_VERIFIED; G5/G6 pendientes; sin APK instalado ni merge.**

**2026-10-18 — V11 P1/C2 ataques con CR/LF/TAB de origen:** filtro de una sola fila física en Android/Desktop V11: nombres, alcances o daños multilineales se conservan como ataques fuente completos en C2 en lugar de forzar P1; ataques ordinarios no cambian. Prueba PDF A/B real con Aldren más tres entradas difíciles y una normal, ocho marcadores/UUID/sin duplicar, y prueba de NOTAS final. **CI exacto pendiente**. G4 BLOCKED y 105 reglas globales NOT_VERIFIED; #173 sigue DRAFT/sin merge. [Checkpoint canónico](checkpoints/2026-10-18_V11_G2_G3_P1_MULTILINE_ATTACKS_C2.md).

**2026-10-18 — V11 P1 retrato sin foto: PASS LOCAL acotado.** [Código 5436e146](https://github.com/MrSimkin/dnd_custom_aid/commit/5436e146534e5cfa78f03bbbbf6ca66ec626d820): marco chamfered y silueta de dos óvalos azul pálido en Android/Desktop V11, con colores aislados y rutas Crop/Fit preservadas. [Scaffold #39157851734](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39157851734) + [QA #39157851694](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39157851694) SUCCESS. [Auditoría independiente #12148041854](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39157851734/artifacts/12148041854): 48/48 retratos vacíos en proporción golden, 48 PDF/466 páginas sin alerta, última NOTAS 48/48; 418 folios no-P1 sin cambios en trazados, P1 fuera del retrato conserva textos/colores. [Checkpoint canónico](checkpoints/2026-10-18_V11_G2_G3_P1_GOLDEN_NO_IMAGE_PORTRAIT.md). **G4 BLOCKED, 105 reglas globales NOT_VERIFIED; sin Android instalado, QA propietario ni merge de #173.**

**2026-10-18 — V11 P1 retrato vacío, golden source-native:** los dos renderizadores PDFBox V11 ahora muestran marco de esquinas recortadas, cabeza/hombros rellenos azul tenue y RETRATO abajo como el golden (sin modificar FIT/CROP). Inspector PyMuPDF independiente de marco/óvalos reales añadido a CI de 48 PDFs; evita falso PASS basado solo en compilación. **CI y verificación exactos PENDIENTES**; G4 BLOCKED, 105 reglas globales no aprobadas, G5/G6 pendientes, PR #173 sin fusionar. [Checkpoint canónico](checkpoints/2026-10-18_V11_G2_G3_P1_GOLDEN_NO_IMAGE_PORTRAIT.md).

**2026-10-18 — PASS LOCAL verificable, última NOTAS golden:** [commit 9a5ca96](https://github.com/MrSimkin/dnd_custom_aid/commit/9a5ca96fef68921052c3e2d4a717c6f786aa74b0), [Scaffold #39154527421](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39154527421) y [QA #39154527367](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39154527367) SUCCESS. [Auditoría #12146457937](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39154527421/artifacts/12146457937): 48/48 hojas finales igualan 121 vectores golden; 48 PDF/466 páginas sin alertas de perímetro. 48/48 otras páginas conservan texto/posiciones/conteo de trazados/imágenes; última NOTAS inspeccionada Ilyra B/Mara A. [Checkpoint canónico](checkpoints/2026-10-18_V11_G2_G3_FINAL_NOTES_GOLDEN_MANUSCRIPT.md). **G2/G3 PASS LOCAL solo de este folio; G4 BLOCKED con 105 reglas sin verificar, G5/G6 pendientes, sin Android instalado, QA owner ni merge.**

**2026-10-18 — V11 última NOTAS: diferencia golden real identificada y corregida en los dos renderizadores:** siete golden con NOTAS final contienen 17 renglones/columna con 34 bandas alternadas, más 34×20 cuadrículas finas azules. Los PDF App anteriores dibujaban 15 renglones y 33×22 cuadrículas negras a 1pt. Nuevo cambio restringido al último folio, oráculo independiente PyMuPDF por vectores reales (121 primitivas) en CI. **Scaffold exacto PENDIENTE**; no adjudicar PASS antes de las pruebas. [Checkpoint canónico](checkpoints/2026-10-18_V11_G2_G3_FINAL_NOTES_GOLDEN_MANUSCRIPT.md). G4 BLOCKED, 105 global sin verificación; PR #173 DRAFT/sin merge.

**2026-10-18 — Verificación CI completa del auditor G4:** [Scaffold #39147958117](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39147958117) **SUCCESS**, commit `a2b8568b9f8a71d8e758bf726a71d629e101e52b`; jobs Kotlin (incl. Android DEBUG y Desktop PDF), backend y base de datos **SUCCESS**. [Informe independiente #12144595619](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39147958117/artifacts/12144595619) leído de ZIP verificable: **48 PDF, 466 páginas, 0 alertas, 48/48 PASS solo del perímetro físico**; 48 PDF App [#12143809732](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39147958117/artifacts/12143809732). **Golden renovado** por reejecución exitosa de [#38482518998](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38482518998), [8 PDF + 2 PNG #12143757898](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38482518998/artifacts/12143757898), ZIP nuevo SHA `51ab3c75890b9fa5973e05e339ad0efdabf271d561f82e02a55062b7c80c9512`, CRC PASS y todos los PDF Carta. Sigue **G4 BLOCKED**, 105 reglas globales NOT_VERIFIED; esta auditoría NO sustituye cotejo golden/D-0076, Android instalado ni QA propietario. PR #173 DRAFT/sin merge.

**2026-10-18 — G4 barrido físico independiente de 48 PDF reales / 466 páginas:** comprobación PyMuPDF de Caja Carta, límites de texto, intersecciones sustantivas, tipografías, P1/P2/P3, folios y última NOTAS: **0 alertas local** sobre artefacto #12123599698; prueba negativa PASS. Nuevo auditor reproducible integrado al Scaffold de esta rama con JSON de hashes por archivo; **CI nuevo pendiente**, no G4 PASS ni aceptación Android/owner, 105 reglas globales sin verificar. [Checkpoint canónico](checkpoints/2026-10-18_V11_G4_INDEPENDENT_PDF_PHYSICAL_ENVELOPE.md). PR #173 DRAFT/sin merge; golden/main/legacy intactos.

**2026-10-18 — V11 C2 Equipo Especial: nombre multilineal / UUID:** [código y prueba `62ee971`](https://github.com/MrSimkin/dnd_custom_aid/commit/62ee971ff0ace1fedd19bb4969aaf7eaee76afd4). El nombre con CR/LF permanece completo en la tabla y la referencia de continuación usa el UUID persistido, no glifos CR/LF en rótulo. [Scaffold #39094514187](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39094514187) y [QA #39094514195](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39094514195) SUCCESS; [48 PDF App #12123599698](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39094514187/artifacts/12123599698) ZIP SHA `3c37e5ee1273ffa5f67f28df71032e2478d9ca82d557026c219f1e0e1c97a713`: A/B 12 páginas, C2 p7–10, ID real p8–10, marcadores nombre y descripción únicos; 46/46 PDF previos sin cambios físicos detectados. [Checkpoint canónico](checkpoints/2026-10-18_V11_G2_G3_C2_SPECIAL_MULTILINE_UUID_CONTINUATION.md). PASS LOCAL G2/G3 solamente, 105 global NOT_VERIFIED, G4 BLOCKED, G5/G6 pendientes, PR #173 DRAFT sin merge.

**2026-10-17 — V11 C2 Equipo Especial, descripción partida entre páginas:** [código V11 Android/Desktop + prueba `a7d3456`](https://github.com/MrSimkin/dnd_custom_aid/commit/a7d34560265709fc0d10fa6993083abfef0462bb) agrega **referencia al nombre fuente** en encabezado libre de cada C2 física que reanuda un mismo registro, con fallback al UUID real cuando haga falta; sin duplicar filas ni alterar cuadrícula NOTAS. [Scaffold #39090496826](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39090496826) SUCCESS; [46 PDF App #12122961164](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39090496826/artifacts/12122961164), ZIP SHA `4c24e7a376c71c8a16e077223d664653c8f76130fa59f8208ccaca457b93f2e7`, CRC PASS: A/B 13 páginas, especial C2 p7–11, rótulo de continuidad p8–11, texto inicial y final únicos, última NOTAS; 44 PDF anteriores sin cambios de palabras/posiciones/páginas/trazados/imágenes. [Checkpoint canónico](checkpoints/2026-10-17_V11_G2_G3_SPECIAL_EQUIPMENT_PHYSICAL_CONTINUATION.md). **PASS local G2/G3**, no QA owner/Android instalado; 105 reglas globales NOT_VERIFIED, 12 PASS etapa históricos; G4 BLOCKED, G5/G6 pendientes; SALVACIÓN y cuadrículas NOTAS ya presentes y no reimplementadas.

**2026-10-17 — V11 C2 atributo custom sobredimensionado entre columnas/páginas:** [código y prueba `b5c575d`](https://github.com/MrSimkin/dnd_custom_aid/commit/b5c575da0542beabe1f865d2fdf9b32dcbacc0b6) agrega referencia fuente en franja libre de C2 solo al continuar el mismo atributo mayor que una columna; sin reimprimir score/ID ni alterar renglones. [Scaffold #39089195856](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39089195856) y [QA #39089195908](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39089195908) SUCCESS exactos. [44 PDF reales #12122340801](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39089195856/artifacts/12122340801), ZIP SHA `b595fa1ad8076afef1133a3525d06a45fdba3644bf739ad348c580efd7107ae4`, CRC PASS: dos nuevos A/B con **140 habilidades únicas por variante** en tres columnas reales, contexto identificable en dos continuaciones, 11 páginas Carta, última NOTAS; **42/42 PDF anteriores sin variación en firma física examinada**, franjas A/B visualmente inspeccionadas. [Checkpoint canónico](checkpoints/2026-10-17_V11_G2_G3_C2_OVERSIZED_ATTRIBUTE_CONTEXT.md). **PASS G2/G3 local**, no aceptación Android instalado/propietario; 105 reglas globales NOT_VERIFIED/12 stage PASS históricos, G4 BLOCKED, G5/G6 pendientes, `main`/golden/legacy intactos; PR #173 DRAFT sin merge.

**2026-10-17 — V11 C2 grupos atributo+habilidades que caben:** [código `cc00a8f7`](https://github.com/MrSimkin/dnd_custom_aid/commit/cc00a8f729b7e35ef22737068994baeefec86ccf), compilación corregida [`43d77f8`](https://github.com/MrSimkin/dnd_custom_aid/commit/43d77f879166c9cfb16fcd588c6926d654ee3f20): shared flow y renderers V11 Android/Desktop mueven juntos grupos medidos que caben en una columna. [Scaffold #39087923925](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39087923925) y [QA #39087923919](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39087923919) SUCCESS exactos. [42 PDF reales #12121592973](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39087923925/artifacts/12121592973) ZIP SHA `9444666cad1cf9746b20aa43b3ab71b05497f4632719eafadb7798f4c6aef342`: dos nuevos A/B 11 páginas, 72/72 grupos fuente por variante mismos columna/página, 40 PDF previos sin diferencia detectada de texto/posición, número de páginas/trazados/imágenes. Primeras dos CI FAIL por misma etiqueta de bucle ya reparada, documentadas sin ocultar. [Checkpoint canónico](checkpoints/2026-10-17_V11_G2_G3_C2_FITTING_ATTRIBUTE_SKILL_GROUPS.md). **PASS G2/G3 local (grupos que caben)**, 105 reglas globales NOT_VERIFIED/12 stage PASS históricos, G4 BLOCKED; no Android instalado, QA owner ni merge.

**2026-10-17 — V11 C2 habilidades custom: sufijo (XXX) real:** código Android/Desktop [`1efda23`](https://github.com/MrSimkin/dnd_custom_aid/commit/1efda233f6a565b9653591a03eb9728f79c981fa) usa `FOR/COR/ETE` de los atributos guardados, no `Fortuna/Cordura/Éter` completos para habilidades separadas; reparación SOLO de assertions antiguos [`2b4727b`](https://github.com/MrSimkin/dnd_custom_aid/commit/2b4727b172a38f73a26478592aebc72e7dedbce6). [Scaffold #39086418847](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39086418847) y [QA #39086418809](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39086418809) SUCCESS exactos, primer intento FAIL por test histórico documentado. [40 PDF App #12120803799](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39086418847/artifacts/12120803799) ZIP SHA `56e93401f9231a793547c944d0403c3a799f0e2ac34f6eea1f5d99baaee2c1cc`: 36 sin cambios físicos detectados, 4 SOLO C2 pág.5 con tres abreviaturas sustituidas, cero otro texto/página, Mara B inspeccionada visualmente. [Checkpoint canónico](checkpoints/2026-10-17_V11_G2_G3_C2_CUSTOM_SKILL_SUFFIX_XXX.md). **PASS G2/G3 local** sin QA propietario; matriz 105 global NOT_VERIFIED, 12 stage PASS históricos, G4 BLOCKED, G5/G6 pendientes; PR #173 DRAFT/unmerged, `main`/golden/legado intactos.

**2026-10-17 — V11 P2/C2: preservar orden físico Equipo/Tesoro:** [código Shared y tests `2f380d3`](https://github.com/MrSimkin/dnd_custom_aid/commit/2f380d368596de31a726e19c6def304c7c54a936) deriva a C2 el sufijo entero desde primer objeto/cantidad que no cabe en P2, evitando adelantar filas posteriores, sin cambiar tamaño P2 ni las 4 familias legacy. [Scaffold #39084859184](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39084859184) y [QA #39084859246](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39084859246) SUCCESS; [40 PDF reales #12119883551](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39084859184/artifacts/12119883551) ZIP SHA `c39af633bc0008711c6004556c93781195f029d0c078d017aeaaa9dfd68d2150`: dos nuevos A/B con orden y cantidades únicas verificados externamente; 24/38 baselines iguales en firma física y 14 cambian solo P2/C2 EQUIPO, con cero palabras perdidas/incorporadas y mismo número de páginas/trazados, inspección visual Aldren antes/después. [Checkpoint canónico](checkpoints/2026-10-17_V11_G2_G3_P2_C2_ORDERED_SOURCE_PREFIX.md). **PASS local G2/G3 de este caso, no aprobación visual/owner**; 105 globales NOT_VERIFIED/12 stage PASS, G4 BLOCKED, G5/G6 pendientes, `main`/golden/legacy intactos; PR #173 DRAFT/unmerged.

**2026-10-17 — V11 P4 Trucos nivel 0 (175 densos) A/B:** [test `efdb1860`](https://github.com/MrSimkin/dnd_custom_aid/commit/efdb18607ee48b46aedb8b56ca1b33839bfe326c) verifica continuidad real P4 de 175 trucos nivel 0 únicos por variante, dos páginas P4 físicas y rótulos `TRUCOS / NIVEL 0 / CONTINUACIÓN` entre columnas y páginas. [Scaffold #39083161297](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39083161297) y [QA #39083161213](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39083161213) **SUCCESS**; [38 PDF App #12119613456](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39083161297/artifacts/12119613456), ZIP SHA `32084784140528d1b65c45ae6bc07411524a86a219110e19e6918f057ea824d3`, CRC PASS; 36 previos sin cambios de palabras/posiciones/conteos físicos, 2 nuevos A/B, 175/175 fuente única, franjas P4 inspeccionadas. [Checkpoint canónico](checkpoints/2026-10-17_V11_G3_P4_DENSE_CANTRIPS_LEVEL_ZERO.md). **PASS local G3 solo de este caso**, 105 global NOT_VERIFIED/12 stage PASS históricos, G4 BLOCKED, G5/G6 pendientes; PR #173 DRAFT sin merge; main/golden/legacy intactos, no QA owner ni Android instalado.

### Actualización 2026-10-17 — V11 P4 continuidad de nivel entre columnas

PR #173 OPEN/DRAFT/unmerged. Código `dbf8a196` dibuja contexto `NIVEL N / CONTINUACIÓN` dentro de la franja existente de cada columna física P4 que comienza con filas del nivel anterior; sin añadir páginas ni desplazar datos. [Scaffold #39082179553](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39082179553) y [QA traceability #39082179525](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39082179525) SUCCESS exactos; [ZIP real 36 PDF #12119138570](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39082179553/artifacts/12119138570) SHA `b7db5e9d65054a185a834b86461948391dffafc5a5b47c2fe4c9bbd315526aeb`: 338 páginas, 19 sin cambio textual, 17 solo con 36 rótulos esperados P4; ninguna pérdida o movimiento de texto fuente; A/B densos 175/175, etiquetas col2/col3, NOTAS final; capturas visuales revisadas. [Checkpoint](checkpoints/2026-10-17_V11_G2_G3_P4_PHYSICAL_COLUMN_CONTEXT.md). Solo PASS local G2/G3, **105 reglas globales NOT_VERIFIED / 12 stage PASS históricos**, G4 BLOCKED, G5/G6 pendientes; main/golden/legacy intactos y sin QA propietario ni merge.

### Actualización 2026-10-17 — V11 P4 nivel en nuevas páginas físicas

PR #173 DRAFT/unmerged; código `a63bd6c8` agrega rótulo real `NIVEL N / CONTINUACIÓN` a página P4 de arrastre, en Android/Desktop, sin modificar filas ni maqueta. [Scaffold #39081392551](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39081392551) y [QA #39081392536](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39081392536) SUCCESS. [Artefacto App #12119331435](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39081392551/artifacts/12119331435) 36 PDF ZIP SHA `ed79b5d0afa2e3ba2d46d0c40391a720cdafdec3bad3ffb1c2a6892a3a561223`: regresión 34/34 previos y 318p sin diferencias en texto/coordenadas/dibujos contados; nuevos A/B 10p y 175/175 conjuros únicos, contexto p5, última NOTAS, inspección visual parcial. [Checkpoint](checkpoints/2026-10-17_V11_G2_G3_P4_CONTINUATION_LEVEL_CONTEXT.md). **PASS local G2/G3**, matriz 105 globales NOT_VERIFIED/12 checks etapa PASS históricos, G4 BLOCKED; G5/G6 pendientes, no aceptación propietario ni merge, main/legacy/golden intactos.

### Actualización 2026-10-17 — V11 P4 fuentes distintas con igual nombre

En [PR #173](https://github.com/MrSimkin/dnd_custom_aid/pull/173) (DRAFT/unmerged), código `b5e10685` diferencia fuentes con nombre repetido mediante prefijos reales de UUID, preservando su `[X]`/`[ ]` por asociación en Android/Desktop; fuentes sin ambigüedad conservan formato. [Scaffold #39080139256](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39080139256) y [QA #39080139272](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39080139272) SUCCESS. ZIP [34 PDFs App #12118394108](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39080139256/artifacts/12118394108), SHA `5560efd064892fb5e8933c1ac808ea545793f6dc70eae19be4119e0936cde9f9`: 32/32 antiguos, 300 páginas, iguales en palabras/coordenadas/conteo de dibujos; dos A/B 9p con estado y origen diferenciados en P4 p4, inspeccionados. [Checkpoint](checkpoints/2026-10-17_V11_G2_G3_P4_DUPLICATE_SPELL_SOURCES.md). **PASS LOCAL** G1/G2/G3 de este slice solamente; 105 reglas globales NOT_VERIFIED/12 stage PASS históricos, G4 BLOCKED, G5/G6 pendientes. Nada en main, golden, familias legacy o QA del propietario.

### Actualización 2026-10-17 — V11 nombre largo en cabecera sin recorte de fuente

En [PR #173](https://github.com/MrSimkin/dnd_custom_aid/pull/173) DRAFT/unmerged, implementación Kotlin shared + renderers PDFBox Android/Desktop mide la cabecera real y conserva nombre completo en C2 cuando excede el ancho fijo. [Scaffold #39074710117](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39074710117) y [QA #39074710185](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39074710185) SUCCESS; primera suite #39074182113 falló por test contando también cabecera repetida, corregido de manera acotada sin segundo cambio productivo. Artefacto PDF real #12116582703 ZIP SHA `acccd4e42c8c89306decb30a1632851c9aeec4737276cf0084d11a4a3ec54f70`: 30/30 baselines, 286 páginas con texto/coordenadas/conteo de vectores sin diferencia; dos PDF A/B 7 páginas con tokens completos en C2 y última NOTAS. [Checkpoint](checkpoints/2026-10-17_V11_G2_G3_LONG_CHARACTER_HEADER_C2.md). Solo **PASS LOCAL G2/G3**, 105 reglas globales NOT_VERIFIED/12 stage PASS históricos, G4 BLOCKED y G5/G6 pendientes, sin QA propietario, Android instalado, merge ni alteración main/legacy/golden.

### Actualización 2026-10-17 — V11 contadores de espacios de conjuro P1/C2: evidencia verificada

PR #173 DRAFT/sin fusionar. Código Desktop `d8ee9ba`, Android `c11051c` y prueba PDF App A/B `5bc9efc`: contadores que no caben pasan a C2 con `VER C2`, cifras completas sin pérdida. [Scaffold #39072946515](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39072946515) y [QA #39072946529](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39072946529) **SUCCESS**; [30 PDF genuinos #12115184410](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39072946515/artifacts/12115184410), 28 anteriores físicamente equivalentes (no byte a byte) y dos A/B de 7 páginas con cifras en C2 p4 y NOTAS final, inspección visual. [Checkpoint](checkpoints/2026-10-17_V11_G2_G3_P1_LARGE_SPELL_SLOT_COUNTS.md). **PASS local G2/G3**, matriz 105 reglas globales NOT_VERIFIED / 12 stage checks PASS históricos, G4 BLOCKED y G5/G6 pendientes; no Android instalado, QA propietario ni merge; main/legacy/golden intactos.

### Actualización 2026-10-17 — V11 P1 Ataques: selección por cabida física y fuente original

PR #173 DRAFT; commit `f3c406af980473b0e9be7d6e30f8c0cf3adc54eb` corrige selección de seis P1 entre todos los ataques de origen (no solo primeros seis antes de filtrar), mantiene cuatro largos completos en C2. CI Scaffold #39071100853 y QA #39071100909 SUCCESS; ZIP nativo 28 PDF #12115157309 SHA `a356c7720edfbb61bf7a6bfef2a7cf196453978598952bdde0186571b860c8bb`, 26/26 baselines físicamente equivalentes; dos nuevos A/B siete páginas: 6 ataques cortos P1, 4 largos C2 p4, 10/10 únicos, última NOTAS. [Checkpoint canónico](checkpoints/2026-10-17_V11_G2_G3_ATTACKS_FIT_SOURCE_ORDER.md). PASS solo local, matriz 105/12 inalterada, **G4 BLOCKED**, G5/G6 pendientes; main/legacy/golden intactos, no QA del propietario ni merge.

# Actualización 2026-10-17 — V11 seis atributos bajo saturación

PR #173 OPEN/DRAFT, código `e1681c14c722ac256243c8af50cf883af062e350`: asignación Shared de filas reales para P1 A, conserva los seis atributos y envía habilidades excedentes a C2 sin reducción de fuente, también aplicado en renderer Android/Desktop. Scaffold #39069330008 + QA #39069330059 SUCCESS; 26 PDF App #12115230306 ZIP `94aa6fb09ab1874f38e2de6e6888469e842288fb526e4b1c9c741d302544775f`: 24/24 baselines físicamente idénticos; A/B 10p con 54 habilidades únicas y seis atributos P1, C2 p5/6. [Checkpoint canónico](checkpoints/2026-10-17_V11_G2_G3_DENSE_ABILITY_SKILL_CAPACITY.md). G2/G3 PASS solo local; matriz 105 global NOT_VERIFIED/12 checks PASS históricos, G4 BLOCKED, G5/G6 pendientes, ningún QA instalado ni merge; main/legacy/golden intactos.

### Actualización 2026-10-17 — V11 G3 impresión de tabuladores en Rasgos/Habilidades/P4/Libro

PR #173 sigue OPEN/DRAFT. Implementación real de código `b24576f`+`7d27831`, test correcto `2ba7058`: las rutas directas restantes PDFBox de texto fuente normalizan tabulaciones solo al medir/dibujar, preservando fuente; Desktop/Android comparten corrección. Scaffold #39067439661 falló por libro opcional no activado en fixture (sin atribución productiva), reejecutado y **SUCCESS** en [#39067945274](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39067945274), QA [#39067945251](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39067945251) SUCCESS. Artefacto 24 PDFs #12114620678 ZIP SHA256 `a6e8e768712b81c5661092c3a38c5622aa5eba98171bcab5eb6b6540693862f3`: 22/22 baselines sin divergencia; A/B 14p, P1 trait/skill, P4 fuentes, Libro p10–11 íntegro con continuación real. [Checkpoint](pc-sheet-v11/G3_TABS_CAMPOS_MECANICOS_Y_LIBRO_2026-10-17.md). PASS solo local G3 para el defecto concreto; matriz 105/12 intacta, G4 BLOCKED, sin G5/G6/QA propietario ni cambios legacy/golden/main.

### ACTUALIZACIÓN 2026-10-17 — V11 Equipo/Tesoro P2→C2 cantidades extensas

PR #173 continúa DRAFT/unmerged. Implementación real `cfdf9c9a7def0382e96adae6032c5ccdb4ebf702` corrige error de cantidad que no cabe en columnas 36pt: P2 conserva valores normales, C2 recibe el registro completo y ensancha par cantidad/separador según medición con fuente nativa; Android/Desktop comparten plan. Scaffold #39064659575 y QA #39064659551 SUCCESS; artefacto 22 PDFs App #12112039499 SHA ZIP `5cf1d0e57d7abd82b45e8e3bc77b2632b6a5e6411ccc9fdc7c768f82fa89093e`, 20/20 baselines físicamente idénticos, 2 A/B estrés cantidad, Equipo p6/Tesoro p7 sin duplicados ni colisión. [Checkpoint](pc-sheet-v11/G2_G3_P2_C2_CANTIDADES_EXTENSAS_2026-10-17.md). Prueba local G2/G3, **NO G4**. Matriz 105 reglas/12 PASS etapa históricos, G4 BLOCKED, G5/G6 sin iniciar, Android instalado/QA propietario sin aprobación, main/legacy/golden inalterados.

# Actualización 2026-10-17 — V11 P1 combate sin abortar por datos extensos

**P1/C2 G2–G3 local:** PR #173 sigue OPEN/DRAFT; commit `8e9b6bc8f76a65a676f94875a3e6e7b1f8e46690` agrega router de celdas de combate compartido, medición real PDFBox, VER C2 y entrada fuente completa en C2. Tests Shared + PDF real Desktop A/B en escenario extremo multiclase; Scaffold #39063006457 y QA #39063006437 SUCCESS. 20 PDF App #12112407223 ZIP SHA-256 `650de0d98d758ea10720db321bff8fd304797662bf6fcb367934b6fe9e8b564d`: 18/18 baselines idénticos en palabras/coordenadas/vectores/páginas; A/B nuevos 16 valores una vez, C2 p4, NOTAS última. Prueba acotada, no PASS global. [Checkpoint](pc-sheet-v11/G2_G3_P1_COMBATE_DESBORDE_C2_2026-10-17.md). Matriz 105 reglas / 12 checks etapa PASS anteriores, G4 BLOCKED; G5/G6 pendientes, main/legacy/golden intactos.

# Actualización 2026-10-17 — V11 tabulaciones en PDFBox

**2026-10-17 — V11 G2/G3, tabulaciones en fuentes reales:** PR #173 DRAFT/unmerged. Commit productivo [11a2723](https://github.com/MrSimkin/dnd_custom_aid/commit/11a2723fcb9502849786349515cd872dfe80f283) repara render PDFBox Android/Desktop y medición compartida para `\\t` preservando offsets/fuente; nuevos tests G2/G3, último test ahora conserva PDFs nativos tabulados A/B. [Scaffold #39057693058](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39057693058) y [QA #39057693073](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39057693073) SUCCESS en código base; nuevo commit con PDFs tabulados requiere CI/inspección independiente. [Checkpoint](pc-sheet-v11/G1_G2_G3_TABULACIONES_FUENTE_PDF_2026-10-17.md). 105 reglas, 12 PASS etapas previos; sin nuevos PASS falsos, G4 BLOCKED, sin G5/G6, sin cambios legacy/golden/main.

# Branch status and repository-ordering map

### ACTUALIZACIÓN 2026-10-17 — BK04 + BK05 G2/G3: LIBRO CON PAGINACIÓN ÍNTEGRA

Continúa PR #173 DRAFT/unmerged. BK04 ya implementado por el paginador sin cambios productivos: se comprobó por geometría G2 y PDFs de App G3 (fuente real Ilyra, Fira embebida A/B) que Fire Bolt termina en izquierda y Light pasa entera a la derecha cuando allí cabe, sin anuncio falso de división, con índices reales. BK05/G2 añade prueba de fuente larga, tres columnas en dos páginas, offsets/flags perfectos y ninguna superposición. Código de pruebas `d26ced4dc0443127fb3607c8d8e14aa63dbef71f` y `8c82b5004238095b7683dde16119871c2798c3e9`; Scaffold #38997328224 y #38997803250 SUCCESS. 16 PDFs de App del segundo SHA (artefacto #12087649359) permanecen iguales en contenido físico a los anteriores; ZIP SHA256 `131b70db48c72e682df31d81a0afc4378ca239c31ff6a3e16e6eef55f03192e8`. BK04 G2/G3 PASS y BK05 G2 PASS locales, BK05 G3 PASS ya demostrado, estatus global NOT_VERIFIED, G4 BLOCKED y 105 reglas conservadas. Checkpoint `docs/pc-sheet-v11/G2_G3_BK04_BK05_BOOK_PAGINATION_2026-10-17.md`. Sin QA owner/merge, main/legacy/golden intactos.


### ACTUALIZACIÓN 2026-10-16 — BK05 LIBRO CON CONTINUACIÓN IDENTIFICADA

Continuidad V11 PR #173 DRAFT/unmerged. D-0076 §3.48 / BK05: cada fragmento de ficha de Libro que pasa a columna nueva muestra nombre real desde `sourceKey` (o referencia VER ÍNDICE si el rótulo auxiliar es demasiado ancho) a 7pt en franja source-free y707; Android/Desktop nativos, sin consumir descripción ni alterar paginación/índice. Código `315b8f81...`/fix `c268cefd...`, SHA probado `05744b69c8eb114adfe7e88df5294a1d92886e2f`, Scaffold #38992203285 SUCCESS y QA #38992203349 SUCCESS. ZIP de 14 PDFs App #12085324827 SHA256 `239210a76ba9962d7a92411ca024efc69df1cbbe8b67cddf7094cf5e26dfa991`, CRC PASS: 12 documentos baseline completamente idénticos; A/B extremos 14 páginas cada uno, cinco continuaciones por variante, 25/25 índice, descripción completa y Notas al final. BK05 G3 PASS con evidencia exacta, BK05 G2/global pendientes, G4 BLOCKED y 105 reglas no promovidas artificialmente. Checkpoint `docs/pc-sheet-v11/G3_BOOK_LONG_CARD_CONTINUATION_BK05_2026-10-16.md`. Una PR, main/legacy/golden intactos.


### ACTUALIZACIÓN 2026-10-16 — BK06 LIBRO ESENCIAL CONTRACTUAL

PR #173 DRAFT/unmerged. Libro opcional V11 corregido en Android/Desktop para cumplir D-0076 §3.47 y BK06: ficha de nombre, nivel, descripción original completa y escuela únicamente cuando exista origen verificable (CharacterSpell carece de campo escuela). Eliminadas líneas automáticas no autorizadas de tiempo/alcance/componentes/duración/preparación/notas del Libro; el personaje, P4, índice y notas finales no cambian. Código `7cf0194266e1e579912e945efd550a5dd47f33e8`; Scaffold #38990283697 y QA #38990283731 SUCCESS. Artefacto de 12 PDFs genuinos #12085520133, ZIP SHA `892db1ac658835ccf4af80988c4208283a273e0ffb0634fdb94bdc87babd0e41`, CRC PASS: Ilyra 13→12 (25/25 conjuros), Mara B 14→13 (24/24), 10 archivos pre-book iguales y stress A/B sin descripción cortada. `BK06.G3=PASS` limitado, `BK06.G2` y global pendientes, `G4=BLOCKED`. Checkpoint `docs/pc-sheet-v11/G3_BOOK_ESSENTIAL_CARDS_BK06_2026-10-16.md`; 105 reglas y G0–G6 se conservan, main/legacy/golden sin tocar.


### ACTUALIZACIÓN 2026-10-16 — C206 EQUIPO Y TESORO C2 INDEPENDIENTES

La única PR #173 se mantiene DRAFT/unmerged. C206 corregido en ambos PDFBox V11: las claves de navegación C2 se separan por Equipo/Tesoro, sin cambiar títulos visibles, datos ni layout. Último SHA productivo `54320d2e13a80dbb92d9e6b59d83f67a1bd3b3f9`, test fix `a446371b4fda4a40ff64da5fa8293a4eaa374f52`; Scaffold #38987925558 y QA #38987925396 SUCCESS. Diez PDFs de la aplicación, artefacto 12083518190, ZIP SHA `edf5fac8417bb2e92e028b0290f6f2fc7587843220392c389ea0907de62a4a36`; independiente PyMuPDF confirma 8 baselines idénticos y A/B con dos C2 Equipo y dos C2 Tesoro enlazados por origen real sin cruce y 137 registros únicos. Matriz 105 reglas, C206 G3 PASS local con evidencia; C206 G2 y G4 pendientes, global NOT_VERIFIED, G4 BLOCKED. Checkpoint `docs/pc-sheet-v11/G3_C2_EQUIPO_TESORO_NAVEGACION_INDEPENDIENTE_2026-10-16.md`. Ningún merge/QA propietario, main/legacy/golden intactos.


### AVANCE V11 C206 (2026-10-16) — navegabilidad P1/P2/C2 por origen real

Se corrigió una referencia falsa de P1 en C2 mixta cuando los únicos registros prolongados son Rasgos de P2, en los DOS renderizadores PDFBox V11. Commit fuente `d25c2824a90d4d19fa9ac3794f2621ea6b4ab4b3`, Scaffold #38985980989 SUCCESS, QA ledger #38985980924 SUCCESS. Se inspeccionaron ocho PDFs reales [artefacto #12083292410](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38985980989/artifacts/12083292410): los seis casos anteriores tienen páginas/texto/posiciones/vectores idénticos; nuevos casos A/B con 118 Rasgos muestran P2→C2 físico correcto sin P1 ficticio y 118 identidades, última NOTAS intacta. Regla C206 G3 **PASS con evidencia**; G2 y G4 aún pendientes, C206 global NOT_VERIFIED, matriz 105 reglas y G4 BLOCKED. [Checkpoint](../pc-sheet-v11/G3_C2_REFERENCIAS_ORIGEN_FISICO_2026-10-16.md). Una PR #173 DRAFT/unmerged, main/legacy/golden intactos. Avanzar por plan original G0–G6, ampliar reglas sin sustituir evidencia ni demorar desarrollos concretos.


### ACTUALIZACIÓN 2026-10-16 — V11 P2 RESERVAS + CABECERAS VERIFICADAS LOCAL DESKTOP

En única PR #173 OPEN/DRAFT/unmerged se implementaron P203/P206: las filas manuscritas P2 vacías en ambos PDFBox pasan de 12.42pt a 16.5pt (Equipo)/19.5pt (Tesoro) sin cambiar tablas con registros ni C2; P202/P205 añade los metadatos completos aprobados «OBJETO / CANTIDAD» y «DENOMINACIÓN / CANTIDAD» dentro de las cartelas P2 existentes. Último commit funcional `fa2f333d8c1dd986a55512f6bfe1e0326cd17a4b`. Scaffold #38982363198 y #38983178486 SUCCESS; QA #38982363225 y #38983178488 SUCCESS. Se extrajeron seis PDFs Desktop reales por build (artefactos 12082390303 y 12082570208), CRC PASS y PyMuPDF confirma que solo cambian geometrías manuscritas P2 en casos sin filas o palabras de encabezados; texto fuente/paginación/dibujos posteriores intactos. Checkpoint `docs/pc-sheet-v11/G3_P2_PAPER_AND_PAIRED_HEADERS_2026-10-16.md`. G4 continúa BLOCKED con reglas globales NOT_VERIFIED, no owner QA, RCR-1 0/2; no cambios main/legacy/golden.


### ACTUALIZACIÓN OPERATIVA 2026-10-16 — V11 G3 P2 P207/P210 (SOLO RAMA PR #173)

La PR #173 permaneció DRAFT sin merge. P210 (ubicaciones libres/reconocidas y 8 casillas manuscritas ACTIVO de Equipo Especial) y P207 (cartela RASGOS Y ATRIBUTOS) se implementaron en los dos PDFBox nativos con pruebas Kotlin reales y scripts de paridad. Último commit funcional `b8dee5121235ee9cb0c3459483c4ebf9488d1206`; Scaffold #38976601596 y #38977308376 SUCCESS, QA #38976601631 y #38977308364 SUCCESS. Los cinco PDF Desktop reales del último artefacto 12079707053 fueron contrastados físicamente con PyMuPDF: P210 no desplaza texto ni páginas, P207 solo añade 2 palabras P2 sin mover nada más. Ver `docs/pc-sheet-v11/G3_P2_EQUIPO_ESPECIAL_RASGOS_2026-10-16.md`. G4 sigue BLOCKED, reglas globales NOT_VERIFIED, G5/G6 y QA propietario NO habilitados. Ninguna familia legacy, main, golden o evidencia del propietario cambió; RCR-1 0/2. Continuar siguiente requisito real medido.


### ACTUALIZACIÓN OPERATIVA 2026-10-11 — PDF REAL, NO SOLO COMPONENTES


El usuario reiteró que el objetivo principal es que la aplicación genere los PDF aprobados, no extender indefinidamente modelos de V11. Android DEBUG y Desktop ya tienen rutas Guardar/Compartir PREQA comprobadas en Scaffold runs 38178454511, 38178555148 y 38178703810 (SUCCESS).

Se publicó código **dentro de los renderizadores PDFBox reales**: commit d3ee992 dibuja módulos P2 de Equipo/Cantidad, Tesoro/Cantidad, Rasgos en dos columnas y Equipo Especial completo con continuaciones físicas C2 después de P3/P4. Commit 3144269 dibuja página física final con dos columnas de renglones y cuadrícula completa; añade prueba PDF real Ilyra además de Mara/Aldren. Commit d6c172d exige y publica artefacto CI pequeño v11-internal-real-app-pdf-proofs con tres PDFs reales.

**GATE PENDIENTE:** Scaffold run 38180947733 aún no estaba concluido al registrar. Verificar resultado final y existencia del artefacto; corregir fallos Kotlin concretos antes de avanzar. La salida es una prueba PREQA rotulada NO APROBADA y NO replica aún todos los módulos, el logo, marcos, retrato ni golden visual. No promover a APK QA del propietario ni fusionar PR #173.

Documento del estado: docs/pc-sheet-v11/G3_PDF_REAL_P2_Y_NOTAS_2026-10-11.md. Estado: G0 histórico parcial, G1 PASS semántico, G2 no cerrado, G3 PDF real inicial probado y mejoras en validación, G4-G6 pendientes, RCR-1 0/2.



**2026-10-11 PR #173 current continuation:** genuine app-generated V11 PDF proved on Desktop (Scaffold `38178067583` SUCCESS, PDF artifacts id `11716577069`). Android DEBUG actual Save/Share UI added `b622a48`, Desktop PREQA Save/Share UI `933ed4d`; final Kotlin Scaffold `38178555148` in progress and QA ledger `38178555151` SUCCESS. No final visual owner acceptance, no dedicated APK version or owner runtime test. New QA-only routes do not modify the four existing renderers. Continue straight to CI outcome and renderer golden integration; no more isolated models. See `docs/pc-sheet-v11/PRIORIDAD_EXPORTACION_PDF_REAL_2026-10-11.md`. PR remains DRAFT/unmerged; RCR-1 0/2.


**2026-10-11 branch PR #173 — owner course correction:** Highest priority ACTUAL PDF OUTPUT from App, not more models/planners. App PDFBox renderer proof `85acc20`, real Mara/Aldren PDF CI artifacts `b115110`; Scaffold run `38178067583` awaiting result; QA traceability run `38178067552` SUCCESS. Internal NOT APPROVED, not yet normal Android/Desktop Save/Share UI. Strict next: verify real PDF artifact and fix, then finish golden-aligned renderer and UI; preserve 4 existing families, PR draft/unmerged. See `docs/pc-sheet-v11/PRIORIDAD_EXPORTACION_PDF_REAL_2026-10-11.md`.


**2026-10-11 PR #173 current scope:** G2 Kotlin module commits `524020f`, `ed72c46` add two-column pairs for ordinary Equipo/Tesoro + full-width Especial with paper ACTIVO unknown, robust continuation and original-ID/amount preservation. Authentic Mara Desktop regression 29/5/5 with packaged Fira. Baseline `8af0da5` CI SUCCESS; new QA ledger `38176858259` SUCCESS, Scaffold `38176858258` still awaiting conclusion. G0 historical owner QA PARTIAL, G1 semantic PASS, G2 IN_PROGRESS/NO PASS, G3–G6 NOT STARTED. PR DRAFT/open/unmerged, no renderer changes or RCR-1 usage. Detail `docs/pc-sheet-v11/G2_MODULOS_P2_EQUIPO_TESORO_ESPECIAL_2026-10-11.md`.


**2026-10-11 V11 PR #173 newest:** code `4414b27` adds isolated Android/Desktop PDFBox native metrics with true asset Fira/Barlow and authentic Mara JVM measurements. `9b135397` previous Scaffold/QA SUCCESS; current code QA `38175718666` SUCCESS, Scaffold `38175718653` awaiting result. G1 PASS, G2 IN_PROGRESS, G0 QA historical PARTIAL, G3–G6 NOT STARTED; branch DRAFT/open/unmerged; no legacy renderer mutation or recovery budget spent. See `docs/pc-sheet-v11/G2_METRICAS_TIPOGRAFICAS_REALES_2026-10-11.md`.


**2026-10-11 current V11 PR #173:** most recent code commit `1109a18` provides PDF-point geometry and per-column row occupancy (fixing future overlapping blocks); QA ledger `38174780469` SUCCESS, Kotlin Scaffold `38174780447` awaiting result. Earlier text-flow/page-order CI `38173199042` SUCCESS. **G1 semantic PASS; G2 IN_PROGRESS without PDF/font metrics; G0 historical owner QA PARTIAL**. No merge of PR #173, no previous-family renderer edits or RCR-1 usage; see `docs/pc-sheet-v11/G2_GEOMETRIA_CARTA_LINEAS_2026-10-11.md`.


**2026-10-11 current PR #173:** G1 semantic projection **PASS** (Scaffold `38170199248`, QA ledger `38170199195`, HEAD `4acc1a82`). G2 text-flow code + real Mara regression test started on commits `a5eb0e4`/`014f0e9`; **G2 IN_PROGRESS / verify CI**, not an approved runtime PDF. G0 historical owner QA remains partial. Branch remains sole active implementation authority, DRAFT/open/unmerged; no legacy renderer modifications; RCR-1 0/2. Reference `docs/pc-sheet-v11/G1_PASS_Y_G2_PRIMER_FLUJO_2026-10-11.md`.

**PDF branch update 2026-10-11 (active PR #173):** branch `docs/pc-sheet-v11-approved-implementation-handoff` remains the **only V11 implementation branch**; first isolated G1 semantic Kotlin/test commit `97b4e8f8ecec9465bea55fabc15fcffc5a658195`, G0 still partial, no G1 PASS, no runtime export, legacy renderer unchanged. Owner permitted this slice without original Mara PDFs, not QA final acceptance. Main routing PR #174 was merged (`0c009f10d5e66880d68fa3732491c6fc3e172f22`); do not merge PR #173 automatically. QA originals remain OPEN; RCR-1 0/2.

**PDF branch/route update 2026-10-09 (handoff branch, unmerged):** V11 visuals OWNER-APPROVED; implementation authorized; active proposed branch `docs/pc-sheet-v11-approved-implementation-handoff`. Work checkpoint `docs/checkpoints/2026-10-09_PC_SHEET_V11_APPROVED_IMPLEMENTATION_HANDOFF.md`; G0 partial, G1–G6 not started. No renderer edits/QA APK or merge. Supersedes older design-only route below **on this branch**, with its historical evidence retained. PR must be reviewed before merge.

**PDF branch/route update 2026-10-08:** Owner-led redesign contract discussion is active (D-0076; see `docs/checkpoints/LATEST.md`). **No renderer implementation branch is authorized.** This documentation work preserves prior Mara Phase-2 and old repair branch evidence; the formerly planned Phase 3 implementation is deferred. No product code or templates changed by this route decision.

**Updated:** 2026-09-30 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified functional `main`:** `15f86ec8285e69969054d40defaa2c16259b8dce` (PR #107 Mara Custom-v2 Extended overflow repair integrated)  
**Post-merge Scaffold:** `36276643170` / #3929 — SUCCESS  
**Wave 5 lifecycle:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable-content lifecycle:** COMPLETE / INTEGRATED  
**Wave 7 lifecycle:** ACTIVE  
**Historical 2026-09-30 work snapshot (superseded by 2026-10-08 redesign route):** PC Sheet PDF Export — **Phase 1 provenance CLOSED**. The exact owner-supplied 50800 APK matches `15f86ec...` / artifact `10917523331` byte-for-byte and does not match the final adaptive-repair APK `ce695c...` / artifact `10943700272`. The 2026-09-28 QA therefore remains valid for the pre-adaptive baseline but is not an owner-runtime rejection of the later repair. The full owner QA matrix remains binding. **Next phase is audit-only until the owner explicitly starts Phase 2; no new renderer code yet.**

This file controls branch lifecycle. Branch existence alone never establishes authority.

## `main`

`main` is the sole normal integrated-MVP trunk and normal continuation point.

Integrated Wave 6 implementation PRs:

- #46 reusable-content persistence foundation;
- #48 Creature payload persistence;
- #51 NPC payload persistence;
- #53 lightweight Homebrew/Rule payload persistence;
- #55 Place payload persistence;
- #57 Zone payload persistence;
- #59 Encounter payload persistence.

Integrated Wave 7 implementation PRs:

- #61 Desktop Creature/Monster Manager local authoring core — merged as `12a62288457ebe5892f90f637fe41c142b094591`;
- #63 Desktop NPC Manager local authoring core — merged as `58a565c3a33a433ce47e7fd4ac1185b5f980644f`;
- #65 Desktop Homebrew & Rules Manager lightweight local authoring core — merged as `6febe3f936593999834189b92aeda9d209385fa7`;
- #67 Desktop Place/Shop Manager local authoring core — merged as `8be8ec82702a782c65b2d6aedf9bbe4b5b58f240`;
- #69 Desktop Stage Manager Place retrieval/organization core — merged as `a99f03bf53637494695cc39b39d077ea1ef61ada`;
- #71 Adventure/Scene Spine lightweight local core — merged as `a84a8857102806f8a9ac588545167d697ea8a311`;
- #73 Desktop Dungeon/Zone Manager local authoring core — merged as `5be90a994f453e5444ecf00762cd72407cfe790a`;
- #75 Desktop Encounter Manager local authoring core — merged as `7000535b78df2b2a7149b019796ff3d5903fdb3d`;
- #77 Desktop PC Manager inspection/audit core — merged as `e14784390971f2e27025dd2fff1f5000658eb2f0`;
- #79 PC ownership/controller administration repository core — merged as `2e12400ee18026c702d6727793a3aea5d23d07b4`;
- #83 PC Sheet PDF Export shared semantic/render-plan foundation — merged as `f6350d34087aae55d5247f2ba23153814eeed04b`;
- #85 PC Sheet PDF renderer/visual gate closure — merged as `49dfc78f132c9db9763513c591e877a1749285d5`;
- #86 Desktop PC Sheet Save/Share integration — merged as `c28ad548113b368413e479de544c85aa8c924ef4`.
- #88 Android PC Sheet generated renderer bridge — merged as `c5963881bdff2597770d3f6a26992b8567b2a35b`;
- #89 Android Player / authorized-DM PC Sheet Save/Share — merged as `e6e153a53bba8aa532b5c371dcc16849a901a541`.

PC authority administration validation: corrected push Scaffold `35291183960`, PR Scaffold `35291417403`, post-merge Scaffold `35291685597` — all SUCCESS. Initial push `35290905581` failed on narrow backend row-typing and Kotlin visibility compile issues while the hosted-database contract passed.

Do not restart completed Wave 5, Wave 6, Creature Manager, NPC Manager, Homebrew/Rules Manager, Place/Shop Manager, Stage retrieval, Scene Spine, Dungeon/Zone Manager, Encounter Manager, PC Manager inspection/audit or PC authority repository implementation without new defect evidence.

## Completed implementation branches

Wave 5 historical branches:

- `wave5/desktop-workbench-shell`;
- `wave5/campaign-membership-administration-core`;
- `wave5/desktop-hosted-campaign-administration`.

Wave 6 historical branches:

- `wave6/reusable-content-persistence`;
- `wave6/creature-payload-persistence`;
- `wave6/npc-payload-persistence`;
- `wave6/homebrew-rule-payload-persistence`;
- `wave6/place-payload-persistence`;
- `wave6/zone-payload-persistence`;
- `wave6/encounter-payload-persistence`.

Wave 7 historical implementation branches:

- `wave7/desktop-creature-manager-core` — PR #61 merged;
- `wave7/desktop-npc-manager-core` — PR #63 merged;
- `wave7/desktop-homebrew-rules-manager-core` — PR #65 merged;
- `wave7/desktop-place-shop-manager-core` — PR #67 merged;
- `wave7/desktop-stage-manager-core` — PR #69 merged;
- `wave7/adventure-scene-spine-core` — PR #71 merged;
- `wave7/desktop-dungeon-zone-manager-core` — PR #73 merged;
- `wave7/desktop-encounter-manager-core` — PR #75 merged;
- `wave7/desktop-pc-manager-audit-core` — PR #77 merged;
- `wave7/desktop-pc-authority-administration-core` — PR #79 merged;
- `wave7/pc-sheet-pdf-export-foundation` — PR #83 merged.

Integrated scope belongs to `main`; these refs are not continuation authority.

## Documentation closure branches

Historical/short-lived Wave 7 closure branches:

- `docs/wave7-creature-manager-integrated` — Creature -> NPC;
- `docs/wave7-npc-manager-integrated` — NPC -> Homebrew & Rules;
- `docs/wave7-homebrew-rules-manager-integrated` — Homebrew & Rules -> Place/Shop;
- `docs/wave7-place-shop-manager-integrated` — Place/Shop -> Stage;
- `docs/wave7-stage-manager-integrated` — Stage -> Adventure/Scene Spine;
- `docs/wave7-scene-spine-integrated` — Scene Spine -> Dungeon/Zone Manager;
- `docs/wave7-dungeon-zone-manager-integrated` — Dungeon/Zone Manager -> Encounter Manager / Encounter Creator;
- `docs/wave7-encounter-manager-integrated` — Encounter Manager -> PC Manager / Audit;
- `docs/wave7-pc-manager-audit-integrated` — PC Manager inspection/audit -> ownership/controller administration;
- `docs/wave7-pc-authority-repo-integrated` — authority repository merge -> provider deployment gate;
- `docs/wave7-pc-sheet-pdf-foundation-integrated` — PDF semantic foundation -> physical renderer/template mapping.

After a closure merges, normal implementation starts from current `main`; do not continue coding on a docs branch.

## Current branch direction

**Active audit branch:** `audit/mara-phase1-provenance` — Phase 1 provenance closure, ready to integrate.

**Relevant repair evidence branch:** `repair/pc-sheet-adaptive-continuations-cross-family` is repository/CI/Worker-preflight green at its recorded final repair head, but it has **not yet been owner-runtime verified**. It remains evidence for Phase 2 audit, not automatic implementation authority.

**Sole active implementation PR:** none — do not promote/merge until the failed owner re-QA is reconciled.

Current authority:
- canonical checkpoint: `docs/checkpoints/2026-09-30_MARA_PHASE1_PROVENANCE_AUDIT.md`;
- parent runtime QA checkpoint: `docs/checkpoints/2026-09-28_PC_SHEET_MARA_OWNER_REQA_RUNTIME_FAIL.md`;
- consolidated pre-fix matrix: `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`;
- original acceptance ledger: `docs/checkpoints/2026-09-26_PC_SHEET_MARA_PREQA8_PDF_VISUAL_QA_DEFECTS.md`;
- superseded claimed closure to audit: `docs/checkpoints/2026-09-27_PC_SHEET_MARA_PREQA8_ADAPTIVE_REPAIR_CLOSURE.md`;
- prior repair head: `ce695c40782e1847ed70a2256885b5b07113feae`;
- prior Scaffold #4088 / `36354213574` and proof artifact `10942589698` are repository evidence only, not owner runtime acceptance;
- owner runtime: Fantasy **45**, Custom v1 **generation FAIL**, Custom v2 Atributo **28**, Custom v2 Habilidad **27**;
- prior claimed repaired counts **29 / 18 / 16 / 15** therefore do not match runtime;
- owner runtime disposition for `15f86ec...`: **QA FAIL**; this result does not classify `ce695c...` because that binary was not the one tested;
- Phase 1 resolved finding: owner-supplied APK SHA-256 `ff367e9b7b44d1844bd3358dbf6f5979087536a5ae404a3fe36623a3550a93c6` equals pre-adaptive `15f86ec...` / artifact `10917523331`; it does not equal final adaptive APK `ce695c...` / artifact `10943700272` / SHA-256 `68bbe5a055920a0c9ffc481f1c73c38a7dbc87867e78bcb72fabe3ff4ce00042`;
- next technical gate after owner starts Phase 2: map every OPEN matrix item against the existing adaptive repair using implementation surface + native/source reference + regression + actual-candidate evidence before new renderer code;
- owner reuse-first clarification applies across attributes, Traits/Rasgos, Trasfondo/Historia-style modules, ordinary Equipment, Equipo Especial, Notes and analogous components;
- ordinary Equipment must not show weight, `Consumible` or prose descriptions;
- do not reuse `0.5.0-preqa.8 / 50800`;
- Current Snapshot and Media/Handouts remain blocked.

Canonical resume authority:

`RESUME.md -> docs/checkpoints/LATEST.md -> docs/checkpoints/2026-10-08_PC_SHEET_PDF_REDESIGN_DISCUSSION.md -> D-0076 (design only)`

## Historical/stale open PRs

PR #36 and PR #37 are older Wave 4-era items. They are not current continuation authority. Do not infer current work from their open state.

## Accidental refs

`__noop_should_not_create__` and `__should_not_create__` were verified with no unique project work and are non-authoritative. Safe deletion is optional housekeeping and not a development blocker.

## Operating rule

Routine green branch creation, CI, PR creation, merge and post-merge validation are not separate owner-confirmation gates when scope/risk is unchanged. Continue autonomously until a real owner/product/risk/provider/failure/async-wait boundary appears.
