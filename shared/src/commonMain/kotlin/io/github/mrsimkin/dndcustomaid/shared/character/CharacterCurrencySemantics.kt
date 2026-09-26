package io.github.mrsimkin.dndcustomaid.shared.character

enum class StandardCurrencyKind {
    COPPER,
    SILVER,
    ELECTRUM,
    GOLD,
    PLATINUM,
}

fun CharacterCurrency.standardCurrencyKindOrNull(): StandardCurrencyKind? {
    if (!isDefault) return null

    val normalizedName = name
        .lowercase()
        .replace('á', 'a')
        .replace('é', 'e')
        .replace('í', 'i')
        .replace('ó', 'o')
        .replace('ú', 'u')
        .trim()

    when (normalizedName) {
        "cobre", "copper" -> return StandardCurrencyKind.COPPER
        "plata", "silver" -> return StandardCurrencyKind.SILVER
        "electrum" -> return StandardCurrencyKind.ELECTRUM
        "oro", "gold" -> return StandardCurrencyKind.GOLD
        "platino", "platinum" -> return StandardCurrencyKind.PLATINUM
    }

    return when (key.lowercase().trim()) {
        "cp", "pc" -> StandardCurrencyKind.COPPER
        "sp" -> StandardCurrencyKind.SILVER
        "ep", "pe" -> StandardCurrencyKind.ELECTRUM
        "gp", "po" -> StandardCurrencyKind.GOLD
        "pp", "pt", "ppt" -> StandardCurrencyKind.PLATINUM
        else -> null
    }
}

fun List<CharacterCurrency>.standardCurrency(kind: StandardCurrencyKind): CharacterCurrency? =
    firstOrNull { it.standardCurrencyKindOrNull() == kind }
