# V4 Character-Sheet QA — Incremental Results

**QA date:** 2026-08-31  
**Working branch:** `implementation/character-data-foundation`  
**QA target code:** `3c21cf649b31687180b73a8d314ca56eb937d147` — `Remove obsolete V3 character editor`  
**CI run:** #107 / `33358486525`  
**Artifact:** `dnd-custom-aid-debug-apk` / ID `9745937666`

This file records owner-supplied V4 manual QA observations incrementally. Do not ask the owner to repeat results already recorded here.

## Progress

### Installation / migration baseline

- V4 was installed **over V3 without uninstalling**: **PASS**.
- Existing campaigns remained present after the in-place update: **PASS**.
- Existing PCs remained present after the in-place update: **PASS**.
- Existing V3 Initiative, saving-throw, skill and Passive Perception displayed totals remained numerically unchanged after migration: **PASS**.
- Previously unusual/manual totals were preserved by V4 through the expected explicit adjustments rather than being silently normalized or changed: **PASS**.
- Saving-throw proficiency on migrated V3 PCs began unchecked, as required because V3 did not store that metadata: **PASS**.

**Migration acceptance: PASS.**

### Ability scores / automatic modifiers

- Representative ability scores produced the expected automatic modifiers: **PASS**.
- Owner reported the automatic modifier behavior looked correct for the requested representative checks.
- The six ability scores/modifiers fit acceptably in one row on the intended phone layout: **PASS / visually acceptable**.

## Pending next checks

1. Verify Initiative calculation from Dexterity modifier plus explicit signed adjustment.
2. Continue saving-throw, skill and Passive Perception derived-value checks from `docs/QA_CHECKLIST.md`.
