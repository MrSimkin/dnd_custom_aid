# D-0047 — Phase 4 character closure expansion

**Status:** Approved  
**Date:** 2026-09-03  
**Decision owner:** Project owner

## Decision

Phase 4 will receive one substantial character-foundation closure build before the project moves into the DM-focused stage.

This is intentionally more than a correction build. It combines:

1. the owner-observed QA corrections from the previous APK;
2. the owner's new character-sheet requirements;
3. all agent-originated feature proposals F01–F18;
4. all agent-originated design proposals D01–D18;
5. all agent-originated improvements I01–I22;
6. a verified class/subclass audit with conditional reusable character modules;
7. first-class phone and tablet behavior, including portrait and landscape QA.

The application remains a permissive D&D assistant and digital backup/reference. None of this converts it into a guided/legal character builder or automatic rules-enforcement engine.

## 1. Owner requirements retained as mandatory closure scope

The next closure APK must include all of the following:

- app-wide IME/keyboard-safe editing. Any editor/dialog with actions such as `Guardar`/`Cancelar`, `Aplicar`/`Cancelar`, creation actions or equivalent must keep those actions reachable while the Android keyboard is visible;
- `Monedas` substantially more compact;
- real reorder UX with visible drag feedback rather than only changing order after a gesture;
- ordinary `Equipo` substantially denser because a mundane object should not consume a large rich card by default;
- optional alphabetical ordering for ordinary `Equipo`, independently within its block;
- optional alphabetical ordering for `Equipo especial`, independently within its block;
- optional alphabetical ordering for `Conjuros` within the relevant source/block and spell level;
- switching to alphabetical order must not destroy the previously saved manual order; returning to Manual restores that order;
- drag/reorder affordances are disabled or hidden while an automatic sort mode is active;
- Android phone and tablet are both first-class targets. Portrait and landscape are both QA targets;
- layouts respond to available width rather than merely checking a hard device category. Wide screens should use additional columns, navigation rails or master-detail layouts when useful;
- `Configuración de la aplicación` is reachable from PC Settings while theme/font/text scale remain global application preferences, not character mechanics;
- Add/Edit/Delete/etc. interactions use one consistent, intuitive vocabulary and visual grammar across the application;
- character lifecycle status (`Activo`, `Inactivo`, `Retirado`, `Muerto`) moves from General into PC Settings;
- other controls that configure how the character sheet behaves, rather than describing live character state, should be considered for PC Settings;
- an experimental `Vista supercompacta` is reachable from PC Settings and is intentionally evaluated during QA for usefulness and visual quality rather than assumed successful in advance;
- classes and subclasses become first-class structured character identity, with source/provenance support and a permissive manual/custom escape path;
- all relevant official classes and subclasses, including Artificer and official supplemental material outside the SRDs, are audited for whether they benefit from conditional character-sheet modules;
- official catalog conveniences must never reject manually entered/custom/homebrew classes or subclasses.

## 2. Approved general character-management surface

A new general character tab/surface is approved for live character upkeep. The current working Spanish label is **`Gestión`**; exact user-facing wording remains QA-adjustable if `Gestión` proves unclear.

`Gestión` is not PC Settings. It contains character state the player may actively maintain during or between sessions.

Approved responsibilities include:

- F01 conditions and Exhaustion;
- F04 current Concentration tracking;
- F05 Short/Long Rest assistant;
- F06 generic reusable resources;
- F10 end-of-session reconciliation snapshots/checkpoints;
- F18 temporary session effects/bonuses;
- related live maintenance such as resource/hit-die recovery where the underlying data already exists and the operation is understandable.

The Rest assistant previews what it proposes to restore/reset and allows selective confirmation. It does not silently enforce class legality or assume every custom resource follows official recovery rules.

Character lifecycle status does **not** belong in Gestión; it belongs in PC Settings as requested by the owner.

## 3. Approved PC Settings responsibilities

PC Settings is the character-wide configuration surface. It should contain, where applicable:

- character lifecycle status;
- spellcasting visibility/enabled state, preserving the existing hide-not-delete behavior;
- automatic conditional-module visibility plus manual module overrides for homebrew/custom characters; hiding a module must not delete its stored data;
- configurable haptic feedback under D14;
- access/configuration for the experimental Supercompact view;
- F16 Table/read-only mode configuration;
- F12 XP-vs-Milestone progress mode when that mode changes what progress UI is shown;
- shortcut/entry to global Application Settings;
- other genuine sheet-behavior configuration discovered during implementation.

Live gameplay state such as HP, Inspiration, conditions, resources and spell slots must not be moved into PC Settings merely to reduce tabs.

## 4. Approved agent-originated features F01–F18

All are approved for the Phase 4 closure scope.

- **F01 — Conditions + Exhaustion:** structured current conditions and Exhaustion state.
- **F02 — Defenses:** structured resistances, immunities and vulnerabilities.
- **F03 — Senses + special movement:** structured special senses and movement modes such as fly/swim/climb/burrow.
- **F04 — Concentration:** manually track the currently concentrated spell/effect and clear it quickly.
- **F05 — Rest assistant:** Short/Long Rest preview plus selective recovery in `Gestión`, without silent rules enforcement.
- **F06 — Generic resources:** reusable current/max/recovery/source counters suitable for class/subclass/homebrew resources.
- **F07 — Consumables/ammunition:** optional quick quantity consumption for inventory entries.
- **F08 — Containers/locations:** inventory locations such as backpack, belt, chest, Bag of Holding or custom location.
- **F09 — Portrait/token:** optional character image suitable for later reuse by DM quick views/combat.
- **F10 — Reconciliation checkpoints:** named/date-aware end-of-session digital reconciliation snapshots/checkpoints.
- **F11 — Own-format backup:** export/import the application's own character backup format for local emergency recovery; no third-party parsing implied.
- **F12 — XP or Milestone:** optional progress mode.
- **F13 — Custom skills:** manually add skills tied to an ability while preserving the standard set.
- **F14 — Languages/proficiencies/training:** structured languages, tools, weapons, armor and other proficiencies.
- **F15 — Favorite/Quick Access:** flag useful attacks, traits, spells, resources, forms, companions, etc. for Supercompact and future DM quick views.
- **F16 — Table/read-only mode:** reduce accidental structural edits while retaining intentionally allowed live operational controls.
- **F17 — Simple dice roller:** quick dice from attacks/saves/skills; it must not grow into automatic rules resolution by implication.
- **F18 — Temporary effects:** manual session effects/bonuses with source/note and quick clearing/recovery.

## 5. Approved design proposals D01–D18

All are approved.

- **D01:** sticky mini character header with identity and saved/unsaved state.
- **D02:** wide/tablet navigation rail where it is better than a phone-like top tab strip.
- **D03:** tablet/wide master-detail editors for list-heavy domains.
- **D04:** progressive disclosure: compact summary first, detailed editor on demand.
- **D05:** one visual grammar: rows for simple objects, cards for rich objects, panels for groups.
- **D06:** visible D&D 5e / D&D 5.5e / Custom/source badges without color as the only differentiator.
- **D07:** consistent state badges for prepared/equipped/attuned/concentrating/favorite/etc.
- **D08:** compact contextual toolbar for long tabs: count/search/filter/add as relevant.
- **D09:** quick edits use compact panels; complex edits use a larger/full editor.
- **D10:** validation appears beside the relevant field; normal input mistakes should not cause disruptive modal errors.
- **D11:** destructive confirmations explicitly name the affected/deleted object.
- **D12:** empty sections explain what belongs there and expose one obvious first action.
- **D13:** sticky group headers where useful in long grouped lists.
- **D14:** small consistent haptic language for drag/resource/destructive interactions. **Haptics are configurable from PC Settings.**
- **D15:** persistent visible `Guardado` / `Cambios sin guardar` feedback.
- **D16:** preserve list/editor/navigation context when opening and closing child editors. **This is a general technical UX rule wherever feasible, not a tablet-only behavior.**
- **D17:** defined spacing hierarchy (micro/normal/section) so compact layouts remain orderly.
- **D18:** emphasize live/operational information and visually de-emphasize rare reference detail without hiding it.

## 6. Approved improvements I01–I22

All are approved.

- **I01:** known official classes may prefill obvious/common hit-die/default suggestions, but every saved value remains editable.
- **I02:** class rows compactly show class + subclass + rules generation/source.
- **I03:** add Passive Insight and Passive Investigation alongside Passive Perception where useful.
- **I04:** custom skills participate in both existing Habilidades organization modes.
- **I05:** Combat entries show action type and damage/effect type at a glance.
- **I06:** HP gets quick damage/heal/temp-HP adjustment while exact manual editing remains available.
- **I07:** when at 0 HP, death-save controls can surface prominently without permanently consuming the healthy layout.
- **I08:** Equipment summary shows total carried weight and current attunement count.
- **I09:** Equipment filters carried/equipped/stored/special without changing saved order.
- **I10:** equipment location/container appears as a small readable chip/label.
- **I11:** Traits can group by source/class/subclass/species/feat with useful counts.
- **I12:** limited-use Traits show a compact remaining/max meter.
- **I13:** spell rows show V/S/M, Concentration, Ritual and Prepared as compact badges.
- **I14:** spell-level headers can remain visible/collapse for long lists.
- **I15:** Spells filter by source/prepared/concentration/ritual without altering stored order.
- **I16:** Note cards show a short text preview before opening the full note.
- **I17:** Background separates short identity/personality information from a collapsible long story.
- **I18:** Character list shows class/subclass/level + freshness + optional portrait.
- **I19:** remember the last open tab per character across a full app restart.
- **I20:** leaving with unsaved changes presents Save / Discard / Keep editing.
- **I21:** theme/font settings preview against a miniature real character-sheet sample rather than generic text only.
- **I22:** spell slots and generic resources support one-tap spend/recover with direct exact editing still available.

## 7. Conditional class/subclass module architecture

The class/subclass audit supports reusable modules rather than one permanent tab per class/subclass.

Approved module families for implementation planning:

- **Artífice / Artifice** — Artificer-specific plan/replication/invention state and subclass-specific sections where useful;
- **Formas / Forms** — reusable form/transformation library/state;
- **Técnicas / Techniques** — maneuvers, arcane shots, runes, flourishes or similarly chosen technique libraries;
- **Metamagia / Metamagic** — Sorcerer metamagic options integrated with generic resources;
- **Pactos / Pacts** — Warlock pacts/invocations/Mystic Arcanum-facing choices integrated with Spells and resources rather than duplicating them;
- **Compañeros / Companions** — structured companion/construct/spirit/familiar-like owned entities with enough persistent state to deserve a reusable surface.

Rules:

- visible modules are the union of all relevant classes/subclasses for a multiclass character;
- do not show duplicate tabs for multiple triggers;
- a recognized class/subclass may suggest/auto-enable its relevant module(s);
- PC Settings permits manual module enable/disable for custom/homebrew cases;
- disabling/hiding is non-destructive and must not erase stored module data;
- subclasses normally add sections/data to an existing module rather than receiving a unique permanent tab;
- modules may also be enabled manually even when no catalog entry recognizes the character.

The detailed class/subclass mapping lives in `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md`.

## 8. Phone + tablet acceptance boundary

The closure APK is explicitly a **phone + tablet** build.

Manual QA must cover at least:

1. phone portrait;
2. phone landscape;
3. tablet portrait;
4. tablet landscape;
5. representative larger application text scale on both form factors where practical.

Responsive behavior is based on available width/window constraints rather than a brittle `tablet = fixed N columns` rule.

Tablet should exploit width through additional columns, navigation rail and master-detail layouts where they improve use. Phone remains compact and usable without requiring a wide screen.

The Supercompact view is deliberately stress-tested across those surfaces and may be refined after QA if its density or visual hierarchy is not useful.

## 9. Non-goals preserved

This decision does not approve:

- a guided/legal character builder;
- automatic character-build legality enforcement;
- automatic combat resolution;
- a VTT;
- third-party character import/parsing merely because own-format backup exists;
- automatic application of every official class/subclass rule;
- forcing official catalog choices over manual/custom entries.

## 10. Implementation consequence

The already-existing prototype work on `implementation/phase4-character-closure` may be reused only where it conforms to this approved decision and the class/subclass audit. Existing code is not retroactively correct merely because it compiles or passed CI.

Before implementation resumes, the branch must contain:

- this approved decision;
- the class/subclass module audit;
- an approved-scope checkpoint/current project state;
- an implementation/gate map describing migration, UI increments and new phone+tablet QA target.

A new owner-QA APK must receive its own tested commit/artifact identity. Artifact `9876725270` remains historical and is not the acceptance target for this expanded closure scope.
