# RESUME.md — canonical fresh-context entry

Use this file when a fresh human/AI session is told to **see/resume the `dnd_custom_aid` repository** and no narrower task-specific pointer was supplied.

## Fast route

1. Verify the remote `main` HEAD.
2. Read `AGENTS.md` for mandatory operating/security rules.
3. Immediately read `docs/checkpoints/LATEST.md`.
4. Follow the single **Canonical active checkpoint** named there.
5. Read only the additional files that that checkpoint or `LATEST.md` says are needed for the active task.

Do **not** reconstruct the current task from branch names, old PRs, historical checkpoints, README prose, or a remembered chat before following `LATEST.md`.

### Branch-selection rule

If `docs/checkpoints/LATEST.md` names a current implementation branch, that exact branch is the **only non-main continuation authority**. Ignore every other remote branch/ref unless the canonical active checkpoint explicitly names it for evidence. Do not infer active work from branch recency, branch-name similarity, open PR state, or historical `next` instructions.

If `main` and that named active branch differ in routing metadata, the route published on current `main` is the entry authority and the named active branch is the implementation authority.

## Route-update contract

`docs/checkpoints/LATEST.md` is the single mutable practical resume pointer.

Whenever the active work route changes, the same coherent repository change must:

- create or update the checkpoint that describes the new active route;
- update the **Canonical active checkpoint** in `docs/checkpoints/LATEST.md`;
- update its current branch/manual boundary/next action as applicable;
- update `docs/PROJECT_STATE.md` and `docs/BRANCH_STATUS.md` when their material state changed.

A checkpoint may exist as historical evidence without becoming active. It becomes the resume route only when `LATEST.md` points to it.

CI runs `scripts/check_resume_route.py` so the root entry route and canonical checkpoint pointer cannot silently break.
