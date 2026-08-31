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
- Additional visual observation: the derived ability modifiers should be made **slightly larger/more prominent** so they do not get visually lost beneath the ability scores.

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

### Saving throws

- Tested proficiency off/on, positive and negative explicit adjustments, and derived arithmetic: **PASS**.
- Owner reported **no saving-throw-specific notes or defects** beyond the already-recorded global optional-adjustment behavior; do not duplicate that known issue in each derived-value section.

**Saving-throw-specific acceptance: PASS.**

### Skills

- Tested skill arithmetic across no proficiency / `Competente` / `Pericia` and explicit positive adjustment: **PASS**.
- Training-state changes produced the expected totals: **PASS**.
- The fixed-footprint empty / single-check / double-check training control is functionally understandable and acceptable: **PASS**.
- Owner reported no additional skill-mechanics notes beyond the already-recorded global adjustment behavior.

**Skill mechanics/control acceptance: PASS.**

The owner-provided screenshots also show a **presentation issue in `Por atributo`**, not a skill-mechanics failure: at phone width, some labels such as `Juego de manos` and `Investigación` are forced into awkward multi-line fragments and compete with totals/training/adjustment controls for width. The ability-centered grouping concept remains accepted, but its responsive geometry needs refinement.

### Passive Perception

- Tested Wisdom/Perception/proficiency arithmetic and passive-specific positive adjustment: **PASS**.
- Owner reported no Passive-Perception-specific presentation or functional issue beyond the already-recorded global optional-adjustment behavior.
- Rule formula was explicitly re-verified against both official SRDs because the owner remembered a possible base-8 rule.
- **SRD 5.1:** passive checks use `10 + all modifiers that normally apply to the check`; passive Wisdom (Perception) therefore uses base 10.
- **SRD 5.2.1:** explicitly defines `Passive Perception = 10 + Wisdom (Perception) check modifier`.
- No 8→10 change exists between SRD 5.1 and SRD 5.2.1 for this rule. The current V4 base-10 arithmetic is correct.

**Passive Perception acceptance: PASS.**

### Screenshot-reviewed layout / consistency observations

Owner supplied three annotated phone screenshots during this QA pass. The screenshots make the earlier generic alignment/consistency concern concrete:

1. **Portrait — class / hit-die row:** the `Tipo` / `d8` selector uses noticeably different control geometry from adjacent `Nv.` and `DG` numeric fields. Although functional and no longer vertically wrapping, the row reads as visually inconsistent. Next build should standardize alignment/visual rhythm without losing the compact die selector.
2. **Landscape — combat reference:** `Referencia`, `Puntos de golpe`, and `Referencia secundaria` do not read as one clean aligned grid. The transition around `PG temporales` → `Referencia secundaria` / `Bono competencia` is especially visually uneven. Preserve semantic subgrouping, but improve common baselines/field alignment and spacing.
3. **Portrait — `Por atributo`:** the two-column ability-group layout becomes too tight for several skill labels and related controls. Labels wrap into awkward fragments, while the save/skill control areas feel crowded. Keep `Por atributo` as an accepted alternative view, but tune responsive column widths/label allocation and internal alignment.

These are **presentation/layout findings for the next build**, not reasons to reject the underlying class, combat-reference, or skill-grouping concepts.

### Additional UX / product observations from this pass

1. **Proficiency bonus:** owner approves expanding the basic calculation assistance slightly and wants `Bonificador por competencia` to become a calculated value rather than a manually maintained reference field. This is not a request for a full character builder.
2. **Quick Magic / spellcasting reference — APPROVED NEXT-BUILD DIRECTION:** remove `CD de salvación de conjuros` from `Referencia de combate`. Add a separate compact **Quick Magic** reference block, using the owner-supplied paper-sheet crop as grouping inspiration rather than a literal layout requirement. The block is deliberately lightweight and manual rather than a spellcasting builder. It should provide:
   - spell-slot tracking by spell level, with available/total space and spent-slot marking suitable for quick session use;
   - spell save DC;
   - spell attack modifier;
   - spellcasting ability (`Aptitud mágica`).
   These spellcasting reference values remain manually entered for now; the approved purpose is better grouping and quick reference, not additional rule automation.
3. **Numeric editing bug:** required numeric inputs currently cannot be temporarily cleared while replacing a value. Example: changing Strength `20` to `8` forces an awkward intermediate value because deleting the last digit is rejected and the field retains a value. Expected UX: allow a temporary blank editor state while typing/replacing a number, then validate requiredness/range at save/commit rather than blocking deletion keystrokes.

Implementation inspection confirms the numeric editing behavior: ability-score input is passed `allowBlank = false`, and `CompactIntInputV4` only propagates an edit when `allowBlank || cleaned.isNotBlank()`. This explains the observed inability to clear the field while editing.

## Pending next checks / work

1. Finish class/hit-die, combat-reference, selector, Settings/themes/fonts, and regression QA on the current APK.
2. In the next follow-up build:
   - fix blank optional adjustments globally;
   - implement the approved interactive derived-value breakdown/editor pattern;
   - make ability modifiers slightly more prominent;
   - calculate proficiency bonus from character level while preserving the project’s permissive exception philosophy;
   - allow temporary blank numeric draft states during editing and validate on save/commit;
   - remove spell save DC from `Referencia de combate` and add the approved manual Quick Magic reference block;
   - standardize class/hit-die control geometry;
   - improve landscape combat-reference alignment;
   - refine `Por atributo` phone-width label/control allocation while keeping the accepted grouping concept.
