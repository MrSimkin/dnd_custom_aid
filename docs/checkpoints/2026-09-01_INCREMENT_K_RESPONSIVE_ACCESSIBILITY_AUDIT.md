# Increment K — Responsive + accessibility audit

**Date:** 2026-09-01  
**Working branch:** `tmp/increment-k-responsive-accessibility`  
**Verified baseline:** `2fe7ab0bc6ce18f3956bda0ed750f433367e483b` — Increment J closure, durable promoted CI run `33466075487` fully green

## Authority

D-0064 and the consolidated next-build package control this pass. Increment K is an integrated responsive/accessibility correction pass, not a redesign.

Automated/static verification and intended-device visual/ergonomic QA are separate. Code inspection/CI may establish structure, semantics, persistence and compilation; it cannot honestly establish phone rendering at every text scale/orientation or drag feel.

## Static audit — already satisfied

### Supported text scales

`UiPreferences.kt` exposes exactly the approved app scales: **80, 90, 100, 115, 130**. The selected percentage multiplies `LocalDensity.fontScale` rather than shrinking fixed layout dp values.

### Top-level navigation

`CharacterTopTabStripV4` uses a single Material `ScrollableTabRow` with one-line, non-wrapping labels. Material's selected-tab index drives the scrollable row, while top-level selection itself is `rememberSaveable` in the editor. Spellcaster OFF already resolves an invalid selected `Conjuros` tab deterministically to `General`.

### Habilidades — Por atributo

The grouped attribute layout uses **2 columns on narrow layouts and 3 on wide layouts**. It does not collapse to one column based on font scale, so the approved two-column concept remains structurally intact at 115/130%.

### Wide layouts

- `Trasfondo` uses two-column narrative previews on wide layouts and keeps Story full-width/generous.
- `Rasgos` uses two trait cards per row on wide layouts.
- `Notas` keeps the unrestricted general-notes area generous and increases wide margins; titled cards remain an ordered vertical list to avoid ambiguous drag semantics. D-0064 allows, but does not require, titled-note multicolumn layout.
- `Conjuros` uses the available width for its list and compact summaries without permanently expanding descriptions.

### IME/outside-tap safety for new-domain editors

The new active editors inspected in `Trasfondo`, `Rasgos`, spell details, spell-source edit, and titled Notes use IME/navigation-bar padding where needed and block outside-tap dismissal while unsaved editor state is active (`onDismissRequest = {}`). Explicit Apply/Cancel controls remain the state boundary.

### Existing vector/semantic controls

Back/settings controls use Canvas vector drawings inside Material `IconButton`; drag handles use Canvas with content descriptions. Source management already uses the semantic settings control.

## Static audit — code corrections required

### K1 — selected Conjuros source is not auto-kept visible

The current subordinate source strip uses a plain horizontally scrolling `Row`. It is scrollable, but changing/retaining selection does not programmatically bring the selected source fully into view. D-0064 explicitly requires this.

**Correction:** use a keyed `LazyRow`/lazy-list state and scroll to the selected stable source-ID item whenever selection/source ordering changes. Bound very long source labels and use ellipsis instead of allowing one custom name to consume the entire strip.

### K2 — drag handle touch target is 40 dp

`StableDragHandle` is currently 40 dp. It carries semantics and gesture input but is below the conventional 48 dp touch target used by Material icon controls.

**Correction:** enlarge the handle surface to 48 dp while retaining the same visual three-line glyph and long-press drag behavior.

### K3 — Unicode/text glyph pseudo-buttons remain in V4 controls

The class editor still uses icon-only `×` and `▾` text glyphs as clickable controls. The character-list FAB also renders a text `+` as its sole icon. D-0064 prohibits Unicode/text glyph pseudo-buttons for icon-only controls.

**Correction:** add reusable Canvas vector remove/dropdown/add glyphs with semantics and adequate touch targets, then replace the icon-only text-glyph controls. Text labels such as `+ Añadir` are not icon-only controls and may remain textual actions.

### K4 — create-character dialog can discard typed name on outside tap

`CreateCharacterDialog` currently delegates `onDismissRequest` to dismissal. A typed character name can therefore disappear after an outside tap/back-dismiss without an explicit Cancel action.

**Correction:** block implicit dialog dismissal while the editor is active and preserve explicit Create/Cancel as the state boundary, matching the new-domain editor safety rule.

## Manual-only checks retained for owner QA

CI/static inspection cannot honestly certify:

- actual rendering at 80/90/100/115/130% on the intended phone;
- portrait/landscape visual balance;
- whether selected tab/source auto-scroll feels natural rather than merely being wired;
- drag feel at 130%;
- keyboard/IME behavior on the intended device/keyboard;
- whether 48 dp targets feel appropriately spaced in every row.

These remain C-0010 owner phone QA and will be carried to Increment L.

## Implementation safety

Most K corrections live in small files (`IconControls.kt`, `CharacterSpellsTabV4.kt`, `CharacterUi.kt`). The `CharacterEditorV4.kt` pseudo-controls are in the historical high-risk large file and must be changed only through an asserted exact-match patch that fails closed on mismatch.

## Gate K

After corrections:

- run the normal backend + shared tests + Android + desktop gate;
- add focused pure/static regression coverage only where it materially tests a reusable policy rather than pretending to test visual ergonomics;
- record all manual-only responsive checks explicitly as pending, not PASS.

## Next exact step

Implement K1–K4 with narrow changes, run Gate K, and promote only after a green descendant-only result. `main` remains untouched.