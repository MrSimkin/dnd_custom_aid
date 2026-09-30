# Phase 2 audit — existing adaptive repair against owner QA

**Date:** 2026-09-30 (Chile local time)  
**Phase:** 2 — audit/recovery of existing repair work  
**Branch:** `audit/mara-phase2-existing-repair`  
**Base main:** `bf4ed5b7017e63f2e0f1cfb43f60250d1c5ab988`  
**Repair evidence head under audit:** `ce695c40782e1847ed70a2256885b5b07113feae`  
**Status:** ACTIVE / AUDIT ONLY / NO RENDERER CHANGES  
**Owner review mode:** batches of at most 5 decision points; unresolved owner questions are accumulated rather than interrupting technical audit.

## Governing authority

Phase 1 proved that the owner's failed 50800 QA used the pre-adaptive `15f86ec...` APK, not the later `ce695c...` repair APK.

Therefore Phase 2 audits the later repair against the full owner QA. It does not infer success from CI or page counts.

Binding acceptance authority remains:

- `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`;
- `docs/checkpoints/2026-09-28_PC_SHEET_MARA_OWNER_REQA_RUNTIME_FAIL.md`;
- `docs/checkpoints/2026-09-26_PC_SHEET_MARA_PREQA8_PDF_VISUAL_QA_DEFECTS.md`.

Later owner clarifications remain binding, especially:

- reuse existing native/source component grammar before reconstruction;
- ordinary Equipment: compact identity only, no weight, no `Consumible`, no prose description;
- meaningful names wrap rather than ellipsize;
- no fixed page-count target;
- actual output inspection is required.

## Audit classification

Each repair item is classified provisionally as:

- **KEEP** — implementation directly addresses the owner observation and is suitable to retain;
- **MODIFY** — useful implementation exists but does not fully satisfy the owner contract;
- **REPLACE** — implementation approach conflicts with the clarified owner contract;
- **MISSING/GAP** — no sufficient repair exists;
- **PENDING OWNER** — technical evidence is available but owner confirmation is required before final classification.

No renderer/product code is changed during Phase 2.

## Batch 1 — pending owner review

### B1-01 — M50800-02 Custom v1 generation / “Lectura de presagios”

**Original observation:** owner runtime on `15f86ec...` failed before producing Custom v1 PDF with:

`Custom-v1 source-matched label requires excessive compression: 'Lectura de presagios' (49.175125%)`.

**Repair attempted:**

- Desktop commit `ab9ea202710866621f1fc9003a5c79652ee9566c`;
- Android commit `ab7356320450caa2dd2410490beb8eb0339c5d2f`.

Long Custom-v1 skill labels are converted to multiple physical lines with `skillLines` before module packing. Total/training marker remains only on the first physical line.

**Evidence from exact final repair proof:** `mara-custom-v1-stress-baseline.pdf` from artifact `10942589698` generates successfully with 18 pages. Page 6 visibly contains “Lectura de presagios” as wrapped content and the cross-family failures file is empty.

**Provisional classification:** **KEEP** for the specific generation blocker. This does not approve the whole Custom-v1 Statistics layout.

**Owner question:** confirm whether the intended behavior for long custom skill names is wrapping to additional physical rows at normal/readable scale rather than further compression.

### B1-02 — M50800-04 Custom Statistics visual grammar

**Original observation / later clarification:** Extended custom attributes must reuse the already-correct Custom-v2 page-1/native attribute grammar. The QA rejected generic large black/white cards and later clarified that a generic Extended reconstruction is not acceptable merely because it includes source-like ornament fragments.

**Repair attempted:** several iterations replaced broken programmatic score/modifier boxes with source-derived ornament crops:

- `d62bc99...` restored source attribute ornament;
- `2419e557...` / `d98958e...` preserved light/dark source variants;
- `ea3e4e79...` / `db0c030...` corrected measured dark crop.

**Actual final proof:** source ornament fragments are visibly improved, but the Extended layout remains a bespoke reconstruction:
- Per Attribute uses three 184-pt custom-stat columns across the page;
- Per Ability uses a custom three-column “ATRIBUTOS / TIRADAS / HABILIDADES” table;
- neither reproduces the page-1 native attribute arrangement as a whole.

**Provisional classification:** **MODIFY/REPLACE presentation layer**. KEEP the recovered source ornament assets/crop measurements, but the surrounding Extended Custom Statistics layout does not satisfy the later reuse-first requirement.

**Owner question:** confirm that source ornament reuse alone is insufficient and that the Extended layout should be rebuilt around the native page-1 attribute module/grammar rather than retaining these generic containers.

### B1-03 — M50800-05 Custom Statistics capacity

**Original clarification:** Mara has four custom attributes — Fortuna, Cordura, Éter, Renombre — and the native-scale grammar should allow all four together; the previous three-card page limit is not a product requirement.

**Repair state in `ce695c...`:**
- Per Attribute still has `ATTRIBUTE_COLUMNS_PER_PAGE = 3`;
- final proof page 5 renders Fortuna/Cordura/Éter;
- page 6 contains Renombre alone.
- Per Ability has `ABILITY_ATTRIBUTES_PER_PAGE = 6`, so all four Mara custom attributes fit on page 5.

**Provisional classification:** **PARTIAL / MODIFY**. Per Ability removes the capacity problem for this fixture; Per Attribute explicitly preserves the rejected three-per-page limit.

**Owner question:** confirm that Per Attribute should also place all four Mara custom attributes together using the native-scale grammar, rather than preserving a three-column capacity.

### B1-04 — M50800-06 phantom custom attributes

**Original observation:** the old output painted unused custom-stat capacity as fake attribute cards/shells, visually indistinguishable from missing statistics.

**Repair attempted:**
- Per Attribute Desktop/Android: `5bd9cc18...` / `9254b262...` draw structures/markers only for `attributes.size`;
- Per Ability Desktop/Android: `bf55ab86...` / `fe5d4449...` change fixed repetition to `repeat(attributes.size)`.

**Actual final proof:**
- Per Attribute page 6 shows only the real Renombre card; the two old fake sibling cards are gone.
- Per Ability page 5 shows the four real Mara attributes and no additional fake attribute cards.

Empty table rows reserved for saves/skills remain, but they are not painted as additional attribute identities.

**Provisional classification:** **KEEP / specific defect appears corrected**. This does not approve the general visual grammar or capacity.

**Owner question:** confirm that this exact phantom-card defect can be considered correctly addressed, while leaving the surrounding Custom Statistics design open.

### B1-05 — M50800-07 Éter identity/presentation

**Original observation:** the old card appeared approximately as `ETE ter`, losing an unambiguous representation of Éter.

**Repair attempted:**
- Desktop/Android identity commits `43aff39a...` / `bc8cc476...` force explicit `<KEY> · <full name>` construction instead of merging abbreviation/name heuristically.

**Actual final proof:** both Custom-v2 presentations visibly render `ETE · Éter`; extracted PDF text also contains `ETE · Éter`.

**Provisional classification:** **KEEP / specific identity defect appears corrected**. The label may later move if the whole Custom Statistics component is replaced, but the identity rule itself is sound.

**Owner question:** confirm that `ETE · Éter` is the intended unambiguous presentation and that this identity rule should be retained even if the surrounding layout is rebuilt.

## Batch 1 evidence

Final repair proof artifact: `10942589698` from Scaffold #4088 / `36354213574`, repair head `ce695c...`.

Relevant proof files inspected directly:

- `mara-custom-v1-stress-baseline.pdf`;
- `mara-custom-v2-attribute-stress-baseline.pdf`;
- `mara-custom-v2-ability-stress-baseline.pdf`;
- `mara-cross-family-failures.txt`.

No page-count result is treated as acceptance by itself.

## Gate

Batch 1 remains **PENDING OWNER REVIEW**. No item above becomes final KEEP/MODIFY/REPLACE until the owner responds.

Phase 2 may continue technical investigation independently, but no renderer changes are permitted.
