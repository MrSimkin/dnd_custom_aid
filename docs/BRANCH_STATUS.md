# Branch status and repository-ordering map

**Updated:** 2026-09-09  
**Controlling consolidation decision:** D-0066  
**Canonical branch:** `main`  
**Focused continuation branch:** `implementation/phase4a-successor-cycle`  
**Night-close boundary:** completed successor Increment E

This file exists because the repository accumulated many implementation, safety, retry and QA branch refs during Phase 4. Branch existence does **not** imply current authority.

## 1. Current authority

At the 2026-09-09 night-close boundary:

- `main` is the canonical current development baseline;
- `implementation/phase4a-successor-cycle` is the focused continuation branch for the remaining F–I work;
- the two are deliberately aligned at the completed-E night-close baseline;
- `docs/PROJECT_STATE.md` is the authoritative current-state snapshot;
- `docs/checkpoints/LATEST.md` is the exact resume pointer;
- `docs/checkpoints/2026-09-09_NIGHT_CLOSE_AFTER_INCREMENT_E.md` is the session continuity package.

Canonical does not mean release-ready or owner-accepted. Increment E is technically green, while owner/device acceptance and increments F–I remain open.

## 2. Frozen immutable QA evidence — KEEP

These two refs remain intentionally immutable historical evidence:

- `tmp/phase4-l-frozen-qa-candidate`;
- `tmp/phase4-m5-frozen-qa-candidate`.

Never delete, force-move, repurpose or treat them as current development branches merely for tidiness.

## 3. Historical milestone branches

Older discovery/foundation/architecture/implementation milestone branches may remain as historical labels. They are not current authority once their accepted content is represented by `main`.

Examples include:

- `discovery/initial-product-picture`;
- `foundation/continuity-structure`;
- `architecture/phase2-topology`;
- `architecture/approved-backend-and-android`;
- `implementation/initial-scaffold`;
- `implementation/local-campaign-selection`;
- `implementation/character-data-foundation`;
- `implementation/phase4-character-closure`;
- `implementation/phase4-preqa-consolidation`;
- `implementation/phase4-preqa-ux-repair`.

Do not start new work from them.

## 4. Obsolete temporary refs

The many non-frozen `tmp/*` refs were implementation safety/retry/helper labels accumulated during Phase 4. D-0066 already established that they are not valid resume points.

At this night-close boundary they are handled as follows:

1. write a durable archive index containing each branch name and its final SHA before deletion;
2. keep the two frozen refs above;
3. remove the remaining obsolete `tmp/*` refs from the visible branch list;
4. also remove the clearly invalid/superseded `implementation/phase4a-successor-cycle-temp-invalid` ref after recording its SHA;
5. leave meaningful historical milestone branches in place.

Deleting those obsolete refs is branch-list housekeeping. It does not rewrite `main`, mutate frozen candidates or change current product state.

Durable archive index:

`docs/archive/2026-09-09_BRANCH_REF_ARCHIVE_BEFORE_CLEANUP.md`

Use that file when an old deleted branch name/SHA needs historical reconstruction.

## 5. Current successor branch relation to main

Before the night-close fast-forward, `implementation/phase4a-successor-cycle` was a clean descendant of `main` with zero commits behind. The owner explicitly requested consolidation after finishing Increment E.

The night-close operation therefore uses a **normal fast-forward**, not a force push or history rewrite, and aligns both refs at the same completed-E development baseline.

Tomorrow's Increment F work continues on:

`implementation/phase4a-successor-cycle`

A later coherent boundary may again be consolidated into `main` when explicitly desired. Acceptance remains separate from repository ordering.

## 6. Historical M6 and helper evidence

The 2026-09-08 M6 detour record has already been copied into canonical history with a HISTORICAL / SUPERSEDED status. Temporary validator/workflow code that existed only to manipulate or verify old branch refs is intentionally not a product feature and does not need a live branch ref once its relevant evidence is archived.

## 7. Interpretation rule

When branch history is confusing:

1. read `docs/checkpoints/LATEST.md`;
2. read `docs/PROJECT_STATE.md`;
3. use `main` for canonical project truth;
4. use the focused successor branch only for the documented active continuation;
5. consult the branch-ref archive for deleted historical refs;
6. consult frozen branches only for exact historical QA evidence.

Do not reconstruct current product truth from an arbitrary old branch merely because the ref still exists.
