"""C2 proposal preview with Mara's real actions and resources, real Fira/Barlow fonts.

Left: ACCIONES table in the approved P1 attack-table grammar (name | type | range/effect),
notes on a 7pt line under the row. Right: RECURSOS with box tracks reusing the approved
ammunition grammar (groups of five), shown in two variants: R1 empty boxes + 'Quedan x de y',
R2 spent boxes pre-marked with an X.
"""
import json
import sys
from pathlib import Path
import fitz

repo, out = Path(sys.argv[1]), sys.argv[2]
fira = str(repo / "assets/fonts/pdf/text/FiraSans-Regular.ttf")
barlow = str(repo / "assets/fonts/pdf/text/BarlowCondensed-Bold.ttf")
c = json.loads((repo / "qa/pc-sheet/fixtures/03_mara_siete_umbrales_custom_extended.json")
               .read_text(encoding="utf-8"))["character"]
F = fitz.Font(fontfile=fira)
NAVY, LINE, HEAD = (0.31, 0.48, 0.59), (148/255, 176/255, 193/255), (217/255, 236/255, 245/255)
BAND = [(231/255, 242/255, 247/255), (240/255, 247/255, 250/255)]
TYPES = {"ACTION": "Acción", "BONUS_ACTION": "Adicional", "REACTION": "Reacción"}

doc = fitz.open()
page = doc.new_page(width=612, height=792)
page.insert_font(fontname="fira", fontfile=fira)
page.insert_font(fontname="barlow", fontfile=barlow)


def text(x, y, s, size=9, font="fira", color=(0, 0, 0)):
    page.insert_text((x, y), s, fontname=font, fontsize=size, color=color)


def wrap(s, size, width):
    lines, cur = [], ""
    for w in s.split():
        t = (cur + " " + w).strip()
        if F.text_length(t, fontsize=size) <= width:
            cur = t
        else:
            lines.append(cur); cur = w
    return lines + ([cur] if cur else [])


def cartela(x, y, w, label, right=""):
    r = fitz.Rect(x, y, x + w, y + 16)
    page.draw_rect(r, color=NAVY, fill=HEAD, width=0.55)
    text(x + 6, y + 12, label, 11, "barlow", (0.1, 0.2, 0.35))
    if right:
        text(x + w - 6 - F.text_length(right, fontsize=6) * 0.8, y + 11, right, 6, "barlow", NAVY)


def band(x, y, w, h, i):
    page.draw_rect(fitz.Rect(x, y, x + w, y + h), color=None, fill=BAND[i % 2])
    page.draw_line((x, y + h), (x + w, y + h), color=LINE, width=0.32)


text(42, 52, "Mara de los Siete Umbrales", 14, "barlow", (0.1, 0.2, 0.35))
text(42, 70, "C2 - Habilidades, acciones y recursos / 1   (VISTA PREVIA - PROPUESTA)", 11, "barlow")
page.draw_line((42, 76), (570, 76), color=NAVY, width=0.6)

# ---- left: ACCIONES -------------------------------------------------------
L, W = 42, 258
y = 92
cartela(L, y, W, "ACCIONES", "VIENE P.1")
y += 18
c1, c2 = 112, 44
page.draw_rect(fitz.Rect(L, y, L + W, y + 12), color=None, fill=HEAD)
text(L + 4, y + 9, "ACCIÓN", 7, "barlow", NAVY)
text(L + c1 + 3, y + 9, "TIPO", 7, "barlow", NAVY)
text(L + c1 + c2 + 3, y + 9, "ALCANCE / EFECTO", 7, "barlow", NAVY)
y += 12
actions = [e for e in c["combatEntries"] if e["type"] != "ATTACK"]
for i, e in enumerate(actions):
    name = wrap(e["name"], 8, c1 - 6)
    eff = wrap((e.get("rangeText") or "") + " · " + (e.get("damageEffect") or ""), 8, W - c1 - c2 - 6)
    notes = wrap("NOTAS: " + e["notes"], 6.5, W - 8) if e.get("notes") else []
    rows = max(len(name), len(eff))
    h = rows * 10 + 4 + len(notes) * 8.5
    band(L, y, W, h, i)
    for x in (L + c1, L + c1 + c2):
        page.draw_line((x, y), (x, y + rows * 10 + 4), color=LINE, width=0.32)
    for j, s in enumerate(name):
        text(L + 4, y + 10 + j * 10, s, 8)
    text(L + c1 + 3, y + 10, TYPES.get(e["type"], e["type"]), 8)
    for j, s in enumerate(eff):
        text(L + c1 + c2 + 3, y + 10 + j * 10, s, 8)
    for j, s in enumerate(notes):
        text(L + 4, y + rows * 10 + 10 + j * 8.5, s, 6.5, color=(0.25, 0.3, 0.38))
    y += h
y += 10
cartela(L, y, W, "NOTAS")
page.draw_rect(fitz.Rect(L, y + 16, L + W, 760), color=LINE, width=0.4)
gx = L
while gx < L + W:
    page.draw_line((gx, y + 16), (gx, 760), color=(0.85, 0.9, 0.93), width=0.3); gx += 16.125
gy = y + 16
while gy < 760:
    page.draw_line((L, gy), (L + W, gy), color=(0.85, 0.9, 0.93), width=0.3); gy += 16

# ---- right: RECURSOS (two variants) ---------------------------------------
R = 312
y = 92


def resources(y, variant, items):
    cartela(R, y, W, "RECURSOS" + ("  · R1: casillas vacías" if variant == 1 else "  · R2: gastados marcados"))
    y += 18
    for i, r in enumerate(items):
        meta = "RECUPERA " + r["recovery"] + " · FUENTE " + r["source"]
        notes = wrap("NOTAS: " + r["notes"], 6.5, W - 8) if r.get("notes") else []
        h = 10 + 13 + 9 + len(notes) * 8.5 + 3
        band(R, y, W, h, i)
        text(R + 4, y + 10, r["name"], 8.5)
        mx, cur = r["maxValue"], r["currentValue"]
        bx = R + 6
        for k in range(mx):
            if k and k % 5 == 0:
                bx += 4
            rect = fitz.Rect(bx, y + 14, bx + 8, y + 22)
            page.draw_rect(rect, color=NAVY, fill=(1, 1, 1), width=0.5)
            if variant == 2 and k >= cur:
                page.draw_line(rect.tl, rect.br, color=(0.1, 0.1, 0.1), width=0.7)
                page.draw_line(rect.tr, rect.bl, color=(0.1, 0.1, 0.1), width=0.7)
            bx += 10
        if variant == 1:
            text(bx + 6, y + 21, f"Quedan {cur} de {mx}", 8)
        text(R + 4, y + 31, meta, 6.5, color=(0.25, 0.3, 0.38))
        for j, s in enumerate(notes):
            text(R + 4, y + 40 + j * 8.5, s, 6.5, color=(0.25, 0.3, 0.38))
        y += h
    return y


y = resources(y, 1, c["resources"][:4])
y += 12
resources(y, 2, c["resources"][:4])
page.get_pixmap(dpi=120).save(out)
print(out)
