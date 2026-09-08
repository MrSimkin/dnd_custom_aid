# Phase 4 pre-QA owner audition — phone Stage A/B

**Date:** 2026-09-07  
**Status:** OWNER PHONE AUDITION IN PROGRESS; typography and text-scale evidence recorded  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Review identity:** `0.4.0-preqa.7` / build `40700` / `debug`  
**Primary phone:** Redmi Note 11 Pro 5G

## Preconditions already passed

- APK installed and launched successfully on the owner phone;
- `Ajustes -> Acerca de` confirmed expected version/build/type;
- owner has a representative populated character available for review.

## Stage A — typography evidence

Owner observations:

- **Mona Sans Condensed:** rejected — too tight/compressed and difficult to read;
- **League Spartan:** rejected — visually unpleasant to the owner;
- **PT Sans Narrow:** provisional only — owner likes the underlying shape/readability direction, but considers the current weight too light; keep it only if a satisfactory Bold presentation is available/usable;
- **all other sampled font candidates:** pass at this stage.

Do not force a final font winner yet. The current result is an elimination/approval pass, not a branding decision.

## Stage B — application text scale evidence

- very large application zoom/text sizes are predictably difficult to read/use, but the in-app warning is clear and the owner considers the behavior acceptable;
- very small application sizes are genuinely very small, but the owner explicitly likes having those options and wants them retained for preference flexibility;
- no range reduction is requested from this observation.

Stage B is considered sufficiently reviewed on the primary phone for this pre-QA audition pass. Tablet/later formal QA may still expose device-specific issues.

## One deferred final UX note

At a final polish step, revisit the **Application Settings presentation menu** only as one bounded UX cleanup item:

- improve/example the meaning of text-size choices so scale differences are easier to understand before selection;
- reduce explanatory/source text shown around font choices so the audition menu is less text-heavy;
- retain an appropriate acknowledgement/credits notice for owners/authors of original font/material assets used by the application, together with the project’s required licensing notices.

This is a deferred final-polish note, not a blocker and not a request to reopen implementation now.

## Next action

Proceed to **Stage C — whitespace compactness** on the Redmi Note 11 Pro 5G. Keep a comfortable passing font and near-normal text size, then compare application spacing 100%, 80% and 60% on representative dense surfaces. Judge whether 60% actually removes whitespace without making intrinsic controls/touch targets feel cramped or causing visual collisions.

After Stage C evidence, update `docs/checkpoints/LATEST.md` before moving to the next stage.
