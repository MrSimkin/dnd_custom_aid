# Development and Review Workflow

## 1. Guiding principle

Git must distinguish approved product state, implemented state, accepted/manual state, work in progress, verification actually performed and branch lifecycle. Never confuse chat memory, an old checkpoint, a branch name or green CI with current project truth.

## 2. Branch model

`main` is the sole normal integrated trunk. Use short-lived outcome-oriented branches from current `main`; shared foundations integrate early. Permanent Player/Desktop/Server/provider silos are prohibited absent a concrete later need.

Wave 5 is complete/integrated. Normal continuation is Wave 6 reusable/persistent content architecture.

## 3. Owner vs technical responsibility

Ask the owner for product behavior/workflow, UX/game semantics, privacy/visibility, MVP scope, destructive/safety behavior and meaningful cost/security/compatibility/irreversible-lock-in tradeoffs.

Technical agents normally decide schema/table layout, class/type decomposition, endpoint/request shapes, migrations, internal sync structures, serialization, rendering internals, tests and branch/package granularity. Do not ask for ceremonial approval of routine engineering.

## 4. Work lifecycle

Before implementation:

1. verify remote repository/branch/PR/CI state;
2. read mandatory authority files and current checkpoint;
3. separate approved semantics, implemented constraints, routine engineering and genuine unresolved owner choices;
4. identify real provider/manual boundaries.

Then choose the simplest safe design, implement the smallest coherent batch, run focused + aggregate checks appropriate to risk, update operative memory, leave durable Git evidence, integrate through the normal branch/PR workflow, and continue until a genuine owner/manual/provider boundary.

Historical checkpoints remain evidence, not automatic `next` instructions.

## 5. Verification discipline

Record exact revision/build, checks executed, results, untested areas and evidence type. Never claim a test was run when it was not. CI, real-provider evidence and owner physical/manual QA are different evidence classes.

Green CI cannot establish provider deployment/auth behavior. Conversely, provider work that was completed/tested/recorded must not be repeated merely because documentation changed.

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

Normal DEV owner/DM identity is Outlook-backed. Gmail is historical/inactive by default; preserve historical evidence.

## 8. Secrets/cost

Never request or commit passwords, OTPs, provider/API/deployment tokens, DB passwords/connection strings, JWTs, private keys, signing credentials or secret environment values.

External-service operating budget is USD $0 unless explicitly changed. New provider/resource activation requires current verification of payment method, quotas, hard caps/overage, region/data location and migration/lock-in. Object storage remains deferred until assets actually require it.

## 9. Current implementation sequence

```text
completed baseline/shared/hosted foundations
-> completed Wave 4 Player <-> Server
-> completed Wave 5 Desktop + Campaign Administration
-> NOW Wave 6 reusable/persistent content architecture
-> Wave 7 authoring Managers
-> DM live workspace
-> combat exchange/handoff
-> SRD clarification
-> backup/operator completion
-> integrated owner QA
```

For current exact continuation, use `docs/checkpoints/LATEST.md`.