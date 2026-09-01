# Gate C checkpoint — explicit spell association cleanup promoted

Date: 2026-09-01
Branch: `implementation/character-data-foundation`

## Recovery point

The validated repository hardening has been promoted to the implementation branch.

- Promotion commit: `c4871f30bce9d76440253d7e335026c59c236239`
- `CharacterRepository.kt` resulting blob: `583b900049dbe1e1b2a475c305ecd27820ddde72`
- Maintenance query commit: `f301e9d9f3b83e9804bfc9337a4a1b61036dd1cd`

## Exact correction

Before replacing conceptual spells and spellcasting sources during `saveCharacter`, the repository now explicitly deletes all spell-source association rows belonging to that character through `CharacterMaintenance.sq`.

Persistence order is now:

1. delete character-owned spell/source associations;
2. delete conceptual spells;
3. delete spellcasting sources;
4. reinsert normalized sources;
5. reinsert conceptual spells;
6. reinsert source associations including per-source `prepared` state.

This removes reliance on SQLite foreign-key cascade enforcement for repeated character saves.

## Safety validation already performed

The repository edit was first applied on temporary branch `tmp/gate-c-association-cleanup` at commit `8f932572905a7eaa666daf9fe89ebac59be6e0e7`.

Comparison against the working branch showed exactly one source-file change: **1 addition, 0 deletions** in `CharacterRepository.kt`. File head, changed region, and tail were refetched and verified before promotion.

## Rejected approach retained for recovery context

A trigger-based cleanup approach was rejected because the current SQLDelight parser does not accept SQLite trigger pseudo-row references such as `OLD.id` in schema source. That diagnosis is checkpointed at `ef067577d8511560173727d5a1d4ba3b300fc714`.

## Gate status

Increment C is **not closed yet**. The next action is a full Scaffold checks / Gate C run on this checkpoint. Increment D remains blocked until that run is green.