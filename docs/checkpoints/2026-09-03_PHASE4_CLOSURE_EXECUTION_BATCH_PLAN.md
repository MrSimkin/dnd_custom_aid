# Phase 4 Character Foundation Closure — execution batch plan

**Date:** 2026-09-03  
**Status:** Approved-scope execution plan  
**Branch:** `implementation/phase4-character-closure`  
**Canonical `main`:** untouched

## Purpose

The owner explicitly confirmed that **all Phase 4 character changes, improvements, new features, bug fixes, phone work and tablet work must be implemented and accepted before any DM-feature implementation begins**.

The D-0047 closure package is large. Work therefore proceeds in small recoverable batches. Every batch must leave a Git checkpoint before the next batch begins.

This file decomposes the higher-level A–J map in `2026-09-03_PHASE4_CLOSURE_IMPLEMENTATION_MAP.md`; it does not change D-0047 scope.

## Global execution rule

- No DM feature implementation while this plan is open.
- `main` remains untouched until final owner acceptance and explicit merge approval.
- Each batch is independently checkpointed and verified at the proportionate level.
- A failed gate is repaired before continuing to a dependent batch.
- Tablet responsiveness is implemented throughout, not postponed until the final batch.
- User-facing application text remains Spanish; technical repository work remains English.

## Batch 0 — repository housekeeping

Goal: remove continuity contradictions that could misdirect later agents.

- update README current implementation/status prose;
- update AGENTS current phase and debug-signing wording;
- update MANIFEST implemented areas;
- update ROADMAP Phase 4 closure boundary;
- update ARCHITECTURE current implementation consequence;
- update TESTING current Phase 4 phone+tablet acceptance boundary;
- record the master `docs/DECISIONS.md` lag as a known index-maintenance item if it cannot be safely reconciled in the same atomic change;
- preserve historical checkpoints rather than rewriting history.

Gate: documentation is internally consistent with `docs/PROJECT_STATE.md`, D-0047 and the implementation map.

## Batch A1 — official class/subclass catalog reconciliation

- remove stale `upcoming/early-access` status from released/current Arcana Unleashed catalog entries;
- verify all audited current-source keys are present;
- verify representative legacy source/version variants remain distinguishable;
- verify Artificer and companion/technique/metamagic/pact/form triggers;
- add focused catalog tests.

Gate A1: shared tests pass and catalog assertions cover representative current + legacy + custom/open behavior.

## Batch A2 — schema 7 durable domain model

Add the remaining D-0047 durable state through additive schema 7 rather than rewriting schema 6:

- conditions/exhaustion;
- defenses;
- movement/senses;
- concentration;
- recovery/rest metadata;
- inventory consumable/container metadata;
- portrait/token reference metadata;
- reconciliation checkpoints;
- XP/milestone state;
- custom skills;
- temporary effects;
- module overrides/visibility;
- Table mode;
- haptic preference;
- any non-derived Supercompact configuration;
- Favorite/Quick Access consistency.

Gate A2: schema creation + migrations + holistic repository round-trip + shared tests pass.

## Batch B1 — global editor/IME/action foundation

- one reusable IME-safe editor pattern;
- consistent Save/Cancel/Create/Delete/Duplicate semantics;
- icon/touch/accessibility vocabulary;
- inline validation;
- named destructive confirmation;
- useful empty states.

Gate B1: Android assemble + targeted interaction/state tests; no known critical editor actions obscured by IME in implementation review.

## Batch B2 — ordering/search/filter/drag/context foundation

- Manual/A–Z presentation helpers that never destroy manual order;
- reusable search/filter toolbar state;
- real drag feedback/reflow;
- configurable haptic hook;
- D15 dirty/saved state;
- I20 unsaved-leave guard;
- D16 context-preservation primitives.

Gate B2: helper/state tests + Android assemble.

## Batch C — PC Settings consolidation

- lifecycle status moves here;
- spellcaster hide-not-delete retained;
- module suggestions + manual overrides;
- haptic setting;
- Supercompact entry/configuration;
- Table mode;
- XP/Milestone mode;
- Application Settings entry;
- phone/tablet adaptive layout.

Gate C: persistence/recreation + Back hierarchy + hide/show-no-delete tests.

## Batch D — Gestión / character maintenance

- conditions/exhaustion;
- concentration;
- resources;
- short/long rest assistant with preview + selective apply;
- temporary effects;
- reconciliation checkpoints;
- inspiration/death-save operational placement.

Gate D: resource/rest state tests + persistence + phone/tablet responsive smoke.

## Batch E — General + Habilidades + Combate

- class/subclass/source presentation;
- hit-die suggestions without enforcement;
- freshness;
- portrait/token;
- defenses/senses/movement;
- Passive Insight/Investigation;
- custom skills in both existing organization modes;
- combat type/damage-at-glance;
- quick HP operations;
- contextual death saves;
- Favorites;
- simple dice roller.

Gate E: D-0046 regression + custom-skill + HP-operation tests + Android assemble.

## Batch F — Equipo + Monedas closure

- dense ordinary rows;
- compact currencies;
- independent Manual/A–Z ordinary/special sorting;
- drag feedback;
- search/filter;
- total weight/attunement;
- containers/locations;
- consumables/ammunition;
- duplicate/collapse;
- tablet multi-column/master-detail.

Gate F: sort/order, quantity/weight/attunement, persistence, IME and responsive checks.

## Batch G1 — Rasgos closure

- source/type grouping/filter/search;
- use meter;
- Favorites;
- duplicate;
- real reorder;
- tablet grouping.

## Batch G2 — Conjuros closure

- Manual/A–Z inside level/source rules;
- filters;
- V/S/M/concentration/ritual/prepared badges;
- sticky/collapsible levels;
- Favorites/duplicate/reorder;
- shared-slot regression;
- tablet master-detail.

## Batch G3 — Notas + Trasfondo closure

- note preview/reorder/duplicate/context preservation;
- long-note scrolling;
- responsive columns;
- compact background identity/personality;
- collapsible long story;
- Raza/Religión regression.

Gate G1–G3: existing domain persistence + IME + order regressions pass.

## Batch H1 — Artífice + Formas

Implement conditional reusable module UI over shared state.

## Batch H2 — Técnicas + Metamagia + Pactos

Implement conditional reusable module UI over shared state.

## Batch H3 — Compañeros + module-union integration

- companion editor/reference;
- multiclass union;
- custom manual overrides;
- hide-not-delete;
- source/provenance + Favorites.

Gate H: representative official trigger tests + multiclass/custom override persistence.

## Batch I1 — adaptive shell

- phone/tablet available-width behavior;
- navigation rail on suitable wide layouts;
- sticky compact header;
- master-detail for list-heavy surfaces;
- D16 holistic context preservation.

## Batch I2 — Supercompact + Table mode

- Supercompact operational projection from authoritative values + Favorites;
- responsive density/columns;
- Table/read-only structural-edit suppression while permitted live controls remain usable.

Gate I: rotate/recreate/reopen + last-tab + phone/tablet portrait/landscape + larger-text checks.

## Batch J — own-format backup/import + reconciliation completion

- versioned local backup format;
- safe import/restore;
- no silent overwrite/ID collision;
- reconciliation integration.

Gate J: richly populated export/import round trip and malformed-input safety.

## Integration batch K — closure candidate stabilization

- full migration regression from owner APK lineage;
- all shared tests;
- Android debug assemble;
- Desktop build;
- backend type-check;
- focused cross-tab integration defects only;
- no new unrelated scope.

## Candidate batch L — final phone + tablet APK

Freeze one candidate and record exact:

- commit;
- CI workflow run;
- artifact name/ID;
- artifact ZIP hash;
- extracted APK hash.

No silent code changes after owner QA starts.

## Owner QA batch M

Required matrix:

1. phone portrait;
2. phone landscape;
3. tablet portrait;
4. tablet landscape;
5. representative larger text scale.

Test migration, IME/actions, Back, D16 context, sorting/search/filter/drag, Gestión, all existing tabs, conditional modules, Supercompact, Table mode, backup round trip and resilience.

Blocking findings create focused repair batches with new candidate identity. Non-blocking new ideas normally go to later backlog.

## Phase 4 exit gate

Phase 4 closes only after:

- D-0047 implementation is complete;
- automated gates are green;
- phone + tablet owner QA is accepted;
- continuity/governance housekeeping is complete;
- exact accepted APK identity is recorded;
- owner explicitly approves merge to `main`.

**Only after that exit gate may DM-feature implementation begin.**

## Exact next action

Complete Batch 0 housekeeping, then Batch A1 catalog reconciliation. Batch A2 schema 7 follows only after A1 has its own durable checkpoint.