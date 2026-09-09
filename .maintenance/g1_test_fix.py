from pathlib import Path

path = Path('shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterFollowupFoundationTest.kt')
text = path.read_text()
old = 'assertEquals(listOf("Cobre", "Plata", "Electro", "Oro", "Platino"),'
new = 'assertEquals(listOf("Cobre", "Plata", "Electrum", "Oro", "Platino"),'
count = text.count(old)
if count != 2:
    raise SystemExit(f'currency expectation: expected exactly two matches, found {count}')
path.write_text(text.replace(old, new))
