#!/usr/bin/env python3
from pathlib import Path

root = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android")
needle = "rememberCharacterHapticHookV4"
matches = []
for path in root.glob("*.kt"):
    text = path.read_text(encoding="utf-8")
    if needle in text:
        matches.append((path, text))

print(f"HAPTIC_HOOK_OCCURRENCE_FILES={len(matches)}")
for path, text in matches:
    print(f"===== {path} =====")
    lines = text.splitlines()
    for i, line in enumerate(lines):
        if needle in line:
            start = max(0, i - 18)
            end = min(len(lines), i + 70)
            print(f"--- occurrence line {i + 1} ---")
            for n in range(start, end):
                print(f"{n + 1:04d}: {lines[n]}")

if not matches:
    raise SystemExit("No haptic hook occurrences found")
