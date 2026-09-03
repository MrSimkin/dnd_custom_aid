from pathlib import Path

path = Path('shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterClosureRepositoryTest.kt')
text = path.read_text()
marker = 'fun migrationEightAddsCarriedStateWithoutLosingExistingInventoryUsage()'
if marker not in text:
    raise SystemExit('Expected migration-eight test not found')
old = '''            AppDatabase.Schema.migrate(\n                driver = driver,\n                oldVersion = 7,\n                newVersion = AppDatabase.Schema.version,\n            )\n'''
new = '''            AppDatabase.Schema.migrate(\n                driver = driver,\n                oldVersion = 8,\n                newVersion = AppDatabase.Schema.version,\n            )\n'''
if text.count(old) != 1:
    raise SystemExit(f'Expected exactly one migration fixture block, found {text.count(old)}')
path.write_text(text.replace(old, new, 1))
print('Batch F1 migration fixture repaired.')
