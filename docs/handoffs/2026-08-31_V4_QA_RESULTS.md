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

**Initiative acceptance: NEEDS CHANGES.**

Implementation inspection confirms the blank-value defect is consistent with `initiativeAdjustment.toIntOrNull() ?: return null`; the same blank-as-invalid pattern also appears in save, skill and Passive Perception adjustment parsing and must be corrected consistently rather than only for Initiative.

### Approved adjustment UX direction for next build

The owner approved the progressive-disclosure design discussed during QA:

- compact/default presentation shows the calculated final value rather than a permanently visible unexplained adjustment box;
- the calculated value itself is interactive;
- tapping it opens a compact calculation breakdown/editor;
- the breakdown exposes an optional **Ajuste adicional** and shows the relevant arithmetic inputs/contributions plus the final total;
- when adjustment is zero, the compact sheet stays visually clean;
- when adjustment is non-zero, a small secondary indication such as `ajuste +2` may be shown;
- the same interaction pattern applies to Initiative, saving throws, skills and Passive Perception;
- use `Ajuste adicional` rather than `modificador personalizado` in user-facing wording.

This approved UX change goes into the next build; the owner wants to continue testing the current V4 APK first.

A multiple named-modifier-source model (for example `Alerta +5`, `Objeto mágico +1`, `Maldición -2`) is recorded only as a potentially useful **far-future enhancement**. It is explicitly **not MVP, not current roadmap, and not a current implementation plan**.

## Pending next checks / work

1. Continue current-build QA with saving throws.
2. Continue skill and Passive Perception derived-value checks.
3. Finish remaining V4 presentation/regression QA before changing the APK.
4. In the next follow-up build, fix blank optional adjustments globally and implement the approved interactive derived-value breakdown/editor pattern.