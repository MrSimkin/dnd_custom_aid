#!/usr/bin/env python3
"""Read-only V11 G4 modular coverage report. No automatic PASS promotion."""
import argparse
import json
from collections import Counter, defaultdict
from pathlib import Path

DEFAULT_MATRIX = Path("docs/pc-sheet-v11/qa/MATRIZ_CONTRATO_V11_PRE_QA.json")
STATES = {"NOT_VERIFIED", "FAIL", "BLOCKED", "PASS"}
SOURCE_KEYS = ("legacy_48_sections", "golden_eight_pdfs", "conflicts")


def sources_complete(src):
    """Summary-only precondition; release checker verifies full evidence."""
    legacy, golden = src.get("legacy_decisions", []), src.get("golden_pdf_cases", [])
    return (
        all(src.get(k) == "COMPLETE" for k in SOURCE_KEYS)
        and len(legacy) == 48 and len(golden) == 8
        and len({x.get("section") for x in legacy}) == 48
        and len({x.get("file") for x in golden}) == 8
        and all(x.get("status") == "PASS" for x in legacy + golden)
    )


def candidate_stage_verified(rule, candidate):
    """A PASS label alone is NOT a candidate-specific stage PASS."""
    if not isinstance(candidate, dict) or not candidate.get("source_commit"):
        return False
    if not candidate.get("apk_sha256") or rule.get("status") != "PASS":
        return False
    proof = rule.get("evidence")
    if not isinstance(proof, dict):
        return False
    if proof.get("candidate_commit") != candidate["source_commit"]:
        return False
    if proof.get("apk_sha256") != candidate["apk_sha256"]:
        return False
    stages = rule.get("stage_checks") or []
    return bool(stages) and all(
        c.get("status") == "PASS"
        and isinstance(c.get("evidence"), dict)
        and c["evidence"].get("source_commit") == candidate["source_commit"]
        for c in stages
    )

def summarize(data, selected=None):
    rules = data["rules"]
    ids = [rule["id"] for rule in rules]
    if len(set(ids)) != len(ids):
        raise ValueError("duplicate contract rule IDs")
    areas = sorted({rule["area"] for rule in rules})
    if selected and selected not in areas:
        raise ValueError(f"unknown area {selected!r}; valid: {', '.join(areas)}")
    candidate = data.get("active_candidate")
    sources = data.get("source_reconciliation") or {}
    source_complete = sources_complete(sources)
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
            "unverified_candidate_stage_ids": [r["id"] for r in subset if not candidate_stage_verified(r, candidate)],
            "stage_checks": [
                {"stage": stage, "status": state, "count": count}
                for (stage, state), count in sorted(stages.items())
            ],
        })
    checked_all = all(candidate_stage_verified(r, candidate) for r in rules)
    checked_selected = all(
        candidate_stage_verified(r, candidate) for r in rules
        if selected is None or r["area"] == selected
    )
    return {
        "candidate_present": isinstance(candidate, dict) and bool(candidate),
        "gate": data.get("gate"),
        "source_reconciliation": {k: sources.get(k) for k in SOURCE_KEYS},
        "source_reconciliation_complete": source_complete,
        "pending_legacy_sections": sum(x.get("status") != "PASS" for x in sources.get("legacy_decisions", [])),
        "pending_golden_pdfs": sum(x.get("status") != "PASS" for x in sources.get("golden_pdf_cases", [])),
        "areas": output,
        "total_rules": sum(x["total"] for x in output),
        "total_pending": sum(len(x["pending_ids"]) for x in output),
        "total_unverified_candidate_stages": sum(
            len(x["unverified_candidate_stage_ids"]) for x in output
        ),
        "ready": data.get("gate") == "PASS" and source_complete and checked_all,
        "selected_area_rules_pass": all(
            r["status"] == "PASS" for r in rules
            if selected is None or r["area"] == selected
        ),
        "selected_area_clear": source_complete and checked_selected,
        "requires_authoritative_release_validation": True,
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
    if args.strict and not (report["selected_area_clear"] if args.area else report["ready"]):
        raise SystemExit(1)

if __name__ == "__main__":
    main()
