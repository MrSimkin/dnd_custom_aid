# Latest project checkpoint — global resume map

**Updated:** 2026-09-17 (Chile local time)  
**Normal implementation trunk:** `main`  
**Last verified integrated runtime merge:** `fc870fe8303b0f9925c479bdc21c388ef5cc8450`  
**PR #53:** MERGED  
**Post-merge Scaffold:** `35256531651` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 reusable-content persistence foundation:** INTEGRATED  
**Wave 6 Creature payload persistence:** INTEGRATED  
**Wave 6 NPC payload persistence:** INTEGRATED  
**Wave 6 lightweight Homebrew/Rule payload persistence:** INTEGRATED  
**Current checkpoint:** `docs/checkpoints/2026-09-17_WAVE6_HOMEBREW_RULE_PAYLOAD_INTEGRATED.md`  
**Current wave:** Wave 6 — reusable/persistent content architecture continues  
**Next bounded package:** Place payload + local persistence core  
**Owner implementation authorization:** GRANTED

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-17_WAVE6_HOMEBREW_RULE_PAYLOAD_INTEGRATED.md`;
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
post-merge Scaffold                               PASS (35256531651)
        |
        v
Wave 6 Place payload + local persistence core     NEXT
```

## Practical continuation

Resume from current `main` and create a short-lived outcome-oriented branch for the Place payload + local persistence core using `ReusableContentFamily.PLACE`.

The first Place package should establish a canonical reusable Place record and preserve the approved rule that a Shop is a specialized Place rather than a separate top-level system. Keep the payload self-contained around human-facing retrieval/presentation data such as summary, area/geographic context, function, presentation text, services/interactives, hooks, player-safe text, DM-only notes, paper references and tags; exact low-level decomposition remains delegated engineering work.

Do not introduce generalized Place <-> NPC/Scene/Zone/Encounter dependency-copy graphs, clocks, media/object-storage references or automatic reveal/publication behavior in this first Place package. Introduce those relationships deliberately once the canonical Place record exists and a concrete dependent domain requires them.

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
