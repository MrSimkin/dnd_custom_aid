from pathlib import Path

path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/MainActivity.kt')
text = path.read_text(encoding='utf-8')

missing = '''                    CharacterListScreen(\n                        campaign = selectedCampaign,\n                        repository = characterRepository,\n                        backupRepository = characterBackupRepository,\n                        onBack = {\n'''
fixed = '''                    CharacterListScreen(\n                        campaign = selectedCampaign,\n                        repository = characterRepository,\n                        backupRepository = characterBackupRepository,\n                        closureRepository = characterClosureRepository,\n                        onBack = {\n'''
count = text.count(missing)
assert count == 1, f'second CharacterListScreen: expected exactly one missing closure repository, found {count}'
text = text.replace(missing, fixed, 1)

duplicate = '''                    backupRepository = characterBackupRepository,\n                    closureRepository = characterClosureRepository,\n                    closureRepository = characterClosureRepository,\n                    navigationPreferenceStore = characterNavigationPreferenceStore,\n'''
repaired = '''                    backupRepository = characterBackupRepository,\n                    closureRepository = characterClosureRepository,\n                    navigationPreferenceStore = characterNavigationPreferenceStore,\n'''
count = text.count(duplicate)
assert count == 1, f'CharacterEditor duplicate closure repository: expected exactly one, found {count}'
text = text.replace(duplicate, repaired, 1)

assert text.count('closureRepository = characterClosureRepository,') == 3
path.write_text(text, encoding='utf-8')
