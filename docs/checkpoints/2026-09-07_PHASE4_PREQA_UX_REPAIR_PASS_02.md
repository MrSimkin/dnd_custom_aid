# Phase 4 pre-QA UX repair — Pass 02

**Date:** 2026-09-07  
**Status:** IMPLEMENTED + AUTOMATED GATE GREEN; owner visual/device review still pending  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Tested product commit:** `16210721115c3a04a6be7142d85f13ad79bdee7b`  
**Tested product tree:** `cb49f221e588bf5fdcd3222a3a287a1e176043a0`  
**Helper workflow run:** `34165128594`  
**Review version:** `0.4.0-preqa.2`  
**Review build:** `40200`  
**Review APK filename:** `DND_Custom_Aid_0.4.0-preqa.2_Build_40200_debug.apk`

## Why implementation resumed

The historical 2026-09-04 M6 pause checkpoint said owner QA was next. On 2026-09-07 the owner explicitly reviewed the candidate, found additional UX issues and reopened pre-QA implementation. Formal M6 QA is therefore deferred until these repair passes produce a new replacement review candidate.

## Pass 02 completed

- explicit Android version/build identity: version `0.4.0-preqa.2`, build `40200`;
- APK and GitHub artifact naming include both version and build;
- Application Settings exposes an explicit **Acerca de** dialog showing VERSION, BUILD and BUILD TYPE prominently;
- text-size audition expanded to 70–200%; 145%+ shows a phone-specific warning rather than blocking the choice;
- ten new font candidates added to the existing audition set: Inter, Figtree, Public Sans, Barlow Semi Condensed, Space Grotesk, Recursive, Cabin Condensed, Encode Sans Condensed, PT Sans Narrow and League Spartan;
- typography candidates keep their design/source provenance visible; selection is not restricted to one distributor/ecosystem;
- a separate **Compactación adicional de espacios** preference (100/90/80/70/60%) was added; it is explicitly distinct from Supercompact and begins by scaling app-owned padding/gaps in shared collection/dialog primitives plus the General shell, without shrinking icon/touch-target geometry;
- user-controlled column maxima expanded to phone portrait 1–4, phone landscape 1–5, tablet portrait 1–5 and tablet landscape 1–6; individual surfaces may still cap unsafe layouts;
- character-name input forces the first visible character uppercase while preserving the rest of the entered name; shared tests cover normal, accented and temporary blank input;
- the activity already used `adjustResize`; Settings and campaign-name dialogs explicitly add IME/navigation-bar insets, while the reusable character editor dialog retains IME-safe scroll behavior;
- `AGENTS.md` requires `docs/checkpoints/LATEST.md` to be refreshed after every implementation/review pass;
- `docs/PROJECT_STATE.md` points to the active repair branch/LATEST checkpoint instead of incorrectly resuming historical M6.

## Still open — next repair pass

1. remove embedded d20 controls from cramped sheet rows and create the dedicated **Tirada de dados** tab, with explicit roll context/name;
2. audit every tab and pin/sticky the highest-value reference/actions while the remainder scrolls;
3. continue propagating the extra spacing-scale preference through every tab-specific margin/padding/gap not yet covered by the shared primitives;
4. audit all remaining dialog/window surfaces for IME-safe resizing + internal scroll, not only the reusable editor/settings/campaign surfaces;
5. exercise the expanded column maxima surface-by-surface and tune safe caps where cards become unreadable;
6. owner visual audition of the enlarged font set, including phone/tablet and compactness combinations;
7. continue iterative pre-QA repair; do **not** resume formal M6 until owner says the replacement build is ready.

## Verification

The helper gate passed `git diff --check`, `:shared:desktopTest`, `:androidApp:assembleDebug`, `:desktopApp:build` and backend TypeScript check before the tested product commit was created.

## Exact resume instruction

Read `AGENTS.md`, then `docs/checkpoints/LATEST.md`, then this checkpoint. Continue with the dedicated dice-roll tab + per-tab sticky-important-elements audit. Do not return to the historical M6 APK unless the owner explicitly asks for historical comparison.
