package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

enum class CharacterRestKind {
    SHORT,
    LONG,
}

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
 * Builds a permissive rest preview from durable resource values + optional structured recovery
 * metadata. Nothing is changed by this function, and custom/manual descriptions remain visible
 * even when no numeric recovery can be proposed safely.
 */
fun previewResourceRecovery(
    rest: CharacterRestKind,
    resources: List<CharacterResource>,
    recoveryRules: List<CharacterResourceRecovery>,
): List<CharacterResourceRestPreview> {
    val rulesByResource = recoveryRules.associateBy(CharacterResourceRecovery::resourceId)

    return resources.mapNotNull { resource ->
        val rule = rulesByResource[resource.id]

        if (rule == null) {
            resource.recovery
                ?.trim()
                ?.takeIf { it.isNotEmpty() }
                ?.let { description ->
                    CharacterResourceRestPreview(
                        resourceId = resource.id,
                        resourceName = resource.name,
                        currentValue = resource.currentValue,
                        maxValue = resource.maxValue,
                        proposedValue = null,
                        detail = description,
                    )
                }
        } else if (rule.cadence == CharacterRecoveryCadence.MANUAL) {
            CharacterResourceRestPreview(
                resourceId = resource.id,
                resourceName = resource.name,
                currentValue = resource.currentValue,
                maxValue = resource.maxValue,
                proposedValue = null,
                detail = rule.notes?.trim()?.takeIf { it.isNotEmpty() }
                    ?: resource.recovery?.trim()?.takeIf { it.isNotEmpty() }
                    ?: "Recuperación manual",
            )
        } else if (!rule.appliesTo(rest)) {
            null
        } else {
            val proposed = when (rule.amountMode) {
                CharacterRecoveryAmountMode.TO_MAX -> resource.maxValue
                CharacterRecoveryAmountMode.FIXED -> rule.fixedAmount?.let { amount ->
                    val recovered = resource.currentValue + amount
                    resource.maxValue?.let { max -> recovered.coerceAtMost(max) } ?: recovered
                }
                CharacterRecoveryAmountMode.NONE -> null
            }
            val detail = when (rule.amountMode) {
                CharacterRecoveryAmountMode.TO_MAX -> "Recuperar hasta el máximo"
                CharacterRecoveryAmountMode.FIXED -> "Recuperar ${rule.fixedAmount ?: 0}"
                CharacterRecoveryAmountMode.NONE -> rule.notes?.trim()?.takeIf { it.isNotEmpty() }
                    ?: resource.recovery?.trim()?.takeIf { it.isNotEmpty() }
                    ?: "Revisar recuperación manualmente"
            }
            CharacterResourceRestPreview(
                resourceId = resource.id,
                resourceName = resource.name,
                currentValue = resource.currentValue,
                maxValue = resource.maxValue,
                proposedValue = proposed,
                detail = detail,
            )
        }
    }
}

/** Applies only explicitly selected numeric proposals. Manual-review rows can never mutate state. */
fun applySelectedResourceRecovery(
    resources: List<CharacterResource>,
    preview: List<CharacterResourceRestPreview>,
    selectedResourceIds: Set<Uuid>,
): List<CharacterResource> {
    val selectedChanges = preview
        .asSequence()
        .filter { it.resourceId in selectedResourceIds }
        .mapNotNull { item -> item.proposedValue?.let { value -> item.resourceId to value } }
        .toMap()

    return resources.map { resource ->
        selectedChanges[resource.id]?.let { proposed ->
            resource.copy(currentValue = proposed)
        } ?: resource
    }
}

private fun CharacterResourceRecovery.appliesTo(rest: CharacterRestKind): Boolean = when (cadence) {
    CharacterRecoveryCadence.SHORT_REST -> rest == CharacterRestKind.SHORT
    CharacterRecoveryCadence.LONG_REST -> rest == CharacterRestKind.LONG
    CharacterRecoveryCadence.SHORT_OR_LONG_REST -> true
    CharacterRecoveryCadence.NONE,
    CharacterRecoveryCadence.MANUAL,
    -> false
}
