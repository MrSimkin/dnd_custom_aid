# Phase 4 character closure proposal-review checkpoint

Date: 2026-09-03
Canonical branch: `main` (untouched)
Durable Phase 4 branch: `implementation/character-data-foundation`
Focused proposal/prototype branch: `implementation/phase4-character-closure`
Pre-checkpoint prototype head: `89aad12a094476c7b6798f6f0626bf978a5d0831`
Automated verification for that prototype head: GitHub Actions run `33779104922` — PASS

## Status

**PENDING OWNER REVIEW. NOT APPROVED PRODUCT SCOPE. DO NOT CONTINUE IMPLEMENTATION FROM MOMENTUM.**

The owner explicitly stopped implementation because design alternatives and genuinely agent-originated proposals had not been presented first. Coding is therefore paused. The prototype data/schema work already present on this focused branch remains non-canonical and non-authoritative even though its automated gate is green.

No new owner-QA APK is designated by this checkpoint. Documentation-only commits/runs must not be mistaken for a new acceptance APK.

## Owner requirements retained as baseline and NOT counted as agent proposals

- app-wide IME/keyboard-safe dialogs and editors;
- more compact Monedas;
- real visible drag-and-drop feedback;
- substantially more compact ordinary Equipo;
- optional alphabetical ordering for ordinary Equipo, Equipo especial, and Conjuros inside their relevant groups;
- responsive phone/tablet layouts, with more useful columns/layout density on tablet portrait and landscape;
- application Settings reachable from PC Settings;
- consistent, intuitive Add/Edit/Delete/etc. interaction language;
- all relevant official classes including Artificer, plus official subclasses and owner custom-SRD provenance, with conditional dedicated tabs/modules when justified;
- character status moved from General into PC Settings, plus moving other true character-configuration controls there when appropriate;
- an experimental `Vista supercompacta`, reachable from PC Settings, to be judged during QA rather than assumed successful.

## Agent-originated feature proposals

Recommendation scale: 5 = closure-stage priority; 4 = strong; 3 = useful if cost stays moderate; 2 = defer unless especially cheap.

- F01 (5): Conditions + Exhaustion tracker.
- F02 (5): Structured defenses — resistances, immunities, vulnerabilities.
- F03 (5): Structured senses + special movement modes.
- F04 (5): Manual Concentration tracker linked to an effect/spell.
- F05 (4): Short/Long Rest assistant with preview and selective reset; no forced rules enforcement.
- F06 (5): Generic reusable resource counters with current/max/recovery/source.
- F07 (4): Consumable/ammunition tracking mode tied to inventory quantity.
- F08 (4): Inventory containers/locations such as backpack, belt, chest or Bag of Holding.
- F09 (5): Optional character portrait/token reusable later by DM views/combat.
- F10 (4): Named end-of-session reconciliation snapshots/checkpoints.
- F11 (3): Export/import the app's own character-backup format for local emergency backup; not third-party parsing.
- F12 (3): Optional XP or Milestone progress mode.
- F13 (4): Custom skills attached to an ability while preserving the standard skills.
- F14 (5): Structured languages/proficiencies/training.
- F15 (5): Favorite/Quick Access flag usable by Supercompact view and later DM quick view.
- F16 (4): Read-only/table mode that prevents accidental structural edits while referencing the sheet in play.
- F17 (3): Simple dice roller launched from attacks/saves/skills without becoming a rules engine.
- F18 (4): Manual temporary session effects/bonuses with note/source and easy clear/recovery behavior.

## Agent-originated design proposals

- D01 (5): Sticky mini character header while scrolling — identity plus saved/unsaved state stays visible.
- D02 (5): Wide/tablet navigation rail instead of forcing the phone top-tab strip on every screen width.
- D03 (5): Tablet master-detail editors — list on one side, selected item's editor on the other.
- D04 (5): Progressive disclosure — compact summary first, detailed editor only after tap.
- D05 (5): One visual grammar: rows for simple objects, cards for rich objects, panels for groups.
- D06 (4): Visible source badges for D&D 5e / D&D 5.5e / Custom without using color as the only cue.
- D07 (5): Consistent state badges for prepared/equipped/attuned/concentrating/favorite/etc.
- D08 (5): Small contextual toolbar per long tab for count/search/filter/add instead of scattered controls.
- D09 (4): Quick edits use a compact sheet/panel; long or complex edits use a full editor.
- D10 (5): Validation beside the field that needs correction; ordinary mistakes should not trigger disruptive modal dialogs.
- D11 (5): Destructive confirmations name exactly what will be deleted/affected.
- D12 (4): Empty sections explain what belongs there and offer one obvious first action.
- D13 (4): Sticky group headers for long lists such as spell levels or grouped traits.
- D14 (3): Small consistent haptic language for drag pickup/drop, resource ticks and destructive confirmation.
- D15 (5): Persistent visible `Guardado / Cambios sin guardar` feedback rather than relying on user memory.
- D16 (4): Preserve list context on tablet when opening/closing editors so the screen does not visually reset.
- D17 (4): Defined spacing hierarchy (micro / normal / section) so compact screens remain orderly rather than merely cramped.
- D18 (4): Visually emphasize live/operational data and de-emphasize rare reference detail without hiding it.

## Agent-originated improvements to existing surfaces

- I01 (4): Official class selection may prefill its common hit die/defaults, but every value remains editable.
- I02 (5): Class rows show class + subclass + rules generation/source compactly when available.
- I03 (4): Add Passive Insight and Passive Investigation alongside Passive Perception where useful.
- I04 (4): Custom skills participate in both existing Habilidades organization modes.
- I05 (5): Combat entries show their action type and damage/effect type at a glance.
- I06 (5): HP gets a quick damage/heal/temp-HP adjustment interaction while exact manual editing remains available.
- I07 (4): At 0 HP, optionally surface death-save controls automatically without forcing them into the normal healthy layout.
- I08 (5): Equipo header shows total carried weight and current attunement count.
- I09 (4): Equipo can filter carried/equipped/stored/special without changing saved order.
- I10 (4): Equipment location/container appears as a small readable chip rather than hidden detail.
- I11 (5): Rasgos can be grouped by source/class/subclass/species/feat with useful counts.
- I12 (5): Limited-use Rasgos show a small remaining/max meter instead of arithmetic-looking prose.
- I13 (5): Spell rows show V/S/M, Concentration, Ritual and Prepared status as compact badges.
- I14 (4): Spell-level headers can stick/collapse so long spell lists remain navigable.
- I15 (5): Conjuros filters by source, prepared state, concentration and ritual without altering stored order.
- I16 (4): Nota cards show a short text preview before opening the full note.
- I17 (4): Trasfondo separates short identity/personality information from the long story, with the long story collapsible.
- I18 (5): Character list shows class/subclass/level + freshness + optional portrait so the right PC is identifiable immediately.
- I19 (4): Remember the last open tab per character across a full app restart, not only Android recreation.
- I20 (5): Leaving with unsaved changes gets an explicit guard: Save / Discard / Keep editing.
- I21 (3): App font/theme settings preview against a miniature real character-sheet sample rather than generic text only.
- I22 (4): Spell slots and generic resources use one-tap spend/recover, with direct exact edit still available.

## Scope recommendation before implementation

The agent should next present all proposals to the owner in layman terms, clearly separated from owner-originated requirements. The owner may Accept / Reject / Defer individual items or groups. Only after that review should the implementation package, migration plan, class/subclass module map and QA matrix be frozen.

Do not treat the prototype's already-created schema/domain choices as pre-approved simply because they exist or pass CI.

## Exact continuation rule

1. Owner reviews this proposal matrix.
2. Record each accepted/rejected/deferred direction durably.
3. Complete the official class/subclass conditional-module audit against the approved design.
4. Produce the implementation map and checkpoint/gate plan.
5. Only then resume code.
