#!/usr/bin/env python3
from __future__ import annotations

import argparse
import copy
import hashlib
import math
from pathlib import Path
from typing import Callable, Iterable

from fontTools.fontBuilder import FontBuilder
from fontTools.pens.ttGlyphPen import TTGlyphPen
from fontTools.pens.transformPen import TransformPen
from fontTools.ttLib import TTFont

UPM = 2048
ADVANCE = 1904
LSB = 0
CX = 800
CY = 800
V1_SHA256 = "d0c0d50ad8ed33064e3af89b0976933b9eba8ead2d26234011c1a3233c246658"
FIXED_TIMESTAMP = 3872534400  # 2026-09-18 00:00:00 UTC in Mac epoch seconds.
EXPECTED_TTF_SHA256 = "5e99861a9e9774c05da253b1bf0963eacd95ae50cef19ac5ffd2ba4a8362b588"


def sha256(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def polygon(pen: TTGlyphPen, pts: Iterable[tuple[float, float]], reverse: bool = False) -> None:
    p = list(pts)
    if reverse:
        p.reverse()
    pen.moveTo(p[0])
    for pt in p[1:]:
        pen.lineTo(pt)
    pen.closePath()


def quadratic_ellipse(pen: TTGlyphPen, cx: float, cy: float, rx: float, ry: float, clockwise: bool = False) -> None:
    # Eight-segment quadratic ellipse, generalized from the actual v3 circle generator.
    n = 8
    angles = [2 * math.pi * i / n for i in range(n + 1)]
    if clockwise:
        angles = list(reversed(angles))
    a0 = angles[0]
    pen.moveTo((cx + rx * math.cos(a0), cy + ry * math.sin(a0)))
    for a0, a1 in zip(angles[:-1], angles[1:]):
        p1 = (cx + rx * math.cos(a1), cy + ry * math.sin(a1))
        mid = (a0 + a1) / 2
        half = (a1 - a0) / 2
        ctrl = (
            cx + (rx / math.cos(half)) * math.cos(mid),
            cy + (ry / math.cos(half)) * math.sin(mid),
        )
        pen.qCurveTo(ctrl, p1)
    pen.closePath()


def ellipse_glyph(rx: int, ry: int, stroke: int | None = None, cx: int = CX, cy: int = CY):
    pen = TTGlyphPen(None)
    quadratic_ellipse(pen, cx, cy, rx, ry)
    if stroke is not None:
        quadratic_ellipse(pen, cx, cy, rx - stroke, ry - stroke, clockwise=True)
    return pen.glyph()


def rect_glyph(x0: int, y0: int, x1: int, y1: int, stroke: int | None = None):
    pen = TTGlyphPen(None)
    outer = [(x0, y0), (x1, y0), (x1, y1), (x0, y1)]
    polygon(pen, outer)
    if stroke is not None:
        inner = [(x0 + stroke, y0 + stroke), (x1 - stroke, y0 + stroke), (x1 - stroke, y1 - stroke), (x0 + stroke, y1 - stroke)]
        polygon(pen, inner, reverse=True)
    return pen.glyph()


def scaled_polygon(points: list[tuple[float, float]], factor: float, cx: float = CX, cy: float = CY) -> list[tuple[float, float]]:
    return [(cx + (x - cx) * factor, cy + (y - cy) * factor) for x, y in points]


def polygon_glyph(points: list[tuple[float, float]], outline_factor: float | None = None):
    pen = TTGlyphPen(None)
    polygon(pen, points)
    if outline_factor is not None:
        polygon(pen, scaled_polygon(points, outline_factor), reverse=True)
    return pen.glyph()


def regular_polygon(n: int, radius: float, rotation_deg: float = -90, cx: float = CX, cy: float = CY) -> list[tuple[float, float]]:
    return [
        (cx + radius * math.cos(math.radians(rotation_deg + i * 360 / n)),
         cy + radius * math.sin(math.radians(rotation_deg + i * 360 / n)))
        for i in range(n)
    ]


def star_points(outer: float, inner: float, rotation_deg: float = 90) -> list[tuple[float, float]]:
    pts = []
    for i in range(10):
        r = outer if i % 2 == 0 else inner
        a = math.radians(rotation_deg + i * 36)
        pts.append((CX + r * math.cos(a), CY + r * math.sin(a)))
    return pts


def contours_glyph(contours: list[tuple[list[tuple[float, float]], bool]]):
    pen = TTGlyphPen(None)
    for pts, reverse in contours:
        polygon(pen, pts, reverse=reverse)
    return pen.glyph()


def canonical_check_points(style: str = "v3") -> list[tuple[float, float]]:
    """A conventional ✓ silhouette: short descending arm, long ascending arm, clear elbow."""
    # Coordinates are intentionally close to the familiar U+2713 silhouette rather than
    # the elongated v3 draft that read like a blade/slash in owner QA.
    if style == "v1":
        return [(220, 820), (360, 680), (610, 430), (1430, 1370), (1580, 1230), (620, 220), (80, 680)]
    return [(250, 800), (385, 665), (625, 430), (1405, 1335), (1540, 1200), (635, 250), (110, 660)]


def transform_points(points: list[tuple[float, float]], sx: float = 1.0, sy: float = 1.0, dx: float = 0.0, dy: float = 0.0):
    return [(CX + (x - CX) * sx + dx, CY + (y - CY) * sy + dy) for x, y in points]


def check_glyph(style: str, double: bool = False, boxed: bool = False):
    base = canonical_check_points(style)
    contours: list[tuple[list[tuple[float, float]], bool]] = []
    if boxed:
        outer = [(70, 70), (1530, 70), (1530, 1530), (70, 1530)]
        inner = [(250, 250), (1350, 250), (1350, 1350), (250, 1350)] if style == "v1" else [(215, 215), (1385, 215), (1385, 1385), (215, 1385)]
        contours += [(outer, False), (inner, True)]
    if double:
        # Two individually normal checks, stacked vertically as in the Android app grammar.
        # Horizontal compression is modest so each still reads as ✓ rather than a slash.
        sx = 0.66 if boxed else 0.72
        sy = 0.39 if boxed else 0.42
        contours.append((transform_points(base, sx, sy, 0, 330), False))
        contours.append((transform_points(base, sx, sy, 0, -330), False))
    else:
        sx = 0.70 if boxed else 0.90
        sy = 0.70 if boxed else 0.90
        contours.append((transform_points(base, sx, sy, 0, 0), False))
    return contours_glyph(contours)


def merge_glyphs(base_glyph, mark_glyph, scale: float = 0.55, dx: float = 0.0, dy: float = 0.0):
    """Compose an outline container and a centered mark into one simple glyph."""
    pen = TTGlyphPen(None)
    base_glyph.draw(pen, None)
    t = (scale, 0, 0, scale, CX * (1 - scale) + dx, CY * (1 - scale) + dy)
    mark_glyph.draw(TransformPen(pen, t), None)
    return pen.glyph()


def asterisk_glyph():
    pen = TTGlyphPen(None)
    # Three intersecting bars, sized to work both as a normal '*' and an internal mark.
    polygon(pen, [(715, 260), (885, 260), (885, 690), (1280, 460), (1365, 610), (970, 840), (1365, 1068), (1280, 1215), (885, 988), (885, 1340), (715, 1340), (715, 988), (320, 1215), (235, 1068), (630, 840), (235, 610), (320, 460), (715, 690)])
    return pen.glyph()


def digit_glyph(digit: str):
    """Geometric, ordinary-readable numerals for labels and counters."""
    segs = {
        'a': (360, 1370, 1240, 1510), 'b': (1190, 820, 1330, 1420),
        'c': (1190, 180, 1330, 780), 'd': (360, 90, 1240, 230),
        'e': (270, 180, 410, 780), 'f': (270, 820, 410, 1420),
        'g': (360, 730, 1240, 870),
    }
    mapping = {
        '0': 'ab cdef'.replace(' ', ''), '1': 'bc', '2': 'abged'.replace(' ', ''),
        '3': 'abgcd', '4': 'fgbc', '5': 'afgcd', '6': 'afgecd',
        '7': 'abc', '8': 'abcdefg', '9': 'abfgcd',
    }
    pen = TTGlyphPen(None)
    for key in mapping[digit]:
        x0,y0,x1,y1 = segs[key]
        polygon(pen, [(x0,y0),(x1,y0),(x1,y1),(x0,y1)])
    return pen.glyph()


def punctuation_glyph(kind: str):
    pen = TTGlyphPen(None)
    def rect(x0,y0,x1,y1): polygon(pen,[(x0,y0),(x1,y0),(x1,y1),(x0,y1)])
    def dot(cx,cy,r=105): quadratic_ellipse(pen,cx,cy,r,r)
    if kind == 'underscore': rect(210,0,1390,115)
    elif kind == 'backslash': polygon(pen,[(470,1600),(690,1600),(1130,0),(910,0)])
    elif kind == 'comma':
        dot(800,260,110); polygon(pen,[(760,230),(900,230),(760,-80),(620,-80)])
    elif kind == 'semicolon':
        dot(800,1050,95); dot(800,350,100); polygon(pen,[(760,320),(895,320),(760,20),(630,20)])
    elif kind == 'exclam': rect(720,430,880,1450); dot(800,190,105)
    elif kind == 'question':
        # block-sans question mark with open lower stem
        rect(470,1320,1130,1460); rect(1040,930,1180,1390); rect(760,790,1120,930); rect(690,480,830,850); dot(760,190,105)
    elif kind == 'hash':
        rect(500,240,640,1360); rect(960,240,1100,1360); rect(290,610,1310,750); rect(290,990,1310,1130)
    elif kind == 'percent':
        quadratic_ellipse(pen,470,1160,190,190); quadratic_ellipse(pen,470,1160,85,85,clockwise=True)
        quadratic_ellipse(pen,1130,440,190,190); quadratic_ellipse(pen,1130,440,85,85,clockwise=True)
        polygon(pen,[(470,100),(620,100),(1130,1500),(980,1500)])
    elif kind == 'parenleft':
        polygon(pen,[(810,1510),(960,1420),(720,1120),(650,800),(720,480),(960,180),(810,90),(560,400),(470,800),(560,1200)])
    elif kind == 'parenright':
        polygon(pen,[(790,1510),(640,1420),(880,1120),(950,800),(880,480),(640,180),(790,90),(1040,400),(1130,800),(1040,1200)])
    elif kind == 'bracketleft': rect(520,120,680,1480); rect(520,1320,1030,1480); rect(520,120,1030,280)
    elif kind == 'bracketright': rect(920,120,1080,1480); rect(570,1320,1080,1480); rect(570,120,1080,280)
    elif kind == 'braceleft':
        rect(650,190,790,650); rect(650,950,790,1410); rect(510,730,790,870); rect(650,1340,960,1480); rect(650,120,960,260)
    elif kind == 'braceright':
        rect(810,190,950,650); rect(810,950,950,1410); rect(810,730,1090,870); rect(640,1340,950,1480); rect(640,120,950,260)
    elif kind == 'less': polygon(pen,[(1180,1330),(420,800),(1180,270),(1280,430),(750,800),(1280,1170)])
    elif kind == 'greater': polygon(pen,[(420,1330),(1180,800),(420,270),(320,430),(850,800),(320,1170)])
    elif kind == 'quote': rect(560,1120,690,1440); rect(910,1120,1040,1440)
    elif kind == 'apostrophe': rect(735,1120,865,1440)
    elif kind == 'caret': polygon(pen,[(330,690),(800,1240),(1270,690),(1130,570),(800,960),(470,570)])
    elif kind == 'tilde':
        polygon(pen,[(250,720),(470,930),(720,930),(980,690),(1160,690),(1350,870),(1350,1040),(1130,840),(990,840),(730,1080),(430,1080),(250,900)])
    elif kind == 'at':
        quadratic_ellipse(pen,800,800,650,650); quadratic_ellipse(pen,800,800,510,510,clockwise=True)
        quadratic_ellipse(pen,800,800,250,250); quadratic_ellipse(pen,800,800,120,120,clockwise=True)
        rect(1030,520,1170,1000); rect(1030,520,1320,660)
    return pen.glyph()

def cross_glyph(style: str):
    if style == "v1":
        pts = [(120, 0), (800, 680), (1480, 0), (1600, 120), (920, 800), (1600, 1480), (1480, 1600), (800, 920), (120, 1600), (0, 1480), (680, 800), (0, 120)]
    else:
        pts = [(215, 65), (800, 650), (1385, 65), (1535, 215), (950, 800), (1535, 1385), (1385, 1535), (800, 950), (215, 1535), (65, 1385), (650, 800), (65, 215)]
    return polygon_glyph(pts)


def target_glyph(style: str, bullseye: bool = False):
    pen = TTGlyphPen(None)
    stroke = 200 if style == "v1" else 150
    outer = 700 if style == "v1" else 650
    quadratic_ellipse(pen, CX, CY, outer, outer)
    quadratic_ellipse(pen, CX, CY, outer - stroke, outer - stroke, clockwise=True)
    if bullseye:
        quadratic_ellipse(pen, CX, CY, 430, 430)
        quadratic_ellipse(pen, CX, CY, 300, 300, clockwise=True)
        quadratic_ellipse(pen, CX, CY, 115, 115)
    else:
        quadratic_ellipse(pen, CX, CY, 130, 130)
    return pen.glyph()


def heart_points() -> list[tuple[float, float]]:
    pts=[]
    for i in range(64):
        t=2*math.pi*i/64
        x=16*math.sin(t)**3
        y=13*math.cos(t)-5*math.cos(2*t)-2*math.cos(3*t)-math.cos(4*t)
        pts.append((CX + x*42, 720 + y*45))
    return pts


def shield_points() -> list[tuple[float, float]]:
    return [(180,1450),(800,1600),(1420,1450),(1350,710),(1120,280),(800,0),(480,280),(250,710)]


def flame_points() -> list[tuple[float,float]]:
    return [(800,1600),(1110,1200),(1030,920),(1350,1140),(1450,760),(1280,350),(800,0),(320,350),(150,760),(300,1100),(520,880),(500,1260)]


def bolt_points() -> list[tuple[float,float]]:
    return [(860,1600),(360,850),(690,850),(470,0),(1260,950),(900,950),(1190,1600)]


def drop_points() -> list[tuple[float,float]]:
    return [(800,1600),(1180,1120),(1380,720),(1300,330),(1030,70),(800,0),(570,70),(300,330),(220,720),(420,1120)]


def eye_glyph(style: str, filled: bool):
    pen=TTGlyphPen(None)
    outer=[(80,800),(360,1180),(800,1320),(1240,1180),(1520,800),(1240,420),(800,280),(360,420)]
    if style == 'v3': outer = scaled_polygon(outer, 0.92)
    polygon(pen,outer)
    if not filled:
        inner=scaled_polygon(outer,0.72)
        polygon(pen,inner,reverse=True)
        quadratic_ellipse(pen,CX,CY,150,150)
    return pen.glyph()


def hourglass_points() -> list[tuple[float,float]]:
    return [(250,1500),(1350,1500),(1120,1180),(900,900),(900,700),(1120,420),(1350,100),(250,100),(480,420),(700,700),(700,900),(480,1180)]


def book_glyph(style: str, filled: bool):
    pen=TTGlyphPen(None)
    left=[(120,1450),(760,1320),(760,180),(120,310)]
    right=[(840,1320),(1480,1450),(1480,310),(840,180)]
    if style == 'v3':
        left = scaled_polygon(left, 0.92); right = scaled_polygon(right, 0.92)
    polygon(pen,left); polygon(pen,right)
    if not filled:
        polygon(pen,scaled_polygon(left,0.72,440,800),reverse=True)
        polygon(pen,scaled_polygon(right,0.72,1160,800),reverse=True)
    return pen.glyph()


def icon_pair(points_fn: Callable[[], list[tuple[float,float]]], style: str):
    pts=points_fn()
    if style == 'v3': pts = scaled_polygon(pts, 0.92)
    factor=0.70 if style == "v1" else 0.78
    return polygon_glyph(pts,factor), polygon_glyph(pts)


def standard_line_glyph(kind: str):
    pen=TTGlyphPen(None)
    if kind=="plus":
        polygon(pen,[(690,250),(910,250),(910,690),(1350,690),(1350,910),(910,910),(910,1350),(690,1350),(690,910),(250,910),(250,690),(690,690)])
    elif kind=="minus": polygon(pen,[(250,690),(1350,690),(1350,910),(250,910)])
    elif kind=="equals":
        polygon(pen,[(250,490),(1350,490),(1350,670),(250,670)])
        polygon(pen,[(250,930),(1350,930),(1350,1110),(250,1110)])
    elif kind=="slash": polygon(pen,[(470,0),(690,0),(1130,1600),(910,1600)])
    elif kind=="bar": polygon(pen,[(690,0),(910,0),(910,1600),(690,1600)])
    elif kind=="dot": quadratic_ellipse(pen,800,260,120,120)
    elif kind=="colon":
        quadratic_ellipse(pen,800,500,100,100); quadratic_ellipse(pen,800,1100,100,100)
    return pen.glyph()


def build(v1_path: Path, out: Path) -> None:
    if sha256(v1_path) != V1_SHA256:
        raise RuntimeError("v1 source SHA-256 mismatch")
    src=TTFont(v1_path)
    if src['head'].unitsPerEm != UPM:
        raise RuntimeError("unexpected v1 unitsPerEm")

    glyphs={}
    metrics={}
    order=['.notdef','space']
    glyphs['.notdef']=TTGlyphPen(None).glyph(); glyphs['space']=TTGlyphPen(None).glyph()
    metrics['.notdef']=(ADVANCE,0); metrics['space']=(900,0)

    # Exact v1 historical A-E geometry and metrics, copied from the authoritative font.
    historical={
        'legacy_v1_circle_outline':'A', 'legacy_v1_square_outline':'B',
        'legacy_v1_oval_historical_outline':'C','legacy_v1_oval_historical_filled':'D',
        'legacy_v1_square_filled':'E',
    }
    for new,old in historical.items():
        glyphs[new]=copy.deepcopy(src['glyf'][old]); metrics[new]=tuple(src['hmtx'][old]); order.append(new)

    def add(name,glyph,adv=ADVANCE,lsb=0):
        glyphs[name]=glyph; metrics[name]=(adv,lsb); order.append(name)

    # v1-derived pairable core; stroke language follows the historical 200-unit outlines.
    add('v1_circle_filled', ellipse_glyph(800,800,None))
    add('v1_circle_double', target_glyph('v1',False))
    add('v1_uniform_oval_outline', ellipse_glyph(560,800,200))
    add('v1_uniform_oval_filled', ellipse_glyph(560,800,None))
    for base,n,rot in [('diamond',4,0),('triangle',3,90),('hexagon',6,0)]:
        pts=regular_polygon(n,760 if base!='hexagon' else 740,rot)
        add(f'v1_{base}_outline',polygon_glyph(pts,0.73)); add(f'v1_{base}_filled',polygon_glyph(pts))
    sp=star_points(760,340); add('v1_star_outline',polygon_glyph(sp,0.68)); add('v1_star_filled',polygon_glyph(sp))
    add('v1_pip_outline',ellipse_glyph(245,245,105)); add('v1_pip_filled',ellipse_glyph(245,245,None))
    add('v1_check',check_glyph('v1')); add('v1_double_check_stacked',check_glyph('v1',double=True))
    add('v1_boxed_check',check_glyph('v1',boxed=True)); add('v1_boxed_double_check_stacked',check_glyph('v1',double=True,boxed=True))
    add('v1_cross',cross_glyph('v1'))

    # v3-derived geometric model. Circle/check/diamond dimensions derive from the actual v3 generator.
    add('v3_circle_outline',ellipse_glyph(700,700,165)); add('v3_circle_filled',ellipse_glyph(700,700,None))
    pen=TTGlyphPen(None); quadratic_ellipse(pen,CX,CY,700,700); quadratic_ellipse(pen,CX,CY,535,535,True); quadratic_ellipse(pen,CX,CY,455,455); quadratic_ellipse(pen,CX,CY,335,335,True); add('v3_circle_double',pen.glyph())
    add('v3_square_outline',rect_glyph(100,100,1500,1500,165)); add('v3_square_filled',rect_glyph(100,100,1500,1500,None))
    add('v3_oval_outline',ellipse_glyph(560,760,165)); add('v3_oval_filled',ellipse_glyph(560,760,None))
    for base,n,rot in [('diamond',4,0),('triangle',3,90),('hexagon',6,0)]:
        pts=regular_polygon(n,720 if base!='hexagon' else 700,rot)
        add(f'v3_{base}_outline',polygon_glyph(pts,0.78)); add(f'v3_{base}_filled',polygon_glyph(pts))
    sp=star_points(720,310); add('v3_star_outline',polygon_glyph(sp,0.75)); add('v3_star_filled',polygon_glyph(sp))
    add('v3_pip_outline',ellipse_glyph(220,220,80)); add('v3_pip_filled',ellipse_glyph(220,220,None))
    add('v3_check',check_glyph('v3')); add('v3_double_check_stacked',check_glyph('v3',double=True))
    add('v3_boxed_check',check_glyph('v3',boxed=True)); add('v3_boxed_double_check_stacked',check_glyph('v3',double=True,boxed=True))
    add('v3_cross',cross_glyph('v3'))

    # Extended useful sheet icon vocabulary in both design languages.
    for style in ('v1','v3'):
        for nm,fn in [('heart',heart_points),('shield',shield_points),('flame',flame_points),('bolt',bolt_points),('drop',drop_points),('hourglass',hourglass_points)]:
            outline,filled=icon_pair(fn,style); add(f'{style}_{nm}_outline',outline); add(f'{style}_{nm}_filled',filled)
        add(f'{style}_eye_outline',eye_glyph(style,False)); add(f'{style}_eye_filled',eye_glyph(style,True))
        add(f'{style}_book_outline',book_glyph(style,False)); add(f'{style}_book_filled',book_glyph(style,True))
        add(f'{style}_target',target_glyph(style,False)); add(f'{style}_bullseye',target_glyph(style,True))

    for nm in ('plus','minus','equals','slash','bar','dot','colon'):
        add(f'std_{nm}',standard_line_glyph(nm))
    add('std_backslash', punctuation_glyph('backslash'))
    add('std_underscore', punctuation_glyph('underscore'))
    add('std_asterisk', asterisk_glyph())
    for d in '0123456789': add(f'std_digit_{d}', digit_glyph(d), adv=1500)
    for nm in ('comma','semicolon','exclam','question','hash','percent','parenleft','parenright','bracketleft','bracketright','braceleft','braceright','less','greater','quote','apostrophe','caret','tilde','at'):
        add(f'std_{nm}', punctuation_glyph(nm), adv=1500)

    # Precomposed marked variants for fillable/outline shapes.  These are intentionally
    # actual glyphs rather than relying on PDF overprinting, so small-sheet rendering is stable.
    mark_sources = {
        'slash': glyphs['std_slash'], 'backslash': glyphs['std_backslash'],
        'x': glyphs['v3_cross'], 'asterisk': glyphs['std_asterisk'],
        'plus': glyphs['std_plus'], 'minus': glyphs['std_minus'],
        'check': glyphs['v3_check'], 'dot': glyphs['std_dot'],
    }
    mark_scale = {'slash':0.48,'backslash':0.48,'x':0.42,'asterisk':0.38,'plus':0.40,'minus':0.46,'check':0.46,'dot':0.55}
    v1_containers = [
        ('circle','legacy_v1_circle_outline'), ('square','legacy_v1_square_outline'),
        ('oval_historical','legacy_v1_oval_historical_outline'), ('oval_uniform','v1_uniform_oval_outline'),
        ('diamond','v1_diamond_outline'), ('triangle','v1_triangle_outline'), ('hexagon','v1_hexagon_outline'),
        ('star','v1_star_outline'), ('heart','v1_heart_outline'), ('shield','v1_shield_outline'),
        ('drop','v1_drop_outline'),
    ]
    v3_containers = [
        ('circle','v3_circle_outline'), ('square','v3_square_outline'), ('oval','v3_oval_outline'),
        ('diamond','v3_diamond_outline'), ('triangle','v3_triangle_outline'), ('hexagon','v3_hexagon_outline'),
        ('star','v3_star_outline'), ('heart','v3_heart_outline'), ('shield','v3_shield_outline'),
        ('drop','v3_drop_outline'),
    ]
    mark_names = list(mark_sources)
    for style, containers in (('v1',v1_containers),('v3',v3_containers)):
        for shape, base_name in containers:
            # Narrow/complex containers need slightly smaller internal marks.
            container_factor = (
                0.72 if 'oval' in shape else
                0.68 if shape == 'heart' else
                0.70 if shape == 'star' else
                0.72 if shape == 'triangle' else
                0.72 if shape == 'drop' else
                0.82 if shape == 'shield' else
                1.0
            )
            for mark_name in mark_names:
                name=f'{style}_{shape}_marked_{mark_name}'
                add(name, merge_glyphs(glyphs[base_name], mark_sources[mark_name], mark_scale[mark_name]*container_factor))

    cmap={0x20:'space'}
    # Historical exact aliases.
    for ch in 'Aa': cmap[ord(ch)]='legacy_v1_circle_outline'
    for ch in 'Bb': cmap[ord(ch)]='legacy_v1_square_outline'
    for ch in 'Cc': cmap[ord(ch)]='legacy_v1_oval_historical_outline'
    for ch in 'Dd': cmap[ord(ch)]='legacy_v1_oval_historical_filled'
    for ch in 'Ee': cmap[ord(ch)]='legacy_v1_square_filled'
    # v1-style convenient keyboard pairs.
    pairs=[('f','F','legacy_v1_circle_outline','v1_circle_filled'),('g','G','legacy_v1_square_outline','legacy_v1_square_filled'),('h','H','v1_uniform_oval_outline','v1_uniform_oval_filled'),('i','I','v1_diamond_outline','v1_diamond_filled'),('j','J','v1_triangle_outline','v1_triangle_filled'),('k','K','v1_hexagon_outline','v1_hexagon_filled'),('l','L','v1_star_outline','v1_star_filled'),('m','M','v1_pip_outline','v1_pip_filled')]
    for lo,up,a,b in pairs: cmap[ord(lo)]=a; cmap[ord(up)]=b
    cmap[ord('v')]='v1_check'; cmap[ord('V')]='v1_double_check_stacked'; cmap[ord('x')]='v1_boxed_check'; cmap[ord('X')]='v1_boxed_double_check_stacked'; cmap[ord('z')]='v1_cross'
    # v3-style convenient keyboard pairs; n/o/p reproduce A/B-C-D-E concepts in v3 language.
    pairs=[('n','N','v3_circle_outline','v3_circle_filled'),('o','O','v3_square_outline','v3_square_filled'),('p','P','v3_oval_outline','v3_oval_filled'),('q','Q','v3_diamond_outline','v3_diamond_filled'),('r','R','v3_triangle_outline','v3_triangle_filled'),('s','S','v3_hexagon_outline','v3_hexagon_filled'),('t','T','v3_star_outline','v3_star_filled'),('u','U','v3_pip_outline','v3_pip_filled')]
    for lo,up,a,b in pairs: cmap[ord(lo)]=a; cmap[ord(up)]=b
    cmap[ord('w')]='v3_check'; cmap[ord('W')]='v3_double_check_stacked'; cmap[ord('y')]='v3_boxed_check'; cmap[ord('Y')]='v3_boxed_double_check_stacked'; cmap[ord('Z')]='v3_cross'

    # Ordinary ASCII numerals and punctuation useful in sheet labels, modifiers and counters.
    for d in '0123456789': cmap[ord(d)] = f'std_digit_{d}'
    ascii_map={
        '+':'std_plus','-':'std_minus','_':'std_underscore','=':'std_equals','/':'std_slash','\\':'std_backslash','|':'std_bar',
        '.':'std_dot',',':'std_comma',':':'std_colon',';':'std_semicolon','*':'std_asterisk','!':'std_exclam','?':'std_question',
        '#':'std_hash','%':'std_percent','(':'std_parenleft',')':'std_parenright','[':'std_bracketleft',']':'std_bracketright',
        '{':'std_braceleft','}':'std_braceright','<':'std_less','>':'std_greater','"':'std_quote',"'":'std_apostrophe',
        '^':'std_caret','~':'std_tilde','@':'std_at',
    }
    for ch,nm in ascii_map.items(): cmap[ord(ch)]=nm
    unicode_aliases={0x25CB:'v3_circle_outline',0x25CF:'v3_circle_filled',0x25A1:'v3_square_outline',0x25A0:'v3_square_filled',0x25C7:'v3_diamond_outline',0x25C6:'v3_diamond_filled',0x25B3:'v3_triangle_outline',0x25B2:'v3_triangle_filled',0x2606:'v3_star_outline',0x2605:'v3_star_filled',0x2713:'v3_check',0x2715:'v3_cross',0x2661:'v3_heart_outline',0x2665:'v3_heart_filled',0x25CE:'v3_circle_double',0x2299:'v3_bullseye',0x26A1:'v3_bolt_filled'}
    cmap.update(unicode_aliases)

    concepts=['circle_outline','circle_filled','circle_double','square_outline','square_filled','oval_outline','oval_filled','diamond_outline','diamond_filled','triangle_outline','triangle_filled','hexagon_outline','hexagon_filled','star_outline','star_filled','pip_outline','pip_filled','check','double_check_stacked','boxed_check','boxed_double_check_stacked','cross','heart_outline','heart_filled','shield_outline','shield_filled','flame_outline','flame_filled','bolt_outline','bolt_filled','drop_outline','drop_filled','eye_outline','eye_filled','hourglass_outline','hourglass_filled','book_outline','book_filled','target','bullseye']
    def resolve(style,concept):
        if style=='v1':
            special={'circle_outline':'legacy_v1_circle_outline','square_outline':'legacy_v1_square_outline','square_filled':'legacy_v1_square_filled','oval_outline':'legacy_v1_oval_historical_outline','oval_filled':'legacy_v1_oval_historical_filled'}
            return special.get(concept,f'v1_{concept}')
        return f'v3_{concept}'
    for idx,concept in enumerate(concepts):
        cmap[0xE200+idx]=resolve('v1',concept)
        cmap[0xE300+idx]=resolve('v3',concept)

    # Precomposed marked-container PUA ranges.
    # E400+ = v1-derived; E500+ = v3-derived; E600+ = generic renderer (v3-derived).
    # Ordering is shape-major, then marks: slash, backslash, x, asterisk, plus, minus, check, dot.
    for shape_idx,(shape,_) in enumerate(v1_containers):
        for mark_idx,mark_name in enumerate(mark_names):
            cmap[0xE400 + shape_idx*8 + mark_idx] = f'v1_{shape}_marked_{mark_name}'
    for shape_idx,(shape,_) in enumerate(v3_containers):
        for mark_idx,mark_name in enumerate(mark_names):
            gname=f'v3_{shape}_marked_{mark_name}'
            cmap[0xE500 + shape_idx*8 + mark_idx] = gname
            cmap[0xE600 + shape_idx*8 + mark_idx] = gname

    # Current generic renderer namespace uses v3 geometric forms.
    generic={
        0xE000:'v3_circle_outline',0xE001:'v3_circle_filled',0xE002:'v3_circle_double',0xE003:'v3_square_outline',0xE004:'v3_square_filled',0xE005:'v3_check',0xE006:'v3_cross',0xE007:'v3_boxed_check',0xE008:'v3_diamond_outline',0xE009:'v3_diamond_filled',0xE00A:'v3_oval_outline',0xE00B:'v3_oval_filled',0xE00C:'v3_double_check_stacked',0xE00D:'v3_boxed_double_check_stacked',0xE00E:'v3_triangle_outline',0xE00F:'v3_triangle_filled',0xE010:'v3_hexagon_outline',0xE011:'v3_hexagon_filled',0xE012:'v3_star_outline',0xE013:'v3_star_filled',0xE014:'v3_pip_outline',0xE015:'v3_pip_filled',0xE016:'v3_heart_outline',0xE017:'v3_heart_filled',0xE018:'v3_shield_outline',0xE019:'v3_shield_filled',0xE01A:'v3_flame_outline',0xE01B:'v3_flame_filled',0xE01C:'v3_bolt_outline',0xE01D:'v3_bolt_filled',0xE01E:'v3_drop_outline',0xE01F:'v3_drop_filled',0xE020:'v3_eye_outline',0xE021:'v3_eye_filled',0xE022:'v3_hourglass_outline',0xE023:'v3_hourglass_filled',0xE024:'v3_book_outline',0xE025:'v3_book_filled',0xE026:'v3_target',0xE027:'v3_bullseye'}
    cmap.update(generic)
    # Preserve v4/v3 compatibility aliases E100-E109; double check is now correctly stacked.
    cmap.update({0xE100:'v3_circle_filled',0xE101:'v3_circle_double',0xE102:'v3_square_outline',0xE103:'v3_check',0xE104:'v3_circle_outline',0xE105:'v3_circle_filled',0xE106:'v3_circle_outline',0xE107:'v3_circle_filled',0xE108:'v3_check',0xE109:'v3_double_check_stacked'})
    # New semantic aliases useful to current/future sheets.
    cmap.update({0xE10A:'v3_boxed_check',0xE10B:'v3_boxed_double_check_stacked',0xE10C:'v3_check',0xE10D:'v3_cross',0xE10E:'v3_star_filled',0xE10F:'v3_diamond_filled',0xE110:'v3_eye_outline',0xE111:'v3_shield_outline',0xE112:'v3_heart_filled',0xE113:'v3_flame_filled',0xE114:'v3_bolt_filled',0xE115:'v3_hourglass_outline',0xE116:'v3_book_outline',0xE117:'v3_target',0xE118:'v3_bullseye'})

    fb=FontBuilder(UPM,isTTF=True)
    fb.setupGlyphOrder(order); fb.setupCharacterMap(cmap); fb.setupGlyf(glyphs); fb.setupHorizontalMetrics(metrics)
    fb.setupHorizontalHeader(ascent=1854,descent=-434,lineGap=67)
    fb.setupNameTable({'familyName':'Para Hoja de PJ Symbols v5','styleName':'Regular','uniqueFontIdentifier':'GustavoMunoz: Para Hoja de PJ Symbols v5: 5.00: 2026-09-18','fullName':'Para Hoja de PJ Symbols v5 Regular','psName':'ParaHojadePJSymbolsV5-Regular','version':'Version 5.00; 2026-09-18','manufacturer':'Gustavo Munoz / DnD Custom Aid','designer':'Gustavo Munoz; technical reconstruction assisted by OpenAI','description':'Owner-authored symbol font. v5 preserves exact v1 A-E geometry, adds a v1-derived extension family and a v3-derived geometric family, canonical single/stacked checks, precomposed marked fillable variants, ordinary numerals/punctuation, standard aliases, and expanded character-sheet symbols.','licenseDescription':'Owner-authored font. Public repository publication and project modification/versioning authorized by Gustavo Munoz for DnD Custom Aid.'})
    fb.setupOS2(sTypoAscender=1854,sTypoDescender=-434,sTypoLineGap=67,usWinAscent=1854,usWinDescent=434,usWeightClass=400,usWidthClass=5,fsType=0)
    fb.setupPost(keepGlyphNames=True); fb.setupMaxp(); fb.setupHead(unitsPerEm=UPM,created=FIXED_TIMESTAMP,modified=FIXED_TIMESTAMP)
    out.parent.mkdir(parents=True,exist_ok=True); fb.save(out)
    built=sha256(out)
    if EXPECTED_TTF_SHA256 != 'TO_BE_FILLED' and built != EXPECTED_TTF_SHA256:
        out.unlink(missing_ok=True); raise RuntimeError(f'v5 deterministic SHA mismatch: {built}')
    print(out); print(f'sha256={built}'); print(f'glyphs={len(order)} cmap={len(cmap)}')


def main():
    p=argparse.ArgumentParser(); p.add_argument('v1',type=Path); p.add_argument('output',type=Path); a=p.parse_args(); build(a.v1,a.output)

if __name__=='__main__': main()
