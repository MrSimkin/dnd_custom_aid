#!/usr/bin/env python3
"""Expand Gustavo Muñoz's legacy Para Hoja de PJ symbol font without breaking A-E mappings.

Usage:
    python expand_para_hoja_de_pj_v3.py INPUT.ttf OUTPUT.ttf

Requires:
    pip install fonttools

The script leaves the input untouched, preserves the original legacy glyph mappings,
and adds a renderer-oriented Private Use Area (PUA) symbol namespace.
"""
from __future__ import annotations

import math
import sys
from pathlib import Path

from fontTools.pens.ttGlyphPen import TTGlyphPen
from fontTools.ttLib import TTFont

UPM = 2048
ADVANCE = 1904
LSB = 0

# Geometry intentionally follows the legacy A-E glyph scale: y=0..1600.
CX = 800
CY = 800
R_OUTER = 700
R_RING_INNER = 535
R_DOUBLE_MID_OUTER = 455
R_DOUBLE_MID_INNER = 335


def _polygon(pen: TTGlyphPen, pts: list[tuple[float, float]], reverse: bool = False) -> None:
    points = list(reversed(pts)) if reverse else pts
    pen.moveTo(points[0])
    for p in points[1:]:
        pen.lineTo(p)
    pen.closePath()


def _quad_circle(pen: TTGlyphPen, cx: float, cy: float, r: float, clockwise: bool = False) -> None:
    """Draw an 8-segment quadratic approximation of a circle."""
    n = 8
    angles = [2 * math.pi * i / n for i in range(n + 1)]
    if clockwise:
        angles = list(reversed(angles))
    start = (cx + r * math.cos(angles[0]), cy + r * math.sin(angles[0]))
    pen.moveTo(start)
    for a0, a1 in zip(angles[:-1], angles[1:]):
        p1 = (cx + r * math.cos(a1), cy + r * math.sin(a1))
        mid = (a0 + a1) / 2
        cdist = r / math.cos((a1 - a0) / 2)
        ctrl = (cx + cdist * math.cos(mid), cy + cdist * math.sin(mid))
        pen.qCurveTo(ctrl, p1)
    pen.closePath()


def glyph_filled_circle() -> object:
    pen = TTGlyphPen(None)
    _quad_circle(pen, CX, CY, R_OUTER)
    return pen.glyph()


def glyph_double_circle() -> object:
    pen = TTGlyphPen(None)
    _quad_circle(pen, CX, CY, R_OUTER)
    _quad_circle(pen, CX, CY, R_RING_INNER, clockwise=True)
    _quad_circle(pen, CX, CY, R_DOUBLE_MID_OUTER)
    _quad_circle(pen, CX, CY, R_DOUBLE_MID_INNER, clockwise=True)
    return pen.glyph()


CHECK_POINTS = [
    (160, 760), (365, 555), (670, 875),
    (1385, 1550), (1570, 1360), (675, 390), (45, 600),
]


def _scaled_check_points(scale_x: float, scale_y: float, dy: float) -> list[tuple[float, float]]:
    return [
        (CX + (x - CX) * scale_x, CY + (y - CY) * scale_y + dy)
        for x, y in CHECK_POINTS
    ]


def glyph_check() -> object:
    pen = TTGlyphPen(None)
    _polygon(pen, CHECK_POINTS)
    return pen.glyph()


def glyph_double_check() -> object:
    """Two stacked checks matching the app's Expertise visual grammar."""
    pen = TTGlyphPen(None)
    _polygon(pen, _scaled_check_points(0.84, 0.62, 210))
    _polygon(pen, _scaled_check_points(0.84, 0.62, -210))
    return pen.glyph()


def glyph_cross() -> object:
    pen = TTGlyphPen(None)
    _polygon(
        pen,
        [
            (215, 65), (800, 650), (1385, 65), (1535, 215),
            (950, 800), (1535, 1385), (1385, 1535), (800, 950),
            (215, 1535), (65, 1385), (650, 800), (65, 215),
        ],
    )
    return pen.glyph()


def glyph_diamond(outline: bool) -> object:
    pen = TTGlyphPen(None)
    outer = [(800, 80), (1520, 800), (800, 1520), (80, 800)]
    _polygon(pen, outer)
    if outline:
        inner = [(800, 300), (1300, 800), (800, 1300), (300, 800)]
        _polygon(pen, inner, reverse=True)
    return pen.glyph()


def glyph_ring_circle() -> object:
    pen = TTGlyphPen(None)
    _quad_circle(pen, CX, CY, R_OUTER)
    _quad_circle(pen, CX, CY, R_RING_INNER, clockwise=True)
    return pen.glyph()


NEW_GLYPHS = {
    "dcaCircleOutline": glyph_ring_circle,
    "dcaCircleFilled": glyph_filled_circle,
    "dcaCircleDouble": glyph_double_circle,
    "dcaCheck": glyph_check,
    "dcaDoubleCheck": glyph_double_check,
    "dcaCross": glyph_cross,
    "dcaDiamondOutline": lambda: glyph_diamond(True),
    "dcaDiamondFilled": lambda: glyph_diamond(False),
}

PUA_MAP = {
    0xE000: "dcaCircleOutline",
    0xE001: "dcaCircleFilled",
    0xE002: "dcaCircleDouble",
    0xE003: "B",
    0xE004: "E",
    0xE005: "dcaCheck",
    0xE006: "dcaCross",
    0xE007: "dcaCheck",
    0xE008: "dcaDiamondOutline",
    0xE009: "dcaDiamondFilled",
    0xE00A: "C",
    0xE00B: "D",
    0xE00C: "dcaDoubleCheck",
    0xE100: "dcaCircleFilled",
    0xE101: "dcaCircleDouble",
    0xE102: "B",
    0xE103: "dcaCheck",
    0xE104: "dcaCircleOutline",
    0xE105: "dcaCircleFilled",
    0xE106: "dcaCircleOutline",
    0xE107: "dcaCircleFilled",
    0xE108: "dcaCheck",
    0xE109: "dcaDoubleCheck",
}


def set_name(font: TTFont, name_id: int, value: str) -> None:
    name = font["name"]
    for rec in name.names:
        if rec.nameID == name_id:
            enc = rec.getEncoding()
            try:
                rec.string = value.encode(enc)
            except Exception:
                rec.string = (
                    value.encode("utf-16-be")
                    if rec.platformID == 3
                    else value.encode("latin-1", errors="replace")
                )


def main() -> int:
    if len(sys.argv) != 3:
        print("Usage: python expand_para_hoja_de_pj_v3.py INPUT.ttf OUTPUT.ttf", file=sys.stderr)
        return 2

    src = Path(sys.argv[1])
    dst = Path(sys.argv[2])
    font = TTFont(src)

    if font["head"].unitsPerEm != UPM:
        raise RuntimeError(
            f"Expected {UPM} UPM from the legacy font, got {font['head'].unitsPerEm}"
        )

    order = font.getGlyphOrder()
    glyf = font["glyf"]
    hmtx = font["hmtx"]

    for glyph_name, factory in NEW_GLYPHS.items():
        if glyph_name not in order:
            order.append(glyph_name)
        glyf[glyph_name] = factory()
        hmtx.metrics[glyph_name] = (ADVANCE, LSB)

    font.setGlyphOrder(order)

    unicode_tables = [t for t in font["cmap"].tables if t.isUnicode()]
    if not unicode_tables:
        raise RuntimeError("No Unicode cmap table found")

    for table in unicode_tables:
        table.cmap.update(PUA_MAP)

    set_name(font, 1, "Para Hoja de PJ Symbols v3")
    set_name(font, 2, "Regular")
    set_name(font, 4, "Para Hoja de PJ Symbols v3 Regular")
    set_name(font, 5, "Version 3.00 2026-09-18; double-check expertise + expanded renderer symbols")
    set_name(font, 6, "ParaHojadePJSymbolsV3-Regular")
    set_name(font, 16, "Para Hoja de PJ Symbols v3")
    set_name(font, 17, "Regular")

    dst.parent.mkdir(parents=True, exist_ok=True)
    font.save(dst, reorderTables=True)
    print(dst)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
