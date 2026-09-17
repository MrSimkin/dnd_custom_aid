# START HERE — Fast Project Resume

This is the normal entry point for `MrSimkin/dnd_custom_aid`.

The goal is to resume safely in a few minutes without rereading the whole repository or reconstructing history from chat memory.

## Mandatory conversation language

Project chat stays in **English**, even when the owner writes a prompt in another language.

Only switch away from English when the owner explicitly requests a language change for that specific interaction.

## Fast resume order

Read and verify in this order:

1. `CURRENT_TASK.md` — the one-page volatile execution pointer.
2. Verify the remote Git state named there: current `main`, active branch/PR/head SHA and latest relevant CI state. Do this once; do not poll.
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

## Async CI rule

CI is a checkpoint boundary, not something to wait on indefinitely.

- Inspect once.
- If complete, act on the result.
- If still running, record run ID/SHA/status in `CURRENT_TASK.md` and stop that bounded task.
- Do not use repeated polling or `sleep` loops.

## Before ending meaningful work

When the execution state changes materially, update `CURRENT_TASK.md` before stopping so the next chat can resume without archaeology.

Examples: branch created, commit pushed, PR opened, CI failed, CI running at stop boundary, PR merged, owner/provider action required, or task completed and next task identified.
