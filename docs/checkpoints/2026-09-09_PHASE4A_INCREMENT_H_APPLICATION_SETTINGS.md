# Phase 4A — Increment H full-screen Application Settings

**Date:** 2026-09-09  
**Branch:** `implementation/phase4a-successor-cycle`  
**Canonical branch:** `main` (unchanged by Increment H)  
**Status:** COMPLETE / INTEGRATED AUTOMATED GREEN / OWNER ACCEPTANCE PENDING

## 1. Boundary

Increment H implements the approved Application Settings redesign from D-0067 and the reconciled A–I successor plan. It is application/UI preference work and does not create a second character-data authority. The separate tablet redesign remains Increment I.

Automated validation is not owner/device acceptance. Repaired Increment F's targeted Redmi retest remains outstanding unless separate owner evidence closes it.

## 2. Product commits

- H1: `e6a68da696bb48648242303254b347316da6ceeb` — `feat: add Increment H1 full-screen application settings`;
- H2: `a64ed207d906ae9aea695da34d01ded0e2ccf32a` — `feat: add Increment H2 settings previews and themes`.

Relative to finalized Increment G documentation head `0377681a75de749c7d2e08b1b8984dea0014f86a`, H changes exactly four Android product files:

- `MainActivity.kt`;
- `UiPreferences.kt`;
- `CharacterPcSettingsContextV4.kt`;
- `CharacterPcSuccessorSettingsV4.kt`.

Temporary helper workflows/scripts self-removed after their focused product commits; the normal workflow remains `scaffold-check.yml`.

## 3. H1 — full-screen settings, sliders and haptic ownership

Implemented:

- `Configuración de la aplicación` is a full-screen application surface rather than the previous settings `AlertDialog`;
- the underlying current screen remains composed while settings are open, avoiding loss of an in-progress character-editor composition/draft;
- Android Back and the visible back affordance return to the prior surface;
- text size is a discrete stepped slider preserving the broad 70–200% values, with explicit current/min/max values and a live representative preview;
- spacing compactness is a discrete stepped slider preserving approved values including 40%, with explicit current/min/max values and a live preview;
- existing preference keys/value semantics remain compatible;
- help mode and dice-result presentation remain global preferences;
- haptic strength/duration are provided once at application root and edited in Application Settings;
- PC Settings retains only per-character haptics activation plus a read-only device-profile summary;
- bounded haptic choices remain `Suave/Media/Fuerte` and `Corta/Media/Larga`.

Focused H1 workflow: `34415832735` — **SUCCESS**.

## 4. H2 — previews, labels and theme families

Implemented:

- theme renames requested by the owner: `Púrpura`, `Cyan`, `Cyan claro`, `Noche`, `Noche despejada`, `Bosque`, `Oasis`;
- added six selectable audition families: `Carmesí`, `Ámbar`, `Glaciar`, `Lavanda`, `Pizarra`, `Terracota`;
- existing enum identifiers for prior themes remain stable, preserving saved theme compatibility;
- normal font choice no longer exposes provider/source metadata or special `audición` framing;
- each font choice shows a representative rules/numeric sample in that font;
- theme cards now render a miniature sheet fragment using each theme's own background/container/surface/primary colors rather than only color swatches;
- every phone/tablet orientation column selector includes a live mini-grid reflecting the selected count and current spacing;
- the existing full-sheet preview remains live under theme/font/text/spacing/help/dice changes.

Focused H2 workflow: `34416164922` — **SUCCESS**.

## 5. Authoritative integrated gate

Validation head:

`5d287cc42331c46c8df39224348fa7380b4c1aeb`

Normal `Scaffold checks` workflow:

`34416419033` — **SUCCESS**

Verified together:

- backend dependency install/type-check: PASS;
- shared/Kotlin desktop tests: PASS;
- Android debug assembly: PASS;
- desktop application build: PASS;
- Android debug APK upload: PASS.

Artifact:

- ID `10129307655`;
- name `dnd-custom-aid-debug-apk`;
- ZIP size `13,302,532` bytes;
- digest `sha256:f103f614141b8cf8ca57280bd75243c79a5f7dc63dbed84b9b2b48a7578c9f83`.

This is the authoritative automated Increment H boundary. It is not owner acceptance.

## 6. Acceptance boundary

Physical/future owner audition should still cover:

- full-screen settings navigation from a character editor without losing in-progress work;
- stepped slider feel and live response, including 70/160/200% text and 40% spacing;
- actual glyph-size behavior inside representative dialogs/editors;
- new theme preference and practical contrast, especially the six delegated families;
- font preview/usefulness;
- haptic intensity/duration differences on real hardware while on/off remains character-specific;
- existing repaired-F phone acceptance debt;
- tablet portrait/landscape behavior only after Increment I redesign.

## 7. Next implementation position

Increment **I — separate tablet portrait/landscape redesign** is the next and final planned A–I successor implementation increment.

Do not stretch the phone layout and call it tablet design. Reuse canonical state and shared interaction primitives, then design tablet portrait and tablet landscape as first-class surfaces.

No DM implementation begins before later owner acceptance, formal closure work and explicit Phase 4A closure.
