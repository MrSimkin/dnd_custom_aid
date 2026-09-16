# Owner test devices

This file records owner-confirmed physical devices, emulator inventory and the owner Windows QA workstation available for visual audition and QA. Do not infer unrecorded OS/display settings from model/profile names; add them only when they become material to a finding.

## QA inventory summary

**Owner-reported 2026-09-16:** the current Android QA inventory contains **3 phone-class devices** and **3 tablet-class devices**:

- **Phone:** 1 physical + 2 Android Studio emulators;
- **Tablet:** 1 physical + 2 Android Studio emulators.

This gives six available Android test instances in total. Physical and virtual coverage must remain explicitly distinguished in QA records.

## Primary physical phone

- **Role:** primary owner phone test device
- **Owner-reported model:** Redmi Note 11 Pro 5G
- **First recorded:** 2026-09-07
- **Current known QA build family:** `0.4.0-preqa.15` / build `41500` / `DEBUG QA`
- **Observed hosted-QA use:** used successfully for hosted campaign/PC convergence and membership revoke/reinstate physical gates
- **Representative test data:** available
- **Android version / system display scale / exact effective viewport:** not yet recorded; do not guess

## Physical tablet

- **Role:** owner physical tablet available for tablet-class QA
- **Model:** Lenovo Tab P11 `TB-J606F`
- **RAM:** 6 GB
- **Android:** 11
- **Owner-confirmed availability/details:** 2026-09-16
- **System display scale / exact effective viewport:** not yet recorded; do not guess

## Android Studio emulator inventory

Android Studio currently has **4 available emulators**:

- **2 phone emulators**;
- **2 tablet emulators**.

Their exact AVD profiles, Android/API versions, resolutions, density/display settings and orientation defaults have not yet been recorded. Do not invent those details; capture them only when a specific QA task needs them.

These emulators are available for multi-form-factor development/QA and can be useful for phone/tablet comparisons, multi-client scenarios and reproducible diagnostics. They do **not** replace a required physical-device acceptance gate when the relevant QA plan explicitly requires physical hardware.

## Owner Windows Desktop QA workstation

**Owner-confirmed 2026-09-16:** Desktop manual QA is currently performed on the owner's Windows 11 x64 laptop/local workstation.

Current relevant local tool/runtime inventory:

- **Repository checkout:** `D:\DnD_Aid\repo\dnd_custom_aid`
- **System Java:** Eclipse Temurin OpenJDK `25.0.4`
- **Android Studio bundled runtime:** JetBrains Runtime / OpenJDK `25.0.2`
- **System Gradle:** not installed/on `PATH` at the time of the Wave 5 Desktop manual gate
- **Repository Gradle wrapper:** no `gradlew` / `gradlew.bat` wrapper entrypoint was present at the Wave 5 Desktop manual gate
- **Portable Desktop QA JDK:** Eclipse Temurin OpenJDK `17.0.20.1+1`
- **Portable Desktop QA Gradle:** `9.5.0`
- **Portable QA tools root:** `D:\DnD_Aid\tools\desktop-qa`
- **Portable JDK path:** `D:\DnD_Aid\tools\desktop-qa\temurin17\jdk-17.0.20.1+1`
- **Portable Gradle path:** `D:\DnD_Aid\tools\desktop-qa\gradle-9.5.0`

The portable JDK 17 + Gradle 9.5.0 pair was added specifically to mirror the versions used by the repository's Scaffold GitHub Actions workflow for local Desktop QA. It is intentionally isolated outside the repository and does not replace or modify the owner's system-wide Java installation.

This tooling choice does **not** change the Desktop product architecture: Desktop source remains Kotlin with Compose for Desktop. The JDK/JVM is build/runtime infrastructure, not a decision to implement the Desktop application in the Java programming language.

For the current Wave 5 local Desktop launch, use the portable JDK only for the current PowerShell process and invoke the portable Gradle directly, for example:

```powershell
$env:JAVA_HOME='D:\DnD_Aid\tools\desktop-qa\temurin17\jdk-17.0.20.1+1'
& 'D:\DnD_Aid\tools\desktop-qa\gradle-9.5.0\bin\gradle.bat' :desktopApp:run
```

Do not assume these exact versions/paths remain current forever. Reconfirm them when the workstation/toolchain changes materially, and keep CI/local parity explicit when manual Desktop evidence depends on it.

## Usage rule

When a concrete visual/IME/layout finding is recorded, include the device or emulator profile, orientation, app font, application text scale, application spacing scale and card-column preference. Add OS/system display details only when they materially affect reproduction.

For synchronization/multi-client findings, also identify which client/device instance produced each state so emulator-to-emulator, emulator-to-phone, emulator-to-tablet and physical-device observations are not conflated.

For Desktop findings, identify the Windows workstation/toolchain used when it is material to reproduction, especially when local behavior could differ from CI or from a packaged distributable.
