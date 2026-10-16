#!/usr/bin/env python3
"""Tests for fail-closed V11 G4 gate itself. No PDF PASS implied."""
import copy
import json
import tempfile
import unittest
from pathlib import Path

from check_v11_pre_qa_gate import MATRIX, QA_REGISTRY, EXPECTED_IDS, validate

ROOT = Path(__file__).resolve().parent.parent


class V11PreQaGateGuardTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.root = Path(self.temp.name)
        self.matrix = json.loads((ROOT / MATRIX).read_text(encoding="utf-8"))
        self.qa = json.loads((ROOT / QA_REGISTRY).read_text(encoding="utf-8"))
        (self.root / MATRIX).parent.mkdir(parents=True)
        (self.root / QA_REGISTRY).parent.mkdir(parents=True, exist_ok=True)
        self.write()

    def tearDown(self):
        self.temp.cleanup()

    def write(self):
        (self.root / MATRIX).write_text(
            json.dumps(self.matrix, ensure_ascii=False, indent=2), encoding="utf-8")
        (self.root / QA_REGISTRY).write_text(
            json.dumps(self.qa, ensure_ascii=False, indent=2), encoding="utf-8")

    def test_inventory_complete_but_no_qa_pass(self):
        self.assertGreaterEqual(len(EXPECTED_IDS), 100)
        self.assertEqual(validate(self.root), [])
        self.assertTrue(any("rules remain" in e for e in validate(self.root, True)))

    def test_rule_deletion_fail_closed(self):
        self.matrix["rules"] = [r for r in self.matrix["rules"] if r["id"] != "RSG02"]
        self.write()
        self.assertTrue(any("dropped required rules" in e for e in validate(self.root)))

    def test_duplicate_rule_fail_closed(self):
        self.matrix["rules"].append(copy.deepcopy(self.matrix["rules"][0]))
        self.write()
        self.assertTrue(any("duplicate rule" in e for e in validate(self.root)))

    def test_fake_pass_without_evidence_fails_even_in_inventory(self):
        self.matrix["rules"][0]["status"] = "PASS"
        self.write()
        self.assertTrue(any("PASS without structured evidence" in e for e in validate(self.root)))

    def test_owner_ready_without_g4_pass_blocked(self):
        self.qa["release_gate"] = "READY_FOR_OWNER_QA"
        self.write()
        errors = validate(self.root)
        self.assertTrue(any("release blocked" in e for e in errors), errors)

    def test_unreviewed_sources_block_even_all_status_pass(self):
        self.matrix["gate"] = "PASS"
        self.write()
        self.assertTrue(any("historic/golden/precedence crosscheck incomplete" in e
                            for e in validate(self.root)))

    def test_d0076_section_is_not_erasable(self):
        self.matrix["source_reconciliation"]["legacy_decisions"].pop()
        self.write()
        self.assertTrue(any("48 legacy decision sections" in e for e in validate(self.root)))

    def test_one_golden_pdf_is_not_erasable(self):
        self.matrix["source_reconciliation"]["golden_pdf_cases"].pop()
        self.write()
        self.assertTrue(any("eight golden PDF cases" in e for e in validate(self.root)))

    def test_legacy_expected_ids_are_immutable(self):
        for required in ("RSG01", "RSG02", "RSG03", "P301", "P302", "C207",
                         "FIN01", "QA05", "QA07", "QA08"):
            self.assertIn(required, EXPECTED_IDS)


if __name__ == "__main__":
    unittest.main()
