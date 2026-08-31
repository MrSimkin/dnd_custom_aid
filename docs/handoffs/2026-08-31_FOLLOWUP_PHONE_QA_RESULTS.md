# Follow-up character-sheet phone QA results — 2026-08-31

Branch: `implementation/character-data-foundation`

QA target:
- Scaffold checks run #180 (`33436382484`)
- verified implementation head: `8be69ce94a0ce613cc29e3752e40bcc365c81b47`
- artifact ID: `9774615456`
- APK SHA-256: `8c13056e3b8deda3b9679621d0d5a128e24b3d6d616fec303e68dad31fd22430`

## Incremental owner QA

### Installation

Owner report: `installed`.

Result:
- installing the run #180 APK over the existing V4 installation: **PASS**;
- no uninstall was reported or requested.

### Migration and top-level navigation

Owner report: `1-5 ok`.

The five requested checks were:
1. campaigns still present;
2. existing PCs still present;
3. previously populated PC preserves its existing core values/classes/skills;
4. tabs appear in approved order `General / Habilidades / Combate / Equipo`;
5. switching through all four tabs produces no immediate crash, blank screen, missing data or obviously broken layout.

Results:
- campaigns preserved: **PASS**;
- PCs preserved: **PASS**;
- previously populated character core values/classes/skills preserved: **PASS**;
- four-tab presence/order: **PASS**;
- initial switching/navigation smoke test: **PASS**.

This establishes the initial run #180 migration/navigation gate as **PASS**.

### General + derived values + Quick Magic

Owner report: `All OK`.

The requested checks covered:
1. ability scores/modifiers and class-row presentation;
2. calculation breakdowns and `Ajuste adicional` for Initiative, proficiency bonus, a save/skill and Passive Perception;
3. blank optional adjustment behaving as zero without suppressing totals;
4. temporary blank required-number editing;
5. required blank-number Save warning, Cancel behavior and confirmed save-as-zero behavior;
6. proficiency bonus following total character level;
7. Quick Magic spell save DC, spell attack modifier, Aptitud mágica and configured-level visibility;
8. spell-slot pips, `Restaurar espacios`, Save/reopen persistence.

Results:
- checks 1–8: **PASS**, based solely on the owner's earlier `All OK` response.

Clarification: a later standalone message `8` was accidental (the owner's phone fell on the keyboard) and carries no QA meaning. It must not be interpreted as a separate confirmation or observation.

Owner UX observations / approved follow-up corrections:

1. A derived field with a non-zero additional/custom adjustment does **not** need a second text line such as `ajuste +2`; it consumes too much vertical space. A compact marker, for example an asterisk, is sufficient to indicate that the displayed total contains an adjustment because tapping the value already exposes the full calculation and exact `Ajuste adicional`.
2. `Velocidad` is a structured distance and must follow the already-approved measurement convention: imperial first with approximate metric in parentheses, e.g. `30 ft (9 m)`.

These observations are follow-up UX refinements; they do not invalidate the PASS results for the functional General/Quick Magic batch.

### Combate

Owner report:
- `1. ok. I would make text vertically centered is there is another text that makes 2 lines.`
- `2. almost ok. keyboard covers part of window and if i click out of keyboard, window closes, and I would use drag and drop to move attacks`
- `3-9 ok`

The requested checks covered:
1. quick-reference values match General;
2. add an Ataque with the full proposed fields;
3. add a spell/non-weapon effect summary;
4. exercise entry types Ataque / Acción / Acción adicional / Reacción / Otro;
5. edit an entry;
6. reorder entries;
7. delete with confirmation;
8. Save/leave/reopen persistence including order;
9. portrait → landscape → portrait recreation on Combate.

Results:
- check 1: **PASS**, with a layout refinement requested;
- check 2: **PARTIAL / UX DEFECTS**, because entry creation works but the editor interaction is not acceptable yet;
- checks 3–9: **PASS**.

Owner-approved Combate UX follow-ups:

1. **Logical-row vertical centering.** If one quick-reference label/value needs two lines, neighboring one-line text in that same logical row should be vertically centered within the resulting shared row height rather than appearing top-biased.
2. **IME-safe entry editor.** Opening the keyboard must not cover important lower editor fields/actions. The attack/action editor should remain usable with the keyboard visible, using scrolling/insets or equivalent phone-safe behavior.
3. **Outside tap must not discard/close the editor.** Tapping outside the keyboard/editor content while the keyboard is visible should not dismiss the entire attack/action editor. The intended behavior is to retain the editor/draft; explicit Apply/Cancel (or an equally intentional close action) should control editor dismissal. At minimum, an outside tap used to get out of the keyboard must not lose the window/draft.
4. **Drag-and-drop reorder.** Replace the current up/down reorder controls for attacks/actions with direct drag-and-drop ordering. Persist the resulting user order exactly after Save/reopen.

These are follow-up UX corrections for the next build. They do not invalidate the successful persistence, type, edit, delete, rotation, and non-weapon/spell-summary behavior already verified in checks 3–9.

## Next QA step

Proceed to focused `Equipo` tab QA, then Settings/theme/font/large-text and recreation/regression checks.

Do not infer PASS for any item the owner does not explicitly report.
