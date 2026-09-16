# Project State — global repository navigation

**Last verified:** 2026-09-16 (Chile local time)  
**Owner integrated-MVP implementation authorization:** **GRANTED**  
**Normal integrated trunk:** `main`  
**Verified integrated main:** `587a000dae7ff9b9f997dd138b0ebbaeca201256`  
**Post-merge Scaffold:** `35116690562` — **SUCCESS**  
**Hosted DEV provider activation:** **COMPLETE / VERIFIED**  
**Android hosted-session integration:** **COMPLETE / OWNER-PHYSICAL PASS**  
**Hosted campaign/PC integration and convergence:** **COMPLETE / OWNER-PHYSICAL PASS**  
**Current implementation checkpoint:** `docs/checkpoints/2026-09-16_MULTI_CLIENT_PC_CONVERGENCE_PHYSICAL_QA_COMPLETE.md`  
**Current focused package:** membership revoke + Player/DM authorization

## 1. Current authority/topology

`main` is the single normal integrated-MVP development trunk.

The old Player successor and convergence lines remain historical/frozen evidence. New work uses short-lived outcome-oriented branches from current verified `main` and reintegrates early. Do not recreate permanent Player/Server/Desktop silos.

Current focused branch:

`wave4/membership-revoke-authorization`

`docs/checkpoints/LATEST.md` controls the practical resume point. `docs/BRANCH_STATUS.md` controls branch lifecycle.

## 2. Current verified implementation baseline

Wave 2 — **Shared Integrated-MVP Spine** — is complete and integrated, including:

- account/global identity;
- Campaign;
- Membership + campaign role;
- PC owner vs current controller;
- stable object identities;
- monotonic revisions and stale-write semantics;
- tombstones/non-resurrection semantics;
- Personal/Campaign/System/Official scope models where valid;
- independent-copy provenance;
- local sync metadata and invariant tests.

Provider-neutral hosted foundation is integrated and includes:

- hosted `/v1` API/auth/domain foundations;
- explicit PostgreSQL schema/migrations/contracts and PostgreSQL CI validation;
- shared Android/Desktop Ktor transport;
- provider-neutral token acquisition/verification seams;
- durable SQLDelight hosted outbox;
- local-first campaign creation + hosted delivery using stable mutation identity;
- authenticated hosted account/campaign bootstrap;
- explicit campaign membership lifecycle reconciliation (`ACTIVE`, `KICKED`, `BANNED`) and campaign soft-deletion state;
- hosted PC current-state snapshot read/write using versioned app-owned Player serialization as JSONB;
- server-side PC authorization using active membership plus DM authority or Player owner/controller authority;
- optimistic PC revisions, mutation idempotency, stale-write rejection and tombstone handling;
- durable PC snapshot outbox delivery with authoritative revision acknowledgement;
- same-identity hosted reconciliation distinct from user-facing backup restore-as-copy;
- guards against equal-revision overwrite, local-ahead overwrite and tombstone resurrection;
- non-destructive local recovery when hosted PC state is deleted.

PR #40 completed the current multi-client convergence layer with permanent QA diagnostics, narrow missing-baseline equal-state recovery and explicit reviewed keep-local conflict resolution. It merged to `main` as `587a000dae7ff9b9f997dd138b0ebbaeca201256`; post-merge Scaffold `35116690562` completed **SUCCESS**.

## 3. Hosted DEV activation — complete

The first real external-provider activation is complete and verified.

Verified DEV architecture:

```text
Android / Desktop clients
        |
        v
Cloudflare Worker/API <---- Descope identity proof
        |
        v
Neon PostgreSQL
```

Current DEV resources remain the existing Neon `dnd-custom-aid-dev`, Descope DEV project and Cloudflare Worker recorded in the provider activation checkpoint. The Worker runtime uses server-side provider secret storage; native clients do not hold database credentials.

Verified provider evidence includes real Neon migration/contracts, real email OTP authentication, deployed Worker health/authenticated `/v1/me`, application identity persistence through Neon and representative Workers Free CPU/runtime proof for the tested authenticated path.

See `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md` for exact environment evidence.

## 4. Completed Wave 4 Player <-> Server path so far

Completed and physically verified where applicable:

1. remembered Android Descope session/token acquisition;
2. token delivery through the existing `HostedAccessTokenProvider` seam;
3. owner-facing hosted account/campaign bootstrap;
4. local-first campaign creation + durable hosted delivery;
5. PC snapshot push/pull and blocked-row recovery;
6. unchanged-sync/no-op behavior;
7. second-client observation and offline/concurrent edit convergence;
8. explicit owner-controlled keep-local conflict resolution with hosted compare-and-swap protection.

The convergence physical gate proved that concurrent divergence does not silently overwrite either side, that explicit reviewed keep-local can safely advance the server revision, and that a clean second client subsequently converges to the selected hosted state.

Exact evidence:

`docs/checkpoints/2026-09-16_MULTI_CLIENT_PC_CONVERGENCE_PHYSICAL_QA_COMPLETE.md`

## 5. Current implementation boundary

Wave 4 — Player <-> Server end-to-end — remains active.

The current primary package is now:

> **membership revoke + Player/DM authorization**

Current focused branch:

`wave4/membership-revoke-authorization`

This package must first inspect the existing membership lifecycle/backend authorization/shared synchronization contracts and regression coverage. Implement only the missing end-to-end enforcement and verification needed to prove the approved behavior.

Approved semantics already exist and should not be reopened without concrete contradictory evidence:

- `ACTIVE`, `KICKED` and `BANNED` are meaningful membership lifecycle states;
- membership removal/revoke stops future hosted access/sync but does not silently wipe local cached data;
- account identity, membership, campaign role, PC ownership and PC current control are distinct;
- DM campaign authority does not imply PC ownership;
- Player PC access is constrained by owner/controller authority;
- hosted authorization must fail closed where access is no longer valid;
- local-first data preservation, stale revisions, mutation idempotency, tombstones/non-resurrection and no-silent-overwrite semantics remain protected.

No new owner decision is required for routine implementation. Return to the owner when a genuine product/security/cost/destructive-behavior ambiguity, external-account action or manual/physical QA gate is reached.

Do not introduce a generalized RBAC/ACL framework, reset databases, clear outboxes or wipe local cached data merely to make the package pass.

## 6. Controlling integrated-MVP product direction

The approved product remains one ecosystem:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Paper-first Player play, local-first saves, bounded project-specific sync, complete DM Desktop fallback, explicit combat authority resume/handoff, object storage/media, meaningful recovery/backup and official-SRD clarification remain protected integrated-MVP direction.

## 7. Protected integrated-MVP scope

Protected MVP scope continues to include Player hosted/shared integration, project-specific sync semantics, object storage/media, complete DM live Android/Desktop capability, combat authority resume/handoff, authoring Managers, structured homebrew/import-export, PC audit/correction, cross-surface PC Sheet PDF export, Campaign/System Administration, audit/recovery/full backup and official-SRD clarification.

The project remains paper-first and intentionally not a VTT, automatic legality/rules engine, generalized sync platform, marketplace/social product or enterprise infrastructure exercise.

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

Object storage remains required for the MVP but provider selection/activation is **deferred** until Media/Handouts/assets reach real integration. Do not assume R2 by inertia.

Workers AI remains a later approved official-SRD clarification direction only while it can be used safely under the hard `$0` policy.

## 9. External-service and security state

D-0075 remains controlling:

- hard external-service operating budget = **USD $0** unless owner changes it;
- repository visibility is intentionally public;
- never commit secrets;
- paid plans/overage/billing commitments require explicit owner approval.

Important security residuals carried forward:

- review JWT/fail-closed verification robustness;
- extend object-level authorization regression coverage;
- maintain SQL/query safety;
- prevent token/secret leakage through errors/logs;
- review replay/idempotency authorization;
- inspect the locally reported **3 high severity npm vulnerabilities** before remediation; never run `npm audit fix --force` blindly;
- evaluate a dedicated least-privilege Neon runtime role instead of the current project-owner runtime credential;
- reassess Descope region/settings before production release.

Completion of current gates is not a claim that security work is permanently finished.

## 10. Owner/local development notes

Owner project root: `D:\DnD_Aid`  
Local clone: `D:\DnD_Aid\repo\dnd_custom_aid`  
Owner credential file: `D:\DnD_Aid\dnd_custom_aid_dev_credentials.md`

The credential file is intentionally outside Git and plaintext by explicit owner choice. Do not read/copy/commit its contents or replace the workflow with a vault/password-manager migration unless requested.

A local Windows SChannel issue prevents PowerShell `Invoke-RestMethod`/Windows `curl.exe` from negotiating TLS with the Worker URL on the owner's machine, while Node `fetch()` and Vivaldi work. Treat Node/browser as the known-good local endpoint-test path unless the Windows TLS issue is separately investigated.

## 11. Release/acceptance status

The project remains development/debug and is not release-ready.

Hosted provider activation, Android hosted-session, account/campaign bootstrap, campaign/PC delivery and the bounded multi-client convergence gate have passed their recorded verification/manual boundaries. This does not retroactively convert unrelated historical Player QA into a PASS.

Historical frozen Player candidate remains `0.4.0-preqa.13 / 41300` at `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`; preserve its historical evidence for exactly what it tested.

## 12. Resume rule

For exact continuation, read:

1. `docs/checkpoints/LATEST.md`;
2. `docs/BRANCH_STATUS.md`;
3. `docs/checkpoints/2026-09-16_MULTI_CLIENT_PC_CONVERGENCE_PHYSICAL_QA_COMPLETE.md` for predecessor evidence;
4. the current membership-revoke/authorization package files/tests as they are created.

Continue on `wave4/membership-revoke-authorization`. Do not restart provider/session/bootstrap/PC-convergence work. The next owner interaction should occur only when the package reaches a genuine manual/physical QA or owner-decision boundary.
