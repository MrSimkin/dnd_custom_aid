# Testing and Verification

## Current status

Before convergence, current Player runtime/QA authority remains:

`implementation/phase4a-successor-cycle`

Observed branch HEAD during the 2026-09-14 technical-readiness review:

`b9dea8ad6b17dcf3feeabba263eff1ee498f1536`

Frozen Player candidate:

- version `0.4.0-preqa.13`;
- versionCode/build `41300`;
- candidate commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — **SUCCESS**;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted cross-device physical revalidation pending.

The Actions run was independently rechecked during technical readiness and is completed/successful on the exact candidate SHA.

Automation is not physical owner acceptance.

D-0071/D-0073 change the next product-cycle acceptance strategy: the next major owner-facing QA target is the integrated MVP across Player + hosted/shared services + DM Android/Desktop, not a separate tiny server/DM product milestone.

This does **not** mean deferring testing until the end. Engineering checks and integration proofs run continuously.

## 1. Core verification rule

Never claim a test passed unless it was actually executed successfully against the relevant revision/environment.

Every meaningful implementation/QA batch should record:

- exact revision/build;
- what was tested;
- how;
- what passed/failed;
- what was not tested and why when material;
- relevant device/environment;
- whether evidence is automated, local integration, emulator/simulator or physical owner/device evidence.

Historical evidence remains evidence only for the behavior/build boundary it exercised.

## 2. Preserve current Player evidence

Before integrated implementation/convergence, read the current successor-branch `docs/checkpoints/LATEST.md` and frozen-candidate checkpoint.

Do not replay all historical Player discovery/QA as if it never happened.

The convergence must preserve:

- current Player runtime;
- SQLDelight migrations through the current successor state;
- Player domain tests;
- permanent Player guard scripts from the successor workflow;
- frozen-candidate historical evidence.

A new integrated baseline is a new technical revision. It does not retroactively inherit physical acceptance from `preqa.13`, but neither does it invalidate unrelated historical evidence automatically.

## 3. Standard automated verification surfaces

### Kotlin / Android / Desktop / SQLDelight

Aggregate gate:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

Before convergence, the successor workflow also runs permanent Player guards for:

- control geometry;
- reorder target stability;
- checkbox consistency;
- spellcasting bootstrap;
- class-editor controls;
- application-settings semantics;
- wide Combat composition;
- Table Mode affordances;
- structured dice/Custom Throw presentation.

The integrated baseline must keep those guards unless a later verified change deliberately supersedes a specific one.

### Backend

Current scaffold check:

```bash
cd backend
npm install
npm run check
```

As the backend becomes real, add proportionate tests for material behavior rather than relying only on TypeScript compilation.

### Database/migrations

Hosted PostgreSQL and local SQLDelight migrations with data-preservation risk require explicit migration tests. Never describe an untested migration as safe.

## 4. Convergence gate

When implementation is authorized and the convergence branch is created, it must at minimum:

1. retain the successor Player guard scripts;
2. run all permanent Player guards;
3. run `:shared:desktopTest`;
4. assemble the Android debug APK;
5. build Desktop;
6. run backend type-check/check;
7. confirm local SQLDelight migrations are not lost/reordered accidentally;
8. inspect Git diff for accidental regression to stale `main` Player runtime;
9. verify current integrated docs/governance are preserved;
10. record any unresolved conflict explicitly.

Do not merge the convergence branch to `main` merely because Git reports no textual conflicts. Semantic preservation is the gate.

## 5. First hosted/shared implementation test priorities

As the shared/hosted spine becomes real, prioritize tests for:

- API/domain serialization compatibility;
- global identity mapping;
- campaign membership/role authorization;
- PC owner vs current controller;
- server-side restriction of full PC data vs tiny public identity projection;
- optimistic revision/stale-write rejection;
- idempotent mutation retry;
- tombstone deletion/non-resurrection;
- local Save independent from network;
- Desktop explicit Sync;
- Android opportunistic/manual Sync behavior;
- scoped campaign/library synchronization;
- outbox retry without duplicate mutations;
- transactional remote-apply behavior;
- conflict detection and non-silent overwrite;
- asset authorization/integrity;
- grouped PC audit and compensating correction;
- restore-as-new-current-version behavior;
- Personal -> Campaign independent copy/provenance;
- saved encounter -> independent live encounter;
- full backup manifest/completeness/integrity.

## 6. Hosted PC snapshot compatibility tests

The technical-readiness review recommends using the existing versioned application-owned Player serialization family as the basis for the hosted current-PC snapshot rather than mirroring every local SQLDelight character table in PostgreSQL.

Therefore add tests that prove:

- current Player snapshot round-trip is lossless for the authoritative aggregates;
- snapshot format/version is explicit;
- older supported snapshot versions continue to decode/migrate as promised;
- unknown future fields do not destroy older supported decoding where intentionally allowed;
- hosted metadata and snapshot character/campaign identity cannot silently disagree;
- full mechanical snapshot is never returned through a Player-to-Player public projection route;
- recovery snapshot restoration produces a new current revision rather than erasing intervening history.

## 7. Authentication/authorization testing

Descope proves authentication; the application owns domain authorization.

Test at minimum:

- missing/invalid/expired session token denial;
- valid token -> stable internal application-user mapping;
- campaign membership required for campaign data;
- Player cannot obtain another PC's restricted sheet merely by knowing an ID;
- DM authority is campaign-scoped;
- DM authority does not imply PC ownership;
- owner and current controller may differ;
- membership removal stops future hosted access/sync;
- kick vs ban behavior differs correctly;
- global Freeze Account remains system-admin-only;
- remembered-login/offline local behavior does not turn cached identity into server authorization.

## 8. Object-storage testing

The current technical recommendation is R2 Standard, pending owner/service activation.

Regardless of provider, verify:

- asset metadata uses stable application identity rather than provider keys as domain identity;
- unauthorized users cannot fetch private assets merely by guessing object IDs;
- Player-safe does not mean automatically revealed;
- replacing an asset can preserve logical references;
- delete/recover behavior matches the owning domain;
- checksum/media metadata is coherent where used;
- failed upload/finalization does not create a silently valid broken asset.

If upload strategy later changes from Worker-proxy to short-lived direct/presigned transfer, add tests for authorization expiry and finalization rather than treating the transport change as invisible.

## 9. Intended-device/manual testing

C-0010 remains controlling: first exercise a feature on the form factor where its real use matters.

Examples:

- Player sheet/reconciliation: phone first, tablet sanity where relevant;
- DM live Desks/combat: tablet first **and Desktop fallback path**;
- DM preparation/Creator/Manager/Admin: Desktop first;
- explicit combat authority resume: at least two real DM-capable clients/devices;
- public combat projection: Player device plus authoritative DM device;
- offline/reconnect: real connectivity interruption where practical;
- remembered login: close/reopen/reconnect and appropriate expiration paths;
- backup export: actual artifact plus integrity/metadata verification.

Desktop and tablet need not share identical UI, so visual acceptance is form-factor-specific even when domain behavior is shared.

## 10. Combat authority testing

Combat remains local-first with one authoritative DM device.

Test at minimum:

- monotonically increasing sequence/revision inside one authority generation;
- older hosted state cannot replace newer authoritative local state;
- offline DM actions continue locally;
- public Player projection may become stale rather than blocking DM play;
- explicit resume on another DM device starts from the latest actually synchronized state;
- new authority generation invalidates stale old-device writes;
- simultaneous authoritative editing is not accidentally permitted;
- unsynchronized lost-device state is not falsely claimed as recovered.

No WebSockets/Durable Objects/realtime platform is required merely for this test.

## 11. Backup/export testing

Full backup/export verification must prove more than `download succeeded`.

At minimum verify:

- backup/application/schema/data-format version metadata;
- durable relational data scope;
- asset/object-storage manifest/recovery story;
- checksums/integrity information;
- incomplete/failure detection;
- no secrets/provider credentials are exported accidentally.

A polished one-click restore UI is not required in this MVP, but the backup must be a credible future recovery source rather than a cosmetic partial dump.

## 12. SRD clarification testing

SRD retrieval + AI is implemented late in the MVP cycle but requires focused tests before integrated acceptance.

Verify:

- SRD 5.1 vs SRD 5.2.1 provenance;
- relevant official retrieval;
- answers grounded in retrieved material rather than unsupported model memory;
- Spanish answer/source presentation identifies the correct rules family/version;
- insufficient evidence is handled honestly;
- Homebrew records are **not** silently injected into the MVP official-only clarification corpus.

## 13. Integrated MVP final owner-facing QA

Representative final route:

```text
Player authenticates / remembered login works
-> joins/switches campaign
-> edits/reconciles PC and saves locally
-> synchronizes PC + asset + history
-> DM synchronizes/downloads authorized PC
-> DM audits/views/corrects within authority
-> DM uses Desktop Creator/Manager surfaces
-> saved encounter creates independent live encounter
-> authoritative DM device runs combat locally
-> Players receive only allowed public projection
-> connectivity loss does not stop authoritative DM play
-> reconnect resumes safe synchronization
-> Desktop explicitly resumes latest synchronized combat if tablet is unavailable
-> stale old-authority writes are rejected
-> post-play durable bookkeeping is explicit
-> durable current/history/recovery state is coherent
-> full server backup/export succeeds
-> Player and DM receive grounded official-SRD clarification
```

Complement with negative/error cases: stale revision, duplicate mutation, authorization denial, membership removal, frozen PC, conflict, failed/pending Sync, tombstone non-resurrection, stale combat projection, stale old authority, asset failure and incomplete backup.

## 14. Failure handling

A failing internal package does not require restarting the entire MVP.

1. identify the responsible boundary/domain;
2. preserve unrelated proven evidence;
3. repair the smallest real root cause;
4. run focused checks;
5. run broader integration/aggregate checks when warranted;
6. update operative memory and candidate identity when the testable package changes materially.

Do not hide uncertainty or failing tests behind a broad `in progress` label.

## 15. Current exact resume rule

Product design (7D), exact MVP boundary (7E), implementation strategy (8A), Git convergence direction (8B), first shared-spine direction (8C) and the technical-readiness review are complete.

The current checkpoint is:

`docs/checkpoints/2026-09-14_INTEGRATED_MVP_TECHNICAL_READINESS_REVIEW.md`

No product code was changed by that review.

The next owner-level action is explicit implementation authorization. After authorization:

1. refresh `main` and Player successor;
2. execute the dedicated convergence branch/gate;
3. merge a coherent integrated baseline to `main` only after validation;
4. proceed with delegated technical implementation packages under D-0073;
5. return to the owner only for meaningful product/scope/security/cost choices or physical/manual acceptance gates.