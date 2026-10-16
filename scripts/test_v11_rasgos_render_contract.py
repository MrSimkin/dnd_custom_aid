#!/usr/bin/env python3
"""V11 G1/G2/G3 source-parity tripwire; genuine-PDF tests are separately mandatory."""
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parent.parent
RENDERERS = [
    ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt",
    ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt",
]
SHARED = ROOT / "shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character"


def between(text, before, after):
    return text.split(before, 1)[1].split(after, 1)[0]


class RasgosStageTripwire(unittest.TestCase):
    def test_g1_shared_identity_excludes_description_and_notes(self):
        fmt = (SHARED / "PcSheetV11TraitSummary.kt").read_text(encoding="utf-8")
        body = between(fmt, "fun text(trait:", "\n}")
        self.assertIn("trait.name.trim()", body)
        self.assertIn("trait.source.trim()", body)
        self.assertIn("trait.type", body)
        self.assertNotIn("trait.description", body)
        self.assertNotIn("trait.notes", body)

    def test_g2_shared_flow_uses_identity_only(self):
        flow = (SHARED / "PcSheetV11FlowSourceMapper.kt").read_text(encoding="utf-8")
        traits = between(flow, "traitDescriptions = data.traits.map", "specialEquipmentDescriptions")
        self.assertIn("PcSheetV11TraitSummary.text(trait)", traits)
        self.assertNotIn("trait.description", traits)
        self.assertNotIn("trait.notes", traits)

    def test_g3_both_platforms_do_not_print_trait_descriptions(self):
        for renderer in RENDERERS:
            with self.subTest(renderer=renderer):
                text = renderer.read_text(encoding="utf-8")
                segment = between(text, "val traitLines = data.traits", "val traitsPlaced")
                self.assertIn("PcSheetV11TraitSummary.text(trait)", segment)
                self.assertIn("p1PrintedTraitIds", segment)
                self.assertNotIn("trait.description", segment)
                self.assertNotIn("trait.notes", segment)
                p1 = between(text, "val p1TraitEntries =", "fun drawP1Traits")
                self.assertIn("PcSheetV11TraitSummary.text(trait)", p1)
                self.assertIn("val p1PrintedTraitIds", p1)
                self.assertNotIn("trait.description", p1)

    def test_g3_p1_blue_trait_header_does_not_shift_text_or_data(self):
        for renderer in RENDERERS:
            with self.subTest(renderer=renderer):
                text = renderer.read_text(encoding="utf-8")
                region = between(text, "fun drawP1Traits(", "// Explicit AMMUNITION usage")
                self.assertIn("stream.moveTo(315f,368f)", region)
                self.assertIn("stream.lineTo(312f,371f)", region)
                self.assertIn("stream.fillAndStroke()", region)
                self.assertIn("stream.setFont(headerPdfFont,9f)", region)
                self.assertIn("stream.newLineAtOffset(left + 6f,top - 11f)", region)
                self.assertIn('stream.showText("RASGOS Y ATRIBUTOS")', region)
                self.assertIn("top - 21f - (slot % 6) * 10f", region)

    def test_g3_android_desktop_trait_logic_is_identical(self):
        desktop, android = [r.read_text(encoding="utf-8") for r in RENDERERS]
        for start, end in [
            ("fun p1TraitWrap(", "fun drawP1Traits("),
            ("fun drawP1Traits(", "// Explicit AMMUNITION usage"),
            ("val traitLines = data.traits", "val traitsPlaced ="),
        ]:
            self.assertEqual(between(desktop, start, end), between(android, start, end),
                "Platform split would allow different Rasgos export semantics.")


if __name__ == "__main__":
    unittest.main()
