# Project State — global repository navigation

**Last verified:** 2026-09-16 (Chile local time)  
**Owner integrated-MVP implementation authorization:** **GRANTED**  
**Normal integrated trunk:** `main`  
**Verified integrated main before current package:** `587a000dae7ff9b9f997dd138b0ebbaeca201256`  
**Hosted DEV provider activation:** **COMPLETE / VERIFIED**  
**Android hosted-session integration:** **COMPLETE / OWNER-PHYSICAL PASS**  
**Hosted campaign/PC integration and convergence:** **COMPLETE / OWNER-PHYSICAL PASS**  
**Membership revoke + Player/DM authorization:** **AUTOMATED VERIFIED / OWNER-PHYSICAL PASS**  
**Current completion checkpoint:** `docs/checkpoints/2026-09-16_MEMBERSHIP_REVOKE_AUTHORIZATION_PHYSICAL_QA_COMPLETE.md`  
**Current focused branch:** `wave4/membership-revoke-authorization`  
**Current PR:** #41 — closure/merge pending

## 1. Current authority/topology

`main` is the single normal integrated-MVP development trunk.

Old Player and convergence lines remain historical/frozen evidence. New work uses short-lived outcome-oriented branches from current verified `main` and reintegrates early. Do not recreate permanent Player/Server/Desktop silos.

`docs/checkpoints/LATEST.md` controls the practical resume point. `docs/BRANCH_STATUS.md` controls branch lifecycle.

## 2. Current verified implementation baseline

The integrated foundation includes:

- account/global identity;
- Campaign;
- Membership + campaign role;
- PC owner vs current controller;
- stable object identities;
- monotonic revisions and stale-write semantics;
- tombstones/non-resurrection semantics;
- durable local hosted outbox;
- authenticated hosted account/campaign bootstrap;
- explicit membership lifecycle (`ACTIVE`, `KICKED`, `BANNED`);
- hosted PC snapshot read/write;
- active-membership plus DM-or-owner/controller server-side PC authorization;
- mutation idempotency and optimistic revisions;
- local-first PC delivery/reconciliation;
- equal-revision/local-ahead/tombstone convergence guards;
- explicit reviewed conflict resolution;
- permanent hosted-sync QA diagnostics.

## 3. Hosted DEV activation — complete

Verified DEV architecture remains:

```text
Android / Desktop clients
        |
        v
Cloudflare Worker/API <---- Descope identity proof
        |
        v
Neon PostgreSQL
```

The existing Neon `dnd-custom-aid-dev`, Descope DEV project and Cloudflare Worker remain the current DEV resources. Native clients do not hold database credentials.

## 4. Completed Wave 4 Player <-> Server path

Completed and physically verified where applicable:

1. remembered Android Descope session/token acquisition;
2. token delivery through the existing `HostedAccessTokenProvider` seam;
3. owner-facing hosted account/campaign bootstrap;
4. local-first campaign creation + durable hosted delivery;
5. PC snapshot push/pull and blocked-row recovery;
6. unchanged-sync/no-op behavior;
7. second-client observation and offline/concurrent edit convergence;
8. explicit owner-controlled keep-local conflict resolution with hosted compare-and-swap protection;
9. membership revoke/reinstate hosted eligibility behavior with non-destructive local preservation.

The convergence gate proved no-silent-overwrite multi-client behavior. The current membership gate proved a real DEV DM membership could move `ACTIVE -> KICKED -> ACTIVE`, stop hosted PC eligibility while inactive, preserve local campaign/PC data, and resume cleanly after reinstatement.

Physical membership QA specifically exercised DM lifecycle revoke/reinstate. Player owner/controller revoke is covered by the automated PostgreSQL authorization contract and must not be described as a physical Player revoke test.

## 5. Current implementation boundary

The membership revoke + Player/DM authorization validation package is complete in substance and awaiting normal PR #41 integration closure.

Approved semantics now have both automated and proportionate physical evidence:

- inactive membership stops hosted access/sync;
- local cached campaign/PC data is not silently wiped;
- account identity, membership, campaign role, PC ownership and PC current control remain distinct;
- DM campaign authority does not imply PC ownership;
- Player PC authority is constrained by owner/controller identity;
- hosted authorization fails closed when access is invalid;
- local-first data preservation, stale revisions, mutation idempotency, tombstones/non-resurrection and no-silent-overwrite semantics remain protected.

No new owner/manual action is required to close PR #41 unless CI or merge state reveals a material issue.

Do not extend this validation package into generalized RBAC/ACL or final Campaign Manager moderation UI.

## 6. Controlling integrated-MVP product direction

The approved product remains one ecosystem:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Paper-first Player play, local-first saves, bounded project-specific sync, complete DM Desktop fallback, combat authority resume/handoff, object storage/media, recovery/backup and official-SRD clarification remain protected integrated-MVP direction.

## 7. Protected integrated-MVP scope

Protected MVP scope continues to include Player hosted/shared integration, project-specific sync semantics, object storage/media, complete DM live Android/Desktop capability, combat authority resume/handoff, authoring Managers, structured homebrew/import-export, PC audit/correction, cross-surface PC Sheet PDF export, Campaign/System Administration, audit/recovery/full backup and official-SRD clarification.

The project remains intentionally not a VTT, automatic legality/rules engine, generalized sync platform, marketplace/social product or enterprise infrastructure exercise.

## 8. Current technical direction

The approved technical direction remains:

- Kotlin/Compose Android + Kotlin/Compose Multiplatform Desktop;
- SQLDelight/SQLite local persistence;
- TypeScript Cloudflare Worker API;
- Neon PostgreSQL hosted persistence;
- Descope identity proof with application-owned authorization;
- Ktor Client shared Android/Desktop HTTP;
- versioned `/v1` HTTP/JSON API;
- client mutation UUIDs + optimistic revisions;
- durable SQLDelight outbox + scoped push/pull synchronization;
- explicit hosted SQL migrations and contract validation;
- hosted PC current state as versioned JSONB snapshot plus relational authorization/revision/lifecycle metadata;
- versioned app-owned JSON import/export;
- versioned on-demand backup archives with manifest/integrity data;
- one canonical PC/export snapshot + shared PDF-export semantics with platform-specific rendering.

Object storage remains required for MVP but provider selection/activation is deferred until Media/Handouts/assets reach real integration. Do not assume R2 by inertia.

## 9. External-service and security state

D-0075 remains controlling:

- hard external-service operating budget = **USD $0** unless owner changes it;
- repository visibility is intentionally public;
- never commit secrets;
- paid plans/overage/billing commitments require explicit owner approval.

Important security residuals carried forward:

- review JWT/fail-closed verification robustness;
- continue object-level authorization regression coverage where new objects/features require it;
- maintain SQL/query safety;
- prevent token/secret leakage through errors/logs;
- review replay/idempotency authorization;
- inspect the locally reported **3 high severity npm vulnerabilities** before remediation; never run `npm audit fix --force` blindly;
- evaluate a dedicated least-privilege Neon runtime role;
- reassess identity/production configuration before release.

## 10. Release/acceptance status

The project remains development/debug and is not release-ready.

Hosted provider activation, Android hosted-session, account/campaign bootstrap, campaign/PC delivery, multi-client convergence and the bounded membership revoke/reinstate gate have passed their recorded verification/manual boundaries. This does not retroactively convert unrelated historical Player QA into a PASS.

## 11. Resume rule

For exact continuation, read:

1. `docs/checkpoints/LATEST.md`;
2. `docs/BRANCH_STATUS.md`;
3. `docs/checkpoints/2026-09-16_MEMBERSHIP_REVOKE_AUTHORIZATION_PHYSICAL_QA_COMPLETE.md`;
4. predecessor convergence evidence only as needed.

Close PR #41 through exact-head CI, merge and post-merge verification. After integration, stop before final moderation/Campaign Manager UI unless a separate package is explicitly scoped.
