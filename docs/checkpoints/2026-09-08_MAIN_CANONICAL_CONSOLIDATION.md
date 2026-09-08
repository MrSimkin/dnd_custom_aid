# D-0066 canonical `main` consolidation checkpoint

**Date:** 2026-09-08  
**Status:** COMPLETE  
**Historical source branch:** `implementation/phase4-preqa-ux-repair`  
**Old canonical `main` head:** `471c5570669a6007bea9796d8a2c25536b10be21`  
**Prepared consolidation checkpoint commit:** `c733e71487b4f4a51c78edb2930c271aafda5a43`  
**Latest technically verified product commit:** `43ca1f5662123ce4d355d9d618b0bfba66d17697`  
**Latest technically verified build:** `0.4.0-preqa.7` / `40700` / `debug`

## Purpose

The repository accumulated a very large number of implementation, safety, retry, QA and frozen-candidate branches during Phase 4. The owner explicitly requested that the current development reality be ordered and consolidated into canonical `main` now, even though it is still a debug/pre-QA state with known bugs.

D-0066 records the key distinction:

**canonical development baseline != release acceptance**.

## Completed ref move

`main` was advanced from old head:

`471c5570669a6007bea9796d8a2c25536b10be21`

to the prepared consolidation checkpoint:

`c733e71487b4f4a51c78edb2930c271aafda5a43`

using a normal **non-force fast-forward**.

Post-move verification showed:

- status relative to old `main`: `ahead`;
- ahead by: **804 commits**;
- behind by: **0 commits**;
- merge base remained the old `main` head.

No history rewrite or force update was used.

Subsequent commits on `main` finalize documentation/status wording and correct the consolidation decision number from the accidentally reused D-0048 to unique **D-0066**. Those changes are documentation/governance-only.

## Graph audit

Before the D-0066 documentation commits, GitHub compare established:

- old `main` was the merge base of `implementation/phase4-preqa-ux-repair`;
- the active line was **791 commits ahead and 0 behind** old `main`;
- `implementation/phase4-preqa-consolidation` is an ancestor of the active source line;
- `implementation/phase4-character-closure` is an older ancestor;
- `implementation/character-data-foundation` is an older ancestor;
- `tmp/phase4-post-l-state`, `tmp/phase4-m5-consolidation`, `tmp/phase4-m5-frozen-qa-candidate` and `tmp/phase4-m-audit-safety` are represented in the durable later lineage at the levels recorded by their checkpoints.

Several old safety branches diverge by exact commit identity because work was later re-integrated, replayed or superseded on durable lines. Branch divergence alone is therefore not evidence that current product functionality is missing.

## Tested-code boundary audit

GitHub compare from tested product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697` through pre-consolidation head `0ac2d3d190f989549ced4914d7ce98274118a0d3` showed 39 commits and **only documentation/governance files changed**.

No application source, shared/domain code, SQLDelight schema, Gradle/build configuration, backend product code or build workflow changed after the tested product commit in that interval.

The D-0066 consolidation/finalization commits are also documentation/governance-only.

Therefore build `40700` remains the latest technically verified product identity while repository governance advances.

## Decision-number collision corrected

During final consistency verification, the repository was found to already contain historical approved decision:

`docs/decisions/D-0048_SETTINGS_QA_CANDIDATES.md`

The initial consolidation record had accidentally reused number D-0048. To avoid creating a second ambiguous D-0048, the consolidation decision was renumbered to the next unused decision number:

`D-0066 — Consolidate the current Phase 4 development baseline into main`

Canonical file:

`docs/decisions/D-0066_MAIN_CANONICAL_DEVELOPMENT_CONSOLIDATION.md`

The accidental duplicate consolidation file is removed as part of finalization; the original historical D-0048 Settings decision remains untouched.

## Unique/orphan branch evidence handled

### `tmp/phase4-m6-qa-pause-docs`

Audit result:

- diverged from the current line by exactly one unique file;
- unique file: `docs/checkpoints/2026-09-08_PHASE4_M6_OWNER_QA_PROGRESS.md`.

Action:

- copied the record into canonical history;
- added an explicit `HISTORICAL / SUPERSEDED` warning;
- preserved the real historical in-place-upgrade PASS without allowing the old branch to control current QA.

### `tmp/phase4-m5-candidate-validator`

Audit result:

- one unique temporary helper workflow: `.github/workflows/tmp-m5-candidate-validator.yml`.

Action:

- intentionally **not** promoted into product `main`;
- it existed only to validate an exact historical candidate and has no continuing build/product purpose.

Canonical consolidation means preserving meaningful product/history, not importing obsolete one-off tooling solely so every scratch commit appears on the mainline.

## Branch-list ordering

A dedicated interpretation map now exists:

`docs/BRANCH_STATUS.md`

It records:

- `main` as the single canonical current baseline;
- 45 observed `tmp/*` refs as historical/safety/retry evidence unless explicitly frozen;
- the two frozen branches that must remain immutable;
- durable Phase 4 ancestor branches;
- the rule that old branch presence is not competing current truth.

Physical deletion of obsolete non-frozen branch refs has **not** been performed. It is repository-host tidiness rather than a requirement for canonical correctness. Frozen evidence branches must not be deleted merely for tidiness.

## Current owner-audition state carried into canonical history

The phone audition on Redmi Note 11 Pro 5G sufficiently covered Stages A–F for build `40700` and produced a substantial repair backlog.

The owner explicitly promoted repeated findings to app-wide rules, including:

- excessive padding/margins;
- avoid unnecessary multi-row layout where one row is practical;
- direct hold-and-drag card reordering where safe;
- stronger movement feedback;
- current phone-landscape behavior unacceptable app-wide;
- current tablet/wide UI itself requires redesign/audit;
- shared editor/IME family defects;
- provenance/source information architecture needs consolidation.

No physical owner tablet acceptance has been completed.

## Governance files reconciled

This consolidation refreshed:

- `README.md`;
- `AGENTS.md`;
- `MANIFEST.md`;
- `docs/PROJECT_STATE.md`;
- `docs/checkpoints/LATEST.md`;
- `docs/BRANCH_STATUS.md`;
- `docs/ROADMAP.md`;
- `docs/ARCHITECTURE.md`;
- `docs/TESTING.md`;
- `docs/QA_CHECKLIST.md`;
- detailed decision D-0066;
- historical M6 detour evidence.

Product scope, foundational architecture and recurring conventions were not reopened merely for repository cleanup.

## Current resume rule

The consolidation is complete.

1. use `main` as the only canonical current baseline;
2. use `docs/PROJECT_STATE.md` for current state;
3. use `docs/checkpoints/LATEST.md` for exact continuation;
4. treat `implementation/phase4-preqa-ux-repair` as historical source lineage, not the next development base;
5. wait for the owner's remaining non-QA observations to be durably captured before creating the next repair branch;
6. create that repair branch from `main`.

## Acceptance boundary

This consolidation does not close Phase 4A and does not authorize DM work.

Known defects remain real and canonical. The next product cycle must repair them rather than interpreting the move to `main` as acceptance.
