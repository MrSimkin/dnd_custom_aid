#!/usr/bin/env python3
"""Check Android/Desktop V11 P1 B skills heading parity; native PDF test is independent."""
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parent.parent
RENDERERS = [
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
]
NATIVE_TEST = (ROOT /
    "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt")

class SkillsHeadingParity(unittest.TestCase):
    def test_both_platforms_use_identical_gold_cartela_geometry(self):
        declarations = []
        for path in RENDERERS:
            body = path.read_text(encoding="utf-8").split(
                "fun drawP1BIndependentSkills(", 1
            )[1].split("bSkillsPlaced?.cells", 1)[0]
            self.assertIn("if (variantA) return", body)
            # C358: golden B cartela under the last attribute row, title 6.8 pt over its foot.
            self.assertIn('drawP1Label(stream, "HABILIDADES", 56.69f, cb + 6.8f, 9.1f)', body)
            self.assertNotIn('skillsLegend', body)  # C456: panel B has no legend, as the golden
            for operator in (
                "stream.moveTo(44.69f, cb)",
                "stream.lineTo(255.22f, cb)",
                "stream.lineTo(260.22f, cb + 5f)",
                "stream.lineTo(260.22f, ct - 5f)",
                "stream.lineTo(255.22f, ct)",
                "stream.lineTo(44.69f, ct)",
                "stream.lineTo(39.69f, ct - 5f)",
                "stream.lineTo(39.69f, cb + 5f)",
                "stream.fillAndStroke()",
                "stream.addRect(xStart, rowBottom, xEnd - xStart, 14.74f)",
            ):
                self.assertIn(operator, body, f"{path.name}: missing {operator}")
            declarations.append(tuple(line.strip() for line in body.splitlines()
                                      if line.strip().startswith(("stream.",))))
        self.assertEqual(declarations[0], declarations[1], "Android/Desktop Skills B title has diverged")

    def test_native_pdf_output_guard_exists(self):
        source = NATIVE_TEST.read_text(encoding="utf-8")
        check = source.split(
            "fun g3RealP1VariantBSkillsHeaderHasApprovedBlueClippedCartelaWithoutMovingSkillRows()", 1
        )[1].split("\n    @Test", 1)[0]
        for phrase in (
            "PcSheetV11Variant.B",
            "Loader.loadPDF(proof.bytes)",
            "clippedHeader.findAll(ops).count()",
            "Original B skills frame must not move",
            'page1.contains("Acrobacias")',
        ):
            self.assertIn(phrase, check)


if __name__ == "__main__":
    unittest.main()
