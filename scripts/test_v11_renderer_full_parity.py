"""C426: the Desktop and Android V11 renderers stay the same program, line by line.

Only the package, the PDFBox and colour imports, the class names and the logo loader may differ.
"""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
DESKTOP = ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetV11PdfProofRenderer.kt"
ANDROID = ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetV11PdfProofRenderer.kt"


def normal(path, platform):
    text = path.read_text(encoding="utf-8")
    text = re.sub(r"^package .*$", "package X", text, flags=re.M)
    text = text.replace("com.tom_roush.", "org.apache.")
    text = text.replace("import org.apache.harmony.awt.AWTColor as Color", "import java.awt.Color")
    text = text.replace(platform + "PcSheetV11", "PlatformPcSheetV11")
    # The one real platform difference: how the logo asset is opened.
    text = re.sub(r"requireNotNull\(.*?\?\.use \{ it\.readBytes\(\) \}\)", "LOGO_BYTES", text,
                  count=1, flags=re.S)
    return [re.sub(r"\s+", " ", line).strip() for line in text.splitlines()]


class FullRendererParityTest(unittest.TestCase):
    def test_both_renderers_are_the_same_program(self):
        desktop, android = normal(DESKTOP, "Desktop"), normal(ANDROID, "Android")
        different = [(i + 1, a, b) for i, (a, b) in enumerate(zip(desktop, android)) if a != b]
        self.assertEqual(len(desktop), len(android))
        self.assertEqual(different[:5], [])

    def test_a_real_divergence_is_detected(self):
        desktop = normal(DESKTOP, "Desktop")
        changed = list(desktop)
        changed[len(changed) // 2] += " // drift"
        self.assertNotEqual(desktop, changed)


if __name__ == "__main__":
    unittest.main()
