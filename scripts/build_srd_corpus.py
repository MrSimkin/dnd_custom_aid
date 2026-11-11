#!/usr/bin/env python3
"""Build the in-App Spanish SRD corpus and the Referencia rápida cards (Base para DM, package 3).

Owner decisions: P4 (rules always cite the SRD text, offline search), P10 (ten quick-reference
cards, campaign version with a tap to see the other) and, on 2026-11-10, «todo debe ir en la App,
usaremos todo lo que esté disponible» (the whole SRD ships in the App, not only the ten cards).

Sources (CC BY 4.0, Wizards of the Coast LLC):
  SRD 5.1 ES   https://media.wizards.com/2023/downloads/dnd/SRD_CC_v5.1_ES.pdf
  SRD 5.2.1 ES https://media.dndbeyond.com/compendium-images/srd/5.2/SP_SRD_CC_v5.2.1.pdf

The PDFs are not committed. Turn each one into a text dump with scripts/srd/SrdOutline.java
(PDFBox from the Gradle cache), then run:

  python scripts/build_srd_corpus.py --srd51 SRD_CC_v5.1_ES.dump.txt --srd521 SP_SRD_CC_v5.2.1.dump.txt

Outputs (committed): androidApp/src/main/assets/srd/{srd_5_1_es.json, srd_5_2_1_es.json,
quick_reference_es.json}. --check rebuilds in memory and fails if the committed files differ.
"""
from __future__ import annotations

import argparse
import hashlib
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "androidApp" / "src" / "main" / "assets" / "srd"

VERSIONS = {
    "5.1": {
        "edition": "5e",
        "title": "Documento de referencia del sistema 5.1",
        "file": "srd_5_1_es.json",
        "source_url": "https://media.wizards.com/2023/downloads/dnd/SRD_CC_v5.1_ES.pdf",
        "source_sha256": "75c42a82d627fd6c85e8929985157176c699e1ddbd2ca5d2bcbb58af17254651",
        "attribution": (
            "Esta obra incluye materiales extraídos del Documento de referencia del sistema 5.1 (“SRD 5.1”) "
            "de Wizards of the Coast LLC, que está disponible en "
            "https://dnd.wizards.com/es/resources/systems-reference-document. El SRD 5.1 tiene la licencia "
            "Creative Commons Atribución/Reconocimiento 4.0 Licencia Pública Internacional, que está disponible "
            "en https://creativecommons.org/licenses/by/4.0/legalcode.es."
        ),
        "noise": [
            re.compile(r"^\s*Documento de referencia del sistema 5\.1\.\s*\d+\s*$"),
            re.compile(r"^Prohibida la reventa\. Tienes permiso para imprimir\s*$"),
            re.compile(r"^o fotocopiar este documento solo para uso personal\.\s*$"),
        ],
    },
    "5.2.1": {
        "edition": "5.5e",
        "title": "Documento de referencia del sistema 5.2.1",
        "file": "srd_5_2_1_es.json",
        "source_url": "https://media.dndbeyond.com/compendium-images/srd/5.2/SP_SRD_CC_v5.2.1.pdf",
        "source_sha256": "9ecc1b980016b104004424a4323a65cdd6b396fce4f12b49bc524ebbab8088b5",
        "attribution": (
            "Esta obra incluye material procedente del documento de referencia del sistema 5.2.1 (“SRD 5.2.1”) "
            "de Wizards of the Coast LLC, disponible en https://www.dndbeyond.com/srd. La licencia sobre el "
            "SRD 5.2.1 se concede de acuerdo con la licencia internacional de atribución/reconocimiento 4.0 de "
            "Creative Commons, disponible en https://creativecommons.org/licenses/by/4.0/legalcode."
        ),
        "noise": [
            re.compile(r"^\s*Documento de referencia del sistema 5\.2\.1\s*\d+\s*$"),
            re.compile(r"^\s*\d+\s+Documento de referencia del sistema 5\.2\.1\s*$"),
        ],
    },
}

# Each card lists, per version, the sections it copies: (start heading, end heading, first page).
# The section runs from the start heading line up to (not including) the end heading line.
STATES_521 = [
    ("Agarrado [estado]", "Agarrar"),
    ("Apresado [estado]", "Asfixia [peligro]"),
    ("Asustado [estado]", "Atacar [acción]"),
    ("Aturdido [estado]", "Ayudar [acción]"),
    ("Cansancio [estado]", "Cegado [estado]"),
    ("Cegado [estado]", "Cilindro [área de efecto]"),
    ("Derribado [estado]", "Descanso corto"),
    ("Ensordecido [estado]", "Envenenado [estado]"),
    ("Envenenado [estado]", "Esconderse [acción]"),
    ("Hechizado [estado]", "Hostil [actitud]"),
    ("Incapacitado [estado]", "Inconsciente [estado]"),
    ("Inconsciente [estado]", "Indiferente [actitud]"),
    ("Invisible [estado]", "Levitar"),
    ("Paralizado [estado]", "Peligro"),
    ("Petrificado [estado]", "Posesión"),
]

CARDS = [
    {
        "id": "estados",
        "title": "Estados",
        "5.1": [("Estados", "Apéndice MJ-B:", 389)],
        "5.2.1": [(start, end, 194) for start, end in STATES_521],
    },
    {
        "id": "cobertura",
        "title": "Cobertura",
        "5.1": [("Cobertura", "Daño y curación", 102)],
        "5.2.1": [("Cobertura", "Concentración", 194)],
    },
    {
        "id": "concentracion",
        "title": "Concentración",
        "5.1": [("Concentración", "Objetivos", 108)],
        "5.2.1": [("Concentración", "Conjuro", 194)],
    },
    {
        "id": "cd_tipicas",
        "title": "CD típicas",
        "5.1": [("Clases de Dificultad típicas", "Tiradas enfrentadas", 80)],
        "5.2.1": [("Clases de dificultad típicas", "Tiradas de salvación", 6)],
    },
    {
        "id": "luz_vision",
        "title": "Luz y visión",
        "5.1": [("Visión e iluminación", "Comida y agua", 90)],
        "5.2.1": [
            ("Visión e iluminación", "Sentidos especiales", 12),
            ("Visión ciega", "Visión en la oscuridad", 194),
            ("Visión en la oscuridad", "Visión verdadera", 194),
            ("Visión verdadera", "Volar", 194),
        ],
    },
    {
        "id": "caidas_asfixia",
        "title": "Caídas y asfixia",
        "5.1": [("Caídas", "Visión e iluminación", 90)],
        "5.2.1": [("Caídas [peligro]", "Cambio de forma", 194), ("Asfixia [peligro]", "Asustado [estado]", 194)],
    },
    {
        "id": "agarrar_derribar",
        "title": "Agarrar y derribar",
        "5.1": [("Agarrar", "Tiradas enfrentadas en combate", 101), ("Empujar a una criatura", "Cobertura", 101)],
        "5.2.1": [("Agarrar", "Al día", 194), ("Ataque sin armas", "Aturdido [estado]", 194)],
    },
    {
        "id": "ocultarse",
        "title": "Ocultarse",
        "5.1": [("Esconderse", "Constitución", 84)],
        "5.2.1": [("Esconderse [acción]", "Esfera [área de efecto]", 194)],
    },
    {
        "id": "descansos",
        "title": "Descansos",
        "5.1": [("Descansar", "Entre aventuras", 91)],
        "5.2.1": [("Descanso corto", "Descanso largo", 194), ("Descanso largo", "Deshidratación [peligro]", 194)],
    },
    {
        "id": "viaje",
        "title": "Viaje",
        "5.1": [("Ritmo de viaje", "Terreno difícil", 88)],
        "5.2.1": [("Viajar", "Orden de marcha", 12), ("Ritmo de viaje", "Buenos caminos", 211), ("Viaje largo", None, 211)],
    },
]

END_REPORT: list[str] = []
PAGE_MARK = re.compile(r"^@@PAGE (\d+)$")
HYPHENS = ("-", "‑", "­")
SENTENCE_END = (".", ":", ";", "!", "?")


def read_dump(path: Path, version: str) -> list[tuple[int, str]]:
    """Returns (page, line) pairs without the outline block and the page header/footer noise."""
    noise = VERSIONS[version]["noise"]
    lines: list[tuple[int, str]] = []
    page = 0
    for raw in path.read_text(encoding="utf-8").replace("\r\n", "\n").split("\n"):
        mark = PAGE_MARK.match(raw)
        if mark:
            page = int(mark.group(1))
            continue
        if page == 0 or any(n.match(raw) for n in noise):
            continue
        lines.append((page, raw.replace("\t", " ").rstrip()))
    return lines


def paragraphs(lines: list[str]) -> str:
    """Joins soft-wrapped PDF lines into paragraphs; headings, list items and table rows keep their own line."""
    out: list[str] = []
    for line in lines:
        text = line.strip()
        if not text:
            continue
        if not out:
            out.append(text)
            continue
        prev = out[-1]
        if prev.endswith(HYPHENS) and text[:1].islower():
            out[-1] = prev[:-1] + text
        elif text[:1].islower() and not prev.endswith((".", "!", "?")):
            out[-1] = prev + " " + text
        elif line.startswith((" ", "•")) or text.startswith("•"):
            out.append(text)
        elif prev.endswith(SENTENCE_END) or (len(prev) < 32 and text[:1].isupper()):
            out.append(text)
        else:
            out[-1] = prev + " " + text
    # Non-breaking spaces and non-breaking hyphens become plain ones so offline search matches.
    return "\n".join(out).replace("\u00a0", " ").replace("\u2011", "-")


GLOSSARY_FIRST_PAGE_521 = 194
SENTENCE_CLOSE = (".", "”", ")", "»")


def glossary_heading(lines: list[tuple[int, str]], i: int) -> bool:
    """A 5.2.1 glossary entry title: a short line without inner sentences or final punctuation,
    after a closed sentence and followed by a capitalised definition line."""
    text = lines[i][1].strip()
    if not text or len(text) > 44 or not text[:1].isupper() or ". " in text:
        return False
    if text.endswith((".", ",", ";", ":")) or text.startswith("•"):
        return False
    prev = lines[i - 1][1].strip() if i > 0 else ""
    nxt = lines[i + 1][1].strip() if i + 1 < len(lines) else ""
    return prev.endswith(SENTENCE_CLOSE) and nxt[:1].isupper()


def section(
    lines: list[tuple[int, str]],
    start: str,
    end: str | None,
    first_page: int,
    glossary: bool = False,
) -> dict:
    idx = next(
        (i for i, (page, text) in enumerate(lines) if page >= first_page and text.strip() == start),
        None,
    )
    if idx is None:
        raise SystemExit(f"start heading not found: {start!r} (page >= {first_page})")
    stop = len(lines)
    if end is not None:
        stop = next((i for i in range(idx + 1, len(lines)) if lines[i][1].strip() == end), None)
        if stop is None:
            raise SystemExit(f"end heading not found after {start!r}: {end!r}")
    else:
        # Open-ended: the section runs to the end of its page.
        stop = next((i for i in range(idx + 1, len(lines)) if lines[i][0] != lines[idx][0]), len(lines))
    if glossary:
        # Two-column glossary pages interleave entries; stop at the first entry title found.
        auto = next((i for i in range(idx + 2, stop) if glossary_heading(lines, i)), None)
        if auto is not None:
            stop = auto
    if stop - idx > 400:
        raise SystemExit(f"section {start!r} -> {end!r} is suspiciously long ({stop - idx} lines)")
    kept: list[str] = []
    for _, text in lines[idx + 1:stop]:
        # A table split across two columns repeats its short header row; keep the first one.
        if text.strip() and len(text.strip()) < 40 and any(k.strip() == text.strip() for k in kept):
            if not text.strip()[:1].isdigit():
                continue
        kept.append(text)
    body = paragraphs(kept)
    return {"heading": start, "page": lines[idx][0], "text": body, "_end": lines[stop][1].strip() if stop < len(lines) else None}


def build(dumps: dict[str, Path]) -> dict[str, str]:
    files: dict[str, str] = {}
    parsed = {version: read_dump(path, version) for version, path in dumps.items()}
    for version, lines in parsed.items():
        meta = VERSIONS[version]
        pages: dict[int, list[str]] = {}
        for page, text in lines:
            pages.setdefault(page, []).append(text)
        corpus = {
            "format": "dnd-custom-aid.srd-corpus",
            "formatVersion": 1,
            "language": "es",
            "version": version,
            "edition": meta["edition"],
            "title": meta["title"],
            "license": "CC-BY-4.0",
            "attribution": meta["attribution"],
            "sourceUrl": meta["source_url"],
            "sourceSha256": meta["source_sha256"],
            "pages": [{"page": page, "text": paragraphs(texts)} for page, texts in sorted(pages.items())],
        }
        files[meta["file"]] = json.dumps(corpus, ensure_ascii=False, indent=1) + "\n"

    cards = []
    for card in CARDS:
        entry = {"id": card["id"], "title": card["title"], "versions": {}}
        for version in ("5.1", "5.2.1"):
            sections = []
            for s, e, p in card[version]:
                found = section(parsed[version], s, e, p, glossary=version == "5.2.1" and p >= GLOSSARY_FIRST_PAGE_521)
                END_REPORT.append(f"{card['id']} {version} [{s}] -> [{found.pop('_end')}]")
                sections.append(found)
            entry["versions"][version] = sections
        cards.append(entry)
    quick = {
        "format": "dnd-custom-aid.srd-quick-reference",
        "formatVersion": 1,
        "language": "es",
        "attribution": {version: VERSIONS[version]["attribution"] for version in VERSIONS},
        "cards": cards,
    }
    files["quick_reference_es.json"] = json.dumps(quick, ensure_ascii=False, indent=1) + "\n"
    return files


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--srd51", type=Path, required=True)
    parser.add_argument("--srd521", type=Path, required=True)
    parser.add_argument("--check", action="store_true", help="fail if committed outputs differ")
    parser.add_argument("--report-ends", action="store_true", help="print where every card section stops")
    args = parser.parse_args()
    files = build({"5.1": args.srd51, "5.2.1": args.srd521})
    if args.report_ends:
        print("\n".join(END_REPORT))
    if args.check:
        stale = [name for name, text in files.items() if (OUT / name).read_text(encoding="utf-8") != text]
        if stale:
            print("STALE: " + ", ".join(stale))
            return 1
        print("SRD corpus up to date.")
        return 0
    OUT.mkdir(parents=True, exist_ok=True)
    for name, text in files.items():
        (OUT / name).write_text(text, encoding="utf-8", newline="\n")
        digest = hashlib.sha256(text.encode("utf-8")).hexdigest()[:12]
        print(f"{name}: {len(text.encode('utf-8'))} bytes, sha256 {digest}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
