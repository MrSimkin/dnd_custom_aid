# Branch status and repository-ordering map

**Updated:** 2026-09-16 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Verified `main`:** `587a000dae7ff9b9f997dd138b0ebbaeca201256`  
**Post-merge Scaffold:** `35116690562` — **SUCCESS**  
**Current focused branch:** `wave4/membership-revoke-authorization`  
**Current PR:** #41  
**Starting `main` for current package:** `587a000dae7ff9b9f997dd138b0ebbaeca201256`  
**Automated verified head:** `a188417f8173573271346246e5cc129dabdf45cc`  
**Scaffold:** `35118236579` — **SUCCESS**  
**Current gate:** owner physical real-DEV revoke/reinstate QA

This file is the canonical branch-lifecycle map. Branch existence alone never establishes authority.

## 1. `main` — sole normal integrated-MVP trunk

`main` remains the sole normal development trunk for integrated-MVP work.

Normal implementation should start from verified remote `main`, use short-lived outcome-oriented branches, merge only after proportionate verification, verify post-merge `main`, and refresh durable checkpoints when operational truth changes.

Do not create permanent Player/Server/Desktop silos or months-long catch-all integration branches.

## 2. Completed convergence package

PR #40 completed permanent hosted-sync QA diagnostics, safe legacy baseline equal-state recovery, explicit reviewed keep-local conflict resolution and the bounded multi-client physical gate.

It merged to `main` as:

`587a000dae7ff9b9f997dd138b0ebbaeca201256`

Post-merge Scaffold `35116690562` completed **SUCCESS**. Multi-client convergence remains **OWNER-PHYSICAL PASS**.

Completion evidence:

`docs/checkpoints/2026-09-16_MULTI_CLIENT_PC_CONVERGENCE_PHYSICAL_QA_COMPLETE.md`

## 3. Current focused branch / PR

Branch:

`wave4/membership-revoke-authorization`

PR:

`#41 — test: prove membership revoke and PC authorization boundaries`

Base:

`587a000dae7ff9b9f997dd138b0ebbaeca201256`

Automated behavior/test head:

`a188417f8173573271346246e5cc129dabdf45cc`

Scaffold:

`35118236579` — **SUCCESS**

This package is intentionally verification-focused. Repository inspection found that production hosted PC authorization and explicit inactive membership preservation already satisfy the approved core rules, so the package adds missing transition evidence rather than a new permission architecture.

New evidence proves:

- ACTIVE Player owner/controller -> KICKED removes hosted PC read/write authority;
- revoke does not delete/rewrite the hosted PC or ownership/control;
- independently ACTIVE DM authority still works after Player revoke;
- BANNED remains inactive;
- revoking the DM also removes DM authority;
- unauthorized PC PUT produces stable `403 FORBIDDEN` without hosted-state leakage;
- existing client regression preserves the local campaign when explicit KICKED state is received.

## 4. Current manual gate

The current checkpoint is:

`docs/checkpoints/2026-09-16_MEMBERSHIP_REVOKE_AUTHORIZATION_READY_FOR_PHYSICAL_QA.md`

PR #41 must remain open until this physical gate is reviewed.

Exact immediate owner action is preflight only:

1. open `DnD Aid - QA DEV` on the configured Android test device;
2. run `Ejecutar sincronización QA`;
3. use `Copiar log QA`;
4. paste the complete log into the technical-assistant chat.

Do not change Neon membership yet. After preflight review, use a read-only SQL discovery query before any reversible membership update.

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

Do not clear outbox state, reset databases, uninstall/reinstall, delete local PCs/campaigns or expose credentials merely to make QA pass.

## 6. Scope boundary

This PR does not implement final DM Kick/Ban UI, invitation/rejoin UX, Campaign Manager administration, generalized access-control infrastructure, or unrelated Wave 5 work.

Those remain later product packages.

## 7. Historical branches

`implementation/phase4a-successor-cycle` remains historical/frozen Player evidence. `integration/mvp-baseline-convergence` remains historical convergence evidence. Other frozen refs remain evidence only unless explicitly reactivated.

Do not force-move or repurpose historical refs.

## 8. Security / repository visibility

The repository is intentionally public under D-0075. Never store provider/database credentials, bearer/session tokens, private keys or other confidentiality-dependent material in Git or QA logs.
