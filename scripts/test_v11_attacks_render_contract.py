#!/usr/bin/env python3
"""V11 P1 attack-table source parity tripwire; never substitutes for real PDF tests."""
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parent.parent
DESKTOP = ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt"
ANDROID = ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt"
PDF_TEST = ROOT / "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"


def attack_region(text: str) -> str:
    return text.split("fun drawP1Attacks(", 1)[1].split("fun drawP1Spellcasting(", 1)[0]


class P1AttacksStageTripwire(unittest.TestCase):
    def test_android_desktop_attack_draw_order_and_coordinates_agree(self):
        desktop, android = [attack_region(p.read_text(encoding="utf-8"))
                            for p in (DESKTOP, ANDROID)]
        self.assertEqual(desktop, android, "V11 P1 Attack rendering drifted between platforms")

    def test_alternating_fill_does_not_move_existing_table_geometry(self):
        for platform in (DESKTOP, ANDROID):
            with self.subTest(platform=str(platform)):
                body = attack_region(platform.read_text(encoding="utf-8"))
                self.assertIn("setNonStrokingColor(Color(244, 250, 253))", body)
                self.assertIn("for (rowIndex in 0..6 step 2)", body)
                self.assertIn("stream.addRect(left + 1f, rowTop - 12f, 256f, 12f)", body)
                self.assertIn("stream.fill()", body)
                self.assertIn("stream.addRect(left, bottom, 258f, top - bottom)", body)
                self.assertIn("val top = 491f", body)
                self.assertIn("val bottom = 392f", body)
                self.assertIn("draw(stream, entry.damageEffect, b2 + 3f, y, false)", body)
                self.assertIn('draw(stream, "ATAQUE / RANGO", left + 4f, top - 10f, false)', body)
                self.assertIn('draw(stream, "BONO", b1 + 3f, top - 10f, false)', body)
                self.assertIn('draw(stream, "DAÑO / EFECTO", b2 + 3f, top - 10f, false)', body)

    def test_real_app_pdf_test_exists_for_both_variants_and_four_cases(self):
        suite = PDF_TEST.read_text(encoding="utf-8")
        target = suite.split(
            "fun g3RealP1AttacksHaveSubtleAlternatingBandsWithoutLosingPrintedAttackFields()", 1
        )[1].split("\n    @Test", 1)[0]
        for expected in (
            "Loader.loadPDF(proof.bytes)",
            "PcSheetV11Variant.A", "PcSheetV11Variant.B",
            "01_aldren_vale_srd5_1_champion_fighter.json",
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json",
            "03_mara_siete_umbrales_custom_extended.json",
            "assertEquals(4, bands.findAll(content).count()",
        ):
            self.assertIn(expected, target)


if __name__ == "__main__":
    unittest.main()
