#!/usr/bin/env python3
"""Guard the canonical fresh-context resume route.

The live route is intentionally small:
README/AGENTS/MANIFEST/WORKFLOW -> RESUME.md -> LATEST.md -> one active checkpoint.
This prevents stale duplicated wave/branch prose from becoming accidental continuation authority.
"""

from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
errors: list[str] = []


def text(path: str) -> str:
    target = ROOT / path
    if not target.is_file():
        errors.append(f"missing required resume-route file: {path}")
        return ""
    return target.read_text(encoding="utf-8")


resume = text("RESUME.md")
latest = text("docs/checkpoints/LATEST.md")

for path in ("README.md", "AGENTS.md", "MANIFEST.md", "docs/WORKFLOW.md"):
    content = text(path)
    if "RESUME.md" not in content:
        errors.append(f"{path} does not point fresh-context work to RESUME.md")

if "docs/checkpoints/LATEST.md" not in resume:
    errors.append("RESUME.md does not point to docs/checkpoints/LATEST.md")

pattern = re.compile(
    r"^\*\*Canonical active checkpoint:\*\*\s+`([^`]+)`\s*$",
    re.MULTILINE,
)
matches = pattern.findall(latest)

if len(matches) != 1:
    errors.append(
        "docs/checkpoints/LATEST.md must contain exactly one "
        "'**Canonical active checkpoint:** `...`' line"
    )
else:
    checkpoint = matches[0]
    if not checkpoint.startswith("docs/checkpoints/"):
        errors.append(f"canonical checkpoint must live under docs/checkpoints/: {checkpoint}")
    if checkpoint == "docs/checkpoints/LATEST.md":
        errors.append("LATEST.md cannot point to itself as the canonical checkpoint")
    target = ROOT / checkpoint
    if not target.is_file():
        errors.append(f"canonical active checkpoint does not exist: {checkpoint}")
    if checkpoint not in latest:
        errors.append("canonical checkpoint path is not visible in LATEST.md")
    read_first = latest.split("## Read first on resume", 1)
    if len(read_first) != 2 or checkpoint not in read_first[1].split("## ", 1)[0]:
        errors.append("canonical active checkpoint is missing from LATEST.md Read first on resume")

if "**Normal integrated trunk:** `main`" not in latest:
    errors.append("LATEST.md must identify main as the normal integrated trunk")

if errors:
    print("Canonical resume-route guard FAIL:")
    for error in errors:
        print(f"- {error}")
    sys.exit(1)

print("Canonical resume-route guard PASS")
if matches:
    print(f"Active checkpoint: {matches[0]}")
