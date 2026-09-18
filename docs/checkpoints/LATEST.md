# Latest project checkpoint — global resume map

**Updated:** 2026-09-17 (Chile local time)  
**Normal implementation trunk:** `main`  
**Last verified integrated repository merge:** `2e12400ee18026c702d6727793a3aea5d23d07b4`  
**PR #79:** MERGED  
**Post-merge Scaffold:** `35291685597` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Wave 7:** ACTIVE  
**Integrated Wave 7 repository packages:** Desktop Creature/Monster Manager + Desktop NPC Manager + Desktop Homebrew & Rules lightweight local core + Desktop Place/Shop Manager local core + Desktop Stage Manager retrieval/organization core + lightweight Adventure/Scene Spine + Desktop Dungeon/Zone Manager local core + Desktop Encounter Manager local core + Desktop PC Manager inspection/audit core + PC ownership/controller administration core  
**Current checkpoint:** `docs/checkpoints/2026-09-17_WAVE7_PC_AUTHORITY_REPO_INTEGRATED_PROVIDER_PENDING.md`  
**Immediate gate:** deploy and verify the merged DEV Worker authority route  
**Next code package after provider closure:** PC Sheet PDF Export — shared semantic/render-plan foundation  
**Owner implementation authorization:** GRANTED

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-17_WAVE7_PC_AUTHORITY_REPO_INTEGRATED_PROVIDER_PENDING.md`;
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
Wave 7 PC Manager inspection/audit core             COMPLETE / INTEGRATED
Wave 7 PC authority administration repository core   COMPLETE / MERGED
post-merge Scaffold                                PASS (35291685597)
        |
        v
DEV Worker deploy + live verification               REQUIRED / PENDING
        |
        v
PC Sheet PDF Export shared semantic/render-plan foundation
```

## Practical continuation

The **PC Manager ownership/controller administration repository package is merged and green**, but the new hosted route is not yet declared operational in DEV because the Cloudflare Worker has not been redeployed from this merged code.

Immediate bounded gate:

- deploy the existing DEV Worker `dnd-custom-aid-api` from current `main@2e12400ee18026c702d6727793a3aea5d23d07b4`;
- verify the deployed Worker still passes `/health`;
- verify the authority route exists and preserves fail-closed authentication/authorization behavior;
- do not paste provider credentials or tokens into chat/Git;
- hard external-service budget remains USD $0.

Repository evidence already integrated:

- DM-only hosted authority mutation;
- owner/controller assignment remains independent and nullable;
- every non-null target must be an active member of the same campaign;
- non-DM, inactive/cross-campaign, missing-PC and tombstoned-PC cases fail closed;
- authority mutation does not alter PC snapshot content or revision;
- explicit nulls are preserved on the hosted wire for intentional unassignment;
- authoritative hosted response drives local `PcAuthority` convergence;
- Desktop PC Manager exposes explicit property/control administration.

Once DEV deployment/verification is closed, the next ready implementation package should be **PC Sheet PDF Export — shared semantic/render-plan foundation** under D-0074. D-0074 is fully product-defined and local/offline by design.

Do **not** invent freeze/unfreeze semantics merely to continue coding. D-0072 requires that capability, but the current repository has no freeze field/contract and the approved records do not define what freezing must block. That product behavior needs an explicit definition before implementation. Broader lifecycle/duplication can be sequenced separately.

## PC authority repository integration evidence

- initial implementation head `5de58771702dd2f1f548ca00f066318c630bb622`;
- initial push Scaffold `35290905581` — FAILED in backend/Kotlin compile checks while hosted-database contract passed;
- final/corrected implementation head `418ac19d4d247cfbf19d6fb7f9b158df5c900bdc`;
- corrected push Scaffold `35291183960` — SUCCESS;
- PR Scaffold `35291417403` — SUCCESS;
- PR #79 merged as `2e12400ee18026c702d6727793a3aea5d23d07b4`;
- post-merge Scaffold `35291685597` — SUCCESS;
- DEV Worker deployment/verification — **PENDING** because provider deployment access is not available in the current execution environment.

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
