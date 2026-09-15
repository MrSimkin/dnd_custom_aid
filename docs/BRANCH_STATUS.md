# Branch status and repository-ordering map

**Updated:** 2026-09-15 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Verified hosted/sync implementation checkpoint:** `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`  
**Validation run:** `34985799585` — SUCCESS

This file is the canonical branch-lifecycle map. Branch existence alone never establishes authority.

## 1. `main` — sole normal integrated-MVP trunk

The former split authority has already been reconciled and promoted. `main` is the sole normal development trunk for integrated-MVP work.

Normal implementation should:

1. start from current remote `main`;
2. use a short-lived outcome-oriented branch;
3. integrate early when shared contracts are needed elsewhere;
4. merge only after proportionate validation;
5. verify the post-merge `main` run;
6. update durable docs/checkpoints when operational truth materially changes.

Do not create permanent `player-main`, `server-main`, `desktop-main` or months-long catch-all integration branches.

Current verified implementation checkpoint `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3` is the PR #25 merge that completes the provider-neutral hosted PC current-state/snapshot foundation. Actions run `34985799585` passed Player guards, shared/Kotlin tests, Android, Desktop, backend and hosted-database checks on the merged `main` commit.

## 2. Current implementation position

Wave 2 — Shared Integrated-MVP Spine — is complete and integrated.

Wave 3 provider-neutral hosted work is integrated through PR #25:

- shared spine;
- hosted API/database/transport foundation;
- durable outbox;
- campaign-create hosted delivery;
- authenticated account/campaign bootstrap;
- explicit campaign membership lifecycle/deletion reconciliation;
- hosted PC current-state snapshot API/persistence/authorization;
- local durable PC snapshot delivery and safe reconciliation/conflict semantics.

The next meaningful implementation package is **real authenticated Player↔Server development integration**.

The designed external-provider activation gate has now been reached. Do not manufacture parallel auth/network/sync abstractions merely to avoid it.

## 3. External-provider branch rule

Do not create provider resources, billing commitments, production environments or secrets speculatively.

Before the next owner-facing hosted package, the owner must authorize/create development resources for Cloudflare + Neon + Descope after current plan/region/security/cost choices are reviewed.

Once those resources exist, use a short-lived provider/integration package from current `main`, keep secrets outside Git, validate the real end-to-end path, and merge the resulting code/config documentation normally.

R2 remains later and should be activated only when Media/Handouts/assets reach object-storage integration.

Use `docs/technical/HOSTED_PROVIDER_ACTIVATION_GATE.md` for the prepared activation handoff.

## 4. `implementation/phase4a-successor-cycle` — historical/frozen Player evidence

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

## 5. `integration/mvp-baseline-convergence` — historical integration evidence

The convergence branch completed its purpose and was promoted through PR #14.

Semantic convergence commit:

`5bed85cbb3e86ae63eac79149fadc5e56e61b256`

It remains evidence of the deliberate reconciliation of the former Player successor and integrated product/architecture line. It is not a normal resume or implementation branch.

## 6. Other historical refs

Historical/frozen refs remain evidence only unless a later checkpoint explicitly reactivates one. Use `docs/archive/2026-09-09_BRANCH_REF_ARCHIVE_BEFORE_CLEANUP.md` for deliberately removed historical refs rather than recreating them.

Frozen QA refs must not be force-moved or repurposed.

## 7. Current resume rule

For normal implementation:

- branch from current remote `main`;
- read `AGENTS.md`, `README.md`, `MANIFEST.md`, `docs/checkpoints/LATEST.md` and `docs/PROJECT_STATE.md`;
- use `docs/checkpoints/2026-09-15_PLAYER_SERVER_PROVIDER_BOUNDARY.md` for the current hosted/sync handoff;
- verify current remote HEAD and latest CI before writing;
- do **not** restart Shared Spine, campaign lifecycle, hosted PC snapshot or convergence work;
- do **not** infer lifecycle/removal state from response absence;
- preserve DM authority vs PC ownership distinction;
- preserve no-silent-overwrite, stale-revision and non-resurrection semantics;
- return to the owner only for material product/scope/security/privacy/cost/lock-in/destructive-behavior decisions, external account/service activation, or manual/physical QA gates.

If no later checkpoint supersedes the current one, the next dependency is owner-controlled Cloudflare + Neon + Descope development activation followed by real Player↔Server integration.

## 8. Security visibility note

At the checkpoint above, GitHub repository metadata reports `private: false`. The owner should verify that repository visibility matches their intent before provider integration. Do not change repository visibility autonomously and never store secrets in Git regardless of visibility.
