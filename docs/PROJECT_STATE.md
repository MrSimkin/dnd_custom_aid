# Project State

**Last verified:** 2026-09-01  
**Canonical branch:** `main`  
**Phase 4 durable working branch:** `implementation/character-data-foundation`  
**Current recovery/increment branch:** `tmp/increment-k-responsive-accessibility`  
**Open review:** none  
**Phase:** Phase 4 — MVP Buildout  
**Status:** Increment K (`Responsive + accessibility integration`) is technically closed after a green automated Gate K and is ready for descendant-only promotion. `main` remains untouched. Intended-device phone QA remains a separate owner acceptance boundary.

## 0. Latest verified checkpoint

Increment J is durably promoted at `2fe7ab0bc6ce18f3956bda0ed750f433367e483b`.

Increment K resumed from that exact durable head. Gate K tested `757bc1d3f5d5b498120c70bdee33c1d2379a55c9` in `Scaffold checks` run `33467843328`:

- backend check: **PASS**;
- full shared Kotlin/SQLDelight tests: **PASS**;
- Android debug build: **PASS**;
- desktop build: **PASS**;
- APK upload: **PASS**.

Gate K APK artifact: `9785528879`, digest `sha256:b52bb78b1a0f4cfb80ede1986059507ca0ba5f5f13f0ed2c0f9e1f3fb4f85eec`.

Detailed closure: `docs/handoffs/2026-09-01_INCREMENT_K_RESPONSIVE_ACCESSIBILITY_CLOSURE.md`.

## 1. Authority and working rules

- Git is operative project memory; repository truth controls over chat memory.
- `main` is canonical accepted state and must not receive Phase 4 work without explicit owner approval.
- Phase 4 increments are promoted to `implementation/character-data-foundation` only after their automated gate is green and descendant-only ancestry is confirmed.
- Every meaningful implementation step leaves a durable Git checkpoint.
- C-0009 controls proportionality: prefer the simplest safe implementation satisfying approved requirements.
- Character-sheet manual acceptance is phone-first under C-0010; green CI does not equal manual acceptance.

## 2. Phase 4 implementation sequence

The approved consolidated sequence is recorded in `docs/handoffs/2026-08-31_NEXT_BUILD_CONSOLIDATED_IMPLEMENTATION_PACKAGE.md`.

Current status:

- Increment A — baseline verification/map: **closed**;
- Increment B — approved corrective UX backlog: **implemented through recorded checkpoints**;
- Increment C — persistent data foundation/migration: **closed**;
- Increment D — top-level navigation + PC Settings: **closed**;
- Increment E — Trasfondo: **closed**;
- Increment F — Rasgos: **closed**;
- Increment G — Conjuros source management: **closed**;
- Increment H — Conjuros spell list/details: **closed**, automated gate green;
- Increment I — Quick Magic / Conjuros shared slots: **closed**, automated gate green;
- Increment J — Notas: **closed**, automated gate green;
- Increment K — responsive/accessibility integration: **closed**, automated gate green;
- Increment L — final automated regression + owner QA candidate: **next boundary**.

## 3. Current character-sheet architecture

The Android V4 editor supports `General`, `Habilidades`, `Combate`, `Equipo`, `Trasfondo`, `Rasgos`, conditional `Conjuros`, and persistent `Notas`.

`PC Settings` owns the character-wide `Lanzador de conjuros` switch. OFF hides Quick Magic and Conjuros without deleting spellcasting data and falls back to General if Conjuros was selected.

Unsaved editor working state is recreation-safe and the existing central Save boundary persists structured domains.

## 4. Conjuros and Quick Magic

The model supports stable spellcasting sources, custom sources, `Todos` and per-source views, conceptual spells associated with one or more sources, source-specific manual `Preparado`, spell levels 0–9, CRUD/search/collapse/manual ordering, and permissive spell details.

Quick Magic and Conjuros share the same authoritative spell-slot state. Quick Magic configures slot totals/profile; Conjuros changes spent counts on that same draft state.

## 5. Notas

Notas implements the approved hybrid model: unrestricted general notes plus optional ordered titled cards containing only title + content, with add/edit/delete, long-press reordering, recreation-safe unsaved state, and persistence through existing note fields.

## 6. Responsive/accessibility state after Increment K

Code-level K corrections and confirmations include:

- supported scale choices: 80/90/100/115/130%;
- one-line horizontally scrollable top tabs;
- selected Conjuros source automatically brought into view;
- bounded/ellipsized long source labels;
- `Habilidades -> Por atributo` retains two columns on narrow layouts;
- responsive wide presentation in new domains;
- protected IME/editor state for new-domain dialogs;
- semantic drawn icon controls instead of legacy icon-only Unicode pseudo-buttons in corrected V4 surfaces;
- 48 dp reorder touch targets.

## 7. Verification and owner-device boundary

Standard automated gate:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

plus backend `npm install` / `npm run check` through `Scaffold checks`.

Automated Gates G through K are green at their recorded closure/candidate heads. Do not convert automated success into a claim of phone acceptance.

Owner-device QA still covers:

- 80/90/100/115/130% visual presentation;
- portrait/landscape ergonomics;
- top/source navigation visibility at real widths;
- real IME behavior;
- drag/reorder/touch feel;
- truncation/ellipsis quality;
- end-to-end Phase 4 usability.

## 8. Documentation-history rule

Older `ROADMAP.md` / `ARCHITECTURE.md` prose can lag later approved handoffs and actual branch state. Later specific decisions, the consolidated implementation package, dated handoffs, Git refs and CI evidence control when they conflict.

## 9. Next exact action

1. Confirm the Increment K closure head is a strict descendant of durable Increment J.
2. Fast-forward `implementation/character-data-foundation` without force.
3. Keep `main` unchanged.
4. Start **Increment L — final automated regression + owner QA candidate** from the promoted K head on a new focused branch.
5. L adds no new product scope: consolidate regression evidence, run the entire automated stack, produce the final QA APK, and write the exact owner-phone-QA checklist/acceptance boundary.
