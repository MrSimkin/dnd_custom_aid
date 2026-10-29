"""BK06: essential spellbook PDF content, Android/Desktop parity, no tech header."""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
WRITERS = [
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
]
TEST = ROOT / "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"

class EssentialSpellbookCardParityTest(unittest.TestCase):
    def test_owner_chosen_card_is_the_same_in_both_native_pdf_writers(self):
        bodies = []
        for file in WRITERS:
            source = file.read_text(encoding="utf-8")
            start = source.index("data.spellbookEntries?.takeIf")
            stop = source.index("val contentFlow = bookFont.place(", start)
            section = source[start:stop]
            self.assertIn("D-0076 §3.47 / BK06", section)
            # C240: golden card — name with right-aligned NIVEL, then the whole
            # description; the old labelled layout only for names that cannot share.
            self.assertIn('val levelLabel = "NIVEL " + spell.level', section)
            self.assertIn("val heading = if (inlineLevel) listOf(spell.name)", section)
            self.assertIn('else listOf(spell.name, levelLabel, "DESCRIPCIÓN:")', section)
            self.assertIn("val lines = heading + spell.description", section)
            for forbidden in ("spell.castingTime", "spell.rangeText",
                              "spell.materialText", "spell.duration",
                              "spell.notes", "entry.sources", "spell.sourceAssociations"):
                self.assertNotIn(forbidden, section)
            bodies.append(re.sub(r"\s+", " ", section))
        self.assertEqual(bodies[0], bodies[1],
                         "Desktop and Android must render the same approved card.")
        tests = TEST.read_text(encoding="utf-8")
        self.assertIn(
            "g3Bk06EssentialSpellbookCardsNeverPrintUnapprovedTechnicalMetadata", tests
        )

if __name__ == "__main__":
    unittest.main()
