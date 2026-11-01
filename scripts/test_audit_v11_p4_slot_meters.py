#!/usr/bin/env python3
"""Red/green synthetic adversarial tests for physical P4 meter auditor."""
import unittest

import fitz

import audit_v11_p4_slot_meters as audit


def specimen(missing_level=None, missing_square=None, extra_meter=False,
             real_total_level=None, long_level=None, legacy=False):
    doc = fitz.open()
    page = doc.new_page(width=612, height=792)
    page.insert_text((45, 73), "P4 - Conjuros", fontsize=12)
    for level in range(10):
        if level == missing_level:
            continue
        y = 210 + level * 37
        page.insert_text((45, y),
                         "TRUCOS 0" if level == 0 else f"NIVEL {level}", fontsize=8)
        if level:
            # A genuine expanded label stays in the original level row,
            # not a second synthetic meter elsewhere on the page.
            label = ("ESP.2147483647 G.2147483646" if level == long_level
                     else "ESP.4 G.2" if level == real_total_level else "ESP.")
            page.insert_text((82 if level == long_level else 110, y),
                             label, fontsize=7)
            for index in range(3):
                if missing_square == (level, index):
                    continue
                # C362: golden first column ends at x207.5; 8.22 pt boxes start 42.52 pt
                # before it, 13.04 pt apart.
                x = 164.98 + index * 13.04
                if legacy:  # pre-C362 7 pt squares at right - 33, 11 pt apart
                    page.draw_rect(fitz.Rect(174.5 + index * 11, y - 6, 181.5 + index * 11, y + 1))
                else:
                    page.draw_rect(fitz.Rect(x, y - 7.2, x + 8.22, y + 1.02))
    if extra_meter:
        page.insert_text((110, 615), "ESP.", fontsize=7)
    return doc


class PhysicalP4Meters(unittest.TestCase):
    def test_final_level9_rows_may_reach_the_golden_column_foot(self):
        for foot, ok in ((727.09, True), (740.0, False)):
            doc = fitz.open()
            page = doc.new_page(width=612, height=792)
            page.insert_text((410, 300), "NIVEL 9", fontsize=8)
            page.draw_rect(fitz.Rect(404.5, foot - 15.73, 572.31, foot), color=None,
                           fill=(231/255, 242/255, 247/255))
            in_window, end, errors = audit.final_level9_manuscript_paper(page)
            doc.close()
            self.assertTrue(in_window)
            self.assertEqual(ok, errors == [], (foot, end, errors))

    def test_rejects_the_pre_golden_7pt_meter(self):
        with specimen(legacy=True) as doc:
            result = audit.audit_document(doc)
        self.assertTrue(any("has 0 squares" in e for e in result["errors"]), result["errors"])

    def test_all_ten_levels_nine_meters_and_27_real_squares(self):
        with specimen() as doc:
            result = audit.audit_document(doc)
        self.assertFalse(result["errors"], result["errors"])
        self.assertEqual(9, result["meter_count"])

    def test_real_source_total_and_spent_keep_nine_blank_paper_meter_squares(self):
        with specimen(real_total_level=1) as doc:
            result = audit.audit_document(doc)
        self.assertFalse(result["errors"], result["errors"])
        self.assertEqual(9, result["meter_count"])
        self.assertEqual(1, result["source_backed_meter_count"])

    def test_shifted_real_long_meter_keeps_all_three_physical_squares(self):
        with specimen(long_level=1) as doc:
            result = audit.audit_document(doc)
        self.assertFalse(result["errors"], result["errors"])
        self.assertEqual(9, result["meter_count"])
        self.assertEqual(1, result["source_backed_meter_count"])

    def test_missing_level_cannot_be_hidden_by_successful_pdf_generation(self):
        with specimen(missing_level=7) as doc:
            result = audit.audit_document(doc)
        self.assertTrue(any("level 7 printed 0" in e for e in result["errors"]))

    def test_missing_one_real_box_fails_even_with_all_text_present(self):
        with specimen(missing_square=(3, 1)) as doc:
            result = audit.audit_document(doc)
        self.assertTrue(any("has 2 squares" in e for e in result["errors"]), result["errors"])

    def test_duplicate_meter_fails(self):
        with specimen(extra_meter=True) as doc:
            result = audit.audit_document(doc)
        self.assertTrue(any("meters 10" in e for e in result["errors"]))

    def test_non_spellcaster_is_not_invented(self):
        doc = fitz.open()
        doc.new_page(width=612, height=792)
        self.assertFalse(audit.audit_document(doc)["has_p4"])
        doc.close()


if __name__ == "__main__":
    unittest.main()
