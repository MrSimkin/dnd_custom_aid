# Phase 4 — revised ending batch plan before owner QA

**Date:** 2026-09-04  
**Status:** Owner-approved operational correction; active  
**Working line:** `implementation/phase4-preqa-consolidation`  
**Audit safety branch:** `tmp/phase4-m-audit-safety`  
**Prior durable closure baseline:** `implementation/phase4-character-closure` at `c486df837411107d900331649caf89f1cb642984`  
**Historical frozen L candidate:** `tmp/phase4-l-frozen-qa-candidate` at `5cc034d3fdf4c25d935bd698aeaf2a3f9e427f27` — preserve unchanged  
**Canonical `main`:** preserve unchanged until Batch P

## 1. Why the ending sequence changed

The approved 2026-09-03 execution plan ended at Batch M owner real-device QA. There was no historical Batch N.

Before beginning that QA, the owner identified three priority closure concerns that should be resolved before spending time accepting a final APK:

1. verify that all discussed/approved Phase 4 implementation scope is actually complete rather than relying only on batch-ledger status;
2. audit the codebase for maintainability risks such as oversized mixed-responsibility files, duplication, deprecated/dead/transitional code, redundant paths and avoidable spaghetti risk, and apply only justified pre-QA remediation;
3. reorganize/reconcile repository documentation and then consolidate the authoritative project state into `main` before freezing the final QA candidate.

This changes execution order, not product scope. No DM implementation is authorized by this correction.

The previously frozen Batch L APK remains valid historical technical evidence, but it is no longer the intended final owner-QA candidate. The frozen L branch must not be patched or rewritten.

## 2. Revised Phase 4 ending sequence

### Batch M — closure completeness + code-health audit

**Purpose:** prove what is complete, what is missing, and what technical cleanup is justified before QA.

M is analysis/audit first. It must not silently change production behavior.

#### M1 — scope traceability

Trace the authoritative Phase 4 closure requirements to implementation evidence. At minimum cover:

- owner requirements in D-0047;
- F01–F18;
- D01–D18;
- I01–I22;
- conditional module rules;
- migration/data-preservation requirements;
- phone/tablet, portrait/landscape, IME, Back, unsaved-state and responsive invariants;
- own-format backup/import and reconciliation;
- protected pre-closure behavior documented by the controlling project state.

For each requirement classify:

- implemented and evidenced;
- implemented but evidence/test coverage is weak;
- partial;
- missing;
- intentionally deferred/non-goal.

A batch/checkpoint name alone is supporting evidence, not sufficient proof when implementation can be inspected directly.

#### M2 — code-health/static architecture audit

Inspect at minimum:

- oversized/mixed-responsibility Android UI files;
- overlapping `*TabV4` / `*ClosureV4` / module paths;
- duplicated UI/editor/state logic;
- domain/business logic embedded in UI where it creates maintenance risk;
- repository/persistence responsibility concentration;
- dead/unreachable/superseded/transitional code;
- deprecated APIs or explicit deprecation markers;
- TODO/FIXME/temporary compatibility paths;
- duplicated constants/models/helpers;
- tests that protect behavior needed for safe refactoring;
- boundaries that Phase 5/DM work would need to consume safely.

Large-file size alone is not a defect. Findings must be tied to concrete mixed responsibility, duplication, risk or unnecessary code.

#### M3 — remediation classification

Every finding is classified as one of:

- **N-blocking:** fix before final QA because it represents missing approved scope, correctness/data risk, material maintainability risk, or an unsafe foundation for the next phase;
- **N-safe cleanup:** small, low-risk cleanup worth doing before the final baseline;
- **defer:** cosmetic/preference cleanup or high-churn refactor with no demonstrated pre-QA benefit;
- **retain intentionally:** compatibility/history/architecture that looks redundant but has a documented reason to remain.

**M exit gate:** a durable traceability + code-health report defines exact Batch N scope. No speculative broad rewrite.

### Batch N — focused remediation/refactor

**Purpose:** implement only evidence-backed findings accepted from Batch M.

Rules:

- preserve product behavior unless M demonstrated missing/incorrect approved behavior;
- preserve database/migration and backup compatibility;
- do not refactor merely to reduce line count;
- prefer extraction of coherent responsibilities and removal of proven redundancy/dead paths over architecture replacement;
- add/strengthen tests before or with risky structural changes;
- no DM functionality and no unrelated feature expansion;
- complete proportionate focused gates after each meaningful slice, then a full gate at N closure.

If M finds no justified production-code remediation, Batch N may close as `NOT REQUIRED`; it still receives an explicit checkpoint.

### Batch O — documentation/governance reorganization

**Purpose:** make repository truth discoverable without deleting historical evidence.

At minimum:

- establish one clear documentation entry point/map;
- distinguish current authoritative docs from historical checkpoints/handoffs/superseded material;
- reconcile `docs/DECISIONS.md` with detailed decision files;
- document existing duplicate historical decision numbers without silently renumbering issued records;
- reconcile README, MANIFEST, PROJECT_STATE, ROADMAP, TESTING, WORKFLOW and architecture/product references;
- preserve Git history and useful historical evidence;
- make exact continuation instructions unambiguous for a fresh chat/agent.

### Batch P — full regression + authoritative `main` consolidation

**Purpose:** prove the cleaned/reorganized Phase 4 release-candidate baseline and make it the authoritative integrated project state.

Before changing `main`:

- full shared/Kotlin tests;
- migration regressions including owner schema lineage;
- Android debug assemble;
- Desktop build;
- backend install/type-check standard gate;
- unique-commit/merge-boundary audit;
- verify no required accepted implementation exists only on an orphaned temporary branch;
- verify documentation points to the exact integrated state.

After the gate, consolidate the resulting authoritative pre-QA state into `main` using a safe auditable merge/update. `main` at this point means **Phase 4 release candidate / awaiting owner QA**, not owner-accepted Phase 4 completion.

### Batch Q — new frozen final QA candidate

Freeze one exact candidate from the consolidated authoritative state and record:

- commit/tree;
- workflow run;
- artifact name/ID;
- artifact ZIP SHA-256;
- extracted APK SHA-256.

No silent code changes after Q freeze. Any product-code repair invalidates Q and requires a new candidate identity.

### Batch R — owner real-device QA

Run the owner QA matrix on the Q candidate:

- upgrade over the prior owner installation/data before any clean-install check;
- phone portrait;
- phone landscape;
- tablet portrait;
- tablet landscape;
- representative larger text;
- migration/data preservation;
- core tabs and conditional modules;
- IME/actions/Back/unsaved-state behavior;
- responsive/master-detail/Supercompact/Table mode;
- backup export/import as a new copy without overwrite;
- blocking layout/scroll/rotation/input regressions.

Blocking findings create a focused repair batch, full gate and new Q candidate identity before affected QA evidence is repeated.

### Batch S — Phase 4 acceptance/closure

Phase 4 closes only after:

- Batch R is owner-accepted;
- any blockers are repaired and re-QA'd;
- exact accepted candidate identity is recorded;
- final continuity status is updated;
- owner explicitly accepts Phase 4 closure.

Only after S may DM-focused implementation begin.

## 3. Freeze and branch rules during the revised ending sequence

- Do not modify `tmp/phase4-l-frozen-qa-candidate`; it is historical evidence.
- Do not treat the old L APK as the intended final QA target.
- Do not modify `main` before Batch P.
- Audit work begins from `implementation/phase4-preqa-consolidation`; risky audit/documentation staging may use temporary safety branches.
- Product-code changes resulting from M belong to Batch N, not to the audit itself.
- Never delete migrations or rewrite historical migration meaning merely to simplify the tree.
- Do not delete historical branches/checkpoints before the merge-boundary/unique-commit audit establishes they contain no unique required material.

## 4. Exact next action

Batch M is ACTIVE.

Start with M1 scope traceability and M2 static architecture/code-health evidence on the current durable Phase 4 implementation. Do not begin owner device QA and do not make production-code changes until M has classified the findings and defined the exact Batch N scope.
