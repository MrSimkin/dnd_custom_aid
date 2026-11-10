#!/usr/bin/env python3
"""Compare the fixed parts of the eight golden PDFs with real App PDFs, page role by page role.

Golden pages hold fictional data, so rows and text cannot match a real character. What must match
on P1 (right column), P2, P3, P4 and the final NOTAS page is the fixed structure: every cartela,
card, frame and header shape, same place and inks. C2, C-N3 and the Libro depend on the data, so
they are listed for visual review, not measured here. This is a report, not a PASS.
"""
from __future__ import annotations

import argparse
import json
import zipfile
from pathlib import Path

import fitz

TOLERANCE = 0.6
FIXED = {
    # golden pdf -> (App proof, App page index per role)
    "v11_extremo_variante_A.pdf": "V11-INTERNAL-PROOF-Mara-A.pdf",
    "v11_extremo_variante_B.pdf": "V11-INTERNAL-PROOF-Mara-B.pdf",
    "v11_ordinario_6mas2_variante_A.pdf": "V11-INTERNAL-PROOF-Mara-A.pdf",
    "v11_ordinario_6mas2_variante_B.pdf": "V11-INTERNAL-PROOF-Mara-B.pdf",
    "v11_ordinario_6mas3_variante_A.pdf": "V11-INTERNAL-PROOF-Mara-A.pdf",
    "v11_ordinario_6mas3_variante_B.pdf": "V11-INTERNAL-PROOF-Mara-B.pdf",
    "v11_personaje_casi_vacio.pdf": "V11-INTERNAL-PROOF-C379-empty-sheet-A.pdf",
    "v11_comparador_C2_extremo.pdf": "V11-INTERNAL-PROOF-Mara-A.pdf",
}


def hex_ink(colour):
    return "-" if colour is None else "#%02x%02x%02x" % tuple(round(v * 255) for v in colour)


def shapes(page):
    return [(p["rect"].x0, 792 - p["rect"].y1, p["rect"].x1, 792 - p["rect"].y0,
             hex_ink(p.get("color")), hex_ink(p.get("fill"))) for p in page.get_drawings()]


def role_of(page, pdf_page_count, index):
    """The page role from the words that both golden and App pages print."""
    text = page.get_text()
    if index == pdf_page_count - 1 and "RASGOS Y ATRIBUTOS" not in text and "EQUIPO" not in text:
        return "NOTAS final"
    if index == 0 and "COMBATE Y SUPERVIVENCIA" in text:
        return "P1"
    if "TRASFONDO Y PERFIL NARRATIVO" in text:
        return "P3"
    if "REFERENCIA MÁGICA" in text:
        return "P4"
    if "EQUIPO ESPECIAL" in text and "RASGOS Y ATRIBUTOS" in text:
        return "P2"
    if "NOTAS C-N3" in text:
        return "C-N3"
    if "LIBRO DE CONJUROS" in text:
        return "Libro"
    return "C2"


def is_structure(shape, role):
    x0, y0, x1, y1, stroke, fill = shape
    height, width = y1 - y0, x1 - x0
    if x1 < 155 and y0 > 715:  # the logo stand-in the owner replaced
        return False
    if (width < 15 and height < 15) and (x1 < 53 or x0 > 559) and (y0 > 738 or y1 < 53):
        return False  # corner ticks are one path in the App
    if role == "P1" and x1 < 262:  # left column depends on how many attributes print
        return False
    # Cartelas, cards and long frame or header rules: what does not move with the data.
    return (stroke == "#264b68" and fill in ("#e7f2f7", "#ffffff") and height > 19) or \
           (stroke in ("#264b68", "#527c96", "#bfd3de") and fill == "-" and width > 300)


def covered(shape, others):
    return any(shape[4:] == o[4:] and o[0] - TOLERANCE <= shape[0] and o[1] - TOLERANCE <= shape[1]
               and o[2] + TOLERANCE >= shape[2] and o[3] + TOLERANCE >= shape[3] for o in others)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--golden-zip", type=Path, required=True)
    parser.add_argument("--pdf-dir", type=Path, required=True)
    parser.add_argument("--report", type=Path, required=True)
    args = parser.parse_args()
    report = {}
    with zipfile.ZipFile(args.golden_zip) as archive:
        members = {Path(n).name: n for n in archive.namelist() if n.endswith(".pdf")}
        for name, proof in FIXED.items():
            golden = fitz.open(stream=archive.read(members[name]), filetype="pdf")
            app = fitz.open(args.pdf_dir / proof)
            app_roles = {}
            for index, page in enumerate(app):
                app_roles.setdefault(role_of(page, app.page_count, index), page)
            pages = []
            for index, page in enumerate(golden):
                role = role_of(page, golden.page_count, index)
                row = {"page": index + 1, "role": role}
                if role in ("C2", "C-N3", "Libro") or role not in app_roles:
                    row["result"] = "revisión visual" if role in ("C2", "C-N3", "Libro") else "sin página en la App"
                else:
                    twin = shapes(app_roles[role])
                    fixed = [s for s in shapes(page) if is_structure(s, role)]
                    missing = [[round(v, 2) if isinstance(v, float) else v for v in s]
                               for s in fixed if not covered(s, twin)]
                    row.update(result="coincide" if not missing else "difiere",
                               fixed_shapes=len(fixed), missing=missing)
                pages.append(row)
            report[name] = {"app_proof": proof, "pages": pages}
    args.report.write_text(json.dumps(report, ensure_ascii=False, indent=1), encoding="utf-8")
    for name, data in report.items():
        print(name, data["app_proof"])
        for row in data["pages"]:
            print(f"  p{row['page']} {row['role']}: {row['result']}",
                  f"({row.get('fixed_shapes')} formas, faltan {len(row['missing'])})" if "missing" in row else "")
            for shape in row.get("missing", [])[:4]:
                print("     ", shape)


if __name__ == "__main__":
    main()
