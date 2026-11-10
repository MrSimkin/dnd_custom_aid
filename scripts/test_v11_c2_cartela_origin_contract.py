"""C248/C341: C2 cartela «VIENE DE PÁG. n» origin labels are identical on Android and Desktop."""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
RENDERERS = [
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
]
TESTS = ROOT / "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"


class C2CartelaOriginParityTest(unittest.TestCase):
    def test_both_renderers_print_the_same_origin_labels(self):
        pieces = []
        for path in RENDERERS:
            data = path.read_text(encoding="utf-8")
            part = data.split("// C248: golden C2 cartelas", 1)[1].split("// C213:", 1)[0]
            self.assertIn("PcSheetV11ContinuationText.comesFrom(base)", part)
            pieces.append(re.sub(r"\s+", " ", part))
        self.assertEqual(pieces[0], pieces[1])
        self.assertIn("g3C2ModuleCartelasShowTheirRealBasePageAB", TESTS.read_text(encoding="utf-8"))


if __name__ == "__main__":
    unittest.main()
