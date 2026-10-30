"""P203/P206 source parity: blank paper has wider rows only on empty P2."""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
RENDERERS = [
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
]
TESTS = ROOT / "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"

class P2EmptyPaperRhythmParityTest(unittest.TestCase):
    def test_native_pdfbox_p2_sources_have_matching_empty_only_behavior(self):
        pieces = []
        for path in RENDERERS:
            data = path.read_text(encoding="utf-8")
            self.assertIn("pageLabels.isEmpty()", data)
            self.assertIn("!compactC2 && pageLabels.isEmpty()", data)
            self.assertIn("EQUIPMENT -> 16.15f", data)  # C320 golden
            self.assertIn("TREASURE -> 19.84f", data)  # C320 golden
            # P2 golden shading introduces an opt-in argument while keeping
            # the original empty-only measured writing rhythm unchanged.
            self.assertIn("grid(stream, visibleTable, true, manuscriptRowHeight,", data)
            # C211 R1 added stopRulesBelow after this argument.
            self.assertIn("alternateP2Rows = !compactC2,", data)
            self.assertIn("stopRulesBelow = sideNotes.map", data)
            self.assertIn("alternateP2Rows: Boolean = false", data)
            self.assertIn("grid(stream, traitsArea, true, p2Font.metrics.lineHeightPt, alternateP2Rows = true)", data)
            self.assertIn("val cells = (rect.heightPt / manuscriptRowHeightPt).toInt()", data)
            part = data.split("val manuscriptRowHeight = if (", 1)[1].split("if (compactC2)", 1)[0]
            pieces.append(re.sub(r"\s+", " ", part))
        self.assertEqual(pieces[0], pieces[1])
        tests = TESTS.read_text(encoding="utf-8")
        self.assertIn("g3RealP2EmptyEquipmentAndTreasureUseGoldenWritingRhythmWithoutSyntheticRows", tests)
        self.assertIn("g3RealP2RecordedZeroCurrencyIsNotTreatedAsAbsentPaperEntry", tests)

if __name__ == "__main__":
    unittest.main()
