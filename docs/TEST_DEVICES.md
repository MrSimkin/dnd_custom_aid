# Owner test devices

This file records owner-confirmed physical devices and emulator inventory available for visual audition and QA. Do not infer unrecorded OS/display settings from model/profile names; add them only when they become material to a finding.

## Primary physical phone

- **Role:** primary owner phone test device
- **Owner-reported model:** Redmi Note 11 Pro 5G
- **First recorded:** 2026-09-07
- **Current known QA build family:** `0.4.0-preqa.15` / build `41500` / `DEBUG QA`
- **Observed hosted-QA use:** used successfully for hosted campaign/PC convergence and membership revoke/reinstate physical gates
- **Representative test data:** available
- **Android version / system display scale / exact effective viewport:** not yet recorded; do not guess

## Physical tablet

No owner physical tablet test device has been recorded yet.

## Android Studio emulator inventory

**Owner-reported 2026-09-16:** Android Studio currently has **4 available emulators**:

- **2 phone emulators**;
- **2 tablet emulators**.

Their exact AVD profiles, Android/API versions, resolutions, density/display settings and orientation defaults have not yet been recorded. Do not invent those details; capture them only when a specific QA task needs them.

These emulators are available for multi-form-factor development/QA and can be useful for phone/tablet comparisons, multi-client scenarios and reproducible diagnostics. They do **not** replace a required physical-device acceptance gate when the relevant QA plan explicitly requires physical hardware.

## Usage rule

When a concrete visual/IME/layout finding is recorded, include the device or emulator profile, orientation, app font, application text scale, application spacing scale and card-column preference. Add OS/system display details only when they materially affect reproduction.

For synchronization/multi-client findings, also identify which client/device instance produced each state so emulator-to-emulator, emulator-to-phone and physical-device observations are not conflated.
