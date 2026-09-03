from pathlib import Path

path = Path("docs/PROJECT_STATE.md")
text = path.read_text(encoding="utf-8")

def replace_once(old: str, new: str, label: str) -> None:
    global text
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected exactly one match, found {count}")
    text = text.replace(old, new, 1)

replace_once(
    "**Current execution position:** Batch 0 complete; A1 GREEN; A2 GREEN; B1 GREEN; B2 GREEN; C GREEN; **Batch D active**  ",
    "**Current execution position:** Batch 0 complete; A1 GREEN; A2 GREEN; B1 GREEN; B2 GREEN; C GREEN; D GREEN; **Batch E active**  ",
    "execution position",
)

replace_once(
    """1. `docs/checkpoints/2026-09-03_PHASE4_BATCH_C_PC_SETTINGS.md` — completed C gate and exact continuation into Gestión;
2. `docs/checkpoints/2026-09-03_PHASE4_BATCH_B2_ORDERING_CONTEXT_DRAG.md` — completed B2 gate;
3. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md` — small recoverable execution batches;
4. `docs/checkpoints/2026-09-03_PHASE4_BATCH_A2B_PERSISTENCE.md` — completed schema-7 persistence gate;
5. `docs/checkpoints/2026-09-03_PHASE4_BATCH_A2A_SCHEMA_DOMAIN.md` — completed schema/domain gate;
6. `docs/checkpoints/2026-09-03_PHASE4_BATCH0_A1_HOUSEKEEPING_AND_CATALOG.md` — completed housekeeping/A1 checkpoint;
7. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_IMPLEMENTATION_MAP.md` — higher-level A–J map and final QA matrix;
8. `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md` — approved product/design scope;
9. `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md` — class/subclass/module audit.
""",
    """1. `docs/checkpoints/2026-09-03_PHASE4_BATCH_D_GESTION.md` — completed D gate and exact continuation into E;
2. `docs/checkpoints/2026-09-03_PHASE4_BATCH_C_PC_SETTINGS.md` — completed C gate;
3. `docs/checkpoints/2026-09-03_PHASE4_BATCH_B2_ORDERING_CONTEXT_DRAG.md` — completed B2 gate;
4. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md` — small recoverable execution batches;
5. `docs/checkpoints/2026-09-03_PHASE4_BATCH_A2B_PERSISTENCE.md` — completed schema-7 persistence gate;
6. `docs/checkpoints/2026-09-03_PHASE4_BATCH_A2A_SCHEMA_DOMAIN.md` — completed schema/domain gate;
7. `docs/checkpoints/2026-09-03_PHASE4_BATCH0_A1_HOUSEKEEPING_AND_CATALOG.md` — completed housekeeping/A1 checkpoint;
8. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_IMPLEMENTATION_MAP.md` — higher-level A–J map and final QA matrix;
9. `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md` — approved product/design scope;
10. `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md` — class/subclass/module audit.
""",
    "resume order",
)

start = text.index("## 8. Current batch — D Gestión live maintenance")
end = text.index("## 9. Existing baseline that must not regress")
replacement = """## 8. Batch D — Gestión live maintenance — GREEN

Controlling checkpoint:

- `docs/checkpoints/2026-09-03_PHASE4_BATCH_D_GESTION.md`.

Implemented:

- Conditions + Exhaustion;
- Concentration;
- generic Resources with quick operations and exact editing;
- Short/Long Rest preview + selective apply;
- manual/custom recovery remains informational only;
- temporary effects;
- Inspiration;
- contextual death saves at 0 HP;
- reconciliation checkpoints;
- responsive narrow/wide Gestión layout;
- immediate operational persistence without consuming unrelated structural drafts.

Verification:

- D1 rest-operations workflow `33797081412` — PASS;
- first D2 workflow `33808464225` exposed one Android Kotlin smart-cast compile failure;
- repair commit `2a5d9b35669e62ac44b92bdca9fbcf649ba5fcd0` fixed that compile issue and hidden-FIXED validation after switching to Manual/None;
- final controlling head `64033be2632012cb6cac19728ebecb1d44ec553b`;
- final controlling workflow `33809045740` — PASS;
- backend PASS;
- shared/Kotlin tests PASS;
- Android debug assembly PASS;
- Desktop build PASS;
- APK upload PASS.

Hit Dice remain review-only in the Rest preview; no automatic recovery rule is imposed across mixed D&D 5e / 5.5e / custom characters.

**Batch D gate is closed GREEN.**

## 9. Current batch — E General + Habilidades + Combate

Goal: close the approved General/Habilidades/Combate character data and operational improvements without breaking D-0046 derived-value semantics.

Batch E targets:

- class/subclass/source presentation and non-enforcing hit-die suggestions;
- freshness + portrait/token;
- defenses/senses/special movement;
- Passive Insight + Passive Investigation;
- custom skills integrated into both existing Habilidades organization modes;
- Combat action type + damage/effect at a glance;
- quick HP damage/heal/temp operations while exact editing remains available;
- contextual death saves where useful;
- Favorites/Quick Access integration for relevant E surfaces;
- simple dice roller without automatic rules-resolution scope creep.

Gate E requires D-0046 regression coverage, custom-skill calculations/presentation foundations, HP-operation tests and Android assembly.

"""
text = text[:start] + replacement + text[end:]

for old, new in [
    ("## 9. Existing baseline that must not regress", "## 10. Existing baseline that must not regress"),
    ("## 10. Final acceptance boundary", "## 11. Final acceptance boundary"),
    ("## 11. Merge boundary", "## 12. Merge boundary"),
    ("## 12. Exact continuation", "## 13. Exact continuation"),
]:
    replace_once(old, new, old)

old_tail = """Resume **Batch D — Gestión live character maintenance** on `implementation/phase4-character-closure`.

Build/test pure rest/recovery operations first, then wire the Gestión surface over existing core + closure repositories. Rest must preview proposed changes and apply only explicitly selected operations. Checkpoint D before beginning E."""
new_tail = """Resume **Batch E — General + Habilidades + Combate** on `implementation/phase4-character-closure`.

Start with pure/testable E operations and calculated-value helpers, preserving D-0046. Then wire class/subclass/source presentation, defenses/senses/movement, Passive Insight/Investigation, custom skills, quick HP, Combat summary improvements, Favorites and the bounded simple dice roller. Checkpoint E before beginning F."""
replace_once(old_tail, new_tail, "exact continuation")

path.write_text(text, encoding="utf-8")
print("PROJECT_STATE advanced to Batch E.")
