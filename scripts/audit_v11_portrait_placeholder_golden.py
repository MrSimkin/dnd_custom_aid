#!/usr/bin/env python3
"""Independent, scoped V11 P1 no-image portrait check.

C473 (owner 2026-11-10, «4 · Silueta de línea»): the golden's two pale filled ovals become a bust
drawn only in a thin line of golden rule ink. The head keeps the golden head's place and size;
nothing in the frame is filled."""
import argparse
import hashlib
import json
import zipfile
from pathlib import Path

import fitz

GOLDEN = "V11_Carta/pdfs/v11_ordinario_6mas3_variante_B.pdf"
PALE = (231 / 255, 242 / 255, 247 / 255)
LINE = (148 / 255, 176 / 255, 193 / 255)


def detect_line_bust(page):
    """The App's no-photo portrait: one chamfered frame, no filled shape, a stroked head."""
    frames, heads, filled = [], [], 0
    for item in page.get_drawings():
        r = item["rect"]
        if not (35 <= r.x0 < 160 and r.x1 <= 160 and 85 <= r.y0 < 230 and r.y1 <= 235):
            continue
        if len(item["items"]) == 8 and 70 <= r.width <= 110 and 70 <= r.height <= 110:
            frames.append(r)
            continue
        fill = item.get("fill")
        if fill is not None and all(abs(v - e) < .026 for v, e in zip(fill, PALE)) and r.width < 68:
            filled += 1
        colour = item.get("color")
        if (item["type"] == "s" and colour is not None and all(abs(v - e) < .026 for v, e in zip(colour, LINE))
                and 18 < r.width < 28 and 18 < r.height < 28):
            heads.append(r)
    if len(frames) != 1 or filled or len(heads) != 1:
        raise ValueError(f"expected 1 frame, 0 filled ovals and 1 line head, got {len(frames)}/{filled}/{len(heads)}")
    frame, head = frames[0], heads[0]
    return ((head.x0-frame.x0)/frame.width, (head.y0-frame.y0)/frame.height,
            (head.x1-frame.x0)/frame.width, (head.y1-frame.y0)/frame.height)


def detect(page):
    frames = []
    ovals = []
    for item in page.get_drawings():
        r = item["rect"]
        if not (35 <= r.x0 < 160 and r.x1 <= 160 and 85 <= r.y0 < 230 and r.y1 <= 235):
            continue
        if len(item["items"]) == 8 and 70 <= r.width <= 110 and 70 <= r.height <= 110:
            frames.append(r)
        fill = item["fill"]
        if len(item["items"]) == 4 and item["type"] == "fs" and fill is not None:
            if all(abs(v - e) < .026 for v, e in zip(fill, PALE)) and 20 < r.width < 68 and 20 < r.height < 50:
                ovals.append(r)
    if len(frames) != 1 or len(ovals) != 2:
        raise ValueError(f"expected 1 chamfered frame and 2 golden-filled ovals, got {len(frames)}/{len(ovals)}")
    frame = frames[0]
    return sorted([tuple((x.x0-frame.x0)/frame.width if j == 0 else
                          (x.y0-frame.y0)/frame.height if j == 1 else
                          (x.x1-frame.x0)/frame.width if j == 2 else
                          (x.y1-frame.y0)/frame.height for j in range(4))
                   for x in ovals], key=lambda v: v[2]-v[0])


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--golden-zip", type=Path, required=True)
    parser.add_argument("--pdf-dir", type=Path, required=True)
    parser.add_argument("--minimum-pdfs", type=int, default=48)
    parser.add_argument("--report", type=Path, required=True)
    args = parser.parse_args()
    with zipfile.ZipFile(args.golden_zip) as z:
        if z.testzip() is not None:
            raise ValueError("golden ZIP corrupted")
        gold_bytes = z.read(GOLDEN)
    with fitz.open(stream=gold_bytes, filetype="pdf") as g:
        approved = detect(g[0])
    paths = sorted(args.pdf_dir.glob("V11-INTERNAL-PROOF-*.pdf"))
    results = []
    for path in paths:
        payload = path.read_bytes()
        with fitz.open(stream=payload, filetype="pdf") as pdf:
            portrait_present = len(pdf[0].get_images(full=True)) > 1
            errors = []
            if not portrait_present:
                try:
                    measured = detect_line_bust(pdf[0])
                    # The golden head is the narrower oval.
                    for coord, (observed, gold) in enumerate(zip(measured, approved[0])):
                        if abs(observed-gold) > .035:
                            errors.append(f"line head, normalized bound {coord}: {observed:.4f} vs {gold:.4f}")
                except ValueError as exc:
                    errors.append(str(exc))
        results.append({"file": path.name, "sha256": hashlib.sha256(payload).hexdigest(),
                        "portrait_present": portrait_present, "errors": errors})
    total = len(paths)
    fallback = sum(not r["portrait_present"] for r in results)
    fails = sum(bool(r["errors"]) for r in results)
    report = {"scope": "P1 no-image silhouette/frame golden only; NOT overall G4/Android/owner PASS",
              "golden_sha256": hashlib.sha256(gold_bytes).hexdigest(),
              "pdfs": total, "fallbacks": fallback, "pass": total-fails, "fail": fails,
              "results": results}
    args.report.parent.mkdir(parents=True, exist_ok=True)
    args.report.write_text(json.dumps(report, ensure_ascii=False, indent=2)+"\n", encoding="utf-8")
    print(f"V11_GOLDEN_PORTRAIT_FALLBACK={total-fails}/{total}; absent-image cases={fallback}")
    if fails:
        print(next(x for x in results if x["errors"]))
    return 0 if total >= args.minimum_pdfs and fallback >= 2 and fails == 0 else 1


if __name__ == "__main__":
    raise SystemExit(main())
