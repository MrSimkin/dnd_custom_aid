# Phase 4 — Batch I2b Table mode completion

**Date:** 2026-09-04  
**Status:** GREEN — Batch I complete  
**Durable branch:** `implementation/phase4-character-closure`  
**Safety branch:** `tmp/phase4-i2b-table-mode`  
**Canonical `main`:** untouched

## Scope

I2b completes the Table/read-only half of Batch I2 and therefore closes Batch I. It implements Table mode as an explicit write-policy over the existing character editor and modules. It does not create a second character-sheet model, does not introduce a schema migration and does not use a blanket pointer-blocking overlay.

The controlling requirement is to reduce accidental structural edits during table use while preserving intentional live/session operations and presentation-only controls.

## Shared policy

Added `CharacterTableModePolicy.kt` and focused shared tests.

The policy distinguishes:

- presentation interactions — always allowed;
- operational interactions — allowed in Table mode;
- structural interactions — blocked while Table mode is enabled.

This shared contract keeps Table-mode behavior explicit and testable instead of scattering unrelated ad-hoc rules through the UI.

## Editor and transition safety

`CharacterEditorV4` now establishes one `structuralEditingEnabled` boundary and uses guarded structural callbacks as the final write barrier.

Table mode cannot be enabled while an older structural draft is already dirty. This prevents entering read-only mode with pending structural edits that could later be persisted accidentally.

While Table mode is active:

- ordinary structural Save is disabled;
- structural draft callbacks are ignored at the editor boundary;
- PC Settings communicates the dirty-draft requirement before Table mode can be enabled;
- the character header visibly reports `Modo Mesa · edición estructural bloqueada`.

## Mixed surfaces

The existing structural/operational callback seams were preserved rather than rewritten.

### Combat

Structural combat-entry configuration is locked. Live HP/session sheet operations remain available through the existing operational sheet bridge.

### Traits

Add/edit/delete/duplicate/reorder/Favorite configuration is locked. Existing use-meter operations remain live.

### Equipment and currencies

Structural inventory/currency definition and collection edits are locked. Consumable/ammunition quick-use operations remain live.

### Spells

Spell/source structural editing and Favorite configuration are locked. Persisted spell-slot spend/recover remains live.

### Gestión

Gestión remains primarily operational. Inspiration, death saves, conditions/exhaustion, concentration, temporary/session effects, resource spend/recover and Rest operations remain available. Resource definition add/edit/delete is locked.

## Structural-only surfaces

Notes and Background now render explicit read-only affordances rather than merely accepting interactions whose writes would be rejected later.

The six conditional reusable module families also respect Table mode:

- Artífice;
- Formas;
- Técnicas;
- Metamagia;
- Pactos;
- Compañeros.

Their add/edit/delete/duplicate/reorder/Favorite structural affordances are locked while search, filters, Manual/A–Z presentation and other browsing behavior remain usable.

Companion values in this module remain durable character reference data; future DM encounter state remains a separate authority and was not implemented here.

## Verification history

### I2b1 mixed-surface gate

- clean-tree commit: `e978a26180301f87727edd48fe34f0b27e945a05`;
- workflow `33836333221` — PASS across backend, shared/Kotlin tests, Android debug assemble, Desktop build and APK upload;
- artifact `9923448961`.

### I2b2a transition/read-only gate

- clean-tree commit: `c5d4b7333dbc0e6f8147a5c1fefa000bbcbf0f28`;
- workflow `33836793411` — PASS across backend, shared/Kotlin tests, Android debug assemble, Desktop build and APK upload.

### Final controlling Batch I gate

- exact clean-tree commit: `a36a9b36f56b40088c9cb42b55b347a5ecf4c05b`;
- tree: `75278c4a7569722f0d54a141fa257d710c62f35e`;
- workflow `33837303412` — PASS;
- backend type-check: PASS;
- shared/Kotlin tests: PASS;
- Android debug assemble: PASS;
- Desktop build: PASS;
- APK upload: PASS;
- artifact: `9923757180` (`dnd-custom-aid-debug-apk`);
- artifact ZIP digest: `sha256:d1b577750a14a023d5cca2c5cd7581a37321370ed9b1f68eb501cdbe171f065c`.

The final I2b durable-baseline diff from the I2a continuity head `4617c9e1a539888cb8c96bdfa46f70392af21cb9` contains exactly 16 intended files: 14 Android character surfaces plus the shared Table-mode policy and its shared tests. Temporary workflows, patch scripts and CI marker files are absent from the final product tree.

## Batch I closure

I1 adaptive shell, I2a Supercompact and I2b Table mode are all GREEN. Batch I is therefore technically complete.

This does not close Phase 4 and does not authorize DM implementation. The remaining approved sequence is J → K → L → M, followed by owner acceptance and explicit merge/closure approval.

## Exact continuation

Resume **Batch J — own-format backup/import + reconciliation completion** from the durable branch after this checkpoint.

Implement a versioned local backup/import format, safe import with no silent overwrite or identifier collision, richly populated round-trip coverage and malformed-input safety. Reuse the existing durable character/domain authorities rather than inventing a parallel model. Keep `main` untouched and do not begin DM work early.
