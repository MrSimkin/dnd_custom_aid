# Branch status and repository-ordering map

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
