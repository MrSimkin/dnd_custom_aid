from pathlib import Path


def replace_once(path: str, old: str, new: str) -> None:
    p = Path(path)
    text = p.read_text(encoding="utf-8")
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{path}: expected exactly one match, found {count}")
    p.write_text(text.replace(old, new, 1), encoding="utf-8")


def replace_all_exact(path: str, old: str, new: str, expected: int) -> None:
    p = Path(path)
    text = p.read_text(encoding="utf-8")
    count = text.count(old)
    if count != expected:
        raise SystemExit(f"{path}: expected {expected} matches, found {count}")
    p.write_text(text.replace(old, new), encoding="utf-8")

# Shared drag visual state: preserve all existing callers while allowing grid drags to translate on X.
collection = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCollectionPrimitivesV4.kt"
replace_once(
    collection,
    '''internal data class CharacterDragVisualStateV4(\n    val active: Boolean = false,\n    val offsetY: Float = 0f,\n    val showDropBefore: Boolean = false,\n    val showDropAfter: Boolean = false,\n)''',
    '''internal data class CharacterDragVisualStateV4(\n    val active: Boolean = false,\n    val offsetX: Float = 0f,\n    val offsetY: Float = 0f,\n    val showDropBefore: Boolean = false,\n    val showDropAfter: Boolean = false,\n)''',
)
replace_once(
    collection,
    '''    val offsetY = animateFloatAsState(\n        targetValue = state.offsetY,''',
    '''    val offsetX = animateFloatAsState(\n        targetValue = state.offsetX,\n        animationSpec = if (state.active) {\n            spring(\n                dampingRatio = Spring.DampingRatioNoBouncy,\n                stiffness = Spring.StiffnessHigh,\n            )\n        } else {\n            spring(\n                dampingRatio = Spring.DampingRatioNoBouncy,\n                stiffness = Spring.StiffnessMediumLow,\n            )\n        },\n        label = "character-drag-offset-x",\n    ).value\n    val offsetY = animateFloatAsState(\n        targetValue = state.offsetY,''',
)
replace_once(
    collection,
    '''        .graphicsLayer {\n            translationY = offsetY''',
    '''        .graphicsLayer {\n            translationX = offsetX\n            translationY = offsetY''',
)

# Add a 2-D whole-card drag primitive without changing the existing one-column authority.
interaction = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCardInteractionV4.kt"
replace_once(
    interaction,
    '''import androidx.compose.ui.Modifier\nimport androidx.compose.ui.input.pointer.pointerInput''',
    '''import androidx.compose.ui.Modifier\nimport androidx.compose.ui.geometry.Offset\nimport androidx.compose.ui.input.pointer.pointerInput''',
)
p = Path(interaction)
text = p.read_text(encoding="utf-8")
append = r'''

/**
 * Two-dimensional counterpart for card collections laid out in multiple columns.
 * It deliberately coexists with [characterMeasuredReorderDragV4]: one-column lists keep the
 * already-auditioned vertical interaction, while grid callers can express row/column movement.
 */
@Composable
internal fun Modifier.characterLongPressDrag2DV4(
    enabled: Boolean,
    onHaptic: (CharacterHapticEventV4) -> Unit,
    onDragStart: () -> Unit,
    onDragDelta: (Offset) -> Boolean,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit = onDragEnd,
): Modifier {
    val currentHaptic by rememberUpdatedState(onHaptic)
    val currentDragStart by rememberUpdatedState(onDragStart)
    val currentDragDelta by rememberUpdatedState(onDragDelta)
    val currentDragEnd by rememberUpdatedState(onDragEnd)
    val currentDragCancel by rememberUpdatedState(onDragCancel)

    return if (!enabled) {
        this
    } else {
        pointerInput(enabled) {
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    currentHaptic(CharacterHapticEventV4.DRAG_PICKUP)
                    currentDragStart()
                },
                onDragEnd = {
                    currentDragEnd()
                    currentHaptic(CharacterHapticEventV4.DRAG_DROP)
                },
                onDragCancel = {
                    currentDragCancel()
                    currentHaptic(CharacterHapticEventV4.DRAG_DROP)
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    if (currentDragDelta(dragAmount)) {
                        currentHaptic(CharacterHapticEventV4.DRAG_STEP)
                    }
                },
            )
        }
    }
}

/**
 * Measured whole-card reorder for row-major multi-column collections.
 *
 * X movement requests a column step and Y movement requests a row step. Diagonal motion follows
 * the axis that has crossed the larger normalized fraction of its own measured card dimension.
 * One pointer update still performs at most one logical move. The dragged card visibly follows
 * both axes, so horizontal/diagonal intent is no longer invisible.
 */
@Composable
internal fun Modifier.characterMeasuredGridReorderDragV4(
    enabled: Boolean,
    onHaptic: (CharacterHapticEventV4) -> Unit,
    onMove: (Int, Int) -> Boolean,
    onVisualStateChange: (CharacterDragVisualStateV4) -> Unit,
    thresholdFraction: Float = 0.55f,
): Modifier {
    require(thresholdFraction in 0.35f..0.9f)
    var measuredWidthPx by remember { mutableIntStateOf(0) }
    var measuredHeightPx by remember { mutableIntStateOf(0) }
    var accumulatedX by remember { mutableFloatStateOf(0f) }
    var accumulatedY by remember { mutableFloatStateOf(0f) }
    val minimumStepPx = with(LocalDensity.current) { 32.dp.toPx() }
    val currentMove by rememberUpdatedState(onMove)
    val currentVisualStateChange by rememberUpdatedState(onVisualStateChange)

    fun stepX(): Float = (measuredWidthPx * thresholdFraction).coerceAtLeast(minimumStepPx)
    fun stepY(): Float = (measuredHeightPx * thresholdFraction).coerceAtLeast(minimumStepPx)

    fun publish(active: Boolean) {
        val normalizedX = abs(accumulatedX) / stepX()
        val normalizedY = abs(accumulatedY) / stepY()
        val verticalDominant = normalizedY >= normalizedX
        currentVisualStateChange(
            CharacterDragVisualStateV4(
                active = active,
                offsetX = accumulatedX,
                offsetY = accumulatedY,
                showDropBefore = active && verticalDominant && accumulatedY < 0f,
                showDropAfter = active && verticalDominant && accumulatedY > 0f,
            ),
        )
    }

    return if (!enabled) {
        this
    } else {
        this
            .onSizeChanged {
                measuredWidthPx = it.width
                measuredHeightPx = it.height
            }
            .characterLongPressDrag2DV4(
                enabled = true,
                onHaptic = onHaptic,
                onDragStart = {
                    accumulatedX = 0f
                    accumulatedY = 0f
                    publish(active = true)
                },
                onDragDelta = { delta ->
                    accumulatedX += delta.x
                    accumulatedY += delta.y
                    val normalizedX = abs(accumulatedX) / stepX()
                    val normalizedY = abs(accumulatedY) / stepY()
                    var moved = false
                    if (normalizedX >= 1f || normalizedY >= 1f) {
                        val horizontal = normalizedX >= normalizedY
                        val rowDelta = if (horizontal) 0 else if (accumulatedY > 0f) 1 else -1
                        val columnDelta = if (horizontal) if (accumulatedX > 0f) 1 else -1 else 0
                        moved = currentMove(rowDelta, columnDelta)
                        if (moved) {
                            accumulatedX = 0f
                            accumulatedY = 0f
                        } else if (horizontal) {
                            accumulatedX = 0f
                        } else {
                            accumulatedY = 0f
                        }
                    }
                    publish(active = true)
                    moved
                },
                onDragEnd = {
                    accumulatedX = 0f
                    accumulatedY = 0f
                    publish(active = false)
                },
                onDragCancel = {
                    accumulatedX = 0f
                    accumulatedY = 0f
                    publish(active = false)
                },
            )
    }
}
'''
if "characterMeasuredGridReorderDragV4" in text:
    raise SystemExit("grid reorder primitive already present")
p.write_text(text.rstrip() + append + "\n", encoding="utf-8")

# Compact fields must never clip scaled glyphs: compact size is a minimum, not a forced box height.
spell = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellListClosureV4.kt"
replace_all_exact(
    spell,
    '.height(characterCompactSingleLineFieldHeightV4())',
    '.heightIn(min = characterCompactSingleLineFieldHeightV4())',
    5,
)
replace_once(
    spell,
    '''        modifier = Modifier.fillMaxWidth(),\n    )\n    Row(verticalAlignment = Alignment.CenterVertically) {\n        Checkbox(concentration, onConcentrationChange); Text("Concentración")''',
    '''        modifier = Modifier.fillMaxWidth(),\n        minLines = 2,\n        maxLines = 4,\n    )\n    Row(verticalAlignment = Alignment.CenterVertically) {\n        Checkbox(concentration, onConcentrationChange); Text("Concentración")''',
)
replace_once(spell, '        minLines = 4,\n        maxLines = 10,', '        minLines = 3,\n        maxLines = 5,')
replace_once(spell, '        minLines = 2,\n        maxLines = 6,', '        minLines = 2,\n        maxLines = 4,')

source_editor = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellSourceEditorV4.kt"
replace_all_exact(
    source_editor,
    '.height(characterCompactSingleLineFieldHeightV4())',
    '.heightIn(min = characterCompactSingleLineFieldHeightV4())',
    3,
)
replace_once(
    source_editor,
    'import androidx.compose.foundation.layout.fillMaxWidth',
    'import androidx.compose.foundation.layout.fillMaxWidth\nimport androidx.compose.foundation.layout.heightIn',
)

# Equipo is the physical multi-column proving surface. Preserve one-column behavior and use the new
# grid primitive only when the configured layout actually has multiple columns.
equipment = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEquipmentClosureV4.kt"
replace_once(
    equipment,
    '''                    items.chunked(columns).forEach { rowItems ->\n                        Row(''',
    '''                    items.chunked(columns).forEachIndexed { rowIndex, rowItems ->\n                        Row(''',
)
replace_once(
    equipment,
    '''                            rowItems.forEach { item ->\n                                EquipmentDenseItemF2(\n                                    item = item,''',
    '''                            rowItems.forEachIndexed { columnIndex, item ->\n                                EquipmentDenseItemF2(\n                                    item = item,\n                                    gridIndex = rowIndex * columns + columnIndex,\n                                    gridItemCount = items.size,\n                                    gridColumns = columns,''',
)
replace_once(
    equipment,
    '''private fun EquipmentDenseItemF2(\n    item: CharacterInventoryItem,\n    usage: CharacterInventoryUsage,''',
    '''private fun EquipmentDenseItemF2(\n    item: CharacterInventoryItem,\n    gridIndex: Int,\n    gridItemCount: Int,\n    gridColumns: Int,\n    usage: CharacterInventoryUsage,''',
)
replace_once(
    equipment,
    '''    var accumulatedDrag by remember(item.id) { mutableStateOf(0f) }\n    var dragging by remember(item.id) { mutableStateOf(false) }\n    val dragState = CharacterDragVisualStateV4(\n        active = dragging,\n        offsetY = accumulatedDrag,\n        showDropBefore = dragging && accumulatedDrag < 0f,\n        showDropAfter = dragging && accumulatedDrag > 0f,\n    )''',
    '''    var dragState by remember(item.id) { mutableStateOf(CharacterDragVisualStateV4()) }\n    val reorderModifier = if (gridColumns > 1) {\n        Modifier.characterMeasuredGridReorderDragV4(\n            enabled = canReorder,\n            onHaptic = onHaptic,\n            onMove = { rowDelta, columnDelta ->\n                val currentRow = gridIndex / gridColumns\n                val currentColumn = gridIndex % gridColumns\n                val targetRow = currentRow + rowDelta\n                val targetColumn = currentColumn + columnDelta\n                val targetIndex = targetRow * gridColumns + targetColumn\n                val targetValid =\n                    targetRow >= 0 &&\n                        targetColumn in 0 until gridColumns &&\n                        targetIndex in 0 until gridItemCount\n                if (targetValid) onMove(targetIndex - gridIndex) else false\n            },\n            onVisualStateChange = { dragState = it },\n        )\n    } else {\n        Modifier.characterMeasuredReorderDragV4(\n            enabled = canReorder,\n            onHaptic = onHaptic,\n            onMove = onMove,\n            onVisualStateChange = { dragState = it },\n        )\n    }''',
)
replace_once(
    equipment,
    '''            modifier = Modifier\n                .fillMaxWidth()\n                .characterMeasuredReorderDragV4(\n                    enabled = canReorder,\n                    onHaptic = onHaptic,\n                    onMove = onMove,\n                    onVisualStateChange = { state ->\n                        dragging = state.active\n                        accumulatedDrag = state.offsetY\n                    },\n                )\n                .characterDragFeedbackV4(dragState)''',
    '''            modifier = Modifier\n                .fillMaxWidth()\n                .then(reorderModifier)\n                .characterDragFeedbackV4(dragState)''',
)

print("F residual owner-test repair applied")
