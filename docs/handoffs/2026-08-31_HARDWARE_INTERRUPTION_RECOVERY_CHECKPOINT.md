# Hardware interruption recovery checkpoint — 2026-08-31

**Status:** SAFE RECOVERY POINT  
**Working branch:** `implementation/character-data-foundation`

The project owner reported laptop/hardware instability during the pre-implementation consolidation for the V4 follow-up build and explicitly requested that all current work be durably registered so the project can be resumed after a hardware failure without relying on local files or chat memory.

## Current project state

- V4 run #107 manual QA is complete and closed.
- The current APK is **not** ready for PR/merge because a follow-up build is still required.
- No production-code implementation of the follow-up build has begun after QA closure.
- The pre-implementation consolidation and coding gate are recorded in:
  - `docs/handoffs/2026-08-31_V4_FOLLOWUP_PREIMPLEMENTATION.md`
- That document consolidates the settled next-build requirements and explicitly blocks implementation until the owner reviews/answers Q1–Q6.

## Durable controlling records

Important current records include:

- `docs/handoffs/2026-08-31_V4_QA_RESULTS.md`
- `docs/handoffs/2026-08-31_V4_SETTINGS_QA.md`
- `docs/handoffs/2026-08-31_V4_RUN107_QA_CLOSURE.md`
- `docs/handoffs/2026-08-31_V4_FOLLOWUP_PREIMPLEMENTATION.md`
- `docs/decisions/D-0045_CHARACTER_SHEET_PRESENTATION.md`
- `docs/decisions/D-0046_CHARACTER_DERIVED_VALUES_AND_ADJUSTMENTS.md`
- `docs/decisions/D-0047_QUICK_MAGIC_REFERENCE.md`
- `docs/decisions/D-0048_SETTINGS_QA_CANDIDATES.md`

## Resume instructions after hardware/chat failure

1. Open branch `implementation/character-data-foundation`.
2. Read `docs/handoffs/2026-08-31_HARDWARE_INTERRUPTION_RECOVERY_CHECKPOINT.md`.
3. Read `docs/handoffs/2026-08-31_V4_FOLLOWUP_PREIMPLEMENTATION.md`.
4. Resume from the **Q1–Q6 design discussion** in that file.
5. Do **not** begin production coding until those questions are answered or explicitly deferred by the owner and the controlling decisions/spec are updated.

## Current open coding-gate questions

Q1. Approve/revise expanded 8-font audition set.  
Q2. Decide whether new tabs are discussion-only or actual new implemented data domains in the follow-up build.  
Q3. Confirm Quick Magic remains at bottom of `Resumen` even if a future detailed `Magia` tab exists.  
Q4. Choose manual spell-slot interaction model.  
Q5. Confirm behavior when a required numeric field is still blank at Save.  
Q6. Confirm calculated proficiency bonus uses the same interactive breakdown / `Ajuste adicional` pattern as other derived values.

This checkpoint exists specifically so a laptop failure does not require reconstructing decisions from memory or chat history.