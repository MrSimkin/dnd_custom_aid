"""Owner point 11: what was pulled out of the SRD is checked, and what is incomplete is reported.

Reads the SRD files the App ships and reports what a DM would notice at the table: a monster with
no armour class, hit points or actions, a class that does not reach level 20 or has no saves, a
species or background with nothing in it.

Nothing here edits data. CI runs it to catch a build that makes the gaps worse: the counts live in
`docs/technical/SRD_REVISION_BASELINE.json`, and a count above its baseline fails. Use
`--update-baseline` after fixing (or knowingly accepting) a change.
"""

from __future__ import annotations

import argparse
import json
import sys
from pathlib import Path

sys.stdout.reconfigure(encoding="utf-8")

REPO = Path(__file__).resolve().parent.parent
SRD = REPO / "androidApp" / "src" / "main" / "assets" / "srd"
BASELINE = REPO / "docs" / "technical" / "SRD_REVISION_BASELINE.json"


def load(name: str) -> dict:
    return json.loads((SRD / name).read_text(encoding="utf-8"))


def blank(value) -> bool:
    return value is None or (isinstance(value, str) and not value.strip()) or value == [] or value == {}


def check_monsters() -> dict[str, list[str]]:
    monsters = load("srd_monsters_es.json")["monsters"]
    problems: dict[str, list[str]] = {
        "monstruo sin CA": [],
        "monstruo sin PV": [],
        "monstruo sin acciones": [],
        "monstruo sin valor de desafío": [],
    }
    for monster in monsters:
        payload = monster.get("payload", {})
        who = f"{monster.get('name', '?')} ({monster.get('edition', '?')})"
        if blank(payload.get("armorClass")):
            problems["monstruo sin CA"].append(who)
        if blank(payload.get("hitPoints")):
            problems["monstruo sin PV"].append(who)
        if blank(payload.get("actions")) and blank(payload.get("legendaryActions")) and blank(payload.get("traits")):
            problems["monstruo sin acciones"].append(who)
        if blank(payload.get("challengeRating")):
            problems["monstruo sin valor de desafío"].append(who)
    return problems


def check_rules() -> dict[str, list[str]]:
    rules = load("srd_rules_es.json")
    problems: dict[str, list[str]] = {
        "clase que no llega a nivel 20": [],
        "clase sin dado de golpe": [],
        "clase sin salvaciones": [],
        "clase sin habilidades para elegir": [],
        "subclase sin rasgos": [],
        "raza sin rasgos": [],
        "trasfondo sin habilidades": [],
        "dote sin texto": [],
    }
    for entry in rules.get("classes", []):
        who = f"{entry.get('name', '?')} ({entry.get('edition', '?')})"
        levels = entry.get("levels") or {}
        if len(levels) < 20:
            problems["clase que no llega a nivel 20"].append(f"{who}: {len(levels)} niveles")
        if blank(entry.get("hitDie")):
            problems["clase sin dado de golpe"].append(who)
        if blank(entry.get("saves")):
            problems["clase sin salvaciones"].append(who)
        if blank(entry.get("skillOptions")):
            problems["clase sin habilidades para elegir"].append(who)
    for entry in rules.get("subclasses", []):
        if blank(entry.get("features")):
            problems["subclase sin rasgos"].append(f"{entry.get('name', '?')} ({entry.get('edition', '?')})")
    for entry in rules.get("species", []):
        if blank(entry.get("traits")):
            problems["raza sin rasgos"].append(f"{entry.get('name', '?')} ({entry.get('edition', '?')})")
    for entry in rules.get("backgrounds", []):
        if blank(entry.get("skills")):
            problems["trasfondo sin habilidades"].append(f"{entry.get('name', '?')} ({entry.get('edition', '?')})")
    for entry in rules.get("feats", []):
        if blank(entry.get("text")):
            problems["dote sin texto"].append(f"{entry.get('name', '?')} ({entry.get('edition', '?')})")
    return problems


def main() -> int:
    parser = argparse.ArgumentParser(description="Revisa lo que se sacó del SRD y dice qué quedó incompleto.")
    parser.add_argument("--update-baseline", action="store_true", help="Guarda las cuentas actuales como referencia.")
    parser.add_argument("--show", type=int, default=6, help="Cuántos nombres mostrar por problema.")
    arguments = parser.parse_args()

    problems = {**check_monsters(), **check_rules()}
    counts = {name: len(items) for name, items in problems.items()}

    print("Revisión del contenido del SRD")
    print("=" * 34)
    for name, items in problems.items():
        if not items:
            print(f"  OK   {name}: ninguno")
            continue
        shown = ", ".join(items[: arguments.show])
        more = "" if len(items) <= arguments.show else f" y {len(items) - arguments.show} más"
        print(f"  AVISO {name}: {len(items)} ({shown}{more})")

    if arguments.update_baseline:
        BASELINE.parent.mkdir(parents=True, exist_ok=True)
        BASELINE.write_text(json.dumps(counts, ensure_ascii=False, indent=2, sort_keys=True) + "\n", encoding="utf-8")
        print(f"\nReferencia guardada en {BASELINE.relative_to(REPO)}")
        return 0

    if not BASELINE.exists():
        print(f"\nNo hay referencia todavía: crea {BASELINE.relative_to(REPO)} con --update-baseline.")
        return 0

    baseline = json.loads(BASELINE.read_text(encoding="utf-8"))
    worse = {name: (counts[name], baseline.get(name, 0)) for name in counts if counts[name] > baseline.get(name, 0)}
    if worse:
        print("\nEsta versión empeora el contenido:")
        for name, (now, before) in sorted(worse.items()):
            print(f"  {name}: {before} -> {now}")
            for item in problems[name][: arguments.show]:
                print(f"      {item}")
        print("\nArregla el dato, o acepta el cambio con: python scripts/check_srd_content.py --update-baseline")
        return 1

    better = {name: (counts[name], baseline.get(name, 0)) for name in counts if counts[name] < baseline.get(name, 0)}
    if better:
        print("\nMejoró desde la última referencia:")
        for name, (now, before) in sorted(better.items()):
            print(f"  {name}: {before} -> {now}")
    print("\nSin empeoramientos respecto de la referencia.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
