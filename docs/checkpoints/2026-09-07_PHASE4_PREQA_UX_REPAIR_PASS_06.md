# Phase 4 pre-QA UX repair — Pass 06

**Date:** 2026-09-07  
**Status:** IMPLEMENTED + AUTOMATED GATE GREEN; owner visual/device review still pending  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Tested product commit:** `d72571afb0a0a41fbde8e9b8eabc29b8a381c9db`  
**Tested product tree:** `a1bd62064ef5b9c5b35ca49f94b4a3eb2e6b0716`  
**Helper workflow run:** `34171045399`  
**Review version:** `0.4.0-preqa.6`  
**Review build:** `40600`  
**Review APK filename:** `DND_Custom_Aid_0.4.0-preqa.6_Build_40600_debug.apk`  
**Review artifact name:** `DND-Custom-Aid-0.4.0-preqa.6-build-40600-debug`  
**APK SHA-256:** `36817044ccae7faa3d6a561c2994d41563e402b473c7897599f20f9a5dbb1d9c`  
**APK size:** `36161616` bytes

## Pass 06 completed

- audited every current use of the user-configurable card-column helper instead of globally removing readability caps;
- preserved **Combate**, **Rasgos** and **Notas** at maximum 4 columns because their cards contain text and/or multiple operational controls;
- preserved **Equipo especial** at maximum 3 columns because its cards add semantic badges, descriptions and actions;
- raised only **Equipo ordinario** from wide maximum 4 to **5 columns** while retaining its phone maximum of 3; ordinary equipment is deliberately the densest row-style collection and is the only audited card surface that safely benefits from the extra wide column;
- preserved the specialized Monedas, ability-score and Supercompact grids under their existing dedicated rules rather than incorrectly treating them as generic card-column consumers;
- propagated `spacingScalePercent` through remaining raw `Arrangement.spacedBy` layout gaps and selected container/list paddings in **Combate, Rasgos, Equipo, Notas and Vista supercompacta**, plus `CompactMenuSurfaceV4` interior spacing;
- deliberately did **not** scale `heightIn`, widths, icon sizes, editor working heights, min/max line counts or other intrinsic/minimum touch dimensions;
- completed the remaining IME/window structural audit: all current text-input surfaces are protected either by `CharacterImeSafeEditorDialog`, by an owning container with `imePadding()`, or—in the case of the reusable collection search field—by every current parent call site; no redundant nested IME inset was added;
- reviewed the accumulated fixed/sticky footprint in Habilidades, Gestión and Conjuros; no further static reduction was made because Conjuros already keeps reorder guidance scrollable and additional removal would require a visible interaction redesign better judged during real-device audition;
- removed the temporary Pass 06 audit/helper/transform/checkpoint infrastructure from the tested product commit;
- Android review identity advanced to version `0.4.0-preqa.6` / build `40600`.

## Still open — next repair pass

1. perform one final residual spacing/consistency sweep outside the focused Pass 03–06 surfaces, changing only clearly app-controlled whitespace and not intrinsic/touch/editor dimensions;
2. prepare the owner visual audition matrix for fonts, application text scale, spacing compactness and high-column opt-in on phone/tablet, with special attention to the fixed Conjuros footprint and 5-column ordinary Equipment on wide layouts;
3. reconcile stale current-stage prose in `README.md`, `MANIFEST.md`, `ROADMAP.md`, `TESTING.md`, `ARCHITECTURE.md` and the historical current-stage paragraph in `AGENTS.md` after the repair implementation line is stable; `docs/PROJECT_STATE.md` + `docs/checkpoints/LATEST.md` remain current-state authority meanwhile;
4. freeze a replacement owner-QA candidate only when the owner says the repair line is ready for formal device QA;
5. do **not** resume historical M6 or DM implementation before that explicit owner gate.

## Verification

The Pass 06 helper gate executed successfully before the tested product commit: `git diff --check`, explicit column-cap/spacing/IME-shape checks, version/build identity checks, `:shared:desktopTest`, `:androidApp:assembleDebug`, `:desktopApp:build`, backend TypeScript check, identified APK copy/hash/size capture and artifact upload.

The preceding Pass 06 audit workflow also inventoried every `constrainedCardColumnsV4` call, specialized grid site, fixed/sticky control, `imePadding()` marker and text-input file before implementation.

Automated green is technical evidence only. Phone/tablet visual acceptance has not yet been performed for this pass.

## Exact resume instruction

Read `AGENTS.md`, then `docs/checkpoints/LATEST.md`, then this checkpoint. Continue with the final residual spacing/consistency sweep and owner-audition preparation. Do not return to historical M6 unless the owner explicitly requests it.
