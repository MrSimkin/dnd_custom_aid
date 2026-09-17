# Latest project checkpoint — global resume map

**Updated:** 2026-09-17 (Chile local time)  
**Normal implementation trunk:** `main`  
**Last verified integrated runtime merge:** `12a62288457ebe5892f90f637fe41c142b094591`  
**PR #61:** MERGED  
**Post-merge Scaffold:** `35267674641` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Wave 7:** ACTIVE  
**Integrated Wave 7 package:** Desktop Creature/Monster Manager local authoring core  
**Current checkpoint:** `docs/checkpoints/2026-09-17_WAVE7_CREATURE_MANAGER_INTEGRATED.md`  
**Next bounded package:** Desktop NPC Manager — local authoring core  
**Owner implementation authorization:** GRANTED

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-17_WAVE7_CREATURE_MANAGER_INTEGRATED.md`;
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
post-merge Scaffold                               PASS (35267674641)
        |
        v
Wave 7 Desktop NPC Manager local authoring core
```

## Practical continuation

After the short-lived Creature Manager documentation closure merges, start the NPC Manager from current `main`.

Reuse the integrated `NpcPayload` and `NpcContentRepository` rather than creating a new persistence model.

Initial slice:

- browse/search Personal + active-Campaign NPCs;
- create/open/edit the existing Quick and Developed NPC fields;
- preserve incomplete NPCs as valid;
- keep combat mechanics optional and reuse `CreaturePayload` when present;
- display scope/provenance/revision;
- explicitly copy Personal NPC -> active Campaign;
- preserve stale-write/tombstone behavior.

Do not pull NPC assistant/AI ideation, import/export, live-improvisation promotion, media/object storage, hosted reusable-content sync or a generalized all-Managers framework into this first NPC slice unless concrete implementation evidence requires it.

## Creature Manager integrated evidence

- implementation head `38d68dc832188f29c76ec40990297f53a85e9bed`;
- push Scaffold `35267065066` — SUCCESS;
- PR Scaffold `35267241770` — SUCCESS;
- PR #61 merged as `12a62288457ebe5892f90f637fe41c142b094591`;
- post-merge Scaffold `35267674641` — SUCCESS.

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
