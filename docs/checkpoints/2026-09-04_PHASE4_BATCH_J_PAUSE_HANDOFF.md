# Phase 4 — Batch J pause / handoff checkpoint

**Checkpoint date:** 2026-09-04  
**Intended resume:** 2026-09-05  
**Owner:** project owner  
**Canonical `main`:** untouched at `471c5570669a6007bea9796d8a2c25536b10be21`  
**Phase 4 durable branch before this documentation checkpoint:** `implementation/phase4-character-closure` at `57269e91a3d9f9261c145ea4dfd93fc70617953e`  
**Active working/safety branch:** `tmp/phase4-j-backup-import`  
**Active working branch head at pause:** `4416eeacb2f2dec9f302d96b62a01506a0722a98`  
**Current phase:** Phase 4 Character Foundation Closure  
**Execution position:** Batch I complete; Batch J active; J1 GREEN; J2 blocked at focused test-compilation gate; no J2 product commit accepted

## 1. First rule when resuming

Do **not** restart Batch J from the durable branch and do **not** discard the existing J safety branch.

Resume from:

`tmp/phase4-j-backup-import` @ `4416eeacb2f2dec9f302d96b62a01506a0722a98`

That branch contains the already-validated J1 product work plus the intentionally retained J2 staging helpers after a failed guarded gate.

The durable Phase 4 branch intentionally does **not** yet contain J1/J2 product code. J must be completed and clean-tree gated before promotion.

## 2. Batch J scope remains unchanged

Controlling owner-approved requirement: F11 / Batch J in D-0047.

Batch J owns:

- the application's own versioned local character backup format;
- export from the existing authoritative character state;
- safe import/restore as a **new local copy**;
- no silent overwrite;
- no identifier collision;
- malformed/unsupported input safety;
- reconciliation integration;
- richly populated round-trip coverage;
- Android local-file UX after repository semantics are proven.

Non-goals remain unchanged:

- no third-party character parser;
- no cloud backup implication;
- no DM feature work;
- no second character model;
- no legality/rules-enforcement engine.

## 3. J1 — versioned backup contract — GREEN

J1 is complete on the J safety line.

### Product/result

Own-format v1 contract:

- `format = "dnd-custom-aid.character-backup"`;
- `version = 1`;
- payload authority is the existing `CharacterSheet` plus `CharacterClosureState`;
- JSON codec rejects empty input, malformed JSON, wrong format, unsupported version and invalid payloads;
- import planning uses restore-as-copy semantics;
- character identity, every child stable identity and every supported internal reference are remapped;
- the source character/campaign identity is retained only as import provenance, never reused as destination storage identity.

### J1 relevant source files

- `shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterBackup.kt`
- `shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterSheet.kt`
- `shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterClosureDomain.kt`
- `shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterClassCatalog.kt`
- `shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterBackupTest.kt`
- `shared/build.gradle.kts`
- `gradle/libs.versions.toml`

### J1 identity and verification

- J1 product commit: `2d377d76d717ed47a302390009526934db4ca290`;
- exact clean J1 gate commit: `5a734feb66f1341bed4688a9a8122247f245f8cb`;
- exact clean J1 tree: `e384f0f7f04991c0db7b7b35ace5aaf965135e76`;
- focused J1 workflow: `33838097663` — PASS;
- exact-tree full retry workflow: `33838622420` — PASS across backend, shared/Kotlin tests, Android debug assemble, Desktop build and APK upload;
- retry APK artifact: `9924202949`;
- retry artifact digest: `sha256:95ac091f02523c9b3480f548d0597979f7afc6b5af2653daa65a73e602c62240`.

Historical note: workflow `33838221269` had already passed the full Kotlin/Android/Desktop side and produced artifact `9924047062`; its backend runner stalled in `npm install`. The exact-tree retry `33838622420` was therefore created from the same exact commit and subsequently passed both backend and Kotlin jobs. J1 is accepted GREEN on exact commit/tree identity.

## 4. J2 — repository export/import — current pause state

### Intended J2 design already staged

The J2 helper currently proposes:

- `CharacterBackupRepository` as a repository-level bridge over the existing `CharacterRepository` and `CharacterClosureRepository`;
- `exportCharacter(...)` joins those two authorities into `CharacterBackupDocument`;
- `importAsCopy(...)` validates first, creates a fresh destination character, remaps all identities/references, saves core then closure state inside one outer database transaction, and returns `CharacterBackupImportResult`;
- an automatic reconciliation checkpoint is appended with label `Importado desde respaldo`;
- the checkpoint references the newly persisted character timestamp;
- the outer transaction is intended to guarantee rollback if a stricter repository invariant rejects the imported candidate after the placeholder character has been created.

The intended product file is:

- `shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterBackupRepository.kt`

The intended focused test file is:

- `shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterBackupRepositoryTest.kt`

### Guarded J2 gate result

Workflow:

- `33838749228` — **FAIL**, intentionally before commit.

Safety result:

- the guarded patch applied inside the runner;
- main/shared production compilation reached completion;
- `:shared:compileTestKotlinDesktop` failed while compiling the new J2 test fixture;
- the product commit/self-clean step was skipped;
- therefore **no J2 product code was committed**;
- the branch still contains the J2 staging helpers so the demonstrated defect can be repaired in place.

### Exact demonstrated failure

The new test fixture used constructor fields that no longer match the authoritative domain model.

In `CharacterBackupRepositoryTest.kt` generated by `.github/j2_repository_patch.py`:

1. `CharacterInventoryItem(...)` incorrectly uses stale/nonexistent parameters:
   - `category`
   - `pinned`
   - `container`
   - `visible`

   Current authoritative constructor requires:
   - `special`
   - `description`
   - `location`
   - `attuned`

   while retaining `id`, `name`, `quantity`, `weightLb`, `equipped`, `notes`, `sortOrder`.

2. `CharacterSpell(...)` incorrectly uses `range = "30 pies"`.

   Current authoritative constructor requires:
   - `rangeText = "30 pies"`.

Compiler evidence from workflow `33838749228`:

- `CharacterBackupRepositoryTest.kt:157:25 No parameter with name 'category' found.`
- `...:159:25 No parameter with name 'pinned' found.`
- `...:161:25 No parameter with name 'container' found.`
- `...:162:25 No parameter with name 'visible' found.`
- missing required `special`, `description`, `location`, `attuned`;
- `...:192:25 No parameter with name 'range' found.`
- missing required `rangeText`.

This is currently a **test-fixture constructor drift failure**, not evidence that the J2 repository algorithm itself is wrong. However, J2 is **not GREEN** because the focused tests never executed.

## 5. Files intentionally present on the J safety branch at pause

Compared with durable Phase 4 baseline `57269e91...`, the current J branch includes the validated J1 product delta plus exactly these J2 staging helpers:

- `.github/j2_repository_patch.py`
- `.github/workflows/tmp-j2-apply.yml`

Do not mistake those helpers for product code. They are intentionally retained because the guarded gate failed before the self-clean/commit step.

The current branch head is:

`4416eeacb2f2dec9f302d96b62a01506a0722a98`

Its tree includes J1 product files and J2 helpers, but **not** the generated J2 repository/test files because those existed only in the failed runner workspace.

## 6. Exact first actions for 2026-09-05

1. Read this checkpoint first.
2. Verify `tmp/phase4-j-backup-import` still points to `4416eeacb2f2dec9f302d96b62a01506a0722a98` or understand any owner-authored change before proceeding.
3. Open `.github/j2_repository_patch.py`.
4. Repair only the demonstrated test-fixture constructor drift:
   - replace the stale `CharacterInventoryItem` fixture fields with current `special`, `description`, `location`, `attuned` semantics;
   - replace `CharacterSpell(range = ...)` with `rangeText = ...`.
5. Do not alter the J2 production algorithm unless the next compile/test demonstrates a production defect.
6. Re-run the same guarded focused J2 workflow/test:
   - `gradle :shared:desktopTest --tests '*CharacterBackupRepositoryTest' --stacktrace`.
7. If focused J2 is GREEN, allow the helper to self-remove and commit the intended J2 product files.
8. Audit the net delta and verify no staging helper survives.
9. Run exact-clean-tree full CI: backend + shared/Kotlin tests + Android debug + Desktop + APK.
10. Only after J2 is GREEN proceed to J3 Android local-file UX.

## 7. Planned J3 after J2 becomes GREEN

Current architectural placement discovered during J audit:

- **Import** belongs at campaign/character-list context, because import semantics are always restore-as-new-copy into the selected campaign.
- **Export** belongs as a character-specific backup action.
- Android should use the system document picker/local file APIs rather than inventing app-managed external storage.
- Import UX must clearly communicate that a new local character copy is created; no overwrite flow is needed for v1.
- Existing reconciliation UI/domain should expose the automatic import checkpoint; do not create a parallel import-history model.

Relevant Android seams already identified:

- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/MainActivity.kt`
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterUi.kt`
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterManagementTabV4.kt`

## 8. Batch J exit gate before K

Do not start Batch K until all of J is complete and clean-tree gated.

J exit should prove at minimum:

- versioned own-format export;
- decode/validation safety;
- restore-as-copy with fresh character identity;
- fresh nested identities;
- all internal references remapped correctly;
- source character remains unchanged;
- repeated import produces independent copies without collision;
- malformed/unsupported input creates no character;
- persistence failure rolls back all partial destination rows;
- automatic reconciliation checkpoint exists after successful import;
- Android export/import file flow is usable and reports errors safely;
- full normal CI is GREEN on the exact clean product tree.

## 9. Authoritative resume reading order

For the next session use this order:

1. **This file:** `docs/checkpoints/2026-09-04_PHASE4_BATCH_J_PAUSE_HANDOFF.md`
2. `docs/PROJECT_STATE.md`
3. `docs/checkpoints/2026-09-04_PHASE4_BATCH_I2B_TABLE_MODE.md`
4. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md`
5. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_IMPLEMENTATION_MAP.md`
6. `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md`
7. `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md`
8. `docs/WORKFLOW.md`
9. `docs/TESTING.md`
10. `docs/QA_CHECKLIST.md`

The older execution-plan file still contains an historical `Exact next action` from the start of the closure sequence. Do **not** use that stale line as current operational truth. `docs/PROJECT_STATE.md` plus this checkpoint are the current continuity authority.

## 10. Other relevant repository documents and their role

- `README.md` — project-level orientation/current implemented areas.
- `AGENTS.md` — repository execution/governance instructions.
- `MANIFEST.md` — repository component inventory.
- `docs/PRODUCT.md` — product intent and boundaries.
- `docs/ARCHITECTURE.md` — architecture/topology consequences.
- `docs/CONVENTIONS.md` — coding/documentation conventions.
- `docs/DECISIONS.md` — consolidated decision index; known governance debt remains that it ends at D-0043 while detailed D-0044–D-0047 files exist.
- `docs/ROADMAP.md` — phase sequencing and DM boundary.
- `docs/CHARACTER_SHEET_UX.md` — character UX baseline.

Do not rewrite historical checkpoint evidence simply to make it read like current state; use this handoff + `PROJECT_STATE.md` to supersede stale historical continuation lines.

## 11. Protected baseline / do-not-regress reminder

All completed A–I behavior remains protected, including:

- schema/repository migrations;
- General / Habilidades / Combate / Gestión;
- Equipo / Monedas;
- Trasfondo including Raza and Religión/Fe;
- Rasgos;
- Conjuros with sources/prepared/shared slots;
- Notas;
- all six conditional modules;
- PC Settings;
- Supercompact;
- Table mode;
- adaptive navigation and last-tab restoration;
- D-0046 derived values/adjustments;
- owner-approved phone/tablet behavior already delivered.

Keep `main` untouched. Do not begin DM implementation. Do not promote partially tested J work.

## 12. One-line continuation

**Tomorrow: resume `tmp/phase4-j-backup-import` at `4416eeac...`, repair only the J2 test fixture's stale `CharacterInventoryItem` / `CharacterSpell` constructor arguments, rerun the guarded `CharacterBackupRepositoryTest`, and proceed only if that gate becomes GREEN.**
