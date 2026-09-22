# Checkpoint — PC Sheet PDF visual recovery — owner review 2 rejected

**Date:** 2026-09-22 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Rejected review artifact:** `10720833172`  
**Rejected implementation baseline:** `6ea493c9ee53cf727464a985bc6ef0b558a73e99`  
**Documentation head that presented it:** `f53c70f1cb00056a62e607792c1a741114696886`  
**Status:** OWNER REJECTED / REPAIR ACTIVE

## Classification / authority correction

This owner review is **not a fourth rule layer** and does **not** create a new design contract.

It is a defect report against rules that were already established and should already have been preserved. The durable rule ledger is now:

`docs/PC_SHEET_PDF_VISUAL_CONTRACT.md`

The review observations below must therefore be classified as **PRE-EXISTING RULE VIOLATED / REGRESSION**, except that they identify the exact current symptom/location.

Already-existing authority includes:

- Classic D&D paper-sheet lineage, visible writing rules and shared text/rule Y rhythm;
- no skipped writing rows or gratuitous blank semantic bands;
- Custom v1 measured geometry/font/check-marker contracts;
- Custom v2 five-layer OCG construction `STRUCTURE -> CLEANUP -> LABELS -> VALUES -> MARKERS`;
- Custom v2 source Corbel/Fira role contract and source-measured geometry;
- user-facing `Raza` terminology / `Especie` absent.

Do not ask the owner to restate these rules again, and do not describe them as newly introduced in later checkpoints.

## Owner review 2 observations

### Classic

- Rasgos de clase: generated text is not aligned to the writing rules.
- Rasgos adicionales: generated text is not aligned to the writing rules.
- Inventario continuación: text in the table and in Valor / Ubicación / Notas is not aligned to its writing rules.
- Notas de campaña: generated text is not aligned to writing rules.
- Referencias y recordatorios: generated text is not aligned to writing rules.
- Historia y personalidad: strange/unnecessary vertical gaps between text lines/semantic blocks; this had already been called out.
- Inventario continuación: strange/unnecessary empty row gaps; this had already been called out.
- Idiomas: writing/reference lines must remain visibly available.
- Aliados y Tesoro: writing/reference lines must remain visibly available.
- Rasgos y características — continuación: writing/reference lines must remain visibly available under generated text.
- **Never use “Especie” in this product language. Use “Raza”.**
- Therefore use `RAZA`, `ATRIBUTOS DE RAZA`, `RASGOS DE RAZA / TRASFONDO / OTROS`, etc.
- The relevant Classic page(s) must recover the previously approved recognizable D&D paper-sheet lineage. The current recovered page is perceived as an invented/random redesign and is not acceptable.

### Custom v1

- Equipo especial: checked markers remain visibly off-center.

### Custom v2 — both variants

- Clase / Dotes and Raza / Trasfondo / Otros: unnecessary blank vertical spaces between lines/blocks remain; this had already been called out.
- This applies to both Custom-v2 variants.
- All titles and subtitles on Extended pages use the wrong visual font role compared with the approved Custom-v2 Run-7 family.
- Logo rendering contains artifacts.

## Root-cause findings already confirmed

### Classic ruled rhythm

`DesktopClassicRenderer.ruledTextArea()` currently draws physical rules every 20 pt but renders prose through one generic text box with `lineHeightMultiplier = 1.95`.

Therefore text baselines and physical writing rules are not mechanically coupled. This single implementation defect affects Rasgos de clase, Rasgos adicionales, Idiomas, Aliados y Tesoro, Valor / Ubicación / Notas, Notas de campaña and Referencias y recordatorios.

Repair rule: one measured generated text line per physical paper rule; no independent paragraph line-height model.

### Classic history/inventory gaps

History/personality is currently positioned as separate fixed semantic boxes rather than flowing consecutively over the existing ruled-paper capacity.

Inventory continuation expands long item detail into additional whole table rows, leaving the other columns blank on those continuation rows.

Repair rule: use consecutive physical rules for prose; keep one native inventory row per ordinary item and route long descriptions/notes to the native lower notes area/additional page capacity instead of synthetic half-empty rows.

### Custom-v2 typography/logo

Approved Extended Run 7 uses direct source-matched Corbel fixed-scale roles for headings. Current production introduced `centeredSource(...fallbackFont...)`, which can substitute Fira when the imported subset is considered unsupported.

Repair rule: restore the exact Run-7 fixed source-font mechanics for frozen headings/subheadings where Run 7 already proved the text, instead of silently substituting a different font.

The page header/logo defect must be repaired **inside the established layered/source-preserving construction**. Do not rasterize/flatten the header as a shortcut. Use the existing STRUCTURE/CLEANUP separation to remove the artifact while preserving the approved source appearance.

## Current gate

Artifact `10720833172`: **OWNER REJECTED / DO NOT FREEZE**.

PR #85 remains DRAFT / DO NOT MERGE.

Save/Share remains blocked.

Next output must be a smaller corrected proof set focused on these exact regressions, governed by `docs/PC_SHEET_PDF_VISUAL_CONTRACT.md`.
