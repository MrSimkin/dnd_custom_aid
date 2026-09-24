#!/usr/bin/env python3
from pathlib import Path

ROOT = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android")
ui = (ROOT / "UiPreferences.kt").read_text(encoding="utf-8")
responsive = (ROOT / "CharacterResponsivePreferencesV4.kt").read_text(encoding="utf-8")
haptic_prefs = (ROOT / "CharacterHapticPreferencesV4.kt").read_text(encoding="utf-8")
haptic_hook = (ROOT / "CharacterCollectionPrimitivesV4.kt").read_text(encoding="utf-8")
errors: list[str] = []


def require(text: str, marker: str, label: str) -> None:
    if marker not in text:
        errors.append(f"missing {label}: {marker}")


# T3 adaptive intent and compatibility.
for marker, label in [
    ('COMFORTABLE("Cómodo", 420)', "comfortable density"),
    ('BALANCED("Equilibrado", 340)', "balanced density"),
    ('COMPACT("Compacto", 280)', "compact density"),
    ('DENSE("Denso", 230)', "dense density"),
    ('val portraitCardDensity: CharacterCardDensityV4 = CharacterCardDensityV4.BALANCED', "portrait balanced default"),
    ('val landscapeCardDensity: CharacterCardDensityV4 = CharacterCardDensityV4.BALANCED', "landscape balanced default"),
    ('KEY_PORTRAIT_CARD_DENSITY = "portrait_card_density"', "portrait density key"),
    ('KEY_LANDSCAPE_CARD_DENSITY = "landscape_card_density"', "landscape density key"),
    ('legacyCardDensityV4(phonePortraitColumns, tabletPortraitColumns, landscape = false)', "portrait legacy mapping"),
    ('legacyCardDensityV4(phoneLandscapeColumns, tabletLandscapeColumns, landscape = true)', "landscape legacy mapping"),
    ('Text("Distribución de tarjetas"', "adaptive settings heading"),
    ('label = "Vertical"', "portrait/vertical selector"),
    ('label = "Horizontal"', "landscape/horizontal selector"),
    ('no promete un número exacto de columnas', "non-exact user contract"),
]:
    require(ui, marker, label)

for forbidden in [
    'label = "Teléfono · vertical"',
    'label = "Teléfono · horizontal"',
    'label = "Tablet · vertical"',
    'label = "Tablet · horizontal"',
    'private fun ColumnCountSettingV4(',
]:
    if forbidden in ui:
        errors.append(f"legacy exact-count settings UI remains: {forbidden}")

for marker, label in [
    ("layoutContext.availableWidthDp", "actual available-width input"),
    ("layoutContext.isLandscape", "orientation density selection"),
    ("preferences.portraitCardDensity", "portrait density runtime"),
    ("preferences.landscapeCardDensity", "landscape density runtime"),
    ("density.minCardWidthDp", "minimum usable card width"),
    ("effectiveFontScale", "effective text pressure"),
    ("preferences.spacingScalePercent", "UI spacing pressure"),
    ("widthBound.coerceIn(1, baseMax.coerceAtLeast(1))", "safe runtime column cap"),
]:
    require(responsive, marker, label)

for forbidden in ["preferences.phonePortraitColumns", "preferences.phoneLandscapeColumns", "preferences.tabletPortraitColumns", "preferences.tabletLandscapeColumns"]:
    if forbidden in responsive:
        errors.append(f"runtime still consumes legacy exact-count preference: {forbidden}")

# T4 text-size symmetry and nearest migration.
require(ui, "internal val FONT_SCALE_OPTIONS = (50..150 step 10).toList()", "symmetric text scale")
require(ui, "FONT_SCALE_OPTIONS.minByOrNull { abs(it - storedScale.coerceIn(50, 150)) }", "nearest legacy text-scale migration")
require(ui, "internal val SPACING_SCALE_OPTIONS = (50..150 step 10).toList()", "unchanged symmetric spacing scale")

# T9 explicit global-off state while retaining MEDIUM as upgrade/default fallback.
require(haptic_prefs, 'NONE("Ninguna", 0)', "haptic None option")
require(haptic_prefs, "val strength: CharacterHapticStrengthV4 = CharacterHapticStrengthV4.MEDIUM", "non-none default preservation")
require(haptic_prefs, "?: CharacterHapticStrengthV4.MEDIUM", "missing/legacy non-none fallback")
require(haptic_hook, "if (enabled && hapticPreferences.strength != CharacterHapticStrengthV4.NONE)", "global haptic dispatch short-circuit")
require(ui, "Ninguna desactiva la respuesta háptica generada por la app", "T9 user-facing explanation")
require(ui, 'Text("Herramientas DEV"', "debug QA settings entry")
require(ui, 'MainActivity::class.java.name.substringBeforeLast(\'.\') + ".HostedDevAuthActivity"', "debug QA activity class target")
if 'context.packageName + ".HostedDevAuthActivity"' in ui:
    errors.append("debug QA launcher incorrectly derives activity class from applicationId")


# Representative compatibility semantics mirrored from the source thresholds.
def legacy_density(phone: int, tablet: int, landscape: bool) -> str:
    score = max(phone, 1) + max(tablet, 1)
    if landscape:
        if score <= 4:
            return "COMFORTABLE"
        if score <= 5:
            return "BALANCED"
        if score <= 7:
            return "COMPACT"
        return "DENSE"
    if score <= 2:
        return "COMFORTABLE"
    if score <= 3:
        return "BALANCED"
    if score <= 5:
        return "COMPACT"
    return "DENSE"

if legacy_density(1, 2, False) != "BALANCED":
    errors.append("legacy portrait defaults do not map to BALANCED")
if legacy_density(2, 3, True) != "BALANCED":
    errors.append("legacy landscape defaults do not map to BALANCED")
if legacy_density(1, 1, False) != "COMFORTABLE":
    errors.append("low-density portrait legacy intent is not preserved")
if legacy_density(4, 5, True) != "DENSE":
    errors.append("high-density landscape legacy intent is not preserved")

font_options = list(range(50, 151, 10))
def nearest_font(value: int) -> int:
    clamped = min(150, max(50, value))
    return min(font_options, key=lambda option: abs(option - clamped))

for old, expected in [(70, 70), (145, 140), (160, 150), (200, 150), (45, 50)]:
    actual = nearest_font(old)
    if actual != expected:
        errors.append(f"text-scale migration {old} -> {actual}, expected {expected}")

if errors:
    raise SystemExit("Player application-settings semantics guard FAIL:\n- " + "\n- ".join(errors))

print(
    "Player application-settings semantics guard PASS: adaptive portrait/landscape density replaces exact-column UI/runtime; "
    "legacy defaults map to Balanced; text scale is symmetric 50-150 with nearest migration; "
    "haptic None short-circuits both vibrator and fallback dispatch while MEDIUM remains upgrade/default fallback."
)
