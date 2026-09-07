from pathlib import Path

path = Path(__file__).with_name("preqa_ux_pass4.py")
text = path.read_text(encoding="utf-8")
old = '''s = replace_once(
    s,
    'horizontalArrangement = Arrangement.spacedBy(4.dp),',
    'horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),',
    "spell source outer gap",
)
s = replace_once(
    s,
    'horizontalArrangement = Arrangement.spacedBy(4.dp),',
    'horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),',
    "spell source row gap",
)
'''
new = '''spell_source_gap = 'horizontalArrangement = Arrangement.spacedBy(4.dp),'
spell_source_gap_count = s.count(spell_source_gap)
if spell_source_gap_count != 2:
    raise RuntimeError(f"spell source gaps: expected exactly 2 matches, found {spell_source_gap_count}")
s = s.replace(
    spell_source_gap,
    'horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),',
    2,
)
'''
if text.count(old) != 1:
    raise RuntimeError(f"pass4 gap patch expected 1 block, found {text.count(old)}")
path.write_text(text.replace(old, new, 1), encoding="utf-8")
print("Pass 04 transformer corrected for both spell-source gaps.")
