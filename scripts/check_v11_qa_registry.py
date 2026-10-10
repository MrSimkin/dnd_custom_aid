#!/usr/bin/env python3
"""Fail-closed QA ledger checker. Formal evidence checks are not PDF/runtime tests."""
from __future__ import annotations
import argparse
import json
import re
import subprocess
import sys
from pathlib import Path

REL = "docs/pc-sheet-v11/qa/REGISTRO_QA_V11.json"
SHA256 = re.compile(r"^[0-9a-f]{64}$")
SHA = re.compile(r"^[0-9a-f]{40}$")
STATES = {"OPEN", "TRIAGED", "IMPLEMENTED_UNVERIFIED", "CANDIDATE_VERIFIED", "OWNER_ACCEPTED", "BLOCKED"}
MARA = {f"M50800-{i:02d}" for i in range(1, 33)}
FAMILIES = {"CLASSIC_DND_STYLE", "CUSTOM_V1", "CUSTOM_V2_PER_ATTRIBUTE", "CUSTOM_V2_PER_ABILITY"}
# Verbatim owner statements, independently frozen from the mutable QA registry.
ORIGINAL_OWNER_V11 = {
    "V11-QA-001": "casi casi, un poco mas arriba y a la ferecha y estamos perfectos",
    "V11-QA-002": "si, aunque todavia hay espacios vacios, ver pagina 5, no hay que hacer otra prueba para este en particular, pero solo asegurarse que cuando se haga la pieza, no deje huecos",
}

def nonempty(x):
    return isinstance(x, str) and bool(x.strip())

def hash_ok(x, pattern=SHA256):
    return isinstance(x, str) and bool(pattern.fullmatch(x))

def validate(root: Path, gate="record", prior=None):
    problems = []
    def check(ok, message):
        if not ok:
            problems.append(message)

    try:
        d = json.loads((root / REL).read_text(encoding="utf-8"))
    except (OSError, ValueError) as e:
        return [f"Cannot load QA registry: {e}"]

    check(d.get("schema_version") == 1, "Unexpected QA schema version")
    gold = d.get("visual_baseline") or {}
    check(gold.get("owner_visual_approved") is True and hash_ok(gold.get("sha256")), "Visual golden authority/hash missing")
    mat = d.get("legacy_acceptance_matrix") or {}
    source_file = mat.get("path")
    check(nonempty(source_file), "Mara original matrix missing")
    if nonempty(source_file) and (root / source_file).is_file():
        rows = {}
        for line in (root / source_file).read_text(encoding="utf-8").splitlines():
            m = re.match(r"^\|\s*(M50800-\d+)\s*\|(.+)$", line)
            if m:
                rows[m.group(1)] = [s.strip() for s in m.group(2).split("|")]
        check(set(rows) == MARA, f"Mara original ID set changed: {sorted(MARA ^ set(rows))}")
        entries = mat.get("items") or []
        ids = [x.get("id") for x in entries]
        check(len(ids) == len(set(ids)), "Duplicate Mara IDs")
        check(set(ids) == MARA, f"Mara owner observations lost: {sorted(MARA ^ set(ids))}")
        for entry in entries:
            src = rows.get(entry.get("id"))
            if src:
                check(entry.get("area") == src[0] and entry.get("historical_status") == src[4] and entry.get("source_file") == source_file, f"Historical Mara evidence edited: {entry.get('id')}")
    else:
        check(False, f"Source Mara matrix not accessible: {source_file}")

    rounds = d.get("qa_rounds") or []
    round_ids = [r.get("id") for r in rounds]
    check(len(round_ids) == len(set(round_ids)), "Duplicate QA round")
    round_map = {r.get("id"): r for r in rounds}
    records = d.get("observations") or []
    ids = [o.get("id") for o in records]
    check(len(ids) == len(set(ids)), "Duplicate QA ID")
    check({"V11-QA-001", "V11-QA-002"}.issubset(ids), "Approved visual review corrections missing")
    indexed = {o.get("id"): o for o in records}
    for id_, original in ORIGINAL_OWNER_V11.items():
        check(indexed.get(id_, {}).get("original_text") == original, f"Owner's original verbatim testimony changed: {id_}")
    for r in rounds:
        reported = r.get("reported_ids") or []
        check(len(set(reported)) == len(reported), f"QA round {r.get('id')}: duplicate ID")
        check(all(x in indexed for x in reported), f"QA round {r.get('id')}: owner observations absent")
        if r.get("kind") == "runtime":
            check(hash_ok(r.get("artifact_sha256")) and hash_ok(r.get("pdf_sha256")) and hash_ok(r.get("source_commit"), SHA), f"Runtime round {r.get('id')}: candidate identity incomplete")
    c = d.get("active_candidate")
    if c is not None:
        check(isinstance(c, dict), "active_candidate must be null or object")
        if isinstance(c, dict):
            check(hash_ok(c.get("source_commit"), SHA) and hash_ok(c.get("apk_sha256")), "Candidate lacks source/build hashes")
            check(all(nonempty(c.get(k)) for k in ("version_name", "build_id", "artifact_ref")), "Candidate lacks version/build/artefact identity")
            vc = c.get("version_code")
            check(type(vc) is int and vc > 50800, "Candidate version_code not a fresh unique build > 50800")
    for o in records:
        item, state = o.get("id"), o.get("status")
        check(state in STATES, f"{item}: invalid status {state}")
        check(all(nonempty(o.get(k)) for k in ("original_text", "source_locator", "expected", "target_scope", "session_id", "page_hint")), f"{item}: original owner observation missing")
        check(o.get("session_id") in round_map and item in round_map.get(o.get("session_id"), {}).get("reported_ids", []), f"{item}: missing QA-round reference")
        history = o.get("history") or []
        check(bool(history) and history[0].get("kind") == "OWNER_REPORTED" and history[-1].get("state") == state, f"{item}: status/history mismatch or original report missing")
        if state in {"TRIAGED", "IMPLEMENTED_UNVERIFIED", "CANDIDATE_VERIFIED", "OWNER_ACCEPTED"}:
            p = o.get("plan") or {}
            check(all(nonempty(p.get(k)) for k in ("cause", "reproducer", "red_test", "acceptance", "target_gate")) and bool(p.get("production_paths")), f"{item}: must have a concrete correction plan before code changes")
        if state in {"IMPLEMENTED_UNVERIFIED", "CANDIDATE_VERIFIED", "OWNER_ACCEPTED"}:
            imp = o.get("implementation") or {}
            check(hash_ok(imp.get("commit"), SHA) and bool(imp.get("changed_production_paths")) and nonempty(imp.get("diff_ref")), f"{item}: no genuine code/resource change or commit")
            check(nonempty(imp.get("red_test_result")) and nonempty(imp.get("green_test_result")), f"{item}: no red-to-green proof")
            for changed in imp.get("changed_production_paths", []):
                check(changed.startswith(("shared/", "androidApp/", "desktopApp/", "assets/")), f"{item}: evidence is documentation/test only ({changed})")
        if state in {"CANDIDATE_VERIFIED", "OWNER_ACCEPTED"}:
            proof = o.get("proof") or {}
            check(isinstance(c, dict), f"{item}: verified without active build")
            check(hash_ok(proof.get("pdf_sha256")) and hash_ok(proof.get("apk_sha256")), f"{item}: no PDF/APK SHA proof")
            check(all(nonempty(proof.get(k)) for k in ("candidate_commit", "independent_review_ref", "reviewed_page", "actual_result", "fixture_id")), f"{item}: incomplete independent runtime evidence")
            if isinstance(c, dict):
                check(proof.get("candidate_commit") == c.get("source_commit") and proof.get("apk_sha256") == c.get("apk_sha256") and proof.get("version_code") == c.get("version_code"), f"{item}: stale proof from a DIFFERENT build")
        if state == "OWNER_ACCEPTED":
            a = o.get("owner_acceptance") or {}
            check(a.get("explicit_approval") is True and nonempty(a.get("owner_message_ref")) and a.get("candidate_commit") == (c or {}).get("source_commit"), f"{item}: owner acceptance is not attributable to this candidate")
    b = d.get("rcr_budget") or {}
    used = b.get("global_structural_recoveries_used")
    check(type(used) is int and 0 <= used <= 2 and b.get("max_global") == 2, "RCR-1 global maximum exceeded or relaxed")
    recoveries = b.get("recoveries") or []
    check(len(recoveries) == used, "RCR-1 recovery count mismatch")
    check(len({(x.get("gate"), x.get("cause_id")) for x in recoveries}) == len(recoveries), "RCR-1 repeated gate/cause")
    for k, v in (b.get("per_gate_attempts") or {}).items():
        check(k in {f"G{i}" for i in range(7)} and type(v) is int and 0 <= v <= 3, f"Normal attempt limit exceeded: {k}")
    rel = d.get("release_gate")
    check(rel in {"NOT_BUILT", "READY_FOR_OWNER_QA", "READY_FOR_MERGE"}, "Invalid release state")
    if gate in {"owner_qa", "merge"} or rel in {"READY_FOR_OWNER_QA", "READY_FOR_MERGE"}:
        check(isinstance(c, dict), "Runtime QA cannot proceed without identifiable build")
        check(all(o.get("status") in {"CANDIDATE_VERIFIED", "OWNER_ACCEPTED"} for o in records if o.get("blocking")), "Unverified owner defect blocks QA handoff")
        r = d.get("regression_suite") or {}
        check(r.get("status") == "PASS" and r.get("latest_candidate_commit") == (c or {}).get("source_commit"), "Regression PASS missing or stale")
        observed = {x.get("family") for x in r.get("legacy_family_evidence", [])}
        check(FAMILIES.issubset(observed) and bool(r.get("v11_evidence")) and nonempty(r.get("independent_pdf_inspection")), "All-family real QA/PDF evidence missing")
    if gate == "merge" or rel == "READY_FOR_MERGE":
        check(rel == "READY_FOR_MERGE", "Merge requires explicit release state")
        check(all(o.get("status") == "OWNER_ACCEPTED" for o in records if o.get("blocking")), "Owner has not accepted all blocking QA issues")
    if prior is not None:
        old_obs = {o["id"]: o for o in prior.get("observations", [])}
        for old_id, old in old_obs.items():
            now = indexed.get(old_id)
            check(now is not None, f"Observation silently deleted: {old_id}")
            if now:
                check(all(now.get(k) == old.get(k) for k in ("original_text", "source_locator", "session_id")), f"Original testimony overwritten: {old_id}")
                oh, nh = old.get("history") or [], now.get("history") or []
                check(nh[:len(oh)] == oh, f"QA history deleted or rewritten: {old_id}")
        old_rounds = prior.get("qa_rounds") or []
        check(rounds[:len(old_rounds)] == old_rounds, "QA rounds overwritten instead of appended")
        check(mat.get("items") == (prior.get("legacy_acceptance_matrix") or {}).get("items"), "Mara source evidence rewritten")
        check(used >= (prior.get("rcr_budget") or {}).get("global_structural_recoveries_used", 0), "RCR budget counter illegally reset")
    return problems

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--repo-root", type=Path, default=Path(__file__).resolve().parent.parent)
    ap.add_argument("--gate", choices=["record", "owner_qa", "merge"], default="record")
    ap.add_argument("--compare-ref", help="Previously committed revision for append-only comparison, e.g. HEAD^")
    args = ap.parse_args()
    prior = None
    if args.compare_ref:
        p = subprocess.run(["git", "show", f"{args.compare_ref}:{REL}"], cwd=args.repo_root, capture_output=True, text=True)
        if p.returncode == 0:
            try:
                prior = json.loads(p.stdout)
            except ValueError as exc:
                sys.exit(f"Invalid previous QA ledger: {exc}")
        elif not any(t in p.stderr for t in ("does not exist", "exists on disk", "not in", "unknown revision")):
            sys.exit(f"Cannot inspect prior QA ledger: {p.stderr}")
    errors = validate(args.repo_root, args.gate, prior)
    for err in errors:
        print("FAIL:", err, file=sys.stderr)
    print(f"V11 QA registry: {'FAIL' if errors else 'PASS'} (formal control, not proof that APK/PDF works)")
    return 1 if errors else 0

if __name__ == "__main__":
    raise SystemExit(main())
