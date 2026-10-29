#!/usr/bin/env python3
"""G3 V11 Attributes A/B source parity; genuine PDFs have a separate Kotlin test."""
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parent.parent
RENDERERS = [
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
]
NATIVE = ROOT / "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"
LINES = [
    "stream.moveTo(46f, 579f)",
    "stream.lineTo(296f, 579f)",
    "stream.lineTo(300f, 583f)",
    "stream.lineTo(300f, 598f)",
    "stream.lineTo(296f, 602f)",
    "stream.lineTo(46f, 602f)",
    "stream.lineTo(42f, 598f)",
    "stream.lineTo(42f, 583f)",
    "stream.fillAndStroke()",
    '"ATRIBUTOS · A" else "ATRIBUTOS · B",58f,586f,9.1f)',  # C264 golden 9.1 pt
]


class AttributesHeaderStageTest(unittest.TestCase):
    def test_both_native_renderers_use_same_approved_cartela(self):
        regions = []
        for path in RENDERERS:
            body = path.read_text(encoding="utf-8").split(
                "fun drawP1Attributes(", 1)[1].split("val k=0.5522848f", 1)[0]
            for op in LINES:
                self.assertIn(op, body, f"{path}: missing {op}")
            regions.append(tuple(line.strip() for line in body.splitlines()
                                 if line.strip().startswith(("stream.moveTo", "stream.lineTo"))))
        self.assertEqual(regions[0], regions[1], "Platforms differ in P1 ATRIBUTOS geometry.")

    def test_genuine_pdf_output_contract_is_exercised(self):
        test = NATIVE.read_text(encoding="utf-8")
        section = test.split(
            "fun g3RealP1BothVariantsHaveGoldenAttributesClippedCartelaWithoutLosingScores()", 1
        )[1].split("\n    @Test", 1)[0]
        for required in ("PcSheetV11Variant.A", "PcSheetV11Variant.B",
                         "Loader.loadPDF(proof.bytes)",
                         "titlePolygon.findAll(ops).count()",
                         'p1.contains("FUERZA")',
                         'p1.contains("DESTREZA")',
                         'p1.contains("SALVACIÓN")'):
            self.assertIn(required, section)


if __name__ == "__main__":
    unittest.main()
