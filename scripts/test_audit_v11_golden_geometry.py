#!/usr/bin/env python3
"""C427 red/green tests: a golden shape missing from the App page is reported."""
import unittest

import fitz

from audit_v11_golden_geometry import page_failures


def ink(r, g, b):
    return (r / 255, g / 255, b / 255)


def page_with(*shapes):
    doc = fitz.open()
    page = doc.new_page(width=612, height=792)
    for rect, color, fill in shapes:
        page.draw_rect(fitz.Rect(*rect), color=color, fill=fill, width=0.5)
    return doc, page


BAND = ((40, 200, 300, 220), ink(38, 75, 104), ink(231, 242, 247))


class GoldenGeometryTests(unittest.TestCase):
    def check(self, golden_shapes, app_shapes, page_number=1):
        golden_doc, golden = page_with(*golden_shapes)
        app_doc, app = page_with(*app_shapes)
        try:
            return page_failures(golden, app, page_number)
        finally:
            golden_doc.close()
            app_doc.close()

    def test_same_shape_passes(self):
        self.assertEqual(self.check([BAND], [BAND]), [])

    def test_missing_shape_fails(self):
        self.assertEqual(len(self.check([BAND], [])), 1)

    def test_moved_shape_fails(self):
        moved = ((40, 210, 300, 230), BAND[1], BAND[2])
        self.assertEqual(len(self.check([BAND], [moved])), 1)

    def test_wrong_ink_fails(self):
        recoloured = (BAND[0], ink(130, 165, 186), BAND[2])
        self.assertEqual(len(self.check([BAND], [recoloured])), 1)

    def test_a_shape_inside_a_longer_app_shape_of_the_same_ink_passes(self):
        longer = ((40, 180, 300, 240), BAND[1], BAND[2])
        self.assertEqual(self.check([BAND], [longer]), [])

    def test_the_absent_level_slot_boxes_are_excused_only_on_p1(self):
        box = ((304.44, 608.74, 313.23, 617.53), ink(82, 124, 150), ink(255, 255, 255))
        self.assertEqual(self.check([box], [], page_number=0), [])
        self.assertEqual(len(self.check([box], [], page_number=1)), 1)


if __name__ == "__main__":
    unittest.main()
