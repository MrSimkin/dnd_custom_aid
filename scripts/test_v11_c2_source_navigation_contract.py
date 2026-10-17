"""C206 guard: actual V11 C2 physical source backlinks match P1/P2 origin."""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
RENDERERS = [
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
]
TEST = ROOT / "desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11RuntimePdfProofTest.kt"

class C2PhysicalOriginParityTest(unittest.TestCase):
    def test_both_native_pdf_writers_use_real_physical_content_origins(self):
        implementations = []
        for file in RENDERERS:
            source = file.read_text(encoding="utf-8")
            self.assertIn("val mixedC2P1Pages = mutableSetOf<Int>()", source)
            self.assertIn('it.sourceKey == "c2-heading:traits"', source)
            self.assertIn("val c2FromP1 = mixedC2P1Pages.minOrNull()", source)
            self.assertIn('origins.takeIf { it.isNotEmpty() }', source)
            self.assertIn('physicalPage == mixedC2TraitPages.minOrNull()', source)
            part = source.split('val mixedC2Key = "C2 - Habilidades, acciones y recursos"', 1)[1]
            part = part.split("pdf.save(target)", 1)[0]
            implementations.append(re.sub(r"\s+", " ", part))
        self.assertEqual(implementations[0], implementations[1])
        tests = TEST.read_text(encoding="utf-8")
        self.assertIn("g3OnlyP2TraitsInMixedC2NeverInventAP1Backlink", tests)
        self.assertIn("g3MixedC2ActualAldrenIlyraMaraNavigationKeepsTrueP1AndP2Origins", tests)

if __name__ == "__main__":
    unittest.main()
