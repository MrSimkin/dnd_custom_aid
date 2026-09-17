# Latest project checkpoint — global resume map

**Updated:** 2026-09-17 (Chile local time)  
**Normal implementation trunk:** `main`  
**Last verified integrated runtime merge:** `a99f03bf53637494695cc39b39d077ea1ef61ada`  
**PR #69:** MERGED  
**Post-merge Scaffold:** `35280636549` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Wave 7:** ACTIVE  
**Integrated Wave 7 packages:** Desktop Creature/Monster Manager + Desktop NPC Manager + Desktop Homebrew & Rules lightweight local core + Desktop Place/Shop Manager local core + Desktop Stage Manager place retrieval/organization core  
**Current checkpoint:** `docs/checkpoints/2026-09-17_WAVE7_STAGE_MANAGER_INTEGRATED.md`  
**Next bounded package:** Adventure/Scene Spine — lightweight local core  
**Owner implementation authorization:** GRANTED

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-17_WAVE7_STAGE_MANAGER_INTEGRATED.md`;
3. `docs/PROJECT_STATE.md`;
4. `docs/BRANCH_STATUS.md`;
5. D-0071, D-0072, D-0073 and D-0075;
6. `docs/ROADMAP.md`.

## Integrated sequence

```text
Wave 4 Player <-> Server                           COMPLETE / INTEGRATED
Wave 5 Desktop shell/campaign administration      COMPLETE / OWNER-QA PASS / INTEGRATED
Wave 6 reusable-content architecture              COMPLETE / INTEGRATED
Wave 7 Creature/Monster Manager local core        COMPLETE / INTEGRATED
Wave 7 NPC Manager local core                     COMPLETE / INTEGRATED
Wave 7 Homebrew & Rules lightweight local core    COMPLETE / INTEGRATED
Wave 7 Place/Shop Manager local core              COMPLETE / INTEGRATED
Wave 7 Stage Manager retrieval/organization core  COMPLETE / INTEGRATED
post-merge Scaffold                               PASS (35280636549)
        |
        v
Wave 7 Adventure/Scene Spine — lightweight local core
```

## Practical continuation

After the short-lived Stage documentation closure merges, start the Adventure/Scene Spine package from current `main`.

Source inspection confirms Scene is not already implemented: there is no `SCENE` reusable-content family, Scene payload repository or Scene SQL schema. The generic reusable-content spine is family-agnostic and current payload migrations run through `24.sqm` for Encounter.

Initial Scene package principles:

- lightweight orientation, not a quest engine;
- title/display name and purpose/summary;
- possible next Scenes;
- only the lightweight references needed by the concrete preparation/navigation flow;
- Personal/Campaign scope and explicit independent Personal -> Campaign copy using existing reusable-content semantics;
- optimistic revisions, stale-write rejection and tombstone/non-resurrection;
- minimal Desktop browse/create/edit/copy surface;
- focused migration/persistence/revision/copy coverage.

Do not pre-model clocks, media storage, generalized dependency graphs or live-state orchestration. Add only the minimum persistence/schema surface that the concrete Scene package requires.

## Stage Manager integrated evidence

- implementation head `a01eb18a8385807d973c9f2059ff32779c0f6897`;
- push Scaffold `35280359257` — SUCCESS;
- PR Scaffold `35280486923` — SUCCESS;
- PR #69 merged as `a99f03bf53637494695cc39b39d077ea1ef61ada`;
- post-merge Scaffold `35280636549` — SUCCESS.

## Operating rule

Routine green engineering boundaries do not require owner confirmation. Continue autonomously through branch/commit/CI/PR/merge/post-merge/docs closure while scope and risk remain unchanged. Stop only for a real failure/judgment issue, owner product/scope/risk decision, provider/manual action, destructive/cost/security ambiguity, or genuine async wait with nothing safe/useful left to do.

## Permanent safety rules

- repository intentionally public;
- hard external-service budget USD $0;
- never request/paste/commit secrets, credentials, OTPs or session/access/refresh tokens;
- preserve historical identity/audit evidence;
- normal DEV owner/DM identity is Outlook-backed; Gmail is historical/inactive by default;
- preserve stable identity, revisions/stale-write rejection, idempotency, tombstones/non-resurrection and explicit conflicts;
- DM authority is not PC ownership;
- do not redeploy Cloudflare unless Worker code materially changes or new evidence requires it;
- object-storage provider selection remains deferred until a concrete Media/Handouts requirement;
- do not run `npm audit fix --force` blindly.
