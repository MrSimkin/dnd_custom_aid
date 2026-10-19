#!/usr/bin/env python3
"""Read-only V11 G4 modular coverage report. No automatic PASS promotion."""
import argparse
import json
from collections import Counter, defaultdict
from pathlib import Path

DEFAULT_MATRIX = Path("docs/pc-sheet-v11/qa/MATRIZ_CONTRATO_V11_PRE_QA.json")
STATES = {"NOT_VERIFIED", "FAIL", "BLOCKED", "PASS"}

def summarize(data, selected=None):
    rules = data["rules"]
    ids = [rule["id"] for rule in rules]
    if len(set(ids)) != len(ids):
        raise ValueError("duplicate contract rule IDs")
    areas = sorted({rule["area"] for rule in rules})
    if selected and selected not in areas:
        raise ValueError(f"unknown area {selected!r}; valid: {', '.join(areas)}")
    candidate = data.get("active_candidate")
    groups = defaultdict(list)
    for rule in rules:
        if rule["status"] not in STATES:
            raise ValueError(f"unknown status in {rule['id']}")
        if rule["status"] == "PASS" and (not candidate or not rule.get("evidence")):
            raise ValueError(f"unsupported PASS in {rule['id']}: missing candidate/evidence")
        for check in rule.get("stage_checks", []):
            if check["status"] not in STATES:
                raise ValueError(f"invalid stage status in {rule['id']}")
        groups[rule["area"]].append(rule)
    output = []
    for area in areas:
        if selected and selected != area:
            continue
        subset = groups[area]
        statuses = Counter(r["status"] for r in subset)
        stages = Counter((c["stage"], c["status"]) for r in subset for c in r.get("stage_checks", []))
        output.append({
            "area": area,
            "total": len(subset),
            "statuses": {s: statuses[s] for s in sorted(STATES)},
            "pending_ids": [r["id"] for r in subset if r["status"] != "PASS"],
            "stage_checks": [
                {"stage": stage, "status": state, "count": count}
                for (stage, state), count in sorted(stages.items())
            ],
        })
    return {
        "candidate_present": candidate is not None,
        "gate": data.get("gate"),
        "source_reconciliation": {
            k: data.get("source_reconciliation", {}).get(k)
            for k in ("legacy_48_sections", "golden_eight_pdfs", "conflicts")
        },
        "areas": output,
        "total_rules": sum(x["total"] for x in output),
        "total_pending": sum(len(x["pending_ids"]) for x in output),
        "ready": bool(candidate) and all(
            r["status"] == "PASS" for r in rules
        ) and data.get("gate") == "PASS",
    }

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--matrix", type=Path, default=DEFAULT_MATRIX)
    parser.add_argument("--area", help="Only one module (e.g. P3, C-N3, C2)")
    parser.add_argument("--strict", action="store_true", help="Exit 1 while selected rules remain pending")
    args = parser.parse_args()
    try:
        report = summarize(json.loads(args.matrix.read_text(encoding="utf-8")), args.area)
    except (OSError, ValueError, KeyError, TypeError) as exc:
        parser.error(str(exc))
    print(json.dumps(report, ensure_ascii=False, indent=2))
    if args.strict and (report["total_pending"] or not report["ready"]):
        raise SystemExit(1)

if __name__ == "__main__":
    main()
