package io.github.mrsimkin.dndcustomaid.shared.db

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

/** Refactor F5 (AGENTS §6.4): a test run of the App de PC never uses the owner's real data folder. */
class DesktopDataDirectoryTest {
    private val home = File("C:/Users/someone").path

    @Test
    fun normalRunUsesTheFolderInHome() {
        assertEquals(File(home, ".dnd_custom_aid"), DesktopDatabaseFactory.defaultDataDirectory(null, null, home))
    }

    @Test
    fun javaPropertyPointsAtATestFolder() {
        val folder = File("D:/qa/datos-prueba")
        assertEquals(folder.absoluteFile, DesktopDatabaseFactory.defaultDataDirectory(folder.path, null, home))
    }

    @Test
    fun environmentVariableWorksForThePackagedApp() {
        val folder = File("D:/qa/otra")
        assertEquals(folder.absoluteFile, DesktopDatabaseFactory.defaultDataDirectory(null, folder.path, home))
    }

    @Test
    fun propertyWinsOverEnvironmentAndBlankValuesAreIgnored() {
        val a = File("D:/qa/a")
        assertEquals(a.absoluteFile, DesktopDatabaseFactory.defaultDataDirectory(a.path, "D:/qa/b", home))
        assertEquals(File(home, ".dnd_custom_aid"), DesktopDatabaseFactory.defaultDataDirectory("  ", "", home))
    }
}
