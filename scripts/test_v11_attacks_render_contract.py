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
                # C232 step 2: every body row uses the golden's two alternating
                # blue-mist tones through fillBand (previously only even rows).
                self.assertIn("for (rowIndex in 0..6)", body)
                self.assertIn("fillBand(stream, left + 1f, rowTop - 12f, 256f, 12f, rowIndex)", body)
                self.assertIn("stream.addRect(left, bottom, 258f, top - bottom)", body)
                self.assertIn("val top = 491f", body)
                self.assertIn("val bottom = 392f", body)
                # C237: golden 7 pt Barlow labels; bonus/damage right-aligned.
                self.assertIn("drawAttackValues(stream, entry, left, y)", body)
                self.assertIn('drawP1Label(stream, "ATAQUE / RANGO", left + 4f, top - 9f)', body)
                self.assertIn('drawP1Label(stream, "BONO", b2 - 4f - labelWidth("BONO"), top - 9f)', body)
                self.assertIn('left + 254f - labelWidth("DAÑO / EFECTO"), top - 9f)', body)

    def test_c2_attack_table_rows_agree_between_platforms(self):
        # Golden C2 ATAQUES: fitting deferred attacks reuse the P1 row grammar.
        regions = []
        for platform in (DESKTOP, ANDROID):
            text = platform.read_text(encoding="utf-8")
            self.assertIn("val p1Attacks=attackRows.filter{ attackFitsTableRow(it) }.take(6)", text)
            self.assertIn("it.id !in p1AttackIds && attackFitsTableRow(it)", text)
            regions.append(text.split("c2AttackTableRows[cell.sourceKey]?.let { attack ->", 1)[1]
                           .split("// C219:", 1)[0])
        self.assertEqual(regions[0], regions[1])
        values = [p.read_text(encoding="utf-8").split("fun drawAttackValues(", 1)[1]
                  .split("fun drawP1Attacks(", 1)[0] for p in (DESKTOP, ANDROID)]
        self.assertEqual(values[0], values[1], "C237 right-aligned attack values drifted")
        self.assertIn("g3DeferredFittingAttacksUseGoldenC2AttackTableRowsAB",
                      PDF_TEST.read_text(encoding="utf-8"))

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
