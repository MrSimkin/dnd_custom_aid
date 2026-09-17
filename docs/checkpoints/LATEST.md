# Latest project checkpoint — global resume map

**Updated:** 2026-09-17 (Chile local time)  
**Normal implementation trunk:** `main`  
**Last verified integrated runtime merge:** `1aebc6d6b769d0da59b4dfd6e13a1ce52ccbb99a`  
**PR #51:** MERGED  
**Post-merge Scaffold:** `35254527744` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 reusable-content persistence foundation:** INTEGRATED  
**Wave 6 Creature payload persistence:** INTEGRATED  
**Wave 6 NPC payload persistence:** INTEGRATED  
**Current checkpoint:** `docs/checkpoints/2026-09-17_WAVE6_NPC_PAYLOAD_INTEGRATED.md`  
**Current wave:** Wave 6 — reusable/persistent content architecture continues  
**Next bounded package:** lightweight Homebrew/Rule payload + local persistence core  
**Owner implementation authorization:** GRANTED

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-17_WAVE6_NPC_PAYLOAD_INTEGRATED.md`;
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
post-merge Scaffold                               PASS (35254527744)
        |
        v
Wave 6 lightweight Homebrew/Rule payload core     NEXT
```

## Practical continuation

Resume from current `main` and create a short-lived outcome-oriented branch for the lightweight Homebrew/Rule payload + local persistence core.

Use `ReusableContentFamily.HOMEBREW_RULE` for a self-contained rule/ruling/custom-system payload with summary/body, optional category/rationale, examples, related references, tags and the simple `DRAFT / ACTIVE / RETIRED` lifecycle approved by D-0072.

Keep structured race/class/subclass/background/feat/spell/item families outside this bounded package; they need family-appropriate structure rather than one universal record.

Keep Place/Zone/Encounter dependency/reference graphs outside this package as well. Introduce relationship semantics deliberately when those domains require them.

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
