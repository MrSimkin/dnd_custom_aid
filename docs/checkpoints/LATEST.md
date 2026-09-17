# Latest project checkpoint — global resume map

**Updated:** 2026-09-17 (Chile local time)  
**Normal implementation trunk:** `main`  
**Last verified integrated runtime merge:** `013abbb9e57af0ba04fe1e8b678e8ed29522bedd`  
**PR #46:** MERGED  
**Post-merge Scaffold:** `35220099721` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 reusable-content persistence foundation:** INTEGRATED  
**Current checkpoint:** `docs/checkpoints/2026-09-17_WAVE6_REUSABLE_CONTENT_FOUNDATION_INTEGRATED.md`  
**Current wave:** Wave 6 — reusable/persistent content architecture continues  
**Owner implementation authorization:** GRANTED

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-17_WAVE6_REUSABLE_CONTENT_FOUNDATION_INTEGRATED.md`;
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
post-merge Scaffold                               PASS (35220099721)
        |
        v
Wave 6 reusable/persistent content architecture   CONTINUES
```

## Practical continuation

Resume from current `main` and determine the next bounded Wave 6 package from the current architecture, decisions and roadmap. The reusable-content persistence foundation is already integrated; do not rebuild it and do not assume a later package already exists.

No immediate owner decision or provider handoff is required solely to resume architectural planning.

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