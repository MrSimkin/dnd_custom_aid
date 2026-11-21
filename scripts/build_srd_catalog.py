#!/usr/bin/env python3
"""Build the structured SRD catalog the PC editor picks from (redesign R5, owner decisions D8 and D9).

The owner asked (2026-11-13) that creating a PC and adding spells, weapons, armour and similar
offer the SRD first, then the DM's homebrew, then custom text. This script turns the committed
in-App SRD corpus (scripts/build_srd_corpus.py, CC BY 4.0, Wizards of the Coast LLC) into lists
of entries: spells, species, classes and subclasses, backgrounds, feats, weapons and armour.

Input:  androidApp/src/main/assets/srd/{srd_5_1_es.json, srd_5_2_1_es.json}
Output: androidApp/src/main/assets/srd/{srd_catalog_5_1_es.json, srd_catalog_5_2_1_es.json}

Run:  python scripts/build_srd_catalog.py          (writes the files)
      python scripts/build_srd_catalog.py --check  (fails if the committed files differ)

Each entry: {"kind", "name", "summary", "body", "fields", "page", "parent"?}. Kinds match
RulesEntryKind in the shared module. The text is the SRD's own; nothing is paraphrased.
"""
from __future__ import annotations

import argparse
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SRD = ROOT / "androidApp" / "src" / "main" / "assets" / "srd"

SCHOOLS = [
    "Abjuración", "Adivinación", "Conjuración", "Encantamiento", "Evocación",
    "Ilusionismo", "Nigromancia", "Transmutación",
]
SCHOOL_RE = "|".join(SCHOOLS)
CLASSES_ES = ["bardo", "brujo", "clérigo", "druida", "explorador", "hechicero", "mago", "paladín", "bárbaro", "guerrero", "monje", "pícaro"]


def load(name: str) -> dict:
    return json.loads((SRD / name).read_text(encoding="utf-8"))


def page_lines(corpus: dict, first: int, last: int) -> list[tuple[int, str]]:
    """(page, line) pairs for pages first..last (1-based, inclusive)."""
    out = []
    for page in corpus["pages"]:
        if first <= page["page"] <= last:
            for line in page["text"].split("\n"):
                line = line.strip()
                if line:
                    out.append((page["page"], line))
    return out


def find_page(corpus: dict, starts_with: str, after: int = 0) -> int:
    for page in corpus["pages"]:
        if page["page"] > after and page["text"].strip().startswith(starts_with):
            return page["page"]
    raise SystemExit(f"Page starting with {starts_with!r} not found")


# ---------------------------------------------------------------- spells

SPELL_HEAD_51 = re.compile(
    rf"^(?:(?P<school>{SCHOOL_RE}) nivel (?P<level>\d)|(?P<cschool>{SCHOOL_RE}) \(truco\))(?P<ritual> \(ritual\))?\s*$",
    re.IGNORECASE,
)
SPELL_HEAD_521 = re.compile(
    rf"^(?:(?P<school>{SCHOOL_RE}) de nivel (?P<level>\d)|Truco de (?P<cschool>{SCHOOL_RE.lower()})) \((?P<classes>[^)]*)\)\s*(?P<rest>.*)$",
    re.IGNORECASE,
)
FIELD_RE = re.compile(r"(Tiempo de lanzamiento|Alcance|Componentes|Duración):\s*")


def split_fields(text: str) -> tuple[dict, str]:
    """Pulls «Tiempo de lanzamiento / Alcance / Componentes / Duración» from the start of a spell."""
    fields: dict[str, str] = {}
    rest = text
    for label in ["Tiempo de lanzamiento", "Alcance", "Componentes", "Duración"]:
        m = re.match(rf"\s*{label}:\s*", rest)
        if not m:
            break
        rest = rest[m.end():]
        nxt = FIELD_RE.search(rest)
        if label == "Duración":
            # The duration ends where the description starts: the first sentence break after a short value.
            m2 = re.match(r"(Concentración, hasta [^.]*?(?:minutos|minuto|horas|hora|días|día|asaltos|asalto)|Instantáneo|Instantánea|Hasta que se disipe(?: o se active)?|Especial|\d+ (?:asaltos|asalto|minutos|minuto|horas|hora|días|día|turno))\s*", rest)
            if m2:
                fields["duracion"] = m2.group(1).strip()
                rest = rest[m2.end():]
            else:
                value, _, rest = rest.partition(" ")
                fields["duracion"] = value
            break
        value = rest[: nxt.start()] if nxt else rest
        rest = rest[nxt.start():] if nxt else ""
        key = {"Tiempo de lanzamiento": "tiempo", "Alcance": "alcance", "Componentes": "componentes"}[label]
        fields[key] = value.strip()
    return fields, rest.strip()


def spells(corpus: dict, version: str) -> list[dict]:
    first = find_page(corpus, "Descripciones de conjuros") if version == "5.1" else 118
    last = 208 if version == "5.1" else 193
    lines = page_lines(corpus, first, last)
    head = SPELL_HEAD_51 if version == "5.1" else SPELL_HEAD_521
    entries: list[dict] = []
    current: dict | None = None
    body: list[str] = []

    def close():
        if current is None:
            return
        text = " ".join(body).strip()
        if version == "5.1":
            fields, rest = split_fields(text)
        else:
            fields, rest = split_fields(current.pop("_rest") + " " + text)
        current["fields"].update(fields)
        current["body"] = rest
        sentence = re.split(r"(?<=[.!?])\s", rest, maxsplit=1)[0] if rest else ""
        current["summary"] = sentence[:220]
        entries.append(current)

    for i, (page, line) in enumerate(lines):
        m = head.match(line)
        if m and i > 0 and len(lines[i - 1][1]) <= 45:
            # The previous line was the spell's name: it belongs to the new spell, not the old body.
            name = lines[i - 1][1]
            if current is not None and body and body[-1] == name:
                body.pop()
            close()
            level = int(m.group("level")) if m.group("level") else 0
            school = (m.group("school") or m.group("cschool") or "").capitalize()
            fields = {"nivel": str(level), "escuela": school}
            if version == "5.1" and m.group("ritual"):
                fields["ritual"] = "sí"
            classes = []
            if version != "5.1":
                classes = [c.strip() for c in m.group("classes").split(",") if c.strip()]
                fields["clases"] = ", ".join(classes)
            current = {"kind": "SPELL", "name": name, "fields": fields, "page": page}
            if version != "5.1":
                current["_rest"] = m.group("rest")
            body = []
            continue
        if current is not None:
            body.append(line)
    close()
    # 5.1 lists the classes of each spell in its class lists (some lines run several names together).
    if version == "5.1":
        text = " \n ".join(line for _, line in page_lines(corpus, find_page(corpus, "Listas de conjuros"), first - 1))
        sections = re.split(r"Conjuros de (\w+)", text)
        names = sorted((e["name"] for e in entries), key=len, reverse=True)
        by_name = {e["name"]: e for e in entries}
        for cls, section in zip(sections[1::2], sections[2::2]):
            cls = cls.lower()
            if cls not in CLASSES_ES:
                continue
            rest = " " + section + " "
            for name in names:
                pattern = re.compile(r"(?<=[\s])" + re.escape(name) + r"(?=[\s])")
                if pattern.search(rest):
                    rest = pattern.sub(" ", rest)
                    f = by_name[name]["fields"]
                    known = [c for c in f.get("clases", "").split(", ") if c]
                    if cls not in known:
                        f["clases"] = ", ".join(known + [cls])
    return entries


# ---------------------------------------------------------------- weapons and armour

PRICE = r"(?:\d[\d.]*\s?(?:pc|pp|pe|po|ppt)|—)"
WEIGHT = r"(?:\d[\d,]*\s?(?:kg|g)|—)"
DAMAGE = r"(?:\d+d\d+|1|—)"
DTYPE = r"(?:contundente|cortante|perforante)"
MASTERIES = "Derribar|Mellar|Ralentizar|Empujar|Molestar|Debilitar|Hender|Rozar"
WEAPON_CATEGORY = re.compile(r"Armas (cuerpo a cuerpo|a distancia) (sencillas|marciales)")


def category_label(match: re.Match) -> str:
    return f"{match.group(1)}, {'sencilla' if match.group(2) == 'sencillas' else 'marcial'}"


def table_text(corpus: dict, start: str, end_markers: list[str]) -> tuple[int, str]:
    for page in corpus["pages"]:
        i = page["text"].find(start)
        if i < 0:
            continue
        text = page["text"][i:]
        nxt = next((p for p in corpus["pages"] if p["page"] == page["page"] + 1), None)
        if nxt:
            text += "\n" + nxt["text"]
        cut = min([text.find(m, len(start)) for m in end_markers if text.find(m, len(start)) > 0] or [len(text)])
        return page["page"], text[:cut]
    raise SystemExit(f"Table {start!r} not found")


def weapons(corpus: dict, version: str) -> list[dict]:
    entries = []
    if version == "5.1":
        page, text = table_text(corpus, "Armas\nNombre Precio Daño", ["\nPropiedades de las armas", "\nEquipo de aventurero", "\nArmaduras"])
        row = re.compile(rf"^(?P<name>[^\d—]+?) (?P<price>{PRICE}) (?P<dmg>{DAMAGE})(?: (?P<type>{DTYPE}))? (?P<weight>{WEIGHT})(?: (?P<props>.*))?$")
        category = ""
        for raw in text.split("\n")[1:]:
            line = raw.strip()
            head = WEAPON_CATEGORY.search(line)
            if head and head.start() == line.find("Armas", 1 if line.startswith("Nombre") else 0) and line.startswith(("Nombre", "Armas")):
                category = category_label(head)
                continue
            trailing = None
            if head:
                trailing = head
                line = line[: head.start()].strip()
            m = row.match(line)
            if m:
                fields = {"categoria": category, "precio": m.group("price"), "dano": " ".join(filter(None, [m.group("dmg"), m.group("type")])),
                          "peso": m.group("weight"), "propiedades": (m.group("props") or "—").strip()}
                entries.append({"kind": "WEAPON", "name": m.group("name").strip(), "fields": fields, "page": page,
                                "summary": f"{fields['dano']} · {fields['propiedades']}", "body": ""})
            if trailing:
                category = category_label(trailing)
    else:
        page, text = table_text(corpus, "Armas\nNombre Daño Propiedades Maestría", ["Descripciones de armas", "\nArmaduras\n"])
        flat = " ".join(text.split("\n")[1:])
        row = re.compile(rf"(?P<name>[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+(?: [a-záéíóúñ]+)*?) (?P<dmg>{DAMAGE}) (?P<type>{DTYPE}) (?P<props>.*?) (?P<mastery>{MASTERIES}) (?P<weight>{WEIGHT}) (?P<price>{PRICE})")
        pos = 0
        category = ""
        while True:
            head = WEAPON_CATEGORY.search(flat, pos)
            m = row.search(flat, pos)
            if not m:
                break
            if head and head.start() < m.start():
                category = category_label(head)
                pos = head.end()
                continue
            fields = {"categoria": category, "precio": m.group("price"), "dano": f"{m.group('dmg')} {m.group('type')}",
                      "peso": m.group("weight"), "propiedades": m.group("props").strip(), "maestria": m.group("mastery")}
            entries.append({"kind": "WEAPON", "name": m.group("name").strip(), "fields": fields, "page": page,
                            "summary": f"{fields['dano']} · {fields['propiedades']} · {fields['maestria']}", "body": ""})
            pos = m.end()
    return entries


ARMOR_CATEGORY = re.compile(r"(Armaduras (?:ligeras|medias|pesadas)|Escudos?)\b(?: \([^)]*\))?")


def armor(corpus: dict, version: str) -> list[dict]:
    entries = []
    ac = r"(?P<ac>\+?\d+(?: \+ modificador por Des(?: \(máx\. 2\))?)?)"
    name = r"(?P<name>(?!Armaduras |Escudos )[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+(?: [a-záéíóúñ]+)*)"
    if version == "5.1":
        page, text = table_text(corpus, "Armaduras\nArmadura Precio", ["\nPonerse y quitarse"])
        row = re.compile(rf"{name} (?P<price>{PRICE})\s+{ac} (?P<str>—|Fue \d+) (?P<stealth>—|Desventaja) (?P<weight>{WEIGHT})")
    else:
        page, text = table_text(corpus, "Armaduras\nArmadura Clase de armadura", ["\nPonerse", "Herramientas"])
        row = re.compile(rf"{name} {ac} (?P<str>—|Fue \d+) (?P<stealth>—|Desventaja) (?P<weight>{WEIGHT}) (?P<price>{PRICE})")
    flat = " ".join(text.split("\n")[1:])
    heads = [(h.start(), h.group(1)) for h in ARMOR_CATEGORY.finditer(flat)]
    for m in row.finditer(flat):
        category = next((label for start, label in reversed(heads) if start < m.start()), "")
        fields = {"categoria": category, "ca": m.group("ac"), "fuerza": m.group("str"), "sigilo": m.group("stealth"),
                  "peso": m.group("weight"), "precio": m.group("price")}
        entries.append({"kind": "ARMOR", "name": m.group("name").strip(), "fields": fields, "page": page,
                        "summary": f"CA {fields['ca']} · {category}", "body": ""})
    return entries


# ---------------------------------------------------------------- species, classes, backgrounds, feats

# (kind, name, page where it starts, parent). Names and pages are the SRD's own headings.
SECTIONS = {
    "5.1": [
        ("SPECIES", "Elfo", 2, ""), ("SUBSPECIES", "Alto elfo", 3, "Elfo"),
        ("SPECIES", "Enano", 3, ""), ("SUBSPECIES", "Enano de las colinas", 4, "Enano"),
        ("SPECIES", "Humano", 4, ""), ("SPECIES", "Mediano", 4, ""), ("SUBSPECIES", "Piesligeros", 4, "Mediano"),
        ("SPECIES", "Dracónido", 4, ""), ("SPECIES", "Gnomo", 5, ""), ("SUBSPECIES", "Gnomo de las rocas", 5, "Gnomo"),
        ("SPECIES", "Semielfo", 6, ""), ("SPECIES", "Semiorco", 6, ""), ("SPECIES", "Tiefling", 7, ""),
        ("CLASS", "Bárbaro", 8, ""), ("SUBCLASS", "Senda del Berserker", 10, "Bárbaro"),
        ("CLASS", "Bardo", 11, ""), ("SUBCLASS", "Colegio del Conocimiento", 13, "Bardo"),
        ("CLASS", "Brujo", 15, ""), ("SUBCLASS", "El Infernal", 20, "Brujo"),
        ("CLASS", "Clérigo", 21, ""), ("SUBCLASS", "Dominio de la Vida", 23, "Clérigo"),
        ("CLASS", "Druida", 25, ""), ("SUBCLASS", "Círculo de la Tierra", 27, "Druida"),
        ("CLASS", "Explorador", 30, ""), ("SUBCLASS", "Cazador", 33, "Explorador"),
        ("CLASS", "Guerrero", 34, ""), ("SUBCLASS", "Campeón", 35, "Guerrero"),
        ("CLASS", "Hechicero", 36, ""), ("SUBCLASS", "Linaje Dracónico", 39, "Hechicero"),
        ("CLASS", "Mago", 40, ""), ("SUBCLASS", "Escuela de Evocación", 42, "Mago"),
        ("CLASS", "Monje", 44, ""), ("SUBCLASS", "Camino de la Mano Abierta", 47, "Monje"),
        ("CLASS", "Paladín", 48, ""), ("SUBCLASS", "Juramento de Entrega", 51, "Paladín"),
        ("CLASS", "Pícaro", 53, ""), ("SUBCLASS", "Ladrón", 54, "Pícaro"),
        ("END", "Avance de personajes", 56, ""),
        ("BACKGROUND", "Acólito", 61, ""), ("END", "Equipo", 63, ""),
        ("FEAT", "Apresador", 78, ""), ("END", "Uso de las puntuaciones de característica", 79, ""),
    ],
    "5.2.1": [
        ("CLASS", "Bárbaro", 32, ""), ("SUBCLASS", "Senda del berserker", 34, "Bárbaro"),
        ("CLASS", "Bardo", 35, ""), ("SUBCLASS", "Colegio del conocimiento", 39, "Bardo"),
        ("CLASS", "Brujo", 40, ""), ("SUBCLASS", "Patrón infernal", 45, "Brujo"),
        ("CLASS", "Clérigo", 47, ""), ("SUBCLASS", "Dominio de la vida", 51, "Clérigo"),
        ("CLASS", "Druida", 52, ""), ("SUBCLASS", "Círculo de la tierra", 57, "Druida"),
        ("CLASS", "Explorador", 58, ""), ("SUBCLASS", "Cazador", 60, "Explorador"),
        ("CLASS", "Guerrero", 62, ""), ("SUBCLASS", "Campeón", 63, "Guerrero"),
        ("CLASS", "Hechicero", 64, ""), ("SUBCLASS", "Hechicería dracónica", 69, "Hechicero"),
        ("CLASS", "Mago", 71, ""), ("SUBCLASS", "Evocador", 76, "Mago"),
        ("CLASS", "Monje", 77, ""), ("SUBCLASS", "Guerrero de la mano abierta", 79, "Monje"),
        ("CLASS", "Paladín", 81, ""), ("SUBCLASS", "Juramento de entrega", 84, "Paladín"),
        ("CLASS", "Pícaro", 86, ""), ("SUBCLASS", "Ladrón", 88, "Pícaro"),
        ("END", "Orígenes de personaje", 89, ""),
        ("BACKGROUND", "Acólito", 89, ""), ("BACKGROUND", "Criminal", 89, ""), ("BACKGROUND", "Erudito", 89, ""), ("BACKGROUND", "Soldado", 89, ""),
        ("END", "Partes de una especie", 90, ""),
        ("SPECIES", "Dracónido", 90, ""), ("SPECIES", "Elfo", 90, ""), ("SPECIES", "Enano", 91, ""), ("SPECIES", "Gnomo", 91, ""),
        ("SPECIES", "Goliat", 92, ""), ("SPECIES", "Humano", 92, ""), ("SPECIES", "Mediano", 92, ""), ("SPECIES", "Orco", 92, ""),
        ("SPECIES", "Tiefling", 93, ""), ("END", "Dotes", 94, ""),
        ("FEAT", "Alerta", 94, ""), ("FEAT", "Atacante salvaje", 94, ""), ("FEAT", "Habilidoso", 94, ""), ("FEAT", "Iniciado en la magia", 94, ""),
        ("FEAT", "Apresador", 94, ""), ("FEAT", "Mejora de característica", 94, ""),
        ("FEAT", "Combate con armas a dos manos", 95, ""), ("FEAT", "Combate con dos armas", 95, ""), ("FEAT", "Defensa", 95, ""),
        ("FEAT", "Tiro con arco", 95, ""), ("FEAT", "Don de la pericia en combate", 95, ""), ("FEAT", "Don de la visión verdadera", 95, ""),
        ("FEAT", "Don del ataque imparable", 95, ""), ("FEAT", "Don del destino", 95, ""), ("FEAT", "Don del espíritu de la noche", 95, ""),
        ("FEAT", "Don del recuerdo de conjuros", 95, ""), ("FEAT", "Don del viaje dimensional", 96, ""),
        ("END", "Equipo", 97, ""),
    ],
}


def sections(corpus: dict, version: str) -> list[dict]:
    items = SECTIONS[version]
    pages = {p["page"]: p["text"] for p in corpus["pages"]}
    first, last = min(i[2] for i in items), max(i[2] for i in items) + 1
    text = ""
    offsets = {}
    for number in range(first, last + 1):
        offsets[number] = len(text)
        text += "\n" + pages.get(number, "") + "\n"
    found = []
    cursor = 0
    for kind, name, page, parent in items:
        start = max(offsets[page], cursor)
        # A heading is the name on its own line, or (5.2.1 runs lines together) followed by a capital.
        pattern = re.compile(rf"(?:^|\n|(?<=[.)\s]))({re.escape(name)})(?=\n| [A-ZÁÉÍÓÚÑ¿])")
        m = pattern.search(text, start)
        if not m:
            raise SystemExit(f"{version}: heading {name!r} not found from page {page}")
        found.append((m.start(1), kind, name, parent, page))
        cursor = m.end(1)
    entries = []
    for index, (pos, kind, name, parent, page) in enumerate(found):
        if kind == "END":
            continue
        end = found[index + 1][0] if index + 1 < len(found) else len(text)
        body = re.sub(r"\n{2,}", "\n", text[pos + len(name): end]).strip()
        fields = {}
        if kind == "CLASS":
            hit = re.search(r"Dados? de (?:puntos de )?golpe:?\s*(?:1)?[dD](\d+)", body, re.IGNORECASE)
            if hit:
                fields["dado_de_golpe"] = "d" + hit.group(1)
        # The summary skips the short sub-headings that open a section («Atributos de los elfos»).
        prose = next((line for line in body.split("\n") if len(line) > 50 or line.endswith(".")), body)
        sentence = re.split(r"(?<=[.!?])\s", prose, maxsplit=1)[0] if prose else ""
        entry = {"kind": kind, "name": name, "fields": fields, "page": page, "summary": sentence[:220], "body": body[:12000]}
        if parent:
            entry["parent"] = parent
        entries.append(entry)
    return entries


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--check", action="store_true")
    parser.add_argument("--stats", action="store_true")
    args = parser.parse_args()
    ok = True
    for version, source, target in [
        ("5.1", "srd_5_1_es.json", "srd_catalog_5_1_es.json"),
        ("5.2.1", "srd_5_2_1_es.json", "srd_catalog_5_2_1_es.json"),
    ]:
        corpus = load(source)
        entries = sections(corpus, version) + spells(corpus, version) + weapons(corpus, version) + armor(corpus, version)
        catalog = {
            "format": "dnd-custom-aid.srd-catalog",
            "formatVersion": 1,
            "version": version,
            "edition": corpus["edition"],
            "license": corpus["license"],
            "attribution": corpus["attribution"],
            "entries": entries,
        }
        text = json.dumps(catalog, ensure_ascii=False, indent=1) + "\n"
        path = SRD / target
        if args.stats:
            kinds: dict[str, int] = {}
            for e in entries:
                kinds[e["kind"]] = kinds.get(e["kind"], 0) + 1
            print(version, kinds)
        if args.check:
            if not path.is_file() or path.read_text(encoding="utf-8") != text:
                print(f"{target} is out of date", file=sys.stderr)
                ok = False
        else:
            path.write_text(text, encoding="utf-8", newline="\n")
    return 0 if ok else 1


if __name__ == "__main__":
    raise SystemExit(main())
