from pathlib import Path


def replace_once(text, old, new, label):
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{label}: expected exactly one match, found {count}")
    return text.replace(old, new, 1)

# Protected terminology in shared trait presentation.
ops_path = Path('shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterTraitOperations.kt')
ops = ops_path.read_text()
ops = replace_once(
    ops,
    'CharacterTraitType.SPECIES_RACE -> "Especie / raza"',
    'CharacterTraitType.SPECIES_RACE -> "Raza"',
    'trait Raza terminology',
)
ops_path.write_text(ops)

# Make the existing structured provenance primitive reusable when a domain has a meaningful
# type dimension but no approved catalog of specific origins.
prov_path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterHelpProvenanceV4.kt')
prov = prov_path.read_text()
prov = replace_once(
    prov,
    '    BACKGROUND("Trasfondo"),\n    OTHER("Otro"),',
    '    BACKGROUND("Trasfondo"),\n    GIFT("Don / bendición"),\n    OTHER("Otro"),',
    'provenance gift origin',
)
prov = replace_once(
    prov,
    '''    onCustomOriginChange: (String) -> Unit,\n    modifier: Modifier = Modifier,\n) {''',
    '''    onCustomOriginChange: (String) -> Unit,\n    modifier: Modifier = Modifier,\n    allowedTypes: List<CharacterOriginTypeV4> = CharacterOriginTypeV4.entries,\n) {''',
    'provenance allowed types signature',
)
prov = replace_once(
    prov,
    '                    CharacterOriginTypeV4.entries.forEach { option ->',
    '                    allowedTypes.forEach { option ->',
    'provenance type menu',
)
prov = replace_once(
    prov,
    '''                                    onOriginTypeChange(option)\n                                    onOriginKeyChange(null)\n                                    if (option != CharacterOriginTypeV4.OTHER) onCustomOriginChange("")\n''',
    '''                                    onOriginTypeChange(option)\n                                    onOriginKeyChange(null)\n''',
    'provenance preserve custom specific origin',
)
prov = replace_once(
    prov,
    '            if (originType == CharacterOriginTypeV4.OTHER) {',
    '            if (originType == CharacterOriginTypeV4.OTHER || options.isEmpty()) {',
    'provenance custom fallback',
)
prov = replace_once(
    prov,
    '                                "Origen personalizado",',
    '                                if (originType == CharacterOriginTypeV4.OTHER) "Origen personalizado" else "Origen específico",',
    'provenance custom placeholder',
)
prov_path.write_text(prov)

traits_path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterTraitsClosureV4.kt')
traits = traits_path.read_text()
traits = replace_once(
    traits,
    'import androidx.compose.foundation.layout.fillMaxWidth\n',
    'import androidx.compose.foundation.layout.fillMaxWidth\nimport androidx.compose.foundation.layout.heightIn\n',
    'traits height import',
)
traits = replace_once(
    traits,
    'import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind\n',
    'import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind\n'
    'import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResource\n'
    'import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResourcePlacement\n'
    'import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResourceSuccessorConfiguration\n'
    'import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrackableValueKind\n',
    'traits resource imports',
)
traits = replace_once(
    traits,
    '''internal fun CharacterTraitsClosureTabV4(\n    traits: List<CharacterTrait>,\n    closureState: CharacterClosureState,\n    persistedTraitIds: Set<Uuid>,\n    onTraitsChange: (List<CharacterTrait>) -> Unit,\n    onClosureStateChange: (CharacterClosureState) -> Unit,\n    structuralEditingEnabled: Boolean,\n    wide: Boolean,\n    hapticsEnabled: Boolean,\n) {''',
    '''internal fun CharacterTraitsClosureTabV4(\n    traits: List<CharacterTrait>,\n    closureState: CharacterClosureState,\n    persistedTraitIds: Set<Uuid>,\n    resources: List<CharacterResource>,\n    onTraitsChange: (List<CharacterTrait>) -> Unit,\n    onClosureStateChange: (CharacterClosureState) -> Unit,\n    onResourceValueChange: (Uuid, Int) -> Unit,\n    structuralEditingEnabled: Boolean,\n    wide: Boolean,\n    hapticsEnabled: Boolean,\n) {''',
    'traits signature',
)
traits = replace_once(
    traits,
    '''    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)\n    val grouping = runCatching { CharacterTraitGrouping.valueOf(groupingName) }''',
    '''    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)\n    val settingsContext = LocalCharacterPcSettingsContextV4.current\n    val resourceConfigurations = settingsContext?.successorState?.resourceConfigurations.orEmpty().associateBy { it.resourceId }\n    val traitResources = resources\n        .filter { resource -> CharacterResourcePlacement.TRAITS in (resourceConfigurations[resource.id]?.placements ?: emptySet()) }\n        .sortedBy { it.sortOrder }\n    val grouping = runCatching { CharacterTraitGrouping.valueOf(groupingName) }''',
    'traits resource projection',
)
traits = replace_once(
    traits,
    '"Clase, especie/raza, trasfondo, dotes, dones y contenido personalizado."',
    '"Clase, raza, trasfondo, dotes, dones / bendiciones y contenido personalizado."',
    'traits top terminology',
)

# Add canonical Resource projection immediately below the sticky tools, before collection states.
needle = '''        if (traits.isEmpty()) {\n'''
resource_item = '''        if (traitResources.isNotEmpty()) {\n            item(key = "traits-resources") {\n                TraitsResourcesCardG2(\n                    resources = traitResources,\n                    configurations = resourceConfigurations,\n                    onResourceValueChange = { resourceId, value ->\n                        onResourceValueChange(resourceId, value)\n                        haptic(CharacterHapticEventV4.RESOURCE)\n                    },\n                )\n            }\n        }\n\n        if (traits.isEmpty()) {\n'''
traits = replace_once(traits, needle, resource_item, 'traits resource item')

# Give every trait card row-major grid coordinates so the shared 2D primitive can provide
# visible horizontal/vertical/diagonal feedback in multi-column layouts.
traits = replace_once(
    traits,
    '''                            group.traits.chunked(columns).forEach { rowTraits ->\n                                Row(''',
    '''                            group.traits.chunked(columns).forEachIndexed { rowIndex, rowTraits ->\n                                Row(''',
    'traits indexed rows',
)
traits = replace_once(
    traits,
    '''                                    rowTraits.forEach { trait ->\n                                        TraitCardG1(\n                                            trait = trait,''',
    '''                                    rowTraits.forEachIndexed { columnIndex, trait ->\n                                        TraitCardG1(\n                                            trait = trait,\n                                            gridIndex = rowIndex * columns + columnIndex,\n                                            gridItemCount = group.traits.size,\n                                            gridColumns = columns,''',
    'traits indexed cards',
)
traits = replace_once(
    traits,
    '''private fun TraitCardG1(\n    trait: CharacterTrait,\n    favorite: Boolean,''',
    '''private fun TraitCardG1(\n    trait: CharacterTrait,\n    gridIndex: Int,\n    gridItemCount: Int,\n    gridColumns: Int,\n    favorite: Boolean,''',
    'trait card grid signature',
)
old_drag = '''    var accumulatedDrag by remember(trait.id) { mutableStateOf(0f) }\n    var dragging by remember(trait.id) { mutableStateOf(false) }\n    val dragState = CharacterDragVisualStateV4(\n        active = dragging,\n        offsetY = accumulatedDrag,\n        showDropBefore = dragging && accumulatedDrag < 0f,\n        showDropAfter = dragging && accumulatedDrag > 0f,\n    )\n'''
new_drag = '''    var dragState by remember(trait.id) { mutableStateOf(CharacterDragVisualStateV4()) }\n    val reorderModifier = if (gridColumns > 1) {\n        Modifier.characterMeasuredGridReorderDragV4(\n            enabled = canReorder,\n            onHaptic = onHaptic,\n            onMove = { rowDelta, columnDelta ->\n                val currentRow = gridIndex / gridColumns\n                val currentColumn = gridIndex % gridColumns\n                val targetRow = currentRow + rowDelta\n                val targetColumn = currentColumn + columnDelta\n                val targetIndex = targetRow * gridColumns + targetColumn\n                val targetValid =\n                    targetRow >= 0 &&\n                        targetColumn in 0 until gridColumns &&\n                        targetIndex in 0 until gridItemCount\n                if (targetValid) onMove(targetIndex - gridIndex) else false\n            },\n            onVisualStateChange = { dragState = it },\n        )\n    } else {\n        Modifier.characterMeasuredReorderDragV4(\n            enabled = canReorder,\n            onHaptic = onHaptic,\n            onMove = onMove,\n            onVisualStateChange = { dragState = it },\n        )\n    }\n'''
traits = replace_once(traits, old_drag, new_drag, 'trait 2D drag state')
traits = replace_once(
    traits,
    '''            modifier = Modifier\n                .fillMaxWidth()\n                .characterMeasuredReorderDragV4(\n                    enabled = canReorder,\n                    onHaptic = onHaptic,\n                    onMove = onMove,\n                    onVisualStateChange = { state ->\n                        dragging = state.active\n                        accumulatedDrag = state.offsetY\n                    },\n                )\n                .characterDragFeedbackV4(dragState)\n''',
    '''            modifier = Modifier\n                .fillMaxWidth()\n                .then(reorderModifier)\n                .characterDragFeedbackV4(dragState)\n''',
    'trait surface 2D drag',
)

# Replace the separate generic Fuente + Tipo controls with the existing structured provenance row.
old_editor_origin = '''        OutlinedTextField(\n            value = source,\n            onValueChange = onSourceChange,\n            label = { Text("Fuente") },\n            modifier = Modifier.fillMaxWidth(),\n            singleLine = true,\n        )\n        Column {\n            Text("Tipo", style = MaterialTheme.typography.labelSmall)\n            androidx.compose.foundation.layout.Box {\n                OutlinedButton(onClick = { typeMenuOpen = true }, modifier = Modifier.fillMaxWidth()) {\n                    Text(characterTraitTypeDisplayLabel(type))\n                }\n                DropdownMenu(expanded = typeMenuOpen, onDismissRequest = { typeMenuOpen = false }) {\n                    CharacterTraitType.entries.forEach { option ->\n                        DropdownMenuItem(\n                            text = { Text(characterTraitTypeDisplayLabel(option)) },\n                            onClick = {\n                                onTypeChange(option)\n                                typeMenuOpen = false\n                            },\n                        )\n                    }\n                }\n            }\n        }\n'''
new_editor_origin = '''        CharacterProvenanceRowV4(\n            originType = traitOriginTypeG2(type),\n            originKey = null,\n            customOrigin = source,\n            optionsForType = { emptyList() },\n            onOriginTypeChange = { onTypeChange(traitTypeForOriginG2(it)) },\n            onOriginKeyChange = {},\n            onCustomOriginChange = onSourceChange,\n            allowedTypes = listOf(\n                CharacterOriginTypeV4.CLASS,\n                CharacterOriginTypeV4.RACE,\n                CharacterOriginTypeV4.BACKGROUND,\n                CharacterOriginTypeV4.FEAT,\n                CharacterOriginTypeV4.GIFT,\n                CharacterOriginTypeV4.OTHER,\n            ),\n        )\n        CharacterHelpV4(\n            "Tipo de origen describe de dónde nace el rasgo; Origen específico identifica la clase, raza, trasfondo, dote, don/bendición o creación concreta. Ambos se guardan en el mismo rasgo, no como fuentes paralelas.",\n        )\n'''
traits = replace_once(traits, old_editor_origin, new_editor_origin, 'structured trait provenance')
traits = replace_once(
    traits,
    '    var typeMenuOpen by rememberSaveable { mutableStateOf(false) }\n',
    '',
    'remove obsolete trait type menu state',
)

# Add shared-looking compact Resource controls and type/origin mapping near the existing helpers.
insert_before = '''private fun traitUnsignedIntegerG1(raw: String): String = raw.filter(Char::isDigit)\n'''
helpers = '''@Composable\nprivate fun TraitsResourcesCardG2(\n    resources: List<CharacterResource>,\n    configurations: Map<Uuid, CharacterResourceSuccessorConfiguration>,\n    onResourceValueChange: (Uuid, Int) -> Unit,\n) {\n    Card(modifier = Modifier.fillMaxWidth()) {\n        Column(\n            modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(6.dp), vertical = appSpacingV4(4.dp)),\n            verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),\n        ) {\n            Text("Recursos de Rasgos", style = MaterialTheme.typography.titleSmall)\n            resources.forEach { resource ->\n                val configuration = configurations[resource.id] ?: return@forEach\n                Row(\n                    modifier = Modifier.fillMaxWidth(),\n                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),\n                    verticalAlignment = Alignment.CenterVertically,\n                ) {\n                    Text(\n                        resource.name,\n                        modifier = Modifier.weight(1f),\n                        style = MaterialTheme.typography.bodySmall,\n                        maxLines = 1,\n                        overflow = TextOverflow.Ellipsis,\n                    )\n                    when (configuration.valueKind) {\n                        CharacterTrackableValueKind.BINARY -> {\n                            val active = resource.currentValue > 0\n                            TraitResourceChipG2(if (active) "Activo" else "Inactivo", active) {\n                                onResourceValueChange(resource.id, if (active) 0 else 1)\n                            }\n                        }\n                        CharacterTrackableValueKind.COUNTER,\n                        CharacterTrackableValueKind.CURRENT_MAX,\n                        -> {\n                            val maximum = if (configuration.valueKind == CharacterTrackableValueKind.CURRENT_MAX) resource.maxValue else null\n                            TraitResourceStepG2("−", resource.currentValue > 0) {\n                                onResourceValueChange(resource.id, (resource.currentValue - 1).coerceAtLeast(0))\n                            }\n                            Text(\n                                maximum?.let { "${resource.currentValue}/$it" } ?: resource.currentValue.toString(),\n                                style = MaterialTheme.typography.labelLarge,\n                            )\n                            TraitResourceStepG2("+", maximum?.let { resource.currentValue < it } ?: true) {\n                                val next = resource.currentValue + 1\n                                onResourceValueChange(resource.id, maximum?.let { next.coerceAtMost(it) } ?: next)\n                            }\n                        }\n                    }\n                }\n            }\n            CharacterHelpV4("Estos controles operan el mismo recurso canónico que Gestión, General o Equipo; la pestaña solo cambia dónde se proyecta.")\n        }\n    }\n}\n\n@Composable\nprivate fun TraitResourceChipG2(label: String, selected: Boolean, onClick: () -> Unit) {\n    Surface(\n        modifier = Modifier.heightIn(min = 30.dp).clickable(onClick = onClick),\n        shape = MaterialTheme.shapes.small,\n        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),\n        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,\n    ) {\n        androidx.compose.foundation.layout.Box(\n            modifier = Modifier.padding(horizontal = appSpacingV4(7.dp), vertical = appSpacingV4(3.dp)),\n            contentAlignment = Alignment.Center,\n        ) { Text(label, style = MaterialTheme.typography.labelSmall) }\n    }\n}\n\n@Composable\nprivate fun TraitResourceStepG2(label: String, enabled: Boolean, onClick: () -> Unit) {\n    Surface(\n        modifier = Modifier.heightIn(min = 30.dp).clickable(enabled = enabled, onClick = onClick),\n        shape = MaterialTheme.shapes.small,\n        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),\n        color = if (enabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,\n    ) {\n        androidx.compose.foundation.layout.Box(\n            modifier = Modifier.padding(horizontal = appSpacingV4(8.dp), vertical = appSpacingV4(3.dp)),\n            contentAlignment = Alignment.Center,\n        ) { Text(label, style = MaterialTheme.typography.labelLarge) }\n    }\n}\n\nprivate fun traitOriginTypeG2(type: CharacterTraitType): CharacterOriginTypeV4 = when (type) {\n    CharacterTraitType.CLASS -> CharacterOriginTypeV4.CLASS\n    CharacterTraitType.SPECIES_RACE -> CharacterOriginTypeV4.RACE\n    CharacterTraitType.BACKGROUND -> CharacterOriginTypeV4.BACKGROUND\n    CharacterTraitType.FEAT -> CharacterOriginTypeV4.FEAT\n    CharacterTraitType.GIFT_BLESSING -> CharacterOriginTypeV4.GIFT\n    CharacterTraitType.OTHER -> CharacterOriginTypeV4.OTHER\n}\n\nprivate fun traitTypeForOriginG2(origin: CharacterOriginTypeV4): CharacterTraitType = when (origin) {\n    CharacterOriginTypeV4.CLASS -> CharacterTraitType.CLASS\n    CharacterOriginTypeV4.RACE -> CharacterTraitType.SPECIES_RACE\n    CharacterOriginTypeV4.BACKGROUND -> CharacterTraitType.BACKGROUND\n    CharacterOriginTypeV4.FEAT -> CharacterTraitType.FEAT\n    CharacterOriginTypeV4.GIFT -> CharacterTraitType.GIFT_BLESSING\n    CharacterOriginTypeV4.PACT,\n    CharacterOriginTypeV4.ITEM,\n    CharacterOriginTypeV4.OTHER,\n    -> CharacterTraitType.OTHER\n}\n\n'''
traits = replace_once(traits, insert_before, helpers + insert_before, 'traits helper insertion')
traits_path.write_text(traits)

# Wire canonical Resource updates from the editor into Rasgos.
editor_path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt')
editor = editor_path.read_text()
old_call = '''                        CharacterTabV4.TRAITS -> CharacterTraitsClosureTabV4(\n                            traits = traitsDraft,\n                            closureState = closureState,\n                            persistedTraitIds = stored.traits.mapTo(mutableSetOf()) { it.id },\n                            onTraitsChange = ::updateTraits,\n                            onClosureStateChange = ::persistStructuralClosureState,\n                            structuralEditingEnabled = structuralEditingEnabled,\n                            wide = wide,\n                            hapticsEnabled = closureState.hapticsEnabled,\n                        )'''
new_call = '''                        CharacterTabV4.TRAITS -> CharacterTraitsClosureTabV4(\n                            traits = traitsDraft,\n                            closureState = closureState,\n                            persistedTraitIds = stored.traits.mapTo(mutableSetOf()) { it.id },\n                            resources = stored.resources,\n                            onTraitsChange = ::updateTraits,\n                            onClosureStateChange = ::persistStructuralClosureState,\n                            onResourceValueChange = { resourceId, value ->\n                                stored.resources.firstOrNull { it.id == resourceId }?.let { resource ->\n                                    val normalized = resource.maxValue?.let { value.coerceIn(0, it) } ?: value.coerceAtLeast(0)\n                                    if (normalized != resource.currentValue) {\n                                        persistOperationalSheet(\n                                            stored.copy(\n                                                resources = stored.resources.map { item ->\n                                                    if (item.id == resourceId) item.copy(currentValue = normalized) else item\n                                                },\n                                            ),\n                                        )\n                                    }\n                                }\n                            },\n                            structuralEditingEnabled = structuralEditingEnabled,\n                            wide = wide,\n                            hapticsEnabled = closureState.hapticsEnabled,\n                        )'''
editor = replace_once(editor, old_call, new_call, 'editor traits resources')
editor_path.write_text(editor)
