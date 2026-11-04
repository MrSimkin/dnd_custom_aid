#!/usr/bin/env python3
"""C412 red/green tests: only golden inks pass the palette audit."""
import unittest

import fitz

from audit_v11_golden_palette import page_failures


def ink(r, g, b):
    return (r / 255, g / 255, b / 255)


class GoldenPaletteTests(unittest.TestCase):
    def setUp(self):
        self.doc = fitz.open()
        self.page = self.doc.new_page(width=612, height=792)
        self.addCleanup(self.doc.close)

    def test_golden_vectors_pass(self):
        self.page.draw_rect(fitz.Rect(40, 40, 200, 80), color=ink(148, 176, 193), fill=ink(231, 242, 247))
        self.assertEqual(page_failures(self.page), [])

    def test_foreign_stroke_is_rejected(self):
        self.page.draw_line((40, 40), (200, 40), color=ink(130, 165, 186))
        self.assertTrue(any("stroke #82a5ba" in f for f in page_failures(self.page)))

    def test_pure_black_stroke_is_rejected(self):
        self.page.draw_line((40, 40), (200, 40), color=(0, 0, 0))
        self.assertTrue(any("stroke #000000" in f for f in page_failures(self.page)))

    def test_foreign_fill_is_rejected(self):
        self.page.draw_rect(fitz.Rect(40, 40, 200, 80), fill=ink(238, 247, 251), color=None)
        self.assertTrue(any("fill #eef7fb" in f for f in page_failures(self.page)))

    def test_black_text_in_a_foreign_face_is_rejected(self):
        self.page.insert_text((50, 50), "N1", fontsize=7)
        failures = page_failures(self.page)
        self.assertTrue(any("text ink #000000" in f for f in failures), failures)
        self.assertTrue(any("face Helvetica" in f for f in failures), failures)


if __name__ == "__main__":
    unittest.main()
