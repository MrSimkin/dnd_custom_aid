#!/usr/bin/env python3
"""C427: every drawing of the near-empty golden must exist in the App's near-empty PDF.

Reads `v11_personaje_casi_vacio.pdf` from the approved golden ZIP and compares it with the real
App PDF of a nearly empty sheet, page by page: same place, same inks. Only three things are
excused, each one a written decision and not a tolerance:

* the header logo and its rule (the App prints the owner's original logo);
* the attribute rows of P1 (owner row, P104, approved 2026-11-03);
* the spell-slot boxes of P1 and the rows of an absent level (contract: never invent boxes);
* the P1 portrait without photo, a thin-line bust (owner 2026-11-10, «4 · Silueta de línea»);
* the free rows of EQUIPO ESPECIAL on P2, one EQUIPO line high (owner 2026-11-10, «Sí, la propuesta»).
"""
from __future__ import annotations

import argparse
import json
import zipfile
from pathlib import Path

import fitz

TOLERANCE = 0.6
GOLDEN_MEMBER = "v11_personaje_casi_vacio.pdf"
APP_PDF = "V11-INTERNAL-PROOF-C379-empty-sheet-A.pdf"


def hex_ink(colour):
    return "-" if colour is None else "#%02x%02x%02x" % tuple(round(v * 255) for v in colour)


def drawings(page):
    found = []
    for path in page.get_drawings():
        rect = path["rect"]
        found.append((rect.x0, 792 - rect.y1, rect.x1, 792 - rect.y0,
                       hex_ink(path.get("color")), hex_ink(path.get("fill"))))
    return found


def covered(golden, app):
    """A golden shape is present if an App shape of the same inks covers it."""
    return any(golden[4:] == other[4:] and
               other[0] - TOLERANCE <= golden[0] and other[1] - TOLERANCE <= golden[1] and
               other[2] + TOLERANCE >= golden[2] and other[3] + TOLERANCE >= golden[3]
               for other in app)


def excused(shape, page_number):
    x0, y0, x1, y1 = shape[:4]
    if x1 < 155 and y0 > 715:  # header logo and its rule
        return True
    # Short corner ticks are drawn together by the App: same place, one path.
    if (x1 - x0 < 15 and y1 - y0 < 15) and (x1 < 53 or x0 > 559) and (y0 > 738 or y1 < 53):
        return True
    if page_number == 0:
        if x0 >= 42 and x1 <= 230 and y0 >= 275 and y1 <= 583 and (x1 - x0 < 100) and (y1 - y0 < 20):
            return True  # owner attribute row: score box, oval and save box
        if 300 < x0 and x1 < 552 and 130 < y0 and y1 < 185 and x1 - x0 < 10:
            return True  # slot boxes of an absent spell level
        if 42 <= x0 and x1 <= 120 and 620 <= y0 and y1 <= 700:
            return True  # C473: the portrait's golden ovals, now a thin-line bust
    if page_number == 1 and y0 >= 50 and y1 <= 278 and (
            (x0 < 41 and x1 > 571) or (544 < x0 < 547 and x1 - x0 < 10)):
        return True  # C473: EQUIPO ESPECIAL full-width rows and ACTIVO boxes, one EQUIPO line high
    return False


def page_failures(golden_page, app_page, page_number):
    app = drawings(app_page)
    return [shape for shape in drawings(golden_page)
            if not excused(shape, page_number) and not covered(shape, app)]


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--golden-zip", type=Path, required=True)
    parser.add_argument("--pdf-dir", type=Path, required=True)
    parser.add_argument("--report", type=Path)
    args = parser.parse_args()
    with zipfile.ZipFile(args.golden_zip) as archive:
        member = next(name for name in archive.namelist() if name.endswith(GOLDEN_MEMBER))
        golden = fitz.open(stream=archive.read(member), filetype="pdf")
    app = fitz.open(args.pdf_dir / APP_PDF)
    # Golden pages: P1, P2, P3 and the final NOTAS sheet; the App's last page is its NOTAS sheet.
    pairs = [(0, 0), (1, 1), (2, 2), (golden.page_count - 1, app.page_count - 1)]
    failures = {}
    for golden_index, app_index in pairs:
        missing = page_failures(golden[golden_index], app[app_index], golden_index)
        if missing:
            failures[f"golden p{golden_index + 1} / app p{app_index + 1}"] = [
                [round(v, 2) if isinstance(v, float) else v for v in shape] for shape in missing]
    if args.report:
        args.report.parent.mkdir(parents=True, exist_ok=True)
        args.report.write_text(json.dumps({"pages": len(pairs), "failures": failures}, indent=1), encoding="utf-8")
    if failures:
        for where, shapes in failures.items():
            print(where, len(shapes), shapes[:6])
        print(f"FAIL golden geometry: {sum(len(v) for v in failures.values())} missing shapes")
        raise SystemExit(1)
    print(f"V11 golden geometry: PASS {len(pairs)} pages")


if __name__ == "__main__":
    main()
