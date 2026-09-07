# Phase 4 pre-QA UX repair — Pass 04

**Date:** 2026-09-07  
**Status:** IMPLEMENTED + AUTOMATED GATE GREEN; owner visual/device review still pending  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Tested product commit:** `122c55998126d3028c92f32cc1c57558be20f841`  
**Tested product tree:** `b3559871e603c0b32f538a98ca54ea82160d180c`  
**Helper workflow run:** `34169915610`  
**Review version:** `0.4.0-preqa.4`  
**Review build:** `40400`  
**Review APK filename:** `DND_Custom_Aid_0.4.0-preqa.4_Build_40400_debug.apk`  
**Review artifact name:** `DND-Custom-Aid-0.4.0-preqa.4-build-40400-debug`  
**APK SHA-256:** `e2eda1e09ab335ea370bb2b299fc28ce64059edbd3a1b53d889cca9718c85520`  
**APK size:** `36161616` bytes

## Pass 04 completed

- continued the tab-by-tab fixed/sticky audit using a deliberately narrow rule: pin only high-value control/reference bands, not ordinary section headings;
- **Habilidades:** moved the view selector and the three passive reference values outside the detail `LazyColumn`; the detailed ability/save/skill/custom/proficiency content now scrolls independently below them;
- **Gestión:** moved `Estado operativo` outside the management `LazyColumn`, keeping Inspiration, HP reference and contextual death-save controls available while conditions, concentration, resources, rest assistant, temporary effects and reconciliation checkpoints scroll below;
- **Equipo:** promoted the existing summary + Add + search/filter control card to a sticky tools header while object sections and currencies remain scrollable;
- **Rasgos:** promoted the existing summary + Add + search/filter + grouping control card to a sticky tools header while trait groups remain scrollable;
- **Conjuros:** preserved the already-fixed source selector and existing sticky level headers; intentionally did not introduce a competing second sticky hierarchy for search/filter tools in this pass;
- propagated the user spacing-scale preference through the newly fixed bands and the main Habilidades, Gestión, Equipo, Rasgos and Conjuros list margins/gaps touched by this pass, while retaining minimum control sizes/touch targets;
- confirmed the six Gestión editors/previews already use `CharacterImeSafeEditorDialog`; no redundant editor rewrite was introduced;
- removed the temporary Pass 03/Pass 04 helper workflows and transform/checkpoint tools from the tested product commit so the repair branch does not accumulate execution scaffolding;
- Android review identity advanced to version `0.4.0-preqa.4` / build `40400`.

## Still open — next repair pass

1. continue the long-collection fixed-control audit for Conjuros as a special nested-sticky case, plus Notas and conditional module collections where the controls genuinely benefit from staying visible;
2. continue spacing-scale propagation through remaining tab-specific margins/paddings/gaps, avoiding intrinsic sizes and minimum touch targets;
3. continue the IME/window audit outside the already-verified reusable dialogs, especially persistent wide/tablet editor panels and any remaining inline editors;
4. exercise expanded column maxima surface-by-surface and cap only layouts that become genuinely unreadable;
5. owner visual audition of fonts/text-size/compactness combinations on phone and tablet;
6. reconcile stale current-stage prose in `README.md`, `MANIFEST.md`, `ROADMAP.md`, `TESTING.md`, `ARCHITECTURE.md` and the historical current-stage paragraph in `AGENTS.md`; `docs/PROJECT_STATE.md` + `docs/checkpoints/LATEST.md` remain the active current-state authority meanwhile;
7. do **not** resume formal M6 until the owner explicitly says the replacement build is ready.

## Verification

The Pass 04 helper gate executed successfully before the tested product commit: `git diff --check`, explicit fixed/sticky-shape checks, version/build identity checks, `:shared:desktopTest`, `:androidApp:assembleDebug`, `:desktopApp:build`, backend TypeScript check, identified APK copy/hash/size capture and artifact upload.

Automated green is technical evidence only. Phone/tablet visual acceptance has not yet been performed for this pass.

## Exact resume instruction

Read `AGENTS.md`, then `docs/checkpoints/LATEST.md`, then this checkpoint. Continue with the remaining long-collection fixed-control audit, spacing-scale propagation and IME/window audit. Do not return to historical M6 unless the owner explicitly requests comparison.
