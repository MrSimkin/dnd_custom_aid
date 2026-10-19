"""Read-only G4 module report: no false PASS or destructive status writes."""
import importlib.util
import unittest
from pathlib import Path

PATH = Path(__file__).resolve().parents[1] / "scripts/check_v11_g4_module_coverage.py"
spec = importlib.util.spec_from_file_location("v11g4", PATH)
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)

def sample(status="NOT_VERIFIED", candidate=None, evidence=None):
    return {"gate": "BLOCKED", "active_candidate": candidate,
            "source_reconciliation": {},
            "rules": [{"id": "P301", "area": "P3", "status": status,
                       "evidence": evidence, "stage_checks": [
                           {"stage": "G3", "status": "NOT_VERIFIED"}]}]}

class TestG4ModuleCoverage(unittest.TestCase):
    def test_pending_is_not_promoted(self):
        source = sample()
        result = module.summarize(source)
        self.assertEqual(result["total_pending"], 1)
        self.assertFalse(result["ready"])
        self.assertEqual(source["rules"][0]["status"], "NOT_VERIFIED")

    def test_area_and_stage_are_visible(self):
        result = module.summarize(sample(), "P3")
        self.assertEqual(result["areas"][0]["pending_ids"], ["P301"])
        self.assertEqual(result["areas"][0]["stage_checks"][0]["stage"], "G3")

    def test_unknown_area_rejected(self):
        with self.assertRaises(ValueError):
            module.summarize(sample(), "NO_SUCH_AREA")

    def test_false_pass_rejected(self):
        with self.assertRaises(ValueError):
            module.summarize(sample("PASS"))

    def test_duplicate_rule_rejected(self):
        data = sample()
        data["rules"].append(dict(data["rules"][0]))
        with self.assertRaises(ValueError):
            module.summarize(data)

if __name__ == "__main__":
    unittest.main()
