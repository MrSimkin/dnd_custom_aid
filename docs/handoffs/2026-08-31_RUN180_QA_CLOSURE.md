# Run #180 phone QA closure — 2026-08-31

Branch: `implementation/character-data-foundation`

QA target:
- Scaffold checks run #180 (`33436382484`)
- verified implementation head: `8be69ce94a0ce613cc29e3752e40bcc365c81b47`
- artifact ID: `9774615456`
- APK SHA-256: `8c13056e3b8deda3b9679621d0d5a128e24b3d6d616fec303e68dad31fd22430`

## Final regression / recreation batch

Owner report: `1 - 8 OK`.

The eight checks were:
1. unsaved draft + selected PC/tab survive portrait -> landscape -> portrait;
2. same unsaved draft + selected PC/tab survive screen off/on;
3. Quick Magic lower content remains reachable with the keyboard visible;
4. multiple classes, normal dropdown class, `Otro` custom class, and d8/d10/d12 remain stable/coherent;
5. Save/leave/reopen preserves a mixed character containing Quick Magic, attacks/actions, ordinary equipment, special equipment, and custom currency;
6. full app close/reopen preserves old + new durable data;
7. repeated switching among General / Habilidades / Combate / Equipo after restart causes no crash, blank screen, data loss, or disappearance;
8. an unusual/custom proficiency bonus remains numerically preserved through the migrated `Ajuste adicional` model.

Results:
- checks 1–8: **PASS**.

## Overall run #180 disposition

Run #180 is **functionally accepted as the completed exploratory/owner-QA build**. The following areas passed sufficiently to stop testing this APK:

- over-install migration and preservation;
- four-tab navigation;
- General / derived calculations / blank-number behavior;
- Quick Magic and spell-slot persistence;
- Habilidades arithmetic and persistence;
- Combate functional data/persistence/recreation behavior;
- Equipo functional data/persistence/recreation behavior;
- per-PC currencies and custom currencies;
- font/theme/text-scale preference persistence;
- rotation, screen-off/on, save/reopen, and full restart regression coverage.

Run #180 is **not the final merge candidate** because owner QA identified a bounded corrective UX/layout backlog already checkpointed separately.

## Required corrective build backlog

The next build must address only the already-recorded corrections, without introducing unrelated new scope. Controlling records include:

- D-0053 — compact marker for non-zero `Ajuste adicional`; `Velocidad` as imperial + metric structured distance;
- D-0054 — logical-row vertical centering; IME-safe/non-dismissive attack editor; drag-and-drop attack ordering;
- D-0055 — Equipo compactness/responsive layout, visual separation of special items, drag-and-drop inventory ordering, vector icons instead of text-character buttons, compact currencies, font-preview requirement;
- D-0056 — settings/theme/font pruning/refinement and restoration of responsive two-column `Por atributo` with proper alignment rather than collapsing to one column;
- Equipo item editor inherits the same IME-safe/non-dismissive editor correction as Combate;
- responsive multi-column presentation should be used for attacks/items/currencies where phone width genuinely supports it;
- special items remain the same underlying inventory model but must be visually separated from ordinary equipment;
- `Matrix` replaces the `Matriz` label;
- theme revisions/additions requested during run #180 QA remain part of the corrective build.

## Gate

Owner QA of run #180 is closed. Do not request additional testing on this exact APK unless investigating a newly discovered blocker.

Next project step: implement the bounded corrective UX/layout build, checkpoint each meaningful increment, run CI, freeze a new exact APK target, and then perform targeted phone QA only on the corrected areas plus a short regression pass.
