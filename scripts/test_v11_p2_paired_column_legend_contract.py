"""P202/P205 V11 legend parity for Android and Desktop native PDFBox."""
import pathlib
import re
import unittest

ROOT = pathlib.Path(__file__).resolve().parents[1]
SOURCES = [
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
]
TEST = ROOT / "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"

class P2PairedLegendParityTest(unittest.TestCase):
    def test_genuine_renderers_have_approved_legends_and_real_pdf_test(self):
        equivalents = []
        for source in SOURCES:
            body = source.read_text(encoding="utf-8")
            self.assertIn('if (ordinary) "EQUIPO" else "TESORO"', body)
            self.assertIn('if (ordinary) "OBJETO / CANTIDAD" else "denominación / cantidad"', body)
            self.assertIn("columnLegend?.let { legend ->", body)
            self.assertIn("legend overlaps its approved heading", body)
            part = body.split("columnLegend?.let { legend ->", 1)[1].split("fun drawPaired(", 1)[0]
            equivalents.append(re.sub(r"\s+", " ", part))
        self.assertEqual(equivalents[0], equivalents[1])
        self.assertIn("g3RealP2PairedTableHeadersIdentifyObjectAndDenominationWithoutMovingSourceCells",
                      TEST.read_text(encoding="utf-8"))

if __name__ == "__main__":
    unittest.main()
