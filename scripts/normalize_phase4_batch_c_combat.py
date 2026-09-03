from pathlib import Path

path = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCombatTabV4.kt")
text = path.read_text(encoding="utf-8")
old = """                                onDragStart = {
                                    accumulatedDrag = 0f
                                    dragging = true
                                },
                                onDragEnd = {
                                    accumulatedDrag = 0f
                                    dragging = false
                                },
"""
new = """                            onDragStart = { accumulatedDrag = 0f; dragging = true },
                            onDragEnd = { accumulatedDrag = 0f; dragging = false },
"""
if text.count(old) != 1:
    raise RuntimeError(f"combat drag normalization expected one match, found {text.count(old)}")
path.write_text(text.replace(old, new, 1), encoding="utf-8")
print("Combat drag block normalized for guarded patch.")
