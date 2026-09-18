# Latest project checkpoint — global resume map

**Updated:** 2026-09-18 (Chile local time)  
**Normal integrated trunk:** `main`  
**Last verified integrated `main`:** `2dc74e2d9c7d853a068e9052ec4928bf5178eb9f`  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Wave 7:** ACTIVE  
**Current active branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**Draft PR:** #85 — OPEN / DRAFT / **DO NOT MERGE**  
**Primitive PDF QA:** **OWNER ACCEPTED**  
**Full PC Sheet PDF visual QA:** PENDING — no visual family approved  
**Para Hoja de PJ v4 visual QA:** PENDING  
**Current checkpoint:** `docs/checkpoints/2026-09-18_PARA_HOJA_DE_PJ_V4_PUBLISHED_PRIMITIVE_QA_ACCEPTED.md`

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

The exact owner-authored 2007 `Para Hj De Pj.ttf` is now published as immutable archival v1 after byte-level verification:

- SHA-256 `d0c0d50ad8ed33064e3af89b0976933b9eba8ead2d26234011c1a3233c246658`;
- Git blob `67cf8eb51dbcee7b4c68c8ef7e9e238ce010271c`;
- publication commit `87525f4f3648343a76c0192bc50a74e08740b396`.

v2/v3 remain preserved historical renderer candidates.

The current v4 redesign candidate is also published:

- compiled TTF Git blob `cfc04ea4fb0ce53e8ee41c72df7e6281ef329c4a`;
- deterministic source/builder at `scripts/fonts/para-hoja-de-pj/v4/build_para_hoja_de_pj_v4.py`;
- complete mapping/use guide at `assets/fonts/owner/para-hoja-de-pj/v4/GUIDE.md`;
- local deterministic rebuild matches the committed v4 byte-for-byte.

v4 preserves historical A-E/a-e meanings, adds coordinated legacy-refined and modern-clean families, renderer PUA aliases and the app grammar **Competent = one check; Expertise/Pericia = double check**.

v4 is technically validated but **not yet owner-approved visually**.

## Next substantive work

The shared renderer primitives have passed the owner Primitive PDF QA gate. Renderer work may now proceed beyond that gate.

Current bounded continuation:

1. present and resolve the separate Para Hoja de PJ v4 visual specimen gate;
2. continue complete populated rendering across Classic, Custom v1, Custom v2 per Attribute and Custom v2 per Ability using the accepted primitive foundation;
3. keep typography candidates/family choices subject to their remaining visual QA;
4. perform structured section -> page -> family -> end-to-end QA;
5. keep PR #85 draft until the remaining visual/functional gates are satisfied.

The v4 font review does not re-close the renderer Primitive QA gate. If v4 artwork needs changes after publication, preserve v4 and create the next versioned derivative rather than silently replacing the published binary.

## Owner Primitive QA accepted

The owner accepted the Primitive PDF QA after the recorded typography/marker corrections. The accepted foundation includes metric-based text/layout, explicit adaptive vs fixed sizing, repeated rows, portrait Fit/Crop behavior, deterministic typography candidates, vector markers and the single/double-check training grammar.

The exact owner font and v4 publication state is recorded in `2026-09-18_PARA_HOJA_DE_PJ_V4_PUBLISHED_PRIMITIVE_QA_ACCEPTED.md`.

A final Scaffold run on the synchronized publication/documentation head is still required before treating this state as clean/stable automation evidence.

## Mandatory owner gate

No complete PC Sheet PDF visual family is approved.

The immediate owner decision is the **Para Hoja de PJ v4 visual specimen**. Technical reproducibility does not approve its artwork.

PR #85 remains draft and must not merge until the remaining visual/functional gates are satisfied.

## Owner orchestration alert — DO NOT TRIGGER YET

Primitive QA is now accepted, but this alert remains pending while the newly published v4 artwork is at its explicit owner visual gate and the final synchronized Scaffold evidence has not yet been confirmed. Do not fan out on top of a font candidate that may still require a versioned follow-up.

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
