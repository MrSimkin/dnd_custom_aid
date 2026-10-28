#!/usr/bin/env python3
"""Independent physical-envelope audit of real Desktop V11 PDF proofs.

A smoke guard, NOT the full approved-golden/G4/Android/owner acceptance gate.
"""
import argparse
import hashlib
import json
import re
from pathlib import Path

import fitz  # PyMuPDF: independent of the application's PDFBox renderer

PAGE = (612.0, 792.0)
TEXT_BOUNDS = (34.0, 30.0, 578.0, 767.0)
# Frozen golden-inspired alternating physical P2 paper ink, not a
# mock/screenshot test; positions are top-origin actual PDF coordinates.
P2_SHADE = tuple(v / 255 for v in (231, 242, 247))
P2_MODULES = (
    # REAL golden source-free P2 rows expand to manuscript rhythm;
    # source-populated tables use the measured Fira baseline height.
    ("Equipo", 42.0, 108.0, 258.0, (12.42, 16.5)),
    ("Tesoro", 42.0, 344.0, 258.0, (12.42, 19.5)),
    ("Rasgos", 312.0, 108.0, 258.0, (12.42,)),
    # D-0076 §3.21: a multi-line special entry spans whole 25 pt pitches.
    ("Equipo especial", 42.0, 497.0, 528.0, tuple(25.0 * n for n in range(1, 9))),
)


def p2_alternating_writing_rows(page: fitz.Page) -> list[str]:
    """Independent guard for four P2 bands, without touching C2 or pagination."""
    tinted = [
        d["rect"] for d in page.get_drawings()
        if d["type"] == "f" and d.get("fill") is not None
        and all(abs(d["fill"][i] - P2_SHADE[i]) < .012 for i in range(3))
    ]
    errors = []
    for name, x, y, width, possible_heights in P2_MODULES:
        if not any(abs(r.x0-x) < .4 and abs(r.y0-y) < .4
                   and abs(r.width-width) < .4
                   and any(abs(r.height-height) < .5 for height in possible_heights)
                   for r in tinted):
            errors.append(f"P2 missing golden row shade in {name} at ({x},{y})")
    return errors
SPECIAL_TABLE = (42.0, 497.0, 570.0, 697.0)  # P2 special rows, top-origin


def p2_special_rows(page: fitz.Page) -> list[str]:
    """D-0076 §3.21: taller special rows keep their ink between rules and one ACTIVO box each."""
    x0, top, x1, bottom = SPECIAL_TABLE
    rules = sorted({round(item[1].y, 1) for d in page.get_drawings() for item in d["items"]
                    if item[0] == "l" and abs(item[1].y - item[2].y) < .05
                    and item[1].x <= x0 + .5 and item[2].x >= x1 - .5
                    and top - .5 <= item[1].y <= bottom + .5})
    boxes = [d["rect"] for d in page.get_drawings()
             if abs(d["rect"].width - 9) < .2 and abs(d["rect"].height - 9) < .2
             and top <= d["rect"].y0 <= bottom]
    errors = []
    if len(rules) < 2:
        return ["P2 special table rules not found"]
    bands = [(a, b) for a, b in zip(rules, rules[1:]) if b - a > 20]
    for a, b in bands:
        if sum(1 for r in boxes if a < r.y0 and r.y1 < b) != 1:
            errors.append(f"P2 special band {a}-{b} lacks exactly one ACTIVO box")
    for w in page.get_text("words"):
        if x0 < w[0] < x1 and top < w[1] < bottom:
            if any(w[1] + .5 < y < w[3] - .5 for y in rules):
                errors.append(f"P2 special word crosses a row rule: {w[4]!r}")
    return errors


def cn3_cards(page: fitz.Page) -> list[str]:
    """C238: golden C-N3 — NOTAS cartela and every digital-note word inside a 528 pt card."""
    errors = []
    if "SOLO NOTAS DIGITALES" not in page.get_text():
        errors.append("C-N3 page lacks the golden NOTAS cartela")
    cards = [d["rect"] for d in page.get_drawings()
             if d["type"] == "s" and abs(d["rect"].x0 - 42) < .5 and abs(d["rect"].width - 528) < .5
             and d["rect"].height > 10]
    if not cards:
        return errors + ["C-N3 page has no note cards"]
    # Digital note text is Fira; Barlow is cartela/header paper text.
    spans = [s for b in page.get_text("dict")["blocks"] for line in b.get("lines", [])
             for s in line["spans"] if "Fira" in s["font"] and s["text"].strip()]
    for s in spans:
        x0, y0, x1, y1 = s["bbox"]
        if 92 < y0 < 688 and not any(c.x0 + 4 < x0 and x1 < c.x1 - 4 and c.y0 - .5 <= y0 and y1 <= c.y1 + .5
                                     for c in cards):
            errors.append(f"C-N3 note text outside its card: {s['text'][:30]!r}")
            break
    return errors


FOOTER = re.compile(r"\bHOJA\s+(\d+)\s*/\s*(\d+)\b")


def inspect_one(path: Path) -> dict:
    errors = []
    content = path.read_bytes()
    doc = fitz.open(stream=content, filetype="pdf")
    total = len(doc)
    roles = []
    word_count = 0
    for index, page in enumerate(doc, start=1):
        if abs(page.rect.width - PAGE[0]) > 0.01 or abs(page.rect.height - PAGE[1]) > 0.01:
            errors.append(f"p{index}: non-Letter page {page.rect.width}x{page.rect.height}")
        lines = [line.strip() for line in page.get_text().splitlines() if line.strip()]
        role = lines[1] if len(lines) >= 2 else ""
        roles.append(role)
        if role.startswith("C-N3"):
            errors.extend(f"p{index}: {e}" for e in cn3_cards(page))
        if index == 2:
            errors.extend(p2_alternating_writing_rows(page))
            errors.extend(p2_special_rows(page))
        fonts = " ".join(item[3] for item in page.get_fonts(full=True))
        if "BarlowCondensed" not in fonts or (index < total and "FiraSans" not in fonts):
            errors.append(f"p{index}: expected embedded text font missing")
        foot = FOOTER.findall(page.get_text())
        if index < total and foot != [(str(index), str(total))]:
            errors.append(f"p{index}: missing or inconsistent physical footer {foot}")
        if index == total and foot:
            errors.append(f"p{index}: last manuscript-only NOTES has an unexpected footer")
        words = sorted(page.get_text("words"), key=lambda w: w[1])
        word_count += len(words)
        for w in words:
            x0, y0, x1, y1 = w[:4]
            if x0 < TEXT_BOUNDS[0] - 0.01 or y0 < TEXT_BOUNDS[1] - 0.01 or x1 > TEXT_BOUNDS[2] + 0.01 or y1 > TEXT_BOUNDS[3] + 0.01:
                errors.append(f"p{index}: outside printable text area: {w[4]!r}")
            if len(errors) > 30:
                break
        # Ignore mere touching; report substantive printed-word intersections.
        for i, first in enumerate(words):
            fx0, fy0, fx1, fy1 = first[:4]
            j = i + 1
            while j < len(words) and words[j][1] < fy1 - 2.0:
                second = words[j]
                sx0, sy0, sx1, sy1 = second[:4]
                dx = max(0.0, min(fx1, sx1) - max(fx0, sx0))
                dy = max(0.0, min(fy1, sy1) - max(fy0, sy0))
                smaller = min((fx1 - fx0) * (fy1 - fy0), (sx1 - sx0) * (sy1 - sy0))
                if dx > 2.0 and dy > 3.0 and smaller > 0 and dx * dy / smaller > 0.35:
                    errors.append(f"p{index}: overlapping words {first[4]!r}/{second[4]!r}")
                j += 1
            if len(errors) > 30:
                break
    for index, prefix in enumerate(("P1 ", "P2 ", "P3 ")):
        if len(roles) <= index or not roles[index].startswith(prefix):
            errors.append(f"p{index + 1}: absent or misordered {prefix.strip()} sheet")
    if not roles or roles[-1] != "NOTAS":
        errors.append("last physical sheet is not manuscript-only NOTAS")
    if total and [line.strip() for line in doc[-1].get_text().splitlines() if line.strip()][2:]:
        errors.append("last physical NOTAS sheet contains extra digital text")
    return {"file": path.name, "sha256": hashlib.sha256(content).hexdigest(),
            "pages": total, "words": word_count, "page_roles": roles,
            "status": "PASS" if not errors else "FAIL", "errors": errors}


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--pdf-dir", type=Path, required=True)
    parser.add_argument("--minimum-pdfs", type=int, default=1)
    parser.add_argument("--report", type=Path, required=True)
    args = parser.parse_args()
    paths = sorted(args.pdf_dir.glob("V11-INTERNAL-PROOF-*.pdf"))
    reports = [inspect_one(path) for path in paths]
    output = {
        "scope": "independent physical-envelope smoke, NOT G4/golden/owner acceptance",
        "source": "actual Desktop App PDFBox V11 proofs from current CI checkout",
        "count": len(paths), "pages": sum(r["pages"] for r in reports),
        "errors": sum(len(r["errors"]) for r in reports), "pdfs": reports,
    }
    args.report.parent.mkdir(parents=True, exist_ok=True)
    args.report.write_text(json.dumps(output, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f'V11_PHYSICAL_AUDIT_PDFS={output["count"]}')
    print(f'V11_PHYSICAL_AUDIT_PAGES={output["pages"]}')
    print(f'V11_PHYSICAL_AUDIT_ERRORS={output["errors"]}')
    for r in reports:
        if r["errors"]:
            print(f'{r["file"]}: {r["errors"][:5]}')
    return 0 if len(paths) >= args.minimum_pdfs and not output["errors"] else 1


if __name__ == "__main__":
    raise SystemExit(main())
