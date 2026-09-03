# Project State

**Last verified:** 2026-09-03 UTC / 2026-09-02 owner local time  
**Canonical branch:** `main`  
**Phase 4 durable working branch:** `implementation/character-data-foundation`  
**Current phase:** Phase 4 correction candidate — focused owner phone retest  
**Open review:** none  
**Status:** The original 42-step owner phone QA is complete. Its blocking findings were mapped and Corrections A/B/C were implemented. The correction candidate is green in automated CI and a new APK artifact is identified. **Focused owner real-device retest is the remaining acceptance gate. `main` remains untouched and Phase 4 is not yet merge-approved.**

## 0. Current continuity checkpoint

Primary resume document:

`docs/handoffs/2026-09-03_PHASE4_CORRECTION_CANDIDATE.md`

Supporting authoritative records:

- original completed owner-QA results: `docs/handoffs/2026-09-01_PHASE4_OWNER_PHONE_QA_RESULTS.md`;
- approved correction scope/map: `docs/handoffs/2026-09-02_PHASE4_OWNER_QA_CORRECTION_PLAN.md`;
- original 42-check definitions: `docs/handoffs/2026-09-01_INCREMENT_L_PHONE_QA_TARGET.md`.

Do **not** restart implementation increments A–L and do **not** restart all 42 QA checks by default. Continue with the focused correction retest below. If a focused retest reveals a cross-cutting regression, expand only the affected area.

## 1. Repository / branch state at recovery verification

- `main`: `471c5570669a6007bea9796d8a2c25536b10be21` — unchanged by Phase 4 work.
- Phase 4 correction-candidate branch head before this state-document recovery commit: `7bf76f9faa5f39463c05c498abb24a69bb8c260c`.
- That correction-candidate head is a clean descendant of `main`: **366 commits ahead, 0 behind** at verification.
- No newer repository push/branch head was found after the correction-candidate handoff.
- Historical/focused/tmp branches remain intentionally preserved. Do not delete them before the eventual post-merge unique-commit audit.

The current working line therefore remains `implementation/character-data-foundation`; no alternate branch should replace it for continuation.

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
- artifact digest: `sha256:ca0bf1a9...777bd2` (full value retained in the correction-candidate handoff);
- extracted APK SHA-256: `7eb0543a...5b9` (full value retained in the correction-candidate handoff).

The later commit `7bf76f9faa5f39463c05c498abb24a69bb8c260c` records that artifact identity and does not change application behavior.

Automated success does **not** establish real-device visual, IME, Back-navigation, drag, touch-target, orientation, or migration acceptance.

## 5. Focused owner real-device retest — exact next action

Use the correction APK identified above and execute the focused correction retest. The intended order is:

1. **Migration/data-preservation smoke** — prior character/campaign data survives; new `Raza` and `Religión / Fe` fields initialize empty on migrated data.
2. **Android Back hierarchy (`N-01`)** — PC Settings → character editor → character list → campaigns; only root exits normally; repeat with IME open.
3. **Combate** — Check 12A alignment at normal + larger text scale; 12B action reachability with IME; outside-tap keyboard dismissal without draft loss; spacing and drag feedback.
4. **Equipo** — Check 13B ordinary/special editor IME reachability; 13C compact Monedas in portrait/landscape with custom currency; action compactness and drag feedback.
5. **Trasfondo `B-01`** — enter `Raza` and `Religión / Fe`, save, reopen, rotate/recreate, verify exact persistence.
6. **Rasgos** — usage wording, Spend/Recover regression, reorder feedback/persistence, landscape layout.
7. **Conjuros** — `Lanzamiento de Conjuros` terminology, numeric spell-level keypad, reorder feedback, shared slot synchronization/persistence.
8. **Notas** — long-text scrolling, portrait/landscape, wide two-column titled notes, reorder/save/reopen, keyboard/rotation.
9. **Final resilience smoke** — tab switching, screen off/on, full app reopen, rotation with in-progress edits, spellcasting OFF/ON hide-not-delete, sampled icon touch/meaning.

Record any failure against the exact finding/component. Do not restart Phase 4 from scratch.

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

The implementation includes persistent character data, migration coverage, spellcasting sources/spells/prepared state/shared slots, Notes, Trasfondo/Rasgos ownership models, responsive/accessibility work, and the owner-QA correction set summarized above.

This is substantially newer than several root/core documentation files that still describe the earlier Phase 3/scaffold state.

## 7. Known continuity / governance drift still pending

This recovery verified that several long-lived project documents still contain Phase 3/scaffold-era status prose, including portions of:

- `README.md`;
- `AGENTS.md` current-stage section;
- `MANIFEST.md` implemented-area descriptions;
- `docs/ARCHITECTURE.md` current-status/implementation consequence prose;
- `docs/TESTING.md` current-status emphasis.

`docs/ROADMAP.md` already identifies Phase 4 as current, but its character-slice description predates much of the later implemented expansion.

This factual continuity drift must be reconciled before an eventual merge proposal, but it does **not** require changing the already-green correction code before focused owner retest.

There is also a separate debug-signing governance wording contradiction to resolve before merge: CI deliberately reconstructs a stable development-only debug signing identity for update-in-place migration testing, while older broad repository rules prohibit committing signing-key material. Do not silently choose a new governance convention or expose/reproduce key material. Resolve the wording/policy explicitly before merge.

## 8. Merge / cleanup boundary

The correction candidate is **not yet owner-accepted**.

Do not:

- merge Phase 4 to `main`;
- declare Phase 4 complete/accepted;
- delete tmp/historical branches;
- silently patch the tested candidate after owner acceptance;
- treat green CI as a substitute for the focused phone retest.

Phase 4 may be proposed for merge only after:

- focused owner phone retest completes;
- no unresolved blocking defect remains;
- remaining limitations are explicitly accepted/deferred where needed;
- continuity documentation drift is reconciled;
- debug-signing governance wording is reconciled;
- the exact accepted commit/APK remains identified;
- the owner explicitly approves the merge.

## 9. Exact continuation rule

**Next project action: perform the focused owner real-device correction retest against artifact `9876725270`.**

If it passes, proceed to continuity/governance reconciliation and prepare a merge proposal for explicit owner approval. If it fails, create a focused non-`main` correction for the exact failed finding, rerun the appropriate automated gate, identify a new APK, and retest only the affected/regression scope.
