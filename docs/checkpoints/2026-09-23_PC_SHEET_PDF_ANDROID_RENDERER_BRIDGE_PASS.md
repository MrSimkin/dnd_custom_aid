# Checkpoint - Android PC Sheet PDF renderer bridge PASS

**Date:** 2026-09-23  
**Base main:** `b44e8a44bc838bd17082a67050488bc510ca6b2f`  
**Branch:** `wave7/pc-sheet-pdf-android-renderer-bridge`  
**PR:** #88 - DRAFT while documentation closes  
**Implementation:** `53872e8dab57e6dca49a8292b6c1855fb784f133`  
**Status:** GENERATED-PORT SYNC + ANDROID COMPILE/APK PACKAGING + PUSH/PR CI PASS

## Frozen visual authority

This package does not reopen PC-sheet visual design.

Frozen authority remains:

- owner-approved renderer implementation: `f7e4417c05a2981415ef3648ead740e20469fe33`;
- owner-approved populated proof artifact: `10756937024`;
- final owner approval checkpoint: `docs/checkpoints/2026-09-23_PC_SHEET_PDF_FINAL_VISUAL_OWNER_APPROVED.md`;
- accepted Custom-v2 deviation remains exactly as recorded: Equipment / Equipo Especial continuation is approved as rendered even though it is not a literal copy of the original normal-page module.

No Desktop renderer/layout source was changed by this package.

## Purpose

D-0074 requires the same PC Sheet export capability on Android and Desktop while retaining local/offline generation.

The approved physical renderer was implemented first on Desktop with Apache PDFBox 3.x. Android previously had the shared export semantics/render plan but no physical PDF renderer or PDF library.

This package establishes the Android physical-renderer seam without creating a second independently maintained visual design.

## Generated-port strategy

Android renderer counterparts are mechanically derived from the frozen Desktop renderer source by:

`scripts/generate_android_pc_sheet_renderer.py`

The generator maps platform mechanics only:

- Desktop package/classes -> Android package/classes;
- Apache PDFBox -> PdfBox-Android package names;
- PDFBox 3 `Loader.loadPDF` -> PdfBox-Android `PDDocument.load`;
- Java AWT color/transform types -> PdfBox-Android compatibility types;
- Java `BufferedImage` ornament processing -> Android `Bitmap` with the same crop/mask/luma/alpha algorithm;
- JVM classloader resource access -> one Android asset loader.

The actual renderer geometry, coordinates, typography choices, pagination logic, masks, thresholds and content rules remain source-derived from Desktop authority.

CI guard:

`python3 scripts/generate_android_pc_sheet_renderer.py --check`

This fails whenever committed Android generated sources no longer equal a fresh generation from the Desktop authority.

## Android platform seam

Added:

- PdfBox-Android dependency;
- `PDFBoxResourceLoader.init(...)` in application startup;
- `AndroidPcSheetAssetLoader`;
- `AndroidPcSheetRendererBridge`;
- generated Android counterparts for the 11 production renderer/primitives files.

The bridge accepts the same `PcSheetPdfRenderPlan` and produces local PDF bytes.

It does not own layout.

## Shared physical assets

Android packages the existing root repository `assets/` directory directly.

There is no second copied set of owner templates/fonts.

Inspection of the built debug APK from PR run #3310 confirmed these exact packaged entries:

- `assets/character-sheets/templates/Hoja de PJ - 5.0 - Simkin.pdf`;
- `assets/character-sheets/templates/Hoja de PJ v2 - 5.0 - Simkin.pdf`;
- `assets/fonts/owner/para-hoja-de-pj/v8/Para Hoja de PJ Symbols v8.ttf`;
- `assets/fonts/pdf/text/BarlowCondensed-Bold.ttf`;
- `assets/fonts/pdf/text/FiraSans-Regular.ttf`.

DEX inspection also confirmed the Android PC-sheet renderer bridge and PdfBox-Android `PDDocument` references are packaged in the APK.

## Validation

Implementation:

`53872e8dab57e6dca49a8292b6c1855fb784f133`

Push:

- Scaffold #3309 / `35904995876` - **SUCCESS**.

PR:

- Scaffold #3310 / `35905025318` - **SUCCESS**.

Both runs passed:

- Android renderer sync guard;
- Android debug APK assembly;
- shared/Desktop Kotlin tests;
- backend;
- hosted database;
- PC sheet source-render upload;
- populated PC sheet proof upload.

APK artifact inspected:

- artifact ID `10771041791`;
- artifact name `dnd-custom-aid-debug-apk`.

## Deliberate remaining boundary

This package establishes and packages the Android renderer but does not yet invoke it from Android product UI.

Therefore:

- compile/package parity: PASS;
- source-authority sync: PASS;
- required templates/fonts packaged: PASS;
- Android runtime generation through a real Save/Share action: **NOT YET EXERCISED**.

That runtime invocation is the next package rather than being falsely claimed complete here.

## Next bounded package

Expose PC Sheet PDF export in the existing Android character experience for Player and authorized DM use without creating a second character model.

The next package must:

1. invoke the existing shared `PcSheetPdfExportPlanner`;
2. invoke `AndroidPcSheetRendererBridge`;
3. preserve D-0027: Save and Export remain distinct; if unsaved edits exist, warn and allow explicit export of the current edited state without saving;
4. expose D-0074 export-time choices;
5. Save through Android document creation;
6. Share through an Android content URI / chooser flow;
7. remain local/offline for generation;
8. preserve the frozen visual contract and generated-source sync guard;
9. use runtime export evidence to validate the Android renderer rather than reopening visual design.

No external provider action is required for this renderer-bridge package.
