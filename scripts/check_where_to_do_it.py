"""Owner point 5: the table of what is done where must say the same in the doc and in the App.

`docs/technical/QUE_SE_HACE_DONDE.md` is what the owner reads; `dmUi/.../WhereToDoIt.kt` is what
both Apps show. CI compares the task of every row, so the two never drift apart.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

sys.stdout.reconfigure(encoding="utf-8")

REPO = Path(__file__).resolve().parent.parent
DOC = REPO / "docs" / "technical" / "QUE_SE_HACE_DONDE.md"
SCREEN = REPO / "dmUi" / "src" / "kotlin" / "io" / "github" / "mrsimkin" / "dndcustomaid" / "dmui" / "WhereToDoIt.kt"


def doc_tasks() -> list[str]:
    rows = []
    for line in DOC.read_text(encoding="utf-8").splitlines():
        if not line.startswith("| ") or line.startswith("| ---") or line.startswith("| Tarea"):
            continue
        rows.append(line.split("|")[1].strip())
    return rows


def screen_tasks() -> list[str]:
    text = SCREEN.read_text(encoding="utf-8")
    block = text[text.index("WHERE_TO_DO_IT"):text.index("/** The same table")]
    return [match.group(1) for match in re.finditer(r'WhereRow\("([^"]+)"', block)]


def main() -> int:
    doc = doc_tasks()
    screen = screen_tasks()
    if not doc or not screen:
        print("No se pudo leer la tabla en el documento o en la pantalla.")
        return 1

    # The App says it shorter than the doc, so a row matches when one starts like the other.
    def matches(a: str, b: str) -> bool:
        short, long = sorted((a.lower(), b.lower()), key=len)
        return long.startswith(short[: min(len(short), 24)])

    if len(doc) != len(screen):
        print(f"La tabla tiene {len(doc)} filas en el documento y {len(screen)} en la pantalla.")
        return 1

    problems = [(a, b) for a, b in zip(doc, screen) if not matches(a, b)]
    if problems:
        print("Estas filas no dicen lo mismo:")
        for a, b in problems:
            print(f"  documento: {a}\n  pantalla:  {b}\n")
        return 1

    print(f"La tabla de qué se hace dónde coincide en sus {len(doc)} filas.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
