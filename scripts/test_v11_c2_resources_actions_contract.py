"""C259: C2 resource boxes, action table and Spanish training are identical on Android and Desktop."""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
RENDERERS = [
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
]


class C2ResourcesActionsParityTest(unittest.TestCase):
    def test_both_renderers_share_the_owner_grammar(self):
        pieces = []
        for path in RENDERERS:
            data = path.read_text(encoding="utf-8")
            # Internal enum names are never printed on the sheet.
            self.assertNotIn("skill.training.name", data)
            self.assertNotIn('entry.name + " | " + entry.type', data)
            self.assertNotIn('"RECURSO " + resource.name', data)
            build = data.split("// C259 (owner 2026-10-28): actions as a table", 1)[1].split(
                "// P1 is one physical sheet", 1)[0]
            res = data.split("// C259 (owner 2026-10-28, «A casillas vacias", 1)[1].split(
                "ammunitionItems.forEach", 1)[0]
            draw = data.split("// C259: golden ACCIONES table header and rows.", 1)[1].split(
                "if (cell.sourceKey in ammunitionTracks &&", 1)[0]
            self.assertIn('listOf("ACCIÓN", "TIPO", "ALCANCE / EFECTO")', draw)
            self.assertIn('val count = spent.toString() + "/" + total', draw)
            self.assertIn('"Acción adicional"', build)
            pieces.append(re.sub(r"\s+", " ", build + res + draw))
        self.assertEqual(pieces[0], pieces[1])


if __name__ == "__main__":
    unittest.main()
