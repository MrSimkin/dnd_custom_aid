#!/usr/bin/env python3
"""Independent final-NOTAS vector comparator; NOT a full V11 G4 or owner gate.

Reads the original owner-approved ZIP and real PDFBox App outputs. Different
character data does not affect the final blank manuscript geometry.
"""
import argparse
import hashlib
import json
import zipfile
from pathlib import Path

import fitz

GOLDEN_PDF = "V11_Carta/pdfs/v11_ordinario_6mas3_variante_B.pdf"


def physical_strokes(page):
    """Isolate ONLY the final manuscript fields, excluding page header/frame."""
    result = []
    for item in page.get_drawings():
        rect = item["rect"]
        if not (39 <= rect.x0 and rect.x1 <= 573 and
                95 <= rect.y0 and rect.y1 <= 735):
            continue
        color, fill = item["color"], item["fill"]
        width = item["width"] or 0
        # Alternating two-column background, each stripe exactly one row.
        if fill is not None and rect.width > 250 and 18 <= rect.height <= 20:
            kind = "ruled-fill"
            rgb = fill
        # Two-column thin ruled baselines above the grid.
        elif color is not None and rect.width > 250 and rect.height < 0.1 and \
                110 <= rect.y0 <= 419.5 and width <= 0.4:
            kind = "ruled-line"
            rgb = color
        # One COMPLETE 34 x 20 grid border.
        elif color is not None and rect.width > 530 and rect.height > 300 and \
                428 <= rect.y0 < 431 and 733 <= rect.y1 < 735 and width <= 0.5:
            kind = "grid-frame"
            rgb = color
        # 33 inner vertical + 19 inner horizontal grid lines.
        elif color is not None and 428 <= rect.y0 <= 735 and \
                rect.y1 <= 735 and width <= 0.31 and (
                (rect.width < 0.1 and rect.height > 300) or
                (rect.height < 0.1 and rect.width > 530)):
            kind = "grid-inner"
            rgb = color
        else:
            continue
        result.append((kind, *(round(z, 3) for z in (rect.x0, rect.y0, rect.x1, rect.y1)),
                       round(width, 3), *(round(v, 4) for v in rgb)))
    return sorted(result)


def compare(golden, candidate):
    expected = physical_strokes(golden)
    actual = physical_strokes(candidate)
    by_kind = {}
    for kind in ("ruled-fill", "ruled-line", "grid-frame", "grid-inner"):
        e = [item for item in expected if item[0] == kind]
        a = [item for item in actual if item[0] == kind]
        failures = []
        if len(e) != len(a):
            failures.append(f"count {len(a)} != golden {len(e)}")
        for k, (lhs, rhs) in enumerate(zip(e, a)):
            if any(abs(v - w) > (0.40 if 1 <= j <= 4 else 0.025)
                   for j, (v, w) in enumerate(zip(lhs[1:], rhs[1:]), start=1)):
                failures.append(f"vector {k} differs from approved physical bounds/color/width")
                if len(failures) > 4:
                    break
        by_kind[kind] = {"golden": len(e), "actual": len(a), "errors": failures}
    if [len([x for x in expected if x[0] == t]) for t in by_kind] != [34, 34, 1, 52]:
        raise ValueError("Approved golden has unexpected manuscript vector topology")
    return by_kind


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--golden-zip", type=Path, required=True)
    parser.add_argument("--pdf-dir", type=Path, required=True)
    parser.add_argument("--minimum-pdfs", type=int, default=48)
    parser.add_argument("--report", type=Path, required=True)
    args = parser.parse_args()
    with zipfile.ZipFile(args.golden_zip) as archive:
        if archive.testzip() is not None:
            raise ValueError("Approved source ZIP is corrupt")
        golden_bytes = archive.read(GOLDEN_PDF)
    golden = fitz.open(stream=golden_bytes, filetype="pdf")
    pdfs = sorted(args.pdf_dir.glob("V11-INTERNAL-PROOF-*.pdf"))
    results = []
    for path in pdfs:
        payload = path.read_bytes()
        with fitz.open(stream=payload, filetype="pdf") as doc:
            checks = compare(golden[-1], doc[-1])
        results.append({
            "file": path.name,
            "sha256": hashlib.sha256(payload).hexdigest(),
            "checks": checks,
            "status": "FAIL" if any(x["errors"] for x in checks.values()) else "PASS",
        })
    report = {
        "scope": "frozen V11 final NOTAS physical vector only; NOT overall G4/Android/owner PASS",
        "approved_golden_pdf_sha256": hashlib.sha256(golden_bytes).hexdigest(),
        "app_pdfs": len(results),
        "pass": sum(r["status"] == "PASS" for r in results),
        "fail": sum(r["status"] == "FAIL" for r in results),
        "pdfs": results,
    }
    args.report.parent.mkdir(parents=True, exist_ok=True)
    args.report.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print("V11_GOLDEN_FINAL_NOTES=" + str(report["pass"]) + "/" + str(report["app_pdfs"]))
    if report["fail"]:
        for row in results:
            if row["status"] == "FAIL":
                print(row["file"], {k: v["errors"][:2] for k, v in row["checks"].items() if v["errors"]})
                break
    return 0 if len(pdfs) >= args.minimum_pdfs and report["fail"] == 0 else 1


if __name__ == "__main__":
    raise SystemExit(main())
