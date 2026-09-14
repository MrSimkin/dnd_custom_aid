# Testing and Verification

## Current status

The current Player runtime/QA authority remains `implementation/phase4a-successor-cycle`.

Its 2026-09-14 frozen candidate is:

- version `0.4.0-preqa.13`;
- versionCode/build `41300`;
- commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — **SUCCESS**;
- targeted cross-device physical revalidation — pending on that branch.

This evidence is preserved. Automation is not physical owner acceptance.

D-0071 changes the **next product-cycle acceptance strategy**: the next major owner-facing QA target is the integrated MVP across Player + Server/shared services + DM tablet/Desktop rather than a tiny isolated server/DM slice treated as a separate product milestone.

This does not mean `do not test until the end`. Engineering tests and integration proofs must run continuously while the MVP is built.

## 1. Core verification rule

Never claim a test passed unless it was actually executed successfully against the relevant revision/environment.

Every meaningful implementation or QA batch should record:

- exact revision/build;
- what was tested;
- how;
- what passed/failed;
- what was not tested and why when material;
- relevant device/environment;
- whether evidence is automated, local integration, emulator/simulator or physical owner/device evidence.

Historical evidence remains evidence only for the exact behavior/build boundary it exercised.

## 2. Current Player evidence must not be discarded

Before integrated implementation begins, read the current successor-branch `docs/checkpoints/LATEST.md` and its frozen-candidate checkpoint.

Do not replay all historical Player discovery/QA as if it never happened.

The architecture discussion also retained four bounded planning labels (A10, B1, J1, J2). Before coding/retesting, reconcile those labels against the then-current successor source/evidence rather than allowing a conversational label to overwrite exact branch proof.

A Player defect whose root cause is local persistence, shared state, layout primitive or other cross-system behavior may require broader integrated retesting, but inference is not physical evidence.

## 3. Standard automated verification surfaces

### Kotlin / Android / Desktop / SQLDelight

Representative aggregate gate remains conceptually:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

Use focused tests as appropriate before/alongside the aggregate gate.

### Backend

Representative backend check remains conceptually:

```bash
cd backend
npm install --no-package-lock
npm run check
```

As real backend/database/object-storage/auth/sync features are implemented, add proportionate automated tests for material failure modes rather than chasing coverage percentages.

### Database/migrations

Test explicit PostgreSQL and SQLDelight migrations where data-preservation risk exists. Do not describe an untested migration as safe.

## 4. Integrated MVP engineering test priorities

D-0071 identifies the following high-value automated/integration areas as implementation becomes real:

- shared domain model/contract serialization;
- authentication identity mapping and remembered-login boundary behavior;
- campaign membership/role authorization;
- PC ownership vs current control;
- minimum campaign-visible PC identity vs restricted mechanical sheet data;
- revision/stale-write handling;
- idempotent mutations;
- tombstone deletion/non-resurrection;
- local Save independent from network;
- Desktop explicit Sync semantics;
- Android opportunistic/manual Sync behavior;
- conflict detection and non-silent overwrite behavior;
- sync scope/campaign isolation;
- object-asset authorization/reference/integrity;
- grouped PC audit/history and compensating correction behavior;
- recovery restore-as-new-version semantics;
- saved encounter -> independent live encounter copy;
- reusable personal DM content -> independent campaign copy;
- active DM combat sequence/authority;
- public Player combat projection filtering;
- stale authority-generation rejection after device resume/handoff;
- offline/reconnect behavior;
- full server backup/export completeness/integrity;
- SRD provenance/retrieval grounding and correct source-version identification.

## 5. Intended-device/manual testing

C-0010 remains controlling: test a feature first on the form factor where its real use matters.

Current/expanded examples:

- Player sheet/reconciliation: phone first, tablet sanity/coverage where relevant;
- DM live Desks/combat: tablet first **and Desktop fallback path must also be exercised**;
- DM preparation/Creator/Manager/Admin workflows: Desktop first;
- explicit combat authority resume: at least two real DM-capable clients/devices in a realistic handoff/recovery scenario;
- public combat projection: Player device plus authoritative DM device;
- offline/reconnect: actual connectivity interruption where practical;
- remembered login: close/reopen/reboot/reconnect scenarios proportionate to implementation;
- backup export: actual exported artifact and integrity/metadata verification.

Desktop and tablet need not share identical UI, so visual acceptance is form-factor-specific even when domain behavior is shared.

## 6. Integrated MVP final owner-facing QA

The next major product acceptance candidate should support an end-to-end route approximately like:

```text
Player authenticates / remembered login works
-> joins/switches campaign
-> edits/reconciles PC and saves locally
-> synchronizes PC + asset + history
-> DM synchronizes/downloads authorized PC
-> DM audits/views/corrects within authority
-> DM uses Desktop Creator/Manager surfaces for campaign content
-> saved encounter creates independent live encounter
-> authoritative DM device runs combat locally
-> Players receive only allowed public projection
-> connectivity loss does not stop authoritative DM play
-> reconnect resumes safe synchronization
-> Desktop can explicitly resume latest synchronized combat if tablet is unavailable
-> stale old-authority writes are rejected
-> participants perform explicit post-play bookkeeping rather than automatic VTT reconciliation
-> durable current/history/recovery state is coherent
-> full server backup/export succeeds
-> Player and DM can ask official-SRD questions and receive grounded source-identified answers
```

This route should be complemented by bounded negative/error scenarios, especially:

- stale revisions;
- duplicate/retried idempotent mutation;
- authorization denial;
- membership removal;
- frozen PC read-only behavior;
- conflict handling;
- failed/pending Sync;
- tombstone non-resurrection;
- missing/stale public combat projection;
- stale old combat authority after resume;
- object asset missing/unauthorized/corrupt case where practical;
- backup failure/incomplete-manifest detection.

## 7. Combat authority testing

Combat is local-first and one DM device is authoritative at a time.

Test at minimum:

- monotonically increasing sequence/revision inside one authority generation;
- older hosted state cannot replace newer authoritative local state;
- offline DM actions continue locally;
- Player projection may become stale rather than blocking DM play;
- explicit resume on another DM device begins from the latest actually synchronized state;
- new authority generation invalidates stale writes from the old device;
- simultaneous authoritative editing is not accidentally permitted;
- unsynchronized state from a lost device is not falsely claimed as recovered.

This does not require WebSockets/Durable Objects/realtime infrastructure merely for testing convenience.

## 8. Backup/export testing

Because full backup/export is an early MVP foundation, verification must prove more than `download succeeded`.

At minimum verify that the export identifies:

- schema/data format/application version;
- durable relational data scope;
- relevant object-storage asset manifest/reference/integrity information;
- checksum/integrity metadata;
- obvious failure/incomplete-generation cases.

A polished restore UI is not required in this cycle merely to test backup export, but the backup format must be credible as a future recovery source rather than a cosmetic JSON dump with missing domains.

## 9. SRD clarification testing

SRD retrieval + AI is implemented late in the MVP cycle but must still receive focused tests before integrated acceptance.

Verify:

- SRD 5.1 vs SRD 5.2.1 provenance is preserved;
- retrieval returns relevant supported official chunks;
- answers are grounded in retrieved material rather than unsupported model memory;
- Spanish answer/source presentation identifies D&D 5e / D&D 5.5e correctly;
- unsupported/insufficient evidence is handled honestly;
- Homebrew Rules records are **not** silently injected into the MVP official-only clarification corpus.

## 10. Failure handling during the MVP build

A failing internal slice does not require restarting the entire MVP.

1. identify the responsible boundary/domain;
2. preserve unrelated proven evidence;
3. repair the smallest real root cause;
4. run focused checks;
5. run broader integration/aggregate checks when the touched boundary warrants them;
6. update operative memory and candidate identity when a testable package changes materially.

Do not hide uncertainty or failing tests behind a broad `in progress` label.

## 11. Current exact resume rule

This documentation checkpoint did **not** start implementation.

Before coding:

1. finish 7D — detailed DM Desktop App product definition;
2. finish 7E — exact expanded-MVP/post-MVP boundary;
3. derive final Git/development topology and concrete implementation gates;
4. obtain explicit coding authorization;
5. reconcile the then-current Player successor evidence with the integration plan.

Until then, this file defines the planned testing architecture, not a claim that the integrated MVP currently exists or has passed.