# Interrupted Chat Recovery Procedure

Use this when a ChatGPT/agent session stops because of timeout, connection loss, usage limit, browser failure or any other interruption.

The objective is to recover in minutes without repeating completed work.

## Recovery procedure

1. Read `START_HERE.md`.
2. Read `CURRENT_TASK.md`.
3. Verify remote Git once:
   - current `main` HEAD;
   - active branch and HEAD named in `CURRENT_TASK.md`;
   - PR state, if one exists;
   - latest relevant CI run/status.
4. Compare Git with `CURRENT_TASK.md`.
5. Identify the **last durable boundary already completed**: commit, push, PR creation, merge, CI result, provider/owner handoff or other externally recorded state.
6. Resume from the **first unfinished action after that boundary**.
7. Do not repeat completed investigation, implementation, tests, pushes, PRs, merges or provider actions unless new evidence shows they must be repeated.
8. Read deeper project files only when the current task requires them.
9. Before stopping again after a material state change, replace `CURRENT_TASK.md` with the new current execution state.

## Conflict rule

If chat memory, an old prompt or `CURRENT_TASK.md` conflicts with current remote Git evidence, Git wins.

Correct `CURRENT_TASK.md` before continuing.

## CI rule

Never burn a session waiting for GitHub Actions.

- Inspect the relevant run once.
- If completed, act on the result.
- If still running, record run ID, SHA and status in `CURRENT_TASK.md` and end the bounded task.
- No `sleep` loops and no repeated polling.

If CI failed, inspect only enough evidence to identify the failing job/step. Repair only failures clearly attributable to the current authorized package. Do not turn a focused repair into a new feature package.

## Provider/manual boundary

Do not retry unavailable Cloudflare, Neon, Descope or other provider capabilities repeatedly.

Record the exact owner/manual action required in `CURRENT_TASK.md`, provide a bounded owner-action packet, and stop there. Never request or expose secrets.

## Conversation language

All project chat remains in English unless the owner explicitly requests a different language for a specific interaction.

## Recovery completion test

Recovery is complete when you can state, from current Git evidence:

- current integrated `main` HEAD;
- current Wave;
- current task;
- active branch/PR/head, if any;
- latest relevant CI state;
- last completed durable boundary;
- exactly one next unfinished action.

At that point, continue the task instead of performing more archaeology.
