from pathlib import Path

source_path = Path("scripts/migrate_phase4a_t2.py")
source = source_path.read_text(encoding="utf-8")
old = '''# Reset all dice-result channels when target/category changes.
replace_once(
    DICE,
    ''' + "'''" + '''                            firstDie = null\\n                            secondDie = null\\n                            damageResultText = null\\n''' + "'''" + ''',
    ''' + "'''" + '''                            firstDie = null\\n                            secondDie = null\\n                            customDieResult = null\\n                            damageResultText = null\\n                            damageVisualsText = ""\\n''' + "'''" + ''',
)
replace_once(
    DICE,
    ''' + "'''" + '''                            firstDie = null\\n                            secondDie = null\\n                            damageResultText = null\\n                        },\\n                        modifier = Modifier.weight(1.35f),\\n''' + "'''" + ''',
    ''' + "'''" + '''                            firstDie = null\\n                            secondDie = null\\n                            customDieResult = null\\n                            damageResultText = null\\n                            damageVisualsText = ""\\n                        },\\n                        modifier = Modifier.weight(1.35f),\\n''' + "'''" + ''',
)
'''
new = '''# Reset all dice-result channels when target/category changes.
def replace_all_exact(path: str, old: str, new: str, expected: int) -> None:
    target = Path(path)
    text = target.read_text(encoding="utf-8")
    count = text.count(old)
    if count != expected:
        raise RuntimeError(f"Expected {expected} matches in {path}, found {count}: {old[:120]!r}")
    target.write_text(text.replace(old, new), encoding="utf-8")

replace_all_exact(
    DICE,
    ''' + "'''" + '''                            firstDie = null\\n                            secondDie = null\\n                            damageResultText = null\\n''' + "'''" + ''',
    ''' + "'''" + '''                            firstDie = null\\n                            secondDie = null\\n                            customDieResult = null\\n                            damageResultText = null\\n                            damageVisualsText = ""\\n''' + "'''" + ''',
    expected=2,
)
'''
if old not in source:
    raise RuntimeError("Retry patch could not find the original reset block")
patched = source.replace(old, new, 1)
exec(compile(patched, str(source_path), "exec"), {"__name__": "__main__", "__file__": str(source_path)})
