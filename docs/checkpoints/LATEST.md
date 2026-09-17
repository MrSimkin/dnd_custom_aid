# Latest project checkpoint — global resume map

**Updated:** 2026-09-17 (Chile local time)  
**Normal implementation trunk:** `main`  
**Last verified integrated runtime merge:** `0b73d79e46edb7022ace2a2efd161149d9aefc73`  
**PR #57:** MERGED  
**Post-merge Scaffold:** `35262999144` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 reusable-content persistence foundation:** INTEGRATED  
**Wave 6 Creature payload persistence:** INTEGRATED  
**Wave 6 NPC payload persistence:** INTEGRATED  
**Wave 6 lightweight Homebrew/Rule payload persistence:** INTEGRATED  
**Wave 6 Place payload persistence:** INTEGRATED  
**Wave 6 Zone payload persistence:** INTEGRATED  
**Current checkpoint:** `docs/checkpoints/2026-09-17_WAVE6_ZONE_PAYLOAD_INTEGRATED.md`  
**Current wave:** Wave 6 — reusable/persistent content architecture continues  
**Next bounded package:** TO BE SELECTED after Zone documentation closure  
**Owner implementation authorization:** GRANTED

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-17_WAVE6_ZONE_PAYLOAD_INTEGRATED.md`;
3. `docs/PROJECT_STATE.md`;
4. `docs/BRANCH_STATUS.md`;
5. D-0071, D-0072, D-0073 and D-0075;
6. `docs/ROADMAP.md`.

## Current sequence

```text
Wave 4 Player <-> Server                           COMPLETE / INTEGRATED
Wave 5 Desktop shell/local campaign               COMPLETE / OWNER-QA PASS / MERGED (#42)
hosted membership administration core             COMPLETE / MERGED (#43)
Desktop hosted Campaign Administration            COMPLETE / OWNER-QA PASS / MERGED (#44)
Wave 6 reusable-content persistence foundation    COMPLETE / MERGED (#46)
Wave 6 Creature payload persistence               COMPLETE / MERGED (#48)
Wave 6 NPC payload persistence                    COMPLETE / MERGED (#51)
Wave 6 Homebrew/Rule payload persistence          COMPLETE / MERGED (#53)
Wave 6 Place payload persistence                  COMPLETE / MERGED (#55)
Wave 6 Zone payload persistence                   COMPLETE / MERGED (#57)
post-merge Scaffold                               PASS (35262999144)
        |
        v
Select next dependency-safe bounded Wave 6 package after this docs closure
```

## Practical continuation

Finish and integrate the short-lived Zone documentation closure branch.

After that integration, re-read current D-0072/D-0073 authority and select the next dependency-safe bounded Wave 6 package from the integrated foundation + Creature + NPC + Homebrew/Rule + Place + Zone state. Encounter is the remaining reserved rich reusable family, but its first bounded package should not prematurely pull in the full future relationship/dependency graph unless the approved requirement concretely needs it.

Under the coherent-task continuation rule, routine green boundaries do not require separate owner confirmation. Continue through normal safe engineering steps automatically until a genuine owner/product/risk/provider/failure/async-wait boundary appears.

## Permanent safety rules

- repository intentionally public;
- hard external-service budget USD $0;
- never request/paste/commit secrets, credentials, OTPs or session/access/refresh tokens;
- preserve historical identity/audit evidence;
- normal DEV owner/DM identity is Outlook-backed; Gmail is historical/inactive by default;
- preserve stable identity, revisions/stale-write rejection, idempotency, tombstones/non-resurrection and explicit conflicts;
- DM authority is not PC ownership;
- do not redeploy Cloudflare unless Worker code materially changes or new evidence requires it;
- object-storage provider selection remains deferred;
- do not run `npm audit fix --force` blindly.
