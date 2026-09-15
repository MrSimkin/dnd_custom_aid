#!/usr/bin/env python3
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
dice = (ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterDiceRollSuccessorV4.kt").read_text(encoding="utf-8")
editor = (ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt").read_text(encoding="utf-8")
prefs = (ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/UiPreferences.kt").read_text(encoding="utf-8")
ops = (ROOT / "shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterCombatDiceOperations.kt").read_text(encoding="utf-8")

required_dice = [
    "STANDARD_DIE_SIDES_RESULT_V4 = listOf(4, 6, 8, 10, 12, 20)",
    "CUSTOM_DIE_OTHER_V4",
    "CharacterCustomDieSelectorV4",
    "Caras del dado · 2–1000",
    "CharacterSignedModifierEditorV4",
    "resolveCharacterDiceExpressionRoll(expression)",
    "CharacterDieResultVisualV4",
    "characterDieSilhouettePathV4",
    "CharacterDamageDiceVisualsV4",
    "CharacterDiceResultModeSelectorV4",
    "onPreferencesChange(preferences.copy(diceResultMode = selectedMode))",
]
for token in required_dice:
    if token not in dice:
        raise SystemExit(f"T2 dice guard: missing {token!r}")

if 'label = "Resultados de dados"' in prefs:
    raise SystemExit("T2 dice guard: dice-result presentation ownership leaked back into Application Settings")
if 'KEY_DICE_RESULT_MODE = "dice_result_mode"' not in prefs:
    raise SystemExit("T2 dice guard: existing dice_result_mode persistence key changed or disappeared")
if "preferences = preferences" not in editor or "onPreferencesChange = onPreferencesChange" not in editor:
    raise SystemExit("T2 dice guard: CharacterEditor no longer passes preference ownership to Dice tab")
if "fun resolveCharacterDiceExpressionRoll(" not in ops:
    raise SystemExit("T2 dice guard: shared arbitrary-die resolver missing")
if "val roll = resolveCharacterDiceExpressionRoll(expression, dieRoller)" not in ops:
    raise SystemExit("T2 dice guard: damage rolling no longer reuses shared arbitrary-die resolver")

print("Player T2 dice guard: OK")
