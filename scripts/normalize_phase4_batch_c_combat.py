from pathlib import Path

path = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCombatTabV4.kt")
text = path.read_text(encoding="utf-8")

old_drag = """                                onDragStart = {
                                    accumulatedDrag = 0f
                                    dragging = true
                                },
                                onDragEnd = {
                                    accumulatedDrag = 0f
                                    dragging = false
                                },
"""
new_drag = """                            onDragStart = { accumulatedDrag = 0f; dragging = true },
                            onDragEnd = { accumulatedDrag = 0f; dragging = false },
"""
if text.count(old_drag) != 1:
    raise RuntimeError(f"combat drag normalization expected one match, found {text.count(old_drag)}")
text = text.replace(old_drag, new_drag, 1)

old_step = """                                        if (onMove(direction)) {
                                            accumulatedDrag -= direction * reorderStepPx
"""
new_step = """                                    if (onMove(direction)) {
                                        accumulatedDrag -= direction * reorderStepPx
"""
if text.count(old_step) != 1:
    raise RuntimeError(f"combat step normalization expected one match, found {text.count(old_step)}")
text = text.replace(old_step, new_step, 1)

path.write_text(text, encoding="utf-8")
print("Combat drag source normalized for guarded patch.")
