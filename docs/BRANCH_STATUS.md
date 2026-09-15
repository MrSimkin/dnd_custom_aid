# Branch status and repository-ordering map

**Updated:** 2026-09-15 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Provider-neutral implementation checkpoint:** `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`  
**Hosted DEV provider activation:** COMPLETE / VERIFIED  
**Android hosted-session integration:** COMPLETE / OWNER-PHYSICAL PASS  
**Android hosted campaign bootstrap:** COMPLETE / OWNER-PHYSICAL PASS  
**Campaign + PC hosted delivery/recovery:** COMPLETE / OWNER-PHYSICAL PASS

This file is the canonical branch-lifecycle map. Branch existence alone never establishes authority.

## 1. `main` — sole normal integrated-MVP trunk

`main` is the sole normal development trunk for integrated-MVP work.

Normal implementation should:

1. start from current remote `main`;
2. use a short-lived outcome-oriented branch;
3. integrate early when shared contracts are needed elsewhere;
4. merge only after proportionate validation;
5. verify post-merge `main` CI;
6. update durable docs/checkpoints when operational truth materially changes.

Do not create permanent `player-main`, `server-main`, `desktop-main` or months-long catch-all integration branches.

## 2. Current implementation position

Completed/integrated foundations include:

- Wave 2 Shared Integrated-MVP Spine;
- provider-neutral hosted foundation through PR #25;
- real Neon + Descope + Cloudflare DEV activation;
- PR #30 remembered Android Descope session + `HostedAccessTokenProvider`;
- PR #32 ordinary Player hosted account/campaign bootstrap;
- PR #34 local-first campaign hosted delivery + PC snapshot push/pull;
- repair of the real PC wire-envelope `VALIDATION_FAILED` found during owner physical testing;
- successful retry/acknowledgement/removal of the same preserved blocked PC mutation.

PR #34 integrated commit:

`75d5acf354b41185255ff7d1a5eb4a689f300721`

Validation:

- exact-head Actions `35027987125` / #1939 — SUCCESS;
- post-merge Actions `35028893643` / #1940 — SUCCESS.

One physical observation is deliberately carried forward: an unchanged repeat Player sync followed by a second empty-outbox diagnostic was requested but not separately reported before consolidation. Include it in the next batched physical gate rather than overclaiming it.

The next meaningful implementation package is **multi-client PC convergence safety**.

## 3. Current integration branch rule

For the next package:

- branch from current remote `main`;
- reuse the existing Descope Android session, `HostedAccessTokenProvider`, hosted API and durable outbox;
- add enough last-synchronized PC baseline knowledge to distinguish clean-old local state from locally edited old state;
- automatically apply server-newer PC state only when the old local copy is still clean;
- preserve local data and surface an explicit conflict when both local and hosted state advanced;
- preserve offline local edits and deliver them after reconnect when the server did not change;
- preserve stable IDs, optimistic revisions, idempotency, tombstones/non-resurrection, DM authority vs PC ownership and owner vs controller distinction;
- do not create another parallel networking/auth/sync architecture;
- do not rebuild the completed provider/session/bootstrap/campaign/first-PC-delivery edges.

The owner explicitly prefers batched development/testing. Accumulate several related implementation steps with automated CI before returning for one consolidated physical Android gate.

That next physical gate should include:

- the carried-forward unchanged-repeat no-op check;
- fresh second-client observation;
- offline edit/reconnect when the server did not change;
- remote-newer clean-local convergence;
- concurrent local+remote edit conflict preservation.

The debug-only `DnD Aid - Hosted DEV Auth` activity remains a verification harness, not final Player login UX.

Secrets remain outside Git. Tested DEV provider resources should be reused rather than recreated.

Object storage remains later and should only be activated when Media/Handouts/assets reach object-storage integration after a fresh `$0` review.

## 4. Current completion evidence

Current exact handoff:

`docs/checkpoints/2026-09-15_ANDROID_HOSTED_CAMPAIGN_PC_SYNC_HANDOFF.md`

Latest practical resume map:

`docs/checkpoints/LATEST.md`

Hosted provider/environment checkpoint:

`docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md`

D-0075 remains controlling for the hard `$0` budget, intentional public repository, secret hygiene and owner-guidance contract.

## 5. `implementation/phase4a-successor-cycle` — historical/frozen Player evidence

Historical head at convergence:

`b9dea8ad6b17dcf3feeabba263eff1ee498f1536`

Its runtime/migrations/tests were absorbed into the integrated baseline. The branch remains useful as historical Player QA/implementation evidence but is no longer a normal development resume point.

Frozen candidate evidence remains:

- `0.4.0-preqa.13 / 41300`;
- candidate commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — SUCCESS;
- targeted physical cross-device revalidation was pending at that historical boundary.

Do not force-move or repurpose this branch. New integrated CI/hosted success does not retroactively claim physical acceptance of the historical candidate.

## 6. `integration/mvp-baseline-convergence` — historical integration evidence

The convergence branch completed its purpose and was promoted through PR #14.

Semantic convergence commit:

`5bed85cbb3e86ae63eac79149fadc5e56e61b256`

It remains evidence of deliberate reconciliation of the former Player successor and integrated product/architecture line. It is not a normal resume or implementation branch.

## 7. Other historical refs

Historical/frozen refs remain evidence only unless a later checkpoint explicitly reactivates one. Use `docs/archive/2026-09-09_BRANCH_REF_ARCHIVE_BEFORE_CLEANUP.md` for deliberately removed historical refs rather than recreating them.

Frozen QA refs must not be force-moved or repurposed.

## 8. Current resume rule

For normal implementation:

- branch from current remote `main`;
- read `AGENTS.md`, `README.md`, `MANIFEST.md`, `docs/checkpoints/LATEST.md`, `docs/PROJECT_STATE.md`;
- read `docs/checkpoints/2026-09-15_ANDROID_HOSTED_CAMPAIGN_PC_SYNC_HANDOFF.md` for the exact current Wave 4 position;
- read the hosted activation checkpoint only when exact provider evidence is needed;
- verify current remote HEAD and latest CI before writing;
- do not restart Shared Spine, provider activation, Android session acquisition, campaign bootstrap, campaign delivery or first PC snapshot delivery;
- preserve no-silent-overwrite, stale-revision, idempotency and non-resurrection semantics;
- return to the owner only for material product/scope/security/privacy/cost/lock-in/destructive-behavior decisions, new external account/service actions, or manual/physical QA gates.

If no later checkpoint supersedes this state, continue with **multi-client PC convergence safety**.

## 9. Security / repository visibility

The repository is intentionally **public** under D-0075. `private: false` is expected and is not a discrepancy.

Never store provider/database credentials, bearer/session tokens, private keys or other confidentiality-dependent material in Git.

Open security residuals are tracked in the current checkpoints; they do not reopen completed provider/session/bootstrap/campaign/PC gates.
