# Testing and Verification

## Current status

The integrated-MVP implementation is authorized and the former `main` + Player-successor baseline has been semantically converged.

Provider-neutral hosted/sync implementation checkpoint:

`8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`

GitHub Actions:

`34985799585` — **SUCCESS**.

That run verified permanent Player guards, shared/Kotlin tests, Android debug assembly, Desktop build, backend checks, hosted PostgreSQL contracts and APK artifact upload.

The first real hosted DEV provider activation has also been verified outside CI against actual Neon + Descope + Cloudflare resources. See `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md`.

Historical Player physical evidence remains bounded to the old frozen candidate and is not retroactively upgraded by integrated CI or hosted-provider proof.

The next major owner-facing QA target remains the integrated MVP across Player + hosted/shared services + DM Android/Desktop. Engineering verification occurs continuously before that final gate.

## 1. Core verification rule

Never claim a test passed unless it was actually executed successfully against the relevant revision/environment.

Every meaningful implementation/QA batch should record:

- exact revision/build where applicable;
- what was tested and how;
- pass/fail results;
- material untested areas and why;
- relevant device/environment;
- whether evidence is automated, local integration, real hosted provider, emulator/simulator or physical owner/device evidence.

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

The Scaffold workflow also runs permanent Player guards for compact controls, reorder stability, checkbox consistency, spellcasting bootstrap, class-editor controls, application settings, wide Combat composition, Table Mode and structured dice/Custom Throw presentation.

Keep these guards unless a later verified change deliberately supersedes a specific invariant.

### Backend

Current gate:

```bash
cd backend
npm install
npm run check
```

Backend tests should continue emphasizing material auth/authorization/revision/idempotency/sync behavior rather than TypeScript compilation alone.

A local install during provider activation reported **3 high severity npm vulnerabilities**. That report is a security/dependency-review input, not a test failure and not authorization for `npm audit fix --force`. Inspect exact packages, reachability and compatible fixed versions before remediation.

### Database/migrations

Hosted PostgreSQL and local SQLDelight migrations with data-preservation risk require explicit migration tests. Never describe an untested migration as safe.

## 4. Completed automated integration gates

### Baseline convergence

Completed successfully with successor Player guards, shared tests, Android assembly, Desktop build, backend check and historical evidence preservation.

### Provider-neutral hosted/sync foundation

Checkpoint `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`, Actions `34985799585` — SUCCESS.

This includes backend and hosted PostgreSQL contract validation for campaign/membership lifecycle and PC snapshot authorization/revision behavior.

## 5. Real hosted DEV verification — COMPLETED for activation boundary

The following were actually executed against real development providers:

### Neon

- migration `database/migrations/0001_integrated_mvp_spine.sql` applied successfully;
- database contract tests 0001–0004 executed successfully inside one transaction;
- transaction rolled back afterward;
- application tables were confirmed clean after the contract-test run;
- later real authenticated application access created/resolved the expected `app_user` record.

The embedded Neon TypeScript psql fallback did not accept the test files' leading `\set ON_ERROR_STOP on` when included. Temporary local copies removed only that psql meta-command; repository test files were not modified.

### Descope + Cloudflare + Neon end-to-end

Verified:

```text
real email OTP
-> Descope session JWT
-> Cloudflare Worker JWT verification
-> /v1/me HTTP 200
-> application identity resolution
-> Neon app_user persistence
```

Control case:

- `/v1/me` without auth -> HTTP 401 `UNAUTHENTICATED`.

Health case:

- `/health` -> HTTP 200.

### Cloudflare Workers Free runtime proof

Repeated authenticated `/v1/me` requests were generated against the real DEV environment.

Cloudflare Observability showed visible individual requests at approximately **1 ms CPU time** with no observed benchmark errors. Wall time varied because network/database waiting is not equivalent to Worker CPU time.

Result: representative Workers Free CPU/runtime gate **PASS** for the tested authenticated path.

Do not generalize this number to materially heavier future endpoints; profile them when they exist.

## 6. Current Player <-> Server test priorities

The next implementation package should add/verify:

- real remembered Android Descope session/token acquisition;
- token delivery through `HostedAccessTokenProvider`;
- hosted account/campaign bootstrap from the owner-facing Player flow;
- local campaign creation + durable hosted delivery;
- PC snapshot push/pull;
- app restart/session continuity as appropriate;
- second-device observation;
- offline local edits + reconnect/convergence;
- membership removal/revoke stopping future hosted access;
- Player versus DM authorization boundaries;
- stale revision responses remaining explicit/non-destructive;
- no silent local-data loss.

Do not replace these concrete contracts with generic coverage targets.

## 7. Security regression priorities

As hosted use expands, maintain or add tests for:

- missing/malformed bearer authorization;
- invalid session tokens;
- audience/subject/fail-closed behavior where practical;
- protected-route object-level authorization;
- campaign role/ownership/control boundaries;
- mutation idempotency and replay authorization;
- stale revision conflicts;
- tombstone propagation/non-resurrection;
- malformed request payloads;
- error responses not exposing credentials/internal exception details.

A later focused security hardening pass should also evaluate the current Neon project-owner runtime credential versus a dedicated least-privilege Worker role.

## 8. Object storage/media priorities

When object storage is implemented, verify:

- authorization for upload/download/reveal;
- stable logical asset identity independent of provider key;
- replacement preserving logical references where intended;
- referenced-delete warnings/behavior;
- portrait offline-cache behavior;
- backup/export asset completeness/integrity.

No object-storage provider is activated yet.

## 9. PC Sheet PDF export priorities

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

Use deterministic layout tests where possible plus rendered golden/reference checks. Visual owner review remains appropriate for final sheet fidelity.

## 10. Live combat authority priorities

When combat exchange is implemented, verify:

- exactly one authoritative DM device;
- local actions continue offline;
- hosted state cannot overwrite newer local authority;
- explicit resume/handoff advances authority generation/epoch;
- stale previous authority cannot write over new authority;
- public Player projection exposes only approved data;
- unsynchronized lost-device actions are not falsely reconstructed.

## 11. Backup/recovery priorities

Verify:

- current state + meaningful history/recovery semantics;
- restore creates a new current revision rather than deleting history;
- backup manifest/version metadata;
- relational export completeness;
- future asset manifest/binary recovery completeness;
- checksum/integrity validation;
- failure is detectable rather than producing a falsely successful partial archive.

## 12. Integrated owner-facing QA

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

Physical/manual acceptance must still be explicitly recorded; green automation and real hosted integration proof alone are not owner acceptance.
