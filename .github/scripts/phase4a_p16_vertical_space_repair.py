from pathlib import Path
import re

ROOT = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android")
LAYOUT = ROOT / "CharacterLayoutContextV4.kt"
SHELL = ROOT / "CharacterAdaptiveShellV4.kt"
EDITOR = ROOT / "CharacterEditorV4.kt"
COLLECTIONS = ROOT / "CharacterCollectionPrimitivesV4.kt"
WORKFLOW = Path(".github/workflows/phase4a-p16-repair.yml")
SCRIPT = Path(".github/scripts/phase4a_p16_vertical_space_repair.py")

STICKY_FILES = [
    "CharacterFormsModuleV4.kt",
    "CharacterClassOptionModulesV4.kt",
    "CharacterTraitsClosureV4.kt",
    "CharacterEquipmentClosureV4.kt",
    "CharacterSpellListClosureV4.kt",
    "CharacterArtificeModuleV4.kt",
    "CharacterCompanionsModuleV4.kt",
]


def replace_once(source: str, old: str, new: str, label: str) -> str:
    count = source.count(old)
    if count != 1:
        raise SystemExit(f"{label}: expected exactly one match, found {count}")
    return source.replace(old, new, 1)


# 1) Central layout context: preserve form-factor classification from Configuration,
# but permit the editor shell to provide its real constrained viewport dimensions.
layout = LAYOUT.read_text()
layout = replace_once(
    layout,
    "import androidx.compose.runtime.Composable\n",
    "import androidx.compose.runtime.Composable\nimport androidx.compose.runtime.compositionLocalOf\n",
    "layout compositionLocal import",
)
layout = replace_once(
    layout,
    "internal const val CHARACTER_TABLET_MIN_SHORT_SIDE_DP = 600\n",
    "internal val LocalCharacterLayoutContextV4 = compositionLocalOf<CharacterLayoutContextV4?> { null }\n\ninternal const val CHARACTER_TABLET_MIN_SHORT_SIDE_DP = 600\n",
    "layout context local",
)
old_context = '''@Composable
internal fun characterLayoutContextV4(): CharacterLayoutContextV4 {
    val configuration = LocalConfiguration.current
    return CharacterLayoutContextV4(
        formFactor = characterFormFactorV4(
            screenWidthDp = configuration.screenWidthDp,
            screenHeightDp = configuration.screenHeightDp,
            landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE,
        ),
        availableWidthDp = configuration.screenWidthDp,
        availableHeightDp = configuration.screenHeightDp,
    )
}
'''
new_context = '''@Composable
internal fun characterLayoutContextForAvailableSizeV4(
    availableWidthDp: Int,
    availableHeightDp: Int,
): CharacterLayoutContextV4 {
    val configuration = LocalConfiguration.current
    return CharacterLayoutContextV4(
        formFactor = characterFormFactorV4(
            screenWidthDp = configuration.screenWidthDp,
            screenHeightDp = configuration.screenHeightDp,
            landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE,
        ),
        availableWidthDp = availableWidthDp.coerceAtLeast(0),
        availableHeightDp = availableHeightDp.coerceAtLeast(0),
    )
}

@Composable
internal fun characterLayoutContextV4(): CharacterLayoutContextV4 {
    LocalCharacterLayoutContextV4.current?.let { return it }
    val configuration = LocalConfiguration.current
    return characterLayoutContextForAvailableSizeV4(
        availableWidthDp = configuration.screenWidthDp,
        availableHeightDp = configuration.screenHeightDp,
    )
}
'''
layout = replace_once(layout, old_context, new_context, "layout context implementation")
LAYOUT.write_text(layout.rstrip() + "\n")

# 2) Character shell provides one measured context to every nested surface.
shell = SHELL.read_text()
shell = replace_once(
    shell,
    "import androidx.compose.runtime.Composable\n",
    "import androidx.compose.runtime.Composable\nimport androidx.compose.runtime.CompositionLocalProvider\n",
    "shell provider import",
)
shell = replace_once(
    shell,
    '''internal fun CharacterAdaptiveShellV4(
    navigationPresentation: CharacterNavigationPresentationV4,''',
    '''internal fun CharacterAdaptiveShellV4(
    layoutContext: CharacterLayoutContextV4,
    navigationPresentation: CharacterNavigationPresentationV4,''',
    "shell layout parameter",
)
shell = replace_once(shell, "    val layoutContext = characterLayoutContextV4()\n", "", "shell local context removal")
shell = replace_once(
    shell,
    "    Column(modifier = Modifier.fillMaxSize()) {\n",
    "    CompositionLocalProvider(LocalCharacterLayoutContextV4 provides layoutContext) {\n        Column(modifier = Modifier.fillMaxSize()) {\n",
    "shell provider open",
)
shell = replace_once(
    shell,
    "    }\n}\n\n@Composable\nprivate fun CharacterNavigationRailV4(",
    "        }\n    }\n}\n\n@Composable\nprivate fun CharacterNavigationRailV4(",
    "shell provider close",
)
SHELL.write_text(shell.rstrip() + "\n")

# 3) Use BoxWithConstraints' actual post-Scaffold/post-adjustResize viewport.
editor = EDITOR.read_text()
editor = replace_once(
    editor,
    "                val layoutContext = characterLayoutContextV4()\n",
    '''                val layoutContext = characterLayoutContextForAvailableSizeV4(
                    availableWidthDp = maxWidth.value.toInt(),
                    availableHeightDp = maxHeight.value.toInt(),
                )
''',
    "editor measured layout context",
)
editor = replace_once(
    editor,
    "                CharacterAdaptiveShellV4(\n                    navigationPresentation = navigationPresentation,\n",
    "                CharacterAdaptiveShellV4(\n                    layoutContext = layoutContext,\n                    navigationPresentation = navigationPresentation,\n",
    "editor shell context wiring",
)
EDITOR.write_text(editor.rstrip() + "\n")

# 4) Shared adaptive sticky helper: same content, ordinary list item under height pressure.
collections = COLLECTIONS.read_text()
collections = replace_once(
    collections,
    "import androidx.compose.foundation.BorderStroke\n",
    "import androidx.compose.foundation.BorderStroke\nimport androidx.compose.foundation.ExperimentalFoundationApi\n",
    "collection experimental import",
)
collections = replace_once(
    collections,
    "import androidx.compose.foundation.layout.widthIn\n",
    "import androidx.compose.foundation.layout.widthIn\nimport androidx.compose.foundation.lazy.LazyListScope\n",
    "collection lazy scope import",
)
helper = '''@OptIn(ExperimentalFoundationApi::class)
internal fun LazyListScope.characterAdaptiveStickyHeaderV4(
    key: Any,
    sticky: Boolean,
    content: @Composable () -> Unit,
) {
    if (sticky) {
        stickyHeader(key = key) { content() }
    } else {
        item(key = key) { content() }
    }
}

'''
marker = "@Composable\ninternal fun CharacterCollectionToolbarV4(\n"
collections = replace_once(collections, marker, helper + marker, "adaptive sticky helper")
COLLECTIONS.write_text(collections.rstrip() + "\n")

# 5) Full audited sticky set: keep sticky only when vertical room is comfortable.
for name in STICKY_FILES:
    path = ROOT / name
    text = path.read_text()
    sticky_count = text.count("stickyHeader(")
    if sticky_count != 1:
        raise SystemExit(f"{name}: expected exactly one audited stickyHeader, found {sticky_count}")
    if "keepCollectionToolsSticky" in text:
        raise SystemExit(f"{name}: adaptive sticky policy already present unexpectedly")

    sticky_pos = text.index("stickyHeader(")
    preceding = text[:sticky_pos]
    list_state_matches = list(re.finditer(r"(?m)^(\s*)val\s+\w+\s*=\s*rememberLazyListState\(\)\s*$", preceding))
    if not list_state_matches:
        raise SystemExit(f"{name}: no rememberLazyListState() anchor before audited sticky header")
    anchor = list_state_matches[-1]
    indent = anchor.group(1)
    insertion = (
        "\n"
        + indent
        + "val keepCollectionToolsSticky =\n"
        + indent
        + "    characterLayoutContextV4().verticalSpace == CharacterVerticalSpaceV4.COMFORTABLE"
    )
    text = text[:anchor.end()] + insertion + text[anchor.end():]
    text = text.replace(
        "stickyHeader(",
        "characterAdaptiveStickyHeaderV4(sticky = keepCollectionToolsSticky, ",
        1,
    )
    path.write_text(text.rstrip() + "\n")

# Guard the core P16 invariants this repair owns.
if "characterLayoutContextForAvailableSizeV4(" not in EDITOR.read_text():
    raise SystemExit("Editor is not measuring its constrained viewport")
if "LocalCharacterLayoutContextV4 provides layoutContext" not in SHELL.read_text():
    raise SystemExit("Shell is not propagating the measured layout context")
for name in STICKY_FILES:
    text = (ROOT / name).read_text()
    if "stickyHeader(" in text:
        raise SystemExit(f"{name}: raw stickyHeader remains outside adaptive policy")
    if "keepCollectionToolsSticky" not in text:
        raise SystemExit(f"{name}: missing adaptive sticky policy")

WORKFLOW.unlink()
SCRIPT.unlink()
