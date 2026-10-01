package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * Semantic destinations used by PC-sheet PDF rendering.
 *
 * These are intentionally more specific than the historical Extended-page kinds: a renderer may
 * place several active semantic modules on one physical Extended page, but a record must keep its
 * semantic association throughout that composition.
 */
enum class PcSheetSemanticModule {
    CUSTOM_STATISTICS,
    TRAITS,
    BACKGROUND_STORY,
    COMBAT_ACTIONS,
    RESOURCES,
    CLASS_CHOICES,
    ORDINARY_EQUIPMENT,
    SPECIAL_EQUIPMENT,
    NOTES,
    SPELLS,
}

data class PcSheetSemanticRecordRef(
    val module: PcSheetSemanticModule,
    val stableKey: String,
    val displayName: String,
) {
    init {
        require(stableKey.isNotBlank()) { "Semantic record stable key is required." }
        require(displayName.isNotBlank()) { "Semantic record display name is required." }
    }
}

fun CharacterTrait.pcSheetSemanticRecordRef(): PcSheetSemanticRecordRef =
    PcSheetSemanticRecordRef(
        module = PcSheetSemanticModule.TRAITS,
        stableKey = id.toString(),
        displayName = name,
    )

fun CharacterCombatEntry.pcSheetSemanticRecordRef(): PcSheetSemanticRecordRef =
    PcSheetSemanticRecordRef(
        module = PcSheetSemanticModule.COMBAT_ACTIONS,
        stableKey = id.toString(),
        displayName = name,
    )

fun CharacterResource.pcSheetSemanticRecordRef(): PcSheetSemanticRecordRef =
    PcSheetSemanticRecordRef(
        module = PcSheetSemanticModule.RESOURCES,
        stableKey = id.toString(),
        displayName = name,
    )

fun CharacterClassOption.pcSheetSemanticRecordRef(): PcSheetSemanticRecordRef =
    PcSheetSemanticRecordRef(
        module = PcSheetSemanticModule.CLASS_CHOICES,
        stableKey = id.toString(),
        displayName = name,
    )

fun CharacterInventoryItem.pcSheetSemanticRecordRef(): PcSheetSemanticRecordRef =
    PcSheetSemanticRecordRef(
        module = if (special) {
            PcSheetSemanticModule.SPECIAL_EQUIPMENT
        } else {
            PcSheetSemanticModule.ORDINARY_EQUIPMENT
        },
        stableKey = id.toString(),
        displayName = name,
    )

fun CharacterNote.pcSheetSemanticRecordRef(): PcSheetSemanticRecordRef =
    PcSheetSemanticRecordRef(
        module = PcSheetSemanticModule.NOTES,
        stableKey = id.toString(),
        displayName = title.ifBlank { "Nota" },
    )

fun CharacterSpell.pcSheetSemanticRecordRef(): PcSheetSemanticRecordRef =
    PcSheetSemanticRecordRef(
        module = PcSheetSemanticModule.SPELLS,
        stableKey = id.toString(),
        displayName = name,
    )

enum class PcSheetContinuationSurface {
    NORMAL,
    EXTENDED,
}

data class PcSheetContinuationEndpoint(
    val module: PcSheetSemanticModule,
    val sectionName: String,
    val surface: PcSheetContinuationSurface,
    val extendedIndex: Int? = null,
    val recordLabel: String? = null,
    val columnIndex: Int? = null,
) {
    init {
        require(sectionName.isNotBlank()) { "Continuation section name is required." }
        columnIndex?.let {
            require(it > 0) { "Continuation column index must be positive." }
        }
        when (surface) {
            PcSheetContinuationSurface.NORMAL ->
                require(extendedIndex == null) {
                    "Normal sections cannot carry an Extended sequence number."
                }

            PcSheetContinuationSurface.EXTENDED ->
                require(extendedIndex != null && extendedIndex > 0) {
                    "Extended sections require a positive sequence number."
                }
        }
    }

    fun ownerFacingReference(): String = buildString {
        append(
            when (surface) {
                PcSheetContinuationSurface.NORMAL -> "sección normal "
                PcSheetContinuationSurface.EXTENDED -> "sección extendida "
            },
        )
        append(sectionName.trim().uppercase())
        if (surface == PcSheetContinuationSurface.EXTENDED) {
            append(' ')
            append(requireNotNull(extendedIndex).toString().padStart(2, '0'))
        }
        columnIndex?.let {
            append(" · columna ")
            append(it)
        }
        recordLabel?.trim()?.takeIf { it.isNotEmpty() }?.let {
            append(" / ")
            append(it)
        }
    }
}

data class PcSheetBidirectionalContinuation(
    val record: PcSheetSemanticRecordRef,
    val source: PcSheetContinuationEndpoint,
    val target: PcSheetContinuationEndpoint,
) {
    init {
        require(record.module == source.module) {
            "Continuation source must preserve the semantic module of the record."
        }
        require(record.module == target.module) {
            "Continuation target must preserve the semantic module of the record."
        }
        require(source != target) { "Continuation source and target must differ." }
    }

    fun sourceMarker(): String =
        "[continúa en ${target.ownerFacingReference()}]"

    fun targetMarker(): String =
        "[proviene de ${source.ownerFacingReference()}]"
}

/**
 * Paper-first tracker state.
 *
 * [currentAvailable] preserves the digital snapshot at export time while [compactEditableLabel]
 * deliberately leaves a writable blank for table use.
 */
data class PcSheetWritableTrackerState(
    val currentAvailable: Int,
    val maximum: Int,
) {
    init {
        require(maximum > 0) { "Writable tracker maximum must be positive." }
        require(currentAvailable in 0..maximum) {
            "Writable tracker current value must be between zero and maximum."
        }
    }

    fun compactEditableLabel(writableBlank: String = "____"): String {
        require(writableBlank.isNotBlank()) { "Writable tracker blank must be visible." }
        return "$writableBlank($currentAvailable)/$maximum"
    }

    companion object {
        fun fromSpent(maximum: Int, spent: Int): PcSheetWritableTrackerState {
            require(maximum > 0) { "Writable tracker maximum must be positive." }
            require(spent in 0..maximum) {
                "Spent amount must be between zero and maximum."
            }
            return PcSheetWritableTrackerState(
                currentAvailable = maximum - spent,
                maximum = maximum,
            )
        }
    }
}

fun CharacterTrait.pcSheetWritableUsesTrackerOrNull(): PcSheetWritableTrackerState? =
    maxUses?.let { maximum ->
        PcSheetWritableTrackerState.fromSpent(
            maximum = maximum,
            spent = spentUses,
        )
    }

fun CharacterResource.pcSheetWritableTrackerOrNull(): PcSheetWritableTrackerState? =
    maxValue?.let { maximum ->
        PcSheetWritableTrackerState(
            currentAvailable = currentValue,
            maximum = maximum,
        )
    }

enum class PcSheetSemanticTextDisposition {
    NATIVE_SINGLE_LINE,
    UNIFORM_COMPRESSED_SINGLE_LINE,
    WRAPPED,
    EXPLICIT_CONTINUATION,
}

/**
 * Renderer-measured fit inputs for meaningful semantic text.
 *
 * The renderer owns font metrics and wrapping. This shared policy owns the decision order:
 * native size -> readable uniform compression -> readable uniform wrapping -> explicit continuation.
 * Semantic ellipsis is intentionally not an available disposition.
 */
data class PcSheetSemanticTextFitInput(
    val fitsNativeSingleLine: Boolean,
    val requiredSingleLineScale: Float?,
    val wrappedLineCount: Int,
    val availableWrappedLines: Int,
    val wrappedUniformScale: Float,
    val minimumReadableScale: Float,
) {
    init {
        requiredSingleLineScale?.let {
            require(it > 0f && it <= 1f) { "Single-line scale must be in (0, 1]." }
        }
        require(wrappedLineCount >= 1) { "Wrapped line count must be positive." }
        require(availableWrappedLines >= 1) { "Available wrapped-line capacity must be positive." }
        require(wrappedUniformScale > 0f && wrappedUniformScale <= 1f) {
            "Wrapped uniform scale must be in (0, 1]."
        }
        require(minimumReadableScale > 0f && minimumReadableScale <= 1f) {
            "Minimum readable scale must be in (0, 1]."
        }
    }
}

data class PcSheetSemanticTextFitDecision(
    val disposition: PcSheetSemanticTextDisposition,
    val uniformScale: Float?,
) {
    val usesSemanticEllipsis: Boolean
        get() = false
}

fun decidePcSheetSemanticTextFit(
    input: PcSheetSemanticTextFitInput,
): PcSheetSemanticTextFitDecision {
    if (input.fitsNativeSingleLine) {
        return PcSheetSemanticTextFitDecision(
            disposition = PcSheetSemanticTextDisposition.NATIVE_SINGLE_LINE,
            uniformScale = 1f,
        )
    }

    val singleLineScale = input.requiredSingleLineScale
    if (singleLineScale != null && singleLineScale >= input.minimumReadableScale) {
        return PcSheetSemanticTextFitDecision(
            disposition = PcSheetSemanticTextDisposition.UNIFORM_COMPRESSED_SINGLE_LINE,
            uniformScale = singleLineScale,
        )
    }

    if (
        input.wrappedLineCount <= input.availableWrappedLines &&
        input.wrappedUniformScale >= input.minimumReadableScale
    ) {
        return PcSheetSemanticTextFitDecision(
            disposition = PcSheetSemanticTextDisposition.WRAPPED,
            uniformScale = input.wrappedUniformScale,
        )
    }

    return PcSheetSemanticTextFitDecision(
        disposition = PcSheetSemanticTextDisposition.EXPLICIT_CONTINUATION,
        uniformScale = null,
    )
}
