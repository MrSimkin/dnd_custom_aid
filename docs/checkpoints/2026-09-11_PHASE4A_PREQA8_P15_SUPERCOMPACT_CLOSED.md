# Phase 4A — preqa.8 — P15 Supercompact closure

**Date:** 2026-09-11  
**Branch:** `implementation/phase4a-successor-cycle`  
**Status:** P15 CLOSED / READY FOR REPAIR SPEC  
**Implementation authorization:** NOT YET

## P15 — Supercompact conceptual redesign

The current Supercompact implementation is rejected as a tile/dashboard presentation. The owner approves a redesign toward a dense modern 5.5e-inspired **PC stat-block information hierarchy**, without reproducing copyrighted visual layout/artwork. Existing live-state plumbing may be reused underneath where compatible.

### Round 1 — top-level hierarchy

1. Supercompact becomes one coherent stat block rather than a dashboard of independent cards/tiles.
2. Identity header describes the PC: character name plus species/subrace and class/subclass information. Multiclass is shown naturally. Background may be quieter secondary identity information. Lifecycle status appears only when meaningful; do not waste permanent space on `Activo`.
3. Top combat summary contains high-frequency data such as CA, canonical PV current/max, temporary HP, initiative, speed using the global imperial-first + metric formatter, and proficiency bonus. HP operations reuse P1/P2/P5 canonical behavior rather than a separate `-1/+1` Supercompact system.
4. Abilities use a compact stat-block table/grammar, including score, modifier and saves. Custom Atributos participate coherently below/alongside the canonical six without masquerading as one of them.
5. Useful reference data follows compactly and only when it exists: passive perception, senses, languages, proficiencies, resistances and analogous reference facts. Do not reserve empty monster-style rows.
6. Favorites remain useful but no longer define Supercompact's structure. Zero Favorites must still produce a complete useful stat block.

### Round 2 — lower stat-block structure / play interactions

7. Combat entries are grouped by actual action economy, e.g. `ACCIONES`, `ACCIONES ADICIONALES`, `REACCIONES`, and other real categories when present. Empty sections do not render.
8. All configured combat actions appear in their natural section, not only Favorites. Favorites may influence ordering/emphasis but not visibility.
9. Traits live in a dense `RASGOS` section. Mechanically useful summaries appear first. Long prose may initially collapse/truncate and expand on demand; canonical content must remain accessible.
10. Spellcasting gets a dedicated compact `CONJUROS` section with source-aware spellcasting statistics, compact slot status, cantrips and prepared/known spell access. Multiple spellcasting sources must retain their distinctions.
11. Spell slots and bounded resources remain operational but compact. Avoid permanent `Usar / Recup.` buttons on every row; tapping/selecting a compact resource/slot can expose the appropriate spend/recover controls.
12. Supercompact is primarily a **play surface**, not a structural editing surface: `use here; define/edit elsewhere`. Live/session operations remain; formula/provenance/name/max-definition/add-delete and similar structural edits stay in the normal sheet.

### Round 3 — live state / favorites / responsive structure

13. Active combat/session state appears near the top only when relevant: concentration, conditions, exhaustion, temporary effects and analogous live state. Empty state consumes no permanent space.
14. Death saves become prominent and operational near the top only when relevant (e.g. PV 0 or existing death-save state). No permanent death-save row at normal HP.
15. Favorites do not create duplicate cards/sections. Favorite entries sort toward the beginning of their natural section and may receive a subtle proper graphical favorite indicator.
16. Long sections may collapse presentation for density but must not silently omit canonical content. Provide expansion/`Ver todos` or equivalent access to the complete list.
17. Responsive layout follows **usable dimensions**, not device labels alone. Narrow layouts stack sections; genuinely wide layouts may use multiple columns when that improves scanability. Do not force tablet = two columns.
18. Supercompact uses minimal permanent app chrome. Character identity replaces redundant `Vista supercompacta / Consulta rápida` explanation. Remove footer copy explaining shared persistence. If Table Mode is active, a concise `Modo Mesa` status may remain where useful.

## Interaction/state integration

- P1 canonical HP state applies.
- P2 damage/healing operation applies.
- P5 compact HUD/speed presentation principles apply.
- P7 canonical structured identity/provenance rules apply to any projected class/subclass/species/subrace/source information.
- P10 pseudo-icon rule applies to Favorite and all other controls.
- P13 copy simplification applies: no redundant explanatory prose.
- P14 Table Mode interaction policy applies: presentation + operational interactions remain available, structural edits remain blocked.

## Audit classification

**FULL APP AUDIT REQUIRED.**

Although P15's visual redesign is Supercompact-specific, implementation must audit every datum and live action projected into Supercompact against the canonical/shared authorities already accepted elsewhere. No second HP/resource/spell-slot/combat identity logic may be introduced. Shared formatters and state mutations must be reused or reconciled.

## Regression boundary

Repair verification must cover at minimum:

- complete useful Supercompact with zero Favorites;
- identity including subclass/multiclass/subrace where present;
- canonical HP/temp HP and P2 damage/healing behavior;
- global speed formatting;
- ability/modifier/save table including custom attributes;
- conditional reference rows;
- all combat entries grouped by real action economy;
- dense traits with access to full content;
- source-aware spellcasting and compact live slot operations;
- compact resource/session operations;
- relevant concentration/condition/exhaustion/death-save state;
- Favorites reorder/emphasis without duplicate content;
- no structural editing affordances that violate the play-surface rule;
- narrow and wide responsive layouts based on usable dimensions;
- minimal redundant chrome/copy;
- consistency with Table Mode.

**Status:** `CLOSED / READY FOR REPAIR SPEC`.
