"""Preview P3 'HISTORIA Y CRÓNICA' row rhythm options with the real Fira Sans and Mara's real story.

Options: A current app (9pt / 12.42pt rows), B golden (7pt / 17pt rows, baseline near the row foot),
C hybrid (9pt / 17pt rows). Prints capacity estimates for one P3 history area (two columns).
"""
import json
import sys
from pathlib import Path
import fitz

repo = Path(sys.argv[1])
out = Path(sys.argv[2])
fira = str(repo / "assets/fonts/pdf/text/FiraSans-Regular.ttf")
barlow = str(repo / "assets/fonts/pdf/text/BarlowCondensed-Bold.ttf")
story = json.loads((repo / "qa/pc-sheet/fixtures/03_mara_siete_umbrales_custom_extended.json")
                   .read_text(encoding="utf-8"))["character"]["background"]["story"]
font = fitz.Font(fontfile=fira)
COL_W, GAP, INSET, AREA_H = 258.0, 12.0, 5.0, 340.0
SHADE, BASE, RULE = (231/255, 242/255, 247/255), (238/255, 247/255, 251/255), (148/255, 176/255, 193/255)
OPTIONS = [
    ("A — ACTUAL APP", 9.0, 12.42, 9.4),
    ("B — GOLDEN APROBADO", 7.0, 17.0, 13.0),
    ("C — HÍBRIDO: letra actual, renglón del golden", 9.0, 17.0, 13.0),
]


def wrap(text, size, width):
    lines, cur = [], ""
    for word in text.split():
        trial = (cur + " " + word).strip()
        if font.text_length(trial, fontsize=size) <= width:
            cur = trial
        else:
            lines.append(cur)
            cur = word
    if cur:
        lines.append(cur)
    return lines


ROWS_SHOWN = 10
doc = fitz.open()
panel_h = [24 + ROWS_SHOWN * o[2] + 40 for o in OPTIONS]
page = doc.new_page(width=612, height=sum(panel_h) + 30)
page.insert_font(fontname="fira", fontfile=fira)
page.insert_font(fontname="barlow", fontfile=barlow)
y = 20
report = []
for (title, size, pitch, base_off), ph in zip(OPTIONS, panel_h):
    lines = wrap(story, size, COL_W - 2 * INSET)
    rows_per_col = int(AREA_H // pitch)
    sample = "Creció en una ciudad de canales y aprendió a escuchar las voces que atravesaban sus puentes. " * 40
    chars_per_line = len(sample) / len(wrap(sample, size, COL_W - 2 * INSET))
    capacity = int(rows_per_col * 2 * chars_per_line)
    report.append(f"{title}: {size:g} pt sobre renglón de {pitch:g} pt; {rows_per_col} renglones por columna; "
                  f"~{capacity} caracteres caben en P3; Mara usa {len(lines)} renglones")
    page.insert_text((42, y + 10), f"{title}  ·  {size:g} pt / renglón {pitch:g} pt  ·  "
                     f"P3 admite ~{capacity} caracteres de historia", fontname="barlow", fontsize=10,
                     color=(0.1, 0.2, 0.35))
    top = y + 22
    for col in range(2):
        x = 42 + col * (COL_W + GAP)
        page.draw_rect(fitz.Rect(x, top, x + COL_W, top + ROWS_SHOWN * pitch), color=None, fill=BASE)
        for r in range(ROWS_SHOWN):
            ry = top + r * pitch
            if r % 2 == 0:
                page.draw_rect(fitz.Rect(x, ry, x + COL_W, ry + pitch), color=None, fill=SHADE)
            page.draw_line((x, ry + pitch), (x + COL_W, ry + pitch), color=RULE, width=0.4)
        col_lines = lines[col * ROWS_SHOWN:(col + 1) * ROWS_SHOWN]
        for r, text in enumerate(col_lines):
            page.insert_text((x + INSET, top + r * pitch + base_off), text, fontname="fira", fontsize=size)
    y += ph
page.get_pixmap(dpi=130).save(out)
print(out)
print("\n".join(report))
