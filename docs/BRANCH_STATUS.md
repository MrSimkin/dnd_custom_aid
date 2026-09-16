# Branch status and repository-ordering map

**Updated:** 2026-09-16 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Current focused branch:** `wave4/qa-log-baseline-recovery`  
**Current PR:** #40  
**Starting `main` for current package:** `94d27dda71f87cbb6167886b2d55f2b3fd1120dc`  
**Current QA build:** `0.4.0-preqa.15` / `41500`  
**Behavior head physically tested:** `008a73c20101a127edd82947af71c6024f894609`

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

Provider-neutral hosted work, real hosted DEV activation, Android remembered-session acquisition, owner-facing campaign bootstrap, local-first campaign creation/delivery, PC snapshot push/pull, blocked-row recovery and unchanged-sync/no-op confirmation are complete.

PR #39 established durable normalized PC synchronization baselines and no-silent-overwrite multi-client convergence rules.

PR #40 added permanent hosted-sync QA diagnostics, narrow safe legacy missing-baseline equal-state recovery, and an explicit owner-controlled keep-local resolution path for a reviewed `FINAL_PULL / LOCAL_AND_HOSTED_CHANGED` conflict while preserving hosted compare-and-swap protection.

The owner has now physically completed the relevant multi-client convergence gate on `0.4.0-preqa.15`:

- concurrent local revision `7` vs hosted revision `8` divergence correctly produced `LOCAL_AND_HOSTED_CHANGED` and preserved local state;
- explicit reviewed keep-local resolution succeeded and advanced hosted state `8 -> 9`;
- the emulator returned to a clean no-conflict/empty-outbox state;
- the clean phone subsequently displayed the emulator-selected hosted PC value and reported no conflict/queued/retryable/blocked work.

Result: **multi-client PC convergence = OWNER-PHYSICAL PASS**.

The exact completion evidence is:

`docs/checkpoints/2026-09-16_MULTI_CLIENT_PC_CONVERGENCE_PHYSICAL_QA_COMPLETE.md`

Do not rerun the entire physical scenario unless later behavior changes touch the relevant sync/conflict-resolution code.

## 3. Current integration branch rule

PR #40 is now in package-closure state. Do not add the next authorization/revoke package to this branch.

Immediate sequence:

1. persist/verify the closure documentation on `wave4/qa-log-baseline-recovery`;
2. require the resulting head to remain green;
3. merge PR #40 into `main`;
4. verify the post-merge `main` run as required by normal workflow;
5. only then start a new short-lived branch for **membership revoke + Player/DM authorization**.

Do not clear the outbox, reset the database, uninstall/reinstall, delete local PCs or force a baseline merely to make future tests pass.

The active campaign selector is local Player state. Hosted synchronization is intended to process all eligible hosted campaigns.

## 4. Current completion evidence

Current Wave 4 completion checkpoint:

`docs/checkpoints/2026-09-16_MULTI_CLIENT_PC_CONVERGENCE_PHYSICAL_QA_COMPLETE.md`

Immediate historical implementation checkpoint:

`docs/checkpoints/2026-09-15_HOSTED_PC_EXPLICIT_CONFLICT_RESOLUTION_READY_FOR_PHYSICAL_QA.md`

Permanent QA-log/legacy-recovery checkpoint:

`docs/checkpoints/2026-09-15_HOSTED_SYNC_QA_LOG_AND_LEGACY_BASELINE_RECOVERY_READY_FOR_PHYSICAL_QA.md`

Underlying convergence checkpoint:

`docs/checkpoints/2026-09-15_MULTI_CLIENT_PC_CONVERGENCE_SAFETY_READY_FOR_PHYSICAL_QA.md`

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

Do not force-move or repurpose this branch. New integrated CI or hosted-provider/session/convergence success does not retroactively claim physical acceptance of the historical candidate.

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
- read the current Wave 4 completion checkpoint for the exact evidence and closure boundary;
- do **not** restart Shared Spine, hosted provider activation, Android session acquisition, campaign bootstrap, completed PC delivery or completed convergence work;
- preserve DM authority vs PC ownership distinction;
- preserve owner vs controller distinction;
- preserve no-silent-overwrite, stale-revision, idempotency and non-resurrection semantics;
- treat an empty outbox as insufficient evidence of PC cleanliness;
- keep explicit keep-local resolution owner-controlled and revision-bound;
- after PR #40 is merged and post-merge `main` is verified, begin **membership revoke + Player/DM authorization** as a separate focused package.

## 9. Security / repository visibility

The repository is intentionally **public** under D-0075. `private: false` is expected and is not a discrepancy.

Never store provider/database credentials, bearer/session tokens, private keys or other confidentiality-dependent material in Git or in the QA log.

The reusable QA report may contain campaign/PC IDs, names, revisions, baseline state, conflict enums and outbox retry state, but must continue to exclude authentication secrets.

Open security residuals are tracked in current checkpoints; they do not reopen completed provider/session/bootstrap/campaign/PC/convergence packages.
