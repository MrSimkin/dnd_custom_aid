#!/usr/bin/env python3
"""Structured SRD rules for creating a PC (QA 0.7.0-preqa.2, observation J1).

The owner found the PC wizard «horrible»: it copied only names. This script turns the formatted SRD
(androidApp/src/main/assets/srd/srd_*_es.layout.json, made by scripts/build_srd_layout.py) into the
facts a new PC needs: per class its hit die, saving throws, skill choices, armour/weapon/tool
training, starting equipment, multiclass text and features by level; per subclass its features;
per race (species) its ability increases (5e), speed and traits; per background its ability
choices (5.5e), feat, skills, tools and equipment; and every feat's text.

Output (committed): androidApp/src/main/assets/srd/srd_rules_es.json
Usage: python scripts/build_srd_rules.py
"""
from __future__ import annotations

import json
import re
import sys
import unicodedata
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "androidApp" / "src" / "main" / "assets" / "srd"
ABILITIES = {"fuerza": "STR", "destreza": "DEX", "constitución": "CON", "constitucion": "CON", "inteligencia": "INT", "sabiduría": "WIS", "sabiduria": "WIS", "carisma": "CHA"}
SKILLS = ["Acrobacias", "Atletismo", "Arcanos", "Engaño", "Historia", "Interpretación", "Intimidación", "Investigación", "Juego de manos",
          "Medicina", "Naturaleza", "Percepción", "Perspicacia", "Persuasión", "Religión", "Sigilo", "Supervivencia", "Trato con animales",
          "Arcano", "C. Arcano", "Conocimiento Arcano"]
NUMBERS = {"una": 1, "uno": 1, "dos": 2, "tres": 3, "cuatro": 4, "cinco": 5}


def norm(text: str) -> str:
    text = unicodedata.normalize("NFKD", text.lower())
    return re.sub(r"[^a-z0-9]+", " ", "".join(c for c in text if not unicodedata.combining(c))).strip()


def plain(md: str) -> str:
    return re.sub(r"\*\*", "", md)


def abilities_in(text: str) -> list[str]:
    found = []
    for word in re.findall(r"[A-Za-zÁÉÍÓÚáéíóú]+", text):
        code = ABILITIES.get(word.lower())
        if code and code not in found:
            found.append(code)
    return found


def skills_in(text: str) -> list[str]:
    out = []
    for skill in SKILLS:
        if re.search(rf"\b{re.escape(skill)}\b", text, re.I) and skill not in out:
            out.append("Arcanos" if skill in ("Arcano", "C. Arcano", "Conocimiento Arcano") else skill)
    return list(dict.fromkeys(out))


def table_rows(md: str) -> list[list[str]]:
    rows = []
    for line in md.split("\n"):
        if line.startswith("|") and not re.fullmatch(r"\|(-+\|)+", line.strip()):
            rows.append([c.strip() for c in line.strip().strip("|").split("|")])
    return rows


def labeled(md: str) -> dict[str, str]:
    """«**Etiqueta:** valor» lines and two-column «| Etiqueta | valor |» rows."""
    out: dict[str, str] = {}
    for m in re.finditer(r"^\*\*([^*]+?):?\*\*\s*(.*)$", md, re.M):
        out.setdefault(m.group(1).strip().rstrip(":"), m.group(2).strip())
    for row in table_rows(md):
        if len(row) == 2 and row[0] and not row[0][0].isdigit():
            out.setdefault(row[0].rstrip(":"), row[1])
    return out


def equipment_options(text: str) -> dict[str, str]:
    """«Elige A o B: (A) …, o (B) 75 po» or 5e bullets «(a) … o (b) …» → {"A": …, "B": …}."""
    m = re.search(r"\(A\)\s*(.*?),?\s*o\s*\(B\)\s*(.*)$", text, re.S)
    if m:
        return {"A": m.group(1).strip().rstrip(","), "B": m.group(2).strip().rstrip(".")}
    return {"A": text.strip()} if text.strip() else {}


def sections(md: str) -> list[tuple[int, str, str]]:
    """(heading level, heading, text until the next heading of the same or a higher level)."""
    lines = md.split("\n")
    heads = [(i, len(l) - len(l.lstrip("#")), l.lstrip("#").strip()) for i, l in enumerate(lines) if l.startswith("#")]
    out = []
    for n, (i, level, title) in enumerate(heads):
        end = len(lines)
        for j, lvl, _ in heads[n + 1:]:
            if lvl <= level:
                end = j
                break
        out.append((level, title, "\n".join(lines[i + 1:end]).strip()))
    return out


def run_in_traits(md: str) -> list[dict]:
    """«**Nombre.** texto» and 5.1 «Nombre. texto» paragraphs as traits."""
    traits = []
    for para in md.split("\n\n"):
        para = para.strip()
        m = re.match(r"^\*\*([^*]{2,60}?)\.\*\*\s*(.*)$", para, re.S) or re.match(r"^([A-ZÁÉÍÓÚ][\wáéíóúñ ]{2,40}?)\.\s+(.+)$", para, re.S)
        if m and not para.startswith("|"):
            traits.append({"name": m.group(1).strip(), "text": plain(m.group(2)).strip()})
    return traits


def level_features(md: str) -> dict[int, list[str]]:
    levels: dict[int, list[str]] = {}
    for row in table_rows(md):
        if len(row) >= 3 and re.fullmatch(r"\d{1,2}", row[0]) and re.fullmatch(r"\+\d", row[1]):
            names = [n.strip() for n in re.split(r",\s*", row[2]) if n.strip() and n.strip() != "—"]
            levels.setdefault(int(row[0]), names)
    return levels


def build_class(name: str, md: str, edition: str) -> dict:
    lab = labeled(md)
    get = lambda *keys: next((lab[k] for k in lab for want in keys if norm(k).startswith(norm(want))), "")
    hit = re.search(r"d(\d+)", get("Dado de puntos de golpe", "Dados de Golpe"))
    skills_text = get("Competencias en habilidades", "Habilidades")
    choose = re.search(r"(?:Elige|escoge)\s+(\w+)", skills_text, re.I)
    if edition == "5e":
        eq_lines = [l for l in md.split("\n") if l.startswith("• (a)") or l.startswith("• un") or l.startswith("• una") or l.startswith("• (a")]
        equipment = {"A": "; ".join(l[2:] for l in eq_lines)} if eq_lines else {}
    else:
        equipment = equipment_options(get("Equipo inicial"))
    multiclass = ""
    for level, title, text in sections(md):
        if norm(title).startswith("como personaje multiclase"):
            multiclass = plain(text)
    features: dict[str, str] = {}
    levels = level_features(md)
    wanted = {norm(f): f for names in levels.values() for f in names}
    for level, title, text in sections(md):
        t = re.sub(r"^Nivel\s+\d+\s*:\s*", "", title)
        key = norm(t)
        if key in wanted or any(key.startswith(w) for w in wanted):
            features.setdefault(t, plain(text)[:6000])
    return {
        "edition": edition, "name": name,
        "hitDie": int(hit.group(1)) if hit else 8,
        "primary": abilities_in(get("Característica principal")),
        "saves": abilities_in(get("Competencias en tiradas de salvación", "Tiradas de salvación")),
        "skillChoose": NUMBERS.get(choose.group(1).lower(), 2) if choose else 2,
        "skillOptions": skills_in(skills_text) if not re.search(r"cualquier|cualesquiera", skills_text, re.I) else SKILLS[:18],
        "armor": get("Entrenamiento con armaduras", "Armadura"),
        "weapons": get("Competencias con armas", "Armas"),
        "tools": get("Competencias con herramientas", "Herramientas"),
        "equipment": equipment,
        "multiclass": multiclass,
        "levels": {str(k): v for k, v in sorted(levels.items())},
        "features": features,
    }


def build_subclass(name: str, parent: str, md: str, edition: str) -> dict:
    feats = []
    for level, title, text in sections(md):
        m = re.match(r"^Nivel\s+(\d+)\s*:\s*(.+)$", title)
        if m:
            feats.append({"level": int(m.group(1)), "name": m.group(2).strip(), "text": plain(text)[:6000]})
        elif level >= 2 and edition == "5e":
            lm = re.search(r"nivel\s+(\d+)", text)
            feats.append({"level": int(lm.group(1)) if lm else 3, "name": title, "text": plain(text)[:6000]})
    if not feats:
        lm = re.search(r"nivel\s+(\d+)", md)
        feats = [{"level": int(lm.group(1)) if lm else 3, "name": name, "text": plain(md)[:6000]}]
    return {"edition": edition, "name": name, "className": parent, "features": feats}


def build_species(name: str, md: str, edition: str, parent: str = "") -> dict:
    traits = run_in_traits(md)
    increases: dict[str, int] = {}
    speed = ""
    size = ""
    for t in list(traits):
        n = norm(t["name"])
        if n.startswith("mejora de caracteristica") or n.startswith("aumento de puntuacion"):
            for word, amount in re.findall(r"(Fuerza|Destreza|Constitución|Inteligencia|Sabiduría|Carisma)[^.]*?aumenta(?:n)? en (\d)", t["text"]):
                increases[ABILITIES[word.lower()]] = int(amount)
            if "todas" in t["text"].lower() and not increases:
                increases = {k: 1 for k in ("STR", "DEX", "CON", "INT", "WIS", "CHA")}
            traits.remove(t)
        elif n in ("alineamiento", "edad", "idiomas", "tamano", "velocidad") or n.startswith("velocidad"):
            if n.startswith("velocidad"):
                speed = t["text"]
            if n.startswith("tamano"):
                size = t["text"]
            if n != "idiomas":
                traits.remove(t)
    lab = labeled(md)
    speed = speed or next((v for k, v in lab.items() if norm(k).startswith("velocidad")), "")
    size = size or next((v for k, v in lab.items() if norm(k).startswith("tamano")), "")
    meters = re.search(r"(\d+(?:,\d+)?)\s*m", speed)
    return {
        "edition": edition, "name": name, "parent": parent,
        "abilityIncreases": increases,
        "speedMeters": float(meters.group(1).replace(",", ".")) if meters else None,
        "size": size[:200],
        "traits": [t for t in traits if len(t["text"]) > 10][:20],
    }


def build_background(name: str, md: str, edition: str) -> dict:
    lab = labeled(md)
    get = lambda *keys: next((lab[k] for k in lab for want in keys if norm(k).startswith(norm(want))), "")
    feat = re.sub(r"\s*\(consulta.*?\)", "", get("Dote")).strip()
    return {
        "edition": edition, "name": name,
        "abilities": abilities_in(get("Puntuaciones de característica")),
        "feat": feat,
        "skills": skills_in(get("Competencias en habilidades", "Competencia en habilidades", "Habilidades")),
        "tools": get("Competencia con herramientas", "Competencias con herramientas", "Herramientas"),
        "languages": get("Idiomas"),
        "equipment": equipment_options(get("Equipo")),
        "features": [t for t in run_in_traits(md) if norm(t["name"]).startswith("rasgo") or edition == "5e"][:4],
    }


def main() -> int:
    out = {"format": "dnd-custom-aid.srd-rules", "formatVersion": 1, "classes": [], "subclasses": [], "species": [], "backgrounds": [], "feats": []}
    for version, edition in (("5_1", "5e"), ("5_2_1", "5.5e")):
        catalog = json.loads((ASSETS / f"srd_catalog_{version}_es.json").read_text(encoding="utf-8"))
        layout = json.loads((ASSETS / f"srd_{version}_es.layout.json").read_text(encoding="utf-8"))
        md = lambda kind, name: layout["entries"].get(f"{kind}|{name}", "")
        for e in catalog["entries"]:
            kind, name = e["kind"], e["name"]
            if kind == "CLASS":
                out["classes"].append(build_class(name, md(kind, name), edition))
            elif kind == "SUBCLASS":
                out["subclasses"].append(build_subclass(name, e.get("parent", ""), md(kind, name), edition))
            elif kind in ("SPECIES", "SUBSPECIES"):
                out["species"].append(build_species(name, md(kind, name) or e.get("body", ""), edition, e.get("parent", "") if kind == "SUBSPECIES" else ""))
            elif kind == "BACKGROUND":
                out["backgrounds"].append(build_background(name, md(kind, name) or e.get("body", ""), edition))
            elif kind == "FEAT":
                out["feats"].append({"edition": edition, "name": name, "text": plain(md(kind, name) or e.get("body", ""))[:6000]})
    (ASSETS / "srd_rules_es.json").write_text(json.dumps(out, ensure_ascii=False, indent=0), encoding="utf-8")
    for key in ("classes", "subclasses", "species", "backgrounds", "feats"):
        print(key, len(out[key]))
    return 0


if __name__ == "__main__":
    sys.exit(main())
