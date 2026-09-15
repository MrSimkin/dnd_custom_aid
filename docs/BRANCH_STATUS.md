# Branch status and repository-ordering map

**Updated:** 2026-09-15 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Provider-neutral implementation checkpoint:** `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`  
**Provider-neutral validation run:** `34985799585` — SUCCESS  
**Hosted DEV provider activation:** COMPLETE / VERIFIED

This file is the canonical branch-lifecycle map. Branch existence alone never establishes authority.

## 1. `main` — sole normal integrated-MVP trunk

The former split authority has been reconciled and promoted. `main` is the sole normal development trunk for integrated-MVP work.

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

Provider-neutral hosted work is integrated through PR #25, including hosted API/database/native transport, durable outbox, campaign delivery/lifecycle reconciliation, hosted PC snapshots, authorization, revisions/idempotency/conflicts/tombstones and safe reconciliation semantics.

The first real hosted DEV environment is now also activated and verified:

- Neon PostgreSQL DEV — active and migration/contracts verified;
- Descope DEV — real OTP login verified;
- Cloudflare Worker DEV — deployed and authenticated route verified;
- real application identity persistence through Neon — verified;
- representative Workers Free CPU/runtime proof — PASS for the tested authenticated `/v1/me` path.

Provider activation is no longer the current dependency.

The next meaningful implementation package is **real authenticated Player <-> Server development integration**.

## 3. Current integration branch rule

For the next package:

- branch from current `main`;
- reuse the existing provider-neutral auth/network/sync seams;
- wire the real remembered Android Descope session/token at the platform edge;
- connect owner-facing campaign bootstrap/create/select and PC snapshot flows to the already deployed DEV environment;
- preserve local-first behavior and durable outbox semantics;
- prove second-device, offline/reconnect/convergence and revoke/authorization behavior before deeper DM integration.

Do not create another parallel networking/auth/sync architecture merely because providers are now real.

Secrets remain outside Git. The tested DEV provider resources should be reused rather than recreated.

R2/object storage remains later and should only be activated when Media/Handouts/assets reach object-storage integration after a fresh `$0` review.

## 4. Hosted-provider completion evidence

Current hosted completion checkpoint:

`docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md`

It supersedes older operational wording that says Cloudflare + Neon + Descope still need to be created before work can continue.

D-0075 remains controlling for the hard `$0` budget, intentional public repository, secret hygiene and owner-guidance contract.

## 5. `implementation/phase4a-successor-cycle` — historical/frozen Player evidence

Historical head at convergence:

`b9dea8ad6b17dcf3feeabba263eff1ee498f1536`

Its runtime/migrations/tests were absorbed into the integrated baseline. The branch remains useful as historical Player QA/implementation evidence but is no longer a normal development resume point.

Frozen candidate evidence remains:

- `0.4.0-preqa.13 / 41300`;
- candidate commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — SUCCESS;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted physical cross-device revalidation was pending at that historical boundary.

Do not force-move or repurpose this branch. New integrated CI or hosted-provider success does not retroactively claim physical acceptance of the historical candidate.

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

- branch from current remote `main`;
- read `AGENTS.md`, `README.md`, `MANIFEST.md`, `docs/PROJECT_STATE.md` and `docs/checkpoints/LATEST.md`;
- read `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md` for the current hosted environment and exact next package;
- verify current remote HEAD and latest CI before writing;
- do **not** restart Shared Spine, campaign lifecycle, hosted PC snapshot, provider activation or convergence work;
- preserve DM authority vs PC ownership distinction;
- preserve owner vs controller distinction;
- preserve no-silent-overwrite, stale-revision, idempotency and non-resurrection semantics;
- return to the owner only for material product/scope/security/privacy/cost/lock-in/destructive-behavior decisions, external account/service actions not already completed, or manual/physical QA gates.

If no later checkpoint supersedes the current one, continue with real authenticated Player <-> Server integration using the already activated DEV providers.

## 9. Security / repository visibility

The repository is intentionally **public** under D-0075. `private: false` is expected and is not a discrepancy.

Never store provider/database credentials, bearer/session tokens, private keys or other confidentiality-dependent material in Git.

Open security residuals are tracked in the hosted-provider completion checkpoint; they do not reopen the completed provider-activation gate.
