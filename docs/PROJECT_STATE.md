# Project State — global repository navigation

**Last verified:** 2026-09-16 (Chile local time)  
**Owner integrated-MVP implementation authorization:** **GRANTED**  
**Normal integrated trunk:** `main`  
**Verified current main / Wave 5 package base:** `fb113909cb53b2463bd643cae7d2f54f0673fec4`  
**Current focused branch:** `wave5/campaign-membership-administration-core`  
**Current PR:** #43  
**Verified implementation head:** `fcaa533f79332f6a2f13fb06b7f1bb889dd1982c`  
**Verified implementation Scaffold:** `35140381721` — **SUCCESS**  
**Current package checkpoint:** `docs/checkpoints/2026-09-16_CAMPAIGN_MEMBERSHIP_ADMINISTRATION_CORE_AUTOMATED_VERIFIED.md`

## 1. Current authority/topology

`main` is the single normal integrated-MVP trunk. New work uses short-lived outcome-oriented branches and reintegrates after proportionate verification. Historical Player/convergence/Wave 4 refs remain evidence rather than parallel trunks.

`docs/checkpoints/LATEST.md` controls the practical resume point. `docs/BRANCH_STATUS.md` controls branch lifecycle.

The previous Wave 5 branch `wave5/desktop-workbench-shell` is completed/merged. The current package is PR #43 on `wave5/campaign-membership-administration-core`.

## 2. Integrated foundation

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
- explicit reviewed conflict resolution;
- permanent hosted-sync QA diagnostics;
- persistent Desktop local SQLite campaign context and workbench shell;
- hosted Campaign Administration member-roster/moderation contract in the current verified PR.

## 3. Hosted DEV state

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

The Android hosted path is physically verified through Wave 4. Native clients do not hold database credentials. The hard external-service operating budget remains USD $0 unless the owner changes it.

Desktop currently has local persistence but does **not** yet have hosted provider/session acquisition or hosted synchronization.

The current Campaign Administration routes are repository/API-contract verified but are **not yet claimed as deployed** to the real DEV Worker; this repository has no automatic Worker deployment workflow.

## 4. Wave 4 — complete

Wave 4 Player <-> Server is complete and integrated for the recorded scope, including remembered authentication, campaign bootstrap/delivery, PC push/pull, multi-client convergence, explicit conflict resolution and membership revoke/reinstate authorization behavior.

PR #41 merged as `6f7165e6e5ae56a4b1985f037a656bf527b94d01`; post-merge Scaffold `35123027446` passed.

Physical membership QA specifically exercised DM `ACTIVE -> KICKED -> ACTIVE`; Player owner/controller revoke remains automated-contract evidence, not physical Player evidence.

## 5. Wave 5 — active

### 5.1 Desktop shell/local campaign — complete

PR #42 merged as:

`fb113909cb53b2463bd643cae7d2f54f0673fec4`

Post-merge Scaffold `35138029472` — **SUCCESS**.

Owner Windows Desktop QA passed the workbench shell, local campaign persistence/context, Spanish product language, persistent Application Settings, QA/diagnostic copy surface and honest deferred placeholders.

### 5.2 Hosted Campaign membership administration core — automated verified

PR #43 implements:

- ACTIVE-DM-only hosted member roster;
- member application user ID/display name/role/lifecycle projection;
- Player `KICK`, `BAN`, `LIFT_BAN` actions;
- `LIFT_BAN` as `BANNED -> KICKED`, with invitation/rejoin later owning `KICKED -> ACTIVE`;
- idempotent/no-op moderation;
- campaign revision only for actual lifecycle changes;
- no membership-row/PC/owner-controller destruction;
- provider-neutral Shared hosted Campaign Administration client;
- focused backend/database/Shared verification.

Exact implementation head `fcaa533f79332f6a2f13fb06b7f1bb889dd1982c` passed Scaffold `35140381721`.

No owner/manual gate is required for this backend/shared-only package.

## 6. Current gate / continuation

Current gate:

1. final documentation-only PR #43 head must pass Scaffold;
2. if head remains unchanged, merge PR #43;
3. verify post-merge `main` Scaffold.

If those steps are already complete when this file is read, treat PR #43 as integrated and continue from current `main` with the next bounded Wave 5 Desktop-hosted consumer package: Desktop hosted authentication/session acquisition + real Campaign Administration consumption, including explicit DEV Worker deployment/authenticated integration when the new routes are first needed.

Do not let the earlier PC-sync clarification reorder the approved sequence.

## 7. Current Wave 5 boundaries

Not part of PR #43:

- invitations/join/rejoin;
- co-DM moderation/role editing;
- Desktop provider/session acquisition or full hosted sync;
- final Desktop Campaign Administration member UI;
- PC ownership/control assignment shortcuts;
- PC Manager/Audit;
- live combat authority/resume/handoff;
- substantive authoring Managers;
- Media/Handouts/object-storage activation;
- System Administration;
- backup/export/PDF hardening;
- generalized RBAC/ACL.

D-0072 controls the Desktop workbench/product direction. D-0073 controls integrated-MVP sequencing.

## 8. Owner-approved next Desktop settings enhancement

When the next genuine Desktop feature build occurs:

- expand the Desktop font catalogue to Android-equivalent choices where technically appropriate;
- replace plain Theme and Font selectors with Android-like preview cards/forms so the result can be previewed before selection.

The durable detail is in `docs/technical/DESKTOP_APPLICATION_SETTINGS_FOLLOWUPS.md`.

## 9. Controlling integrated-MVP direction

The approved product remains one ecosystem:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Paper-first Player play, local-first saves, bounded project-specific sync, complete DM Desktop fallback, combat authority resume/handoff, object storage/media, meaningful recovery/backup and official-SRD clarification remain protected integrated-MVP direction.

The project remains intentionally not a VTT, automatic legality/rules engine, generalized sync platform, marketplace/social product or enterprise infrastructure exercise.

## 10. Technical direction

- Kotlin/Compose Android + Kotlin/Compose Multiplatform Desktop;
- SQLDelight/SQLite local persistence;
- TypeScript Cloudflare Worker API;
- Neon PostgreSQL hosted persistence;
- Descope identity proof with application-owned authorization;
- Ktor Client shared Android/Desktop HTTP;
- versioned `/v1` HTTP/JSON API;
- client mutation UUIDs + optimistic revisions where retry identity is required;
- durable SQLDelight outbox + scoped push/pull synchronization;
- explicit hosted SQL migrations/contracts;
- versioned app-owned JSON import/export;
- one canonical PC/export snapshot + shared PDF-export semantics with platform rendering.

Object storage remains required for MVP but provider activation remains deferred until Media/Handouts/assets reach real integration.

## 11. Security / operating residuals

D-0075 remains controlling: repository intentionally public, hard USD $0 external-service budget, never commit secrets, and paid/overage commitments require explicit owner approval.

Carry forward JWT/fail-closed review, object-level authorization regression coverage, SQL/query safety, token/secret leakage prevention, replay/idempotency authorization review, dependency-vulnerability investigation without blind `npm audit fix --force`, least-privilege Neon runtime-role evaluation, and production identity/configuration review before release.

## 12. QA environment

Current owner-reported Android QA inventory is six instances:

- phone-class: 1 physical Redmi Note 11 Pro 5G + 2 emulators;
- tablet-class: 1 physical Lenovo Tab P11 TB-J606F, 6 GB RAM, Android 11 + 2 emulators.

The Windows Desktop QA workstation/toolchain is recorded in `docs/TEST_DEVICES.md`.

## 13. Release/acceptance status

The project remains development/debug and is not release-ready.

Wave 4 recorded hosted/manual gates are complete. The first Wave 5 Desktop package is owner-accepted and integrated. PR #43 is automated verified and requires only final documentation-head CI, merge and post-merge CI; it has no owner/manual acceptance gate.

## 14. Resume rule

Read, in order:

1. `docs/checkpoints/LATEST.md`;
2. `docs/BRANCH_STATUS.md`;
3. `docs/checkpoints/2026-09-16_CAMPAIGN_MEMBERSHIP_ADMINISTRATION_CORE_AUTOMATED_VERIFIED.md`;
4. D-0072 and D-0073 as needed;
5. the completed Desktop-shell and membership-revoke checkpoints for predecessor evidence.

Continue PR #43 through exact-head CI/merge/post-merge verification. If it is already integrated, start the next bounded Wave 5 package from current `main`.