"""Tidies an owner package (owner, 2026-11-18) without touching what links the objects together.

What the agent saw while walking the real App with the Campus Celeste set:

- PNJ whose name carries their description («Sabria Leth. Mujer alta de piel morena»), so lists and
  the table cards read as a paragraph instead of a name;
- one PNJ whose name is a whole sentence;
- «Función / tipo | …» left over from the source document's table in «Concepto / rol».

It only rewrites names and text. Keys stay as they are, so every link between scenes, places and
PNJ keeps working and loading the package again updates the same objects.

    python scripts/clean_owner_package.py "D:/.../paquete.json"            # says what it would change
    python scripts/clean_owner_package.py "D:/.../paquete.json" --write    # writes it, with a .bak
"""

from __future__ import annotations

import argparse
import json
import re
import shutil
import sys
from pathlib import Path

sys.stdout.reconfigure(encoding="utf-8")

# «Nombre Apellido. Resto de la descripción»: a name of up to four words, then a sentence.
NAME_THEN_DESCRIPTION = re.compile(r"^([^.·|]{3,32}?)\.\s+(.{4,})$")
# A sentence as a name: «Barek Surn atiende turnos y conoce…».
VERB_IN_NAME = re.compile(r"^((?:[A-ZÁÉÍÓÚÑ][\wáéíóúñ'’-]*\s){1,3})((?:atiende|lleva|dirige|cuida|vende|regenta|trabaja|conoce|administra)\b.*)$")
# «Etiqueta | valor» left over from a table of the source document.
TABLE_LABEL = re.compile(r"^\s*[^|\n]{2,40}\s\|\s")

FAMILIES = ("creatures", "places", "npcs", "dungeons", "scenes", "encounters", "handouts", "notes", "rules")


def split_name(name: str) -> tuple[str, str] | None:
    """The name alone and the description that was glued to it, or None when the name is fine."""
    sentence = VERB_IN_NAME.match(name)
    if sentence:
        return sentence.group(1).strip(), sentence.group(2).strip()
    pieces = NAME_THEN_DESCRIPTION.match(name)
    if pieces and len(pieces.group(1).split()) <= 4:
        return pieces.group(1).strip(), pieces.group(2).strip()
    return None


def add_text(payload: dict, field: str, text: str) -> None:
    current = (payload.get(field) or "").strip()
    if text.lower() in current.lower():
        return
    payload[field] = f"{text}. {current}".strip() if current else text


def clean(package: dict) -> list[str]:
    changes: list[str] = []

    for npc in package.get("npcs", []):
        split = split_name(npc.get("name", ""))
        if split:
            name, description = split
            payload = npc.setdefault("payload", {})
            # A description of what they look like goes with the first impression; anything else is
            # what they do, which belongs with the role.
            field = "appearanceFirstImpression" if re.match(r"^(?:[A-ZÁÉÍÓÚÑ]?[a-záéíóúñ]+\s)?(?:mujer|hombre|varón|varon|elfa|elfo|enana|enano|humana|humano|semielfa|semielfo|mediana|mediano|dracónid|orca|orco|gnoma|gnomo|tieflin)", description, re.I) else "notes"
            add_text(payload, field, description)
            changes.append(f"PNJ «{npc['name']}» → «{name}» (lo demás pasó a {field})")
            npc["name"] = name

    for family in FAMILIES:
        for item in package.get(family, []):
            payload = item.get("payload")
            if not isinstance(payload, dict):
                continue
            for field, value in list(payload.items()):
                if isinstance(value, str) and TABLE_LABEL.match(value):
                    cleaned = TABLE_LABEL.sub("", value, count=1).strip()
                    payload[field] = cleaned
                    changes.append(f"{family}: «{item.get('name', '?')}» {field}: se quitó la etiqueta de la tabla")

    return changes


def main() -> int:
    parser = argparse.ArgumentParser(description="Ordena nombres y textos de un paquete del propietario.")
    parser.add_argument("package", type=Path, help="El archivo .json del paquete.")
    parser.add_argument("--write", action="store_true", help="Guarda los cambios (deja una copia .bak).")
    arguments = parser.parse_args()

    package = json.loads(arguments.package.read_text(encoding="utf-8"))
    changes = clean(package)

    if not changes:
        print("Nada que ordenar: los nombres y los textos ya están limpios.")
        return 0

    print(f"{len(changes)} cambios:")
    for line in changes:
        print(f"  {line}")

    if not arguments.write:
        print("\nNada se guardó. Repite con --write para aplicarlo (deja una copia .bak).")
        return 0

    backup = arguments.package.with_suffix(arguments.package.suffix + ".bak")
    if not backup.exists():
        shutil.copy2(arguments.package, backup)
    arguments.package.write_text(json.dumps(package, ensure_ascii=False, indent=1), encoding="utf-8")
    print(f"\nGuardado. La copia anterior quedó en {backup.name}.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
