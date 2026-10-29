"""C250: golden P1 LANZAMIENTO cards and shaded slot grid are identical on Android and Desktop."""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
RENDERERS = [
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
]
TESTS = ROOT / "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"


class P1SpellcastingGridParityTest(unittest.TestCase):
    def test_both_renderers_draw_the_same_golden_block(self):
        pieces = []
        for path in RENDERERS:
            data = path.read_text(encoding="utf-8")
            part = data.split("// C250: golden P1 LANZAMIENTO grammar", 1)[1].split(
                "// Eight real combat cells", 1)[0]
            self.assertIn('"APTITUD MÁGICA"', part)
            self.assertIn("stream.addRect(p1MagicColumn(col),222f-row*24f,84f,24f)", part)
            self.assertIn("stream.addRect(x+20f+n*markStep,b+8f,8f,8f)", part)
            # Counts that boxes cannot show keep their real numbers.
            self.assertIn("draw(stream,v.totalSlots.toString(),x+20f,b+8f,false)", part)
            self.assertNotIn('draw(stream,"GASTADOS"', part)
            pieces.append(re.sub(r"\s+", " ", part))
        self.assertEqual(pieces[0], pieces[1])
        self.assertIn("g3P1SpellcastingUsesGoldenReferenceCardsAndShadedSlotGridAB",
                      TESTS.read_text(encoding="utf-8"))


if __name__ == "__main__":
    unittest.main()
