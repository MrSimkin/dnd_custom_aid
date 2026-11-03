#!/usr/bin/env python3
"""Red/green tests for the owner attribute-row oracle (P104, C372)."""
import unittest

from audit_v11_attribute_row_owner import inspect_shapes

CY = 217.21
BOX = (42.24, CY - 9.07, 85.32, CY + 9.07)
OVAL = (76.46, CY - 7.23, 111.9, CY + 7.23)
SAVE = (225.84, CY - 4.39, 234.62, CY + 4.39)
LABELS = [("TIRADA DE", 222.84, CY - 2.8), ("SALVACIÓN", 222.84, CY + 3.5)]


class OwnerAttributeRowTests(unittest.TestCase):
    def test_owner_row_passes(self):
        self.assertEqual(inspect_shapes([BOX], [OVAL], [SAVE], LABELS), [])

    def test_rejects_the_golden_side_by_side_oval(self):
        beside = (88.72, CY - 7.23, 124.16, CY + 7.23)
        self.assertTrue(inspect_shapes([BOX], [beside], [SAVE], LABELS))

    def test_rejects_an_oval_or_save_box_off_the_middle_line(self):
        low = (76.46, CY - 6.23, 111.9, CY + 8.23)
        self.assertTrue(any("no oval" in e for e in inspect_shapes([BOX], [low], [SAVE], LABELS)))
        high = (225.84, CY - 6.39, 234.62, CY + 2.39)
        self.assertTrue(any("saving-throw box" in e for e in inspect_shapes([BOX], [OVAL], [high], LABELS)))

    def test_rejects_a_left_aligned_or_missing_label(self):
        ragged = [("TIRADA DE", 220.1, CY - 2.8), ("SALVACIÓN", 222.84, CY + 3.5)]
        self.assertTrue(any("TIRADA DE" in e for e in inspect_shapes([BOX], [OVAL], [SAVE], ragged)))
        self.assertTrue(any("TIRADA DE" in e for e in inspect_shapes([BOX], [OVAL], [SAVE], LABELS[1:])))


if __name__ == "__main__":
    unittest.main()
