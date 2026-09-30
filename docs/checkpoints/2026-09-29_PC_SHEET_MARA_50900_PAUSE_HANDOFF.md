# Mara 50900 — pause handoff after Phase 5 candidate stamping

**Date:** 2026-09-29 (Chile local time)  
**Branch:** `fix/pc-sheet-50800-phase1-native-semantics`  
**Candidate source head:** `93d56a55cbc8546713c86bc6d5c72b908b14a5ba`  
**Candidate identity:** `0.5.0-preqa.9 / 50900`  
**Status:** PAUSED / EXACT CANDIDATE BUILT / FINAL 32-ITEM MATRIX DISPOSITION PENDING / OWNER RE-QA NOT YET STARTED  
**Acceptance authority:** `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`

## 1. Why this checkpoint exists

Work was explicitly paused after creating and validating the first uniquely identified post-50800 candidate. This checkpoint is the canonical resume authority so a later session does not restart old Phase 2B/Phase 3/Phase 4 work or treat a green build as owner acceptance.

The repair remains unmerged. No owner QA result exists for 50900 yet.

## 2. Work completed before the pause

The earlier internal progression phases remain valid evidence:

- Phase 1/1B — Custom-v1 generation safety and native Equipment / Equipo Especial reuse;
- Phase 2A — Custom-v2 Custom Statistics, clean dynamic labels, source-underlay cleanup and name ribbon;
- Phase 2B.1 — Custom-v2 Traits/Rasgos + Trasfondo/Historia native reuse;
- Phase 2B.2 — Custom-v2 Notes native reuse, boundaries, continuation identity and packing;
- Phase 2B.3 — Equipment atomicity and surviving-module reclaim;
- Phase 3 — Custom-v2 Combat/Actions and Resources/Options adaptive reclaim;
- Phase 4 — Fantasy Combat semantics, semantic wrapping and adaptive continuation reclaim.

All of those were internal exact-artifact progression passes only. The master matrix was intentionally left OPEN until one exact promoted candidate could be reviewed end-to-end.

## 3. Phase 5 findings and repairs

### 3.1 Run 4309 exposed a remaining Custom-v1 Traits blocker

Exact source:

- head `22bb175d8bd9bbd12c067420d310a3b0a4fbb94d`;
- workflow 4309 / `36623266391` — SUCCESS;
- proof artifact `11059618225`;
- proof digest `sha256:1e63f3c9bdbcb1f6c97774d157da0e105809e35fc25df14cbee09bb70154cec7`.

Artifact QA showed that Custom v1 still repeated the full native Traits scaffold across pages 9–31 while only trait-detail content survived. It also allowed anonymous semantic tails at page starts. This violated M50800-27 even though CI was green.

The defect was mapped in:

`docs/checkpoints/2026-09-29_PC_SHEET_MARA_50800_PHASE5_PRECODE_MAP.md`

and repaired as Phase 5A.

### 3.2 Phase 5A — Custom-v1 Traits reclaim

Validated checkpoint:

`docs/checkpoints/2026-09-29_PC_SHEET_MARA_50800_PHASE5A_V1_TRAITS_CLOSURE.md`

Key validated evidence:

- run 4318 / `36649417194` — SUCCESS;
- head `4255e38b92cec8f9e04257ef157ddc2e66a1df10`;
- artifact `11069468492`;
- Custom v1 moved from repeated full scaffold to source-derived full-width `Detalles de Rasgos` pages after sibling streams exhausted;
- logical trait records remain whole and identifiable;
- hidden source-form underlay was removed;
- all Mara trait identities remain preserved.

M50800-27 Custom-v1 Traits portion has positive internal evidence but remains formally OPEN pending final candidate disposition.

### 3.3 Phase 5B — Custom-v1 Resources / Options reclaim

The same 4309 artifact also showed pages that retained an empty Options table after Options were exhausted, plus anonymous Resource detail tails at page starts.

Repair sequence ended at:

- renderer fix `33b2e29c5a203bfb34c600cf8463dad66ce5f23a`;
- generated Android sync `57f115082fcc48a9b453a44cb410b88908b6e31f`;
- regression guard `daabe258803d93f8a585d90a457fd9c2eea5f90e`;
- workflow 4324 / `36650594025` — SUCCESS;
- proof artifact `11069919274`;
- proof digest `sha256:3cb2e62180df8998f146609ff047be12aa55cc86e0ea8f0dbdf2f52df64100c0`.

Exact 4324 visual review showed:

- Custom v1 = 25 pages observed;
- native split Resources + Options remains while both streams survive;
- once Options exhaust, Resources reclaim the page instead of retaining an empty Options scaffold;
- logical Resource groups remain associated with their identity;
- previously repaired Custom-v1 Notes still use both native columns and preserve continuation identity;
- Equipment / Equipo Especial remains on the native grammar.

No new blocking visual regression was identified in the inspected Fantasy or Custom-v2 pages.

## 4. Exact 50900 candidate

After the 4324 artifact review, the owner-facing candidate identity was bumped rather than reusing 50800:

- versionName: `0.5.0-preqa.9`;
- versionCode: `50900`;
- source commit: `93d56a55cbc8546713c86bc6d5c72b908b14a5ba`;
- workflow: 4325 / `36661323553` — SUCCESS;
- Kotlin: SUCCESS;
- backend: SUCCESS;
- hosted-database: SUCCESS.

Artifacts from the exact 50900 commit:

- APK: `11074108563`, digest `sha256:8fc59ed1e377f82174cc8740d57e9042bae999b1ff4274b680263c716f9e175a`;
- populated proof bundle: `11074068606`, digest `sha256:5b818fabb8ffa3306228bcee66eb630a75f3034b4b38e3343caba8ad3ad9c9bf`;
- source renders: `11073634096`, digest `sha256:fc04025802914e467a06cb890e1de6434f6a7b5fc0a1b96dadd0e961ea5ae638`.

Exact 50900 real-Mara PDF observations:

| Family | Pages | PDF SHA-256 |
| --- | ---: | --- |
| Fantasy | 37 | `70c8acc26ed394e821043476bc0556968ec3442c5b6673f09e78e19024931b84` |
| Custom v1 | 25 | `cdfd27dfd6d01b27250120cfa8466c5aab33c15eea6fc6501118ec48c0b81bae` |
| Custom v2 · Atributo | 16 | `00bab431ecc5f1151e01609c21ee092821624b5bb62217ece52e00dcf86e4fe7` |
| Custom v2 · Habilidad | 16 | `eb43906fb4bd5c854f8b502e2edb345f88f4749da2b3ef8cb2c06865187d0e13` |

Page counts are observations only, never acceptance targets.

The extracted text hashes of all four 4325 PDFs match their corresponding 4324 PDFs exactly. The only code change between those runs is the Android version/build identity stamp. This is useful equivalence evidence, but it does **not** replace the required final exact-candidate visual/matrix walk.

## 5. Acceptance state at pause

Positive candidate evidence now exists for the substantive repair areas exercised in Phases 1–5B, including all-four-family generation and the unique build identity.

However, **M50800-31 is not yet complete**: the final item-by-item disposition of M50800-01 through M50800-32 against the exact 50900 candidate has not been persisted.

Therefore:

- do **not** mark the 32-item master matrix closed;
- do **not** call 50900 owner-approved;
- do **not** merge the repair branch;
- do **not** resume Current Snapshot or Media/Handouts;
- do **not** create another code fix unless the exact 50900 matrix walk exposes a real OPEN or CHANGED/NEW defect.

## 6. Exact resume procedure

Resume from this checkpoint and do only the following:

1. verify the active branch still points to this documentation state and that candidate source commit `93d56a55...` remains the renderer/product basis;
2. reopen the exact 50900 proof artifact `11074068606`;
3. walk **M50800-01 through M50800-32** against the exact candidate, using rendered pages where the item is visual and text/data regressions where appropriate;
4. record every item as `FIXED`, `OPEN`, or `CHANGED/NEW`;
5. if any blocking item is OPEN/NEW, map and repair only that defect, assign a new unique candidate identity if the owner-facing binary changes, and repeat exact-candidate acceptance;
6. if all blocking items pass, update the canonical checkpoint to an owner-QA-ready closure and only then hand the APK to the owner for manual re-QA.

Do not restart at Phase 2B, Phase 3 or Phase 4. Do not use page-count reduction as a substitute for visual acceptance.

## 7. Owner QA boundary

The owner explicitly asked to pause before the final matrix closure was persisted.

The 50900 APK exists as a CI artifact, but at this checkpoint it is **not yet handed off as an owner-QA-ready build**. The next session must finish the exact-candidate acceptance ledger first.
