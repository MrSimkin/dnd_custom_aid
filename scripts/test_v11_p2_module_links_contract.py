"""C251: golden P2 footer module links are identical on Android and Desktop."""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
RENDERERS = [
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
]
TESTS = ROOT / "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"


class P2ModuleLinksParityTest(unittest.TestCase):
    def test_both_renderers_name_every_p2_module_with_its_page(self):
        pieces = []
        for path in RENDERERS:
            data = path.read_text(encoding="utf-8")
            self.assertNotIn("OTROS C2", data)
            part = data.split("// C251: golden P2 footer", 1)[1].split("// C246:", 1)[0]
            self.assertIn('"ESPECIAL" -> "EQUIPO ESPECIAL"', part)
            self.assertIn('} + " A P. " + physical', part)
            self.assertIn('.joinToString(" · ")', part)
            self.assertIn("draw(stream, p2ModuleLinks, 42f, 63f, false)", data)
            pieces.append(re.sub(r"\s+", " ", part))
        self.assertEqual(pieces[0], pieces[1])
        self.assertIn("g3P2FooterNamesEachContinuedModuleWithItsRealC2PageAB",
                      TESTS.read_text(encoding="utf-8"))


if __name__ == "__main__":
    unittest.main()
