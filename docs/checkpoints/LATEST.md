# Latest project checkpoint — global resume map

**Updated:** 2026-09-17 (Chile local time)  
**Normal implementation trunk:** `main`  
**Last verified integrated runtime merge:** `5762058645ba8af8fa470dcf185bd2b418af65e9`  
**PR #48:** MERGED  
**Post-merge Scaffold:** `35253188344` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 reusable-content persistence foundation:** INTEGRATED  
**Wave 6 Creature payload persistence:** INTEGRATED  
**Current checkpoint:** `docs/checkpoints/2026-09-17_WAVE6_CREATURE_PAYLOAD_INTEGRATED.md`  
**Current wave:** Wave 6 — reusable/persistent content architecture continues  
**Next bounded package:** NPC payload + local persistence core  
**Owner implementation authorization:** GRANTED

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-17_WAVE6_CREATURE_PAYLOAD_INTEGRATED.md`;
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
post-merge Scaffold                               PASS (35253188344)
        |
        v
Wave 6 NPC payload + local persistence core       NEXT
```

## Practical continuation

Resume from current `main` and create a short-lived outcome-oriented branch for the NPC payload + local persistence core.

Preserve the approved NPC semantics from D-0072: Quick and Developed NPCs are both valid; combat mechanics are optional; when full combat mechanics are present, reuse the Creature/stat-block machinery rather than creating a second incompatible combat representation.

Keep this package bounded to domain payload + local persistence/revision/copy behavior. Do not pull Wave 7 Manager UI, hosted reusable-content sync, import/export, AI helpers, object storage or provider work into it.

No immediate owner decision or provider handoff is required to begin this package.

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
