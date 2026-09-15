# Latest project checkpoint — global resume map

**Updated:** 2026-09-15 (Chile local time)  
**Normal implementation trunk:** `main`  
**Provider-neutral implementation checkpoint:** `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`  
**Provider-neutral implementation validation:** Actions `34985799585` — **SUCCESS**  
**Last pre-activation consolidated main:** `a6bb965cf08a14878150a74d41191023dd70d552`  
**Pre-activation main validation:** Actions `34993181692` — **SUCCESS**  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md` — mandatory project operating rules;
2. `README.md` — repository entry point;
3. `MANIFEST.md` — project-memory/navigation map;
4. `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md` — current hosted DEV environment and exact continuation;
5. `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md` — controlling `$0`, public-repository and owner-guidance policy;
6. `docs/PROJECT_STATE.md` — current global product/engineering state;
7. `docs/BRANCH_STATUS.md` — branch lifecycle/resume rule;
8. `docs/ROADMAP.md` — implementation-wave sequence;
9. `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md` — provider-neutral engineering contracts;
10. `docs/recovery/PROJECT_RECOVERY_PROMPT.md` — reusable whole-project fresh-chat recovery prompt.

If older operational prose conflicts with this file or the current completion checkpoint, the newer specific checkpoint controls unless an even later approved decision/checkpoint supersedes it.

## Important supersession/corrections

The GitHub repository is intentionally **public** under D-0075. Any older wording treating `private: false` as an unexpected security/privacy discrepancy is superseded and must not be acted upon.

The first Cloudflare + Neon + Descope DEV activation is **complete**. Older text saying those resources still need to be created/authorized is historical and no longer the current dependency.

The real Neon database name is `dnd-custom-aid-dev` with hyphens.

The current deployed Worker contract actually used by `backend/src/index.ts` requires `DATABASE_URL` + `DESCOPE_PROJECT_ID`, with optional `DESCOPE_BASE_URL`; older activation handoff references to `APP_ENV`, `AUTH_MODE` and `DESCOPE_JWKS_URL` are not the current deployed configuration contract.

Secret hygiene remains strict regardless of repository visibility.

## Hard external-service budget

External-service operating budget remains **USD $0** unless the owner explicitly changes it.

Provider activation completed without authorizing paid plans or billable infrastructure. Do not silently introduce paid plans, paid add-ons, overage-enabled resources or billing commitments later.

Object storage remains deferred. Do not activate R2 merely because Cloudflare is already in use.

Workers AI remains a later conditional direction for official-SRD clarification only while it can be used safely under the `$0` policy.

## Current implementation state

`main` is the sole normal integrated-MVP development trunk. Historical Player/convergence branches are evidence only.

Completed:

- baseline convergence;
- Wave 2 Shared Integrated-MVP Spine;
- provider-neutral hosted foundation through PR #25;
- real Neon DEV migration and contract proof;
- real Descope DEV OTP authentication;
- deployed Cloudflare DEV Worker;
- real authenticated `/v1/me` -> application identity -> Neon persistence;
- representative Cloudflare Workers Free CPU/runtime proof for the authenticated `/v1/me` path.

The completion checkpoint contains the exact provider identifiers, public URLs, verification details, local-workflow caveats and security residuals.

## Hosted DEV status

```text
Neon PostgreSQL      COMPLETE / VERIFIED
Descope identity     COMPLETE / VERIFIED
Cloudflare Worker    COMPLETE / VERIFIED
Real auth round trip VERIFIED
Real Neon persistence VERIFIED
Workers Free CPU gate PASS for tested representative path
```

The tested path showed roughly 1 ms CPU per visible authenticated `/v1/me` invocation with no observed errors. Wall time may be higher because network/database waiting is distinct from Worker CPU time.

Future materially heavier endpoints should still be profiled.

## Exact current continuation

The next primary development package is:

**real authenticated Player <-> Server development integration**

Provider activation prerequisites are satisfied. Do not create another parallel auth/network/sync abstraction and do not repeat account setup.

Proceed from existing contracts in dependency order:

```text
remembered Android Descope session/token
        |
        v
existing HostedAccessTokenProvider
        |
        v
hosted account/campaign bootstrap
        |
        v
campaign create/select + durable hosted delivery
        |
        v
PC snapshot push/pull
        |
        v
second-device observation
        |
        v
offline/reconnect/convergence + revoke enforcement
```

Preserve local-first behavior, stable IDs, revisions, idempotency, tombstones, explicit conflicts, DM authority vs PC ownership, owner vs controller distinction and non-destructive local recovery.

## Security residuals carried forward

Activation success does not close security work. Important follow-up topics include:

- JWT/fail-closed verification review;
- object-level authorization regression coverage;
- SQL/query safety;
- error/log secret leakage;
- replay/idempotency authorization;
- request/API hardening;
- exact investigation of the locally reported **3 high severity npm vulnerabilities** — do not run `npm audit fix --force` blindly;
- evaluate a dedicated least-privilege Neon runtime role instead of the current owner-role credential;
- production-region/identity configuration review before release.

These are visible residuals, not a reason to pretend provider activation is still pending.

## Historical Player evidence remains bounded

Current integrated CI and hosted DEV proof do not retroactively establish physical owner acceptance of the historical frozen Player candidate. Preserve the historical QA evidence for exactly what it tested.
