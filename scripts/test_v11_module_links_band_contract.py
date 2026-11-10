"""C252: golden module-link footer band is identical on Android and Desktop."""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
RENDERERS = [
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
]
TESTS = ROOT / "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"


class ModuleLinksBandParityTest(unittest.TestCase):
    def test_both_renderers_draw_the_same_band(self):
        pieces = []
        for path in RENDERERS:
            data = path.read_text(encoding="utf-8")
            part = data.split("// C252: golden P1/P2 module-link footer", 1)[1].split(
                "// A C2 page never points", 1)[0]
            # C290: golden band geometry (v11_extremo p1).
            self.assertIn("stream.addRect(39.69f, 43.94f, 532.62f, 13.04f)", part)
            self.assertIn("drawP1Label(stream, links, 42.52f, 47.34f, 6.1f)", part)
            self.assertIn("drawModuleLinks(stream, p1ModuleLinks)", data)
            self.assertIn("drawModuleLinks(stream, p2ModuleLinks)", data)
            pieces.append(re.sub(r"\s+", " ", part))
        self.assertEqual(pieces[0], pieces[1])
        self.assertIn("g3ModuleLinksUseGoldenShadedFooterBandOnP1AndP2AB",
                      TESTS.read_text(encoding="utf-8"))


if __name__ == "__main__":
    unittest.main()
