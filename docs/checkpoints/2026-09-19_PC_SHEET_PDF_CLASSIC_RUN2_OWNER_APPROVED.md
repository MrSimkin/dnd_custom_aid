# Checkpoint — Classic D&D-style Run 2 — OWNER APPROVED / FROZEN

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Visual family:** Classic D&D-style  
**Approved renderer/proof commit:** `3dbcff8f5f9a2413f6deb8e400daeb6288b4f6b1`  
**Scaffold push run:** `35480871986` / run #2793 — SUCCESS  
**Approved proof artifact:** `pc-sheet-populated-template-proofs` / artifact `10595468434`

## Owner decision

The owner approved the complete corrected Classic Run-2 proof after reviewing the new PDF generated specifically to address the final Y-axis / ruled-line feedback.

**Status: OWNER APPROVED / FROZEN.**

The owner-approved result is the corrected nine-page proof produced from commit `3dbcff8f5f9a2413f6deb8e400daeb6288b4f6b1`, not the earlier Run-1 candidate and not the pre-correction Run-2 artifact.

## Approved page family

The approved proof contains nine Letter pages:

1. normal main sheet — summary, combat, official Attributes and governed Abilities/skills;
2. normal character/history/equipment page;
3. normal spell-list page;
4. Extended — Custom Statistics;
5. Extended — Traits & Features;
6. Extended — Resources & Options;
7. Extended — Inventory / Equipment;
8. Extended — Spells;
9. Extended — Notes.

The optional appended Spellbook is outside this visual-family approval because D-0074 defines it as a separate application-owned printable design.

## Approved visual/product grammar

The approved Classic family:

- is application-designed and does not trace/reproduce official artwork;
- deliberately inherits recognizable official D&D paper-sheet spatial/visual grammar;
- uses Spanish owner-facing labels;
- preserves substantial writable paper space;
- treats compact calculated/reference values differently from player-maintained freeform information;
- uses one coherent training-marker grammar;
- keeps Attribute -> saving throw -> governed Ability/skill relationships visible;
- gives custom Attributes and custom Abilities the same conceptual completeness as official equivalents;
- keeps family-matched Extended pages visually native to Classic rather than generic dashboard appendices;
- preserves blank capacity for handwriting instead of collapsing unused rows.

## Final owner correction — Y-axis / ruled writing areas

The owner accepted the Run-2 direction but required a new proof rather than a documentation-only correction.

Final correction requirements:

- audit vertical/Y alignment of lines, text and markers;
- do not omit writing rules merely because generated text already occupies the area;
- generated text must sit coherently within the ruled-paper rhythm rather than replacing it or colliding with it.

Commit `3dbcff8f5f9a2413f6deb8e400daeb6288b4f6b1` implements those corrections.

The final visual regression audit found that pages 1, 2, 7 and 9 changed as expected because they use the corrected ruled-line mechanics; pages 3, 4, 5, 6 and 8 remained pixel-identical to the previously accepted Run-2 render.

## Technical audit

Final CI on the approved commit:

- backend — SUCCESS;
- hosted database — SUCCESS;
- Kotlin/build/tests — SUCCESS;
- proof generation — SUCCESS;
- page count — 9 Letter pages;
- strict text-overflow gate — PASS;
- Spanish-only owner-facing regression scan — PASS;
- embedded text/symbol fonts — PASS;
- independent raster render inspection of all pages — PASS.

Green CI remains distinct from owner approval; this checkpoint records both.

## Freeze rule

Do not recalibrate or redesign the approved Classic visual family without a new owner-observed issue or a new product requirement.

Production renderer work may reuse/promote the approved mechanics, geometry and visual language. Data-driven pagination may generate more pages of the same approved extension types when required, but must preserve this family grammar and D-0074 readability/writability rules.

PR #85 remains **DRAFT / DO NOT MERGE** until the remaining PDF renderer/product integration gates are satisfied.
