package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

enum class CharacterRestKind {
    SHORT,
    LONG,
}

/**
 * Resources and Custom Markers intentionally remain different domain entities. This typed target
 * is used only by shared tracker/recovery operations so equal UUID values can never collapse the
 * two identities into one selection key.
 */
enum class CharacterTrackableTargetKind {
    RESOURCE,
    MARKER,
}

data class CharacterTrackableTarget(
    val kind: CharacterTrackableTargetKind,
    val id: Uuid,
)

data class CharacterTrackableRestPreview(
    val target: CharacterTrackableTarget,
    val name: String,
    val valueKind: CharacterTrackableValueKind,
    val currentValue: Int,
    val maxValue: Int?,
    /** Null means the target requires manual review/edit and is never auto-applied. */
    val proposedValue: Int?,
    val detail: String,
) {
    val hasAutomaticChange: Boolean
        get() = proposedValue != null && proposedValue != currentValue

    val requiresManualReview: Boolean
        get() = proposedValue == null
}

data class CharacterTrackableRecoveryApplication(
    val resources: List<CharacterResource>,
    val markers: List<CharacterCustomMarker>,
)

data class CharacterResourceRestPreview(
    val resourceId: Uuid,
    val resourceName: String,
    val currentValue: Int,
    val maxValue: Int?,
    /** Null means the resource requires manual review/edit and is never auto-applied. */
    val proposedValue: Int?,
    val detail: String,
) {
    val hasAutomaticChange: Boolean
        get() = proposedValue != null && proposedValue != currentValue

    val requiresManualReview: Boolean
        get() = proposedValue == null
}

/**
 * Builds one permissive mixed rest preview for canonical Resources + Custom Markers.
 *
 * Automatic proposals are produced only from explicit structured recovery metadata. Legacy
 * Resource recovery text remains visible as review-only information and is never parsed into a
 * numeric rule. Marker notes are general notes, not recovery instructions, and are therefore not
 * interpreted here.
 *
 * Resource placement is intentionally irrelevant to recovery eligibility: placement controls
 * presentation only, while one canonical Resource value participates in the rest mechanic.
 */
fun previewTrackableRecovery(
    rest: CharacterRestKind,
    resources: List<CharacterResource>,
    resourceRecoveryRules: List<CharacterResourceRecovery>,
    resourceConfigurations: List<CharacterResourceSuccessorConfiguration> = emptyList(),
    markers: List<CharacterCustomMarker> = emptyList(),
): List<CharacterTrackableRestPreview> {
    val rulesByResource = resourceRecoveryRules.associateBy(CharacterResourceRecovery::resourceId)
    val configurationsByResource = resourceConfigurations.associateBy(CharacterResourceSuccessorConfiguration::resourceId)

    val resourcePreview = resources.mapNotNull { resource ->
        val rule = rulesByResource[resource.id]
        val valueKind = configurationsByResource[resource.id]?.valueKind
            ?: if (resource.maxValue != null) CharacterTrackableValueKind.CURRENT_MAX else CharacterTrackableValueKind.COUNTER
        val effectiveMax = effectiveTrackableMaximum(valueKind, resource.maxValue)
        val legacyRecoveryText = resource.recovery?.trim()?.takeIf { it.isNotEmpty() }

        if (rule == null) {
            legacyRecoveryText?.let { description ->
                CharacterTrackableRestPreview(
                    target = CharacterTrackableTarget(CharacterTrackableTargetKind.RESOURCE, resource.id),
                    name = resource.name,
                    valueKind = valueKind,
                    currentValue = resource.currentValue,
                    maxValue = effectiveMax,
                    proposedValue = null,
                    detail = description,
                )
            }
        } else {
            previewStructuredTrackableRecovery(
                rest = rest,
                target = CharacterTrackableTarget(CharacterTrackableTargetKind.RESOURCE, resource.id),
                name = resource.name,
                valueKind = valueKind,
                currentValue = resource.currentValue,
                maxValue = effectiveMax,
                cadence = rule.cadence,
                amountMode = rule.amountMode,
                fixedAmount = rule.fixedAmount,
                manualDetail = rule.notes?.trim()?.takeIf { it.isNotEmpty() }
                    ?: legacyRecoveryText
                    ?: "Recuperación manual",
                reviewDetail = rule.notes?.trim()?.takeIf { it.isNotEmpty() }
                    ?: legacyRecoveryText
                    ?: "Revisar recuperación manualmente",
            )
        }
    }

    val markerPreview = markers.mapNotNull { marker ->
        val effectiveMax = effectiveTrackableMaximum(marker.valueKind, marker.maxValue)
        previewStructuredTrackableRecovery(
            rest = rest,
            target = CharacterTrackableTarget(CharacterTrackableTargetKind.MARKER, marker.id),
            name = marker.name,
            valueKind = marker.valueKind,
            currentValue = marker.currentValue,
            maxValue = effectiveMax,
            cadence = marker.recovery.cadence,
            amountMode = marker.recovery.amountMode,
            fixedAmount = marker.recovery.fixedAmount,
            manualDetail = "Recuperación manual",
            reviewDetail = "Revisar recuperación manualmente",
        )
    }

    return resourcePreview + markerPreview
}

/**
 * Applies only explicitly selected numeric proposals from a mixed preview.
 *
 * Resource and Marker selection keys remain typed, so even an identical UUID in both collections
 * cannot cause cross-domain mutation. Review-only rows have no numeric proposal and can never
 * mutate state.
 */
fun applySelectedTrackableRecovery(
    resources: List<CharacterResource>,
    markers: List<CharacterCustomMarker>,
    preview: List<CharacterTrackableRestPreview>,
    selectedTargets: Set<CharacterTrackableTarget>,
): CharacterTrackableRecoveryApplication {
    val selectedChanges = preview
        .asSequence()
        .filter { it.target in selectedTargets }
        .mapNotNull { item -> item.proposedValue?.let { value -> item.target to value } }
        .toMap()

    val updatedResources = resources.map { resource ->
        selectedChanges[CharacterTrackableTarget(CharacterTrackableTargetKind.RESOURCE, resource.id)]?.let { proposed ->
            resource.copy(currentValue = proposed)
        } ?: resource
    }

    val updatedMarkers = markers.map { marker ->
        selectedChanges[CharacterTrackableTarget(CharacterTrackableTargetKind.MARKER, marker.id)]?.let { proposed ->
            marker.copy(currentValue = proposed)
        } ?: marker
    }

    return CharacterTrackableRecoveryApplication(
        resources = updatedResources,
        markers = updatedMarkers,
    )
}

/**
 * Backward-compatible Resource-only facade. The mixed engine above is canonical for recovery
 * mechanics; this API remains stable for existing callers and tests.
 */
fun previewResourceRecovery(
    rest: CharacterRestKind,
    resources: List<CharacterResource>,
    recoveryRules: List<CharacterResourceRecovery>,
): List<CharacterResourceRestPreview> = previewTrackableRecovery(
    rest = rest,
    resources = resources,
    resourceRecoveryRules = recoveryRules,
).mapNotNull { item ->
    if (item.target.kind != CharacterTrackableTargetKind.RESOURCE) return@mapNotNull null
    CharacterResourceRestPreview(
        resourceId = item.target.id,
        resourceName = item.name,
        currentValue = item.currentValue,
        maxValue = item.maxValue,
        proposedValue = item.proposedValue,
        detail = item.detail,
    )
}

/**
 * Backward-compatible Resource-only apply facade. Manual-review rows can never mutate state.
 */
fun applySelectedResourceRecovery(
    resources: List<CharacterResource>,
    preview: List<CharacterResourceRestPreview>,
    selectedResourceIds: Set<Uuid>,
): List<CharacterResource> {
    val mixedPreview = preview.map { item ->
        CharacterTrackableRestPreview(
            target = CharacterTrackableTarget(CharacterTrackableTargetKind.RESOURCE, item.resourceId),
            name = item.resourceName,
            valueKind = if (item.maxValue != null) CharacterTrackableValueKind.CURRENT_MAX else CharacterTrackableValueKind.COUNTER,
            currentValue = item.currentValue,
            maxValue = item.maxValue,
            proposedValue = item.proposedValue,
            detail = item.detail,
        )
    }
    val selectedTargets = selectedResourceIds.mapTo(mutableSetOf()) { resourceId ->
        CharacterTrackableTarget(CharacterTrackableTargetKind.RESOURCE, resourceId)
    }
    return applySelectedTrackableRecovery(
        resources = resources,
        markers = emptyList(),
        preview = mixedPreview,
        selectedTargets = selectedTargets,
    ).resources
}

private fun previewStructuredTrackableRecovery(
    rest: CharacterRestKind,
    target: CharacterTrackableTarget,
    name: String,
    valueKind: CharacterTrackableValueKind,
    currentValue: Int,
    maxValue: Int?,
    cadence: CharacterRecoveryCadence,
    amountMode: CharacterRecoveryAmountMode,
    fixedAmount: Int?,
    manualDetail: String,
    reviewDetail: String,
): CharacterTrackableRestPreview? {
    if (cadence == CharacterRecoveryCadence.NONE) return null

    if (cadence == CharacterRecoveryCadence.MANUAL) {
        return CharacterTrackableRestPreview(
            target = target,
            name = name,
            valueKind = valueKind,
            currentValue = currentValue,
            maxValue = maxValue,
            proposedValue = null,
            detail = manualDetail,
        )
    }

    if (!cadence.appliesTo(rest)) return null

    val proposed = when (amountMode) {
        CharacterRecoveryAmountMode.TO_MAX -> maxValue
        CharacterRecoveryAmountMode.FIXED -> fixedAmount?.let { amount ->
            val recovered = currentValue + amount
            maxValue?.let { maximum -> recovered.coerceAtMost(maximum) } ?: recovered
        }
        CharacterRecoveryAmountMode.NONE -> null
    }
    val detail = when (amountMode) {
        CharacterRecoveryAmountMode.TO_MAX -> "Recuperar hasta el máximo"
        CharacterRecoveryAmountMode.FIXED -> "Recuperar ${fixedAmount ?: 0}"
        CharacterRecoveryAmountMode.NONE -> reviewDetail
    }

    return CharacterTrackableRestPreview(
        target = target,
        name = name,
        valueKind = valueKind,
        currentValue = currentValue,
        maxValue = maxValue,
        proposedValue = proposed,
        detail = detail,
    )
}

private fun effectiveTrackableMaximum(
    valueKind: CharacterTrackableValueKind,
    configuredMaximum: Int?,
): Int? = when (valueKind) {
    CharacterTrackableValueKind.BINARY -> 1
    CharacterTrackableValueKind.CURRENT_MAX -> configuredMaximum
    CharacterTrackableValueKind.COUNTER -> null
}

private fun CharacterRecoveryCadence.appliesTo(rest: CharacterRestKind): Boolean = when (this) {
    CharacterRecoveryCadence.SHORT_REST -> rest == CharacterRestKind.SHORT
    CharacterRecoveryCadence.LONG_REST -> rest == CharacterRestKind.LONG
    CharacterRecoveryCadence.SHORT_OR_LONG_REST -> true
    CharacterRecoveryCadence.NONE,
    CharacterRecoveryCadence.MANUAL,
    -> false
}
