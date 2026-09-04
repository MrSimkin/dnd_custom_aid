# Phase 4 Batch M2 — code-health/static architecture audit

**Date:** 2026-09-04  
**Status:** M2 COMPLETE  
**Audit branch:** `tmp/phase4-m-audit-safety`  
**Production code changed:** no

## Result

The codebase is not generally spaghetti. Shared repositories/domain logic and the newer conditional-module architecture are coherent enough to keep. The main maintainability debt is historical layering in Android: newer closure UI coexists with superseded implementations and a few duplicated editor blocks.

## Confirmed findings

1. `CharacterEditorV4.kt` still contains an obsolete private class/status editor chain (`StatusSelectorV4`, `ClassesCardV4`, `ClassRowV4`, `ClassSelectorV4`, `HitDieSelectorV4`, `classNamesV4`) although active Overview uses `CharacterClassIdentityCardV4` and lifecycle status lives in PC Settings. The old entry points have no active call sites.
2. `CharacterEquipmentTabV4.kt` is a superseded whole Equipment implementation; active routing uses `CharacterEquipmentClosureTabV4`.
3. `CharacterTraitsTabV4.kt` is a superseded whole Traits implementation; active routing uses `CharacterTraitsClosureTabV4`.
4. `CharacterSpellListV4.kt` is the older list implementation; active `CharacterSpellsTabV4` routes to `CharacterSpellListClosureV4`.
5. `CharacterDomainShellV4` is a placeholder scaffold with no active route.
6. `CharacterEquipmentClosureV4.kt` duplicates substantial phone-dialog and wide-panel editor-field definitions. This is a safe consolidation candidate, but not a reason for broader rewrite.
7. `CharacterEditorScreenV4` mixes routing, draft serialization, dirty-state, persistence synchronization, Back handling, backup export and operational/structural coordination. This is a genuine hotspot, but no broad pre-QA rewrite is justified because the current boundary is heavily integrated and already protected by tests/gates.
8. No TODO/FIXME trail was found in the main audited hotspots; unfinished work is structural/scope-related rather than explicitly marked.

## Keep as-is before QA

- `CharacterRepository` remains one coherent aggregate persistence/validation responsibility despite its size.
- `CharacterClosureRepository` remains separate and appropriate.
- H2 Techniques/Metamagic/Pacts already share a config-driven engine and should not be split.
- SQLDelight migration history must not be rewritten.

## Interaction with M1

M1 already found four approved-scope holes that require closure before final QA: F14 proficiency/language UI, F15 Resource Quick Access, I18 richer character-list summary, and I21 real-sheet settings audition. D06/D07 require only the smallest semantic presentation normalization justified by the approved design.

## Revised next order after owner instruction

The owner requested an explicit audit that all previous Phase 4 batches were actually implemented completely, independent of later device-quality QA.

Therefore:

- **M3:** prior-batch implementation completeness audit (Batch 0/A1 through L).
- **M4:** close any historical implementation holes found by M3.
- **M5:** consolidate M1/M2 findings and remaining remediation into the final pre-QA candidate plan.

Do not modify production code until M3 identifies the exact historical holes.
