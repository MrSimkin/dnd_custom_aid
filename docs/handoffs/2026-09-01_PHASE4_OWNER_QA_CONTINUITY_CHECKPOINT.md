# Phase 4 Owner QA Continuity Checkpoint

**Date:** 2026-09-01
**Status:** Automated implementation A–L complete; owner phone QA has not started.

## Resume point

The next session must resume with **owner phone QA**, not additional implementation and not a merge to `main`.

- canonical `main`: `471c5570669a6007bea9796d8a2c25536b10be21` — untouched by Phase 4 work;
- durable Phase 4 code/state head before this documentation checkpoint: `102d4e045462da37538037c13f191d3012041ddd`;
- promoted-head durability workflow: run `33468580024` — backend, full Kotlin/shared suite, Android build, desktop build and APK upload all PASS;
- no open PR was present at final verification.

## Designated owner-QA APK

Use the Gate L artifact, not an arbitrary later build:

- workflow run: `33468310534`;
- tested commit: `089a991c6491627961f1e75f3815959a8a1c8b48`;
- artifact ID: `9785676981`;
- artifact name: `dnd-custom-aid-debug-apk`;
- artifact ZIP digest: `sha256:4836f5b1fe1b9ae8cb11bdb6b61231782a2a474377afb4f9e27a347288d0f194`;
- extracted APK SHA-256 verified during handoff: `35dd06c7fda8848a3cf6c45bd96a914d066ddebecd39fde9eb13104a8691dc48`.

The QA artifact is the exact build tied to the final Gate L checkpoint. A later promoted-head artifact exists only as durability evidence and does not replace this QA target.

## Automated state already proven

Increment K responsive/accessibility corrections are closed and promoted. Increment L adds no product behavior; it adds only final regression coverage and QA documentation.

Gate L and the promoted-head durability run prove automatically:

- backend type-check;
- full shared Kotlin/SQLDelight tests;
- migration/data-preservation tests;
- holistic file-backed legacy + Phase 4 persistence/reopen regression;
- Android debug build;
- desktop build;
- APK generation/upload.

Do **not** reinterpret this as real-device visual/ergonomic acceptance.

## Owner phone QA scope

The full authoritative checklist remains `docs/handoffs/2026-09-01_INCREMENT_L_PHONE_QA_TARGET.md`.

In practical terms the QA consists of eight groups:

1. **Migration/data preservation** — confirm a pre-Phase-4/run-#180 character keeps old stats, classes, saves, skills, Quick Magic, slots, combat, equipment and currencies; new domains initialize correctly; caster migration is correct.
2. **Navigation + PC Settings** — seven/eight top tabs as appropriate; horizontal scrolling and selected-tab visibility; recreation/rotation; spellcaster OFF/ON behavior and hide-not-delete semantics.
3. **Corrective backlog** — General adjustment/speed presentation; Combat and Equipment editor/drag/responsive behavior; Habilidades two-column behavior at large text scales; Settings/theme/font persistence where applicable.
4. **Trasfondo** — edit/save/reopen all narrative fields; responsive image placeholders/cards/story; real keyboard reachability and safe dismissal.
5. **Rasgos** — create different feature types/sources; activation and usage/recovery fields; drag reorder; reopen; wide layout.
6. **Conjuros** — source CRUD/reorder/delete/fallback; class unlink safety; spells levels 0–9; multi-source spell; source-specific prepared state; search; spell order; Quick Magic/shared-slot synchronization.
7. **Notas** — large general scratchpad; titled-note add/edit/delete/reorder; keyboard behavior; recreation/rotation.
8. **Final resilience/accessibility** — 80/90/100/115/130% text scales; portrait/landscape; repeated tab switching; screen off/on; app close/reopen; unsaved-state recreation where supported; spellcaster hide/show preservation; icon touch targets/semantic controls; drag ergonomics.

The authoritative list contains 42 numbered checks. Record each finding as PASS, FAIL/blocking, or limitation/non-blocking.

## Acceptance boundary

Phase 4 is **not yet merge-eligible** solely because CI is green.

Before proposing a merge candidate:

- complete owner phone QA on the designated APK;
- no unresolved blocking defect may remain;
- known non-blocking limitations must be recorded;
- if a blocking defect appears, fix it on a focused non-`main` branch, rerun the relevant automated gate, and issue a new identified QA target;
- only after QA acceptance may the owner explicitly authorize preparation/merge toward `main`.

## Exact next action next session

1. Retrieve/read `docs/PROJECT_STATE.md`.
2. Read this continuity checkpoint.
3. Read `docs/handoffs/2026-09-01_INCREMENT_L_PHONE_QA_TARGET.md`.
4. Confirm `main` has not moved unexpectedly and identify the current durable Phase 4 branch head.
5. Begin owner phone QA with check 1 using the designated Gate L APK.
6. Record results durably as QA checkpoints as testing proceeds.

Do not restart implementation A–L and do not merge to `main` before owner QA acceptance.
