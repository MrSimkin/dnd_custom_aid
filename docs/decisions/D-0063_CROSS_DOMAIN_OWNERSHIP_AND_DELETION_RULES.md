# D-0063 — Cross-domain ownership and deletion rules

**Status:** Approved  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Context

Before implementation of the enlarged post-run-#180 character sheet, the project audited domain boundaries so the new tabs do not create duplicate authoritative state or unsafe cascades.

The owner approved the complete ownership model proposed in discussion, including soft reference failure and conservative deletion behavior.

## Approved ownership rules

### 1. Classes vs spellcasting sources

- `CharacterClassLevel` remains authoritative for PC classes and class levels.
- A spellcasting source may reference a class row, but does not own or duplicate the class level.
- A spellcasting source remains a stable entity even if its display name changes.
- Deleting a class must not automatically delete a linked spellcasting source or its spells.
- If a linked class disappears, the spellcasting source survives and becomes unlinked.

### 2. Quick Magic vs `Conjuros`

- Spell-slot state has one authoritative persisted representation shared by Quick Magic and `Conjuros`.
- Quick Magic remains the one primary manual at-a-glance profile for spell save DC, spell attack modifier and spellcasting ability.
- `Conjuros` owns conceptual spell records, spellcasting sources, spell-source associations and source-specific `Preparado` state.
- Never persist a duplicate set of slot totals/spent state merely because the values are shown in both UIs.

### 3. Spell level ownership

- A conceptual spell record owns its spell level.
- Spell-source associations do not have independent spell levels.
- A conceptual spell associated with multiple sources therefore retains one level.
- If a homebrew/exception case genuinely requires the same named spell at different spell levels, represent it as separate conceptual spell records rather than contradictory level state on one record.

### 4. `Trasfondo` vs `Rasgos`

- `Trasfondo` owns narrative character identity: background name/summary, personality traits, ideals, bonds, flaws and character story.
- `Rasgos` owns mechanical/descriptive character features.
- A mechanical feature originating from a background belongs in `Rasgos` with an appropriate type/source rather than being duplicated as authoritative feature data inside `Trasfondo`.

### 5. `Equipo` vs `Rasgos`

- A magic/special item's identity, quantity, weight, equipped state, notes, description, location and attunement remain authoritative in `Equipo`.
- Do not automatically create a `Rasgo` for every special/magic item.
- A player may manually create a related trait/feature for convenience, but that does not replace or become authoritative for the inventory item.

### 6. `Rasgos` / `Conjuros` vs `Combate`

- `Combate` is intentionally a quick-play list.
- A combat entry may contain a condensed representation of a spell, trait, item effect, attack or other action.
- Such a combat summary is not the authoritative full record of the underlying spell/trait/item.
- This build introduces no automatic cross-linking or synchronization between combat summaries and those richer domains.

### 7. `Notas`

- `Notas generales` and titled note cards own only free-form player notes.
- Text mentioning spells, items, traits, NPCs or other entities does not become authoritative structured game data merely because it appears in Notes.
- `Notas` remains deliberately permissive and independent.

### 8. Repeated structured references

- Structured values such as Speed, AC, HP and Initiative keep one authoritative character value even when displayed in more than one tab.
- `Combate` quick references are views of the same underlying character state rather than separately persisted values.
- The same principle applies to any future repeated reference surface.

## Approved reference/deletion principles

### A. References fail softly

When a referenced entity disappears, surviving data should remain whenever it can still make sense independently.

Examples:
- deleting a class unlinks a spellcasting source rather than deleting the source;
- removing one relationship does not silently destroy richer independent records.

### B. Cascade only for true ownership

- Deleting the PC may cascade through its character-owned domains.
- Deleting a spellcasting source may remove that source's associations after the approved warning/confirmation behavior, but must not delete a conceptual spell still associated with another source.
- Deleting a class must not cascade into spells, spellcasting sources, equipment, traits, notes or other unrelated character domains.
- Destructive cascades must be explicit consequences of actual ownership, not incidental references.

## Consequence

The cross-domain ownership gate is closed. Remaining product-design work before implementation is limited to responsive/navigation edge cases and the final consolidated implementation/QA package.
