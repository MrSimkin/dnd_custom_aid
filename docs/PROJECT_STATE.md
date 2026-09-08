# Project State

**Last verified:** 2026-09-08  
**Canonical branch:** `main` under D-0066  
**Repository consolidation:** COMPLETE  
**Historical consolidation source:** `implementation/phase4-preqa-ux-repair`  
**Current phase:** Phase 4A — Character Foundation Closure / owner-driven repair cycle  
**Release status:** debug / pre-QA; known defects remain; not release-ready  
**DM work:** blocked until the separate Phase 4A closure/acceptance gate is later satisfied and explicitly approved

## 1. Canonical state after D-0066

The owner explicitly approved consolidating the current Phase 4 development baseline into `main` on 2026-09-08 even though formal QA/closure is not complete.

The consolidation is complete. `main` was advanced by a normal non-force fast-forward from old head `471c5570669a6007bea9796d8a2c25536b10be21` to the prepared consolidation checkpoint. Verification showed the new mainline 804 commits ahead and 0 behind the old head, with the old head preserved as merge base.

This is a repository-ordering decision, not a release or QA acceptance decision.

From this point forward:

- `main` is the only canonical current development baseline;
- old implementation/safety/tmp branches are historical evidence, not competing current state;
- future substantial product work starts from `main` on a new focused branch;
- use `docs/BRANCH_STATUS.md` to interpret the remaining historical branch refs;
- use `docs/checkpoints/LATEST.md` as the exact resume pointer.

Controlling decision:

`docs/decisions/D-0066_MAIN_CANONICAL_DEVELOPMENT_CONSOLIDATION.md`

Completed consolidation checkpoint:

`docs/checkpoints/2026-09-08_MAIN_CANONICAL_CONSOLIDATION.md`

## 2. Latest technically verified product build

The latest product code that completed the full automated gate remains:

- version `0.4.0-preqa.7`;
- build `40700`;
- type `debug`;
- tested product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- tested product tree `3b2f2ab471097d3b108c9a787fc2342c5aad683a`;
- workflow `34171466714` — SUCCESS;
- artifact `10035895186` / `DND-Custom-Aid-0.4.0-preqa.7-build-40700-debug`;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`;
- APK size `36,161,616` bytes.

A repository comparison performed during consolidation verified that the 39 commits between the tested product commit and pre-consolidation active head `0ac2d3d190f989549ced4914d7ce98274118a0d3` changed only documentation/governance files. No application source, schema, Gradle, backend or build-workflow product code changed in that interval.

The D-0066 consolidation/finalization commits are also documentation/governance-only. Therefore build `40700` remains the latest technically verified **product** identity while the canonical repository state advances.

## 3. Owner phone audition status

Primary real device:

**Redmi Note 11 Pro 5G**

The staged phone audition has covered Stages A–F sufficiently for this build. The owner explicitly generalized repeated findings rather than re-reporting them on every surface.

This does **not** mean the build passed visual QA. The audition intentionally produced a substantial repair backlog.

Detailed evidence:

- `docs/checkpoints/2026-09-07_PHASE4_PREQA_OWNER_AUDITION_PHONE_STAGE_AB.md`
- `docs/checkpoints/2026-09-07_PHASE4_PREQA_OWNER_AUDITION_PHONE_STAGE_ABC.md`
- `docs/checkpoints/2026-09-07_PHASE4_PREQA_OWNER_AUDITION_PHONE_STAGE_D.md`
- `docs/checkpoints/2026-09-07_PHASE4_PREQA_OWNER_AUDITION_PHONE_STAGE_E.md`
- `docs/checkpoints/2026-09-07_PHASE4_PREQA_OWNER_AUDITION_PHONE_STAGE_F.md`
- `docs/checkpoints/2026-09-08_PHASE4_PREQA_FUENTE_REDUNDANCY_AUDIT.md`

## 4. Current major repair families

The next product cycle must work from the recorded owner findings rather than repeating the same audit screen by screen.

### Density / layout

- excessive margins and padding are an app-wide problem across cards, buttons, boxes, dialogs/windows and fixed regions;
- controls/information that fit clearly in one row at the current scale should not be spread across several rows;
- owner wants a 40% spacing option auditioned in a successor build;
- framework/dialog spacing must honor compactness more consistently;
- sticky/fixed areas should remain only where they earn their permanent footprint.

### Card interaction

- reorderable cards should prefer direct press-and-hold/drag from the card where safe;
- dedicated move controls should not consume large permanent card space;
- movement feedback should feel stronger and clearer;
- card action controls should be grouped compactly.

### Phone landscape / tablet-wide UI

- a physical phone in landscape must not automatically become the current tablet/wide interaction model merely because width crosses a breakpoint;
- the current tablet/wide presentation itself is not considered good enough and requires redesign/audit;
- Conjuros is effectively unusable in current phone landscape because fixed source/filter controls can consume the full content viewport;
- scroll/context preservation across rotation remains a repair target.

### Editors / IME

- the shared editor family still has major keyboard/action reachability problems across multiple domains;
- active editor composition can be disrupted by orientation change;
- ordinary numeric editing must not be obstructed by leading-zero behavior.

### Information architecture / terminology

- generic `Fuente` provenance has been over-exposed across many editors;
- preferred direction is a compact structured origin model where provenance is useful: origin type + specific origin on one row;
- origin type defaults to `Clase` and may support `Dote`, `Pacto`, `Objeto`, `Raza`, `Trasfondo`, `Otro`, etc.;
- Conjuros' real spellcasting-source association system remains functional and must be preserved rather than confused with generic provenance;
- Rasgos `Fuente`/`Tipo` semantics require consolidation/clarification;
- use `Raza` only, never `Especie/raza`;
- currency label is `Electrum`, not `Electro`;
- Spanish UI should present class/subclass names in Spanish;
- D&D 5e / 5.5e metadata should not create user-facing complexity where it has no current operational purpose;
- `Consumible` / `Munición` UX is unclear and oversized.

### Other owner directions already recorded

- Habilidades sticky passive-reference band earns its space, but should explicitly say the values are passive and use horizontal space efficiently;
- Gestión fixed operational state is too tall, especially death saves;
- help text should support `Siempre visible`, tooltip/circled-i, or `Oculto` through app preferences;
- user-configurable character-tab order is desired while preserving conditional-module visibility semantics;
- PT Sans Narrow remains provisional pending satisfactory Bold presentation.

## 5. Tablet acceptance status

No physical owner tablet device has been recorded yet.

Tablet acceptance is therefore **not complete**.

The current wide/tablet model already has known design concerns and should be repaired before treating tablet QA as a final acceptance gate.

## 6. Historical QA evidence

Historical frozen candidates remain immutable evidence and are not active targets.

Especially:

- `tmp/phase4-l-frozen-qa-candidate`;
- `tmp/phase4-m5-frozen-qa-candidate`.

A brief M6 detour on 2026-09-08 produced one real in-place upgrade/data-preservation PASS. That unique record has been copied into canonical history here:

`docs/checkpoints/2026-09-08_PHASE4_M6_OWNER_QA_PROGRESS.md`

It is explicitly historical/superseded and must not replace `LATEST.md` as the resume point.

## 7. Branch-ordering status

The repository accumulated many historical safety/retry branches during Phase 4.

Key verified lineage facts from the consolidation audit:

- old `main` head `471c5570669a6007bea9796d8a2c25536b10be21` was the merge base of the pre-consolidation active line;
- `implementation/phase4-preqa-ux-repair` was 791 commits ahead and 0 behind old `main` before the D-0066 documentation commits;
- `implementation/character-data-foundation`, `implementation/phase4-character-closure` and `implementation/phase4-preqa-consolidation` are historical durable ancestors of the current line;
- the temporary M6 docs branch had one unique historical record, now preserved canonically;
- the temporary M5 candidate-validator branch has one unique one-off validator workflow, deliberately excluded from product `main` because it has no continuing purpose.

The visible historical branch refs have not been physically deleted. Their presence does not make them current alternatives to `main`.

See:

`docs/BRANCH_STATUS.md`

## 8. Current execution position

The repository/main consolidation is complete.

The owner is compiling additional observations that are **outside the formal QA exercise** so they can be included in the same upcoming development cycle.

Do not start a broad repair implementation merely from partial chat memory while that owner input is still being assembled.

Exact next sequence:

1. collect and durably record the owner's additional non-QA observations;
2. reconcile those observations with the existing audition backlog;
3. design one coherent successor repair batch from canonical `main`;
4. implement on a new focused branch;
5. run the complete automated gate and identify a successor debug build;
6. perform targeted owner retesting of repaired families instead of blindly repeating every old check;
7. only later, after an acceptable phone/tablet baseline exists, freeze a replacement formal M6 candidate and execute the formal acceptance matrix.

## 9. Phase 4 closure boundary remains open

D-0066 changes the repository merge boundary, **not** the Phase 4 acceptance boundary.

Phase 4A is not closed until the owner later accepts the repaired result through the required real-device gates and explicitly approves closure.

No DM feature implementation begins before that separate approval.
