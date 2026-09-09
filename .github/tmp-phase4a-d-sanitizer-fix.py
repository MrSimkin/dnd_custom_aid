from pathlib import Path

for relative in [
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCombatSuccessorV4.kt",
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterDiceRollSuccessorV4.kt",
]:
    path = Path(relative)
    text = path.read_text()
    old = "sanitizeSignedIntV4(it)"
    if text.count(old) != 1:
        raise SystemExit(f"{relative}: expected one {old!r}, found {text.count(old)}")
    path.write_text(text.replace(old, "sanitizeSignedIntegerInputV4(it)", 1))
