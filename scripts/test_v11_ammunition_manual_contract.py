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
            self.assertIn('stream.moveTo(316f, 120f)',section)
            self.assertIn('stream.lineTo(410f, 120f)',section)
            self.assertIn("val y = if (index < 10) 119f else 106f",section)
            self.assertIn("val x = 419.8f + column * 13.75f + (column / 5) * 2.83f",section)
            self.assertIn("p1AmmoItems.forEach { item ->",section)
            # C243: golden MUNICIONES cartela and banded rows in the same box.
            self.assertIn('drawP1Label(stream, "MUNICIONES", 328f, 136.5f, 9.1f)',section)
            self.assertIn("fillBand(stream, 312f, 102f, 258f, 29f, 0)",section)
            parts.append(section)
        self.assertEqual(parts[0],parts[1])

    def test_real_pdf_test_exists(self):
        txt=TEST.read_text(encoding="utf-8")
        self.assertIn("g3RealP1WithoutAmmunitionHasOnePaperEntryWithTwentyEmptyBoxes",txt)
        self.assertIn("Loader.loadPDF(proof.bytes)",txt)
        self.assertIn("assertEquals(20, marks.findAll(ops).count()",txt)

if __name__=="__main__":
    unittest.main()
