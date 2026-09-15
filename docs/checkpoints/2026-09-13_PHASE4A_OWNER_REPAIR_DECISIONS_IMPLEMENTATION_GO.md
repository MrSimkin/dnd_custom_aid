# Phase 4A — owner repair decisions and implementation GO

**Date:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Physical baseline:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a`  
**Controlling source audit:** `docs/checkpoints/2026-09-13_PHASE4A_POST_P17_CROSS_DEVICE_AUDIT_REPAIR_PLAN.md`  
**Status:** OWNER DECISIONS COMPLETE / CONSOLIDATED IMPLEMENTATION AUTHORIZED

## T3 — approved adaptive card-distribution contract

The owner selected Option A and approved the refined orientation-aware semantics.

The existing exact-column-count model must be replaced by **adaptive card-distribution / density preferences with separate Portrait and Landscape controls**.

Each orientation preference describes how aggressively the UI should pack compatible cards; it does **not** promise an exact number of columns. Use a small discrete scale with user-facing concepts equivalent to:

- Comfortable
- Balanced
- Compact
- Dense

`Balanced` is the default for new/unmapped state unless compatibility mapping requires preserving a user's existing intent more closely.

The runtime computes the actual safe column count from at least:

- available content width;
- current orientation preference (Portrait or Landscape);
- effective text-size / UI-density constraints;
- a minimum usable card width appropriate to the card family.

The runtime may reduce the effective column count whenever necessary to preserve readability and interaction. A denser landscape preference may therefore result in more columns on a tablet than on a phone without lying to the user about an exact count.

Compatibility requirement: migrate/map legacy exact column preferences to the closest adaptive density intent without resetting existing users unnecessarily.

## T9 — haptic feedback `None`

Add an explicit **None** option to the haptic-feedback setting.

Selecting `None` disables app-generated haptic feedback. Existing users must retain their current haptic preference after upgrade; the migration/defaulting path must not silently convert existing non-none users to `None`.

T9 is a new bounded settings requirement and does not replace T6 (the class-editor control finding).

## Implementation authorization

The owner explicitly approved the consolidated repair direction and instructed the project to proceed with implementation.

Implementation should follow the dependency-aware repair plan, preserve accepted physical QA evidence, and create a new monotonic physical-QA candidate after material product changes. Physical revalidation remains targeted to failed/touched/affected families plus previously unresolved/blocked checks; do not replay the full phone/tablet suites from zero.

Phase 4A remains OPEN during implementation and targeted revalidation. DM implementation remains blocked until explicit owner Phase 4A closure. No P18 exists.
