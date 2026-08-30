# Testing and Verification

## Current status

The initial implementation scaffold now exists on `implementation/initial-scaffold` and has executable CI/build checks. D-0043 remains controlling: testing protects material risks without turning this personal project into an enterprise test program.

## 1. Core rule

Never claim a test passed unless it was actually executed successfully against the relevant revision.

Every meaningful implementation change should state concisely:

- what was tested;
- how it was tested;
- what passed or failed;
- what was not tested when that matters;
- relevant environment/device information when it matters.

## 2. Scaffold verification commands

### Kotlin / Android / Desktop / SQLDelight

From repository root:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

The current scaffold CI uses:

- JDK 17;
- Gradle 9.5;
- Android SDK platform 36.

This command currently checks:

- shared Kotlin compilation;
- SQLDelight code generation;
- the in-memory SQLite smoke schema/query test;
- Android debug APK assembly;
- Desktop compilation/build.

### Backend

```bash
cd backend
npm install
npm run check
```

This runs Wrangler type generation and TypeScript type checking.

## 3. Last successful verification

Final pre-handoff scaffold code revision:

`335cf523785a7f503186acd1057ebce72c121b27`

GitHub Actions run #9 completed successfully on that exact revision:

- `kotlin` job — **success**;
- `backend` job — **success**.

The immediately preceding CI runs were also used during scaffold diagnosis. Early failures were resolved before the verified checkpoint; they are not current blockers.

## 4. Known non-blocking tooling note

The current Kotlin/AGP toolchain warns that the KMP `androidLibrary` target DSL used in `shared` is deprecated in favor of a newer API. The current official Kotlin KMP application template still uses the same block, so this project intentionally leaves the green scaffold unchanged until the supported transition is clearer.

Do not suppress or workaround that warning with custom infrastructure merely for cosmetic cleanliness.

A committed Gradle wrapper JAR is also not currently present because the repository connector could not safely transfer the official binary JAR. CI provisions Gradle 9.5 directly. This is a local-developer convenience gap, not a test failure.

## 5. Initial automated verification scope

As real features appear, automated tests should concentrate on:

- shared Kotlin domain logic where mistakes could corrupt/misrepresent state;
- SQLDelight schema/migration correctness;
- outbox/idempotent mutation/revision behavior **once implemented**;
- DM live-combat sequence/authority behavior **once implemented**;
- consequential backend authentication/authorization/sync behavior **once implemented**;
- Android/Desktop/backend compilation/build checks.

Do not create tests for infrastructure or behavior that does not yet exist.

## 6. CI

Use one simple GitHub Actions workflow for relevant pushes/pull requests.

CI is a safety check, not deployment.

Initial CI does not require:

- coverage gates;
- SonarQube;
- emulator/device farms;
- broad API-version matrices;
- staging infrastructure;
- automatic production deployment;
- automated installer/release publishing;
- giant screenshot suites.

## 7. Android device verification

Approved minimum is Android 11 / API 30.

Manual real-device UX testing becomes important once a meaningful UI workflow exists. Compact phone and larger tablet layouts matter more than hypothetical obsolete Android versions.

The current scaffold has **not** received meaningful device UX testing because its UI is only a shell.

## 8. Desktop verification

The DM desktop client is native Kotlin + Compose Multiplatform Desktop. Build verification is currently automated; keyboard/mouse workflow verification should begin once real desktop features exist.

Desktop synchronization tests later should reflect the approved simple model:

- Save persists locally;
- Sync sends pending local changes and retrieves applicable remote changes;
- failed Sync does not lose local work;
- later Sync can retry safely.

Do not build/test a continuous background-sync service unless the product later requires one.

## 9. Local-first synchronization and combat verification

When those features exist, high-value tests include:

- coherent local mutation/outbox persistence;
- duplicate mutations not applying twice;
- stale ordinary revisions detected rather than blindly overwriting newer data;
- DM combat surviving same-device interruption/restart;
- network loss not preventing DM continuation;
- increasing combat sequence/version rejecting delayed older updates;
- no accidental MVP authority-generation/handoff machinery;
- player offline Next-turn/visible-condition changes remaining ephemeral, never uploaded, and discarded on reconnect.

## 10. Character/PDF verification

When implemented, verify:

- saved/freshness state is accurate;
- Save and Export remain distinct;
- unsaved-export warning works;
- exporting unsaved values does not save them;
- Android/Desktop PDF export works offline against approved template mappings.

## 11. SRD clarification verification

When implemented, verify:

- retrieval is grounded in supported official Spanish SRD 5.1 / 5.2.1;
- source/version provenance is preserved;
- homebrew is not silently presented as official SRD;
- PostgreSQL full-text retrieval is tested before adding vector/embedding machinery.

## 12. Regression rule

When fixing a reproducible defect, add a focused automated regression test when practical/useful. Otherwise record the manual verification used.

## 13. Proportionality

C-0009 controls testing: protect this application's useful behavior and data, not an imaginary commercial compliance process.
