# Checkpoint — Android in-app DEV tools launch repair

**Date:** 2026-09-24 (Chile local time)  
**Integrated main entering repair:** `780c5e2f7063d901747688198441d310df01495e`  
**Observed build:** `0.5.0-preqa.1`  
**Owner-observed QA stage:** Stage 1 — in-app DEV tools entry

## Observed failure

Stage 0 passed: the repaired Android build installed correctly and reported `0.5.0-preqa.1`.

At Stage 1, tapping:

`Configuración de la aplicación -> Herramientas DEV -> Abrir QA / diagnóstico DEV`

caused an immediate app crash before the QA activity opened.

No sync, authentication, PC mutation or conflict action was attempted after the crash.

## Root cause

The debug Settings button constructed the target activity class from `context.packageName`:

`io.github.mrsimkin.dndcustomaid.HostedDevAuthActivity`

The actual Kotlin activity class lives under the Android source namespace:

`io.github.mrsimkin.dndcustomaid.android.HostedDevAuthActivity`

Therefore the explicit Intent targeted a non-existent class and failed when launched.

This is a navigation/component-resolution defect only. It does not invalidate the previously verified hosted Gmail Player session or converged hosted-PC data.

## Repair

The launch target now derives the activity class from the actual `MainActivity` class package while retaining the installed application ID as the component package.

A static application-settings guard now requires the correct class-package derivation and rejects the former applicationId-derived activity class.

The replacement QA package advances to:

- version name: `0.5.0-preqa.2`;
- version code: `50200`.

## Manual continuation

After merged-main CI is green:

1. update the existing emulator installation without uninstalling it;
2. confirm `0.5.0-preqa.2` in Acerca de;
3. reopen `Herramientas DEV -> Abrir QA / diagnóstico DEV`;
4. if the screen opens, continue Stage 1 with `Probar sesión recordada`;
5. then run one QA sync and copy the complete QA log;
6. do not use conflict resolution unless an actual conflict is reported.

The Aldren PDF manual retest remains pending until Stage 1 is restored and verified.
