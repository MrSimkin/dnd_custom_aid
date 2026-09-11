# Phase 4A — repair implementation authorized

**Date:** 2026-09-11  
**Branch:** `implementation/phase4a-successor-cycle`  
**Authorized baseline:** `639140a08dacf1de43bae3f321843f8fff7a3850`  
**Status:** IMPLEMENTATION AUTHORIZED / IN PROGRESS

## Authorization

The owner explicitly authorized implementation after the P1–P17 repair-design discussion was closed.

This authorizes the Player Phase 4A repair pass only. It does **not** authorize DM implementation, merge/fast-forward of `main`, destructive history rewriting, reset, force push, or unrelated architecture work.

## Controlling repair decisions

Implementation must conform to the durable P1–P17 checkpoints and the consolidated owner-phone QA checkpoint. Shared/transversal points marked `FULL APP AUDIT REQUIRED` must be repaired at the shared primitive/state/policy level where practical rather than by isolated screen patches.

## Execution order

Repair shared/root contracts first, then dependent screens and concepts:

1. adaptive editor/layout and responsive primitives;
2. canonical/live HP and combat operational interaction;
3. shared collection/reorder and visual primitives;
4. provenance/catalog/state contracts;
5. settings/theme/help IA and density;
6. Table Mode state contract;
7. Supercompact redesign;
8. responsive/sticky-height integration sweep;
9. automated/regression validation and new QA build checkpoint.

Each logical batch should remain separately committed where practical for traceability.

## Acceptance boundary

Implementation completion is not itself owner acceptance. A repaired build must pass automated checks and return to physical owner QA. Tablet QA follows the P17 gate: hard shared/systemic primitive failures defer it; a meaningful soft-pass/soft-fail phone state is sufficient to proceed.
