# Phase 4 pre-QA continuity checkpoint — after M3, before M4

**Date:** 2026-09-04  
**Status:** RECOVERY CHECKPOINT — M1/M2/M3 complete; M4 next  
**Authoritative branch:** `implementation/phase4-preqa-consolidation`  
**Authoritative head before this checkpoint:** `7e7052571b98210bdd5b16f2266be9341bbc6cde`  
**Canonical `main`:** `471c5570669a6007bea9796d8a2c25536b10be21` — keep untouched  
**Historical frozen L candidate:** keep untouched

## 1. Why this checkpoint exists

The owner explicitly requested a durable recovery point in case the chat is lost. This file is the shortest safe resume document for the current pre-QA closure work.

Do not resume from the old historical instruction `M = owner QA`. `docs/PROJECT_STATE.md` has been revised and is controlling.

The continuity correction was verified by workflow `33892766929` — SUCCESS. The resulting durable state commit is `7e7052571b98210bdd5b16f2266be9341bbc6cde`.

## 2. Current Phase 4 ending sequence

- **M1 — COMPLETE:** D-0047 scope traceability audit.
- **M2 — COMPLETE:** code-health/static architecture audit.
- **M3 — COMPLETE:** prior-batch implementation-completeness audit.
- **M4 — NEXT:** close the six inter-batch D-0047 scope holes.
- **M5 — AFTER M4:** re-audit repaired scope, perform only justified bounded M2 cleanup, run complete automated regression/migration/build gate, and freeze a replacement exact QA candidate.
- **M6 — AFTER M5:** owner phone/tablet real-device QA.
- After accepted M6: documentation/governance consolidation, unique-commit/merge-boundary audit, explicit owner merge/closure approval, then Phase 4 exit.

No DM implementation begins before the Phase 4 exit gate.

## 3. M3 result

Historical Batch 0/A1 through L were genuinely implemented to their own written contracts. No earlier batch is currently found to be falsely GREEN relative to what that batch explicitly promised.

However, the A–J decomposition failed to allocate/fully close six approved D-0047 requirements. Therefore Phase 4 is not yet implementation-complete.

M4 fixed scope:

1. **F14:** reachable structured Languages/Tools/Weapons/Armor/Other proficiency management UI over existing durable `CharacterSheet.proficiencies`.
2. **F15:** Favorite/Quick Access toggle for durable generic Resources in Gestión, including stale-reference pruning after successful deletion/save.
3. **I18:** character-list summary with class + subclass + level, freshness/last-updated and optional portrait.
4. **I21:** Application Settings theme/font/text-scale audition against a miniature representative real character-sheet sample.
5. **D06:** compact textual rules-generation/source semantic badges; color cannot be the only differentiator.
6. **D07:** bounded consistent semantic state-badge grammar for the approved state-rich surfaces, without a global visual redesign.

Controlling M4 plan:

`docs/checkpoints/2026-09-04_PHASE4_BATCH_M4_INTER_BATCH_SCOPE_HOLE_CLOSURE_PLAN.md`

## 4. M2 maintainability findings reserved for M5 unless M4 directly touches them

Do not start a broad architecture rewrite.

Confirmed bounded debt:

- dead private legacy class/status editor chain remains inside `CharacterEditorV4.kt` while active Overview uses the newer class identity surface and lifecycle status lives in PC Settings;
- superseded whole-screen `CharacterEquipmentTabV4.kt` remains while active routing uses the closure Equipment surface;
- superseded `CharacterTraitsTabV4.kt` remains while active routing uses the closure Traits surface;
- older `CharacterSpellListV4.kt` remains while the active route uses `CharacterSpellListClosureV4`;
- unused `CharacterDomainShellV4` placeholder remains;
- `CharacterEquipmentClosureV4.kt` contains duplicated phone/wide editor-field blocks;
- `CharacterEditorScreenV4` is a mixed-responsibility hotspot, but a broad pre-QA split is not justified.

Keep repositories, SQLDelight migration history and the config-driven H2 conditional-module engine as-is unless M4 demonstrates a concrete defect.

## 5. Historical frozen candidate identity

Preserve branch `tmp/phase4-l-frozen-qa-candidate` exactly.

- commit `5cc034d3fdf4c25d935bd698aeaf2a3f9e427f27`;
- tree `b0e25a194ba0ed1926422230f3c29f70bfcd4e24`;
- workflow `33887576972` — PASS;
- artifact `9942595794`;
- ZIP SHA-256 `04fccd1c1078e302ddc621f9b546248f6588afcf46aa5f5050b4173919c2999b`;
- APK SHA-256 `73282b433c519840e73ef9f8c8e63a311dcdf7bd9352c299469a0f7c290be079`.

It is historical evidence only once M4 changes production code. Do not use it as the eventual M6 candidate and never patch it in place.

## 6. Partial M4 scratch branch — not authoritative

An earlier isolated branch exists:

`tmp/phase4-m4-scope-holes`

Current scratch head:

`0af54ea30622812cf4c2837d3e40779bf5ac6e0d`

It contains early, **unaccepted and unintegrated** M4 work, including a new structured proficiency editor file. It is useful as source material only. It has not been promoted to the authoritative branch, has not passed the M4 gate, and must not be described as completed F14.

Resume M4 from a fresh safety branch based on the authoritative pre-QA consolidation head, then reapply/review useful scratch changes deliberately. Do not fast-forward the old scratch branch wholesale without comparing it against current authoritative continuity.

## 7. Exact next action

1. Create a fresh M4 implementation safety branch from the current authoritative `implementation/phase4-preqa-consolidation` head.
2. Start with **M4a/F14** because it requires the most explicit structural draft integration.
3. Reuse the existing durable proficiency model/repository; no schema migration is expected.
4. Wire the proficiency list into Habilidades and into the existing global structural dirty/Save/Discard/unsaved-leave flow.
5. Add focused pure/persistence evidence before declaring M4a complete.
6. Run the standard repository gate after the coherent M4a slice before proceeding to the remaining M4 holes.

Keep `main` untouched. Keep the historical L candidate untouched. Do not begin owner QA yet.
