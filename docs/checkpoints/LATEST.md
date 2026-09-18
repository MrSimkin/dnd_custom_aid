# Latest project checkpoint — global resume map

**Updated:** 2026-09-17 (Chile local time)  
**Normal implementation trunk:** `main`  
**Last verified integrated `main`:** `f6350d34087aae55d5247f2ba23153814eeed04b`  
**Latest implementation PR #83:** MERGED  
**Post-merge Scaffold:** `35295050340` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Wave 7:** ACTIVE  
**Integrated Wave 7 repository packages:** Desktop Creature/Monster Manager + Desktop NPC Manager + Desktop Homebrew & Rules lightweight local core + Desktop Place/Shop Manager local core + Desktop Stage Manager retrieval/organization core + lightweight Adventure/Scene Spine + Desktop Dungeon/Zone Manager local core + Desktop Encounter Manager local core + Desktop PC Manager inspection/audit core + PC ownership/controller administration core + PC Sheet PDF Export shared semantic/render-plan foundation  
**Current checkpoint:** `docs/checkpoints/2026-09-17_WAVE7_PC_SHEET_PDF_FOUNDATION_INTEGRATED.md`  
**PC authority DEV deployment:** VERIFIED — Worker version `ccdeca47-7622-4eb7-8dfa-a197d62bf3cb`  
**Next bounded package:** PC Sheet PDF Export — local renderer + authoritative template mapping  
**Owner implementation authorization:** GRANTED

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-17_WAVE7_PC_SHEET_PDF_FOUNDATION_INTEGRATED.md`;
3. `docs/PROJECT_STATE.md`;
4. `docs/BRANCH_STATUS.md`;
5. D-0071, D-0072, D-0073, D-0074 and D-0075;
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
DEV Worker deploy + route/auth-boundary verification COMPLETE / VERIFIED
        |
        v
PC Sheet PDF Export shared semantic/render-plan foundation COMPLETE / INTEGRATED
        |
        v
PC Sheet PDF Export local renderer + authoritative template mapping
```

## Practical continuation

The **PC Sheet PDF Export shared semantic/render-plan foundation is integrated**.

PR #83 added one read-only, platform-neutral export planner over the existing canonical Character Sheet + Closure + Successor data. It defines:

- Permanent vs Current Snapshot selection, with explicit non-destructive fallback when no separate current aggregate is supplied;
- the four D-0074 visual families;
- exact authoritative source-template page mapping for Custom v1 and both Custom v2 first-page alternatives;
- custom-stat projection and the three approved presentation modes;
- overflow destinations without prematurely hard-coding coordinates or typography;
- portrait Crop-to-fill / Fit-entire-image semantics with non-blocking missing-local-asset handling;
- optional Spellbook planning from the spells actually attached to the PC, preserving recorded source relationships and calculated casting values.

No PDF renderer, UI export dialog, persistence migration, backend change or provider action was introduced.

The next bounded package is **PC Sheet PDF Export — local renderer + authoritative template mapping**. This is the stage that begins producing real PDFs from the shared plan.

The owner's mandatory visual gate remains active: when each visual family reaches a reviewable rendering state, a populated example using representative dummy character data must be shown to the owner before that family is considered visually/functionally approved. Custom v1/v2 must use the actual PDFs under `assets/character-sheets/templates/` as authoritative bases rather than unnecessary redraws.

Do **not** invent freeze/unfreeze semantics merely to continue coding.

## PC authority repository integration evidence

- initial implementation head `5de58771702dd2f1f548ca00f066318c630bb622`;
- initial push Scaffold `35290905581` — FAILED in backend/Kotlin compile checks while hosted-database contract passed;
- final/corrected implementation head `418ac19d4d247cfbf19d6fb7f9b158df5c900bdc`;
- corrected push Scaffold `35291183960` — SUCCESS;
- PR Scaffold `35291417403` — SUCCESS;
- PR #79 merged as `2e12400ee18026c702d6727793a3aea5d23d07b4`;
- post-merge Scaffold `35291685597` — SUCCESS;
- docs closure PR #80 merged as `8693f834e9f663fcabcfad33c0c6afc193486ae5`; post-merge Scaffold `35292356409` — SUCCESS;
- DEV Worker deployment — **VERIFIED** by owner execution; Worker version `ccdeca47-7622-4eb7-8dfa-a197d62bf3cb`;
- live checks — `/health` 200, old protected Campaign route 401, new protected PC authority route 401.

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

## PC Sheet PDF foundation integration evidence

- owner-template approval clarification PR #82 merged as `b83097efbb93eab949fc8811068489af15de13c0`;
- implementation branch `wave7/pc-sheet-pdf-export-foundation` final head `dea22b0823d6c4ad54e24839943d5952e2020392`;
- push Scaffold `35294590552` — SUCCESS;
- PR #83 Scaffold `35294792657` — SUCCESS;
- PR #83 merged as `f6350d34087aae55d5247f2ba23153814eeed04b`;
- post-merge Scaffold `35295050340` — SUCCESS;
- provider deployment: not applicable; no Worker/API code changed.
