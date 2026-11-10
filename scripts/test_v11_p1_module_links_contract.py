"""C246: golden P1 per-module C2 destinations are identical on Android and Desktop."""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
RENDERERS = [
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
]
TESTS = ROOT / "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"


class P1ModuleLinksParityTest(unittest.TestCase):
    def test_both_renderers_print_the_same_p1_module_destinations(self):
        pieces = []
        for path in RENDERERS:
            data = path.read_text(encoding="utf-8")
            self.assertIn("mixedC2HeadingPages.putIfAbsent(it.sourceKey, pdf.numberOfPages)", data)
            self.assertIn('if (key.startsWith("P1 - ") && p1ModuleLinks != null) {', data)
            part = data.split("val p1ModuleLinks = listOf(", 1)[1].split("// A C2 page never points", 1)[0]
            pieces.append(re.sub(r"\s+", " ", part))
        self.assertEqual(pieces[0], pieces[1])
        self.assertIn("g3P1FooterNamesEachContinuedModuleWithItsRealC2PageAB",
                      TESTS.read_text(encoding="utf-8"))


if __name__ == "__main__":
    unittest.main()
