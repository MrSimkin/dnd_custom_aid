#!/usr/bin/env python3
"""Scoped physical BK07 oracle: source-native clipped V11 book-card panels.

Uses actual PDFBox Desktop App PDFs and the frozen golden geometry. This
checks visible frame/header structures, not book text completeness, Android
runtime, eight-golden equivalence or G4 acceptance.
"""
import argparse
import hashlib
import json
from pathlib import Path

import fitz

NAV = "Índice del Libro de Conjuros"
TITLE = "Libro de conjuros /"
BLUE = (38 / 255, 75 / 255, 104 / 255)
MIST = (231 / 255, 242 / 255, 247 / 255)


def close_color(actual, expected, eps=0.035):
    return actual is not None and all(abs(a - b) <= eps for a, b in zip(actual, expected))


def inspect_page(page):
    draws = page.get_drawings()
    frames = []
    header_fills = []
    for drawing in draws:
        box = drawing["rect"]
        if not (35 <= box.x0 and box.x1 <= 577 and 73 <= box.y0 and box.y1 <= 720):
            continue
        if (len(drawing["items"]) == 8 and drawing["type"] in ("s", "fs")
                and 240 <= box.width <= 280 and box.height >= 7
                and close_color(drawing["color"], BLUE)):
            frames.append(box)
        if (drawing["type"] == "f" and drawing["fill"] is not None
                and 240 <= box.width <= 280
                and 6 <= box.height <= 23
                and close_color(drawing["fill"], MIST)):
            header_fills.append(box)
    # Every real card fragment has its own protected envelope.
    # A continuation may have no fresh name/level band, so only require
    # at least one band across the WHOLE corpus (not per page).
    return {"frames": len(frames), "title_bands": len(header_fills),
            "bounds": [[round(x, 2) for x in rect] for rect in frames]}


def audit_pdf(payload):
    with fitz.open(stream=payload, filetype="pdf") as doc:
        book_pages = []
        for i, page in enumerate(doc):
            head = page.get_text().splitlines()[:3]
            if not any(TITLE in line for line in head) or any(NAV in line for line in head):
                continue
            measurements = inspect_page(page)
            measurements["page"] = i + 1
            book_pages.append(measurements)
    errors = []
    for page in book_pages:
        if not page["frames"]:
            errors.append(f"p{page['page']} book has no clipped-source cards")
    if book_pages and sum(p["title_bands"] for p in book_pages) == 0:
        errors.append("book has no blue-mist source header")
    return {"pages": book_pages, "errors": errors}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--pdf-dir", type=Path, required=True)
    parser.add_argument("--minimum-pdfs", type=int, default=48)
    parser.add_argument("--minimum-books", type=int, default=4)
    parser.add_argument("--report", type=Path, required=True)
    a = parser.parse_args()
    paths = sorted(a.pdf_dir.glob("V11-INTERNAL-PROOF-*.pdf"))
    records = []
    for path in paths:
        payload = path.read_bytes()
        result = audit_pdf(payload)
        records.append({"file": path.name, "sha256": hashlib.sha256(payload).hexdigest(), **result})
    books = sum(bool(r["pages"]) for r in records)
    pages = sum(len(r["pages"]) for r in records)
    errors = [r for r in records if r["errors"]]
    report = {"scope": "BK07 actual Desktop App spellbook card frames/headers only; NOT G4/owner PASS",
              "pdfs": len(paths), "books": books, "book_pages": pages,
              "failed_books": len(errors), "records": records}
    a.report.parent.mkdir(parents=True, exist_ok=True)
    a.report.write_text(json.dumps(report, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    print(f"V11_BK07_PHYSICAL_CARD_FRAMES={books-len(errors)}/{books}; pages={pages}; PDFs={len(paths)}")
    if errors:
        print(errors[0]["file"], errors[0]["errors"][:3])
    return 0 if len(paths) >= a.minimum_pdfs and books >= a.minimum_books and not errors else 1


if __name__ == "__main__":
    raise SystemExit(main())
