# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified integrated main:** `587a000dae7ff9b9f997dd138b0ebbaeca201256`  
**Post-merge Scaffold:** `35116690562` — **SUCCESS**  
**Current focused branch:** `wave4/membership-revoke-authorization`  
**Current PR:** #41  
**Automated package head:** `a188417f8173573271346246e5cc129dabdf45cc`  
**Automated Scaffold:** `35118236579` — **SUCCESS**  
**Current package:** membership revoke + Player/DM authorization  
**Current gate:** **OWNER PHYSICAL QA / MANUAL DEV MEMBERSHIP REVOKE**  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md` — mandatory project operating rules;
2. `docs/checkpoints/2026-09-16_MEMBERSHIP_REVOKE_AUTHORIZATION_READY_FOR_PHYSICAL_QA.md` — **current exact gate and owner action**;
3. this file — global practical resume map;
4. `docs/PROJECT_STATE.md` — current global implementation state;
5. `docs/BRANCH_STATUS.md` — current branch lifecycle;
6. `docs/checkpoints/2026-09-16_MULTI_CLIENT_PC_CONVERGENCE_PHYSICAL_QA_COMPLETE.md` — immediately preceding completed convergence evidence;
7. `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md` — controlling `$0`, public-repository and secret/guidance policy.

If older operational prose conflicts with this file or the current specific checkpoint, the newer current checkpoint controls.

## Current Wave 4 state

```text
remembered Android Descope session/token             COMPLETE / OWNER-PHYSICAL PASS
owner-facing hosted account/campaign bootstrap       COMPLETE / OWNER-PHYSICAL PASS
campaign create + durable hosted delivery            COMPLETE / OWNER-PHYSICAL PASS
PC snapshot push/pull + blocked-row recovery         COMPLETE / OWNER-PHYSICAL PASS
unchanged-sync/no-op confirmation                    COMPLETE / OWNER-PHYSICAL PASS
multi-client PC convergence safety                    COMPLETE / OWNER-PHYSICAL PASS
PR #40 merge + post-merge main                       COMPLETE / VERIFIED
        |
        v
membership revoke + Player/DM authorization          AUTOMATED VERIFIED
        |
        v
REAL DEV REVOKE / REINSTATE PHYSICAL GATE            CURRENT OWNER ACTION
```

## Current package findings

Repository inspection established that the core authorization implementation already exists:

- hosted PC reads require ACTIVE membership plus DM-or-owner/controller authority;
- hosted PC writes independently re-check the same boundary;
- `ACTIVE`, `KICKED`, and `BANNED` are already explicit membership lifecycle states;
- client bootstrap stores explicit inactive state without deleting the local campaign;
- inactive campaigns are excluded from hosted PC synchronization eligibility;
- non-transient hosted authorization failure preserves durable local/outbox work rather than deleting it.

The package therefore added verification rather than replacing the existing permission architecture.

New automated evidence at `a188417f8173573271346246e5cc129dabdf45cc` proves dynamic ACTIVE -> KICKED/BANNED revocation for Player owner/controller authority, independent ACTIVE DM authority, DM revoke, hosted PC preservation, and stable HTTP `403 FORBIDDEN` behavior. Scaffold `35118236579` passed all database/backend/Kotlin/Android/Desktop checks.

## Exact next owner action

Do **not** change Neon yet.

On the currently configured Android test device:

1. open `DnD Aid - QA DEV`;
2. tap `Ejecutar sincronización QA`;
3. tap `Copiar log QA`;
4. paste the complete log into the technical-assistant chat.

This is a preflight only. Do not uninstall, clear app data, edit the PC, clear the outbox, or change membership state yet.

After the preflight is reviewed, the assistant will provide a read-only Neon query first. The actual reversible `ACTIVE -> KICKED -> ACTIVE` DEV membership change will be performed only after the exact row is identified.

## Completed convergence package

PR #40 merged into `main` as `587a000dae7ff9b9f997dd138b0ebbaeca201256`; post-merge Scaffold `35116690562` completed **SUCCESS**. Its multi-client convergence physical gate remains OWNER-PHYSICAL PASS. Do not rerun it unless later behavior changes touch convergence/conflict-resolution code.

## Permanent safety rules

- Repository is intentionally public under D-0075.
- External-service operating budget remains **USD $0** unless explicitly changed.
- Never expose database credentials, provider tokens, private keys, authorization headers or session/refresh tokens.
- Do not reset local databases/app data, clear outboxes, delete PCs/campaigns, or reinstall merely to make QA pass.
- Membership, campaign role, PC ownership and current control remain distinct.
- DM authority does not imply PC ownership.
- Preserve stale-revision, idempotency, tombstone/non-resurrection and no-silent-overwrite guarantees.
- Final Kick/Ban product UI and Campaign Manager administration remain later packages; do not pull them into this validation PR.

## Historical Player evidence remains bounded

Current integrated CI and hosted physical proofs do not retroactively establish physical owner acceptance of the historical frozen Player candidate. Preserve historical QA evidence for exactly what it tested.
