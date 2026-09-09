from pathlib import Path


def replace_once(text, old, new, label):
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{label}: expected exactly one match, found {count}")
    return text.replace(old, new, 1)


equipment_path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEquipmentClosureV4.kt')
text = equipment_path.read_text()

text = replace_once(
    text,
    'import io.github.mrsimkin.dndcustomaid.shared.character.CharacterPresentationOrder\n',
    'import io.github.mrsimkin.dndcustomaid.shared.character.CharacterPresentationOrder\n'
    'import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResource\n'
    'import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResourcePlacement\n'
    'import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResourceSuccessorConfiguration\n'
    'import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrackableValueKind\n',
    'equipment imports',
)

text = replace_once(
    text,
    '''internal fun CharacterEquipmentClosureTabV4(\n    draft: CharacterEquipmentDraftV4,\n    onDraftChange: (CharacterEquipmentDraftV4) -> Unit,\n    structuralEditingEnabled: Boolean,\n    wide: Boolean,\n    hapticsEnabled: Boolean,\n) {''',
    '''internal fun CharacterEquipmentClosureTabV4(\n    draft: CharacterEquipmentDraftV4,\n    onDraftChange: (CharacterEquipmentDraftV4) -> Unit,\n    armorClass: Int,\n    resources: List<CharacterResource>,\n    onResourceValueChange: (Uuid, Int) -> Unit,\n    structuralEditingEnabled: Boolean,\n    wide: Boolean,\n    hapticsEnabled: Boolean,\n) {''',
    'equipment signature',
)

text = replace_once(
    text,
    '''    val canReorderSpecial = structuralEditingEnabled && specialOrder == CharacterPresentationOrder.MANUAL && query.isEmptyF2()\n    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)\n''',
    '''    val canReorderSpecial = structuralEditingEnabled && specialOrder == CharacterPresentationOrder.MANUAL && query.isEmptyF2()\n    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)\n    val settingsContext = LocalCharacterPcSettingsContextV4.current\n    val successorState = settingsContext?.successorState\n    val resourceConfigurations = successorState?.resourceConfigurations.orEmpty().associateBy { it.resourceId }\n    val equipmentResources = resources\n        .filter { resource -> CharacterResourcePlacement.EQUIPMENT in (resourceConfigurations[resource.id]?.placements ?: emptySet()) }\n        .sortedBy { it.sortOrder }\n    val equippedItems = draft.items.filter { it.equipped }.sortedBy { it.sortOrder }\n''',
    'equipment successor projections',
)

marker = '''        item {\n            CompactCurrenciesF2(\n                currencies = draft.currencies,\n                wide = wide,\n                structuralEditingEnabled = structuralEditingEnabled,\n                onCurrenciesChange = { onDraftChange(draft.copy(currencies = it)) },\n                onAddCurrency = {\n                    customCurrencyName = ""\n                    customCurrencyAmount = "0"\n                    addCurrencyOpen = true\n                },\n            )\n        }\n'''
replacement = '''        item {\n            EquipmentDefensesAndResourcesG1(\n                armorClass = armorClass,\n                equippedItems = equippedItems,\n                resources = equipmentResources,\n                configurations = resourceConfigurations,\n                onResourceValueChange = { resourceId, value ->\n                    onResourceValueChange(resourceId, value)\n                    haptic(CharacterHapticEventV4.RESOURCE)\n                },\n            )\n        }\n\n        item {\n            CompactCurrenciesF2(\n                currencies = draft.currencies,\n                wide = wide,\n                structuralEditingEnabled = structuralEditingEnabled,\n                onCurrenciesChange = { onDraftChange(draft.copy(currencies = it)) },\n                onAddCurrency = {\n                    customCurrencyName = ""\n                    customCurrencyAmount = "0"\n                    addCurrencyOpen = true\n                },\n            )\n        }\n\n        item {\n            EquipmentValuablesG1(\n                value = successorState?.preferences?.valuablesText.orEmpty(),\n                editingEnabled = structuralEditingEnabled && settingsContext != null,\n                onValueChange = { updated ->\n                    settingsContext?.let { context ->\n                        val current = context.successorState\n                        context.onSuccessorStateChange(\n                            current.copy(preferences = current.preferences.copy(valuablesText = updated)),\n                        )\n                    }\n                },\n            )\n        }\n'''
text = replace_once(text, marker, replacement, 'equipment G1 cards')

text = text.replace('label = "Uso de cantidad",', 'label = "Tipo de consumo",')
if text.count('label = "Tipo de consumo",') != 2:
    raise SystemExit('expected two Tipo de consumo labels')
text = text.replace('label = { Text("Cantidad por uso rápido") },', 'label = { Text("Descuento por uso") },')
if text.count('label = { Text("Descuento por uso") },') != 2:
    raise SystemExit('expected two Descuento por uso labels')
text = replace_once(
    text,
    '''private fun consumableLabelF2(kind: CharacterConsumableKind): String = when (kind) {\n    CharacterConsumableKind.NONE -> "Normal"\n    CharacterConsumableKind.CONSUMABLE -> "Consumible"\n    CharacterConsumableKind.AMMUNITION -> "Munición"\n}\n''',
    '''private fun consumableLabelF2(kind: CharacterConsumableKind): String = when (kind) {\n    CharacterConsumableKind.NONE -> "No consume cantidad"\n    CharacterConsumableKind.CONSUMABLE -> "Consumible"\n    CharacterConsumableKind.AMMUNITION -> "Munición"\n}\n''',
    'consumable labels',
)

insertion_point = '''@Composable\nprivate fun CompactCurrenciesF2(\n'''
if text.count(insertion_point) != 1:
    raise SystemExit('currency insertion point missing or duplicated')
new_composables = '''@Composable
private fun EquipmentDefensesAndResourcesG1(
    armorClass: Int,
    equippedItems: List<CharacterInventoryItem>,
    resources: List<CharacterResource>,
    configurations: Map<Uuid, CharacterResourceSuccessorConfiguration>,
    onResourceValueChange: (Uuid, Int) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(5.dp), vertical = appSpacingV4(4.dp)),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        ) {
            Text("Defensas y recursos", style = MaterialTheme.typography.titleSmall)
            val equippedText = equippedItems.joinToString(", ") { item ->
                if (item.quantity > 1) "${item.name} ×${item.quantity}" else item.name
            }.ifBlank { "—" }
            Text(
                "CA $armorClass · Equipado: $equippedText",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            CharacterHelpV4(
                "Equipo proyecta la misma CA y los mismos objetos marcados como Equipado que General/Defensas. La ficha aún no clasifica armadura y escudo como categorías estructuradas separadas.",
            )
            if (resources.isNotEmpty()) {
                Text("Recursos de Equipo", style = MaterialTheme.typography.labelMedium)
                resources.forEach { resource ->
                    val configuration = configurations[resource.id] ?: return@forEach
                    EquipmentResourceRowG1(
                        resource = resource,
                        configuration = configuration,
                        onValueChange = { value -> onResourceValueChange(resource.id, value) },
                    )
                }
            }
        }
    }
}

@Composable
private fun EquipmentResourceRowG1(
    resource: CharacterResource,
    configuration: CharacterResourceSuccessorConfiguration,
    onValueChange: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            resource.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        when (configuration.valueKind) {
            CharacterTrackableValueKind.BINARY -> {
                val active = resource.currentValue > 0
                EquipmentToggleG1(
                    label = if (active) "Activo" else "Inactivo",
                    selected = active,
                    onClick = { onValueChange(if (active) 0 else 1) },
                )
            }
            CharacterTrackableValueKind.COUNTER,
            CharacterTrackableValueKind.CURRENT_MAX,
            -> {
                val maximum = if (configuration.valueKind == CharacterTrackableValueKind.CURRENT_MAX) resource.maxValue else null
                EquipmentStepG1("−", enabled = resource.currentValue > 0) {
                    onValueChange((resource.currentValue - 1).coerceAtLeast(0))
                }
                Text(
                    maximum?.let { "${resource.currentValue}/$it" } ?: resource.currentValue.toString(),
                    style = MaterialTheme.typography.labelLarge,
                )
                EquipmentStepG1("+", enabled = maximum?.let { resource.currentValue < it } ?: true) {
                    val next = resource.currentValue + 1
                    onValueChange(maximum?.let { next.coerceAtMost(it) } ?: next)
                }
            }
        }
    }
}

@Composable
private fun EquipmentToggleG1(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.heightIn(min = 30.dp).clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.padding(horizontal = appSpacingV4(7.dp), vertical = appSpacingV4(3.dp)),
            contentAlignment = Alignment.Center,
        ) { Text(label, style = MaterialTheme.typography.labelSmall) }
    }
}

@Composable
private fun EquipmentStepG1(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.heightIn(min = 30.dp).clickable(enabled = enabled, onClick = onClick),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = if (enabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.padding(horizontal = appSpacingV4(8.dp), vertical = appSpacingV4(3.dp)),
            contentAlignment = Alignment.Center,
        ) { Text(label, style = MaterialTheme.typography.labelLarge) }
    }
}

@Composable
private fun EquipmentValuablesG1(
    value: String,
    editingEnabled: Boolean,
    onValueChange: (String) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(5.dp), vertical = appSpacingV4(4.dp)),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
        ) {
            Text("Gemas / arte", style = MaterialTheme.typography.titleSmall)
            OutlinedTextField(
                value = value,
                onValueChange = { if (editingEnabled) onValueChange(it) },
                readOnly = !editingEnabled,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Gemas, obras de arte, joyas u otros valores") },
                minLines = 1,
                maxLines = 4,
            )
        }
    }
}

'''
text = text.replace(insertion_point, new_composables + insertion_point, 1)
equipment_path.write_text(text)

editor_path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt')
editor = editor_path.read_text()
old_call = '''                        CharacterTabV4.EQUIPMENT -> CharacterEquipmentClosureTabV4(\n                            draft = equipmentDraft,\n                            onDraftChange = ::updateEquipmentDraft,\n                            structuralEditingEnabled = structuralEditingEnabled,\n                            wide = wide,\n                            hapticsEnabled = closureState.hapticsEnabled,\n                        )'''
new_call = '''                        CharacterTabV4.EQUIPMENT -> CharacterEquipmentClosureTabV4(\n                            draft = equipmentDraft,\n                            onDraftChange = ::updateEquipmentDraft,\n                            armorClass = stored.armorClass,\n                            resources = stored.resources,\n                            onResourceValueChange = { resourceId, value ->\n                                stored.resources.firstOrNull { it.id == resourceId }?.let { resource ->\n                                    val normalized = resource.maxValue?.let { value.coerceIn(0, it) } ?: value.coerceAtLeast(0)\n                                    if (normalized != resource.currentValue) {\n                                        persistOperationalSheet(\n                                            stored.copy(\n                                                resources = stored.resources.map { item ->\n                                                    if (item.id == resourceId) item.copy(currentValue = normalized) else item\n                                                },\n                                            ),\n                                        )\n                                    }\n                                }\n                            },\n                            structuralEditingEnabled = structuralEditingEnabled,\n                            wide = wide,\n                            hapticsEnabled = closureState.hapticsEnabled,\n                        )'''
editor = replace_once(editor, old_call, new_call, 'editor equipment call')
editor_path.write_text(editor)

repo_path = Path('shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterRepository.kt')
repo = repo_path.read_text()
repo = replace_once(
    repo,
    'CharacterCurrency(key, name, amount.toInt(), sortOrder.toInt(), isDefault != 0L)',
    'CharacterCurrency(key, if (isDefault != 0L && key == "ep") "Electrum" else name, amount.toInt(), sortOrder.toInt(), isDefault != 0L)',
    'existing electrum display normalization',
)
repo = replace_once(
    repo,
    'CharacterCurrency("ep", "Electro", 0, 2, true)',
    'CharacterCurrency("ep", "Electrum", 0, 2, true)',
    'new electrum default',
)
repo_path.write_text(repo)
