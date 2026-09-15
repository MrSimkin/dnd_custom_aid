# Testing and Verification

## Current status

The integrated-MVP implementation is authorized and the former `main` + Player-successor baseline has been semantically converged.

Validated convergence commit:

`5bed85cbb3e86ae63eac79149fadc5e56e61b256`

GitHub Actions run:

`34917259324` / #1694 — **SUCCESS**.

That run verified all permanent Player guards, `:shared:desktopTest`, Android debug assembly, Desktop build, backend type-check and APK artifact upload.

Historical Player physical evidence remains bounded to the old frozen candidate and is not retroactively upgraded by the integrated CI result.

The next major owner-facing QA target remains the integrated MVP across Player + hosted/shared services + DM Android/Desktop. Engineering verification occurs continuously before that final gate.

## 1. Core verification rule

Never claim a test passed unless it was actually executed successfully against the relevant revision/environment.

Every meaningful implementation/QA batch should record:

- exact revision/build;
- what was tested and how;
- pass/fail results;
- material untested areas and why;
- relevant device/environment;
- whether evidence is automated, local integration, emulator/simulator or physical owner/device evidence.

Historical evidence remains evidence only for the boundary it actually exercised.

## 2. Historical Player evidence

Frozen historical candidate:

- `0.4.0-preqa.13 / 41300`;
- candidate `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- run `34801612526` / #1630 — SUCCESS;
- artifact `10331503478`;
- targeted physical cross-device revalidation was pending at that historical boundary.

The Player runtime/migrations/tests/guards from the successor are now present in the integrated baseline. Do not replay old repair work without new evidence.

## 3. Standard automated verification surfaces

### Kotlin / Android / Desktop / SQLDelight

Aggregate gate:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

The Scaffold workflow also runs permanent Player guards for:

- compact control geometry;
- reorder target stability;
- checkbox consistency;
- spellcasting bootstrap;
- class-editor controls;
- application-settings semantics;
- wide Combat composition;
- Table Mode affordances;
- structured dice / Custom Throw presentation.

Keep these guards unless a later verified change deliberately supersedes a specific invariant.

### Backend

Current scaffold gate:

```bash
cd backend
npm install
npm run check
```

As backend behavior becomes real, add tests for material auth/authorization/revision/idempotency/sync behavior rather than relying only on TypeScript compilation.

### Database/migrations

Hosted PostgreSQL and local SQLDelight migrations with data-preservation risk require explicit migration tests. Never describe an untested migration as safe.

## 4. Convergence gate — COMPLETED

The baseline convergence passed the required gate:

- successor Player guards retained and successful;
- shared desktop tests successful;
- Android debug APK assembled;
- Desktop built;
- backend check successful;
- successor migrations/runtime preserved semantically;
- current integrated governance/docs preserved;
- historical Player evidence preserved.

Evidence: `docs/checkpoints/2026-09-14_INTEGRATED_MVP_BASELINE_CONVERGENCE.md` and Actions run `34917259324`.

## 5. Shared Integrated-MVP Spine test priorities

The next package should prove invariants rather than only constructors/serialization:

- global identity is distinct from campaign role;
- campaign membership is distinct from PC ownership/control;
- PC owner and controller may differ;
- DM authority does not imply ownership;
- stable IDs survive serialization/persistence;
- revisions advance monotonically;
- stale expected revisions are rejected rather than silently overwriting;
- idempotent mutation retries do not duplicate effects;
- tombstoned synchronized records cannot be resurrected by stale clients;
- Personal -> Campaign copy creates a new independent identity with provenance;
- later Personal edits do not silently mutate Campaign copies.

Add migration tests whenever schema is introduced/changed.

## 6. Hosted/API/sync priorities

As the hosted foundation becomes real, add focused tests for:

- Descope subject -> internal user mapping;
- server-side protected-route authorization;
- campaign role/ownership/control authorization boundaries;
- revision conflict responses;
- idempotency receipts/retries;
- scoped pull cursors/order;
- tombstone propagation;
- transactional local application of remote batches;
- membership removal stopping future hosted access;
- tiny public PC projection never leaking full sheet data.

Do not build generic coverage targets as a substitute for these invariants.

## 7. Object storage/media priorities

When object storage is implemented, verify:

- authorization for upload/download/reveal;
- stable logical asset identity independent of provider key;
- replacement preserving logical references where intended;
- referenced-delete warnings/behavior;
- portrait offline-cache behavior;
- backup/export asset completeness/integrity.

## 8. PC Sheet PDF export priorities

Representative automated/rendered checks should cover:

- Classic, Custom v1, Custom v2-per-Attribute and Custom v2-per-Ability;
- Permanent vs Current Snapshot;
- custom-stat modes 1/2/3;
- all custom Attributes/Abilities preserved;
- portrait Crop/Fit;
- missing uncached portrait warning + nonblocking export;
- overflow continuation cues and matching Extended pages;
- hard readability floor/no silent truncation;
- blank writable areas preserved;
- optional Spellbook grouped by level/alphabetical with index;
- complete spell fields/descriptions and PC-specific casting values where known;
- offline static Save/Share behavior.

Use deterministic layout tests where possible plus rendered golden/reference checks for representative documents. Visual owner review remains appropriate for final sheet fidelity.

## 9. Live combat authority priorities

When combat exchange is implemented, verify:

- exactly one authoritative DM device;
- local actions continue offline;
- hosted state cannot overwrite newer local authority;
- explicit resume/handoff advances authority generation/epoch;
- stale previous authority cannot write over new authority;
- public Player projection exposes only approved data;
- unsynchronized lost-device actions are not falsely reconstructed.

## 10. Backup/recovery priorities

Verify:

- current state + meaningful history/recovery semantics;
- restore creates a new current revision rather than deleting history;
- backup manifest/version metadata;
- relational export completeness;
- asset manifest/binary recovery completeness;
- checksum/integrity validation;
- failure is detectable rather than producing a falsely “successful” partial archive.

## 11. Integrated owner-facing QA

The major QA candidate should exercise representative end-to-end flows:

```text
login / remembered device
-> campaign join/switch/role
-> Player local Save + hosted PC sync
-> DM authorized PC inspect/audit/correct
-> PC Sheet PDF export
-> Personal -> Campaign content copy
-> author Monster/NPC/Place/Zone/Encounter
-> start live encounter on tablet
-> Player public combat projection
-> disconnect/reconnect
-> Desktop explicitly resumes DM combat authority
-> old authority rejected
-> archive/discard/save-as-new-template
-> full backup export
-> official-SRD clarification from Player and DM
```

Physical/manual acceptance must still be explicitly recorded; green automation alone is not owner acceptance.
