from pathlib import Path

root = Path('shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character')
changed = []
for path in root.glob('*.kt'):
    text = path.read_text()
    needle = 'newVersion = AppDatabase.Schema.version,'
    if needle not in text:
        continue
    # Historical migration tests used the then-current schema 13 as their endpoint.
    # Pin them there so later unrelated migrations do not require their deliberately
    # minimal fixture schemas to contain every table in the application.
    updated = text.replace(needle, 'newVersion = 13,')
    path.write_text(updated)
    changed.append(path.name)

if not changed:
    raise SystemExit('expected at least one historical migration test to pin')

Path(root / 'CharacterSpellcastingOriginMigrationTest.kt').write_text('''package io.github.mrsimkin.dndcustomaid.shared.character\n\nimport app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver\nimport io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase\nimport java.io.File\nimport java.sql.DriverManager\nimport kotlin.test.Test\nimport kotlin.test.assertEquals\nimport kotlin.test.assertNull\n\nclass CharacterSpellcastingOriginMigrationTest {\n    @Test\n    fun migrationThirteenClassifiesLegacySpellSourcesWithoutLosingNames() {\n        val file = File.createTempFile("dnd-custom-aid-spell-origin", ".db")\n        file.delete()\n        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"\n        try {\n            DriverManager.getConnection(jdbcUrl).use { connection ->\n                connection.createStatement().use { statement ->\n                    statement.executeUpdate(\n                        """CREATE TABLE character_spell_source (\n                            id TEXT NOT NULL PRIMARY KEY,\n                            character_id TEXT NOT NULL,\n                            name TEXT NOT NULL,\n                            linked_class_id TEXT,\n                            sort_order INTEGER NOT NULL,\n                            UNIQUE(character_id, sort_order)\n                        )""".trimIndent(),\n                    )\n                    statement.executeUpdate(\n                        "INSERT INTO character_spell_source VALUES ('class-source', 'pc', 'Paladín', 'class-paladin', 0)",\n                    )\n                    statement.executeUpdate(\n                        "INSERT INTO character_spell_source VALUES ('custom-source', 'pc', 'Objeto heredado', NULL, 1)",\n                    )\n                }\n            }\n\n            val driver = JdbcSqliteDriver(jdbcUrl)\n            AppDatabase.Schema.migrate(driver = driver, oldVersion = 13, newVersion = AppDatabase.Schema.version)\n            val connection = DriverManager.getConnection(jdbcUrl)\n            connection.use { jdbc ->\n                jdbc.createStatement().use { statement ->\n                    statement.executeQuery(\n                        "SELECT name, origin_kind, origin_reference_id FROM character_spell_source ORDER BY sort_order",\n                    ).use { rows ->\n                        rows.next()\n                        assertEquals("Paladín", rows.getString("name"))\n                        assertEquals("CLASS", rows.getString("origin_kind"))\n                        assertNull(rows.getString("origin_reference_id"))\n                        rows.next()\n                        assertEquals("Objeto heredado", rows.getString("name"))\n                        assertEquals("OTHER", rows.getString("origin_kind"))\n                        assertNull(rows.getString("origin_reference_id"))\n                    }\n                }\n            }\n            driver.close()\n        } finally {\n            file.delete()\n        }\n    }\n}\n''')

print('Pinned historical migration tests:', ', '.join(sorted(changed)))
