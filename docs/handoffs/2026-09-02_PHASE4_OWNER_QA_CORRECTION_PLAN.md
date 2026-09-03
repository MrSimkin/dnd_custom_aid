# Phase 4 Owner QA — correction implementation plan

Date: 2026-09-02  
Branch: `implementation/character-data-foundation`  
Planning baseline: `93bde845bcc605ecae9cd86b08b0ac30822dea44`  
Authoritative QA record: `docs/handoffs/2026-09-01_PHASE4_OWNER_PHONE_QA_RESULTS.md`  
Designated failed owner-QA executable: Gate L artifact `9785676981`, tested commit `089a991c6491627961f1e75f3815959a8a1c8b48`.

## Purpose and boundary

This is the post-owner-QA correction map. It translates every active finding into concrete code/data/test scope before production changes begin.

No finding is considered fixed by this document. `main` remains unchanged. Phase 4 remains non-mergeable until a correction build passes automated gates and the required focused owner retest.

Historical per-check QA files are subordinate evidence; the consolidated owner QA record above is the source of truth.

## 1. Blocking corrections

### U-01 / Check 12A — Combate quick-reference vertical centering

**Primary code:** `androidApp/.../CharacterCombatTabV4.kt`.

The top reference row already uses `Alignment.CenterVertically`, but each `ReadOnlyReferenceV4` is a label-plus-value column. A wrapped label changes the total child height and makes the neighboring value surfaces appear vertically misaligned. Correct the cell/reference composition so mixed one-line/two-line labels share a stable aligned label/value geometry; do not solve this by forcing unreadable truncation.

**Retest:** Check 12A at normal scale and at least one larger text scale; include a label that wraps.

### C-01 / Check 12B — Combate editor actions unreachable with IME

**Primary code:** `CharacterCombatTabV4.kt`.

The editor is an `AlertDialog`; form fields are inside an IME-padded `LazyColumn`, while `Aplicar`/`Cancelar` live in the dialog action region outside that scroll container. Increasing list bottom padding alone cannot guarantee reachability. Move to a keyboard-safe dialog composition in which actions remain reachable/visible with the IME open, or are inside the scrollable/inset-safe content.

Preserve the successful owner-QA behavior that outside taps do not silently discard drafts.

**Retest:** Check 12B plus 12C draft-retention regression.

### E-04 / Check 13B — Equipo editor actions inaccessible with IME

**Primary code:** `CharacterEquipmentTabV4.kt`.

This has the same structural pattern as the combat editor: scrollable IME-aware dialog content with `Aplicar`/`Cancelar` outside it. Reuse the same minimal keyboard-safe dialog solution if possible instead of creating two unrelated fixes.

**Retest:** Check 13B with ordinary and special equipment; ensure lower special-item fields and dialog actions remain reachable.

### E-05 / Check 13C — Monedas too tall

**Primary code:** `CharacterEquipmentTabV4.kt`, especially `CurrencyCardV4` / `CurrencyCellV4`.

Each currency currently consumes a full bordered cell with a Material `OutlinedTextField`, and custom currencies add a separate delete action. Replace this with a materially more compact phone-first amount editor while retaining readable currency names, numeric editing, custom-currency deletion, and the existing responsive multi-column behavior. Do not reduce touch targets below accessibility minimums.

**Retest:** Check 13C portrait and landscape; verify all default currencies and at least one custom currency remain editable and persist.

### N-01 — Android system Back exits instead of navigating internally

**Primary code:** `MainActivity.kt`, plus `CharacterEditorV4.kt` for the internal PC Settings surface.

The app currently owns navigation through Compose state (`CAMPAIGNS -> CHARACTERS -> CHARACTER_EDITOR`) but has no Android `BackHandler`, so system Back falls through to Activity exit.

Required hierarchy:

1. if PC Settings is open inside a character, Back closes PC Settings;
2. otherwise from CHARACTER_EDITOR, Back returns to the character list;
3. from CHARACTERS, Back returns to campaigns;
4. only at the campaigns/root screen should system Back exit normally.

Android Back while the IME is open may continue to dismiss the IME first. Existing modal/dialog semantics must not be made destructive.

**Retest:** system Back from PC Settings, character editor, character list, and root; repeat once with IME open.

## 2. Owner-requested persisted data correction

### B-01 — add `Raza` and `Religion / Fe` to Trasfondo

This is not UI-only.

**Domain:** `shared/.../character/CharacterSheet.kt`
- add two default-empty strings to `CharacterBackground`.

**Current schema/query:** `shared/.../db/Character.sq`
- add two NOT NULL default-empty columns to `character_background`;
- update background select/upsert query shapes.

**Migration:** create `shared/.../db/5.sqm`.
- ALTER `character_background` to add both columns with `NOT NULL DEFAULT ''`;
- do not rewrite migration `4.sqm`.

**Repository:** `CharacterRepository.kt`
- hydrate and persist both fields.

**Android draft/recreation:** `CharacterBackgroundDraftCodecV4.kt`
- encode/decode both fields with backward-compatible empty defaults.

**UI:** `CharacterBackgroundTabV4.kt`
- add persisted single-line fields labelled `Raza` and `Religión / Fe` in the compact Trasfondo identity area.

**Automated regression:**
- migration from schema 4 preserves all pre-existing data and initializes both fields empty;
- repository save/reopen round-trip preserves both values;
- Phase 4 holistic disk-reopen regression includes both fields.

**Owner retest:** migrated existing character has empty new fields without any lost data; enter values, save, close/reopen, rotate/recreate, verify exact persistence.

## 3. Non-blocking corrections to include in the correction build

### T-01 — `Quick Magic` terminology

**Primary code:** `CharacterEditorV4.kt`.

Known UI occurrences include the compact section title and the spellcasting-hide warning. Replace product-facing `Quick Magic` terminology with the PDF-reference wording `Lanzamiento de Conjuros`; keep technical/historical identifiers unchanged unless renaming them has direct product value. `assets/character-sheets/templates/REFERENCE.md` remains the terminology reference.

**Retest:** General spellcasting summary and spellcasting-disable warning; shared slot behavior remains unchanged.

### C-02 — outside tap does not dismiss keyboard in combat editor

**Primary code:** combat dialog/shared keyboard-safe dialog solution.

Desired correction is keyboard dismissal only, not dialog dismissal and not draft loss. Implement only through an interaction that clearly separates IME dismissal from destructive dialog dismissal. Preserve 12C draft-retention behavior.

### C-03 + L-01 — excess vertical spacing / padding

**Primary scope:** phone-first character tabs, especially `CharacterCombatTabV4.kt`, `CharacterEquipmentTabV4.kt`, `CharacterTraitsTabV4.kt`, `CharacterNotesTabV4.kt`, `CharacterSpellListV4.kt`, `CharacterBackgroundTabV4.kt`, and shared character layout helpers.

Current tabs repeatedly use large synthetic bottom paddings (often 150–170 dp) plus nested card spacing. After the IME/action fix removes the need for compensating space, reduce/cap unnecessary margins and padding consistently. Preserve accessibility/touch targets and readable grouping. Do not perform a visual redesign.

### D-01 / E-01 — weak drag feedback

**Primary code:** all long-press reorder surfaces in combat, equipment, traits, spell-source management, spell rows, and note cards; shared `StableDragHandle` in `IconControls.kt` where useful.

Functional reorder already passed. Add a clear transient visual dragging state (for example elevation/tonal/background/alpha or equivalent visible state) while the long press/drag is active. Keep persisted reorder logic unchanged.

### E-02 — Equipo row actions too bulky

**Primary code:** `CharacterEquipmentTabV4.kt`.

Compact `Editar`/`Eliminar` presentation without making actions ambiguous or hard to tap. A compact icon action may be used only with meaningful semantics/content descriptions and a full touch target.

### E-03 — drag-handle footprint / handle-free proposal

**Primary code:** `IconControls.kt` and reorder call sites.

The current handle reserves a 48 dp layout/touch area. The owner's handle-free long-press-row idea was a proposal, not an approved interaction change. Therefore this correction cycle must **not** silently remove the handle. Preserve a valid touch target; reduce visual/layout burden only if it can be done without accessibility regression. Any switch to whole-row long press remains a separate owner decision.

### R-01 — Rasgos usage summary clarity

**Primary code:** `CharacterTraitsTabV4.kt`.

Current text is `Usos: remaining / max · Gastados spent`. Replace with an immediately readable Spanish presentation while preserving remaining/max/spent meaning. Preferred minimal direction: show remaining/max prominently and spent only when useful, without arithmetic-looking ambiguity.

**Retest:** one unused, partially used, and exhausted Rasgo; Spend/Recover behavior remains correct.

### S-01 — numeric field opens normal keyboard

**Exact mapped code:** `CharacterSpellListV4.kt`, spell editor field labelled `Nivel (0-9)`.

The field filters input to digits but does not set numeric keyboard options. Add `KeyboardOptions(keyboardType = KeyboardType.Number)` (and required imports) to this exact field. The spellcasting-source editor has no numeric field and should not be modified for this finding.

**Retest:** add/edit spell level; confirm numeric keypad and levels 0–9 behavior.

### N-02 — visible long-text scroll affordance in Notes

**Primary code:** `CharacterNotesTabV4.kt`.

For `Notas generales`, and for the titled-note body editor, use bounded-height long-text editing after a sensible growth range and provide a subtle visible vertical scroll affordance when content exceeds the visible area. Avoid making a single note consume the entire screen and avoid awkward nested-scroll behavior.

**Retest:** long general notes and long titled note, portrait/landscape, keyboard open, rotation/recreation.

### N-03 — two-column titled notes in landscape/wide

**Primary code:** `CharacterNotesTabV4.kt`.

The current implementation always renders titled cards sequentially in one column even when `wide == true`. In wide layouts, chunk cards into two columns with deterministic left-to-right/top-to-bottom order; retain one-column fallback when narrow.

**Retest:** 3+ titled cards in portrait and landscape; reorder, save/reopen, confirm order remains unambiguous.

## 4. Implementation sequence

Use small reviewable increments on the existing non-main Phase 4 line; do not merge until the complete correction candidate is accepted.

### Correction A — navigation + keyboard blockers

- N-01 system Back hierarchy.
- C-01 combat IME-safe actions.
- E-04 equipment IME-safe actions.
- C-02 keyboard-dismiss improvement only if it can preserve draft behavior safely.

Gate: Android build + focused device retest of navigation/IME behavior before relying on the pattern elsewhere.

### Correction B — persisted Trasfondo addition

- B-01 domain + `5.sqm` + current schema/queries + repository + codec + UI.
- migration and persistence tests.
- extend holistic Phase 4 regression.

Gate: full shared SQLDelight/Kotlin suite must pass before UI polish continues.

### Correction C — compactness, feedback and terminology

- U-01 alignment.
- E-05 compact Monedas.
- C-03/L-01 spacing rationalization.
- D-01/E-01 drag feedback.
- E-02 compact equipment actions.
- R-01 usage text.
- S-01 numeric keypad.
- T-01 terminology.
- N-02/N-03 Notes improvements.
- E-03 only within its non-redesign boundary above.

Gate: Android + desktop build, shared suite, then produce one correction APK.

## 5. Automated gate for the correction candidate

Before owner retest, require at minimum:

- backend install/type-check remains green;
- full shared Kotlin/SQLDelight suite green;
- schema 4 -> 5 migration/data-preservation test green;
- repository background round-trip with `Raza` and `Religion / Fe` green;
- holistic Phase 4 disk-reopen regression green with the new fields;
- Android debug build green;
- desktop build green;
- correction APK artifact identity recorded.

UI-only behavior that lacks an existing Compose UI-test harness remains mandatory real-device QA rather than introducing a new test framework just for this correction cycle.

## 6. Focused owner retest target

Do not automatically repeat all 42 checks. The correction APK requires:

1. migration preservation smoke: original Checks 1–4, because schema changes;
2. navigation/tab smoke: Checks 5–10 plus explicit N-01 Back hierarchy;
3. full corrective backlog: Checks 11–15, with special focus on 12 and 13 blockers;
4. B-01 Trasfondo add/save/reopen + Check 19 keyboard regression;
5. Rasgos Checks 22–24 for R-01/drag/layout regression;
6. Conjuros: spell add/edit level keypad, Check 33 drag reorder, Check 34 slot synchronization, plus a quick source/spell persistence smoke;
7. Notes Checks 35–37 for N-02/N-03;
8. resilience Checks 38–42, including spellcasting hide-not-delete.

If a focused retest reveals a cross-cutting regression, expand only the affected area instead of restarting Phase 4 implementation increments A–L.

## 7. Merge boundary

A correction APK is only a candidate. Phase 4 may be proposed for merge only after:

- all five current blockers are verified fixed on the correction APK;
- B-01 migration and persistence are verified;
- automated gates are green;
- remaining limitations, if any, are explicitly accepted/deferred by the owner;
- continuity/docs drift is repaired so README/PROJECT_STATE/AGENTS/MANIFEST/ROADMAP/TESTING do not contradict the accepted Phase 4 state;
- debug-signing governance wording is reconciled without exposing key material;
- the owner explicitly approves the merge.

Branch cleanup remains after merge and requires a unique-commit audit; do not delete divergent historical/tmp branches merely because Phase 4 reaches acceptance.
