# Latest project checkpoint

**Updated:** 2026-09-09  
**Canonical branch:** `main`  
**Active continuation branch:** `implementation/phase4a-successor-cycle`  
**Repository state:** Increment F implemented and automated-green, but owner-device retest failed; F repair required before G  
**Phase:** Phase 4A successor repair/refinement cycle  
**Release status:** debug / development; NOT owner-accepted and NOT release-ready  
**Primary owner phone:** Redmi Note 11 Pro 5G  
**Tablet acceptance:** pending; tablet/wide redesign remains required  
**DM implementation:** blocked until Phase 4A is later accepted and explicitly closed

## Increment status

1. A — schema/domain/storage foundation: **COMPLETE / GREEN**;
2. B — shared UX/responsive primitives: **COMPLETE / GREEN**; drag feel still requires owner-device acceptance;
3. C — character-first navigation + PC Settings + General/Habilidades: **COMPLETE / GREEN**;
4. D — Combat + Dice: **COMPLETE / GREEN**;
5. E — Gestión + Markers + Resources + cross-domain rest/conditions: **COMPLETE / GREEN**;
6. **F — Conjuros compact source-context redesign: AUTOMATED GREEN / OWNER RETEST FAILED / REPAIR NOW**;
7. G — Equipo/Rasgos/conditional modules/Notas/Trasfondo: pending and blocked on F repair;
8. H — full-screen Application Settings/live previews/themes: pending;
9. I — separate tablet portrait/landscape redesign: pending.

## Read next

1. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_F_CONJUROS_COMPACT_SOURCE_CONTEXT.md` — F implementation evidence plus failed owner-device retest and exact repair scope;
2. `docs/checkpoints/2026-09-09_NIGHT_CLOSE_AFTER_INCREMENT_E.md` — prior continuity package;
3. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_E_MANAGEMENT_RECOVERY.md`;
4. `docs/checkpoints/2026-09-08_PHASE4A_RECONCILED_SUCCESSOR_IMPLEMENTATION_PLAN.md` — controlling A–I plan;
5. `docs/PROJECT_STATE.md` — compact broader project-state snapshot;
6. `docs/BRANCH_STATUS.md` — branch interpretation and cleanup status.

## Latest automated product boundary — Increment F

Active F source commit:

`0e25fb0a84c2be63a16fee709bd7e96d7469373b`

Authoritative validation commit:

`4ba248f7749a062ac40e1e0c46c0687f4caccbbf`

Final integrated workflow:

`34376169597` — **SUCCESS**

Verified together:

- backend/type-check: PASS;
- shared/Kotlin tests: PASS;
- Android debug compilation/assembly: PASS;
- desktop compilation/build: PASS;
- Android debug APK upload: PASS.

Artifact:

- ID `10114037181`;
- name `dnd-custom-aid-debug-apk`;
- ZIP digest `sha256:a0aa9ddfeeaa74bffa17ecb15dd1d3e0880c7238159c9058bfc80beaeec5041d`.

This automated-green boundary is **not owner acceptance**.

## First Increment F owner-device retest result

The Redmi Note 11 Pro 5G physical retest found blocking issues:

- filters: acceptable;
- drag/reorder: severe failure, including skipping two cards;
- spell cards wrongly depend on the visible three-line drag handle instead of long-press/drag from the card itself;
- favorite/star control is visually unacceptable;
- portrait collection controls are awkwardly compressed;
- spell cards still use unnecessary vertical action tiers;
- spell editor still leaves naturally compatible short fields on separate rows;
- keyboard still obscures editor content despite the shared IME-safe dialog.

Code inspection after the retest identified a concrete drag defect: `SpellRowG2` still uses a private direct gesture implementation with a hard-coded `66.dp` step and a multi-step `while` loop, rather than the shared stale-callback-safe `characterLongPressDragV4` primitive. The private pointer coroutine can also retain stale reorder callbacks after live recomposition.

## Exact next action — F repair pass

Do **not** begin G.

Repair F on `implementation/phase4a-successor-cycle`:

1. whole-card long-press drag; remove spell-card drag handle;
2. use shared stale-safe drag primitive;
3. measured card geometry + no accidental multi-step pointer jumps;
4. compact card action layout;
5. proper stable favorite icon instead of Unicode star text;
6. portrait toolbar responsiveness without adding another permanent row;
7. spell-editor row-efficiency audit and consolidation of short fields;
8. shared IME-safe editor repair so keyboard never hides the focused usable editor area/actions;
9. focused compile/tests, full integrated gate, then a new targeted Redmi portrait/landscape retest APK.

## Protected owner directions

- one datum / one canonical state across tabs;
- phone landscape remains a phone interaction model;
- tablet/wide UI requires a separate redesign;
- app-wide compactness means naturally compatible controls share rows rather than stacking by default;
- whole-card drag is the intended interaction where safe; a visible handle is not the required initiation mechanism;
- do not shrink required touch targets merely to make layouts look compact;
- contextual help remains one canonical explanation rendered as `Siempre visible`, `ⓘ / tooltip`, or `Oculto`;
- generic `Fuente` schema leakage must not be reintroduced;
- use `Raza`, never `Especie/raza`;
- use `Electrum`, never `Electro`;
- no DM feature implementation before explicit Phase 4A closure.
