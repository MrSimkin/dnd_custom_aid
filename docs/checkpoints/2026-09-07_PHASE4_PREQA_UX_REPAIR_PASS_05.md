# Phase 4 pre-QA UX repair — Pass 05

**Date:** 2026-09-07  
**Status:** IMPLEMENTED + AUTOMATED GATE GREEN; owner visual/device review still pending  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Tested product commit:** `d24017bde4679dc2d61908a8be84b44a11c9325b`  
**Tested product tree:** `495caa04fc4e44b4e65f30bafb973af64b994144`  
**Helper workflow run:** `34170320626`  
**Review version:** `0.4.0-preqa.5`  
**Review build:** `40500`  
**Review APK filename:** `DND_Custom_Aid_0.4.0-preqa.5_Build_40500_debug.apk`  
**Review artifact name:** `DND-Custom-Aid-0.4.0-preqa.5-build-40500-debug`  
**APK SHA-256:** `64861ed5c37896d9169d50d020c0707815ed1160951d998d26d7b4372973549f`  
**APK size:** `36161616` bytes

## Pass 05 completed

- continued the fixed-control audit across the remaining long conditional collections without changing domain models or persistence;
- **Conjuros:** moved the compact operational band (title, Add, search/order/filter toolbar) outside the scrolling spell-level list, so it remains visible while the existing level sticky headers continue to operate independently; the contextual manual-reorder guidance remains scrollable rather than consuming permanent height;
- **Técnicas / Metamagia / Pactos:** promoted the shared class-option tools card to a sticky collection header in the common implementation;
- **Artífice:** promoted its search/filter/order tools card to a sticky collection header;
- **Formas:** promoted its search/filter/order tools card to a sticky collection header;
- **Compañeros:** promoted its search/filter/order tools card to a sticky collection header;
- propagated the spacing-scale preference through those collection margins/gaps and through the persistent wide/tablet editor panel spacing while deliberately preserving their functional editor widths (340/400/420 dp) and all minimum control/touch sizes;
- **Notas:** propagated spacing-scale behavior through list/card/grid spacing only; the large general-notes editor remains scrollable content and its editor height/min-line constraints were intentionally left unchanged;
- preserved mobile conditional-module editors on `CharacterImeSafeEditorDialog` and preserved wide/tablet editors as scrollable persistent side panels with the existing outer `imePadding()` behavior;
- removed temporary Pass 05 workflow/transform/checkpoint infrastructure from the tested product commit;
- Android review identity advanced to version `0.4.0-preqa.5` / build `40500`.

## Still open — next repair pass

1. audit expanded card-column maxima surface-by-surface and cap only layouts that become genuinely unreadable at the user-selected maximum;
2. continue remaining spacing-scale propagation on tabs not yet covered by the focused repair passes, while avoiding intrinsic sizes, editor working heights and minimum touch targets;
3. complete the remaining IME/window audit for inline editors and any persistent panels not already covered by IME-safe dialogs or scrollable side-panel patterns;
4. perform a final consistency pass over fixed/sticky bands to ensure the accumulated permanent vertical footprint is acceptable on phone as well as tablet;
5. owner visual audition of fonts/text-size/compactness combinations on phone and tablet;
6. reconcile stale current-stage prose in `README.md`, `MANIFEST.md`, `ROADMAP.md`, `TESTING.md`, `ARCHITECTURE.md` and the historical current-stage paragraph in `AGENTS.md`; `docs/PROJECT_STATE.md` + `docs/checkpoints/LATEST.md` remain the active current-state authority meanwhile;
7. do **not** resume formal M6 until the owner explicitly says the replacement build is ready.

## Verification

The Pass 05 helper gate executed successfully before the tested product commit: `git diff --check`, explicit fixed/sticky-shape checks, version/build identity checks, `:shared:desktopTest`, `:androidApp:assembleDebug`, `:desktopApp:build`, backend TypeScript check, identified APK copy/hash/size capture and artifact upload.

Automated green is technical evidence only. Phone/tablet visual acceptance has not yet been performed for this pass.

## Exact resume instruction

Read `AGENTS.md`, then `docs/checkpoints/LATEST.md`, then this checkpoint. Continue with column-maxima, residual spacing and final IME/window/fixed-footprint audits. Do not return to historical M6 unless the owner explicitly requests comparison.
