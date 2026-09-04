# Phase 4 Batch M3 — prior-batch implementation completeness audit

**Date:** 2026-09-04  
**Status:** M3 COMPLETE — historical batches verified; inter-batch scope holes detected  
**Audit branch:** `tmp/phase4-m-audit-safety`  
**Production code changed:** no  
**Quality/device QA:** intentionally not evaluated here

## 1. Owner question

Before final real-device QA, verify that earlier Phase 4 batches were not merely marked GREEN: confirm that the work each batch was expected to implement actually exists in the current closure line.

This audit asks **implementation completeness**, not whether the implementation feels good on a real phone/tablet. Device quality remains later owner QA.

## 2. Controlling sources

- D-0047 — owner-approved Phase 4 closure scope;
- `2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md` — A–M decomposition;
- individual Batch 0/A1–L checkpoints and their recorded gates;
- current reachable Android/shared implementation inspected during M1/M2;
- current `docs/PROJECT_STATE.md` ledger.

## 3. Result in one sentence

**No earlier batch from Batch 0/A1 through L is found to be falsely GREEN relative to its own written batch contract. However, the execution decomposition itself left several approved D-0047 requirements unassigned or only partially closed, so Phase 4 is still not implementation-complete.**

## 4. Batch-by-batch audit

| Batch | Historical contract completeness | Audit conclusion |
|---|---|---|
| Batch 0 | documentation/current-truth housekeeping; preserve history; explicitly leave consolidated `DECISIONS.md` index lag visible if unsafe to fix atomically | **COMPLETE AS WRITTEN**. The D-0044–D-0047 consolidated-index lag was explicitly deferred to pre-merge governance, not falsely claimed complete. |
| A1 | official class/subclass catalog reconciliation + current/legacy/provenance/module tests | **COMPLETE**. Current catalog/test evidence and PASS gate recorded. |
| A2a/A2b | additive closure domain/schema + repository persistence/round-trip/dangling-reference safety | **COMPLETE**. Domain, SQLDelight migration, closure repository and regression suite exist and remain active. |
| B1 | reusable IME-safe character editor/action grammar; migrate one-off editable modal flows | **COMPLETE AS BATCH CONTRACT**. B2 explicitly records final B1 head/workflow; active editor flows inspected in M1/M2 use the shared IME-safe primitive where modal editing is intended. Wide embedded master-detail panes are not one-off modal dialogs. |
| B2 | Manual/A–Z presentation, search/filter state, drag feedback/haptics, dirty state, unsaved-leave guard, D16 state ownership foundation | **COMPLETE**. Shared helpers/primitives and current editor wiring exist. |
| C | PC Settings consolidation; lifecycle status, spellcaster hide-not-delete, haptics, progress mode, module overrides, Application Settings entry, initial Supercompact; defer final Supercompact/Table behavior to I2 | **COMPLETE AS WRITTEN**. Intentional I2 deferrals were later completed. |
| D | Gestión conditions/exhaustion, concentration, resources, rest preview/selective apply, temporary effects, inspiration/death saves, reconciliation | **COMPLETE**. Current `CharacterManagementTabV4` and shared rest operations match the contract. |
| E | General/Habilidades/Combate: class/subclass identity, hit-die suggestion, freshness in character reference metadata, portrait/token, defenses/senses/movement, passives/custom skills, combat at-glance, HP operations, contextual death saves, combat Favorite, d20 | **COMPLETE AS WRITTEN**. Current reachable surfaces contain these items. |
| F | dense Equipment/Currencies, independent Manual/A–Z, search/filter, drag, weight/attunement, locations, consumables/ammunition, duplicate/collapse, tablet master-detail | **COMPLETE**. Current active closure surface contains these behaviors. |
| G1 | Traits search/filter/group/reorder/duplicate/use meter/Favorite/responsive grouping | **COMPLETE AS WRITTEN**. Current active Traits closure surface matches the checkpoint. |
| G2 | Spells Manual/A–Z, filters, V/S/M/concentration/ritual/prepared badges, sticky/collapsible levels, Favorites/duplicate/reorder/shared slots/tablet master-detail | **COMPLETE**. Current active spell closure surface matches the checkpoint. |
| G3 | Notes previews/reorder/duplicate/context + long text; Background compact identity + collapsible story + Raza/Religión + honest image placeholders | **COMPLETE**. Current active Notes/Background surfaces match the checkpoint and earlier D-0058 placeholder boundary. |
| H1 | Artífice + Formas conditional modules over shared durable state | **COMPLETE**. Current conditional destinations/surfaces and shared operations exist. |
| H2 | Técnicas + Metamagia + Pactos, explicit ownership, shared reusable class-option implementation | **COMPLETE**. Current config-driven H2 engine is reachable and remains the correct architecture. |
| H3 | Compañeros + module-union/override integration + global draft/Save/Quick Access semantics | **COMPLETE**. Current companion surface/draft/navigation wiring exists. |
| I1 | available-width adaptive shell, navigation rail, sticky header, preserve existing master-detail, last-tab across app reopen | **COMPLETE**. Current adaptive shell and navigation preference store implement the contract. |
| I2a | Supercompact authoritative projection + ordered Quick Access + live HP/resource/slot controls | **COMPLETE AS WRITTEN**. Shared projection supports the intended target kinds and Supercompact consumes it. |
| I2b | Table mode structural-write policy while preserving operational/presentation interactions | **COMPLETE**. Shared policy and guarded UI seams are present. |
| J | versioned own-format backup/import, restore-as-copy, ID remap, atomic rollback, reconciliation, SAF import/export | **COMPLETE AS WRITTEN**. Device interaction quality remains later QA, but implementation exists. |
| K | owner-lineage schema 5 -> current migration regression + full stabilization gate | **COMPLETE**. Exact historical fixture/test and clean gate recorded; no production repair was required. |
| L | freeze exact owner-QA candidate identity + workflow/artifact/ZIP/APK hashes; no candidate-only feature change | **COMPLETE / FROZEN HISTORICAL CANDIDATE**. This historical candidate is superseded for eventual QA by any later M4 production repair, but Batch L itself was performed correctly. |

## 5. Important distinction: no false GREEN, but decomposition holes exist

The A–J execution plan was a decomposition of D-0047 and explicitly said it did **not** change D-0047 scope. D-0047 therefore remains controlling when a requirement slipped between batches.

M1/current-code inspection proves the following approved requirements are not fully represented despite all historical batch contracts being executed:

### H1 — F14 missing reachable UI

D-0047 F14 requires structured languages, tools, weapons, armor and other proficiencies.

The shared durable model/repository already contains `CharacterProficiency` / `CharacterProficiencyType` (and separate durable Weapon Mastery records), but current Android General/Habilidades/PC Settings routing does not expose a structured proficiency/language management surface.

**Classification:** inter-batch decomposition hole; implementation missing.

### H2 — F15 incomplete for generic Resources

D-0047 F15 explicitly includes resources in Favorite/Quick Access.

The shared Quick Access projection supports `CharacterQuickAccessKind.RESOURCE`, and Supercompact can resolve it, but current Gestión resource rows have no Favorite/Quick Access toggle.

**Classification:** inter-batch decomposition hole; implementation partial.

### H3 — I18 character-list summary incomplete

D-0047 I18 requires class/subclass/level + freshness + optional portrait on the character list.

Current character-list cards expose name, class name/level, lifecycle status and total level, but omit subclass, freshness and optional portrait.

**Classification:** inter-batch decomposition hole; implementation partial.

### H4 — I21 settings audition missing

D-0047 I21 requires theme/font settings to preview against a miniature real character-sheet sample rather than generic text only.

Current Application Settings uses generic `Aa Bb 123` font samples and theme color swatches; no miniature real-sheet sample is shown.

**Classification:** inter-batch decomposition hole; implementation missing.

### H5 — D06 semantic rules/source badges only partial

D-0047 D06 requires visible D&D 5e / D&D 5.5e / Custom/source badges without color as the only differentiator.

Class/subclass rows currently expose rules/source text, but not the approved semantic badge treatment.

**Classification:** approved design requirement only partially closed.

### H6 — D07 state-badge grammar only partial/inconsistent

D-0047 D07 requires consistent state badges for prepared/equipped/attuned/concentrating/favorite/etc.

Spells have clear compact badges, while comparable state in Equipment/class/other surfaces is still often plain joined metadata or isolated controls.

**Classification:** approved design requirement only partially closed. Fix must be bounded; do not turn it into a full visual rewrite.

## 6. What is NOT an M3 hole

- Real-device IME, haptic feel, rotation, layout quality or document-provider UX: implementation exists; these belong to later QA.
- The two Trasfondo image placeholders: D-0058 explicitly allowed honest non-functional placeholders; portrait/token attachment was later added separately in General.
- Consolidated `docs/DECISIONS.md` ending at D-0043: known governance/index debt, explicitly deferred before merge.
- Superseded/dead Android files found by M2: maintainability cleanup, not evidence that the historical batch failed to implement its intended replacement.
- K making no production repair: K was stabilization; no defect was demonstrated by its gate.

## 7. Required next stage created by this audit

Because M3 found approved-scope holes, add **Batch M4 — inter-batch scope-hole closure** before final remediation planning or owner QA.

M4 must close H1–H6 above with focused implementation and regression coverage. Any production change means the historical Batch L frozen candidate remains historical evidence only; a later candidate must be rebuilt/frozen after all M work is green.

After M4:

- **M5:** consolidate M1/M2/M3 findings, perform bounded dead-code/duplication cleanup that is still justified, verify no scope hole remains, and define the new candidate gate.
- only then build/freeze the replacement candidate and resume owner real-device QA.

`main` remains untouched and DM implementation remains blocked.
