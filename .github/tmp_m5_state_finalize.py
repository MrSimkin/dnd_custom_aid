from pathlib import Path

path = Path('docs/PROJECT_STATE.md')
text = path.read_text()

lines = text.splitlines()
for i, line in enumerate(lines):
    if line.startswith('**Current execution position:**'):
        lines[i] = '**Current execution position:** Batch 0 complete; A1 GREEN; A2 GREEN; B1 GREEN; B2 GREEN; C GREEN; D GREEN; E GREEN; F GREEN; G1 GREEN; G2 GREEN; G3 GREEN; H1 GREEN; H2 GREEN; H3 GREEN; I1 GREEN; I2a GREEN; I2b GREEN; **Batch I complete; Batch J GREEN; Batch K GREEN; historical Batch L GREEN/FROZEN; expanded Batch M pre-QA complete through M5 GREEN/FROZEN; M6 owner real-device QA is NEXT**'
        break
else:
    raise SystemExit('Current execution position line not found')
text = '\n'.join(lines) + '\n'

resume_start = text.index('## 0. Primary resume order\n')
resume_end = text.index('\n## 1. Closure scope and merge boundary\n', resume_start)
resume = '''## 0. Primary resume order

1. `docs/checkpoints/2026-09-04_PHASE4_BATCH_M5_PREQA_CONSOLIDATION.md` — controlling pre-QA closure checkpoint; M5 GREEN, exact replacement QA candidate frozen and independently validated;
2. `docs/checkpoints/2026-09-04_PHASE4_BATCH_M4_INTER_BATCH_SCOPE_HOLE_CLOSURE_PLAN.md` — repair plan whose six approved-scope holes are now implemented and closed;
3. `docs/checkpoints/2026-09-04_PHASE4_BATCH_M3_PRIOR_BATCH_COMPLETENESS_AUDIT.md` — historical implementation-completeness audit that established the inter-batch holes;
4. `docs/checkpoints/2026-09-04_PHASE4_BATCH_M2_CODE_HEALTH_AUDIT.md` — code-health/static architecture audit that bounded the M5 cleanup;
5. `docs/checkpoints/2026-09-04_PHASE4_BATCH_M1_SCOPE_TRACEABILITY_AUDIT.md` — D-0047 scope traceability audit;
6. `docs/checkpoints/2026-09-04_PHASE4_BATCH_L_FROZEN_QA_CANDIDATE.md` — historical pre-M frozen candidate; preserve unchanged as evidence only, not the active M6 candidate;
7. `docs/checkpoints/2026-09-04_PHASE4_BATCH_K_STABILIZATION_COMPLETE.md` — owner-schema migration proof;
8. `docs/checkpoints/2026-09-04_PHASE4_BATCH_J_BACKUP_IMPORT_COMPLETE.md` — completed backup/import gate;
9. `docs/checkpoints/2026-09-04_PHASE4_BATCH_I2B_TABLE_MODE.md` — completed Batch I closure;
10. `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md` and `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md` — controlling approved scope and historical execution decomposition.
'''
text = text[:resume_start] + resume + text[resume_end:]

section3 = text.index('## 3. Current active Batch M — pre-QA implementation completion\n')
new_tail = '''## 3. Expanded Batch M — pre-QA complete; owner QA next

Batch M was expanded from the historical single owner-QA step so implementation completeness and code health were proven before asking the owner to perform device QA.

The distinction remains controlling:

- M1/M2/M3 prove approved-scope coverage, structural health and historical batch implementation completeness;
- M4 repairs approved-scope holes;
- M5 re-audits the repaired tree, performs bounded justified cleanup, runs full automated gates and freezes one exact replacement candidate;
- M6 is owner real-device QA and is **not** interchangeable with the implementation audits.

### M1 — scope traceability audit — COMPLETE

D-0047 was traced against implementation and exposed four concrete missing/partial capabilities plus two design-fidelity gaps.

### M2 — code-health/static architecture audit — COMPLETE

No broad rewrite was justified. Bounded superseded/dead Android cleanup was identified and later completed in M5 after the scope-hole repairs.

### M3 — prior-batch implementation completeness audit — COMPLETE

No historical Batch 0/A1–L was found falsely GREEN relative to its own written batch contract. The issue was inter-batch allocation: six D-0047-approved requirements had not been fully closed by the historical decomposition.

### M4 — inter-batch scope-hole closure — COMPLETE

All six M3 holes are now implemented:

1. F14 structured languages/proficiencies/training reachable management UI;
2. F15 generic Resource Favorite/Quick Access;
3. I18 richer character-list class/subclass/freshness/optional portrait summary;
4. I21 real-sheet theme/font audition in Application Settings;
5. D06 semantic rules/source badge grammar;
6. D07 bounded semantic state-badge consistency pass.

No schema migration was required.

### M5 — post-repair consolidation + exact replacement candidate — GREEN / FROZEN

Controlling checkpoint:

`docs/checkpoints/2026-09-04_PHASE4_BATCH_M5_PREQA_CONSOLIDATION.md`

M5 completed:

- re-audit of all six M4 repairs;
- fail-closed removal of four superseded Android surfaces plus the obsolete private class/status chain in `CharacterEditorV4`;
- four bounded compiler-warning repairs, including replacement of deprecated `ScrollableTabRow` with `PrimaryScrollableTabRow`;
- full shared tests including owner-lineage migration regression;
- Android debug assemble;
- Desktop build;
- backend type-check;
- ordinary clean repository gate;
- independent exact-SHA candidate validation.

Exact active M6 candidate identity:

- branch `tmp/phase4-m5-frozen-qa-candidate`;
- commit `adc286b3e1305ed706c2ed04d478a43652f6b365`;
- tree `fd1f7feffde082b34cce41248e951a25eed7a004`;
- ordinary clean standard workflow `33911956696` — SUCCESS;
- independent exact-candidate validator workflow `33912322920` — SUCCESS;
- validator artifact ID `9951922423`, name `phase4-m5-frozen-qa-apk`;
- artifact ZIP digest `sha256:5fb8d7f281dbf937def89db4377e9b4157c46343f07721912aa759bb52d6f9fa`;
- exact APK size `35,720,588` bytes;
- exact APK SHA-256 `e31ce44a84cd79260ea2c51c65cb6a63675b1f916998e44d583358d72893c8ee`.

The frozen M5 candidate branch must not be changed. The historical L branch remains frozen historical evidence only.

### M6 — owner real-device QA — NEXT

Owner QA now begins only on the exact M5 candidate above.

Critical migration order:

1. **Do not clear app data before the upgrade test.**
2. Install the exact M5 candidate over the prior owner-QA installation/data so the schema lineage is exercised on the real device.
3. Verify existing campaigns, characters and representative General/Combat/Equipment/Spells/Notes data survive and reopen.
4. Only after the upgrade/data-preservation check is complete may a clean-install pass be used for fresh-install behavior.

Required device/layout matrix:

- phone portrait;
- phone landscape;
- tablet portrait;
- tablet landscape;
- representative larger text setting.

Core QA coverage includes all main and conditional tabs, dirty/Save/Discard behavior, Table mode, Supercompact, backup export/import-as-copy, IME/keyboard safety, rotation/context retention, scrolling/larger text and responsive/master-detail behavior.

QA is quality/real-device acceptance. It does not reopen M1–M5 unless a demonstrated QA finding requires a production repair.

## 4. Remaining approved execution sequence

From the current position:

- **M1 — COMPLETE:** scope traceability audit;
- **M2 — COMPLETE:** code-health/static architecture audit;
- **M3 — COMPLETE:** prior-batch implementation-completeness audit;
- **M4 — COMPLETE:** six inter-batch approved-scope holes closed;
- **M5 — GREEN / FROZEN:** repaired tree re-audited, bounded cleanup complete, full automated gates green, replacement exact QA candidate frozen and independently validated;
- **M6 — NEXT:** owner real-device QA on that exact candidate;
- blocking M6 finding requiring production change -> focused repair -> complete automated gate -> new frozen candidate identity -> repeat affected QA evidence;
- after owner QA acceptance, complete continuity/governance housekeeping including consolidated `docs/DECISIONS.md`, perform the unique-commit/merge-boundary audit, and prepare the Phase 4 merge proposal;
- merge to `main` only after the owner explicitly approves Phase 4 closure/merge.

No DM feature implementation begins before that explicit Phase 4 exit decision.

## 5. Existing baseline that must not regress

Persistent General/Habilidades, Combate, Gestión, Equipo/currencies, Trasfondo including Raza and Religión/Fe, Rasgos, conditional Conjuros with sources/prepared/shared slots, Notas, all six conditional modules, PC Settings, Supercompact/Quick Access/live controls, Table mode, D-0046 derived values/adjustments, adaptive navigation/last-tab restoration, Batch J backup/import/reconciliation and the schema-5 -> current migration path remain protected baseline behavior.

Historical/focused/tmp branches remain intentionally preserved. Do not delete them before the eventual post-merge unique-commit audit.

## 6. Final acceptance boundary

The historical Batch L APK remains frozen evidence of the pre-M audit tree. The exact M5 frozen candidate is now the active owner-QA target.

Phase 4 remains open until:

1. M6 owner phone+tablet QA is completed and accepted on the exact M5 candidate (or a replacement candidate created after a blocking QA repair);
2. blocking findings are resolved and affected QA evidence repeated when necessary;
3. continuity/governance housekeeping is complete;
4. the unique-commit/merge-boundary audit is complete;
5. the owner explicitly approves merge/closure.

Implementation-completeness audits are not substitutes for QA, and QA is not a substitute for verifying that approved work was actually implemented.

## 7. Exact continuation

**Next action: M6 owner real-device QA.**

Use only:

- branch `tmp/phase4-m5-frozen-qa-candidate`;
- commit `adc286b3e1305ed706c2ed04d478a43652f6b365`;
- artifact `9951922423` / `phase4-m5-frozen-qa-apk`;
- APK SHA-256 `e31ce44a84cd79260ea2c51c65cb6a63675b1f916998e44d583358d72893c8ee`.

Begin with the in-place upgrade/data-preservation test; do not clear app data first.

Keep `main` untouched. Keep both historical `tmp/phase4-l-frozen-qa-candidate` and active `tmp/phase4-m5-frozen-qa-candidate` untouched. Do not begin DM work before successful Phase 4 exit and explicit owner approval.
'''
text = text[:section3] + new_tail
path.write_text(text)
print('PROJECT_STATE finalized for M5 GREEN / M6 NEXT')
