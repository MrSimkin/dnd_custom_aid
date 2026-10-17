"""BK04: original measured whole-card handling and native A/B PDF proof."""
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[1]
FLOW = ROOT / "shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/PcSheetV11TextFlow.kt"
RENDERERS = (
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
)
G2 = ROOT / "shared/src/commonTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/PcSheetV11MeasuredFlowPlacerTest.kt"
G3 = ROOT / "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"

class BookWholeCardContractTest(unittest.TestCase):
    def test_measured_whole_card_does_not_split_when_it_fits_empty_column(self):
        source = FLOW.read_text(encoding="utf-8")
        self.assertIn("item.keepTogetherIfFits && lines.size <= column.lineCapacity", source)
        self.assertIn("lines.size > remaining", source)
        self.assertIn("nextColumn()", source)
        self.assertIn("bk04MeasuredBookKeepsMediumCardWholeAtStartOfNextColumn",
                      G2.read_text(encoding="utf-8"))
        proof = G3.read_text(encoding="utf-8")
        self.assertIn("g3Bk04MediumRealSpellCardMovesWholeToNextPhysicalBookColumn", proof)
        self.assertIn("V11-INTERNAL-PROOF-BK04-whole-next-column-", proof)
        for renderer in RENDERERS:
            content = renderer.read_text(encoding="utf-8")
            self.assertIn("keepTogetherIfFits = true", content)
            self.assertIn('addSection("Libro de conjuros"', content)

if __name__ == "__main__":
    unittest.main()
