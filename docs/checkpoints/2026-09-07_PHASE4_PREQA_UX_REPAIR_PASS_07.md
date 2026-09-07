# Phase 4 pre-QA UX repair — Pass 07

**Date:** 2026-09-07  
**Status:** IMPLEMENTED + AUTOMATED GATE GREEN; focused technical repair line stable; owner visual/device audition still pending  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Tested product commit:** `43ca1f5662123ce4d355d9d618b0bfba66d17697`  
**Tested product tree:** `3b2f2ab471097d3b108c9a787fc2342c5aad683a`  
**Helper workflow run:** `34171466714`  
**Review version:** `0.4.0-preqa.7`  
**Review build:** `40700`  
**Review APK filename:** `DND_Custom_Aid_0.4.0-preqa.7_Build_40700_debug.apk`  
**Review artifact name:** `DND-Custom-Aid-0.4.0-preqa.7-build-40700-debug`  
**APK SHA-256:** `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`  
**APK size:** `36161616` bytes

## Pass 07 completed

- ran a repository-wide Android UI audit for remaining numeric `Arrangement.spacedBy(N.dp)` gaps after the focused Pass 03–06 repairs;
- converted every remaining raw numeric `Arrangement.spacedBy` in the Android UI package to `Arrangement.spacedBy(appSpacingV4(...))`, making child-to-child whitespace consistently honor the owner spacing preference across General, class identity, Habilidades helpers, Gestión internals, spell helpers, conditional modules, PC settings, dice UI, app/list surfaces and other residual character UI;
- deliberately did **not** mass-convert remaining `Modifier.padding` or `PaddingValues`: several are part of badge geometry, clickable row/control footprint, drawing geometry or other intrinsic sizing and therefore are outside the safe global whitespace sweep;
- preserved representative geometry boundaries explicitly, including the character-editor Canvas inset and semantic-badge intrinsic padding;
- preserved all Pass 06 readability caps: Combate/Rasgos/Notas max 4, special Equipo max 3, ordinary Equipo wide max 5, currencies under their dedicated 3/6 grid rule;
- re-confirmed the IME audit conclusion: the reusable collection search field remains the only text-input owner without its own local inset marker, and its current call sites are protected by their owning IME-safe surfaces; no nested inset was added;
- produced `docs/PREQA_OWNER_VISUAL_AUDITION.md`, a staged owner-device audition guide covering typography, application text scale, whitespace compactness, column maxima, rotation, keyboard behavior and the accumulated fixed/sticky footprint;
- removed the temporary Pass 07 audit/helper/transform/checkpoint infrastructure from the tested product commit;
- Android review identity advanced to version `0.4.0-preqa.7` / build `40700`.

## Technical stopping rule reached

The automated repair line has now completed the planned fixed/sticky, spacing-scale, IME/window and card-column audits. Further speculative visual changes are **not recommended before owner device audition**: the remaining questions are perceptual/ergonomic and need phone/tablet observation rather than more static code inference.

This is not formal M6 acceptance. Automated green remains technical evidence only.

## Still open

1. reconcile stale current-stage prose in `README.md`, `MANIFEST.md`, `ROADMAP.md`, `TESTING.md`, `ARCHITECTURE.md` and the historical current-stage paragraph in `AGENTS.md` now that the focused implementation line is stable;
2. owner executes the staged visual audition in `docs/PREQA_OWNER_VISUAL_AUDITION.md` on phone and tablet and reports findings;
3. repair any concrete owner-observed blocking/regression findings, if any, with a new identified build rather than speculative polishing;
4. when the owner explicitly says the replacement build is ready, freeze that exact candidate and resume formal M6 QA from the refreshed governance state;
5. do **not** begin DM implementation before Phase 4 closure/merge approval.

## Verification

The Pass 07 helper gate executed successfully before the tested product commit: `git diff --check`, global raw-Arrangement elimination checks, preserved geometry/readability-cap checks, version/build identity checks, `:shared:desktopTest`, `:androidApp:assembleDebug`, `:desktopApp:build`, backend TypeScript check, identified APK copy/hash/size capture and artifact upload.

The preceding Pass 07 audit workflow inventoried residual raw Arrangement gaps, raw padding forms, current `appSpacingV4` adoption and IME ownership before implementation.

## Exact resume instruction

Read `AGENTS.md`, then `docs/checkpoints/LATEST.md`, then this checkpoint. Reconcile current-stage governance prose, then use `docs/PREQA_OWNER_VISUAL_AUDITION.md` for owner device audition. Do not reopen speculative UX implementation or historical M6 unless concrete owner findings or an explicit owner QA-ready instruction require it.
