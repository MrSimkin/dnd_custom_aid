#!/usr/bin/env python3
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]

def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")

def require(text: str, needle: str, label: str) -> None:
    if needle not in text:
        raise SystemExit(f"Android PC sheet PDF delivery guard FAIL: missing {label}: {needle}")

service = read("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf/AndroidPcSheetExportService.kt")
editor = read("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt")
settings = read("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterPcSettingsClosureV4.kt")
manifest = read("androidApp/src/main/AndroidManifest.xml")
paths = read("androidApp/src/main/res/xml/pc_sheet_file_paths.xml")

require(service, "PcSheetPdfExportPlanner.plan", "shared export planner invocation")
require(service, "AndroidPcSheetRendererBridge", "generated Android renderer bridge invocation")
require(service, "FileProvider.getUriForFile", "private Android share URI")
require(service, 'File(appContext.cacheDir, "pc-sheet-pdf/$purpose")', "cache-only share staging")

require(editor, 'ActivityResultContracts.CreateDocument("application/pdf")', "native PDF Save document flow")
require(editor, "Intent.ACTION_SEND", "native Android Share flow")
require(editor, "PcSheetExportSources(permanent = aggregate)", "single canonical export source")
require(editor, "fun currentPcSheetExportAggregate()", "pure current-editor export projection")
require(editor, 'title = { Text("Exportar cambios sin guardar") }', "unsaved-edit warning")
require(editor, 'Text("Exportar sin guardar")', "explicit export-without-saving confirmation")

require(settings, "PcSettingsPageClosureV4.PDF_EXPORT", "PC settings PDF surface")
require(settings, "PcSheetVisualFamily.entries", "all visual-family choices")
require(settings, "PcSheetExportStateSelection.entries", "state selection choices")
require(settings, "PcSheetCustomStatisticsPresentation.entries", "custom-stat presentation choices")
require(settings, "PcSheetPortraitFitMode.entries", "portrait fit choices")
require(settings, "Incluir descripciones de conjuros", "optional Spellbook choice")

require(manifest, 'android:name="androidx.core.content.FileProvider"', "FileProvider declaration")
require(manifest, 'android:exported="false"', "non-exported FileProvider")
require(manifest, 'android:grantUriPermissions="true"', "temporary URI grants")
require(paths, '<cache-path', "cache-only FileProvider path")
require(paths, 'path="pc-sheet-pdf/"', "bounded PC-sheet cache path")

print("Android PC sheet PDF delivery guard PASS.")
