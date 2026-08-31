# V4 follow-up build — pre-implementation consolidation

**Status:** DRAFT / IMPLEMENTATION BLOCKED PENDING OWNER ANSWERS  
**Date:** 2026-08-31  
**Working branch:** `implementation/character-data-foundation`  
**Baseline QA:** V4 run #107 manual QA is complete; see `2026-08-31_V4_RUN107_QA_CLOSURE.md`.

This document consolidates the next-build target before production code changes. Its purpose is to prevent implicit product decisions during implementation. **Do not begin the follow-up implementation until the open questions at the end have been reviewed with the owner and recorded.**

## 1. Baseline that must be preserved

The run #107 build established a passing functional baseline for:

- V3→V4 in-place migration and preservation of campaigns/PCs;
- preservation of legacy displayed derived totals through migration adjustments;
- ability-modifier arithmetic;
- saving-throw arithmetic and binary proficiency;
- skill arithmetic and none / Competente / Pericia state;
- Passive Perception base-10 arithmetic;
- class/hit-die behavior and multiclass entries;
- Habilidades layout selector/state;
- keyboard/IME accessibility;
- rotation and screen-off recreation;
- save/reopen persistence and full app restart.

The follow-up build must not regress these behaviors.

## 2. Settled derived-value changes

### Blank optional adjustments

For Initiative, saving throws, skills and Passive Perception:

- blank/omitted `Ajuste adicional` means `0`;
- a blank adjustment must never suppress the derived total.

### Progressive-disclosure adjustment UX

For derived values with optional adjustments:

- compact sheet shows the calculated final total;
- the total is interactive;
- activating it opens a compact calculation breakdown/editor;
- the editor exposes `Ajuste adicional` and the contributing arithmetic;
- zero adjustment stays visually quiet;
- non-zero adjustment may receive a small secondary indication;
- user-facing wording is `Ajuste adicional`, not `modificador personalizado`.

This applies to Initiative, saving throws, skills and Passive Perception. The same interaction is recommended for calculated proficiency bonus, but owner confirmation is requested below because proficiency bonus was approved later than the original four-value interaction.

### Proficiency bonus

- derive standard proficiency bonus from total character level;
- total level = sum of class levels;
- use ordinary level bands +2 through +6;
- preserve permissive/homebrew support through an additional adjustment rather than reverting ordinary proficiency bonus to manual arithmetic.

### Ability modifiers

- keep automatic arithmetic;
- make displayed ability modifiers slightly more visually prominent than run #107.

## 3. Numeric-editor behavior

Required numeric editors must allow a **temporary blank draft state while typing** so a user can naturally replace values such as `20` → blank → `8`.

The current implementation must not force an intermediate `1`, `0`, or other fallback digit on every keystroke.

Final save/commit behavior for a still-blank required value remains an explicit owner decision below.

## 4. Layout/presentation fixes from device QA

### Class / hit-die rows

Standardize geometry and visual rhythm across:

- normal class dropdown-selected mode;
- `Otro` custom/open class-name mode;
- `Nv.`;
- hit-die quantity (`DG`);
- hit-die type (`d8`, `d10`, etc.).

The issue applies in both normal and custom class modes.

### Combat reference

After moving spellcasting-specific information out:

- keep core reference: `CA` · `Iniciativa` · `Velocidad`;
- keep health: `PG actuales` · `PG máximos` · `PG temporales`;
- secondary general reference contains `Bonificador por competencia` and `Percepción pasiva`;
- improve portrait/landscape common alignment and row/baseline rhythm rather than allowing subgroup transitions to look accidental.

### Habilidades — Por atributo

The grouping concept remains accepted. Improve phone-width allocation so long labels such as `Juego de manos` and `Investigación` do not fragment awkwardly or crowd totals/training controls.

### 115% / 130% text scale

Keep the larger text-size choices. Fix row geometry so when one label wraps to two lines, neighboring cells in the same logical row align to the resulting row height rather than remaining at an independent height/baseline.

## 5. Quick Magic — settled scope, interaction detail still open

Approved scope:

- remove `CD de salvación de conjuros` from `Referencia de combate`;
- add a compact Quick Magic reference block;
- include manual spell-slot tracking by spell level;
- include manual spell save DC;
- include manual spell attack modifier;
- include manual spellcasting ability / `Aptitud mágica`;
- do not infer slots, spell DC, attack modifier, legality, spell lists, preparation or multiclass spellcasting rules in this slice.

Approved current placement remains **bottom of `Resumen`** unless the separate tab-architecture discussion below results in a consciously revised decision.

The exact spell-slot interaction is not yet fixed and must be agreed before implementation.

## 6. Theme audition — approved direction

Retire `Gris claro` and replace it with a genuinely neutral `Gris`.

Keep existing general themes that were not rejected, including system/light/dark and `Morado oscuro`, and add reversible phone-QA candidates:

- `Gris`;
- `Cian oscuro`;
- `Azul noche`;
- `Verde bosque`;
- `Pergamino`;
- `Alto contraste`;
- `Matriz` — near-black + vivid green, recognizable black/green identity without decorative falling-code effects.

These are audition candidates and should be pruned after intended-device QA rather than assumed permanent.

Saved-preference migration should preserve intent when choices are renamed/retired; for example an existing `LIGHT_GRAY` preference should migrate to the new neutral `Gris` rather than silently falling back to an unrelated theme.

## 7. Font audition — expanded proposal, pending owner confirmation

The owner requested applying the same reversible-audition philosophy used for themes to typography.

Recommended next-build audition size: **8 fonts** — enough variety to compare but still manageable on one phone.

### Normal-width sans candidates

1. **Manrope** — retained from V4;
2. **Sora** — retained from V4;
3. **Source Sans 3** — humanist/UI-oriented alternative;
4. **Lexend** — high-legibility / more openly spaced alternative.

### Condensed / narrow candidates

5. **Barlow Condensed** — retained from V4;
6. **Roboto Condensed** — replacement direction already discussed for IBM Plex Sans Condensed;
7. **Archivo Narrow** — compact grotesque/narrow alternative;
8. **Oswald** — stronger/tighter narrow alternative that deliberately tests a more distinctive end of the condensed spectrum.

The audition is not a branding commitment. The next phone QA should remove fonts that are unattractive, redundant, insufficiently readable, or destabilize layout.

**IBM Plex Sans Condensed remains rejected as a candidate, but its slot is replaced rather than reducing condensed-font variety.**

Saved font preference migration must be intentional. If an existing device preference is IBM Plex Sans Condensed when that enum/value is retired, recommended behavior is to migrate that preference to **Roboto Condensed** rather than silently switch the user to a normal-width default.

## 8. Additional character-sheet tabs — exploration gate

The owner wants the next design/build pass to consider additional character-sheet tabs.

Do **not** add empty placeholder tabs merely to preview names. D-0045's principle remains useful: a tab should correspond to meaningful content/domain responsibility.

Candidate future domains worth discussing include:

- `Combate` / actions and attacks;
- `Rasgos` / features, traits, languages and proficiencies;
- `Equipo` / inventory, currency and treasure;
- `Magia` / detailed spell-management content if/when that domain grows beyond Quick Magic;
- `Notas` / freeform session or character notes.

These are discussion candidates, not approved tabs. Adding a tab that requires new durable character data is a scope decision, not merely visual navigation work.

## 9. Explicitly out of current scope

- named multiple modifier sources (far-future idea only);
- full character builder / legality enforcement;
- automatic spell-slot inference;
- spell list/preparation system merely because Quick Magic exists;
- empty future-feature tabs;
- opening/merging the Phase 4 PR before the follow-up APK passes targeted owner QA.

## 10. Open questions — coding gate

### Q1 — Expanded font audition

Approve the proposed eight-font set?

- Manrope;
- Sora;
- Source Sans 3;
- Lexend;
- Barlow Condensed;
- Roboto Condensed;
- Archivo Narrow;
- Oswald.

If not, identify additions/removals before implementation.

### Q2 — New tabs: architecture experiment or actual new data domains?

For the next build, does the owner want:

A. only to **discuss/decide** the future tab architecture while keeping implemented tabs at `Resumen` + `Habilidades`; or

B. to actually implement one or more new tabs now, which means choosing meaningful content/data for them?

Assistant recommendation: do **not** create empty tabs. If implementing new tabs now, choose at most one or two meaningful domains so this Phase 4 follow-up remains testable.

### Q3 — Quick Magic if a future `Magia` tab is eventually added

Assistant recommendation: keep **Quick Magic at the bottom of `Resumen` as the quick summary**, even if a later detailed `Magia` tab exists. A future `Magia` tab would contain deeper spell-management content rather than stealing the quick-reference block from the overview.

Confirm or revise.

### Q4 — Spell-slot manual interaction

Choose the intended lightweight model:

A. `Total` + `Gastados` as two numeric values per spell level;

B. numeric `Total` plus tappable slot marks/pips to mark spent slots during play;

C. manual `Restantes / Total` numeric values;

D. another interaction supplied by the owner.

Assistant recommendation: **B**, if we can preserve arbitrary/homebrew totals cleanly; otherwise **A** is the simplest durable model.

### Q5 — Required numeric value left blank at Save

Temporary blank while typing is approved. What happens if the user presses Save while a semantically required numeric field is still blank?

Assistant recommendation:

- do not coerce it to `1`, `0`, or the previous value;
- keep the editor open;
- mark the field as required/invalid inline;
- do not persist an incomplete required value until corrected.

Confirm or revise.

### Q6 — Proficiency bonus adjustment interaction

Because proficiency bonus is now calculated but may have a homebrew adjustment, should it use the **same interactive-total → breakdown → `Ajuste adicional`** pattern as Initiative/saves/skills/Passive Perception?

Assistant recommendation: **yes**, for consistency.

## 11. Implementation gate

No production-code implementation should begin until Q1–Q6 have been answered or explicitly deferred by the owner. After those answers, update the controlling decisions/specification first, then implement the follow-up APK.