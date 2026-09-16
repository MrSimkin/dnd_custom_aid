# Branch status and repository-ordering map

**Updated:** 2026-09-15 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Current focused branch:** `wave4/qa-log-baseline-recovery`  
**Current PR:** #40  
**Starting `main` for current package:** `94d27dda71f87cbb6167886b2d55f2b3fd1120dc`  
**Current QA build:** `0.4.0-preqa.14` / `41400`

This file is the canonical branch-lifecycle map. Branch existence alone never establishes authority.

## 1. `main` — sole normal integrated-MVP trunk

`main` remains the sole normal development trunk for integrated-MVP work.

Normal implementation should:

1. start from current remote `main`;
2. use a short-lived outcome-oriented branch;
3. integrate early when shared contracts are needed elsewhere;
4. merge only after proportionate validation;
5. verify the post-merge `main` run;
6. update durable docs/checkpoints when operational truth materially changes.

Do not create permanent `player-main`, `server-main`, `desktop-main` or months-long catch-all integration branches.

## 2. Current implementation position

Wave 2 — Shared Integrated-MVP Spine — is complete and integrated.

Provider-neutral hosted work, the first real hosted DEV environment, Android remembered-session acquisition, owner-facing campaign bootstrap, local-first campaign creation/delivery, PC snapshot push/pull, blocked-row recovery and unchanged-sync/no-op confirmation are complete.

PR #39 added the durable normalized PC synchronization baseline and the multi-client no-silent-overwrite convergence rules.

The first owner physical convergence attempt after PR #39 was **inconclusive because the app did not expose enough evidence**, not because an implementation defect was proven. The owner observed a generic local conflict while the hosted outbox was empty; this is compatible with a pull-side conflict detected before any PC snapshot is queued.

The current focused package is PR #40 on:

`wave4/qa-log-baseline-recovery`

It adds:

- a permanent debug QA console with run/copy/share log support;
- exact campaign/PC convergence diagnostics across initial and final pull phases;
- explicit campaign-active-vs-hosted-sync wording;
- a bounded legacy missing-baseline recovery when complete normalized local state already equals complete newer hosted state;
- continued `SYNC_BASELINE_MISSING` protection whenever local and hosted differ;
- Android QA build `0.4.0-preqa.14` / `41400`.

Implementation head `cf11c88cf7a424793c40f3d7bcb57b859e058b1e` passed Scaffold `35043186839`, including Shared desktop tests, Android assemble, Desktop build, guard scripts, backend type-check and hosted DB contracts. A later launcher-label-only commit renames the debug launcher to `DnD Aid - QA DEV`; use the final branch run for the physical APK artifact.

## 3. Current integration branch rule

Do not start another parallel implementation branch while PR #40 is the active physical-QA package unless a blocking repair requires it.

The immediate sequence is:

1. keep PR #40 open while its final head is automatically verified;
2. obtain the final debug APK artifact;
3. install it over the existing phone state without clearing app data;
4. run the `DnD Aid - QA DEV` hosted synchronization diagnostic;
5. copy the complete QA log back to the technical-assistant chat;
6. classify the exact conflict/revision/baseline state from that evidence;
7. finish the remaining multi-client convergence scenarios only after the diagnostic state is understood;
8. merge/close the package according to the physical result and project workflow.

Do not clear the outbox, reset the database, reinstall from scratch, recreate campaigns/PCs or force a baseline merely to make the test pass.

The active campaign selector is local Player state. Hosted synchronization is intended to process all eligible hosted campaigns.

## 4. Current completion evidence

Current Wave 4 checkpoint:

`docs/checkpoints/2026-09-15_HOSTED_SYNC_QA_LOG_AND_LEGACY_BASELINE_RECOVERY_READY_FOR_PHYSICAL_QA.md`

Underlying convergence checkpoint:

`docs/checkpoints/2026-09-15_MULTI_CLIENT_PC_CONVERGENCE_SAFETY_READY_FOR_PHYSICAL_QA.md`

Completed campaign/PC delivery evidence:

`docs/checkpoints/2026-09-15_ANDROID_HOSTED_CAMPAIGN_PC_SYNC_COMPLETE.md`

Hosted provider/environment checkpoint:

`docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md`

D-0075 remains controlling for the hard `$0` budget, intentional public repository, secret hygiene and owner-guidance contract.

## 5. `implementation/phase4a-successor-cycle` — historical/frozen Player evidence

Historical head at convergence:

`b9dea8ad6b17dcf3feeabba263eff1ee498f1536`

Its runtime/migrations/tests were absorbed into the integrated baseline. The branch remains useful as historical Player QA/implementation evidence but is no longer a normal development resume point.

Frozen candidate evidence remains:

- historical `0.4.0-preqa.13 / 41300`;
- candidate commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — SUCCESS;
- targeted physical cross-device revalidation was pending at that historical boundary.

Do not force-move or repurpose this branch. New integrated CI or hosted-provider/session success does not retroactively claim physical acceptance of the historical candidate.

## 6. `integration/mvp-baseline-convergence` — historical integration evidence

The convergence branch completed its purpose and was promoted through PR #14.

Semantic convergence commit:

`5bed85cbb3e86ae63eac79149fadc5e56e61b256`

It remains evidence of the deliberate reconciliation of the former Player successor and integrated product/architecture line. It is not a normal resume or implementation branch.

## 7. Other historical refs

Historical/frozen refs remain evidence only unless a later checkpoint explicitly reactivates one. Use `docs/archive/2026-09-09_BRANCH_REF_ARCHIVE_BEFORE_CLEANUP.md` for deliberately removed historical refs rather than recreating them.

Frozen QA refs must not be force-moved or repurposed.

## 8. Current resume rule

For normal implementation:

- verify current remote `main`, current active PR and final CI before writing;
- read `AGENTS.md`, `README.md`, `MANIFEST.md`, `docs/PROJECT_STATE.md` and `docs/checkpoints/LATEST.md`;
- read the current Wave 4 checkpoint above for the exact physical gate;
- do **not** restart Shared Spine, hosted provider activation, Android session acquisition, campaign bootstrap or already completed PC delivery work;
- preserve DM authority vs PC ownership distinction;
- preserve owner vs controller distinction;
- preserve no-silent-overwrite, stale-revision, idempotency and non-resurrection semantics;
- treat empty outbox as insufficient evidence of PC cleanliness;
- return to the owner for the current manual/physical QA gate or genuinely material product/scope/security/privacy/cost/lock-in/destructive-behavior decisions.

If no later checkpoint supersedes this one, continue with the **PR #40 diagnostic physical sync**, not membership revoke/authorization work yet.

## 9. Security / repository visibility

The repository is intentionally **public** under D-0075. `private: false` is expected and is not a discrepancy.

Never store provider/database credentials, bearer/session tokens, private keys or other confidentiality-dependent material in Git or in the QA log.

The reusable QA report may contain campaign/PC IDs, names, revisions, baseline state, conflict enums and outbox retry state, but must continue to exclude authentication secrets.

Open security residuals are tracked in the current checkpoints; they do not reopen completed provider/session/bootstrap/campaign/PC packages.
