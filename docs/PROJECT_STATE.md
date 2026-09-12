# Project State — global repository navigation

**Last verified:** 2026-09-12  
**Canonical navigation/discovery branch:** `main`  
**Authoritative current Player implementation branch:** `implementation/phase4a-successor-cycle`  
**Current Player QA candidate:** `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7`  
**Phase 4A Player state:** P1–P16 repaired/automation-qualified; physical owner/device QA pending under P17  
**DM implementation:** blocked until Phase 4A owner acceptance and explicit closure

## 1. `main` is not the latest Player runtime

`main` remains the canonical place for global repository navigation and later Phase 5A/DM product-discovery decisions, but it is intentionally divergent from the active Player implementation branch.

The Player successor branch contains extensive Phase 4A implementation/repair work not on `main`. `main` contains later DM/Phase 5A discovery documentation not on the Player branch.

Do not use `main` as current Player source merely because it is the default branch, and do not overwrite `main` with the Player branch because doing so would lose valid later discovery history.

See:

- `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md`;
- `docs/BRANCH_STATUS.md`.

## 2. Current Player / Phase 4A state

Authoritative code branch:

`implementation/phase4a-successor-cycle`

Repaired P1–P16 source boundary:

`d630270f2f3d8fab94f3c1290963c2da7afaf06d`

Current uniquely identifiable QA candidate:

- version: `0.4.0-preqa.9`;
- build/versionCode: `40900`;
- candidate commit: `cd0c203d337c062fa388010d300e875f2f54ced7`;
- Scaffold run `34726572588`: **SUCCESS**;
- artifact ID `10307444450`;
- artifact name `dnd-custom-aid-debug-apk`;
- artifact digest `sha256:2e8c7e3b2a3b11096eaeed3179b707a61b0d24e241c3fb5c31e9a5d99251ba7e`.

Current interpretation:

- P1–P16 accepted repair implementation is present and automation-qualified;
- P17 is the physical tablet-QA gate policy, not another code repair;
- physical owner/device acceptance remains pending;
- no CI result may be promoted to owner acceptance;
- Phase 4A is not yet explicitly owner-closed.

The next Player evidence is physical QA of `preqa.9 / 40900`. Any real-device failure may reopen only the relevant accepted repair boundary.

## 3. Current DM / Phase 5A discovery line on `main`

The later valid discovery/decision work on `main` must be preserved. Current important records include:

- D-0068 — DM live Workspace/Desk direction and DM Attention Budget foundations;
- D-0069 — approved DM Desk family, including DM Screen, Stage Desk, Dungeon Desk and Combat Desk;
- D-0070 — shared Player/DM rules-question capability and its scope/naming clarification;
- associated checkpoints preserving those decisions.

These are product/design decisions and continuity records. They do **not** authorize DM feature implementation before Phase 4A closes.

If DM discovery resumes, continue from the accepted records on `main` rather than reconstructing them from the Player branch.

## 4. Authorization state

Historical 2026-09-11 authorization on the Player branch permitted the accepted P1–P17 Phase 4A repair/validation cycle, but explicitly did not authorize DM implementation or self-awarded owner acceptance.

On 2026-09-12 the owner explicitly authorized continuity files to be corrected on all appropriate development branches, including `main`, so no development line remains unclear, and asked development to continue within the real existing authorizations.

This does not:

- retroactively alter the historical authorization checkpoint;
- authorize DM implementation;
- waive physical Player QA;
- authorize claiming owner acceptance;
- authorize destructive history rewrite or loss of either branch's valid work.

## 5. Exact continuation rules

### Player implementation / QA

Use `implementation/phase4a-successor-cycle` and its own `docs/checkpoints/LATEST.md`.

The current technical packaging step is complete. Do not restart older A–I increments or P1–P16 repairs and do not invent a new repair merely to continue coding. The next required evidence is physical owner/device QA of `0.4.0-preqa.9 / 40900`.

If that QA finds a defect, reopen the relevant accepted repair boundary and repair it on the successor branch.

### DM / Phase 5A discovery

Use `main` and preserve D-0068/D-0069/D-0070 plus subsequent accepted discovery decisions.

Discovery may continue when explicitly requested. DM implementation remains blocked until Phase 4A is explicitly owner-closed.

### Cross-line integration

Do not force-push or mechanically fast-forward one divergent line over the other. Any future integration must explicitly preserve both the current Player implementation and later main-only discovery records.

## 6. Release/acceptance status

The project remains development/debug and is not release-ready.

Phase 4A cannot be marked accepted/closed until the repaired `preqa.9 / 40900` candidate receives the required physical owner/device exercise and explicit owner closure.