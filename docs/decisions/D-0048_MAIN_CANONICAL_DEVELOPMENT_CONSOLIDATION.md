# D-0048 — Consolidate the current Phase 4 development baseline into `main`

**Status:** Approved  
**Date:** 2026-09-08  
**Decision owner:** Project owner

## Decision

The owner explicitly approves consolidating the current Phase 4 development state into canonical `main` now, before Phase 4 formal QA/closure is complete.

The source line for this consolidation is `implementation/phase4-preqa-ux-repair`, including its current owner-audition documentation and the repository-ordering checkpoint created for this consolidation.

This is a repository-governance consolidation, **not a release approval**.

## Meaning of `main` after this decision

After the consolidation:

- `main` is the single canonical current development baseline;
- the current product remains a debug/pre-QA build with known defects and an open repair backlog;
- build `0.4.0-preqa.7` / `40700` remains the latest technically verified product build identity until a successor is built and tested;
- the owner-audition findings recorded after the tested product commit are canonical requirements/input for the next repair cycle;
- future substantial repair work should branch from the new `main`, not from old Phase 4 implementation/safety branches.

## What this approval does **not** mean

This decision does not mean that:

- build `40700` passed the owner visual audition;
- build `40700` is a frozen M6 candidate;
- formal M6 QA is complete;
- Phase 4A is accepted/closed;
- the current tablet/wide UI is accepted;
- known phone landscape, IME, spacing/density, card interaction, information-architecture or terminology defects are waived;
- the application is release-ready or production-ready;
- DM feature implementation may begin.

All those gates remain separate.

## Historical and frozen branches

Historical implementation/safety branches stop being candidates for current truth after `main` is advanced.

Frozen QA-evidence branches remain immutable historical evidence and must not be repurposed or force-moved, especially:

- `tmp/phase4-l-frozen-qa-candidate`;
- `tmp/phase4-m5-frozen-qa-candidate`.

The temporary M6 detour documentation branch is also historical evidence. Its one unique QA-progress record is copied into the canonical checkpoint history with an explicit superseded notice during this consolidation.

Temporary validator/retry/helper branches do not become product truth merely because they contain unique helper commits. Such helper-only content is excluded from `main` unless it has a continuing concrete purpose.

## Next development rule

Once the consolidation lands, use:

1. `main` as canonical baseline;
2. `docs/PROJECT_STATE.md` for the current state;
3. `docs/checkpoints/LATEST.md` for the exact resume point;
4. a new focused branch from `main` for the next repair/development batch.

The next product work is the owner-driven repair/consolidation cycle based on the recorded audition findings plus any additional owner observations collected before implementation starts.

**DM work remains blocked until the separate Phase 4 closure/acceptance gate is later satisfied and explicitly approved.**
