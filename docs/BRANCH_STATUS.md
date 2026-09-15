# Branch status and repository-ordering map

**Updated:** 2026-09-14 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Validated convergence branch:** `integration/mvp-baseline-convergence`  
**Convergence commit:** `5bed85cbb3e86ae63eac79149fadc5e56e61b256`  
**Validation run:** `34917259324` / #1694 — SUCCESS  
**Normal integrated trunk after promotion:** `main`

This file is the canonical branch-lifecycle map. Branch existence alone never establishes authority.

## 1. Convergence is complete and validated

The previous split was:

- `main` — integrated product/design/architecture/governance;
- `implementation/phase4a-successor-cycle` — Player runtime/migrations/tests/QA evidence.

The owner explicitly authorized beginning the integrated-MVP implementation. A dedicated convergence branch was created from current `main` and the two lines were reconciled semantically rather than mechanically.

The convergence commit has both source lines as parents and preserves:

- successor Player runtime, SQLDelight migrations, tests, permanent guard scripts and implementation evidence;
- current `main` integrated product/architecture/governance;
- current template/PDF-export direction;
- historical Player checkpoints needed for traceability.

Actions run `34917259324` / #1694 completed SUCCESS across all Player guards, shared tests, Android build, Desktop build, backend type-check and APK artifact upload.

## 2. `main` — normal integrated-MVP trunk after promotion

Once PR #14 promotes the validated convergence branch, `main` is the sole normal development trunk for the integrated MVP.

New implementation should normally:

1. start from current `main`;
2. use a short-lived outcome-oriented branch;
3. integrate early when shared contracts are needed elsewhere;
4. merge back after proportionate validation;
5. update durable docs/checkpoints when operational truth materially changes.

Do not create permanent `player-main`, `server-main`, `desktop-main` or months-long catch-all integration branches.

## 3. `implementation/phase4a-successor-cycle` — historical/frozen Player evidence

Historical head at convergence:

`b9dea8ad6b17dcf3feeabba263eff1ee498f1536`

Its runtime/migrations/tests were absorbed into the validated integrated baseline. The branch remains useful as historical Player QA/implementation evidence but is no longer a normal development resume point after promotion.

Frozen candidate evidence remains:

- `0.4.0-preqa.13 / 41300`;
- candidate commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — SUCCESS;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted physical cross-device revalidation was pending at that historical boundary.

Do not force-move or repurpose this branch. New integrated CI success does not retroactively claim physical acceptance of the historical candidate.

## 4. `integration/mvp-baseline-convergence` — temporary promotion branch

This branch exists only to safely converge and validate the two former authority lines.

Current semantic merge commit:

`5bed85cbb3e86ae63eac79149fadc5e56e61b256`

After its continuity updates and successful promotion to `main`, it becomes historical integration evidence and should not be used as a permanent development branch.

PR #13 represented the direct successor-to-convergence relationship and is superseded/recognized by the semantic merge. PR #14 promotes the validated convergence branch to `main`.

## 5. Other historical refs

Historical/frozen refs remain evidence only unless a later checkpoint explicitly reactivates one. Examples include old Phase 4 implementation milestones, discovery/audit branches and frozen QA candidate refs.

Use `docs/archive/2026-09-09_BRANCH_REF_ARCHIVE_BEFORE_CLEANUP.md` for deliberately removed historical refs rather than recreating them.

Frozen QA refs must not be force-moved or repurposed.

## 6. Current resume rule

For normal implementation after promotion:

- branch from current `main`;
- read `docs/checkpoints/LATEST.md` and `docs/PROJECT_STATE.md`;
- follow `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md`;
- next package is the Shared Integrated-MVP Spine;
- return to the owner only for material product/scope/security/privacy/cost/lock-in/destructive-behavior decisions, external account/service actions or manual/physical QA gates.

Do not restart historical Player repair work merely because old checkpoints contain pending language.
