#!/usr/bin/env python3
"""Independent V11 continuation-cue audit of real exported PDFs (C341).

Owner 2026-10-30: every reference between physical pages reads
«CONTINÚA EN PÁG. X» where content leaves a page and «VIENE DE PÁG. Y» where it
arrives; each page but the final NOTAS prints its bare number (C340).
Checks the *rendered PDF* ink only: every cue names a page that prints that
number, «CONTINÚA EN» points forward and «VIENE DE» backward, no abbreviated
legacy form survives, and every page that announces a destination is answered
by that destination. Does not certify complete G4 or owner acceptance.
"""
import argparse
import hashlib
import json
import re
from pathlib import Path

import fitz

COMES_FROM = re.compile(r"VIENE DE PÁG\. (\d+(?:(?:, | Y )\d+)*)")
CONTINUES_ON = re.compile(r"CONTINÚAN? EN PÁG\. (\d+)")
LEGACY = re.compile(
    r"\bA P\. \d|VIENE P\. \d|\bSIG\. \d|\bANT\. \d|CONTINÚA:|CONTINÚA ID|"
    r"CONTINÚA ATRIBUTO|CONTINÚA EQUIPO|EN C2 PÁG|VIENE DE P[12] PÁG"
)
PAGE_NUMBER_CLIP = fitz.Rect(544, 750, 575, 766)


def inspect_pages(texts: list[str], numbers: list[str]) -> tuple[int, list[str]]:
    """Pure oracle over printed page text and printed footer numbers."""
    errors = []
    cues = 0
    total = len(texts)
    for index, number in enumerate(numbers, start=1):
        expected = "" if index == total else str(index)
        if number != expected:
            errors.append(f"p{index}: printed page number {number!r}, expected {expected!r}")
    incoming = {i: set() for i in range(1, total + 1)}
    outgoing = {i: set() for i in range(1, total + 1)}
    for index, text in enumerate(texts, start=1):
        for match in LEGACY.finditer(text):
            errors.append(f"p{index}: legacy abbreviated cue {match.group(0)!r}")
        for match in COMES_FROM.finditer(text):
            for page in map(int, re.findall(r"\d+", match.group(1))):
                cues += 1
                incoming[index].add(page)
                if not 1 <= page < index:
                    errors.append(f"p{index}: «VIENE DE PÁG. {page}» does not point back")
        for match in CONTINUES_ON.finditer(text):
            page = int(match.group(1))
            cues += 1
            outgoing[index].add(page)
            if not index < page < total:
                errors.append(f"p{index}: «CONTINÚA EN PÁG. {page}» does not point forward")
    for source, targets in outgoing.items():
        for target in targets:
            if target in incoming and source not in incoming[target]:
                errors.append(f"p{source}: announces p{target}, which never says it comes from p{source}")
    return cues, errors


def inspect_pdf(path: Path) -> dict:
    data = path.read_bytes()
    with fitz.open(stream=data, filetype="pdf") as doc:
        texts = [page.get_text() for page in doc]
        numbers = [page.get_text(clip=PAGE_NUMBER_CLIP).strip() for page in doc]
    cues, errors = inspect_pages(texts, numbers)
    return {"pdf": path.name, "sha256": hashlib.sha256(data).hexdigest(),
            "pages": len(texts), "cues": cues,
            "status": "PASS" if not errors else "FAIL", "errors": errors}


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--pdf-dir", type=Path, required=True)
    parser.add_argument("--minimum-pdfs", type=int, default=1)
    parser.add_argument("--report", type=Path, required=True)
    args = parser.parse_args()
    paths = sorted(args.pdf_dir.glob("V11-INTERNAL-PROOF-*.pdf"))
    results = [inspect_pdf(p) for p in paths]
    report = {"scope": "independent V11 continuation cues and page numbers; NOT G4 or owner QA",
              "count": len(results),
              "cues": sum(x["cues"] for x in results),
              "errors": sum(len(x["errors"]) for x in results),
              "results": results}
    args.report.parent.mkdir(parents=True, exist_ok=True)
    args.report.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n",
                           encoding="utf-8")
    print(f'V11_CONTINUATION_CUES={report["cues"]}; PDFs={report["count"]}; ERRORS={report["errors"]}')
    for item in results:
        if item["errors"]:
            print(f'{item["pdf"]}: {item["errors"][:4]}')
    return 0 if len(results) >= args.minimum_pdfs and report["errors"] == 0 else 1


if __name__ == "__main__":
    raise SystemExit(main())
