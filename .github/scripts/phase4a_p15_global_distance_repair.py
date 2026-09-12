from pathlib import Path
import re

ROOT = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android")
TARGETS = {
    ROOT / "CharacterCombatTabV4.kt": ("formatSpeedCombatV4",),
    ROOT / "CharacterCombatOperationalV4.kt": ("formatSpeedOperationalV4",),
}
WORKFLOW = Path(".github/workflows/phase4a-p16-audit.yml")
SCRIPT = Path(".github/scripts/phase4a_p15_global_distance_repair.py")

for path, (formatter,) in TARGETS.items():
    text = path.read_text()
    expected_calls = text.count(f"{formatter}(speed)")
    if expected_calls < 1:
        raise SystemExit(f"{path}: expected at least one {formatter}(speed) call")
    text = text.replace("import kotlin.math.abs\n", "")
    text = text.replace(f"{formatter}(speed)", "formatCharacterDistanceFeetV4(speed)")
    pattern = rf"\nprivate fun {formatter}\(raw: String\): String \{{.*?\n\}}\n"
    text, removed = re.subn(pattern, "\n", text, count=1, flags=re.S)
    if removed != 1:
        raise SystemExit(f"{path}: expected exactly one local formatter definition")
    if formatter in text or "metricTenths" in text or "kotlin.math.abs" in text:
        raise SystemExit(f"{path}: local distance authority still remains")
    path.write_text(text.rstrip() + "\n")

# The only feet->metric conversion authority in production Android code must now be the shared formatter.
conversion_hits = []
for path in ROOT.glob("*.kt"):
    text = path.read_text()
    if "metricTenths" in text or "0.3048" in text:
        conversion_hits.append(path.name)
if conversion_hits != ["CharacterDistanceFormatV4.kt"]:
    raise SystemExit(f"Unexpected distance conversion authorities remain: {conversion_hits}")

WORKFLOW.unlink()
SCRIPT.unlink()
