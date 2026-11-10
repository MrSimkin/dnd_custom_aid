"""C439 / IN06, IN01: the V11 export path has no network dependency, and Android offers it.

This reads source code. It complements the Desktop test that presses the real buttons; it does not
replace a run on the installed Android app.
"""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
SHARED = ROOT / "shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character"
DESKTOP = ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop"
ANDROID = ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android"
NETWORK = re.compile(
    r"\b(ktor|okhttp|HttpClient|HttpURLConnection|openConnection|java\.net\.(?!URI\b)\w+|shared\.hosted|Socket)\b"
)


def export_path():
    yield from sorted(SHARED.glob("PcSheetV11*.kt"))
    yield SHARED / "PcSheetPdfExportFoundation.kt"
    yield from sorted(DESKTOP.glob("DesktopPcSheetV11*.kt"))
    yield DESKTOP / "DesktopPcSheetExportService.kt"
    yield from sorted((ANDROID / "pdf").glob("AndroidPcSheetV11*.kt"))
    yield ANDROID / "pdf/AndroidPcSheetExportService.kt"


class ExportOfflineTest(unittest.TestCase):
    def test_export_path_exists(self):
        names = [p.name for p in export_path()]
        for expected in ("PcSheetV11SemanticContract.kt", "DesktopPcSheetV11PdfProofRenderer.kt",
                         "AndroidPcSheetV11PdfProofRenderer.kt", "AndroidPcSheetExportService.kt"):
            self.assertIn(expected, names)

    def test_no_file_on_the_export_path_reaches_the_network(self):
        for path in export_path():
            hits = NETWORK.findall(path.read_text(encoding="utf-8"))
            self.assertEqual([], hits, path.name)

    def test_android_screen_offers_only_v11_a_and_b(self):
        # C473 (owner 2026-11-10, «Sí, así»): V11 A and B with clear names; the old families are gone.
        text = (ANDROID / "CharacterPcSettingsClosureV4.kt").read_text(encoding="utf-8")
        for option in ('"A · Habilidades por atributo"', '"B · Habilidades en orden alfabético"',
                       '"Números en tinta clara"', '"Incluir Libro de conjuros"'):
            self.assertIn(option, text)
        for gone in ('"Familia visual"', '"Familias anteriores"', '"Salida PDF de diagnóstico"'):
            self.assertNotIn(gone, text)

    def test_android_save_and_share_use_one_v11_generation(self):
        text = (ANDROID / "CharacterEditorV4.kt").read_text(encoding="utf-8")
        self.assertEqual(1, text.count("pcSheetExportService.generateV11Proof("))
        self.assertEqual(1, text.count("pcSheetExportService.stageV11Proof("))
        self.assertIn("PcSheetAndroidExportActionV4.SAVE ->", text)
        self.assertIn("PcSheetAndroidExportActionV4.SHARE ->", text)


if __name__ == "__main__":
    unittest.main()
