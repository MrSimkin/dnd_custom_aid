from pathlib import Path
import re

path = Path('docs/PROJECT_STATE.md')
text = path.read_text(encoding='utf-8')

old_exec = '**Current execution position:** Batch 0 complete; A1 GREEN; A2 GREEN; B1 GREEN; B2 GREEN; C GREEN; D GREEN; E GREEN; F GREEN; G1 GREEN; G2 GREEN; G3 GREEN; H1 GREEN; H2 GREEN; H3 GREEN; I1 GREEN; I2a GREEN; I2b GREEN; **Batch I complete; Batch J GREEN; Batch K GREEN; Batch L GREEN/FROZEN; Batch M active**'
new_exec = '**Current execution position:** Batch 0 complete; A1 GREEN; A2 GREEN; B1 GREEN; B2 GREEN; C GREEN; D GREEN; E GREEN; F GREEN; G1 GREEN; G2 GREEN; G3 GREEN; H1 GREEN; H2 GREEN; H3 GREEN; I1 GREEN; I2a GREEN; I2b GREEN; **Batch I complete; Batch J GREEN; Batch K GREEN; historical Batch L GREEN/FROZEN; expanded Batch M active — M1/M2/M3 audits complete, M4 required, owner QA deferred to M6**'
assert text.count(old_exec) == 1, 'execution-position marker mismatch'
text = text.replace(old_exec, new_exec, 1)

primary = '''## 0. Primary resume order

1. `docs/checkpoints/2026-09-04_PHASE4_BATCH_M3_PRIOR_BATCH_COMPLETENESS_AUDIT.md` — verifies that prior historical batches were actually implemented to their written contracts and identifies inter-batch D-0047 scope holes;
2. `docs/checkpoints/2026-09-04_PHASE4_BATCH_M4_INTER_BATCH_SCOPE_HOLE_CLOSURE_PLAN.md` — required repair batch for the holes found by M3;
3. `docs/checkpoints/2026-09-04_PHASE4_BATCH_M2_CODE_HEALTH_AUDIT.md` — code-health/static architecture audit and bounded cleanup findings;
4. `docs/checkpoints/2026-09-04_PHASE4_BATCH_M1_SCOPE_TRACEABILITY_AUDIT.md` — D-0047 scope traceability audit that first exposed the missing/partial approved requirements;
5. `docs/checkpoints/2026-09-04_PHASE4_BATCH_L_FROZEN_QA_CANDIDATE.md` — historical frozen candidate identity; preserve unchanged, but it is no longer the future final QA candidate after M4 production repairs;
6. `docs/checkpoints/2026-09-04_PHASE4_BATCH_K_STABILIZATION_COMPLETE.md` — owner-schema migration proof and exact K stabilization gate;
7. `docs/checkpoints/2026-09-04_PHASE4_BATCH_J_BACKUP_IMPORT_COMPLETE.md` — completed Batch J backup/import gate;
8. `docs/checkpoints/2026-09-04_PHASE4_BATCH_I2B_TABLE_MODE.md` — completed Batch I closure;
9. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md` — historical A–M execution decomposition; its original single “M = owner QA” step is now expanded by M1–M6 and does not override this current state;
10. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_IMPLEMENTATION_MAP.md` and `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md` — higher-level implementation scope and controlling owner-approved closure requirements.

'''
text, count = re.subn(r'## 0\. Primary resume order\n.*?(?=## 1\. Closure scope and merge boundary)', primary, text, count=1, flags=re.S)
assert count == 1, 'primary-resume section mismatch'

section3 = '''## 3. Current active Batch M — pre-QA implementation completion

Batch M is now an expanded pre-QA consolidation sequence rather than a single immediate owner-QA step.

The distinction is deliberate:

- M1/M2/M3 ask whether approved functionality exists, whether the implementation is structurally healthy, and whether historical batches were actually carried out as promised;
- they do **not** judge real-device quality, ergonomics or visual acceptance;
- owner phone/tablet QA is deferred until missing implementation is closed and a new exact candidate is frozen.

### M1 — scope traceability audit — COMPLETE

M1 traced D-0047 against current implementation and identified four concrete missing/partial capabilities plus two design-fidelity areas.

### M2 — code-health/static architecture audit — COMPLETE

M2 found bounded historical Android layering/dead-code/duplication debt but no justification for a broad rewrite.

### M3 — prior-batch implementation completeness audit — COMPLETE

M3 audited Batch 0/A1 through L against what each historical batch explicitly promised.

Result:

- **no earlier batch is currently found to be falsely GREEN relative to its own written batch contract**;
- the problem is instead that the A–J decomposition failed to allocate/fully close several approved D-0047 requirements;
- therefore Phase 4 is not yet implementation-complete even though the historical batches were genuinely executed.

The six M3 holes are:

1. F14 structured languages/proficiencies/training — missing reachable management UI;
2. F15 generic Resource Favorite/Quick Access — partial;
3. I18 character-list subclass/freshness/optional portrait summary — partial;
4. I21 real-sheet theme/font audition in Application Settings — missing;
5. D06 semantic rules/source badge treatment — partial;
6. D07 consistent semantic state-badge grammar — partial/inconsistent.

### M4 — inter-batch scope-hole closure — REQUIRED / NEXT IMPLEMENTATION BATCH

M4 closes exactly those six approved-scope holes. It is not a feature-expansion batch and must not become a broad redesign.

Any M4 production change means the historical Batch L APK remains immutable historical evidence only. It must not be used as the final owner-QA candidate for the repaired tree.

### M5 — post-repair consolidation and replacement candidate — PLANNED

After M4:

- re-run D-0047 traceability against the repaired tree;
- perform only the bounded M2 cleanup still justified after the repair;
- run the complete automated regression/build gate;
- verify migration and protected baseline behavior remain green;
- freeze and record a **new** exact candidate identity, workflow, artifact and hashes.

### M6 — owner real-device QA — DEFERRED

Only the new M5-frozen candidate proceeds to owner QA across phone/tablet portrait/landscape and representative larger text, including upgrade/data preservation and the full interaction matrix.

The original historical L candidate remains frozen and must not be patched in place.

'''
text, count = re.subn(r'## 3\. Current active batch — M owner real-device QA\n.*?(?=## 4\. Remaining approved execution sequence)', section3, text, count=1, flags=re.S)
assert count == 1, 'section 3 mismatch'

section4 = '''## 4. Remaining approved execution sequence

From the current position:

- **M1 — COMPLETE:** D-0047 scope traceability audit;
- **M2 — COMPLETE:** code-health/static architecture audit;
- **M3 — COMPLETE:** historical prior-batch implementation-completeness audit;
- **M4 — REQUIRED / NEXT:** close the six inter-batch approved-scope holes recorded by M3;
- **M5 — AFTER M4:** bounded cleanup/re-audit, complete automated gate, and freeze a replacement exact QA candidate;
- **M6 — AFTER M5:** owner real-device QA on that replacement candidate;
- blocking M6 findings -> focused repair batch -> complete automated gate -> new frozen candidate identity -> restart affected QA evidence;
- after owner QA acceptance, complete continuity/governance housekeeping including consolidated `docs/DECISIONS.md`, perform the unique-commit/merge-boundary audit, and prepare the Phase 4 merge proposal;
- merge to `main` only after the owner explicitly approves Phase 4 closure/merge.

No DM feature implementation begins before that explicit Phase 4 exit decision.

'''
text, count = re.subn(r'## 4\. Remaining approved execution sequence\n.*?(?=## 5\. Existing baseline that must not regress)', section4, text, count=1, flags=re.S)
assert count == 1, 'section 4 mismatch'

section6 = '''## 6. Final acceptance boundary

The historical Batch L APK remains frozen evidence of the pre-M audit tree, but it is **not** the future final QA candidate once M4 changes production code.

Phase 4 remains open until:

1. M4 closes all six M3 implementation holes;
2. M5 re-audits the repaired tree, completes bounded justified cleanup, passes the full technical gate and freezes a new exact candidate;
3. M6 owner phone+tablet QA is completed and accepted on that new candidate;
4. blocking findings are resolved through a new candidate when necessary;
5. continuity/governance housekeeping is complete;
6. the unique-commit/merge-boundary audit is complete;
7. the owner explicitly approves merge/closure.

Implementation-completeness audits are not substitutes for QA, and QA is not a substitute for verifying that approved work was actually implemented.

'''
text, count = re.subn(r'## 6\. Final acceptance boundary\n.*?(?=## 7\. Exact continuation)', section6, text, count=1, flags=re.S)
assert count == 1, 'section 6 mismatch'

section7 = '''## 7. Exact continuation

Do **not** begin owner QA on the historical Batch L candidate now.

Current sequence:

1. M1 scope audit — complete;
2. M2 code-health audit — complete;
3. M3 prior-batch implementation-completeness audit — complete;
4. **M4 inter-batch scope-hole closure — next implementation batch**;
5. M5 full re-audit/gate + replacement frozen candidate;
6. M6 owner real-device QA.

For the present continuity update, M4 is planned but should not be treated as already executed merely because its plan exists.

Keep `main` untouched. Keep `tmp/phase4-l-frozen-qa-candidate` untouched. Do not patch the historical L candidate in place. Do not begin DM work before successful Phase 4 exit and explicit owner approval.
'''
text, count = re.subn(r'## 7\. Exact continuation\n.*\Z', section7, text, count=1, flags=re.S)
assert count == 1, 'section 7 mismatch'

# Guard against stale current-state wording.
assert 'Current active batch — M owner real-device QA' not in text
assert 'Do **not** make another code change now.' not in text
assert 'M3 — prior-batch implementation completeness audit — COMPLETE' in text
assert 'M4 — inter-batch scope-hole closure — REQUIRED / NEXT IMPLEMENTATION BATCH' in text
assert 'M6 — owner real-device QA — DEFERRED' in text

path.write_text(text, encoding='utf-8')
