# Latest project checkpoint — global resume map

**Updated:** 2026-09-17 (Chile local time)  
**Normal implementation trunk:** `main`  
**Last verified integrated runtime merge:** `7000535b78df2b2a7149b019796ff3d5903fdb3d`  
**PR #75:** MERGED  
**Post-merge Scaffold:** `35287713130` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Wave 7:** ACTIVE  
**Integrated Wave 7 packages:** Desktop Creature/Monster Manager + Desktop NPC Manager + Desktop Homebrew & Rules lightweight local core + Desktop Place/Shop Manager local core + Desktop Stage Manager retrieval/organization core + lightweight Adventure/Scene Spine + Desktop Dungeon/Zone Manager local core + Desktop Encounter Manager local core  
**Current checkpoint:** `docs/checkpoints/2026-09-17_WAVE7_ENCOUNTER_MANAGER_INTEGRATED.md`  
**Next bounded package:** PC Manager / Audit — Desktop inspection/audit core  
**Owner implementation authorization:** GRANTED

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-17_WAVE7_ENCOUNTER_MANAGER_INTEGRATED.md`;
3. `docs/PROJECT_STATE.md`;
4. `docs/BRANCH_STATUS.md`;
5. D-0071, D-0072, D-0073 and D-0075;
6. `docs/ROADMAP.md`.

## Integrated sequence

```text
Wave 4 Player <-> Server                            COMPLETE / INTEGRATED
Wave 5 Desktop shell/campaign administration       COMPLETE / OWNER-QA PASS / INTEGRATED
Wave 6 reusable-content architecture               COMPLETE / INTEGRATED
Wave 7 Creature/Monster Manager local core         COMPLETE / INTEGRATED
Wave 7 NPC Manager local core                      COMPLETE / INTEGRATED
Wave 7 Homebrew & Rules lightweight local core     COMPLETE / INTEGRATED
Wave 7 Place/Shop Manager local core               COMPLETE / INTEGRATED
Wave 7 Stage Manager retrieval/organization core   COMPLETE / INTEGRATED
Wave 7 Adventure/Scene Spine lightweight core      COMPLETE / INTEGRATED
Wave 7 Desktop Dungeon/Zone Manager local core     COMPLETE / INTEGRATED
Wave 7 Encounter Manager local core                COMPLETE / INTEGRATED
post-merge Scaffold                                PASS (35287713130)
        |
        v
Wave 7 PC Manager / Audit — Desktop inspection/audit core
```

## Practical continuation

Start the **PC Manager / Audit — Desktop inspection/audit core** from current `main` after this short-lived Encounter documentation closure merges.

D-0072 defines Desktop PC Manager as a DM inspection, audit and administration surface over the same canonical PC records used by the Player App. It is **not** a second Desktop character-builder and must not silently impersonate the Player.

Before adding new persistence, inspect the already-integrated PC, authority, history and synchronization surfaces and reuse them wherever they satisfy the approved workflow.

Initial bounded direction:

- campaign PC overview and retrieval;
- complete DM inspection of the existing canonical PC record;
- visible distinction between PC data freshness and synchronization freshness where existing evidence supports it;
- review of meaningful grouped audit/history already available in the project;
- explicit DM correction entry points using the project’s preserved-history/authority rules rather than direct destructive overwrite;
- preserve the separation between campaign membership, PC ownership and PC control;
- focused Desktop/controller tests around inspection, authority and correction boundaries.

Do not turn this first slice into a second character-builder, erase audit history, conflate DM role with PC ownership/control, or add speculative generalized administration infrastructure.

## Encounter Manager integrated evidence

- initial implementation head `91d744a66e3ff18ee9190c41d4dbb3970ca412fe`;
- initial push Scaffold `35286844850` — FAILED on one Kotlin visibility mismatch;
- corrected/final implementation head `a68f62897d6178f1da1c19deb2721ea04abc837a`;
- corrected push Scaffold `35287257508` — SUCCESS;
- PR Scaffold `35287507268` — SUCCESS;
- PR #75 merged as `7000535b78df2b2a7149b019796ff3d5903fdb3d`;
- post-merge Scaffold `35287713130` — SUCCESS.

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
