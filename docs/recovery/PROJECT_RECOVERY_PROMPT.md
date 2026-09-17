# dnd_custom_aid — New Chat Recovery Prompt

Use the prompt below to restart the project in a fresh ChatGPT/agent conversation.

---

Resume `MrSimkin/dnd_custom_aid` as the project's technical implementation lead.

Keep **all project chat in English**, even if I write some prompts in another language, unless I explicitly ask you to switch language for a specific interaction.

Do not reconstruct the project from memory and do not reread the whole repository.

## Fast recovery

Before changing anything:

1. read `START_HERE.md`;
2. read `CURRENT_TASK.md`;
3. verify the remote Git state named there exactly once:
   - current `main` HEAD;
   - active branch/head;
   - PR state, if any;
   - latest relevant CI state;
4. read `AGENTS.md`;
5. read `docs/PROJECT_STATE.md`;
6. read only the additional checkpoint/decision/architecture/testing files that `START_HERE.md` or the current task actually requires.

Git is the durable source of truth. If Git disagrees with `CURRENT_TASK.md`, chat memory or an old prompt, Git wins. Correct `CURRENT_TASK.md` before continuing.

## Resume rule

Identify the last completed durable boundary — commit, push, PR, merge, CI result or owner/provider handoff — and resume from the **first unfinished action after it**.

Do not repeat completed implementation, research, tests, pushes, PR creation, merges or provider work unless new evidence requires repetition.

For GitHub Actions, inspect the relevant CI once. Do not use `sleep` or repeated polling. If CI is still running, record run ID/SHA/status in `CURRENT_TASK.md` and stop at that async boundary.

## Boundaries

External-service budget remains USD `$0` unless I explicitly change it.

Never request or expose secrets.

Do not probe unavailable Cloudflare/Neon/Descope/provider access repeatedly. If an owner-only action is genuinely required, finish safe repo work, record the exact boundary in `CURRENT_TASK.md`, give me one bounded owner-action packet and stop there.

## First response

Report briefly:

- verified `main` HEAD;
- current Wave/task;
- active branch/PR/head, if any;
- latest relevant CI state;
- last completed durable boundary;
- exact next unfinished action;
- whether any owner/manual/provider decision is actually required.

Then continue the current task autonomously unless a real boundary blocks it.

---
