# Latest project checkpoint — global resume map

**Updated:** 2026-09-17 (Chile local time)  
**Normal implementation trunk:** `main`  
**Last verified integrated runtime merge:** `8be8ec82702a782c65b2d6aedf9bbe4b5b58f240`  
**PR #67:** MERGED  
**Post-merge Scaffold:** `35279329344` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Wave 7:** ACTIVE  
**Integrated Wave 7 packages:** Desktop Creature/Monster Manager + Desktop NPC Manager + Desktop Homebrew & Rules lightweight local core + Desktop Place/Shop Manager local core  
**Current checkpoint:** `docs/checkpoints/2026-09-17_WAVE7_PLACE_SHOP_MANAGER_INTEGRATED.md`  
**Next bounded package:** Desktop Stage Manager — Place retrieval/organization core  
**Owner implementation authorization:** GRANTED

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-17_WAVE7_PLACE_SHOP_MANAGER_INTEGRATED.md`;
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
post-merge Scaffold                               PASS (35279329344)
        |
        v
Wave 7 Stage Manager — Place retrieval/organization core
```

## Practical continuation

After the short-lived Place/Shop documentation closure merges, start the Stage Manager from current `main`.

Build on the integrated Place/Shop Manager rather than introducing a duplicate Stage persistence family. The existing Place payload already provides kind, summary, area, function, presentation, services, interactives, hooks, player-safe text, DM notes, paper references and tags; reusable-content metadata provides scope/provenance/revision and update timestamps.

Initial Stage slice:

- present Places/Shops as the Stage preparation collection;
- richer retrieval/filter/grouping by kind, area, function, tags, scope and recent updates;
- preserve existing create/open/edit/copy behavior and optimistic revision/tombstone guarantees;
- keep active Campaign context visible;
- make only the smallest refactor needed to share existing Place behavior;
- focused coverage for retrieval/filtering and stable selection/state.

Do not pull Scene Spine into this package. D-0072's lightweight Adventure/Scene Spine remains the following bounded package. Current source has no Scene reusable-content family, so its later persistence extension must be introduced explicitly with that concrete package rather than pre-modeled here.

## Place/Shop Manager integrated evidence

- implementation head `f4bb75f4b2872ebc6a1dc4302cd4890367e4618c`;
- push Scaffold `35278740631` — SUCCESS;
- PR Scaffold `35279052405` — SUCCESS;
- PR #67 merged as `8be8ec82702a782c65b2d6aedf9bbe4b5b58f240`;
- post-merge Scaffold `35279329344` — SUCCESS.

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
