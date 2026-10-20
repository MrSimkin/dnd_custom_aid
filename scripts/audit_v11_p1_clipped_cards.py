#!/usr/bin/env python3
"""Physical P1 C2c guard for real Desktop App V11 PDFs (not G4 QA)."""
import argparse
import hashlib
import json
from pathlib import Path

import fitz

INK = (80/255, 122/255, 151/255)
IDENTITY = [(158, 87, 570, 115), (158, 117, 284, 145),
            (295, 117, 421, 145), (432, 117, 570, 145)]
# Frozen P1 3+3+2 actual app positions, PDF top-origin.
COMBAT = [
    (312 + col*(86 if row < 2 else 129) + 1.2,
     201 + row*28 + 1.2,
     312 + (col+1)*(86 if row < 2 else 129) - 1.2,
     201 + row*28 + (28 if row < 2 else 32) - 1.2)
    for row in range(3) for col in range(3 if row < 2 else 2)
]
LABELS = [
    ("CA", 0, 0), ("INICIATIVA", 1, 0), ("VELOCIDAD", 2, 0),
    ("COMPETENCIA", 0, 1), ("PV", 1, 1), ("DADOS", 2, 1),
    ("PV", 0, 2), ("PV", 1, 2),
]


def near_rect(a, b, tolerance=.8):
    return all(abs(x-y) <= tolerance for x,y in zip(a, b))


def near_ink(actual):
    return actual is not None and all(abs(x-y) < .028 for x,y in zip(actual, INK))


def physical_p1(pdf_bytes):
    with fitz.open(stream=pdf_bytes, filetype="pdf") as doc:
        if not len(doc):
            return dict(errors=["Empty PDF"], identities=0, combat=0)
        page = doc[0]
        words = page.get_text()
        if not ("P1 - " in words and "COMBATE Y SUPERVIVENCIA" in words):
            return dict(errors=["First page has no V11 P1 combat module"], identities=0, combat=0)
        drawings = page.get_drawings()
        observed_identity = []
        observed_combat = []
        for draw in drawings:
            rect = tuple(float(v) for v in draw["rect"])
            if len(draw["items"]) != 8 or not near_ink(draw["color"]):
                continue
            if draw["type"] == "s":
                observed_identity.append(rect)
            elif draw["type"] == "fs" and draw["fill"] is not None:
                observed_combat.append(rect)
        missing_id = [expected for expected in IDENTITY if not any(
            near_rect(actual, expected) for actual in observed_identity)]
        missing_combat = [expected for expected in COMBAT if not any(
            near_rect(actual, expected) for actual in observed_combat)]
        errors = (["P1 identity missing clipped frames: "+str(missing_id)]
                  if missing_id else [])
        if missing_combat:
            errors.append("P1 combat missing clipped source cells: "+str(missing_combat))
        # Printed SOURCE labels must be inside their actual cards.
        # Numeric values may be deferred to measured C2 when too wide.
        words = page.get_text("words")
        for label, col, row in LABELS:
            width = 86 if row < 2 else 129
            left = 312 + col*width
            top = 201 + row*28
            in_card = [w[4] for w in words
                       if left <= w[0] and w[2] <= left+width
                       and top <= w[1] and w[3] <= top+(28 if row < 2 else 32)]
            if label not in in_card:
                errors.append(f"P1 real {label} outside card row={row},col={col}")
        # Genuine EMPTY ammunition receives twenty UNMARKED 6pt squares
        # arranged as 10+10, and exactly one writable vector NAME baseline.
        # The legacy internal proof printed a literal 12-underscore token;
        # this is paper, never a persisted ammunition label.
        blank_square_y = (667., 683.)
        blank_square_x = (392., 400., 408., 416., 424.,
                          436., 444., 452., 460., 468.)
        def has_square(x,y):
            return any(d["type"] == "s" and
                       near_rect(tuple(d["rect"]), (x,y,x+6.,y+6.), .25)
                       for d in drawings)
        empty_ammunition = all(
            has_square(x,y) for y in blank_square_y for x in blank_square_x
        )
        if empty_ammunition:
            if any(w[4] == "____________" and 315 <= w[0] <= 380 and
                   661 <= w[1] <= 674 for w in words):
                errors.append("P1 empty ammunition printed fake underscore glyphs.")
            if not any(
                d["type"] == "s" and near_rect(
                    tuple(d["rect"]), (316.,672.,377.,672.), .35
                ) and d.get("color") is not None
                and all(abs(d["color"][i] - (130/255,165/255,186/255)[i]) < .025
                        for i in range(3))
                for d in drawings
            ):
                errors.append("P1 empty ammunition has no physical writable name line.")
        return dict(errors=errors, identities=len(IDENTITY)-len(missing_id),
                    combat=len(COMBAT)-len(missing_combat),
                    empty_ammo_name_lines=int(empty_ammunition))


def main():
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--pdf-dir", type=Path, required=True)
    p.add_argument("--minimum-pdfs", type=int, default=48)
    p.add_argument("--report", type=Path, required=True)
    a = p.parse_args()
    paths = sorted(a.pdf_dir.glob("V11-INTERNAL-PROOF-*.pdf"))
    records = []
    for path in paths:
        data = path.read_bytes()
        records.append(dict(file=path.name, sha256=hashlib.sha256(data).hexdigest(),
                            **physical_p1(data)))
    failures = [r for r in records if r["errors"]]
    report = dict(scope="P1 C2c identity plus 3+3+2 real combat sources, NOT G4/owner PASS",
                  pdfs=len(paths), valid=len(records)-len(failures),
                  identity_frames=sum(r["identities"] for r in records),
                  combat_cells=sum(r["combat"] for r in records),
                  empty_ammo_name_lines=sum(r.get("empty_ammo_name_lines",0)
                                            for r in records),
                  errors=len(failures), records=records)
    a.report.parent.mkdir(parents=True,exist_ok=True)
    a.report.write_text(json.dumps(report,ensure_ascii=False,indent=2)+"\n",
                        encoding="utf-8")
    print(f"V11_P1_CLIPPED_CARDS={len(records)-len(failures)}/{len(paths)}; "
          f"identity={report['identity_frames']}; combat={report['combat_cells']}")
    if failures:
        print(failures[0]["file"],failures[0]["errors"][:2])
    return 0 if len(paths)>=a.minimum_pdfs and not failures else 1


if __name__ == "__main__":
    raise SystemExit(main())
