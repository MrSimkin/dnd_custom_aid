# D-0046 — Character derived values and explicit adjustments

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner

## Context

Phase 4 character QA and a targeted review of the official Spanish SRD 5.2.1 showed that the current first-slice model stores several values as manually entered final totals even though the application already has enough information to calculate their ordinary D&D values.

The project must remain permissive: homebrew, gifts, mixed rule generations and owner-granted exceptions must remain representable. The application is not a legality checker and must not reject a character merely because a value differs from ordinary SRD arithmetic.

The owner approved replacing manual maintenance of ordinary arithmetic with a **calculated standard value + optional explicit adjustment** model.

## Approved model

### 1. Ability modifiers

Ability modifiers are derived automatically from the stored ability score using the normal D&D relationship:

`modifier = floor((score - 10) / 2)`

The modifier is displayed beside/with its ability score and is not entered as an independent durable value.

Implementation must use floor semantics for odd negative differences; ordinary truncating integer division is not sufficient for scores below 10.

### 2. Skills

For each standard skill:

`standard = associated ability modifier + proficiency contribution`

Proficiency contribution is:

- none: `0`;
- Competente / proficient: `1 × proficiency bonus`;
- Pericia / expertise: `2 × proficiency bonus`.

Durable skill state becomes conceptually:

- skill key;
- training state (`NONE`, `PROFICIENT`, `EXPERTISE`);
- explicit numeric **adjustment**.

Displayed/final total:

`skill total = standard + adjustment`

The adjustment may be positive, negative or zero and is the escape path for gifts, homebrew and other exceptions.

### 3. Saving throws

For each ability saving throw, durable state becomes conceptually:

- binary saving-throw proficiency state;
- explicit numeric adjustment.

Displayed/final total:

`save total = ability modifier + (proficiency bonus if proficient) + adjustment`

Saving-throw proficiency is binary and must not reuse the three-state skill training model.

### 4. Passive Perception

Passive Perception is derived from the **final Perception skill total**, with a separate optional adjustment for effects that apply specifically to passive Perception:

`Passive Perception = 10 + final Perception total + passive Perception adjustment`

This avoids duplicating Perception arithmetic while retaining an explicit exception path.

### 5. Initiative

Initiative is derived from Dexterity plus an explicit adjustment:

`Initiative = Dexterity modifier + initiative adjustment`

### 6. Proficiency bonus

During V4 phone QA the owner clarified that the earlier “no character builder” boundary does not prohibit simple deterministic calculations that remove clerical arithmetic. The owner therefore approved making **Bonificador por competencia** a calculated value in the next follow-up build.

The standard proficiency bonus should be derived from **total character level** (sum of class levels) using the ordinary D&D progression:

- levels 1–4: `+2`;
- levels 5–8: `+3`;
- levels 9–12: `+4`;
- levels 13–16: `+5`;
- levels 17–20: `+6`.

This remains calculation assistance rather than guided/legal character building. The application should not begin enforcing class legality, advancement choices or character-build rules merely because it calculates this deterministic value.

If a future/homebrew exception requires a proficiency bonus different from the standard derived value, preserve the project’s permissive philosophy through the same **calculated standard + Ajuste adicional** concept rather than reverting the normal field to manual arithmetic.

### 7. Values that remain explicit in this slice

The following remain explicitly stored/manual for now rather than being expanded into additional rule automation:

- Armor Class;
- maximum/current/temporary HP;
- speed;
- optional spell save DC, while its final UI placement is separately under review.

Those values may have broader contextual derivations that are outside the current character-foundation slice.

## Product principle

This is **calculation assistance, not rules enforcement**.

A standard value is calculated because the application already knows the inputs. An explicit adjustment then preserves the ability to represent any final value required by a gift, house rule, homebrew feature or other exception.

The UI should make the ordinary total easy to understand without forcing the user to maintain arithmetic manually.

## Approved presentation direction after V4 QA

The owner approved a progressive-disclosure interaction for all derived values that support optional adjustments, including Initiative, saving throws, skills and Passive Perception.

The compact/default sheet should prioritize the **calculated final total** and should not permanently expose a small unexplained adjustment input beside every value.

The derived total itself should be interactive. Activating it should open a compact breakdown/editor that explains the calculation and exposes the optional **Ajuste adicional**. Example for a skill:

- ability contribution;
- proficiency/training contribution;
- `Ajuste adicional`;
- final total.

When the additional adjustment is zero, the compact sheet should remain visually clean. When it is non-zero, the UI may show a small secondary indication such as `ajuste +2` so the user can see that the ordinary calculation contains an exception without making that exception control visually dominant.

Use **Ajuste adicional** rather than **modificador personalizado** in user-facing language. `Modificador` already has a specific D&D meaning for ability modifiers and should not be overloaded unnecessarily.

This interaction replaces the current V4 presentation of always-visible `±0`/adjustment inputs in the next build. The durable arithmetic model itself remains unchanged.

### Optional far-future enhancement — named modifier sources

A richer system could someday store multiple named adjustment sources, for example `Alerta +5`, `Objeto mágico +1` or `Maldición -2`, and sum them into the final adjustment.

This is recorded only as a **possible far-future enhancement**. It is **not part of the MVP, not on the current roadmap, and not a current implementation plan**. It should only be reconsidered if real use later demonstrates that a single explicit adjustment is insufficient.

## Blank optional adjustment semantics

An omitted/blank optional adjustment is semantically `0`.

Therefore Initiative, saving throws, skills and Passive Perception must continue to calculate and display normally when their additional-adjustment input has never been populated or has been cleared. Blank optional adjustment state must not suppress the derived total or make an otherwise valid character unsavable.

## Migration from the V3 model

V3 currently stores final initiative, saving-throw, passive-Perception and skill totals.

Migration must preserve existing displayed totals wherever the old data contains enough information to do so.

### Skills

For each existing skill:

`new adjustment = old final modifier - newly calculated standard`

Because V3 already stores skill training state, this preserves the old final skill total exactly.

### Initiative

`new initiative adjustment = old initiative total - Dexterity modifier`

This preserves the existing initiative total exactly.

### Passive Perception

After the Perception skill has been migrated:

`new passive adjustment = old passive Perception - (10 + migrated final Perception total)`

This preserves the existing passive-Perception total exactly.

### Saving throws

V3 stores only the final saving-throw number. It does **not** store whether each saving throw is proficient.

Therefore migration must not guess proficiency from arithmetic, class or apparent totals.

For existing characters:

- initialize saving-throw proficiency as `false`;
- set the adjustment so the old displayed total is preserved:

`new save adjustment = old final save - ability modifier`

This preserves the number exactly but cannot recreate proficiency metadata that was never stored. The player may mark the appropriate proficient saves after migration.

This limitation is acceptable for the current QA/test data and is preferable to silently inventing rules state.

## Data-shape guidance

The durable domain model should expose **inputs/state + adjustment**, not duplicate authoritative calculated totals.

Implementation may use the simplest safe SQLite/SQLDelight migration technique, including temporary/deprecated columns if necessary during migration, but the resulting domain contract must not treat both a stored final total and a calculated total as competing authoritative values.

## Consequences for V4

V4 must:

- show automatic ability modifiers;
- calculate skill totals from ability + training + proficiency bonus + adjustment;
- calculate saving throws from ability + binary proficiency + proficiency bonus + adjustment;
- calculate Passive Perception from final Perception + passive adjustment;
- calculate Initiative from Dexterity + initiative adjustment;
- expose adjustments compactly without turning the sheet back into a large form;
- keep the product permissive for arbitrary final totals;
- migrate existing V3 test data without silently changing displayed totals where preservation is possible;
- clearly acknowledge that old save proficiency cannot be reconstructed because it was not previously stored.

The **next V4 follow-up build** must additionally:

- treat blank optional adjustments as zero across Initiative, saves, skills and Passive Perception;
- replace the always-visible adjustment fields with the approved interactive-total / calculation-breakdown pattern;
- derive proficiency bonus from total character level and preserve an adjustment escape path for exceptional/homebrew cases;
- retain the current simple single numeric adjustment model rather than introducing named modifier sources.
