# Testing and Verification

## Current status

The initial scaffold is canonical on `main` after PR #4. The Phase 3 local campaign create/select slice is implemented and CI-green on `implementation/local-campaign-selection`; manual Android UX verification remains pending. D-0043 remains controlling: testing protects material risks without turning this personal project into an enterprise test program.

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

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

Current CI uses JDK 17, Gradle 9.5 and Android SDK platform 36.

### Backend

```bash
cd backend
npm install
npm run check
```

This runs Wrangler type generation and TypeScript type checking.

## 3. Last successful Phase 3 verification

Latest verified feature code head:

`c909cd34f34137b2ce74691fe195dfd48f6a02f2`

GitHub Actions run #25 passed on that exact revision:

- shared Kotlin compilation — success;
- campaign repository tests — success;
- SQLDelight generation — success;
- Android debug APK assembly — success;
- Desktop build — success;
- Android debug APK artifact upload — success;
- backend Wrangler/TypeScript check — success.

The successful run includes the correction that makes all current campaign-screen user-facing text Spanish under C-0006.

The campaign persistence test closes SQLite, reopens the same database file, and verifies that the stored campaign and active selection survive the reopen.

Manual Android device/emulator UX verification has not yet occurred.

## 4. Scaffold baseline verification

Final Phase 2 scaffold branch head:

`2f8746de1053bf97cc18d7a522f2027e91879251`

GitHub Actions run #14 passed on that exact revision before PR #4 merged it into `main` as merge commit `d50409270db52df05508f91363bf76385030a77d`.

## 5. Known non-blocking tooling notes

The KMP `androidLibrary` target DSL used by `shared` currently emits a deprecation warning. The current official Kotlin KMP application template still uses the same API, so the project deliberately does not churn a green foundation merely to silence a tooling-transition warning.

The Gradle wrapper JAR is not currently committed because the repository connector could not safely transfer the official binary JAR. CI provisions Gradle 9.5 directly. A normal local Git workflow can add the wrapper later.

## 6. Phase 3 campaign-slice verification scope

Focused automated verification covers behavior that actually exists:

- creating a campaign with a nonblank trimmed name succeeds;
- blank/whitespace-only campaign names are rejected;
- multiple campaigns persist independently;
- duplicate display names are allowed without identity collision;
- selecting an active campaign persists locally;
- changing the active campaign replaces the previous active selection;
- reopening the database/application storage can recover campaigns and active selection.

Do not add tests for hosted synchronization, membership, roles or other features excluded from the slice.

## 7. CI

Use one simple GitHub Actions workflow for relevant pushes/pull requests. CI is a safety check, not deployment.

Initial CI does not require coverage gates, SonarQube, emulator farms, broad API matrices, staging, automatic production deployment, automated release publishing, or giant screenshot suites.

## 8. Android device verification

Approved minimum is Android 11 / API 30.

Manual real-device UX testing is now the remaining Phase 3 gate. Check at least the actual relevant phone/tablet form factor(s) when practical. For the current slice, verify:

1. launch the application;
2. confirm the campaign screen is presented in Spanish;
3. create at least two campaigns;
4. confirm blank/whitespace-only names cannot be submitted;
5. select the first campaign and verify it is visibly active;
6. select the second campaign and verify active selection changes;
7. close/restart the app and verify both campaigns and the active selection remain;
8. confirm the simple layout is usable on the intended phone/tablet form factor(s).

Automated UI infrastructure is not required merely to prove this slice.

## 9. Future focused verification

As later features actually appear, add tests for consequential SQLDelight migrations, sync/outbox/idempotency, combat sequence authority, backend auth/authz, PDF behavior, and SRD grounding. Do not create tests for infrastructure or behavior that does not exist.

## 10. Regression rule

When fixing a reproducible defect, add a focused automated regression test when practical/useful. Otherwise record the manual verification used.

## 11. Proportionality

C-0009 controls testing: protect this application's useful behavior and data, not an imaginary commercial compliance process.
