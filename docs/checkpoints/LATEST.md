# Latest project checkpoint — global resume map

**Updated:** 2026-09-18 (Chile local time)  
**Normal integrated trunk:** `main`  
**Last verified integrated `main`:** `2dc74e2d9c7d853a068e9052ec4928bf5178eb9f`  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Wave 7:** ACTIVE  
**Current active branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**Draft PR:** #85 — OPEN / DRAFT / **DO NOT MERGE**  
**PC Sheet PDF visual QA:** ROUND 1 **REJECTED**  
**Current checkpoint:** `docs/checkpoints/2026-09-18_PDF_PRIMITIVE_QA_OWNER_FEEDBACK_APPLIED.md`

## Read first on resume

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-18_WAVE7_PC_SHEET_PDF_RENDERER_RESEARCH_PAUSE.md`;
3. `docs/checkpoints/2026-09-17_WAVE7_PC_SHEET_PDF_MAIN_PAGE_VISUAL_QA_ROUND1_REJECTED.md`;
4. `docs/checkpoints/2026-09-17_PC_SHEET_PDF_WHOLE_EXPORT_RENDERER_QA_STRATEGY.md`;
5. `docs/checkpoints/2026-09-17_PC_SHEET_PDF_RENDERER_FOUNDATION_RESEARCH.md`;
6. `docs/checkpoints/2026-09-18_REPOSITORY_VISIBILITY_AND_PDF_FONT_ASSET_POLICY.md`;
7. `docs/checkpoints/2026-09-18_OWNER_SYMBOL_FONT_INSPECTION_AND_V2_CANDIDATE.md`;
8. `docs/PROJECT_STATE.md`;
9. `docs/BRANCH_STATUS.md`;
10. D-0071 through D-0075 as relevant.

## Current truth

The PC Sheet PDF shared semantic/render-plan foundation is integrated.

A first local PDFBox/template-overlay proof was implemented on PR #85 and generated reviewable Custom v1/v2 MAIN-page PDFs successfully. CI generation evidence was green, but **owner visual QA rejected the rendering**.

The rejected proof must not be treated as a nearly approved mapping. Problems include:

- horizontal and vertical alignment;
- incorrect typography choices;
- inappropriate one-font treatment;
- text glyphs used as semantic markers;
- insufficient full-page dummy data;
- inadequate whole-page review method.

The owner approved a **slower, whole-export-first renderer strategy**:

```text
full export survey
    -> shared renderer research/design
    -> reusable text/layout/font/marker primitives
    -> primitive QA
    -> all required pages/families
    -> section QA
    -> page QA
    -> family QA
    -> end-to-end export QA
```

Do not resume with isolated coordinate nudging on page 1.

## Symbol-font continuity

The owner created the original `Para-hoja-de-pj` symbol font and provided it for inspection.

A v2 candidate has been investigated locally and may continue to evolve as real renderer requirements appear.

Public-repo reproducibility:

- `scripts/fonts/expand_para_hoja_de_pj_v2.py`;
- `docs/reference/Para_Hoja_de_PJ_Symbols_v2_MAPPING.md`.

No original or generated TTF binary is committed while the repository remains public.

## Next substantive work

The renderer foundation has now moved past the original research-only pause:

1. PDFBox 3.0.8 is settled for this package;
2. metric-based text/layout, wrapping/overflow, vector markers, repeated rows and portrait Fit/Crop primitives exist;
3. Fira Sans / Barlow Condensed Bold / Kalam Bold are bundled as deterministic **QA candidates**, not approved final typography;
4. a dedicated handwritten/script role exists;
5. adaptive vs fixed font-size behavior is explicit;
6. the app-derived single-check / double-check training grammar exists in the vector marker set;
7. `Para-hoja-de-pj` now has versioned repository architecture and a v3 generator/guide; original v1 binary publication is authorized but awaits re-attachment/hash verification.

The immediate gate is **structured owner Primitive QA** focused on typography, per-element scale, condensed-font readability, handwritten treatment and marker variants.

Do not resume broad full-page mapping until that primitive typography/symbol gate is accepted.

## Owner typography feedback applied

The owner accepted the direction so far with changes recorded in `2026-09-18_PDF_TYPOGRAPHY_AND_OWNER_SYMBOL_FONT_FEEDBACK.md`.

Latest fully green implementation Scaffold before this documentation/text correction: `35359508972` at `4d4c2fae3d6b483c5e508886783fda37076f2d81`.

## Mandatory owner gate

No PC Sheet PDF visual family is approved.

PR #85 remains draft and must not merge until the appropriate visual/functional gates are satisfied.

## Owner orchestration alert — DO NOT TRIGGER YET

### Purpose of this alert

The owner is **not expected to remember when parallel development becomes appropriate**, nor to design the technical split himself.

This alert exists so the current Worker explicitly stops at the right moment and tells the owner, in simple language, exactly what to do next.

### When this alert becomes active

Trigger this alert only when **all** of these are true:

- the owner has accepted the current Primitive QA;
- every correction requested during that QA has been applied;
- the shared renderer/primitives foundation is green and stable;
- the foundation is no longer expected to keep changing underneath the next pieces of work;
- the work has reached a clean common starting point for several independent follow-up tasks;
- preferably, that common starting point has already been integrated into `main`. If it has not, the Worker must explicitly confirm that there is still one unambiguous stable starting revision that every future worker can use.

Do **not** trigger this merely because automated tests are green, because Primitive QA has started, or because one visual example looks promising.

If the shared foundation is still being reviewed, corrected, rejected or materially redesigned, continue the current work normally and keep this alert pending.

### What the current Worker must tell the owner

When the conditions above are satisfied, **do not automatically begin the next broad implementation package**.

Tell the owner prominently:

> **SAFE ORCHESTRATION POINT REACHED — this is the moment to introduce the orchestrator.**

Then explain in plain language:

> The common groundwork is now stable. From this point onward, some of the remaining work can be safely divided among several Work chats so they can progress in parallel. You do not need to decide the technical split yourself. The next step is to create one new Work chat whose only initial job is to act as the orchestrator: it will inspect the current repository, decide which tasks can safely run in parallel, prepare the individual worker instructions, and later coordinate bringing their results back together.

### What the owner must do — step by step

The Worker must give these instructions to the owner in this order:

1. **Do not ask the current Worker to continue into the next large development package.** The current stage has reached the handoff point.

2. **Open one new ChatGPT chat in Work mode.** This new chat will be the orchestrator. Do not create all the parallel worker chats yet.

3. **Copy and paste the orchestrator prompt supplied by the current Worker.** The owner should not have to edit technical details, branch names, commit identifiers or file ownership manually.

4. **Let the orchestrator inspect the current repository before dividing anything.** It must reconstruct the real current state from GitHub, not from assumptions or old chat memory.

5. **The orchestrator must then propose the first safe parallel split in simple language.** It must explain to the owner:
   - how many worker chats are useful now;
   - what each worker will do;
   - why those pieces can safely proceed at the same time;
   - what work must remain sequential;
   - what the next consolidation point will be.

6. **Only after that explanation, the orchestrator must provide one complete copy-paste prompt for each worker chat.** The owner can then open the required Work chats and paste those prompts one by one.

7. **Each worker works only on its assigned piece.** The owner should not need to coordinate technical overlap manually. The worker prompts and orchestrator must handle that boundary.

8. **When the parallel workers finish, return to the orchestrator.** The owner should simply tell it that the assigned workers have completed their work. The orchestrator should inspect GitHub itself, verify what actually landed, and coordinate the consolidation/integration step.

9. **After consolidation and verification, the orchestrator decides whether another parallel round is safe.** If so, repeat the same pattern:
   - divide;
   - parallel work;
   - consolidate;
   - verify;
   - divide again.

10. **If the project reaches another owner decision or QA gate, parallel expansion pauses again.** Resolve the shared decision first, establish a new stable common base, and only then fan out again.

The owner is not expected to understand or manually manage Git branching strategy, file ownership, merge ordering or integration mechanics. Those are responsibilities of the orchestrator and technical workers. The owner remains responsible for product choices, visual/UX approval, scope, and other decisions that genuinely require owner judgment.

### Mandatory deliverable when this alert triggers

The current Worker must **not merely say "create an orchestrator."**

In the same message that triggers this alert, it must provide the owner with a **complete, ready-to-copy orchestrator prompt** tailored to the repository state at that moment.

That prompt must tell the future orchestrator to:

- work from the actual current GitHub state and verify it first;
- act as coordinator/integrator rather than immediately implementing everything itself;
- identify dependency boundaries before creating parallel work;
- use separate bounded worker branches/tasks when appropriate;
- prevent two workers from unknowingly changing the same shared foundation;
- keep product/owner decisions with the owner;
- consolidate completed work at sensible checkpoints rather than waiting for one giant final merge;
- verify the combined result before starting the next fan-out;
- explain its plan and owner actions in clear layman terms;
- generate the exact copy-paste prompts the owner needs for each worker;
- stop and ask the owner only when a genuine owner-level decision, QA gate, cost/security issue or other meaningful boundary is reached.

The prompt supplied at trigger time must include the **actual current repository state**, including the correct stable base and current relevant project checkpoint. Do not leave placeholders that require the owner to discover technical information himself.

### Simple mental model for the owner

When the alert triggers, the intended workflow is:

```text
finish shared groundwork
        ↓
YOU receive this alert
        ↓
open ONE new Work chat: the orchestrator
        ↓
orchestrator divides the next work
        ↓
you open the worker chats using its ready-made prompts
        ↓
workers progress in parallel
        ↓
return to orchestrator
        ↓
orchestrator consolidates and verifies
        ↓
repeat when another safe parallel split exists
```

The objective is to reduce elapsed development time **without making the owner become the technical project manager**.

## Permanent safety / operating constraints

- current repo intentionally public during development; owner intends private repo at project completion;
- final application is private/personal-use;
- do not publish proprietary third-party font binaries merely because they can be used locally;
- external-service budget remains USD $0 unless owner changes it;
- never commit secrets/credentials/tokens;
- preserve PC ownership/controller/campaign authority distinctions;
- preserve D-0074 PC export semantics; renderer work must consume, not redefine, the shared plan;
- green automated tests are not visual approval.
