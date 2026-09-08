# Phase 4A — Increment C1 Character-First Entry

Date: 2026-09-08

## Status

**C1 implementation checkpoint: COMPLETE / AUTOMATED GATE GREEN.**

This checkpoint implements the owner-approved character-first application entry without introducing a duplicate persisted character-summary model.

It does **not** imply owner visual acceptance of the new successor UI.

## Branch and verified product head

Implementation branch:

`implementation/phase4a-successor-cycle`

Verified product head:

`399ac4f6fc7ae48cd1c8ec425aded4db917e419b`

Commit:

`feat: make character directory the application entry`

Workflow:

`34292608435`

Result:

- backend check: PASS;
- Kotlin/shared tests: PASS;
- Android debug compilation/assembly: PASS;
- desktop build/verification: PASS;
- Android debug APK upload: PASS.

`main` remains outside successor implementation.

## C1 behavior

### Application entry

The application now starts on a global `Personajes` directory rather than forcing campaign selection first.

The root directory reads characters across campaigns and opens a character directly by durable character ID.

### Character cards

Each character card projects canonical data and shows at least:

- character name;
- `Raza`, from `CharacterSheet.background.race`;
- class(es) + level(s), using the existing class-summary presentation helper;
- campaign name, joined through the character's canonical `campaignId`;
- lifecycle status and freshness as secondary compact context.

No list-only summary record is persisted.

### Global character read path

Added a dedicated read-only SQLDelight query selecting all character IDs, plus `CharacterDirectoryRepository` that hydrates each ID through the existing canonical `CharacterRepository`.

This avoids duplicating character mapping logic and requires no schema migration.

A focused desktop regression test creates characters in two different campaigns and verifies that the global directory returns both while preserving canonical campaign, `Raza`, class and level data.

### Create and import

Because the directory is no longer campaign-scoped:

- creating a character explicitly chooses its destination campaign;
- importing a character backup explicitly chooses the destination campaign;
- imported characters remain independent copies through the existing backup repository;
- if no campaigns exist, the primary add action routes to campaign administration first.

### Campaign administration

Campaigns remain available as a secondary administration surface from the character directory.

The campaign screen manages campaign creation and active-campaign selection; it is no longer the primary application obstacle before opening a character.

### Android Back hierarchy

The successor root no longer consumes Android Back.

Expected hierarchy is now:

- character editor → global character directory → system exit;
- campaign administration → global character directory → system exit;
- Application Settings → dismiss to the underlying current surface.

This preserves the previously owner-validated principle that internal screens navigate inward-to-root before the system exits the app.

## Files

Added:

- `shared/src/commonMain/sqldelight/io/github/mrsimkin/dndcustomaid/shared/db/CharacterDirectory.sq`
- `shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterDirectoryRepository.kt`
- `shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterDirectoryRepositoryTest.kt`
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterDirectoryUi.kt`

Updated:

- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/MainActivity.kt`

The old campaign-scoped character-list implementation remains in source for now as historical/internal code and is not the application entry.

## Acceptance boundary

C1 is technically green and suitable as the navigation foundation for C2–C5.

It does **not** mean:

- owner has visually accepted the new character directory;
- successor phone density is accepted;
- tablet/wide presentation is accepted;
- the open card-drag stiffness finding is closed;
- a replacement formal M6 candidate exists.

## Exact next action

Proceed to **C2 — PC Settings information architecture**:

1. keep character identity/lifecycle/status and safe character-level actions at the top;
2. move functional module/theme/tab visibility/order configuration lower;
3. remove operational/combat-state editing from PC Settings where it belongs to operational tabs;
4. preserve Application Settings as a separate route;
5. keep lifecycle transitions and destructive actions safe and explicit.

Then continue C3/C4 General and C5 Habilidades as the remaining coherent Increment C surface work.