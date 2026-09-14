#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path

PATH = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterClassIdentitySuccessorV4.kt")
text = PATH.read_text(encoding="utf-8")
errors: list[str] = []


def require(marker: str, label: str) -> None:
    if marker not in text:
        errors.append(f"missing {label}: {marker}")

require("import androidx.compose.foundation.text.KeyboardOptions", "KeyboardOptions import")
require("import androidx.compose.ui.text.input.KeyboardType", "KeyboardType import")
require("keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)", "numeric keyboard")
require("private val standardClassHitDieSidesV4 = listOf(4, 6, 8, 10, 12, 20)", "standard hit die set")
require("private fun CompactClassHitDieSelectorV4(", "shared local hit die selector")
require('val display = sides?.takeIf { it in standardClassHitDieSidesV4 }?.let { "d$it" } ?: "Otro…"', "standard/custom selector display")
require('text = { Text("Otro…") }', "custom die menu option")
require('label = "Caras del dado"', "custom sides numeric field")
require("if (draft.hitDieSides.toIntOrNull() !in standardClassHitDieSidesV4)", "nonstandard preservation gate")
require("hitDieSides = hitDie?.toString() ?: draft.hitDieSides", "catalog hit die preselection preservation")

if 'label = "Dado",\n                value = draft.hitDieSides' in text:
    errors.append("legacy freeform Dado numeric field still present")

if errors:
    raise SystemExit("Player class-editor control guard FAIL:\n- " + "\n- ".join(errors))

print(
    "Player class-editor control guard PASS: numeric keyboard active; standard hit-die selector "
    "d4/d6/d8/d10/d12/d20 + Otro… present; nonstandard values preserved; catalog preselection retained."
)
