#!/usr/bin/env python3
"""C401 red/green tests: the C2 audits recognise only the golden NOTAS grid."""
import unittest

import fitz

from audit_v11_c207_vacancy import grids
from audit_v11_p3_c2_manuscript import c2_result


def page_with_grid(golden=True):
    doc = fitz.open()
    page = doc.new_page(width=612, height=792)
    page.insert_text((45, 60), "C2 - Equipo y tesoro / EQUIPO / 2", fontsize=11)
    page.insert_text((50, 136), "FUENTE REAL", fontsize=11)
    # NOTAS label 7.6 pt; the golden grid frame top lies 5.1 pt below its baseline.
    baseline = 400.0
    page.insert_text((48.19, baseline), "NOTAS", fontsize=7.6)
    top, bottom, left, right = baseline + 5.1, 732.47, 39.69, 301.04
    if golden:
        page.draw_rect(fitz.Rect(left, top, right, bottom), color=(148/255, 176/255, 193/255), width=0.45)
        columns, rows = 17, int((bottom - top) // 16)
        for i in range(1, columns):
            x = left + i * (right - left) / columns
            page.draw_line((x, top), (x, bottom), color=(191/255, 211/255, 222/255), width=0.27)
        for j in range(1, rows):
            y = top + j * (bottom - top) / rows
            page.draw_line((left, y), (right, y), color=(191/255, 211/255, 222/255), width=0.27)
    else:  # the pre-C401 16 pt grid, 7 pt under the label
        top = baseline + 7
        for i in range(17):
            page.draw_line((43 + 16 * i, top), (43 + 16 * i, bottom), color=(130/255, 165/255, 186/255), width=0.5)
        for j in range(int((bottom - top) // 16) + 1):
            page.draw_line((43, top + 16 * j), (299, top + 16 * j), color=(130/255, 165/255, 186/255), width=0.5)
    return doc, page


class GoldenC2GridTests(unittest.TestCase):
    def test_golden_grid_is_recognised_and_complete(self):
        doc, page = page_with_grid(golden=True)
        try:
            self.assertEqual(len(grids(page)), 1)
            count, complete, failures = c2_result(page)
            self.assertEqual((count, complete), (1, 1), failures)
        finally:
            doc.close()

    def test_pre_golden_grid_is_rejected(self):
        doc, page = page_with_grid(golden=False)
        try:
            self.assertEqual(grids(page), [])
            count, complete, failures = c2_result(page)
            self.assertEqual(complete, 0)
            self.assertTrue(any("no grid frame" in f for f in failures), failures)
        finally:
            doc.close()


if __name__ == "__main__":
    unittest.main()
