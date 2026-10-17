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
