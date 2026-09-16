# Desktop workbench shell + local campaign context — owner QA complete

**Date:** 2026-09-16 (Chile local time)  
**Wave:** 5 — Desktop shell + Campaign Administration  
**Branch:** `wave5/desktop-workbench-shell`  
**PR:** #42  
**Starting integrated main:** `40b29006052c9986e2d79227a6053f36241de1e6`

## Result

The first Wave 5 Desktop package is **OWNER-QA PASS / AUTOMATED VERIFIED / READY FOR MERGE AFTER FINAL EXACT-HEAD CI**.

The previous placeholder Desktop window has been replaced with a real local-first workbench shell that uses existing Shared campaign semantics. No hosted Desktop synchronization or final Campaign Administration moderation behavior is claimed by this package.

## Implemented

### Persistent Desktop local store

Shared provides `DesktopDatabaseFactory` and `DesktopDatabaseHandle` for the JVM/Desktop target.

- SQLite JDBC is used through the existing Shared dependency.
- Default owner-local database path is `~/.dnd_custom_aid/dnd_custom_aid.db`.
- the SQLDelight driver lifetime is explicit and closed when the Desktop application exits;
- the same Shared `AppDatabase` schema and `CampaignRepository` semantics are reused.

Desktop and Android still have separate local database files. Cross-device convergence remains a hosted-sync concern and is not implied by this local Desktop store.

### Workbench shell

The Desktop app provides the approved first workbench frame:

1. top toolbar;
2. left navigation;
3. central work area;
4. contextual Campaign panel where relevant;
5. bottom status strip.

Navigation includes approved long-term Desktop destinations while substantive implementation remains bounded to this package.

### Functioning destinations in this package

- **Dashboard** — local campaign count and active campaign;
- **Campañas** — local campaign list/create/active selection through Shared `CampaignRepository`;
- **Administración de campaña** — same selected campaign context, with hosted membership/moderation explicitly deferred;
- **Configuración de la aplicación** — persistent owner-local Desktop presentation preferences;
- **QA / Diagnóstico** — bounded selectable/copyable runtime, campaign, local-path, preference and session-event context.

Other workbench destinations remain honest navigation placeholders only.

## Automated verification

### Original behavior/test head

`c5e23ebbec495e3aea2b36a4cbe695c9cc586bd4`

Scaffold `35125170321` — **SUCCESS**.

### Repair head

`9417e969b4a861bc5950600ced07ed943b997f20`

Scaffold `35136378221` — **SUCCESS**.

### Pre-final current tree head

`709424d257f61ca17d7f8a7ccc604a450ca582d4`

The branch tree at this head is identical to the verified repair tree after removal of an accidental empty temporary file. Exact-head Scaffold completed successfully across:

- Shared/Desktop tests and Desktop build;
- Desktop preference persistence tests;
- Android build and permanent Player guards;
- backend type-check;
- hosted PostgreSQL contracts;
- APK artifact upload.

## Owner Windows Desktop QA — 2026-09-16

### Original behavioral gate — PASS

Owner manually verified on the recorded Windows QA workstation:

- Desktop workbench launches successfully;
- major chrome/navigation is usable with no obvious clipping in the tested views;
- local campaign creation succeeds;
- newly created campaign becomes active automatically;
- right-side campaign context matches the active campaign;
- Campaign Administration reflects the same campaign context;
- test campaign `QA Wave 5 - 2026-09-16` persisted across normal close/relaunch;
- campaign UUID remained `30609c9d-89f7-42ef-85dd-a7a35df3c506`;
- deferred destinations remained clearly labeled as deferred rather than pretending implementation;
- normal close returned the Gradle run to `BUILD SUCCESSFUL`.

### Pre-merge repairs discovered and implemented

1. **Spanish product-language pass** — user-facing Desktop UI is Spanish where appropriate. `Dashboard` and `Backup` remain accepted product terms.
2. **Desktop Application Settings** — persistent owner-local settings aligned with applicable Android concepts: text size, spacing density, font choice, theme and workspace density. Android-only haptics are not copied to Desktop.
3. **QA diagnostics / copyable data** — bounded QA surface with selectable/copyable OS/runtime, local-path, campaign, UI-preference and recent-session-event context.

### Repair visual recheck — PASS

Owner updated the local branch by clean fast-forward from the earlier QA code to current Wave 5 repair code and manually confirmed:

- repaired Desktop app opens normally;
- main visible product UI is Spanish;
- existing QA campaign remains present and active;
- Application Settings surface exposes the intended controls;
- all tested settings changed the UI successfully;
- QA / Diagnóstico exposes the intended diagnostic data;
- diagnostic block is selectable/copyable, proven by copying it directly from the app;
- diagnostic data correctly reported Windows 11, Java 17.0.20.1, local DB/preferences paths, one campaign, the active QA campaign and its full UUID, current UI values and recent session events;
- after normal close/relaunch, changed settings persisted, including `Pergamino` theme, `Monoespaciada` font and `50%` spacing density;
- final close completed normally.

The owner suggested eventually using the same richer font catalogue as Android. This is recorded as a **later genuine Desktop-development follow-up**, not a blocker for PR #42 or this package.

## Scope clarification preserved

The owner asked whether Desktop PC download/sync should already be testable. This was a clarification only and **does not change the approved plan or current package sequence**. This package remains the shell/local-campaign slice; hosted Desktop synchronization and later PC Manager/Audit behavior remain follow-up work under the existing roadmap/decisions.

## Non-goals preserved

This package does not implement:

- invitation/join/rejoin flows;
- Kick/Ban/Unban controls;
- hosted Desktop authentication/full synchronization;
- live combat authority/resume/handoff;
- substantive Managers;
- Media/Handouts/object-storage activation;
- System Administration behavior;
- backup/export/PDF hardening;
- generalized RBAC/ACL.

## Current action

Allow exact-head CI for this final documentation commit to complete. If green and the PR head remains unchanged, merge PR #42 to `main`, then verify post-merge `main` CI.
