#!/usr/bin/env python3
"""Independent physical P4 spell-meter audit on actual Desktop App PDFs.

Checks a deliberately narrow part of the frozen V11 contract: one printed
level 0..9 and, for levels 1..9, one ESP. label with three genuine 7pt vector
squares on the same baseline. It cannot establish spell-source completeness,
visual golden equivalence, Android parity or G4 acceptance.
"""
import argparse
import hashlib
import json
import re
from pathlib import Path

import fitz

PDF_PATTERN = "V11-INTERNAL-PROOF-*.pdf"
P4_HEADING = re.compile(r"\bP4\s*-\s*Conjuros\b", re.IGNORECASE)
LEVEL = re.compile(r"\bNIVEL\s+(\d+)\b")


def p4_pages(doc):
    return [(index + 1, page) for index, page in enumerate(doc)
            if P4_HEADING.search(page.get_text())]


def audit_spell_page(page, page_number):
    errors = []
    if abs(page.rect.width - 612) > 0.05 or abs(page.rect.height - 792) > 0.05:
        errors.append("P4 is not US Letter 612x792")
    lines = page.get_text().splitlines()
    levels = []
    for line in lines:
        match = LEVEL.search(line)
        if match and "CONTINUACIÓN" not in line.upper():
            levels.append(int(match.group(1)))
    # PDF drawing inspection, separate from the Kotlin text/slot model.
    squares = []
    for drawing in page.get_drawings():
        r = drawing["rect"]
        if (abs(r.width - 7) <= 0.6 and abs(r.height - 7) <= 0.6
                and 41 <= r.x0 and r.x1 <= 571 and 180 <= r.y0 <= 717):
            squares.append(r)
    meters = []
    for word in page.get_text("words"):
        if str(word[4]).strip() != "ESP.":
            continue
        x, y = float(word[0]), (float(word[1]) + float(word[3])) / 2
        boxes = [r for r in squares
                 if 36 <= r.x0 - x <= 76 and abs((r.y0 + r.y1) / 2 - y) <= 9]
        meters.append({"x": round(x, 2), "y": round(y, 2), "boxes": len(boxes)})
        if len(boxes) != 3:
            errors.append(f"ESP. meter at x={x:.1f}, y={y:.1f} has {len(boxes)} squares, expected 3")
    return {"page": page_number, "levels": levels, "meters": meters,
            "vector_squares": len(squares), "errors": errors}


def audit_document(doc):
    pages = p4_pages(doc)
    if not pages:
        return {"has_p4": False, "pages": [], "errors": []}
    findings = [audit_spell_page(page, index) for index, page in pages]
    errors = [f"p{r['page']}: {e}" for r in findings for e in r["errors"]]
    levels = [n for r in findings for n in r["levels"]]
    for n in range(10):
        if levels.count(n) != 1:
            errors.append(f"level {n} printed {levels.count(n)} times, expected once")
    unexpected = sorted(set(levels) - set(range(10)))
    # Higher levels can be real epic spells. No source claim from PDF alone.
    meters = [meter for r in findings for meter in r["meters"]]
    if len(meters) != 9:
        errors.append(f"ESP. meters {len(meters)}, expected nine for levels 1..9")
    return {"has_p4": True, "pages": findings, "levels_found": levels,
            "extra_epic_levels_not_source_audited": unexpected,
            "meter_count": len(meters), "errors": errors}


def main(argv=None):
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--pdf-dir", type=Path, required=True)
    p.add_argument("--minimum-pdfs", type=int, default=48)
    p.add_argument("--minimum-casters", type=int, default=2)
    p.add_argument("--report", type=Path, required=True)
    args = p.parse_args(argv)
    paths = sorted(args.pdf_dir.glob(PDF_PATTERN))
    results = []
    for path in paths:
        data = path.read_bytes()
        with fitz.open(stream=data, filetype="pdf") as pdf:
            check = audit_document(pdf)
        results.append({"file": path.name, "sha256": hashlib.sha256(data).hexdigest(), **check})
    casters = sum(row["has_p4"] for row in results)
    failures = [row["file"] for row in results if row["errors"]]
    report = {"scope": "P4 physical 0..9 levels + 9 ESP. meters / 27 vector squares; local Desktop PDFs only; NOT G4 PASS",
              "pdfs": len(paths), "casters": casters, "failed_pdfs": len(failures),
              "results": results}
    args.report.parent.mkdir(parents=True, exist_ok=True)
    args.report.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"V11_P4_PHYSICAL_SLOT_METERS={casters - len(failures)}/{casters}; PDFs={len(paths)}")
    if failures:
        bad = next(x for x in results if x["errors"])
        print(bad["file"], bad["errors"][:5])
    return 0 if len(paths) >= args.minimum_pdfs and casters >= args.minimum_casters and not failures else 1


if __name__ == "__main__":
    raise SystemExit(main())
