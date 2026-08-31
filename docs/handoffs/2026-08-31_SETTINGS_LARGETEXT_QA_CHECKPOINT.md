# Run #180 Settings / fonts / themes / large-text QA checkpoint — 2026-08-31

Branch: `implementation/character-data-foundation`
QA target: Scaffold checks run #180 (`33436382484`), APK SHA-256 `8c13056e3b8deda3b9679621d0d5a128e24b3d6d616fec303e68dad31fd22430`.

## Owner results

The requested batch covered:
1. eight font candidates selectable and visibly applied;
2. owner judgement on font candidates;
3. theme audition and owner judgement;
4. text scales 80 / 90 / 100 / 115 / 130%;
5. tab wrapping/clipping at 115/130%;
6. `Habilidades -> Por atributo` at 115/130%;
7. landscape at 115/130%;
8. persistence of font/theme/text-size preferences through full app restart.

Owner report:
- 1: yes;
- 2: Lexend and Sora look too similar; remove or replace one, no preference which. Oswald is disliked and should be removed or replaced. Barlow Condensed and Roboto Condensed look too similar; remove or replace one, no preference which;
- 3: Gris is not an acceptable middle ground between light/dark; Azul noche should look more distinctly blue; add adapted light versions of Cian oscuro, Azul noche and Verde bosque; rename Matriz to Matrix; Pergamino does not visually read as parchment/scroll; with this many themes, replace the current dropdown with another selection control;
- 4: OK;
- 5: top-level tabs do not wrap, but `Por atributo` was changed to one column and this is not acceptable; the earlier two-column concept was better. Attribute/modifier alignment remains wrong;
- 6: same as 5;
- 7: all OK except previously recorded UX observations/bugs;
- 8: OK.

## QA disposition

- eight font candidates load/select/apply: **PASS**;
- font audition itself: **PASS as an audition**, with pruning/replacement required before final acceptance;
- theme candidates load/select/apply: **PASS as an audition**, with visual redesign/additions/control redesign required before final acceptance;
- text scales 80/90/100/115/130 remain usable/reachable: **PASS**;
- top-level tab labels at 115/130 do not wrap: **PASS** on run #180;
- `Habilidades -> Por atributo` responsive presentation: **FAIL / UX REGRESSION** because the implementation solved width pressure by collapsing to one column and still leaves attribute/modifier alignment incorrect;
- landscape 115/130: **PASS**, subject to already-recorded cross-cutting UX defects;
- persisted font/theme/text-size preferences after full restart: **PASS**.

## Critical correction

The approved direction is **not** to solve `Por atributo` width pressure by reducing it to one column. Restore/preserve a two-column presentation where it was previously successful and fix width allocation, logical-row height, attribute/modifier alignment and large-text reflow within that two-column design.

This checkpoint records owner QA only. Implementation corrections are recorded separately.