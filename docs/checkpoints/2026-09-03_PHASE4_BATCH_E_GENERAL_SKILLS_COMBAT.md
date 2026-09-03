# Phase 4 — Batch E General + Habilidades + Combate closure

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** GREEN  
**Canonical `main`:** untouched

## Result

Batch E is complete and GREEN. The approved General/Habilidades/Combate closure work is implemented without replacing the existing core persistence model or turning the app into a rules-enforcing character builder.

## Delivered

### General

- structured class/subclass/source presentation;
- official catalog as optional convenience metadata only;
- arbitrary manual/homebrew class and subclass identity remains supported;
- non-enforcing Hit Die suggestions;
- portrait/token local references;
- Defenses;
- Senses;
- special Movement;
- existing freshness/updated-state presentation retained as authoritative character reference metadata.

### Habilidades

- Passive Perception;
- Passive Insight;
- Passive Investigation;
- custom/homebrew skills linked to an ability;
- custom proficiency/expertise and adjustment calculations;
- custom skills participate in both existing Habilidades organization modes;
- bounded d20 convenience roller for standard saves, standard skills and custom skills.

### Combate

- action type + attack modifier + damage/effect at a glance;
- quick damage, healing and temporary-HP operations;
- temporary HP absorbs damage first;
- HP synchronization prevents later structural Save from restoring stale operational HP;
- contextual manual death-save controls at 0 HP;
- Quick Access Favorite toggle for already-persisted combat entries;
- bounded `d20 + modifier` convenience roller for attack entries with an attack modifier.

## Boundaries preserved

- no class/subclass legality enforcement;
- no multiclass legality enforcement;
- no automatic 5e/5.5e/homebrew compatibility policing;
- no automatic Hit Die overwrite;
- no advantage/disadvantage, critical, damage or feature-resolution rules engine;
- no automatic death-save outcome inference;
- Quick Access for Traits, Spells and later reusable modules remains assigned to their own batches;
- `main` remains untouched.

## Sub-gates and verification

### E2a — General + Habilidades

Checkpoint: `2026-09-03_PHASE4_BATCH_E2A_GENERAL_SKILLS.md`

- controlling workflow `33810868863` — PASS;
- backend PASS;
- shared/Kotlin tests PASS;
- Android debug assembly PASS;
- Desktop build PASS;
- APK upload PASS.

### E2b — class/subclass/source identity

Checkpoint: `2026-09-03_PHASE4_BATCH_E2B_CLASS_IDENTITY.md`

- controlling workflow `33811490741` — PASS;
- backend PASS;
- shared/Kotlin tests PASS;
- Android debug assembly PASS;
- Desktop build PASS;
- APK upload PASS.

### E3 — Combate/Favorites/d20

Checkpoint: `2026-09-03_PHASE4_BATCH_E3_COMBAT_FAVORITES_D20.md`

- integration commit `bd340abdec50a4bbbeec44378e6f83d44b760f3f`;
- controlling tested head `1ae8fd235b0fa863b3efc62e091a97242f295aea`;
- controlling workflow `33812352925` — PASS;
- backend PASS;
- shared/Kotlin tests PASS;
- Android debug assembly PASS;
- Desktop build PASS;
- APK upload PASS;
- artifact `dnd-custom-aid-debug-apk`, ID `9915350879`.

## Gate conclusion

D-0046 derived-value semantics, custom-skill calculations, HP operations and the Android/Desktop surfaces all passed their controlling automated gates.

**Full Batch E gate is closed GREEN.**

Green CI is not owner device acceptance. Phone/tablet portrait/landscape, rotation, IME behavior and larger-text visual acceptance remain required for the later frozen closure candidate.

## Exact continuation

Proceed to **Batch F — Equipo + Monedas closure**.

F must implement the already-approved scope without changing manual stored order when the user chooses A–Z presentation:

- denser ordinary-equipment rows;
- much more compact currencies;
- independent Manual/A–Z modes for ordinary and special equipment;
- visible drag feedback only in Manual mode;
- search/filter without mutating stored order;
- total carried weight + attunement summary;
- containers/locations;
- consumable/ammunition quick-use metadata;
- duplicate/collapse behavior;
- tablet multi-column/master-detail behavior where useful.

Gate F requires sort/order, quantity/weight/attunement, persistence, IME and responsive verification before advancing to G1.
