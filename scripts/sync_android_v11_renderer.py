#!/usr/bin/env python3
"""One source for the V11 sheet renderer (refactor F3, owner priority 2026-11-14).

The App de PC's V11 renderer and font measurer are the source of truth. The tablet's copies are
generated from them, so the two can never drift: only the package, the PDF library
(org.apache.pdfbox → com.tom_roush.pdfbox), the colour class and the way a bundled file is read
change.

  python scripts/sync_android_v11_renderer.py          writes the Android copies
  python scripts/sync_android_v11_renderer.py --check  fails if they differ from what it would write
"""
from __future__ import annotations

import argparse
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DESKTOP = ROOT / "desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop"
ANDROID = ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/pdf"

COMMON = [
    ("package io.github.mrsimkin.dndcustomaid.desktop\n", "package io.github.mrsimkin.dndcustomaid.android.pdf\n"),
    ("import org.apache.pdfbox.", "import com.tom_roush.pdfbox."),
    ("import java.awt.Color\n", "import com.tom_roush.harmony.awt.AWTColor as Color\n"),
    ("DesktopPcSheetV11FontMeasurer", "AndroidPcSheetV11FontMeasurer"),
    ("DesktopPcSheetV11PdfProofRenderer", "AndroidPcSheetV11PdfProofRenderer"),
]
FILES = {
    "DesktopPcSheetV11PdfProofRenderer.kt": ("AndroidPcSheetV11PdfProofRenderer.kt", [
        ('''            val originalSheetPixels = requireNotNull(AndroidPcSheetV11PdfProofRenderer::class.java.classLoader
                .getResourceAsStream("character-sheets/templates/renders/original/logo-only.png")
                ?.use { it.readBytes() }) {''',
         '''            val originalSheetPixels = requireNotNull(AndroidPcSheetAssetLoader.open(
                "character-sheets/templates/renders/original/logo-only.png",
            )?.use { it.readBytes() }) {'''),
    ]),
    "DesktopPcSheetV11FontMeasurer.kt": ("AndroidPcSheetV11FontMeasurer.kt", [
        ("    private val resourceLoader: (String) -> InputStream? = { path -> AndroidPcSheetV11FontMeasurer::class.java.classLoader.getResourceAsStream(path) },",
         "    private val resourceLoader: (String) -> InputStream? = AndroidPcSheetAssetLoader::open,"),
    ]),
}


def generate(source: str, specific: list[tuple[str, str]], name: str) -> str:
    text = source
    for old, new in COMMON:
        text = text.replace(old, new)
    for old, new in specific:
        if text.count(old) != 1:
            raise SystemExit(f"V11 renderer sync FAIL: {name} no longer has the expected platform-specific block:\n{old}")
        text = text.replace(old, new)
    return text


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    stale = []
    for desktop_name, (android_name, specific) in FILES.items():
        expected = generate((DESKTOP / desktop_name).read_text(encoding="utf-8"), specific, desktop_name)
        target = ANDROID / android_name
        if args.check:
            if target.read_text(encoding="utf-8") != expected:
                stale.append(android_name)
        else:
            target.write_text(expected, encoding="utf-8", newline="\n")
    if stale:
        print("V11 renderer sync FAIL: regenerate with python scripts/sync_android_v11_renderer.py: " + ", ".join(stale))
        return 1
    print("V11 renderer sync PASS." if args.check else "V11 renderer copies written.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
