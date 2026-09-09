# Phase 4A — Increment F: Conjuros compact source context

**Date:** 2026-09-09  
**Branch:** `implementation/phase4a-successor-cycle`  
**Status:** REPAIR AUTOMATED-GREEN / TARGETED OWNER DEVICE RETEST PENDING  
**Pre-F baseline:** `f7f0389fde4fd3a72ca8f8a547dae38255825266`  
**Original Increment F source commit:** `0e25fb0a84c2be63a16fee709bd7e96d7469373b`  
**Original authoritative workflow:** `34376169597` — SUCCESS, followed by owner-device retest failure  
**Repaired product source commit:** `8bb328153bb0f73ef220afdbf0f53407efe9b2e6`  
**Repair residual-audit cleanup commit:** `bf0ae2c`  
**Repair integrated workflow:** `34392690411` — SUCCESS

## Scope implemented

Increment F replaced the vertically expensive Conjuros source-selector/header stack with one compact collection context while preserving the existing source model and per-source spellcasting authority.

### F1 — compact source context

- the permanent horizontal source `LazyRow` and separate divider/settings footprint were removed;
- source selection now lives inside the shared compact collection toolbar;
- the selected source displays its own casting ability abbreviation, spell save DC and spell attack modifier;
- `Todos los conjuros` intentionally shows no fake/global casting statistics;
- source selection and `Gestionar fuentes…` are transient dropdown actions;
- ordinary spell-list content receives more permanent viewport height, including phone landscape.

### F2 — compact search/filter/add behavior

`CharacterCollectionToolbarV4` uses compact opt-in controls:

- `collapsibleSearch = true` opens Conjuros search in the same fixed toolbar row;
- while search is expanded, source context and ancillary controls are temporarily replaced by the search field plus `Cerrar`;
- non-empty collapsed search remains indicated by `Buscar •`;
- `showItemCount = false` preserves horizontal room;
- sort uses a compact shared icon control while preserving Manual/A–Z functionality;
- filters and add remain functionally available;
- other toolbar consumers retain previous behavior by default.

### F3 — source authority preserved

- no new shared/domain state was introduced;
- no generic free-text `Fuente` field was added to the spell-source model;
- spell sources remain `CharacterSpellcastingSource` identities that may link to a class or remain custom/unlinked;
- source add/edit/delete/reorder semantics remain intact;
- source-specific spellcasting values continue to project from the canonical successor spellcasting profiles through `generalSpellcastingRows(successorState)`;
- no global spellcasting ability/DC/attack authority was recreated.

### F4 — editor numeric normalization

- spell-level input uses shared `normalizeCharacterUnsignedIntegerInput(..., maxDigits = 1)` in both editor paths;
- the old private digit-taking workaround was removed;
- shared tests cover `05 -> 5` and single-digit behavior.

### F5 — contextual help

- source-system explanatory copy uses the existing `CharacterHelpV4` authority;
- the source dropdown explains that casting ability, save DC and spell attack belong to each source.

## Original automated boundary and failed owner retest

The original F implementation passed workflow `34376169597` on validation commit `4ba248f7749a062ac40e1e0c46c0687f4caccbbf` and produced artifact `10114037181`.

The first Redmi Note 11 Pro 5G physical retest then **failed owner acceptance**. Blocking findings were:

- spell drag/reorder could skip two cards;
- reorder incorrectly depended on a visible drag handle rather than whole-card long-press/drag;
- raw Unicode favorite-star presentation was visually unacceptable;
- portrait toolbar controls were awkwardly compressed;
- spell-card actions consumed unnecessary vertical tiers;
- spell editor left naturally compatible short controls on separate rows;
- keyboard still obscured editor content despite the prior shared IME wrapper.

Technical diagnosis confirmed a private spell-row `detectDragGesturesAfterLongPress` path, fixed `66.dp` threshold, multi-step `while` behavior, stale callback risk, raw star text, stacked action tiers, and a non-filling weighted IME body.

## F repair pass completed

The reopened repair pass addresses the diagnosed blockers without changing canonical spell/source authority.

### R1 — whole-card measured stale-safe drag

- `SpellRowG2` uses shared `characterMeasuredReorderDragV4` on the card surface;
- no spell-card drag handle is required;
- the shared low-level detector keeps callbacks current through `rememberUpdatedState`;
- reorder threshold is derived from rendered card height;
- one pointer update can cause at most one logical move;
- accumulated distance resets after a successful/blocked step;
- the old spell-row fixed `66.dp`, private detector and multi-step `while` path are gone.

Physical drag feel remains an owner-device audition item; automated evidence cannot establish tactile acceptance.

### R2 — compact stable card actions

- raw favorite `★/☆` button controls were replaced app-wide with `StableFavoriteIconButton`;
- duplicate and remove actions use shared icon controls;
- spell favorite/duplicate/remove controls share the primary card row instead of forming a vertical action tower;
- equivalent obvious secondary-card action towers were flattened in Forms, Companions, Artifice and class-option surfaces;
- the residual audit found zero raw Unicode favorite controls and zero remaining `Text("Duplicar")` controls in Android character UI.

### R3 — responsive collection toolbar

- expanded Conjuros search receives the toolbar row except for its close control;
- source context and ancillary order/filter/add controls are suppressed while search is expanded rather than compressed into the same width;
- compact sort control preserves Manual/A–Z behavior;
- collapsed active search remains visible as state without adding a permanent row.

### R4 — editor row efficiency

Conjuros now pairs naturally short fields:

- `Nivel + Tiempo de lanzamiento`;
- `Alcance + Duración`;
- V/S/M remain a compact boolean row;
- Concentración/Ritual remain a compact boolean row.

The same app-wide compactness rule was applied conservatively to existing short editor metadata where readability remains sensible on phone width, including:

- `Raza + Religión / Fe`;
- combat `Ataque + Alcance`;
- sense `Sentido + Alcance`;
- movement `Nombre + Velocidad`;
- custom characteristic `Abreviatura + Puntuación`;
- Forms `Fuente + CR`;
- Artifice/class-option `Fuente + Coste`.

Long descriptive fields remain full-width.

### R5 — shared IME/window geometry and compactness

`CharacterImeSafeEditorDialog` now:

- uses full available dialog height after IME/navigation insets;
- keeps the editor body as a real `weight(1f)` scroll region;
- keeps save/cancel actions fixed outside the scrolling body;
- uses compact spacing through the shared `appSpacingV4` authority.

Shared confirmation/editor/settings window interiors were also tightened. Some raw container/control paddings intentionally remain because the residual audit separates visual-density opportunities from required interaction hit geometry; they were not mass-scaled blindly.

### R6 — text-size observation audit

The owner's observation that text size appeared not to affect window text was explicitly audited:

- Android UI has zero explicit `fontSize = ...` overrides;
- application text scaling is applied through `LocalDensity.fontScale` above `MaterialTheme` in `DndCustomAidTheme`;
- dialogs are therefore structurally expected to inherit the same font scale as ordinary screen content.

This remains a **physical verification item**, not a closed visual finding. The Redmi retest must compare actual glyph size inside an editor at a clearly small setting (70%) and large setting (160%). If glyphs do not visibly change, investigate the Compose dialog/window boundary. If glyphs change but fields remain visually tall, treat that separately as Material text-field internal-frame/padding geometry rather than inventing a second text-size authority.

## Residual audit

One self-removing repository audit scanned 60 Android Kotlin files and passed all critical F invariants:

- whole-card measured spell drag: PASS;
- legacy private/fixed-step spell drag in `SpellRowG2`: absent;
- compact Conjuros search/sort/source wiring: PASS;
- shared numeric normalization: present;
- IME-safe full-height/weighted-scroll geometry: PASS;
- raw Unicode favorite controls: `0`;
- explicit Android `fontSize` overrides: `0`;
- text `Duplicar` controls: `0`;
- IME-safe editor caller files: `23`.

The audit reported 42 raw horizontal+vertical `dp` padding pairs elsewhere. They are recorded as non-blocking density findings, not automatically rewritten, because some belong to interactive custom surfaces where blind spacing reduction could violate required touch geometry.

Audit workflow `34392690399` — SUCCESS. The helper self-removed in commit `bf0ae2c`; no temporary audit workflow remains.

## Repaired integrated gate

Workflow `34392690411` — **SUCCESS** on head `6f0d09b2bf00b3f9ace8c10af0dd56dd87ed97a6`.

That validation head contains the repaired product code from source commit `8bb328153bb0f73ef220afdbf0f53407efe9b2e6` plus only the transient residual-audit helper files; the helper was then removed without changing product code.

Verified together by the repository's normal `Scaffold checks` workflow:

- backend/type-check: PASS;
- `:shared:desktopTest`: PASS;
- Android debug assembly: PASS;
- desktop build: PASS;
- Android debug APK upload: PASS.

Artifact:

- ID `10120351510`;
- name `dnd-custom-aid-debug-apk`;
- uploaded ZIP size `13,230,630` bytes;
- ZIP digest `sha256:6461c90cde69ffa0b3e255721f040553da3acec7041dbeb5216e3e801c49d83d`;
- generated `2026-09-09T19:05:15Z`;
- extracted APK size `37,718,132` bytes;
- extracted APK SHA-256 `5ec1e17298aa6d31fbdb84be0c7bbdd2a8c2ebf7c1fb47994e572207cb998d72`.

## Exact next action — targeted owner repair retest

Do **not** begin Increment G yet.

Install the repaired APK on the Redmi Note 11 Pro 5G and test, at minimum:

1. Conjuros portrait: source context, search, sort, filters and add remain understandable without awkward compression;
2. Conjuros landscape: ordinary spell-list content is visible; controls do not consume the usable viewport;
3. search opens in the same toolbar footprint and does not stack a new permanent row;
4. collapsed non-empty search remains visibly indicated;
5. source selector shows per-source aptitud/CD/ataque correctly; `Todos` does not invent global values;
6. custom/unlinked source and linked-class source behavior still work;
7. prepared/list/source filtering behaves correctly;
8. whole-card long-press drag no longer skips cards and does not require a visible handle;
9. card drag feel is physically acceptable or produces a precise remaining tactile finding;
10. favorite/duplicate/remove actions are visually acceptable and not vertically wasteful;
11. spell editor short-field rows remain usable in portrait;
12. keyboard does not hide the focused usable editor area or save/cancel actions;
13. changing a spell level from default `0` by entering `5` behaves correctly through shared normalization;
14. window/editor interiors feel materially more compact without making taps impractical;
15. compare editor/window glyphs at text size 70% versus 160% and report whether the glyph size itself changes.

This is an **early targeted F-repair retest**, not final Phase 4A acceptance and not tablet acceptance.

## Acceptance boundary

Increment F repair is now **automated-green**, but owner-device repair acceptance is pending. Do not start Increment G, do not infer tablet acceptance, and do not infer Phase 4A closure until the owner performs the targeted Redmi retest and the remaining F blockers, if any, are reconciled.