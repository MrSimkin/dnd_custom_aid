from pathlib import Path

path = Path("docs/PROJECT_STATE.md")
text = path.read_text()

old_position = "**Current execution position:** Batch 0 complete; A1 GREEN; A2 GREEN; B1 GREEN; B2 GREEN; C GREEN; D GREEN; E GREEN; F GREEN; G1 GREEN; G2 GREEN; G3 GREEN; H1 GREEN; H2 GREEN; H3 GREEN; I1 GREEN; I2a GREEN; I2b GREEN; **Batch I complete; Batch J GREEN; Batch K active**"
new_position = "**Current execution position:** Batch 0 complete; A1 GREEN; A2 GREEN; B1 GREEN; B2 GREEN; C GREEN; D GREEN; E GREEN; F GREEN; G1 GREEN; G2 GREEN; G3 GREEN; H1 GREEN; H2 GREEN; H3 GREEN; I1 GREEN; I2a GREEN; I2b GREEN; **Batch I complete; Batch J GREEN; Batch K GREEN; Batch L GREEN/FROZEN; Batch M active**"
if text.count(old_position) != 1:
    raise RuntimeError("Expected exactly one current execution position marker")
text = text.replace(old_position, new_position)

resume_start = text.index("## 0. Primary resume order")
resume_end = text.index("## 1. Closure scope and merge boundary")
resume = '''## 0. Primary resume order

1. `docs/checkpoints/2026-09-04_PHASE4_BATCH_L_FROZEN_QA_CANDIDATE.md` — frozen owner-QA candidate identity and Batch M entry;
2. `docs/checkpoints/2026-09-04_PHASE4_BATCH_K_STABILIZATION_COMPLETE.md` — owner-schema migration proof and exact K stabilization gate;
3. `docs/checkpoints/2026-09-04_PHASE4_BATCH_J_BACKUP_IMPORT_COMPLETE.md` — completed Batch J backup/import gate;
4. `docs/checkpoints/2026-09-04_PHASE4_BATCH_I2B_TABLE_MODE.md` — completed Batch I closure;
5. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md` — approved execution sequence through owner QA; historical next-action text is superseded by this Project State;
6. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_IMPLEMENTATION_MAP.md` — higher-level implementation/gate map and final QA matrix;
7. `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md` — controlling owner-approved closure scope;
8. `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md` — approved class/subclass/module triggers and boundaries;
9. earlier A–J checkpoints as historical implementation/verification evidence.

'''
text = text[:resume_start] + resume + text[resume_end:]

suffix_start = text.index("## 3. Current active batch — K closure candidate stabilization")
suffix = '''### Batch K — closure candidate stabilization — GREEN

Controlling checkpoint:

`docs/checkpoints/2026-09-04_PHASE4_BATCH_K_STABILIZATION_COMPLETE.md`

Result:

- prior owner-QA APK lineage verified as SQLDelight schema 5;
- current closure schema verified as schema 9;
- new exact historical-schema migration regression exercises schema 5 -> 9 through migrations 5–8;
- representative Campaign, character, class/save/skill, slots, Combat, Equipment/currency, Trasfondo, Rasgo, Nota and spell/source/Prepared data survive and reopen through current repositories;
- closure-era additions receive safe defaults;
- no production migration repair was required;
- accepted regression commit `5030a0ed03df4ae92e6de312b1951b7f364c40d7`, tree `bcd22883c7a08d4c59394d799336f664137f1961`;
- focused migration workflow `33886853307` — PASS;
- controlling exact-clean workflow `33887059005` — PASS across backend, all shared tests, Android debug assemble, Desktop build and APK upload;
- technical artifact `9942413356`, ZIP digest `sha256:dde2ebdfc5f82ce2d2623c8bbd6fc52a00bdc609d90abeed539c5f8c830212ce`.

No integration defect was demonstrated, so Batch K made no production-code stabilization change.

### Batch L — frozen phone+tablet QA candidate — GREEN / FROZEN

Controlling checkpoint:

`docs/checkpoints/2026-09-04_PHASE4_BATCH_L_FROZEN_QA_CANDIDATE.md`

Frozen candidate identity:

- branch `tmp/phase4-l-frozen-qa-candidate`;
- exact commit `5cc034d3fdf4c25d935bd698aeaf2a3f9e427f27`;
- exact tree `b0e25a194ba0ed1926422230f3c29f70bfcd4e24`;
- controlling workflow `33887576972` — PASS across backend, all shared/migration tests, Android debug assemble, Desktop build and APK upload;
- artifact ID `9942595794`, name `dnd-custom-aid-debug-apk`;
- GitHub artifact ZIP digest `sha256:04fccd1c1078e302ddc621f9b546248f6588afcf46aa5f5050b4173919c2999b`;
- independently downloaded ZIP SHA-256 `04fccd1c1078e302ddc621f9b546248f6588afcf46aa5f5050b4173919c2999b` — exact match;
- ZIP contains exactly one `androidApp-debug.apk`, size `36146572` bytes;
- extracted APK SHA-256 `73282b433c519840e73ef9f8c8e63a311dcdf7bd9352c299469a0f7c290be079`.

The frozen candidate branch must not be changed. Any code repair after owner QA begins invalidates this identity and requires a new candidate.

## 3. Current active batch — M owner real-device QA

Automated closure work is complete through the frozen candidate. The next authority is owner real-device QA; CI cannot substitute for this gate.

Required device matrix:

- phone portrait;
- phone landscape;
- tablet portrait;
- tablet landscape;
- representative larger text.

Required upgrade/data check:

- install the frozen candidate over the prior owner-QA installation/data lineage rather than clearing app data first;
- confirm protected campaign/character data survives the schema-5 -> current upgrade;
- only after upgrade preservation is confirmed may a clean-install check be used as supplemental evidence.

Representative closure checks must include at least:

- app opens and existing campaign/character can be opened after upgrade;
- global character navigation and remembered tab behavior;
- General/Habilidades/Combate/Gestión/Equipo/Trasfondo/Rasgos/Conjuros/Notas protected baseline;
- conditional Artífice/Formas/Técnicas/Metamagia/Pactos/Compañeros visibility and representative interaction where applicable;
- global dirty/Save/Discard/unsaved-leave behavior;
- Table mode structural lock versus intended live/session controls;
- Supercompact/Quick Access representative live controls;
- backup export through the Android document picker;
- backup import into a campaign as a **new copy**, with no overwrite of the source/existing character;
- no blocking layout, keyboard/IME, rotation, scrolling, larger-text or tablet master-detail regression.

Record every blocking finding with device/orientation, exact screen/module, reproduction steps and whether protected data is at risk. Do not repair on the frozen candidate branch.

## 4. Remaining approved execution sequence

From the current position:

- **M — ACTIVE — owner real-device QA** on the one frozen L candidate;
- blocking QA findings -> focused repair branch/batch -> complete automated gate -> a **new** frozen candidate identity -> restart affected QA evidence;
- if owner QA is accepted with no blockers, complete continuity/governance housekeeping including consolidated `docs/DECISIONS.md`, perform the unique-commit/merge-boundary audit, and prepare the Phase 4 merge proposal;
- merge to `main` only after the owner explicitly approves Phase 4 closure/merge.

No DM feature implementation begins before that explicit Phase 4 exit decision.

## 5. Existing baseline that must not regress

Persistent General/Habilidades, Combate, Gestión, Equipo/currencies, Trasfondo including Raza and Religión/Fe, Rasgos, conditional Conjuros with sources/prepared/shared slots, Notas, all six conditional modules, PC Settings, Supercompact/Quick Access/live controls, Table mode, D-0046 derived values/adjustments, adaptive navigation/last-tab restoration, Batch J backup/import/reconciliation and the schema-5 -> current migration path remain protected baseline behavior.

Historical/focused/tmp branches remain intentionally preserved. Do not delete them before the eventual post-merge unique-commit audit.

## 6. Final acceptance boundary

The frozen L APK is the only current Phase 4 QA candidate. Green CI and hashes establish technical identity; they do not establish product acceptance.

Phase 4 remains open until:

1. owner phone+tablet QA is completed and accepted;
2. blocking findings are resolved through a new candidate when necessary;
3. continuity/governance housekeeping is complete;
4. the unique-commit/merge-boundary audit is complete;
5. the owner explicitly approves merge/closure.

## 7. Exact continuation

Do **not** make another code change now.

Use the frozen candidate APK defined by `docs/checkpoints/2026-09-04_PHASE4_BATCH_L_FROZEN_QA_CANDIDATE.md` and perform Batch M owner QA.

The candidate identity is:

- commit `5cc034d3fdf4c25d935bd698aeaf2a3f9e427f27`;
- workflow `33887576972`;
- artifact `9942595794`;
- ZIP SHA-256 `04fccd1c1078e302ddc621f9b546248f6588afcf46aa5f5050b4173919c2999b`;
- APK SHA-256 `73282b433c519840e73ef9f8c8e63a311dcdf7bd9352c299469a0f7c290be079`.

Keep `main` untouched. Keep `tmp/phase4-l-frozen-qa-candidate` untouched. If QA finds a blocker, open a focused repair branch from the durable closure line; never patch the frozen candidate in place. Do not begin DM work before successful Phase 4 exit and explicit owner approval.
'''
text = text[:suffix_start] + suffix

path.write_text(text)
