#!/usr/bin/env python3
"""C412: every ink and face in a real App PDF must belong to the golden.

The palette was measured from all eight approved golden PDFs. The only additions
are owner or contract decisions: the "Para Hoja de PJ Symbols" state glyphs, the
EB Garamond Bold accent fallback (C285) and the #24394b vector rule that replaces
the golden's underscore glyphs. Images are not inspected: the header logo is one.
"""
from __future__ import annotations

import argparse
import json
from pathlib import Path

import fitz

TEXT_INKS = {"#24394b", "#264b68", "#1d384f", "#657d90"}
FACES = ("EBGaramond12-Bold", "EBGaramond-Bold", "NotoSansDisplay-CondensedBold",
         "NotoSans-Regular", "NotoSans-Bold", "ParaHojadePJSymbolsV8-Regular")
STROKES = {"#94b0c1", "#527c96", "#bfd3de", "#264b68", "#657d90", "#24394b"}
# #527c96 fills only the C2 continuity rail, a stroke-like 1.5 pt bar.
FILLS = {"#ffffff", "#e7f2f7", "#f0f7fa", "#bfd3de", "#527c96"}


def hex_ink(colour):
    return "#%02x%02x%02x" % tuple(round(v * 255) for v in colour)


def page_failures(page):
    failures = []
    for block in page.get_text("dict")["blocks"]:
        for line in block.get("lines", []):
            for span in line["spans"]:
                if not span["text"].strip():
                    continue
                ink = "#%06x" % span["color"]
                face = span["font"].split("+")[-1]
                label = span["text"][:30]
                if ink not in TEXT_INKS:
                    failures.append(f"text ink {ink}: {label!r}")
                # PyMuPDF reports a font name cut to 24 characters.
                if not any(known[:24] == face[:24] for known in FACES):
                    failures.append(f"face {face}: {label!r}")
    for path in page.get_drawings():
        where = tuple(round(v) for v in path["rect"])
        if path.get("color") is not None and hex_ink(path["color"]) not in STROKES:
            failures.append(f"stroke {hex_ink(path['color'])} at {where}")
        if path.get("fill") is not None and hex_ink(path["fill"]) not in FILLS:
            failures.append(f"fill {hex_ink(path['fill'])} at {where}")
    return failures


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--pdf-dir", type=Path, required=True)
    parser.add_argument("--minimum-pdfs", type=int, default=1)
    parser.add_argument("--report", type=Path)
    args = parser.parse_args()
    pdfs = sorted(args.pdf_dir.glob("V11-INTERNAL-PROOF-*.pdf"))
    results = {}
    for pdf in pdfs:
        with fitz.open(pdf) as document:
            found = {str(i + 1): page_failures(page) for i, page in enumerate(document)}
        results[pdf.name] = {k: v for k, v in found.items() if v}
    bad = {k: v for k, v in results.items() if v}
    report = {"pdfs": len(pdfs), "failures": bad}
    if args.report:
        args.report.parent.mkdir(parents=True, exist_ok=True)
        args.report.write_text(json.dumps(report, ensure_ascii=False, indent=1), encoding="utf-8")
    if len(pdfs) < args.minimum_pdfs or bad:
        for name, pages in list(bad.items())[:5]:
            for number, items in list(pages.items())[:3]:
                print(name, "p" + number, ascii(items[:4]))
        print(f"FAIL golden palette: {len(bad)} of {len(pdfs)} PDFs")
        raise SystemExit(1)
    print(f"V11 golden palette: PASS {len(pdfs)} PDFs")


if __name__ == "__main__":
    main()
