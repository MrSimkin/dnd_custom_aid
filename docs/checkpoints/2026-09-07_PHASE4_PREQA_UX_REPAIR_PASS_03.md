# Phase 4 pre-QA UX repair — Pass 03

**Date:** 2026-09-07  
**Status:** IMPLEMENTED + AUTOMATED GATE GREEN; owner visual/device review still pending  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Tested product commit:** `68f74dad84ac2496431a3ae81c8e6e0864d5d772`  
**Tested product tree:** `fb1181befc32dff9caebbfae2fdd3c38dcb76435`  
**Helper workflow run:** `34167905159`  
**Review version:** `0.4.0-preqa.3`  
**Review build:** `40300`  
**Review APK filename:** `DND_Custom_Aid_0.4.0-preqa.3_Build_40300_debug.apk`  
**Review artifact name:** `DND-Custom-Aid-0.4.0-preqa.3-build-40300-debug`  
**APK SHA-256:** `292349b9e43ac2e653393dbba5e6134ea79dd9c58c8d5ffe64b084288ac30b27`  
**APK size:** `36161616` bytes

## Pass 03 completed

- removed the distributed row-level d20 UI from standard skills, saving throws, custom skills and combat entries;
- removed the obsolete per-button `CharacterD20RollUiV4.kt` implementation and fail-closed verification ensures no `CharacterD20RollButtonV4` reference survives;
- added a dedicated **Dados** navigation destination whose surface is explicitly titled **Tirada de dados**;
- the dice surface offers contextual targets for all six ability checks, all six saving throws, all standard skills, custom skills and combat entries that have an attack modifier;
- one fixed top panel always states exactly what is selected, its context and modifier, provides the only d20 action, and shows the latest raw d20 + modifier = total result inline;
- the selectable roll catalogue scrolls below the fixed panel, so adding roll targets no longer consumes permanent horizontal space in the character rows;
- the Combat operational card is now fixed outside the attacks/actions `LazyColumn`; attacks/actions scroll independently below it;
- the global character identity/save header was already fixed by the adaptive shell and remains unchanged;
- Android review identity advanced to version `0.4.0-preqa.3` / build `40300`; About continues to read the real BuildConfig values.

## Still open — next repair pass

1. continue the tab-by-tab fixed/sticky audit beyond the already-fixed global identity header, Dice selected-roll panel and Combat operational panel;
2. prioritize Habilidades, Gestión and long collection tabs, pinning only high-value reference/actions rather than indiscriminately making every section sticky;
3. continue propagating the extra spacing-scale preference through tab-specific margins/paddings/gaps not yet routed through shared primitives;
4. complete the remaining IME/window audit so every editor/dialog is resize/scroll safe at large text sizes;
5. exercise expanded column maxima surface-by-surface and cap only layouts that become genuinely unreadable;
6. owner visual audition of fonts/text-size/compactness combinations on phone and tablet;
7. reconcile stale current-stage prose in `README.md`, `MANIFEST.md`, `ROADMAP.md`, `TESTING.md`, `ARCHITECTURE.md` and the historical current-stage paragraph in `AGENTS.md`; `docs/PROJECT_STATE.md` + `docs/checkpoints/LATEST.md` remain the active current-state authority meanwhile;
8. do **not** resume formal M6 until the owner explicitly says the replacement build is ready.

## Verification

The Pass 03 helper gate executed successfully before the tested product commit: `git diff --check`, explicit absence of embedded d20 component references, `:shared:desktopTest`, `:androidApp:assembleDebug`, `:desktopApp:build`, backend TypeScript check, identified APK copy/hash/size capture and artifact upload.

Automated green is technical evidence only. Phone/tablet visual acceptance has not yet been performed for this pass.

## Exact resume instruction

Read `AGENTS.md`, then `docs/checkpoints/LATEST.md`, then this checkpoint. Continue with the per-tab fixed/sticky audit plus remaining spacing-scale and IME propagation. Do not return to historical M6 unless the owner explicitly requests comparison.
