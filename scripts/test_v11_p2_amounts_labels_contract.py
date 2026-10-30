"""C242: golden right-aligned paired amounts and small special-equipment labels on both renderers."""
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[1]
RENDERERS = [
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
]
TESTS = ROOT / "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"


class P2AmountsLabelsParityTest(unittest.TestCase):
    def test_both_renderers_follow_the_golden_amount_and_label_grammar(self):
        for path in RENDERERS:
            data = path.read_text(encoding="utf-8")
            with self.subTest(path=path.name):
                # C292: golden amounts in Noto Sans Bold #264b68, right edge 3 pt inside.
                self.assertIn("stream.setFont(boldPdfFont, amountPt)", data)
                self.assertIn("stream.newLineAtOffset(cell.rectPt.rightPt - 3f -", data)
                self.assertIn("drawP1Label(stream, label, headerX + 5f, specialArea.topPt - 12f,", data)
                self.assertIn("drawP1Label(stream, label, headerX + 5f, drawingArea.topPt - 12f,", data)
                self.assertNotIn("draw(stream, label, headerX + 5f, specialArea.topPt - 13f, false)", data)
        self.assertIn("g3P2PairedAmountsAreRightAlignedLikeTheGoldenAB",
                      TESTS.read_text(encoding="utf-8"))


if __name__ == "__main__":
    unittest.main()
