#!/usr/bin/env python3
"""V11 G4 contract compliance guard. Checks documented proof, NOT PDF pixels itself.

Inventory mode is a development-time CI check; release mode is deliberately
fail-closed until all source/visual rules have independent genuine-App proof
for the exact owner-QA candidate.
"""
from __future__ import annotations

import argparse
import json
import re
import sys
from pathlib import Path

MATRIX = "docs/pc-sheet-v11/qa/MATRIZ_CONTRATO_V11_PRE_QA.json"
QA_REGISTRY = "docs/pc-sheet-v11/qa/REGISTRO_QA_V11.json"
SHA256 = re.compile(r"^[0-9a-f]{64}$")
COMMIT = re.compile(r"^[0-9a-f]{40}$")
ALLOWED = {"NOT_VERIFIED", "FAIL", "BLOCKED", "PASS"}
EXPECTED_IDS = (
    {f"GLB{i:02d}" for i in range(1, 13)}
    | {f"IN{i:02d}" for i in range(1, 9)}
    | {f"P1{i:02d}" for i in range(1, 20)}
    | {f"RSG{i:02d}" for i in range(1, 7)}
    | {f"P2{i:02d}" for i in range(1, 13)}
    | {f"P3{i:02d}" for i in range(1, 7)}
    | {f"P4{i:02d}" for i in range(1, 11)}
    | {f"C2{i:02d}" for i in range(1, 10)}
    | {f"CN3{i:02d}" for i in range(1, 5)}
    | {f"BK{i:02d}" for i in range(1, 7)}
    | {f"FIN{i:02d}" for i in range(1, 5)}
    | {f"QA{i:02d}" for i in range(1, 9)}
)
# New mandatory rules may be added, never silently substituted for baseline IDs.
DECISION_SECTIONS = {f"3.{i}" for i in range(1, 49)}
GOLDEN_CASES = {
    "v11_comparador_C2_extremo.pdf",
    "v11_extremo_variante_A.pdf",
    "v11_extremo_variante_B.pdf",
    "v11_ordinario_6mas2_variante_A.pdf",
    "v11_ordinario_6mas2_variante_B.pdf",
    "v11_ordinario_6mas3_variante_A.pdf",
    "v11_ordinario_6mas3_variante_B.pdf",
    "v11_personaje_casi_vacio.pdf",
}
REQUIRED_EVIDENCE_FIELDS = (
    "candidate_commit", "apk_sha256", "pdf_sha256", "artifact_ref",
    "fixture_id", "page_or_scope", "independent_review_ref",
    "verifier", "observed_result",
)


def present(s: object) -> bool:
    return isinstance(s, str) and bool(s.strip())


def matches(s: object, pattern: re.Pattern[str]) -> bool:
    return isinstance(s, str) and bool(pattern.fullmatch(s))


def validate(root: Path, require_pass: bool = False) -> list[str]:
    errors: list[str] = []
    def check(condition: bool, error: str) -> None:
        if not condition:
            errors.append(error)

    try:
        data = json.loads((root / MATRIX).read_text(encoding="utf-8"))
    except (OSError, ValueError) as exc:
        return [f"V11 G4 cannot load contract matrix: {exc}"]
    try:
        qa = json.loads((root / QA_REGISTRY).read_text(encoding="utf-8"))
    except (OSError, ValueError) as exc:
        return [f"V11 G4 cannot load QA candidate authority: {exc}"]

    check(data.get("schema_version") == 1, "V11 G4 unexpected schema")
    records = data.get("rules")
    if not isinstance(records, list):
        return errors + ["V11 G4 rules must be a list"]
    ids = [r.get("id") for r in records if isinstance(r, dict)]
    check(len(ids) == len(records), "V11 G4 invalid non-object rule")
    check(len(ids) == len(set(ids)), "V11 G4 duplicate rule ID")
    check(EXPECTED_IDS.issubset(ids),
          f"V11 G4 dropped required rules: {sorted(EXPECTED_IDS - set(ids))}")
    check(len(ids) >= len(EXPECTED_IDS), "V11 G4 mandatory coverage truncated")
    check(set(data.get("valid_statuses") or ()) == ALLOWED,
          "V11 G4 statuses must not silently admit waivers")
    check(data.get("gate") in {"BLOCKED", "PASS"}, "V11 G4 invalid gate state")
    candidate = qa.get("active_candidate")
    matrix_candidate = data.get("active_candidate")
    if matrix_candidate is not None:
        check(isinstance(matrix_candidate, dict), "V11 G4 matrix candidate invalid")
        if isinstance(matrix_candidate, dict) and isinstance(candidate, dict):
            check(matrix_candidate.get("source_commit") == candidate.get("source_commit")
                  and matrix_candidate.get("apk_sha256") == candidate.get("apk_sha256"),
                  "V11 G4 matrix candidate differs from QA candidate")
        elif isinstance(matrix_candidate, dict):
            check(False, "V11 G4 cannot identify same active QA candidate")
    sources = data.get("source_reconciliation") or {}
    for key in ("legacy_48_sections", "golden_eight_pdfs", "conflicts"):
        check(sources.get(key) in {"PENDING_INDEPENDENT_CROSSCHECK",
                                   "PENDING_PHYSICAL_MATCH",
                                   "PENDING_PRIORITY_REVIEW", "COMPLETE"},
              f"V11 G4 source audit state invalid/missing: {key}")

    # Never allow a blanket "48 sections reviewed" checkbox to substitute
    # for 48 individual precedent decisions + all eight frozen visual cases.
    legacy = sources.get("legacy_decisions") or []
    golden = sources.get("golden_pdf_cases") or []
    legacy_ids = [e.get("section") for e in legacy if isinstance(e, dict)]
    golden_ids = [e.get("file") for e in golden if isinstance(e, dict)]
    check(len(legacy_ids) == len(legacy) and len(legacy_ids) == len(set(legacy_ids))
          and set(legacy_ids) == DECISION_SECTIONS,
          "V11 G4 48 legacy decision sections omitted/duplicated")
    check(len(golden_ids) == len(golden) and len(golden_ids) == len(set(golden_ids))
          and set(golden_ids) == GOLDEN_CASES,
          "V11 G4 eight golden PDF cases omitted/duplicated")
    for item in legacy:
        if not isinstance(item, dict):
            continue
        key = item.get("section")
        check(item.get("status") in {"NOT_VERIFIED", "PASS", "BLOCKED"},
              f"V11 G4 legacy {key}: invalid status")
        if item.get("status") == "PASS":
            check(item.get("disposition") in {"RETAIN", "SUPERSEDED_BY_V11"},
                  f"V11 G4 legacy {key}: no explicit retained/superseded decision")
            check(present(item.get("evidence")) and present(item.get("verifier")),
                  f"V11 G4 legacy {key}: PASS without independent trace")
    for item in golden:
        if not isinstance(item, dict):
            continue
        key = item.get("file")
        check(item.get("status") in {"NOT_VERIFIED", "PASS", "BLOCKED"},
              f"V11 G4 golden {key}: invalid status")
        if item.get("status") == "PASS":
            check(matches(item.get("candidate_pdf_sha256"), SHA256)
                  and present(item.get("comparison_ref"))
                  and present(item.get("verifier")),
                  f"V11 G4 golden {key}: PASS without genuine comparison")

    for record in records:
        if not isinstance(record, dict):
            continue
        rid = record.get("id")
        for field in ("id", "area", "obligation", "authority", "method", "scenarios"):
            check(present(record.get(field)), f"V11 G4 {rid}: missing {field}")
        status = record.get("status")
        check(status in ALLOWED, f"V11 G4 {rid}: illegal status {status}")
        if status == "PASS":
            ev = record.get("evidence")
            if not isinstance(ev, dict):
                errors.append(f"V11 G4 {rid}: PASS without structured evidence")
                continue
            for field in REQUIRED_EVIDENCE_FIELDS:
                check(present(ev.get(field)), f"V11 G4 {rid}: missing proof {field}")
            check(matches(ev.get("candidate_commit"), COMMIT),
                  f"V11 G4 {rid}: invalid commit SHA")
            check(matches(ev.get("apk_sha256"), SHA256)
                  and matches(ev.get("pdf_sha256"), SHA256),
                  f"V11 G4 {rid}: invalid PDF/APK hashes")
            check(ev.get("genuine_runtime") is True,
                  f"V11 G4 {rid}: proof must use real App output")
            check(ev.get("independent_inspection") is True,
                  f"V11 G4 {rid}: independent verification required")
            if isinstance(candidate, dict):
                check(ev.get("candidate_commit") == candidate.get("source_commit")
                      and ev.get("apk_sha256") == candidate.get("apk_sha256"),
                      f"V11 G4 {rid}: stale candidate")
            else:
                check(False, f"V11 G4 {rid}: no active candidate for PASS")

    declared_pass = data.get("gate") == "PASS"
    ready = (qa.get("release_gate") in {"READY_FOR_OWNER_QA", "READY_FOR_MERGE"})
    if require_pass or declared_pass or ready:
        check(isinstance(candidate, dict), "V11 G4 release without QA candidate")
        check(isinstance(matrix_candidate, dict), "V11 G4 release without matrix candidate")
        check(all(r.get("status") == "PASS" for r in records if isinstance(r, dict)),
              "V11 G4 release blocked: NOT VERIFIED / FAIL / BLOCKED rules remain")
        check(all(item.get("status") == "PASS" for item in legacy if isinstance(item, dict))
              and len(legacy) == len(DECISION_SECTIONS),
              "V11 G4 release blocked: unreviewed D0076 sections remain")
        check(all(item.get("status") == "PASS" for item in golden if isinstance(item, dict))
              and len(golden) == len(GOLDEN_CASES),
              "V11 G4 release blocked: unreviewed golden PDFs remain")
        check(all(sources.get(k) == "COMPLETE" for k in
                  ("legacy_48_sections", "golden_eight_pdfs", "conflicts")),
              "V11 G4 release blocked: historic/golden/precedence crosscheck incomplete")
        check(declared_pass, "V11 G4 release blocked: gate is not PASS")
        if isinstance(candidate, dict):
            check(matches(candidate.get("source_commit"), COMMIT)
                  and matches(candidate.get("apk_sha256"), SHA256),
                  "V11 G4 release candidate lacks verifiable identity")
    return errors


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--repo-root", type=Path, default=Path(__file__).resolve().parent.parent)
    parser.add_argument("--mode", choices=("inventory", "release"), default="inventory")
    args = parser.parse_args()
    errors = validate(args.repo_root, require_pass=args.mode == "release")
    if errors:
        for error in errors:
            print("FAIL:", error, file=sys.stderr)
        return 1
    print("PASS: V11 G4 inventory structure" if args.mode == "inventory"
          else "PASS: V11 G4 pre-owner-QA compliance gate")
    if args.mode == "inventory":
        print("CAUTION: inventory check DOES NOT mean rule compliance or owner QA readiness")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
