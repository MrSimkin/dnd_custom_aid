package io.github.mrsimkin.dndcustomaid.desktop

import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

class DesktopPreferencesStoreTest {
    @Test
    fun preferencesRoundTripPersistsPresentationChoices() {
        val directory = Files.createTempDirectory("dnd-custom-aid-desktop-preferences-test")
        val file = directory.resolve("desktop_preferences.properties")
        val store = DesktopPreferencesStore(file)
        val expected = DesktopPreferences(
            fontScalePercent = 130,
            spacingScalePercent = 80,
            fontChoice = DesktopFontChoice.MONOSPACE,
            themeChoice = DesktopThemeChoice.PARCHMENT,
            workspaceDensity = DesktopWorkspaceDensity.COMPACT,
        )

        store.save(expected)

        assertEquals(expected, store.load())
    }

    @Test
    fun missingPreferenceFileLoadsDefaults() {
        val directory = Files.createTempDirectory("dnd-custom-aid-desktop-preferences-default-test")
        val store = DesktopPreferencesStore(directory.resolve("missing.properties"))

        assertEquals(DesktopPreferences(), store.load())
    }
}
