# START HERE — Fast Project Resume

This is the normal entry point for `MrSimkin/dnd_custom_aid`.

The goal is to resume safely in a few minutes without rereading the whole repository or reconstructing history from chat memory.

## Mandatory conversation language

Project chat stays in **English**, even when the owner writes a prompt in another language.

Only switch away from English when the owner explicitly requests a language change for that specific interaction.

## Fast resume order

Read and verify in this order:

1. `CURRENT_TASK.md` — the one-page volatile execution pointer.
2. Verify the remote Git state named there: current `main`, active branch/PR/head SHA and latest relevant CI state. Do this once; do not poll rapidly.
3. `AGENTS.md` — mandatory operating, security, cost and workflow rules.
4. `docs/PROJECT_STATE.md` — integrated whole-project truth.
5. `docs/checkpoints/LATEST.md` and its referenced checkpoint only when milestone context is needed.
6. Read only the directly relevant decision/architecture/test files required for the current task.

Do **not** start by reading every historical checkpoint, every decision or every branch.

## File roles

- `CURRENT_TASK.md` — current execution only. It is intentionally volatile and must be replaced, not accumulated as history.
- `docs/PROJECT_STATE.md` — durable integrated project state.
- `docs/checkpoints/LATEST.md` — latest durable milestone/checkpoint pointer.
- `docs/BRANCH_STATUS.md` — branch lifecycle/history when branch context is needed.
- `MANIFEST.md` — map of the deeper repository authority.
- `docs/decisions/`, architecture/product/testing docs — deeper authority read only when relevant.
- historical checkpoints — evidence for their time, not automatic resume instructions.

## If a chat was interrupted

Follow `docs/recovery/INTERRUPTION_RECOVERY.md`.

Core rule: **resume from the first unfinished durable step; never redo a completed commit, push, PR, merge, provider action or verified test without new evidence requiring it.**

## If `CURRENT_TASK.md` disagrees with Git

Git wins.

Verify the current remote state, update `CURRENT_TASK.md` to match reality, then continue from the first unfinished action.

## Coherent-task continuation rule

Routine safe engineering boundaries are **not** owner-confirmation boundaries.

Within an already-authorized coherent package, continue autonomously through the normal sequence when each gate is green and scope/risk has not changed, for example:

`implementation -> push CI -> PR -> PR CI -> merge -> post-merge CI -> checkpoint/documentation closure -> next already-delegated planning/implementation step`.

Do **not** artificially split that sequence into separate turns merely because a commit, PR, CI result or merge created a durable boundary. Durable boundaries exist for recovery and traceability, not to force the owner to keep saying `go`.

Stop and ask/hand off only when a real boundary appears: a material failure requiring judgment, a consequential owner-level product/scope/risk decision, an authenticated provider/manual action, destructive/cost/security uncertainty, or a genuinely long-running asynchronous wait with no other safe useful work available.

## Async CI rule

CI is an asynchronous gate, not a reason either to poll continuously or to fragment every normal workflow.

- Inspect at a meaningful gate.
- If complete, act on the result and continue the coherent task automatically.
- If still running, do any independent safe/useful work that does not assume the result.
- A later single recheck in the same task/session is allowed after such progress or when enough time has naturally passed; do not use rapid repeated polling or `sleep` loops.
- If CI is still running and there is genuinely nothing else safe/useful to do, record run ID/SHA/status in `CURRENT_TASK.md` and stop at that actual waiting boundary.

## Before ending meaningful work

When the execution state changes materially, update `CURRENT_TASK.md` before stopping so the next chat can resume without archaeology.

Examples: branch created, commit pushed, PR opened, CI failed, CI running at a real stop boundary, PR merged, owner/provider action required, or task completed and next task identified.
