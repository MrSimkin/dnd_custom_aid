# Branch status and repository-ordering map

**Updated:** 2026-09-08  
**Controlling decision:** D-0048  
**Canonical branch after the consolidation:** `main`

This file exists because the repository accumulated many implementation, safety, retry and QA branches during Phase 4. Branch existence does **not** imply current authority.

## 1. Current authority

After D-0048 is applied to `main`:

- `main` is the only canonical current development baseline;
- `docs/PROJECT_STATE.md` is the authoritative current-state snapshot;
- `docs/checkpoints/LATEST.md` is the exact resume pointer;
- future substantial work starts from `main` on a new focused branch;
- old implementation/tmp branches must not be used to reconstruct current state unless a historical investigation specifically requires them.

Canonical does not mean release-ready. The current baseline remains pre-QA/debug with known defects and an open repair backlog.

## 2. Verified durable lineage already contained by the consolidation source

The following durable Phase 4 lines were directly verified as ancestors of `implementation/phase4-preqa-ux-repair` or of its durable consolidation ancestor:

- `implementation/character-data-foundation`;
- `implementation/phase4-character-closure`;
- `implementation/phase4-preqa-consolidation`.

`implementation/phase4-preqa-ux-repair` is the source line being promoted to canonical `main` under D-0048.

Before the D-0048 documentation commits, that branch was 791 commits ahead of old `main` and 0 commits behind it, so the repository can be consolidated through a non-destructive fast-forward rather than a history rewrite.

## 3. Frozen immutable QA evidence — KEEP

These branches remain intentionally immutable historical evidence:

- `tmp/phase4-l-frozen-qa-candidate`;
- `tmp/phase4-m5-frozen-qa-candidate`.

Do not force-move, repurpose or treat them as current development branches.

## 4. Historical M6 detour

`tmp/phase4-m6-qa-pause-docs` diverged from the current line by exactly one unique file:

`docs/checkpoints/2026-09-08_PHASE4_M6_OWNER_QA_PROGRESS.md`

That record has now been copied into canonical history with an explicit **HISTORICAL / SUPERSEDED** notice. The branch itself is no longer needed for current reconstruction.

## 5. Temporary validator/helper content deliberately excluded from product `main`

`tmp/phase4-m5-candidate-validator` contains one unique temporary workflow:

`.github/workflows/tmp-m5-candidate-validator.yml`

That workflow existed only to validate an exact historical candidate. It has no continuing product/build purpose and is intentionally **not** promoted into canonical `main` merely to make every temporary commit reachable from the mainline.

The same rule applies generally to disposable exact-retry, safety and helper scaffolding: current product behavior, durable verification evidence and meaningful checkpoints belong in `main`; obsolete one-off helper machinery does not.

## 6. Temporary/historical branches — NON-CANONICAL

The repository currently contains 45 `tmp/*` branch refs. Except for the frozen branches named above, they are historical implementation/safety/retry evidence and are not valid current resume points.

Current known `tmp/*` refs:

- `tmp/gate-c-association-cleanup`
- `tmp/gate-j1-exact-retry`
- `tmp/gate-j2-exact-retry`
- `tmp/gate-j-final-exact`
- `tmp/gate-k-exact`
- `tmp/general-b1-safe-edit`
- `tmp/increment-d1-navigation-wiring`
- `tmp/increment-d2-pc-settings-wiring`
- `tmp/increment-e-trasfondo-wiring`
- `tmp/increment-f-rasgos-wiring`
- `tmp/increment-g-source-ui-fix`
- `tmp/increment-g-source-wiring`
- `tmp/increment-h-spell-list`
- `tmp/increment-i-shared-slot-integration`
- `tmp/increment-j-notes-tab`
- `tmp/increment-k-responsive-accessibility`
- `tmp/increment-l-final-regression-qa-target`
- `tmp/phase4-h3-companions-ui`
- `tmp/phase4-i1-adaptive-shell`
- `tmp/phase4-i1-adaptive-shell-wire`
- `tmp/phase4-i1-adaptive-shell-wire2`
- `tmp/phase4-i1-adaptive-shell-wire3`
- `tmp/phase4-i1-adaptive-shell-wire-final`
- `tmp/phase4-i1-adaptive-shell-wire-final2`
- `tmp/phase4-i1-adaptive-shell-wire-final3`
- `tmp/phase4-i1-adaptive-shell-wire-final4`
- `tmp/phase4-i2a-supercompact`
- `tmp/phase4-i2b-table-mode`
- `tmp/phase4-j-backup-import`
- `tmp/phase4-k-stabilization`
- `tmp/phase4-l-frozen-qa-candidate` — **KEEP / FROZEN**
- `tmp/phase4-m4-implementation`
- `tmp/phase4-m4-scope-holes`
- `tmp/phase4-m4b-resource-favorites`
- `tmp/phase4-m4c-character-list`
- `tmp/phase4-m4d-settings-preview`
- `tmp/phase4-m4e-rules-source-badges`
- `tmp/phase4-m4f-state-badges`
- `tmp/phase4-m5-candidate-validator`
- `tmp/phase4-m5-consolidation`
- `tmp/phase4-m5-frozen-qa-candidate` — **KEEP / FROZEN**
- `tmp/phase4-m6-qa-pause-docs`
- `tmp/phase4-m-audit-safety`
- `tmp/phase4-post-l-state`
- `tmp/skills-b3-safe-edit`

Some safety branches diverge by commit identity because work was later re-integrated/reworked on durable lines. A divergent old commit is not automatically missing current functionality. Use the durable batch checkpoints and current code, not branch ancestry alone, to determine implementation truth.

## 7. Older milestone branches

Older discovery/foundation/architecture/implementation milestone branches may remain as historical labels, but they are not current authority once their accepted content is represented by `main`.

Examples include:

- `discovery/initial-product-picture`;
- `foundation/continuity-structure`;
- `architecture/phase2-topology`;
- `architecture/approved-backend-and-android`;
- `implementation/initial-scaffold`;
- `implementation/local-campaign-selection`.

Do not start new work from them.

## 8. Branch-ref cleanup policy

After canonical consolidation, obsolete non-frozen branch refs are safe candidates for repository-host cleanup once the owner wants the visible branch list reduced. Deleting a merged/superseded branch ref is branch-list housekeeping, not deletion of canonical history already present in `main`.

Never delete the explicitly frozen evidence branches above merely for tidiness.

Until branch refs are physically cleaned up, this file is the controlling interpretation: **old branch presence is historical evidence, not competing project truth.**
