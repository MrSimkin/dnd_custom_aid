"""C253: golden P3 footer continuation links are identical on Android and Desktop."""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
RENDERERS = [
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
]
TESTS = ROOT / "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"


class P3ModuleLinksParityTest(unittest.TestCase):
    def test_both_renderers_name_the_same_p3_continuations(self):
        pieces = []
        for path in RENDERERS:
            data = path.read_text(encoding="utf-8")
            part = data.split("val p3StoryPagesBefore = physicalSectionKeys.size", 1)[1].split(
                "// Resource balances and ammunition", 1)[0]
            self.assertIn('"HISTORIA" to (p3StoryPagesBefore + 1)', part)  # C341
            self.assertIn("p3FieldSpecs[index].first to it", part)
            self.assertIn("PcSheetV11ContinuationText.moduleLinks(", part)
            self.assertIn('if (key == "P3 - Trasfondo / Perfil / Historia" && p3ModuleLinks != null) {', data)
            pieces.append(re.sub(r"\s+", " ", part))
        self.assertEqual(pieces[0], pieces[1])
        self.assertIn("g3P3FooterNamesEachContinuedFlowWithItsRealPageAB",
                      TESTS.read_text(encoding="utf-8"))


if __name__ == "__main__":
    unittest.main()
