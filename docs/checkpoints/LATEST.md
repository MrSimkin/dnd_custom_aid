# Latest project checkpoint — global resume map

**Updated:** 2026-09-17 (Chile local time)  
**Normal implementation trunk:** `main`  
**Last verified integrated runtime merge:** `a84a8857102806f8a9ac588545167d697ea8a311`  
**PR #71:** MERGED  
**Post-merge Scaffold:** `35282483851` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Wave 7:** ACTIVE  
**Integrated Wave 7 packages:** Desktop Creature/Monster Manager + Desktop NPC Manager + Desktop Homebrew & Rules lightweight local core + Desktop Place/Shop Manager local core + Desktop Stage Manager retrieval/organization core + lightweight Adventure/Scene Spine  
**Current checkpoint:** `docs/checkpoints/2026-09-17_WAVE7_SCENE_SPINE_INTEGRATED.md`  
**Next bounded package:** Desktop Dungeon/Zone Manager — local Zone Brief authoring core  
**Owner implementation authorization:** GRANTED

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-17_WAVE7_SCENE_SPINE_INTEGRATED.md`;
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
Wave 7 Adventure/Scene Spine lightweight core     COMPLETE / INTEGRATED
post-merge Scaffold                               PASS (35282483851)
        |
        v
Wave 7 Desktop Dungeon/Zone Manager — local Zone Brief authoring core
```

## Practical continuation

After the short-lived Scene documentation closure merges, start the Dungeon/Zone Manager from current `main`.

Existing Zone persistence is already integrated from Wave 6 and is rich enough for a useful first Desktop Manager without a schema migration. The current `ZonePayload` contains summary, area, presentation, space, exploration, interactives, clues, checks, consequences, encounter brief, DM guidance, player-safe text, paper references and tags.

Initial Dungeon/Zone slice:

- Personal + active-Campaign Zone browse/search/create/open/edit;
- editor framing around the approved **PRESENTAR / INTERACTUAR / ENCUENTRO** Zone Brief groupings while retaining all existing richer fields;
- area/tag/search retrieval;
- explicit independent Personal -> Campaign copy with provenance;
- atomic display-name + payload save under one optimistic revision;
- stale-write rejection and tombstone/non-resurrection;
- focused controller/repository coverage;
- integrate into the existing Desktop Managers surface with active Campaign context visible.

The current `space` / `exploration` fields may carry flow/topology preparation in this bounded slice. Do not introduce tactical geometry, VTT maps, generalized graph infrastructure, clocks/readiness, automated fictional consequences or provider work unless a concrete later requirement justifies them.

## Scene Spine integrated evidence

- implementation head `face568e7985125975731fef5e275e333ef79b9d`;
- push Scaffold `35281913166` — SUCCESS;
- PR Scaffold `35282188319` — SUCCESS;
- PR #71 merged as `a84a8857102806f8a9ac588545167d697ea8a311`;
- post-merge Scaffold `35282483851` — SUCCESS.

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
