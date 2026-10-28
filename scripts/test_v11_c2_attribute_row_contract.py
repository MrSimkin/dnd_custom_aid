"""C247: golden C2 custom attribute row is identical on Android and Desktop."""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
RENDERERS = [
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
]
TESTS = ROOT / "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"


class C2AttributeRowParityTest(unittest.TestCase):
    def test_both_renderers_draw_the_same_golden_attribute_row(self):
        pieces = []
        for path in RENDERERS:
            data = path.read_text(encoding="utf-8")
            self.assertIn('if (goldenRow) attribute.name + "\\n " else', data)
            self.assertIn("if (cell.sourceStartOffset == 0) drawC2AttributeRow(stream, cell)", data)
            part = data.split("fun drawC2AttributeRow(", 1)[1].split("fun addSection(", 1)[0]
            pieces.append(re.sub(r"\s+", " ", part))
        self.assertEqual(pieces[0], pieces[1])
        tests = TESTS.read_text(encoding="utf-8")
        self.assertIn('flat.contains("13 +1 Cordura SALVACIÓN +0")', tests)


if __name__ == "__main__":
    unittest.main()
