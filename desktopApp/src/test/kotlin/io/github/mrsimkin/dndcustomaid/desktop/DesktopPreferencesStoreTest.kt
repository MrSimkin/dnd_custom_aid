package io.github.mrsimkin.dndcustomaid.desktop

import java.nio.file.Files
import java.util.Properties
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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

    @Test
    fun removedStoredFontFallsBackToStableSystemSans() {
        assertEquals(
            DesktopFontChoice.SANS_SERIF,
            resolveDesktopFontChoice("REMOVED_FONT", installedFamilies = emptySet()),
        )
    }

    @Test
    fun unavailableNamedSystemFontDoesNotPretendToBeSelectable() {
        assertFalse(DesktopFontChoice.MANROPE.isAvailable(installedFamilies = emptySet()))
        assertEquals(
            DesktopFontChoice.SANS_SERIF,
            resolveDesktopFontChoice("MANROPE", installedFamilies = emptySet()),
        )
    }

    @Test
    fun bundledFontsRemainAvailableWithoutSystemInstallation() {
        assertTrue(DesktopFontChoice.GEIST.isAvailable(installedFamilies = emptySet()))
        assertTrue(DesktopFontChoice.MONA_SANS_CONDENSED.isAvailable(installedFamilies = emptySet()))

        val selectable = DesktopFontChoice.selectableChoices(installedFamilies = emptySet())
        assertTrue(DesktopFontChoice.GEIST in selectable)
        assertTrue(DesktopFontChoice.MONA_SANS_CONDENSED in selectable)
        assertFalse(DesktopFontChoice.MANROPE in selectable)
    }

    @Test
    fun unavailableStoredNamedFontFallsBackWhenLoadingPreferences() {
        val directory = Files.createTempDirectory("dnd-custom-aid-desktop-preferences-font-fallback")
        val file = directory.resolve("desktop_preferences.properties")
        val properties = Properties().apply {
            setProperty("font_choice", "A_FONT_THAT_DOES_NOT_EXIST")
        }
        Files.newOutputStream(file).use(properties::store)

        val loaded = DesktopPreferencesStore(file).load()

        assertEquals(DesktopFontChoice.SANS_SERIF, loaded.fontChoice)
    }
}
