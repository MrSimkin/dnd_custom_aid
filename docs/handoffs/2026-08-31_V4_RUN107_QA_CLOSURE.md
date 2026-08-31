# V4 run #107 — Manual QA closure

**Date:** 2026-08-31  
**Working branch:** `implementation/character-data-foundation`  
**QA target code:** `3c21cf649b31687180b73a8d314ca56eb937d147`  
**CI run:** #107 / `33358486525`  
**Artifact:** `dnd-custom-aid-debug-apk` / ID `9745937666`

This note closes the owner-led manual QA pass for the current V4 run #107 APK. It does **not** mean Phase 4 is accepted for merge; known defects and approved next-build changes still require a follow-up APK and another targeted QA pass.

## Final regression result

Owner performed the requested general regression pass after the feature-specific QA:

- keyboard/IME access to lower editable content: **PASS**;
- portrait → landscape → portrait recreation: **PASS**;
- screen off/on recreation: **PASS**;
- switching between `Resumen` and `Habilidades`: **PASS**;
- save, leave character, reopen character: **PASS**;
- full app close/reopen: **PASS**;
- no new data loss, unexpected navigation, inaccessible content, or regression beyond already-recorded known presentation/derived-adjustment issues was reported.

**General regression acceptance for run #107: PASS.**

## Settings clarification at closure

The owner clarified that rejecting **IBM Plex Sans Condensed** does **not** mean reducing the typography audition from four fonts to three. The second condensed-font slot should remain and IBM Plex Sans Condensed should be replaced by another condensed candidate.

Approved next-build audition:

- Manrope;
- Sora;
- Barlow Condensed;
- **Roboto Condensed** as the recommended replacement QA candidate for IBM Plex Sans Condensed.

Roboto Condensed is not pre-approved as final typography; it must be judged on the phone.

The owner also added one additional theme candidate to the next-build audition:

- **Matriz** — near-black surfaces with vivid green text/accent language, inspired by the recognizable black-and-green Matrix aesthetic while retaining ordinary app readability.

This is in addition to the already-recorded neutral `Gris`, `Cian oscuro`, `Azul noche`, `Verde bosque`, `Pergamino`, and `Alto contraste` candidates.

## Run #107 disposition

Manual QA on this exact APK is now **complete**.

The build demonstrated that the V3→V4 migration and the core derived-value mechanics are sound, with passing checks for migration preservation, ability modifiers, saving throws, skills, Passive Perception, class/hit-die behavior, Habilidades selector/state, and general Android regression behavior.

It is **not ready for PR/merge acceptance** because the next follow-up build must still address the recorded defects and approved design changes, including at minimum:

- blank optional adjustments behaving as zero;
- progressive-disclosure adjustment editor/breakdown;
- calculated proficiency bonus with permissive adjustment path;
- temporary blank numeric editing state;
- layout/alignment refinements from owner screenshots;
- larger ability-modifier prominence;
- Quick Magic reference block;
- responsive 115%/130% row alignment;
- revised font/theme audition;
- evaluation of possible additional character-sheet tabs.

The next logical project action is therefore to consolidate and implement the **V4 follow-up build specification**, build a new APK, and run targeted owner QA against the changed areas rather than repeating the entire run #107 test matrix from zero.
