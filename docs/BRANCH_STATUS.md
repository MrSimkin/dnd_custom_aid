# Branch status and repository-ordering map

**Updated:** 2026-09-09  
**Controlling consolidation decision:** D-0066  
**Canonical branch:** `main`  
**Focused continuation branch:** `implementation/phase4a-successor-cycle`  
**Current continuation boundary:** planned successor Increments A–I integrated automated-green; acceptance/closure pending

This file exists because the repository accumulated many implementation, safety, retry and QA branch refs during Phase 4. Branch existence does **not** imply current authority.

## 1. Current authority

At the completed A–I successor engineering boundary:

- `main` remains the canonical baseline and is intentionally not being advanced merely because successor CI is green;
- `implementation/phase4a-successor-cycle` contains the current successor implementation through Increment I and is the authoritative branch for acceptance work against that implementation;
- `docs/PROJECT_STATE.md` is the broader current-state snapshot;
- `docs/checkpoints/LATEST.md` is the exact resume pointer;
- `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_I_TABLET_REDESIGN.md` is the latest planned engineering checkpoint and records the final A–I integrated gate.

Canonical does not mean release-ready or owner-accepted. A–I is technically integrated-green on the continuation branch, while owner/device acceptance, blocking repair resolution if needed, formal candidate/regression work and explicit Phase 4A closure remain open.

## 2. Frozen immutable QA evidence — KEEP

These two refs remain intentionally immutable historical evidence:

- `tmp/phase4-l-frozen-qa-candidate`;
- `tmp/phase4-m5-frozen-qa-candidate`.

Never delete, force-move, repurpose or treat them as current development branches merely for tidiness.

## 3. Historical milestone branches

Older discovery/foundation/architecture/implementation milestone branches may remain as historical labels. They are not current authority once their accepted content is represented by the documented canonical/current implementation line.

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

The many non-frozen `tmp/*` refs were implementation safety/retry/helper labels accumulated during Phase 4. D-0066 established that they are not valid resume points.

Their pre-cleanup branch names/final SHAs were archived before deletion in:

`docs/archive/2026-09-09_BRANCH_REF_ARCHIVE_BEFORE_CLEANUP.md`

Use that archive when an old deleted branch name/SHA needs historical reconstruction. The clearly invalid/superseded `implementation/phase4a-successor-cycle-temp-invalid` ref is likewise historical, not a resume point.

Deleting obsolete refs was branch-list housekeeping; it did not rewrite `main`, mutate frozen candidates or change accepted product data.

## 5. Current successor branch relation to main

At the completed-E night-close, `main` and the continuation branch were aligned by normal fast-forward. Successor work F–I then continued on:

`implementation/phase4a-successor-cycle`

The continuation branch now contains the automated-green A–I successor implementation while `main` remains the deliberately older canonical baseline. Do not silently fast-forward or merge this successor line to `main` merely because the automated gate is green.

A later consolidation into `main` must follow the project's explicit owner/review/acceptance decision. Owner/device acceptance remains separate from repository ordering.

## 6. Historical M6 and helper evidence

The 2026-09-08 M6 detour record remains historical/superseded evidence. Temporary one-off implementation helper workflows used during successor work self-removed after their successful product commits and are not part of the current product tree; `.github/workflows` returns to the normal scaffold workflow after each successful helper boundary.

The current integrated successor build is still a development/audition artifact, not a formal M6 candidate. A new M6 candidate should be frozen only after the owner-audited baseline is acceptable.

## 7. Interpretation rule

When branch history is confusing:

1. read `docs/checkpoints/LATEST.md`;
2. read `docs/PROJECT_STATE.md`;
3. use `main` as the canonical baseline;
4. use `implementation/phase4a-successor-cycle` for the documented current successor implementation/acceptance line;
5. consult the branch-ref archive for deleted historical refs;
6. consult frozen branches only for exact historical QA evidence.

Do not reconstruct current product truth from an arbitrary old branch merely because the ref still exists.

## 8. Next branch action

Do not create an Increment J or begin DM implementation.

Remain on `implementation/phase4a-successor-cycle` for successor owner/device acceptance and any narrowly scoped acceptance-blocking repairs. Only after the owner-audited baseline is ready should the project freeze a new formal M6 candidate, run the required regression/upgrade QA and make an explicit Phase 4A closure/consolidation decision.
