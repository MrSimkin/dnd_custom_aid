# D-0075 — Zero-budget provider policy, provider revalidation and owner guidance

**Status:** Approved / controlling  
**Date:** 2026-09-15  
**Owner authority:** explicit clarification and approval in project conversation

## 1. Purpose

This decision makes two owner-level constraints durable so they survive loss of chat context:

1. the project's external-service operating budget is **USD $0** unless the owner explicitly changes it later;
2. technical guidance must be written for an owner who is technically oriented and willing to do substantial hands-on work, but is **not a professional software developer**.

This record also captures the September 2026 revalidation of the previously selected hosted-provider direction.

## 2. Hard external-service budget rule

The normal project budget for hosted/external services is:

> **USD $0 recurring and USD $0 usage spend.**

This is a hard architectural/operational constraint, not merely a preference to minimize cost.

A service described as having a free tier is not automatically acceptable. Before activation, verify current official provider terms and determine:

- whether a payment method is required;
- whether free usage can automatically roll into billable overage;
- whether a hard quota, suspension, failure or explicit upgrade gate prevents accidental spend;
- relevant storage, compute, transfer, request, authentication-user and AI quotas;
- meaningful region/data-location limits;
- practical migration/exit path;
- material vendor lock-in.

Prefer services where exhausting the free allowance **fails, suspends or requires an explicit owner upgrade** rather than generating an automatic invoice.

No agent may autonomously enable a paid plan, paid add-on, billing commitment, overage-enabled resource or other spend-capable configuration. If a technically attractive option requires payment or introduces realistic automatic-charge risk, return to the owner with a plain-language explanation and alternatives.

The owner may explicitly revise this policy later. Until then, `$0` controls.

## 3. September 2026 provider revalidation

The completed integrated-MVP design was re-evaluated against the hard `$0` constraint rather than preserving historical provider choices by inertia.

Current verdict:

- **Cloudflare Workers — KEEP, conditional.** Strong free-tier fit for the expected personal/small-scale workload. Before deep client integration, prove representative authenticated API requests fit the applicable free-tier CPU/runtime limits. If they do not, reassess the API host rather than silently moving to a paid plan.
- **Neon PostgreSQL — KEEP.** Strong fit for the current relational model, free-plan development use and future PostgreSQL portability. Keep binary/media assets out of PostgreSQL and monitor free storage/network limits.
- **Descope — KEEP, conditional.** Good fit for native Android remembered authentication/session handling and standards-based native/Desktop authentication. Before activation, confirm the then-current Free-plan payment-method, regional/data-location and quota conditions.
- **Cloudflare Workers AI — KEEP for the approved SRD clarification direction**, provided the selected model/path remains usable within the then-current free allowance and exhaustion fails safely rather than producing spend.
- **Object storage — DEFER provider selection.** Cloudflare R2 remains a candidate, not an assumed provider. At Media/Handouts integration, perform a separate zero-spend review because a nominal free allowance is insufficient if overage can generate charges.

The architecture therefore remains, for the first hosted-development activation:

```text
Player / DM native clients
          |
          v
Cloudflare Worker/API
          |
          +---- identity proof: Descope
          |
          v
Neon PostgreSQL
```

This is a revalidated decision, not merely preservation of an old one.

## 4. Migration and incrementality principle

Free-now must not mean trapped-later.

Provider choices should continue to favor:

- project-owned application/domain contracts;
- PostgreSQL portability for core hosted relational data;
- provider-specific code localized at infrastructure/platform edges;
- standards-based authentication where practical;
- stable application-owned IDs and serialization;
- migration paths that can be incremental if the `$0` constraint is later relaxed or a provider becomes unsuitable.

Do not introduce unnecessary provider abstraction layers merely for hypothetical portability. Preserve practical migration seams without building an enterprise framework.

## 5. Repository visibility and secret hygiene

The GitHub repository is **intentionally public**. This is expected project configuration and is **not** a security/privacy discrepancy.

Any older active or historical wording that treats `private: false` or public repository visibility as an unexpected discrepancy is superseded by this decision.

Public visibility does not weaken secret-handling rules:

- never commit database credentials;
- never commit provider API/admin/deployment tokens;
- never commit bearer/access/refresh/session tokens;
- never commit private keys or confidentiality-dependent signing material;
- never place secrets in durable documentation, issues, PR bodies or fixtures;
- use provider secret stores and ignored local configuration as appropriate.

## 6. Owner technical profile and guidance contract

The owner is:

- technically oriented;
- experienced as a heavy/power user;
- able to understand programming concepts and perform substantial hands-on work;
- interested in learning and understanding what is being configured or changed;
- **not** a professional programmer/developer and should not be treated as one.

Therefore owner-facing instructions must normally:

1. start with a plain-language explanation of **what we are doing and why**;
2. introduce the real technical term when useful, but explain it rather than hiding it;
3. give explicit, ordered actions for account dashboards, command lines, IDEs or devices;
4. state what the owner should expect to see after important steps;
5. state any meaningful stop condition, risk or irreversible consequence before the action;
6. explain what information is safe to share and what is a secret when credentials/configuration are involved;
7. use small ASCII diagrams, flow diagrams, wireframes or before/after sketches when spatial/system relationships are easier to understand visually;
8. distinguish owner actions from agent/developer implementation work;
9. teach enough that the owner understands the system rather than merely copying opaque commands;
10. avoid patronizing language and avoid assuming professional developer fluency.

Example preferred explanation style:

```text
Android app
   |  sends login token
   v
Cloudflare Worker
   |  verifies identity + applies campaign rules
   v
Neon PostgreSQL
```

Then explain each arrow and only afterward provide the concrete setup steps.

## 7. Delegation remains unchanged

This guidance contract does **not** mean the owner should be asked to make routine engineering choices.

Agents still autonomously decide ordinary implementation details such as schema mechanics, class decomposition, test structure, request models, migration commands and internal sync implementation when those choices do not create a material owner-level consequence.

Return to the owner for genuine product/scope behavior, cost/billing, security/privacy, meaningful lock-in, destructive behavior, external-account activation or manual/physical QA gates.

## 8. Consequence for the next project step

Before the first real Player↔Server hosted integration:

1. use current official provider documentation, not stale remembered pricing;
2. confirm the Cloudflare, Neon and Descope development setup satisfies this `$0` policy;
3. explain each owner action in the guidance style above;
4. create development/test resources only after the owner performs/authorizes the external-account actions;
5. run the early Cloudflare free-tier CPU/runtime proof before deepening client integration;
6. do not activate object storage yet.

This decision supersedes any older wording that merely says to "consider cost" without recording `$0` as the controlling budget.