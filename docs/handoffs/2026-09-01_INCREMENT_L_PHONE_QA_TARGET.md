# Increment L — Final automated regression / phone QA target

**Date:** 2026-09-01  
**Branch:** `tmp/increment-l-final-regression-qa-target`  
**Executable/tested commit:** `089a991c6491627961f1e75f3815959a8a1c8b48`  
**Baseline:** promoted Increment K head `a43526a1a0ae9d30a0b53023fa4a8b9ee1836f02`  
**Status:** automated Phase 4 implementation sequence complete through Increment L; owner phone QA pending.

## 1. Gate L result

Authoritative workflow: `Scaffold checks`  
Run ID: `33468310534`  
Tested head: `089a991c6491627961f1e75f3815959a8a1c8b48`

Result:

- backend install/type-check: **PASS**;
- full shared Kotlin/SQLDelight suite: **PASS**;
- existing migration/data-preservation tests: **PASS**;
- new holistic Phase 4 disk-reopen regression: **PASS**;
- Android debug build: **PASS**;
- desktop build: **PASS**;
- APK upload: **PASS**.

Final owner-QA APK artifact:

- artifact ID: `9785676981`;
- name: `dnd-custom-aid-debug-apk`;
- size: `11120637` bytes;
- digest: `sha256:4836f5b1fe1b9ae8cb11bdb6b61231782a2a474377afb4f9e27a347288d0f194`.

This is the clearly identified APK to use for the owner phone-QA sequence below.

## 2. Increment L regression addition

`CharacterPhase4FinalRegressionTest.kt` adds a file-backed current-schema round trip that stores representative legacy/run-#180 domains and all new Phase 4 domains on one character, closes SQLite, reopens it, verifies the combined state, and confirms spellcasting OFF remains non-destructive.

It covers representative core stats, class/hit dice, saves, skills, proficiency state, Quick Magic/slots, combat entries, inventory/special equipment, currencies, caster visibility, Background, Traits, spell sources/multi-source prepared state, and Notes.

No production source, schema, migration, or UI behavior changed in Increment L.

## 3. Owner phone-QA checklist

Use APK artifact `9785676981` from run `33468310534`.

### A. Migration preservation

1. Upgrade/open a character created against the accepted run-#180 state.
2. Confirm old stats, classes, saving throws, skills, proficiency, Quick Magic, spell slots, combat entries, equipment and currencies remain unchanged.
3. Confirm newly introduced Background, Traits, Notes and conceptual-spell domains begin empty for migrated data where appropriate.
4. Confirm caster-toggle migration is correct for representative caster and non-caster characters.

### B. Navigation and PC Settings

5. Verify seven top-level tabs when spellcasting is OFF and eight when ON.
6. Verify horizontal top-tab scrolling and selected-tab visibility at 80%, 90%, 100%, 115% and 130%.
7. Verify rotation/recreation preserves the selected tab when it still exists.
8. While on Conjuros, disable spellcasting and confirm deterministic fallback to General.
9. Re-enable spellcasting and confirm data returns without forcing navigation to Conjuros.
10. Confirm the hide-not-delete warning appears when disabling a character with meaningful spellcasting data.

### C. Run-#180 corrective backlog

11. Check General adjustment-marker behavior and imperial-first speed with approximate metric display.
12. Check Combate vertical alignment, keyboard safety, explicit editor dismissal and drag reorder.
13. Check Equipo keyboard safety, drag reorder, compact currencies, responsive columns and special-item presentation.
14. Check Habilidades -> Por atributo remains two-column at 115% and 130%.
15. Check Settings font/theme corrections and persistence.

### D. Trasfondo

16. Edit, save and reopen every narrative field.
17. Check both image placeholders respond correctly to available width.
18. Check compact narrative cards and the larger Story area.
19. Check keyboard reachability and confirm outside taps never silently discard active narrative edits.

### E. Rasgos

20. Create several feature types and sources.
21. Exercise activation/action-type values.
22. Exercise max uses, spent uses and recovery text.
23. Drag reorder traits, save and reopen to confirm exact order.
24. Check wide multi-column presentation.

### F. Conjuros

25. Create multiple spellcasting sources, including a class-linked source and a custom source.
26. Rename, reorder and delete sources; verify selected-source fallback behavior.
27. Delete/unlink a linked class and confirm the surviving source/spells are preserved as designed.
28. Create conceptual spells across cantrips and levels 1–9.
29. Associate one conceptual spell with multiple sources.
30. Verify Todos displays that conceptual spell only once.
31. Verify Preparado can differ independently between sources.
32. Verify search is scoped to the current Todos/source view.
33. Drag reorder spells within a level, save and reopen to confirm order.
34. Verify Quick Magic and Conjuros spell-slot pips stay synchronized in both directions.

### G. Notas

35. Use Notas generales as a large unrestricted scratchpad.
36. Create, edit, delete and drag-reorder titled note cards.
37. Check keyboard behavior and rotation/recreation with Notes state.

### H. Final resilience

38. Switch repeatedly among all available tabs and confirm no blank screen, crash or data loss.
39. Test screen off/on and a full app close/reopen.
40. Test portrait/landscape recreation with unsaved in-progress editor state where supported.
41. Toggle spellcasting OFF/ON and confirm hidden spellcasting data is never deleted.
42. Verify icon-only controls have usable touch targets and meaningful semantic/accessibility descriptions.

## 4. Acceptance / merge boundary

A green Gate L is not final owner acceptance.

The next build becomes eligible to be proposed as a merge candidate only when:

- all planned implementation checkpoints remain present;
- all CI gates are green;
- migration/data-preservation tests remain green;
- the 42-step owner phone QA has no unresolved blocking defect;
- known non-blocking limitations are explicitly recorded;
- this exact tested commit/APK identity remains recorded.

`main` remains unchanged until explicit owner approval of a later merge candidate.
