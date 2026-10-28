"""C241: golden variant B skill values right-aligned identically on Android and Desktop."""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
RENDERERS = [
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
]
TESTS = ROOT / "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"


class P1BSkillValuesParityTest(unittest.TestCase):
    def test_both_renderers_right_align_b_skill_values(self):
        pieces = []
        for path in RENDERERS:
            data = path.read_text(encoding="utf-8")
            self.assertIn('bSkillParts["skill:" + index] = label to value', data)
            self.assertIn("val valueRight = bandLeft + bandWidth - 3f", data)
            part = data.split("// C241: golden B", 1)[1].split("val bSkillOverflow", 1)[0]
            pieces.append(re.sub(r"\s+", " ", part))
        self.assertEqual(pieces[0], pieces[1])
        self.assertIn("g3VariantBSkillValuesAreRightAlignedInTheirGoldenSubcolumns",
                      TESTS.read_text(encoding="utf-8"))


if __name__ == "__main__":
    unittest.main()
