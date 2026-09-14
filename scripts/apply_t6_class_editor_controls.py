#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path

PATH = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterClassIdentitySuccessorV4.kt")
MARKER = "CompactClassHitDieSelectorV4"


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected exactly one match, observed {count}")
    return text.replace(old, new, 1)


def main() -> None:
    text = PATH.read_text(encoding="utf-8")
    if MARKER in text:
        print("T6 class-editor control migration already applied; no changes required.")
        return

    text = replace_once(
        text,
        "import androidx.compose.foundation.layout.padding\n"
        "import androidx.compose.material3.Card\n",
        "import androidx.compose.foundation.layout.padding\n"
        "import androidx.compose.foundation.text.KeyboardOptions\n"
        "import androidx.compose.material3.Card\n",
        "KeyboardOptions import",
    )
    text = replace_once(
        text,
        "import androidx.compose.ui.text.style.TextOverflow\n"
        "import androidx.compose.ui.unit.dp\n",
        "import androidx.compose.ui.text.input.KeyboardType\n"
        "import androidx.compose.ui.text.style.TextOverflow\n"
        "import androidx.compose.ui.unit.dp\n",
        "KeyboardType import",
    )

    old_die = '''            CompactLabeledNumberInputSuccessorV4(
                label = "Dado",
                value = draft.hitDieSides,
                onValueChange = { value -> draft = draft.copy(hitDieSides = value.filter(Char::isDigit)) },
                prefix = "d",
                modifier = Modifier.weight(1f),
            )'''
    new_die = '''            CompactClassHitDieSelectorV4(
                value = draft.hitDieSides,
                onValueChange = { value -> draft = draft.copy(hitDieSides = value) },
                modifier = Modifier.weight(1f),
            )'''
    text = replace_once(text, old_die, new_die, "Dado selector replacement")

    help_anchor = '''        }
        CharacterHelpV4("Los DG máximos se derivan del nivel de esta clase; solo se guarda cuántos quedan disponibles y el tipo de dado.")
'''
    help_replacement = '''        }
        val customHitDieSides = draft.hitDieSides.toIntOrNull()
        if (customHitDieSides == null || customHitDieSides !in standardClassHitDieSidesV4) {
            CompactLabeledNumberInputSuccessorV4(
                label = "Caras del dado",
                value = draft.hitDieSides,
                onValueChange = { value -> draft = draft.copy(hitDieSides = value.filter(Char::isDigit)) },
                prefix = "d",
            )
        }
        CharacterHelpV4("Los DG máximos se derivan del nivel de esta clase; solo se guarda cuántos quedan disponibles y el tipo de dado.")
'''
    text = replace_once(text, help_anchor, help_replacement, "custom die field")

    number_field_anchor = '''@Composable
private fun CompactLabeledNumberInputSuccessorV4(
'''
    selector = '''private val standardClassHitDieSidesV4 = listOf(4, 6, 8, 10, 12, 20)

@Composable
private fun CompactClassHitDieSelectorV4(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val sides = value.toIntOrNull()
    val display = sides?.takeIf { it in standardClassHitDieSidesV4 }?.let { "d$it" } ?: "Otro…"

    Column(modifier = modifier) {
        Text("Dado", style = MaterialTheme.typography.labelSmall, maxLines = 1)
        Box {
            CompactClassMenuV4(display) { expanded = true }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                standardClassHitDieSidesV4.forEach { standardSides ->
                    DropdownMenuItem(
                        text = { Text("d$standardSides") },
                        onClick = {
                            onValueChange(standardSides.toString())
                            expanded = false
                        },
                    )
                }
                DropdownMenuItem(
                    text = { Text("Otro…") },
                    onClick = {
                        if (sides != null && sides in standardClassHitDieSidesV4) onValueChange("")
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun CompactLabeledNumberInputSuccessorV4(
'''
    text = replace_once(text, number_field_anchor, selector, "hit die selector helper")

    field_call = '''            singleLine = true,
            prefix = if (prefix.isBlank()) null else ({ Text(prefix) }),
'''
    field_replacement = '''            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            prefix = if (prefix.isBlank()) null else ({ Text(prefix) }),
'''
    text = replace_once(text, field_call, field_replacement, "numeric keyboard options")

    PATH.write_text(text, encoding="utf-8")
    print(
        "T6 class-editor controls applied: Nivel/DG/custom sides numeric keyboard; "
        "standard hit-die selector d4/d6/d8/d10/d12/d20 + Otro… with nonstandard preservation."
    )


if __name__ == "__main__":
    main()
