# Latest project checkpoint — global resume map

**Updated:** 2026-09-17 (Chile local time)  
**Normal implementation trunk:** `main`  
**Last verified integrated runtime merge:** `306377df1a453f531af4b670d2b231c88a3c9419`  
**PR #44:** MERGED  
**Post-merge Scaffold:** `35168920031` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Current checkpoint:** `docs/checkpoints/2026-09-17_WAVE5_INTEGRATED_WAVE6_READY.md`  
**Next normal wave:** Wave 6 — reusable/persistent content architecture  
**Owner implementation authorization:** GRANTED

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-17_WAVE5_INTEGRATED_WAVE6_READY.md`;
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
post-merge Scaffold                               PASS (35168920031)
        |
        v
Wave 6 reusable/persistent content architecture   NEXT
```

## Practical continuation

Create a short-lived Wave 6 branch from current `main`. Reuse existing Shared spine semantics (`ContentScope`, provenance, revisions/tombstones) and extend them into local reusable-content persistence before building rich Wave 7 Manager UI.

No immediate owner decision or provider handoff is required.

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