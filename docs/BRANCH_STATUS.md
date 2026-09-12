# Branch status and repository-ordering map

**Updated:** 2026-09-12  
**Canonical navigation branch:** `main`  
**Authoritative Player implementation branch:** `implementation/phase4a-successor-cycle`  
**Current Player QA candidate:** `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7`  
**Current Player boundary:** P1–P16 repaired/automation-qualified; P17 physical owner/device QA pending

This file exists so branch names, old checkpoints, or commit chronology cannot be mistaken for current authority.

## 1. Two active authoritative lines

### `main` — global navigation + later DM/Phase 5A discovery decisions

`main` is the place to read the latest global project/discovery decisions. It contains later Phase 5A/DM product-design documentation that is not present on the Player successor branch.

It does **not** contain the latest Player runtime repair implementation and must not be used as the source branch for current Phase 4A Player code.

DM implementation remains blocked until Phase 4A receives physical owner/device acceptance and explicit closure.

### `implementation/phase4a-successor-cycle` — current Player runtime

This is the authoritative source line for current Player/Phase 4A code, including the accepted `preqa.8 / 40800` repair pass and the repaired `preqa.9 / 40900` QA candidate.

Canonical QA candidate source/package commit:

`cd0c203d337c062fa388010d300e875f2f54ced7`

Normal Scaffold run `34726572588` succeeded on that exact SHA and published artifact `10307444450` (`dnd-custom-aid-debug-apk`) with artifact digest `sha256:2e8c7e3b2a3b11096eaeed3179b707a61b0d24e241c3fb5c31e9a5d99251ba7e`.

P1–P16 are implemented/automation-qualified. P17 is the physical tablet-QA gate decision, not another implementation repair. Physical owner/device acceptance remains pending.

Documentation-only commits may move the branch HEAD beyond the candidate SHA. The candidate identity and automated evidence remain anchored to `cd0c203...` and run `34726572588`.

## 2. The active refs are intentionally divergent

The 2026-09-12 continuity reconciliation established that `main` and `implementation/phase4a-successor-cycle` contain different valid post-common-ancestor work:

- `main` carries later DM/Phase 5A discovery documentation;
- the successor branch carries extensive Player implementation/repair work.

Therefore:

- do not force-move either branch;
- do not assume `main` is latest Player code merely because it is the default branch;
- do not assume the Player branch supersedes later DM discovery records on `main`;
- do not merge merely for cosmetic linearity;
- any future integration must explicitly preserve both sets of valid work.

## 3. Historical implementation branches

These are milestone/history refs, not current resume points:

- `implementation/phase4-preqa-ux-repair` — strict ancestor of the current successor line;
- `implementation/phase4-preqa-consolidation` — strict ancestor of the current successor line;
- `implementation/phase4-character-closure`;
- `implementation/character-data-foundation`;
- `implementation/local-campaign-selection`;
- `implementation/initial-scaffold`;
- older architecture/foundation/discovery milestone refs.

Do not begin new work from them unless explicitly investigating history.

`discovery/p12-material3-audit` is retained as P12 audit evidence, not as the active implementation line.

## 4. Frozen QA evidence — keep immutable

The surviving frozen QA refs are historical evidence:

- `tmp/phase4-l-frozen-qa-candidate`;
- `tmp/phase4-m5-frozen-qa-candidate`.

Do not repurpose, force-move or use them as current development branches.

## 5. Removed/superseded temporary branches

Temporary Table Mode / QA helper branches that no longer appear in the current branch inventory are not missing active development lines. Their relevant accepted work is represented in the successor branch and/or durable checkpoints.

Use `docs/archive/2026-09-09_BRANCH_REF_ARCHIVE_BEFORE_CLEANUP.md` for deliberately removed historical refs rather than recreating them.

## 6. P-series reading rule

The `preqa.8 / 40800` repair-design sequence is P1–P17. Later files that repeat a P number are implementation/audit/closure records for those accepted points; numeric label order is not a substitute for Git ancestry and checkpoint meaning.

In particular:

- P15 = Supercompact repair; later P15 audit/implementation work may include transversal consistency fixes discovered while proving it;
- P16 = landscape/adaptive vertical-space policy and implementation;
- P17 = physical tablet-QA gate policy; physical tablet acceptance is still pending.

## 7. Exact resume rule

If the task is Player implementation/QA:

1. switch to `implementation/phase4a-successor-cycle`;
2. read `docs/checkpoints/LATEST.md` there;
3. use `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_QA_CANDIDATE.md` as the current QA candidate checkpoint;
4. do not restart P1–P16;
5. next evidence is physical owner/device QA of `0.4.0-preqa.9 / 40900`;
6. only a real QA finding should reopen a relevant repair point.

If the task is DM/Phase 5A product discovery:

1. use `main`;
2. read `docs/checkpoints/LATEST.md` there;
3. preserve D-0068/D-0069/D-0070 and later accepted discovery decisions;
4. do not implement DM features before explicit Phase 4A closure.

If the task is repository reconciliation, read `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md` first.