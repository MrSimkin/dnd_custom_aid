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

## Next QA step

Focused `General` + derived-value + Quick Magic batch:
1. Confirm ability scores/modifiers and class rows look intact; modifiers are readable/prominent enough.
2. Tap Initiative, proficiency bonus, one saving throw or skill total, and Passive Perception; confirm each opens the calculation breakdown and `Ajuste adicional` behaves correctly.
3. Confirm a blank optional adjustment behaves as 0 and does not blank the total.
4. Clear one required numeric field temporarily, enter a replacement value, and confirm editing works naturally.
5. Clear one required numeric field, press Save, confirm the warning appears; Cancel should preserve the blank draft. Repeat and confirm `Guardar con 0` stores 0.
6. Confirm proficiency bonus follows total character level; optionally change class level across a PB threshold and verify the total updates.
7. In Quick Magic, set spell save DC, spell attack modifier and Aptitud mágica; configure at least one spell-slot level and confirm only configured levels appear.
8. Tap spell-slot pips spent/unspent, use `Restaurar espacios`, Save, leave/reopen the PC, and confirm Quick Magic state persists.

Do not infer PASS for any item the owner does not explicitly report.
