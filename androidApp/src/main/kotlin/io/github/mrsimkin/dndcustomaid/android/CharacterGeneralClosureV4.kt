package io.github.mrsimkin.dndcustomaid.android

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDefense
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDefenseType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterMovement
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterMovementType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSense
import kotlin.uuid.Uuid

@Composable
internal fun CharacterGeneralClosureCardsV4(
    state: CharacterClosureState,
    onStateChange: (CharacterClosureState) -> Unit,
    wide: Boolean,
) {
    CharacterMediaCardV4(state = state, onStateChange = onStateChange, wide = wide)
    CharacterDefensesSensesMovementCardV4(state = state, onStateChange = onStateChange, wide = wide)
}

@Composable
private fun CharacterMediaCardV4(
    state: CharacterClosureState,
    onStateChange: (CharacterClosureState) -> Unit,
    wide: Boolean,
) {
    val context = LocalContext.current
    fun persistReadPermission(uri: Uri) {
        runCatching {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    val portraitLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            persistReadPermission(it)
            onStateChange(state.copy(portraitRef = it.toString()))
        }
    }
    val tokenLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            persistReadPermission(it)
            onStateChange(state.copy(tokenRef = it.toString()))
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 7.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text("Retrato y token", style = MaterialTheme.typography.titleSmall)
            Text(
                "Referencias locales opcionales. La imagen permanece en tu dispositivo; la ficha guarda solo el permiso/URI de lectura.",
                style = MaterialTheme.typography.labelSmall,
            )
            if (wide) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CharacterImageReferenceV4(
                        title = "Retrato",
                        uriRef = state.portraitRef,
                        onChoose = { portraitLauncher.launch(arrayOf("image/*")) },
                        onClear = { onStateChange(state.copy(portraitRef = null)) },
                        round = false,
                        modifier = Modifier.weight(1f),
                    )
                    CharacterImageReferenceV4(
                        title = "Token",
                        uriRef = state.tokenRef,
                        onChoose = { tokenLauncher.launch(arrayOf("image/*")) },
                        onClear = { onStateChange(state.copy(tokenRef = null)) },
                        round = true,
                        modifier = Modifier.weight(1f),
                    )
                }
            } else {
                CharacterImageReferenceV4(
                    title = "Retrato",
                    uriRef = state.portraitRef,
                    onChoose = { portraitLauncher.launch(arrayOf("image/*")) },
                    onClear = { onStateChange(state.copy(portraitRef = null)) },
                    round = false,
                )
                CharacterImageReferenceV4(
                    title = "Token",
                    uriRef = state.tokenRef,
                    onChoose = { tokenLauncher.launch(arrayOf("image/*")) },
                    onClear = { onStateChange(state.copy(tokenRef = null)) },
                    round = true,
                )
            }
        }
    }
}

@Composable
private fun CharacterImageReferenceV4(
    title: String,
    uriRef: String?,
    onChoose: () -> Unit,
    onClear: () -> Unit,
    round: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val bitmap = remember(uriRef) {
        uriRef?.let { raw ->
            runCatching {
                context.contentResolver.openInputStream(Uri.parse(raw))?.use { input ->
                    BitmapFactory.decodeStream(input)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(86.dp).clip(if (round) CircleShape else MaterialTheme.shapes.small),
                shape = if (round) CircleShape else MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = title,
                        modifier = Modifier.size(86.dp),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Text(if (uriRef == null) "Sin imagen" else "No disponible", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, style = MaterialTheme.typography.labelLarge)
                Text(
                    if (uriRef == null) "No configurado" else "Referencia local guardada",
                    style = MaterialTheme.typography.labelSmall,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = onChoose) { Text(if (uriRef == null) "Elegir" else "Cambiar") }
                    if (uriRef != null) TextButton(onClick = onClear) { Text("Quitar") }
                }
            }
        }
    }
}

@Composable
private fun CharacterDefensesSensesMovementCardV4(
    state: CharacterClosureState,
    onStateChange: (CharacterClosureState) -> Unit,
    wide: Boolean,
) {
    var defenseEditorId by rememberSaveable { mutableStateOf<String?>(null) }
    var defenseEditorOpen by rememberSaveable { mutableStateOf(false) }
    var defenseDeleteId by rememberSaveable { mutableStateOf<String?>(null) }
    var senseEditorId by rememberSaveable { mutableStateOf<String?>(null) }
    var senseEditorOpen by rememberSaveable { mutableStateOf(false) }
    var senseDeleteId by rememberSaveable { mutableStateOf<String?>(null) }
    var movementEditorId by rememberSaveable { mutableStateOf<String?>(null) }
    var movementEditorOpen by rememberSaveable { mutableStateOf(false) }
    var movementDeleteId by rememberSaveable { mutableStateOf<String?>(null) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 7.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Text("Defensas, sentidos y movimiento especial", style = MaterialTheme.typography.titleSmall)
            Text(
                "Referencia estructurada para la ficha y para futuras vistas rápidas del DM. No aplica reglas automáticamente.",
                style = MaterialTheme.typography.labelSmall,
            )
            if (wide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    GeneralReferenceGroupV4(
                        title = "Defensas",
                        entries = state.defenses.sortedBy { it.sortOrder },
                        label = { "${defenseTypeLabelV4(it.type)} · ${it.name}" },
                        detail = { it.source },
                        onOpen = { item -> defenseEditorId = item.id.toString(); defenseEditorOpen = true },
                        onDelete = { defenseDeleteId = it.id.toString() },
                        onAdd = { defenseEditorId = null; defenseEditorOpen = true },
                        addLabel = "Añadir defensa",
                        modifier = Modifier.weight(1f),
                    )
                    GeneralReferenceGroupV4(
                        title = "Sentidos",
                        entries = state.senses.sortedBy { it.sortOrder },
                        label = { it.name },
                        detail = { it.rangeFeet?.let { range -> "$range ft" } },
                        onOpen = { item -> senseEditorId = item.id.toString(); senseEditorOpen = true },
                        onDelete = { senseDeleteId = it.id.toString() },
                        onAdd = { senseEditorId = null; senseEditorOpen = true },
                        addLabel = "Añadir sentido",
                        modifier = Modifier.weight(1f),
                    )
                    GeneralReferenceGroupV4(
                        title = "Movimiento",
                        entries = state.movements.sortedBy { it.sortOrder },
                        label = { it.name },
                        detail = { movement -> listOf(movementTypeLabelV4(movement.type), movement.speedFeet?.let { "$it ft" }).filterNotNull().joinToString(" · ") },
                        onOpen = { item -> movementEditorId = item.id.toString(); movementEditorOpen = true },
                        onDelete = { movementDeleteId = it.id.toString() },
                        onAdd = { movementEditorId = null; movementEditorOpen = true },
                        addLabel = "Añadir movimiento",
                        modifier = Modifier.weight(1f),
                    )
                }
            } else {
                GeneralReferenceGroupV4(
                    title = "Defensas",
                    entries = state.defenses.sortedBy { it.sortOrder },
                    label = { "${defenseTypeLabelV4(it.type)} · ${it.name}" },
                    detail = { it.source },
                    onOpen = { item -> defenseEditorId = item.id.toString(); defenseEditorOpen = true },
                    onDelete = { defenseDeleteId = it.id.toString() },
                    onAdd = { defenseEditorId = null; defenseEditorOpen = true },
                    addLabel = "Añadir defensa",
                )
                GeneralReferenceGroupV4(
                    title = "Sentidos",
                    entries = state.senses.sortedBy { it.sortOrder },
                    label = { it.name },
                    detail = { it.rangeFeet?.let { range -> "$range ft" } },
                    onOpen = { item -> senseEditorId = item.id.toString(); senseEditorOpen = true },
                    onDelete = { senseDeleteId = it.id.toString() },
                    onAdd = { senseEditorId = null; senseEditorOpen = true },
                    addLabel = "Añadir sentido",
                )
                GeneralReferenceGroupV4(
                    title = "Movimiento",
                    entries = state.movements.sortedBy { it.sortOrder },
                    label = { it.name },
                    detail = { movement -> listOf(movementTypeLabelV4(movement.type), movement.speedFeet?.let { "$it ft" }).filterNotNull().joinToString(" · ") },
                    onOpen = { item -> movementEditorId = item.id.toString(); movementEditorOpen = true },
                    onDelete = { movementDeleteId = it.id.toString() },
                    onAdd = { movementEditorId = null; movementEditorOpen = true },
                    addLabel = "Añadir movimiento",
                )
            }
        }
    }

    if (defenseEditorOpen) {
        val existing = defenseEditorId?.let { id -> state.defenses.firstOrNull { it.id.toString() == id } }
        DefenseEditorDialogV4(existing, { defenseEditorOpen = false }) { saved ->
            val updated = if (existing == null) state.defenses + saved.copy(sortOrder = state.defenses.size)
            else state.defenses.map { if (it.id == existing.id) saved.copy(sortOrder = it.sortOrder) else it }
            onStateChange(state.copy(defenses = updated))
            defenseEditorOpen = false
        }
    }
    defenseDeleteId?.let { id ->
        val target = state.defenses.firstOrNull { it.id.toString() == id }
        if (target == null) defenseDeleteId = null else CharacterNamedDeleteConfirmationDialog(
            itemName = target.name,
            itemTypeLabel = "defensa",
            onDismissRequest = { defenseDeleteId = null },
            onConfirm = {
                onStateChange(state.copy(defenses = state.defenses.filterNot { it.id == target.id }.mapIndexed { index, item -> item.copy(sortOrder = index) }))
                defenseDeleteId = null
            },
        )
    }

    if (senseEditorOpen) {
        val existing = senseEditorId?.let { id -> state.senses.firstOrNull { it.id.toString() == id } }
        SenseEditorDialogV4(existing, { senseEditorOpen = false }) { saved ->
            val updated = if (existing == null) state.senses + saved.copy(sortOrder = state.senses.size)
            else state.senses.map { if (it.id == existing.id) saved.copy(sortOrder = it.sortOrder) else it }
            onStateChange(state.copy(senses = updated))
            senseEditorOpen = false
        }
    }
    senseDeleteId?.let { id ->
        val target = state.senses.firstOrNull { it.id.toString() == id }
        if (target == null) senseDeleteId = null else CharacterNamedDeleteConfirmationDialog(
            itemName = target.name,
            itemTypeLabel = "sentido",
            onDismissRequest = { senseDeleteId = null },
            onConfirm = {
                onStateChange(state.copy(senses = state.senses.filterNot { it.id == target.id }.mapIndexed { index, item -> item.copy(sortOrder = index) }))
                senseDeleteId = null
            },
        )
    }

    if (movementEditorOpen) {
        val existing = movementEditorId?.let { id -> state.movements.firstOrNull { it.id.toString() == id } }
        MovementEditorDialogV4(existing, { movementEditorOpen = false }) { saved ->
            val updated = if (existing == null) state.movements + saved.copy(sortOrder = state.movements.size)
            else state.movements.map { if (it.id == existing.id) saved.copy(sortOrder = it.sortOrder) else it }
            onStateChange(state.copy(movements = updated))
            movementEditorOpen = false
        }
    }
    movementDeleteId?.let { id ->
        val target = state.movements.firstOrNull { it.id.toString() == id }
        if (target == null) movementDeleteId = null else CharacterNamedDeleteConfirmationDialog(
            itemName = target.name,
            itemTypeLabel = "movimiento",
            onDismissRequest = { movementDeleteId = null },
            onConfirm = {
                onStateChange(state.copy(movements = state.movements.filterNot { it.id == target.id }.mapIndexed { index, item -> item.copy(sortOrder = index) }))
                movementDeleteId = null
            },
        )
    }
}

@Composable
private fun <T> GeneralReferenceGroupV4(
    title: String,
    entries: List<T>,
    label: (T) -> String,
    detail: (T) -> String?,
    onOpen: (T) -> Unit,
    onDelete: (T) -> Unit,
    onAdd: () -> Unit,
    addLabel: String,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small, tonalElevation = 1.dp) {
        Column(modifier = Modifier.fillMaxWidth().padding(6.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            if (entries.isEmpty()) Text("Sin registros", style = MaterialTheme.typography.labelSmall)
            entries.forEach { entry ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(1f).clickable { onOpen(entry) }.padding(vertical = 4.dp),
                    ) {
                        Text(label(entry), style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        detail(entry)?.takeIf { it.isNotBlank() }?.let { Text(it, style = MaterialTheme.typography.labelSmall, maxLines = 1) }
                    }
                    TextButton(onClick = { onDelete(entry) }) { Text("Eliminar") }
                }
            }
            TextButton(onClick = onAdd) { Text("+ $addLabel") }
        }
    }
}

@Composable
private fun DefenseEditorDialogV4(
    existing: CharacterDefense?,
    onDismiss: () -> Unit,
    onSave: (CharacterDefense) -> Unit,
) {
    var typeName by rememberSaveable { mutableStateOf((existing?.type ?: CharacterDefenseType.RESISTANCE).name) }
    var name by rememberSaveable { mutableStateOf(existing?.name.orEmpty()) }
    var source by rememberSaveable { mutableStateOf(existing?.source.orEmpty()) }
    var notes by rememberSaveable { mutableStateOf(existing?.notes.orEmpty()) }
    val type = runCatching { CharacterDefenseType.valueOf(typeName) }.getOrDefault(CharacterDefenseType.RESISTANCE)
    CharacterImeSafeEditorDialog(
        title = if (existing == null) "Añadir defensa" else "Editar defensa",
        onCancel = onDismiss,
        onSave = { onSave(CharacterDefense(existing?.id ?: Uuid.random(), type, name.trim(), source.trim().takeIf { it.isNotEmpty() }, notes.trim().takeIf { it.isNotEmpty() }, existing?.sortOrder ?: 0)) },
        saveEnabled = name.trim().isNotEmpty(),
    ) {
        EnumDropdownV4("Tipo", defenseTypeLabelV4(type), CharacterDefenseType.entries.map { it.name to defenseTypeLabelV4(it) }) { typeName = it }
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Daño / efecto") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = source, onValueChange = { source = it }, label = { Text("Fuente opcional") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
    }
}

@Composable
private fun SenseEditorDialogV4(existing: CharacterSense?, onDismiss: () -> Unit, onSave: (CharacterSense) -> Unit) {
    var name by rememberSaveable { mutableStateOf(existing?.name.orEmpty()) }
    var range by rememberSaveable { mutableStateOf(existing?.rangeFeet?.toString().orEmpty()) }
    var notes by rememberSaveable { mutableStateOf(existing?.notes.orEmpty()) }
    val parsedRange = range.takeIf { it.isNotBlank() }?.toIntOrNull()
    val valid = name.trim().isNotEmpty() && (range.isBlank() || (parsedRange != null && parsedRange >= 0))
    CharacterImeSafeEditorDialog(
        title = if (existing == null) "Añadir sentido" else "Editar sentido",
        onCancel = onDismiss,
        onSave = { onSave(CharacterSense(existing?.id ?: Uuid.random(), name.trim(), parsedRange, notes.trim().takeIf { it.isNotEmpty() }, existing?.sortOrder ?: 0)) },
        saveEnabled = valid,
    ) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Sentido") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = range, onValueChange = { range = it.filter(Char::isDigit) }, label = { Text("Alcance en pies (opcional)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
    }
}

@Composable
private fun MovementEditorDialogV4(existing: CharacterMovement?, onDismiss: () -> Unit, onSave: (CharacterMovement) -> Unit) {
    var typeName by rememberSaveable { mutableStateOf((existing?.type ?: CharacterMovementType.FLY).name) }
    var name by rememberSaveable { mutableStateOf(existing?.name.orEmpty()) }
    var speed by rememberSaveable { mutableStateOf(existing?.speedFeet?.toString().orEmpty()) }
    var notes by rememberSaveable { mutableStateOf(existing?.notes.orEmpty()) }
    val type = runCatching { CharacterMovementType.valueOf(typeName) }.getOrDefault(CharacterMovementType.OTHER)
    val parsedSpeed = speed.takeIf { it.isNotBlank() }?.toIntOrNull()
    val valid = name.trim().isNotEmpty() && (speed.isBlank() || (parsedSpeed != null && parsedSpeed >= 0))
    CharacterImeSafeEditorDialog(
        title = if (existing == null) "Añadir movimiento" else "Editar movimiento",
        onCancel = onDismiss,
        onSave = { onSave(CharacterMovement(existing?.id ?: Uuid.random(), type, name.trim(), parsedSpeed, notes.trim().takeIf { it.isNotEmpty() }, existing?.sortOrder ?: 0)) },
        saveEnabled = valid,
    ) {
        EnumDropdownV4("Tipo", movementTypeLabelV4(type), CharacterMovementType.entries.map { it.name to movementTypeLabelV4(it) }) { typeName = it }
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = speed, onValueChange = { speed = it.filter(Char::isDigit) }, label = { Text("Velocidad en pies (opcional)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
    }
}

@Composable
private fun EnumDropdownV4(
    label: String,
    currentLabel: String,
    options: List<Pair<String, String>>,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text(currentLabel) }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { (key, optionLabel) ->
                    DropdownMenuItem(text = { Text(optionLabel) }, onClick = { onSelect(key); expanded = false })
                }
            }
        }
    }
}

private fun defenseTypeLabelV4(type: CharacterDefenseType): String = when (type) {
    CharacterDefenseType.RESISTANCE -> "Resistencia"
    CharacterDefenseType.IMMUNITY -> "Inmunidad"
    CharacterDefenseType.VULNERABILITY -> "Vulnerabilidad"
}

private fun movementTypeLabelV4(type: CharacterMovementType): String = when (type) {
    CharacterMovementType.FLY -> "Vuelo"
    CharacterMovementType.SWIM -> "Nado"
    CharacterMovementType.CLIMB -> "Trepar"
    CharacterMovementType.BURROW -> "Excavar"
    CharacterMovementType.OTHER -> "Otro"
}
