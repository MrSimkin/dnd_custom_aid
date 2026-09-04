from pathlib import Path

ui_path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterUi.kt')
ui = ui_path.read_text(encoding='utf-8')


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    assert count == 1, f'{label}: expected exactly one match, found {count}'
    return text.replace(old, new, 1)

ui = replace_once(
    ui,
    '''package io.github.mrsimkin.dndcustomaid.android\n\nimport androidx.activity.compose.rememberLauncherForActivityResult\n''',
    '''package io.github.mrsimkin.dndcustomaid.android\n\nimport android.graphics.BitmapFactory\nimport android.net.Uri\nimport androidx.activity.compose.rememberLauncherForActivityResult\n''',
    'android image imports',
)
ui = replace_once(
    ui,
    '''import androidx.compose.foundation.layout.Arrangement\n''',
    '''import androidx.compose.foundation.Image\nimport androidx.compose.foundation.layout.Arrangement\n''',
    'Image import',
)
ui = replace_once(
    ui,
    '''import androidx.compose.foundation.layout.padding\nimport androidx.compose.foundation.layout.widthIn\n''',
    '''import androidx.compose.foundation.layout.padding\nimport androidx.compose.foundation.layout.size\nimport androidx.compose.foundation.layout.widthIn\n''',
    'size import',
)
ui = replace_once(
    ui,
    '''import androidx.compose.material3.Scaffold\nimport androidx.compose.material3.Text\n''',
    '''import androidx.compose.material3.Scaffold\nimport androidx.compose.material3.Surface\nimport androidx.compose.material3.Text\n''',
    'Surface import',
)
ui = replace_once(
    ui,
    '''import androidx.compose.ui.Alignment\nimport androidx.compose.ui.Modifier\nimport androidx.compose.ui.platform.LocalContext\n''',
    '''import androidx.compose.ui.Alignment\nimport androidx.compose.ui.Modifier\nimport androidx.compose.ui.draw.clip\nimport androidx.compose.ui.graphics.asImageBitmap\nimport androidx.compose.ui.layout.ContentScale\nimport androidx.compose.ui.platform.LocalContext\n''',
    'image UI imports',
)
ui = replace_once(
    ui,
    '''import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupRepository\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus\n''',
    '''import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupRepository\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureRepository\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus\nimport io.github.mrsimkin.dndcustomaid.shared.character.characterListClassSummary\nimport io.github.mrsimkin.dndcustomaid.shared.character.characterListFreshnessLabel\n''',
    'character list authority imports',
)
ui = replace_once(
    ui,
    '''internal fun CharacterListScreen(\n    campaign: Campaign,\n    repository: CharacterRepository,\n    backupRepository: CharacterBackupRepository,\n    onBack: () -> Unit,\n''',
    '''internal fun CharacterListScreen(\n    campaign: Campaign,\n    repository: CharacterRepository,\n    backupRepository: CharacterBackupRepository,\n    closureRepository: CharacterClosureRepository,\n    onBack: () -> Unit,\n''',
    'closure repository parameter',
)
ui = replace_once(
    ui,
    '''    var characters by remember(campaign.id) { mutableStateOf(repository.listCharacters(campaign.id)) }\n    var showCreateDialog by remember { mutableStateOf(false) }\n''',
    '''    var characters by remember(campaign.id) { mutableStateOf(repository.listCharacters(campaign.id)) }\n    var closureStates by remember(campaign.id) {\n        mutableStateOf(\n            characters.associate { character ->\n                character.id to closureRepository.state(character.id)\n            },\n        )\n    }\n    var showCreateDialog by remember { mutableStateOf(false) }\n''',
    'closure state snapshot',
)
ui = replace_once(
    ui,
    '''    val context = LocalContext.current\n\n    fun reload() {\n        characters = repository.listCharacters(campaign.id)\n    }\n''',
    '''    val context = LocalContext.current\n    val nowEpochSeconds = System.currentTimeMillis() / 1000L\n\n    fun reload() {\n        characters = repository.listCharacters(campaign.id)\n        closureStates = characters.associate { character ->\n            character.id to closureRepository.state(character.id)\n        }\n    }\n''',
    'reload authorities and freshness clock',
)
ui = replace_once(
    ui,
    '''                    items(characters, key = { it.id.toString() }) { character ->\n                        CharacterCard(character = character, onClick = { onEdit(character.id) })\n                    }\n''',
    '''                    items(characters, key = { it.id.toString() }) { character ->\n                        CharacterCard(\n                            character = character,\n                            closureState = closureStates[character.id] ?: CharacterClosureState(),\n                            nowEpochSeconds = nowEpochSeconds,\n                            onClick = { onEdit(character.id) },\n                        )\n                    }\n''',
    'character card authority wiring',
)
old_card = '''@Composable\nprivate fun CharacterCard(\n    character: CharacterSheet,\n    onClick: () -> Unit,\n) {\n    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {\n        Column(\n            modifier = Modifier\n                .fillMaxWidth()\n                .padding(horizontal = 12.dp, vertical = 9.dp),\n            verticalArrangement = Arrangement.spacedBy(2.dp),\n        ) {\n            Text(character.name, style = MaterialTheme.typography.titleMedium)\n            val classSummary = if (character.classes.isEmpty()) {\n                "Sin clase registrada"\n            } else {\n                character.classes.joinToString(" / ") { "${it.name} ${it.level}" }\n            }\n            Text(classSummary, style = MaterialTheme.typography.bodyMedium)\n            Text(\n                "${statusLabel(character.status)} · Nivel total ${character.totalLevel}",\n                style = MaterialTheme.typography.bodySmall,\n            )\n        }\n    }\n}\n'''
new_card = '''@Composable\nprivate fun CharacterCard(\n    character: CharacterSheet,\n    closureState: CharacterClosureState,\n    nowEpochSeconds: Long,\n    onClick: () -> Unit,\n) {\n    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {\n        Row(\n            modifier = Modifier\n                .fillMaxWidth()\n                .padding(horizontal = 12.dp, vertical = 9.dp),\n            horizontalArrangement = Arrangement.spacedBy(10.dp),\n            verticalAlignment = Alignment.CenterVertically,\n        ) {\n            CharacterPortraitThumbnailV4(\n                uriRef = closureState.portraitRef,\n                characterName = character.name,\n            )\n            Column(\n                modifier = Modifier.weight(1f),\n                verticalArrangement = Arrangement.spacedBy(2.dp),\n            ) {\n                Text(character.name, style = MaterialTheme.typography.titleMedium)\n                Text(characterListClassSummary(character.classes), style = MaterialTheme.typography.bodyMedium)\n                Text(\n                    "${statusLabel(character.status)} · Nivel total ${character.totalLevel}",\n                    style = MaterialTheme.typography.bodySmall,\n                )\n                Text(\n                    characterListFreshnessLabel(character.updatedAtEpochSeconds, nowEpochSeconds),\n                    style = MaterialTheme.typography.labelSmall,\n                    color = MaterialTheme.colorScheme.onSurfaceVariant,\n                )\n            }\n        }\n    }\n}\n\n@Composable\nprivate fun CharacterPortraitThumbnailV4(\n    uriRef: String?,\n    characterName: String,\n) {\n    val context = LocalContext.current\n    val bitmap = remember(uriRef) {\n        uriRef?.let { raw ->\n            runCatching {\n                context.contentResolver.openInputStream(Uri.parse(raw))?.use { input ->\n                    BitmapFactory.decodeStream(input)?.asImageBitmap()\n                }\n            }.getOrNull()\n        }\n    }\n\n    Surface(\n        modifier = Modifier\n            .size(64.dp)\n            .clip(MaterialTheme.shapes.medium),\n        shape = MaterialTheme.shapes.medium,\n        color = MaterialTheme.colorScheme.surfaceVariant,\n    ) {\n        if (bitmap != null) {\n            Image(\n                bitmap = bitmap,\n                contentDescription = "Retrato de $characterName",\n                modifier = Modifier.size(64.dp),\n                contentScale = ContentScale.Crop,\n            )\n        } else {\n            Box(contentAlignment = Alignment.Center) {\n                Text(\n                    text = characterName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "—",\n                    style = MaterialTheme.typography.titleLarge,\n                    color = MaterialTheme.colorScheme.onSurfaceVariant,\n                )\n            }\n        }\n    }\n}\n'''
ui = replace_once(ui, old_card, new_card, 'character card implementation')

assert 'characterListClassSummary(character.classes)' in ui
assert 'characterListFreshnessLabel(character.updatedAtEpochSeconds, nowEpochSeconds)' in ui
assert 'closureState.portraitRef' in ui
assert 'CharacterPortraitThumbnailV4' in ui
ui_path.write_text(ui, encoding='utf-8')

main_path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/MainActivity.kt')
main = main_path.read_text(encoding='utf-8')
needle = '''                    repository = characterRepository,\n                    backupRepository = characterBackupRepository,\n'''
replacement = '''                    repository = characterRepository,\n                    backupRepository = characterBackupRepository,\n                    closureRepository = characterClosureRepository,\n'''
count = main.count(needle)
assert count == 2, f'MainActivity CharacterListScreen authority wiring: expected 2 matches, found {count}'
main = main.replace(needle, replacement)
assert main.count('closureRepository = characterClosureRepository,') >= 3
main_path.write_text(main, encoding='utf-8')
