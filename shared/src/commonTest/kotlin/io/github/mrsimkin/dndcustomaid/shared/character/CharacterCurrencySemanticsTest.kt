package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CharacterCurrencySemanticsTest {
    @Test
    fun canonicalAndLocalizedDefaultCurrenciesResolveToStandardKinds() {
        val canonical = listOf(
            CharacterCurrency("cp", "Cobre", 1, 0, true),
            CharacterCurrency("sp", "Plata", 2, 1, true),
            CharacterCurrency("ep", "Electrum", 3, 2, true),
            CharacterCurrency("gp", "Oro", 4, 3, true),
            CharacterCurrency("pp", "Platino", 5, 4, true),
        )

        assertEquals(1, canonical.standardCurrency(StandardCurrencyKind.COPPER)?.amount)
        assertEquals(2, canonical.standardCurrency(StandardCurrencyKind.SILVER)?.amount)
        assertEquals(3, canonical.standardCurrency(StandardCurrencyKind.ELECTRUM)?.amount)
        assertEquals(4, canonical.standardCurrency(StandardCurrencyKind.GOLD)?.amount)
        assertEquals(5, canonical.standardCurrency(StandardCurrencyKind.PLATINUM)?.amount)

        assertEquals(
            StandardCurrencyKind.SILVER,
            CharacterCurrency("pp", "Plata", 7, 0, true).standardCurrencyKindOrNull(),
        )
        assertEquals(
            StandardCurrencyKind.PLATINUM,
            CharacterCurrency("pp", "Platinum", 8, 0, true).standardCurrencyKindOrNull(),
        )
    }

    @Test
    fun customCurrencyNeverMasqueradesAsStandardEvenWithFamiliarKey() {
        assertNull(
            CharacterCurrency("gp", "Fichas de gremio", 12, 0, false)
                .standardCurrencyKindOrNull(),
        )
    }
}
