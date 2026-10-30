#!/usr/bin/env python3
"""Independent physical P4 spell-meter audit on actual Desktop App PDFs.

Checks a deliberately narrow part of the frozen V11 contract: one printed
level 0..9 and, for levels 1..9, an ESP. physical meter with three genuine
7pt vector squares on the same baseline. An ESP.N G.M label means true
persisted capacity/spent; blank ESP. remains blank paper, never synthetic. It cannot establish spell-source completeness,
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
LEVEL = re.compile(r"\b(?:NIVEL|TRUCOS)\s+(\d+)\b")  # C330: golden «TRUCOS 0»


# Golden P4 continuation panels are REAL vector clipped blue cartelas
# in an existing 10pt free paper strip, never duplicate source headings.
CONTINUATION_FILL = tuple(c / 255 for c in (231, 242, 247))  # C293 golden
CONTINUATION_INK = tuple(c / 255 for c in (80, 122, 151))


def continued_level_bands(page):
    """Require physical blue clipped context cards at real continued columns."""
    words = page.get_text("words")
    continued = [
        word for word in words if word[4] == "CONTINUACIÓN"
        and 148 < word[1] < 160 and word[0] >= 39.69  # C322: band above the golden column top
    ]
    bands = [
        draw["rect"] for draw in page.get_drawings()
        if draw["type"] == "fs" and len(draw["items"]) == 8
        and draw.get("fill") is not None and draw.get("color") is not None
        and all(abs(draw["fill"][i] - CONTINUATION_FILL[i]) < .013 for i in range(3))
        and all(abs(draw["color"][i] - CONTINUATION_INK[i]) < .013 for i in range(3))
        # C322: band between the golden column top (y630.43) and the cards.
        and abs(draw["rect"].y0 - 152.27) < .5
        and abs(draw["rect"].y1 - 161.27) < .5
    ]
    failures = []
    for word in continued:
        # C309: golden P4 columns (39.69 / 222.09 / 404.5, 167.81 pt wide).
        left = next((col for col in (39.69, 222.09, 404.5)
                     if col + 2 <= word[0] < col + 167.81), None)
        if left is None or not any(
            abs(band.x0 - left) < .5 and abs(band.width - 167.81) < .5
            for band in bands
        ):
            failures.append(
                f"P4 source level CONTINUACIÓN at x={word[0]:.1f} "
                "lacks approved clipped blue context strip"
            )
    return len(continued), failures


def final_level9_manuscript_paper(page):
    """Source-neutral golden P4: use remaining writable rows in last column.

    Apply only when the genuine level-9 header is in physical column 3
    of a SINGLE P4 page. Dense multi-page and epic P4 are not altered.
    The top-origin writing region ends at PDF y=716pt.
    """
    words = page.get_text("words")
    level9_in_right = any(
        word[4] == "NIVEL" and 400 <= word[0] <= 575
        and 200 < word[1] < 700
        and idx+1 < len(words) and words[idx+1][4] == "9"
        for idx, word in enumerate(words)
    )
    if not level9_in_right:
        return False, 0.0, []
    ends = [
        drawing["rect"].y1
        for drawing in page.get_drawings()
        if drawing["type"] == "f" and drawing.get("fill") is not None
        and abs(drawing["rect"].x0 - 404.5) < .45
        and abs(drawing["rect"].width - 167.81) < .45
        and 200 < drawing["rect"].y0 < 718
        and all(abs(drawing["fill"][i] - color) < .013
                for i, color in enumerate((231/255, 242/255, 247/255)))  # C330 golden row fill
    ]
    if not ends:
        return True, 0.0, ["P4 final level 9 lacks real pale writing rows"]
    bottom = max(ends)
    errors = ([] if 703 <= bottom <= 717 else [
        f"P4 last-level handwritten lines end at y={bottom:.2f}, "
        "leaving avoidable unused printable paper"
    ])
    return True, bottom, errors


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
                and 41 <= r.x0 and r.x1 <= 571 and 158 <= r.y0 <= 730):  # C322: golden columns from y161.6
            squares.append(r)
    continued, context_errors = continued_level_bands(page)
    errors.extend(context_errors)
    meters = []
    for word in page.get_text("words"):
        if not str(word[4]).strip().startswith("ESP."):
            continue
        x, y = float(word[0]), (float(word[1]) + float(word[3])) / 2
        # C220: genuine long totals may start right after the actual
        # "NIVEL X" heading, not at the former fixed x=right-83.
        # Locate the three boxes by the immutable PHYSICAL column
        # right edge, rather than the variable position of ESP.N G.M.
        column_right = next((edge for left, edge in
                             ((39.69, 207.5), (222.09, 389.9), (404.5, 572.31))
                             if left <= x < edge - 2), None)
        expected_first_box_x = (column_right - 33
                                if column_right is not None else None)
        boxes = [r for r in squares
                 if expected_first_box_x is not None
                 and any(abs(r.x0 - (expected_first_box_x + n * 11)) <= .6
                         for n in range(3))
                 and abs((r.y0 + r.y1) / 2 - y) <= 9]
        meters.append({
            "x": round(x, 2), "y": round(y, 2), "boxes": len(boxes),
            "actual_total": (
                int(word[4][4:]) if re.fullmatch(r"ESP\.[0-9]+", word[4])
                else None
            ),
        })
        if len(boxes) != 3:
            errors.append(f"ESP. meter at x={x:.1f}, y={y:.1f} has {len(boxes)} squares, expected 3")
    return {"page": page_number, "levels": levels, "meters": meters,
            "vector_squares": len(squares),
            "level_continuations_with_blue_bands": continued,
            "errors": errors}


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
    # The last-level paper cap is a single-page physical golden property.
    # Do not demand an empty filler page for genuinely overflowing casters.
    final_paper = {"checked": False, "bottom_top_origin": None}
    if len(pages) == 1 and not unexpected:
        checked, bottom, paper_errors = final_level9_manuscript_paper(pages[0][1])
        final_paper = {"checked": checked,
                       "bottom_top_origin": round(bottom, 2) if checked else None}
        errors.extend(paper_errors)
    meters = [meter for r in findings for meter in r["meters"]]
    if len(meters) != 9:
        errors.append(f"ESP. meters {len(meters)}, expected nine for levels 1..9")
    return {"has_p4": True, "pages": findings, "levels_found": levels,
            "extra_epic_levels_not_source_audited": unexpected,
            "meter_count": len(meters),
            "source_backed_meter_count": sum(
                1 for meter in meters if meter["actual_total"] is not None
            ),
            "last_level9_paper": final_paper,
            "level_continuations_with_blue_bands": sum(
                r["level_continuations_with_blue_bands"] for r in findings),
            "errors": errors}


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
              "last_level9_paper_checks": sum(
                  1 for row in results
                  if row.get("last_level9_paper", {}).get("checked")
              ),
              "blue_level_continuation_bands": sum(
                  row.get("level_continuations_with_blue_bands", 0)
                  for row in results),
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
