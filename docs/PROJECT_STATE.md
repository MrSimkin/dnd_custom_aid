# Project State

**Last verified:** 2026-08-29  
**Canonical branch:** `main`  
**Current working branch:** none  
**Open review:** none  
**Phase:** Phase 2 — Technical Options and Foundation / Architecture & Technology Evaluation  
**Status:** Phase 1 is complete. PR #2 was owner-approved and merged into `main` at `b5a059b8e7fb9312232ad684356af05e27331b65`. The approved MVP is multicampaign. Architecture evaluation is now active; no application architecture, technology stack, framework, provider, or application code has been selected/scaffolded yet.

## 1. Current product baseline

`dnd_custom_aid` is a personal/small-scale tabletop RPG assistant beginning with D&D.

Approved product shape:

- Android phone/tablet is the primary at-the-table/live-use surface.
- Desktop/laptop is an intentionally narrower DM preparation/administration companion using the same shared campaign/domain data.
- The MVP is **multicampaign**.
- Paper is normally the authoritative live character surface; the latest intentionally reconciled digital character is the durable backup/reference baseline and exposes freshness/last-update information.
- Campaigns may mix D&D 5e/SRD 5.1, D&D 5.5e/SRD 5.2.1 and homebrew; the application is not a rules enforcer.
- MVP rules clarification is official-SRD-only, may use both supported SRDs, answers in Spanish, and preserves source/version provenance.
- Monster records are complete for human use while mechanics are selectively structured and future additive enrichment must remain possible.
- Live combat is local-first and DM-authoritative: DM actions commit locally first, hosted sync is secondary/opportunistic, and older remote state must not overwrite newer authoritative DM state.
- Player offline combat-view edits are provisional and yield to authoritative DM state on reconnection.
- Campaign moderation and global application administration are separate authority layers.
- Campaign invitations are campaign-scoped, reusable until revoked/regenerated, and permit direct join without a second DM approval step.

Detailed authoritative behavior lives in `docs/PRODUCT.md` and approved decisions through D-0033 in `docs/DECISIONS.md`.

## 2. Phase 1 closure

Phase 1 — Product Discovery and Design is **complete**.

The owner explicitly resolved the final eight product tensions before merge:

1. Android live use vs desktop administration;
2. multicampaign MVP scope;
3. mixed/homebrew campaigns vs official-SRD-only MVP clarification;
4. complete monster records vs selective structured mechanics;
5. paper live authority vs durable digital state;
6. local-first DM combat vs hosted/shared data;
7. campaign moderation vs global account administration;
8. invitation lifecycle and rejoining.

The final audit found no remaining product-level contradiction or behavioral ambiguity from the identified Phase 1 set. D-0009 remains intentionally pending because it is the Phase 2 architecture/technology decision, not unresolved product behavior.

PR #2 was merged by normal merge commit:

- PR: #2 — `Capture and refine initial product discovery without premature implementation`;
- merged: 2026-08-29;
- merge commit: `b5a059b8e7fb9312232ad684356af05e27331b65`;
- pre-merge branch state: 59 commits ahead, 0 behind `main`;
- pre-merge GitHub status: `mergeable: true`.

## 3. Current technical state

No application code exists yet.

No choice has been made for:

- overall application topology;
- Android language/framework/UI toolkit or minimum Android version;
- desktop/laptop administration implementation form;
- local persistence technology;
- synchronization architecture;
- hosted backend/database provider;
- authentication provider;
- PDF generation/rendering technology;
- SRD storage/retrieval/clarification implementation;
- build system, project/module layout, test stack, or CI.

Named technologies mentioned during discovery, including Neon/Postgres, remain candidates only and are **not approved project choices**.

## 4. Phase 2 objective

Phase 2 is now **active**.

The objective is to evaluate realistic architecture/technology alternatives against the approved product baseline, explain trade-offs and recommendations to the owner, obtain approval for consequential choices, record those choices in Git, and only then scaffold implementation.

Implementation/scaffolding is still blocked until the required architecture decisions are approved.

## 5. Architecture evaluation order

Begin with **overall application topology and surface relationship**, not with individual framework names.

Recommended sequence:

1. overall Android + desktop/laptop topology and shared-domain relationship;
2. Android client approach;
3. desktop/laptop administration delivery approach;
4. multicampaign domain/data boundaries;
5. local-first combat persistence, authority, synchronization and reconciliation;
6. hosted backend/database/authentication/authorization/moderation boundaries;
7. PDF generation/rendering;
8. SRD corpus storage/retrieval/clarification and provenance;
9. testing/build/CI and durable project/module conventions.

## 6. Immediate next decision

The next owner-facing architecture question is **overall application topology**.

Compare realistic whole-system shapes before choosing specific technologies. At minimum, evaluation should consider variants such as:

- Android primary client + browser-based desktop administration + shared hosted backend;
- a shared-code/cross-platform client strategy where justified;
- Android primary client + native desktop administration + shared hosted backend;
- another topology only if it materially fits the approved requirements better.

Evaluation must consider:

- Android phone/tablet live-use quality;
- desktop administration comfort without requiring parity;
- multicampaign shared data and permissions;
- local-first authoritative DM combat;
- player public-projection sync and offline reconciliation;
- authentication/security/moderation boundaries;
- PDF generation;
- SRD-only Spanish clarification and provenance;
- personal-scale/no-cost hosting practicality;
- maintainability, testability and AI-assisted development;
- migration cost and future extensibility without speculative scope creep.

Do **not** select Kotlin, Compose, Flutter, React Native, Electron, Tauri, Firebase, Supabase, Neon/Postgres, a web framework, PDF library, AI provider, or any other consequential implementation technology before the relevant architecture comparison and owner approval.

## 7. Handoff

A fresh agent should read, in order, `README.md`, `AGENTS.md`, `MANIFEST.md`, this file, `docs/DECISIONS.md`, `docs/PRODUCT.md`, `docs/ROADMAP.md`, `docs/WORKFLOW.md`, `docs/ARCHITECTURE.md`, `docs/TESTING.md`, and relevant discovery notes only when historical rationale is needed.

Treat Phase 1 product decisions as closed unless a genuinely new requirement or contradiction emerges. The active task is Phase 2 architecture evaluation, starting with overall topology. Consequential choices remain owner-controlled.