#!/usr/bin/env python3
"""Independent V11 physical page-role topology audit of real exported PDFs.

Checks the *rendered PDF*, not the Kotlin plan. Does not certify complete G4,
source record fidelity, golden artwork, Android parity or owner acceptance.
"""
import argparse
import hashlib
import json
from pathlib import Path

import fitz

ORDER = {"P1": 0, "P2": 1, "P3": 2, "C2": 3, "P4": 4,
         "CN3": 5, "BOOK": 6, "NOTES": 7}


def classify(title: str) -> str | None:
    if title.startswith("P1 - "):
        return "P1"
    if title.startswith("P2 - "):
        return "P2"
    if title.startswith("P3 - "):
        return "P3"
    if title.startswith("P4 - "):
        return "P4"
    if title.startswith("C2 - "):
        return "C2"
    if title.startswith("C-N3 - "):
        return "CN3"
    if title.startswith("Índice del Libro de Conjuros") or title.startswith("Libro de conjuros"):
        return "BOOK"
    if title == "NOTAS":
        return "NOTES"
    return None


def inspect_roles(titles: list[str]) -> list[str]:
    """Pure independent oracle; page titles are extracted from actual PDF ink."""
    errors = []
    roles = [classify(t) for t in titles]
    if len(roles) < 4:
        errors.append("fewer than four mandatory physical pages")
    if roles[:3] != ["P1", "P2", "P3"]:
        errors.append(f"first three sheets must be P1,P2,P3, got {roles[:3]}")
    if not roles or roles[-1] != "NOTES" or roles.count("NOTES") != 1:
        errors.append("exactly one manuscript NOTES sheet must be last")
    previous = -1
    for index, (title, role) in enumerate(zip(titles, roles), start=1):
        if role is None:
            errors.append(f"p{index}: unknown printed page role {title!r}")
            continue
        if ORDER[role] < previous:
            errors.append(f"p{index}: role {role} appears after a later section")
        previous = max(previous, ORDER[role])
        if role == "CN3" and not title.startswith("C-N3 - "):
            errors.append(f"p{index}: digital notes mixed with other module")
    return errors


def inspect_pdf(path: Path) -> dict:
    data = path.read_bytes()
    errors = []
    with fitz.open(stream=data, filetype="pdf") as doc:
        titles = []
        for index, page in enumerate(doc, start=1):
            lines = [line.strip() for line in page.get_text().splitlines() if line.strip()]
            if len(lines) < 2:
                errors.append(f"p{index}: no printed header/page-role text")
                titles.append("")
            else:
                titles.append(lines[1])
        errors.extend(inspect_roles(titles))
    return {"pdf": path.name, "sha256": hashlib.sha256(data).hexdigest(),
            "pages": len(titles), "page_titles": titles,
            "status": "PASS" if not errors else "FAIL", "errors": errors}


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--pdf-dir", type=Path, required=True)
    parser.add_argument("--minimum-pdfs", type=int, default=1)
    parser.add_argument("--report", type=Path, required=True)
    args = parser.parse_args()
    paths = sorted(args.pdf_dir.glob("V11-INTERNAL-PROOF-*.pdf"))
    results = [inspect_pdf(p) for p in paths]
    report = {"scope": "independent V11 printed module topology; NOT G4 or owner QA",
              "count": len(results),
              "pages": sum(x["pages"] for x in results),
              "errors": sum(len(x["errors"]) for x in results),
              "results": results}
    args.report.parent.mkdir(parents=True, exist_ok=True)
    args.report.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n",
                           encoding="utf-8")
    print(f'V11_MODULE_TOPOLOGY_PDFS={report["count"]}')
    print(f'V11_MODULE_TOPOLOGY_PAGES={report["pages"]}')
    print(f'V11_MODULE_TOPOLOGY_ERRORS={report["errors"]}')
    for item in results:
        if item["errors"]:
            print(f'{item["pdf"]}: {item["errors"][:4]}')
    return 0 if len(results) >= args.minimum_pdfs and report["errors"] == 0 else 1


if __name__ == "__main__":
    raise SystemExit(main())
