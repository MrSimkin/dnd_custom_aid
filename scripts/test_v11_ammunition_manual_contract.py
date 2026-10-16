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
            self.assertIn("repeat(20) { index ->",section)
            self.assertIn('p1DrawSmall(stream, "____________", 316f, 122f)',section)
            self.assertIn("val y = if (index < 10) 119f else 103f",section)
            self.assertIn("val x = 392f + column * 8f + (column / 5) * 4f",section)
            self.assertIn("p1AmmoItems.forEachIndexed",section)
            parts.append(section)
        self.assertEqual(parts[0],parts[1])

    def test_real_pdf_test_exists(self):
        txt=TEST.read_text(encoding="utf-8")
        self.assertIn("g3RealP1WithoutAmmunitionHasOnePaperEntryWithTwentyEmptyBoxes",txt)
        self.assertIn("Loader.loadPDF(proof.bytes)",txt)
        self.assertIn("assertEquals(20, marks.findAll(ops).count()",txt)

if __name__=="__main__":
    unittest.main()
