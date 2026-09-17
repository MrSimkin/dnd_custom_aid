# Latest project checkpoint — global resume map

**Updated:** 2026-09-17 (Chile local time)  
**Normal implementation trunk:** `main`  
**Last verified integrated runtime merge:** `6febe3f936593999834189b92aeda9d209385fa7`  
**PR #65:** MERGED  
**Post-merge Scaffold:** `35277359425` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Wave 7:** ACTIVE  
**Integrated Wave 7 packages:** Desktop Creature/Monster Manager + Desktop NPC Manager + Desktop Homebrew & Rules lightweight local authoring cores  
**Current checkpoint:** `docs/checkpoints/2026-09-17_WAVE7_HOMEBREW_RULES_MANAGER_INTEGRATED.md`  
**Next bounded package:** Desktop Place/Shop Manager — local authoring core  
**Owner implementation authorization:** GRANTED

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-17_WAVE7_HOMEBREW_RULES_MANAGER_INTEGRATED.md`;
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
post-merge Scaffold                               PASS (35277359425)
        |
        v
Wave 7 Place/Shop Manager — local authoring core
```

## Practical continuation

After the short-lived Homebrew & Rules documentation closure merges, start the Place/Shop Manager from current `main`.

Reuse the integrated Wave 6 Place persistence family rather than creating a new family. D-0072 treats Shops as specialized Places; first expose the existing Place model and add Shop-specific behavior only if the persisted payload already supports it or concrete implementation evidence requires a bounded extension.

Initial slice:

- browse/search Personal + active-Campaign Places;
- create/open/edit the existing Place payload;
- display scope/provenance/revision;
- explicitly copy Personal -> active Campaign as an independent object;
- save display name + payload atomically under one optimistic revision;
- preserve stale-write/tombstone semantics and the same conservative Personal-owner identity rule used by the preceding Managers;
- focused controller coverage for persistence, atomic edit, stale-write rejection, provenance and copy independence.

Do not pull Scene Spine into this first package. Scene persistence is not yet an integrated reusable-content family and belongs with a later concrete Stage/Scene package rather than speculative pre-modeling.

## Homebrew & Rules Manager integrated evidence

- implementation head `17f4cd686114f734ab0bc50f453de9d579a52c79`;
- push Scaffold `35272269481` — SUCCESS;
- PR Scaffold `35272560206` — SUCCESS;
- PR #65 merged as `6febe3f936593999834189b92aeda9d209385fa7`;
- post-merge Scaffold `35277359425` — SUCCESS.

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
