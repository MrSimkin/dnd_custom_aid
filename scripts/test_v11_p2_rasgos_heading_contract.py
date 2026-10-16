"""P207: compare approved P2 Rasgos title in native Android/Desktop sources."""
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[1]
RENDERERS = (
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
)
TEST = ROOT / "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"

class P2RasgosHeadingContractTest(unittest.TestCase):
    def test_exact_heading_in_both_native_renderers_and_real_pdf_test(self):
        for path in RENDERERS:
            source = path.read_text(encoding="utf-8")
            self.assertEqual(source.count('drawP2Cartela(stream, "RASGOS Y ATRIBUTOS", traitsArea)'), 1)
            self.assertNotIn('drawP2Cartela(stream, "RASGOS", traitsArea)', source)
        self.assertIn("fun g3RealP2RasgosHeadingMatchesApprovedGoldenWithoutChangingActualTraits()",
                      TEST.read_text(encoding="utf-8"))

if __name__ == "__main__":
    unittest.main()
