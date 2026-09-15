# Hosted provider activation gate — development environment handoff

**Status:** PREPARED / OWNER ACTION REQUIRED BEFORE ACTIVATION  
**Prepared:** 2026-09-15  
**Controlling owner policy:** `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md`  
**Implementation checkpoint:** `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`

This document defines the first external-service activation boundary for the integrated MVP. It is a handoff/checklist, not authorization to create accounts, billing commitments or production resources.

## 1. Hard budget gate

The project external-service operating budget is **USD $0** unless the owner explicitly changes it.

Do not interpret "free tier" as automatically acceptable. Immediately before activation, verify current official provider information for:

- whether a payment method is required;
- whether free usage can automatically become billable overage;
- whether quota exhaustion fails, suspends or requires explicit upgrade;
- relevant compute/request/storage/network/auth-user quotas;
- region/data-location implications;
- meaningful vendor lock-in and migration/exit path.

Prefer a hard stop/suspension/explicit-upgrade gate over any configuration capable of producing an automatic invoice.

Do **not** enable paid plans, paid add-ons, billing commitments or overage-enabled resources without explicit owner approval.

## 2. Revalidated first development providers

The September 2026 full-product review retained the original first hosted-development direction:

1. **Cloudflare Workers** — development Worker/API runtime; KEEP, conditional on an early representative free-tier CPU/runtime proof;
2. **Neon PostgreSQL** — development relational database; KEEP;
3. **Descope** — development authentication/identity; KEEP, conditional on current Free-plan payment-method/region confirmation.

Workers AI remains the approved later SRD clarification provider while its relevant path remains safely usable at `$0`.

Object storage is deliberately **not selected/activated here**. R2 remains only a candidate and must receive a separate zero-spend review when Media/Handouts/assets reach implementation.

## 3. Already ready before activation

Repository code already provides:

- Worker `/v1` request/auth/domain handling;
- application-owned authorization;
- provider-neutral token-verifier seam and local-only static auth mode;
- Descope/JWKS verifier path;
- explicit PostgreSQL migrations/contracts and PostgreSQL CI tests;
- campaign/membership lifecycle contracts;
- hosted PC snapshot contracts and authorization;
- shared Ktor hosted client;
- `HostedAccessTokenProvider` seam;
- local-first durable outbox, idempotency and retry classification;
- campaign reconciliation and PC snapshot reconciliation/conflict rules.

Do not duplicate or replace these systems merely to postpone external activation.

## 4. Owner guidance contract during setup

The owner is technically oriented and capable of substantial hands-on work, but is not a professional developer. Setup instructions must teach while guiding.

For each provider/action, explain:

```text
WHAT this component does
        |
WHY the project needs it
        |
WHAT the owner must click/type
        |
WHAT the owner should expect to see
        |
WHAT must remain secret / when to STOP
```

Use real technical terminology, but explain it in plain language. Use small ASCII flows/wireframes when they improve understanding. Do not reduce instructions to unexplained command dumps, and do not push routine engineering design decisions back to the owner.

## 5. Owner-controlled activation sequence

Before asking the owner to create anything, re-check the then-current official provider documentation and confirm D-0075 compliance.

Recommended development/test sequence:

1. Neon development PostgreSQL project/database;
2. Descope development project/application;
3. Cloudflare development Worker environment/project.

The ordering is operational convenience, not architecture. Native clients still talk to the Worker/API and must not receive Neon credentials.

If a signup/dashboard unexpectedly requests payment, paid-plan selection, an overage commitment or another material billing/security choice, **stop before accepting it** and return to the owner with a plain-language explanation and alternatives.

## 6. Configuration mapping

The Worker currently expects the established configuration/binding family:

- `APP_ENV` — environment classification;
- `DATABASE_URL` — Neon PostgreSQL connection string; **secret**;
- `AUTH_MODE` — hosted auth mode;
- `DESCOPE_PROJECT_ID` — normally non-secret project identifier, subject to current provider guidance;
- `DESCOPE_JWKS_URL` — verifier/JWKS endpoint configuration;
- local static-auth values only for the explicitly local development path.

Inspect current code at activation time in case later work changes configuration names.

## 7. Secret-handling rules

Never commit or place in durable public documentation:

- database passwords/connection credentials;
- access/refresh/session tokens;
- private keys;
- provider API/admin/deployment tokens;
- local static bearer tokens used for testing.

Use provider/runtime secret stores or ignored local configuration. Native Android/Desktop clients must never receive Neon/database credentials.

The GitHub repository is intentionally **public**. Public visibility is expected, not a discrepancy. Secret hygiene is mandatory precisely regardless of repository visibility.

Any older wording that treats `private: false` as unexpected is superseded by D-0075.

## 8. Early Cloudflare free-tier proof

Before deep owner-facing Android hosted integration, deploy/exercise representative authenticated API requests and verify that normal endpoints fit the then-current Cloudflare Workers Free runtime/CPU limits.

Representative proof should include at least:

- token verification;
- a normal authenticated account/campaign read;
- a representative campaign or PC mutation;
- normal Neon database access.

If the actual workload does not fit the free runtime reliably, do not silently upgrade to a paid Worker plan. Reassess the API host under the `$0` policy.

## 9. First real validation after activation

The activation package is complete only after the development environment proves, in order:

1. Worker health/configuration against the real development environment;
2. SQL migrations applied and compatible with development Neon;
3. valid Descope-authenticated request resolves to the application-owned user identity;
4. representative Worker CPU/runtime behavior is acceptable on the free plan;
5. campaign list/bootstrap works through the deployed Worker;
6. local campaign creation survives offline-first persistence and later hosted delivery;
7. hosted PC snapshot upload/download works through the existing shared sync path;
8. stale revision and authorization failures remain explicit/non-destructive;
9. second-device observation and reconnect/convergence are demonstrated before calling Player↔Server end-to-end complete.

## 10. Android auth/session package after activation

The first owner-facing Android hosted package should add remembered authentication/session acquisition at the platform edge and feed access tokens into the existing shared `HostedAccessTokenProvider` contract.

Keep provider-specific Android/session logic outside common domain logic. Do not rewrite the Player runtime or replace shared sync/transport solely because a provider SDK is introduced.

## 11. Stop conditions

Return to the owner before proceeding if activation requires a material choice involving:

- any payment, paid plan, billing commitment or realistic automatic-charge risk;
- region/data residency with meaningful consequences;
- account ownership/recovery policy;
- security/privacy tradeoff;
- meaningful vendor lock-in;
- production rather than development resources;
- destructive/manual action outside ordinary reversible setup.

Routine project naming, migration commands, code wiring and test mechanics remain delegated technical work once the owner-controlled free development resources exist.
