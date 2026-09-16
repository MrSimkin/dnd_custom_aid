# Project State — global repository navigation

**Last verified:** 2026-09-16 (Chile local time)  
**Owner integrated-MVP implementation authorization:** **GRANTED**  
**Normal integrated trunk:** `main`  
**Verified current main / Wave 5 package base:** `fb113909cb53b2463bd643cae7d2f54f0673fec4`  
**Current focused branch:** `wave5/campaign-membership-administration-core`  
**Current PR:** not opened yet  
**Current package checkpoint:** `docs/checkpoints/2026-09-16_CAMPAIGN_MEMBERSHIP_ADMINISTRATION_CORE_OPEN.md`

## 1. Current authority/topology

`main` is the single normal integrated-MVP trunk. New work uses short-lived outcome-oriented branches and reintegrates after proportionate verification. Historical Player/convergence/Wave 4 refs remain evidence rather than parallel trunks.

`docs/checkpoints/LATEST.md` controls the practical resume point. `docs/BRANCH_STATUS.md` controls branch lifecycle.

The previous Wave 5 branch `wave5/desktop-workbench-shell` is completed/merged and must not be treated as the active work branch.

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
- equal-revision/local-ahead/tombstone convergence guards;
- explicit reviewed conflict resolution;
- permanent hosted-sync QA diagnostics;
- persistent Desktop local SQLite campaign context and workbench shell.

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

The native Android hosted path is physically verified through Wave 4. Native clients do not hold database credentials. The hard external-service operating budget remains USD $0 unless the owner changes it.

Desktop currently has local persistence but does **not** yet have hosted provider/session acquisition or hosted synchronization.

## 4. Wave 4 — complete

Wave 4 Player <-> Server is complete and integrated for the recorded scope, including remembered authentication, campaign bootstrap/delivery, PC push/pull, blocked-row recovery, unchanged sync, multi-client convergence, explicit reviewed conflict resolution and membership revoke/reinstate authorization behavior.

PR #41 merged as `6f7165e6e5ae56a4b1985f037a656bf527b94d01`; post-merge Scaffold `35123027446` passed.

Physical membership QA specifically exercised DM `ACTIVE -> KICKED -> ACTIVE`; Player owner/controller revoke remains automated-contract evidence, not physical Player evidence.

## 5. Wave 5 — active

Wave 5 Desktop shell + Campaign Administration is active.

### 5.1 First package — complete

PR #42 established Desktop workbench shell + local campaign context and merged as:

`fb113909cb53b2463bd643cae7d2f54f0673fec4`

Post-merge Scaffold:

`35138029472` — **SUCCESS**.

Owner Windows Desktop QA passed for:

- workbench launch/close/navigation/layout;
- local campaign create/select/persistence;
- same active campaign context in Campaign Administration;
- Spanish product-language repair;
- persistent Desktop Application Settings;
- bounded QA/diagnostic selectable/copyable data;
- deferred destinations honestly presented as deferred.

The owner QA campaign remains:

- `QA Wave 5 - 2026-09-16`;
- `30609c9d-89f7-42ef-85dd-a7a35df3c506`.

### 5.2 Current package

Current branch:

`wave5/campaign-membership-administration-core`

Current bounded objective:

- authenticated active-DM-only hosted campaign member roster;
- explicit Player `KICK`, `BAN`, `LIFT_BAN` actions;
- preserve rows/data and apply lifecycle transitions without destructive cleanup;
- `LIFT_BAN` returns `BANNED -> KICKED`; valid invitation/rejoin later owns `KICKED -> ACTIVE`;
- campaign revision advances only for actual lifecycle changes;
- provider-neutral Shared contract for later Desktop consumption;
- focused automated backend/database/Shared verification.

This package is deliberately backend/shared-first. Desktop hosted auth/session acquisition and the final Campaign Administration member UI follow after these contracts exist.

## 6. Current Wave 5 boundaries

Not part of the current package:

- invitations/join/rejoin;
- co-DM moderation/role editing;
- Desktop provider/session acquisition or full hosted sync;
- PC ownership/control assignment shortcuts;
- PC Manager/Audit;
- live combat authority/resume/handoff;
- substantive authoring Managers;
- Media/Handouts/object-storage activation;
- System Administration;
- backup/export/PDF hardening;
- generalized RBAC/ACL.

D-0072 controls the Desktop workbench/product direction. D-0073 controls integrated-MVP sequencing.

## 7. Owner-approved next Desktop settings enhancement

When the next genuine Desktop feature build occurs:

- expand the Desktop font catalogue to Android-equivalent choices where technically appropriate;
- replace plain Theme and Font selectors with Android-like preview cards/forms so the result can be previewed before selection.

This is a durable Desktop UX requirement, not a reason to reorder the current dependency sequence.

## 8. Controlling integrated-MVP direction

The approved product remains one ecosystem:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Paper-first Player play, local-first saves, bounded project-specific sync, complete DM Desktop fallback, combat authority resume/handoff, object storage/media, meaningful recovery/backup and official-SRD clarification remain protected integrated-MVP direction.

The project remains intentionally not a VTT, automatic legality/rules engine, generalized sync platform, marketplace/social product or enterprise infrastructure exercise.

## 9. Current technical direction

- Kotlin/Compose Android + Kotlin/Compose Multiplatform Desktop;
- SQLDelight/SQLite local persistence;
- TypeScript Cloudflare Worker API;
- Neon PostgreSQL hosted persistence;
- Descope identity proof with application-owned authorization;
- Ktor Client shared Android/Desktop HTTP;
- versioned `/v1` HTTP/JSON API;
- client mutation UUIDs + optimistic revisions where mutations require retry identity;
- durable SQLDelight outbox + scoped push/pull synchronization;
- explicit hosted SQL migrations/contracts;
- versioned app-owned JSON import/export;
- one canonical PC/export snapshot + shared PDF-export semantics with platform rendering.

Object storage remains required for MVP but provider activation remains deferred until Media/Handouts/assets reach real integration. Do not assume R2 by inertia.

## 10. Security / operating residuals

D-0075 remains controlling: repository intentionally public, hard USD $0 external-service budget, never commit secrets, and paid/overage commitments require explicit owner approval.

Carry forward:

- JWT/fail-closed verification review;
- object-level authorization regression coverage for new hosted objects/actions;
- SQL/query safety;
- token/secret leakage prevention;
- replay/idempotency authorization review;
- locally reported 3 high-severity npm vulnerabilities — inspect before remediation; never run `npm audit fix --force` blindly;
- least-privilege Neon runtime role evaluation;
- production identity/configuration review before release.

## 11. QA device context

Current owner-reported Android QA inventory is six instances:

- phone-class: 1 physical Redmi Note 11 Pro 5G + 2 emulators;
- tablet-class: 1 physical Lenovo Tab P11 TB-J606F, 6 GB RAM, Android 11 + 2 emulators.

The Windows Desktop QA workstation/toolchain is recorded in `docs/TEST_DEVICES.md`.

Emulator coverage does not replace physical-device acceptance where a physical gate is explicitly required.

## 12. Release/acceptance status

The project remains development/debug and is not release-ready.

Wave 4 recorded hosted/manual gates are complete. The first Wave 5 Desktop package is owner-accepted and integrated. The current campaign-membership administration core has no owner/manual gate yet; implementation and automated verification are pending.

## 13. Resume rule

Read, in order:

1. `docs/checkpoints/LATEST.md`;
2. `docs/BRANCH_STATUS.md`;
3. `docs/checkpoints/2026-09-16_CAMPAIGN_MEMBERSHIP_ADMINISTRATION_CORE_OPEN.md`;
4. D-0072 and D-0073 as needed;
5. the completed Desktop-shell and membership-revoke checkpoints for predecessor evidence.

Continue on `wave5/campaign-membership-administration-core`. No owner/manual action is currently required.
