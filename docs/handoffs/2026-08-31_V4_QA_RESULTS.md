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

Migration is not yet marked fully PASS because preservation of the prior displayed Initiative, saving-throw, skill and Passive Perception totals has not yet been manually verified, and migrated saving-throw proficiency still needs confirmation that it begins unchecked rather than being guessed.

## Pending next checks

1. Open an existing V3-created PC and verify prior displayed Initiative, saving-throw, skill and Passive Perception totals remain numerically unchanged after migration.
2. Confirm saving-throw proficiency on migrated V3 PCs begins unchecked.
3. Continue with the remaining V4 suite in `docs/QA_CHECKLIST.md`.
