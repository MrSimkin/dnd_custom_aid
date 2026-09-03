# Project State

**Last verified:** 2026-09-03 UTC / 2026-09-03 owner local time  
**Canonical branch:** `main`  
**Phase 4 durable working branch:** `implementation/character-data-foundation`  
**Focused proposal/prototype branch:** `implementation/phase4-character-closure`  
**Current phase:** Phase 4 character-closure design review — coding paused pending owner scope approval  
**Open review:** owner review of closure features/design/improvements and class/subclass conditional-module design  
**Status:** The previous correction APK remains historical test evidence, but the owner has already identified further issues and intentionally stopped a new QA cycle. A focused closure/prototype branch was created, but implementation was stopped when the owner required product/design alternatives first. The prototype head `89aad12a094476c7b6798f6f0626bf978a5d0831` is green in automated CI (run `33779104922`) but is **not approved product scope and does not define a new owner-QA APK**. `main` remains untouched.

## 0. Current continuity checkpoint

Primary current review document:

`docs/checkpoints/2026-09-03_PHASE4_CLOSURE_PROPOSAL_REVIEW.md`

This checkpoint explicitly separates:

- owner-originated requirements that remain baseline but are not counted as agent proposals;
- agent-originated new feature proposals;
- agent-originated design proposals;
- agent-originated improvements to existing surfaces;
- recommendation levels;
- the rule that coding must not resume until owner review is recorded.

Historical supporting records remain valid:

- deferred correction-QA checkpoint: `docs/handoffs/2026-09-03_PHASE4_QA_DEFERRED_CHECKPOINT.md`;
- correction-candidate implementation handoff: `docs/handoffs/2026-09-03_PHASE4_CORRECTION_CANDIDATE.md`;
- original completed owner-QA results: `docs/handoffs/2026-09-01_PHASE4_OWNER_PHONE_QA_RESULTS.md`;
- approved earlier correction scope/map: `docs/handoffs/2026-09-02_PHASE4_OWNER_QA_CORRECTION_PLAN.md`;
- original 42-check definitions: `docs/handoffs/2026-09-01_INCREMENT_L_PHONE_QA_TARGET.md`.

Do **not** restart the old 42-check QA by default and do **not** continue coding the closure prototype simply because its CI is green. The current gate is owner design/scope review.

## 1. Repository / branch state at recovery verification

- `main`: `471c5570669a6007bea9796d8a2c25536b10be21` — unchanged by Phase 4 work.
- Phase 4 correction-candidate branch head before recovery/checkpoint documentation commits: `7bf76f9faa5f39463c05c498abb24a69bb8c260c`.
- That correction-candidate head is a clean descendant of `main`: **366 commits ahead, 0 behind** at verification.
- Historical/focused/tmp branches remain intentionally preserved. Do not delete them before the eventual post-merge unique-commit audit.
- `implementation/phase4-character-closure` is a focused proposal/prototype branch only; it does not replace `implementation/character-data-foundation` as durable accepted Phase 4 working history.

## 2. Original owner phone-QA result

The original Gate L real-device QA completed on 2026-09-02:

- **40 of 42 top-level checks passed**;
- Check 12 failed because of Combate alignment/IME-action reachability;
- Check 13 failed because of Equipo IME-action reachability and excessive Monedas height;
- Android system Back navigation (`N-01`) was also discovered as a separate blocking defect;
- additional non-blocking correction findings were recorded for drag feedback, terminology, spacing, Rasgos wording, numeric keyboard behavior, and Notes presentation;
- the owner requested persisted `Raza` and `Religión / Fe` fields in Trasfondo (`B-01`).

The original Gate L APK is historical evidence only. It is **not** the current acceptance candidate.

## 3. Implemented correction lineage

### Correction A — navigation + keyboard blockers

Commit: `b9fb1347...`

Implemented:

- `N-01` Android system Back hierarchy;
- `C-01` Combate editor IME-safe actions;
- `E-04` Equipo editor IME-safe actions;
- `C-02` outside-tap focus/IME dismissal without draft discard.

### Correction B — persisted Trasfondo addition

Commit: `81f0147b...`

Implemented `B-01` end-to-end:

- persisted `Raza`;
- persisted `Religión / Fe`;
- domain model;
- SQLDelight current schema/query changes;
- migration `5.sqm`;
- repository hydration/persistence;
- Android recreation codec;
- Trasfondo UI;
- migration, round-trip, and holistic regression coverage.

### Correction C — presentation/usability corrections

Commit: `24ef827a302349c213a915ac26c7d73e42e26853`

Implemented:

- `U-01` stable Combate compact-reference alignment;
- `E-05` compact Monedas editors;
- `L-01` / `C-03` reduced synthetic phone-first bottom whitespace;
- `D-01` / `E-01` visible active drag feedback across reorder surfaces;
- `E-02` compact semantic Equipo actions;
- `E-03` preserved approved accessible drag-handle interaction rather than silently changing to handle-free row dragging;
- `R-01` clearer Rasgos usage wording;
- `S-01` numeric keypad for spell level `Nivel (0-9)`;
- `T-01` product-facing `Quick Magic` terminology replaced by `Lanzamiento de Conjuros`;
- `N-02` bounded long Notes editors with scrolling affordance;
- `N-03` deterministic two-column titled Notes in wide/landscape layouts.

## 4. Correction candidate automated verification

Standard correction-candidate workflow:

- workflow run: `33710347091`;
- tested commit: `6ae415d8919efb865d7b22092d95b94b3fa7866a`;
- conclusion: **PASS**;
- full shared Kotlin/SQLDelight suite: **PASS**;
- schema migration/persistence coverage including `B-01`: **PASS**;
- Android debug assembly: **PASS**;
- Desktop build: **PASS**;
- backend install/type-check: **PASS**;
- APK upload: **PASS**.

Correction APK identity:

- artifact name: `dnd-custom-aid-debug-apk`;
- artifact ID: `9876725270`;
- artifact ZIP SHA-256: `ca0bf1a9aa25bb3c679a786ec126662bd429229cebec9aa0ed9d9eb551777bd2`;
- extracted APK SHA-256: `7eb0543ac70960f50555905acf7a9580e917f9e532f44048260bd2fd445bd5b9`.

That APK is now superseded as an acceptance target by the owner's newly identified closure requirements. It remains useful evidence but should not receive a fresh full QA cycle.

## 5. Earlier focused owner real-device retest plan — superseded as immediate next action

The earlier recovery checkpoint expected a focused retest of artifact `9876725270`. The owner has since identified additional issues and requested a broader final character-stage build before returning to QA. Therefore the earlier exact retest sequence remains historical guidance for regression coverage, but **it is no longer the immediate next action**.

When the eventual new closure APK is produced, regression coverage should still include migration/data preservation, Android Back, IME/action reachability, Combate, Equipo, Trasfondo, Rasgos, Conjuros, Notas, orientation/recreation and persistence.

## 6. Current product implementation state

The Phase 4 Android V4 character editor currently includes:

- General;
- Habilidades;
- Combate;
- Equipo;
- Trasfondo;
- Rasgos;
- conditional Conjuros;
- Notas;
- full-screen PC Settings with character-wide spellcasting visibility.

The durable correction implementation includes persistent character data, migration coverage, spellcasting sources/spells/prepared state/shared slots, Notes, Trasfondo/Rasgos ownership models, responsive/accessibility work, and the earlier owner-QA correction set.

The focused closure prototype additionally contains unapproved exploratory character-domain/schema work. It must not be described as accepted functionality until owner review and later QA.

## 7. Known continuity / governance drift still pending

Several long-lived project documents still contain Phase 3/scaffold-era status prose, including portions of:

- `README.md`;
- `AGENTS.md` current-stage section;
- `MANIFEST.md` implemented-area descriptions;
- `docs/ARCHITECTURE.md` current-status/implementation consequence prose;
- `docs/TESTING.md` current-status emphasis.

`docs/ROADMAP.md` already identifies Phase 4 as current, but its character-slice description predates much of the later implemented expansion.

The consolidated `docs/DECISIONS.md` also lags some detailed Phase 4 decision files (D-0044 through D-0046). Do not invent or silently renumber a new authoritative decision while this drift exists; proposal checkpoints may remain explicitly Pending until the owner chooses scope, after which decision consolidation must be reconciled.

There is also a separate debug-signing governance wording contradiction to resolve before merge: CI deliberately reconstructs a stable development-only debug signing identity for update-in-place migration testing, while older broad repository rules prohibit committing signing-key material. Do not silently choose a new governance convention or expose/reproduce key material. Resolve the wording/policy explicitly before merge.

## 8. Merge / cleanup boundary

Phase 4 is **not owner-accepted**.

Do not:

- merge Phase 4 to `main`;
- declare Phase 4 complete/accepted;
- delete tmp/historical branches;
- treat green CI as product approval;
- treat the closure prototype schema/domain work as approved because it already exists;
- designate a documentation-only build artifact as the next QA APK.

Phase 4 may be proposed for merge only after:

- owner approves/defer/rejects the closure proposal set;
- the official class/subclass module audit is completed against that approved design;
- approved closure work is implemented and automatically verified;
- an exact new APK commit/artifact is designated;
- focused phone + tablet QA completes with no unresolved blocking defect;
- remaining limitations are explicitly accepted/deferred where needed;
- continuity documentation drift is reconciled;
- debug-signing governance wording is reconciled;
- the owner explicitly approves the merge.

## 9. Exact continuation rule

**Current next action: owner review of `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_PROPOSAL_REVIEW.md`.**

The owner may accept, reject or defer each proposal or group. Record those results in Git. Then complete the class/subclass conditional-module audit and produce an implementation/checkpoint/gate map. Only after those product/design decisions are sufficiently approved should coding resume.
