# Phase 4A — Increment H full-screen Application Settings

**Date:** 2026-09-09  
**Branch:** `implementation/phase4a-successor-cycle`  
**Canonical branch:** `main` (unchanged by Increment H)  
**Status:** COMPLETE IMPLEMENTATION / H1–H2 FOCUSED GREEN / INTEGRATED GATE PENDING / OWNER ACCEPTANCE PENDING

## 1. Boundary

Increment H implements the approved Application Settings redesign from D-0067 and the reconciled A–I successor plan. It is UI/application-preference work; it does not create a second character-data authority and does not begin the separate tablet redesign assigned to Increment I.

The completed Increment G automated boundary remains valid. Repaired Increment F's targeted Redmi owner retest also remains outstanding unless separate owner evidence later closes it. Automated implementation progress must not be misreported as owner/device acceptance.

## 2. Product commits

Focused product commits:

- H1: `e6a68da696bb48648242303254b347316da6ceeb` — `feat: add Increment H1 full-screen application settings`;
- H2: `a64ed207d906ae9aea695da34d01ded0e2ccf32a` — `feat: add Increment H2 settings previews and themes`.

Relative to finalized Increment G documentation head `0377681a75de749c7d2e08b1b8984dea0014f86a`, the H product tree changes exactly four Android files:

- `MainActivity.kt`;
- `UiPreferences.kt`;
- `CharacterPcSettingsContextV4.kt`;
- `CharacterPcSuccessorSettingsV4.kt`.

Temporary one-off helper workflows/scripts self-removed after successful focused commits. At the H2 product boundary `.github/workflows` again contains only the normal `scaffold-check.yml` workflow.

## 3. H1 — full-screen settings, sliders and haptic ownership

Implemented and focused-green:

- `Configuración de la aplicación` is now a full-screen application surface instead of an `AlertDialog` settings window;
- the underlying current app screen remains composed while the settings surface is open, preventing settings navigation from discarding an in-progress character editor composition/draft;
- Android Back and the visible back affordance close the full-screen settings surface and return to the prior screen;
- text size uses a discrete stepped slider with the previously supported broad 70–200% values, explicit current/min/max values and a live representative sample;
- spacing compactness uses a discrete stepped slider with the approved values including 40%, explicit current/min/max values and a live representative sample;
- preference persistence semantics remain compatible: the same integer values are stored and the same existing keys continue to load/save;
- help mode and dice-result presentation remain global Application Settings preferences;
- device-wide haptic strength/duration are now provided once at the application root and edited from Application Settings;
- PC Settings retains only the per-character haptics-enabled switch plus a read-only summary of the current device haptic profile, avoiding two editing surfaces for the same device-wide preference;
- the existing bounded `Suave/Media/Fuerte` and `Corta/Media/Larga` haptic choices remain intact.

Focused H1 workflow: `34415832735` — SUCCESS.

## 4. H2 — visual previews, labels and theme families

Implemented and focused-green:

- owner-requested visible theme renames:
  - `Morado oscuro` -> `Púrpura`;
  - dark cyan -> `Cyan`;
  - light cyan remains distinct as `Cyan claro`;
  - `Azul noche` -> `Noche`;
  - `Azul noche claro` -> `Noche despejada`;
  - `Verde bosque` -> `Bosque`;
  - `Verde bosque claro` -> `Oasis`;
- six additional selectable audition families were added with explicit light/dark contrast palettes:
  - `Carmesí`;
  - `Ámbar`;
  - `Glaciar`;
  - `Lavanda`;
  - `Pizarra`;
  - `Terracota`;
- existing theme enum identifiers remain unchanged for prior themes, so saved theme names remain compatible while their visible labels improve;
- normal font selection no longer shows provider/source metadata or `audición` framing;
- each font choice now shows the font name plus an immediate representative rules/numeric sample in that font;
- theme cards now render a miniature character-sheet fragment using each candidate's own background, primary-container, surface-variant and primary/on-colors instead of showing only three color swatches;
- each phone/tablet orientation column selector now includes a live mini-grid reflecting the chosen number of columns and current spacing preference;
- the existing full-sheet preview remains and continues to update immediately with theme/font/text/spacing/help/dice changes.

Focused H2 workflow: `34416164922` — SUCCESS.

## 5. Focused validation status

Both H1 and H2 successful product boundaries ran:

- `:shared:desktopTest` — PASS;
- `:androidApp:compileDebugKotlin` — PASS;
- guarded diff checks for the intended product properties.

These focused checks are not the final Increment H integration gate.

## 6. Integrated gate — pending

This checkpoint commit intentionally triggers the repository's normal `Scaffold checks` workflow against the complete H1–H2 product tree.

Required authoritative H gate:

- shared/Kotlin desktop tests;
- Android debug assembly;
- desktop application build;
- backend dependency install/type-check;
- Android debug APK artifact upload.

Workflow ID, validation head and artifact evidence will be written here after the normal workflow finishes successfully. Until then, do not describe Increment H as integrated-green.

## 7. Acceptance boundary

Even after the automated integrated gate becomes green:

- Increment H is not owner/device accepted merely because CI passes;
- theme visual preference/contrast remains an owner audition item, particularly the six newly delegated families;
- full-screen settings behavior, slider feel, 70%/160%/200% text behavior, 40% spacing and haptic differences remain appropriate real-device checks;
- repaired Increment F targeted phone acceptance remains a distinct outstanding boundary;
- tablet/wide acceptance remains deferred to Increment I, which is a redesign rather than an acceptance pass over the old layout.

## 8. Next implementation position

After a successful integrated H gate and documentation finalization:

- Increment I — separate tablet portrait/landscape redesign — is the next and final planned A–I successor implementation increment;
- no DM implementation begins before later owner acceptance, formal closure work and explicit Phase 4A closure.
