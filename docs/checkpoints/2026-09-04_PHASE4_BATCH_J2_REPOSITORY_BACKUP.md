# Phase 4 — Batch J2 repository backup checkpoint

**Checkpoint date:** 2026-09-04  
**Phase:** Phase 4 Character Foundation Closure  
**Batch:** J — own-format backup/import  
**Sub-batch:** J2 — repository export/import  
**Active product branch:** `tmp/phase4-j-backup-import`  
**J2 product commit:** `86befdc15e6e1c08a73b46714c1b75c860f6fee1`  
**J2 product tree:** `e9fcc739a6c38b97b189584faf5ebec2f27809b3`  
**Status:** GREEN

## 1. Resume authority

This checkpoint supersedes the J2-paused continuation in `docs/checkpoints/2026-09-04_PHASE4_BATCH_J_PAUSE_HANDOFF.md`.

The original pause diagnosis was correct: the failed gate was caused by stale test-fixture constructor arguments, not by demonstrated production-algorithm failure.

Do not restart J from the durable closure branch. Continue the existing J safety line.

## 2. Repair performed

Only the demonstrated fixture drift was repaired before rerunning J2:

- `CharacterInventoryItem` test construction now uses the current `special`, `description`, `location` and `attuned` fields rather than stale `category`, `pinned`, `container` and `visible` arguments;
- `CharacterSpell` test construction now uses `rangeText` rather than stale `range`;
- J2 production repository semantics were not changed for this repair.

## 3. Focused guarded gate — GREEN

Guarded workflow:

- workflow run `33877700478` — PASS;
- `Apply guarded J2 patch` — PASS;
- focused `gradle :shared:desktopTest --tests '*CharacterBackupRepositoryTest' --stacktrace` — PASS;
- self-clean/commit step — PASS.

The guarded workflow committed the intended product files and removed both staging helpers:

- `.github/j2_repository_patch.py` — absent after the product commit;
- `.github/workflows/tmp-j2-apply.yml` — absent after the product commit.

## 4. Implemented J2 semantics

`CharacterBackupRepository` now bridges the two existing authoritative character persistence aggregates rather than introducing a second character model.

### Export

`exportCharacter(...)`:

1. requires an existing local character;
2. reads the authoritative `CharacterSheet`;
3. reads the authoritative `CharacterClosureState`;
4. creates a validated `CharacterBackupDocument`.

### Import

`importAsCopy(...)`:

1. validates the backup before writing;
2. creates a fresh destination character in the selected destination campaign;
3. prepares a fully remapped restore-as-copy plan;
4. saves the remapped core `CharacterSheet`;
5. appends an automatic `Importado desde respaldo` reconciliation checkpoint;
6. saves the remapped `CharacterClosureState`;
7. performs the entire restore inside one outer SQLDelight database transaction.

Import therefore never targets an existing local character and never silently overwrites one.

## 5. Focused behavior proven

The J2 repository tests now execute and pass for:

- export -> JSON -> decode -> repository import round trip;
- fresh destination character identity;
- fresh nested identities;
- remapped class/spell/source/resource/inventory/Quick Access references;
- source character preservation;
- automatic reconciliation checkpoint creation;
- importing the same backup twice produces two independent local copies without identity collision;
- a stricter repository validation failure after placeholder creation rolls back the placeholder and partial destination state.

## 6. Exact clean-tree full gate — GREEN

An isolated exact-tree gate branch was created without changing the product tree.

Controlling full workflow:

- exact product commit under test: `86befdc15e6e1c08a73b46714c1b75c860f6fee1`;
- exact product tree: `e9fcc739a6c38b97b189584faf5ebec2f27809b3`;
- workflow run `33877925311` — PASS;
- backend install/type-check — PASS;
- shared/Kotlin/SQLDelight tests — PASS;
- Android debug assemble — PASS;
- Desktop build — PASS;
- APK upload — PASS.

Artifact:

- artifact ID `9938727963`;
- name `dnd-custom-aid-debug-apk`;
- digest `sha256:88d15ab5f2fa7a0b66c3c52a5c1f153030b8e0d9843d9acc1a8f7761bcddc0d8`.

This artifact is integration evidence only, not the future frozen Batch L owner-QA candidate.

## 7. J1 robustness follow-up discovered during review

Before Batch J is closed, add a small regression around backup header type safety.

Current J1 decoding safely handles malformed JSON and ordinary wrong format/version values, but header access should also return a controlled backup failure when syntactically valid JSON supplies a non-primitive `format` or `version` value rather than allowing a JSON-type accessor exception to escape.

This is a small robustness correction within already-approved malformed-input safety; it is not an architecture or product change.

## 8. Exact next action — J3

Proceed to **J3 Android local-file UX** from the existing `tmp/phase4-j-backup-import` line.

Required placement remains:

- **Import** at campaign/character-list context because v1 import always restores a new copy into the selected campaign;
- **Export** as a character-specific backup action;
- use Android system document picker/local document APIs rather than inventing app-managed external storage;
- import messaging must clearly state that a new local character copy is created and no existing character is overwritten;
- successful import must expose the already-persisted reconciliation result rather than create a parallel history model;
- errors must be reported safely in Spanish;
- no third-party parser, cloud backup, DM work or second character model.

After J3 implementation, run focused Android/state verification as appropriate, then the full exact-clean-tree J gate. Also close the small J1 header-type robustness item before declaring Batch J GREEN.
