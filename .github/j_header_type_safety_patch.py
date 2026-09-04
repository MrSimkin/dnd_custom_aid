from pathlib import Path


def replace_exact(path: str, old: str, new: str, count: int = 1) -> None:
    file_path = Path(path)
    text = file_path.read_text()
    actual = text.count(old)
    if actual != count:
        raise RuntimeError(f"{path}: expected {count} occurrence(s), found {actual}")
    file_path.write_text(text.replace(old, new))


backup = "shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterBackup.kt"
replace_exact(
    backup,
    "import kotlinx.serialization.json.Json\n"
    "import kotlinx.serialization.json.jsonObject\n"
    "import kotlinx.serialization.json.jsonPrimitive\n",
    "import kotlinx.serialization.json.Json\n"
    "import kotlinx.serialization.json.JsonPrimitive\n"
    "import kotlinx.serialization.json.jsonObject\n",
)
replace_exact(
    backup,
    "        val format = root[\"format\"]?.jsonPrimitive?.content\n",
    "        val format = (root[\"format\"] as? JsonPrimitive)?.content\n",
)
replace_exact(
    backup,
    "        val version = root[\"version\"]?.jsonPrimitive?.content?.toIntOrNull()\n",
    "        val version = (root[\"version\"] as? JsonPrimitive)?.content?.toIntOrNull()\n",
)


test = "shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterBackupTest.kt"
needle = '''        assertEquals(
            CharacterBackupErrorCode.UNSUPPORTED_VERSION,
            assertIs<CharacterBackupDecodeResult.Failure>(
                CharacterBackupCodec.decode("{\\\"format\\\":\\\"$CHARACTER_BACKUP_FORMAT\\\",\\\"version\\\":999}"),
            ).error.code,
        )
    }

    @Test
    fun importPlanRestoresAsNewCopyAndRemapsInternalReferences() {
'''
replacement = '''        assertEquals(
            CharacterBackupErrorCode.UNSUPPORTED_VERSION,
            assertIs<CharacterBackupDecodeResult.Failure>(
                CharacterBackupCodec.decode("{\\\"format\\\":\\\"$CHARACTER_BACKUP_FORMAT\\\",\\\"version\\\":999}"),
            ).error.code,
        )
    }

    @Test
    fun nonPrimitiveHeaderValuesReturnControlledFailures() {
        assertEquals(
            CharacterBackupErrorCode.WRONG_FORMAT,
            assertIs<CharacterBackupDecodeResult.Failure>(
                CharacterBackupCodec.decode("{\\\"format\\\":{},\\\"version\\\":1}"),
            ).error.code,
        )
        assertEquals(
            CharacterBackupErrorCode.UNSUPPORTED_VERSION,
            assertIs<CharacterBackupDecodeResult.Failure>(
                CharacterBackupCodec.decode("{\\\"format\\\":\\\"$CHARACTER_BACKUP_FORMAT\\\",\\\"version\\\":{}}"),
            ).error.code,
        )
    }

    @Test
    fun importPlanRestoresAsNewCopyAndRemapsInternalReferences() {
'''
replace_exact(test, needle, replacement)
