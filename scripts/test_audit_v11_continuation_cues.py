#!/usr/bin/env python3
"""Red/green tests for the V11 continuation-cue oracle (C341)."""
import unittest

from audit_v11_continuation_cues import inspect_pages


class ContinuationCueTests(unittest.TestCase):
    def test_bidirectional_cues_with_bare_numbers_pass(self):
        texts = ["P1 CONTINÚA EN PÁG. 3", "P2 RASGOS CONTINÚA EN PÁG. 3",
                 "C2 VIENE DE PÁG. 1 Y 2", "NOTAS"]
        cues, errors = inspect_pages(texts, ["1", "2", "3", ""])
        self.assertEqual(errors, [])
        self.assertEqual(cues, 4)

    def test_rejects_legacy_abbreviations(self):
        for legacy in ("RASGOS A P. 3", "VIENE P. 2", "SIG. 3", "ANT. 1",
                       "CONTINÚA: RECURSOS", "CONTINÚA EN C2 PÁG. 3", "VIENE DE P2 PÁG. 2"):
            _, errors = inspect_pages(["x", "y", legacy, "NOTAS"], ["1", "2", "3", ""])
            self.assertTrue(any("legacy" in e for e in errors), legacy)

    def test_rejects_wrong_direction_and_unanswered_destination(self):
        _, errors = inspect_pages(["CONTINÚA EN PÁG. 1", "VIENE DE PÁG. 3", "z", "NOTAS"],
                                  ["1", "2", "3", ""])
        self.assertTrue(any("does not point forward" in e for e in errors))
        self.assertTrue(any("does not point back" in e for e in errors))
        _, errors = inspect_pages(["CONTINÚA EN PÁG. 2", "nada", "z", "NOTAS"],
                                  ["1", "2", "3", ""])
        self.assertTrue(any("never says it comes from" in e for e in errors))

    def test_rejects_missing_or_extra_page_numbers(self):
        _, errors = inspect_pages(["a", "b", "NOTAS"], ["1", "", "3"])
        self.assertTrue(any("p2: printed page number" in e for e in errors))
        self.assertTrue(any("p3: printed page number" in e for e in errors))
        _, errors = inspect_pages(["a", "b", "NOTAS"], ["1", "02", ""])
        self.assertTrue(any("p2: printed page number '02'" in e for e in errors))


if __name__ == "__main__":
    unittest.main()
