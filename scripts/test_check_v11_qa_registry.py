#!/usr/bin/env python3
"""Regression tests for the QA-record guard itself: it must fail closed."""
import copy
import json
import tempfile
import unittest
from pathlib import Path
from check_v11_qa_registry import validate, REL

ROOT = Path(__file__).resolve().parent.parent

class QaRegistryGuardTest(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.root = Path(self.tmp.name)
        origin = json.loads((ROOT / REL).read_text(encoding="utf-8"))
        self.original = origin
        self.data = copy.deepcopy(origin)
        target = self.root / REL
        target.parent.mkdir(parents=True)
        source = ROOT / origin["legacy_acceptance_matrix"]["path"]
        dest = self.root / origin["legacy_acceptance_matrix"]["path"]
        dest.parent.mkdir(parents=True)
        dest.write_bytes(source.read_bytes())
        self.target = target
        self.write()

    def tearDown(self):
        self.tmp.cleanup()

    def write(self):
        self.target.write_text(json.dumps(self.data, ensure_ascii=False, indent=2), encoding="utf-8")

    def check_fails(self, needle):
        errors = validate(self.root)
        self.assertTrue(any(needle in e for e in errors), errors)

    def test_original_unresolved_registry_is_valid(self):
        self.assertEqual(validate(self.root), [])

    def test_delete_owner_observation_fails(self):
        self.data["observations"].pop(0)
        self.write()
        self.check_fails("observations were deleted")

    def test_synthetic_verified_status_fails(self):
        record = self.data["observations"][0]
        record["status"] = "CANDIDATE_VERIFIED"
        record["history"].append({"state": "CANDIDATE_VERIFIED", "kind": "PRETEND"})
        self.write()
        self.check_fails("no genuine code/resource change")

    def test_stale_old_owner_text_fails_append_only(self):
        self.data["observations"][0]["original_text"] = "edited away"
        self.write()
        errors = validate(self.root, prior=self.original)
        self.assertTrue(any("Original testimony overwritten" in e for e in errors), errors)

    def test_missing_mara_inventory_id_fails(self):
        self.data["legacy_acceptance_matrix"]["items"].pop(3)
        self.write()
        self.check_fails("Mara owner observations lost")

    def test_qa_release_without_actual_artifact_fails(self):
        self.data["release_gate"] = "READY_FOR_OWNER_QA"
        self.write()
        self.check_fails("Runtime QA cannot proceed without identifiable build")

    def test_rcr_reset_or_excess_rejected(self):
        self.data["rcr_budget"]["global_structural_recoveries_used"] = 3
        self.write()
        self.check_fails("RCR-1 global maximum")

if __name__ == "__main__":
    unittest.main()
