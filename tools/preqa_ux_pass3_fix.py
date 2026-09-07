from pathlib import Path

path = Path(__file__).with_name("preqa_ux_pass3.py")
text = path.read_text(encoding="utf-8")
old = '''s = replace_once(
    s,
    '    add(CharacterTabV4.COMBAT)\\n    add(CharacterTabV4.MANAGEMENT)',
    '    add(CharacterTabV4.COMBAT)\\n    add(CharacterTabV4.DICE)\\n    add(CharacterTabV4.MANAGEMENT)',
    "dice visible order",
)
'''
if text.count(old) != 1:
    raise RuntimeError(f"navigation transform patch expected 1 match, found {text.count(old)}")
path.write_text(text.replace(old, "", 1), encoding="utf-8")
print("Pass 03 transformer corrected for enum-derived visible tab order.")
