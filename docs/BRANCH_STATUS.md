# Branch status and repository-ordering map

**Updated:** 2026-09-14  
**Canonical navigation branch:** `main`  
**Authoritative Player implementation branch:** `implementation/phase4a-successor-cycle` until planned convergence  
**Observed Player branch HEAD:** `b9dea8ad6b17dcf3feeabba263eff1ee498f1536`  
**Frozen Player candidate:** `0.4.0-preqa.13 / 41300` at `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`  
**Current global decisions:** D-0071 / D-0072 / D-0073  
**Next topology change:** dedicated validated convergence after explicit implementation authorization

This file is the canonical branch-lifecycle map. Branch existence, old checkpoint wording or commit chronology do not by themselves establish current authority.

## 1. Two active authoritative lines remain before convergence

### `main` — global integrated-MVP truth

`main` is the canonical global navigation/product/architecture/governance line.

It contains the current integrated-MVP decisions, including:

- D-0071 — integrated Player + Server + DM architecture;
- D-0072 — complete DM Desktop product/Manager definition;
- D-0073 — exact MVP boundary, implementation governance and branch convergence direction.

`main` does **not yet** contain the authoritative latest Player runtime.

### `implementation/phase4a-successor-cycle` — current Player runtime/evidence

This remains the authoritative source for current Player/Phase 4A runtime code, local migrations, Player tests/guard scripts and exact frozen-candidate evidence until the planned convergence is completed.

Observed branch HEAD during the 2026-09-14 technical-readiness review:

`b9dea8ad6b17dcf3feeabba263eff1ee498f1536`

That HEAD is a documentation synchronization commit after the frozen APK candidate.

Current frozen candidate:

- version `0.4.0-preqa.13`;
- versionCode `41300`;
- candidate commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — **SUCCESS**;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted cross-device physical revalidation pending.

The candidate run was independently verified during the technical-readiness review as completed/successful on the exact candidate SHA.

Automation is not physical owner acceptance.

## 2. Planned convergence — approved direction, not yet executed

D-0073 replaces the older `DM implementation blocked until standalone Phase 4A closure` sequencing rule with an integrated-MVP convergence/build strategy.

After explicit owner implementation authorization:

1. refresh both active lines;
2. create a dedicated convergence branch from current `main`;
3. deliberately reconcile the Player successor into it;
4. preserve successor authority for Player runtime, SQLDelight migrations, Player tests/guards and exact Player evidence;
5. preserve current `main` authority for later integrated product/architecture/governance decisions;
6. deliberately reconcile shared CI/navigation files;
7. run the aggregate build/test/guard gates;
8. merge the coherent validated baseline to `main` only if no valid work was lost.

After that successful merge:

- `main` becomes the single normal integrated-MVP trunk;
- `implementation/phase4a-successor-cycle` becomes frozen historical/QA evidence;
- this file must be updated again to reflect the new topology.

Do not force-move either current active line over the other.

## 3. Divergence observed during technical-readiness review

Comparison of current `main` and the Player successor showed:

- status: `diverged`;
- successor ahead by 574 commits;
- successor behind `main` by 40 commits;
- merge base: `f7f0389fde4fd3a72ca8f8a547dae38255825266`.

The large commit count does not imply two competing full products. The successor carries the substantial Player runtime evolution; later `main` work is predominantly integrated product/architecture/documentation. The backend, hosted database and Desktop product on `main` remain scaffolds.

Therefore convergence is expected to require deliberate reconciliation primarily around Player/shared runtime plus governance/CI/navigation, not a choice between competing server/Desktop implementations.

## 4. Historical surviving refs

The following known historical/frozen refs remain evidence and are not normal development resume points unless a later checkpoint explicitly changes their status:

| Branch/ref | Lifecycle status | Resume rule |
| --- | --- | --- |
| `discovery/p12-material3-audit` | Historical audit evidence | Do not resume implementation here |
| `implementation/phase4-preqa-ux-repair` | Historical implementation milestone / successor ancestor | Do not resume new work here |
| `implementation/phase4-preqa-consolidation` | Historical implementation milestone / successor ancestor | Do not resume new work here |
| `implementation/phase4-character-closure` | Historical Phase 4 milestone | Do not resume new work here |
| `implementation/character-data-foundation` | Historical implementation milestone | Reference only |
| `implementation/local-campaign-selection` | Historical implementation milestone | Reference only |
| `implementation/initial-scaffold` | Historical scaffold milestone | Reference only |
| `architecture/approved-backend-and-android` | Historical architecture milestone | Reference only |
| `architecture/phase2-topology` | Historical architecture milestone | Reference only |
| `foundation/continuity-structure` | Historical continuity/foundation milestone | Reference only |
| `discovery/initial-product-picture` | Historical discovery milestone | Reference only |
| `tmp/phase4-l-frozen-qa-candidate` | **FROZEN QA EVIDENCE** | Keep immutable |
| `tmp/phase4-m5-frozen-qa-candidate` | **FROZEN QA EVIDENCE** | Keep immutable |

Use `docs/archive/2026-09-09_BRANCH_REF_ARCHIVE_BEFORE_CLEANUP.md` for deliberately removed historical refs rather than recreating them.

Branch existence alone never makes a branch active.

## 5. Frozen QA evidence rule

Frozen QA refs and exact candidate commits remain historical evidence. Do not:

- repurpose them as normal development branches;
- force-move them;
- claim a newer integrated baseline retroactively passed the old physical gate;
- discard useful accepted/manual evidence merely because architecture expands.

The integrated-MVP strategy changes future implementation sequencing; it does not rewrite history.

## 6. Current authorization boundary

Product/design scope and the technical implementation strategy are sufficiently defined under D-0071/D-0072/D-0073 and the technical-readiness checkpoint.

Routine low-level engineering is delegated to the technical assistant/Workers and does not require owner rubber-stamping.

However, the owner has **not yet authorized product-code implementation/convergence** merely by asking for technical review.

Therefore at this exact point:

- documentation/governance/readiness corrections are allowed;
- do not begin the convergence branch as product-code work yet;
- do not begin hosted/DM feature implementation yet;
- the next owner decision is the explicit go/no-go to start the integrated-MVP implementation, beginning with protected branch convergence.

## 7. Exact resume rule

For global technical/product state:

1. use `main`;
2. read `docs/checkpoints/LATEST.md`;
3. read `docs/checkpoints/2026-09-14_INTEGRATED_MVP_TECHNICAL_READINESS_REVIEW.md`;
4. preserve D-0071/D-0072/D-0073.

For current Player source/evidence before convergence:

1. switch to `implementation/phase4a-successor-cycle`;
2. read that branch's `docs/checkpoints/LATEST.md`;
3. preserve the exact `preqa.13` candidate/evidence and current local migrations/tests;
4. do not restart historical repair work absent real evidence.

For implementation after owner authorization:

1. refresh both refs;
2. follow the convergence procedure in D-0073 and the technical-readiness checkpoint;
3. validate the integrated baseline before merging to `main`;
4. update this file immediately after topology changes.