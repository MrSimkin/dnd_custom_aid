# Testing and Verification

## Current status

Phase 3 is complete and canonical on `main` after PR #5. The local Android campaign create/select slice passed focused automated verification plus manual phone and tablet checks. Post-merge CI also passed on the canonical merge commit. D-0043 remains controlling: testing protects material risks without turning this personal project into an enterprise test program.

C-0010 now adds a repeatable manual QA rule: after a build reaches a manual-testable state, execute the defined post-build QA suite on the feature's intended primary device/form factor before calling the feature manually accepted. The reusable suite lives in `docs/QA_CHECKLIST.md`.

## 1. Core rule

Never claim a test passed unless it was actually executed successfully against the relevant revision.

Every meaningful implementation change should state concisely:

- what was tested;
- how it was tested;
- what passed or failed;
- what was not tested when that matters;
- relevant environment/device information when it matters.

Automated verification and manual intended-device acceptance are separate gates. Green CI does not by itself mean a feature has passed manual QA.

## 2. Standard verification commands

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

## 3. Phase 3 final verification

PR #5 final review head:

`124626aa6f0fabd449ee5823c1651e3cc01f3e70`

The pull-request GitHub Actions workflow passed on that head.

PR #5 was then merged into `main` as:

`dc1304080f0b71bcb44690b5ee317f3877385286`

GitHub Actions run #34 passed on that canonical merge commit:

- shared Kotlin compilation — success;
- campaign repository tests — success;
- SQLDelight generation — success;
- Android debug APK assembly — success;
- Desktop build — success;
- Android debug APK artifact upload — success;
- backend Wrangler/TypeScript check — success.

The campaign persistence test closes SQLite, reopens the same database file, and verifies that the stored campaign and active selection survive the reopen.

Documentation-only Phase 4 transition commits made after the merge also trigger the same CI workflow but do not change application behavior.

## 4. Phase 3 behavior covered by automated tests

- creating a campaign with a nonblank trimmed name succeeds;
- blank/whitespace-only campaign names are rejected;
- multiple campaigns persist independently;
- duplicate display names are allowed without identity collision;
- selecting an active campaign persists locally;
- changing the active campaign replaces the previous active selection;
- reopening database storage recovers campaigns and active selection.

No tests were added for hosted synchronization, membership, roles or other features excluded from the slice.

## 5. Manual Android verification

Approved minimum is Android 11 / API 30.

On 2026-08-30 the owner manually verified the Phase 3 CI-built debug APK on an Android phone and tablet.

Confirmed:

- installation and launch succeeded;
- campaign screen was in Spanish;
- campaign creation worked;
- active-campaign selection worked;
- campaigns and active selection survived app restart;
- phone layout was functional;
- tablet layout was functional and not excessively stretched.

Non-blocking observations:

- current UI has more empty/dead space than desired;
- tablet landscape underuses horizontal space;
- richer future screens will provide a better basis for adaptive-layout decisions;
- theme support is desired for future UI work.

These observations did not fail the Phase 3 acceptance criteria.

## 6. Scaffold baseline verification

Final Phase 2 scaffold branch head:

`2f8746de1053bf97cc18d7a522f2027e91879251`

GitHub Actions run #14 passed on that exact revision before PR #4 merged it into `main` as `d50409270db52df05508f91363bf76385030a77d`.

## 7. Known non-blocking tooling notes

The KMP `androidLibrary` target DSL used by `shared` currently emits a deprecation warning. The current official Kotlin KMP application template still uses the same API, so the project deliberately does not churn a green foundation merely to silence a tooling-transition warning.

The Gradle wrapper JAR is not currently committed because the repository connector could not safely transfer the official binary JAR. CI provisions Gradle 9.5 directly. A normal local Git workflow can add the wrapper later.

Development CI APKs now use a stable project-owned **debug-only** signing identity so successive development APKs can normally update one another in place for realistic migration/persistence testing. This identity is not a production/release signing key and must never be reused as one.

## 8. CI

Use one simple GitHub Actions workflow for relevant pushes/pull requests. CI is a safety check, not deployment.

Initial CI does not require coverage gates, SonarQube, emulator farms, broad API matrices, staging, automatic production deployment, automated release publishing, or giant screenshot suites.

## 9. Post-build intended-device QA

C-0010 controls manual feature acceptance.

After a build reaches a manual-testable state:

1. run automated verification appropriate to the revision;
2. identify the feature's intended primary device/form factor;
3. execute the relevant sections of `docs/QA_CHECKLIST.md` on that intended device;
4. record pass/fail, defects and non-blocking observations;
5. use secondary form-factor checks only where useful/proportionate;
6. do not call the feature manually accepted until its intended-device QA has passed or deviations are explicitly accepted.

Current intended-device priorities include:

- player character-sheet workflow — Android **phone first**;
- DM combat tracker/live board — Android **tablet first**;
- DM preparation/administration — **desktop first**.

The QA checklist contains a small persistent regression core and a focused current-feature suite. Grow the persistent core only where a regression would materially matter.

## 10. Phase 4 verification rule

For each Phase 4 slice, add only focused automated/manual verification for behavior that actually exists and the material risks introduced by that slice.

As features appear, likely verification areas include consequential SQLDelight migrations, sync/outbox/idempotency, combat sequence authority, backend auth/authz, PDF behavior, and SRD grounding. Do not create tests for infrastructure or behavior that does not exist.

For the current character-foundation slice, the primary manual acceptance device is the Android phone. Tablet checks are secondary unless a concrete character-sheet defect or future tablet-specific character workflow makes them material.

## 11. Regression rule

When fixing a reproducible defect, add a focused automated regression test when practical/useful. Otherwise record the manual verification used.

## 12. Proportionality

C-0009 controls testing: protect this application's useful behavior and data, not an imaginary commercial compliance process.
