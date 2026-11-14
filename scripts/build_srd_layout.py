#!/usr/bin/env python3
"""Readable SRD for the App (QA 0.7.0-preqa.2, observations A5, L1, L2).

The owner found the SRD text unreadable: it came from the PDF line by line, so tables ran together
and headings looked like any other line. This script reads a layout dump of each official PDF (one
record per line with position, font size and bold, made by scripts/srd/SrdStructured.java) and
writes a light Markdown per page:

  #, ##, ### headings · **bold run-in labels** · • bullets · | tables | with a header row

Output (committed): androidApp/src/main/assets/srd/srd_<version>_es.layout.json with
  {"format": "dnd-custom-aid.srd-layout", "version", "pages": [{"page", "md"}],
   "entries": {"<KIND>|<name>": "<md>"}}
The entries give each catalog entry (scripts/build_srd_catalog.py) its formatted text.

Usage (PDFs are not committed; same sources and SHA-256 as scripts/build_srd_corpus.py):
  java -cp <pdfbox jars> scripts/srd/SrdStructured.java SRD_CC_v5.1_ES.pdf 51.layout.txt
  java -cp <pdfbox jars> scripts/srd/SrdStructured.java SP_SRD_CC_v5.2.1.pdf 521.layout.txt
  python scripts/build_srd_layout.py --srd51 51.layout.txt --srd521 521.layout.txt
"""
from __future__ import annotations

import argparse
import json
import re
import statistics
import sys
import unicodedata
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "androidApp" / "src" / "main" / "assets" / "srd"
SEP = "\u001f"
FOOTER = re.compile(r"^(Documento de referencia del sistema|SRD\s*5|System Reference Document)", re.I)


class Line:
    __slots__ = ("x0", "xe", "y", "size", "bold_prefix", "all_bold", "text")

    def __init__(self, parts: list[str]):
        self.x0, self.xe, self.y, self.size = (float(p.replace(",", ".")) for p in parts[1:5])
        self.bold_prefix = int(parts[5])
        self.all_bold = parts[6] == "true"
        self.text = parts[7]


def read_layout(path: Path) -> dict[int, list[Line]]:
    pages: dict[int, list[Line]] = {}
    page = 0
    for raw in path.read_text(encoding="utf-8").splitlines():
        if raw.startswith("@@PAGE "):
            page = int(raw.split()[1])
            pages[page] = []
        elif raw.startswith("L\t"):
            parts = raw.split("\t", 7)
            if len(parts) == 8:
                pages[page].append(Line(parts))
    return pages


def body_size(pages: dict[int, list[Line]]) -> float:
    sizes = [ln.size for lines in pages.values() for ln in lines if len(ln.text) > 40 and not ln.all_bold]
    return statistics.mode(sizes)


def plain(text: str) -> str:
    return text.replace(SEP, " ")


def join_text(a: str, b: str) -> str:
    """Joins two wrapped lines, undoing the PDF's hyphenation («Intimida-» + «ción»)."""
    if not a:
        return b
    if re.search(r"[a-záéíóúñü][-‐‑]$", a) and b[:1].islower():
        return a[:-1] + b
    return a + " " + b


def esc(cell: str) -> str:
    return cell.replace("|", "/").strip()


def table_md(region: list[Line]) -> list[str]:
    """Turns a run of small-print lines into a Markdown table (header row when it has one)."""
    first_plain_y = min((ln.y for ln in region if not ln.all_bold), default=10_000)
    header = [ln for ln in region if ln.all_bold and ln.y < first_plain_y - 2]
    body = sorted((ln for ln in region if ln not in header), key=lambda ln: (ln.y, ln.x0))
    if not body:
        return []

    rows: list[list[str]] = []
    level_row = re.compile(r"^(\d{1,2})(?:\.º)?\s+(\+\d)\s+(.*)$")
    if sum(SEP in ln.text for ln in body) * 2 < len(body) and sum(bool(level_row.match(plain(ln.text))) for ln in body) >= 5:
        # SRD 5.1 level tables: each row starts with «N +B»; the numbers at the end of a row are
        # the class's own columns (furias, conjuros conocidos, espacios…).
        texts: list[str] = []
        numbers_of: list[str] = []
        only_numbers = re.compile(r"^(?:\d+d\d+|\+\d+|\d+(?:\s?m)?|—|–|-|Ilimitadas|ilimitadas)(?:\s+(?:\d+d\d+|\+\d+|\d+(?:\s?m)?|—|–|-|Ilimitadas|ilimitadas))*$")
        for ln in sorted(body, key=lambda ln: (ln.y, ln.x0)):
            t = plain(ln.text)
            if only_numbers.match(t.strip()) and not level_row.match(t) and texts:
                numbers_of[-1] = (numbers_of[-1] + " " + t).strip()
                continue
            parts = re.split(r"\s(?=\d{1,2}\s+\+\d\s)", t)
            for part in parts:
                if level_row.match(part) or not texts:
                    texts.append(part)
                    numbers_of.append("")
                else:
                    texts[-1] = join_text(texts[-1], part)
        texts = [(t + " " + n).strip() for t, n in zip(texts, numbers_of)]
        tail = re.compile(r"((?:\s+(?:\d+d\d+|\+\d+|\d+(?:\s?m)?|—|–|-|Ilimitadas|ilimitadas))+)$")
        for t in texts:
            m = level_row.match(t)
            if not m:
                continue
            rest = m.group(3)
            tm = tail.search(rest)
            numbers = tm.group(1).split() if tm else []
            features = rest[: tm.start()] if tm else rest
            rows.append([m.group(1), m.group(2), features.strip() or "—"] + numbers)
    elif sum(SEP in ln.text for ln in body) * 2 >= len(body):
        # One record per row (cells already split); a record without separators continues the
        # longest cell of the row above (a wrapped feature list).
        for ln in body:
            if SEP in ln.text:
                rows.append([c.strip() for c in ln.text.split(SEP)])
            elif rows:
                longest = max(range(len(rows[-1])), key=lambda i: len(rows[-1][i]))
                rows[-1][longest] = join_text(rows[-1][longest], ln.text)
            else:
                rows.append([ln.text])
    else:
        # Label / value layout: columns by x, rows anchored on the first column's lines that share
        # their y with a line of another column.
        xs = sorted({round(ln.x0) for ln in body})
        columns: list[float] = []
        for x in xs:
            if not columns or x - columns[-1] > 12:
                columns.append(x)
        col_of = lambda ln: max(i for i, c in enumerate(columns) if ln.x0 >= c - 6) if ln.x0 >= columns[0] - 6 else 0
        spacing = statistics.median([b.y - a.y for a, b in zip(body, body[1:]) if 4 < b.y - a.y < 30] or [12])
        anchors: list[float] = []
        for ln in body:
            if col_of(ln) != 0:
                continue
            partner = any(col_of(o) != 0 and abs(o.y - ln.y) < 0.8 for o in body)
            if partner or not anchors or ln.y - anchors[-1] > 1.35 * spacing:
                anchors.append(ln.y)
        if not anchors:
            anchors = [body[0].y]
        rows = [["" for _ in columns] for _ in anchors]
        anchored_cols: set[int] = set()
        for ln in body:
            if any(abs(ln.y - a) < 0.8 for a in anchors):
                anchored_cols.add(col_of(ln))
        for ln in body:
            r = max((i for i, a in enumerate(anchors) if a <= ln.y + 2.5), default=0)
            c = col_of(ln)
            if c not in anchored_cols:
                c = max([k for k in anchored_cols if k < c] or [0])
            pieces = ln.text.split(SEP)
            for k, piece in enumerate(pieces):
                cc = min(c + k, len(columns) - 1)
                rows[r][cc] = join_text(rows[r][cc], piece.strip())
        keep = sorted(anchored_cols) or [0]
        rows = [[r[k] for k in keep] for r in rows]

    width = max(len(r) for r in rows)
    if width == 1:
        # A boxed block of one column (5.2.1 spell stats): lines with bold labels, not a table.
        lines_out: list[str] = []
        for r in rows:
            for piece in re.split(r"\s+(?=[A-ZÁÉÍÓÚ][a-záéíóúñ ]{2,30}:\s)", r[0]):
                m = re.match(r"^([A-ZÁÉÍÓÚ][^:]{1,40}:)\s*(.*)$", piece)
                lines_out.append(f"**{m.group(1)}** {m.group(2)}" if m else piece)
                lines_out.append("")
        return lines_out
    rows = [r + [""] * (width - len(r)) for r in rows]
    out: list[str] = []
    if header:
        heads = header_cells(header, width)
        out.append("| " + " | ".join(esc(h) for h in heads) + " |")
        out.append("|" + "---|" * width)
    for r in rows:
        out.append("| " + " | ".join(esc(c) for c in r) + " |")
    return out


def header_cells(header: list[Line], width: int) -> list[str]:
    pieces: list[tuple[float, float, str]] = []  # (x-centre, y, text)
    for ln in header:
        parts = ln.text.split(SEP)
        if len(parts) == 1:
            pieces.append(((ln.x0 + ln.xe) / 2, ln.y, ln.text))
            continue
        total = sum(len(p) for p in parts) or 1
        x = ln.x0
        for p in parts:
            w = (ln.xe - ln.x0) * len(p) / total
            pieces.append((x + w / 2, ln.y, p.strip()))
            x += w
    pieces.sort()
    if len(pieces) <= width:
        cells = [p[2] for p in pieces]
        return cells + [""] * (width - len(cells))
    gaps = sorted(range(1, len(pieces)), key=lambda i: pieces[i][0] - pieces[i - 1][0], reverse=True)[: width - 1]
    cuts = sorted(gaps)
    groups, start = [], 0
    for cut in cuts + [len(pieces)]:
        groups.append(pieces[start:cut])
        start = cut
    return [" ".join(t for _, _, t in sorted(g, key=lambda p: (p[1], p[0]))) for g in groups]


def page_md(lines: list[Line], body: float) -> str:
    lines = [ln for ln in lines if ln.y < 740 and not FOOTER.match(ln.text) and not re.fullmatch(r"\d{1,3}", ln.text.strip())]
    out: list[str] = []
    para = ""
    prev: Line | None = None
    region: list[Line] = []
    col_right: dict[int, float] = {}
    for ln in lines:
        key = round(ln.x0 / 120)
        col_right[key] = max(col_right.get(key, 0), ln.xe)

    def end_para():
        nonlocal para
        if para:
            out.append(para)
            out.append("")
            para = ""

    def end_region():
        nonlocal region
        if region:
            end_para()
            out.extend(table_md(region))
            out.append("")
            region = []

    same_size_table = False
    margin = 0.0
    for idx, ln in enumerate(lines):
        text = ln.text
        if not same_size_table and ln.all_bold and abs(ln.size - body) < 0.4 and len(text) < 30:
            neighbours = [o for o in lines[idx + 1: idx + 6] if o.all_bold and abs(o.size - body) < 0.4 and abs(o.y - ln.y) < 14 and abs(o.x0 - ln.x0) > 15]
            if len(neighbours) >= 2:
                same_size_table = True
                margin = prev.x0 if prev is not None else ln.x0 - 4
        if same_size_table and (ln.size > body + 0.4 or (abs(ln.x0 - margin) < 1.5 and not ln.all_bold) or (region and abs(ln.y - region[-1].y) > 60)):
            same_size_table = False
            end_region()
        is_table = same_size_table or (ln.size < body - 0.4 and not re.match(r"^\s*[•●]", text))
        if is_table:
            if region and ln.y < region[-1].y - 40 and abs(ln.x0 - region[-1].x0) > 120:
                end_region()
            region.append(ln)
            prev = ln
            continue
        end_region()
        clean = plain(text).strip()
        if ln.size >= body + 1.5 and len(clean) < 120:
            end_para()
            level = "#" if ln.size >= body + 7 else "##" if ln.size >= body + 3 else "###"
            if out and out[-2:-1] and out[-2].startswith(level + " ") and prev is not None and prev.all_bold and abs(prev.size - ln.size) < 0.5 and 0 < ln.y - prev.y < ln.size * 1.6:
                out[-2] = out[-2] + " " + clean  # a heading wrapped over two lines
            else:
                out.append(f"{level} {clean}")
                out.append("")
            prev = ln
            continue
        if ln.all_bold and len(clean) < 90 and not clean.endswith((".", ",", ";")):
            end_para()
            if out[-2:-1] and out[-2].startswith("#### ") and prev is not None and prev.all_bold and 0 < ln.y - prev.y < ln.size * 1.6:
                out[-2] = out[-2] + " " + clean
            else:
                out.append(f"#### {clean}")
                out.append("")
            prev = ln
            continue
        bullet = re.match(r"^\s*[•●]\s*", clean)
        if bullet:
            end_para()
            clean = "• " + clean[bullet.end():]
        if ln.bold_prefix > 0 and not ln.all_bold:
            words = clean.split(" ")
            # boldPrefix counts characters; turn the bold run into a **label**.
            label = clean[: ln.bold_prefix].rstrip()
            rest = clean[ln.bold_prefix:].lstrip()
            if label and len(label) < 70:
                end_para()
                clean = f"**{label}** {rest}".rstrip()
        in_bullet = para.startswith("• ") or para.startswith("**")
        new_para = (
            not para
            or bullet is not None
            or clean.startswith("**")
            or (in_bullet and prev is not None and ln.x0 < prev.x0 - 4 and abs(ln.x0 - prev.x0) < 60)
            or (para.startswith("**") and prev is not None and prev.xe < col_right.get(round(prev.x0 / 120), prev.xe) - 28 and ln.x0 <= prev.x0 + 2 and abs(ln.x0 - prev.x0) < 60)
            or (not in_bullet and prev is not None and ln.x0 > prev.x0 + 4 and abs(ln.x0 - prev.x0) < 60)
            or (prev is not None and prev.xe < col_right.get(round(prev.x0 / 120), prev.xe) - 28 and para.rstrip().endswith((".", ":", "!", "?", "»", "”")))
        )
        if new_para:
            end_para()
            para = clean
        else:
            para = join_text(para, clean)
        prev = ln
    end_region()
    end_para()
    text = "\n".join(out)
    return re.sub(r"\n{3,}", "\n\n", text).strip()


def norm(text: str) -> str:
    text = unicodedata.normalize("NFKD", text.lower())
    text = "".join(c for c in text if not unicodedata.combining(c))
    return re.sub(r"[^a-z0-9]+", " ", text).strip()


def entry_md(md_pages: dict[int, str], catalog: dict) -> dict[str, str]:
    """The formatted text of each catalog entry: from its heading to the next entry's heading."""
    stream: list[tuple[int, str]] = [(p, line) for p in sorted(md_pages) for line in md_pages[p].split("\n")]
    names_by_kind: dict[str, list[dict]] = {}
    for e in catalog["entries"]:
        names_by_kind.setdefault(e["kind"], []).append(e)
    starts: dict[str, int] = {}
    for kind, entries in names_by_kind.items():
        cursor = 0
        for e in entries:
            target = norm(e["name"])
            found = None
            for i in range(cursor, len(stream)):
                page, line = stream[i]
                if page < e.get("page", 0) - 1:
                    continue
                head = line.lstrip("#").strip().strip("*").strip()
                if (line.startswith("#") or line.startswith("**")) and ((norm(head).startswith(target) and len(norm(head)) <= len(target) + 25) or (":" in head and norm(head.split(":", 1)[1]) == target)):
                    found = i
                    break
            if found is not None:
                starts[f"{kind}|{e['name']}"] = found
                cursor = found + 1
    ordered = sorted(starts.items(), key=lambda kv: kv[1])
    out: dict[str, str] = {}
    for n, (key, i) in enumerate(ordered):
        level = len(stream[i][1]) - len(stream[i][1].lstrip("#"))
        end = len(stream)
        for j in range(i + 1, len(stream)):
            if any(idx == j for _, idx in ordered[n + 1: n + 2]):
                end = j
                break
            line = stream[j][1]
            if level and line.startswith("#"):
                other = len(line) - len(line.lstrip("#"))
                if other < level:
                    end = j
                    break
        text = "\n".join(line for _, line in stream[i + 1: end]).strip()
        out[key] = text[:16000]
    return out


def build(layout: Path, corpus_file: str, catalog_file: str, version: str) -> dict:
    pages = read_layout(layout)
    body = body_size(pages)
    md_pages = {p: page_md(lines, body) for p, lines in pages.items()}
    catalog = json.loads((OUT / catalog_file).read_text(encoding="utf-8"))
    corpus = json.loads((OUT / corpus_file).read_text(encoding="utf-8"))
    return {
        "format": "dnd-custom-aid.srd-layout",
        "formatVersion": 1,
        "version": version,
        "attribution": corpus["attribution"],
        "pages": [{"page": p, "md": md} for p, md in sorted(md_pages.items()) if md],
        "entries": entry_md(md_pages, catalog),
    }


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--srd51", type=Path, required=True)
    parser.add_argument("--srd521", type=Path, required=True)
    args = parser.parse_args()
    for layout, corpus, catalog, version, out in (
        (args.srd51, "srd_5_1_es.json", "srd_catalog_5_1_es.json", "5.1", "srd_5_1_es.layout.json"),
        (args.srd521, "srd_5_2_1_es.json", "srd_catalog_5_2_1_es.json", "5.2.1", "srd_5_2_1_es.layout.json"),
    ):
        data = build(layout, corpus, catalog, version)
        (OUT / out).write_text(json.dumps(data, ensure_ascii=False, indent=0), encoding="utf-8")
        catalog_entries = json.loads((OUT / catalog).read_text(encoding="utf-8"))["entries"]
        print(f"{out}: {len(data['pages'])} pages, {len(data['entries'])} of {len(catalog_entries)} entries formatted")
    return 0


if __name__ == "__main__":
    sys.exit(main())
