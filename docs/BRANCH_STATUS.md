# Branch status and repository-ordering map

**Updated:** 2026-09-16 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Verified `main` before current package:** `587a000dae7ff9b9f997dd138b0ebbaeca201256`  
**Current focused branch:** `wave4/membership-revoke-authorization`  
**Current PR:** #41  
**Starting `main` for current package:** `587a000dae7ff9b9f997dd138b0ebbaeca201256`  
**Automated verified head:** `a188417f8173573271346246e5cc129dabdf45cc`  
**Automated Scaffold:** `35118236579` — **SUCCESS**  
**Physical gate:** **OWNER-PHYSICAL PASS**  
**Current lifecycle state:** closure/merge pending exact documentation-head CI

This file is the canonical branch-lifecycle map. Branch existence alone never establishes authority.

## 1. `main` — sole normal integrated-MVP trunk

`main` remains the sole normal development trunk for integrated-MVP work.

Normal implementation should start from verified remote `main`, use short-lived outcome-oriented branches, merge only after proportionate verification, verify post-merge `main`, and refresh durable checkpoints when operational truth changes.

Do not create permanent Player/Server/Desktop silos or months-long catch-all integration branches.

## 2. Completed predecessor package

PR #40 completed permanent hosted-sync QA diagnostics, safe legacy baseline recovery, explicit reviewed keep-local conflict resolution and the bounded multi-client convergence gate.

It merged to `main` as `587a000dae7ff9b9f997dd138b0ebbaeca201256`; post-merge Scaffold `35116690562` completed **SUCCESS**. Multi-client convergence remains **OWNER-PHYSICAL PASS**.

## 3. Current focused branch / PR

Branch:

`wave4/membership-revoke-authorization`

PR:

`#41 — test: prove membership revoke and PC authorization boundaries`

Base:

`587a000dae7ff9b9f997dd138b0ebbaeca201256`

Automated behavior/test head:

`a188417f8173573271346246e5cc129dabdf45cc`

Automated Scaffold:

`35118236579` — **SUCCESS**

The package is intentionally verification-focused. Production hosted PC authorization and explicit inactive-membership preservation already satisfy the approved core rules, so this branch adds missing transition evidence rather than a new permission architecture.

Automated evidence proves:

- ACTIVE Player owner/controller -> KICKED removes hosted PC read/write authority;
- revoke does not delete/rewrite the hosted PC or ownership/control;
- independently ACTIVE DM authority still works after Player revoke;
- BANNED remains inactive;
- revoking the DM removes DM authority;
- unauthorized PC PUT produces stable `403 FORBIDDEN` without hosted-state leakage;
- existing client regression preserves local campaign data when explicit KICKED state is received.

## 4. Physical gate — PASS

Completion evidence:

`docs/checkpoints/2026-09-16_MEMBERSHIP_REVOKE_AUTHORIZATION_PHYSICAL_QA_COMPLETE.md`

The real DEV physical test used the exact DM membership for `Hosted Batch Test` and exercised:

`ACTIVE -> KICKED -> ACTIVE`

While KICKED, Android observed the explicit inactive lifecycle state, excluded the campaign from hosted PC eligibility, performed no PC synchronization, produced no conflicts/pending outbox work, and retained the local campaign and PC for local viewing.

After reinstatement, Android returned to active hosted eligibility with the PC unchanged, no conflicts and an empty outbox.

The physical gate specifically covered DM lifecycle revoke/reinstate. Player revoke is automated contract evidence, not physical evidence.

## 5. Current closure sequence

PR #41 must remain focused and may now close through the normal sequence:

1. verify the documentation-only closure head in Scaffold;
2. if green and unchanged, merge PR #41;
3. verify post-merge `main`;
4. refresh final closure bookkeeping on `main` if needed so the repository resume point does not remain stale.

No further owner/manual input is required for this package unless CI or merge state reveals a material problem.

## 6. Safety and authorization invariants

Preserve:

- `ACTIVE`, `KICKED`, `BANNED` lifecycle semantics;
- membership revoke stopping future hosted access/sync without silently wiping local cached data;
- membership/role/ownership/current-control distinctions;
- DM campaign authority distinct from PC ownership;
- Player PC authority constrained to owner/controller;
- local-first data preservation on hosted authorization failure;
- stale-revision, mutation-idempotency, tombstone/non-resurrection and no-silent-overwrite semantics;
- project-specific authorization rather than generalized RBAC/ACL infrastructure.

## 7. Scope boundary

PR #41 does not implement final DM Kick/Ban UI, invitation/rejoin UX, Campaign Manager administration, generalized access-control infrastructure, or unrelated Wave 5 work.

Those remain separate product packages. Do not automatically start them as part of PR #41 closure.

## 8. Historical branches

`implementation/phase4a-successor-cycle` remains historical/frozen Player evidence. `integration/mvp-baseline-convergence` remains historical convergence evidence. Other frozen refs remain evidence only unless explicitly reactivated.

Do not force-move or repurpose historical refs.

## 9. Security / repository visibility

The repository is intentionally public under D-0075. Never store provider/database credentials, bearer/session tokens, private keys or other confidentiality-dependent material in Git or QA logs.
