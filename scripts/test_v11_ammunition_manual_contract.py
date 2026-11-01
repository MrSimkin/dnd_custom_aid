#!/usr/bin/env python3
"""V11 G3 P118 native Android/Desktop empty-ammo source parity, not PDF approval."""
from pathlib import Path
import unittest

ROOT=Path(__file__).resolve().parent.parent
RENDERERS=[
    ROOT/"desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT/"androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
]
TEST=ROOT/"desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"

class EmptyAmmoParity(unittest.TestCase):
    def test_same_genuine_source_grammar(self):
        parts=[]
        for path in RENDERERS:
            text=path.read_text(encoding="utf-8")
            section=text.split("fun drawP1Ammunition(",1)[1].split("fun drawP1Portrait(",1)[0]
            self.assertIn("if (p1AmmoItems.isEmpty())",section)
            self.assertIn("ammoBoxes(20)",section)
            # Approved empty ammunition uses a real blank name LINE,
            # never fake underscore glyphs. Both native renderers must
            # have identical paper geometry and 20 grouped source-free marks.
            self.assertNotIn('p1DrawSmall(stream, "____________", 316f, 122f)',section)
            self.assertIn('stream.moveTo(274.96f, 90.69f)',section)  # C356 golden
            self.assertIn('stream.lineTo(412.28f, 90.69f)',section)
            self.assertIn("val y = if (index < 10) 94.82f else 79.37f // C356 golden",section)
            self.assertIn("val x = 422.08f + column * 13.75f + (column / 5) * 2.83f // C351 golden",section)
            self.assertIn("p1AmmoItems.forEach { item ->",section)
            # C243: golden MUNICIONES cartela and banded rows in the same box.
            self.assertIn('drawGoldenCartela(stream, "MUNICIONES", 270.43f, 572.32f, 106.30f)',section)  # C356
            self.assertIn("drawGoldenRows(stream, 270.43f, 301.89f, 104.31f, 34.01f, 1)",section)
            parts.append(section)
        self.assertEqual(parts[0],parts[1])

    def test_real_pdf_test_exists(self):
        txt=TEST.read_text(encoding="utf-8")
        self.assertIn("g3RealP1WithoutAmmunitionHasOnePaperEntryWithTwentyEmptyBoxes",txt)
        self.assertIn("Loader.loadPDF(proof.bytes)",txt)
        self.assertIn("assertEquals(20, marks.findAll(ops).count()",txt)

if __name__=="__main__":
    unittest.main()
