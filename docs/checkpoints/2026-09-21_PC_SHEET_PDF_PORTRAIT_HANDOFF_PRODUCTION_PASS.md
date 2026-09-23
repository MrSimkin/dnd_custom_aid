# Checkpoint — Portrait Byte Handoff + Crop/Fit Production — PASS

**Date:** 2026-09-21 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Implementation head:** `93490d2247da5ea46ef50563fd17da78840e659e`  
**Final Scaffold:** `35683430947` / run #3177 — SUCCESS  
**Proof artifact:** `pc-sheet-populated-template-proofs` / artifact `10676160917`  
**Artifact digest:** `sha256:b5d631186c43beccb165b45f8613b7b7cb9f4ab026cfa0ce9eaffac34bfd97a1`

## Purpose

Close the D-0074 local portrait-byte handoff and Crop/Fit rendering gate without changing the canonical PC model, introducing a network dependency or reopening any frozen character-sheet family.

The shared export plan continues to carry only the stable portrait reference, local-availability fact and selected fit mode. The Desktop renderer now owns the final platform boundary that resolves a locally available reference to bytes and overlays those bytes into the selected family portrait frame.

## Product contract implemented

Production now preserves the D-0074 portrait rules:

- no portrait reference -> portrait area remains blank;
- portrait reference not locally available -> existing planner warning remains authoritative and export continues with a blank portrait area;
- locally available portrait -> the platform resolver supplies bytes at export time;
- unreadable/invalid resolved bytes fail safely to a blank portrait instead of blocking PDF generation;
- **Crop to fill** preserves aspect ratio while filling the usable frame and clips excess edges;
- **Fit entire image** preserves the whole image and leaves blank internal margins when aspect ratios differ;
- no network, Cloudflare, Neon, Descope or new media/object-storage dependency is introduced.

The canonical `CharacterClosureState.portraitRef` remains unchanged. Android may continue to persist its local content-URI reference; a future Save/Share/export surface supplies the platform resolver when it invokes the renderer.

## Family-specific placement

Portrait bytes are overlaid only after the approved family output has rendered, so the existing family renderers remain unchanged.

Audited targets:

- **Classic** — page 2, inside the existing `ASPECTO` frame;
- **Custom v1** — page 1, inside the upper-right owner-authored portrait frame and above the handwritten name banner;
- **Custom v2 per Attribute** — page 1, clipped inside the measured irregular portrait frame;
- **Custom v2 per Ability** — page 1, clipped inside its distinct taller portrait frame while preserving the lower handwritten-name band.

The two Custom-v2 first pages intentionally use different portrait depths. Production therefore carries separate clipping geometry rather than pretending the two source pages are identical.

## Automated preservation evidence

The integrated regression renders, for all four visual families:

1. baseline with the portrait reference unavailable;
2. locally available **Crop to fill**;
3. locally available **Fit entire image**.

It verifies:

- page count is unchanged;
- the intended portrait page changes;
- Crop and Fit produce distinct output;
- a representative non-portrait page remains pixel-identical to baseline;
- Custom-v2 decorative top-frame pixels remain unchanged;
- Custom-v2 per-Ability uses its taller lower portrait field rather than the shorter per-Attribute geometry.

The low-level `DesktopPdfRenderingPrimitives.drawImage` Crop/Fit behavior remains independently covered by the existing primitive QA.

## Visual QA history

Scaffold #3169 / `35682252187` was fully green, but artifact review identified that the first v2-per-Ability mapping reused the shorter per-Attribute portrait depth.

Scaffold #3173 / `35682881385` was fully green after fixing the taller field. Artifact review then identified a subtler issue: both v2 clipping paths sat on the decorative frame and allowed image ink to cover portions of the top/side border.

The final candidate inset both v2 clipping polygons to the measured inner border while preserving the distinct per-Ability depth.

Final Scaffold #3177 / `35683430947`:

- Kotlin — SUCCESS;
- backend — SUCCESS;
- hosted database — SUCCESS.

Final artifact review confirms:

- Classic Crop/Fit — clean inside the `ASPECTO` frame;
- Custom-v1 Crop/Fit — clean inside the source portrait box with the name banner untouched;
- Custom-v2 per-Attribute Crop/Fit — image remains inside the decorative inner frame;
- Custom-v2 per-Ability Crop/Fit — the taller field is used and decorative borders/name banner remain visible.

## Representative proof hashes

From artifact `10676160917`:

- Classic Crop:  
  `5bc665fdc89283bab525f4cf92d8472ad45ce4de8a4ccc23b7057bb1429fd0ed`
- Classic Fit:  
  `4b68118d0cf47aa87d53f36a2b78ac8c4c97db91c880852fa899062da8a1828d`
- Custom-v1 Crop:  
  `2abfae8b932fb78923e41b58ec5ea2d042700bc25bfb775aaed4aacfb4d5600d`
- Custom-v1 Fit:  
  `7c76a508cb9510d6a8babcc4974253c334e5bdf338357a1f9925951897d92e33`
- Custom-v2 per Attribute Crop:  
  `c49244f3dd216e49dbc62957599a34590f05290aa14144067f5a0b9b22b8db21`
- Custom-v2 per Attribute Fit:  
  `cd0933635502938722c39c4f4f18e45ed2f97a3e9e8415505279cb01f09ec296`
- Custom-v2 per Ability Crop:  
  `a7231a4a47f5c8b7ff4f5e665b16dfa3141dd97e3ce156797cd0b90218b59d95`
- Custom-v2 per Ability Fit:  
  `7ef23f2a197b7021c716e7810821b03e31affa94f9559b2123996e664a9ccf6e`

## PDF preflight

Representative final Crop proofs are openable, unencrypted, non-scan PDFs and have the expected Letter-page family output.

The Custom baseline PDFs and their portrait-on counterparts report the same pre-existing font-embedding warning in the generic preflight tool; portrait overlay introduces no new font or structural delta. This checkpoint does not reinterpret that older family-renderer behavior as a portrait defect.

## Integration boundary

There is not yet a real Desktop PDF Save/Share/export UI in `DesktopPcManager`. That is intentional: Save/Share remains a later product gate.

This checkpoint closes the renderer/platform seam, not the final invocation UI:

```text
canonical portraitRef
-> planner local-availability decision / warning
-> platform ref-to-bytes resolver
-> family-specific Crop/Fit overlay
-> static PDF
```

The later Desktop Save/Share workflow must supply the resolver from its actual local asset access path rather than duplicating portrait semantics.

## Remaining D-0074 gates

This checkpoint closes portrait rendering, not the complete PDF-export product.

Remaining known gates are:

1. owner-facing `APP_MODIFIED_SHEET` visual implementation;
2. originating-section continuation cues on frozen base sheets;
3. Save/Share integration, beginning with the Desktop DM workflow and then approved surface parity.

## Conclusion

**PORTRAIT BYTE HANDOFF + CROP/FIT PRODUCTION: PASS**

The local portrait asset boundary is now renderer-ready across Classic, Custom v1 and both Custom-v2 first-page variants, with offline-safe blank fallback, both approved fit modes and no portrait-induced change to unrelated pages.

Next gate:

> Prepare and validate the owner-facing `APP_MODIFIED_SHEET` and originating-section continuation-cue visual treatments without silently reopening or redefining the frozen family grammar.

PR #85 remains **OPEN / DRAFT / DO NOT MERGE**.
