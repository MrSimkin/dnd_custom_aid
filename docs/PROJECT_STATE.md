# Project State

**Last verified:** 2026-09-01  
**Canonical branch:** `main`  
**Phase 4 durable working branch:** `implementation/character-data-foundation`  
**Current phase:** Phase 4 owner phone QA  
**Open review:** none at final implementation verification  
**Status:** Planned automated implementation sequence A–L is complete and green. The final owner-phone-QA target is identified and downloaded. Owner phone QA has **not started yet**. The build is not merge-eligible solely from CI. `main` remains untouched.

## 0. Current continuity checkpoint

Primary resume document:

`docs/handoffs/2026-09-01_PHASE4_OWNER_QA_CONTINUITY_CHECKPOINT.md`

Full authoritative 42-step QA checklist:

`docs/handoffs/2026-09-01_INCREMENT_L_PHONE_QA_TARGET.md`

The exact next session action is to resume with owner phone QA, beginning at check 1. Do not restart implementation A–L and do not merge to `main` before QA acceptance.

## 1. Latest verified implementation state

- `main`: `471c5570669a6007bea9796d8a2c25536b10be21` at final verification; untouched by Phase 4 work.
- durable Phase 4 code/state head before the final continuity-only documentation commits: `102d4e045462da37538037c13f191d3012041ddd`.
- promoted-head durability workflow: `Scaffold checks` run `33468580024` — **PASS** for backend, full Kotlin/shared suite, Android debug build, desktop build and APK upload.
- no open PR was present at final implementation verification.

Increment sequence:

- A — baseline verification/map: **closed**;
- B — corrective UX backlog: **implemented through recorded checkpoints**;
- C — persistence foundation/migration: **closed**;
- D — navigation + PC Settings: **closed**;
- E — Trasfondo: **closed**;
- F — Rasgos: **closed**;
- G — Conjuros source management: **closed**;
- H — Conjuros spell list/details: **closed**;
- I — Quick Magic / Conjuros shared slots: **closed**;
- J — Notas: **closed**;
- K — responsive/accessibility integration: **closed**;
- L — final automated regression + owner QA target: **closed automatically; owner QA pending**.

## 2. Designated owner-QA APK

Use the Gate L QA artifact:

- tested commit: `089a991c6491627961f1e75f3815959a8a1c8b48`;
- workflow run: `33468310534`;
- artifact ID: `9785676981`;
- artifact name: `dnd-custom-aid-debug-apk`;
- artifact ZIP digest: `sha256:4836f5b1fe1b9ae8cb11bdb6b61231782a2a474377afb4f9e27a347288d0f194`;
- extracted APK SHA-256 verified at handoff: `35dd06c7fda8848a3cf6c45bd96a914d066ddebecd39fde9eb13104a8691dc48`.

This exact APK is the owner acceptance target. A later promoted-head APK exists as durability evidence but does not replace the designated QA artifact.

## 3. Automated regression state

Gate L passed:

- backend type-check;
- full shared Kotlin/SQLDelight suite;
- existing migration/data-preservation tests;
- holistic `CharacterPhase4FinalRegressionTest` with representative legacy/run-#180 and all new Phase 4 domains persisted together across a real SQLite close/reopen;
- Android debug assembly;
- desktop build;
- APK upload.

Automated success does **not** establish real-device visual, IME, drag, touch-target or orientation acceptance.

## 4. Current product state

The Android V4 character editor supports:

- General;
- Habilidades;
- Combate;
- Equipo;
- Trasfondo;
- Rasgos;
- conditional Conjuros;
- Notas;
- full-screen PC Settings with character-wide spellcaster visibility.

Spellcasting supports stable class-linked/custom sources, conceptual multi-source spells, source-specific prepared state, search/manual ordering and one shared Quick Magic/Conjuros spell-slot state.

Notas supports unrestricted general notes plus ordered titled cards. Trasfondo and Rasgos use their approved ownership models.

Responsive/accessibility integration through K includes the 80/90/100/115/130% supported scales, horizontally scrollable single-line navigation, selected Conjuros source visibility, bounded long source labels, two-column Habilidades-by-attribute on narrow layouts, responsive wide layouts, protected editor dismissal, semantic drawn icon controls in corrected surfaces and 48 dp reorder targets.

## 5. Owner phone-QA scope

The authoritative test has 42 numbered checks grouped into:

1. migration/data preservation;
2. navigation + PC Settings;
3. run-#180 corrective backlog;
4. Trasfondo;
5. Rasgos;
6. Conjuros;
7. Notas;
8. final resilience/accessibility.

Real-device coverage includes 80/90/100/115/130% text scale, portrait/landscape, real keyboard behavior, drag/reorder feel, navigation visibility, app close/reopen, recreation/state preservation, spellcaster hide/show preservation and icon/touch-target usability.

Record each check as PASS, FAIL/blocking, or limitation/non-blocking.

## 6. Merge/defect boundary

Phase 4 may be proposed as a merge candidate only after:

- owner phone QA is complete;
- no unresolved blocking defect remains;
- known non-blocking limitations are recorded;
- the exact accepted commit/APK remains identified.

If QA finds a blocker, fix it on a focused non-`main` branch, rerun the appropriate automated gate and issue a new QA target. Do not silently patch the accepted candidate.

## 7. Next exact action

Next session:

1. read this file;
2. read `docs/handoffs/2026-09-01_PHASE4_OWNER_QA_CONTINUITY_CHECKPOINT.md`;
3. read `docs/handoffs/2026-09-01_INCREMENT_L_PHONE_QA_TARGET.md`;
4. confirm repository refs have not moved unexpectedly;
5. install/open the designated QA APK;
6. begin owner phone QA at check 1 and record findings durably.
