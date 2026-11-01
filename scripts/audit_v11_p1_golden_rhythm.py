#!/usr/bin/env python3
"""Independent V11 P1 vertical-rhythm audit of real exported PDFs (C356-C360).

Reads the drawn ink of page 1 only: the golden v11_extremo P1 places six 22.96 pt
cartelas - ATRIBUTOS (x39.69..260.22) and COMBATE, ATAQUES, RASGOS, LANZAMIENTO,
MUNICIONES (x270.43..572.32) - with their feet at fixed heights; variant B adds
HABILIDADES under its last attribute row. Does not certify G4 or owner QA.
"""
import argparse
import hashlib
import json
from pathlib import Path

import fitz

CARTELA_FILL = (231, 242, 247)
LEFT = (39.69, 220.53)
RIGHT = (270.43, 301.89)
RIGHT_FEET = {"COMBATE": 588.19, "ATAQUES": 462.9, "RASGOS": 348.94,
              "LANZAMIENTO": 219.68, "MUNICIONES": 106.30}
LEFT_FOOT = 588.19
HEIGHT = 22.96


def cartelas(rects: list[tuple[float, float, float, float]]) -> list[tuple[float, float, float]]:
    """(x0, width, foot) of every 22.96 pt cartela among (x0, width, foot, height) fills."""
    return [(x, w, foot) for x, w, foot, h in rects if abs(h - HEIGHT) < .1]


def inspect_rects(rects: list[tuple[float, float, float, float]], variant: str) -> list[str]:
    """Pure oracle over bottom-origin (x0, width, foot, height) cartela-coloured 8-point fills."""
    found = cartelas(rects)
    errors = []

    def has(x, w, foot):
        return any(abs(fx - x) < .3 and abs(fw - w) < .3 and abs(ff - foot) < .3 for fx, fw, ff in found)

    if not has(*LEFT, LEFT_FOOT):
        errors.append("ATRIBUTOS cartela not at the golden y588.19")
    for name, foot in RIGHT_FEET.items():
        if not has(*RIGHT, foot):
            errors.append(f"{name} cartela not at the golden y{foot}")
    left_feet = sorted({round(ff, 2) for fx, fw, ff in found
                        if abs(fx - LEFT[0]) < .3 and abs(fw - LEFT[1]) < .3})
    if variant == "B" and len(left_feet) != 2:
        errors.append(f"B needs ATRIBUTOS and HABILIDADES cartelas in the left column, got {left_feet}")
    if variant == "A" and left_feet != [LEFT_FOOT]:
        errors.append(f"A has exactly one left cartela, got {left_feet}")
    return errors


def inspect_pdf(path: Path) -> dict:
    data = path.read_bytes()
    with fitz.open(stream=data, filetype="pdf") as doc:
        page = doc[0]
        H = page.rect.height
        rects = []
        for d in page.get_drawings():
            fill = d.get("fill")
            if fill is None or len(d["items"]) != 8:
                continue
            if tuple(round(c * 255) for c in fill) != CARTELA_FILL:
                continue
            r = d["rect"]
            rects.append((r.x0, r.width, H - r.y1, r.height))
        text = page.get_text()
    variant = "B" if "ATRIBUTOS · B" in text else "A"
    errors = inspect_rects(rects, variant)
    return {"pdf": path.name, "sha256": hashlib.sha256(data).hexdigest(), "variant": variant,
            "status": "PASS" if not errors else "FAIL", "errors": errors}


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--pdf-dir", type=Path, required=True)
    parser.add_argument("--minimum-pdfs", type=int, default=1)
    parser.add_argument("--report", type=Path, required=True)
    args = parser.parse_args()
    paths = sorted(args.pdf_dir.glob("V11-INTERNAL-PROOF-*.pdf"))
    results = [inspect_pdf(p) for p in paths]
    report = {"scope": "independent V11 P1 golden vertical rhythm (cartela feet); NOT G4 or owner QA",
              "count": len(results),
              "errors": sum(len(x["errors"]) for x in results),
              "results": results}
    args.report.parent.mkdir(parents=True, exist_ok=True)
    args.report.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f'V11_P1_GOLDEN_RHYTHM={report["count"] - sum(1 for r in results if r["errors"])}/{report["count"]}; ERRORS={report["errors"]}')
    for item in results:
        if item["errors"]:
            print(f'{item["pdf"]}: {item["errors"][:3]}')
    return 0 if len(results) >= args.minimum_pdfs and report["errors"] == 0 else 1


if __name__ == "__main__":
    raise SystemExit(main())
