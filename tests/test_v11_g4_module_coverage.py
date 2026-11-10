"""Fail-closed read-only V11 G4 module coverage tests."""
import importlib.util
import unittest
from pathlib import Path

p = Path(__file__).resolve().parents[1] / "scripts/check_v11_g4_module_coverage.py"
spec = importlib.util.spec_from_file_location("v11g4", p)
m = importlib.util.module_from_spec(spec)
spec.loader.exec_module(m)
COMMIT, APK = "a" * 40, "b" * 64


def sample(state="NOT_VERIFIED", complete=False):
    src = {}
    if complete:
        src = {
            "legacy_48_sections": "COMPLETE",
            "golden_eight_pdfs": "COMPLETE",
            "conflicts": "COMPLETE",
            "legacy_decisions": [
                {"section": f"3.{i}", "status": "PASS"} for i in range(1, 49)
            ],
            "golden_pdf_cases": [
                {"file": f"golden{i}.pdf", "status": "PASS"} for i in range(8)
            ],
        }
    return {
        "gate": "PASS" if complete else "BLOCKED",
        "active_candidate": {"source_commit": COMMIT, "apk_sha256": APK},
        "source_reconciliation": src,
        "rules": [{
            "id": "P301", "area": "P3", "status": state,
            "evidence": {"candidate_commit": COMMIT, "apk_sha256": APK} if state == "PASS" else None,
            "stage_checks": [{
                "stage": "G3", "status": "PASS" if complete else "NOT_VERIFIED",
                "evidence": {"source_commit": COMMIT} if complete else None,
            }],
        }],
    }


class ModuleCoverageTests(unittest.TestCase):
    def test_pending_not_promoted_or_mutated(self):
        d = sample()
        x = m.summarize(d, "P3")
        self.assertEqual(x["total_pending"], 1)
        self.assertFalse(x["selected_area_clear"])
        self.assertEqual(d["rules"][0]["status"], "NOT_VERIFIED")
        self.assertEqual(x["areas"][0]["unverified_candidate_stage_ids"], ["P301"])

    def test_pass_label_only_is_not_stage_approval(self):
        x = m.summarize(sample("PASS"), "P3")
        self.assertTrue(x["selected_area_rules_pass"])
        self.assertFalse(x["selected_area_clear"])
        self.assertFalse(x["ready"])

    def test_stale_candidate_stage_is_not_approved(self):
        d = sample("PASS", True)
        d["rules"][0]["stage_checks"][0]["evidence"]["source_commit"] = "c" * 40
        self.assertFalse(m.summarize(d, "P3")["selected_area_clear"])

    def test_incomplete_golden_reconciliation_blocks_report(self):
        d = sample("PASS", True)
        d["source_reconciliation"]["golden_pdf_cases"].pop()
        self.assertFalse(m.summarize(d)["ready"])

    def test_synthetic_complete_is_still_only_a_report(self):
        x = m.summarize(sample("PASS", True), "P3")
        self.assertTrue(x["ready"])
        self.assertTrue(x["selected_area_clear"])
        self.assertTrue(x["requires_authoritative_release_validation"])

    def test_duplicate_ids_rejected(self):
        d = sample()
        d["rules"].append(dict(d["rules"][0]))
        with self.assertRaises(ValueError):
            m.summarize(d)

    def test_bad_area_rejected(self):
        with self.assertRaises(ValueError):
            m.summarize(sample(), "NO_SUCH_AREA")

    def test_false_pass_rejected(self):
        d = sample("PASS")
        d["active_candidate"] = None
        with self.assertRaises(ValueError):
            m.summarize(d)


if __name__ == "__main__":
    unittest.main()
