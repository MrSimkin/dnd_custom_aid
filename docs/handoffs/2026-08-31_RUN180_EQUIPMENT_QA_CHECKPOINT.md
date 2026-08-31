# Run #180 Equipment QA checkpoint — 2026-08-31

Branch: `implementation/character-data-foundation`

QA target:
- Scaffold checks run #180 (`33436382484`)
- verified implementation head: `8be69ce94a0ce613cc29e3752e40bcc365c81b47`
- artifact ID: `9774615456`
- APK SHA-256: `8c13056e3b8deda3b9679621d0d5a128e24b3d6d616fec303e68dad31fd22430`

## Owner report

Owner report: `1-13 ok (except 12 and same observation on drag and drop)`.

The requested checks were:
1. default currencies;
2. ordinary item creation with common fields;
3. quantity × per-unit weight and imperial/metric carried-weight total;
4. missing optional weight behavior;
5. special-item description/location/Sintonizado;
6. predefined and custom locations;
7. Sintonización count without hard cap;
8. independence of Equipado and Ubicación;
9. warning before discarding special-only data;
10. custom per-PC currency persistence;
11. manual inventory reorder persistence;
12. keyboard/IME editor behavior;
13. portrait → landscape → portrait recreation.

## QA disposition

- checks 1–11: **PASS**;
- check 12: **PARTIAL / UX DEFECT** — the same IME/editor-dismissal problem previously found in Combate applies to Equipo;
- check 13: **PASS**.

Existing arrow-based reorder functionality/persistence passes functionally, but the owner repeats the preference that inventory ordering should use drag-and-drop in the corrective build.

## Additional owner observations

- Combat editor UX defects also apply to Equipo editors: keyboard coverage and outside-tap dismissal must be corrected consistently.
- `Monedas` consumes far too much vertical space relative to the project's compact-view principle.
- Currency layout should become responsive and materially denser. Landscape should support at least three currency cells/columns where width allows. Two columns in portrait is a candidate to test, not yet a mandatory fixed count.
- Attack/action entries and inventory entries could benefit from responsive multiple-column presentation when the available width genuinely supports it, especially landscape/tablet-sized widths.
- Character glyphs/text are again being used as icon buttons. This violates the established presentation preference: icon-only controls should use stable vector icons rather than Unicode/text glyphs.
- Special items should be visually separated from common inventory because they are special. This is a presentation grouping requirement, not a reversal of the approved unified underlying inventory data model.
- In Settings, each font option should preview/render its own font face so the user can visually evaluate the typography before choosing it.

## Recovery consequence

The next corrective build must preserve the functional PASS behavior above while addressing the UX requirements recorded separately in D-0055.

This checkpoint supersedes any earlier `Next QA step: Equipo` wording in the cumulative run #180 QA file. The next QA domain is Settings/theme/font/large-text behavior, followed by general recreation/regression checks.