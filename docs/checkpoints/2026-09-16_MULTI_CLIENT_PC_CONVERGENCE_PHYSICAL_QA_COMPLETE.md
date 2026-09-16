# Multi-client PC convergence — physical QA complete

**Date:** 2026-09-16 (Chile local time)  
**Branch:** `wave4/qa-log-baseline-recovery`  
**PR:** #40  
**Behavior head tested:** `008a73c20101a127edd82947af71c6024f894609`  
**QA build:** `0.4.0-preqa.15` / `41500`  
**Behavior-head Scaffold:** `35044956294` — **SUCCESS**  
**QA APK SHA-256:** `4615d1a9c8f9e87c2ebbc5ecb80e4a22f747384baa3091c1f05b6d90e4cac4f5`

## Result

The Wave 4 multi-client PC convergence physical gate is **OWNER-PHYSICAL PASS**.

This checkpoint records the completed owner/device evidence. Do not rerun the full scenario merely to rediscover the same result unless later code changes touch the relevant convergence/conflict-resolution behavior.

## 1. Concurrent divergence protection — PASS

Two clients were preserved with intentionally different state:

- Android phone: original PC value;
- Android emulator: locally modified PC value that had never been uploaded.

The hosted PC independently advanced to revision `8` while the emulator local metadata/baseline remained at revision `7`.

The emulator QA diagnostic correctly reported:

- conflict reason `LOCAL_AND_HOSTED_CHANGED`;
- local revision `7`;
- baseline present `YES` at revision `7`;
- hosted revision `8`;
- local differs from baseline `YES`;
- local equals hosted `NO`;
- local hosted outbox empty.

The empty outbox was expected because the initial hosted pull detected the conflict before the local snapshot queueing phase.

Result: the local edit was preserved and the newer hosted state was not silently overwritten.

## 2. Explicit owner-controlled keep-local resolution — PASS

On emulator build `0.4.0-preqa.15`, the owner deliberately chose:

`Resolver conflicto: conservar PC local`

The resulting QA report recorded:

`SUCCESS | PC=HBT PJ Test 2 B OFFLINE | reviewedHostedRevision=8 | resultingRevision=9`

This proves the reviewed local state was queued against the exact reviewed hosted revision and accepted by the normal server compare-and-swap path.

Hosted revision therefore advanced:

`8 -> 9`

The refreshed emulator QA sync then reported a clean state:

- Hosted campaigns: `1`;
- Eligible campaigns: `1`;
- Campaign conflicts: `0`;
- Hosted PCs: `1`;
- Unchanged PCs: `1`;
- PC conflicts: `0`;
- queued snapshots: `0`;
- retryable mutations: `0`;
- blocked mutations: `0`;
- local hosted outbox: empty.

Result: the explicit action worked without weakening stale-revision/CAS protection or silently discarding local state.

## 3. Clean second-client convergence — PASS

After the emulator resolution produced hosted revision `9`, the phone ultimately displayed the **emulator-modified PC value**.

The phone QA log on `preqa.15` reported:

- Hosted campaigns: `1`;
- Eligible campaigns: `1`;
- Applied/reconciled campaigns: `1`;
- Campaign conflicts: `0`;
- Hosted PCs: `1`;
- Applied PCs: `0`;
- Unchanged PCs: `1`;
- PC conflicts: `0`;
- queued snapshots: `0`;
- acknowledged mutations: `0`;
- retryable mutations: `0`;
- blocked mutations: `0`;
- outbox empty.

`Applied PCs: 0 / Unchanged PCs: 1` is consistent with the normal app having already synchronized revision `9` before the explicit QA diagnostic was run. The decisive physical observation is that the phone, which previously held the old/original value, displayed the emulator-selected value afterward.

Result: the clean second client converged to the hosted state produced by the explicit resolution.

## 4. What this gate proves

The physically exercised path demonstrated all of the following together:

1. concurrent local + hosted divergence produces an explicit conflict rather than a silent winner;
2. an empty outbox does not erase evidence of a pull-side local conflict;
3. the owner can explicitly choose the reviewed local version;
4. the chosen snapshot is still guarded by the exact reviewed hosted revision and normal compare-and-swap behavior;
5. successful acknowledgement advances hosted/local sync state and baseline;
6. a second clean client converges to the resulting hosted state;
7. the observed final states contain no PC conflict, blocked/retryable/queued mutation or leftover outbox work.

Therefore the multi-client PC convergence physical QA gate is complete.

## 5. Safety boundary preserved

This PASS does **not** authorize or imply:

- automatic local-wins behavior;
- automatic server-wins behavior;
- newest-timestamp-wins behavior;
- broad missing-baseline reseeding;
- ignoring baseline revision mismatch;
- clearing the hosted outbox as recovery;
- deleting local PCs;
- resetting local databases/app data;
- automatic ambiguous conflict resolution.

The narrow legacy equal-state baseline recovery and explicit reviewed keep-local path remain the intended boundaries.

## 6. Repository/CI continuity

No behavior code changed after `008a73c20101a127edd82947af71c6024f894609` before this physical PASS was recorded. The two later pre-closure commits (`4893af9a8cb45f2e47513e8b181893fe5c2456aa` and `fa8ccafba0f81a8c5bc9036f7e540302291cf845`) were documentation-only updates.

The pre-closure branch head `fa8ccafba0f81a8c5bc9036f7e540302291cf845` also passed Scaffold run `35045274751`.

## 7. Package closure and next boundary

PR #40 now has the required physical evidence for its convergence/QA/recovery/conflict-resolution scope.

Closure sequence:

1. persist this completion checkpoint and refresh resume/branch status;
2. verify the resulting documentation-only head remains green;
3. merge PR #40 into `main`;
4. verify post-merge `main` as required by normal workflow.

Only after that should a **new, separate development package** begin for:

> membership revoke + Player/DM authorization

Do not mix that next package into PR #40.