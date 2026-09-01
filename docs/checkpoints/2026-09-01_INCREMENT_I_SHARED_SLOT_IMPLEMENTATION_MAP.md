# Increment I — Quick Magic / Conjuros shared-slot implementation map

**Date:** 2026-09-01  
**Working branch:** `tmp/increment-i-shared-slot-integration`  
**Verified baseline:** `bf8d5d4af8f750edd061d3589af1b6339fd8e8ee` — Increment H closure

## Verification before implementation

The durable Phase 4 implementation branch and Increment H recovery branch both point to the verified H closure. `main` remains untouched. The closure commit passed the full `Scaffold checks` workflow, and there are no open pull requests.

`docs/PROJECT_STATE.md` and several older phase summaries lag the later H closure: they still describe H as closing or earlier Phase 4 scope as deferred. Per `AGENTS.md` inconsistency rules, the later specific H closure, approved next-build package, detailed decisions, actual Git refs, and green CI control current implementation state. This stale summary will be corrected as part of Increment I continuity work.

## Approved Increment I boundary

The owner-approved consolidated package defines Increment I as **Quick Magic / Conjuros shared-slot integration**:

- Quick Magic and `Conjuros` mutate exactly the same authoritative spell-slot records;
- no second slot cache or persistence model is introduced;
- changes made in either UI appear immediately in the other;
- Quick Magic remains the one primary manual profile for spell save DC, spell attack modifier, and casting ability;
- disabling `Lanzador de conjuros` hides both surfaces without deleting slot or spellcasting data.

D-0047 and D-0061 remain controlling for the Quick Magic profile and caster visibility behavior.

## Existing implementation seam

Quick Magic already edits `CharacterEditorDraftV4.spellSlots`, which maps to the persisted `CharacterSheet.spellSlots` records. `Conjuros` currently receives only its source/spell draft and therefore cannot yet mutate the slot state.

Increment H already renders level headers for `Trucos` and levels 1–9 in `CharacterSpellListV4`. Those headers are the approved integration seam.

## Technical approach

Use the existing editor draft as the single in-memory authority:

1. expose a small internal UI projection of level/total/spent slot state to the `Conjuros` call chain;
2. pass slot changes back to `CharacterEditorV4` through callbacks that call the existing `withSpellSlot(...)` mutation on the same editor draft used by Quick Magic;
3. render compact spent/unspent slot controls only in spell levels 1–9 with configured totals; cantrips never display slots;
4. do not add schema, migration, repository slot tables, or slot fields to `CharacterSpellcastingDraftV4`;
5. leave Quick Magic's manual DC/attack/ability profile as the sole profile.

This keeps UI projection separate from persistence and avoids duplicated state.

## Verification plan

Automated Gate I will include:

- existing full shared/Kotlin/Android/Desktop/backend CI;
- a focused repository regression proving slot totals/spent state survive caster OFF/ON without deletion;
- a shared-authority regression that updates the same slot records in sequence and verifies later reads observe the previous mutation, representing the two UI surfaces sharing one persisted state;
- compile-time verification that both Quick Magic and `Conjuros` wire through the same editor draft rather than separate persistence.

Manual phone QA remains a separate acceptance boundary for tap ergonomics and visual density of slot controls inside `Conjuros` level headers.

## Safety rule for source wiring

`CharacterEditorV4.kt` is a large file and has a prior truncation incident. Its Increment I change must be a narrow asserted patch, not a whole-file replacement. The two Conjuros files may also be patched narrowly. Any matcher mismatch must fail closed before committing source changes.

## Next exact step

Implement the isolated slot UI projection/control and narrow wiring, then run Gate I before promotion to `implementation/character-data-foundation`.