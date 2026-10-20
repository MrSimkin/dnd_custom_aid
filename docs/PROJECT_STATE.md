**2026-10-19 — V11 C209 recuperación segura / PASS LOCAL Desktop A/B:** código y pruebas ya publicados `36d9a64f→71b944a8→e03e9802→2be1da3b`; tres CI intermedios fallaron por tests de orden anterior y [Scaffold #39348234906](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39348234906) final SUCCESS. 74 PDFs auténticos/691p; golden extremos C2 antes de P4; 72 PDFs anteriores conservaron todos los C2/P4 por rol (41 cambian sitio), 125 referencias P2→C2 válidas; QA global intacta. Checkpoint `docs/checkpoints/2026-10-19_V11_C209_GOLDEN_PHYSICAL_ORDER_REAL_PDF_LOCAL_PASS.md`; G4 BLOCKED 105 reglas NOT_VERIFIED, G0 parcial, G1 PASS previo, G2/G3 parciales, G5/G6 pendientes, Android instalado/owner sin PASS, PR DRAFT/no merge.

**2026-10-19 — V11 C207 omisiones inferiores verificadas contra PDF real negativo/positivo:** nuevo auditor `9700bc1c` + 4 pruebas; [Scaffold #39342450957](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39342450957) SUCCESS, 72 PDF/197 C2/358 regiones sin omisiones; 68 PDF históricos con dos omisiones efectivas C206 A/B p6 correctamente detectadas (pre-repair), ocho golden SHA registrados sin cotejo visual. [Checkpoint](docs/checkpoints/2026-10-19_V11_G4_C207_MISSING_MANUSCRIPT_NEGATIVE_CONTROL.md); C207/G3 y global NOT_VERIFIED, G4 BLOCKED 105/105, Android instalado/owner sin PASS, #173 DRAFT sin merge.

**2026-10-19 — V11 auditor P305/C207/C208 físico ejecutado en 72 PDF reales:** [Scaffold #39339891507](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39339891507) SUCCESS, [JSON + hashes](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39339891507/artifacts/12228538001). P305/G3 PASS de etapa acotado (92 P3/18.953 palabras alineadas), C208/G3 PASS de etapa acotado (197 C2 con fuente, 218 grids completos), C207/G3 sigue NOT_VERIFIED: faltan huecos omitidos/golden. G4 BLOCKED 105 NOT_VERIFIED / active_candidate=null, G0 parcial, G1 PASS previo, G2/G3 parciales, G5/G6 pendientes, Android instalado y owner SIN PASS, #173 DRAFT/no merge. [Checkpoint](docs/checkpoints/2026-10-19_V11_G4_P305_C207_C208_PHYSICAL_EVIDENCE.md).

**2026-10-19 — V11 P304 multi-párrafo en PDF auténtico, PASS de etapa G3:** test `d4d16de7` con tres párrafos fuente separados, 570 palabras de rastro y seis marcadores únicos por variante A/B, 6 continuaciones p4–p9; [Scaffold #39333912231](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39333912231) SUCCESS, 72 PDFs reales vs 70 previos intactos. Prueba PyMuPDF y SHA en `docs/pc-sheet-v11/qa/evidence/2026-10-19_P3_C206_REAL_PDF_SCOPE.json`; C207 193 páginas auditadas solo parcialmente, sin PASS. **G4 BLOCKED 105/105 NOT_VERIFIED**, G2 pendiente, G5/G6 pendientes, sin APK propietario ni PDF Android físico, PR #173 DRAFT/no merge. Checkpoint `docs/checkpoints/2026-10-19_V11_G4_P3_C2_G3_SOURCE_PROOF_RECONCILIATION.md`.

**2026-10-19 — V11 G4 P3/C2 stage-evidence audit sin cambios de renderer:** `P304/G3` pasó de NOT_VERIFIED a PASS de etapa (500/500 palabras fuente en A/B PDF real y cuatro continuaciones); `C206/G3` conserva PASS de etapa pero reemplaza evidencia vieja con PDF/run actuales y destinos reales; C207 medido solo parcialmente. Reporte `docs/pc-sheet-v11/qa/evidence/2026-10-19_P3_C206_REAL_PDF_SCOPE.json`; [Scaffold #39328633469](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39328633469) SUCCESS. **105/105 reglas globales NOT_VERIFIED, gate G4 BLOCKED, active_candidate=null; G2 pendientes, Android instalado/owner sin PASS, PR #173 DRAFT/no merge.** Checkpoint `docs/checkpoints/2026-10-19_V11_G4_P3_C2_G3_SOURCE_PROOF_RECONCILIATION.md`.

**2026-10-19 — V11 C206 P2→múltiples módulos C2 PASS LOCAL Desktop:** [Scaffold #39328633469](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39328633469) SUCCESS en `36914c8`; Android+Desktop corrigen P2 enlaces de ida a Equipo/Tesoro/Rasgos/Especial, conservan C2 fuentes: 70 PDF reales/647 folios, 24 P2 con enlaces nuevos, sin cambios en páginas/texto original/trazos. [Checkpoint](docs/checkpoints/2026-10-19_V11_G2_G3_C206_MULTI_MODULE_FORWARD_LINKS_LOCAL_PASS.md). G0 parcial, G1 PASS previo, G2/G3 parciales, G4 BLOCKED 105 NOT_VERIFIED, G5/G6 pendientes; no owner/Android instalado PASS y #173 DRAFT sin merge.

**2026-10-19 — V11 C2 tabla de dos parejas asimétricas con NOTAS manuscritas PASS LOCAL:** código Android/Desktop `9ebf5b3`/`b4c6b7d`, test integrado C206 A/B `d568a492`, [Scaffold #39326088504](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39326088504) SUCCESS y [70 PDF Desktop #12222611889](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39326088504/artifacts/12222611889); solo C206 A/B p6 cambian, 68/70 íntegramente iguales, sin pérdida de texto ni folios. Checkpoint `docs/checkpoints/2026-10-19_V11_G2_G3_C2_ASYMMETRIC_TABLE_NOTES_LOCAL_PASS.md`; G0 parcial, G1 PASS previo, G2/G3 parciales, G4 BLOCKED 105 NOT_VERIFIED, G5/G6 pendientes, owner/Android sin PASS, PR #173 sin merge.

**2026-10-19 — V11 P3 continuidad larga PASS LOCAL en PDF Desktop A/B:** [Scaffold #39323890281](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39323890281) SUCCESS en `70a13026`; Android/Desktop muestran identidad del relato en hojas P3 posteriores sin modificar texto, paginación ni 68/68 PDFs existentes; 70 PDF reales / 647 folios, 500 segmentos preservados por A/B. Checkpoint `docs/checkpoints/2026-10-19_V11_G2_G3_P3_LONG_STORY_CONTEXT_LOCAL_PASS.md`. G0 parcial, G1 PASS, G2/G3 parciales, G4 BLOCKED 105 NOT_VERIFIED, G5/G6 pendientes; no aprobación owner, RCR-1 0/2, #173 DRAFT sin merge.

**2026-10-19 — V11 C-N3 PASS LOCAL de PDF auténticos:** [Scaffold #39321593761](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39321593761) SUCCESS en `15a99f8` tras reparar guardia de reanudación; 68 PDF Desktop, 64/64 regresiones fuente sin cambio de texto/folios, manuscrito y UUID continuado comprobados. Solo C-N3 scoped; G4 105 NOT_VERIFIED/G5-G6 pendientes, Android owner NO PASS, #173 sin merge. [Checkpoint](docs/checkpoints/2026-10-19_V11_CN3_RUNTIME_PDF_LOCAL_PASS.md).

**2026-10-18/19 — V11 C-N3 PASS LOCAL:** Productivo Android/Desktop `5bf52d8`, test de estrés `53532a7`, [Scaffold 39193379904](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39193379904) + QA documental SUCCESS. 64 PDF/589p, 60 originales estables en páginas/palabras; 4 pruebas nuevas, continuaciones de nota con nombre fuente. Checkpoint `docs/checkpoints/2026-10-19_V11_G2_G3_CN3_NOTE_BOUNDARY_CONTINUATION.md`. G4 BLOCKED 105 NOT_VERIFIED, G5/G6 pendientes, QA owner sin otorgar, RCR-1 0/2, no merge.

**2026-10-18/19 — C-N3 V11:** mejora productiva anclajes visuales entre notas y títulos/UUID auténticos en páginas de continuación; test real Mara A/B con registro extenso. CI pendiente; ruta `docs/checkpoints/2026-10-19_V11_G2_G3_CN3_NOTE_BOUNDARY_CONTINUATION.md`, G4 BLOCKED 105 NOT_VERIFIED, G5/G6 pendientes, #173 DRAFT sin merge.

**2026-10-18 — V11 PR #173 C2 Equipo+Especial PASS LOCAL:** código Android/Desktop `9168ba8` + [Scaffold 39189299120](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39189299120) SUCCESS, 60 PDF Desktop/549p, Ilyra-B 12→11 sin pérdida de registros; checkpoint `docs/checkpoints/2026-10-18_V11_G2_G3_C2_PAIRED_PLUS_SPECIAL_MEASURED.md`. Solo prueba local; G4 BLOCKED 105 NOT_VERIFIED, G5/G6 pendientes, RCR-1 0/2, PR DRAFT sin merge.

**2026-10-18 — V11 PR #173 C2 Equipo+Especial:** trabajo funcional de compactación de dos módulos autenticados en una hoja Carta si geometría medida lo permite, Android/Desktop y test A/B. CI PENDIENTE. Ruta: `docs/checkpoints/2026-10-18_V11_G2_G3_C2_PAIRED_PLUS_SPECIAL_MEASURED.md`; G4 BLOCKED 105 NOT_VERIFIED, sin QA owner/G5/G6/merge.

**2026-10-18 — PR #173 C2 Equipo/Tesoro compactado, PASS LOCAL:** `16a4201` producción y `08b7772` prueba. [Scaffold 39185287349](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39185287349) SUCCESS, 58 PDF Desktop/550p; casos A/B 8→7p, otras 56 salidas estables y auditores físicos 0 alertas. Evidencia y no conformidades en `docs/checkpoints/2026-10-18_V11_G2_G3_C2_COMPACT_EQUIPMENT_TREASURE.md`; G4 BLOCKED 105 NOT_VERIFIED, G5/G6 pendientes, sin owner QA ni merge.

**2026-10-18 — PR #173 V11 C2:** optimización productiva condicional de dos tablas C2 de Equipo y Tesoro en página física compartida sin comprimir filas; prueba real A/B extendida; CI pendiente. Continuidad: `docs/checkpoints/2026-10-18_V11_G2_G3_C2_COMPACT_EQUIPMENT_TREASURE.md`. G4 BLOCKED 105 NOT_VERIFIED, G5/G6 pendientes, RCR-1 0/2.

**2026-10-18 — V11 P4/Libro nombre multilineal:** prueba App Desktop exacta `262c439` [CI #39181706739](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39181706739) SUCCESS. 58 PDF/552 folios, 2 A/B nuevos sin pérdida, 56 PDF previos sin cambio en estructura/texto/coordenadas; documento canónico `docs/checkpoints/2026-10-18_V11_G2_G3_P4_MULTILINE_SPELL_NAMES_AND_BOOK.md`. G4 BLOCKED, 105 NOT_VERIFIED, owner QA no autorizado, G5/G6 pendientes, PR #173 DRAFT sin merge.

**2026-10-18 — P4 nombre multilínea V11:** cambiado código Android/Desktop + test de PDF real Ilyra A/B y Libro opcional; CI PENDIENTE. [Checkpoint](docs/checkpoints/2026-10-18_V11_G2_G3_P4_MULTILINE_SPELL_NAMES_AND_BOOK.md). G4 BLOCKED (105 NOT_VERIFIED), G5/G6 pendientes, RCR-1=0/2, PR #173 sin merge.

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

# Project State — global repository navigation

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



**2026-10-11 real App PDF milestone:** Desktop app `generateV11Proof` produced actual US Letter PDFs for Mara/Aldren; verified via PDFBox tests + Scaffold SUCCESS `38178067583`, artifact `pc-sheet-populated-template-proofs` id `11716577069`. Android DEBUG Save/Share QA UI `b622a48` and Desktop PREQA Save/Share QA UI `933ed4d` coded; final Scaffold `38178555148` pending, QA traceability `38178555151` SUCCESS. These are incomplete, explicitly watermarked INTERNAL proof sheets, not approved visual V11 or owner QA APK. Primary priority: validate UI CI then golden-aligned PDF + installed app exports. See `docs/pc-sheet-v11/PRIORIDAD_EXPORTACION_PDF_REAL_2026-10-11.md`. PR #173 unmerged; original families unchanged; G0 historical QA partial, G1 semantic PASS, G2 incomplete, G3 runtime proof PASS scoped; RCR-1 0/2.


**2026-10-11 OWNER PRIORITY CORRECTION:** Real PDF export in App is the goal; no more standalone V11 sub-planners. New isolated Desktop/Android PDFBox proof renderers + app-service calls in `85acc20`; CI runtime test for actual PDF files (Mara/Aldren), extraction and upload as proof artifacts in `b115110`. CI Scaffold `38178067583` pending; traceability QA `38178067552` SUCCESS. Outputs internal and visually incomplete, not user Save/Share yet. See `docs/pc-sheet-v11/PRIORIDAD_EXPORTACION_PDF_REAL_2026-10-11.md`. PR #173 still DRAFT/unmerged, G1 semantic PASS, G2 IN_PROGRESS, G3 PDF proof pending, G0 historic QA PARTIAL, no legacy regression claims, RCR-1 0/2.


**2026-10-11 V11 G2 active PR #173:** New Kotlin components `524020f` (paired ordinary equipment + treasure, exact amounts/zero) and `ed72c46` (full-width special equipment, unknown ACTIVO left blank), measured real Fira on authentic Mara (29 ordinary, 5 currencies, 5 special), stress fixtures 386. Prior typography `8af0da5` Scaffold `38175854581` PASS; latest QA registry `38176858259` PASS, Kotlin Scaffold `38176858258` pending. See `docs/pc-sheet-v11/G2_MODULOS_P2_EQUIPO_TESORO_ESPECIAL_2026-10-11.md`. G1 semantic PASS; G2 IN_PROGRESS (no PDF), G0 owner QA historical partial; 32 M50800 + V11-QA-001/002 OPEN, RCR-1 0/2. PR #173 still DRAFT/unmerged, legacy renderers untouched.


**2026-10-11 V11 G2 latest:** commit `4414b27` implements real bundled Fira/Barlow PDFBox text/font measurement adapters for Desktop and Android (distinct from legacy), with Desktop tests against actual Mara backup. Previous `9b135397` CI Scaffold/QA both SUCCESS; new `4414b27` QA registry `38175718666` SUCCESS, Scaffold `38175718653` pending. See `docs/pc-sheet-v11/G2_METRICAS_TIPOGRAFICAS_REALES_2026-10-11.md`. G1 semantic PASS; G2 in progress/no PDF or G2 PASS. G0 original owner QA partial, M50800 + V11 QA OPEN, RCR-1 0/2, PR #173 DRAFT/open without merge.


**2026-10-11 latest G2 V11 PR #173:** commit `1109a18` adds shared US Letter PDF-point column/row reservations, nonoverlapping measured text cells, and new geometry tests; prior text-flow/page-order CI `38173199042` PASS and QA ledger `38173199107` PASS. New code owner QA ledger `38174780469` PASS, new Kotlin Scaffold `38174780447` awaiting completion. Detailed map: `docs/pc-sheet-v11/G2_GEOMETRIA_CARTA_LINEAS_2026-10-11.md`. **G1 semantic PASS; G2 IN_PROGRESS/NO PASS, G0 historical QA PARTIAL**. No font measurement adapter or V11 PDF renderer; legacy unchanged, QA OPEN, RCR-1 0/2, PR open draft without merge.


**2026-10-11 V11 active PR #173:** G1 semantic Kotlin **PASS** on `4acc1a82` (Scaffold run `38170199248`, ledger `38170199195`, both SUCCESS). G2 **IN_PROGRESS**, shared lossless measured text-flow commits `a5eb0e4`/`014f0e9` and real Mara section tests, no G2 PASS or PDF output yet. See `docs/pc-sheet-v11/G1_PASS_Y_G2_PRIMER_FLUJO_2026-10-11.md`. Original owner Mara 50800 PDF QA remains outstanding; do not claim G0 QA PASS. PR #173 unmerged, legacy renderers unchanged, RCR-1 0/2; `main` already points to PR #173 through merged documentation PR #174.

**PDF route update 2026-10-11 (PR #173, still unmerged):** Owner explicitly allows **isolated G1 shared Kotlin semantic-contract work** despite missing exact Mara historical PDFs; this does NOT grant G0 PASS or waive original artifact regression/owner runtime acceptance. First product G1 commit `97b4e8f8ecec9465bea55fabc15fcffc5a658195` adds `PcSheetV11SemanticContract.kt` + tests, preserves the four legacy renderers and keeps V11 A/B separate. The main PR #174 route is already merged; PR #173 remains DRAFT, with unresolved merge reconciliation. No V11 UI/renderer/PDF owner candidate; QA OPEN and RCR-1 0/2. Continue from `docs/checkpoints/LATEST.md`, never the historical sections below.

**PDF route update 2026-10-09 (handoff branch, unmerged):** The owner APPROVED V11 visuals and AUTHORIZED implementation. Authoritative next route `docs/checkpoints/2026-10-09_PC_SHEET_V11_APPROVED_IMPLEMENTATION_HANDOFF.md`, `docs/pc-sheet-v11/`. Gate G0 partially checked; no Kotlin V11 integration, APK or runtime PASS. Golden ZIP binary now versioned and independently verified (GitHub Actions #38009729554; R-01 CLOSED). G0 still partial: original owner-runtime Mara artifact, complete semantic map/build preflight not yet closed. This paragraph overrides the 2026-10-08 design-only route below **on this branch only**; main remains unchanged pending review.

**PDF route update 2026-10-08:** The owner has reopened the PC-sheet PDF contract/architecture. New canonical resume is `docs/checkpoints/2026-10-08_PC_SHEET_PDF_REDESIGN_DISCUSSION.md` and design-decision ledger `docs/decisions/D-0076_PC_SHEET_PDF_REDESIGN_DECISION_LEDGER.md`. **Design/discussion only; no renderer, template, code or PDF work authorized.** Former Phase 3 repair is deferred; existing D-0074 and QA baselines remain historical/implemented truth, not new design acceptance.

**Last reconstructed:** 2026-09-30 (Chile local time)  
**Owner integrated-MVP implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified functional `main`:** `15f86ec8285e69969054d40defaa2c16259b8dce` (PR #107 Mara Custom-v2 Extended overflow repair integrated)  
**Post-merge Scaffold:** `36276643170` / #3929 — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Wave 7:** ACTIVE — Desktop authoring Managers  
**Integrated Wave 7 repository packages:** Desktop Creature/Monster Manager + Desktop NPC Manager + Desktop Homebrew & Rules lightweight local core + Desktop Place/Shop Manager local core + Desktop Stage Manager retrieval/organization core + lightweight Adventure/Scene Spine + Desktop Dungeon/Zone Manager local core + Desktop Encounter Manager local core + Desktop PC Manager inspection/audit core + PC ownership/controller administration core + PC Sheet PDF Export shared semantic/render-plan foundation  
**PC authority DEV deployment:** VERIFIED — Worker version `ccdeca47-7622-4eb7-8dfa-a197d62bf3cb`  
**Historical package snapshot (superseded as active route 2026-10-08):** PC Sheet PDF Export — Phase 1 provenance is **CLOSED**. The exact owner-supplied `0.5.0-preqa.8 / 50800` APK is byte-for-byte `15f86ec...` / artifact `10917523331` / SHA-256 `ff367e9b7b44d1844bd3358dbf6f5979087536a5ae404a3fe36623a3550a93c6`, not the later adaptive-repair `ce695c...` / artifact `10943700272`. Thus the 2026-09-28 owner QA did not owner-test the later repair branch. Every owner observation remains binding. Next phase is an explicit audit of the existing repair against the full acceptance matrix before any new renderer implementation. Current Snapshot and Media/Handouts remain blocked.**

### Superseding PDF visual state — 2026-09-20

The historical renderer-proof work from PR #85 is integrated/frozen evidence, **not the current active branch**. Current work is routed exclusively by `RESUME.md -> docs/checkpoints/LATEST.md`.

Approved/frozen base-sheet baselines:
- Custom v1 Run 7;
- Custom v2 per Attribute Run 4;
- Custom v2 per Ability corrected Run 2;
- Para Hoja de PJ Symbols v8.

Current Classic candidate:
- approved corrected Classic Run-2 commit `3dbcff8f5f9a2413f6deb8e400daeb6288b4f6b1`;
- Scaffold `35480871986` / #2793 — SUCCESS;
- complete nine-page Classic approval set: three normal pages plus all six D-0074 extension roles;
- OWNER APPROVED / FROZEN.

D-0074 + owner clarification require design-specific Extended pages for **all** families. The Custom base-sheet approvals do not close their Extended-page work.

## 1. Current topology

`main` is the sole normal integrated-MVP trunk. New work uses short-lived outcome-oriented branches from current `main`.

Do not repeat completed Wave 5, Wave 6, Creature Manager, NPC Manager, Homebrew/Rules Manager, Place/Shop Manager, Stage retrieval, Scene Spine, Dungeon/Zone Manager, Encounter Manager, PC Manager inspection/audit, PC authority repository work or the PC Sheet PDF semantic foundation without new defect evidence.

### Current active PDF route / renderer state

The prior `repair/pc-sheet-adaptive-continuations-cross-family` branch is **unmerged evidence only**, not accepted continuation authority. Its claimed closure at `ce695c40782e1847ed70a2256885b5b07113feae`, Scaffold #4088 / `36354213574` and proof artifact `10942589698` is superseded as runtime acceptance because owner 50800 output reproduced the original defect classes and added a Custom-v1 export blocker.

Current authority is:

- `docs/checkpoints/2026-09-28_PC_SHEET_MARA_OWNER_REQA_RUNTIME_FAIL.md`;
- `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`;
- `docs/checkpoints/2026-09-26_PC_SHEET_MARA_PREQA8_PDF_VISUAL_QA_DEFECTS.md`;
- `docs/PC_SHEET_PDF_VISUAL_CONTRACT.md`;
- `AGENTS.md` section 6.3.

Phase 1 fingerprint/classification is complete and recorded in `docs/checkpoints/2026-09-30_MARA_PHASE1_PROVENANCE_AUDIT.md`. No new renderer code is authorized yet: Phase 2 must audit the existing adaptive-repair implementation against the full owner matrix and map each OPEN item to existing implementation surface + native/source reference + regression + actual-candidate evidence.

The active owner design clarification is **reuse before reconstruction**: where the sheet already has a correct native component (attributes, Traits/Rasgos, Trasfondo/Historia-style modules, Equipment, Equipo Especial, Notes and analogous sections), extend/copy that grammar instead of inventing a generic Extended replacement. Ordinary Equipment specifically shows compact identity only: no weight, no `Consumible`, no prose descriptions.

The shared primitive foundation has now passed owner Primitive PDF QA. Renderer work may proceed beyond the primitive gate using:

- PDFBox 3.0.8;
- metric-based text/layout, explicit adaptive vs fixed font sizing and measured wrapping/overflow;
- repeated-row/table and portrait Fit/Crop primitives;
- deterministic sans/condensed/handwritten typography candidates;
- native vector markers and the app single-check / double-check training grammar.

The exact owner-authored `Para-hoja-de-pj` v1 is now committed as immutable provenance after SHA-256 verification. v4 is published as a contemporary redesign with legacy-refined and modern-clean families, stable renderer PUA aliases, complete documentation and a deterministic TTX-backed builder.

Para Hoja de PJ Symbols v8 is OWNER APPROVED / FROZEN. Custom v1 base pages, Custom v2 per-Attribute base pages and Custom v2 per-Ability base pages are owner-approved/frozen. Classic Run 2 corrected complete family is OWNER APPROVED / FROZEN at `3dbcff8f5f9a2413f6deb8e400daeb6288b4f6b1`. Classic Run 1 is owner-rejected historical evidence only. Custom-v1 Extended Run 1 is owner-rejected historical evidence. Run 2 established the source-faithful extension direction but received bounded owner feedback on page-6 artifacts, block selection and typography. Custom-v1 Extended Runs 1–5 are historical rejected/superseded evidence. Custom-v1 Extended Run 6 is OWNER APPROVED / FROZEN at `69b308f3d5d493d06bd0107ac66c7524935aa9fa` (Scaffold #2885 SUCCESS, artifact `10609869599`). Custom-v2 Extended Run 7 is OWNER APPROVED / FROZEN and its integrated production audit is PASS at `0295f30ca77214284902b0e13a563dcdaf58b501`. Custom-v1 production promotion is also complete: integrated production audit PASS at `5d09271231dd395e44f0c4c2a33cdb39509cc6b5`, final push #3097 SUCCESS, PR #3098 SUCCESS, artifact `10660478619`. Classic Run-2 production promotion and its integrated semantic/pagination audit are now PASS at `30ac4d073e9fab47a498f4e6dc3d9266c633110e` (Scaffold #3151 / `35677870304` SUCCESS; artifact `10674062891`). Printed spent-slot markers remain intentionally blank for writable paper tracking. The application-owned optional Spellbook is also production-complete at `ac0cbb2b26202bf934ac4926b56899014390613e` (Scaffold #3161 / `35679451757` SUCCESS; artifact `10674370966`), with append-only preservation of all preceding family pages. Local portrait-byte handoff plus Crop/Fit is production-complete at `93490d2247da5ea46ef50563fd17da78840e659e` (Scaffold #3177 / `35683430947` SUCCESS; artifact `10676160917`), covering Classic, Custom v1 and both Custom-v2 portrait geometries with v2 frame-preservation guards. The `APP_MODIFIED_SHEET` plus originating-section continuation-cue candidate at `bf7d4d8f5324af7aabb5bf4d3d0038936af2b53d` (Scaffold #3195 / `35767630932`; artifact `10713481125`) is OWNER REJECTED / DO NOT USE. The visual recovery at implementation head `6ea493c9ee53cf727464a985bc6ef0b558a73e99` / artifact `10720833172` is OWNER REJECTED after a second owner review. The observations are regressions against pre-existing visual rules, not a new design contract. Those rules are consolidated in `docs/PC_SHEET_PDF_VISUAL_CONTRACT.md`; repair is active. WIP renderer `4e7ea99997de46ef8a9a8d1066b08fa362854cdd` correctly couples Classic text to physical rules but Scaffold #3245/#3246 exposed base-excerpt overflow and its rasterized v2-logo attempt must be reverted in favor of the established layered source-preserving repair. Canonical Custom Extended methodology remains `docs/PC_SHEET_CUSTOM_EXTENDED_STRATEGY.md`.

Resume from the latest checkpoint referenced by `docs/checkpoints/LATEST.md`.

Do not infer a continuation branch from the remote branch list. If `LATEST.md` names an active implementation branch, that exact ref is authoritative and all other old repair/docs/tmp branches are historical only.

## 2. Integrated Wave 5 baseline

Wave 5 verified Desktop behavior includes local campaign/workbench persistence, real Descope email-OTP authentication, hosted campaign bootstrap/convergence, authoritative campaign member administration, canonical Outlook DEV owner/DM identity, device-local settings and explicit hosted-session lifecycle/sign-out semantics.

Historical Gmail evidence remains truthful and unchanged.

## 3. Hosted DEV architecture

```text
Android / Desktop clients
        |
        v
Cloudflare Worker/API <---- Descope identity proof
        |
        v
Neon PostgreSQL
```

Existing DEV Worker: `dnd-custom-aid-api`.

Wave 6 and the earlier Wave 7 Creature/NPC/Homebrew/Place/Stage/Scene/Zone/Encounter/PC-audit packages did not require Worker changes. PR #79 materially changed Worker/API code and has now been deployed to DEV. Owner-executed checks verified Worker version `ccdeca47-7622-4eb7-8dfa-a197d62bf3cb`, `/health` = 200, and both the existing Campaign-members route and new PC-authority route = 401 when unauthenticated.

Hard external-service operating budget remains USD $0.

## 4. Reusable-content architecture

The shared reusable-content spine provides Personal/Campaign scope, stable identity, family metadata, provenance for independent Personal -> Campaign copies, optimistic revisions/stale-write rejection, tombstones/non-resurrection, sync metadata/invariants, SQLDelight persistence and verified migrations/reopen behavior.

Approved semantics remain: Personal DM material is reusable; explicit use in a Campaign creates a new independent Campaign object ID; provenance may remain visible; later Personal edits do not automatically update Campaign copies.

Integrated reusable families:

- Creature — PR #48, migration `19.sqm`;
- NPC — PR #51, migration `20.sqm`;
- Homebrew/Rule — PR #53, migration `21.sqm`;
- Place — PR #55, migration `22.sqm`;
- Zone — PR #57, migration `23.sqm`;
- Encounter — PR #59, migration `24.sqm`;
- Scene — PR #71, migration `25.sqm`.

Encounter dependency copy/remap remains domain-specific; no generalized dependency graph was introduced. Scene possible-next-scene cues and preparation references are intentionally human-readable strings in the current lightweight orientation model.

## 5. Wave 7 integrated Managers and preparation surfaces

### Creature/Monster Manager

PR #61 integrated Personal + active-Campaign Creature browse/search/create/edit, scope/provenance/revision visibility, explicit independent Personal -> Campaign copy and atomic display-name + payload updates.

Validation: implementation `38d68dc832188f29c76ec40990297f53a85e9bed`; push `35267065066`; PR `35267241770`; merge `12a62288457ebe5892f90f637fe41c142b094591`; post-merge `35267674641` — SUCCESS.

### NPC Manager

PR #63 integrated Personal/Campaign browse/create/edit for Quick -> Developed NPC data, optional combat mechanics through `CreaturePayload`, provenance/revision visibility, explicit independent copy and atomic name + payload updates.

Validation: implementation `58b680e71ec59c871854eb9c083ff2bc6906fe88`; push `35269013875`; PR `35269163375`; merge `58a565c3a33a433ce47e7fd4ac1185b5f980644f`; post-merge `35270643883` — SUCCESS.

### Homebrew & Rules Manager

PR #65 integrated the lightweight rule-record core: Personal/active-Campaign browse/search/create/edit, Draft / Active / Retired lifecycle, provenance/revision visibility, explicit independent copy and atomic name + payload updates.

Validation: implementation `17f4cd686114f734ab0bc50f453de9d579a52c79`; push `35272269481`; PR `35272560206`; merge `6febe3f936593999834189b92aeda9d209385fa7`; post-merge `35277359425` — SUCCESS.

Structured races/classes/subclasses/backgrounds/feats/spells/items, official/SRD customization, import/export, homebrew-aware AI, media/object storage and hosted reusable-content sync remain later work.

### Place/Shop Manager

PR #67 integrated Personal/Campaign Place browse/search/create/open/edit, `PLACE` / `SHOP` specialization, full existing Place payload authoring, provenance/revision visibility, explicit independent copy and atomic display-name + payload update.

Validation: implementation `f4bb75f4b2872ebc6a1dc4302cd4890367e4618c`; push `35278740631`; PR `35279052405`; merge `8be8ec82702a782c65b2d6aedf9bbe4b5b58f240`; post-merge `35279329344` — SUCCESS.

### Stage Manager — Place retrieval/organization core

PR #69 added Stage-oriented retrieval over the existing Place/Shop family without duplicate Stage persistence: kind/scope/area/function/tag filters, full-text retrieval and deterministic Name / Area / Recent ordering while preserving the Place editor/copy flows.

Validation: implementation `a01eb18a8385807d973c9f2059ff32779c0f6897`; push `35280359257`; PR `35280486923`; merge `a99f03bf53637494695cc39b39d077ea1ef61ada`; post-merge `35280636549` — SUCCESS.

### Adventure/Scene Spine — lightweight local core

PR #71 integrated the lightweight Scene orientation model and Desktop authoring surface: title, purpose, possible-next-scene cues, preparation references, DM notes, Personal/active-Campaign authoring, explicit independent copy and atomic title + payload update.

Persistence adds `ReusableContentFamily.SCENE`, `ScenePayload` / `SceneContent`, `SceneContentRepository`, `scene_payload` and migration `25.sqm`. Scene remains orientation, not a quest engine.

Validation: final implementation `face568e7985125975731fef5e275e333ef79b9d`; push `35281913166`; PR `35282188319`; merge `a84a8857102806f8a9ac588545167d697ea8a311`; post-merge `35282483851` — SUCCESS.

### Desktop Dungeon/Zone Manager — local Zone Brief authoring core

PR #73 integrated the first Desktop Zone Manager on top of the existing Wave 6 Zone persistence. No schema migration was required.

Integrated behavior:

- Personal + active-Campaign Zone browse/search/create/open/edit;
- area/tag/full-text filtering with deterministic Personal-before-Campaign browsing;
- editor grouping around **PRESENTAR / INTERACTUAR / ENCUENTRO**, plus DM support fields;
- full existing Zone payload authoring: summary, area, presentation, space, exploration, interactives, clues, checks, consequences, encounter brief, DM guidance, player-safe text, paper references and tags;
- explicit Personal -> Campaign independent copy with provenance;
- atomic display-name + Zone payload update under one optimistic revision;
- focused stale-write, copy-independence and tombstone/non-resurrection coverage;
- dedicated `Mazmorras / Zonas` route in the existing Desktop Managers hub.

The `space` and `exploration` fields carry the current topology/flow preparation. Tactical geometry, VTT maps, generalized graph infrastructure, clocks/readiness and automated fictional consequences remain outside this bounded Manager core.

Validation:

- implementation head `1a8b875a567ed73fbcceee73872ec59702b4b3f0`;
- push Scaffold `35285060523` — SUCCESS;
- PR Scaffold `35285359440` — SUCCESS;
- PR #73 merged as `5be90a994f453e5444ecf00762cd72407cfe790a`;
- post-merge Scaffold `35285629973` — SUCCESS.

### Desktop Encounter Manager / Encounter Creator — local authoring core

PR #75 integrated the first Desktop Encounter Manager on top of the existing Wave 6 Encounter persistence. No schema migration was required.

Integrated behavior:

- Personal + active-Campaign Encounter browse/search/create/open/edit;
- full-text and tag filtering;
- participant authoring through same-scope Creature/NPC selection or freeform label-only groups;
- quantity plus `EXPECTED` / `RESERVE` / `CONDITIONAL` readiness, condition, encounter-specific overrides and notes;
- explicit Personal -> Campaign independent Encounter copy preserving the existing domain-specific Creature/NPC dependency copy/remap and repeated-dependency deduplication behavior;
- atomic display-name + Encounter payload update under one optimistic revision;
- focused dependency, stale-write, copy and tombstone/non-resurrection coverage;
- dedicated `Encuentros` route in the Desktop Managers hub.

The initial branch push exposed one Kotlin visibility mismatch between a public controller method and an internal Desktop participant-source UI type. The follow-up commit only aligned visibility; the corrected push, PR and post-merge gates all passed.

Validation:

- initial implementation head `91d744a66e3ff18ee9190c41d4dbb3970ca412fe`;
- initial push Scaffold `35286844850` — FAILED on the visibility mismatch;
- final implementation head `a68f62897d6178f1da1c19deb2721ea04abc837a`;
- corrected push Scaffold `35287257508` — SUCCESS;
- PR Scaffold `35287507268` — SUCCESS;
- PR #75 merged as `7000535b78df2b2a7149b019796ff3d5903fdb3d`;
- post-merge Scaffold `35287713130` — SUCCESS.

Saved Encounters remain preparation, not live initiative/combat working state. No generalized dependency graph, encounter-balancing authority, hosted reusable-content synchronization or provider change was introduced.

### Desktop PC Manager / Audit — inspection and audited correction core

PR #77 integrated the first PC Manager/Audit slice over the same canonical PC data used by Player. No new local schema was required.

Integrated behavior:

- `Personajes jugadores` is now a real Desktop destination;
- active-Campaign PC browse/search;
- inspection of the canonical core Character sheet plus Closure and Successor aggregates;
- PC owner and controller shown independently from campaign DM role;
- local data timestamp, sync revision, durable baseline revision and pending outbox state shown as distinct evidence;
- existing reconciliation checkpoints surfaced as grouped history;
- explicit `Correct / Edit as DM` flow for bounded core fields;
- DM correction requires active DM campaign membership, preserves PC/campaign identity and owner/controller authority, appends a `Corrección DM` checkpoint with reason/change summary, exports the resulting canonical aggregate and queues the existing optimistic hosted PC snapshot mutation;
- correction refuses to proceed while another PC snapshot mutation still requires resolution;
- focused shared/domain and Desktop controller tests.

The initial branch push exposed one Kotlin visibility mismatch between a public controller method and an internal Desktop details model. The follow-up commit aligned visibility only.

Validation:

- initial implementation head `2d792abaa843734acd2a1226f91d2e86e7b2b929`;
- initial push Scaffold `35288970002` — FAILED on the visibility mismatch;
- final implementation head `50132cdb295097ac4a7ab91c4d766b900eeb7771`;
- corrected push Scaffold `35289198415` — SUCCESS;
- PR Scaffold `35289424011` — SUCCESS;
- PR #77 merged as `e14784390971f2e27025dd2fff1f5000658eb2f0`;
- post-merge Scaffold `35289703289` — SUCCESS.

This slice does not create a second Desktop character model, a generalized audit/event-sourcing framework, a new local schema, automatic Player impersonation or direct sync bypass.

### PC Manager ownership/controller administration — repository core

PR #79 merged the explicit ownership/controller administration core. No hosted database migration was required; the existing nullable `pc.owner_user_id` and `pc.controller_user_id` columns are reused.

Integrated repository behavior:

- dedicated `PUT /v1/pcs/{pcId}/authority` route;
- authenticated actor must be an active campaign DM;
- owner/controller are changed independently and may be explicitly unassigned;
- omitted authority fields are invalid, preventing accidental partial-clear semantics;
- every non-null target must be an active member of the same campaign;
- cross-campaign/inactive targets fail closed;
- missing PC -> typed 404; tombstoned PC -> typed 410;
- repeat of the same authority state is an idempotent no-op;
- authority mutation does not alter PC snapshot JSON or its optimistic synchronization revision;
- shared Kotlin hosted client preserves explicit JSON nulls even though the normal hosted serializer omits nullable null properties;
- Desktop Campaign Administration hydrates hosted roster/account/membership state before local convergence;
- Desktop PC Manager exposes explicit owner/controller controls using active campaign-member choices;
- local `PcAuthority` is updated from the authoritative hosted response.

Validation:

- initial implementation head `5de58771702dd2f1f548ca00f066318c630bb622`;
- initial push Scaffold `35290905581` — FAILED on narrow backend query-row typing and Kotlin visibility compile issues; hosted database contract passed;
- final head `418ac19d4d247cfbf19d6fb7f9b158df5c900bdc`;
- corrected push Scaffold `35291183960` — SUCCESS;
- PR Scaffold `35291417403` — SUCCESS;
- PR #79 merged as `2e12400ee18026c702d6727793a3aea5d23d07b4`;
- post-merge Scaffold `35291685597` — SUCCESS.

### DEV provider verification — CLOSED

The Worker/API change from PR #79 is deployed to the existing DEV Worker `dnd-custom-aid-api`.

Owner-executed deployment evidence:

- local project path: `D:\DnD_Aid\repo\dnd_custom_aid\backend`;
- `npm run deploy` succeeded;
- deployed Worker version: `ccdeca47-7622-4eb7-8dfa-a197d62bf3cb`;
- `GET /health` -> 200 / normal service-health JSON;
- unauthenticated `GET /v1/campaigns/00000000-0000-0000-0000-000000000000/members` -> 401 / `UNAUTHENTICATED`;
- unauthenticated `GET /v1/pcs/00000000-0000-0000-0000-000000000000/authority` -> 401 / `UNAUTHENTICATED`.

This closes the deployment/route-presence/auth-boundary gate. An authenticated live authority mutation was not part of this deployment smoke check; its semantics remain covered by repository tests.

## 6. Next bounded package — PC Sheet PDF Export shared foundation

D-0074 fully closes the PC Sheet PDF product definition. The canonical PC model is now coherent enough to start a shared export-semantics/render-plan foundation without waiting on Media/object storage.

Initial bounded direction after provider closure:

- canonical export snapshot abstraction over the existing PC data;
- Permanent vs Current Snapshot selection contract;
- visual-family selection model: Classic, Custom v1, Custom v2 per Attribute, Custom v2 per Ability;
- custom-stat presentation modes and overflow/Extended-page render-plan semantics;
- portrait behavior represented as optional local/cached input rather than network dependency;
- optional Spellbook inclusion contract;
- platform-neutral render plan first; actual platform PDF renderer/coordinates follow in separate bounded work where helpful;
- preserve local/offline generation and one canonical PC across Player/DM surfaces.

D-0072 freeze/unfreeze remains required but currently lacks a concrete product contract in the repository: there is no freeze field and no approved definition of what operations freezing blocks. Do not invent that behavior. Broader lifecycle administration and duplication should also remain separate from the PDF foundation.

Media/Handouts and deferred richer Homebrew families remain later Wave 7 packages.

## 7. Security/provider boundaries

Repository is intentionally public. Never request, paste or commit secrets, credentials, OTPs, tokens, DB connection strings, JWTs or private keys.

Green CI does not prove provider/deployment behavior. Do not repeat completed provider verification merely because docs/local persistence changed.

Object-storage provider selection remains deferred until Media/Handouts/assets concretely require it.

Known residual: owner-local backend install reported 3 high-severity npm vulnerabilities. Do not run `npm audit fix --force` blindly; inspect package reachability and available fixed versions when a relevant hardening package is scheduled.

### PC Sheet post-preqa.4 repair / preqa.5 candidate — 2026-09-25

The owner-observed `0.5.0-preqa.4` cross-family failure remains the acceptance authority, but the repair package is now repository-green. Final implementation head `cfaa98f92cecb846babcf83f180783839ccd56c6` passed push Scaffold `36196067865` and PR Scaffold `36196073964`. The distinguishable Android candidate is `0.5.0-preqa.5` / `50500` at `62eb4b474b1b55d3e3875ca45a41b30c4f7a4523`; push `36196696061` and PR `36196702100` both passed, with preferred debug-APK artifact `10890483613`.

Owner manual QA for **Aldren Vale / Permanente** across Fantasy Sheet, Custom v1, Custom v2 per Attribute and Custom v2 per Ability is **PASS** on `0.5.0-preqa.5`. The owner reported only a minor Custom v1/v2 continuation-page visual issue—header/first-row overlap and excessive vertical row height—and explicitly authorized continuing while fixing it opportunistically. This residual is non-blocking. PR #104 merged the passed repair into `main` as `8b1618d56d5483524559f9598bc8862acfe91c9a`; post-merge Scaffold #3826 / `36203501345` passed. Resume the hosted-player Android runtime sequence with Aldren Share, then Ilyra Custom v2 + Spellbook/unsaved-export persistence verification, Mara both Custom-v2 Extended variants and one Current Snapshot fallback-notice check. Media/Handouts remains blocked until that runtime sequence is recorded.

## 8. Resume rule

Read `AGENTS.md`, `docs/PROJECT_STATE.md`, `docs/BRANCH_STATUS.md`, `docs/checkpoints/LATEST.md`, the checkpoint referenced there, D-0071/D-0072/D-0073/D-0075 and `docs/ROADMAP.md`.

Resume Wave 7 from current `main` after Android PC-sheet Save/Share integration. The PDF visual/layout/text-filling gate is OWNER APPROVED and frozen at renderer `f7e4417c05a2981415ef3648ead740e20469fe33` / proof artifact `10756937024`; preserve the accepted Custom-v2 Equipment / Equipo Especial deviation exactly. PR #86 integrated Desktop Save/Share as `c28ad548113b368413e479de544c85aa8c924ef4`. PR #88 integrated the generated Android renderer bridge as `c5963881bdff2597770d3f6a26992b8567b2a35b`. PR #89 integrated Android Player/authorized-DM Save/Share as `e6e153a53bba8aa532b5c371dcc16849a901a541`; post-merge main Scaffold #3336 / `35909497243` passed. Read `docs/checkpoints/2026-09-23_PC_SHEET_PDF_ANDROID_SAVE_SHARE_PASS.md` first. The first remaining boundary is real Android device/runtime smoke of Save, Share, unsaved-export non-persistence and the Current Snapshot fallback notice. Do not reopen the frozen visual design or start the next Wave 7 package before that manual smoke is recorded.

### PC Sheet PDF Export — shared semantic/render-plan foundation

PR #83 integrated the D-0074 platform-neutral export plan over canonical PC data. The foundation covers state selection, visual-family selection, authoritative v1/v2 template-page mapping, custom-stat modes, overflow routes, portrait behavior and the optional attached-spell Spellbook contract. It does not render physical PDFs yet.

Validation: final implementation head `dea22b0823d6c4ad54e24839943d5952e2020392`; push Scaffold `35294590552` SUCCESS; PR Scaffold `35294792657` SUCCESS; merge `f6350d34087aae55d5247f2ba23153814eeed04b`; post-merge Scaffold `35295050340` SUCCESS.

Custom v1/v2 source PDFs remain authoritative. Populated dummy-data examples are a mandatory owner approval gate when visual rendering reaches reviewable output.


## PC Sheet PDF visual-process security - 2026-09-22

Current visual work must read:

- `docs/PC_SHEET_PDF_VISUAL_CONTRACT.md`;
- `docs/PC_SHEET_PDF_ITERATION_LEDGER.md`;
- `docs/PC_SHEET_PDF_GEOMETRY_GATES.md`.

Owner-facing name for the application-designed legacy `CLASSIC_DND_STYLE` family is **Fantasy Sheet**. It must not be described as Classic, official, or official-like. An official-like D&D sheet would be a separate future family/gate.

Every future PDF iteration must record run logs/results and pass an X/Y pre-print audit before an owner-facing proof is promoted.


### PC Sheet PDF recovery VR-3 - pre-print pass

Implementation `a5cb2311a0beb8454291d8b9985cb1b13f37a3dd` passed Scaffold push #3261 and PR #3262. Exact proof artifact `10723153227` passed the durable terminology, ordered-layer and measured X/Y gates (15/15 anchors) plus Worker rendered-page preflight. Owner visual QA remains pending. PR #85 stays DRAFT / DO NOT MERGE; Save/Share remains blocked.


### PC Sheet PDF final visual approval + Desktop Save/Share - 2026-09-23

The final visual/layout/text-filling gate is OWNER APPROVED. PR #85 integrated the frozen renderer and its approval history into main as `49dfc78f132c9db9763513c591e877a1749285d5`.

Frozen renderer implementation: `f7e4417c05a2981415ef3648ead740e20469fe33`. Frozen owner-approved proof artifact: `10756937024`.

Historical 2026-09-23 fact: the owner approved Custom-v2 Equipment / Equipo Especial continuation despite a non-literal implementation. **That historical acceptance is superseded for the active 2026-09-28 repair by the newer owner clarification:** ordinary Equipment and Equipo Especial already have usable native/source elements and the next repair must reuse/copy those elements rather than preserve the later generic reconstruction. Do not rewrite the historical approval; do not use it as current repair authority.

Desktop Save/Share implementation `fafd0f136e5b5dd283b17865e3edc494261f4126` adds PC Manager export choices and a delivery service that invokes the existing `PcSheetPdfExportPlanner -> DesktopPcSheetWholeDraftRenderer` path. Save writes locally; Share stages the same PDF bytes and places the file on the system clipboard. No renderer/layout file changes in this package.

Validation: push Scaffold #3294 / `35900383811` SUCCESS; PR Scaffold #3295 / `35900422551` SUCCESS. The parity test compares service vs direct renderer plan, extracted text, page count and rasterized page pixels, and verifies Save/Share staging preserve the generated bytes.


### PC Sheet PDF Android renderer + Save/Share integration — 2026-09-23

Android now uses the same shared D-0074 export semantics and a mechanically generated port of the frozen Desktop renderer authority rather than an independently maintained layout.

Integrated milestones:

- PR #88 — Android generated renderer bridge, merged as `c5963881bdff2597770d3f6a26992b8567b2a35b`; generated-source sync guard, shared root owner assets and PdfBox-Android packaging; post-merge main #3315 PASS.
- PR #89 — Android Player / authorized-DM Save/Share invocation, merged as `e6e153a53bba8aa532b5c371dcc16849a901a541`; post-merge main #3336 / `35909497243` PASS.

Android export behavior:

- `Ajustes de personaje -> Hoja de personaje PDF`;
- all D-0074 family/state/custom-stat/portrait/Spellbook choices;
- local/offline generation through `PcSheetPdfExportPlanner -> AndroidPcSheetRendererBridge`;
- native Save via `CreateDocument(application/pdf)`;
- native Share via private cache + non-exported FileProvider + Android chooser;
- explicit `Exportar sin guardar` confirmation for pending structural edits;
- export projection does not call repository Save;
- Table Mode retains PDF export access;
- Current Snapshot truthfully falls back to Permanent with the existing notice when no separate local snapshot aggregate exists.

Repository validation is complete. The remaining gate is **manual Android device/runtime smoke**, because repository CI cannot tap a real document picker/share target. Do not classify that manual smoke as a visual-design gate.


### PC Sheet runtime QA character pack — 2026-09-23

The owner cannot run the pending Android real-device PDF smoke immediately, so a reproducible QA data pack is integrated into `main` through PR #91.

Durable resume record:

`docs/checkpoints/2026-09-23_PC_SHEET_RUNTIME_QA_CHARACTER_PACK_READY.md`

Fixtures:

- Aldren Vale — strict SRD 5.1 Human Acolyte Fighter 5 / Champion;
- Ilyra Quill — strict SRD 5.2.1 High Elf Sage Wizard 5 / Evoker;
- Mara de los Siete Umbrales — explicitly CUSTOM high-volume Extended-page stress character.

All three are app-owned Character Backup v2 JSON documents suitable for the Android Character Directory `Importar` flow. Automated tests prove codec decode/round-trip/restore-as-copy and fixture-specific PDF QA invariants.

A guarded DEV PostgreSQL seed is prepared at:

`database/qa/seed_pc_sheet_runtime_characters.sql`

The original guarded seed remains the canonical fixture-loading design. Hosted DEV QA data is now **READY / VERIFIED** through an owner-run pure-PostgreSQL Neon SQL Editor adaptation generated from the exact integrated fixture files: Outlook remains `DM / ACTIVE`; Gmail is deliberately `PLAYER / ACTIVE` for this QA; Aldren, Ilyra and Mara are present with their deterministic IDs and are owned/controlled by Gmail without advancing snapshot revision/content.

Fixture/test head `a06b2fe5712426e6b42e5ea88f0fe0b2f9529fd1` passed #3348/#3349; PR #91 merged as `5e778ced3b85fc66d4ca449727a3451e91c50d7e`; closure PR #92 merged as `026dc8ca00967e5ca3d932562e2a49d97e6eaaad`; post-merge #3367 / `35915730801` PASS.

Durable hosted-setup evidence: `docs/checkpoints/2026-09-24_PC_SHEET_RUNTIME_QA_HOSTED_PLAYER_SETUP_READY.md`. Hosted Player-path sync has since been manually verified and converged. The first Aldren Fantasy Sheet runtime export then failed safely on six bounded-routing overflows before a PDF was produced. PR #96 reproduced and repaired that defect and is integrated as `eece864e5867f477b8bdd57596a1bee1638b4ceb`; push `36056511437`, PR `36056515058`, and post-merge `36057383682` all passed. It also moves QA diagnostics behind a debug-only in-app settings entry and advances Android to `0.5.0-preqa.1` / `50100`. Resume from `docs/checkpoints/2026-09-24_PC_SHEET_FANTASY_ALDREN_RUNTIME_OVERFLOW_REPAIR.md`; Save/Share remains unpassed until the repaired APK is manually rerun. Do not start Media / Handouts before that gate is recorded.
