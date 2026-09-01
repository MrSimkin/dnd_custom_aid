# Increment I — Gate candidate

**Date:** 2026-09-01  
**Branch:** `tmp/increment-i-shared-slot-integration`  
**Source wiring head:** `6aada24ec5f671d33dd0e4888e9344a460f37393`

## Candidate behavior

`Conjuros` now receives a UI-only projection of the same `CharacterEditorDraftV4.spellSlots` state already used by Quick Magic.

For configured levels 1–9, the Conjuros level header shows compact spent/unspent slot pips and the spent/total count. Tapping those pips updates the existing editor slot record through `CharacterEditorDraftV4.withSpellSlot(...)`. Quick Magic therefore sees the changed state immediately when the user returns to `Resumen`; Quick Magic edits likewise rebuild the Conjuros projection from the same draft on recomposition.

Slot totals remain configured in Quick Magic. `Conjuros` only changes the manual spent count. Cantrips never render slot controls.

There is no new slot table, schema, migration, `CharacterSpellcastingDraftV4` field, persistence cache, or independent spellcasting profile.

## Safety evidence

The existing-file wiring was applied through an asserted patch:

- `CharacterEditorV4.kt`: +17 lines only;
- `CharacterSpellsTabV4.kt`: +4 lines only;
- `CharacterSpellListV4.kt`: focused level-header/callback wiring;
- every expected source matcher had to occur exactly once;
- matcher failure would have aborted before commit;
- temporary patch script/workflow were deleted in the same source-wiring commit.

## Regression coverage

`CharacterSpellSlotIntegrationTest` exercises the same authoritative `CharacterSpellSlot` records through sequential Quick-Magic-style and Conjuros-style updates, verifies later reads observe prior mutations, restores spent slots, toggles `spellcasterEnabled` OFF and ON, and verifies the slots are preserved unchanged.

## Gate I

This direct checkpoint commit intentionally triggers the normal `Scaffold checks` workflow on the complete candidate because the preceding GitHub Actions self-patch commit does not recursively trigger ordinary push workflows.

Gate I requires:

- backend check PASS;
- shared desktop tests PASS, including `CharacterSpellSlotIntegrationTest`;
- Android debug build PASS;
- desktop build PASS;
- APK artifact upload PASS.

Manual intended-phone QA remains separate and will be recorded as not yet executed unless the owner tests the produced APK.