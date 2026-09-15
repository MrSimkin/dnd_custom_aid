# Phase 4A — P6 direct-drag reorder implementation progress

Date: 2026-09-11
Branch: `implementation/phase4a-successor-cycle`
Status at checkpoint: **IN PROGRESS / IMPLEMENTATION AUTHORIZED**

## Purpose

Durably record the completed P6 implementation work before continuing the full-app reorder audit and remaining migrations.

P6 accepted contract remains authoritative:
- direct long-press + drag on the card/body;
- no dedicated reorder mode or visible drag handle required;
- child controls retain their actions and must not accidentally initiate reorder;
- row-major / spatial reordering where the normal layout is multi-column;
- one durable order commit on successful drop;
- Manual order only for collection surfaces that expose sort modes;
- reorder disabled while search/filter is active;
- PC Settings tab order uses the same direct-drag grammar;
- responsive behavior applies to phone and tablet surfaces.

## Implemented and green

### Shared reorder/session foundation
- Added/extended the hardened reorder session and overlay path used by migrated Player collections.
- Added group constraints so one session can own a shared viewport while preventing invalid cross-group drops.
- Existing migrated consumers remain compatible when no group map is supplied.

Relevant commits in this implementation sequence include:
- `bc3a4ad…` — constrained reorder-session foundation.

### Spells

Completed:
- spell manual order is preserved per spell level;
- source-filtered reorder preserves hidden spell positions;
- cross-level movement is structurally prevented;
- old per-row drag accumulator / repeated threshold writes removed from the migrated spell-list path;
- auto-scroll and lifted-overlay behavior retained;
- spell slot operational controls left outside the reorder interaction;
- final order applies as one atomic drop transaction.

Supporting order-policy work:
- `723ae6d…` — atomic final-order helper preserving per-level and visible-subset semantics.
- `988fe43…` — spell-list UI migration.

Validation:
- workflow `34660678265`: backend SUCCESS; Kotlin build/tests SUCCESS; debug APK upload SUCCESS.

### Spell-source manager

Completed:
- source order migrated from the legacy threshold-drag mechanism to its own direct-drag reorder session;
- edit/delete controls remain outside the pickup region;
- source ordering commits once on drop.

Relevant commit:
- `58f6ff4…`

Validation:
- workflow `34660760220`: backend SUCCESS; Kotlin build/tests SUCCESS; debug APK upload SUCCESS.

### Traits

Completed:
- obsolete dedicated `Reordenar` / `Listo` mode removed from the active Traits flow;
- traits remain in their normal responsive multi-column card layout while reordering;
- direct 2D drag uses the accepted P6 interaction grammar;
- reorder is constrained to the active grouping domain (TYPE / SOURCE / NONE as applicable);
- unrelated group slots remain canonical;
- operational use/recovery controls remain outside the pickup region;
- final grouped order applies atomically on successful drop.

Supporting order-policy work:
- `42be755…` — atomic grouped-trait reorder policy.
- `0ad04af…` — active Traits UI migration.

Validation:
- workflow `34660997245`: backend SUCCESS; Kotlin build/tests SUCCESS; debug APK upload SUCCESS.

## Audited and already compliant

### PC Settings tab order

`CharacterPcSettingsReorderV4.kt` already uses the hardened direct-drag session:
- long-press drag;
- no up/down controls in the active subpage;
- single final-order commit;
- lifted overlay;
- Table Mode read-only behavior respected.

No additional P6 implementation change is required for the active tab-order subpage.

## Active-branch correction: custom skills

A follow-up trace after the first checkpoint write established that `CharacterCustomSkillsV4.kt` is not the active sheet path that owns custom-skill editing. The active Player sheet renders custom skills as projections through `presentCharacterSkills(...)`; the sheet text explicitly sends custom-skill configuration to PC Settings.

The live structural editor is:
- `CharacterPcSuccessorSettingsV4.kt` → `CharacterCustomSkillsSettingsV4(...)`
- reached from `CharacterPcSettingsClosureV4.kt` → PC Settings → `Habilidades personalizadas`.

Therefore the earlier tentative target of adding reorder to the old `Por habilidades` / `Por atributo` card is superseded and must **not** be implemented merely because that older file still contains `sortOrder` presentation logic.

## Confirmed remaining P6 work

### Custom skills — active PC Settings manager

The live custom-skill manager persists `closureState.customSkills` by `sortOrder` but currently renders a plain ordered list with Edit/Delete controls and no direct drag interaction.

Required migration:
- direct long-press drag in the active PC Settings custom-skills list;
- stable-id reorder of the complete custom-skill list;
- preserve ability mapping, training and every non-order field;
- rewrite only `sortOrder` to the validated final order;
- commit once on successful drop;
- Edit/Delete controls remain ordinary child actions and must not become pickup regions;
- Table Mode keeps this structural manager read-only/disabled;
- use existing reorder/haptic primitives rather than introducing another interaction grammar.

The normal Habilidades sheet remains a projection surface; it should not gain a competing structural-order editor as part of P6.

## Final audit still required after custom skills

Perform a branch-specific residue sweep for active Player code and distinguish:
1. live legacy threshold / repeated-move consumers that still violate P6;
2. obsolete helpers with no remaining live consumers, which may then be removed safely;
3. historical/default-branch-only references that are not active on this branch;
4. insertion-order-only `sortOrder` fields that are not user-manual-order collections and therefore must not be converted merely because they have an order field.

Do not broaden P6 into new reorder affordances without evidence that the collection is intended to support user-controlled manual order.

## Branch state at first checkpoint creation

Head before the original documentation commit:
`0ad04af7a107bcaaecfe0d0f30473894b9b26e50`

Original documentation commit:
`41dd6bba624d3c84a07139e86e6eb5ef1ae45efd`

This checkpoint records implementation already completed; it does not replace the original P6 design decision checkpoint.
