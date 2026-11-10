#!/usr/bin/env python3
"""Independent audit of the owner's V11 attribute row (P104, owner 2026-11-01).

Reads the drawn ink of real exported PDFs (P1 and C2 pages): every chamfered
score box must have the modifier oval over its right edge (a quarter of the
oval's width) and a saving-throw box on the same middle line, with the two-line
«TIRADA DE / SALVACIÓN» label right-aligned 3 pt left of that box. Does not
certify G4 or owner QA.
"""
import argparse
import hashlib
import json
from pathlib import Path

import fitz

BOX = (43.08, 18.14)
OVAL = (35.44, 14.46)
SAVE = 8.78
TOL = 0.1


def close(a, b, tol=TOL):
    return abs(a - b) <= tol


def inspect_shapes(boxes, ovals, saves, labels):
    """Pure oracle. boxes/ovals/saves: (x0, y0, x1, y1) top-origin; labels: (text, x1, y)."""
    errors = []
    for box in boxes:
        cy = (box[1] + box[3]) / 2
        oval = next((o for o in ovals if close((o[1] + o[3]) / 2, cy)
                     and box[0] < o[0] < box[2]), None)
        if oval is None:
            errors.append(f"score box at y{cy:.2f} has no oval centred on its line")
            continue
        if not close(oval[0], box[2] - OVAL[0] / 4, .2):
            errors.append(f"oval at y{cy:.2f} does not ride a quarter over the box edge")
        save = next((s for s in saves if close((s[1] + s[3]) / 2, cy) and s[0] > oval[2]), None)
        if save is None:
            errors.append(f"row at y{cy:.2f} has no saving-throw box on its middle line")
            continue
        for word in ("TIRADA DE", "SALVACIÓN"):
            if not any(text == word and close(x1, save[0] - 3, .45) and abs(y - cy) < 8
                       for text, x1, y in labels):
                errors.append(f"row at y{cy:.2f}: «{word}» is not right-aligned 3 pt left of its box")
    return errors


def page_shapes(page):
    boxes, ovals, saves = [], [], []
    for d in page.get_drawings():
        r = d["rect"]
        kinds = [it[0] for it in d["items"]]
        if d.get("fill") != (1.0, 1.0, 1.0) and tuple(round(c, 2) for c in (d.get("fill") or ())) != (1.0, 1.0, 1.0):
            continue
        if len(kinds) == 8 and set(kinds) == {"l"} and close(r.width, BOX[0], .2) and close(r.height, BOX[1], .2):
            boxes.append(tuple(r))
        elif kinds == ["c"] * 4 and close(r.width, OVAL[0], .2) and close(r.height, OVAL[1], .2):
            ovals.append(tuple(r))
        elif kinds == ["re"] and close(r.width, SAVE, .05) and close(r.height, SAVE, .05):
            saves.append(tuple(r))
    labels = []
    for block in page.get_text("dict")["blocks"]:
        for line in block.get("lines", []):
            for span in line["spans"]:
                text = span["text"].strip()
                if text in ("TIRADA DE", "SALVACIÓN"):
                    labels.append((text, span["bbox"][2], (span["bbox"][1] + span["bbox"][3]) / 2))
    return boxes, ovals, saves, labels


def inspect_pdf(path: Path) -> dict:
    data = path.read_bytes()
    rows, errors = 0, []
    with fitz.open(stream=data, filetype="pdf") as doc:
        for index, page in enumerate(doc):
            if index != 0 and "MÓDULOS COMPARTIDOS · DOS COLUMNAS" not in page.get_text():  # C463
                continue
            boxes, ovals, saves, labels = page_shapes(page)
            rows += len(boxes)
            errors += [f"p{index + 1}: {e}" for e in inspect_shapes(boxes, ovals, saves, labels)]
            if index == 0 and not boxes:
                errors.append("p1: no attribute rows found")
    return {"pdf": path.name, "sha256": hashlib.sha256(data).hexdigest(), "rows": rows,
            "status": "PASS" if not errors else "FAIL", "errors": errors}


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--pdf-dir", type=Path, required=True)
    parser.add_argument("--minimum-pdfs", type=int, default=1)
    parser.add_argument("--report", type=Path, required=True)
    args = parser.parse_args()
    paths = sorted(args.pdf_dir.glob("V11-INTERNAL-PROOF-*.pdf"))
    results = [inspect_pdf(p) for p in paths]
    report = {"scope": "independent V11 owner attribute row (P104); NOT G4 or owner QA",
              "count": len(results), "rows": sum(r["rows"] for r in results),
              "errors": sum(len(r["errors"]) for r in results), "results": results}
    args.report.parent.mkdir(parents=True, exist_ok=True)
    args.report.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f'V11_ATTRIBUTE_ROW_OWNER={report["count"] - sum(1 for r in results if r["errors"])}/{report["count"]}; '
          f'ROWS={report["rows"]}; ERRORS={report["errors"]}')
    for item in results:
        if item["errors"]:
            print(f'{item["pdf"]}: {item["errors"][:3]}')
    return 0 if len(results) >= args.minimum_pdfs and report["errors"] == 0 else 1


if __name__ == "__main__":
    raise SystemExit(main())
