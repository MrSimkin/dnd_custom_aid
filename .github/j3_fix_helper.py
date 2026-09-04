from pathlib import Path

path = Path('.github/j3_android_backup_patch.py')
text = path.read_text()
old = '''replace_exact(
    main,
    "                CharacterListScreen(\\n"
    "                    campaign = selectedCampaign,\\n"
    "                    repository = characterRepository,\\n",
    "                CharacterListScreen(\\n"
    "                    campaign = selectedCampaign,\\n"
    "                    repository = characterRepository,\\n"
    "                    backupRepository = characterBackupRepository,\\n",
    count=2,
)
'''
new = '''replace_exact(
    main,
    "                CharacterListScreen(\\n"
    "                    campaign = selectedCampaign,\\n"
    "                    repository = characterRepository,\\n",
    "                CharacterListScreen(\\n"
    "                    campaign = selectedCampaign,\\n"
    "                    repository = characterRepository,\\n"
    "                    backupRepository = characterBackupRepository,\\n",
)
replace_exact(
    main,
    "                    CharacterListScreen(\\n"
    "                        campaign = selectedCampaign,\\n"
    "                        repository = characterRepository,\\n",
    "                    CharacterListScreen(\\n"
    "                        campaign = selectedCampaign,\\n"
    "                        repository = characterRepository,\\n"
    "                        backupRepository = characterBackupRepository,\\n",
)
'''
if text.count(old) != 1:
    raise RuntimeError(f'expected one stale MainActivity call-site block, found {text.count(old)}')
path.write_text(text.replace(old, new))
