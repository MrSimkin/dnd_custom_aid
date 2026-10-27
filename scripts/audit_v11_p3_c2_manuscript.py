#!/usr/bin/env python3
"""Physical P305/C207/C208 evidence from *real* Desktop-App V11 PDFs.

Independent PyMuPDF audit. Rejects missing ruled baselines, incomplete
handwriting grids, collisions with printed source words, and C2 pages
consisting only of handwriting notes. This does NOT compare all eight
golden PDFs or validate installed Android, so it cannot grant G4 PASS.
"""
from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path

import fitz

PALE = (0.93333, 0.96863, 0.98431)
GOLDEN_ROW = (231/255, 242/255, 247/255)


def close(a: float, b: float, tolerance: float = 0.16) -> bool:
    return abs(a - b) <= tolerance


def steps_regular(values: list[float], step: float = 16) -> bool:
    ordered = sorted(set(round(value, 2) for value in values))
    return len(ordered) >= 2 and all(
        close(b - a, step) for a, b in zip(ordered, ordered[1:])
    )


def page_role(page: fitz.Page) -> str:
    lines = page.get_text().splitlines()
    return lines[1] if len(lines) > 1 else ""


def p3_result(page: fitz.Page) -> tuple[int, int, list[str]]:
    drawings = page.get_drawings()
    areas = [
        dr["rect"] for dr in drawings
        if dr["type"] == "f"
        and dr.get("fill") is not None
        and all(close(dr["fill"][k], PALE[k], .012) for k in range(3))
        and dr["rect"].width >= 240
        and dr["rect"].height >= 40
        and 110 < dr["rect"].y0 < dr["rect"].y1 < 718
    ]
    lines = [
        dr["rect"] for dr in drawings
        if dr["type"] == "s"
        and dr.get("width") is not None
        and close(dr["width"], .35, .07)
        and dr["rect"].height < .05
        and dr["rect"].width >= 230
    ]
    words = page.get_text("words")
    failures: list[str] = []
    if not areas or len(lines) < 10:
        return len(areas), 0, ["P3 lacks its real shaded writing bands or ruled baselines"]
    matched = 0
    for area in areas:
        printed = [
            word for word in words
            if word[0] >= area.x0 - 1 and word[2] <= area.x1 + 1
            and word[1] >= area.y0 + .4 and word[3] <= area.y1 + 1
            and word[3] - word[1] > 7.5
        ]
        for word in printed:
            has_rule = any(
                rule.x0 - 1 <= word[0] and rule.x1 + 1 >= word[2]
                and .1 <= rule.y0 - word[3] <= 3.5
                for rule in lines
            )
            if has_rule:
                matched += 1
            elif len(failures) < 8:
                failures.append(
                    "P305 text lacks a directly aligned physical ruled line: "
                    + repr(word[4]) + f" bbox={tuple(round(x, 2) for x in word[:4])}"
                )
    return len(areas), matched, failures


def p3_golden_row_grammar(page: fitz.Page, first_page: bool) -> tuple[int,int,list[str]]:
    """Verify 6 real P3 golden title panels and alternating measured paper."""
    color_fills = [
        dr["rect"] for dr in page.get_drawings()
        if dr["type"] == "f" and dr.get("fill") is not None
        and all(close(dr["fill"][i], GOLDEN_ROW[i], .013) for i in range(3))
    ]
    def found(x,y,w,h,xy_tol=.42,height_tol=.42):
        return any(close(r.x0,x,xy_tol) and close(r.y0,y,xy_tol)
                   and close(r.width,w,xy_tol) and close(r.height,h,height_tol)
                   for r in color_fills)
    def stripe(x,y,w,low=9,high=15):
        return any(close(r.x0,x,.42) and close(r.y0,y,.42)
                   and close(r.width,w,.42) and low < r.height < high
                   for r in color_fills)
    errors=[]
    headings=0
    stripes=0
    if first_page:
        # PDF top-origin: golden-preserved first-page 3 paired fields.
        for y in (121.,184.,247.):
            for x in (43.,313.):
                if found(x,y,256.,16.): headings+=1
                else: errors.append(f"P3 golden title band missing at {x},{y}")
        for y in (137.,200.,263.):
            for x in (43.,313.):
                if stripe(x,y,256.): stripes+=1
                else: errors.append(f"P3 alternating field row missing at {x},{y}")
        # Owner option B (2026-10-26): story rows follow the golden 17 pt rhythm.
        for x in (47.,312.):
            if stripe(x,343.,253.,16.5,17.5): stripes+=1
            else: errors.append(f"P3 alternating story row missing at x={x}")
    else:
        # Long true-source P3 continuation: both columns must retain style
        # (12.42 pt profile-field rows or 17 pt story rows).
        for x in (42.,312.):
            if any(close(r.x0,x,.42) and r.width>230
                   and 110<r.y0<150 and (9<r.height<15 or 16.5<r.height<17.5)
                   for r in color_fills): stripes+=1
            else: errors.append(f"P3 continuation missing alternating rows x={x}")
    return headings,stripes,errors


def c2_result(page: fitz.Page) -> tuple[int, int, list[str]]:
    drawings = page.get_drawings()
    horizontal = [
        dr["rect"] for dr in drawings
        if dr.get("width") is not None and close(dr["width"], .5, .08)
        and dr["rect"].height < .05 and dr["rect"].width > 180
    ]
    vertical = [
        dr["rect"] for dr in drawings
        if dr.get("width") is not None and close(dr["width"], .5, .08)
        and dr["rect"].width < .05 and dr["rect"].height > 46
    ]
    words = page.get_text("words")
    cartelas = [
        word for word in words
        if word[4] == "NOTAS" and close(word[3] - word[1], 14.4, .14)
    ]
    failures: list[str] = []
    complete = 0
    for word in cartelas:
        grid_top = word[3] + 4.6
        tops = [
            rect for rect in horizontal
            if close(rect.y0, grid_top, .38)
            and rect.x0 < word[0] + 2 and rect.x1 > word[2] + 50
        ]
        if not tops:
            failures.append(f"C207 NOTAS at y={word[1]:.2f} has no grid top line")
            continue
        first = tops[0]
        columns = [
            rect for rect in vertical
            if close(rect.y0, grid_top, .38)
            and first.x0 - 1 <= rect.x0 <= first.x1 + 1
        ]
        bottom = max((line.y1 for line in columns), default=None)
        rows = [
            rect for rect in horizontal
            if bottom is not None
            and close(rect.x0, first.x0, 1)
            and close(rect.x1, first.x1, 1)
            and grid_top - .4 <= rect.y0 <= bottom + .4
        ]
        if (
            bottom is None or len(columns) < 6 or len(rows) < 4
            or not steps_regular([line.x0 for line in columns])
            or not steps_regular([line.y0 for line in rows])
            or not close(min(line.x0 for line in columns), first.x0, 1)
            or not close(max(line.x0 for line in columns), first.x1, 1)
            or not close(min(line.y0 for line in rows), grid_top, .4)
            or not close(max(line.y0 for line in rows), bottom, .4)
        ):
            failures.append(
                f"C207 incomplete/irregular 16pt grid at y={word[1]:.2f}; "
                f"vertical={len(columns)}, horizontal={len(rows)}"
            )
            continue
        overlaps = [
            other for other in words
            if other != word and other[4] != "NOTAS"
            and other[0] < first.x1 and other[2] > first.x0
            and grid_top <= other[1] < bottom and other[3] > grid_top
        ]
        if overlaps:
            failures.append(
                f"C207 grid covers {len(overlaps)} source words, e.g. "
                + repr(overlaps[0][4])
            )
            continue
        complete += 1
    # C208: a C2 page containing only a manuscript table is forbidden.
    # Words from genuine sources start at y >= 89.5 (C2 titles/cartelas
    # above); NOTAS written by the renderer has exactly 14.4pt bounds.
    source_words = [
        word for word in words
        if 89.5 <= word[1] <= 720
        and not (word[4] == "NOTAS" and close(word[3] - word[1], 14.4, .14))
    ]
    if not source_words:
        failures.append("C208 physical C2 page has no actual digital source content")
    return len(cartelas), complete, failures


def c2_context_result(page: fitz.Page) -> tuple[int, int, list[str]]:
    """Golden C2c continuation bands around existing real source identity ink.

    Deliberately checks only captions already printed in the reserved header
    gap; does not invent missing source identities or approve whole C2.
    """
    captions = [
        w for w in page.get_text("words")
        if w[4].startswith("CONTINÚA") and 76.3 <= w[1] <= 79.0
        and w[3] < 88
    ]
    color = (217 / 255, 236 / 255, 245 / 255)
    bands = [
        dr for dr in page.get_drawings()
        if dr.get("fill") is not None
        and all(close(dr["fill"][j], color[j], .015) for j in range(3))
        and close(dr["rect"].y0, 76., .3)
        and close(dr["rect"].y1, 89., .3)
        and dr["rect"].width > 235
        and dr.get("type") in {"fs", "f"}
    ]
    matched = 0
    failures = []
    for word in captions:
        if any(
            dr["rect"].x0 <= word[0] - .2 and dr["rect"].x1 >= word[2] + .2
            for dr in bands
        ):
            matched += 1
        else:
            failures.append(
                f"C206 continued source identity lacks C2c cartela at "
                f"x={word[0]:.2f}, y={word[1]:.2f}"
            )
    return len(captions), matched, failures



def c2_skill_checkbox_result(page: fitz.Page) -> tuple[int, int, list[str]]:
    """C216: actual source-linked skills have one blank PDF vector paper box.

    Only the first line of an indented `Habilidad` source is eligible;
    generic standalone skills use an unindented source and are not counted.
    This is a physical-PDF check, not a count inferred from Kotlin call sites.
    """
    words = [
        w for w in page.get_text("words")
        if w[4] == "Habilidad" and 90 <= w[1] <= 704
        and (51 <= w[0] <= 70 or 321 <= w[0] <= 340)
    ]
    boxes = [
        dr["rect"] for dr in page.get_drawings()
        if dr.get("type") == "s"
        and dr.get("width") is not None
        and close(dr["width"], .65, .07)
        and close(dr["rect"].width, 7, .08)
        and close(dr["rect"].height, 7, .08)
        and any(close(dr["rect"].x0, x, .22) for x in (44, 314))
    ]
    matched = 0
    errors: list[str] = []
    for w in words:
        x = 44 if w[0] < 200 else 314
        near = [r for r in boxes if close(r.x0, x, .22)
                and w[1] - 8 <= r.y0 <= w[3]]
        if len(near) == 1:
            matched += 1
            if any(not (other is w) and other[0] < near[0].x1
                   and other[2] > near[0].x0 and other[1] < near[0].y1
                   and other[3] > near[0].y0
                   for other in page.get_text("words")):
                errors.append("C216 manual skill square collides with digital source ink")
        else:
            errors.append(
                f"C216 genuine linked skill lacks unique 7pt paper box "
                f"at x={w[0]:.2f}, y={w[1]:.2f}"
            )
    return len(words), matched, errors


def c2_source_skill_right_value_result(page: fitz.Page) -> tuple[int, list[str]]:
    """C217: independent real-PDF check, numeric skill total on right edge.

    Wrapped rows and non-numeric unresolved source totals retain old
    lossless representation; they must never be counted as right-aligned.
    """
    import re
    words = page.get_text("words")
    matched = 0
    failures: list[str] = []
    for w in words:
        if w[4] != "Habilidad" or not (
            52 < w[0] < 70 or 322 < w[0] < 340
        ) or not (90 < w[1] < 704):
            continue
        right_edge = 300 if w[0] < 200 else 570
        same_row = [v for v in words if abs(v[1] - w[1]) <= .55 and v[0] > w[0]]
        totals = [v for v in same_row if
                  re.fullmatch(r"[+-]?[0-9]+", v[4]) and
                  close(v[2], right_edge - 4, .8)]
        if totals:
            matched += 1
            for total in totals:
                collision = [other for other in same_row if
                             other != total and other[0] < total[2] and
                             other[2] > total[0] and
                             abs(other[1] - total[1]) < 2]
                if collision:
                    failures.append(
                        f"C217 right-aligned total overlaps printed source at "
                        f"x={total[0]:.2f}, y={total[1]:.2f}"
                    )
    return matched, failures


def audit(pdf_files: list[Path], minimum_pdfs: int) -> dict:
    if len(pdf_files) < minimum_pdfs:
        raise ValueError(f"Expected >= {minimum_pdfs} genuine PDFs; got {len(pdf_files)}")
    result: dict = {
        "scope": "physical P305/C207/C208 only; not all golden, Android or G4",
        "pdf_count": 0,
        "p3_pages": 0, "p3_shaded_bands": 0, "p3_aligned_words": 0,
        "p3_golden_field_title_bands": 0, "p3_golden_alternate_rows": 0,
        "c2_pages": 0, "c2_notes_cartelas": 0, "c2_complete_grids": 0,
        "c2_context_labels": 0, "c2_context_blue_bands": 0,
        "c2_linked_skill_rows": 0, "c2_linked_skill_paper_boxes": 0,
        "c2_right_aligned_skill_totals": 0,
        "failures": [], "pdfs": [],
    }
    for source in pdf_files:
        raw = source.read_bytes()
        doc = fitz.open(stream=raw, filetype="pdf")
        result["pdf_count"] += 1
        file_entry = {
            "name": source.name,
            "sha256": hashlib.sha256(raw).hexdigest(),
            "pages": doc.page_count,
        }
        result["pdfs"].append(file_entry)
        for index, page in enumerate(doc):
            role = page_role(page)
            if role.startswith("P3 - "):
                result["p3_pages"] += 1
                areas, aligned, failures = p3_result(page)
                result["p3_shaded_bands"] += areas
                result["p3_aligned_words"] += aligned
                headings, stripes, new_failures = p3_golden_row_grammar(
                    page, first_page=role.startswith("P3 - Trasfondo"))
                result["p3_golden_field_title_bands"] += headings
                result["p3_golden_alternate_rows"] += stripes
                failures.extend(new_failures)
            elif role.startswith("C2 - "):
                result["c2_pages"] += 1
                cartelas, complete, failures = c2_result(page)
                result["c2_notes_cartelas"] += cartelas
                result["c2_complete_grids"] += complete
                if role.startswith("C2 - Habilidades, acciones y recursos"):
                    count, matched, context_failures = c2_context_result(page)
                    result["c2_context_labels"] += count
                    result["c2_context_blue_bands"] += matched
                    failures.extend(context_failures)
                    count, matched, skill_failures = c2_skill_checkbox_result(page)
                    result["c2_linked_skill_rows"] += count
                    result["c2_linked_skill_paper_boxes"] += matched
                    failures.extend(skill_failures)
                    count, value_failures = c2_source_skill_right_value_result(page)
                    result["c2_right_aligned_skill_totals"] += count
                    failures.extend(value_failures)
            else:
                continue
            for detail in failures[:8]:
                result["failures"].append(
                    f"{source.name}: physical page {index + 1}: {detail}"
                )
        doc.close()
    if result["p3_pages"] < minimum_pdfs:
        result["failures"].append("P305 insufficient genuine P3 page coverage")
    if result["c2_pages"] < minimum_pdfs or not result["c2_notes_cartelas"]:
        result["failures"].append("C207/C208 insufficient genuine C2 grid coverage")
    if not result["p3_aligned_words"]:
        result["failures"].append("P305 no source words checked")
    if result["c2_right_aligned_skill_totals"] < 3:
        result["failures"].append("C217 no real right-aligned skill totals")
    if result["c2_linked_skill_rows"] < 3:
        result["failures"].append("C216 insufficient actual linked skills exercised")
    if result["c2_context_labels"] < 2:
        result["failures"].append("C206 no genuine C2 source-continuation captions exercised")
    result["result"] = "PASS_LOCAL_PHYSICAL_ONLY" if not result["failures"] else "FAIL"
    return result


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--pdf-dir", type=Path, required=True)
    parser.add_argument("--minimum-pdfs", type=int, default=48)
    parser.add_argument("--report", type=Path, required=True)
    args = parser.parse_args()
    files = sorted(args.pdf_dir.glob("V11-INTERNAL-PROOF-*.pdf"))
    result = audit(files, args.minimum_pdfs)
    args.report.parent.mkdir(parents=True, exist_ok=True)
    args.report.write_text(
        json.dumps(result, ensure_ascii=False, indent=2) + "\n", encoding="utf-8"
    )
    print(json.dumps({
        key: val for key, val in result.items()
        if key not in {"pdfs"}
    }, ensure_ascii=False))
    if result["result"] == "FAIL":
        raise SystemExit(1)


if __name__ == "__main__":
    main()
