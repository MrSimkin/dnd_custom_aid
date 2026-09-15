# Latest project checkpoint — global resume map

**Updated:** 2026-09-15 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified implementation checkpoint:** `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`  
**Implementation validation:** Actions `34985799585` — **SUCCESS**  
**Post-provider-boundary documentation validation:** Actions `34986965813` — **SUCCESS**  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md` — mandatory project operating rules;
2. `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md` — controlling `$0` service policy, September 2026 provider revalidation, intentional public-repository clarification and owner guidance contract;
3. `docs/checkpoints/2026-09-15_PLAYER_SERVER_PROVIDER_BOUNDARY.md` — implementation/provider-boundary checkpoint;
4. `docs/technical/HOSTED_PROVIDER_ACTIVATION_GATE.md` — safe development-provider activation handoff;
5. `docs/PROJECT_STATE.md` — global product/engineering state;
6. `docs/BRANCH_STATUS.md` — branch lifecycle/resume rule;
7. `docs/ROADMAP.md` — implementation-wave sequence;
8. `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md` — engineering baseline;
9. `docs/recovery/PROJECT_RECOVERY_PROMPT.md` — reusable fresh-chat recovery prompt.

If older operational prose conflicts with this file or D-0075, the newer specific rule controls unless an even later approved decision/checkpoint supersedes it.

## Important supersession/correction

The GitHub repository is intentionally **public**. Any older wording in active or historical documents that treats `private: false` or public repository visibility as an unexpected security/privacy discrepancy is **superseded and must not be acted upon**.

Secret hygiene remains strict regardless of visibility: never commit provider credentials, database credentials, access/session tokens, private keys or other confidentiality-dependent material.

## Hard external-service budget

External-service operating budget is **USD $0** unless the owner explicitly changes it.

This is a hard constraint, not a request to merely prefer cheap plans. A service with a headline free tier is acceptable only after checking current official information for payment-method requirements, automatic-overage behavior, hard quotas/failure behavior, region/data-location implications and migration/lock-in.

Prefer free services that suspend/fail/require explicit upgrade when exhausted rather than silently creating charges. Never enable paid plans, paid add-ons, overage-enabled resources or billing commitments without explicit owner approval.

## Owner guidance contract

The owner is technically oriented, a heavy/power user, understands programming concepts and can perform substantial hands-on work, but is not a professional software developer.

Owner-facing technical instructions should teach while guiding:

- explain what is being done and why in plain language first;
- introduce real technical terminology with explanation;
- provide explicit ordered actions;
- say what should be visible/expected after important steps;
- flag secrets, billing/security risk and stop conditions before consequential actions;
- use ASCII diagrams/wireframes/flows when they improve understanding;
- distinguish owner actions from implementation handled by the technical agent;
- do not patronize the owner and do not push routine engineering decisions back to them.

See D-0075 for the controlling detail.

## Current implementation state

`main` is the sole normal integrated-MVP development trunk. Historical Player/convergence branches are evidence only.

Wave 2 — Shared Integrated-MVP Spine — is complete.

Provider-neutral Wave 3 hosted foundation is integrated through PR #25, including hosted API/database contracts, shared native transport, token boundaries, durable hosted outbox, local-first campaign delivery, membership lifecycle reconciliation, hosted PC snapshots, authorization, revisions/idempotency/conflicts/tombstones and safe same-identity reconciliation.

PR #25 merged as:

`8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`

Actions `34985799585` completed SUCCESS across backend, hosted PostgreSQL contracts, shared/Kotlin tests, Android, Desktop, APK upload and preserved Player guards.

PR #26 subsequently consolidated the provider boundary on `main`; its post-merge run `34986965813` completed SUCCESS.

## Revalidated provider direction

The completed MVP design was re-reviewed against the hard `$0` constraint. The core choices remain valid:

- **Cloudflare Workers — KEEP, conditional on an early representative free-tier CPU/runtime proof**;
- **Neon PostgreSQL — KEEP**;
- **Descope — KEEP, conditional on current Free-plan payment/region confirmation at activation**;
- **Workers AI — KEEP** for the approved SRD clarification path while it remains safely usable at `$0`;
- **object storage — DEFER provider selection**; do not assume R2 merely because it has a free allowance.

## Exact next dependency

If no later checkpoint supersedes this one, the next meaningful implementation package is **real authenticated Player↔Server development integration**.

Provider-neutral prerequisites are sufficiently complete. Do not create another parallel auth/networking/sync abstraction merely to postpone the external environment.

Before owner-facing remembered authentication and real hosted round trips are wired into Player Android, owner-controlled **development/test** resources are required for:

1. Cloudflare — Worker/API runtime;
2. Neon — PostgreSQL;
3. Descope — authentication/identity.

Before creating/configuring them, re-check current official provider plans/quotas/regions/payment requirements and demonstrate compliance with D-0075's `$0` policy.

Then proceed with:

```text
free development resources
        |
        v
Cloudflare Worker <-> Neon PostgreSQL
        |
        +-> Descope identity proof
        |
        v
remembered Android session/token
        |
        v
campaign bootstrap/create/select
        |
        v
PC snapshot push/pull
        |
        v
second-device + offline/reconnect + revoke tests
```

Run the early representative Cloudflare CPU/runtime proof before deepening client integration. If the free Worker runtime is not adequate, reassess the API host rather than silently selecting a paid plan.

R2/object storage is not part of the first activation step.

## Historical Player evidence remains bounded

Current integrated CI does not retroactively establish physical acceptance of the historical frozen Player candidate.
