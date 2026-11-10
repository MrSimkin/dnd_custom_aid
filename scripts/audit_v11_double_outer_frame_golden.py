#!/usr/bin/env python3
"""Independent check of both V11 exterior PDF frame vectors on ALL physical pages.

Compares geometry, stroke width and RGB with all eight frozen owner-approved
golden PDF originals. This narrow check is NOT overall G4 or owner/device QA.
"""
import argparse
import hashlib
import json
import zipfile
from pathlib import Path

import fitz


def signature(page):
    strokes = page.get_drawings()
    if len(strokes) < 2:
        raise ValueError("Exterior double frame missing")
    output = []
    for stroke in strokes[:2]:
        box = stroke["rect"]
        if stroke["type"] != "s" or stroke["color"] is None or len(stroke["items"]) != 1:
            raise ValueError("Expected exterior stroked rectangle")
        if stroke["items"][0][0] != "re":
            raise ValueError("Exterior frame is not rectangular")
        output.append((box.x0, box.y0, box.x1, box.y1,
                       stroke["width"], *stroke["color"]))
    return output


def compare(golden, candidate):
    errors = []
    for i, (expected, actual) in enumerate(zip(golden, candidate)):
        for j, (x, y) in enumerate(zip(expected, actual)):
            tolerance = .025 if j < 5 else .004
            if abs(x-y) > tolerance:
                errors.append("frame %s component %s: %.4f != golden %.4f"
                              % (i+1, j, y, x))
    return errors


def main():
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--golden-zip", type=Path, required=True)
    p.add_argument("--pdf-dir", type=Path, required=True)
    p.add_argument("--minimum-pdfs", type=int, default=54)
    p.add_argument("--report", type=Path, required=True)
    args = p.parse_args()
    golden = None
    gold_pages = 0
    hashes = {}
    with zipfile.ZipFile(args.golden_zip) as archive:
        if archive.testzip() is not None:
            raise ValueError("Corrupt approved golden archive")
        pdf_names = [name for name in archive.namelist()
                     if name.lower().endswith(".pdf")]
        if len(pdf_names) != 8:
            raise ValueError("Expected precisely eight frozen approved golden PDFs")
        for name in pdf_names:
            data = archive.read(name)
            hashes[name] = hashlib.sha256(data).hexdigest()
            with fitz.open(stream=data, filetype="pdf") as pdf:
                for page in pdf:
                    gold_pages += 1
                    strokes = signature(page)
                    if golden is None:
                        golden = strokes
                    elif compare(golden, strokes):
                        raise ValueError("Approved golden double frames disagree: "+name)
    docs = sorted(args.pdf_dir.glob("V11-INTERNAL-PROOF-*.pdf"))
    results = []
    pages = 0
    bad_pages = 0
    for path in docs:
        data = path.read_bytes()
        issues = []
        with fitz.open(stream=data, filetype="pdf") as pdf:
            for idx, page in enumerate(pdf):
                pages += 1
                try:
                    errors = compare(golden, signature(page))
                except ValueError as exc:
                    errors = [str(exc)]
                if errors:
                    bad_pages += 1
                    issues.append({"page": idx+1, "errors": errors[:5]})
        results.append({"file": path.name,
                        "sha256": hashlib.sha256(data).hexdigest(),
                        "bad_pages": len(issues), "first_issues": issues[:3]})
    report = {
        "scope": "V11 exterior DOUBLE frame vectors ONLY; NOT G4/owner QA",
        "approved_pdf_sha256": hashes, "golden_pages": gold_pages,
        "candidate_pdfs": len(docs), "candidate_pages": pages,
        "failed_pages": bad_pages, "results": results,
    }
    args.report.parent.mkdir(parents=True, exist_ok=True)
    args.report.write_text(json.dumps(report, indent=2, ensure_ascii=False)+"\n",
                           encoding="utf-8")
    print("V11_GOLDEN_DOUBLE_FRAME=%d/%d; PDFs=%d" %
          (pages-bad_pages, pages, len(docs)))
    if bad_pages:
        print(next(r for r in results if r["bad_pages"]))
    return 0 if len(docs) >= args.minimum_pdfs and bad_pages == 0 else 1


if __name__ == "__main__":
    raise SystemExit(main())
