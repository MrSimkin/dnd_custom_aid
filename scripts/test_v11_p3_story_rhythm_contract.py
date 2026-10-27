"""Owner option B (2026-10-26): P3 HISTORIA uses Fira 7 pt on 17 pt rows on both renderers."""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
RENDERERS = [
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
]
TESTS = ROOT / "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"


class P3StoryRhythmParityTest(unittest.TestCase):
    def test_both_renderers_share_the_owner_approved_story_rhythm(self):
        pieces = []
        for path in RENDERERS:
            data = path.read_text(encoding="utf-8")
            self.assertIn("val storyRowPt = 17f", data)
            self.assertIn("measurer.resolve(PcSheetV11Typeface.BODY, 7f).let { font ->", data)
            self.assertIn("fun storyBaseline(cell: PcSheetV11RectPt): Float = cell.bottomPt + 4f", data)
            self.assertIn("val p3Story = storyFont.place(", data)
            self.assertIn("columns.forEach { p3RuledCells(stream, it, storyRowPt) }", data)
            self.assertIn("p3StoryOverflow, 2, area = PcSheetV11RectPt(42f, 76f, 528f, 597f),", data)
            self.assertIn("font = storyFont,", data)
            part = data.split("val storyRowPt = 17f", 1)[1].split("fun remainingNarrative(", 1)[0]
            pieces.append(re.sub(r"\s+", " ", part))
        self.assertEqual(pieces[0], pieces[1])
        self.assertIn("g3P3StoryUsesOwnerApprovedGoldenRhythmSevenPointOnSeventeenPointRowsAB",
                      TESTS.read_text(encoding="utf-8"))


if __name__ == "__main__":
    unittest.main()
