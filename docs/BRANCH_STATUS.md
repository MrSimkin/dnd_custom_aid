# Branch status and repository-ordering map

**Updated:** 2026-09-15 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Verified hosted/sync checkpoint:** `734477b4e276810de1581dbc2d0a8458ad953f85`  
**Validation run:** `34979121449` — SUCCESS

This file is the canonical branch-lifecycle map. Branch existence alone never establishes authority.

## 1. `main` — sole normal integrated-MVP trunk

The former split authority has already been reconciled and promoted. `main` is the sole normal development trunk for integrated-MVP work.

Normal implementation should:

1. start from current `main`;
2. use a short-lived outcome-oriented branch;
3. integrate early when shared contracts are needed elsewhere;
4. merge after proportionate validation;
5. update durable docs/checkpoints when operational truth materially changes.

Do not create permanent `player-main`, `server-main`, `desktop-main` or months-long catch-all integration branches.

Current verified hosted/sync implementation checkpoint is `734477b4e276810de1581dbc2d0a8458ad953f85`, validated by Actions run `34979121449` across Player guards, shared/Kotlin tests, Android, Desktop, backend and hosted-database checks.

## 2. Current implementation position

Wave 2 — Shared Integrated-MVP Spine — is complete and integrated.

Wave 3 — Hosted foundation — is in progress. PRs #15–#21 have already integrated the shared spine, hosted API/database/transport foundation, durable outbox, campaign-create hosted delivery and authenticated account/campaign bootstrap.

The next normal implementation package is **explicit hosted campaign/membership lifecycle and scoped change semantics**, followed in dependency order by hosted PC current-state/snapshot sync and the Player↔Server end-to-end path.

## 3. `implementation/phase4a-successor-cycle` — historical/frozen Player evidence

Historical head at convergence:

`b9dea8ad6b17dcf3feeabba263eff1ee498f1536`

Its runtime/migrations/tests were absorbed into the integrated baseline. The branch remains useful as historical Player QA/implementation evidence but is no longer a normal development resume point.

Frozen candidate evidence remains:

- `0.4.0-preqa.13 / 41300`;
- candidate commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — SUCCESS;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted physical cross-device revalidation was pending at that historical boundary.

Do not force-move or repurpose this branch. New integrated CI success does not retroactively claim physical acceptance of the historical candidate.

## 4. `integration/mvp-baseline-convergence` — historical integration evidence

The convergence branch completed its purpose and was promoted through PR #14.

Semantic convergence commit:

`5bed85cbb3e86ae63eac79149fadc5e56e61b256`

It remains evidence of the deliberate reconciliation of the former Player successor and integrated product/architecture line. It is not a normal resume or implementation branch.

## 5. Other historical refs

Historical/frozen refs remain evidence only unless a later checkpoint explicitly reactivates one. Examples include old Phase 4 implementation milestones, discovery/audit branches and frozen QA candidate refs.

Use `docs/archive/2026-09-09_BRANCH_REF_ARCHIVE_BEFORE_CLEANUP.md` for deliberately removed historical refs rather than recreating them.

Frozen QA refs must not be force-moved or repurposed.

## 6. Current resume rule

For normal implementation:

- branch from current `main`;
- read `docs/checkpoints/LATEST.md` and `docs/PROJECT_STATE.md`;
- use `docs/checkpoints/2026-09-15_HOSTED_SYNC_FOUNDATION.md` for the current hosted/sync handoff;
- continue Wave 3 with explicit hosted campaign/membership lifecycle/change semantics;
- do **not** restart Shared Spine or convergence work;
- do **not** infer lifecycle/removal state from absence in a bootstrap/list response;
- return to the owner only for material product/scope/security/privacy/cost/lock-in/destructive-behavior decisions, external account/service activation, or manual/physical QA gates.

## 7. External-provider branch rule

Do not create provider-setup branches or resources speculatively.

The first external activation should happen only when a real authenticated hosted development environment is required. At that point use a short-lived provider/integration package from current `main`, activate development Cloudflare + Neon + Descope resources under owner control, keep secrets outside Git, validate the real end-to-end path, and merge the resulting code/config documentation normally.

R2 remains later and should be activated when object-storage work for Media/Handouts/assets actually begins.
