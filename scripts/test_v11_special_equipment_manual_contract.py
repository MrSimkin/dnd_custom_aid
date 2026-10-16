"""P210 local source parity; physical vector boxes tested by Desktop PDFBox."""
import pathlib
import re
import unittest

ROOT = pathlib.Path(__file__).resolve().parents[1]
DESKTOP = ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt"
ANDROID = ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt"
PDF_TEST = ROOT / "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"

class SpecialEquipmentPaperContractTest(unittest.TestCase):
    def test_android_desktop_identical_empty_location_and_activo_rule(self):
        sections = []
        for path in (DESKTOP, ANDROID):
            value = path.read_text(encoding="utf-8")
            start = value.index("val manualSpecialLocations =")
            end = value.index("PDPage(PDRectangle.LETTER).also", start)
            body = value[start:end]
            self.assertIn('listOf("Cuello", "Cuerpo", "Mano izquierda", "Mano derecha")', body)
            self.assertIn("repeat(specialCapacity) { rowIndex ->", body)
            self.assertIn("manualSpecialLocations.getOrNull(rowIndex)", body)
            self.assertIn("specialP2Rows.getOrNull(rowIndex)?.first?.active == true", body)
            self.assertIn("stream.addRect(activeX, activeY, 9f, 9f)", body)
            sections.append(re.sub(r"\\s+", " ", body))
        self.assertEqual(sections[0], sections[1])
        self.assertIn("g3RealP2NoSpecialEquipmentHasRecognizedLocationsFreeRowsAndBlankActivoBoxes",
            PDF_TEST.read_text(encoding="utf-8"))

if __name__ == "__main__":
    unittest.main()
