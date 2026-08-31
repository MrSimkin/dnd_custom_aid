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

### Initiative

- Derived Initiative arithmetic works when the explicit/custom adjustment field contains a numeric value: **PASS for populated adjustment**.
- **BUG:** when the Initiative custom/explicit adjustment field is blank, the displayed Initiative is blank instead of treating the omitted adjustment as `0` and displaying the Dexterity-derived Initiative.
- Expected behavior: blank optional adjustment = `0`; the derived total should remain visible.
- Owner reports the current presentation is visually acceptable overall but the mechanism for entering custom/additional modifiers is **not clear or intuitive enough**.
- UX direction is deliberately not yet fixed. The concept of explicit additional modifiers should be workshopped before final V4 acceptance rather than merely relabeled without discussion.

**Initiative acceptance: NEEDS CHANGES.**

Implementation inspection confirms the blank-value defect is consistent with `initiativeAdjustment.toIntOrNull() ?: return null`; the optional field currently converts blank to a missing total instead of zero.

## Pending next checks / work

1. Fix the Initiative blank-adjustment behavior so blank means zero.
2. Workshop a clearer UX pattern for optional custom/additional modifiers, considering that the same concept applies beyond Initiative to saves, skills and Passive Perception.
3. Continue saving-throw, skill and Passive Perception derived-value checks from `docs/QA_CHECKLIST.md`; observations may help choose the adjustment UX pattern before implementing it broadly.
