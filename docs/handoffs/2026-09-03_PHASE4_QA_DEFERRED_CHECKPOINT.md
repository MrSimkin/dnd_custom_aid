# Phase 4 Correction Candidate — Owner QA Deferred Checkpoint

Date: 2026-09-03 UTC / 2026-09-02 owner local time  
Branch: `implementation/character-data-foundation`  
Purpose: durable pause checkpoint after repository recovery; owner phone QA intentionally deferred to a later session.

## Current state

The lost-chat recovery is complete. No implementation work needs to be reconstructed.

The original 42-step owner phone QA is complete. Its blockers were mapped and Corrections A/B/C were implemented. The resulting correction candidate passed the full automated gate. The remaining acceptance gate is the focused owner real-device correction retest.

No owner retest was performed in this recovery session. This is an intentional pause, not a failure or blocker.

## Exact executable to resume with

Correction candidate:

- workflow run: `33710347091`;
- tested commit: `6ae415d8919efb865d7b22092d95b94b3fa7866a`;
- artifact name: `dnd-custom-aid-debug-apk`;
- artifact ID: `9876725270`;
- artifact ZIP SHA-256: `ca0bf1a9aa25bb3c679a786ec126662bd429229cebec9aa0ed9d9eb551777bd2`;
- extracted APK SHA-256: `7eb0543ac70960f50555905acf7a9580e917f9e532f44048260bd2fd445bd5b9`.

The later documentation-only commits do not alter this APK candidate.

## Focused retest order when the owner resumes

1. Migration/data-preservation smoke, including empty migrated `Raza` and `Religión / Fe`.
2. Android Back hierarchy, including IME-open behavior.
3. Combate correction checks: wrapped alignment, IME-safe actions, keyboard dismissal/draft retention, spacing and drag feedback.
4. Equipo correction checks: IME-safe actions, compact Monedas portrait/landscape, custom currency, compact actions and drag feedback.
5. Trasfondo `Raza` + `Religión / Fe` save/reopen/recreation persistence.
6. Rasgos wording, Spend/Recover, reorder persistence/feedback and landscape sanity.
7. Conjuros terminology, numeric spell-level keypad, reorder and shared slot persistence.
8. Notas long-text scrolling, wide two-column cards, reorder/save/reopen and keyboard/rotation.
9. Final resilience smoke: repeated navigation, screen off/on, app close/reopen, recreation, spellcasting hide-not-delete and sampled icon usability.

Do not restart all 42 original checks unless a focused retest reveals a broader regression.

## Repository boundary

- `main` remains untouched by Phase 4.
- Do not merge Phase 4 before focused owner QA passes and the owner explicitly approves the merge.
- Do not delete temporary/historical branches before the eventual post-merge unique-commit audit.
- Do not silently patch the tested correction candidate after acceptance; a new code fix requires a new automated gate and new QA artifact identity.

## Remaining non-QA work before eventual merge proposal

After focused QA passes:

1. reconcile stale Phase 3/scaffold-era status prose in core project docs (`README.md`, `AGENTS.md`, `MANIFEST.md`, `docs/ARCHITECTURE.md`, `docs/TESTING.md`, and where needed `docs/ROADMAP.md`);
2. explicitly reconcile the stable development-only debug signing identity with the older broad repository prohibition on committed signing-key material, without exposing/reproducing key material;
3. identify the exact accepted commit/APK;
4. prepare merge proposal for explicit owner approval.

## Exact resume instruction

When the owner returns, read:

1. `docs/PROJECT_STATE.md`;
2. this checkpoint;
3. `docs/handoffs/2026-09-03_PHASE4_CORRECTION_CANDIDATE.md` if implementation details are needed.

Then resume directly at focused retest step 1 using artifact `9876725270`. No reconstruction or new implementation should precede that QA unless repository refs have unexpectedly changed.
