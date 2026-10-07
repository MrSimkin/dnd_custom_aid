# Development and Review Workflow

## 1. Guiding principle

Git must distinguish approved product state, implemented state, accepted/manual state, work in progress, verification actually performed and branch lifecycle. Never confuse chat memory, an old checkpoint, a branch name or green CI with current project truth.

## 2. Branch model

`main` is the sole normal integrated trunk. Use short-lived outcome-oriented branches from current `main`; shared foundations integrate early. Permanent Player/Desktop/Server/provider silos are prohibited absent a concrete later need.

The exact current continuation is not duplicated in this workflow file. A fresh session follows `RESUME.md -> docs/checkpoints/LATEST.md -> Canonical active checkpoint`.

## 3. Owner vs technical responsibility

Ask the owner for product behavior/workflow, UX/game semantics, privacy/visibility, MVP scope, destructive/safety behavior and meaningful cost/security/compatibility/irreversible-lock-in tradeoffs.

Technical agents normally decide schema/table layout, class/type decomposition, endpoint/request shapes, migrations, internal sync structures, serialization, rendering internals, tests and branch/package granularity. Do not ask for ceremonial approval of routine engineering.

## 4. Work lifecycle

Before implementation:

1. verify remote repository/branch/PR/CI state;
2. for fresh-context work, follow `RESUME.md -> docs/checkpoints/LATEST.md -> Canonical active checkpoint` before broader archaeology;
3. read only the additional authority files needed by that active task;
4. separate approved semantics, implemented constraints, routine engineering and genuine unresolved owner choices;
5. identify real provider/manual boundaries.

Then choose the simplest safe design, implement the smallest coherent batch, run focused + aggregate checks appropriate to risk, update operative memory, leave durable Git evidence, integrate through the normal branch/PR workflow, and continue until a genuine owner/manual/provider boundary.

Historical checkpoints remain evidence, not automatic `next` instructions.

When the active work route changes, the same coherent repository change must update/create its checkpoint and move the **Canonical active checkpoint** pointer in `docs/checkpoints/LATEST.md`. Update broader state/branch documents when materially affected. A new checkpoint file by itself does not become the active route.

## 5. Verification discipline

Record exact revision/build, checks executed, results, untested areas and evidence type. Never claim a test was run when it was not. CI, real-provider evidence and owner physical/manual QA are different evidence classes.

Green CI cannot establish provider deployment/auth behavior. Conversely, provider work that was completed/tested/recorded must not be repeated merely because documentation changed.

### 5.1 Active Mara Phase-3 two-tier CI feedback loop

For `repair/mara-phase3-semantic-flow-compositor`, ordinary pushes use the focused `PC Sheet fast gate` instead of replaying the full repository Scaffold on every renderer iteration.

The fast gate:

- checks the canonical resume route;
- checks Desktop/Android PC-sheet renderer parity and PDF-delivery guards;
- runs the focused Phase-3 PDF regression tests for the active continuity front;
- uploads only the focused proof PDFs;
- may write Gradle cache for this long-lived repair branch.

The full `Scaffold checks` workflow remains authoritative aggregate evidence and still runs on pull requests, on pushes outside this repair branch, and on explicit manual dispatch. Use it at coherent package/PR closure rather than as the interactive compiler for every small Mara correction.

`CI_GREEN / VISUAL_PENDING` is a valid intermediate state when automated checks passed but the criterion still requires direct PDF inspection. A connection, polling, server or artifact-download failure is not a code failure: verify the existing GitHub run/artifact before creating another commit or replaying the operation.

For interactive repair turns, avoid tight polling loops that keep the chat/session alive solely waiting for CI. After a commit:
1. verify once that the expected fast-gate run exists and record its run ID;
2. if that run is `queued` or `in_progress`, do not immediately end the turn when useful independent read-only work remains;
3. continue safe read-only work that does not depend on the run result, such as burn-down reconciliation, proof archaeology, output review from already-validated artifacts, or diagnosis/preparation of the next criterion;
4. after a meaningful interval of useful work, the same run may be checked again; polling must remain sparse and never become a tight wait loop;
5. do not create a dependent corrective commit, rerun, duplicate run or advance the active write-front until the prior run has completed and any required proof artifact has been inspected;
6. use `CI_RUNNING / NO_ACTION` only when CI is genuinely the sole remaining dependency and no useful independent work remains in the turn.

This async discipline controls timeout risk without turning the owner into a manual “continue” trigger and without weakening evidence gates.

## 6. External-provider capability protocol

For Cloudflare, Descope, Neon or another authenticated provider:

1. determine whether the current environment has actual authenticated capability for the required action;
2. if yes, proceed only inside approved scope/security/cost boundaries;
3. if no, stop trying alternate plugins/MCP/browser/API-token/dashboard routes once that is established;
4. finish all safe repository/code/test/CI preparation;
5. identify the first unavoidable owner provider action;
6. provide one exact owner-action packet with what/why/where, ordered commands/clicks, expected result, secrets not to share, non-secret evidence to return, stop/error conditions and billing/security/destructive warnings;
7. stop at that boundary and resume from returned non-secret evidence without repeating completed work.

Prefer one external handoff per task. Provider inability does not require artificial micro-packaging of safe engineering.

## 7. Current provider baseline

Existing DEV Worker: `dnd-custom-aid-api` at `https://dnd-custom-aid-api.mrsimkin-dev.workers.dev`.

Wave 5 deployment is already verified. Do not create a replacement Worker or redeploy it for documentation-only changes. Redeploy only when Worker code materially changes or newer evidence requires it, preserving configured secrets.

Normal DEV owner/DM identity is Outlook-backed. Gmail is historical/inactive by default, but a current canonical checkpoint may deliberately assign Gmail as a secondary Player for bounded QA; follow that checkpoint without rewriting the normal default. Preserve historical evidence.

## 8. Secrets/cost

Never request or commit passwords, OTPs, provider/API/deployment tokens, DB passwords/connection strings, JWTs, private keys, signing credentials or secret environment values.

External-service operating budget is USD $0 unless explicitly changed. New provider/resource activation requires current verification of payment method, quotas, hard caps/overage, region/data location and migration/lock-in. Object storage remains deferred until assets actually require it.

## 9. Current implementation sequence

The live implementation sequence is intentionally not duplicated here because it changes more often than workflow rules.

For current exact continuation, use:

`RESUME.md -> docs/checkpoints/LATEST.md -> Canonical active checkpoint`.

Roadmap/dependency documents remain useful for broader sequencing after the active checkpoint is satisfied.