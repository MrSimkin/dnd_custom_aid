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

## Confirmed remaining P6 work

### Custom skills

The active custom-skill collections persist and render by `sortOrder`, but the Player-facing custom-skill collection currently has no direct reorder interaction.

Required migration:
- `Por habilidades`: reorder across the complete custom-skill list;
- `Por atributo`: reorder only inside the currently displayed attribute group, preserving the positions of skills belonging to other attributes;
- preserve stable ids, ability assignment, training and all non-order state;
- commit once on successful drop;
- child edit/delete/open controls must not become pickup regions;
- use existing haptic/reorder primitives rather than introducing a separate interaction grammar.

## Final audit still required after custom skills

Perform a branch-specific residue sweep for active Player code and distinguish:
1. live legacy threshold / repeated-move consumers that still violate P6;
2. obsolete helpers with no remaining live consumers, which may then be removed safely;
3. historical/default-branch-only references that are not active on this branch;
4. insertion-order-only `sortOrder` fields that are not user-manual-order collections and therefore must not be converted merely because they have an order field.

Do not broaden P6 into new reorder affordances without evidence that the collection is intended to support user-controlled manual order.

## Branch state at checkpoint creation

Head before this documentation commit:
`0ad04af7a107bcaaecfe0d0f30473894b9b26e50`

This checkpoint records implementation already completed; it does not replace the original P6 design decision checkpoint.
