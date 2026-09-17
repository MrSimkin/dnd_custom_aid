# Testing and Verification

**Updated:** 2026-09-17  
**Integrated-MVP implementation:** AUTHORIZED / IN PROGRESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**PR #44 merge:** `306377df1a453f531af4b670d2b231c88a3c9419`  
**Post-merge Scaffold:** `35168920031` — SUCCESS

## 1. Evidence rule

Never claim a test passed unless it was actually executed successfully against the relevant revision/environment.

Record, as applicable:

- exact revision/build;
- commands/checks;
- pass/fail;
- material untested areas;
- environment/device;
- evidence class: automated CI, local integration, real provider, emulator/simulator, or physical owner/device.

Green CI does not substitute for real provider/deployment evidence or owner physical/visual acceptance. Historical evidence remains bounded to what it actually exercised.

## 2. Current aggregate verification surfaces

Kotlin / Android / Desktop / SQLDelight:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

Scaffold also runs permanent Player guard scripts and uploads the Android debug APK.

Backend:

```bash
cd backend
npm install
npm run check
```

Hosted PostgreSQL migrations/contracts are exercised in Scaffold. Local/hosted migrations with preservation risk require explicit migration tests.

Known owner-local install residual: 3 high-severity npm vulnerabilities. This is a hardening input, not authorization for `npm audit fix --force`.

## 3. Wave 5 final evidence

PR #44 final branch head `20f62b110df80759b5e90d083253b3b87716ff31` passed exact-head Scaffold `35168771704`.

After merge to `main` as `306377df1a453f531af4b670d2b231c88a3c9419`, Scaffold `35168920031` completed successfully with:

- backend — SUCCESS;
- hosted-database — SUCCESS;
- Kotlin/build/test/APK — SUCCESS.

Separate real provider/owner QA had already verified Desktop OTP authentication, hosted bootstrap/roster, moderation state transitions/revisions, canonical Outlook identity migration, settings persistence, shutdown/relaunch session behavior and explicit sign-out. Do not replay those gates absent new defect evidence.

## 4. Wave 6 first-package verification priorities

For reusable-content local persistence, test concrete invariants rather than arbitrary coverage targets:

- Personal content stores creator scope correctly;
- Campaign content requires/retains campaign scope;
- explicit Personal -> Campaign copy creates a **new** object ID;
- copied object starts its own revision history and retains provenance to source ID/scope;
- later source update does not mutate the independent campaign copy;
- optimistic update rejects stale revisions without silent overwrite;
- tombstoned content is not returned as active and stale operations cannot resurrect it;
- list/filter by family/scope behaves deterministically;
- persistence survives database reopen where practical;
- SQLDelight migration preserves existing pre-Wave-6 data;
- domain-specific payloads are not accidentally coupled by the shared envelope.

Run focused `shared` tests while developing, then the aggregate Kotlin/Android/Desktop gate and normal Scaffold before merge.

## 5. Future verification priorities

As later waves arrive, maintain tests for object-level authorization, idempotency/replay, malformed payload/error hygiene, object-storage authorization/reference integrity, PDF export completeness/readability, single-authority combat handoff/stale-authority rejection, backup completeness/checksums and integrated Player+Server+DM owner scenarios.

## 6. Provider evidence discipline

Provider-side behavior requires explicit real-provider or owner-returned evidence. Once a provider action is completed/tested/recorded, do not repeat it merely because docs changed.

If the active worker lacks authenticated provider capability, finish safe repo/CI work, issue one exact owner-action packet and stop at that boundary rather than exploring alternate credentials/access paths.