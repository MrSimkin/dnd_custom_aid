# Mara Phase 3 — interruption recovery checkpoint

**Local date:** 2026-09-30 (Chile)  
**Purpose:** authoritative resume snapshot after ChatGPT timeout / connection / poll interruptions  
**Phase:** 3 — renderer repair implementation  
**Branch:** `repair/mara-phase3-semantic-flow-compositor`  
**Main base:** `c4e65955a5f58f88e0b03807b4616a3f8a9323a8`  
**Current implementation code head (last non-documentation commit):** `5e041cc8b76f1bc8b377be00d3dd8d9e1024b775`  
**Recovery documentation:** this checkpoint and the `LATEST.md` routing update are documentation-only commits on top of that implementation code; always verify the physical branch HEAD before writing.  
**Branch relation to main at initial recovery capture:** 109 implementation/documentation commits ahead / 0 behind before the recovery-document commits  
**Open implementation PR:** none  
**Current status:** ACTIVE / HEAD RED / RECOVERY STATE CONSOLIDATED  
**Owner visual handoff:** NOT AUTHORIZED YET

## Authority order on resume

Read and obey in this order:

1. `AGENTS.md`
2. `RESUME.md`
3. `docs/checkpoints/LATEST.md`
4. **this recovery checkpoint**
5. `docs/checkpoints/2026-09-30_MARA_PHASE3_IMPLEMENTATION.md`
6. the authoritative final burn-down in `docs/checkpoints/2026-09-30_MARA_PHASE2_EXISTING_REPAIR_AUDIT.md`
7. `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`
8. `docs/checkpoints/2026-09-26_PC_SHEET_MARA_PREQA8_PDF_VISUAL_QA_DEFECTS.md`
9. `docs/PC_SHEET_PDF_VISUAL_CONTRACT.md`
10. `docs/PC_SHEET_PDF_GEOMETRY_GATES.md`

When older/provisional text conflicts with a later owner clarification or with the Phase-2 final burn-down, the **latest final owner decision / final burn-down wins**.

Do not ask the owner to repeat any decision already resolved in Phase 2.

## Owner decisions that remain normative

The Phase-2 final burn-down for M50800-01…32 is binding. Important cross-cutting rules include:

- reuse/copy native/source visual components before reconstruction;
- do not invent generic replacement modules when the same semantic native module exists;
- Custom v1/v2 bicolour table-row physical reference is 7 mm (~19.84 pt);
- meaningful semantic names/identities never use `...`;
- text-fit order is native size -> reasonable uniform compression -> wrap/grow -> explicit continuation;
- wrapped lines belonging to one semantic label use uniform typography/scale;
- global Extended pages behave like an automatic page-layout compositor using only valid family-native layouts;
- exhausted modules stop reserving space;
- page count is diagnostic only, never the optimization target;
- ordinary Equipment visible content is quantity + complete identity only;
- ordinary Equipment must not show weight, `Consumible`, `Equipado`, operational-state prose or long descriptions;
- wrapped ordinary Equipment identity stays atomic and continuation lines receive a visible indentation/cue;
- Equipo Especial uses/repeats the fixed native module; custom locations use native blank/custom capacity or a source-faithful blank-location fallback built from native pieces;
- Notes overflow uses the complete native Notes page;
- split semantic content uses explicit bidirectional `continúa en...` / `proviene de...` navigation;
- trait/ammunition/use trackers remain writable and compactly preserve runtime state, e.g. `____(2)/3`;
- Custom-v2 custom attribute title uses integrated native style, e.g. `ETEr`; compact references use `ETE`;
- per-Attribute skills do not repeat the owning key when the owner attribute is structurally explicit; detached/per-Ability/overflow skills do;
- Custom-v2 Combat/Actions uses one logical action record with content-driven height;
- Fantasy Combat/Actions continuation preserves table grammar;
- no owner candidate may reuse `0.5.0-preqa.8 / 50800`;
- exact candidate provenance must bind source commit -> unique version/build -> workflow/artifact -> APK SHA-256 -> exact four Mara proof hashes.

## Phase-3 architecture already implemented and green

### Package 1 — shared semantic-flow contracts

Implemented and validated:
- semantic module identity + stable semantic record references;
- semantic association for Traits, Combat/Actions, Resources, class choices, ordinary/special Equipment, Notes and Spells;
- bidirectional continuation endpoints and markers;
- compact writable tracker state such as `____(2)/3`;
- shared meaningful-text fit policy with no semantic ellipsis disposition;
- ordinary Equipment compact label changed to quantity + identity only;
- ordinary Equipment metadata no longer rerouted into Notes.

Validation:
- Scaffold #4391 / `36785191325` — backend PASS, hosted-database PASS, kotlin PASS.

### Package 2 — native-module contracts

Implemented and validated:
- explicit repeat units: native components / native rows-columns / logical rows / whole module / none;
- shareable vs full-native-page exclusivity;
- exhausted-module space release;
- arbitrary geometry resize prohibited.

Locked behaviors:
- Notes = full native page, exclusive;
- Equipo Especial = repeat whole native module;
- ordinary Equipment = native rows/columns;
- Combat/Actions = content-driven logical rows;
- Traits/Background/Resources/Class choices = shareable only through valid family layouts.

Validation:
- Scaffold #4394 / `36785716176` — PASS on backend, hosted-database and kotlin.

### Package 3 — constraint-aware Extended-page composer

Implemented and validated:
- consumes only renderer/family-approved native layout templates;
- never invents/resizes geometry;
- sees active semantic demands globally;
- lets exhausted modules disappear;
- can place compatible active modules into valid native slots;
- Notes full-page exclusivity enforced;
- returns remaining demand for later pages;
- fails explicitly when no valid layout can consume remaining content.

Validation:
- Scaffold #4396 / `36786217919` — PASS on backend, hosted-database and kotlin.

## Family integration already implemented

The branch contains substantial Desktop + Android parity work. Do not redo these changes from memory.

### Custom-v1

Implemented:
- long custom-stat skill wrapping for `Lectura de presagios`;
- uniform source-scale behavior across wrapped lines;
- shared custom-attribute title semantics;
- ordinary Equipment identity = quantity + full name only;
- ordinary Equipment wrapping/continuation indentation;
- Combat/Actions cell wrapping rather than unreadable compression;
- writable use trackers;
- fixed-native-row Special Equipment flow groundwork;
- native Notes packing/continuation;
- full native Notes pages for continuation;
- bidirectional continuation metadata;
- long-name ribbon staged fit: one line -> bounded reduction -> two centered lines.

### Custom-v2

Implemented:
- integrated attribute titles such as `ETEr`;
- contextual owner-key behavior for skills;
- six-row/native-capacity Custom Statistics direction instead of arbitrary 3-card limit;
- source-derived/native attribute ornament reuse;
- isolated source logo to prevent stale hidden-template text;
- Combat/Actions logical-record rows with content-driven height;
- Notes packed by native records;
- full native Notes continuation pages;
- first Notes semantic-layer identity;
- narrative overflow removed from generic Traits flow and routed through dedicated `BACKGROUND_STORY` semantic module;
- explicit narrative continuation markers;
- safe custom Special-Equipment location placement on native-compatible blank location capacity;
- subsequent ordinary-vs-Special Equipment semantic separation is implemented at current HEAD but **not yet compiling/validated**; see recovery blocker below.

### Fantasy

Implemented:
- Combat/Actions continuation changed from prose serialization to table grammar;
- long/semantic text routing work;
- writable use trackers;
- Notes converted to a full-page semantic flow;
- native Fantasy Notes frame geometry restored;
- Notes semantic tests no longer depend on a fixed page-count target.

### Shared / tests

Added/updated:
- `PcSheetPdfSemanticFlow.kt`
- `PcSheetPdfNativeModuleContracts.kt`
- `PcSheetPdfExtendedComposer.kt`
- `PcSheetPdfAttributeSemantics.kt`
- `PcSheetPdfContinuationFlow.kt`
- `PcSheetPdfNotesFlow.kt`
- semantic/native/composer/attribute/continuation/Notes tests;
- Mara runtime fixture regressions for long Custom-v1 skill identity, Custom-v2 attribute semantics, stale source labels, Fantasy Combat semantics and related routes;
- platform text-layout probing for the character-name ribbon;
- Desktop/Android renderer parity work across affected families.

## Last known fully green implementation state

There are two useful green milestones:

### Family integration checkpoint A

Implementation head:
`ff6bc98bb207e021e3345ebc3f4e3bdb12677303`

Validation:
- Scaffold #4485 / `36804840149` — backend PASS, hosted-database PASS, kotlin PASS;
- Android debug APK and PDF proof artifacts were uploaded by workflow.

The Phase-3 canonical checkpoint records this as **REPOSITORY-GREEN / NOT OWNER-ACCEPTED**.

### Latest green code head before current compile break

Head:
`5281a9db168fb5470a5463c3b981a5556609ec6d`

Commit:
`fix: sync Android safe v2 special location placement`

Validation:
- Scaffold #4488 / `36805644409` — SUCCESS.

This head includes the safe Custom-v2 Special Equipment custom-location placement work.

**Do not reset to this head automatically.** It is a recovery comparison point only. The current branch contains three later commits that must not be duplicated.

## Current HEAD and exact interrupted work

Current implementation code head:
`5e041cc8b76f1bc8b377be00d3dd8d9e1024b775`

The three commits after the latest green code head are:

1. `1691ce8d11e8cc69715e9e7ef5156941915e1ab6` — `fix: separate v2 ordinary and Special Equipment semantics`
2. `d3cd5ff7ec73d4b4be9179dc67427319894a7e43` — `fix: sync Android v2 Equipment semantic separation`
3. `5e041cc8b76f1bc8b377be00d3dd8d9e1024b775` — `test: enforce v2 Equipment semantic association`

These commits modify only:
- Desktop Custom-v2 Extended renderer;
- Android Custom-v2 Extended renderer;
- Desktop whole-draft renderer regression test.

They are **already published on the branch**. Do not recreate them.

### Current CI failure

Latest run:
- Scaffold #4491 / `36805802847`;
- backend PASS;
- hosted-database PASS;
- kotlin FAIL during compilation.

The failure is compile-time, before the new Equipment semantic regression can be evaluated.

Missing references in both Desktop and Android Custom-v2 Extended renderers:

- `INVENTORY_SPECIAL_FIRST_RULE_TOP`
- `INVENTORY_SPECIAL_ROW_STEP`
- `INVENTORY_SPECIAL_CHECK_FIRST_TOP`
- `INVENTORY_ORDINARY_CONTINUATION_INDENT_WIDTH`
- `INVENTORY_ORDINARY_CONTINUATION_PREFIX`
- `INVENTORY_SPECIAL_LOCATION_TEXT_WIDTH`
- `INVENTORY_SPECIAL_NAME_TEXT_WIDTH`
- `INVENTORY_SPECIAL_DETAIL_TEXT_WIDTH`
- `INVENTORY_SPECIAL_CONTINUATION_PREFIX`

No evidence currently shows a semantic/test failure in those three commits because compilation stops first.

## Exact next authorized action

Resume from current HEAD `5e041cc8...`, **not** from an older branch and not from memory.

First task:

1. inspect current Desktop + Android Custom-v2 Inventory code around the unresolved references;
2. recover/derive those constants from the already-approved/native module geometry or existing measured source geometry;
3. do **not** invent arbitrary new module dimensions;
4. add the corresponding constants symmetrically to Desktop and Android;
5. rerun Scaffold for the resulting HEAD;
6. if compile passes, inspect the actual new Equipment semantic-association test result;
7. fix only demonstrated follow-on failures;
8. preserve the three already-published post-green commits instead of replaying them.

Do not begin unrelated new renderer work until this HEAD is green or until a real contradiction in native-source evidence is found.

## Remaining Phase-3 gates after the current blocker

Even after the Custom-v2 Inventory compile/test gate is green, Phase 3 is not closed.

Remaining work includes, at minimum:

- finish Custom-v2 ordinary Equipment + fixed-native Special Equipment presentation under the Phase-2 rules;
- finish/globalize actual family use of the Extended-page compositor where local role-owned page generation still remains;
- finish/reconcile Traits ordering and packing under stored/player order;
- finish Resources/class-choice global reclaim behavior;
- complete semantic-ellipsis / silent-drop audit across all meaningful identity paths;
- verify narrative/background and Notes bidirectional continuity in actual generated outputs;
- verify native-source geometry and no stale underlay after the new integrations;
- preserve Android/Desktop parity;
- generate real Mara successfully in all four families:
  - Fantasy;
  - Custom v1;
  - Custom v2 · Atributo;
  - Custom v2 · Habilidad;
- inspect the actual rendered candidate PDFs against M50800-01…32;
- classify each matrix item FIXED / OPEN / CHANGED-NEW for the exact candidate;
- only after internal acceptance, create a uniquely versioned owner candidate APK (> / different from 50800) with explicit owner-facing filename and complete provenance/hash chain;
- no Current Snapshot or Media/Handouts work while this PDF gate is active.

## Recovery safety rules

After any timeout / connection failure / poll error:

- verify branch HEAD and recent commits before repeating a write;
- verify latest workflow status before rerunning/recreating CI-related work;
- verify whether a PR/build/artifact already exists before creating another;
- never infer success from a commit message;
- a red intermediate Desktop-only or pre-parity commit is historical if a later Android-sync/full package commit exists; validate the latest complete package instead;
- do not resurrect provisional Phase-2 questions already resolved later in the final burn-down;
- do not treat green CI alone as owner visual acceptance;
- do not use historical page-count ceilings as product acceptance;
- do not issue an owner APK until exact-candidate all-four-family inspection passes.

## Point of interruption recorded here

At consolidation time:

- no workflow is in progress;
- no open PR exists for the Phase-3 branch;
- no owner candidate build/version has been issued from Phase 3;
- the last non-documentation implementation head is `5e041cc8...`;
- recovery documentation commits are intentionally layered on top and must not be mistaken for new renderer work;
- the implementation is blocked on the listed missing Custom-v2 Inventory constants;
- the correct continuation is the **Exact next authorized action** above.

This checkpoint is the operational resume authority until superseded by a later, explicitly published Phase-3 checkpoint.


## Copy/paste recovery prompt stored with this checkpoint

Use this after a timeout / connection failure / poll error while this recovery checkpoint remains canonical:

```text
Continúa por favor desde exactamente donde quedó la Fase 3 antes de la interrupción.

IMPORTANTE:
- NO reinicies el trabajo.
- NO reconstruyas el plan desde cero.
- NO me pidas repetir decisiones ya tomadas.
- NO repitas commits, PRs, workflows, builds, publicaciones ni cambios que puedan haber alcanzado el repositorio antes del error.

ANTES DE ACTUAR, reancla el trabajo usando el repositorio como autoridad y siguiendo exactamente:

AGENTS.md
→ RESUME.md
→ docs/checkpoints/LATEST.md
→ checkpoint canónico activo indicado por LATEST.md
→ docs/checkpoints/2026-09-30_MARA_PHASE3_IMPLEMENTATION.md
→ docs/checkpoints/2026-09-30_MARA_PHASE2_EXISTING_REPAIR_AUDIT.md
→ las demás autoridades que el checkpoint canónico mande leer.

Si LATEST.md sigue apuntando a:
docs/checkpoints/2026-09-30_MARA_PHASE3_INTERRUPTION_RECOVERY.md

trata ese archivo como la autoridad operacional inmediata para recuperar:
- fase y estado exactos;
- rama y HEAD real actuales;
- último HEAD de implementación;
- último estado verde comprobado;
- commits posteriores ya publicados;
- decisiones del owner;
- reglas visuales/semánticas vigentes;
- trabajo ya implementado;
- trabajo implementado pero aún no validado;
- fallos CI actuales;
- gates pendientes;
- próximo paso autorizado.

REGLA DE AUTORIDAD:
- las decisiones finales/clarificaciones más recientes del owner y el burn-down final de Fase 2 prevalecen sobre preguntas, clasificaciones o interpretaciones provisionales anteriores;
- NO resucites preguntas de Fase 2 que ya fueron respondidas;
- NO uses memoria del chat como autoridad si contradice el repositorio.

RECUPERACIÓN DE OPERACIONES:
1. verifica el HEAD físico actual de repair/mara-phase3-semantic-flow-compositor;
2. revisa los commits recientes;
3. revisa los workflows recientes y determina si hay alguno en progreso;
4. verifica si existe un PR abierto;
5. si una operación pudo haber quedado a medias, comprueba primero si ya ocurrió antes de repetirla;
6. distingue commits intermedios Desktop-only de paquetes posteriores con paridad Android;
7. no interpretes un mensaje de commit como PASS: usa evidencia CI/tests;
8. no hagas rollback automático al último verde; úsalo sólo como punto de comparación, salvo que evidencia técnica obligue a revertir.

ESTADO DE RECUPERACIÓN QUE DEBES VERIFICAR, NO ASUMIR:
- Fase 3 sigue activa;
- rama de implementación: repair/mara-phase3-semantic-flow-compositor;
- main de base de Fase 3: c4e65955a5f58f88e0b03807b4616a3f8a9323a8;
- último HEAD de código conocido al consolidar: 5e041cc8b76f1bc8b377be00d3dd8d9e1024b775;
- último código verde conocido antes del blocker actual: 5281a9db168fb5470a5463c3b981a5556609ec6d / Scaffold #4488;
- los tres commits posteriores 1691ce8d..., d3cd5ff7... y 5e041cc8... YA EXISTEN y NO deben recrearse;
- no había PR abierto al consolidar;
- no había workflow en progreso al consolidar;
- el blocker conocido era compilación de Custom-v2 Inventory por constantes de geometría/continuación referenciadas pero no declaradas en Desktop y Android.

PRIMER PASO AUTORIZADO:
- verifica que ese blocker siga siendo el actual;
- si sigue vigente, corrige únicamente las constantes faltantes usando geometría nativa/aprobada o medidas fuente existentes, sin inventar nuevas dimensiones;
- aplica paridad Desktop/Android;
- vuelve a ejecutar Scaffold;
- sólo después de compilar, evalúa el test nuevo de asociación semántica de Equipment;
- corrige únicamente fallos demostrados y sigue desde allí.

NO avances todavía a:
- owner APK;
- reutilización del build 50800;
- Current Snapshot;
- Media/Handouts;
- cierre de Fase 3.

Continúa autónomamente hasta el siguiente límite real de decisión del owner.

Además, conserva el entregable ya solicitado para el cierre de Fase 3:
al finalizar Fase 3, debes darme un prompt equivalente de recuperación/continuación para entrar a la fase siguiente sin pérdida de contexto.
```

If a later checkpoint supersedes this one, follow the newer canonical checkpoint instead of freezing the repository to the SHAs recorded above.
