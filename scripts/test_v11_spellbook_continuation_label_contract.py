"""BK05: source-backed spellbook continuation, genuine free header band, two writers."""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
WRITERS = (
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
)
TEST = ROOT / "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"

class SpellbookContinuationParityTest(unittest.TestCase):
    def test_book_overflow_labels_same_spell_without_spending_source_rows(self):
        samples = []
        for file in WRITERS:
            source = file.read_text(encoding="utf-8")
            start = source.index('if (title == "Libro de conjuros")')
            stop = source.index('if (title == "C2 - Habilidades, acciones y recursos")', start)
            segment = source[start:stop]
            for required in (
                "fragment.continuedFromPrevious",
                "fragment.startLineIndexInColumn == 0",
                "col.topPt == 702f",
                '"CONTINUACIÓN: " + realName',
                '"CONTINUACIÓN (VER ÍNDICE)"',
                "col.topPt + 5f",
                "fragment.sourceKey",
                "D-0076 §3.48 / BK05",
            ):
                self.assertIn(required, segment, file.name)
            self.assertNotIn("PcSheetV11FlowItem(", segment,
                             "No extra digital record may be created to label a continuation")
            samples.append(re.sub(r"\s+", " ", segment))
        self.assertEqual(samples[0], samples[1])
        tests = TEST.read_text(encoding="utf-8")
        self.assertIn("overlongRealSpellCardContinuesLosslesslyAfterPhysicalIndexPage", tests)
        self.assertIn('contains("CONTINUACIÓN: Fire Bolt")', tests)

if __name__ == "__main__":
    unittest.main()
