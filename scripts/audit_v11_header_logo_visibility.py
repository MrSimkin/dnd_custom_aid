#!/usr/bin/env python3
"""Independent V11 logo visibility guard on physical App PDF pages.

The eight approved PDF goldens establish that the upper-left header area
contains artwork. This guard checks actual visible ink from real App output,
not merely the presence of an embedded image object (which might be clipped
away). Pixel identity, owner acceptance and full G4 remain NOT VERIFIED.
"""
import argparse
import hashlib
import json
import zipfile
from pathlib import Path

import fitz

EXPECTED_GOLDEN_SHA = "35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a"
# Top-down PDF coordinates. Deliberately INSIDE both exterior frames.
# Excludes the header name on the right and the horizontal frame strokes.
LOGO_INTERIOR = fitz.Rect(49, 33, 135, 58)
NAME_BAND = fitz.Rect(152, 24, 563, 62)


def signature(page):
    if abs(page.rect.width - 612) > 0.05 or abs(page.rect.height - 792) > 0.05:
        raise ValueError("V11 physical page must be US Letter 612x792")
    pix = page.get_pixmap(
        matrix=fitz.Matrix(2, 2), clip=LOGO_INTERIOR,
        colorspace=fitz.csRGB, alpha=False,
    )
    rgb = pix.samples
    dark = sum(1 for k in range(0, len(rgb), 3)
               if min(rgb[k], rgb[k + 1], rgb[k + 2]) < 155)
    words = page.get_text("words", clip=NAME_BAND, sort=True)
    return {
        "logo_dark_fraction": round(dark / (pix.width * pix.height), 5),
        "header_text": " ".join(str(w[4]) for w in words)[:140],
        "header_word_count": len(words),
    }


def evaluate(goldens, candidate, floor):
    if not goldens:
        raise ValueError("Missing golden reference signatures")
    return candidate["logo_dark_fraction"] >= floor


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--golden-zip", type=Path, required=True)
    parser.add_argument("--pdf-dir", type=Path, required=True)
    parser.add_argument("--minimum-pdfs", type=int, default=54)
    parser.add_argument("--report", type=Path, required=True)
    args = parser.parse_args()

    golden_bytes = args.golden_zip.read_bytes()
    if hashlib.sha256(golden_bytes).hexdigest() != EXPECTED_GOLDEN_SHA:
        raise ValueError("Owner-approved golden ZIP changed")
    golden = []
    golden_hashes = {}
    with zipfile.ZipFile(args.golden_zip) as archive:
        if archive.testzip() is not None:
            raise ValueError("Invalid golden ZIP")
        names = sorted(n for n in archive.namelist() if n.lower().endswith(".pdf"))
        if len(names) != 8:
            raise ValueError("Expected all eight approved V11 golden PDFs")
        for name in names:
            data = archive.read(name)
            golden_hashes[name] = hashlib.sha256(data).hexdigest()
            with fitz.open(stream=data, filetype="pdf") as pdf:
                for page in pdf:
                    golden.append(signature(page))
    if len(golden) != 59:
        raise ValueError("Approved reference set must contain 59 pages")
    golden_min = min(x["logo_dark_fraction"] for x in golden)
    if golden_min < 0.006:
        raise ValueError(
            "Golden header logo region is not reliably detectable; audit BLOCKED"
        )
    # This is only a conservative VISIBLE-LOGO bound, not golden visual parity.
    floor = max(0.006, min(0.015, golden_min * 0.15))

    pdf_paths = sorted(args.pdf_dir.glob("V11-INTERNAL-PROOF-*.pdf"))
    results = []
    total = 0
    failed = 0
    for path in pdf_paths:
        data = path.read_bytes()
        bad = []
        fractions = []
        with fitz.open(stream=data, filetype="pdf") as pdf:
            for index, page in enumerate(pdf):
                total += 1
                observation = signature(page)
                fractions.append(observation["logo_dark_fraction"])
                if not evaluate(golden, observation, floor):
                    failed += 1
                    bad.append({"page": index + 1, **observation})
        results.append({
            "file": path.name,
            "sha256": hashlib.sha256(data).hexdigest(),
            "pages": len(fractions),
            "min_logo_dark_fraction": min(fractions) if fractions else None,
            "logo_missing_pages": bad[:8],
            "missing_count": len(bad),
        })
    report = {
        "scope": "Logo ink visibility on ALL V11 PDF pages; NOT pixel parity/G4/owner QA",
        "golden_zip_sha256": EXPECTED_GOLDEN_SHA,
        "golden_pdf_sha256": golden_hashes,
        "golden_pages": len(golden),
        "golden_min_logo_dark_fraction": golden_min,
        "visibility_floor": round(floor, 5),
        "candidate_pdfs": len(pdf_paths),
        "candidate_pages": total,
        "failed_pages": failed,
        "header_text_parity": "NOT_VERIFIED; names logged as diagnostics, no source snapshot oracle",
        "artwork_pixel_parity": "NOT_VERIFIED; requires dedicated visual audit",
        "results": results,
    }
    args.report.parent.mkdir(parents=True, exist_ok=True)
    args.report.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n",
                           encoding="utf-8")
    print("V11_LOGO_VISIBLE=%d/%d; PDFs=%d; golden=%d pages" %
          (total - failed, total, len(pdf_paths), len(golden)))
    if failed:
        print(next(x for x in results if x["missing_count"]))
    return 0 if len(pdf_paths) >= args.minimum_pdfs and total > 0 and failed == 0 else 1


if __name__ == "__main__":
    raise SystemExit(main())
