# Repository continuity reconciliation — 2026-09-12

**Status:** CANONICAL CONTINUITY REPAIR  
**Purpose:** make the current development topology and Phase 4A boundary recoverable without branch archaeology.

## 1. Verified source boundaries before this documentation commit

- `main`: `de3930a8c0357bbbaa77c423f0011041f5cfd111`
- `implementation/phase4a-successor-cycle`: `d630270f2f3d8fab94f3c1290963c2da7afaf06d`

These refs are intentionally divergent. `main` contains later Phase 5A / DM product-discovery and decision documentation that is not on the Player implementation branch. The successor branch contains hundreds of Phase 4A Player implementation/repair commits that are not on `main`.

Do **not** force-move either ref, infer that one supersedes all content on the other, or mechanically merge them merely to make the graph look linear.

## 2. Current authoritative lines

### Global project / DM product-discovery line

Branch: `main`.

Use `main` for the latest global project-navigation state and the later DM/Phase 5A discovery decisions, including the approved Desk-family and shared Player/DM rules-question direction.

DM **implementation** remains blocked until Phase 4A Player work receives the required owner/device acceptance and explicit closure. Discovery/decision records on `main` are not authorization to implement DM features early.

### Player / Phase 4A implementation line

Branch: `implementation/phase4a-successor-cycle`.

This is the authoritative code line for the current Player runtime and the `preqa.8 / 40800` owner-QA repair pass.

The durable owner authorization is `docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md`. It authorized the accepted P1–P17 repair pass, while explicitly separating implementation completion from owner acceptance.

## 3. P-series interpretation

There is one accepted `preqa.8 / 40800` repair-design sequence P1–P17 plus later implementation/audit checkpoints for those decisions. Repeated P labels in later checkpoint names are implementation/closure evidence for the same accepted repair points; numeric labels must not be interpreted from commit chronology alone.

Important examples:

- P15 is the Supercompact repair/design point. Its later implementation/audit work also contains transversal presentation consistency work discovered while validating that point; this does not create a second unrelated P15.
- P16 is the adaptive landscape/vertical-space policy and its implementation closure.
- P17 is **not another code-repair item**. It is the owner-approved physical tablet-QA gate policy. It defines when tablet QA may proceed and explicitly forbids inferring tablet PASS/FAIL without physical tablet evidence.

Historical preqa checkpoints remain immutable evidence and are not rewritten by this reconciliation.

## 4. Current Phase 4A Player boundary

At product/source commit `d630270f2f3d8fab94f3c1290963c2da7afaf06d`:

- P1–P16 repair implementation is present;
- P13's previously pending normal gate is satisfied by the successful descendant/head Scaffold run;
- P15 and P16 have explicit implementation/audit closure checkpoints;
- normal Scaffold workflow run `34721374190` completed `success` on that exact HEAD;
- the run published artifact `10306416852`, `dnd-custom-aid-debug-apk`, digest `sha256:c22e08deb3fbfd9a278b6e48cc4ac6d306229b6b1e354995eb7b25735eca645d`.

This means the P1–P16 implementation set is **automation-qualified**. It does **not** mean owner/device accepted.

## 5. Owner-QA boundary

The physical owner-QA baseline that generated the repair backlog was `0.4.0-preqa.8 / 40800`.

The current source still carries that same package identity, so the successful post-repair artifact must not be confused with the earlier owner-tested `40800` build merely because the version label is unchanged.

The next technical packaging step is therefore to create a monotonic successor QA identity from the current repaired source, run the normal gate, publish the exact artifact/checkpoint, and hand that build to the owner.

P17 then governs the real-device exercise:

1. targeted phone regression/acceptance first;
2. proceed to physical tablet portrait/landscape QA once no hard shared/systemic failure makes tablet evidence meaningless;
3. bounded/soft phone defects do not automatically block tablet QA;
4. any real-device failure may reopen the relevant P1–P16 repair point;
5. only explicit owner acceptance can close the acceptance gate.

## 6. Authorization boundary as of this reconciliation

Historical authorization from 2026-09-11:

- implement the accepted Player P1–P17 repair pass automatically;
- run validation and create repair checkpoints/builds;
- do not invent unrelated behavior or DM implementation;
- do not claim final owner acceptance without real-device QA;
- at that time, do not merge/fast-forward `main` under that authorization alone.

New explicit owner authorization on 2026-09-12 permits continuity documentation to be corrected on the appropriate development branches, including `main` when needed to make repository state unambiguous. This newer authorization is used only for continuity/navigation repair; it does not retroactively rewrite the old checkpoint and does not waive the Phase 4A owner-QA gate or authorize DM implementation.

## 7. Branch reading rule

For future continuation:

- start with `docs/checkpoints/LATEST.md` on the branch you are on;
- read `docs/PROJECT_STATE.md` for that branch's current authority and gate;
- use `docs/BRANCH_STATUS.md` to understand cross-branch topology;
- for Player code, continue from `implementation/phase4a-successor-cycle`;
- for current DM/Phase 5A discovery decisions, use `main`;
- never restart old A–I work or P1–P16 repair work merely because an older checkpoint says it was pending;
- never treat automated green status as owner acceptance.

This checkpoint supersedes stale *live-pointer interpretations* only. It does not delete, alter, or falsify historical checkpoints.