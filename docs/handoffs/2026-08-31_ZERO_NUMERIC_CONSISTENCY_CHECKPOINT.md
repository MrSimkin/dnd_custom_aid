# Recovery checkpoint — zero numeric consistency

**Date:** 2026-08-31  
**Branch:** `implementation/character-data-foundation`

Before the next Android editor integration step, one persistence consistency issue was resolved.

## Completed

- Commit `66b2a32c208443aa769672c4f06bcabcafeda164` changes `CharacterRepository` so a hit-die size of `0` is allowed as incomplete persisted draft data.
- This aligns custom hit-die input with the already approved rule that a required numeric field may be cleared while editing and, if the owner confirms the Save warning, the blank value is persisted as `0`.
- Negative hit-die sizes remain rejected.
- No visible Android UI behavior has been changed by this checkpoint yet.

## Next step

Continue the Android character editor integration with:

1. temporary blank required numeric editing;
2. Save warning/confirmation before blank required numbers become zero;
3. blank optional adjustments treated as zero;
4. calculated proficiency bonus with `Ajuste adicional`;
5. progressive-disclosure calculation editors for Initiative, saves, skills and Passive Perception.

Do not add Quick Magic or the new `Combate` / `Equipo` tabs until this cross-cutting editor increment is compiled, tested and checkpointed.
