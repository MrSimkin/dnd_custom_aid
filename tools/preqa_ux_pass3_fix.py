from pathlib import Path

path = Path(__file__).with_name("preqa_ux_pass3.py")
text = path.read_text(encoding="utf-8")

visible_order_block = '''s = replace_once(
    s,
    '    add(CharacterTabV4.COMBAT)\\n    add(CharacterTabV4.MANAGEMENT)',
    '    add(CharacterTabV4.COMBAT)\\n    add(CharacterTabV4.DICE)\\n    add(CharacterTabV4.MANAGEMENT)',
    "dice visible order",
)
'''
if text.count(visible_order_block) != 1:
    raise RuntimeError(
        f"navigation transform patch expected 1 match, found {text.count(visible_order_block)}"
    )
text = text.replace(visible_order_block, "", 1)

old_rail_anchor = '''    '    CharacterTabV4.COMBAT -> "CO"\\n    CharacterTabV4.MANAGEMENT -> "GE"',
    '    CharacterTabV4.COMBAT -> "CO"\\n    CharacterTabV4.DICE -> "TD"\\n    CharacterTabV4.MANAGEMENT -> "GE"',
'''
new_rail_anchor = '''    '    CharacterTabV4.COMBAT -> "CO"\\n    CharacterTabV4.MANAGEMENT -> "GT"',
    '    CharacterTabV4.COMBAT -> "CO"\\n    CharacterTabV4.DICE -> "TD"\\n    CharacterTabV4.MANAGEMENT -> "GT"',
'''
if text.count(old_rail_anchor) != 1:
    raise RuntimeError(f"rail transform patch expected 1 match, found {text.count(old_rail_anchor)}")
text = text.replace(old_rail_anchor, new_rail_anchor, 1)

old_combat_arg = '                            combatEntries = combatDraft,\n'
new_combat_arg = '                            combatEntries = combatEntries,\n'
if text.count(old_combat_arg) != 1:
    raise RuntimeError(f"dice combat argument patch expected 1 match, found {text.count(old_combat_arg)}")
text = text.replace(old_combat_arg, new_combat_arg, 1)

path.write_text(text, encoding="utf-8")
print("Pass 03 transformer corrected for navigation, rail, and in-scope combat entries.")
