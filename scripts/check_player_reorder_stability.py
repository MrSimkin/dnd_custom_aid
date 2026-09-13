#!/usr/bin/env python3
from __future__ import annotations

import pathlib
import sys

ROOT = pathlib.Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android")
FILES = [
    ROOT / "CharacterReorderSessionV4.kt",
    ROOT / "CharacterSpatialReorderV4.kt",
]

errors: list[str] = []

for path in FILES:
    if not path.exists():
        errors.append(f"missing reorder engine: {path}")
        continue
    text = path.read_text(encoding="utf-8")
    name = path.name

    required = [
        "stableDragSlots",
        "stableCharacterReorderTargetIndex(",
        "previewCharacterReorder(canonicalOrderSnapshot",
        "translateCharacterReorderSlotsY(stableDragSlots, -consumed)",
    ]
    for marker in required:
        if marker not in text:
            errors.append(f"{name}: missing stable-target contract marker: {marker}")

    if "nearestCharacterReorderIndex" in text:
        errors.append(f"{name}: live nearest-slot targeting reintroduced into Android reorder engine")

    register_start = text.find("fun registerBounds(")
    unregister_start = text.find("fun unregisterBounds(", register_start)
    if register_start < 0 or unregister_start < 0:
        errors.append(f"{name}: registerBounds boundary not found")
    else:
        register_block = text[register_start:unregister_start]
        if "retarget" in register_block:
            errors.append(
                f"{name}: registerBounds must not retarget an active drag from preview-layout updates"
            )
        if "stableDragSlots.none" not in register_block:
            errors.append(
                f"{name}: newly rendered lazy targets are not captured once without overwriting stable slots"
            )

    if "retargetFromGeometry" in text:
        errors.append(f"{name}: obsolete live-geometry retarget path remains")

policy = pathlib.Path(
    "shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterSpatialReorderPolicy.kt"
)
if not policy.exists():
    errors.append(f"missing shared stable-target policy: {policy}")
else:
    policy_text = policy.read_text(encoding="utf-8")
    for marker in [
        "fun stableCharacterReorderTargetIndex(",
        "hysteresisFraction: Float = 0.12f",
        "fun translateCharacterReorderSlotsY(",
    ]:
        if marker not in policy_text:
            errors.append(f"shared reorder policy missing marker: {marker}")

test = pathlib.Path(
    "shared/src/commonTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterSpatialReorderPolicyTest.kt"
)
if not test.exists():
    errors.append(f"missing reorder stability regression test: {test}")
else:
    test_text = test.read_text(encoding="utf-8")
    for marker in [
        "stableTargetBuildsPreviewFromCanonicalOrderInsteadOfChasingPreviewGeometry",
        "hysteresisKeepsCurrentTargetNearBisectorUntilPointerClearlyCrosses",
        "viewportScrollTranslatesStableTargetsWithoutRebuildingThemFromPreviewLayout",
    ]:
        if marker not in test_text:
            errors.append(f"reorder regression test missing case: {marker}")

if errors:
    for error in errors:
        print("ERROR:", error, file=sys.stderr)
    raise SystemExit(1)

print(
    "Player reorder stability guard PASS: "
    "drag-start slots stable; preview geometry non-retargeting; canonical preview; "
    "hysteresis present; auto-scroll translates stable slots"
)
