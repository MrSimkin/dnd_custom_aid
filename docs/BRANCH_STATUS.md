# Branch status and repository-ordering map

**Updated:** 2026-09-16 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Verified `main`:** `587a000dae7ff9b9f997dd138b0ebbaeca201256`  
**Post-merge Scaffold:** `35116690562` — **SUCCESS**  
**Current focused branch:** `wave4/membership-revoke-authorization`  
**Current PR:** not opened yet  
**Starting `main` for current package:** `587a000dae7ff9b9f997dd138b0ebbaeca201256`

This file is the canonical branch-lifecycle map. Branch existence alone never establishes authority.

## 1. `main` — sole normal integrated-MVP trunk

`main` remains the sole normal development trunk for integrated-MVP work.

Normal implementation should:

1. start from current verified remote `main`;
2. use a short-lived outcome-oriented branch;
3. integrate early when shared contracts are needed elsewhere;
4. merge only after proportionate validation;
5. verify the post-merge `main` run;
6. update durable docs/checkpoints when operational truth materially changes.

Do not create permanent `player-main`, `server-main`, `desktop-main` or months-long catch-all integration branches.

## 2. Completed convergence package

PR #39 established durable normalized PC synchronization baselines and no-silent-overwrite multi-client convergence rules.

PR #40 added permanent hosted-sync QA diagnostics, narrow safe legacy missing-baseline equal-state recovery, and an explicit owner-controlled keep-local resolution path for a reviewed `FINAL_PULL / LOCAL_AND_HOSTED_CHANGED` conflict while preserving hosted compare-and-swap protection.

The owner physically completed the relevant multi-client convergence gate on `0.4.0-preqa.15`:

- concurrent local revision `7` vs hosted revision `8` divergence correctly produced `LOCAL_AND_HOSTED_CHANGED` and preserved local state;
- explicit reviewed keep-local resolution succeeded and advanced hosted state `8 -> 9`;
- the emulator returned to a clean no-conflict/empty-outbox state;
- the clean phone subsequently displayed the emulator-selected hosted PC value and reported no conflict/queued/retryable/blocked work.

Result: **multi-client PC convergence = OWNER-PHYSICAL PASS**.

PR #40 merged to `main` as `587a000dae7ff9b9f997dd138b0ebbaeca201256`. Post-merge Scaffold `35116690562` completed **SUCCESS**. The convergence package is therefore integrated and closed.

Exact completion evidence:

`docs/checkpoints/2026-09-16_MULTI_CLIENT_PC_CONVERGENCE_PHYSICAL_QA_COMPLETE.md`

Do not rerun the entire physical scenario unless later behavior changes touch the relevant sync/conflict-resolution code.

## 3. Current focused branch

Current implementation branch:

`wave4/membership-revoke-authorization`

This branch starts exactly from verified `main` merge commit:

`587a000dae7ff9b9f997dd138b0ebbaeca201256`

Its bounded purpose is:

> membership revoke + Player/DM authorization

Do not mix unrelated DM Desktop, Media/Handouts, object-storage, SRD/AI, PDF-export or later-wave work into this package.

The package should first inventory existing membership lifecycle and authorization implementation/tests, then add only the missing enforcement, regressions, diagnostics and owner-facing physical QA support needed to prove the approved boundary end to end.

No owner decision is required for routine implementation. Return to the owner when a real product/security/cost/destructive-behavior ambiguity or manual/physical QA gate is reached.

## 4. Safety and authorization invariants

Preserve:

- `ACTIVE`, `KICKED`, `BANNED` membership lifecycle semantics;
- membership removal stopping future hosted access/sync without silently wiping local cached data;
- campaign role distinct from PC ownership/current control;
- DM campaign authority distinct from PC ownership;
- Player PC access limited by approved owner/controller authority;
- no-silent-overwrite, stale-revision, mutation-idempotency and tombstone/non-resurrection semantics;
- local-first saved data preservation on hosted authorization failure;
- explicit conflict handling rather than generalized auto-merge;
- project-specific authorization rather than speculative generic RBAC/ACL infrastructure.

Do not clear outbox state, reset databases, uninstall/reinstall, delete local PCs or discard local cached data merely to make future tests pass.

## 5. Current completion evidence

Immediate predecessor checkpoint:

`docs/checkpoints/2026-09-16_MULTI_CLIENT_PC_CONVERGENCE_PHYSICAL_QA_COMPLETE.md`

Historical implementation checkpoints remain evidence only:

- `docs/checkpoints/2026-09-15_HOSTED_PC_EXPLICIT_CONFLICT_RESOLUTION_READY_FOR_PHYSICAL_QA.md`;
- `docs/checkpoints/2026-09-15_HOSTED_SYNC_QA_LOG_AND_LEGACY_BASELINE_RECOVERY_READY_FOR_PHYSICAL_QA.md`;
- `docs/checkpoints/2026-09-15_MULTI_CLIENT_PC_CONVERGENCE_SAFETY_READY_FOR_PHYSICAL_QA.md`.

D-0075 remains controlling for the hard `$0` budget, intentional public repository, secret hygiene and owner-guidance contract.

## 6. Historical branches

`implementation/phase4a-successor-cycle` remains historical/frozen Player implementation/QA evidence. `integration/mvp-baseline-convergence` remains historical convergence evidence after promotion. Other frozen/historical refs remain evidence only unless a later checkpoint explicitly reactivates one.

Do not force-move, repurpose or treat historical refs as normal implementation branches.

## 7. Current resume rule

For normal implementation:

- verify current remote `main` and the current focused branch before writing;
- read `AGENTS.md`, `docs/PROJECT_STATE.md`, `docs/checkpoints/LATEST.md` and the predecessor completion checkpoint;
- do not restart Shared Spine, hosted provider activation, Android session acquisition, campaign bootstrap, PC delivery or completed convergence work;
- inspect existing membership lifecycle/backend authorization/shared sync/tests before adding code;
- preserve DM authority vs PC ownership and owner vs controller distinctions;
- preserve local cached state when hosted authorization is revoked;
- keep physical QA bounded and evidence-driven;
- continue on `wave4/membership-revoke-authorization` until this package reaches its next integration/manual gate.

## 8. Security / repository visibility

The repository is intentionally **public** under D-0075. `private: false` is expected and is not a discrepancy.

Never store provider/database credentials, bearer/session tokens, private keys or other confidentiality-dependent material in Git or QA logs.

Open security residuals are tracked in current checkpoints; they do not reopen completed provider/session/bootstrap/campaign/PC/convergence packages.
