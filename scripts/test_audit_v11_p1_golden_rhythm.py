#!/usr/bin/env python3
"""Red/green tests for the V11 P1 golden vertical-rhythm oracle (C360)."""
import unittest

from audit_v11_p1_golden_rhythm import RIGHT_FEET, inspect_rects

GOLDEN_A = [(39.69, 220.53, 588.19, 22.96)] + [(270.43, 301.89, f, 22.96) for f in RIGHT_FEET.values()]


class P1GoldenRhythmTests(unittest.TestCase):
    def test_golden_a_and_b_pass(self):
        self.assertEqual(inspect_rects(GOLDEN_A, "A"), [])
        self.assertEqual(inspect_rects(GOLDEN_A + [(39.69, 220.53, 334.35, 22.96)], "B"), [])

    def test_rejects_a_moved_cartela(self):
        moved = [r if r[2] != 462.9 else (270.43, 301.89, 479.0, 22.96) for r in GOLDEN_A]
        self.assertTrue(any("ATAQUES" in e for e in inspect_rects(moved, "A")))
        old_left = [(39.69, 220.53, 579.0, 22.96)] + GOLDEN_A[1:]
        self.assertTrue(any("ATRIBUTOS" in e for e in inspect_rects(old_left, "A")))

    def test_rejects_wrong_height_and_missing_b_heading(self):
        short = [r if r[2] != 106.30 else (270.43, 301.89, 106.30, 17.0) for r in GOLDEN_A]
        self.assertTrue(any("MUNICIONES" in e for e in inspect_rects(short, "A")))
        self.assertTrue(any("HABILIDADES" in e for e in inspect_rects(GOLDEN_A, "B")))


if __name__ == "__main__":
    unittest.main()
