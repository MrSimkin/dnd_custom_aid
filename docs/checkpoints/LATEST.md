# Latest project checkpoint

**Updated:** 2026-09-08  
**Controlling decision:** D-0066  
**Canonical baseline:** `main`  
**Repository consolidation:** COMPLETE  
**Historical consolidation source:** `implementation/phase4-preqa-ux-repair`  
**Current product status:** debug / pre-QA; known owner-observed defects remain  
**Latest technically verified product identity:** `0.4.0-preqa.7` / build `40700` / `debug`  
**Primary owner test phone:** Redmi Note 11 Pro 5G  
**Phone audition:** Stages A–F sufficiently covered; findings recorded; build did not pass visual acceptance  
**Tablet acceptance:** not complete; no physical owner tablet recorded  
**DM implementation:** blocked pending later Phase 4A closure acceptance

## Read next

1. `docs/PROJECT_STATE.md` — authoritative current-state snapshot;
2. `docs/BRANCH_STATUS.md` — interpretation of the many remaining historical branch refs;
3. `docs/decisions/D-0066_MAIN_CANONICAL_DEVELOPMENT_CONSOLIDATION.md` — why the current pre-QA state was consolidated into `main` before release acceptance;
4. `docs/checkpoints/2026-09-08_MAIN_CANONICAL_CONSOLIDATION.md` — completed graph/repository consolidation evidence;
5. `docs/checkpoints/2026-09-07_PHASE4_PREQA_OWNER_AUDITION_PHONE_STAGE_F.md` — latest detailed phone-audition findings;
6. `docs/checkpoints/2026-09-08_PHASE4_PREQA_FUENTE_REDUNDANCY_AUDIT.md` — source/provenance information-architecture follow-up.

## What D-0066 changed

The owner explicitly requested that the repository stop carrying the entire current Phase 4 development state only on confusing long-lived branches.

The current development baseline has therefore been promoted into canonical `main` **without claiming that it is a good release**.

The promotion was a normal non-force fast-forward. Relative to the old `main` head `471c5570669a6007bea9796d8a2c25536b10be21`, the consolidation checkpoint was 804 commits ahead and 0 behind; the old head remained the merge base.

This consolidation means:

- `main` is the single current source of truth;
- the current debug state and its known bugs are canonical development reality;
- old implementation/tmp branches are historical evidence only;
- frozen QA-evidence branches remain immutable;
- future repairs branch from `main`.

It does **not** mean Phase 4 is accepted or closed.

## Latest verified product code

The full automated gate last passed for product commit:

`43ca1f5662123ce4d355d9d618b0bfba66d17697`

Review identity:

- version `0.4.0-preqa.7`;
- build `40700`;
- workflow `34171466714` — SUCCESS;
- artifact `10035895186`;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

The consolidation audit verified that the 39 commits after that tested product commit through pre-consolidation head `0ac2d3d190f989549ced4914d7ce98274118a0d3` were documentation/governance-only. The subsequent consolidation/finalization commits are also documentation/governance-only. No newer untested product-code change is being presented as build evidence.

## Phone audition result

Do **not** resume old M6 or repeat the whole phone audition on build `40700`.

Stages A–F have yielded enough evidence to design a successor repair cycle. Important recorded families include:

- app-wide excessive padding/margins and row fragmentation;
- card action/reorder density and weak move feedback;
- major shared editor/IME problems;
- phone landscape incorrectly entering an inadequate wide/tablet presentation;
- current tablet/wide UI itself requiring redesign/audit;
- Conjuros fixed controls consuming the entire phone-landscape viewport;
- scroll/context reset on rotation;
- compactness issues in Gestión/death saves and various sticky regions;
- unclear Consumible/Munición presentation;
- generic `Fuente` provenance over-exposed in editors;
- preferred structured one-row origin model: origin type + specific origin, with `Clase` as default origin type;
- preserve real Conjuros spellcasting-source associations because they drive behavior and can themselves be custom/non-class sources;
- `Electrum` terminology;
- `Raza` only, never `Especie/raza`;
- Spanish class/subclass presentation;
- do not foreground D&D 5e/5.5e metadata where it currently has no operational purpose;
- help-text display preference and character-tab-order preference;
- 40% spacing option for successor audition;
- PT Sans Narrow Bold still needs a satisfactory presentation before typography acceptance.

Detailed evidence remains in the Stage A–F checkpoints; do not duplicate all findings into every future screen report.

## Historical detour preserved

The temporary `tmp/phase4-m6-qa-pause-docs` branch had one unique owner-QA progress file. It has been copied into canonical history as:

`docs/checkpoints/2026-09-08_PHASE4_M6_OWNER_QA_PROGRESS.md`

That file is explicitly **HISTORICAL / SUPERSEDED**. It records a real historical in-place-upgrade PASS but is not the current QA path.

## Exact next action

The repository/main consolidation is complete.

The owner is currently compiling additional observations that are **outside the formal QA exercise** and wants them included in the same upcoming development cycle.

Therefore:

1. receive and durably record the owner's additional observations;
2. reconcile them with the existing audition backlog;
3. only then design the coherent successor repair batch;
4. create a new focused branch from canonical `main` for implementation;
5. run the complete automated gate and produce a new identified debug build;
6. use targeted owner retesting of repaired families rather than blindly rerunning all prior checks;
7. defer formal M6 freeze/QA until the repaired phone/tablet baseline is acceptable.

No DM-feature implementation begins before the separate Phase 4A closure gate is later satisfied and explicitly approved.
