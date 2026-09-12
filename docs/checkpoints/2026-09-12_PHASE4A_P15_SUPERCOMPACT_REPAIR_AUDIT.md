# Phase 4A — P15 Supercompact repair audit

Date: 2026-09-12
Branch: `implementation/phase4a-successor-cycle`
Authority: `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P15_SUPERCOMPACT_CLOSED.md`

## Status

IMPLEMENTATION REPAIR APPLIED — NORMAL SCAFFOLD VALIDATION PENDING AT THIS CHECKPOINT REVISION.

This checkpoint records the branch-local P15 audit and the demonstrated gaps repaired in source commit:

`22e313f096fe9c42be8db17d3c61473c1339b314` — `repair: close P15 supercompact projection gaps`

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
   - Before repair, Supercompact and the normal editor each owned a local feet→metric formatter.
   - Both local formatters were removed.
   - New shared Android presentation authority: `CharacterDistanceFormatV4.kt` / `formatCharacterDistanceFeetV4(...)`.
   - Supercompact movement and senses, plus the normal editor speed projection, now consume that same formatter.

## Scope safety

The repair changes presentation/projection only. It does not add a second persistence model, change structural-edit ownership, alter Table Mode semantics, modify canonical HP state, or merge anything to `main`.

Temporary repair workflow/script files self-deleted in source commit `22e313f...`; they are not part of the resulting implementation.

## Validation requirement

P15 is not technically closed by this audit document alone. Closure requires the repository's normal Scaffold gate on a descendant containing source commit `22e313f...`, including the exact Kotlin/shared/Android/Desktop build gate and APK upload. The resulting run ID and conclusions must be recorded before P15 is declared automation-green.
