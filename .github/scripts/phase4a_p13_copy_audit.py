from pathlib import Path
import re

ROOT = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android")

STRING_RE = re.compile(r'"(?:\\.|[^"\\])*"', re.S)


def unquote(raw: str) -> str:
    body = raw[1:-1]
    return (
        body.replace('\\n', ' ')
        .replace('\\"', '"')
        .replace('\\t', ' ')
        .replace('\\$', '$')
        .replace('\\\\', '\\')
    )


def context_kind(text: str, start: int, end: int) -> str:
    before = text[max(0, start - 500):start]
    around = text[max(0, start - 220):min(len(text), end + 220)]
    if "CharacterHelpV4(" in before[-350:] or "RichTooltip" in around:
        return "HELP"
    if "CharacterConfirmationDialog(" in before[-500:] or "confirmLabel" in around or "onConfirm" in around:
        return "CONFIRMATION"
    if "CharacterInlineValidationMessage(" in before[-300:] or "Validation" in around:
        return "VALIDATION"
    if re.search(r"\bsecondary\s*=", before[-180:]):
        return "SECONDARY"
    if "Text(" in before[-160:] or re.search(r"Text\s*\(\s*$", before[-80:]):
        return "TEXT"
    if re.search(r"\bmessage\s*=", before[-180:]):
        return "MESSAGE"
    if "contentDescription" in before[-160:]:
        return "A11Y"
    return "OTHER"


rows = []
help_calls = 0
for path in sorted(ROOT.glob("*.kt")):
    text = path.read_text()
    help_calls += text.count("CharacterHelpV4(")
    for match in STRING_RE.finditer(text):
        value = unquote(match.group())
        # Long prose is the primary P13 risk. Keep state/integrity/help strings in the inventory
        # too so the audit can explicitly distinguish necessary copy from permanent verbosity.
        if len(value) < 60:
            continue
        if value.startswith("^") or "\\p{" in value or value.startswith("http"):
            continue
        line = text.count("\n", 0, match.start()) + 1
        kind = context_kind(text, match.start(), match.end())
        rows.append((path.name, line, kind, len(value), " ".join(value.split())))

print(f"P13_COPY_AUDIT files={len(list(ROOT.glob('*.kt')))} long_strings={len(rows)} help_calls={help_calls}")
print("FILE:LINE | KIND | LEN | TEXT")
for file_name, line, kind, length, value in rows:
    print(f"{file_name}:{line} | {kind} | {length} | {value}")

print("\n=== obvious permanent-copy candidates (TEXT/SECONDARY, >= 75 chars) ===")
for file_name, line, kind, length, value in rows:
    if kind in {"TEXT", "SECONDARY"} and length >= 75:
        print(f"{file_name}:{line} | {kind} | {length} | {value}")

print("\n=== contextual/help/warning strings retained for explicit classification ===")
for file_name, line, kind, length, value in rows:
    if kind in {"HELP", "CONFIRMATION", "VALIDATION", "MESSAGE"}:
        print(f"{file_name}:{line} | {kind} | {length} | {value}")
