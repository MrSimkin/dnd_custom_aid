# Project State

**Last verified:** 2026-09-01  
**Canonical branch:** `main`  
**Phase 4 durable working branch:** `implementation/character-data-foundation`  
**Current recovery/increment branch:** `tmp/increment-i-shared-slot-integration`  
**Open review:** none  
**Phase:** Phase 4 — MVP Buildout  
**Status:** Increment I (`Quick Magic` / `Conjuros` shared spell-slot integration) is technically closed after a green automated Gate I. The recovery branch is ready for descendant-only fast-forward promotion to `implementation/character-data-foundation`. `main` remains untouched. Intended-device phone QA remains a separate owner acceptance boundary before any Phase 4 PR/merge.

## 0. Latest verified checkpoint

Increment H was closed at `bf8d5d4af8f750edd061d3589af1b6339fd8e8ee` and promoted to `implementation/character-data-foundation`.

Increment I resumed from that exact head and is closed on the current recovery branch. Gate I tested `2c79bde3dcee35c2c67d109a25cbb08fee23665b` in GitHub Actions run `33465230273`:

- backend check: **PASS**;
- shared Kotlin/SQLDelight tests: **PASS**;
- Increment I shared-slot regression: **PASS** as part of the shared suite;
- Android debug build: **PASS**;
- Desktop build: **PASS**;
- APK upload: **PASS**.

Gate I APK artifact: `9784647947`, digest `sha256:a4ee65b6ed075e0b8b0d5bea3805fe37679ec589492acad540c8134befd041eb`.

Detailed closure: `docs/handoffs/2026-09-01_INCREMENT_I_SHARED_SLOT_CLOSURE.md`.

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
- Increment I — Quick Magic / Conjuros shared slots: **closed**, automated gate green;
- Increment J — Notas: **next implementation boundary**;
- Increment K — responsive/accessibility integration: pending;
- Increment L — final automated regression + owner QA candidate: pending.

## 3. Current character-sheet architecture

The Android V4 character editor now supports the approved top-level character navigation including `Resumen`, `Habilidades`, `Combate`, `Equipo`, `Trasfondo`, `Rasgos`, conditional `Conjuros`, and the `Notas` shell.

`PC Settings` owns the character-wide `Lanzador de conjuros` switch:

- ON shows Quick Magic and `Conjuros`;
- OFF hides both;
- OFF never deletes spellcasting data;
- disabling while `Conjuros` is selected falls back to `General`/`Resumen`.

The character editor keeps its unsaved working state recreation-safe and persists through the existing central character Save boundary.

## 4. Conjuros state after Increment I

### Spell sources and conceptual spells

The model supports:

- stable spellcasting sources, optionally linked to a class;
- custom/non-class sources;
- `Todos` plus per-source filtered views;
- one conceptual spell associated with one or more sources;
- source-specific manual `Preparado` state;
- spell grouping by `Trucos` and levels 1–9;
- spell CRUD, search, collapsible levels, and manual within-level ordering;
- approved permissive spell detail fields without legality/rules enforcement.

Deleting a class unlinks an optional source link rather than deleting the source/spells. Deleting a source removes only its associations; conceptual spells and other associations survive.

### Shared spell slots

Quick Magic and `Conjuros` share the same authoritative `CharacterSheet.spellSlots` / `CharacterEditorDraftV4.spellSlots` state.

- Quick Magic remains the single manual profile for spell save DC, spell attack modifier, casting ability, slot-total configuration, and slot restoration.
- Conjuros levels 1–9 display compact spent/unspent pips for configured slot levels and can change the spent count.
- Changes in either surface are immediately represented by the other because both mutate/read the same editor draft.
- cantrips never show slots.
- no second slot cache, schema, migration, or `CharacterSpellcastingDraftV4` slot field exists.

Focused regression: `shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterSpellSlotIntegrationTest.kt`.

## 5. Persistent domain ownership

Current non-negotiable ownership boundaries from the approved implementation package remain:

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

## 6. Verification and manual-QA boundary

The repository's standard automated gate remains:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

plus backend `npm install` / `npm run check` through the single `Scaffold checks` workflow.

Automated Gates G, H and I are green at their recorded closure heads. Do not convert that into a claim of phone acceptance.

Still requiring intended-device owner QA before final Phase 4 acceptance include the accumulated V4/new-domain presentation and interaction checks, including Conjuros source/spell ergonomics and the new shared-slot pip interaction. The final consolidated phone QA target is produced only after the remaining implementation sequence reaches its QA-candidate boundary.

## 7. Known documentation history issue resolved

The previous `PROJECT_STATE.md`, and some older `ROADMAP.md` / `ARCHITECTURE.md` prose, still described an earlier V4/Phase 4 moment and incorrectly made later implemented domains appear deferred. On 2026-09-01 this was explicitly diagnosed during recovery. Later specific approved decisions, the consolidated implementation package, dated handoffs, actual branch graph and CI evidence control.

This file now represents the current operative implementation state. Older historical documents remain useful for rationale but must not be used to roll back later implemented/approved reality.

## 8. Next exact action

1. Fast-forward `implementation/character-data-foundation` to the Increment I closure once descendant-only ancestry is confirmed.
2. Keep `main` unchanged.
3. Begin **Increment J — Notas tab** from that durable head on a new focused recovery-safe branch.
4. Implement the approved hybrid notes model:
   - prominent unrestricted `Notas generales` area;
   - optional ordered titled note cards;
   - each card owns only title + content;
   - add/edit/delete;
   - drag-and-drop order;
   - IME-safe editing;
   - recreation/persistence coverage;
   - proportional wide-layout presentation.
5. Run Gate J before promotion or moving to Increment K.
