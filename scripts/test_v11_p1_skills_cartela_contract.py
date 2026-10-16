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
            )[1].split("val blankPaperTop", 1)[0]
            self.assertIn("if (variantA) return", body)
            self.assertIn('draw(stream,"HABILIDADES  P=COMPETENCIA  E=PERICIA",51f,335f,true)', body)
            for operator in (
                "stream.moveTo(46f, 330f)",
                "stream.lineTo(296f, 330f)",
                "stream.lineTo(300f, 334f)",
                "stream.lineTo(300f, 344f)",
                "stream.lineTo(296f, 348f)",
                "stream.lineTo(46f, 348f)",
                "stream.lineTo(42f, 344f)",
                "stream.lineTo(42f, 334f)",
                "stream.fillAndStroke()",
                "stream.addRect(42f,105f,258f,223f)",
            ):
                self.assertIn(operator, body, f"{path.name}: missing {operator}")
            declarations.append(tuple(body[body.index("stream.moveTo(46f, 330f)"):
                                           body.index('draw(stream,"HABILIDADES')].splitlines()))
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
