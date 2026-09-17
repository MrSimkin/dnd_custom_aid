# Latest project checkpoint — global resume map

**Updated:** 2026-09-17 (Chile local time)  
**Normal implementation trunk:** `main`  
**Last verified integrated runtime merge:** `81303bf875457bd1fa0a9ce70d7a4e71eaad9edd`  
**PR #59:** MERGED  
**Post-merge Scaffold:** `35265162945` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Integrated reusable families:** Creature, NPC, Homebrew/Rule, Place, Zone, Encounter  
**Current checkpoint:** `docs/checkpoints/2026-09-17_WAVE6_ENCOUNTER_PAYLOAD_INTEGRATED.md`  
**Current engineering wave:** Wave 7 — Desktop authoring Managers  
**Next bounded package:** Desktop Creature/Monster Manager — local authoring core  
**Owner implementation authorization:** GRANTED

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-17_WAVE6_ENCOUNTER_PAYLOAD_INTEGRATED.md`;
3. `docs/PROJECT_STATE.md`;
4. `docs/BRANCH_STATUS.md`;
5. D-0071, D-0072, D-0073 and D-0075;
6. `docs/ROADMAP.md`.

## Integrated sequence

```text
Wave 4 Player <-> Server                           COMPLETE / INTEGRATED
Wave 5 Desktop shell/campaign administration      COMPLETE / OWNER-QA PASS / INTEGRATED
Wave 6 reusable-content foundation                COMPLETE / INTEGRATED
Wave 6 Creature payload                           COMPLETE / INTEGRATED
Wave 6 NPC payload                                COMPLETE / INTEGRATED
Wave 6 Homebrew/Rule payload                      COMPLETE / INTEGRATED
Wave 6 Place payload                              COMPLETE / INTEGRATED
Wave 6 Zone payload                               COMPLETE / INTEGRATED
Wave 6 Encounter payload                          COMPLETE / INTEGRATED
post-merge Scaffold                               PASS (35265162945)
        |
        v
Wave 7 Desktop Creature/Monster Manager local authoring core
```

## Practical continuation

Finish and integrate the short-lived Encounter documentation closure branch.

Then start Wave 7 from current `main` with a bounded Creature/Monster Manager slice using the already-integrated `CreatureContentRepository` and existing Desktop `MANAGERS` destination.

Initial slice: browse/search Personal + active-Campaign Creatures, create/open/edit the existing Creature payload, display scope/provenance, and explicitly copy Personal -> active Campaign.

Do not pull Official/SRD catalog integration, import/export, Creature Creator Assistant, media/object storage, hosted reusable-content sync or a generalized all-Managers framework into the first slice unless concrete implementation evidence requires it.

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
