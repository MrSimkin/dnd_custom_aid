from pathlib import Path

path = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterManagementTabV4.kt")
text = path.read_text(encoding="utf-8")

old_smartcast = """                    OutlinedButton(
                        onClick = { onAdjust(resource, 1) },
                        enabled = resource.maxValue == null || resource.currentValue < resource.maxValue,
                    ) { Text("+") }
"""
new_smartcast = """                    OutlinedButton(
                        onClick = { onAdjust(resource, 1) },
                        enabled = resource.maxValue?.let { max -> resource.currentValue < max } ?: true,
                    ) { Text("+") }
"""
if text.count(old_smartcast) != 1:
    raise RuntimeError(f"resource max smart-cast block expected once, found {text.count(old_smartcast)}")
text = text.replace(old_smartcast, new_smartcast, 1)

old_validation = """    val parsedFixed = fixedAmount.takeIf { it.isNotBlank() }?.toIntOrNull()
    val valid = name.trim().isNotEmpty() && parsedCurrent != null && parsedCurrent >= 0 &&
        (maximum.isBlank() || (parsedMaximum != null && parsedMaximum >= parsedCurrent)) &&
        (amountMode != CharacterRecoveryAmountMode.FIXED || (parsedFixed != null && parsedFixed >= 0))
"""
new_validation = """    val parsedFixed = fixedAmount.takeIf { it.isNotBlank() }?.toIntOrNull()
    val automaticCadence = cadence != CharacterRecoveryCadence.NONE && cadence != CharacterRecoveryCadence.MANUAL
    val valid = name.trim().isNotEmpty() && parsedCurrent != null && parsedCurrent >= 0 &&
        (maximum.isBlank() || (parsedMaximum != null && parsedMaximum >= parsedCurrent)) &&
        (!automaticCadence || amountMode != CharacterRecoveryAmountMode.FIXED || (parsedFixed != null && parsedFixed >= 0))
"""
if text.count(old_validation) != 1:
    raise RuntimeError(f"resource recovery validation block expected once, found {text.count(old_validation)}")
text = text.replace(old_validation, new_validation, 1)

old_normalized = """            val normalizedCadence = if (cadence == CharacterRecoveryCadence.MANUAL) cadence else cadence
"""
new_normalized = """            val normalizedCadence = cadence
"""
if text.count(old_normalized) != 1:
    raise RuntimeError(f"normalized cadence line expected once, found {text.count(old_normalized)}")
text = text.replace(old_normalized, new_normalized, 1)

path.write_text(text, encoding="utf-8")
print("Gestión compile and recovery validation repair applied.")
