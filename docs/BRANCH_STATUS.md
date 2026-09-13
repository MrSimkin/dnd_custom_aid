# Branch status and repository-ordering map

**Updated:** 2026-09-12  
**Canonical navigation branch:** `main`  
**Authoritative Player implementation branch:** `implementation/phase4a-successor-cycle`  
**Current Player QA candidate:** `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7`  
**Current Player boundary:** P1–P16 repaired/automation-qualified; P17 physical owner/device QA pending

This file is the canonical branch-lifecycle map. It exists so branch names, old checkpoints, or commit chronology cannot be mistaken for current authority and so no surviving development line requires archaeology merely to determine whether it is active.

## 1. Two active authoritative lines

### `main` — global navigation + later DM/Phase 5A discovery decisions

`main` is the canonical global navigation/discovery line. It contains later Phase 5A/DM product-design documentation that is not present on the Player successor branch.

It does **not** contain the latest Player runtime repair implementation and must not be used as the source branch for current Phase 4A Player code.

DM implementation remains blocked until Phase 4A receives physical owner/device acceptance and explicit closure.

### `implementation/phase4a-successor-cycle` — current Player runtime

This is the authoritative source line for current Player/Phase 4A code, including the accepted `preqa.8 / 40800` repair pass and the repaired `preqa.9 / 40900` QA candidate.

Canonical QA candidate source/package commit:

`cd0c203d337c062fa388010d300e875f2f54ced7`

Normal Scaffold run `34726572588` succeeded on that exact SHA and published artifact `10307444450` (`dnd-custom-aid-debug-apk`) with artifact digest `sha256:2e8c7e3b2a3b11096eaeed3179b707a61b0d24e241c3fb5c31e9a5d99251ba7e`.

P1–P16 are implemented/automation-qualified. P17 is the physical tablet-QA gate decision, not another implementation repair. Physical owner/device acceptance remains pending.

Documentation-only commits may move the branch HEAD beyond the candidate SHA. The candidate identity and automated evidence remain anchored to `cd0c203...` and run `34726572588`.

## 2. Complete surviving branch inventory

The following snapshot was observed immediately before the continuity-inventory update on 2026-09-12. A later documentation-only commit may move `main` or the Player successor HEAD without changing the lifecycle classification below.

| Branch/ref | Snapshot HEAD | Lifecycle status | Resume rule |
| --- | --- | --- | --- |
| `main` | `115cc7f6387c9da84c1eb1cf6784bc0b053cbfca` | **ACTIVE — global navigation + Phase 5A/DM discovery authority** | Resume DM/product discovery here only; DM implementation remains blocked |
| `implementation/phase4a-successor-cycle` | `998c7bdf73d603767e9434860ba7da34d69f92c1` | **ACTIVE — Player/Phase 4A runtime authority** | Resume Player QA/defect repair here; current QA candidate remains `cd0c203...` |
| `discovery/p12-material3-audit` | `67b1fdef6e31fbc5fd1034c7d45749f34906fbe0` | Historical audit evidence | Do not resume implementation here |
| `implementation/phase4-preqa-ux-repair` | `c733e71487b4f4a51c78edb2930c271aafda5a43` | Historical implementation milestone / ancestor of successor | Do not resume new work here |
| `implementation/phase4-preqa-consolidation` | `4322781ed96dc62af90d4c9966b14b66d4f1ffc0` | Historical implementation milestone / ancestor of successor | Do not resume new work here |
| `implementation/phase4-character-closure` | `c486df837411107d900331649caf89f1cb642984` | Historical Phase 4 milestone | Do not resume new work here |
| `implementation/character-data-foundation` | `69c9ff1cecc3c0417aa9d378a50c747486447d7c` | Historical implementation milestone | Do not resume new work here |
| `implementation/local-campaign-selection` | `124626aa6f0fabd449ee5823c1651e3cc01f3e70` | Historical implementation milestone | Do not resume new work here |
| `implementation/initial-scaffold` | `2f8746de1053bf97cc18d7a522f2027e91879251` | Historical scaffold milestone | Do not resume new work here |
| `architecture/approved-backend-and-android` | `2d0fbf769fb754d199d3191cc388048f5ebf0070` | Historical architecture milestone | Reference only |
| `architecture/phase2-topology` | `a11e347213d35ff2143822b9fb48415974a8d7b3` | Historical architecture milestone | Reference only |
| `foundation/continuity-structure` | `d9aa474c67ee920fef3fcdd112d9f03b9c065b5f` | Historical continuity/foundation milestone | Reference only; current continuity is defined here and in live checkpoints |
| `discovery/initial-product-picture` | `1d246f15500f6fdd61ad62de3d1125ca924a839e` | Historical initial discovery milestone | Reference only |
| `tmp/phase4-l-frozen-qa-candidate` | `5cc034d3fdf4c25d935bd698aeaf2a3f9e427f27` | **FROZEN QA EVIDENCE** | Keep immutable; never use as a development resume point |
| `tmp/phase4-m5-frozen-qa-candidate` | `adc286b3e1305ed706c2ed04d478a43652f6b365` | **FROZEN QA EVIDENCE** | Keep immutable; never use as a development resume point |

No other surviving branch in the repository branch inventory is an active development line as of this snapshot.

If a future branch is created, this table or its successor must be updated when that branch becomes an authoritative resume point. Branch existence alone never makes a branch active.

## 3. The two active refs are intentionally divergent

The 2026-09-12 continuity reconciliation established that `main` and `implementation/phase4a-successor-cycle` contain different valid post-common-ancestor work:

- `main` carries later DM/Phase 5A discovery documentation;
- the successor branch carries extensive Player implementation/repair work.

Therefore:

- do not force-move either branch;
- do not assume `main` is latest Player code merely because it is the default branch;
- do not assume the Player branch supersedes later DM discovery records on `main`;
- do not merge merely for cosmetic linearity;
- any future integration must explicitly preserve both sets of valid work.

## 4. Historical implementation and audit refs

Historical milestone/audit refs are retained for evidence, bisecting, and provenance. They are not current resume points unless a later explicit checkpoint says otherwise.

`discovery/p12-material3-audit` is specifically retained as P12 audit evidence, not as an active implementation line.

Temporary Table Mode / QA helper branches that no longer appear in the current branch inventory are not missing active development lines. Their relevant accepted work is represented in the successor branch and/or durable checkpoints.

Use `docs/archive/2026-09-09_BRANCH_REF_ARCHIVE_BEFORE_CLEANUP.md` for deliberately removed historical refs rather than recreating them.

## 5. Frozen QA evidence — keep immutable

The surviving frozen QA refs are historical evidence:

- `tmp/phase4-l-frozen-qa-candidate`;
- `tmp/phase4-m5-frozen-qa-candidate`.

Do not repurpose, force-move, merge forward merely for convenience, or use them as current development branches.

## 6. P-series reading rule

The `preqa.8 / 40800` repair-design sequence is P1–P17. Later files that repeat a P number are implementation/audit/closure records for those accepted points; numeric label order is not a substitute for Git ancestry and checkpoint meaning.

In particular:

- P15 = Supercompact repair; later P15 audit/implementation work may include transversal consistency fixes discovered while proving it;
- P16 = landscape/adaptive vertical-space policy and implementation;
- P17 = physical tablet-QA gate policy; physical tablet acceptance is still pending.

Do not infer a second active P-series solely from repeated labels in historical records.

## 7. Authorization boundary

The durable 2026-09-11 Player repair authorization permits the accepted P1–P17 Phase 4A repair/validation cycle, QA packaging, checkpoints, and repairs reopened by real QA evidence. It does **not** authorize unrelated Player feature invention, DM implementation, destructive history rewriting, or claiming owner/device acceptance from automation.

The owner's 2026-09-12 instruction additionally authorizes continuity files to be corrected on all appropriate branches, including `main`, so all development lines remain recoverable without archaeology, and asks development to continue within the real existing authorizations.

Accordingly:

- continuity/navigation repair on `main` and the Player successor is authorized;
- current Player QA packaging is authorized and complete;
- a concrete Player defect found by physical QA may be repaired on the successor branch under the accepted repair cycle;
- physical owner/device acceptance must come from the owner;
- DM **discovery/design** may continue on `main` when requested;
- DM **implementation** remains blocked until explicit Phase 4A owner closure.

## 8. Exact resume rule

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

If the task is repository reconciliation, read `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md` first and preserve both active authoritative lines.
