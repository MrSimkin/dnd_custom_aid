# Project State

**Last verified:** 2026-09-01  
**Canonical branch:** `main`  
**Phase 4 durable working branch:** `implementation/character-data-foundation`  
**Current recovery/increment branch:** `tmp/increment-l-final-regression-qa-target`  
**Open review:** none  
**Phase:** Phase 4 — MVP Buildout  
**Status:** The planned automated implementation sequence A–L is complete. Increment L is green and the final owner-phone-QA APK is identified. Final owner phone QA remains pending; the build is not yet merge-eligible solely from CI. `main` remains untouched.

## 0. Latest verified checkpoint

Increment K is durably promoted at `a43526a1a0ae9d30a0b53023fa4a8b9ee1836f02`; its promoted durability run `33468094884` passed backend, full Kotlin/shared tests, Android, desktop and APK upload.

Increment L began from that exact head and adds no production behavior. It adds one holistic file-backed regression plus the final QA checkpoints.

Authoritative Gate L:

- tested commit: `089a991c6491627961f1e75f3815959a8a1c8b48`;
- workflow: `Scaffold checks`;
- run ID: `33468310534`;
- backend: **PASS**;
- full shared Kotlin/SQLDelight suite: **PASS**;
- existing migration/data-preservation tests: **PASS**;
- holistic Phase 4 legacy+new-domain disk-reopen regression: **PASS**;
- Android debug build: **PASS**;
- desktop build: **PASS**;
- APK upload: **PASS**.

Final owner-QA APK:

- artifact ID: `9785676981`;
- name: `dnd-custom-aid-debug-apk`;
- size: `11120637` bytes;
- digest: `sha256:4836f5b1fe1b9ae8cb11bdb6b61231782a2a474377afb4f9e27a347288d0f194`.

Detailed QA target: `docs/handoffs/2026-09-01_INCREMENT_L_PHONE_QA_TARGET.md`.

## 1. Authority and safety rules

- Git is operative project memory; repository truth controls over chat memory.
- `main` is canonical accepted state and must not receive Phase 4 work without explicit owner approval.
- The durable Phase 4 branch is updated only through descendant-only non-force promotion after green gates.
- C-0009 controls proportionality.
- C-0010 keeps final character-sheet acceptance phone-first; green CI is not owner-device acceptance.
- Large source files retain the narrow/asserted-edit safety rule established after the historical truncation incident.

## 2. Phase 4 implementation sequence

The approved sequence from `docs/handoffs/2026-08-31_NEXT_BUILD_CONSOLIDATED_IMPLEMENTATION_PACKAGE.md` now stands as:

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

## 3. Current product state

The Android V4 character editor supports:

- General;
- Habilidades;
- Combate;
- Equipo;
- Trasfondo;
- Rasgos;
- conditional Conjuros;
- Notas;
- full-screen PC Settings with the character-wide spellcaster visibility switch.

Spellcasting supports stable custom/class-linked sources, conceptual multi-source spells, source-specific prepared state, CRUD/search/manual ordering, and one shared Quick Magic/Conjuros slot state.

Notas supports unrestricted general notes plus ordered titled cards. Background and Traits use their approved structured/free-form ownership boundaries.

Responsive/accessibility corrections through K include supported 80/90/100/115/130% scales, horizontally scrollable one-line navigation, selected-source auto-visibility, bounded long source labels, two-column narrow Habilidades-by-attribute, responsive wide layouts, protected new-domain editors, semantic drawn icon controls in corrected surfaces, and 48 dp reorder targets.

## 4. Final regression state

`CharacterPhase4FinalRegressionTest.kt` provides the final holistic current-schema disk-reopen coverage: representative legacy/run-#180 domains and every new Phase 4 domain are stored on one character, the database is closed/reopened, combined state is verified, and spellcasting hide/show remains non-destructive.

Existing migration suites continue to cover v3/v4 migration preservation, caster derivation, new-domain empty defaults, and ownership/soft-link rules.

## 5. Owner phone-QA boundary

Use artifact `9785676981` from run `33468310534` for the final approved 42-step owner phone-QA sequence documented in `docs/handoffs/2026-09-01_INCREMENT_L_PHONE_QA_TARGET.md`.

That QA includes migration preservation, navigation and PC Settings, the run-#180 corrective backlog, Trasfondo, Rasgos, Conjuros, Notas, all supported text scales, portrait/landscape, real IME behavior, drag/touch ergonomics, app reopen/recreation and final resilience.

The next build becomes eligible to be proposed as a merge candidate only when owner QA has no unresolved blocking defect and known non-blocking limitations are recorded.

## 6. Next exact action

1. Confirm the L closure/state head is a strict descendant of promoted K.
2. Fast-forward `implementation/character-data-foundation` without force.
3. Keep `main` unchanged.
4. Verify the promoted L head with the normal durability CI.
5. Stop production implementation work and perform the documented owner phone QA using artifact `9785676981`.
6. Record QA findings as durable checkpoints. If a blocking defect is found, fix it on a focused non-`main` branch and reissue the QA target; otherwise the owner may explicitly approve preparation of a merge candidate.
