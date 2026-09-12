# Project State — global repository navigation

**Last verified:** 2026-09-12  
**Canonical navigation/discovery branch:** `main`  
**Authoritative current Player implementation branch:** `implementation/phase4a-successor-cycle`  
**Phase 4A Player state:** P1–P16 repaired/automation-qualified; P17 physical owner/device QA pending  
**DM implementation:** blocked until Phase 4A owner acceptance and explicit closure

## 1. `main` is not currently the latest Player runtime

`main` remains the canonical place for global repository navigation and the later Phase 5A/DM product-discovery decisions, but it is intentionally divergent from the active Player implementation branch.

Verified pre-reconciliation source boundaries:

- `main`: `de3930a8c0357bbbaa77c423f0011041f5cfd111`;
- Player successor: `d630270f2f3d8fab94f3c1290963c2da7afaf06d`.

The Player successor branch contains extensive Phase 4A implementation/repair work not on `main`. `main` contains later DM/Phase 5A discovery documentation not on the Player branch.

Do not use `main` as current Player source merely because it is the default branch, and do not overwrite `main` with the Player branch because doing so would lose valid later discovery history.

See `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md` and `docs/BRANCH_STATUS.md`.

## 2. Current Player / Phase 4A state

Authoritative code branch:

`implementation/phase4a-successor-cycle`

Latest automation-qualified Player product/source boundary before continuity-documentation commits:

`d630270f2f3d8fab94f3c1290963c2da7afaf06d`

Normal Scaffold run `34721374190` completed `success` on that exact SHA and published artifact `10306416852` with digest `sha256:c22e08deb3fbfd9a278b6e48cc4ac6d306229b6b1e354995eb7b25735eca645d`.

Current interpretation:

- P1–P16 accepted repair implementation is present and automation-qualified;
- P17 is the physical tablet-QA gate policy, not another code repair;
- physical owner/device acceptance remains pending;
- no CI result may be promoted to owner acceptance.

The next Player technical step is a monotonic successor QA package identity from the repaired source, then normal validation and owner/device QA.

## 3. Current DM / Phase 5A discovery line on `main`

The later valid discovery/decision work on `main` must be preserved. Current important records include:

- D-0068 — DM live Workspace/Desk direction and DM Attention Budget foundations;
- D-0069 — approved DM Desk family, including DM Screen, Stage Desk, Dungeon Desk and Combat Desk;
- D-0070 — shared Player/DM rules-question capability and its scope/naming clarification;
- the associated latest/checkpoint updates that preserve these decisions.

These are product/design decisions and continuity records. They do **not** authorize DM feature implementation before Phase 4A closes.

If DM discovery resumes, continue from the accepted records on `main` rather than reconstructing them from the Player branch.

## 4. Authorization state

Historical 2026-09-11 authorization on the Player branch permitted the accepted P1–P17 Phase 4A repair pass and validation, but explicitly did not authorize `main` integration or DM implementation.

On 2026-09-12 the owner explicitly authorized continuity files to be corrected on all appropriate development branches, including `main` if needed so no development line remains unclear. This current documentation reconciliation uses that newer authorization only for repository continuity/navigation.

It does not:

- retroactively alter the historical authorization checkpoint;
- authorize DM implementation;
- waive physical Player QA;
- authorize claiming owner acceptance;
- authorize destructive history rewrite or loss of either branch's valid work.

## 5. Exact continuation rules

### Player implementation / QA preparation

Use `implementation/phase4a-successor-cycle` and its own `docs/checkpoints/LATEST.md`.

Do not restart older A–I increments or P1–P16 repairs. Continue only with the documented QA packaging/owner-QA boundary or with a repair point reopened by actual QA evidence.

### DM / Phase 5A discovery

Use `main` and preserve D-0068/D-0069/D-0070 plus subsequent accepted discovery decisions.

Discovery may continue when explicitly requested. DM implementation remains blocked until Phase 4A is explicitly closed.

### Cross-line integration

Do not force-push or mechanically fast-forward one divergent line over the other. Any future integration must explicitly preserve both the current Player implementation and later main-only discovery records.

## 6. Release/acceptance status

The project is still development/debug and is not release-ready.

Phase 4A cannot be marked accepted/closed until the repaired uniquely identifiable QA build receives the required physical owner/device exercise and explicit owner closure.