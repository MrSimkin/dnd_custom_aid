# Branch status and repository-ordering map

**Updated:** 2026-09-16 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Verified integrated behavior main:** `6f7165e6e5ae56a4b1985f037a656bf527b94d01`  
**Post-merge Scaffold:** `35123027446` — **SUCCESS**  
**Current focused branch:** none  
**PR #41:** **MERGED / CLOSED BY INTEGRATION**  
**Current lifecycle state:** membership revoke + Player/DM authorization package complete

This file is the canonical branch-lifecycle map. Branch existence alone never establishes authority.

## 1. `main` — sole normal integrated-MVP trunk

`main` remains the sole normal development trunk for integrated-MVP work.

Normal implementation should start from verified remote `main`, use short-lived outcome-oriented branches, merge only after proportionate verification, verify post-merge `main`, and refresh durable checkpoints when operational truth changes.

Do not create permanent Player/Server/Desktop silos or months-long catch-all integration branches.

## 2. Completed convergence package

PR #40 completed permanent hosted-sync QA diagnostics, safe legacy baseline recovery, explicit reviewed keep-local conflict resolution and bounded multi-client convergence. It merged as `587a000dae7ff9b9f997dd138b0ebbaeca201256`; post-merge Scaffold `35116690562` passed.

## 3. Completed membership revoke / authorization package

PR:

`#41 — test: prove membership revoke and PC authorization boundaries`

Automated behavior/test head:

`a188417f8173573271346246e5cc129dabdf45cc`

Automated Scaffold:

`35118236579` — **SUCCESS**

Physical completion evidence:

`docs/checkpoints/2026-09-16_MEMBERSHIP_REVOKE_AUTHORIZATION_PHYSICAL_QA_COMPLETE.md`

The real DEV physical gate exercised the exact DM membership for `Hosted Batch Test` through `ACTIVE -> KICKED -> ACTIVE`. While inactive, Android stopped hosted PC eligibility/sync while preserving local campaign/PC data. After reinstatement, eligibility resumed cleanly with no conflicts or pending outbox work.

Physical coverage specifically exercised DM lifecycle revoke/reinstate. Player owner/controller revoke remains automated contract evidence.

Closure documentation head `8335112cc9721a32b66e294e00c72ccdd7f75b7d` passed Scaffold `35122878536`.

PR #41 merged into `main` as:

`6f7165e6e5ae56a4b1985f037a656bf527b94d01`

Post-merge Scaffold:

`35123027446` — **SUCCESS**

This package is integrated and closed.

## 4. Current branch state

There is no active focused implementation branch opened by this closure.

The historical branch `wave4/membership-revoke-authorization` is completed evidence only. Do not continue adding unrelated work to it or treat its existence as an active resume point.

Start any next package from current verified `main` using a new short-lived outcome-oriented branch after its scope is established.

## 5. Safety and authorization invariants

Preserve:

- `ACTIVE`, `KICKED`, `BANNED` lifecycle semantics;
- membership revoke stopping future hosted access/sync without silently wiping local cached data;
- membership/role/ownership/current-control distinctions;
- DM campaign authority distinct from PC ownership;
- Player PC authority constrained to owner/controller;
- local-first data preservation on hosted authorization failure;
- stale-revision, mutation-idempotency, tombstone/non-resurrection and no-silent-overwrite semantics;
- project-specific authorization rather than generalized RBAC/ACL infrastructure.

## 6. Next product boundary

Final DM Kick/Ban UI, invitation/rejoin UX, Campaign Manager administration and broader moderation workflows are separate product packages.

Do not automatically reactivate `wave4/membership-revoke-authorization` or begin those features without a separately scoped continuation package.

## 7. Historical branches

`implementation/phase4a-successor-cycle` remains historical/frozen Player evidence. `integration/mvp-baseline-convergence` remains historical convergence evidence. Other completed/frozen refs remain evidence only unless explicitly reactivated.

Do not force-move or repurpose historical refs.

## 8. Security / repository visibility

The repository is intentionally public under D-0075. Never store provider/database credentials, bearer/session tokens, private keys or other confidentiality-dependent material in Git or QA logs.
