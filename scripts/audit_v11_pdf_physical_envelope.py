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
    ("Equipo", 42.0, 108.0, 258.0, 12.42),
    ("Tesoro", 42.0, 344.0, 258.0, 12.42),
    ("Rasgos", 312.0, 108.0, 258.0, 12.42),
    ("Equipo especial", 42.0, 497.0, 528.0, 25.0),
)


def p2_alternating_writing_rows(page: fitz.Page) -> list[str]:
    """Independent guard for four P2 bands, without touching C2 or pagination."""
    tinted = [
        d["rect"] for d in page.get_drawings()
        if d["type"] == "f" and d.get("fill") is not None
        and all(abs(d["fill"][i] - P2_SHADE[i]) < .012 for i in range(3))
    ]
    errors = []
    for name, x, y, width, height in P2_MODULES:
        if not any(abs(r.x0-x) < .4 and abs(r.y0-y) < .4
                   and abs(r.width-width) < .4 and abs(r.height-height) < .5
                   for r in tinted):
            errors.append(f"P2 missing golden row shade in {name} at ({x},{y})")
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
        if index == 2:
            errors.extend(p2_alternating_writing_rows(page))
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
