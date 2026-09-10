# Phase 4A — Increment I tablet portrait/landscape redesign

**Date:** 2026-09-09  
**Branch:** `implementation/phase4a-successor-cycle`  
**Canonical branch:** `main` (unchanged by Increment I)  
**Status:** IMPLEMENTATION COMPLETE / I1–I2 FOCUSED GREEN / LAYOUT INSPECTION COMPLETE / INTEGRATED GATE PENDING / PHYSICAL OWNER TABLET ACCEPTANCE PENDING

## 1. Boundary

Increment I is the final planned A–I successor engineering increment. It redesigns tablet portrait and tablet landscape as first-class compositions while reusing the same canonical character state, tab composables and shared interaction primitives proven on phone.

It intentionally does **not**:

- stretch one generic `wide` shell across both tablet orientations;
- create a tablet-specific character-data authority;
- add persistent empty detail panes merely because width exists;
- change phone portrait/landscape navigation policy;
- begin DM implementation;
- claim physical owner tablet acceptance from compilation or CI.

Increment H's finalized automated boundary remains intact. Repaired-F targeted phone acceptance and later consolidated owner acceptance remain separate outstanding boundaries.

## 2. Pre-I baseline and product commits

Finalized H documentation/cleanup head before I helper staging:

`4f19a94978e8cfdb69da0c7fd57417da3b87b584`

Focused I product commits:

- I1: `c84d9774cf21d29b91982a7f129ea32345133cc5` — `feat: add Increment I1 tablet-first adaptive shell`;
- I2: `c11ed70260f054a99c7ab5e38f2f772a697db886` — `feat: make tablet detail panes contextual in Increment I2`.

Relative to the finalized H baseline, the I product tree changes exactly nine Android UI/layout files:

- `CharacterAdaptiveShellV4.kt`;
- `CharacterLayoutContextV4.kt`;
- `CharacterResponsivePreferencesV4.kt`;
- `CharacterSpellListClosureV4.kt`;
- `CharacterEquipmentClosureV4.kt`;
- `CharacterArtificeModuleV4.kt`;
- `CharacterFormsModuleV4.kt`;
- `CharacterClassOptionModulesV4.kt`;
- `CharacterCompanionsModuleV4.kt`.

No shared domain/storage/backend file changes belong to Increment I.

Temporary one-off helper workflows self-removed after successful focused commits. At the I2 product boundary `.github/workflows` again contains only the normal `scaffold-check.yml` workflow.

## 3. I1 — first-class tablet shell and large-text policy

The pre-I responsive detector already distinguished phone portrait, phone landscape, tablet portrait and tablet landscape using the 600dp short-side boundary. The defect was the composition policy: both tablet orientations collapsed to the same fixed 112dp side-rail shell and tabs received only a generic `wide=true` signal.

I1 preserves the existing four-form-factor detector and changes the tablet composition policy:

### Tablet portrait

- uses the existing scrollable top-tab navigation rather than a side rail consuming portrait width;
- renders tab content in a centered tablet portrait canvas capped at 1040dp;
- retains app spacing controls inside that canvas;
- keeps the actual tab subtree under a saveable-state holder keyed by tab, preserving practical search/filter/editor state rather than creating a parallel tablet editor.

### Tablet landscape

- uses a dedicated side-rail composition;
- rail width responds to effective text scale: 116dp normally, 132dp from 130%, 148dp from 160%;
- rail labels may occupy two lines rather than being forced into one-line ellipsis;
- renders content in a centered landscape canvas capped at 1480dp;
- uses the same saveable tab subtree as portrait.

### Phone boundary

Phone portrait and phone landscape keep the pre-I top-tab path. I does not reinterpret phone landscape as tablet UI.

### Large-text card policy

Saved per-orientation column preferences remain canonical. I adds only a presentation clamp on tablet so configured high column counts cannot make large text unusable:

- tablet portrait: from 135% cap at 2 columns; from 160% cap at 1;
- tablet landscape: from 130% cap at 3; from 150% cap at 2; from 180% cap at 1.

At lower text scales, the existing configured tablet column limits continue to apply. Phone column behavior is unchanged.

Focused I1 repair/apply workflow: `34420029306` — **SUCCESS**.

The immediately preceding first I1 helper run `34419899181` failed safely before product commit because it incorrectly invoked absent `./gradlew`; repository CI uses installed Gradle 9.5. The guarded source rewrite/diff had passed, no source commit was produced, and the corrected run reused the same guarded source proposal with the repository's actual `gradle` invocation.

## 4. I2 — contextual tablet detail panes

Layout inspection after I1 found a remaining pre-I wide-layout anti-pattern in editor-bearing collections. Conjuros, Equipo, Artífice, Formas, Técnicas/Metamagia/Pactos and Compañeros reserved a 340–420dp editor pane while browsing even when no editor was open. The idle pane showed instructional placeholder content and permanently reduced the primary collection width.

That violated the Increment I rule that a secondary pane must exist only when both regions are useful.

I2 therefore applies one shared behavioral rule across those existing tab implementations:

- while browsing, the collection receives the full available tablet canvas;
- tablet portrait uses the normal modal/IME-safe editor path even though collection cards may still use tablet column counts;
- tablet landscape uses master/detail only while an editor is actually open and structural editing is enabled;
- closing/applying the editor restores the collection to full width;
- search/filter/order/list state remains in the same composable state rather than being replaced by a new tablet-specific model;
- existing editor/domain semantics are unchanged.

Focused I2 workflow: `34420564422` — **SUCCESS**.

## 5. Layout inspection

Repository-level layout inspection was performed before declaring I implementation-complete.

Verified representative categories:

- `Notas`: extra width already creates a configurable multi-column note grid; there is no persistent empty detail pane;
- `Rasgos`: extra width already creates useful multi-column trait cards and keeps Resources projected from canonical state;
- `Trasfondo`: extra width is consumed by simultaneous image/profile/narrative cards rather than an idle editor column;
- editor-bearing long collections: the stale persistent wide editor pattern was found and corrected by I2;
- shell/navigation: portrait and landscape now make independent navigation/canvas decisions while keeping one tab subtree.

The repository does not currently carry a dedicated tablet screenshot/emulator regression harness, so this checkpoint does not claim screenshot-based or physical-device acceptance. The next acceptance phase must inspect the generated build on representative tablet dimensions/device when available.

## 6. Focused validation

Both successful product boundaries ran:

- `:shared:desktopTest` — PASS;
- `:androidApp:compileDebugKotlin` — PASS;
- guarded product-diff checks — PASS.

These focused checks do not substitute for the final full A–I integration gate.

## 7. Integrated gate — pending

This checkpoint commit intentionally triggers the repository's normal `Scaffold checks` workflow against the complete A–I successor product tree.

Required final I/A–I automated gate:

- shared/Kotlin desktop tests;
- Android debug assembly;
- desktop application build;
- backend dependency install/type-check;
- Android debug APK artifact upload.

Workflow ID, validation head and artifact evidence will be recorded here after the normal gate finishes successfully. Until then, do not describe Increment I or the A–I successor line as integrated-green.

## 8. Acceptance boundary after a green gate

A green final automated gate will mean the planned A–I engineering implementation is integrated, **not** that Phase 4A is owner-accepted or closed.

Still outstanding after automated I completion:

- repaired-F targeted Redmi Note 11 Pro 5G phone acceptance unless separately recorded;
- consolidated successor owner audition on phone, including G/H behavior and representative larger text;
- tablet portrait/landscape visual/interaction acceptance on a representative physical tablet when available;
- theme/spacing/haptic and drag-feel owner audition items;
- any blocking owner-reported repair resulting from those checks;
- new formal M6 candidate freeze/regression/upgrade QA when appropriate;
- explicit owner Phase 4A closure.

No DM feature implementation begins before that explicit closure.

## 9. Exact next position

If the integrated gate below becomes green, there is no Increment J in the reconciled successor plan. Engineering should stop adding planned scope and move to the successor audition/acceptance and closure sequence instead of inventing another feature increment.
