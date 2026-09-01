# Project State

**Last verified:** 2026-09-01  
**Canonical branch:** `main`  
**Phase 4 durable working branch:** `implementation/character-data-foundation`  
**Current recovery/increment branch:** `tmp/increment-j-notes-tab`  
**Open review:** none  
**Phase:** Phase 4 — MVP Buildout  
**Status:** Increment J (`Notas`) is technically closed after green automated Gate J. The branch is ready for descendant-only fast-forward promotion to `implementation/character-data-foundation`. `main` remains untouched. Intended-device phone QA remains separate under C-0010.

## 0. Latest verified checkpoint

Increment I is durably promoted and its promoted head `4dd1e86b2ad62cea0789baede6bf20af8bae2b15` passed `Scaffold checks` run `33465502292` completely.

Increment J resumed from that exact green head. Gate J tested `3732dbd62414e06ab8c2ef1820d14009dd518173` in run `33465839442`:

- backend check: **PASS**;
- shared Kotlin/SQLDelight tests: **PASS**;
- file-backed Notes persistence/reopen regression: **PASS** as part of the shared suite;
- Android debug build: **PASS**;
- Desktop build: **PASS**;
- APK upload: **PASS**.

Gate J APK artifact: `9784853364`, digest `sha256:d55958ca91f1d56b0f673b003acdc386f94210babfd128b835eda7da4ef646ae`.

Detailed closure: `docs/handoffs/2026-09-01_INCREMENT_J_NOTES_CLOSURE.md`.

## 1. Authority and working rules

- Git is operative project memory; repository truth controls over chat memory.
- `main` is canonical accepted state and must not receive Phase 4 work without explicit owner approval.
- Substantial Phase 4 work continues on focused non-`main` branches and is promoted to `implementation/character-data-foundation` only after its increment gate is green.
- Every meaningful step needs a durable Git checkpoint.
- C-0009 controls proportionality: prefer the simplest safe implementation satisfying approved requirements.
- Character-sheet manual acceptance is phone-first under C-0010; green CI does not equal manual acceptance.

## 2. Phase 4 implementation sequence

The approved consolidated next-build sequence is recorded in `docs/handoffs/2026-08-31_NEXT_BUILD_CONSOLIDATED_IMPLEMENTATION_PACKAGE.md`.

Current status:

- Increment A — baseline verification/map: **closed**;
- Increment B — approved corrective UX backlog: **implemented through its recorded checkpoints; unresolved owner-audition font choices remain a later QA/design boundary where documented**;
- Increment C — persistent data foundation/migration for new character domains: **closed**;
- Increment D — top-level navigation + PC Settings: **closed**;
- Increment E — Trasfondo: **closed**;
- Increment F — Rasgos: **closed**;
- Increment G — Conjuros source management: **closed**;
- Increment H — Conjuros spell list/details: **closed**, automated gate green;
- Increment I — Quick Magic / Conjuros shared slots: **closed**, automated gate green and durable promoted head green;
- Increment J — Notas: **closed**, automated gate green;
- Increment K — responsive/accessibility integration: **next implementation boundary**;
- Increment L — final automated regression + owner QA candidate: pending.

## 3. Current character-sheet architecture

The Android V4 character editor supports the approved top-level character navigation including `General`, `Habilidades`, `Combate`, `Equipo`, `Trasfondo`, `Rasgos`, conditional `Conjuros`, and persistent `Notas`.

`PC Settings` owns the character-wide `Lanzador de conjuros` switch:

- ON shows Quick Magic and `Conjuros`;
- OFF hides both;
- OFF never deletes spellcasting data;
- disabling while `Conjuros` is selected falls back to `General`.

The character editor keeps unsaved working state recreation-safe and persists through the existing central character Save boundary.

## 4. Conjuros state after Increment I

The model supports stable spellcasting sources, custom/non-class sources, `Todos` plus per-source views, conceptual spells associated with one or more sources, source-specific manual `Preparado`, grouped levels 0–9, spell CRUD/search/collapse/manual ordering, and permissive manual spell detail fields.

Quick Magic and `Conjuros` share the same authoritative `CharacterSheet.spellSlots` / `CharacterEditorDraftV4.spellSlots` state. Quick Magic remains the single manual profile for spell save DC, spell attack modifier, casting ability, slot-total configuration and restoration. Conjuros level headers edit only spent counts on that same state.

Focused regression: `shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterSpellSlotIntegrationTest.kt`.

## 5. Notas state after Increment J

`Notas` implements the approved hybrid model:

- prominent unrestricted `Notas generales` area;
- optional titled note cards;
- titled notes contain only title + content;
- add/edit/delete;
- compact preview;
- long-press drag ordering;
- no dates/tags/categories/session metadata;
- IME padding;
- recreation-safe unsaved JSON draft through `rememberSaveable`;
- persistence through existing `CharacterSheet.generalNotes` and `CharacterSheet.noteCards` only.

Focused regression: `shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterNotesPersistenceTest.kt`.

## 6. Persistent domain ownership

Current non-negotiable ownership boundaries remain:

- classes own class identity/levels;
- spellcasting sources are separate stable entities and may optionally reference classes;
- conceptual spells own spell details/level;
- spell-source associations own source-specific prepared state;
- Quick Magic and Conjuros share one spell-slot state;
- Trasfondo owns background/personality/history narrative;
- Rasgos owns structured features;
- Equipo owns item identity/equipment/attunement/weight/location;
- Combate may contain manual quick-play summaries but is not authoritative for spells/traits/items;
- Notas is free-form and non-authoritative for structured game state;
- broken optional links fail softly; destructive cascades are reserved for true ownership boundaries.

## 7. Verification and manual-QA boundary

The standard automated gate remains:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

plus backend `npm install` / `npm run check` through `Scaffold checks`.

Automated Gates G, H, I and J are green at their recorded closure heads. Do not convert that into a claim of phone acceptance.

Still requiring intended-device owner QA include source/spell drag ergonomics, shared-slot pips, Notes text/drag/IME behavior, text-scale behavior, and portrait/landscape presentation. These are consolidated into the K/L acceptance path.

## 8. Documentation-history rule

Older `ROADMAP.md` / `ARCHITECTURE.md` prose contains historical phase snapshots that can lag later approved handoffs and actual branch state. Later specific decisions, the consolidated implementation package, dated handoffs, actual Git refs and CI evidence control when they conflict.

This file is the current operative state snapshot.

## 9. Next exact action

1. Confirm Increment J closure is a descendant-only update and fast-forward `implementation/character-data-foundation`.
2. Keep `main` unchanged.
3. Begin **Increment K — Responsive + accessibility integration pass** on a new focused branch.
4. Audit and correct:
   - 80/90/100/115/130% text-scale assumptions;
   - portrait + landscape behavior;
   - top-level tab strip integrity/selected visibility;
   - Conjuros source-strip behavior with long/multiple names;
   - `Habilidades` `Por atributo` two-column behavior at 115/130;
   - wide-layout use in Trasfondo/Rasgos/Conjuros/Notas;
   - IME-safe editors and outside-tap state safety;
   - semantic/vector icon controls and touch targets;
   - Unicode pseudo-buttons;
   - drag usability at large text scale.
5. Record static/automated findings separately from intended-device visual/ergonomic QA.
6. Run Gate K before promotion or moving to Increment L.
