# Branch status and repository-ordering map

**Updated:** 2026-09-11  
**Controlling consolidation decision:** D-0066  
**Canonical branch:** `main`  
**Focused continuation branch:** `implementation/phase4a-successor-cycle`  
**Current continuation boundary:** A–I successor implementation + post-A–I stabilization automated-green; owner phone QA of `preqa.8 / 40800` completed and not accepted; point-by-point acceptance-repair design reconciliation is current; DM discovery documented only

This file exists because the repository accumulated many implementation, safety, retry and QA branch refs during Phase 4. Branch existence does **not** imply current authority.

## 1. Current authority

At the current continuation boundary:

- `main` remains the canonical repository baseline under D-0066 and is intentionally older than the live successor acceptance line;
- `implementation/phase4a-successor-cycle` contains the full A–I Player successor implementation, post-A–I stabilization, the current `preqa.8 / 40800` QA evidence and design-only future-DM discovery;
- the continuation branch is the authoritative branch for current Phase 4A Player acceptance work;
- `docs/PROJECT_STATE.md` is the broader current-state snapshot;
- `docs/checkpoints/LATEST.md` is the exact practical resume pointer;
- `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_OWNER_PHONE_QA_CONSOLIDATED.md` is the controlling owner/device QA record;
- `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md` is the point-by-point repair-decision consolidation log;
- `docs/checkpoints/2026-09-09_PHASE4A_PLAYER_PREQA8_STABILIZATION.md` preserves the exact technically verified Player product/full-gate boundary;
- `docs/decisions/D-0068_DM_COMBAT_DESK_PRODUCT_AND_UX.md` is future DM design truth only, not implementation authorization.

Canonical, documented or automated-green does not mean release-ready or owner-accepted.

## 2. Current technically verified and owner-audited Player product

The current Player QA artifact is:

- version `0.4.0-preqa.8` / build `40800` / debug;
- product source commit `c78b06776f5ae7a253b5b12b791c71fa2a7da096`;
- product tree `c612c07345ecdfc91d972118314ee649fe2048c4`;
- authoritative validation/checkpoint head `2a9b682f6aca2e95facecf1f6256039fd96cfefd`;
- normal full-gate workflow `34430548061` — SUCCESS;
- artifact `10134364621` / `dnd-custom-aid-debug-apk`;
- ZIP digest `b7ead12a7501bbef96f861321b5bebfd64c631647423b8eab9faec9580699a`;
- extracted APK digest `bb02b413919f55551eb7d4e78dfab2c37145b852c8827126df80082bd7a40815`.

The owner physically tested this build on Redmi Note 11 Pro 5G through phone portrait, phone landscape and representative larger-text coverage. The result is **NOT ACCEPTED — acceptance-repair pass required**.

Physical tablet QA remains deferred until shared/systemic defects are repaired.

Build `40700` and the earlier staged audition remain historical evidence only. They are not the current resume point.

## 3. Frozen immutable QA evidence — KEEP

These refs remain intentionally immutable historical evidence:

- `tmp/phase4-l-frozen-qa-candidate`;
- `tmp/phase4-m5-frozen-qa-candidate`.

Never delete, force-move, repurpose or treat them as current development branches merely for tidiness.

## 4. Historical milestone branches

Older discovery/foundation/architecture/implementation milestone branches remain historical labels and are not current authority. Examples include:

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

Non-frozen `tmp/*` implementation safety/retry/helper refs are not valid resume points. Their pre-cleanup names/final SHAs were archived in:

`docs/archive/2026-09-09_BRANCH_REF_ARCHIVE_BEFORE_CLEANUP.md`

The invalid/superseded `implementation/phase4a-successor-cycle-temp-invalid` ref is likewise historical.

## 6. Current successor branch relation to main

At the completed-E night close, `main` and the continuation branch were aligned by normal fast-forward. Successor work F–I, post-A–I stabilization, `40800` owner QA documentation and later repair-decision documentation continued on:

`implementation/phase4a-successor-cycle`

Do not silently fast-forward or merge this line to `main` merely because automated gates are green. A later consolidation into `main` must follow an explicit repository-ordering decision. Owner/device acceptance remains separate.

## 7. Current Player next action

Remain on `implementation/phase4a-successor-cycle`.

The immediate task is **not implementation** and is **not another QA pass**. Review the `40800` QA findings **one point at a time** with the owner.

For each point:

1. discuss the exact observed problem and intended behavior;
2. resolve only that point's remaining design/interaction details;
3. once the owner closes the point, consolidate the decision in `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md` and update controlling continuity files if the decision changes project-level truth;
4. then move to the next point.

After all required points are reconciled, define the bounded Phase 4A acceptance-repair implementation plan, implement it on this branch, validate it, produce the next monotonic QA build, retest affected phone boundaries, then perform Player tablet portrait/landscape QA.

## 8. DM boundary

DM design/discovery may continue as documentation when explicitly chosen, but no DM product code may begin before explicit Phase 4A owner acceptance/closure.

## 9. Interpretation rule

When branch history is confusing:

1. read `docs/checkpoints/LATEST.md`;
2. read `docs/PROJECT_STATE.md`;
3. use `implementation/phase4a-successor-cycle` for the live Phase 4A Player acceptance line;
4. use `main` as the canonical repository baseline, not as proof that it contains the newest acceptance work;
5. use the September 11 `40800` QA checkpoint for current owner findings;
6. use D-0068 for future DM design only;
7. consult the branch-ref archive/frozen refs only for historical evidence.

Do not reconstruct current product truth from an arbitrary older branch or historical QA document merely because the ref still exists.
