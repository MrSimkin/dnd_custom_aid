# Decision numbering collision checkpoint — 2026-08-31

While auditing the current branch before the next-build migration/default gate, the decision directory was verified and found to contain multiple recent files using the same numeric prefixes, especially `D-0059` (and earlier duplicate prefixes such as D-0053/D-0057/D-0058).

## Disposition

- This is a documentation-index consistency issue only.
- It does not affect production code, database migration behavior, or owner-approved product semantics.
- Do not rename or rewrite the existing decision files during active design discussion, because their current filenames/commits are already used as durable recovery references.
- Include a safe decision-index/numbering normalization pass in the final next-build consolidation, preserving traceability from old filenames/commits to any corrected numbering.

## Recovery note

The newest dedicated decision created after detecting the collision is `D-0060_NOTES_DETAILED_MODEL.md`. Existing duplicate-number documents remain authoritative by filename/content until the normalization pass.