# PDF Font Assets

Generated PDF text follows these owner rules:

- normal generated text uses sans-serif;
- avoid Helvetica/Arial-adjacent visual language;
- a separate handwritten/script role is allowed for intentionally handwritten content such as a name below a portrait;
- condensed fonts are allowed, even strongly condensed, but readability has priority over density;
- condensed treatments should normally use sufficient weight;
- font size is element-specific rather than globally fixed;
- Attribute scores/modifiers and other major values may be materially larger than ordinary boxes;
- ruled/fixed-line regions may intentionally use a sheet-specific fixed size;
- content must not be silently shrunk below its readability floor merely to make it fit.

Current text-font QA candidates are Fira Sans, Barlow Condensed Bold and Kalam Bold. They are not final family approvals.

## Owner-authored symbol font

See `owner/para-hoja-de-pj/` for the separately versioned owner-authored symbol font.

- exact v1 archival original is now committed after SHA-256 verification;
- v2/v3 remain preserved historical renderer candidates;
- v4 is the current modern redesign candidate with legacy-refined and modern-clean families, deterministic source/build and a complete mapping guide;
- v4 technical validation does not imply owner visual approval.

The renderer may use PUA aliases internally, but symbol characters must not become canonical game/domain state.
