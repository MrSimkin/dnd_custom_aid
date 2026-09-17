# Latest project checkpoint — global resume map

**Updated:** 2026-09-17 (Chile local time)  
**Normal implementation trunk:** `main`  
**Last verified integrated runtime merge:** `5be90a994f453e5444ecf00762cd72407cfe790a`  
**PR #73:** MERGED  
**Post-merge Scaffold:** `35285629973` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Wave 7:** ACTIVE  
**Integrated Wave 7 packages:** Desktop Creature/Monster Manager + Desktop NPC Manager + Desktop Homebrew & Rules lightweight local core + Desktop Place/Shop Manager local core + Desktop Stage Manager retrieval/organization core + lightweight Adventure/Scene Spine + Desktop Dungeon/Zone Manager local core  
**Current checkpoint:** `docs/checkpoints/2026-09-17_WAVE7_DUNGEON_ZONE_MANAGER_INTEGRATED.md`  
**Next bounded package:** Encounter Manager / Encounter Creator — local authoring core  
**Owner implementation authorization:** GRANTED

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-17_WAVE7_DUNGEON_ZONE_MANAGER_INTEGRATED.md`;
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
post-merge Scaffold                                PASS (35285629973)
        |
        v
Wave 7 Encounter Manager / Encounter Creator — local authoring core
```

## Practical continuation

Start the Encounter Manager / Encounter Creator from current `main` after this short-lived documentation closure merges.

Existing Encounter persistence is already integrated from Wave 6 and should remain the foundation for the first Desktop Manager. `EncounterPayload` contains summary, environment, context, DM guidance, participants, tags and notes.

Encounter participants already support optional Creature/NPC source content IDs or a free-text label, quantity, `EXPECTED` / `RESERVE` / `CONDITIONAL` readiness, condition text, overrides and notes. The repository validates source family/scope and its Personal -> Campaign copy performs domain-specific Creature/NPC dependency copy/remapping while deduplicating repeated dependencies.

Initial Encounter slice:

- Personal + active-Campaign Encounter browse/search/create/open/edit;
- participant editing using the existing Creature/NPC dependency model plus label-only participants;
- expose quantity/readiness/condition/overrides/notes without inventing live combat semantics;
- explicit independent Personal -> Campaign Encounter copy preserving current dependency copy/remap behavior;
- atomic display-name + Encounter payload save under one optimistic revision;
- stale-write rejection and tombstone/non-resurrection;
- focused controller/repository coverage;
- integrate into the existing Desktop Managers surface with active Campaign context visible.

Do not add live initiative/combat state, generalized dependency graphs, automated encounter balancing, hosted reusable-content sync or provider work unless a concrete approved requirement demands it.

## Dungeon/Zone Manager integrated evidence

- implementation head `1a8b875a567ed73fbcceee73872ec59702b4b3f0`;
- push Scaffold `35285060523` — SUCCESS;
- PR Scaffold `35285359440` — SUCCESS;
- PR #73 merged as `5be90a994f453e5444ecf00762cd72407cfe790a`;
- post-merge Scaffold `35285629973` — SUCCESS.

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
