"""C238: golden C-N3 NOTAS cartela and note cards are identical on Android and Desktop."""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
RENDERERS = [
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
]
TESTS = ROOT / "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"


class Cn3CardsParityTest(unittest.TestCase):
    def test_both_renderers_draw_the_same_golden_cn3_cards(self):
        pieces = []
        for path in RENDERERS:
            data = path.read_text(encoding="utf-8")
            self.assertIn('val scope = "solo notas digitales"', data)
            self.assertIn('stream.showText("NOTAS MANUSCRITAS")', data)
            self.assertIn("texts.digitalNotes.map { it.copy(leadingSeparationLines = 1) }, 1,", data)
            part = data.split("// C238: golden C-N3", 1)[1].split("// Mixed C2 must preserve", 1)[0]
            pieces.append(re.sub(r"\s+", " ", part))
        self.assertEqual(pieces[0], pieces[1])
        self.assertIn("g3DigitalNotesUseGoldenNotasCartelaAndInsetCardsAB",
                      TESTS.read_text(encoding="utf-8"))


if __name__ == "__main__":
    unittest.main()
