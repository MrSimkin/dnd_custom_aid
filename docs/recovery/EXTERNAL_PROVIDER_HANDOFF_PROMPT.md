# External-provider handoff prompt

Use this prompt when repository work can continue but a later gate may require authenticated Cloudflare, Descope, Neon or another provider that the active worker may not be able to operate.

---

Resume `MrSimkin/dnd_custom_aid` from current Git state. Use English and treat Git as operative memory. First verify remote branch/PR/HEAD/CI and read `AGENTS.md`, `docs/checkpoints/LATEST.md`, its checkpoint, `docs/PROJECT_STATE.md`, `docs/BRANCH_STATUS.md` and `docs/WORKFLOW.md`.

Continue autonomously through all safe repository/code/test/CI/documentation work.

## Hard provider boundary

When work reaches an authenticated provider:

1. determine whether this execution environment has a real authenticated tool/connection capable of the required action;
2. if yes, use it only within already authorized security/cost/scope boundaries;
3. if no, establish that once and do **not** keep trying alternate MCP/plugin/browser/API-token/dashboard mechanisms.

Lack of provider capability is an owner-action boundary, not a reason for repeated access exploration.

## Before stopping

Finish all safe repo/CI work first. Then return one bounded owner-action packet containing:

- **What** the owner must do;
- **Why** it is required;
- **Where** (exact local directory/provider page/resource);
- **Commands/clicks** in order;
- **Expected result**;
- **Do not share** secrets/credential values;
- **Return to me** exact non-secret evidence;
- **Stop conditions** for errors, billing prompts, destructive/unexpected resource changes or security-sensitive deviations.

Never request passwords, OTPs, API/deployment tokens, DB credentials/connection strings, access/session/refresh JWTs, private keys, signing credentials, provider credential files or secret environment values.

After returned evidence, validate it and continue from that exact point without repeating still-valid investigation. Stop again only at the next unavoidable provider/manual boundary. Prefer one external-provider handoff per task.

## Current project-specific baseline

Always verify for newer Git state. At this prompt revision, Wave 5 is complete/integrated through PR #44 merge `306377df1a453f531af4b670d2b231c88a3c9419`, with post-merge Scaffold `35168920031` success and completed real provider/owner QA.

The existing DEV Worker is `dnd-custom-aid-api`. Do not create a replacement. Do not redeploy merely because docs changed; redeploy only if Worker code materially changes or newer evidence requires it.

Normal hosted DEV owner/DM identity is Outlook-backed; Gmail is historical/inactive by default and historical audit evidence remains untouched.

The current next implementation direction is Wave 6 reusable/persistent content architecture, which does not initially require external provider work. Object-storage provider selection remains deferred until Media/Handouts/assets genuinely require it and then requires a fresh `$0` review.

---