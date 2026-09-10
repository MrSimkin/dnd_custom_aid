# Branch status and repository-ordering map

**Updated:** 2026-09-10  
**Controlling consolidation decision:** D-0066  
**Canonical branch:** `main`  
**Focused continuation branch:** `implementation/phase4a-successor-cycle`  
**Current continuation boundary:** planned successor A–I plus post-audition Player stabilization automated full-gate green; consolidated owner QA pending/pinned; DM discovery documented only

This file exists because the repository accumulated many implementation, safety, retry and QA branch refs during Phase 4. Branch existence does **not** imply current authority.

## 1. Current authority

At the current continuation boundary:

- `main` remains the canonical baseline and is intentionally not being advanced merely because successor CI is green;
- `implementation/phase4a-successor-cycle` contains A–I, the post-audition Player stabilization package, current preqa.8 acceptance documentation and design-only D-0068 DM Combat Desk discovery;
- the continuation branch is the authoritative branch for current Player acceptance work against that implementation;
- `docs/PROJECT_STATE.md` is the broader current-state snapshot;
- `docs/checkpoints/LATEST.md` is the exact Player implementation/QA resume pointer;
- `docs/checkpoints/2026-09-09_PHASE4A_PLAYER_PREQA8_STABILIZATION.md` is the current consolidated Player product/full-gate checkpoint;
- `docs/decisions/D-0068_DM_COMBAT_DESK_PRODUCT_AND_UX.md` is the future DM Combat Desk design baseline, **not an implementation boundary or authorization**.

Canonical, documented or automated-green does not mean release-ready or owner-accepted. Player owner/device acceptance, blocking repair resolution if needed, formal candidate/regression work and explicit Phase 4A closure remain open.

The consolidated Player QA is currently pinned because the owner cannot physically test at this time. This is a scheduling pause only; it does not change the branch authority or acceptance criteria.

## 2. Current technically verified Player product

The current acceptance artifact is:

- version `0.4.0-preqa.8` / build `40800` / debug;
- product source commit `c78b06776f5ae7a253b5b12b791c71fa2a7da096`;
- product tree `c612c07345ecdfc91d972118314ee649fe2048c4`;
- authoritative validation/checkpoint head `2a9b682f6aca2e95facecf1f6256039fd96cfefd`;
- normal full-gate workflow `34430548061` — SUCCESS;
- artifact `10134364621` / `dnd-custom-aid-debug-apk`;
- ZIP digest `b7ead12a7501bbef96fef861321b5bebfd64c631647423b8eab9faec9580699a`;
- extracted APK digest `bb02b413919f55551eb7d4e78dfab2c37145b852c8827126df80082bd7a40815`.

Later commits on the continuation branch are documentation-only unless a newer checkpoint explicitly identifies later product code. They do not supersede the validated preqa.8 product identity merely by moving the branch head.

## 3. Frozen immutable QA evidence — KEEP

These two refs remain intentionally immutable historical evidence:

- `tmp/phase4-l-frozen-qa-candidate`;
- `tmp/phase4-m5-frozen-qa-candidate`.

Never delete, force-move, repurpose or treat them as current development branches merely for tidiness.

## 4. Historical milestone branches

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

## 5. Obsolete temporary refs

The many non-frozen `tmp/*` refs were implementation safety/retry/helper labels accumulated during Phase 4. D-0066 established that they are not valid resume points.

Their pre-cleanup branch names/final SHAs were archived before deletion in:

`docs/archive/2026-09-09_BRANCH_REF_ARCHIVE_BEFORE_CLEANUP.md`

Use that archive when an old deleted branch name/SHA needs historical reconstruction. The clearly invalid/superseded `implementation/phase4a-successor-cycle-temp-invalid` ref is likewise historical, not a resume point.

Deleting obsolete refs was branch-list housekeeping; it did not rewrite `main`, mutate frozen candidates or change accepted product data.

## 6. Current successor branch relation to main

At the completed-E night-close, `main` and the continuation branch were aligned by normal fast-forward. Successor work F–I and later Player stabilization then continued on:

`implementation/phase4a-successor-cycle`

The continuation branch now contains the automated-green A–I successor implementation plus the automated-green Player stabilization package while `main` remains the deliberately older canonical baseline. It also contains later documentation-only DM discovery under D-0068.

Do not silently fast-forward or merge this successor line to `main` merely because the automated gate is green or because future DM design has been documented.

A later consolidation into `main` must follow the project's explicit owner/review/acceptance decision. Owner/device acceptance remains separate from repository ordering.

## 7. Historical M6 and helper evidence

The 2026-09-08 M6 detour record remains historical/superseded evidence. Temporary one-off implementation helper workflows used during successor work self-removed after their successful product commits and are not part of the current product tree; `.github/workflows` returns to the normal scaffold workflow after each successful helper boundary.

The current `preqa.8 / 40800` Player build is a consolidated owner-QA artifact, not a formal frozen M6 candidate. A new M6 candidate should be frozen only after the owner-audited baseline is acceptable.

## 8. DM design documentation while Phase 4A is open

D-0068 captures detailed owner-approved Phase 4B product/UX discovery, including the tablet-landscape-only DM Combat Desk, always-visible independent initiative, modular reference/state/notes/clocks/markers/rules surfaces, Party/Creature overviews, DM-private-by-default behavior, live encounter overrides and explicit no-VTT boundaries.

This documentation does **not** change branch sequencing:

- no DM product code has begun;
- no DM implementation branch should be started merely because the design document exists;
- further DM discussion/documentation may occur while Player QA is pinned;
- implementation remains blocked until explicit Phase 4A owner acceptance/closure.

## 9. Interpretation rule

When branch history is confusing:

1. read `docs/checkpoints/LATEST.md`;
2. read `docs/PROJECT_STATE.md`;
3. use `main` as the canonical baseline;
4. use `implementation/phase4a-successor-cycle` for the documented current successor implementation/acceptance line;
5. use D-0068 for future DM Combat Desk design only;
6. consult the branch-ref archive for deleted historical refs;
7. consult frozen branches only for exact historical QA evidence.

Do not reconstruct current product truth from an arbitrary old branch merely because the ref still exists.

## 10. Next branch action

Do not create an Increment J and do not begin DM implementation.

Remain on `implementation/phase4a-successor-cycle` for the pinned/resumed successor owner-device acceptance work and any narrowly scoped acceptance-blocking Player repairs actually observed.

When the owner can test again:

1. install `preqa.8 / 40800` over the existing QA installation/data;
2. run the consolidated Player QA;
3. repair only observed acceptance blockers;
4. freeze a replacement formal M6 candidate when the owner-audited baseline is acceptable;
5. run required formal regression/upgrade QA;
6. explicitly close Phase 4A.

Only **after** explicit Phase 4A closure should the project begin a DM implementation line, starting from D-0068's unresolved design questions and an owner-approved Phase 4B implementation plan.
