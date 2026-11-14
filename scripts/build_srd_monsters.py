#!/usr/bin/env python3
"""SRD monsters as ready-to-use creatures (QA 0.7.0-preqa.2, observations C1 and D6).

The owner could not add creatures to a combat: there were none. This script reads the same layout
dumps as scripts/build_srd_layout.py (scripts/srd/SrdStructured.java over the official PDFs) and
turns every SRD stat block into the App's creature fields (CreaturePayload).

Output (committed): androidApp/src/main/assets/srd/srd_monsters_es.json
  {"format": "dnd-custom-aid.srd-monsters", "monsters": [{"edition", "name", "page", "payload"}]}

Usage: python scripts/build_srd_monsters.py --srd51 51.layout.txt --srd521 521.layout.txt
"""
from __future__ import annotations

import argparse
import json
import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from build_srd_layout import SEP, Line, join_text, read_layout  # noqa: E402

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "androidApp" / "src" / "main" / "assets" / "srd" / "srd_monsters_es.json"

SIZES = r"(Diminut[oa]|Pequeñ[oa]|Median[oa]|Grande|Enorme|Gargantues[co]a?|Gargantuesco)"
TYPE_LINE = re.compile(rf"^(.+?)\s+{SIZES}(?:\s+u?o?\s*{SIZES})?(?:\s*\(([^)]*)\))?\s*,\s*(.+)$")
TYPE_LINE_521 = re.compile(rf"^{SIZES}(?:\s+u?o?\s*{SIZES})?\s+(.+?)(?:\s*\(([^)]*)\))?\s*,\s*(.+)$")
SECTION = re.compile(r"^(Rasgos|Acciones|Acciones adicionales|Acción adicional|Reacciones|Reacción|Acciones legendarias|Acciones de guarida)$")
LABELS = [
    ("armor", r"(?:Clase de Armadura|CA):?"),
    ("hp", r"(?:Puntos de golpe|PG):?"),
    ("speed", r"Velocidad:?"),
    ("saves", r"Tiradas de salvación:?"),
    ("skills", r"Habilidades:?"),
    ("vuln", r"Vulnerabilidad(?:es)?(?: a daño)?:?"),
    ("resist", r"Resistencias?(?: a daño)?:?"),
    ("condimm", r"Inmunidad(?:es)? a estados:?"),
    ("immune", r"Inmunidad(?:es)?(?! a estados)(?: a daño)?:?"),
    ("senses", r"Sentidos:?"),
    ("languages", r"Idiomas:?"),
    ("cr", r"(?:Desafío|VD):?"),
    ("gear", r"Equipo:?"),
]
ABIL_51 = re.compile(r"(\d+)\s*\(\s*[−+\-]?\d+\s*\)")
ABIL_521 = re.compile(r"\b(Fue|Des|Con|Int|Sab|Car)\s+(\d+)\s+([−+\-]\d+)\s+([−+\-]\d+)")
ABIL_KEYS = {"Fue": "strength", "Des": "dexterity", "Con": "constitution", "Int": "intelligence", "Sab": "wisdom", "Car": "charisma"}


def monsters(pages: dict[int, list[Line]], edition: str) -> list[dict]:
    flat = [(p, ln) for p in sorted(pages) for ln in pages[p] if ln.y < 740]
    out: list[dict] = []
    i = 0
    while i < len(flat) - 2:
        page, head = flat[i]
        nxt = flat[i + 1][1].text.replace(SEP, " ").strip()
        start = head.all_bold and 10 <= head.size <= 16 and len(head.text) < 60 and (TYPE_LINE.match(nxt) or TYPE_LINE_521.match(nxt))
        ahead = " ".join(ln.text for _, ln in flat[i + 2: i + 6])
        if not (start and re.search(r"\b(CA|Clase de Armadura)\b", ahead)):
            i += 1
            continue
        name = head.text.strip()
        j = i + 2
        block: list[Line] = []
        while j < len(flat):
            ln = flat[j][1]
            if ln.all_bold and 10 <= ln.size <= 16 and j + 1 < len(flat):
                peek = flat[j + 1][1].text.replace(SEP, " ").strip()
                if TYPE_LINE.match(peek) or TYPE_LINE_521.match(peek):
                    break
            if ln.size >= 13.5 and not SECTION.match(ln.text.strip()):
                break
            block.append(ln)
            j += 1
        monster = parse_block(name, nxt, block, edition)
        if monster:
            monster["page"] = page
            out.append(monster)
        i = j
    return out


def parse_block(name: str, type_line: str, block: list[Line], edition: str) -> dict | None:
    p: dict = {}
    m = TYPE_LINE_521.match(type_line) if edition == "5.5e" else None
    if m:
        size, ctype, tag, align = m.group(1), m.group(3), m.group(4), m.group(5)
    else:
        m = TYPE_LINE.match(type_line)
        if not m:
            return None
        ctype, size, tag, align = m.group(1), m.group(2), m.group(4), m.group(5)
    p["size"] = size
    p["creatureType"] = ctype.strip() + (f" ({tag})" if tag else "")
    p["alignment"] = align.strip()

    fields: dict[str, str] = {}
    sections: dict[str, list[str]] = {}
    current_field: str | None = None
    current_section = "traits"
    ability_text = ""
    after_ability_header = False
    for ln in block:
        text = ln.text.replace(SEP, "  ").strip()
        if not text or re.fullmatch(r"(MOD\.?\s*SALV\.?\s*)+", text.replace(" ", " ")):
            continue
        if re.fullmatch(r"FUE\s+DES\s+CON\s+INT\s+SAB\s+CAR", text):
            after_ability_header = True
            continue
        if after_ability_header or ABIL_521.search(text):
            ability_text += " " + text
            after_ability_header = False
            if edition == "5.1" or len(ABIL_521.findall(ability_text)) >= 6 or len(ABIL_51.findall(ability_text)) >= 6:
                pass
            continue
        sec = SECTION.match(text.strip())
        if sec and (ln.size > 9.5 or ln.all_bold):
            current_section = {"Rasgos": "traits", "Acciones": "actions", "Acciones adicionales": "bonus", "Acción adicional": "bonus",
                               "Reacciones": "reactions", "Reacción": "reactions", "Acciones legendarias": "legendary", "Acciones de guarida": "lair"}[sec.group(1)]
            current_field = None
            continue
        label_hit = None
        if current_section == "traits" and not sections.get("traits"):
            for key, pattern in LABELS:
                lm = re.match(rf"^{pattern}\s*(.*)$", text)
                if lm:
                    label_hit = key
                    rest = lm.group(1)
                    if key == "armor" and "Iniciativa" in rest:
                        rest, init = rest.split("Iniciativa", 1)
                        fields["initiative"] = init.strip(": ").strip()
                    fields[key] = rest.strip()
                    current_field = key
                    break
        if label_hit:
            continue
        if current_field and ln.bold_prefix == 0 and current_section == "traits" and not sections.get("traits"):
            fields[current_field] = join_text(fields[current_field], text)
            continue
        current_field = None
        entries = sections.setdefault(current_section, [])
        if ln.bold_prefix > 0 or not entries:
            entries.append(text)
        else:
            entries[-1] = join_text(entries[-1], text)

    if "armor" not in fields or "hp" not in fields:
        return None
    am = re.match(r"(\d+)\s*(.*)", fields["armor"])
    if am:
        p["armorClass"] = int(am.group(1))
        details = am.group(2).strip().strip("()").strip()
        if details:
            p["armorClassDetails"] = details
    hm = re.match(r"(\d+)\s*\(?([^)]*)\)?", fields["hp"])
    if hm:
        p["hitPoints"] = int(hm.group(1))
        if hm.group(2).strip():
            p["hitDice"] = hm.group(2).strip()
    for key, target in (("speed", "speed"), ("skills", "skills"), ("vuln", "damageVulnerabilities"), ("resist", "damageResistances"),
                        ("immune", "damageImmunities"), ("condimm", "conditionImmunities"), ("senses", "senses"), ("languages", "languages")):
        if fields.get(key):
            p[target] = fields[key]
    if fields.get("cr"):
        cm = re.match(r"([\d/]+)\s*(.*)", fields["cr"])
        p["challengeRating"] = cm.group(1) if cm else fields["cr"]
        bonus = re.search(r"BC\s*\+?(\d+)", fields["cr"])
        if bonus:
            p["proficiencyBonus"] = int(bonus.group(1))

    abilities: dict[str, int] = {}
    saves: list[str] = []
    for code, score, mod, save in ABIL_521.findall(ability_text):
        abilities[ABIL_KEYS[code]] = int(score)
        if save.replace("−", "-") != mod.replace("−", "-"):
            saves.append(f"{code} {save}")
    if not abilities:
        scores = ABIL_51.findall(ability_text)
        if len(scores) >= 6:
            for key, score in zip(["strength", "dexterity", "constitution", "intelligence", "wisdom", "charisma"], scores[:6]):
                abilities[key] = int(score)
    if abilities:
        p["abilityScores"] = abilities
    if saves:
        p["savingThrows"] = ", ".join(saves)
    elif fields.get("saves"):
        p["savingThrows"] = fields["saves"]

    for key, target in (("traits", "traits"), ("actions", "actions"), ("bonus", "bonusActions"), ("reactions", "reactions"),
                        ("legendary", "legendaryActions"), ("lair", "lairActions")):
        if sections.get(key):
            p[target] = "\n".join(sections[key])
    notes = []
    if fields.get("initiative"):
        notes.append(f"Iniciativa {fields['initiative']}")
    if fields.get("gear"):
        notes.append(f"Equipo: {fields['gear']}")
    if notes:
        p["notes"] = " · ".join(notes)
    return {"edition": edition, "name": name, "payload": p}


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--srd51", type=Path, required=True)
    parser.add_argument("--srd521", type=Path, required=True)
    args = parser.parse_args()
    found: list[dict] = []
    for path, edition in ((args.srd51, "5e"), (args.srd521, "5.5e")):
        ms = monsters(read_layout(path), "5.1" if edition == "5e" else "5.5e")
        for m in ms:
            m["edition"] = edition
        names = {}
        for m in ms:
            names.setdefault(m["name"], m)
        found += list(names.values())
        print(f"{edition}: {len(names)} monsters")
    OUT.write_text(json.dumps({"format": "dnd-custom-aid.srd-monsters", "formatVersion": 1, "monsters": found}, ensure_ascii=False, indent=0), encoding="utf-8")
    return 0


if __name__ == "__main__":
    sys.exit(main())
