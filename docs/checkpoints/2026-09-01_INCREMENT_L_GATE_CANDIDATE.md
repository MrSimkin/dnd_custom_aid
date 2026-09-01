# Increment L — Final automated regression gate candidate

**Date:** 2026-09-01  
**Branch:** `tmp/increment-l-final-regression-qa-target`  
**Candidate parent head:** `2ce08020bd75b3e7659edd50663061ea4c1c1565`  
**Baseline:** promoted Increment K head `a43526a1a0ae9d30a0b53023fa4a8b9ee1836f02`

## Candidate scope

Increment L adds no product behavior. Relative to the promoted K baseline it contains only:

- the L implementation map;
- `CharacterPhase4FinalRegressionTest.kt`, a file-backed holistic persistence/reopen regression.

The regression combines representative legacy/run-#180 character paths with all new Phase 4 domains on one character, closes SQLite, reopens it, and verifies the combined state together. It also verifies hiding spellcasting remains non-destructive after reopen.

Ancestry before this checkpoint: 2 commits ahead of K, 0 behind.

## Existing migration evidence retained

The full shared suite already includes v3/v4 migration coverage, new-character defaults, caster migration, soft class/source ownership, spell multi-source/prepared behavior, shared spell slots, and Notes disk-reopen coverage. L intentionally adds only the remaining holistic current-schema coexistence regression rather than duplicating those migration tests.

## Authoritative Gate L

The `Scaffold checks` run triggered by this checkpoint must complete:

- backend install/type-check;
- full shared Kotlin/SQLDelight tests, including the new holistic final regression and all existing migration tests;
- Android debug assembly;
- desktop build;
- APK upload.

The APK artifact from this exact candidate is the intended owner-phone-QA target if Gate L is fully green.

## Acceptance boundary

Automated Gate L does not make the branch merge-eligible by itself. The approved 42-step owner phone-QA sequence must still have no unresolved blocking defect before a later merge candidate can be proposed.
