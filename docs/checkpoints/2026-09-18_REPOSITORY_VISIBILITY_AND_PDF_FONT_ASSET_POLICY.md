# Decision — Repository Visibility and PDF Font Asset Policy

**Date:** 2026-09-18 (Chile local time)  
**Status:** OWNER-CONFIRMED PROJECT CONSTRAINT  
**Applies to:** repository handling, PDF renderer fonts, icon/symbol fonts, final packaging

## Repository visibility lifecycle

The application is a **fully private/personal-use project**.

The GitHub repository is intentionally **public during active development** because the owner's current GitHub usage model gives materially more useful GitHub Actions capacity for the project while public.

When the project is complete, the owner intends to make the repository **private**.

This visibility plan is an operational/development constraint; it does **not** change the fact that the final application is for private/personal use.

## Font implications

For the finished private application, the renderer is allowed to consider locally available/proprietary fonts when they are appropriate for visual fidelity. The renderer does not need to restrict its design choices only to OFL/open-source fonts merely because the final app is private/personal.

However, while the repository remains public:

- do not commit third-party proprietary font binaries unless their redistribution license explicitly permits public repository distribution;
- local/private font use is distinct from committing the binary to Git;
- do not silently replace a requested font with an unrelated font while preserving the original family name;
- prefer a font-loading architecture that can use private/local font assets without requiring those binaries to exist in the public repository.

Making the repository private later does **not** retroactively erase a binary that was previously published in Git history, so proprietary assets should not be committed publicly as a temporary shortcut.

## Owner-created symbol font

The owner has confirmed that **he created the `Para-hoja-de-pj` font** used by the Custom v1 sheet.

The owner plans to provide that font for inspection.

Treat it as a first-class candidate for the renderer's symbol/marker system. Investigate:

- existing glyph inventory and mappings;
- metrics and alignment behavior;
- how the authoritative v1 PDF uses it;
- whether the existing font can be reused as-is;
- whether extending it with additional app symbols would preserve compatibility;
- whether a separate v2 of the symbol font is preferable;
- comparison against direct PDF vector primitives during primitive QA.

Do **not** commit the provided font to the public repository merely because it is available in chat/workspace. Analyze it locally first. Repository inclusion can be decided separately by the owner after inspection.

## Renderer architecture consequence

Font/resource handling should distinguish:

1. **publicly distributable bundled fonts/assets** — safe to live in the current public repo;
2. **owner-authored assets** — may be includable, but repository publication remains an explicit owner decision;
3. **private/proprietary local fonts** — usable by the private application where appropriate, but not automatically committed to the public repo.

The PDF renderer should therefore not assume that every usable runtime font must be stored in Git.

## Continuity note

This decision supplements:

- `2026-09-17_PC_SHEET_PDF_WHOLE_EXPORT_RENDERER_QA_STRATEGY.md`;
- `2026-09-17_PC_SHEET_PDF_RENDERER_FOUNDATION_RESEARCH.md`;
- `2026-09-17_WAVE7_PC_SHEET_PDF_MAIN_PAGE_VISUAL_QA_ROUND1_REJECTED.md`.

It does not alter the current visual QA rejection or authorize merging PR #85.


## Superseding owner authorization — 2026-09-18

The owner now explicitly authorizes the project to commit the original owner-authored `Para-hoja-de-pj` binary to the public development repository and to publish modified/versioned derivatives there.

This supersedes only the earlier precaution against committing this **owner-authored** font. Restrictions on third-party proprietary fonts remain unchanged.

Every owner-font iteration must live in its own version subfolder, preserve earlier versions, include a human-readable mapping/use guide, and record provenance/version information.
