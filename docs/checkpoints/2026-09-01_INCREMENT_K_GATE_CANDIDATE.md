# Increment K — Gate candidate

**Date:** 2026-09-01  
**Branch:** `tmp/increment-k-responsive-accessibility`  
**Candidate parent head:** `6c592a4c283c9a3cf097ecaa565a8c3eaf215be5`  
**Baseline:** Increment J durable closure `2fe7ab0bc6ce18f3956bda0ed750f433367e483b`

## Candidate status

Increment K responsive/accessibility corrections are staged for the authoritative repository gate.

An ancestry comparison from the durable Increment J head to the K candidate reports:

- status: `ahead`;
- ahead: 6 commits;
- behind: 0 commits.

The production delta is intentionally narrow:

- `CharacterEditorV4.kt`: 15 changed lines;
- `CharacterSpellsTabV4.kt`: 51 changed lines;
- `CharacterUi.kt`: 6 changed lines;
- `IconControls.kt`: 98 changed lines;
- plus the Increment K audit checkpoint.

No schema, migration, repository, or backend production changes are part of K.

## Corrections included

The candidate closes static/code-level gaps discovered against approved D-0064:

1. selected Conjuros source now follows stable source identity and is automatically brought into the subordinate horizontal strip viewport;
2. long source labels are bounded/ellipsized rather than forcing malformed navigation;
3. drag/reorder affordances now provide a 48 dp touch target while preserving long-press drag semantics and content descriptions;
4. legacy Unicode/text pseudo-buttons used as icon-only controls in the class editor are replaced by semantic drawn controls;
5. the character creation FAB no longer uses a text `+` as its icon-only control;
6. create-character outside dismiss no longer silently discards typed editor state.

Existing code-level requirements confirmed during the audit include:

- supported app text-scale choices 80/90/100/115/130%;
- `Habilidades -> Por atributo` retains at least two columns on narrow layouts;
- new-domain editors use recreation-safe state and protected IME editing where destructive outside dismissal would lose work;
- Background, Traits, Conjuros and Notas already have responsive/wide-layout behavior from prior increments.

## Safety evidence

The final large-editor/navigation corrections were applied through an asserted temporary GitHub Actions patch. Every matcher passed and the patch workflow committed cleanly. The temporary workflow/script were removed by the same clean source commit and are not part of the candidate.

## Gate K

The authoritative gate is the repository `Scaffold checks` workflow, which must complete all of the following on this candidate checkpoint:

- backend `npm install` / `npm run check`;
- full `:shared:desktopTest` suite;
- Android debug assembly;
- desktop build;
- Android debug APK upload.

A green automated gate is necessary for K closure but is not owner-device acceptance.

## Explicit manual QA boundary

The following remain for intended-device owner QA and must not be claimed as automated PASS:

- 80/90/100/115/130% real-device presentation;
- portrait/landscape ergonomics;
- selected top-level/source navigation visibility under actual device widths;
- keyboard reachability and dismissal behavior with the real IME;
- drag feel/touch ergonomics at large text scales;
- truncation/ellipsis quality and overall phone readability.

## Promotion rule

Only after Gate K is fully green:

1. record the exact workflow run and APK artifact;
2. create the formal Increment K closure handoff;
3. update `PROJECT_STATE.md`;
4. confirm descendant-only ancestry;
5. fast-forward `implementation/character-data-foundation` without force;
6. keep `main` untouched;
7. begin Increment L from the promoted K head.
