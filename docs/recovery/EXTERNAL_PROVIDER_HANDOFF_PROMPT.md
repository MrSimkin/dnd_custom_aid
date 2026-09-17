# External-provider handoff prompt

Use this prompt when repository work is ready to continue but the next gate may require authenticated Cloudflare, Descope, Neon or another external provider that the worker/chat might not actually be able to operate.

---

You are continuing the GitHub project `MrSimkin/dnd_custom_aid` from its current repository state.

Use **English**. Treat Git as operative memory. First verify the current remote branch/PR/HEAD and read the mandatory continuity files, especially `AGENTS.md`, `docs/checkpoints/LATEST.md`, the checkpoint referenced by `LATEST.md`, `docs/PROJECT_STATE.md`, `docs/BRANCH_STATUS.md` and `docs/WORKFLOW.md`.

## Objective

Continue the current package autonomously through all safe repository, code, test, CI and documentation work.

However, apply this hard stop rule:

> **Do not attempt to push through an external-provider capability boundary that this execution environment cannot actually cross.**

## Provider-capability test

When the work reaches Cloudflare, Descope, Neon or another authenticated provider:

1. determine whether you have a real authenticated tool/connection capable of performing the required action;
2. if yes, use it only within the project's already authorized security/cost/scope boundaries;
3. if no, do not keep trying alternate MCP/plugin/browser/API/token approaches after that limitation is established.

Lack of provider capability is an **owner-action boundary**, not a reason for repeated tool exploration.

## Before stopping

Do all safe work that does not require the unavailable provider first. Inspect the repository so the owner is not asked to discover commands, file paths, deployment targets or expected outputs that the technical worker can determine itself.

Then return exactly one bounded owner-action packet containing:

- **What:** the exact external action the owner must perform;
- **Why:** what project gate/evidence it satisfies;
- **Where:** exact local directory/provider screen;
- **Commands/clicks:** ordered steps, ready to execute;
- **Expected result:** what successful output/state should look like;
- **Do not share:** every secret/value that must remain local;
- **Return to me:** only the specific non-secret output/screenshot/result needed to continue;
- **Stop conditions:** errors, billing prompts, destructive actions, unexpected account/resource changes or security-sensitive deviations that require stopping instead of improvising.

Do not ask the owner for passwords, OTPs, API keys, deployment tokens, database credentials, access/refresh/session JWTs, private keys or other secrets.

## Resume behavior

When the owner returns the requested non-secret evidence:

1. validate it;
2. continue from that exact point;
3. do not repeat investigation or verification that remains valid;
4. stop again only at the next unavoidable owner/manual/provider boundary.

Prefer **one external handoff per task**. Large repository work does not need to be broken into artificial microsteps; only the inaccessible provider action needs the handoff.

## Current project-specific context

Always verify for newer Git state, but at the time this prompt was created the active package was Wave 5 draft PR #44 (`wave5/desktop-hosted-campaign-administration`). Its bounded repository implementation was complete/CI-verified and the next gate was explicit real DEV Cloudflare Worker deployment/integration for Campaign Administration routes, followed by owner Windows Desktop QA.

The first DEV Cloudflare/Neon/Descope activation was already complete. Do not recreate accounts/resources or redesign authentication. The current need is deployment/integration of the already implemented code unless newer repository evidence says otherwise.

The repository has no automatic Worker deployment workflow, so green CI alone is not proof that the new routes are live.

## Required final report at each handoff

Before stopping, report:

- branch/HEAD/PR;
- work completed in this run;
- checks run and results;
- exact external boundary reached;
- the owner-action packet;
- what will happen immediately after the owner returns evidence.

---
