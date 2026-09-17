# Testing and Verification

**Updated:** 2026-09-17  
**Integrated-MVP implementation:** AUTHORIZED / IN PROGRESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 reusable-content persistence foundation:** INTEGRATED  
**PR #46 merge:** `013abbb9e57af0ba04fe1e8b678e8ed29522bedd`  
**Post-merge Scaffold:** `35220099721` — SUCCESS

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

## 4. Wave 6 reusable-content foundation evidence

PR #46 final branch head `008b196ec1fc36cbd637cfbb8b2b4915109ddc8d` passed exact-head Scaffold `35219548535`. The PR-triggered Scaffold `35219863619` also passed.

After merge to `main` as `013abbb9e57af0ba04fe1e8b678e8ed29522bedd`, post-merge Scaffold `35220099721` completed successfully.

The integrated test coverage for this first package includes the intended local reusable-content invariants:

- Personal -> Campaign copy creates a new independent identity and retains provenance;
- later source mutation does not automatically mutate the independent Campaign copy;
- stale mutation is rejected;
- tombstoned content is not silently resurrected;
- listing/filtering by scope/family behaves as intended;
- SQLDelight migration `18.sqm` adds the reusable catalog while preserving existing pre-Wave-6 campaign data;
- Desktop can reopen persistent data and safely migrate the recognized unversioned Wave-5 schema;
- unknown unversioned Desktop databases are refused rather than guessed/destructively recreated.

This evidence does **not** claim validation of large Manager UI, hosted reusable-content sync, object storage/provider activation or a universal executable payload model; those were deliberately outside PR #46.

## 5. Future verification priorities

For later Wave 6 packages, derive focused invariants from the selected bounded package and preserve the integrated foundation's identity/scope/provenance/revision/tombstone behavior. Do not repeat foundation tests as a substitute for testing new domain behavior.

As later waves arrive, maintain tests for object-level authorization, idempotency/replay, malformed payload/error hygiene, object-storage authorization/reference integrity, PDF export completeness/readability, single-authority combat handoff/stale-authority rejection, backup completeness/checksums and integrated Player+Server+DM owner scenarios.

## 6. Provider evidence discipline

Provider-side behavior requires explicit real-provider or owner-returned evidence. Once a provider action is completed/tested/recorded, do not repeat it merely because docs changed.

If the active worker lacks authenticated provider capability, finish safe repo/CI work, issue one exact owner-action packet and stop at that boundary rather than exploring alternate credentials/access paths.