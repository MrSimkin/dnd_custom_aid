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
            self.assertIn('physicalSectionKeys += "C2 - Equipo y tesoro / " + label', source)
            self.assertIn('key.startsWith("C2 - Equipo y tesoro / ")', source)
            self.assertEqual(source.count("drawC2ContinuationCartela(stream, column)"), 2,
                "Every genuine C2 continued source-group or attribute heading needs golden cartela.")
            self.assertIn("stream.fillAndStroke()", source)
            self.assertIn("val bottom = 703f", source)
            self.assertIn("val top = 716f", source)
            # C213 physical paper grouping is sourced from real C2 group keys.
            self.assertIn("val c2CustomGroups = pageCells.mapNotNull", source)
            self.assertIn('sourceGroup?.startsWith("custom-attribute-group:") == true', source)
            self.assertIn('it.groupKey == identity.first', source)
            self.assertIn('it.sourceKey.startsWith("attribute-skill:")', source)
            self.assertIn('it.sourceKey.startsWith("attribute:")', source)
            self.assertIn("val railX = column.leftPt - 3f", source)
            # C215: real measured two-level card, one blank line between
            # true ability title and score. Preserve fragment/source offsets.
            self.assertIn('ability.name + "\\n\\n"', source)
            # C216: the paper box is sourced only from a real attribute-skill,
            # with reserved measured text inset and no duplicate on wrapping.
            self.assertIn('c2Inset + "Habilidad " + skill.name', source)  # C262 measured inset
            self.assertIn('cell.sourceKey.startsWith("attribute-skill:") &&', source)
            self.assertIn('cell.sourceStartOffset == 0)', source)
            self.assertIn('cell.sourceText.startsWith(c2Inset + "Habilidad ")', source)
            self.assertIn('body.width.measurePt(c2Inset) >= 11f', source)
            self.assertIn('stream.addRect(cell.cellPt.leftPt + 2f, base - 1f, 7f, 7f)', source)
            # C217 source-backed total is only moved if both sides fit.
            # C218: all C2 persisted numeric modifier/save/skill inks
            # share the genuine P1 plus/minus grammar in both exporters.
            self.assertIn('fun signedC2(value: Int): String =', source)
            self.assertIn('if (value >= 0) "+" + value else value.toString()', source)
            self.assertIn('val modifier = signedC2(ability.modifier)', source)
            self.assertIn('signedC2(attribute.modifier)', source)
            self.assertIn('signedC2(it)', source)
            self.assertIn('val realTotal = wholeSource.substring(', source)
            self.assertIn('val realTraining = wholeSource.substring(trainingSep + 3)', source)
            self.assertIn('val rightX = cell.cellPt.rightPt -', source)
            self.assertIn('labelEnd + 9f <= rightX', source)
            self.assertIn('draw(stream, realTotal, rightX, baseline, false)', source)
            self.assertIn('cell.sourceEndExclusive == wholeSource.length', source)
            self.assertIn('attribute.name + "\\n\\n" + attribute.score', source)
            self.assertIn('prior.rowIndex + 1 == cell.rowIndex', source)
            self.assertIn('prior.sourceEndExclusive == scoreStart', source)
            self.assertIn('prior.drawableText.isEmpty()', source)
            self.assertIn('kotlin.math.abs(prior.cellPt.bottomPt - cell.cellPt.topPt) <= 0.02f', source)
            self.assertNotIn('prior.cellPt.bottomPt == cell.cellPt.topPt', source)
            self.assertIn('top = baseline + if (reserved != null) 17f else 8.9f', source)
            self.assertIn("val scoreInk = printedToken(scoreStart, score)", source)
            self.assertIn("val modifierInk = printedToken(modifierStart, modifier)", source)
            self.assertIn("val savingInk = printedToken(savingStart, saving)", source)
            self.assertIn('entries.single { it.sourceKey == cell.sourceKey }.text ==', source)
            self.assertIn("stream.curveTo(cx + rx, cy + ry*k,", source)
            self.assertIn("stream.addRect(left - 1.25f, bottom,", source)
            self.assertIn("stream.addRect(x, baseline - 1.5f, 7f, 7f)", source)
            self.assertIn("Color(231, 242, 247)", source)
            self.assertIn("Color(217, 236, 245)", source)
            self.assertIn('origins.takeIf { it.isNotEmpty() }', source)
            self.assertIn('physicalPage == mixedC2TraitPages.minOrNull()', source)
            part = source.split('val mixedC2Key = "C2 - Habilidades, acciones y recursos"', 1)[1]
            part = part.split("pdf.save(target)", 1)[0]
            implementations.append(re.sub(r"\s+", " ", part))
        self.assertEqual(implementations[0], implementations[1])
        tests = TEST.read_text(encoding="utf-8")
        self.assertIn("g3C2EquipoAndTesoroOverflowKeepIndependentPhysicalNavigationInBothVariants", tests)
        self.assertIn("g3OnlyP2TraitsInMixedC2NeverInventAP1Backlink", tests)
        self.assertIn("g3MixedC2ActualAldrenIlyraMaraNavigationKeepsTrueP1AndP2Origins", tests)

if __name__ == "__main__":
    unittest.main()
