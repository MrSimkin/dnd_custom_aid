# Latest project checkpoint — global resume map

**Updated:** 2026-09-17 (Chile local time)  
**Normal implementation trunk:** `main`  
**Last verified integrated runtime merge:** `d978a4191054227a03b32ecca3e7ceadc5d6e869`  
**PR #55:** MERGED  
**Post-merge Scaffold:** `35259027937` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 reusable-content persistence foundation:** INTEGRATED  
**Wave 6 Creature payload persistence:** INTEGRATED  
**Wave 6 NPC payload persistence:** INTEGRATED  
**Wave 6 lightweight Homebrew/Rule payload persistence:** INTEGRATED  
**Wave 6 Place payload persistence:** INTEGRATED  
**Current checkpoint:** `docs/checkpoints/2026-09-17_WAVE6_PLACE_PAYLOAD_INTEGRATED.md`  
**Current wave:** Wave 6 — reusable/persistent content architecture continues  
**Next bounded package:** TO BE SELECTED after Place documentation closure  
**Owner implementation authorization:** GRANTED

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-17_WAVE6_PLACE_PAYLOAD_INTEGRATED.md`;
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
post-merge Scaffold                               PASS (35259027937)
        |
        v
Select next dependency-safe bounded Wave 6 package after this docs closure
```

## Practical continuation

Finish the short-lived Place documentation closure branch and integrate it into `main`.

After that integration, do not infer the next implementation package from branch order alone. Re-read current D-0072/D-0073 authority and select one dependency-safe bounded Wave 6 package from the now-integrated foundation + Creature + NPC + Homebrew/Rule + Place state.

No next implementation branch has been created by this closure, and no immediate owner decision or provider handoff is required.

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
