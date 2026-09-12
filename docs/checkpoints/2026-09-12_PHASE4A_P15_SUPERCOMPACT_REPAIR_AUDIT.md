# Phase 4A — P15 Supercompact repair audit

Date: 2026-09-12
Branch: `implementation/phase4a-successor-cycle`
Authority: `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P15_SUPERCOMPACT_CLOSED.md`

## Status

IMPLEMENTATION REPAIR COMPLETE — FINAL NORMAL SCAFFOLD VALIDATION PENDING AT THIS CHECKPOINT REVISION.

This checkpoint records the branch-local P15 audit and the demonstrated gaps repaired in source commits:

- `22e313f096fe9c42be8db17d3c61473c1339b314` — `repair: close P15 supercompact projection gaps`
- `bfa0d2e7d0add3809902280cf4945073478b53fa` — `repair: finish global character distance formatting`

It does not claim owner/device QA. The APK produced before P13–P16 and the aggregate Phase 4A sweep are complete is not a final QA candidate.

## Audit result

The inherited Supercompact implementation already satisfied the broad stat-block direction: one play-oriented surface, responsive wide/narrow composition, canonical HP operational helpers, conditional death saves and live state, complete action-economy grouping, expandable traits, compact spell-slot operations, resources, Favorites as ordering/emphasis rather than duplicate cards, and no structural editing surface.

Four concrete contract gaps remained and were repaired:

1. **Canonical identity projection**
   - Before repair, Supercompact read legacy `sheet.background.race` / `sheet.background.name` directly.
   - It now reads `successorState.speciesIdentity`, `successorState.subraceIdentity`, and `successorState.backgroundIdentity` first.
   - Legacy background fields remain only as compatibility fallback for unmigrated data.

2. **Custom attributes**
   - Before repair, the ability block projected only the six built-in abilities.
   - It now projects successor custom attributes in configured order with score, modifier, and optional saving throw.
   - Saving throws reuse canonical `CharacterSheet.customSavingThrowTotal(...)`; no duplicate calculation authority was introduced.

3. **Source-aware spellcasting**
   - Before repair, the casting summary was flattened through legacy sheet-level save DC / attack modifier / ability.
   - It now consumes canonical `CharacterSheet.generalSpellcastingRows(successorState)` and therefore preserves distinct configured spellcasting sources.
   - Each source may show its configured ability abbreviation, source-specific save DC, and source-specific attack modifier.
   - Spell rows expose prepared state and associated source names alongside concentration/ritual markers.
   - Legacy sheet-level casting summary is retained only as a compatibility fallback when canonical source rows do not exist.

4. **Global imperial-first distance formatting**
   - Initial repair removed local feet→metric formatters from Supercompact and the normal editor and introduced shared Android presentation authority `CharacterDistanceFormatV4.kt` / `formatCharacterDistanceFeetV4(...)`.
   - A branch-wide P16/P15 cross-audit then found two additional local formatters in `CharacterCombatOperationalV4.kt` and `CharacterCombatTabV4.kt`.
   - Follow-up source commit `bfa0d2e7...` removed both of those local authorities and routed both Combat projections through `formatCharacterDistanceFeetV4(...)`.
   - The cleanup script asserted that `CharacterDistanceFormatV4.kt` is the only production Android file still containing the feet→metric conversion implementation.

## P15 validation evidence so far

Normal Scaffold `34720663527`, on descendant `4932a029f472f29601c04731e597873f613c0812` containing source commit `22e313f...`, completed GREEN:

- backend: success
- Kotlin/shared/Android/Desktop build and tests: success
- Android debug APK upload: success

That run proves the principal P15 repair compiles and passes the repository gate. It predates the final Combat formatter consolidation in `bfa0d2e7...`; therefore a final normal Scaffold on a descendant containing both source commits is still required before automation closure.

## Scope safety

The repair changes presentation/projection only. It does not add a second persistence model, change structural-edit ownership, alter Table Mode semantics, modify canonical HP state, or merge anything to `main`.

All temporary repair/audit workflow and script files used for the P15 corrections self-deleted in the repair commits and are not part of the resulting implementation.

## Final validation requirement

P15 is not technically closed by this audit document alone. Closure requires the repository's normal Scaffold gate on a descendant containing both `22e313f...` and `bfa0d2e7...`, including the exact Kotlin/shared/Android/Desktop build gate and APK upload. The resulting run ID and conclusions must be recorded before P15 is declared automation-green.
