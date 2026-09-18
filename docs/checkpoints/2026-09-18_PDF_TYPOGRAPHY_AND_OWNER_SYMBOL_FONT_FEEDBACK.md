# Owner feedback — PDF typography, sizing and symbol-font rules

**Date:** 2026-09-18 (Chile local time)  
**Status:** OWNER REVIEW — FOUNDATION DIRECTION ACCEPTED / CHANGES REQUIRED  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — DRAFT / DO NOT MERGE

The owner accepts the renderer direction so far with these required refinements:

1. Use a distinct **handwritten/script font** role for deliberate handwritten content such as a character name below a portrait.
2. Font size is **element-specific**, not global. Attribute scores/modifiers and major boxes may be much larger; ruled/line-separated sheet areas may use a fixed sheet-specific size.
3. Condensed typography is welcome, including strong condensation, but **readability wins**. Prefer sufficient weight and enforce a readability floor.
4. Generated normal text should be **sans-serif** and should avoid Helvetica/Arial-adjacent visual language. Existing source-template labels are not retroactively redesigned by this rule.
5. The owner explicitly authorizes committing and modifying his `Para-hoja-de-pj` font in the public development repo. Every iteration gets a new version directory plus its own mapping/use guide.
6. Preserve the application's existing training-marker grammar: one check for Competent and two parallel checks for Expertise/Pericia. The double-check is explicitly liked and must exist in both vector and owner-font candidate sets.

Next QA candidates: Fira Sans Regular/SemiBold, Barlow Condensed Bold, Kalam Bold. Candidate only; no final typography approval is implied.

No visual family is approved and PR #85 remains draft.
