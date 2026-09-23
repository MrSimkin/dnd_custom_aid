# Decision — PC Sheet PDF Export QA and Renderer Strategy

**Date:** 2026-09-17 (Chile local time)  
**Status:** APPROVED STRATEGY  
**Applies to:** PC Sheet PDF export implementation and visual QA  
**Related PR:** #85

## Decision

The PC Sheet PDF work will no longer proceed as:

> polish one MAIN page → approve it → extend later to other pages/families.

Instead, the implementation will use a **whole-export-first architecture with incremental QA**.

The owner explicitly prefers the slower path if it reduces rework and improves visual/technical quality.

## Rationale

The first MAIN-page proof demonstrated that the real difficulty is not merely page coordinates. The export needs a reusable rendering system covering:

- font families, weights and semantic typography roles;
- real font metrics and baseline-aware vertical alignment;
- horizontal alignment rules;
- reusable field/box geometry;
- text fitting, wrapping, overflow and readability floors;
- vector markers/icons and non-text UI symbols;
- tables/lists and repeated rows;
- portraits and crop/fit behavior;
- continuation/extended-page behavior;
- deterministic dense dummy data for visual stress testing.

If the project perfected only the first MAIN page before surveying the remaining Custom pages and the Classic D&D-style family, later requirements could force redesign of the same primitives.

## Approved development sequence

1. **Whole-export survey**
   - inspect all Custom v1 pages;
   - inspect both Custom v2 MAIN alternatives and all common pages;
   - enumerate Extended-page needs;
   - enumerate Spellbook needs;
   - enumerate Classic D&D-style needs;
   - identify every distinct rendering primitive required.

2. **Renderer research and design**
   - PDF coordinate/geometry model;
   - font discovery, embedding and role system;
   - true text measurement and baseline/vertical centering;
   - reusable text/number/marker/list/table/portrait primitives;
   - vector marker/icon strategy;
   - wrapping/fit/overflow/readability rules;
   - deterministic testing/render comparison approach.

3. **Shared rendering primitives**
   - implement the reusable renderer components before broad page mapping;
   - family-specific code should primarily define visual geometry/theme, not reimplement rendering mechanics.

4. **Primitive QA gate**
   - present deliberate test examples of the primitives themselves:
     - centered and aligned numbers;
     - left/right/centered text;
     - baseline and vertical centering;
     - multiple font roles/weights;
     - long text and wrapping;
     - proficiency/expertise/vector markers;
     - repeated rows/tables;
     - portrait crop/fit;
     - overflow/readability behavior.
   - do not treat this as final sheet approval.

5. **Full export implementation**
   - Custom v1 all pages;
   - Custom v2 both MAIN alternatives plus common pages;
   - required Extended pages;
   - Classic D&D-style family;
   - Spellbook/export additions according to D-0074 and approved scope.

6. **Structured visual QA**
   - section by section;
   - objective by objective;
   - parameter by parameter;
   - explicit PASS / CHANGE / N/A;
   - page approval only after section approval;
   - family approval only after page approval.

7. **End-to-end export QA**
   - normal representative PC;
   - dense/overflow stress PC;
   - edge cases where needed;
   - permanent/current snapshot behavior;
   - portrait available/missing;
   - full Save/Share flow once product wiring exists.

## Architectural intent

Custom template families and the Classic D&D-style family should share the same renderer primitives where practical.

Family-specific layers may differ in:

- page geometry;
- typography theme;
- visual decoration;
- field placement;
- source background/template usage.

They should not independently reimplement basic text measurement, vertical alignment, wrapping, vector marker semantics, portrait fitting, overflow handling or repeated-row behavior.

## QA principle

Implementation breadth and QA granularity are intentionally different.

The renderer should be designed with the **whole export** in mind, while owner review remains **incremental and structured** so foundational mistakes are caught before being replicated across all pages.

## Current status

Round 1 MAIN-page proofs remain rejected and are historical evidence only.

PR #85 remains draft and must not be merged on the basis of those proofs.

Next substantive work: technical research followed by renderer-foundation design, not blind coordinate tuning.
