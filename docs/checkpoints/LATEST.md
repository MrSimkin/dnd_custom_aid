# Latest project checkpoint — global resume map

**Updated:** 2026-09-17 (Chile local time)  
**Normal implementation trunk:** `main`  
**Last verified integrated runtime merge:** `58a565c3a33a433ce47e7fd4ac1185b5f980644f`  
**PR #63:** MERGED  
**Post-merge Scaffold:** `35270643883` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Wave 7:** ACTIVE  
**Integrated Wave 7 packages:** Desktop Creature/Monster Manager + Desktop NPC Manager local authoring cores  
**Current checkpoint:** `docs/checkpoints/2026-09-17_WAVE7_NPC_MANAGER_INTEGRATED.md`  
**Next bounded package:** Desktop Homebrew & Rules Manager — lightweight rules local authoring core  
**Owner implementation authorization:** GRANTED

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-17_WAVE7_NPC_MANAGER_INTEGRATED.md`;
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
post-merge Scaffold                               PASS (35270643883)
        |
        v
Wave 7 Homebrew & Rules Manager — lightweight rules local core
```

## Practical continuation

After the short-lived NPC Manager documentation closure merges, start the Homebrew & Rules Manager from current `main`.

Reuse the integrated `HomebrewRulePayload` and `HomebrewRuleContentRepository` rather than creating a new persistence family.

Initial slice:

- browse/search Personal + active-Campaign Homebrew/Rule records;
- create/open/edit title plus summary/body/category/rationale/examples/related references/tags/notes;
- expose the existing Draft / Active / Retired lifecycle;
- display scope/provenance/revision;
- explicitly copy Personal -> active Campaign as an independent object;
- save display name + payload atomically under one optimistic revision;
- preserve stale-write/tombstone semantics and the same conservative Personal-owner identity rule used by Creature/NPC Managers.

Do not pull structured races/classes/subclasses/backgrounds/feats/spells/items, official/SRD customization, import/export, homebrew-aware AI, media/object storage, hosted reusable-content sync or a generalized all-Managers framework into this first Homebrew slice unless concrete implementation evidence requires it.

## NPC Manager integrated evidence

- implementation head `58b680e71ec59c871854eb9c083ff2bc6906fe88`;
- push Scaffold `35269013875` — SUCCESS;
- PR Scaffold `35269163375` — SUCCESS;
- PR #63 merged as `58a565c3a33a433ce47e7fd4ac1185b5f980644f`;
- post-merge Scaffold `35270643883` — SUCCESS.

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
