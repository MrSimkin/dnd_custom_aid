# Project State — global repository navigation

**Last verified:** 2026-09-16 (Chile local time)  
**Owner integrated-MVP implementation authorization:** **GRANTED**  
**Normal integrated trunk:** `main`  
**Verified Wave 5 starting main:** `40b29006052c9986e2d79227a6053f36241de1e6`  
**Current focused branch:** `wave5/desktop-workbench-shell`  
**Current PR:** #42  
**Current verified package head:** `c5e23ebbec495e3aea2b36a4cbe695c9cc586bd4`  
**Current Scaffold:** `35125170321` — **SUCCESS**  
**Current package checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_WORKBENCH_SHELL_READY_FOR_MANUAL_QA.md`

## 1. Current authority/topology

`main` is the single normal integrated-MVP trunk. New work uses short-lived outcome-oriented branches and reintegrates after proportionate verification. Historical Player/convergence/Wave 4 refs remain evidence rather than parallel trunks.

`docs/checkpoints/LATEST.md` controls the practical resume point. `docs/BRANCH_STATUS.md` controls branch lifecycle.

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
- permanent hosted-sync QA diagnostics.

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

The current native Android hosted path is physically verified through Wave 4. Native clients do not hold database credentials. The hard external-service operating budget remains USD $0 unless the owner changes it.

## 4. Wave 4 — complete

Wave 4 Player <-> Server is complete and integrated for the recorded scope, including remembered authentication, campaign bootstrap/delivery, PC push/pull, blocked-row recovery, unchanged sync, multi-client convergence, explicit reviewed conflict resolution and membership revoke/reinstate authorization behavior.

PR #41 merged as `6f7165e6e5ae56a4b1985f037a656bf527b94d01`; post-merge Scaffold `35123027446` passed. The later documentation/device-inventory main head `40b29006052c9986e2d79227a6053f36241de1e6` passed Scaffold `35123470001` and is the current Wave 5 base.

Physical membership QA specifically exercised DM `ACTIVE -> KICKED -> ACTIVE`; Player owner/controller revoke remains automated-contract evidence, not physical Player evidence.

## 5. Wave 5 — active

Wave 5 Desktop shell + Campaign Administration is now active.

Current first package:

> **Desktop workbench shell + local campaign context**

Branch:

`wave5/desktop-workbench-shell`

PR:

`#42`

The prior Desktop app was only a placeholder window. This package now establishes:

- persistent Desktop SQLDelight/SQLite JDBC local storage;
- explicit Desktop database-driver lifetime;
- Shared `CampaignRepository` as the campaign source of truth;
- approved workbench chrome: top toolbar, left navigation, central workspace, optional context panel and bottom status strip;
- functioning Dashboard;
- functioning Campaigns list/create/active selection;
- Campaign Administration bound to the same active campaign context;
- stable navigation placeholders for later approved Desktop areas;
- persistence-across-reopen regression coverage.

Desktop and Android use separate local database files. This package does not activate hosted Desktop authentication or synchronization and must not be described as cross-device Desktop convergence.

Exact verified behavior/test head `c5e23ebbec495e3aea2b36a4cbe695c9cc586bd4` passed Scaffold `35125170321` across Shared Desktop tests, Desktop build, Android build/guards, backend checks and hosted PostgreSQL contracts.

The current gate is owner Windows Desktop visual/interaction QA before PR #42 may merge.

## 6. Current Wave 5 boundary

This first package deliberately excludes:

- invitations/join/rejoin;
- Kick/Ban/Unban product UI;
- hosted Desktop authentication/full sync;
- live combat authority/resume/handoff;
- substantive Managers/editors;
- Media/Handouts/object-storage activation;
- System Administration;
- backup/export/PDF hardening;
- generalized RBAC/ACL.

D-0072 controls the Desktop workbench/product direction. D-0073 controls integrated-MVP sequencing. Live Combat remains Wave 6.

## 7. Controlling integrated-MVP direction

The approved product remains one ecosystem:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Paper-first Player play, local-first saves, bounded project-specific sync, complete DM Desktop fallback, combat authority resume/handoff, object storage/media, meaningful recovery/backup and official-SRD clarification remain protected integrated-MVP direction.

The project remains intentionally not a VTT, automatic legality/rules engine, generalized sync platform, marketplace/social product or enterprise infrastructure exercise.

## 8. Current technical direction

- Kotlin/Compose Android + Kotlin/Compose Multiplatform Desktop;
- SQLDelight/SQLite local persistence;
- TypeScript Cloudflare Worker API;
- Neon PostgreSQL hosted persistence;
- Descope identity proof with application-owned authorization;
- Ktor Client shared Android/Desktop HTTP;
- versioned `/v1` HTTP/JSON API;
- client mutation UUIDs + optimistic revisions;
- durable SQLDelight outbox + scoped push/pull synchronization;
- explicit hosted SQL migrations/contracts;
- versioned app-owned JSON import/export;
- one canonical PC/export snapshot + shared PDF-export semantics with platform rendering.

Object storage remains required for MVP but provider activation remains deferred until Media/Handouts/assets reach real integration. Do not assume R2 by inertia.

## 9. Security / operating residuals

D-0075 remains controlling: repository intentionally public, hard USD $0 external-service budget, never commit secrets, and paid/overage commitments require explicit owner approval.

Carry forward:

- JWT/fail-closed verification review;
- object-level authorization regression coverage for new hosted objects;
- SQL/query safety;
- token/secret leakage prevention;
- replay/idempotency authorization review;
- locally reported 3 high-severity npm vulnerabilities — inspect before remediation; never run `npm audit fix --force` blindly;
- least-privilege Neon runtime role evaluation;
- production identity/configuration review before release.

## 10. QA device context

Current owner-reported Android QA inventory is six instances:

- phone-class: 1 physical Redmi Note 11 Pro 5G + 2 emulators;
- tablet-class: 1 physical Lenovo Tab P11 TB-J606F, 6 GB RAM, Android 11 + 2 emulators.

Emulator coverage does not replace physical-device acceptance where a physical gate is explicitly required.

## 11. Release/acceptance status

The project remains development/debug and is not release-ready.

Wave 4 recorded hosted/manual gates are complete. Wave 5 Desktop workbench shell is automated-verified but not yet owner-manual accepted.

## 12. Resume rule

Read, in order:

1. `docs/checkpoints/LATEST.md`;
2. `docs/BRANCH_STATUS.md`;
3. `docs/checkpoints/2026-09-16_DESKTOP_WORKBENCH_SHELL_READY_FOR_MANUAL_QA.md`;
4. D-0072 and D-0073 as needed.

Continue on `wave5/desktop-workbench-shell`. The only current owner action is the guided Windows Desktop audition. Do not merge PR #42 before that gate passes.
